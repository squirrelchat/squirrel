package chat.squirrel.modules;

import chat.squirrel.Squirrel;
import chat.squirrel.auth.AuthHandler;
import chat.squirrel.auth.AuthResult;
import chat.squirrel.entities.User;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

public class ModuleAuth extends AbstractModule {

    @Override
    public void initialize() {
        registerRoute(HttpMethod.POST, "/auth/register", this::handleRegister);
        registerRoute(HttpMethod.POST, "/auth/login", this::handleLogin);
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

        final User usr = res.getUser();

        ctx.response().setStatusCode(201)
                .end(new JsonObject().put("user",
                        new JsonObject().put("username", usr.getUsername()).put("discriminator", usr.getDiscriminator())
                                .put("id", usr.getId().toString()).put("server_role", usr.getServerRole())
                                .put("flag", usr.getFlag()))
                        .encode());

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

        final User usr = res.getUser();

        ctx.response().setStatusCode(
                201).end(
                        new JsonObject()
                                .put("user",
                                        new JsonObject().put("username", usr.getUsername())
                                                .put("discriminator", usr.getDiscriminator())
                                                .put("id", usr.getId().toString()).put("flag", usr.getFlag()))
                                .encode());
    }

}
