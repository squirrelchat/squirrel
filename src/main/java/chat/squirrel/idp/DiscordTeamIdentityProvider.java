package chat.squirrel.idp;

import chat.squirrel.idp.identities.Discord;

import java.util.concurrent.Future;

public class DiscordTeamIdentityProvider implements IIdentityProvider {
    @Override
    public Future<Discord> provide(Object props) {
        return null;
    }
}
