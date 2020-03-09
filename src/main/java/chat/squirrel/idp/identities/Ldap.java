package chat.squirrel.idp.identities;

import java.util.concurrent.Future;

import chat.squirrel.entities.IUser;

public class Ldap implements IIdentity {

    @Override
    public Future<IUser> getSquirrelAccount() {
        return null;
    }

    @Override
    public String getUserId() {
        // TODO Auto-generated method stub
        return null;
    }

}
