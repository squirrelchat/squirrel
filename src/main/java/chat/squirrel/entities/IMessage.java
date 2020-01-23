package chat.squirrel.entities;

import org.bson.types.ObjectId;

import chat.squirrel.entities.channels.IChannel;

/**
 * A general message in a {@link IChannel} or Group (TODO)
 * 
 */
public interface IMessage extends IEntity {
    /**
     * Author of the message. Can be either from a {@link User} or a Bot
     * 
     * @return ID of the author of the message
     */
    ObjectId getAuthor();

    /**
     * @return The String content of the message
     */
    String getContent();

}
