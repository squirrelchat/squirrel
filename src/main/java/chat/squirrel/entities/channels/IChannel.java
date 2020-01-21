package chat.squirrel.entities.channels;

import java.util.Collection;

import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.types.ObjectId;

import chat.squirrel.entities.IEntity;

public interface IChannel extends IEntity {
    String getName();

    @BsonIgnore
    Collection<ObjectId> getParticipants();
}
