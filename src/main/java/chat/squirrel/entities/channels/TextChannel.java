package chat.squirrel.entities.channels;

import java.util.Collection;

import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.types.ObjectId;

import chat.squirrel.entities.AbstractEntity;
import chat.squirrel.entities.IMessage;

public class TextChannel extends AbstractEntity implements IChannel {
    private String name;
    private ObjectId guildId;

    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public void setName(String name) {
        this.name = name;
    }

    @BsonIgnore
    public Collection<IMessage> getMessages(int nbr) { // TODO
        return null;
    }

    @Override
    public Collection<ObjectId> getParticipants() {
        // TODO Auto-generated method stub
        return null;
    }

    public ObjectId getGuildId() {
        return guildId;
    }

    public void setGuildId(ObjectId guildId) {
        this.guildId = guildId;
    }

}
