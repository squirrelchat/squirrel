package chat.squirrel.modules.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import chat.squirrel.Squirrel;
import chat.squirrel.auth.AuthHandler;
import chat.squirrel.auth.AuthResult;
import chat.squirrel.core.MetricsManager;
import chat.squirrel.modules.AbstractModule;
import de.mxro.metrics.jre.Metrics;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

/**
 * This Module manages authentication and MFA to Squirrel
 */
public class ModuleLogin extends AbstractModule {
    private static final Logger LOG = LoggerFactory.getLogger(ModuleLogin.class);

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
        LOG.info("Login attempt: " + res.toString() + ", IP: " + ctx.request().remoteAddress());
        MetricsManager.record(Metrics.happened("login." + (res.isSuccess() ? "success" : "failure")));
        if (!res.isSuccess()) {
            ctx.response().setStatusCode(401).end(new JsonObject().put("failure_reason", res.getReason()).encode());
            return;
        }

        ctx.response().setStatusCode(200)
                .end(new JsonObject().put("mfa_required", false).put("token", res.getToken()).encode());
    }

    private void handleRegister(RoutingContext ctx) {
        if (!Squirrel.getInstance().getConfig().isAllowRegister()) {
            ctx.fail(403);
            return;
        }
        final JsonObject obj = ctx.getBodyAsJson();
        if (obj == null || !(obj.containsKey("email") && obj.containsKey("username") && obj.containsKey("password"))) {
            ctx.fail(400);
            return;
        }

        final String password = obj.getString("password");

        if (password == null) {
            ctx.fail(401);
            return;
        }

        final AuthHandler auth = Squirrel.getInstance().getAuthHandler();
        final AuthResult res = auth.register(obj.getString("email"), obj.getString("username"), password.toCharArray());
        LOG.info("Register attempt: " + res.toString() + ", IP: " + ctx.request().remoteAddress());
        MetricsManager
                .record(Metrics.happened("register." + (res.isSuccess() ? "success" : ("failure." + res.getReason()))));
        if (!res.isSuccess()) {
            ctx.response().setStatusCode(401).end(new JsonObject().put("failure_reason", res.getReason()).encode());
            return;
        }

        ctx.response().setStatusCode(201)
                .end(new JsonObject().put("discriminator", res.getUser().getDiscriminator()).encode());
    }
}
