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
import chat.squirrel.database.entities.IAuditLogEntry;
import org.bson.types.ObjectId;

import java.util.Date;

/**
 * A guild audit log entry
 */
public class AuditLogEntryImpl extends AbstractEntity implements IAuditLogEntry {
    private ObjectId guild, user;
    private Date date;
    private AuditLogEntryType type;

    @Override
    public AuditLogEntryType getType() {
        return type;
    }

    @Override
    public void setType(AuditLogEntryType type) {
        this.type = type;
    }

    @Override
    public ObjectId getGuild() {
        return guild;
    }

    @Override
    public void setGuild(ObjectId guild) {
        this.guild = guild;
    }

    @Override
    public ObjectId getUser() {
        return user;
    }

    @Override
    public void setUser(ObjectId user) {
        this.user = user;
    }

    @Override
    public Date getDate() {
        return date;
    }

    @Override
    public void setDate(Date date) {
        this.date = date;
    }
}
