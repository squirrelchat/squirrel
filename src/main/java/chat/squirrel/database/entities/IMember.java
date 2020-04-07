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

import chat.squirrel.database.entities.impl.MemberImpl;
import org.bson.types.ObjectId;

import javax.annotation.Nullable;
import java.util.Collection;

public interface IMember extends IEntity {
    static IMember create() {
        return new MemberImpl();
    }

    ObjectId getUserId();

    void setUserId(ObjectId userId);

    ObjectId getGuildId();

    void setGuildId(ObjectId guildId);

    String getNickname();

    void setNickname(String nickname);

    /**
     * Populated only if the entity was fetched performing an aggregation.
     *
     * @return Associated user, or {@code null} if not aggregated during entity retrieval.
     */
    @Nullable
    IUser getUser();

    void setUser(IUser user);

    /**
     * Populated only if the entity was fetched performing an aggregation.
     *
     * @return Roles, or {@code null} if not aggregated during entity retrieval.
     */
    @Nullable
    Collection<IRole> getRoles();

    void setRoles(Collection<IRole> roles);

    /**
     * Populated only if the entity was fetched performing an aggregation.
     * Can safely be considered non-null if roles were fetched.
     *
     * @return Permissions, or {@code null} if not aggregated during entity retrieval.
     */
    @Nullable
    Collection<String> getPermissions();

    void setPermissions(Collection<String> permissions);
}
