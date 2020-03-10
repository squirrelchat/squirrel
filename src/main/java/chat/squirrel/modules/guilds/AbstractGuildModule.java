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

package chat.squirrel.modules.guilds;

import java.util.Date;

import org.bson.types.ObjectId;

import com.mongodb.client.model.Filters;

import chat.squirrel.Squirrel;
import chat.squirrel.core.DatabaseManager;
import chat.squirrel.core.DatabaseManager.SquirrelCollection;
import chat.squirrel.entities.AuditLogEntry;
import chat.squirrel.entities.AuditLogEntry.AuditLogEntryType;
import chat.squirrel.entities.IGuild;
import chat.squirrel.entities.IMember;
import chat.squirrel.entities.IUser;
import chat.squirrel.modules.AbstractModule;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

public abstract class AbstractGuildModule extends AbstractModule {
    protected IGuild getGuild(final RoutingContext ctx, final IUser user, final IGuild.Permissions permission) {
        final IGuild guild = Squirrel.getInstance()
                .getDatabaseManager()
                .findFirstEntity(IGuild.class, DatabaseManager.SquirrelCollection.GUILDS,
                        Filters.eq(new ObjectId(ctx.pathParam("id"))));

        if (guild == null) {
            this.fail(ctx, 404, "Guild not found", null);
            return null;
        }

        final IMember member = guild.getMemberForUser(user.getId());
        if (member == null || permission != null && !member.hasEffectivePermission(permission)) {
            ctx.response().setStatusCode(403).end(new JsonObject().put("message", "Missing Permissions").encode());
            return null;
        }

        // @todo: MFA requirement
        return guild;
    }

    protected void submitAudit(final ObjectId guild, final ObjectId user, final AuditLogEntryType type) {
        this.submitAudit(guild, user, type, new Date());
    }

    protected void submitAudit(final ObjectId guild, final ObjectId user, final AuditLogEntryType type,
            final Date date) {
        final AuditLogEntry entry = new AuditLogEntry();
        entry.setGuild(guild);
        entry.setUser(user);
        entry.setType(type);
        entry.setDate(date);
        submitAudit(entry);
    }

    protected void submitAudit(final AuditLogEntry entry) {
        Squirrel.getInstance().getDatabaseManager().insertEntity(SquirrelCollection.AUDITS, entry);
    }
}
