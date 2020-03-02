package chat.squirrel.entities;

import java.util.Collection;
import java.util.concurrent.Future;

import org.bson.types.ObjectId;

import chat.squirrel.entities.channels.IChannel;
import chat.squirrel.entities.impl.GuildImpl;

public interface Guild extends IEntity {
    public static Guild create() {
        return new GuildImpl();
    }

    Collection<ObjectId> getChannels();

    Member getMemberForUser(ObjectId user);

    void addMember(Member m);

    Future<Collection<Member>> getRealMembers();

    Collection<ObjectId> getMembers();

    String getName();

    Future<Collection<IChannel>> getRealChannels();

    Future<Collection<Role>> getRealRoles();

    Collection<ObjectId> getRoles();

    void setChannels(Collection<ObjectId> channels);

    void setMembers(Collection<ObjectId> members);

    void setName(String name);

    void setRoles(Collection<ObjectId> roles);

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
