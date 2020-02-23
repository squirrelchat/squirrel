package chat.squirrel.upload;

import org.bson.types.ObjectId;

import com.mongodb.client.model.Filters;

import chat.squirrel.Squirrel;
import chat.squirrel.core.DatabaseManager.SquirrelCollection;

public abstract class AbstractUploadManager implements IUploadManager {
    protected void insertAsset(Asset asset) {
        Squirrel.getInstance().getDatabaseManager().insertEntity(SquirrelCollection.ASSETS, asset);
    }

    protected Asset retrieveAsset(String id) {
        return retrieveAsset(new ObjectId(id));
    }

    protected Asset retrieveAsset(ObjectId id) {
        return Squirrel.getInstance().getDatabaseManager().findFirstEntity(Asset.class, SquirrelCollection.ASSETS,
                Filters.eq(id));
    }
    
    protected void removeAsset(String id) {
        removeAsset(new ObjectId(id));
    }
    
    protected void removeAsset(ObjectId id) {
        Squirrel.getInstance().getDatabaseManager().deleteEntity(SquirrelCollection.ASSETS, Filters.eq(id));
    }
}
