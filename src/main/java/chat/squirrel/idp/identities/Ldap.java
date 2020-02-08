package chat.squirrel.idp.identities;

import chat.squirrel.entities.User;

import java.util.concurrent.Future;

public class Ldap implements IIdentity {

    @Override
    public Future<User> getSquirrelAccount() {
        return null;
    }

}
