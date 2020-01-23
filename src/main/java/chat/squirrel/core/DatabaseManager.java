/*
 * Copyright (c) 2020-present Bowser65 & vinceh121, All rights reserved.
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

package chat.squirrel.core;

import java.util.Random;

import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.UpdateResult;

import chat.squirrel.Version;
import chat.squirrel.entities.IEntity;
import chat.squirrel.entities.User;

/**
 * A DatabaseManager manages the interactions with MongoDB
 */
public class DatabaseManager {
    private static final Logger LOG = LoggerFactory.getLogger(DatabaseManager.class);
    private final MongoClient client;
    private final MongoDatabase db;
    private final Random random = new Random();
    private final CodecRegistry pojoCodecRegistry;

    /**
     * 
     * @param connectionString MongoDB Connection String
     * @param dbName           The database to use
     */
    public DatabaseManager(String connectionString, String dbName) {
        pojoCodecRegistry = CodecRegistries.fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build()));

        final ConnectionString conStr = new ConnectionString(connectionString);

        LOG.info("Connecting to MongoDB using con string: " + conStr.toString());

        final MongoClientSettings set = MongoClientSettings.builder()
                .applicationName("Squirrel (" + Version.VERSION + ")").applyConnectionString(conStr)
                .codecRegistry(pojoCodecRegistry).build();

        client = MongoClients.create(set);
        db = client.getDatabase(dbName);
    }

    public FindIterable<Document> rawRequest(SquirrelCollection collection, Bson statement) {
        return db.getCollection(collection.getMongoName()).find(statement);
    }

    public UpdateResult updateEntity(SquirrelCollection col, Bson filter, Bson update) {
        return db.getCollection(col.getMongoName()).updateOne(filter, update);
    }

    public void insertEntity(SquirrelCollection col, IEntity ent) {
        db.getCollection(col.getMongoName(), IEntity.class).insertOne(ent);
    }

    public IEntity findFirstEntity(Class<? extends IEntity> type, SquirrelCollection col, Bson filters) {
        return findEntities(type, col, filters).first();
    }

    public FindIterable<? extends IEntity> findEntities(Class<? extends IEntity> type, SquirrelCollection col,
            Bson filters) {
        return db.getCollection(col.getMongoName(), type).find(filters);
    }

    public UpdateResult updateMany(SquirrelCollection col, Bson filter, Bson update) {
        return db.getCollection(col.getMongoName()).updateMany(filter, update);
    }

    public long countDocuments(SquirrelCollection col, Bson filters) {
        return db.getCollection(col.getMongoName()).countDocuments(filters);
    }

    public IEntity convertDocument(Document doc, Class<? extends IEntity> cls) {
        if (doc == null)
            throw new NullPointerException();
        return (IEntity) doc.toBsonDocument(cls, pojoCodecRegistry);
    }

    /**
     * This method is used to get an available discriminator for the specified
     * username
     * <p>
     * Not thread safe cause of big gay loop
     *
     * @return The free discriminator, or if none are available, -1
     */
    public int getFreeDiscriminator(final String username) { // TODO make better
        if (countDocuments(SquirrelCollection.USERS, Filters.eq("username", username)) >= 9999) {
            LOG.warn("Username '" + username + "' is out of discriminators and one was just requested.");
            return -1;
        }
        int dis = -1;
        while (isDiscriminatorTaken(username, dis = random.nextInt(10000))) {
        }

        return dis;
    }

    /**
     * 
     * @param username The username string to check
     * @param dis      The discriminator integer to check
     * @return {@link true} if the discriminator is already used for this username,
     *         {@link false} otherwise
     */
    public boolean isDiscriminatorTaken(final String username, final int dis) {
        return findFirstEntity(User.class, SquirrelCollection.USERS,
                Filters.and(Filters.eq("discriminator", dis), Filters.eq("username", username))) != null;
    }

    /**
     * Shutdown the MongoDB Driver
     */
    public void shutdown() {
        LOG.info("Shutting down MongoDB driver");
        client.close();
    }

    /**
     * The Mongo collections used by squirrel.
     */
    public enum SquirrelCollection {

        /**
         * 'users' collection. Contains entity type {@link User}
         */
        USERS("users"),
        /**
         * 'guilds' collection. Contains entity type {@link Guild}
         */
        GUILDS("guilds"),
        /**
         * 'config' collection. Contains entity type {@link SquirrelConfig}
         */
        CONFIG("config"),
        /**
         * 'messages' collection. Contains entity type {@link IMessage}
         */
        MESSAGES("messages"),
        /**
         * 'channels' collection. Contains entity type {@link IChannel}
         */
        CHANNELS("channels");

        private String mongoName;

        private SquirrelCollection(String mongoName) {
            this.mongoName = mongoName;
        }

        /**
         * @return the name of the MongoDB collection to use because we try to comply
         *         with BSON naming standards.
         */
        public String getMongoName() {
            return mongoName;
        }
    }

}
