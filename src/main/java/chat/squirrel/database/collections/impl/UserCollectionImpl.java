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

package chat.squirrel.database.collections.impl;

import chat.squirrel.Squirrel;
import chat.squirrel.database.collections.AbstractMongoCollection;
import chat.squirrel.database.collections.IUserCollection;
import chat.squirrel.database.entities.IUser;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import io.vertx.core.WorkerExecutor;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class UserCollectionImpl extends AbstractMongoCollection<IUser> implements IUserCollection {
    private final Random random = new Random();

    public UserCollectionImpl(final MongoCollection<IUser> collection, final WorkerExecutor worker) {
        super(collection, worker);
    }

    @Override
    public CompletionStage<IUser> findByUsernameOrEmail(String username) {
        Bson filter;
        if (username.contains("@")) {
            filter = Filters.eq("email", username);
        } else if (username.contains("#")) {
            String[] split = username.split("#");
            filter = Filters.and(Filters.eq("username", split[0]), Filters.eq("discriminator", Integer.parseInt(split[1])));
        } else {
            return CompletableFuture.completedStage(null);
        }
        return this.findEntity(Filters.and(filter, Filters.eq("deleted", false)));
    }

    @Override
    public CompletionStage<Boolean> isUsernameAvailable(String username) {
        final CompletableFuture<Boolean> future = new CompletableFuture<>();
        this.countDocuments(Filters.eq("username", username)).thenAccept(count -> {
            final int max = Squirrel.getInstance().getConfig().getMaximumDiscriminatorsPerUsername();
            future.complete(count <= (max == -1 ? 5000 : max));
        });
        return future;
    }

    @Override
    public CompletionStage<Boolean> isEmailUsed(String email) {
        final CompletableFuture<Boolean> future = new CompletableFuture<>();
        this.countDocuments(Filters.eq("email", email)).thenAccept(count -> future.complete(count != 0));
        return future;
    }

    @Override
    public CompletionStage<Integer> getFreeDiscriminator(final String username) {
        final CompletableFuture<Integer> future = new CompletableFuture<>();
        this.countDocuments(Filters.eq("username", username)).thenAccept(count -> {
            if (count >= 5000) {
                future.completeExceptionally(new IndexOutOfBoundsException());
            }

            final List<Integer> used = new ArrayList<>();
            this.findEntities(Filters.eq("username", username)).thenAccept(entities -> {
                entities.forEach(u -> used.add(u.getDiscriminator()));

                int dis = this.random.nextInt(10000);
                while (used.indexOf(dis) != -1) {
                    dis = this.random.nextInt(10000);
                }
                future.complete(dis);
            });
        });
        return future;
    }
}
