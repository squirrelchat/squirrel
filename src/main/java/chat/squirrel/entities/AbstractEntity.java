package chat.squirrel.entities;

import org.bson.types.ObjectId;

import io.vertx.core.json.JsonObject;

public abstract class AbstractEntity implements IEntity {
    protected ObjectId id;

    /**
     * A new ObjectId is generated
     */
    public AbstractEntity() {
        id = new ObjectId();
    }
    
    @Override
    public ObjectId getId() {
        return id;
    }

    @Override
    public JsonObject toJson() {
        return JsonObject.mapFrom(this);
    }
    
}
