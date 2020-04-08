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
import chat.squirrel.database.collections.IMemberCollection;
import chat.squirrel.database.entities.IGuild;
import chat.squirrel.database.entities.IMember;
import chat.squirrel.modules.AbstractCrudModule;
import com.mongodb.client.result.InsertOneResult;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.bson.conversions.Bson;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class ModuleGuilds extends AbstractCrudModule<IGuild> {
    public ModuleGuilds() {
        super(Squirrel.getInstance().getDatabaseManager().getCollection(IGuildCollection.class));
    }

    @Override
    public void initialize() {
        registerCrud("/guilds");
    }

    @Override
    protected boolean hasPermission(RoutingContext ctx, AbstractCrudModule.CrudContext context) {
        switch (context) {
            case CREATE:
                return true; // TODO: Max guild cap
            case READ:
                return true; // TODO: Is member?
            case UPDATE:
                return true; // TODO: Has permissions?
            case DELETE:
                return ctx.<IGuild>get("entity").getOwnerId().equals(getRequester(ctx).getId());
            default:
                return false;
        }
    }

    @Override
    protected IGuild createEntity(RoutingContext ctx) {
        final JsonObject obj = ctx.getBodyAsJson();
        if (obj == null || !obj.containsKey("name")) {
            return null;
        }

        final String name = obj.getString("name");
        if (name.length() < 2 || name.length() > 32) {
            return null;
        }

        final IGuild guild = IGuild.create();
        guild.setName(name);
        guild.setOwnerId(getRequester(ctx).getId());
        return guild;
    }

    @Override
    protected Bson composeUpdate(RoutingContext ctx) {
        // TODO: Update guild
        return null;
    }

    @Override
    protected CompletionStage<InsertOneResult> insertEntity(IGuild entity) {
        final IMember member = IMember.create();
        member.setUserId(entity.getOwnerId());
        member.setGuildId(entity.getId());

        // TODO: Add default channels
        final CompletableFuture<InsertOneResult> future = new CompletableFuture<>();
        final CompletableFuture<InsertOneResult> futureGuild = super.insertEntity(entity).toCompletableFuture();
        final CompletableFuture<InsertOneResult> futureMember = Squirrel.getInstance()
                .getDatabaseManager().getCollection(IMemberCollection.class).insertOne(member)
                .toCompletableFuture();
        CompletableFuture.allOf(futureGuild, futureMember).thenAccept(v -> future.complete(futureGuild.getNow(null)));
        return future;
    }
}
