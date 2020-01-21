package chat.squirrel;

import chat.squirrel.entities.User;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.Session;

/**
 * Vert.x handler to handle sessions before continuing down a chain
 */
public class WebAuthHandler implements Handler<RoutingContext> {
    public static final String SQUIRREL_SESSION_KEY = "chat.squirrel.user";

    @Override
    public void handle(RoutingContext event) {
        final Session session = event.session();
        if (session == null || session.isDestroyed() || session.isEmpty()) {
            event.fail(401);
            return;
        }

        final User user = session.get(SQUIRREL_SESSION_KEY);

        if (user == null) {
            event.fail(401);
            return;
        }

        if (user.isBanned()) {
            event.fail(403);
            return;
        }
        event.next();
    }

}
