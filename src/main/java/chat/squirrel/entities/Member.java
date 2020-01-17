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
import chat.squirrel.core.DatabaseManager.SquirrelCollection;

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
            return (User) Squirrel.getInstance().getDatabaseManager().findFirstEntity(User.class, SquirrelCollection.USERS,
                    Filters.eq(getUserId()));
        });
    }

    public Collection<ObjectId> getRolesIds() {
        return roles;
    }

    public void setRolesIds(Collection<ObjectId> roles) {
        this.roles = roles;
    }

    @BsonIgnore
    public Future<Guild> getGuild() {
        return new FutureTask<Guild>(new Callable<Guild>() {
            @Override
            public Guild call() throws Exception {
                return (Guild) Squirrel.getInstance().getDatabaseManager().findFirstEntity(Guild.class,
                        SquirrelCollection.GUILDS, Filters.eq(getGuildId()));
            }
        });
    }

    @BsonIgnore
    public Future<Collection<Role>> getRoles() {
        return new FutureTask<Collection<Role>>(new Callable<Collection<Role>>() {
            @Override
            public Collection<Role> call() throws Exception { // XXX this is ugly
                final Guild guild = getGuild().get();
                final Collection<ObjectId> ids = getRolesIds();
                final Collection<Role> realRoles = new ArrayList<Role>();
                for (final Role role : guild.getRoles()) {
                    if (ids.contains(role.getId()))
                        realRoles.add(role);
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
