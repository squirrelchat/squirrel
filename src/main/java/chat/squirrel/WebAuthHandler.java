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

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

import org.bson.types.ObjectId;

import com.mongodb.client.model.Filters;

import chat.squirrel.core.DatabaseManager.SquirrelCollection;
import chat.squirrel.entities.User;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import xyz.bowser65.tokenize.IAccount;
import xyz.bowser65.tokenize.Tokenize;

/**
 * Vert.x handler to handle sessions before continuing down a chain
 */
public class WebAuthHandler implements Handler<RoutingContext> {
    public static final String SQUIRREL_SESSION_KEY = "chat.squirrel.user";

    @Override
    public void handle(RoutingContext event) {
        if (event.request().getHeader("Authorization") != null) {
            event.fail(401);
            return;
        }

        final User user;
        try {
            user = (User) Squirrel.getInstance().getTokenize()
                    .validate(event.request().getHeader("Authorization"), this::fetchAccount).get();
        } catch (InterruptedException | ExecutionException e) {
            event.fail(500);
            return;
        }

        if (user == null) {
            event.fail(401);
            return;
        }

        if (user.isBanned()) {
            event.fail(403);
            return;
        }
        
        final long timeout = Squirrel.getInstance().getConfig().getSessionTimeout();

        if (timeout != -1 && Tokenize.hasTokenExpired(event.request().getHeader("Authorization"),
                timeout)) {
            event.fail(401);
            return;
        }

        event.put(SQUIRREL_SESSION_KEY, user);

        event.next();
    }

    private CompletableFuture<IAccount> fetchAccount(final String id) {
        return CompletableFuture.supplyAsync(new Supplier<IAccount>() {

            @Override
            public IAccount get() {
                return (User) Squirrel.getInstance().getDatabaseManager().findFirstEntity(User.class,
                        SquirrelCollection.USERS, Filters.eq(new ObjectId(id)));
            }
        });
    }

}
