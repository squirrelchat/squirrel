package chat.squirrel.idp;

import java.util.concurrent.Future;

import chat.squirrel.idp.identities.Firefox;

public class FirefoxIdentityProvider implements IIdentityProvider<Object, Object> {

    @Override
    public Object getPreAuth() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Future<Firefox> provide(String userId, Object props) {
        // TODO Auto-generated method stub
        return null;
    }
}
