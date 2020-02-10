package chat.squirrel.idp;

import java.util.concurrent.Future;

import chat.squirrel.idp.identities.Yandex;

public class YandexIdentityProvider implements IIdentityProvider {
    @Override
    public Future<Yandex> provide(final Object props) {
        return null;
    }
}
