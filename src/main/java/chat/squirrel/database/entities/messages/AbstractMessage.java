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

package chat.squirrel.database.entities.messages;

import chat.squirrel.database.entities.AbstractEntity;
import chat.squirrel.database.entities.IPartialUser;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.types.ObjectId;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Date;

public abstract class AbstractMessage extends AbstractEntity implements IMessage {
    private ObjectId authorId, channelId;
    private Date editedTimestamp;
    private Collection<IAttachment> attachments;

    // Aggregated entities
    private IPartialUser author = null;

    @Override
    public ObjectId getAuthorId() {
        return authorId;
    }

    @Override
    public void setAuthorId(final ObjectId authorId) {
        this.authorId = authorId;
    }

    @Override
    public ObjectId getChannelId() {
        return channelId;
    }

    @Override
    public void setChannelId(final ObjectId channelId) {
        this.channelId = channelId;
    }

    @Nullable
    @Override
    public Date getEditedTimestamp() {
        return editedTimestamp;
    }

    @Override
    public void setEditedTimestamp(final Date editedTimestamp) {
        this.editedTimestamp = editedTimestamp;
    }

    @Override
    public Collection<IAttachment> getAttachments() {
        return attachments;
    }

    @Override
    public void setAttachments(final Collection<IAttachment> attachments) {
        this.attachments = attachments;
    }


    // Aggregated entities
    @BsonIgnore
    @Nullable
    @Override
    public IPartialUser getAuthor() {
        return author;
    }

    @Override
    public void setAuthor(final IPartialUser author) {
        this.author = author;
    }
}
