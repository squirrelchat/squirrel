package chat.squirrel.entities;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;

import chat.squirrel.Squirrel;
import chat.squirrel.core.DatabaseManager.SquirrelCollection;

public class UserSettings extends AbstractEntity {
    private String language;

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        if (language.length() > 5)
            throw new IllegalArgumentException("Language string cannot be over 5 in length");
        this.language = language;
    }

    public UpdateResult updateSettings(ObjectId userId) {
        return Squirrel.getInstance().getDatabaseManager().updateEntity(SquirrelCollection.USERS, Filters.eq(userId),
                Updates.set("userSettings", this));
    }

    /**
     * 
     * @param id The Mongo ID of the user to get the settings for
     * @return the UserSettings object for this user
     */
    public static UserSettings getUserSettings(ObjectId id) {
        Document doc = Squirrel.getInstance().getDatabaseManager().rawRequest(SquirrelCollection.USERS, Filters.eq(id))
                .first();

        return (UserSettings) Squirrel.getInstance().getDatabaseManager().convertDocument(doc, UserSettings.class);
    }

}
