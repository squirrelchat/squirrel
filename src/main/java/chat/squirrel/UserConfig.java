package chat.squirrel;

import chat.squirrel.entities.AbstractEntity;

public class UserConfig extends AbstractEntity {
    private Class<?> owner;

    public UserConfig(Class<?> owner) {
        this.owner = owner;
    }

    public Class<?> getOwner() {
        return owner;
    }

    public void setOwner(Class<?> owner) {
        this.owner = owner;
    }


}
