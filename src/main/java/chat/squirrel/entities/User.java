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

package chat.squirrel.entities;

import org.bson.codecs.pojo.annotations.BsonIgnore;
import xyz.bowser65.tokenize.IAccount;

import java.util.Collection;

/**
 * Server wide user account
 */
public class User extends AbstractEntity implements IAccount {
    private String username, email, customEmail;
    private int discriminator, flags;
    private boolean disabled, banned, deleted, mfa;
    @BsonIgnore
    private long tokenValidSince;
    private Collection<String> ips;

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

    /**
     * <table summary="">
     * <tr>
     * <td>Bit 6</td>
     * <td>Bit 5</td>
     * <td>Bit 4</td>
     * <td>Bit 3</td>
     * <td>Bit 2</td>
     * <td>Bit 1</td>
     * <td>Bit 0</td>
     * </tr>
     * <tr>
     * <td>TBD</td>
     * <td>Bug Hunter</td>
     * <td>Translator</td>
     * <td>Contributor</td>
     * <td>Squirrel Developer</td>
     * <td>Instance Moderator</td>
     * <td>Instance Admin</td>
     * </tr>
     * </table>
     *
     * @return The flags integer of this user
     */
    public int getFlags() {
        return flags;
    }

    public void setFlags(int flags) {
        this.flags = flags;
    }

    // Flags
    public boolean isInstanceAdmin() {
        return (0b1 & flags) != 0;
    }

    public boolean isInstanceModerator() {
        return (0b10 & flags) != 0;
    }

    public boolean isSquirrelDeveloper() {
        return (0b100 & flags) != 0;
    }

    public boolean isContributor() {
        return (0b1000 & flags) != 0;
    }

    public boolean isTranslator() {
        return (0b10000 & flags) != 0;
    }

    public boolean isBugHunter() {
        return (0b100000 & flags) != 0;
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

    public int getDiscriminator() {
        return discriminator;
    }

    public void setDiscriminator(int discriminator) {
        this.discriminator = discriminator;
    }

    @Override
    public String toString() {
        return getUsername() + "#" + getDiscriminator() + " (" + getId().toHexString() + ")";
    }

    public boolean hasMfa() {
        return mfa;
    }

    @Override
    @BsonIgnore
    public long tokensValidSince() {
        return tokenValidSince;
    }

    public void setTokenValidSince(long tokenValidSince) {
        this.tokenValidSince = tokenValidSince;
    }

    @Override
    public String getTokenId() {
        return getId().toHexString();
    }
}
