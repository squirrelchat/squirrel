package chat.squirrel.auth;

import chat.squirrel.entities.User;

public class AuthResult {
    private User user;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return (isSuccess() ? "Success" : "Failure") + " " + user
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
         * discriminators Registration only
         */
        OVERUSED_USERNAME,
        DISABLED_ACCOUNT,
        BANNED_ACCOUNT,
        DELETION_SCHEDULED;
    }

}
