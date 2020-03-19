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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.ClassModelBuilder;
import org.bson.codecs.pojo.Convention;
import org.bson.codecs.pojo.Conventions;
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
import com.mongodb.client.model.InsertOneModel;
import com.mongodb.client.model.WriteModel;
import com.mongodb.client.result.UpdateResult;

import chat.squirrel.Version;
import chat.squirrel.entities.IEntity;
import chat.squirrel.entities.IGuild;
import chat.squirrel.entities.IMessage;
import chat.squirrel.entities.IUser;
import chat.squirrel.entities.channels.IChannel;
import chat.squirrel.entities.impl.UserImpl;

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
     * @param connectionString MongoDB Connection String
     * @param dbName           The database to use
     */
    public DatabaseManager(final String connectionString, final String dbName) {
        final Convention entityConvention = new Convention() {

            @Override
            public void apply(ClassModelBuilder<?> classModelBuilder) {
                classModelBuilder.enableDiscriminator(true);
            }
        };

        this.pojoCodecRegistry = CodecRegistries.fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                CodecRegistries.fromProviders(PojoCodecProvider.builder()
                        .automatic(true)
                        .conventions(List.of(entityConvention, Conventions.ANNOTATION_CONVENTION,
                                Conventions.CLASS_AND_PROPERTY_CONVENTION)) // Defaults + entityConvention
                        .build()));

        final ConnectionString conStr = new ConnectionString(connectionString);

        LOG.info("Connecting to MongoDB using con string: " + conStr.toString());

        final MongoClientSettings set = MongoClientSettings.builder()
                .applicationName("Squirrel (" + Version.VERSION + ")")
                .applyConnectionString(conStr)
                .codecRegistry(this.pojoCodecRegistry)
                .build();

        this.client = MongoClients.create(set);
        this.db = this.client.getDatabase(dbName);
    }

    public IEntity convertDocument(final Document doc, final Class<? extends IEntity> cls) {
        if (doc == null) {
            throw new NullPointerException();
        }
        return (IEntity) doc.toBsonDocument(cls, this.pojoCodecRegistry);
    }

    public long countDocuments(final SquirrelCollection col, final Bson filters) {
        return this.db.getCollection(col.getMongoName()).countDocuments(filters);
    }

    public void deleteEntity(final SquirrelCollection col, final Bson filters) {
        this.db.getCollection(col.getMongoName()).findOneAndDelete(filters);
    }

    public <T extends IEntity> FindIterable<T> findEntities(final Class<T> type, final SquirrelCollection col,
            final Bson filters) {
        return this.db.getCollection(col.getMongoName(), type).find(filters);
    }

    public <T extends IEntity> T findFirstEntity(final Class<T> type, final SquirrelCollection col,
            final Bson filters) {
        return this.findEntities(type, col, filters).first();
    }

    /**
     * Method used to get an available discriminator for the specified username
     *
     * @return A free discriminator, or -1 if none are available
     */
    public int getFreeDiscriminator(final String username) {
        if (this.countDocuments(SquirrelCollection.USERS, Filters.eq("username", username)) >= 5000) {
            LOG.warn("Username '" + username + "' is out of discriminators and one was just requested.");
            return -1;
        }

        int dis;
        final List<Integer> used = new ArrayList<>();
        this.findEntities(IUser.class, SquirrelCollection.USERS, Filters.eq("username", username))
                .forEach((Consumer<IUser>) u -> used.add(u.getDiscriminator()));

        // noinspection StatementWithEmptyBody
        while (used.indexOf(dis = this.random.nextInt(10000)) != -1) {

        }
        return dis;
    }

    public void insertEntity(final SquirrelCollection col, final IEntity ent) {
        this.db.getCollection(col.getMongoName(), IEntity.class).insertOne(ent);
    }

    public void bulkInsert(final SquirrelCollection col, final Collection<? extends IEntity> ents) {
        final List<InsertOneModel<IEntity>> ops = new ArrayList<>();
        ents.forEach(e -> {
            ops.add(new InsertOneModel<IEntity>(e));
        });
        bulkWrite(col, ops);
    }

    public void bulkWrite(final SquirrelCollection col, final List<? extends WriteModel<? extends IEntity>> ops) {
        this.db.getCollection(col.getMongoName(), IEntity.class).bulkWrite(ops);
    }

    /**
     * @param username The username string to check
     * @param dis      The discriminator integer to check
     * @return {@code true} if the discriminator is already used for this username,
     *         {@code false} otherwise
     */
    public boolean isDiscriminatorTaken(final String username, final int dis) {
        return this.findFirstEntity(UserImpl.class, SquirrelCollection.USERS,
                Filters.and(Filters.eq("discriminator", dis), Filters.eq("username", username))) != null;
    }

    public FindIterable<Document> rawRequest(final SquirrelCollection collection, final Bson statement) {
        return this.db.getCollection(collection.getMongoName()).find(statement);
    }

    /**
     * Shutdown the MongoDB Driver
     */
    public void shutdown() {
        LOG.info("Shutting down MongoDB driver");
        this.client.close();
    }

    public UpdateResult updateEntity(final SquirrelCollection col, final Bson filter, final Bson update) {
        return this.db.getCollection(col.getMongoName()).updateOne(filter, update);
    }

    public UpdateResult updateMany(final SquirrelCollection col, final Bson filter, final Bson update) {
        return this.db.getCollection(col.getMongoName()).updateMany(filter, update);
    }

    public UpdateResult replaceOne(final SquirrelCollection col, final IEntity entity, final Bson filter) {
        return this.db.getCollection(col.getMongoName(), IEntity.class).replaceOne(filter, entity);
    }

    /**
     * The Mongo collections used by squirrel.
     */
    public enum SquirrelCollection {

        /**
         * 'users' collection. Contains entity type {@link IUser}
         */
        USERS("users"),
        /**
         * 'guilds' collection. Contains entity type {@link IGuild}
         */
        GUILDS("guilds"),
        /**
         * 'config' collection. Contains entity type {@link chat.squirrel.UserConfig}
         */
        CONFIG("config"),
        /**
         * 'messages' collection. Contains entity type {@link IMessage}
         */
        MESSAGES("messages"),
        /**
         * 'channels' collection. Contains entity type {@link IChannel}
         */
        CHANNELS("channels"),
        /**
         * 'members' collection. Contains entity type
         * {@link chat.squirrel.entities.IMember}
         */
        MEMBERS("members"),
        /**
         * 'roles' collection. Contains entity type {@link chat.squirrel.entities.IRole}
         */
        ROLES("roles"),
        /**
         * 'assets'. Contains entity type {@link chat.squirrel.upload.Asset}
         */
        ASSETS("assets"),
        /**
         * 'user_settings' collection. Contains entity type UserSettings
         */
        USER_SETTINGS("user_settings"),
        /**
         * 'metrics' collection. Contains entity type Histogram
         */
        METRICS("metrics"),
        /**
         * 'audits' collection. Contains entity type AuditLogEntry
         */
        AUDITS("audits");

        private String mongoName;

        SquirrelCollection(final String mongoName) {
            this.mongoName = mongoName;
        }

        /**
         * @return the name of the MongoDB collection to use because we try to comply
         *         with BSON naming standards.
         */
        public String getMongoName() {
            return this.mongoName;
        }
    }
}
