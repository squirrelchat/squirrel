package chat.squirrel.core;

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

import chat.squirrel.Version;
import chat.squirrel.entities.IEntity;

public class DatabaseManager {
    private static final Logger LOG = LoggerFactory.getLogger(DatabaseManager.class);
    private final MongoClient client;
    private final MongoDatabase db;

    public DatabaseManager(String connectionString, String dbName) {
        final CodecRegistry pojoCodecRegistry = CodecRegistries.fromRegistries(
                MongoClientSettings.getDefaultCodecRegistry(),
                CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build()));

        final ConnectionString conStr = new ConnectionString(connectionString);
        
        LOG.info("Connecting to MongoDB using con string: " + conStr.toString());

        final MongoClientSettings set = MongoClientSettings.builder()
                .applicationName("Squirrel (" + Version.VERSION + ")").applyConnectionString(conStr)
                .codecRegistry(pojoCodecRegistry).build();

        client = MongoClients.create(set);
        db = client.getDatabase(dbName);
    }

    public FindIterable<Document> rawRequest(String collection, Bson statement) {
        return db.getCollection(collection).find(statement);
    }

    public void updateEntity(String col, Bson filter, Bson update) {
        db.getCollection(col).updateOne(filter, update);
    }

    public void insertEntity(String col, IEntity ent) {
        db.getCollection(col, IEntity.class).insertOne(ent);
    }
    
    public IEntity findFirstEntity(Class<? extends IEntity> type, String col, Bson filters) {
        return findEntities(type, col, filters).first();
    }
    
    public FindIterable<? extends IEntity> findEntities(Class<? extends IEntity> type, String col, Bson filters) {
        return db.getCollection(col, type).find(filters);
    }
    
    public void shutdown() {
        LOG.info("Shutting down MongoDB driver");
        client.close();
    }

}
