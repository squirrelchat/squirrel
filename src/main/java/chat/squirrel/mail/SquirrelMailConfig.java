package chat.squirrel.mail;

import chat.squirrel.UserConfig;
import io.vertx.ext.mail.MailConfig;

public class SquirrelMailConfig extends UserConfig {
    private MailConfig config;
    private String templateLookupFolder, fromEmail;
    private boolean enabled;

    public SquirrelMailConfig(final Class<?> owner) {
        super(owner);
    }
    
    public String getFromEmail() {
        return this.fromEmail;
    }

    public void setFromEmail(final String fromEmail) {
        this.fromEmail = fromEmail;
    }

    public MailConfig getConfig() {
        return this.config;
    }

    public void setConfig(final MailConfig config) {
        this.config = config;
    }

    public String getTemplateLookupFolder() {
        return this.templateLookupFolder;
    }

    public void setTemplateLookupFolder(final String templateLookupFolder) {
        this.templateLookupFolder = templateLookupFolder;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

}
