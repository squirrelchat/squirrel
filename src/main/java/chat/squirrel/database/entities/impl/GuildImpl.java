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
import chat.squirrel.database.entities.channels.IChannel;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.types.ObjectId;

import javax.annotation.Nullable;
import java.util.Collection;

public class GuildImpl extends AbstractEntity implements IGuild {
    private String name, icon, region;
    private ObjectId ownerId;

    // Aggregated entities
    private Collection<IMember> members = null;
    private Collection<IChannel> channels = null;
    private Collection<IRole> roles = null;
    private Collection<IPresence> presences = null;
    private Collection<IVoiceState> voiceStates = null;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(final String name) {
        this.name = name;
    }

    @Override
    public String getIcon() {
        return icon;
    }

    @Override
    public void setIcon(final String icon) {
        this.icon = icon;
    }

    @Override
    public ObjectId getOwnerId() {
        return ownerId;
    }

    @Override
    public void setOwnerId(final ObjectId ownerId) {
        this.ownerId = ownerId;
    }

    @Override
    public String getRegion() {
        return region;
    }

    @Override
    public void setRegion(final String region) {
        this.region = region;
    }

    // Aggregated entities
    @BsonIgnore
    @Nullable
    @Override
    public Collection<IMember> getMembers() {
        return members;
    }

    @Override
    public void setMembers(final Collection<IMember> members) {
        this.members = members;
    }

    @BsonIgnore
    @Nullable
    @Override
    public Collection<IChannel> getChannels() {
        return channels;
    }

    @Override
    public void setChannels(final Collection<IChannel> channels) {
        this.channels = channels;
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
    public Collection<IPresence> getPresences() {
        return presences;
    }

    @Override
    public void setPresences(final Collection<IPresence> presences) {
        this.presences = presences;
    }

    @BsonIgnore
    @Nullable
    @Override
    public Collection<IVoiceState> getVoiceStates() {
        return voiceStates;
    }

    @Override
    public void setVoiceStates(final Collection<IVoiceState> voiceStates) {
        this.voiceStates = voiceStates;
    }
}
