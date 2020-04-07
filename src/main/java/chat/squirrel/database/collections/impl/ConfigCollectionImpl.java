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
import chat.squirrel.database.collections.IConfigCollection;
import chat.squirrel.database.entities.config.IConfig;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.result.UpdateResult;
import io.vertx.core.Promise;
import io.vertx.core.WorkerExecutor;
import org.bson.BsonDocument;
import org.bson.BsonDocumentWrapper;
import org.bson.BsonString;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class ConfigCollectionImpl extends AbstractMongoCollection<IConfig> implements IConfigCollection {
    public ConfigCollectionImpl(final MongoCollection<IConfig> collection, final WorkerExecutor worker) {
        super(collection, worker);
    }

    @Override
    public <T extends IConfig> CompletionStage<T> findConfig(final Class<T> clazz) {
        final CompletableFuture<T> future = new CompletableFuture<>();
        this.getWorker().executeBlocking(
                (Promise<T> p) -> p.complete(getRawCollection().find(Filters.eq("_class", clazz.getCanonicalName()), clazz).first()),
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
    public CompletionStage<UpdateResult> saveConfig(final IConfig config) {
        final CompletableFuture<UpdateResult> future = new CompletableFuture<>();
        this.getWorker().executeBlocking(
                (Promise<UpdateResult> p) -> {
                    String clazz = config.getClass().getCanonicalName();
                    for (final Class<?> interfaceCls : config.getClass().getInterfaces()) {
                        if (interfaceCls != IConfig.class && interfaceCls.isAssignableFrom(IConfig.class)) {
                            clazz = config.getClass().getCanonicalName();
                            break;
                        }
                    }
                    final UpdateResult result = getRawCollection().withDocumentClass(BsonDocument.class)
                            .replaceOne(
                                    Filters.eq("_class", config.getClass().getCanonicalName()),
                                    BsonDocumentWrapper
                                            .asBsonDocument(config, getRawCollection().getCodecRegistry())
                                            .append("_class", new BsonString(clazz)),
                                    new ReplaceOptions().upsert(true)
                            );
                    p.complete(result);
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
}
