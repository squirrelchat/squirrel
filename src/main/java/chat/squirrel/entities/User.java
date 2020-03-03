package chat.squirrel.entities;

import java.util.Collection;

import chat.squirrel.entities.impl.UserImpl;
import xyz.bowser65.tokenize.IAccount;

@Implementation(implCls = UserImpl.class)
public interface User extends IEntity, IAccount {
    public static User create() {
        return new UserImpl();
    }

    String getCustomEmail();

    int getDiscriminator();

    String getEmail();

    int getFlags();

    Collection<String> getIps();

    String getUsername();

    boolean hasMfa();

    boolean isBanned();

    boolean isDeleted();

    boolean isDisabled();

    boolean isInstanceAdmin();

    boolean isInstanceModerator();

    void setBanned(boolean banned);

    void setCustomEmail(String customEmail);

    void setDeleted(boolean deleted);

    void setDisabled(boolean disabled);

    void setDiscriminator(int discriminator);

    void setEmail(String email);

    void setFlags(int flags);

    void setIps(Collection<String> ips);

    void setTokenValidSince(long tokenValidSince);

    void setUsername(String username);

    String getBio();

    void setBio(String bio);

    default Class<? extends IEntity> getImplementing() {
        return UserImpl.class;
    }
}
