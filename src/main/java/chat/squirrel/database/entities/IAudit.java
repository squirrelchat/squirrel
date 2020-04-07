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

import chat.squirrel.database.entities.impl.AuditImpl;
import org.bson.types.ObjectId;

import javax.annotation.Nullable;
import java.util.Map;

public interface IAudit extends IEntity {
    static IAudit create() {
        return new AuditImpl();
    }

    AuditLogEntryType getType();

    void setType(AuditLogEntryType type);

    /**
     * @return The custom audit log type. Always {@code null} for non-CUSTOM audits.
     */
    @Nullable
    String getCustomType();

    void setCustomType(String customType);

    /**
     * @return ID this audit belongs to. {@code null} for instance audits.
     */
    @Nullable
    ObjectId getGuild();

    void setGuild(@Nullable ObjectId guild);

    ObjectId getUser();

    void setUser(ObjectId user);

    Map<String, Object> getData();

    void setData(Map<String, Object> data);

    /**
     * Type of entry an audit is
     * TODO: Keep it as an enum?
     * TODO: Instance audit types
     */
    enum AuditLogEntryType {
        // Guild
        GUILD_UPDATE,
        // Channels
        CHANNEL_CREATE,
        CHANNEL_UPDATE,
        CHANNEL_DELETE,
        // Permission overrides
        OVERRIDE_CREATE,
        OVERRIDE_UPDATE,
        OVERRIDE_DELETE,
        // Integrations
        INTEGRATION_CREATE,
        INTEGRATION_UPDATE,
        INTEGRATION_DELETE,
        BOT_ADD,
        // Webhooks
        WEBHOOK_CREATE,
        WEBHOOK_UPDATE,
        WEBHOOK_DELETE,
        // Invites
        INVITE_CREATE,
        INVITE_DELETE,
        // Emojis
        EMOJI_CREATE,
        EMOJI_UPDATE,
        EMOJI_DELETE,
        // Members
        MEMBER_UPDATE,
        MEMBER_ROLE_UPDATE,
        MEMBER_MUTE,
        MEMBER_KICK,
        MEMBER_TEMP_BAN,
        MEMBER_BAN,
        MEMBER_UNMUTE,
        MEMBER_UNBAN,
        // Messages
        MESSAGE_DELETE,
        MESSAGE_BULK_DELETE,
        MESSAGE_PIN,
        MESSGE_UNPIN,
        // "Rich" messages
        MESSAGE_POLL_RESET,
        MESSAGE_POLL_STOP,

        // For plugins
        CUSTOM
    }
}
