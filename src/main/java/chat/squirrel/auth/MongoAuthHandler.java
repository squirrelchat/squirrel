package chat.squirrel.auth;

import java.util.Iterator;

import org.bson.conversions.Bson;

import com.mongodb.client.model.Filters;

import chat.squirrel.Squirrel;
import chat.squirrel.core.DatabaseManager.SquirrelCollection;
import chat.squirrel.entities.User;
import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import de.mkammerer.argon2.Argon2Factory.Argon2Types;

public class MongoAuthHandler implements AuthHandler {
    private final Argon2 argon;
    private final int ARGON_ITERATION = 3, ARGON_MEMORY = 128000, ARGON_PARALLELISM = 4;

    public MongoAuthHandler() {
        argon = Argon2Factory.create(Argon2Types.ARGON2d);

    }

    @Override
    public AuthResult attemptLogin(final String credential, final char[] password) {
        final String hashedPassword = argon.hash(ARGON_ITERATION, ARGON_MEMORY, ARGON_PARALLELISM, password);

        final AuthResult res = new AuthResult();
        res.setUsername(credential);

        Iterator<User> it;

        if (credential.contains("#")) { // Contains discriminator separator so interpret as username
            final String[] parts = credential.split("#");
            if (parts.length != 2)
                return res;
            final String username = parts[0], discriminator = parts[1];
            it = fetchUsers(Filters.and(Filters.eq("username", username), Filters.eq("discriminator", discriminator),
                    Filters.eq("password", hashedPassword)));
        } else if (credential.contains("@")) { // interpret as email
            it = fetchUsers(Filters.and(Filters.eq("email", credential), Filters.eq("password", hashedPassword)));
        } else {
            return res;
        }

        if (it.hasNext()) {
            final User user = it.next();

            if (it.hasNext()) {
                throw new RuntimeException("Multiple users have matching email and password, this shouldn't happen");
            }
            res.setSuccess(true);
            res.setUserId(user.getId());
        }

        return res;
    }

    @Override
    public AuthResult register(final String email, final String username, final char[] password) {
        final AuthResult res = new AuthResult();
        
        return res;
    }

    @SuppressWarnings("unchecked")
    private Iterator<User> fetchUsers(final Bson filters) {
        return (Iterator<User>) Squirrel.getInstance().getDatabaseManager().findEntities(User.class,
                SquirrelCollection.USERS, filters);
    }

}
