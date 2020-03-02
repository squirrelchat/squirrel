package chat.squirrel.entities;

import java.util.Collection;
import java.util.concurrent.Future;

import org.bson.types.ObjectId;

import chat.squirrel.entities.Guild.Permissions;
import chat.squirrel.entities.impl.MemberImpl;

public interface Member extends IEntity {
    public static Member create() {
        return new MemberImpl();
    }

    Future<Guild> getGuild();

    ObjectId getGuildId();

    String getNickname();

    Collection<Permissions> getPermissions();

    Future<Collection<Role>> getRoles();

    Collection<ObjectId> getRolesIds();

    Future<User> getUser();

    ObjectId getUserId();

    boolean hasEffectivePermission(Permissions perm);

    boolean isOwner();

    void setGuildId(ObjectId guildId);

    void setNickname(String nickname);

    void setOwner(boolean owner);

    void setPermissions(Collection<Permissions> permissions);

    void setRolesIds(Collection<ObjectId> roles);

    void setUserId(ObjectId userId);
}
