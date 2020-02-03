package chat.squirrel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.Handler;
import io.vertx.core.json.DecodeException;
import io.vertx.ext.web.RoutingContext;

/**
 * This handler catches misformatted JSON
 */
public class WebJsonHandler implements Handler<RoutingContext> {
    private static final Logger LOG = LoggerFactory.getLogger(WebJsonHandler.class);

    @Override
    public void handle(RoutingContext event) {
        try {
            event.getBodyAsJson();
        } catch (DecodeException e) {
            LOG.info("Recieved invalid json from " + event.request().remoteAddress().toString());
            event.fail(400);
            return;
        }
        event.next();
    }

}
