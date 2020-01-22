package chat.squirrel.entities.channels;

import java.util.Collection;

import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.types.ObjectId;

import chat.squirrel.entities.AbstractEntity;

public class VoiceChannel extends AbstractEntity implements IChannel {
    private String name;

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

}
