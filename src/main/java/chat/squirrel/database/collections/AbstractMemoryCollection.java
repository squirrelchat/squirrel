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

import chat.squirrel.Squirrel;
import chat.squirrel.database.entities.IEntity;
import chat.squirrel.database.memory.IMemoryAdapter;
import chat.squirrel.database.memory.MemoryOperationException;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertManyResult;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import io.lettuce.core.KeyValue;
import org.bson.*;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public abstract class AbstractMemoryCollection<T extends IEntity> implements ICollection<T> { // TODO
    private final IMemoryAdapter memoryAdapter;
    private final String collectionName;

    protected AbstractMemoryCollection(final IMemoryAdapter memoryAdapter, final String collectionName) {
        this.memoryAdapter = memoryAdapter;
        this.collectionName = collectionName;
    }

    // CREATE
    @Override
    public CompletionStage<InsertOneResult> insertOne(final T entity) {
        final String id = collectionName + ':' + getDocumentId(entity);
        final CompletableFuture<InsertOneResult> future = new CompletableFuture<>();
        final CodecRegistry codecRegistry = Squirrel.getInstance().getDatabaseManager().getCodecRegistry();
        memoryAdapter.setEntity(id, BsonDocumentWrapper.asBsonDocument(entity, codecRegistry))
                .thenAccept(s -> {
                    if (s) {
                        future.complete(InsertOneResult.acknowledged(new BsonString(id)));
                    } else {
                        future.completeExceptionally(new MemoryOperationException());
                    }
                });
        return future;
    }

    @Override
    public CompletionStage<InsertManyResult> insertMany(final Collection<T> entities) {
        throw new UnsupportedOperationException();
    }

    // READ
    @Override
    public CompletionStage<T> findEntity(final Bson filter) {
        final CompletableFuture<T> future = new CompletableFuture<>();
        final CodecRegistry codecRegistry = Squirrel.getInstance().getDatabaseManager().getCodecRegistry();
        final PojoCodecProvider pojoCodecProvider = Squirrel.getInstance().getDatabaseManager().getPojoCodecProvider();
        final ObjectId targetId = filter.toBsonDocument(BSONObject.class, codecRegistry).getObjectId("_id").getValue();
        final String id = collectionName + ':' + targetId.toHexString();
        memoryAdapter.getEntity(id)
                .thenAccept(doc -> {
                    final Codec<T> codec = pojoCodecProvider.get(getEntityClass(), codecRegistry);
                    future.complete(codec.decode(doc.asBsonReader(), DecoderContext.builder().build()));
                });
        return future;
    }

    @Override
    public CompletionStage<Collection<T>> findEntities(final Bson filter) {
        final CompletableFuture<Collection<T>> future = new CompletableFuture<>();
        final CodecRegistry codecRegistry = Squirrel.getInstance().getDatabaseManager().getCodecRegistry();
        final PojoCodecProvider pojoCodecProvider = Squirrel.getInstance().getDatabaseManager().getPojoCodecProvider();
        memoryAdapter.getEntities(keysFromFilter(filter)).thenAccept(results -> {
            final Collection<T> collection = new ArrayList<>();
            final Codec<T> codec = pojoCodecProvider.get(getEntityClass(), codecRegistry);
            for (KeyValue<String, BsonDocument> result : results) {
                final BsonDocument document = result.getValue();
                collection.add(codec.decode(document.asBsonReader(), DecoderContext.builder().build()));
            }
            future.complete(collection);
        });
        return future;
    }

    @Override
    public CompletionStage<Long> countDocuments() {
        throw new UnsupportedOperationException();
    }

    @Override
    public CompletionStage<Long> countDocuments(final Bson filter) {
        throw new UnsupportedOperationException();
    }

    // UPDATE
    @Override
    public CompletionStage<T> findAndUpdateEntity(final Bson filter, final Bson ops) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CompletionStage<UpdateResult> updateEntity(final Bson filter, final Bson ops) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CompletionStage<UpdateResult> updateEntities(final Bson filter, final Bson ops) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CompletionStage<UpdateResult> replaceEntity(final Bson filter, final T entity) {
        final CompletableFuture<UpdateResult> future = new CompletableFuture<>();
        final CodecRegistry codecRegistry = Squirrel.getInstance().getDatabaseManager().getCodecRegistry();
        final String id = collectionName + ':' + filter.toBsonDocument(BSONObject.class, codecRegistry).getObjectId("_id").getValue().toHexString();
        memoryAdapter.setEntity(id, BsonDocumentWrapper.asBsonDocument(entity, codecRegistry))
                .thenAccept(s -> {
                    if (s) {
                        future.complete(UpdateResult.acknowledged(1, null, null));
                    } else {
                        future.completeExceptionally(new MemoryOperationException());
                    }
                });
        return future;
    }

    // DELETE
    @Override
    public CompletionStage<DeleteResult> deleteEntity(final Bson filter) {
        final CompletableFuture<DeleteResult> future = new CompletableFuture<>();
        final CodecRegistry codecRegistry = Squirrel.getInstance().getDatabaseManager().getCodecRegistry();
        final String id = collectionName + ':' + filter.toBsonDocument(BSONObject.class, codecRegistry).getObjectId("_id").getValue().toHexString();
        memoryAdapter.deleteOne(id).thenAccept(count -> future.complete(DeleteResult.acknowledged(count)));
        return future;
    }

    @Override
    public CompletionStage<DeleteResult> deleteEntities(final Bson filter) {
        final CompletableFuture<DeleteResult> future = new CompletableFuture<>();
        memoryAdapter.deleteMany(keysFromFilter(filter)).thenAccept(count -> future.complete(DeleteResult.acknowledged(count)));
        return future;
    }

    protected String getDocumentId(final T document) {
        return document.getId().toHexString();
    }

    // YEET
    protected IMemoryAdapter getMemoryAdapter() {
        return memoryAdapter;
    }

    private String[] keysFromFilter(final Bson filter) {
        final CodecRegistry codecRegistry = Squirrel.getInstance().getDatabaseManager().getCodecRegistry();
        final BsonArray targetIds = filter.toBsonDocument(BSONObject.class, codecRegistry).getArray("_id");
        final String[] keys = new String[targetIds.size()];
        for (int i = 0; i < targetIds.size(); i++) {
            keys[i] = collectionName + ':' + targetIds.get(i).asObjectId().getValue().toHexString();
        }
        return keys;
    }
}
