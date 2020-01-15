package chat.squirrel.idp;

import chat.squirrel.idp.identities.Firefox;

import java.util.concurrent.Future;

public class FirefoxIdentityProvider implements IIdentityProvider {
    @Override
    public Future<Firefox> provide(Object props) {
        return null;
    }
}
