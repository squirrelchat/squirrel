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
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertManyResult;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.conversions.Bson;

import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.concurrent.CompletionStage;

public interface ICollection<T extends IEntity> {
    // CREATE
    CompletionStage<InsertOneResult> insertOne(final T entity);

    CompletionStage<InsertManyResult> insertMany(final Collection<T> entities);

    // READ
    CompletionStage<T> findEntity(final Bson filters);

    CompletionStage<Collection<T>> findEntities(final Bson filters);

    CompletionStage<Long> countDocuments();

    CompletionStage<Long> countDocuments(final Bson filters);

    // UPDATE
    CompletionStage<T> findAndUpdateEntity(final Bson filter, final Bson ops);

    CompletionStage<UpdateResult> updateEntity(final Bson filter, final Bson ops);

    CompletionStage<UpdateResult> updateEntities(final Bson filter, final Bson ops);

    CompletionStage<UpdateResult> replaceEntity(final Bson filter, final T entity);

    // DELETE
    CompletionStage<DeleteResult> deleteEntity(final Bson filter);

    CompletionStage<DeleteResult> deleteEntities(final Bson filter);

    // Stuff
    default Class<T> getEntityClass() {
        // Credits: https://stackoverflow.com/a/13974262/13154480
        @SuppressWarnings("unchecked") final Class<T> clazz = (Class<T>) ((ParameterizedType)
                this.getClass().getGenericInterfaces()[0])
                .getActualTypeArguments()[0];
        return clazz;
    }
}
