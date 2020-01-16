package chat.squirrel.entities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.types.ObjectId;

import com.mongodb.client.model.Filters;

import chat.squirrel.Squirrel;

/**
 * Member of a guild
 */
public class Member extends AbstractEntity {
    private ObjectId userId, guildId;
    private String nickmame;
    private Collection<ObjectId> roles;

    public ObjectId getUserId() {
        return userId;
    }

    public void setUserId(ObjectId userId) {
        this.userId = userId;
    }

    @BsonIgnore
    public Future<User> getUser() {
        return new FutureTask<User>(() -> {
            return (User) Squirrel.getInstance().getDatabaseManager().findFirstEntity(User.class, "users",
                    Filters.eq("_id", getUserId()));
        });
    }

    public Collection<ObjectId> getRoles() {
        return roles;
    }

    public void setRoles(Collection<ObjectId> roles) {
        this.roles = roles;
    }

    @BsonIgnore
    public Future<Collection<Role>> getRealRoles() {
        return new FutureTask<Collection<Role>>(new Callable<Collection<Role>>() {

            @Override
            public Collection<Role> call() throws Exception { // TODO
                Collection<Role> realRoles = new ArrayList<Role>();
                for (ObjectId id : getRoles()) {
                }
                return realRoles;
            }
        });
       
    }

    public String getNickmame() {
        return nickmame;
    }

    public void setNickmame(String nickmame) {
        this.nickmame = nickmame;
    }

    public ObjectId getGuildId() {
        return guildId;
    }

    public void setGuildId(ObjectId guildId) {
        this.guildId = guildId;
    }

}
