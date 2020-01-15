package chat.squirrel.idp;

import chat.squirrel.idp.identities.Google;

import java.util.concurrent.Future;

public class GSuiteIdentityProvider implements IIdentityProvider {
    @Override
    public Future<Google> provide(Object props) {
        return null;
    }
}
