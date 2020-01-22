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

package chat.squirrel.entities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.types.ObjectId;

import com.mongodb.client.model.Filters;

import chat.squirrel.Squirrel;
import chat.squirrel.core.DatabaseManager.SquirrelCollection;

/**
 * Member of a guild
 */
public class Member extends AbstractEntity {
    private ObjectId userId, guildId;
    private String nickmame;
    private Collection<ObjectId> roles;

    public ObjectId getUserId() {
        return userId;
    }

    public void setUserId(ObjectId userId) {
        this.userId = userId;
    }

    @BsonIgnore
    public Future<User> getUser() {
        return new FutureTask<>(() ->  (User) Squirrel.getInstance().getDatabaseManager().findFirstEntity(User.class,
                SquirrelCollection.USERS, Filters.eq(getUserId())));
    }

    public Collection<ObjectId> getRolesIds() {
        return roles;
    }

    public void setRolesIds(Collection<ObjectId> roles) {
        this.roles = roles;
    }

    @BsonIgnore
    public Future<Guild> getGuild() {
        return new FutureTask<>(() -> (Guild) Squirrel.getInstance().getDatabaseManager().findFirstEntity(Guild.class,
                SquirrelCollection.GUILDS, Filters.eq(getGuildId())));
    }

    @BsonIgnore
    public Future<Collection<Role>> getRoles() {
        return new FutureTask<>(() -> { // XXX this is ugly
            final Guild guild = getGuild().get();
            final Collection<ObjectId> ids = getRolesIds();
            final Collection<Role> realRoles = new ArrayList<>();
            for (final Role role : guild.getRoles()) {
                if (ids.contains(role.getId()))
                    realRoles.add(role);
            }
            return realRoles;
        });
    }

    public String getNickmame() {
        return nickmame;
    }

    public void setNickmame(String nickmame) {
        this.nickmame = nickmame;
    }

    public ObjectId getGuildId() {
        return guildId;
    }

    public void setGuildId(ObjectId guildId) {
        this.guildId = guildId;
    }

}
