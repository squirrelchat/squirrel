package chat.squirrel.idp;

import java.util.concurrent.Future;

import chat.squirrel.idp.identities.GitHub;

public class GitHubIdentityProvider implements IIdentityProvider<Object, Object> {

    @Override
    public Object getPreAuth() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Future<GitHub> provide(String userId, Object props) {
        // TODO Auto-generated method stub
        return null;
    }
}
