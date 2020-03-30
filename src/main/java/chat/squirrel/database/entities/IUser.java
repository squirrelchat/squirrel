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
package chat.squirrel.database.entities;

import chat.squirrel.database.entities.impl.UserImpl;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.types.ObjectId;
import xyz.bowser65.tokenize.IAccount;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Map;

public interface IUser extends IPartialUser, IAccount {
    static IUser create() {
        return new UserImpl();
    }

    String getUsername();

    void setUsername(String username);

    int getDiscriminator();

    void setDiscriminator(int discriminator);

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
    int getFlags();

    void setFlags(int flags);

    String getPassword();

    void setPassword(String password);

    long getTokensValidSince();

    void setTokensValidSince(long tokensValidSince);

    String getEmail();

    void setEmail(String email);

    String getAvatar();

    void setAvatar(String avatar);

    String getBiography();

    void setBiography(String biography);

    String getCustomEmail();

    void setCustomEmail(String customEmail);

    boolean isEmailVerified();

    void setEmailVerified(boolean emailVerified);

    boolean isLocked();

    void setLocked(boolean locked);

    boolean isDisabled();

    void setDisabled(boolean disabled);

    boolean isBanned();

    void setBanned(boolean banned);

    boolean isDeleted();

    void setDeleted(boolean deleted);

    boolean isMfaMobile();

    void setMfaMobile(boolean mfaMobile);

    boolean isMfaHardware();

    void setMfaHardware(boolean mfaHardware);

    Collection<ObjectId> getBadgeIds();

    void setBadgeIds(Collection<ObjectId> badgeIds);

    Map<String, String> getTokens();

    void setTokens(Map<String, String> tokens);

    Collection<String> getIps();

    void setIps(Collection<String> ips);

    IUserSettings getUserSettings();

    void setUserSettings(IUserSettings userSettings);

    // Aggregated entities
    @Nullable
    Collection<IBadge> getBadges();

    void setBadges(Collection<IBadge> badges);

    @Nullable
    Collection<IPlatformConnection> getPlatformConnections();

    void setPlatformConnections(Collection<IPlatformConnection> platformConnections);

    // STUFF
    @BsonIgnore
    boolean isInstanceAdmin();

    @BsonIgnore
    boolean isInstanceModerator();
}
