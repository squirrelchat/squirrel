package chat.squirrel.modules;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import chat.squirrel.Squirrel;
import chat.squirrel.WebAuthHandler;
import chat.squirrel.entities.User;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.RoutingContext;

public class ModuleAdmin extends AbstractModule {
    private static Logger LOG = LoggerFactory.getLogger(ModuleAdmin.class);

    @Override
    public void initialize() {
        registerAuthedRoute(HttpMethod.GET, "/admin/shutdown", this::handleShutdown);
    }

    private void handleShutdown(RoutingContext ctx) {
        final User user = ctx.get(WebAuthHandler.SQUIRREL_SESSION_KEY);
        
        if (!user.isServerAdmin()) {
            ctx.fail(401);
            return;
        }

        LOG.info("Server shutdown was requested by " + user.toString());
        Squirrel.getInstance().shutdown();
    }
}
