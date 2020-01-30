/*
 * Copyright (c) 2020-present Bowser65 & vinceh121, All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holder nor the names of its contributors
 *    may be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package chat.squirrel.auth;

import java.util.regex.Pattern;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.FindIterable;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;

import chat.squirrel.Squirrel;
import chat.squirrel.auth.AuthResult.FailureReason;
import chat.squirrel.core.DatabaseManager.SquirrelCollection;
import chat.squirrel.entities.User;
import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import de.mkammerer.argon2.Argon2Factory.Argon2Types;

/**
 * This {@link AuthHandler} manages authentication against the MongoDB database.
 */
public class MongoAuthHandler implements AuthHandler {
    private static Pattern EMAIL_PATTERN = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$",
            Pattern.CASE_INSENSITIVE),
            USERNAME_PATTERN = Pattern.compile("^\\S[^#\\e\\p{Cntrl}}\\R]+\\S$", Pattern.CASE_INSENSITIVE);
    private final Argon2 argon;
    @SuppressWarnings("FieldCanBeLocal")
    private final int ARGON_ITERATION = 3, ARGON_MEMORY = 128000, ARGON_PARALLELISM = 4;

    /**
     * Creates a basic {@link MongoAuthHandler}
     */
    public MongoAuthHandler() {
        argon = Argon2Factory.create(Argon2Types.ARGON2d);
    }

    @Override
    public AuthResult attemptLogin(final String credential, final char[] password) {
        final AuthResult res = new AuthResult();
        FindIterable<Document> it;
        final boolean shouldDistinguish = Squirrel.getInstance().getConfig().isAllowRegister();

        if (credential.contains("#")) { // Contains discriminator separator so interpret as username
            final String[] parts = credential.split("#");
            if (parts.length != 2) {
                res.setReason(shouldDistinguish ? FailureReason.INVALID_USERNAME : FailureReason.INVALID_CREDENTIALS);
                return res;
            }
            final String username = parts[0], discriminator = parts[1];
            try {
                it = fetchUsers(Filters.and(Filters.eq("username", username),
                        Filters.eq("discriminator", Integer.parseInt(discriminator))));
            } catch (NumberFormatException ex) {
                res.setReason(shouldDistinguish ? FailureReason.INVALID_USERNAME : FailureReason.INVALID_CREDENTIALS);
                return res;
            }
        } else if (credential.contains("@")) { // Interpret as email
            it = fetchUsers(Filters.eq("email", credential));
        } else {
            res.setReason(shouldDistinguish ? FailureReason.INVALID_USERNAME : FailureReason.INVALID_CREDENTIALS);
            return res;
        }

        final Document doc = it.first();

        if (doc != null) {
            final String hash = doc.getString("password");
            if (argon.verify(hash, password)) {
                final User user = (User) Squirrel.getInstance().getDatabaseManager().findFirstEntity(User.class,
                        SquirrelCollection.USERS, Filters.eq(doc.get("_id")));
                res.setUser(user);
                res.setReason(null);
                res.setToken(Squirrel.getInstance().getTokenize().generate(user.getId().toHexString()));
            } else {
                res.setReason(FailureReason.INVALID_PASSWORD);
            }
        } else {
            res.setReason(FailureReason.INVALID_USERNAME);
        }

        argon.wipeArray(password);

        if (!shouldDistinguish && res.getReason() == null)
            res.setReason(FailureReason.INVALID_CREDENTIALS);

        return res;
    }

    @Override
    public AuthResult register(final String email, final String username, final char[] password) {
        final AuthResult res = new AuthResult();
        if (!isValidEmail(email)) {
            res.setReason(FailureReason.INVALID_EMAIL);
            return res;
        }
        if (!isValidUsername(username)) {
            res.setReason(FailureReason.INVALID_USERNAME);
            return res;
        }
        if (hitMaxUsernameCount(username)) {
            res.setReason(FailureReason.OVERUSED_USERNAME);
            return res;
        }
        if (isEmailTaken(email)) {
            res.setReason(FailureReason.EMAIL_TAKEN);
            return res;
        }

        int discriminator = Squirrel.getInstance().getDatabaseManager().getFreeDiscriminator(username);
        if (discriminator == -1) {
            res.setReason(FailureReason.OVERUSED_USERNAME);
            return res;
        }

        final User user = new User();
        user.setEmail(email);
        user.setUsername(username);
        user.setDiscriminator(discriminator);

        Squirrel.getInstance().getDatabaseManager().insertEntity(SquirrelCollection.USERS, user);
        final UpdateResult pwdUp = Squirrel.getInstance().getDatabaseManager().updateEntity(SquirrelCollection.USERS,
                Filters.eq(user.getId()),
                Updates.set("password", argon.hash(ARGON_ITERATION, ARGON_MEMORY, ARGON_PARALLELISM, password)));
        argon.wipeArray(password);

        if (pwdUp.getModifiedCount() != 1) {
            throw new IllegalStateException("Password settings at registration didn't succeed: "
                    + pwdUp.getModifiedCount() + " passwords have been changed");
        }

        res.setUser(user);
        res.setReason(null);
        return res;
    }

    private boolean isValidUsername(final String username) {
        // @todo: Sanitize some Unicode stuff (zws, shit like private apple logo)
        return !(username.length() < 2 || username.length() > 32) && USERNAME_PATTERN.matcher(username).matches();
    }

    private boolean isValidEmail(final String email) {
        // @todo: Disallow emails handled by Squirrel's mail server (if configured)
        return EMAIL_PATTERN.matcher(email).matches();
    }

    private boolean isEmailTaken(final String email) {
        return Squirrel.getInstance().getDatabaseManager().countDocuments(SquirrelCollection.USERS,
                Filters.eq("email", email)) != 0;
    }

    private boolean hitMaxUsernameCount(final String username) {
        final int max = Squirrel.getInstance().getConfig().getMaximumUsernameCount();
        final long count = Squirrel.getInstance().getDatabaseManager().countDocuments(SquirrelCollection.USERS,
                Filters.eq("username", username));
        return count >= 5000 || (max != -1 && count >= max);
    }

    private FindIterable<Document> fetchUsers(final Bson filters) {
        return Squirrel.getInstance().getDatabaseManager().rawRequest(SquirrelCollection.USERS, filters);
    }
}
