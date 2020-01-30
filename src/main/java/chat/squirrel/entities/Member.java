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
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.types.ObjectId;

import com.mongodb.client.model.Filters;

import chat.squirrel.Squirrel;
import chat.squirrel.core.DatabaseManager.SquirrelCollection;
import chat.squirrel.entities.Guild.Permissions;

/**
 * Member of a guild
 */
public class Member extends AbstractEntity {
    private ObjectId userId, guildId;
    private String nickname;
    private Collection<ObjectId> roles;
    private Collection<Permissions> permissions;
    private boolean owner;

    /**
     * @return The ID corresponding to the {@link User} associated with this Member
     */
    public ObjectId getUserId() {
        return userId;
    }

    /**
     * @param userId The ID corresponding to the {@link User} associated with this
     *               Member.
     */
    public void setUserId(ObjectId userId) {
        this.userId = userId;
    }

    /**
     * Async because DB request
     *
     * @return Future that will return the {@link User} corresponding to this
     *         Member.
     */
    @BsonIgnore
    public Future<User> getUser() {
        return new FutureTask<>(() -> (User) Squirrel.getInstance().getDatabaseManager().findFirstEntity(User.class,
                SquirrelCollection.USERS, Filters.eq(getUserId())));
    }

    /**
     * @return The IDs corresponding to the Guild roles that the Member possesses.
     */
    public Collection<ObjectId> getRolesIds() {
        return roles;
    }

    /**
     * @param roles The IDs corresponding to the Guild roles that the Member possesses.
     */
    public void setRolesIds(Collection<ObjectId> roles) {
        this.roles = roles;
    }

    /**
     * @return The {@link Guild} that this Member is a part of
     */
    @BsonIgnore
    public Future<Guild> getGuild() {
        // @todo: use an aggregation at query-time
        return new FutureTask<>(() -> (Guild) Squirrel.getInstance().getDatabaseManager().findFirstEntity(Guild.class,
                SquirrelCollection.GUILDS, Filters.eq(getGuildId())));
    }

    /**
     * @return Get the roles this Member possesses.
     */
    @BsonIgnore
    public Future<Collection<Role>> getRoles() {
        // @todo: use an aggregation at query-time
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

    /**
     *
     * @return This user's nickname for this Guild
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * @param nickname The user's nickname for this Guild
     */
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    /**
     * @return The ID corresponding to the Guild this Member is apart of.
     */
    public ObjectId getGuildId() {
        return guildId;
    }

    /**
     *
     * @param guildId The ID corresponding to the Guild this Member is apart of.
     */
    public void setGuildId(ObjectId guildId) {
        this.guildId = guildId;
    }

    public Collection<Permissions> getPermissions() {
        return permissions;
    }

    public void setPermissions(Collection<Permissions> permissions) {
        this.permissions = permissions;
    }

    /**
     * Sets whether this members is the owner of the server
     * @return if the member is the owner of the guild
     */
    public boolean isOwner() {
        return owner;
    }

    public void setOwner(boolean owner) {
        this.owner = owner;
    }

}
