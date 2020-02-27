package chat.squirrel.mail;

import chat.squirrel.UserConfig;
import io.vertx.ext.mail.MailConfig;

public class SquirrelMailConfig extends UserConfig {
    private MailConfig config;
    private String templateLookupFolder, fromEmail;
    private boolean enabled;

    public String getFromEmail() {
        return fromEmail;
    }

    public void setFromEmail(String fromEmail) {
        this.fromEmail = fromEmail;
    }

    public SquirrelMailConfig(Class<?> owner) {
        super(owner);
    }

    public MailConfig getConfig() {
        return config;
    }

    public void setConfig(MailConfig config) {
        this.config = config;
    }

    public String getTemplateLookupFolder() {
        return templateLookupFolder;
    }

    public void setTemplateLookupFolder(String templateLookupFolder) {
        this.templateLookupFolder = templateLookupFolder;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

}
