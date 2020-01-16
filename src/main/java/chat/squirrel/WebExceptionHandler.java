package chat.squirrel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.Handler;

public class WebExceptionHandler implements Handler<Throwable> {
    private static final Logger LOG = LoggerFactory.getLogger(WebExceptionHandler.class);
    private boolean shouldPrintError = false;

    @Override
    public void handle(Throwable event) {
        LOG.error("An error has occured in the API", event);
    }
}
