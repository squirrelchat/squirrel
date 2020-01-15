package chat.squirrel.idp;

import chat.squirrel.idp.identities.Yandex;

import java.util.concurrent.Future;

public class YandexIdentityProvider implements IIdentityProvider {
    @Override
    public Future<Yandex> provide(Object props) {
        return null;
    }
}
