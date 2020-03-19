package chat.squirrel.entities;

import chat.squirrel.entities.impl.BadgeImpl;
import chat.squirrel.upload.Asset;

/**
 * A badge is a server-wide entity and not attributed to a single guild
 */

public interface IBadge extends IEntity {
    String getName();

    void setName(String name);

    String getDescription();

    void setDescription(String description);

    Asset getIcon();

    void setIcon(Asset asset);
}
