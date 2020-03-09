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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.annotation.Nonnull;

import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.types.ObjectId;

import com.mongodb.client.model.Filters;

import chat.squirrel.Squirrel;
import chat.squirrel.core.DatabaseManager.SquirrelCollection;
import chat.squirrel.entities.AbstractEntity;
import chat.squirrel.entities.IGuild;
import chat.squirrel.entities.IMember;
import chat.squirrel.entities.IRole;
import chat.squirrel.entities.channels.IChannel;

/**
 * A basic Guild
 */
public class GuildImpl extends AbstractEntity implements IGuild {
    private String name;
    private Collection<ObjectId> members = Collections.emptySet();
    private Collection<ObjectId> roles = Collections.emptySet();
    private Collection<ObjectId> channels = Collections.emptySet();

    @Override
    @Nonnull
    public Collection<ObjectId> getChannels() {
        return this.channels;
    }

    /**
     * @param user User ID
     * @return The member corresponding to this user or null otherwise
     */
    @Override
    public IMember getMemberForUser(final ObjectId user) {
        try {
            for (final IMember m : this.getRealMembers().get()) {
                if (m.getUserId().equals(user)) {
                    return m;
                }
            }
        } catch (InterruptedException | ExecutionException e) { // Shouldn't be called
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void addMember(final IMember m) {
        m.setGuildId(this.getId());
        Squirrel.getInstance().getDatabaseManager().insertEntity(SquirrelCollection.MEMBERS, m);
    }

    @Override
    @BsonIgnore
    public Future<Collection<IMember>> getRealMembers() {
        return CompletableFuture.supplyAsync(() -> {
            if (this.getMembers() == null) {
                return Collections.emptyList();
            }

            final ArrayList<IMember> list = new ArrayList<>();

            for (final ObjectId id : this.getMembers()) {
                final IMember member = Squirrel.getInstance().getDatabaseManager().findFirstEntity(IMember.class,
                        SquirrelCollection.MEMBERS, Filters.eq(id));
                list.add(member);
            }
            return list;
        });
    }

    /**
     * @return The {@link IMember}s that are a part of this Guild
     */
    @Override
    @Nonnull
    public Collection<ObjectId> getMembers() {
        return this.members;
    }

    /**
     * @return The display name of the Guild
     */
    @Override
    public String getName() {
        return this.name;
    }

    @Override
    @BsonIgnore
    public Future<Collection<IChannel>> getRealChannels() {
        return CompletableFuture.supplyAsync(() -> {
            if (this.getChannels() == null) {
                return Collections.emptyList();
            }

            final ArrayList<IChannel> list = new ArrayList<>();

            for (final ObjectId id : this.getChannels()) {
                final IChannel chan = Squirrel.getInstance().getDatabaseManager().findFirstEntity(IChannel.class,
                        SquirrelCollection.CHANNELS, Filters.eq(id));
                list.add(chan);
            }
            return list;
        });

    }

    @Override
    @BsonIgnore
    public Future<Collection<IRole>> getRealRoles() {
        return CompletableFuture.supplyAsync(() -> {
            if (this.getRoles() == null) {
                return Collections.emptyList();
            }

            final ArrayList<IRole> list = new ArrayList<>();

            for (final ObjectId id : this.getRoles()) {
                final IRole chan = Squirrel.getInstance().getDatabaseManager().findFirstEntity(IRole.class,
                        SquirrelCollection.ROLES, Filters.eq(id));
                list.add(chan);
            }
            return list;
        });

    }

    /**
     * @return The {@link IRole}s that are created in this Guild
     */
    @Override
    @Nonnull
    public Collection<ObjectId> getRoles() {
        return this.roles;
    }

    @Override
    public void setChannels(@Nonnull final Collection<ObjectId> channels) {
        this.channels = channels;
    }

    /**
     * @param members The {@link IMember}s that are a part of this Guild
     */
    @Override
    public void setMembers(@Nonnull final Collection<ObjectId> members) {
        this.members = members;
    }

    /**
     * @param name The display name of the Guild
     */
    @Override
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * @param roles The {@link IRole}s that are created in this Guild
     */
    @Override
    public void setRoles(@Nonnull final Collection<ObjectId> roles) {
        this.roles = roles;
    }

}
