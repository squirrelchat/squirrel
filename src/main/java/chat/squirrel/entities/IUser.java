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
package chat.squirrel.entities;

import chat.squirrel.entities.impl.UserImpl;
import chat.squirrel.upload.Asset;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.types.ObjectId;
import xyz.bowser65.tokenize.IAccount;

import java.util.Collection;

public interface IUser extends IEntity, IAccount {
    static IUser create() {
        return new UserImpl();
    }

    String getCustomEmail();

    Asset getAvatar();

    int getDiscriminator();

    String getEmail();

    int getFlags();

    Collection<String> getIps();

    String getUsername();

    boolean hasMfa();

    boolean isBanned();

    boolean isDeleted();

    boolean isDisabled();

    @BsonIgnore
    boolean isInstanceAdmin();

    @BsonIgnore
    boolean isInstanceModerator();

    void setBanned(boolean banned);

    void setCustomEmail(String customEmail);

    void setDeleted(boolean deleted);

    void setDisabled(boolean disabled);

    void setDiscriminator(int discriminator);

    void setEmail(String email);

    void setFlags(int flags);

    void setIps(Collection<String> ips);

    void setTokenValidSince(long tokenValidSince);

    void setUsername(String username);

    void setAvatar(Asset id);

    String getBio();

    void setBio(String bio);

    Collection<ObjectId> getBadges();

    void setBadges(Collection<ObjectId> badges);

    default Class<? extends IEntity> getImplementing() {
        return UserImpl.class;
    }
}
