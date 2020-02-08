/*
 * Copyright (c) 2020-present Bowser65 & vinceh121, All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holder nor the names of its contributors
 *    may be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package chat.squirrel.modules.guilds;

import chat.squirrel.Squirrel;
import chat.squirrel.core.DatabaseManager.SquirrelCollection;
import chat.squirrel.entities.Guild;
import chat.squirrel.entities.Guild.Permissions;
import chat.squirrel.entities.Member;
import chat.squirrel.entities.User;
import chat.squirrel.entities.channels.IChannel;
import chat.squirrel.entities.channels.TextChannel;
import chat.squirrel.entities.channels.VoiceChannel;
import com.mongodb.client.model.Filters;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.bson.types.ObjectId;

import java.util.concurrent.ExecutionException;

public class ModuleGuildChannels extends AbstractGuildModule {
    @Override
    public void initialize() {
        this.registerAuthedRoute(HttpMethod.POST, "/guilds/:id/channels", this::handleCreateChannel);
        this.registerAuthedRoute(HttpMethod.GET, "/guilds/:id/channels", this::handleListChannels);
        this.registerAuthedRoute(HttpMethod.PATCH, "/guilds/:id/channels", this::notImplemented); // Channel order
        this.registerAuthedRoute(HttpMethod.PATCH, "/guilds/:id/channels/:id", this::notImplemented);
        this.registerAuthedRoute(HttpMethod.DELETE, "/guilds/:id/channels/:id", this::notImplemented);
    }

    private void handleCreateChannel(RoutingContext ctx) {
        final User user = getRequester(ctx);
        final Guild guild = getGuild(ctx, user, Permissions.GUILD_MANAGE_CHANNELS);
        if (guild == null) {
            return; // Payload already handled; No extra processing required
        }

        final JsonObject obj = ctx.getBodyAsJson();
        if (obj == null) {
            ctx.fail(400); // @todo: Proper error payload
            return;
        }

        if (!(obj.containsKey("name") && obj.containsKey("voice"))) {
            ctx.fail(400); // @todo: Proper error payload
            return;
        }

        final String name = obj.getString("name");
        if (name.length() < 3 || name.length() > 32) {
            ctx.fail(400); // @todo: Proper error payload
            return;
        }

        final boolean voiceChan = obj.getBoolean("voice", false);
        final IChannel channel;
        if (voiceChan) {
            channel = new VoiceChannel();
        } else {
            channel = new TextChannel();
        }

        channel.setName(name); // @todo: Take into account other payload fields (?)
        ctx.response().end(channel.toJson().encode());
    }

    private void handleListChannels(RoutingContext ctx) {
        final JsonObject obj = ctx.getBodyAsJson();
        if (obj == null) {
            ctx.fail(400);
            return;
        }

        final User user = getRequester(ctx);
        final Guild guild = Squirrel.getInstance().getDatabaseManager().findFirstEntity(Guild.class,
                SquirrelCollection.GUILDS, Filters.eq(new ObjectId(ctx.pathParam("id"))));

        if (guild == null) {
            ctx.fail(404);
            return;
        }

        final Member member = guild.getMemberForUser(user.getId());
        if (member == null) {
            ctx.fail(404);
            return;
        }

        final JsonArray out = new JsonArray();

        try {
            for (IChannel chan : guild.getRealChannels().get()) { // Channel-chan (#^.^#)
                final JsonObject jsonChan = new JsonObject();
                // @todo: Add all of the fields we'll have
                jsonChan.put("name", chan.getName());
                jsonChan.put("id", chan.getId().toHexString());
                jsonChan.put("voice", chan instanceof VoiceChannel);
                out.add(jsonChan);
            }
        } catch (InterruptedException | ExecutionException e) { // Shouldn't be called
            e.printStackTrace();
        }

        ctx.response().end(out.encode());
    }
}