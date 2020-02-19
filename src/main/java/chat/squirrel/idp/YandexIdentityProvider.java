package chat.squirrel.idp;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import chat.squirrel.Squirrel;
import chat.squirrel.idp.identities.Yandex;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.multipart.MultipartForm;

public class YandexIdentityProvider implements IIdentityProvider<String, String> {
    private String clientId;

    /**
     * @param code the auth code given by yandex
     */
    @Override
    public Future<Yandex> provide(final String userId, final String code) {
        final CompletableFuture<Yandex> future = new CompletableFuture<Yandex>();
        final Yandex iden = new Yandex();
        iden.setUserId(userId);

        final MultipartForm form = MultipartForm.create().attribute("grant_type", "authorization_code")
                .attribute("code", code);

        final HttpRequest<Buffer> req = Squirrel.getInstance().getHttpClient()
                .postAbs("https://oauth.yandex.com/token");

        req.sendMultipartForm(form, (a) -> {
            final JsonObject res = a.result().bodyAsJsonObject();
            if (res.containsKey("error")) {
                future.completeExceptionally(new IdpException(
                        "Yandex returned: " + res.getString("error") + ": " + res.getString("error_description")));
                return;
            }

            iden.setAccessToken(res.getString("access_token"));
            iden.setExpiery(res.getLong("expires_in"));
            iden.setRefreshToken(res.getString("refresh_token"));

            future.complete(iden);
        });
        return future;
    }

    /**
     * @return the auth url
     */
    @Override
    public String getPreAuth() {
        return "https://oauth.yandex.com/authorize?response_type=code&client_id=" + clientId;
    }
}
