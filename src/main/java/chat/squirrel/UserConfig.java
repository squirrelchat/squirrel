package chat.squirrel;

import chat.squirrel.entities.AbstractEntity;

public class UserConfig extends AbstractEntity {
    private Class<?> owner;

    public UserConfig(final Class<?> owner) {
        this.owner = owner;
    }

    public Class<?> getOwner() {
        return this.owner;
    }

    public void setOwner(final Class<?> owner) {
        this.owner = owner;
    }

}
