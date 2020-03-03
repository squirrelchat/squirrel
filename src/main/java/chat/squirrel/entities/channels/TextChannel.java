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

package chat.squirrel.entities.channels;

import java.util.Collection;
import java.util.concurrent.Future;

import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.types.ObjectId;

import chat.squirrel.entities.AbstractEntity;
import chat.squirrel.entities.IMessage;

public class TextChannel extends AbstractEntity implements IChannel {
    private String name;
    private ObjectId guildId;

    public ObjectId getGuildId() {
        return this.guildId;
    }

    @BsonIgnore
    public Collection<IMessage> getMessages(final int nbr) { // TODO
        return null;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Future<Collection<ObjectId>> getParticipants() {
        // TODO Auto-generated method stub
        return null;
    }

    public void setGuildId(final ObjectId guildId) {
        this.guildId = guildId;
    }

    @Override
    public void setName(final String name) {
        this.name = name;
    }
    
}
