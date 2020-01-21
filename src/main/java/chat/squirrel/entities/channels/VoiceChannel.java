package chat.squirrel.entities.channels;

import java.util.Collection;

import org.bson.types.ObjectId;

import chat.squirrel.entities.AbstractEntity;

public class VoiceChannel extends AbstractEntity implements IChannel {
    private String name;
    private Collection<ObjectId> currentlyConnected;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Collection<ObjectId> getParticipants() {
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
     * Useful if server didn't shutdown properly
     */
    public static void wipeCurrentlyConnected() {
        
    }

}
