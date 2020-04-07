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
import chat.squirrel.database.collections.IGuildCollection;
import chat.squirrel.database.entities.IGuild;
import chat.squirrel.database.entities.IMember;
import chat.squirrel.database.entities.IUser;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import java.util.Collections;

public class ModuleGuilds extends AbstractGuildModule {
    @Override
    public void initialize() {
        this.registerAuthedRoute(HttpMethod.POST, "/guilds", this::handleCreate);
        this.registerAuthedRoute(HttpMethod.PATCH, "/guilds/:id", this::notImplemented);
        this.registerAuthedRoute(HttpMethod.DELETE, "/guilds/:id", this::notImplemented);
    }

    private void handleCreate(final RoutingContext ctx) {
        final JsonObject obj = ctx.getBodyAsJson();
        if (obj == null) {
            this.end(ctx, 400, "Invalid JSON payload", null);
            return;
        }

        if (!obj.containsKey("name")) {
            this.end(ctx, 400, "Incomplete payload", null);
            return;
        }

        final String name = obj.getString("name");
        if (name.length() < 3 || name.length() > 32) {
            this.end(ctx, 400, "Field name has invalid length", null);
            return;
        }

        final IUser user = this.getRequester(ctx);
        final IGuild newGuild = IGuild.create();
        final IMember owner = IMember.create();
        owner.setUserId(user.getId());
        newGuild.setName(name);
        newGuild.setMembers(Collections.singleton(owner));
        newGuild.setOwnerId(user.getId());

        Squirrel.getInstance().getDatabaseManager().getCollection(IGuildCollection.class).insertOne(newGuild);

        // MetricsManager.getInstance().happened("guild.create");
        ctx.response().setStatusCode(201).end(newGuild.toJson().encode());
    }
}
