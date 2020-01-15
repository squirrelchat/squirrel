package chat.squirrel.idp;

import chat.squirrel.idp.identities.GitHub;

import java.util.concurrent.Future;

public class GitHubIdentityProvider implements IIdentityProvider {
    @Override
    public Future<GitHub> provide(Object props) {
        return null;
    }
}
