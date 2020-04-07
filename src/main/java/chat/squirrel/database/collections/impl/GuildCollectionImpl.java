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

import chat.squirrel.database.collections.AbstractMongoCollection;
import chat.squirrel.database.collections.IGuildCollection;
import chat.squirrel.database.entities.IGuild;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Field;
import io.vertx.core.Promise;
import io.vertx.core.WorkerExecutor;
import org.bson.conversions.Bson;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static com.mongodb.client.model.Accumulators.first;
import static com.mongodb.client.model.Accumulators.push;
import static com.mongodb.client.model.Aggregates.*;

public class GuildCollectionImpl extends AbstractMongoCollection<IGuild> implements IGuildCollection {
    public GuildCollectionImpl(final MongoCollection<IGuild> collection, final WorkerExecutor worker) {
        super(collection, worker);
    }

    @Override
    public CompletionStage<IGuild> findGuildAggregated(Bson filters) {
        final CompletableFuture<IGuild> future = new CompletableFuture<>();
        this.getWorker().executeBlocking(
                (Promise<IGuild> p) -> {
                    // Thanks mongodb-compass for existing. Seriously.
                    final List<Bson> aggregation = Arrays.asList(
                            lookup("members", "_id", "guildId", "members"),
                            unwind("$members"),
                            lookup("users", "members.userId", "_id", "members.user"),
                            unwind("$members.user"),
                            group("$_id", first("_root", "$$ROOT"), push("members", "$members")),
                            addFields(new Field<>("_root.members", "$members")),
                            replaceRoot("$_root"),
                            lookup("channels", "_id", "guildId", "channels"),
                            lookup("roles", "_id", "guildId", "roles")
                    );
                    p.complete(getRawCollection().aggregate(aggregation).first());
                },
                r -> {
                    if (r.failed()) {
                        future.completeExceptionally(r.cause());
                    } else {
                        future.complete(r.result());
                    }
                });
        return future;
    }

    @Override
    public CompletionStage<Void> findMembersAndPresences(Bson filters) {
        return null; // TODO
    }
}
