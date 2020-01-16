package chat.squirrel.modules;

import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

public class ModuleAuth extends AbstractModule {

    @Override
    public void initialize() {
        registerRoute(HttpMethod.POST, "/auth/register", this::handleRegister);
        registerRoute(HttpMethod.POST, "/auth/login", this::handleLogin);
    }
    
    private void handleRegister(RoutingContext ctx) {
        JsonObject obj = ctx.getBodyAsJson();
        if (obj == null) {
            ctx.fail(400);
            return;
        }

        ctx.response().end(new JsonObject().put("username", obj.getString("username"))
                .put("password", obj.getString("password")).put("email", obj.getString("email")).toBuffer());
    }

    private void handleLogin(RoutingContext ctx) {
        JsonObject obj = ctx.getBodyAsJson();
        if (obj == null) {
            ctx.fail(400);
            return;
        }

        ctx.response().end(new JsonObject().put("username", obj.getString("username"))
                .put("password", obj.getString("password")).toBuffer());
    }

}
