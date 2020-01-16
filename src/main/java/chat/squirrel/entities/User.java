package chat.squirrel.entities;

/**
 * Server wide user account
 */
public class User extends AbstractEntity {
    private String username, email;
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

    public void setEmail(String email) {
        this.email = email;
    }

    public ServerRole getServerRole() {
        return serverRole;
    }

    public void setServerRole(ServerRole role) {
        this.serverRole = role;
    }

    public enum ServerRole {
        USER, MOD, ADMIN
    }
}
