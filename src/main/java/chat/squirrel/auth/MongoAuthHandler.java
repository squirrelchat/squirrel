package chat.squirrel.auth;

import java.util.Iterator;
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

public class MongoAuthHandler implements AuthHandler {
    private static Pattern EMAIL_PATTERN = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$",
            Pattern.CASE_INSENSITIVE), USERNAME_PATTERN = Pattern.compile("^\\S[^#]+\\S$", Pattern.CASE_INSENSITIVE);
    private final Argon2 argon;
    private final int ARGON_ITERATION = 3, ARGON_MEMORY = 128000, ARGON_PARALLELISM = 4;

    public MongoAuthHandler() {
        argon = Argon2Factory.create(Argon2Types.ARGON2d);
    }

    @Override
    public AuthResult attemptLogin(final String credential, final char[] password) {
        final AuthResult res = new AuthResult();

        FindIterable<Document> it;

        if (credential.contains("#")) { // Contains discriminator separator so interpret as username
            final String[] parts = credential.split("#");
            if (parts.length != 2) {
                res.setReason(FailureReason.INVALID_USERNAME);
                return res;
            }
            final String username = parts[0], discriminator = parts[1];
            try {
                it = fetchUsers(Filters.and(Filters.eq("username", username),
                        Filters.eq("discriminator", Integer.parseInt(discriminator))));
            } catch (NumberFormatException ex) {
                res.setReason(FailureReason.INVALID_USERNAME);
                return res;
            }
        } else if (credential.contains("@")) { // interpret as email
            it = fetchUsers(Filters.eq("email", credential));
        } else {
            res.setReason(FailureReason.INVALID_USERNAME);
            return res;
        }

        final Document doc = it.first();

        if (doc != null) {
            final String hash = doc.getString("password");
            if (argon.verify(hash, password)) {
                res.setUser((User) Squirrel.getInstance().getDatabaseManager().findFirstEntity(User.class,
                        SquirrelCollection.USERS, Filters.eq(doc.get("_id"))));
                res.setReason(null);
            } else {
                res.setReason(FailureReason.INVALID_PASSWORD);
            }
        } else {
            res.setReason(FailureReason.INVALID_USERNAME);
        }

        argon.wipeArray(password);

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
        if (hitMaxUsernamecount(username)) {
            res.setReason(FailureReason.OVERUSED_USERNAME);
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
            throw new IllegalStateException("Password settings at registration didn't success: "
                    + pwdUp.getModifiedCount() + " passwords have been changed");
        }

        res.setUser(user);
        res.setReason(null);
        return res;
    }

    private boolean isValidUsername(final String username) {
        return !(username.length() < 2 || username.length() > 32) && USERNAME_PATTERN.matcher(username).matches();
    }

    private boolean isValidEmail(final String email) {
        return EMAIL_PATTERN.matcher(email).matches();
    }

    private boolean hitMaxUsernamecount(final String username) {
        final int max = Squirrel.getInstance().getConfig().getMaximumUsernameCount();
        final long count = Squirrel.getInstance().getDatabaseManager().countDocuments(SquirrelCollection.USERS,
                Filters.eq("username", username));
        return count >= 9999 || (max != -1 && count >= max);
    }

    @SuppressWarnings("unchecked")
    private FindIterable<Document> fetchUsers(final Bson filters) {
        return Squirrel.getInstance().getDatabaseManager().rawRequest(SquirrelCollection.USERS, filters);
    }

}
