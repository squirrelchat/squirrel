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

package chat.squirrel.modules.auth;

import chat.squirrel.Squirrel;
import chat.squirrel.database.collections.ILoginAttemptCollection;
import chat.squirrel.database.collections.IUserCollection;
import chat.squirrel.database.entities.ILoginAttempt;
import chat.squirrel.database.entities.IUser;
import chat.squirrel.modules.AbstractModule;
import chat.squirrel.utils.Sanitizer;
import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import xyz.bowser65.tokenize.Token;
import xyz.bowser65.tokenize.Tokenize;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Module that handles logging in and registering
 */
public class ModuleLogin extends AbstractModule {
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            // RFC 5322 compliant
            "^(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9]))\\.){3}(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])$",
            Pattern.CASE_INSENSITIVE
    );
    private final Argon2 argon;

    public ModuleLogin() {
        this.argon = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2d);
    }

    @Override
    public void initialize() {
        this.registerRoute(HttpMethod.POST, "/auth/register", this::handleRegister);
        this.registerRoute(HttpMethod.POST, "/auth/login", this::handleLogin);
        this.registerRoute(HttpMethod.POST, "/auth/mfa/totp", this::notImplemented);
        this.registerRoute(HttpMethod.POST, "/auth/mfa/hardware", this::notImplemented);
    }

    private void handleLogin(final RoutingContext ctx) {
        final JsonObject obj = ctx.getBodyAsJson();
        if (obj == null || !obj.containsKey("username") || !obj.containsKey("password")) {
            this.end(ctx, 400, "Invalid or incomplete payload", null);
            return;
        }

        final boolean shouldDistinguish = Squirrel.getInstance().getConfig().isRegisterEnabled();
        Squirrel.getInstance().getDatabaseManager().getCollection(IUserCollection.class)
                .findByUsernameOrEmail(obj.getString("username"))
                .thenAccept(user -> {
                    if (user == null) {
                        this.end(ctx, 401, "Authentication failed", new JsonObject()
                                .put("error", shouldDistinguish
                                        ? FailureReason.INVALID_USERNAME
                                        : FailureReason.INVALID_CREDENTIALS)
                        );
                        return;
                    }

                    final ILoginAttempt attempt = ILoginAttempt.create();
                    final ILoginAttemptCollection attemptCollection = Squirrel.getInstance().getDatabaseManager()
                            .getCollection(ILoginAttemptCollection.class);

                    attempt.setUserId(user.getId());
                    if (Squirrel.getInstance().getConfig().isIpLogging()) {
                        attempt.setIpAddress(ctx.request().remoteAddress().host());
                        attempt.setApproximateLocation("Earth, Solar System, Milky Way"); // TODO: GeoIP
                    }

                    if (!this.argon.verify(user.getPassword(), obj.getString("password").getBytes(StandardCharsets.UTF_8))) {
                        attempt.setResult(ILoginAttempt.LoginAttemptResult.INVALID_PASSWORD);
                        this.end(ctx, 401, "Authentication failed", new JsonObject()
                                .put("error", shouldDistinguish
                                        ? FailureReason.INVALID_PASSWORD
                                        : FailureReason.INVALID_CREDENTIALS)
                        );
                        attemptCollection.insertOne(attempt);
                        return;
                    }

                    if (user.isBanned()) {
                        this.end(ctx, 403, "Banned", new JsonObject().put("error", FailureReason.BANNED));
                        return;
                    }

                    if (user.isDisabled() || user.getDeletionScheduledAt() != null) {
                        final JsonObject details = new JsonObject();
                        if (user.isDisabled()) {
                            details.put("error", FailureReason.DISABLED_ACCOUNT);
                        } else {
                            details.put("error", FailureReason.DELETION_SCHEDULED)
                                    .put("date", user.getDeletionScheduledAt().toInstant());
                        }
                        this.end(ctx, 200, "Authentication accepted", details);
                        return;
                    }

                    // TODO: Known IP check

                    final Token token = Squirrel.getInstance().getTokenize().generateToken(user);
                    final JsonObject details = new JsonObject().put("token", token);
                    if (user.isMfaMobile() || user.isMfaHardware()) {
                        if (user.isMfaMobile()) {
                            attempt.setResult(ILoginAttempt.LoginAttemptResult.TOTP_REQUIRED);
                            details.put("mfa_totp", true);
                        } else {
                            attempt.setResult(ILoginAttempt.LoginAttemptResult.HARDWARE_REQUIRED);
                            details.put("mfa_hardware", true);
                        }
                        this.end(ctx, 200, "Partial authentication", details);
                        attemptCollection.insertOne(attempt);
                        return;
                    }

                    attempt.setResult(ILoginAttempt.LoginAttemptResult.SUCCESS);
                    this.end(ctx, 200, "Authentication success", details);
                    attemptCollection.insertOne(attempt);
                });
    }

    private void handleRegister(final RoutingContext ctx) {
        if (!Squirrel.getInstance().getConfig().isRegisterEnabled()) {
            this.end(ctx, 403, "Registering is disabled on this instance", new JsonObject().put("error", FailureReason.REGISTRATION_DISABLED));
            return;
        }

        final JsonObject obj = ctx.getBodyAsJson();
        if (obj == null || !obj.containsKey("username") || !obj.containsKey("email") || !obj.containsKey("password")) {
            this.end(ctx, 400, "Invalid or incomplete payload", null);
            return;
        }

        final IUserCollection collection = Squirrel.getInstance().getDatabaseManager().getCollection(IUserCollection.class);
        // TODO: Check banned IP/email
        final String username = Sanitizer.sanitize(obj.getString("username"));
        if (username.length() < 2 || username.length() > 32) {
            this.end(ctx, 401, "Registration failed", new JsonObject().put("error", FailureReason.INVALID_USERNAME));
        }
        collection.isUsernameAvailable(username).thenAccept(available -> {
            if (!available) {
                this.end(ctx, 401, "Registration failed", new JsonObject().put("error", FailureReason.OVERUSED_USERNAME));
                return;
            }

            final String email = obj.getString("email");
            if (!EMAIL_PATTERN.matcher(email).matches()) {
                this.end(ctx, 401, "Registration failed", new JsonObject().put("error", FailureReason.INVALID_EMAIL));
                return;
            }

            collection.isEmailUsed(email).thenAccept(used -> {
                if (used) {
                    this.end(ctx, 401, "Registration failed", new JsonObject().put("error", FailureReason.EMAIL_TAKEN));
                    return;
                }

                collection.getFreeDiscriminator(username).thenAccept(discriminator -> {
                    final IUser user = IUser.create();
                    user.setUsername(username);
                    user.setDiscriminator(discriminator);
                    user.setEmail(email);
                    user.setPassword(this.argon.hash(3, 128000, 4, obj.getString("password").toCharArray()));
                    user.setTokensValidSince(Tokenize.currentTokenTime());
                    user.setTokens(Map.of("confirmation", "toooooken")); // TODO: Token + email validation
                    collection.insertOne(user);
                    this.end(ctx, 201, "Registration successful", new JsonObject()
                            .put("token", Squirrel.getInstance().getTokenize().generateToken(user)));
                });
            });
        });
    }

    public enum FailureReason {
        REGISTRATION_DISABLED,
        INVALID_EMAIL,
        EMAIL_TAKEN,
        OVERUSED_USERNAME,
        INVALID_USERNAME,
        INVALID_PASSWORD,
        INVALID_CREDENTIALS, // If registration is disabled
        UNKNOWN_IP_ADDRESS, // IP never logged in on that account
        DISABLED_ACCOUNT,
        DELETION_SCHEDULED,
        BANNED
    }
}
