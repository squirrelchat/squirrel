package chat.squirrel.modules.messaging;

import java.util.Arrays;

import org.bson.types.ObjectId;

import com.mongodb.client.model.Filters;

import chat.squirrel.Squirrel;
import chat.squirrel.WebAuthHandler;
import chat.squirrel.core.DatabaseManager.SquirrelCollection;
import chat.squirrel.entities.Guild;
import chat.squirrel.entities.Guild.Permissions;
import chat.squirrel.modules.AbstractModule;
import chat.squirrel.entities.Member;
import chat.squirrel.entities.User;
import chat.squirrel.entities.channels.IChannel;
import chat.squirrel.entities.channels.TextChannel;
import chat.squirrel.entities.channels.VoiceChannel;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

public class ModuleGuilds extends AbstractModule {

    @Override
    public void initialize() {
        this.registerAuthedRoute(HttpMethod.POST, "/guilds/create", this::handleCreate);
        this.registerAuthedRoute(HttpMethod.POST, "/guild/:id/channels", this::handleCreateChannel);
        this.registerAuthedRoute(HttpMethod.GET, "/guild/:id/channels", this::handleListChannels);
    }
    
    private void handleListChannels(RoutingContext ctx) {
        
    }

    private void handleCreateChannel(RoutingContext ctx) {
        final JsonObject obj = ctx.getBodyAsJson();
        if (obj == null) {
            ctx.fail(400);
            return;
        }

        if (!(obj.containsKey("name") && obj.containsKey("voice"))) {
            ctx.fail(400);
            return;
        }

        final String name = obj.getString("name");
        final boolean voiceChan = obj.getBoolean("voice");

        if (name.length() < 3 && name.length() > 32) {
            ctx.fail(400);
            return;
        }

        final User user = ctx.get(WebAuthHandler.SQUIRREL_SESSION_KEY);
        final Guild guild = (Guild) Squirrel.getInstance().getDatabaseManager().findFirstEntity(Guild.class,
                SquirrelCollection.GUILDS, Filters.eq(new ObjectId(ctx.pathParam("id"))));

        if (guild == null) {
            ctx.fail(404);
            return;
        }

        final Member member = guild.getMemberForUser(user.getId());

        if (member == null) {
            ctx.fail(404); // User isn't in guild so tell him it doesn't exist
            return;
        }

        final IChannel channel;
        if (voiceChan) {
            channel = new VoiceChannel();
        } else {
            channel = new TextChannel();
        }

        channel.setName(name);

        ctx.response().end(channel.toJson().encode());
    }

    private void handleCreate(RoutingContext ctx) {
        final JsonObject obj = ctx.getBodyAsJson();
        if (obj == null) {
            ctx.fail(400);
            return;
        }

        if (!obj.containsKey("name")) {
            ctx.fail(400);
            return;
        }

        final String name = obj.getString("name");

        if (name.length() < 3 && name.length() > 32) {
            ctx.fail(400);
            return;
        }

        final User user = ctx.get(WebAuthHandler.SQUIRREL_SESSION_KEY);

        final Guild newGuild = new Guild();
        newGuild.setName(name);

        final Member owner = new Member();
        owner.setOwner(true);
        owner.setUserId(user.getId());
        owner.setPermissions(Arrays.asList(Permissions.ADMINISTRATOR));

        newGuild.setMembers(Arrays.asList(owner));

        Squirrel.getInstance().getDatabaseManager().insertEntity(SquirrelCollection.GUILDS, newGuild);

        ctx.response().end(newGuild.toJson().encode());
    }

}
