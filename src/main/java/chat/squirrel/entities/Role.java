package chat.squirrel.entities;

import java.util.Collection;

import chat.squirrel.entities.impl.RoleImpl;

public interface Role extends IEntity {
    public static Role create() {
        return new RoleImpl();
    }

    int getColor();

    String getName();

    Collection<String> getPermissions();

    void setColor(int color);

    void setName(String name);

    void setPermissions(Collection<String> permissions);
}
