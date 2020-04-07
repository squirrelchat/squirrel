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

package chat.squirrel.database.collections;

import chat.squirrel.database.entities.IEntity;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertManyResult;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import io.vertx.core.Promise;
import io.vertx.core.WorkerExecutor;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public abstract class AbstractMongoCollection<T extends IEntity> implements ICollection<T> {
    private final MongoCollection<T> collection;
    private final WorkerExecutor worker;

    protected AbstractMongoCollection(final MongoCollection<T> collection, final WorkerExecutor worker) {
        this.collection = collection;
        this.worker = worker;
    }

    // CREATE
    @Override
    public CompletionStage<InsertOneResult> insertOne(final T entity) {
        final CompletableFuture<InsertOneResult> future = new CompletableFuture<>();
        this.worker.executeBlocking(
                (Promise<InsertOneResult> p) -> p.complete(collection.insertOne(entity)),
                r -> {
                    if (r.failed()) {
                        future.completeExceptionally(r.cause());
                        System.out.println(r.cause().getMessage());
                    } else {
                        System.out.println(r.result().getInsertedId());
                        System.out.println(r.result().wasAcknowledged());
                        future.complete(r.result());
                    }
                });
        return future;
    }

    @Override
    public CompletionStage<InsertManyResult> insertMany(final Collection<T> entities) {
        final CompletableFuture<InsertManyResult> future = new CompletableFuture<>();
        this.worker.executeBlocking(
                (Promise<InsertManyResult> p) -> p.complete(collection.insertMany(List.copyOf(entities))),
                r -> {
                    if (r.failed()) {
                        future.completeExceptionally(r.cause());
                    } else {
                        future.complete(r.result());
                    }
                });
        return future;
    }

    // READ
    @Override
    public CompletionStage<T> findEntity(final Bson filters) {
        final CompletableFuture<T> future = new CompletableFuture<>();
        this.worker.executeBlocking(
                (Promise<T> p) -> p.complete(collection.find(filters).first()),
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
    public CompletionStage<Collection<T>> findEntities(final Bson filters) {
        final CompletableFuture<Collection<T>> future = new CompletableFuture<>();
        this.worker.executeBlocking(
                (Promise<Collection<T>> p) -> {
                    final List<T> entities = new ArrayList<>();
                    collection.find(filters).into(entities);
                    p.complete(entities);
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
    public CompletionStage<Long> countDocuments() {
        final CompletableFuture<Long> future = new CompletableFuture<>();
        this.worker.executeBlocking(
                (Promise<Long> p) -> p.complete(collection.countDocuments()),
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
    public CompletionStage<Long> countDocuments(final Bson filters) {
        final CompletableFuture<Long> future = new CompletableFuture<>();
        this.worker.executeBlocking(
                (Promise<Long> p) -> p.complete(collection.countDocuments(filters)),
                r -> {
                    if (r.failed()) {
                        future.completeExceptionally(r.cause());
                    } else {
                        future.complete(r.result());
                    }
                });
        return future;
    }

    // UPDATE
    @Override
    public CompletionStage<UpdateResult> updateEntity(final Bson filter, final Bson ops) {
        final CompletableFuture<UpdateResult> future = new CompletableFuture<>();
        this.worker.executeBlocking(
                (Promise<UpdateResult> p) -> p.complete(collection.updateOne(filter, ops)),
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
    public CompletionStage<UpdateResult> updateEntities(final Bson filter, final Bson ops) {
        final CompletableFuture<UpdateResult> future = new CompletableFuture<>();
        this.worker.executeBlocking(
                (Promise<UpdateResult> p) -> p.complete(collection.updateMany(filter, ops)),
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
    public CompletionStage<UpdateResult> replaceEntity(final Bson filter, final T entity) {
        final CompletableFuture<UpdateResult> future = new CompletableFuture<>();
        this.worker.executeBlocking(
                (Promise<UpdateResult> p) -> p.complete(collection.replaceOne(filter, entity)),
                r -> {
                    if (r.failed()) {
                        future.completeExceptionally(r.cause());
                    } else {
                        future.complete(r.result());
                    }
                });
        return future;
    }

    // DELETE
    @Override
    public CompletionStage<DeleteResult> deleteEntity(final Bson filter) {
        final CompletableFuture<DeleteResult> future = new CompletableFuture<>();
        this.worker.executeBlocking(
                (Promise<DeleteResult> p) -> p.complete(collection.deleteOne(filter)),
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
    public CompletionStage<DeleteResult> deleteEntities(final Bson filter) {
        final CompletableFuture<DeleteResult> future = new CompletableFuture<>();
        this.worker.executeBlocking(
                (Promise<DeleteResult> p) -> p.complete(collection.deleteMany(filter)),
                r -> {
                    if (r.failed()) {
                        future.completeExceptionally(r.cause());
                    } else {
                        future.complete(r.result());
                    }
                });
        return future;
    }

    // YEET
    protected MongoCollection<T> getRawCollection() {
        return this.collection;
    }

    protected WorkerExecutor getWorker() {
        return this.worker;
    }
}
