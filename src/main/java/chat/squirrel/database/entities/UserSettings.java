/*
 * Copyright (c) 2020 Squirrel Chat, All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holder nor the names of its contributors
 *    may be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package chat.squirrel.database.entities;

import chat.squirrel.Squirrel;
import chat.squirrel.database.DatabaseManagerEditionBoomerware.SquirrelCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.types.ObjectId;

public class UserSettings extends AbstractEntity {
    private String language;

    public String getLanguage() {
        return this.language;
    }

    public void setLanguage(final String language) {
        if (language.length() > 5) {
            throw new IllegalArgumentException("Language string cannot be over 5 in length");
        }
        this.language = language;
    }

    public UpdateResult updateSettings(final ObjectId userId) {
        return Squirrel.getInstance()
                .getDatabaseManager()
                .updateEntity(SquirrelCollection.USERS, Filters.eq(userId), Updates.set("userSettings", this));
    }

    /**
     * @param id The Mongo ID of the user to get the settings for
     * @return the UserSettings object for this user
     */
    public static UserSettings getUserSettings(final ObjectId id) {
        final Document doc = Squirrel.getInstance()
                .getDatabaseManager()
                .rawRequest(SquirrelCollection.USERS, Filters.eq(id))
                .first();

        return (UserSettings) Squirrel.getInstance().getDatabaseManager().convertDocument(doc, UserSettings.class);
    }

}

/*
const user = {
  settings: {
    --------
  }
}
 */
