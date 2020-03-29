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

import chat.squirrel.database.entities.IEntity;
import chat.squirrel.database.entities.IPartialUser;
import chat.squirrel.database.entities.channels.IChannel;
import org.bson.types.ObjectId;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Date;

/**
 * A general message in a {@link IChannel}
 */
public interface IMessage extends IEntity {
    /**
     * Author of the message
     *
     * @return ID of the author of the message
     */
    ObjectId getAuthorId();

    void setAuthorId(ObjectId author);

    ObjectId getChannelId();

    void setChannelId(ObjectId channel);

    /**
     * @return The edit date, or null if the message never got edited.
     */
    @Nullable
    Date getEditedTimestamp();

    void setEditedTimestamp(Date editedTimestamp);

    Collection<IAttachment> getAttachments();

    void setAttachments(Collection<IAttachment> attachments);

    // Aggregated entities

    /**
     * Populated only if the entity was fetched performing an aggregation.
     *
     * @return Members, or {@code null} if not aggregated during entity retrieval.
     */
    @Nullable
    IPartialUser getAuthor();

    void setAuthor(IPartialUser author);
}