package chat.squirrel.idp;

import java.util.concurrent.Future;

import chat.squirrel.idp.identities.Ldap;

public class LdapIdentityProvider implements IIdentityProvider<Object, Object> {

    @Override
    public Object getPreAuth() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Future<Ldap> provide(final String userId, final Object props) {
        // TODO Auto-generated method stub
        return null;
    }

}
