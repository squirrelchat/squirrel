/*
 * Copyright (c) 2020 Squirrel Chat, All rights reserved.
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

package chat.squirrel.auth;

import chat.squirrel.database.entities.IUser;

public class AuthResult {
    private IUser user;
    private FailureReason reason = FailureReason.UNKNOWN;

    private String token;

    public FailureReason getReason() {
        return this.reason;
    }

    public String getToken() {
        return this.token;
    }

    public IUser getUser() {
        return this.user;
    }

    public boolean isSuccess() {
        return this.reason == null;
    }

    /**
     * Set to null for success
     */
    public void setReason(final FailureReason reason) {
        this.reason = reason;
    }

    public void setToken(final String token) {
        this.token = token;
    }

    public void setUser(final IUser user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return (this.isSuccess() ? "Success" : "Failure") + " " + this.user
                + (this.isSuccess() ? "" : ": " + this.getReason().toString());
    }

    public enum FailureReason {
        UNKNOWN,
        /**
         * Only for registration
         */
        INVALID_EMAIL,
        EMAIL_TAKEN,
        /**
         * For registration and login
         */
        INVALID_USERNAME,
        INVALID_PASSWORD,
        REGISTRATION_DISABLED,
        /**
         * Login only
         */
        INVALID_CREDENTIALS, // If registration is disabled
        UNKNOWN_IP_ADDRESS, // IP never logged in on that account
        /**
         * If the limit for a username has been reached or if there are no free
         * discriminators
         */
        OVERUSED_USERNAME,
        DISABLED_ACCOUNT,
        BANNED_ACCOUNT,
        DELETION_SCHEDULED
    }
}
