package chat.squirrel.upload;

import org.bson.types.ObjectId;

import com.mongodb.client.model.Filters;

import chat.squirrel.Squirrel;
import chat.squirrel.core.DatabaseManager.SquirrelCollection;

public abstract class AbstractUploadManager implements IUploadManager {
    protected void insertAsset(final Asset asset) {
        Squirrel.getInstance().getDatabaseManager().insertEntity(SquirrelCollection.ASSETS, asset);
    }

    protected Asset retrieveAsset(final String id) {
        return this.retrieveAsset(new ObjectId(id));
    }

    protected Asset retrieveAsset(final ObjectId id) {
        return Squirrel.getInstance().getDatabaseManager().findFirstEntity(Asset.class, SquirrelCollection.ASSETS,
                Filters.eq(id));
    }

    protected void removeAsset(final String id) {
        this.removeAsset(new ObjectId(id));
    }

    protected void removeAsset(final ObjectId id) {
        Squirrel.getInstance().getDatabaseManager().deleteEntity(SquirrelCollection.ASSETS, Filters.eq(id));
    }
}
