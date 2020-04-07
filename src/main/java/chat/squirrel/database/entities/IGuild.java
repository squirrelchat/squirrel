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

import chat.squirrel.database.entities.channels.IChannel;
import chat.squirrel.database.entities.impl.GuildImpl;
import chat.squirrel.database.entities.presences.IPresence;
import org.bson.types.ObjectId;

import javax.annotation.Nullable;
import java.util.Collection;


public interface IGuild extends IEntity {
    static IGuild create() {
        return new GuildImpl();
    }

    String getName();

    void setName(String name);

    String getIcon();

    void setIcon(String icon);

    ObjectId getOwnerId();

    void setOwnerId(ObjectId ownerId);

    String getRegion();

    void setRegion(String region);

    // Aggregated entities

    /**
     * Populated only if the entity was fetched performing an aggregation.
     *
     * @return Members, or {@code null} if not aggregated during entity retrieval.
     */
    @Nullable
    Collection<IMember> getMembers();

    void setMembers(Collection<IMember> members);

    /**
     * Populated only if the entity was fetched performing an aggregation.
     *
     * @return Channels, or {@code null} if not aggregated during entity retrieval.
     */
    @Nullable
    Collection<IChannel> getChannels();

    void setChannels(Collection<IChannel> channels);

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
     *
     * @return Presences, or {@code null} if not aggregated during entity retrieval.
     */
    @Nullable
    Collection<IPresence> getPresences();

    void setPresences(Collection<IPresence> presences);

    /**
     * Populated only if the entity was fetched performing an aggregation.
     *
     * @return Voice states, or {@code null} if not aggregated during entity retrieval.
     */
    @Nullable
    Collection<IVoiceState> getVoiceStates();

    void setVoiceStates(Collection<IVoiceState> voiceStates);
}
