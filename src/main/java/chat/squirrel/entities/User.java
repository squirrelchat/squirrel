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

import java.util.Collection;

import org.bson.codecs.pojo.annotations.BsonIgnore;

import xyz.bowser65.tokenize.IAccount;

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

    public String getCustomEmail() {
        return this.customEmail;
    }

    public int getDiscriminator() {
        return this.discriminator;
    }

    public String getEmail() {
        return this.email;
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
     * <td>TBD</td>
     * <td>TBD</td>
     * <td>TBD</td>
     * <td>TBD</td>
     * <td>Instance Moderator</td>
     * <td>Instance Admin</td>
     * </tr>
     * </table>
     *
     * @return The flags integer of this user
     */
    public int getFlags() {
        return this.flags;
    }

    public Collection<String> getIps() {
        return this.ips;
    }

    @Override
    public String getTokenId() {
        return this.getId().toHexString();
    }

    public String getUsername() {
        return this.username;
    }

    public boolean hasMfa() {
        return this.mfa;
    }

    public boolean isBanned() {
        return this.banned;
    }

    public boolean isBugHunter() {
        return (0b100000 & this.flags) != 0;
    }

    public boolean isContributor() {
        return (0b1000 & this.flags) != 0;
    }

    public boolean isDeleted() {
        return this.deleted;
    }

    public boolean isDisabled() {
        return this.disabled;
    }

    // Flags
    public boolean isInstanceAdmin() {
        return (0b1 & this.flags) != 0;
    }

    public boolean isInstanceModerator() {
        return (0b10 & this.flags) != 0;
    }

    public boolean isSquirrelDeveloper() {
        return (0b100 & this.flags) != 0;
    }

    public boolean isTranslator() {
        return (0b10000 & this.flags) != 0;
    }

    public void setBanned(final boolean banned) {
        this.banned = banned;
    }

    public void setCustomEmail(final String customEmail) {
        this.customEmail = customEmail;
    }

    public void setDeleted(final boolean deleted) {
        this.deleted = deleted;
    }

    public void setDisabled(final boolean disabled) {
        this.disabled = disabled;
    }

    public void setDiscriminator(final int discriminator) {
        this.discriminator = discriminator;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public void setFlags(final int flags) {
        this.flags = flags;
    }

    public void setIps(final Collection<String> ips) {
        this.ips = ips;
    }

    public void setTokenValidSince(final long tokenValidSince) {
        this.tokenValidSince = tokenValidSince;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    @Override
    @BsonIgnore
    public long tokensValidSince() {
        return this.tokenValidSince;
    }

    @Override
    public String toString() {
        return this.getUsername() + "#" + this.getDiscriminator() + " (" + this.getId().toHexString() + ")";
    }
}
