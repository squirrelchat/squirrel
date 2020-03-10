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

package chat.squirrel.modules.users;

import org.bson.types.ObjectId;

import com.mongodb.client.model.Filters;

import chat.squirrel.Squirrel;
import chat.squirrel.core.DatabaseManager.SquirrelCollection;
import chat.squirrel.entities.IUser;
import chat.squirrel.entities.UserSettings;
import chat.squirrel.modules.AbstractModule;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

public class ModuleSelf extends AbstractModule {
    @Override
    public void initialize() {
        this.registerAuthedRoute(HttpMethod.GET, "/users/self", this::handleMe);
        this.registerAuthedRoute(HttpMethod.PATCH, "/users/self", this::notImplemented);
        // { disable: true } as payload to just disable the account
        this.registerPasswordConfirmRoute(HttpMethod.DELETE, "/users/self", this::notImplemented);
        this.registerFileUploadRoute(HttpMethod.POST, "/users/self/avatar", this::notImplemented);
    }

    private void handleMe(final RoutingContext ctx) {
        final IUser user = this.getRequester(ctx);
        final UserSettings sets = this.fetchUserSettings(user.getId());
        final JsonObject obj = new JsonObject();

        if (sets != null) {
            obj.put("locale", sets.getLanguage());
        }

        ctx.response()
                .end(obj.put("id", user.getId().toHexString())
                        .put("username", user.getUsername())
                        .put("discriminator", user.getDiscriminator())
                        .put("avatar", JsonObject.mapFrom(user.getAvatar()))
                        .put("bot", false)
                        .put("email", user.getEmail())
                        .put("custom_email", user.getCustomEmail())
                        .put("verified", true)
                        .put("mfa_enabled", false)
                        .put("flags", user.getFlags())
                        .encode());
    }

    private UserSettings fetchUserSettings(ObjectId userId) {
        return Squirrel.getInstance()
                .getDatabaseManager()
                .findFirstEntity(UserSettings.class, SquirrelCollection.USER_SETTINGS, Filters.eq(userId));
    }
}
