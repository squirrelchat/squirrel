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

package chat.squirrel.database;

import chat.squirrel.Version;
import chat.squirrel.database.collections.*;
import chat.squirrel.database.entities.IEntity;
import chat.squirrel.database.memory.ClassicMemoryAdapter;
import chat.squirrel.database.memory.IMemoryAdapter;
import chat.squirrel.database.memory.RedisMemoryAdapter;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.Convention;
import org.bson.codecs.pojo.Conventions;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InvalidClassException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class responsible for managing database interactions
 */
public class DatabaseManager {
    private static final Logger LOG = LoggerFactory.getLogger(DatabaseManager.class);
    private final IMemoryAdapter memoryAdapter;
    private final MongoClient client;
    private final MongoDatabase db;

    private final CodecRegistry pojoCodecRegistry;
    private final Map<String, ICollection<? extends IEntity>> collections;

    public DatabaseManager(final String mongoConString, final String redisConString, final String mongoDbName, final String memoryConfig) {
        IMemoryAdapter memoryAdapter = null;
        if (memoryConfig.equals("MEMORY")) {
            memoryAdapter = new ClassicMemoryAdapter();
        } else if (memoryConfig.equals("REDIS")) {
            memoryAdapter = new RedisMemoryAdapter(redisConString);
        } else {
            LOG.error("Invalid memory database! Expected MEMORY or REDIS, got " + memoryConfig);
            System.exit(-1);
        }
        this.memoryAdapter = memoryAdapter;

        final Convention entityConvention = classModelBuilder -> classModelBuilder.enableDiscriminator(true);

        this.pojoCodecRegistry = CodecRegistries.fromRegistries(
                MongoClientSettings.getDefaultCodecRegistry(),
                CodecRegistries.fromProviders(PojoCodecProvider.builder()
                        .automatic(true)
                        .conventions(List.of( // Defaults + entityConvention
                                entityConvention,
                                Conventions.ANNOTATION_CONVENTION,
                                Conventions.CLASS_AND_PROPERTY_CONVENTION
                        ))
                        .build())
        );
        final ConnectionString conStr = new ConnectionString(mongoConString);

        LOG.info("Connecting to MongoDB using con string: " + mongoConString);
        final MongoClientSettings set = MongoClientSettings.builder()
                .applicationName("Squirrel (" + Version.VERSION + ")") // TODO: maybe consider adding a hash (scaled env)
                .applyConnectionString(conStr)
                .codecRegistry(this.pojoCodecRegistry)
                .build();

        this.client = MongoClients.create(set);
        this.db = this.client.getDatabase(mongoDbName);
        this.collections = new HashMap<>();
        this.registerCollections();
    }

    public void registerCollection(Class<? extends ICollection<? extends IEntity>> collectionClass) throws InvalidClassException, IllegalAccessException, InvocationTargetException, InstantiationException {
        final SquirrelCollection[] annotations = collectionClass.getAnnotationsByType(SquirrelCollection.class);
        if (annotations.length == 0) {
            throw new InvalidClassException("Collection must be annotated with @SquirrelCollection!");
        }
        final SquirrelCollection annotation = annotations[0];

        Class<? extends ICollection<?>> impl;
        if (collectionClass.isInterface() || Modifier.isAbstract(collectionClass.getModifiers())) {
            impl = annotation.impl();
            if (impl.equals(SquirrelCollection.NULL)) {
                throw new InvalidClassException("An implementation class is required for abstract classes and interfaces");
            }
            if (!collectionClass.isAssignableFrom(impl)) {
                throw new InvalidClassException("Invalid implementation specified in @SquirrelCollection annotation");
            }
            if (impl.isInterface() || Modifier.isAbstract(impl.getModifiers())) {
                throw new InvalidClassException("Implementation cannot be an interface or an abstract class");
            }
        } else {
            impl = collectionClass;
        }
        ICollection<? extends IEntity> instance;
        Constructor<?> constructor = impl.getDeclaredConstructors()[0];
        if (annotation.storageMethod() == SquirrelCollection.StorageMethod.PERSISTENT) {
            instance = (ICollection<? extends IEntity>) constructor.newInstance(db.getCollection(annotation.collection()));
        } else if (annotation.storageMethod() == SquirrelCollection.StorageMethod.MEMORY) {
            instance = (ICollection<? extends IEntity>) constructor.newInstance(memoryAdapter);
        } else {
            throw new IllegalStateException("My life is potato? (Invalid storage method");
        }
        collections.put(collectionClass.getCanonicalName(), instance);
    }

    public <E extends IEntity, T extends ICollection<E>> T getCollection(Class<T> collectionClass) {
        @SuppressWarnings("unchecked") final T collection = (T) this.collections.get(collectionClass.getCanonicalName());
        return collection;
    }

    /**
     * Shutdown the MongoDB Manager
     */
    public void shutdown() {
        LOG.info("Shutting down MongoDB manager");
        this.client.close();
    }

    private void registerCollections() {
        try {
            // Mongo collections
            registerCollection(IUserCollection.class);
            registerCollection(IGuildCollection.class);
            registerCollection(IRoleCollection.class);
            registerCollection(IAuditCollection.class);
            registerCollection(IMemberCollection.class);
            registerCollection(IChannelCollection.class);
            registerCollection(IMessageCollection.class);
            registerCollection(IMetricsCollection.class);
            registerCollection(IConfigCollection.class);

            // Memory collections
            registerCollection(IPresenceCollection.class);
            registerCollection(IVoiceStateCollection.class);
        } catch (InvalidClassException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
            LOG.error("Bruh momento - Failed to register collections", e);
            System.exit(-2);
        }
    }
}
