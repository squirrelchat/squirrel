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

import chat.squirrel.database.entities.*;
import io.vertx.core.json.JsonObject;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.types.ObjectId;
import xyz.bowser65.tokenize.IAccount;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Map;

/**
 * Server wide user account
 */
public class UserImpl extends AbstractEntity implements IAccount, IUser {
    private String username, password, email, avatar, biography, customEmail;
    private int discriminator, flags;
    private long tokensValidSince;
    private boolean emailVerified, locked, disabled, banned, deleted, mfaMobile, mfaHardware;
    private Collection<ObjectId> badgeIds;
    private Map<String, String> tokens;
    private Collection<String> ips;
    private IUserSettings userSettings;

    // Aggregated entities
    private Collection<IBadge> badges;
    private Collection<IPlatformConnection> platformConnections;

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public void setUsername(final String username) {
        this.username = username;
    }

    @Override
    public int getDiscriminator() {
        return discriminator;
    }

    @Override
    public void setDiscriminator(final int discriminator) {
        this.discriminator = discriminator;
    }

    @Override
    public int getFlags() {
        return flags;
    }

    @Override
    public void setFlags(final int flags) {
        this.flags = flags;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public void setPassword(final String password) {
        this.password = password;
    }

    @Override
    public long getTokensValidSince() {
        return tokensValidSince;
    }

    @Override
    public void setTokensValidSince(final long tokensValidSince) {
        this.tokensValidSince = tokensValidSince;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public void setEmail(final String email) {
        this.email = email;
    }

    @Override
    public String getAvatar() {
        return avatar;
    }

    @Override
    public void setAvatar(final String avatar) {
        this.avatar = avatar;
    }

    @Override
    public String getBiography() {
        return biography;
    }

    @Override
    public void setBiography(final String biography) {
        this.biography = biography;
    }

    @Override
    public String getCustomEmail() {
        return customEmail;
    }

    @Override
    public void setCustomEmail(final String customEmail) {
        this.customEmail = customEmail;
    }

    @Override
    public boolean isEmailVerified() {
        return emailVerified;
    }

    @Override
    public void setEmailVerified(final boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    @Override
    public boolean isLocked() {
        return locked;
    }

    @Override
    public void setLocked(final boolean locked) {
        this.locked = locked;
    }

    @Override
    public boolean isDisabled() {
        return disabled;
    }

    @Override
    public void setDisabled(final boolean disabled) {
        this.disabled = disabled;
    }

    @Override
    public boolean isBanned() {
        return banned;
    }

    @Override
    public void setBanned(final boolean banned) {
        this.banned = banned;
    }

    @Override
    public boolean isDeleted() {
        return deleted;
    }

    @Override
    public void setDeleted(final boolean deleted) {
        this.deleted = deleted;
    }

    @Override
    public boolean isMfaMobile() {
        return mfaMobile;
    }

    @Override
    public void setMfaMobile(final boolean mfaMobile) {
        this.mfaMobile = mfaMobile;
    }

    @Override
    public boolean isMfaHardware() {
        return mfaHardware;
    }

    @Override
    public void setMfaHardware(final boolean mfaHardware) {
        this.mfaHardware = mfaHardware;
    }

    @Override
    public Collection<ObjectId> getBadgeIds() {
        return badgeIds;
    }

    @Override
    public void setBadgeIds(final Collection<ObjectId> badgeIds) {
        this.badgeIds = badgeIds;
    }

    @Override
    public Map<String, String> getTokens() {
        return tokens;
    }

    @Override
    public void setTokens(final Map<String, String> tokens) {
        this.tokens = tokens;
    }

    @Override
    public Collection<String> getIps() {
        return ips;
    }

    @Override
    public void setIps(final Collection<String> ips) {
        this.ips = ips;
    }

    @Override
    public IUserSettings getUserSettings() {
        return userSettings;
    }

    @Override
    public void setUserSettings(final IUserSettings userSettings) {
        this.userSettings = userSettings;
    }

    // Aggregated entities
    @BsonIgnore
    @Nullable
    @Override
    public Collection<IBadge> getBadges() {
        return badges;
    }

    @Override
    public void setBadges(final Collection<IBadge> badges) {
        this.badges = badges;
    }

    @BsonIgnore
    @Nullable
    @Override
    public Collection<IPlatformConnection> getPlatformConnections() {
        return platformConnections;
    }

    @Override
    public void setPlatformConnections(final Collection<IPlatformConnection> platformConnections) {
        this.platformConnections = platformConnections;
    }

    // STUFF
    @BsonIgnore
    @Override
    public long tokensValidSince() {
        return tokensValidSince;
    }

    @BsonIgnore
    @Override
    public String getTokenId() {
        return this.id.toHexString();
    }

    @BsonIgnore
    @Override
    public boolean isInstanceAdmin() {
        return (0b1 & this.flags) != 0;
    }

    @BsonIgnore
    @Override
    public boolean isInstanceModerator() {
        return (0b10 & this.flags) != 0;
    }

    @BsonIgnore
    @Override
    public String toString() {
        return this.getUsername() + "#" + this.getDiscriminator() + " (" + this.getId().toHexString() + ")";
    }

    @BsonIgnore
    @Override
    public JsonObject toJson() {
        return super.toJson().put("bot", false);
    }
}
