package chat.squirrel.mail;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.Vertx;
import io.vertx.ext.mail.MailClient;
import io.vertx.ext.mail.MailConfig;
import io.vertx.ext.mail.MailMessage;

public class NotificationMailManager {
    private static final Logger LOG = LoggerFactory.getLogger(NotificationMailManager.class);
    private MailClient client;
    private final SquirrelMailConfig dbConf;
    private final ExecutorService exec = Executors.newSingleThreadExecutor();
    private boolean enabled = false;

    public NotificationMailManager(final Vertx vertx, final SquirrelMailConfig dbConf) {
        this.dbConf = dbConf;
        if (this.dbConf == null) {
            LOG.error("Mailing configuration not defined in DB");
            return;
        }
        
        if (!this.dbConf.isEnabled()) {
            LOG.info("Notification mail manager is disabled");
            return;
        }

        final MailConfig config = dbConf.getConfig();
        if (config == null) {
            LOG.error("Mailing configuration not defined");
        }

        client = MailClient.create(vertx, config);
        enabled = true;
    }

    public void sendMail(final String mailName, final String[] to, final Locale locale,
            final Map<String, Object> extras) {
        if (!enabled) {
            LOG.warn("Attempted to send email %s to %s but notification mail manager isn't enabled.");
            return;
        }
        exec.submit(() -> {
            LOG.info("Sending email %s in language %s to email %s", mailName, locale.getISO3Language(), to);
            final String raw;
            try {
                raw = getRawEmail(mailName);
            } catch (IOException e) {
                LOG.error("Failed to read email template", e);
                return;
            }
            final Map<String, Object> arguments = new HashMap<>();
            try {
                arguments.putAll(getTranslationsMap(mailName, locale));
            } catch (IOException e) {
                LOG.error("Failed to read email translations for template " + mailName + " in language "
                        + locale.getISO3Language(), e);
                return;
            }
            arguments.putAll(extras);
            final String content = replaceValues(raw, arguments, locale);

            final MailMessage mail = new MailMessage();
            mail.setTo(Arrays.asList(to));
            mail.setFrom(dbConf.getFromEmail());
            mail.setHtml(content);
            // mail.setText(JSoup.gettextthingy) TODO: extract plain text using jsoup
            client.sendMail(mail, result -> {
                if (result.succeeded()) {
                    LOG.info("Successfully sent email " + result.result().getMessageID() + " to " + to);
                } else {
                    LOG.error("Failed to send email" + mailName + " to " + mail.getTo(), result.cause());
                }
            });
        });
    }

    private Map<String, String> getTranslationsMap(final String mailName, final Locale locale) throws IOException {
        final Properties props = new Properties();
        props.load(new FileReader(
                Paths.get(dbConf.getTemplateLookupFolder(), "i18n", locale.getISO3Language(), mailName).toFile()));
        final Map<String, String> transMap = new HashMap<String, String>();
        props.entrySet().forEach((entry) -> {
            transMap.put((String) entry.getKey(), (String) entry.getValue());
        });
        return transMap;
    }

    private String replaceValues(final String content, final Map<String, Object> arguments, final Locale loc) {
        return Pattern.compile("\\{\\{ ([a-z0-9_]+) }}").matcher(content).replaceAll((replacement) -> {
            return getStringRepresentation(arguments.get(replacement.group()), loc);
        });
    }

    private String getStringRepresentation(final Object obj, final Locale loc) {
        if (obj instanceof String) {
            return (String) obj;
        } else if (obj instanceof Date) {
            return DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL, loc).format(obj);
        } else {
            if (obj == null) {
                return null;
            } else {
                return obj.toString();
            }
        }
    }

    private String getRawEmail(String mailName) throws IOException {
        return Files.readString(Paths.get(dbConf.getTemplateLookupFolder(), mailName + ".html"));
    }
}
