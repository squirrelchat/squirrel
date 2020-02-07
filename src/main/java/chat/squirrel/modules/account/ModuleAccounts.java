package chat.squirrel.modules.account;

import org.bson.types.ObjectId;

import com.mongodb.client.model.Filters;

import chat.squirrel.Squirrel;
import chat.squirrel.core.DatabaseManager.SquirrelCollection;
import chat.squirrel.entities.User;
import chat.squirrel.modules.AbstractModule;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

public class ModuleAccounts extends AbstractModule {

    @Override
    public void initialize() {
        registerAuthedRoute(HttpMethod.GET, "/account/me", this::handleMe);
        registerAuthedRoute(HttpMethod.GET, "/account/:id", this::handleGetAccount);
    }

    private void handleMe(RoutingContext ctx) {
        final User user = getRequester(ctx);

        if (user.isServerAdmin()) {
            ctx.response().end(user.toJson().encode());
            return;
        }

        ctx.response()
                .end(new JsonObject().put("username", user.getUsername()).put("email", user.getEmail())
                        .put("custom_email", user.getCustomEmail()).put("discriminator", user.getDiscriminator())
                        .put("id", user.getId().toHexString()).put("flag", user.getFlag()).encode());
    }

    private void handleGetAccount(RoutingContext ctx) {
        final User requester = getRequester(ctx);

        final User target = (User) Squirrel.getInstance().getDatabaseManager().findFirstEntity(User.class,
                SquirrelCollection.USERS, Filters.eq(new ObjectId(ctx.pathParam("id"))));

        if (requester.isServerAdmin()) {
            ctx.response().end(target.toJson().encode());
            return;
        }

        if (requester.equals(target)) {
            ctx.reroute("/account/me");
            return;
        }

        ctx.response().end(new JsonObject().put("username", target.getUsername())
                .put("discriminator", target.getDiscriminator()).put("id", target.getId().toHexString()).encode());

    }

}
