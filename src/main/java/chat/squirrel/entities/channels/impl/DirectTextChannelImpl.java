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

package chat.squirrel.entities.channels.impl;

import chat.squirrel.Squirrel;
import chat.squirrel.core.DatabaseManager.SquirrelCollection;
import chat.squirrel.entities.IUser;
import chat.squirrel.entities.channels.IDirectTextChannel;
import com.mongodb.client.model.Filters;
import org.bson.types.ObjectId;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public class DirectTextChannelImpl extends AbstractTextChannel implements IDirectTextChannel {
    private ObjectId firstParticipant, secondParticipant;

    @Override
    public Future<Collection<ObjectId>> getParticipants() {
        final CompletableFuture<Collection<ObjectId>> fut = new CompletableFuture<Collection<ObjectId>>();
        fut.complete(Arrays.asList(getFirstParticipant(), getSecondParticipant()));
        return fut;
    }

    @Override
    public ObjectId getFirstParticipant() {
        return firstParticipant;
    }

    @Override
    public void setFirstParticipant(ObjectId fPart) {
        this.firstParticipant = fPart;
    }

    @Override
    public ObjectId getSecondParticipant() {
        return secondParticipant;
    }

    @Override
    public void setSecondParticipant(ObjectId sPart) {
        this.secondParticipant = sPart;
    }

    @Override
    public CompletableFuture<IUser> getRealFirstParticipant() {
        return CompletableFuture.supplyAsync(() -> {
            return Squirrel.getInstance()
                    .getDatabaseManager()
                    .findFirstEntity(IUser.class, SquirrelCollection.USERS, Filters.eq(getFirstParticipant()));
        });
    }

    @Override
    public CompletableFuture<IUser> getRealSecondParticipant() {
        return CompletableFuture.supplyAsync(() -> {
            return Squirrel.getInstance()
                    .getDatabaseManager()
                    .findFirstEntity(IUser.class, SquirrelCollection.USERS, Filters.eq(getSecondParticipant()));
        });
    }

}
