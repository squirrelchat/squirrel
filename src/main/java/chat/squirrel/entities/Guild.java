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
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.types.ObjectId;

import com.mongodb.client.model.Filters;

import chat.squirrel.Squirrel;
import chat.squirrel.core.DatabaseManager.SquirrelCollection;
import chat.squirrel.entities.channels.IChannel;

/**
 * A basic Guild
 */
public class Guild extends AbstractEntity {
    private String name;
    private Collection<ObjectId> members;
    private Collection<ObjectId> roles;

    private Collection<ObjectId> channels;

    public Collection<ObjectId> getChannels() {
        return this.channels;
    }

    /**
     * @param user User ID
     * @return The member corresponding to this user or null otherwise
     */
    public Member getMemberForUser(final ObjectId user) {
        try {
            for (final Member m : this.getRealMembers().get()) {
                if (m.getUserId().equals(user)) {
                    return m;
                }
            }
        } catch (InterruptedException | ExecutionException e) { // Shouldn't be called
            e.printStackTrace();
        }
        return null;
    }

    public void addMember(Member m) {
        m.setGuildId(getId());
        Squirrel.getInstance().getDatabaseManager().insertEntity(SquirrelCollection.MEMBERS, m);
    }

    @BsonIgnore
    public Future<Collection<Member>> getRealMembers() {
        return CompletableFuture.supplyAsync(() -> {
            if (this.getMembers() == null) {
                return Collections.emptyList();
            }

            final ArrayList<Member> list = new ArrayList<>();

            for (final ObjectId id : this.getMembers()) {
                final Member member = Squirrel.getInstance().getDatabaseManager().findFirstEntity(Member.class,
                        SquirrelCollection.MEMBERS, Filters.eq(id));
                list.add(member);
            }
            return list;
        });
    }

    /**
     * @return The {@link Member}s that are a part of this Guild
     */
    public Collection<ObjectId> getMembers() {
        return this.members;
    }

    /**
     * @return The display name of the Guild
     */
    public String getName() {
        return this.name;
    }

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
    
    @BsonIgnore
    public Future<Collection<Role>> getRealRoles() {
        return CompletableFuture.supplyAsync(() -> {
            if (this.getRoles() == null) {
                return Collections.emptyList();
            }

            final ArrayList<Role> list = new ArrayList<>();

            for (final ObjectId id : this.getRoles()) {
                final Role chan = Squirrel.getInstance().getDatabaseManager().findFirstEntity(Role.class,
                        SquirrelCollection.ROLES, Filters.eq(id));
                list.add(chan);
            }
            return list;
        });

    }

    /**
     * @return The {@link Role}s that are created in this Guild
     */
    public Collection<ObjectId> getRoles() {
        return this.roles;
    }

    public void setChannels(final Collection<ObjectId> channels) {
        this.channels = channels;
    }

    /**
     * @param members The {@link Member}s that are a part of this Guild
     */
    public void setMembers(final Collection<ObjectId> members) {
        this.members = members;
    }

    /**
     * @param name The display name of the Guild
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * @param roles The {@link Role}s that are created in this Guild
     */
    public void setRoles(final Collection<ObjectId> roles) {
        this.roles = roles;
    }

    /**
     * Permissions
     */
    public enum Permissions {
        /**
         * Has all perms and ignores channels overrides
         */
        ADMINISTRATOR,
        /**
         * Can manage settings of the guild. <b>Implicitly grants: GUILD_MANAGE_ROLES,
         * GUILD_MANAGE_PERMISSIONS, GUILD_MANAGE_WEBHOOKS, GUILD_MANAGE_INTEGRATIONS,
         * GUILD_MANAGE_CHANNELS, GUILD_MANAGE_INVITES, GUILD_AUDITS, CHANNEL_ACCESS</b>
         */
        GUILD_MANAGE,
        /**
         * Ability to add, edit and delete roles
         */
        GUILD_MANAGE_ROLES,
        /**
         * Ability to modify permissions per-channel
         */
        GUILD_MANAGE_PERMISSIONS,
        /**
         * Ability to add, edit and delete webhooks
         */
        GUILD_MANAGE_WEBHOOKS,
        /**
         * Ability to add, edit and delete integrations as well as adding bots
         */
        GUILD_MANAGE_INTEGRATIONS,
        /**
         * Ability to add, edit and delete channels <b>Bypasses automated text channels
         * moderation</b>
         */
        GUILD_MANAGE_CHANNELS,
        /**
         * Ability to add, edit and delete emojis
         */
        GUILD_MANAGE_EMOJIS,
        /**
         * Ability to add, edit and delete channels
         */
        GUILD_MANAGE_INVITES,
        /**
         * Access guild's audit logs
         */
        GUILD_AUDITS,
        /**
         * Can manage moderation-related settings of the guild. <b>Implicitly grants:
         * GUILD_MANAGE_NICKNAMES, GUILD_AUDITS, MEMBER_BAN, MEMBER_TEMP_BAN,
         * MEMBER_KICK, MEMBER_MUTE, TEXT_MANAGE_MESSAGES</b>
         */
        GUILD_MANAGE_MODERATION,
        /**
         * Ban {@link Member}s from the Guild
         */
        MEMBER_BAN,
        /**
         * Ban temporarily {@link Member}s from the Guild
         */
        MEMBER_TEMP_BAN,
        /**
         * Kick {@link Member} from this Guild
         */
        MEMBER_KICK,
        /**
         * Mute {@link Member} from talking/speaking in channels
         */
        MEMBER_MUTE,
        /**
         * Ability to add, edit and delete channels
         */
        GUILD_MANAGE_NICKNAMES,
        /**
         * Ability to delete messages and/or reactions <b>Bypasses automated text
         * channels moderation</b>
         */
        TEXT_MANAGE_MESSAGES,
        /**
         * Allows the creation of invites
         */
        GUILD_CREATE_INVITES,
        /**
         * Allow Member to change own nickname
         */
        MEMBER_CHANGE_NICKNAME,
        /**
         * Allows viewing and accessing a channel. If not granted guild-wide, members
         * will only be able to see channels they're explicitly allowed to through
         * permission overrides.
         *
         * <b>Implicitly revokes ALL permissions on the channel if missing</b>
         */
        CHANNEL_ACCESS,
        /**
         * Allows sending messages to the channel
         */
        TEXT_SEND_MESSAGES,
        /**
         * Allows creating polls
         */
        TEXT_RICH_MESSAGES_POLL,
        /**
         * Allows adding reactions to messages
         *
         * <b>Note: People without reactions can add their own reaction on already
         * existing reactions.</b>
         */
        TEXT_ADD_REACTIONS,
        /**
         * Allows users to upload files to a channel
         */
        TEXT_UPLOAD_FILES,
        // @todo: Voice chat perms
    }
}
