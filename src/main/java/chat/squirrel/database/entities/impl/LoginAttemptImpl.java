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

package chat.squirrel.database.entities.impl;

import chat.squirrel.database.entities.AbstractEntity;
import chat.squirrel.database.entities.ILoginAttempt;
import org.bson.types.ObjectId;

public class LoginAttemptImpl extends AbstractEntity implements ILoginAttempt {
    private LoginAttemptResult result;
    private ObjectId userId;
    private String ipAddress, userAgent, approximateLocation;

    @Override
    public LoginAttemptResult getResult() {
        return result;
    }

    @Override
    public void setResult(final LoginAttemptResult successful) {
        this.result = successful;
    }

    @Override
    public ObjectId getUserId() {
        return userId;
    }

    @Override
    public void setUserId(final ObjectId userId) {
        this.userId = userId;
    }

    @Override
    public String getIpAddress() {
        return ipAddress;
    }

    @Override
    public void setIpAddress(final String ipAddress) {
        this.ipAddress = ipAddress;
    }

    @Override
    public String getUserAgent() {
        return userAgent;
    }

    @Override
    public void setUserAgent(final String userAgent) {
        this.userAgent = userAgent;
    }

    @Override
    public String getApproximateLocation() {
        return approximateLocation;
    }

    @Override
    public void setApproximateLocation(final String approximateLocation) {
        this.approximateLocation = approximateLocation;
    }
}
