package chat.squirrel.idp;

import java.util.concurrent.Future;

import chat.squirrel.idp.identities.Firefox;

public class FirefoxIdentityProvider implements IIdentityProvider {
    @Override
    public Future<Firefox> provide(final Object props) {
        return null;
    }
}
