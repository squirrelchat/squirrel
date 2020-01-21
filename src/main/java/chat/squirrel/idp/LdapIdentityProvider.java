package chat.squirrel.idp;

import java.util.concurrent.Future;

import chat.squirrel.idp.identities.Ldap;

public class LdapIdentityProvider implements IIdentityProvider {

    @Override
    public Future<Ldap> provide(Object props) {
        return null;
    }

}
