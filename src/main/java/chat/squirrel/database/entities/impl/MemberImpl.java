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
import chat.squirrel.database.entities.IMember;
import chat.squirrel.database.entities.IRole;
import chat.squirrel.database.entities.IUser;
import io.vertx.core.json.JsonObject;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.types.ObjectId;

import javax.annotation.Nullable;
import java.util.Collection;

public class MemberImpl extends AbstractEntity implements IMember {
    private ObjectId userId, guildId;
    private String nickname;

    // Aggregated entities
    private IUser user = null;
    private Collection<IRole> roles = null;
    private Collection<String> permissions = null;

    @Override
    public ObjectId getUserId() {
        return userId;
    }

    @Override
    public void setUserId(final ObjectId userId) {
        this.userId = userId;
    }

    @Override
    public ObjectId getGuildId() {
        return guildId;
    }

    @Override
    public void setGuildId(final ObjectId guildId) {
        this.guildId = guildId;
    }

    @Override
    public String getNickname() {
        return nickname;
    }

    @Override
    public void setNickname(final String nickname) {
        this.nickname = nickname;
    }

    // Aggregated entities
    @BsonIgnore
    @Nullable
    @Override
    public IUser getUser() {
        return user;
    }

    @Override
    public void setUser(final IUser user) {
        this.user = user;
    }

    @BsonIgnore
    @Nullable
    @Override
    public Collection<IRole> getRoles() {
        return roles;
    }

    @Override
    public void setRoles(final Collection<IRole> roles) {
        this.roles = roles;
    }

    @BsonIgnore
    @Nullable
    @Override
    public Collection<String> getPermissions() {
        return permissions;
    }

    @Override
    public void setPermissions(final Collection<String> permissions) {
        this.permissions = permissions;
    }

    @Override
    public JsonObject toJson() {
        return super.toJson(); // TODO: Filter some user fields
    }
}
