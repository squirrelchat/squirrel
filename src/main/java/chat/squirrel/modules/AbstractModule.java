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

package chat.squirrel.modules;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import chat.squirrel.Squirrel;
import chat.squirrel.WebAuthHandler;
import chat.squirrel.entities.User;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import xyz.bowser65.tokenize.Token;

public abstract class AbstractModule {
    private final List<Route> routes = new ArrayList<>();

    public void disable() {
        this.routes.forEach(Route::disable);
    }

    public void enable() {
        this.routes.forEach(Route::enable);
    }

    public User getRequester(final RoutingContext ctx) {
        return (User) this.getToken(ctx).getAccount();
    }

    public Token getToken(final RoutingContext ctx) {
        return ctx.get(WebAuthHandler.SQUIRREL_TOKEN_KEY);
    }

    public abstract void initialize();

    public boolean shouldEnable() {
        return true;
    }

    protected void notImplemented(final RoutingContext ctx) {
        this.fail(ctx, 501, "Not implemented", null);
    }

    /**
     * Ends the context with a safe failure to the client.
     *
     * @param ctx    The RoutingContext to end
     * @param status The HTTP status code
     * @param desc   The error description
     * @param extra  The extra informations to provide, may be null
     */
    protected void fail(final RoutingContext ctx, final int status, final String desc,
            @Nullable final JsonObject extra) {
        final JsonObject out = new JsonObject();
        out.put("status", status);
        out.put("desc", desc);
        if (extra != null) {
            out.put("details", extra);
        }

        ctx.response().setStatusCode(status).end(out.encode());
    }

    /**
     * Registers a new and disabled route behind the default authentication handler.
     * The Route will be enabled on server startup, so this should be called only
     * when your module initializes.
     *
     * @param method  The HTTP Method
     * @param path    The absolute path
     * @param handler The handler
     * @return The new route to be slick :sunglasses:
     */
    protected Route registerAuthedRoute(final HttpMethod method, final String path,
            final Handler<RoutingContext> handler) {
        final Route rt = this.registerRoute(method, path, Squirrel.getInstance().getApiAuthHandler());
        rt.handler(handler);
        return rt;
    }

    /**
     * Registers a new and disabled route. The Route will be enabled on server
     * startup, so this should be called only when your module initializes.
     *
     * @param method  The HTTP Method
     * @param path    The absolute path
     * @param handler The handler (preferably use a lambda pwease)
     * @return The new route to be slick :sunglasses:
     */
    protected Route registerRoute(final HttpMethod method, final String path, final Handler<RoutingContext> handler) {
        final Route rt = Squirrel.getInstance().getRouter().route(method, path).handler(BodyHandler.create())
                .handler(Squirrel.getInstance().getWebJsonHandler()).blockingHandler(handler).disable();
        this.routes.add(rt);
        return rt;
    }
}
