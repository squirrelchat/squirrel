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

import chat.squirrel.Squirrel;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

public abstract class AbstractModule {
    private final List<Route> routes = new ArrayList<>();

    /**
     * Registers a new and disabled route. The Route will be enabled on server
     * startup, so this should be called only when your module initializes.
     *
     * @param method  The HTTP Method
     * @param path    The absolute path
     * @param handler The handler (preferably use a lambda pwease)
     * @return The new route to be slick :sunglasses:
     */
    protected Route registerRoute(HttpMethod method, String path, Handler<RoutingContext> handler) {
        final Route rt = Squirrel.getInstance().getRouter().route(method, path).handler(BodyHandler.create())
                .handler(Squirrel.getInstance().getWebJsonHandler()).blockingHandler(handler).disable();
        routes.add(rt);
        return rt;
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
    protected Route registerAuthedRoute(HttpMethod method, String path, Handler<RoutingContext> handler) {
        final Route rt = registerRoute(method, path, Squirrel.getInstance().getApiAuthHandler());
        rt.handler(handler);
        return rt;
    }

    public void enable() {
        routes.forEach(Route::enable);
    }

    public void disable() {
        routes.forEach(Route::disable);
    }

    public boolean shouldEnable() {
        return true;
    }

    protected void notImplemented(RoutingContext ctx) {
        ctx.fail(501);
    }

    public abstract void initialize();
}
