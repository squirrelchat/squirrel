package chat.squirrel.entities;

import org.bson.types.ObjectId;

public interface IMessage extends IEntity {
    ObjectId getAvatar();

    /**
     * Author of the message. Can be either from a User or a Bot
     * @return ID of the author of the message
     */
    ObjectId getAuthor();

    String getContent();

}
