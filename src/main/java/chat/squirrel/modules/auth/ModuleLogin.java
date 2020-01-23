package chat.squirrel.modules.auth;

import chat.squirrel.Squirrel;
import chat.squirrel.auth.AuthHandler;
import chat.squirrel.auth.AuthResult;
import chat.squirrel.modules.AbstractModule;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

/**
 * This Module manages authentication and MFA to Squirrel
 */
public class ModuleLogin extends AbstractModule {
    @Override
    public void initialize() {
        registerRoute(HttpMethod.POST, "/auth/register", this::handleRegister);
        registerRoute(HttpMethod.POST, "/auth/login", this::handleLogin);
        registerRoute(HttpMethod.POST, "/auth/mfa", this::notImplemented);
    }

    private void handleLogin(RoutingContext ctx) {
        final JsonObject obj = ctx.getBodyAsJson();
        if (obj == null) {
            ctx.fail(400);
            return;
        }

        final AuthHandler auth = Squirrel.getInstance().getAuthHandler();
        final AuthResult res = auth.attemptLogin(obj.getString("username"), obj.getString("password").toCharArray());
        if (!res.isSuccess()) {
            ctx.response().setStatusCode(401).end(new JsonObject().put("failure_reason", res.getReason()).encode());
            return;
        }

        ctx.response().setStatusCode(200).end(
                new JsonObject()
                        .put("mfa_required", false)
                        .put("token", "btw.have.i.told.you.i.use.arch")
                        .encode()
        );

    }

    private void handleRegister(RoutingContext ctx) {
        final JsonObject obj = ctx.getBodyAsJson();
        if (obj == null) {
            ctx.fail(400);
            return;
        }

        final AuthHandler auth = Squirrel.getInstance().getAuthHandler();
        final AuthResult res = auth.register(obj.getString("email"), obj.getString("username"),
                obj.getString("password").toCharArray());
        if (!res.isSuccess()) {
            ctx.response().setStatusCode(400).end(new JsonObject().put("failure_reason", res.getReason()).encode());
            return;
        }

        // @todo: Immediately return a valid token?
        ctx.response().setStatusCode(201).end();
    }
}
