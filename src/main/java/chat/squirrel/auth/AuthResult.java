package chat.squirrel.auth;

import org.bson.types.ObjectId;

public class AuthResult {
    private boolean success = false;
    private String username;
    private ObjectId userId;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public ObjectId getUserId() {
        return userId;
    }

    public void setUserId(ObjectId userId) {
        this.userId = userId;
    }

}
