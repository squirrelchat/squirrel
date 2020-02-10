package chat.squirrel.idp;

import java.util.concurrent.Future;

import chat.squirrel.idp.identities.Discord;

public class DiscordTeamIdentityProvider implements IIdentityProvider {
    @Override
    public Future<Discord> provide(final Object props) {
        return null;
    }
}
