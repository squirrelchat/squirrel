package chat.squirrel.idp;

import java.util.concurrent.Future;

import chat.squirrel.idp.identities.Discord;

public class DiscordIdentityProvider implements IIdentityProvider<Object, Object> {

    @Override
    public Object getPreAuth() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Future<Discord> provide(final String userId, final Object props) {
        // TODO Auto-generated method stub
        return null;
    }
}
