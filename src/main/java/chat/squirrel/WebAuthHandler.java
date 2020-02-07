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

package chat.squirrel;

import chat.squirrel.core.DatabaseManager.SquirrelCollection;
import chat.squirrel.entities.User;
import com.mongodb.client.model.Filters;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import org.bson.types.ObjectId;
import xyz.bowser65.tokenize.IAccount;
import xyz.bowser65.tokenize.Token;

/**
 * Vert.x handler to handle sessions before continuing down a chain
 */
public class WebAuthHandler implements Handler<RoutingContext> {
    public static final String SQUIRREL_SESSION_KEY = "chat.squirrel.user", SQUIRREL_TOKEN_KEY = "chat.squirrel.token";

    @Override
    public void handle(RoutingContext event) {
        final String stringToken = event.request().getHeader("Authorization");
        if (stringToken == null) {
            event.fail(401);
            return;
        }

        final Token token;

        try {
            token = Squirrel.getInstance().getTokenize().validateToken(stringToken, this::fetchAccount);
        } catch (Exception e) {
            e.printStackTrace();
            event.fail(403);
            return;
        }

        if (token == null) {
            event.fail(401);
            return;
        }

        if (token.getPrefix() != null) {
            event.fail(401);
            return;
        }

        final User user = (User) token.getAccount();

        if (user.isBanned()) {
            event.fail(403);
            return;
        }

//        final long timeout = Squirrel.getInstance().getConfig().getSessionTimeout();
//
//        if (timeout != -1 && Tokenize.hasTokenExpired(token, timeout)) {
//            event.fail(401);
//            return;
//        }

//        user.setTokenValidSince(Tokenize.currentTokenTime());
        event.put(SQUIRREL_TOKEN_KEY, token);
        event.put(SQUIRREL_SESSION_KEY, user);

        event.next();
    }

    private User fetchAccount(String id) {
        return Squirrel.getInstance().getDatabaseManager().findFirstEntity(User.class, SquirrelCollection.USERS,
                Filters.eq(new ObjectId(id)));
    }

}
