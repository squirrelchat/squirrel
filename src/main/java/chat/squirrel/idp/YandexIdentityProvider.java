/*
 * Copyright (c) 2020 Squirrel Chat, All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holder nor the names of its contributors
 *    may be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package chat.squirrel.idp;

import chat.squirrel.Squirrel;
import chat.squirrel.idp.identities.Yandex;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.multipart.MultipartForm;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public class YandexIdentityProvider implements IIdentityProvider<String, String> {
    private String clientId;

    /**
     * @param code the auth code given by yandex
     */
    @Override
    public Future<Yandex> provide(final String userId, final String code) {
        final CompletableFuture<Yandex> future = new CompletableFuture<>();
        final Yandex iden = new Yandex();
        iden.setUserId(userId);

        final MultipartForm form = MultipartForm.create()
                .attribute("grant_type", "authorization_code")
                .attribute("code", code);

        final HttpRequest<Buffer> req = Squirrel.getInstance()
                .getHttpClient()
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
        return "https://oauth.yandex.com/authorize?response_type=code&client_id=" + this.clientId;
    }
}
