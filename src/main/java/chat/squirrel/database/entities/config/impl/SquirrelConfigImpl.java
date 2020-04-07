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

package chat.squirrel.database.entities.config.impl;

import chat.squirrel.database.entities.config.AbstractConfig;
import chat.squirrel.database.entities.config.ISquirrelConfig;

import java.security.SecureRandom;

public class SquirrelConfigImpl extends AbstractConfig implements ISquirrelConfig {
    private String serverName = "Squirrel Chat";
    private String serverDescription = "Next-gen, open-source and enterprise-ready chat platform";
    private byte[] secret = null;
    private boolean registerEnabled = true;
    private boolean ipLogging = true;
    private int maximumDiscriminatorsPerUsername = -1;
    private long sessionTimeout = -1;

    @Override
    public String getServerName() {
        return serverName;
    }

    @Override
    public void setServerName(final String serverName) {
        this.serverName = serverName;
    }

    @Override
    public String getServerDescription() {
        return serverDescription;
    }

    @Override
    public void setServerDescription(final String serverDescription) {
        this.serverDescription = serverDescription;
    }

    @Override
    public byte[] getSecret() {
        if (this.secret == null) {
            this.regenerateSecret();
        }
        return secret;
    }

    @Override
    public void setSecret(final byte[] secret) {
        this.secret = secret;
    }

    @Override
    public boolean isRegisterEnabled() {
        return registerEnabled;
    }

    @Override
    public void setRegisterEnabled(final boolean registerEnabled) {
        this.registerEnabled = registerEnabled;
    }

    @Override
    public boolean isIpLogging() {
        return ipLogging;
    }

    @Override
    public void setIpLogging(final boolean ipLogging) {
        this.ipLogging = ipLogging;
    }

    @Override
    public int getMaximumDiscriminatorsPerUsername() {
        return maximumDiscriminatorsPerUsername;
    }

    @Override
    public void setMaximumDiscriminatorsPerUsername(final int maximumDiscriminatorsPerUsername) {
        this.maximumDiscriminatorsPerUsername = maximumDiscriminatorsPerUsername;
    }

    @Override
    public long getSessionTimeout() {
        return sessionTimeout;
    }

    @Override
    public void setSessionTimeout(final long sessionTimeout) {
        this.sessionTimeout = sessionTimeout;
    }

    @Override
    public void regenerateSecret() {
        final SecureRandom secureRandom = new SecureRandom();
        final byte[] secret = new byte[128];
        secureRandom.nextBytes(secret);
        this.secret = secret;
    }
}
