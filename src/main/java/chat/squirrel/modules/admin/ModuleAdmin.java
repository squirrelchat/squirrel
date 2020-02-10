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

package chat.squirrel.modules.admin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.Snapshot;

import chat.squirrel.Squirrel;
import chat.squirrel.core.MetricsManager;
import chat.squirrel.entities.User;
import chat.squirrel.modules.AbstractModule;
import de.mxro.metrics.MetricsCommon;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

// @todo: Make this be "ModuleMetrics"
public class ModuleAdmin extends AbstractModule {
    private static Logger LOG = LoggerFactory.getLogger(ModuleAdmin.class);

    @Override
    public void initialize() {
        // @todo: is this an endpoint worth having?
        this.registerAuthedRoute(HttpMethod.POST, "/admin/shutdown", this::handleShutdown);
        this.registerAuthedRoute(HttpMethod.GET, "/admin/metrics", this::handleMetrics);
        this.registerAuthedRoute(HttpMethod.GET, "/admin/metrics/histogram/:hist", this::handleHistogram);
    }

    private void handleHistogram(final RoutingContext ctx) {
        final User user = this.getRequester(ctx);

        if (!user.isInstanceAdmin()) {
            ctx.fail(401);
            return;
        }

        final String name = ctx.pathParam("hist");

        final Snapshot snap = MetricsCommon.retrieveHistogram(name)
                .perform(MetricsManager.getMetrics().getDataUnsafe());

        ctx.response().end(JsonObject.mapFrom(snap).put("name", name).encode());
    }

    private void handleMetrics(final RoutingContext ctx) {
        final User user = this.getRequester(ctx);

        if (!user.isInstanceAdmin()) {
            ctx.fail(401);
            return;
        }

        ctx.response().end(MetricsManager.getMetrics().render().get());
    }

    private void handleShutdown(final RoutingContext ctx) {
        final User user = this.getRequester(ctx);

        if (!user.isInstanceAdmin()) {
            ctx.fail(401);
            return;
        }

        LOG.info("Server shutdown was requested by " + user.toString());
        MetricsManager.record(MetricsCommon.happened("admin.shutdown"));
        Squirrel.getInstance().shutdown();
    }
}
