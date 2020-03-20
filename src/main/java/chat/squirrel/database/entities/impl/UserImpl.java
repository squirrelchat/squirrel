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
import chat.squirrel.database.entities.IUser;
import chat.squirrel.upload.Asset;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.types.ObjectId;
import xyz.bowser65.tokenize.IAccount;

import java.util.Collection;
import java.util.Collections;

/**
 * Server wide user account
 */
public class UserImpl extends AbstractEntity implements IAccount, IUser {
    private String username, email, customEmail, bio;
    private int discriminator, flags;
    private boolean disabled, banned, deleted, mfa;
    @BsonIgnore
    private long tokenValidSince;
    private Collection<String> ips = Collections.emptySet();
    private Collection<ObjectId> badges = Collections.emptySet();
    private Asset avatar;

    @Override
    public String getCustomEmail() {
        return this.customEmail;
    }

    @Override
    public int getDiscriminator() {
        return this.discriminator;
    }

    @Override
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
    @Override
    public int getFlags() {
        return this.flags;
    }

    @Override
    public Collection<String> getIps() {
        return this.ips;
    }

    @Override
    public String getTokenId() {
        return this.getId().toHexString();
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean hasMfa() {
        return this.mfa;
    }

    @Override
    public boolean isBanned() {
        return this.banned;
    }

    @Override
    public boolean isDeleted() {
        return this.deleted;
    }

    @Override
    public boolean isDisabled() {
        return this.disabled;
    }

    // Flags
    @Override
    @BsonIgnore
    public boolean isInstanceAdmin() {
        return (0b1 & this.flags) != 0;
    }

    @Override
    @BsonIgnore
    public boolean isInstanceModerator() {
        return (0b10 & this.flags) != 0;
    }

    @Override
    public void setBanned(final boolean banned) {
        this.banned = banned;
    }

    @Override
    public void setCustomEmail(final String customEmail) {
        this.customEmail = customEmail;
    }

    @Override
    public void setDeleted(final boolean deleted) {
        this.deleted = deleted;
    }

    @Override
    public void setDisabled(final boolean disabled) {
        this.disabled = disabled;
    }

    @Override
    public void setDiscriminator(final int discriminator) {
        this.discriminator = discriminator;
    }

    @Override
    public void setEmail(final String email) {
        this.email = email;
    }

    @Override
    public void setFlags(final int flags) {
        this.flags = flags;
    }

    @Override
    public void setIps(final Collection<String> ips) {
        this.ips = ips;
    }

    @Override
    public void setTokenValidSince(final long tokenValidSince) {
        this.tokenValidSince = tokenValidSince;
    }

    @Override
    public void setUsername(final String username) {
        this.username = username;
    }

    @Override
    public String getBio() {
        return this.bio;
    }

    @Override
    public void setBio(final String bio) {
        this.bio = bio;
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

    @Override
    public Asset getAvatar() {
        return avatar;
    }

    @Override
    public void setAvatar(Asset id) {
        this.avatar = id;
    }

    @Override
    public Collection<ObjectId> getBadges() {
        return badges;
    }

    @Override
    public void setBadges(Collection<ObjectId> badges) {
        this.badges = badges;
    }

}
