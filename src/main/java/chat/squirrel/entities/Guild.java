package chat.squirrel.entities;

import java.util.Collection;

public class Guild extends AbstractEntity {
    private String name;
    private Collection<Member> members;
    private Collection<Role> roles;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Collection<Member> getMembers() {
        return members;
    }

    public void setMembers(Collection<Member> members) {
        this.members = members;
    }

    public Collection<Role> getRoles() {
        return roles;
    }

    public void setRoles(Collection<Role> roles) {
        this.roles = roles;
    }

    public enum Permissions {
        CHANGE_NICKNAME, BAN, KICK, ROLE_IMMUNITY, MANAGE_CHANNELS
    }
}
