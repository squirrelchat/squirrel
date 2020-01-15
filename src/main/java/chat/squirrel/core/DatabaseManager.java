package chat.squirrel.core;

import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

import chat.squirrel.Squirrel;
import chat.squirrel.Version;
import chat.squirrel.entities.IEntity;

public class DatabaseManager {
    private final MongoClient client;
    private final MongoDatabase db;

    public DatabaseManager() {
        final CodecRegistry pojoCodecRegistry = CodecRegistries.fromRegistries(
                MongoClientSettings.getDefaultCodecRegistry(),
                CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build()));

        final MongoClientSettings set = MongoClientSettings.builder()
                .applicationName("Squirrel (" + Version.VERSION + ")")
                .applyConnectionString(new ConnectionString(Squirrel.getInstance().getProperty("mongo.con-string")))
                .codecRegistry(pojoCodecRegistry).build();

        client = MongoClients.create(set);
        db = client.getDatabase(Squirrel.getInstance().getProperty("mongo.db-name", "squirrel"));
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

}
