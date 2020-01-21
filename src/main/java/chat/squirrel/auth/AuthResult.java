package chat.squirrel.auth;

import org.bson.types.ObjectId;

import chat.squirrel.Squirrel;

public class AuthResult {
    private String username;
    private ObjectId userId;
    private int discriminator;
    /**
     * I don't agree with this but apparently modern UX doesn't care about security
     */
    private FailureReason reason = FailureReason.UNKNOWN;

    public FailureReason getReason() {
        return reason;
    }

    /**
     * Set to null for success
     */
    public void setReason(FailureReason reason) {
        this.reason = reason;
    }

    public boolean isSuccess() {
        return reason == null;
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

    public int getDiscriminator() {
        return discriminator;
    }

    public void setDiscriminator(int discriminator) {
        this.discriminator = discriminator;
    }

    @Override
    public String toString() {
        return (isSuccess() ? "Success" : "Failure") + " " + getUsername() + "#"
                + Squirrel.formatDiscriminator(getDiscriminator())
                + (isSuccess() ? "" : (": " + getReason().toString()));
    }

    public enum FailureReason {
        UNKNOWN,
        /**
         * Only for registration
         */
        INVALID_EMAIL,
        /**
         * For registration and login
         */
        INVALID_USERNAME,
        INVALID_PASSWORD,
        REGISTRATION_DISABLED,
        /**
         * If the limit for a username has been reached or if there are no free
         * discriminators
         * Registration only
         */
        OVERUSED_USERNAME,
        DISABLED_ACCOUNT,
        BANNED_ACCOUNT,
        DELETION_SCHEDULED;
    }

}
