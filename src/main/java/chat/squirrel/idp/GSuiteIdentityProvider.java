package chat.squirrel.idp;

import java.util.concurrent.Future;

import chat.squirrel.idp.identities.Google;

public class GSuiteIdentityProvider implements IIdentityProvider {
    @Override
    public Future<Google> provide(final Object props) {
        return null;
    }
}
