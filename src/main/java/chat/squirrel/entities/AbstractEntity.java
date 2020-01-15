package chat.squirrel.entities;

import org.bson.types.ObjectId;

import io.vertx.core.json.JsonObject;

public abstract class AbstractEntity implements IEntity {
    protected ObjectId id;

    @Override
    public ObjectId getId() {
        return id;
    }

    @Override
    public JsonObject toJson() {
        return JsonObject.mapFrom(this);
    }
    
}
