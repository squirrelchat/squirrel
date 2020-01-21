package chat.squirrel.entities.channels;

import java.util.Collection;

import org.bson.BsonType;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;

import chat.squirrel.Squirrel;
import chat.squirrel.core.DatabaseManager.SquirrelCollection;
import chat.squirrel.entities.AbstractEntity;

public class VoiceChannel extends AbstractEntity implements IChannel {
    private static Logger LOG = LoggerFactory.getLogger(VoiceChannel.class);
    private String name;
    private Collection<ObjectId> currentlyConnected;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    @BsonIgnore
    public Collection<ObjectId> getParticipants() { // TODO
        return null;
    }

    public Collection<ObjectId> getCurrentlyConnected() {
        return currentlyConnected;
    }

    public void setCurrentlyConnected(Collection<ObjectId> currentlyConnected) {
        this.currentlyConnected = currentlyConnected;
    }

    /**
     * Removes all currently connected people from channels
     * 
     * Useful if server didn't shutdown properly
     * 
     * Meant to be called on startup to clean database after an improper shutdown
     */
    public static void wipeCurrentlyConnected() {
        final UpdateResult res = Squirrel.getInstance().getDatabaseManager().updateMany(SquirrelCollection.CHANNELS,
                Filters.type("currentlyConnected", BsonType.ARRAY), Updates.unset("currentlyConnected"));
        if (res.getModifiedCount() != 0)
            LOG.warn("Wipped " + res.getModifiedCount() + " voice channels of participants");
    }

}
