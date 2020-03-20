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
import chat.squirrel.database.collections.ICollection;
import chat.squirrel.database.collections.SquirrelCollection;
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

import java.util.List;

/**
 * Class responsible for managing database interactions
 */
public class DatabaseManager {
    private static final Logger LOG = LoggerFactory.getLogger(DatabaseManager.class);
    private final MongoClient client;
    private final MongoDatabase db;

    private final CodecRegistry pojoCodecRegistry;

    /**
     * @param connectionString MongoDB Connection String
     * @param dbName           The database to use
     */
    public DatabaseManager(final String connectionString, final String dbName) {
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

        final ConnectionString conStr = new ConnectionString(connectionString);

        LOG.info("Connecting to MongoDB using con string: " + conStr.toString());
        final MongoClientSettings set = MongoClientSettings.builder()
                .applicationName("Squirrel (" + Version.VERSION + ")") // TODO: maybe consider adding a hash (scaled env)
                .applyConnectionString(conStr)
                .codecRegistry(this.pojoCodecRegistry)
                .build();

        this.client = MongoClients.create(set);
        this.db = this.client.getDatabase(dbName);
    }

    public <T extends ICollection> T getCollection(Class<T> collection) {
        final SquirrelCollection[] annotations = collection.getAnnotationsByType(SquirrelCollection.class);
        if (annotations.length == 0) {
            return null;
        }

        final SquirrelCollection annotation = annotations[0];
        // todo: yikes
        return null;
    }
}
