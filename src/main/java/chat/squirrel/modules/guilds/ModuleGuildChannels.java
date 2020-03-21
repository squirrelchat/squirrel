/*
 * Copyright (c) 2020 Squirrel Chat, All rights reserved.
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
import chat.squirrel.database.DatabaseManagerEditionBoomerware.SquirrelCollection;
import chat.squirrel.database.entities.IAudit.AuditLogEntryType;
import chat.squirrel.database.entities.IGuild;
import chat.squirrel.database.entities.IGuild.Permissions;
import chat.squirrel.database.entities.IMember;
import chat.squirrel.database.entities.IUser;
import chat.squirrel.database.entities.channels.IChannel;
import chat.squirrel.database.entities.channels.IVoiceChannel;
import chat.squirrel.database.entities.channels.impl.GuildTextChannelImpl;
import chat.squirrel.database.entities.channels.impl.GuildVoiceChannelImpl;
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
        this.registerAuthedRoute(HttpMethod.PATCH, "/guilds/:id/channels/:chanId", this::notImplemented);
        this.registerAuthedRoute(HttpMethod.DELETE, "/guilds/:id/channels/:chanId", this::handleDeleteChannel);
    }

    private void handleCreateChannel(final RoutingContext ctx) {
        final IUser user = this.getRequester(ctx);
        final IGuild guild = this.getGuild(ctx);

        if (guild == null) {
            return; // Payload already handled; No extra processing required
        }

        final IMember member = guild.getMemberForUser(user.getId());

        if (member.hasEffectivePermission(Permissions.GUILD_MANAGE_CHANNELS)) {
            this.fail(ctx, 403, "Missing permissions", null);
            return;
        }

        final JsonObject obj = ctx.getBodyAsJson();
        if (obj == null) {
            this.fail(ctx, 400, "Invalid JSON payload", null);
            return;
        }

        if (!(obj.containsKey("name") && obj.containsKey("voice"))) {
            this.fail(ctx, 400, "Missing fields", null);
            return;
        }

        final String name = obj.getString("name");
        if (name.length() < 3 || name.length() > 32) {
            this.fail(ctx, 400, "name has invalid length", null);
            return;
        }

        final boolean voiceChan = obj.getBoolean("voice", false);
        final IChannel channel;
        if (voiceChan) {
            channel = new GuildVoiceChannelImpl();
        } else {
            channel = new GuildTextChannelImpl();
        }

        channel.setName(name); // @todo: Take into account other payload fields (?)

        this.submitAudit(guild.getId(), user.getId(), AuditLogEntryType.CHANNEL_CREATE);

        ctx.response().end(channel.toJson().encode());
    }

    private void handleListChannels(final RoutingContext ctx) {
        final JsonObject obj = ctx.getBodyAsJson();
        if (obj == null) {
            this.fail(ctx, 400, "Invalid JSON payload", null);
            return;
        }

        final IUser user = this.getRequester(ctx);
        final IGuild guild = Squirrel.getInstance()
                .getDatabaseManager()
                .findFirstEntity(IGuild.class, SquirrelCollection.GUILDS,
                        Filters.eq(new ObjectId(ctx.pathParam("id"))));

        if (guild == null) {
            this.fail(ctx, 404, "Guild not found", null);
            return;
        }

        final IMember member = guild.getMemberForUser(user.getId());
        if (member == null) {
            this.fail(ctx, 404, "Guild not found", null);
            return;
        }

        final JsonArray out = new JsonArray();

        try {
            for (final IChannel chan : guild.getRealChannels().get()) { // Channel-chan (#^.^#)
                final JsonObject jsonChan = new JsonObject();
                // @todo: Add all of the fields we'll have
                jsonChan.put("name", chan.getName());
                jsonChan.put("id", chan.getId().toHexString());
                jsonChan.put("voice", chan instanceof IVoiceChannel);
                out.add(jsonChan);
            }
        } catch (InterruptedException | ExecutionException e) { // Shouldn't be called
            e.printStackTrace();
        }

        ctx.response().end(out.encode());
    }

    private void handleDeleteChannel(final RoutingContext ctx) {
        final IUser user = this.getRequester(ctx);
        final IGuild guild = this.getGuild(ctx);
        if (guild == null) {
            return; // Payload already handled; No extra processing required
        }

        final IMember member = guild.getMemberForUser(user.getId());
        if (member.hasEffectivePermission(Permissions.GUILD_MANAGE_CHANNELS)) {
            this.fail(ctx, 403, "Missing permissions", null);
            // noinspection UnnecessaryReturnStatement
            return;
        }

        // TODO
    }
}
