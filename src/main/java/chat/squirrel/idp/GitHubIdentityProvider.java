package chat.squirrel.idp;

import java.util.concurrent.Future;

import chat.squirrel.idp.identities.GitHub;

public class GitHubIdentityProvider implements IIdentityProvider {
    @Override
    public Future<GitHub> provide(final Object props) {
        return null;
    }
}
