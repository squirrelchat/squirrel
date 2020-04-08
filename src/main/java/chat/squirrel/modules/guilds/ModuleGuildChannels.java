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
import chat.squirrel.database.collections.IChannelCollection;
import chat.squirrel.database.collections.IGuildCollection;
import chat.squirrel.database.entities.IGuild;
import chat.squirrel.database.entities.channels.IChannel;
import chat.squirrel.database.entities.channels.IGuildChannel;
import chat.squirrel.database.entities.channels.IGuildTextChannel;
import chat.squirrel.database.entities.channels.IGuildVoiceChannel;
import chat.squirrel.modules.AbstractCrudChildEntity;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.bson.conversions.Bson;

public class ModuleGuildChannels extends AbstractCrudChildEntity<IChannel, IGuild> {
    public ModuleGuildChannels() {
        super(
                Squirrel.getInstance().getDatabaseManager().getCollection(IChannelCollection.class),
                Squirrel.getInstance().getDatabaseManager().getCollection(IGuildCollection.class),
                "guildId", "guild_id"
        );
    }

    @Override
    public void initialize() {
        registerCrud("/guilds/:guild_id/channels");
        this.registerAuthedRoute(HttpMethod.PATCH, "/guilds/:guild_id/channels", this::notImplemented); // Channel order
    }

    @Override
    @SuppressWarnings("DuplicateBranchesInSwitch")
    protected boolean hasPermission(RoutingContext ctx, CrudContext context) {
        switch (context) {
            case CREATE:
                return true; // TODO: Has permission?
            case READ:
                return true; // TODO: Has permission?
            case UPDATE:
                return true; // TODO: Has permissions?
            case DELETE:
                return ctx.<IGuild>get("parent").getOwnerId().equals(getRequester(ctx).getId());
            default:
                return false;
        }
    }

    @Override
    protected IChannel createEntity(RoutingContext ctx) {
        final JsonObject obj = ctx.getBodyAsJson();
        if (obj == null || !obj.containsKey("name")) {
            return null;
        }

        final String name = obj.getString("name");
        if (name.length() < 4 || name.length() > 32) {
            return null;
        }

        IGuildChannel channel;
        if (ctx.getBodyAsJson().getBoolean("voice", false)) {
            channel = IGuildVoiceChannel.create();
        } else {
            channel = IGuildTextChannel.create();
        }
        channel.setName(name);
        channel.setGuildId(ctx.<IGuild>get("parent").getId());
        return channel;
    }

    @Override
    protected Bson composeUpdate(RoutingContext ctx) {
        return null; // TODO: Update channels
    }
}
