package chat.squirrel;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import chat.squirrel.modules.ModulePing;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.impl.RouterImpl;

public final class SquirrelServer {
    private static final Logger LOG = LoggerFactory.getLogger(SquirrelServer.class);
    private Vertx vertx;
    private HttpServer server;
    private Router router;
    private List<SquirrelModule> modules;

    public static void main(String[] args) {
	SquirrelServer main = new SquirrelServer();
	main.start();
    }

    /**
     * 
     * @return the vertx Router used by the server
     */
    public Router getRouter() {
	return router;
    }

    /**
     * Initialize various components for the server
     */
    private SquirrelServer() {
	LOG.info("Initializing");
	vertx = Vertx.factory.vertx();
	server = vertx.createHttpServer();
	router = new RouterImpl(vertx);
	server.requestHandler(router);
	registerModules();
	setupModules();

    }

    /**
     * Actually starts the server and other components
     */
    private void start() {
	LOG.info("Starting server");
	server.listen(8080);
    }

    /**
     * @TODO: make this better
     */
    private void registerModules() {
	modules = new ArrayList<SquirrelModule>();
	modules.add(new ModulePing(this));
    }

    private void setupModules() {
	for (SquirrelModule m : modules) {
	    LOG.debug("Setting up module " + m.getClass().getCanonicalName());
	    m.setupRoutes();
	}
    }
}
