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

import io.lettuce.core.codec.RedisCodec;
import io.netty.util.CharsetUtil;
import org.bson.BsonDocument;
import org.bson.BsonDocumentWriter;
import org.bson.BsonWriter;
import org.bson.codecs.ByteArrayCodec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

import java.nio.ByteBuffer;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;

public class RedisBsonCodec implements RedisCodec<String, BsonDocument> {
    private final ByteArrayCodec codec;

    public RedisBsonCodec() {
        this.codec = new ByteArrayCodec();
    }

    @Override
    public ByteBuffer encodeKey(final String key) {
        final CharsetEncoder encoder = CharsetUtil.encoder(StandardCharsets.UTF_8);
        final ByteBuffer buffer = ByteBuffer.allocate((int) (encoder.maxBytesPerChar() * key.length()));
        buffer.put(key.getBytes(StandardCharsets.UTF_8));
        return buffer;
    }

    @Override
    public String decodeKey(final ByteBuffer bytes) {
        return StandardCharsets.UTF_8.decode(bytes).toString();
    }

    @Override
    public ByteBuffer encodeValue(final BsonDocument document) {
        final byte[] bytes = this.codec.decode(document.asBsonReader(), DecoderContext.builder().build());
        final ByteBuffer buffer = ByteBuffer.allocate(bytes.length);
        buffer.put(bytes);
        return buffer;
    }

    @Override
    public BsonDocument decodeValue(final ByteBuffer bytes) {
        final BsonDocument document = new BsonDocument();
        final BsonWriter writer = new BsonDocumentWriter(document);
        this.codec.encode(writer, bytes.array(), EncoderContext.builder().build());
        return document;
    }
}
