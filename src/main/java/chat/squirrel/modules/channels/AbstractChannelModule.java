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

package chat.squirrel.modules.channels;

import org.bson.types.ObjectId;

import com.mongodb.client.model.Filters;

import chat.squirrel.Squirrel;
import chat.squirrel.core.DatabaseManager;
import chat.squirrel.entities.Guild;
import chat.squirrel.entities.User;
import chat.squirrel.entities.channels.IChannel;
import chat.squirrel.modules.AbstractModule;
import io.vertx.ext.web.RoutingContext;

public abstract class AbstractChannelModule extends AbstractModule {
    protected IChannel getChannel(final RoutingContext ctx, final User user, final Guild.Permissions permission) {
        final IChannel channel = Squirrel.getInstance().getDatabaseManager().findFirstEntity(IChannel.class,
                DatabaseManager.SquirrelCollection.GUILDS, Filters.eq(new ObjectId(ctx.pathParam("id"))));

        if (channel == null) {
            ctx.fail(404);
            return null;
        }

        // @todo: fetch member and do perm checks
        // final Member member = guild.getMemberForUser(user.getId());
        // if (member == null || (permission != null &&
        // !member.hasEffectivePermission(permission))) {
        // ctx.response().setStatusCode(403).end(new JsonObject().put("message",
        // "Missing Permissions").encode());
        // return null;
        // }

        return channel;
    }
}
