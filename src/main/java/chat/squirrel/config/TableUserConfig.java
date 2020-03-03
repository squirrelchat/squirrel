package chat.squirrel.config;

import java.util.Hashtable;

import chat.squirrel.UserConfig;

public class TableUserConfig extends UserConfig {
    private Hashtable<String, Object> table;

    public TableUserConfig(final Class<?> owner) {
        super(owner);
        this.table = new Hashtable<>();
    }

    public Hashtable<String, Object> getTable() {
        return this.table;
    }

    public void setTable(final Hashtable<String, Object> table) {
        this.table = table;
    }

}
