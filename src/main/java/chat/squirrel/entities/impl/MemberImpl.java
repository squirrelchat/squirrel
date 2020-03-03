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

package chat.squirrel.entities.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import javax.annotation.Nonnull;

import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.types.ObjectId;

import com.mongodb.client.model.Filters;

import chat.squirrel.Squirrel;
import chat.squirrel.core.DatabaseManager.SquirrelCollection;
import chat.squirrel.entities.AbstractEntity;
import chat.squirrel.entities.Guild;
import chat.squirrel.entities.Guild.Permissions;
import chat.squirrel.entities.Member;
import chat.squirrel.entities.Role;
import chat.squirrel.entities.User;

/**
 * Member of a guild
 */
public class MemberImpl extends AbstractEntity implements Member {
    private ObjectId userId, guildId;
    private String nickname;
    private Collection<ObjectId> roles = Collections.emptySet();

    // @todo: Wipe this and fetch them from roles
    private Collection<Permissions> permissions = Collections.emptySet();
    private boolean owner;

    /**
     * @return The {@link Guild} that this Member is a part of
     */
    @Override
    @BsonIgnore
    public Future<Guild> getGuild() {
        // @todo: use an aggregation at query-time
        return new FutureTask<>(() -> Squirrel.getInstance().getDatabaseManager().findFirstEntity(Guild.class,
                SquirrelCollection.GUILDS, Filters.eq(this.getGuildId())));
    }

    /**
     * @return The ID corresponding to the Guild this Member is apart of.
     */
    @Override
    public ObjectId getGuildId() {
        return this.guildId;
    }

    /**
     * @return This user's nickname for this Guild
     */
    @Override
    public String getNickname() {
        return this.nickname;
    }

    // @todo: Fetch them from roles
    @Override
    @Nonnull
    public Collection<Permissions> getPermissions() {
        return this.permissions;
    }

    /**
     * @return Get the roles this Member possesses.
     */
    @Override
    @BsonIgnore
    public Future<Collection<Role>> getRoles() {
        // @todo: use an aggregation at query-time
        return new FutureTask<>(() -> { // XXX this is ugly
            final Guild guild = this.getGuild().get();
            final Collection<ObjectId> ids = this.getRolesIds();
            final Collection<Role> realRoles = new ArrayList<>();
            for (final Role role : guild.getRealRoles().get()) {
                if (ids.contains(role.getId())) {
                    realRoles.add(role);
                }
            }
            return realRoles;
        });
    }

    /**
     * @return The IDs corresponding to the Guild roles that the Member possesses.
     */
    @Override
    public Collection<ObjectId> getRolesIds() {
        return this.roles;
    }

    /**
     * Async because DB request
     *
     * @return Future that will return the {@link User} corresponding to this
     *         Member.
     */
    @Override
    @BsonIgnore
    public Future<User> getUser() {
        return new FutureTask<>(() -> Squirrel.getInstance().getDatabaseManager().findFirstEntity(User.class,
                SquirrelCollection.USERS, Filters.eq(this.getUserId())));
    }

    /**
     * @return The ID corresponding to the {@link User} associated with this Member
     */
    @Override
    public ObjectId getUserId() {
        return this.userId;
    }

    @Override
    public boolean hasEffectivePermission(final Permissions perm) {
        if (this.isOwner()) {
            return true;
        }

        if (this.getPermissions() == null) {
            return false;
        }

        if (perm.name().startsWith("GUILD_MANAGE_") && this.getPermissions().contains(Permissions.GUILD_MANAGE)) {
            return true;
        }

        return this.getPermissions().contains(perm) || this.getPermissions().contains(Permissions.ADMINISTRATOR);
    }

    /**
     * Sets whether this members is the owner of the server
     *
     * @return if the member is the owner of the guild
     */
    @Override
    public boolean isOwner() {
        return this.owner;
    }

    /**
     * @param guildId The ID corresponding to the Guild this Member is apart of.
     */
    @Override
    public void setGuildId(final ObjectId guildId) {
        this.guildId = guildId;
    }

    /**
     * @param nickname The user's nickname for this Guild
     */
    @Override
    public void setNickname(final String nickname) {
        this.nickname = nickname;
    }

    @Override
    public void setOwner(final boolean owner) {
        this.owner = owner;
    }

    // @todo: Fetch them from roles
    @Override
    public void setPermissions(@Nonnull final Collection<Permissions> permissions) {
        this.permissions = permissions;
    }

    /**
     * @param roles The IDs corresponding to the Guild roles that the Member
     *              possesses.
     */
    @Override
    public void setRolesIds(@Nonnull final Collection<ObjectId> roles) {
        this.roles = roles;
    }

    /**
     * @param userId The ID corresponding to the {@link User} associated with this
     *               Member.
     */
    @Override
    public void setUserId(final ObjectId userId) {
        this.userId = userId;
    }

}
