/*
 * Copyright (c) 2020-present Bowser65 & vinceh121, All rights reserved.
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

package chat.squirrel.modules.auth;

import chat.squirrel.Squirrel;
import chat.squirrel.auth.AuthHandler;
import chat.squirrel.auth.AuthResult;
import chat.squirrel.core.MetricsManager;
import chat.squirrel.modules.AbstractModule;
import de.mxro.metrics.jre.Metrics;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This Module manages authentication and MFA to Squirrel
 */
public class ModuleLogin extends AbstractModule {
    private static final Logger LOG = LoggerFactory.getLogger(ModuleLogin.class);

    @Override
    public void initialize() {
        registerRoute(HttpMethod.POST, "/auth/register", this::handleRegister);
        registerRoute(HttpMethod.POST, "/auth/login", this::handleLogin);
        registerRoute(HttpMethod.POST, "/auth/mfa", this::notImplemented);
    }

    private void handleLogin(RoutingContext ctx) {
        final JsonObject obj = ctx.getBodyAsJson();
        if (obj == null) {
            ctx.fail(400); // @todo: Proper error payload
            return;
        }

        final AuthHandler auth = Squirrel.getInstance().getAuthHandler();
        final AuthResult res = auth.attemptLogin(obj.getString("username"), obj.getString("password").toCharArray());
        LOG.info("Login attempt: " + res.toString() + ", IP: " + ctx.request().remoteAddress());
        MetricsManager.record(Metrics.happened("login." + (res.isSuccess() ? "success" : "failure")));
        if (!res.isSuccess()) {
            ctx.response().setStatusCode(401).end(new JsonObject().put("failure_reason", res.getReason()).encode());
            return;
        }

        ctx.response().setStatusCode(200).end(
                new JsonObject()
                        .put("mfa_required", false) // @todo: Consider 3fa support
                        .put("token", res.getToken())
                        .encode()
        );
    }

    private void handleRegister(RoutingContext ctx) {
        if (!Squirrel.getInstance().getConfig().isAllowRegister()) {
            ctx.fail(403); // @todo: Proper error payload
            return;
        }
        final JsonObject obj = ctx.getBodyAsJson();
        if (obj == null || !(obj.containsKey("email") && obj.containsKey("username") && obj.containsKey("password"))) {
            ctx.fail(400); // @todo: Proper error payload
            return;
        }

        final String password = obj.getString("password");
        if (password == null) {
            ctx.fail(401); // @todo: Proper error payload
            return;
        }

        final AuthHandler auth = Squirrel.getInstance().getAuthHandler();
        final AuthResult res = auth.register(obj.getString("email"), obj.getString("username"), password.toCharArray());
        LOG.info("Register attempt: " + res.toString() + ", IP: " + ctx.request().remoteAddress());
        MetricsManager
                .record(Metrics.happened("register." + (res.isSuccess() ? "success" : ("failure." + res.getReason()))));
        if (!res.isSuccess()) {
            ctx.response().setStatusCode(401).end(new JsonObject().put("failure_reason", res.getReason()).encode());
            return;
        }

        ctx.response().setStatusCode(204).end();
    }
}
