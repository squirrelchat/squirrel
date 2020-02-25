package chat.squirrel.config;

import java.util.Hashtable;

import chat.squirrel.UserConfig;

public class TableUserConfig extends UserConfig {
    private Hashtable<String, Object> table;

    public TableUserConfig(Class<?> owner) {
        super(owner);
        table = new Hashtable<String, Object>();
    }

    public Hashtable<String, Object> getTable() {
        return table;
    }

    public void setTable(Hashtable<String, Object> table) {
        this.table = table;
    }

}
