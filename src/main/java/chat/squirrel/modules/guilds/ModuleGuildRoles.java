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
import chat.squirrel.database.collections.IRoleCollection;
import chat.squirrel.database.entities.IGuild;
import chat.squirrel.database.entities.IRole;
import chat.squirrel.modules.AbstractCrudChildEntity;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.RoutingContext;
import org.bson.conversions.Bson;

// TODO: Implement crud methods
public class ModuleGuildRoles extends AbstractCrudChildEntity<IRole, IGuild> {
    public ModuleGuildRoles() {
        super(
                Squirrel.getInstance().getDatabaseManager().getCollection(IRoleCollection.class),
                Squirrel.getInstance().getDatabaseManager().getCollection(IGuildCollection.class),
                "guildId", "guild_id"
        );
    }

    @Override
    public void initialize() {
        registerCrud("/guilds/:guild_id/roles");
        this.registerAuthedRoute(HttpMethod.PATCH, "/guilds/:guild_id/roles", this::notImplemented); // Roles order
    }

    @Override
    protected boolean hasPermission(final RoutingContext ctx, final CrudContext context) {
        return false;
    }

    @Override
    protected IRole createEntity(final RoutingContext ctx) {
        return null;
    }

    @Override
    protected Bson composeUpdate(final RoutingContext ctx) {
        return null;
    }
}
