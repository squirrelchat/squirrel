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

package chat.squirrel.database.memory;

import io.lettuce.core.KeyValue;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.async.RedisAsyncCommands;
import org.bson.BsonDocument;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class RedisMemoryAdapter implements IMemoryAdapter {
    private final RedisAsyncCommands<String, BsonDocument> redisCommands;

    public RedisMemoryAdapter(final String conString) {
        this.redisCommands = RedisClient.create(conString).connect(new RedisBsonCodec()).async();
    }

    @Override
    public CompletionStage<BsonDocument> getEntity(final String key) {
        return redisCommands.get(key);
    }

    @Override
    public CompletionStage<List<KeyValue<String, BsonDocument>>> getEntities(final String... keys) {
        return redisCommands.mget(keys);
    }

    @Override
    public CompletionStage<Boolean> setEntity(final String key, final BsonDocument document) {
        final CompletableFuture<Boolean> future = new CompletableFuture<>();
        redisCommands.set(key, document).thenAccept(s -> future.complete(s.equals("OK")));
        return future;
    }

    @Override
    public CompletionStage<Long> deleteOne(final String key) {
        return redisCommands.del(key);
    }

    @Override
    public CompletionStage<Long> deleteMany(final String... keys) {
        return redisCommands.del(keys);
    }
}
