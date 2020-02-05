package chat.squirrel.modules;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.Snapshot;

import chat.squirrel.Squirrel;
import chat.squirrel.WebAuthHandler;
import chat.squirrel.core.MetricsManager;
import chat.squirrel.entities.User;
import de.mxro.metrics.jre.Metrics;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

public class ModuleAdmin extends AbstractModule {
    private static Logger LOG = LoggerFactory.getLogger(ModuleAdmin.class);

    @Override
    public void initialize() {
        registerAuthedRoute(HttpMethod.GET, "/admin/shutdown", this::handleShutdown);
        registerAuthedRoute(HttpMethod.GET, "/admin/metrics", this::handleMetrics);
        registerAuthedRoute(HttpMethod.GET, "/admin/metrics/histogram/:hist", this::handleHistogram);
    }

    private void handleHistogram(RoutingContext ctx) {
        final User user = ctx.get(WebAuthHandler.SQUIRREL_SESSION_KEY);

        if (!user.isServerAdmin()) {
            ctx.fail(401);
            return;
        }

        final String name = ctx.pathParam("hist");

        final Snapshot snap = Metrics.retrieveHistogram(name).perform(MetricsManager.getMetrics().getDataUnsafe());

        ctx.response().end(JsonObject.mapFrom(snap).put("name", name).encode());
    }

    private void handleMetrics(RoutingContext ctx) {
        final User user = ctx.get(WebAuthHandler.SQUIRREL_SESSION_KEY);

        if (!user.isServerAdmin()) {
            ctx.fail(401);
            return;
        }

        ctx.response().end(MetricsManager.getMetrics().render().get());
    }

    private void handleShutdown(RoutingContext ctx) {
        final User user = ctx.get(WebAuthHandler.SQUIRREL_SESSION_KEY);

        if (!user.isServerAdmin()) {
            ctx.fail(401);
            return;
        }

        LOG.info("Server shutdown was requested by " + user.toString());
        MetricsManager.record(Metrics.happened("admin.shutdown"));
        Squirrel.getInstance().shutdown();
    }
}
