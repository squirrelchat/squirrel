/*
 * Copyright (c) 2020 Squirrel Chat, All rights reserved.
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

import chat.squirrel.Squirrel;
import chat.squirrel.auth.AuthResult.FailureReason;
import chat.squirrel.database.DatabaseManagerEditionBoomerware.SquirrelCollection;
import chat.squirrel.database.entities.IUser;
import com.mongodb.client.FindIterable;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import de.mkammerer.argon2.Argon2Factory.Argon2Types;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.regex.Pattern;

/**
 * This {@link IAuthHandler} manages authentication against the MongoDB
 * database.
 */
public class MongoAuthHandler implements IAuthHandler {
    // TODO: HANDLE WITH CARE - POTENTIAL TRACES OF SEVERAL DISEASES TO BE FOUND
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,8}$",
            Pattern.CASE_INSENSITIVE),
    /**
     * Does not allow escapes, line breaks, apple logo (F8FF)
     */
    USERNAME_PATTERN = Pattern.compile("^\\S[^#\\e\\p{Cntrl}}\\v\\xF8FF]+\\S$", Pattern.CASE_INSENSITIVE);
    private final Argon2 argon;
    @SuppressWarnings("FieldCanBeLocal")
    private final int ARGON_ITERATION = 3, ARGON_MEMORY = 128000, ARGON_PARALLELISM = 4;

    /**
     * Creates a basic {@link MongoAuthHandler}
     */
    public MongoAuthHandler() {
        this.argon = Argon2Factory.create(Argon2Types.ARGON2d);
    }

    @Override
    public AuthResult attemptLogin(final String credential, final char[] password) {
        final AuthResult res = new AuthResult();
        final FindIterable<Document> it;
        final boolean shouldDistinguish = Squirrel.getInstance().getConfig().isAllowRegister();

        if (credential.contains("#")) { // Contains discriminator separator so interpret as username
            final String[] parts = credential.split("#");
            if (parts.length != 2) {
                res.setReason(shouldDistinguish ? FailureReason.INVALID_USERNAME : FailureReason.INVALID_CREDENTIALS);
                return res;
            }
            final String username = parts[0], discriminator = parts[1];
            try {
                it = this.fetchUsers(Filters.and(Filters.eq("username", username),
                        Filters.eq("discriminator", Integer.parseInt(discriminator))));
            } catch (final NumberFormatException ex) {
                res.setReason(shouldDistinguish ? FailureReason.INVALID_USERNAME : FailureReason.INVALID_CREDENTIALS);
                return res;
            }
        } else if (credential.contains("@")) { // Interpret as email
            it = this.fetchUsers(Filters.eq("email", credential));
        } else {
            res.setReason(shouldDistinguish ? FailureReason.INVALID_USERNAME : FailureReason.INVALID_CREDENTIALS);
            return res;
        }

        final Document doc = it.first();

        if (doc != null) {
            final String hash = doc.getString("password");
            if (this.argon.verify(hash, password)) {
                final IUser user = Squirrel.getInstance()
                        .getBoomerDatabaseManager()
                        .findFirstEntity(IUser.class, SquirrelCollection.USERS, Filters.eq(doc.get("_id")));
                res.setUser(user);
                res.setReason(null);
                res.setToken(Squirrel.getInstance().getTokenize().generateToken(user).toString());
            } else {
                res.setReason(FailureReason.INVALID_PASSWORD);
            }
        } else {
            res.setReason(FailureReason.INVALID_USERNAME);
        }

        this.argon.wipeArray(password);

        if (!shouldDistinguish && res.getReason() == null) {
            res.setReason(FailureReason.INVALID_CREDENTIALS);
        }

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
        if (this.hitMaxUsernameCount(username)) {
            res.setReason(FailureReason.OVERUSED_USERNAME);
            return res;
        }
        if (this.isEmailTaken(email)) {
            res.setReason(FailureReason.EMAIL_TAKEN);
            return res;
        }

        final int discriminator = Squirrel.getInstance().getBoomerDatabaseManager().getFreeDiscriminator(username);
        if (discriminator == -1) {
            res.setReason(FailureReason.OVERUSED_USERNAME);
            return res;
        }

        final IUser user = IUser.create();
        user.setEmail(email);
        user.setUsername(username);
        user.setDiscriminator(discriminator);

        Squirrel.getInstance().getBoomerDatabaseManager().insertEntity(SquirrelCollection.USERS, user);
        final UpdateResult pwdUp = Squirrel.getInstance()
                .getBoomerDatabaseManager()
                .updateEntity(SquirrelCollection.USERS, Filters.eq(user.getId()), Updates.set("password",
                        this.argon.hash(this.ARGON_ITERATION, this.ARGON_MEMORY, this.ARGON_PARALLELISM, password)));
        this.argon.wipeArray(password);

        if (pwdUp.getModifiedCount() != 1) {
            throw new IllegalStateException("Password settings at registration didn't succeed: "
                    + pwdUp.getModifiedCount() + " passwords have been changed");
        }

        res.setUser(user);
        res.setReason(null);
        return res;
    }

    @Override
    public AuthResult confirmPassword(ObjectId id, char[] password) {
        final AuthResult res = new AuthResult();
        final FindIterable<Document> it = fetchUsers(Filters.eq(id));
        final Document doc = it.first();

        if (doc != null) {
            final String hash = doc.getString("password");
            if (this.argon.verify(hash, password)) {
                final IUser user = Squirrel.getInstance()
                        .getBoomerDatabaseManager()
                        .findFirstEntity(IUser.class, SquirrelCollection.USERS, Filters.eq(doc.get("_id")));
                res.setUser(user);
                res.setReason(null);
            } else {
                res.setReason(FailureReason.INVALID_PASSWORD);
            }
        } else {
            res.setReason(FailureReason.INVALID_CREDENTIALS);
        }
        return res;
    }

    private FindIterable<Document> fetchUsers(final Bson filters) {
        return Squirrel.getInstance().getBoomerDatabaseManager().rawRequest(SquirrelCollection.USERS, filters);
    }

    private boolean hitMaxUsernameCount(final String username) {
        final int max = Squirrel.getInstance().getConfig().getMaximumUsernameCount();
        final long count = Squirrel.getInstance()
                .getBoomerDatabaseManager()
                .countDocuments(SquirrelCollection.USERS, Filters.eq("username", username));
        return count >= 5000 || max != -1 && count >= max;
    }

    private boolean isEmailTaken(final String email) {
        return Squirrel.getInstance()
                .getBoomerDatabaseManager()
                .countDocuments(SquirrelCollection.USERS, Filters.eq("email", email)) != 0;
    }

    public static boolean isValidEmail(final String email) { // XXX should we move that to Squirrel?
        // @todo: Disallow emails handled by Squirrel's mail server (if configured)
        if (email == null) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }

    public static boolean isValidUsername(final String username) { // XXX should we move that to Squirrel?
        // @todo: Sanitize some Unicode stuff (zws, shit like private apple logo)
        if (username == null) {
            return false;
        }
        return !(username.length() < 2 || username.length() > 32) && USERNAME_PATTERN.matcher(username).matches();
    }
}
