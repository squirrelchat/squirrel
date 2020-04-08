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

package chat.squirrel;

import chat.squirrel.database.DatabaseManager;
import chat.squirrel.database.collections.IConfigCollection;
import chat.squirrel.database.entities.config.ISquirrelConfig;
import chat.squirrel.event.EventBus;
import chat.squirrel.mail.NotificationMailManager;
import chat.squirrel.modules.ModuleManager;
import chat.squirrel.scheduling.SchedulerManager;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.client.WebClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.bowser65.tokenize.Tokenize;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.ExecutionException;

/**
 * The main Squirrel Class. This class is the core of the server, it supervises
 * all of the managers and api.
 */
public final class Squirrel {
    // Stuff
    private static Squirrel instance;
    private static final Logger LOG = LoggerFactory.getLogger(Squirrel.class);
    private final String runtimeHash = Integer.toHexString(new Random().nextInt(0xeffffff + 1) + 0x1000000);
    private final WebExceptionHandler webExceptionHandler;
    private final Properties properties;
    private final ISquirrelConfig config;

    // Managers
    private final ModuleManager moduleManager;
    private final DatabaseManager databaseManager;
    private final Tokenize tokenize;
    private final NotificationMailManager notifMail;
    private final SchedulerManager scheduler;
    private final EventBus eventBus;

    // Vert.x
    private final Vertx vertx;
    private final HttpServer server;
    private final Router rootRouter;
    private final Router apiRouter;
    private final Handler<RoutingContext> apiAuthHandler, webJsonHandler;
    private final WebClient httpClient;

    /**
     * Main entry point for Squirrel. Calling it manually is not recommended :^)
     * Exit codes:
     * -1: Config failed to load or is invalid;
     * -2: Failed to register default database collections;
     *
     * @param args CLI arguments
     */
    public static void main(final String[] args) {
        instance = new Squirrel();
        instance.start();
    }

    /**
     * Initialize various components for the server
     */
    private Squirrel() {
        this.properties = new Properties();
        try {
            this.properties.load(new FileInputStream("./squirrel.properties"));
        } catch (final IOException e) {
            LOG.error("Error while loading settings from squirrel.properties", e);
            System.exit(-1);
        }

        LOG.info("Initializing vert.x");
        vertx = Vertx.vertx();

        final String mongoConString = this.getProperty("database.mongo.con-string", "mongodb://127.0.0.1");
        final String redisConString = this.getProperty("database.redis.con-string");
        final String mongoDbName = this.getProperty("database.mongo.db-name", "squirrel");
        final String memoryConfig = this.getProperty("database.memory", "MEMORY");
        this.databaseManager = new DatabaseManager(mongoConString, redisConString, mongoDbName, memoryConfig, vertx);

        ISquirrelConfig config = null;
        try {
            config = databaseManager.getCollection(IConfigCollection.class).findConfig(ISquirrelConfig.class).toCompletableFuture().get();
        } catch (InterruptedException | ExecutionException e) {
            LOG.error("Error while loading configuration", e);
            System.exit(-1);
        }
        this.config = config == null ? ISquirrelConfig.create() : config;

        LOG.info("Initializing managers");
        this.moduleManager = new ModuleManager();
        this.scheduler = new SchedulerManager();
        this.scheduler.start();
        this.eventBus = new EventBus();
        this.tokenize = new Tokenize(this.config.getSecret());

        this.server = vertx.createHttpServer();

        this.server.webSocketHandler(this.eventBus);

        this.rootRouter = Router.router(vertx);
        this.apiRouter = Router.router(vertx);
        this.server.requestHandler(this.rootRouter);
        this.rootRouter.mountSubRouter("/api/v1", this.apiRouter);

        this.webJsonHandler = new WebJsonHandler();
        this.apiAuthHandler = new WebAuthHandler();

        this.webExceptionHandler = new WebExceptionHandler();
        this.rootRouter.errorHandler(500, this.webExceptionHandler);

        this.httpClient = WebClient.create(vertx);

        this.notifMail = new NotificationMailManager(vertx, null);

        // MetricsManager.getInstance().load(this.boomerDbManager);

        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown, "squirrel-shutdown"));
    }

    public Vertx getVertx() {
        return vertx;
    }

    /**
     * @return The Vert.x authentication handler the precedes protected routes.
     */
    public Handler<RoutingContext> getApiAuthHandler() {
        return this.apiAuthHandler;
    }

    /**
     * @return The {@link ISquirrelConfig} for this instance
     */
    public ISquirrelConfig getConfig() {
        return this.config;
    }

    /**
     * @return The {@link DatabaseManager} used by this server
     */
    public DatabaseManager getDatabaseManager() {
        return this.databaseManager;
    }

    /**
     * @param key The key representing the setting in the properties file to get
     * @return The property content from the properties file
     */
    public String getProperty(final String key) {
        return this.properties.getProperty(key);
    }

    /**
     * @param key The key representing the setting in the properties file to get
     * @param def The default value in case it's not defined
     * @return The value of key or def
     */
    public String getProperty(final String key, final String def) {
        return this.properties.getProperty(key, def);
    }

    /**
     * @return The vert.x Router used by the server
     */
    public Router getRouter() {
        return this.apiRouter;
    }

    public Tokenize getTokenize() {
        return this.tokenize;
    }

    public Handler<RoutingContext> getWebJsonHandler() {
        return this.webJsonHandler;
    }

    public WebClient getHttpClient() {
        return this.httpClient;
    }

    public NotificationMailManager getNotifMail() {
        return this.notifMail;
    }

    public SchedulerManager getScheduler() {
        return scheduler;
    }

    /**
     * @return The main Squirrel instance running on this JVM
     */
    public static Squirrel getInstance() {
        return instance;
    }

    public String getRuntimeHash() {
        return runtimeHash;
    }

    /**
     * Stops the web server and gracefully shutdowns the managers
     */
    public void shutdown() {
        LOG.info("Shutting down");
        scheduler.shutdown();
        this.moduleManager.disableModules();
        this.server.close(e -> this.vertx.close());
        // MetricsManager.getInstance().save();
        this.databaseManager.shutdown();
        LOG.info("Shutdown successful, the process should end");
    }

    /**
     * Actually starts the server and other components
     */
    private void start() {
        LOG.info("Loading modules");
        this.moduleManager.scanPackage("chat.squirrel.modules");

        LOG.info("Loading routes");
        this.moduleManager.loadModules();

        LOG.info("Starting server");
        this.server.listen(Integer.parseInt(this.getProperty("http.port", "80")));
    }
}
