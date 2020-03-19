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

package chat.squirrel.mail;

import io.vertx.core.Vertx;
import io.vertx.ext.mail.MailClient;
import io.vertx.ext.mail.MailConfig;
import io.vertx.ext.mail.MailMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

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

        this.client = MailClient.create(vertx, config);
        this.enabled = true;
    }

    public void sendMail(final String mailName, final String[] to, final Locale locale,
                         final Map<String, Object> extras) {
        if (!this.enabled) {
            LOG.warn("Attempted to send email %s to %s but notification mail manager isn't enabled.");
            return;
        }
        this.exec.submit(() -> {
            LOG.info("Sending email %s in language %s to email %s", mailName, locale.getISO3Language(), to);
            final String raw;
            final String rawPlain;
            try {
                raw = this.getRawEmail(mailName);
                rawPlain = this.getRawEmailPlain(mailName);
            } catch (final IOException e) {
                LOG.error("Failed to read email template", e);
                return;
            }
            final Map<String, Object> arguments;
            try {
                arguments = new HashMap<>(this.getTranslationsMap(mailName, locale));
            } catch (final IOException e) {
                LOG.error("Failed to read email translations for template " + mailName + " in language "
                        + locale.getISO3Language(), e);
                return;
            }
            arguments.putAll(extras);

            final String content = this.replaceValues(raw, arguments, locale);
            final String contentPlain = this.replaceValues(rawPlain, arguments, locale);

            final MailMessage mail = new MailMessage();
            mail.setTo(Arrays.asList(to));
            mail.setFrom(this.dbConf.getFromEmail());
            mail.setHtml(content);
            mail.setText(contentPlain);
            this.client.sendMail(mail, result -> {
                if (result.succeeded()) {
                    LOG.info("Successfully sent email " + result.result().getMessageID() + " to " + Arrays.toString(to));
                } else {
                    LOG.error("Failed to send email" + mailName + " to " + mail.getTo(), result.cause());
                }
            });
        });
    }

    private Map<String, String> getTranslationsMap(final String mailName, final Locale locale) throws IOException {
        final Properties props = new Properties();
        props.load(new FileReader(
                Paths.get(this.dbConf.getTemplateLookupFolder(), "i18n", locale.getISO3Language(), mailName).toFile()));
        final Map<String, String> transMap = new HashMap<>();
        props.forEach((key, value) -> transMap.put((String) key, (String) value));
        return transMap;
    }

    private String replaceValues(final String content, final Map<String, Object> arguments, final Locale loc) {
        return Pattern.compile("\\{\\{ ([a-z0-9_]+) }}").matcher(content)
                .replaceAll((replacement) -> this.getStringRepresentation(arguments.get(replacement.group()), loc));
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

    private String getRawEmail(final String mailName) throws IOException {
        return Files.readString(Paths.get(this.dbConf.getTemplateLookupFolder(), mailName + ".html"));
    }

    private String getRawEmailPlain(final String mailName) throws IOException {
        return Files.readString(Paths.get(this.dbConf.getTemplateLookupFolder(), mailName + ".txt"));
    }
}
