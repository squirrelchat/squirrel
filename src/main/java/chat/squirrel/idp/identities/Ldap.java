package chat.squirrel.idp.identities;

import java.util.concurrent.Future;

import chat.squirrel.entities.User;

public class Ldap implements IIdentity {

    @Override
    public Future<User> getSquirrelAccount() {
        return null;
    }

}