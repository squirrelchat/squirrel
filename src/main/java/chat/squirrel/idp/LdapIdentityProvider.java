package chat.squirrel.idp;

import chat.squirrel.idp.identities.Ldap;

import java.util.concurrent.Future;

public class LdapIdentityProvider implements IIdentityProvider {

    @Override
    public Future<Ldap> provide(Object props) {
        return null;
    }

}
