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

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.client.model.Filters;

import chat.squirrel.auth.IAuthHandler;
import chat.squirrel.auth.MongoAuthHandler;
import chat.squirrel.core.DatabaseManager;
import chat.squirrel.core.DatabaseManager.SquirrelCollection;
import chat.squirrel.core.ModuleManager;
import chat.squirrel.mail.NotificationMailManager;
import chat.squirrel.mail.SquirrelMailConfig;
import chat.squirrel.metrics.MetricsManager;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.client.WebClient;
import xyz.bowser65.tokenize.Tokenize;

/**
 * The main Squirrel Class. This class is the core of the server, it supervises
 * all of the managers and api.
 */
public final class Squirrel {
    // Stuff
    private static Squirrel instance;
    private static final Logger LOG = LoggerFactory.getLogger(Squirrel.class);
    private final WebExceptionHandler webExceptionHandler;
    private final Properties properties;
    private final SquirrelConfig config;

    // Managers
    private final ModuleManager moduleManager;
    private final DatabaseManager dbManager;
    private final IAuthHandler authHandler;
    private final Tokenize tokenize;
    private final NotificationMailManager notifMail;

    // Vert.x
    private final HttpServer server;
    private final Router rootRouter;
    private final Router apiRouter;
    private final Handler<RoutingContext> apiAuthHandler, webJsonHandler;
    private final WebClient httpClient;

    /**
     * Call this if you want stuff to break
     *
     * @param args Command line arguments
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
            LOG.error("Fatal error, exiting");
            System.exit(-1);
        }
        LOG.info("Initializing managers");
        this.moduleManager = new ModuleManager();
        this.dbManager = new DatabaseManager(this.getProperty("mongo.con-string"),
                this.getProperty("mongo.db-name", "squirrel"));

        this.config = (SquirrelConfig) this.getUserConfig(Squirrel.class, new SquirrelConfig(this.getClass()));

        this.authHandler = new MongoAuthHandler(); // TODO: make customizable when there'll be more

        this.tokenize = new Tokenize(this.config.getTokenSecret().getBytes(StandardCharsets.UTF_16));

        LOG.info("Loading modules");
        this.moduleManager.scanPackage("chat.squirrel.modules");
        LOG.info("Initializing vert.x");
        final Vertx vertx = Vertx.vertx();
        this.server = vertx.createHttpServer();

        this.rootRouter = Router.router(vertx);
        this.apiRouter = Router.router(vertx);
        this.server.requestHandler(this.rootRouter);
        this.rootRouter.mountSubRouter("/api/v1", this.apiRouter);

        this.webJsonHandler = new WebJsonHandler();
        this.apiAuthHandler = new WebAuthHandler();

        this.webExceptionHandler = new WebExceptionHandler();
        this.rootRouter.errorHandler(500, this.webExceptionHandler);

        this.httpClient = WebClient.create(vertx);

        this.notifMail = new NotificationMailManager(vertx,
                (SquirrelMailConfig) this.getUserConfig(NotificationMailManager.class, null));
        
        MetricsManager.getInstance().load(this.dbManager);

        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown, "squirrel-shutdown"));
    }

    /**
     * @return The Vert.x authentication handler the precedes protected routes.
     */
    public Handler<RoutingContext> getApiAuthHandler() {
        return this.apiAuthHandler;
    }

    /**
     * @return The AuthHandler that manages authentication to the database
     */
    public IAuthHandler getAuthHandler() {
        return this.authHandler;
    }

    /**
     * @return The SquirrelConfig object for this instance
     */
    public SquirrelConfig getConfig() {
        return this.config;
    }

    /**
     * @return The DatabaseManager used by this server
     */
    public DatabaseManager getDatabaseManager() {
        return this.dbManager;
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

    public UserConfig getUserConfig(final Class<?> owner) {
        final UserConfig def = new UserConfig(owner);
        final UserConfig retConf = this.getUserConfig(owner, def);
        if (retConf == def) {
            this.saveUserConfig(def);
        }
        return retConf;
    }

    public UserConfig getUserConfig(final Class<?> owner, final UserConfig def) {
        final UserConfig conf = this.dbManager.findFirstEntity(UserConfig.class, SquirrelCollection.CONFIG,
                Filters.eq("owner", owner.toString()));
        if (conf == null) {
            return def;
        }
        return conf;
    }

    public void saveUserConfig(final UserConfig conf) {
        this.dbManager.replaceOne(SquirrelCollection.CONFIG, conf, Filters.eq(conf.getId()));
    }

    /**
     * Stops the web server and gracefully shutdowns the managers
     */
    public void shutdown() {
        LOG.info("Gracefully shutting down");
        this.server.close();
        this.moduleManager.disableModules();
        MetricsManager.getInstance().save();
        this.dbManager.shutdown();
        LOG.info("Shutdown successful, the process should end");
        System.exit(0); // TODO is this a good idea?
    }

    /**
     * Actually starts the server and other components
     */
    private void start() {
        LOG.info("Loading routes");
        this.moduleManager.loadModules();

        LOG.info("Starting server");
        this.server.listen(8080);
    }

    /**
     * @param dis The discriminator integer to format
     * @return String format of integer with up to 4 leading zeros
     */
    public static String formatDiscriminator(final int dis) {
        if (dis < 0 || dis > 9999) {
            throw new IllegalArgumentException("Discriminator to be formatted is out of bounds");
        }
        return String.format("%04d", dis);
    }

    /**
     * @return The main Squirrel instance running on this JVM
     */
    public static Squirrel getInstance() {
        return instance;
    }

}
