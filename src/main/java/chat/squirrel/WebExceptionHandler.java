package chat.squirrel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

public class WebExceptionHandler implements Handler<RoutingContext> {
    private static final Logger LOG = LoggerFactory.getLogger(WebExceptionHandler.class);
    private boolean shouldPrintError = false;

    @Override
    public void handle(RoutingContext event) {
        final JsonObject obj = new JsonObject().put("error", event.response().getStatusCode());
        if (shouldPrintError) {
            obj.put("path", event.normalisedPath());
            obj.put("body", event.getBody());
        }
        LOG.error("An unknown error has been caught in routing: " + event.normalisedPath());
        event.response().end(obj.toBuffer());
    }
}
