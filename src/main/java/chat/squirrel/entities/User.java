package chat.squirrel.entities;

import java.util.Collection;

/**
 * Server wide user account
 */
public class User extends AbstractEntity {
    private String username, email, customEmail;
    private int discriminator, flag;
    private boolean disabled, banned, deleted;
    private Collection<String> ips;
    private ServerRole serverRole;

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCustomEmail() {
        return customEmail;
    }

    public void setCustomEmail(String customEmail) {
        this.customEmail = customEmail;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public boolean isBanned() {
        return banned;
    }

    public void setBanned(boolean banned) {
        this.banned = banned;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public Collection<String> getIps() {
        return ips;
    }

    public void setIps(Collection<String> ips) {
        this.ips = ips;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ServerRole getServerRole() {
        return serverRole;
    }

    public void setServerRole(ServerRole role) {
        this.serverRole = role;
    }

    public int getDiscriminator() {
        return discriminator;
    }

    public void setDiscriminator(int discriminator) {
        this.discriminator = discriminator;
    }

    @Override
    public String toString() {
        return getUsername() + "#" + getDiscriminator() + "(" + getId().toHexString() + ")";
    }

    public enum ServerRole {
        USER, MOD, ADMIN
    }
}
