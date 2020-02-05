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

import chat.squirrel.Version;
import chat.squirrel.core.MetricsManager;
import de.mxro.metrics.jre.Metrics;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.RoutingContext;

public class ModulePing extends AbstractModule {
    @Override
    public void initialize() {
        this.registerRoute(HttpMethod.GET, "/squirrelPing", this::ping);
        this.registerAuthedRoute(HttpMethod.GET, "/squirrelAuthPing", this::authPing);
    }

    private void ping(RoutingContext ctx) {
        MetricsManager.record(Metrics.happened("ping.public"));
        ctx.response().end("Squirrel " + Version.VERSION);
    }

    private void authPing(RoutingContext ctx) {
        MetricsManager.record(Metrics.happened("ping.authed"));
        ctx.response().end("Squirrel " + Version.VERSION);
    }
}
