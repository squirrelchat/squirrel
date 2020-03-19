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
import java.util.concurrent.Future;

import org.bson.types.ObjectId;

import chat.squirrel.entities.IGuild.Permissions;
import chat.squirrel.entities.impl.MemberImpl;


public interface IMember extends IEntity {
    static IMember create() {
        return new MemberImpl();
    }

    Future<IGuild> getGuild();

    ObjectId getGuildId();

    String getNickname();

    Collection<Permissions> getPermissions();

    Future<Collection<IRole>> getRoles();

    Collection<ObjectId> getRolesIds();

    Future<IUser> getUser();

    ObjectId getUserId();

    boolean hasEffectivePermission(Permissions perm);

    boolean isOwner();

    void setGuildId(ObjectId guildId);

    void setNickname(String nickname);

    void setOwner(boolean owner);

    void setPermissions(Collection<Permissions> permissions);

    void setRolesIds(Collection<ObjectId> roles);

    void setUserId(ObjectId userId);

}
