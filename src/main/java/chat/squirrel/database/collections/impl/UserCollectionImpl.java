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

import chat.squirrel.database.collections.AbstractCollection;
import chat.squirrel.database.collections.IUserCollection;
import chat.squirrel.database.entities.IUser;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class UserCollectionImpl extends AbstractCollection<IUser> implements IUserCollection {
    private final Random random = new Random();

    protected UserCollectionImpl(MongoCollection<IUser> collection) {
        super(collection);
    }

    @Override
    public int getFreeDiscriminator(final String username) {
        if (this.countDocuments(Filters.eq("username", username)) >= 5000) {
            return -1;
        }

        int dis;
        final List<Integer> used = new ArrayList<>();
        this.findEntities(Filters.eq("username", username)).forEach(u -> used.add(u.getDiscriminator()));

        // noinspection StatementWithEmptyBody
        while (used.indexOf(dis = this.random.nextInt(10000)) != -1) ;
        return dis;
    }

    @Override
    public boolean isDiscriminatorTaken(final String username, final int discriminator) {
        return this.findEntity(Filters.and(
                Filters.eq("username", username),
                Filters.eq("discriminator", discriminator)
        )) != null;
    }
}