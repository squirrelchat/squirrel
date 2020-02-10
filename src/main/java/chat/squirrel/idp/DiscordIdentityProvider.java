package chat.squirrel.idp;

import java.util.concurrent.Future;

import chat.squirrel.idp.identities.Discord;

public class DiscordIdentityProvider implements IIdentityProvider {
    @Override
    public Future<Discord> provide(final Object props) {
        return null;
    }
}
