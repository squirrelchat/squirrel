/*
 * Copyright (c) 2020-present Bowser65 & vinceh121, All rights reserved.
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

package chat.squirrel;

import java.security.SecureRandom;

public class SquirrelConfig extends UserConfig {
    private String orgName = "Squirrel Chat", srvDescription = "Default Squirrel Server", secret;
    private int maximumUsernameCount = -1;
    private long sessionTimeout = -1;
    private boolean allowRegister = true;

    public SquirrelConfig(Class<?> owner) {
        super(owner);
    }

    /**
     * -1 for standard maximum (5000). Cannot be higher than 5000.
     */
    public int getMaximumUsernameCount() {
        return this.maximumUsernameCount;
    }

    public String getOrgName() {
        return this.orgName;
    }

    public long getSessionTimeout() {
        return this.sessionTimeout;
    }

    public String getSrvDescription() {
        return this.srvDescription;
    }

    public String getTokenSecret() {
        if (this.secret == null) {
            final SecureRandom rng = new SecureRandom();
            final byte[] raw = new byte[128];
            rng.nextBytes(raw);
            this.secret = new String(raw);
        }
        return this.secret;
    }

    public boolean isAllowRegister() {
        return this.allowRegister;
    }

    public void setAllowRegister(final boolean allowRegister) {
        this.allowRegister = allowRegister;
    }

    public void setMaximumUsernameCount(final int maximumUsernameCount) {
        this.maximumUsernameCount = maximumUsernameCount;
    }

    public void setOrgName(final String orgName) {
        this.orgName = orgName;
    }

    public void setSessionTimeout(final long sessionTimeout) {
        this.sessionTimeout = sessionTimeout;
    }

    public void setSrvDescription(final String srvDescription) {
        this.srvDescription = srvDescription;
    }

    public void setTokenSecret(final String secret) {
        this.secret = secret;
    }
}
