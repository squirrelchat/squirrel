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

import chat.squirrel.Squirrel;
import chat.squirrel.core.DatabaseManager.SquirrelCollection;
import chat.squirrel.entities.channels.IChannel;
import com.mongodb.client.model.Filters;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

/**
 * A basic Guild
 */
public class Guild extends AbstractEntity {
    private String name;
    private Collection<Member> members; // FIXME maybe move to own collection? (yes)
    private Collection<Role> roles;
    private Collection<ObjectId> channels;

    /**
     * @return The display name of the Guild
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The display name of the Guild
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return The {@link Member}s that are a part of this Guild
     */
    public Collection<Member> getMembers() {
        return members;
    }

    /**
     * @param members The {@link Member}s that are a part of this Guild
     */
    public void setMembers(Collection<Member> members) {
        this.members = members;
    }

    /**
     * @return The {@link Role}s that are created in this Guild
     */
    public Collection<Role> getRoles() {
        return roles;
    }

    /**
     * @param roles The {@link Role}s that are created in this Guild
     */
    public void setRoles(Collection<Role> roles) {
        this.roles = roles;
    }

    /**
     * @param user User ID
     * @return The member corresponding to this user or null otherwise
     */
    public Member getMemberForUser(ObjectId user) {
        for (Member m : getMembers()) {
            if (m.getUserId().equals(user))
                return m;
        }
        return null;
    }

    public Collection<ObjectId> getChannels() {
        return channels;
    }

    public void setChannels(Collection<ObjectId> channels) {
        this.channels = channels;
    }

    @BsonIgnore
    public Future<Collection<IChannel>> getRealChannels() {
        return CompletableFuture.supplyAsync(() -> {
            if (getChannels() == null)
                return Collections.emptyList();

            final ArrayList<IChannel> list = new ArrayList<>();

            for (ObjectId id : getChannels()) {
                final IChannel chan = Squirrel.getInstance().getDatabaseManager()
                        .findFirstEntity(IChannel.class, SquirrelCollection.CHANNELS, Filters.eq(id));
                list.add(chan);
            }
            return list;
        });

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
         * Allows viewing and accessing a channel.
         * <p>
         * If not granted guild-wide, members will only be able to see channels they're
         * explicitly allowed to through permission overrides.
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
