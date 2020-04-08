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

package chat.squirrel.modules;

import chat.squirrel.database.collections.ICollection;
import chat.squirrel.database.entities.IEntity;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.RoutingContext;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.concurrent.CompletionStage;

public abstract class AbstractCrudModule<T extends IEntity> extends AbstractModule {
    private final ICollection<T> collection;

    protected AbstractCrudModule(final ICollection<T> collection) {
        this.collection = collection;
    }

    protected void registerCrud(final String route) {
        this.registerAuthedRoute(HttpMethod.POST, route, this::handleCreate);
        this.registerAuthedRoute(HttpMethod.GET, route + "/:id", this::handleRead);
        this.registerAuthedRoute(HttpMethod.PATCH, route + "/:id", this::handleUpdate);
        this.registerAuthedRoute(HttpMethod.DELETE, route + "/:id", this::handleDelete);
    }

    protected void handleCreate(final RoutingContext ctx) {
        if (!hasPermission(ctx, CrudContext.CREATE)) {
            this.end(ctx, 403, "Insufficient permissions", null);
            return;
        }

        final T entity = createEntity(ctx);
        if (entity == null) {
            this.end(ctx, 400, "Invalid or malformed payload", null);
            return;
        }
        insertEntity(entity).thenAccept(r -> {
            ctx.response().setStatusCode(201);
            ctx.response().end();
        });
    }

    protected void handleRead(final RoutingContext ctx) {
        getEntity(ctx).thenAccept(entity -> {
            if (entity == null) {
                this.end(ctx, 404, "Not Found", null);
                return;
            }
            ctx.put("entity", entity);
            if (!hasPermission(ctx, CrudContext.READ)) {
                this.end(ctx, 403, "Insufficient permissions", null);
                return;
            }
            ctx.response().end(entity.toJson().toBuffer());
        });
    }

    protected void handleUpdate(final RoutingContext ctx) {
        getEntity(ctx).thenAccept(entity -> {
            if (entity == null) {
                this.end(ctx, 404, "Not Found", null);
                return;
            }
            ctx.put("entity", entity);
            if (!hasPermission(ctx, CrudContext.UPDATE)) {
                this.end(ctx, 403, "Insufficient permissions", null);
                return;
            }
            final Bson update = composeUpdate(ctx);
            if (update == null) {
                this.end(ctx, 400, "Invalid or malformed payload", null);
                return;
            }
            updateEntity(ctx, update).thenAccept(updated -> ctx.response().end(updated.toJson().toBuffer()));
        });
    }

    protected void handleDelete(final RoutingContext ctx) {
        getEntity(ctx).thenAccept(entity -> {
            if (entity == null) {
                this.end(ctx, 404, "Not Found", null);
                return;
            }
            ctx.put("entity", entity);
            if (!hasPermission(ctx, CrudContext.DELETE)) {
                this.end(ctx, 403, "Insufficient permissions", null);
                return;
            }
            deleteEntity(ctx).thenAccept(updated -> ctx.response().setStatusCode(204).end());
        });
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    protected abstract boolean hasPermission(final RoutingContext ctx, final CrudContext context);

    protected Bson composeQuery(final RoutingContext ctx) {
        return Filters.eq(new ObjectId(ctx.pathParam("id")));
    }

    protected abstract T createEntity(final RoutingContext ctx);

    protected CompletionStage<InsertOneResult> insertEntity(final T entity) {
        return collection.insertOne(entity);
    }

    protected CompletionStage<T> getEntity(final RoutingContext ctx) {
        return collection.findEntity(composeQuery(ctx));
    }

    protected abstract Bson composeUpdate(final RoutingContext ctx);

    protected CompletionStage<T> updateEntity(final RoutingContext ctx, final Bson query) {
        return collection.findAndUpdateEntity(composeQuery(ctx), query);
    }

    protected CompletionStage<DeleteResult> deleteEntity(final RoutingContext ctx) {
        return collection.deleteEntity(composeQuery(ctx));
    }

    protected ICollection<T> getCollection() {
        return collection;
    }

    protected enum CrudContext {
        CREATE, READ_ALL, READ, UPDATE, DELETE
    }
}
