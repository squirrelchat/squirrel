package chat.squirrel.entities;

import org.bson.types.ObjectId;

public class Message extends AbstractEntity implements IMessage {
    private ObjectId avatar, author;
    private String content;

    @Override
    public ObjectId getAvatar() {
        return avatar;
    }

    @Override
    public ObjectId getAuthor() {
        return author;
    }

    @Override
    public String getContent() {
        return content;
    }

}
