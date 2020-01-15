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

package chat.squirrel;

import chat.squirrel.auth.AuthHandler;
import chat.squirrel.core.ModuleManager;
import chat.squirrel.modules.AbstractModule;
import chat.squirrel.modules.ModulePing;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public final class Squirrel {
    // Stuff
    private static Squirrel instance;
    private static final Logger LOG = LoggerFactory.getLogger(Squirrel.class);

    // Managers
    private final ModuleManager moduleManager;

    private final AuthHandler authHandler;

    // Vert.x
    private final HttpServer server;
    private final Router router;

    public static void main(String[] args) {
        instance = new Squirrel();
        instance.start();
    }

    public static Squirrel getInstance() {
        return instance;
    }

    /**
     * Initialize various components for the server
     */
    private Squirrel() {
        LOG.info("Initializing managers");
        moduleManager = new ModuleManager();
        authHandler = new AuthHandler();

        LOG.info("Loading modules");
        moduleManager.scanPackage("chat.squirrel.modules");
        LOG.info("Initializing vert.x");
        final Vertx vertx = Vertx.vertx();
        server = vertx.createHttpServer();
        router = Router.router(vertx);
        server.requestHandler(router);
    }

    /**
     * Actually starts the server and other components
     */
    private void start() {
        LOG.info("Loading routes");
        moduleManager.loadModules();

        LOG.info("Starting server");
        server.listen(8080);
    }

    /**
     * @return The vert.x Router used by the server
     */
    public Router getRouter() {
        return router;
    }
}
