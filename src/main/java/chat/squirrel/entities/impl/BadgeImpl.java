package chat.squirrel.entities.impl;

import chat.squirrel.entities.AbstractEntity;
import chat.squirrel.entities.IBadge;
import chat.squirrel.upload.Asset;

public class BadgeImpl extends AbstractEntity implements IBadge {
    private String name, description;
    private Asset icon;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Asset getIcon() {
        return icon;
    }

    @Override
    public void setIcon(Asset asset) {
        this.icon = asset;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

}
