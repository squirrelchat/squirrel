package chat.squirrel.idp;

import java.util.concurrent.Future;

import chat.squirrel.idp.identities.Discord;

public class DiscordTeamIdentityProvider implements IIdentityProvider<Object, Object> {

    @Override
    public Object getPreAuth() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Future<Discord> provide(String userId, Object props) {
        // TODO Auto-generated method stub
        return null;
    }
}
