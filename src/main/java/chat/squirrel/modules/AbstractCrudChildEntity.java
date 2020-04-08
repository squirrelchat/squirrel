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
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.RoutingContext;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletionStage;

public abstract class AbstractCrudChildEntity<T extends IEntity, P extends IEntity> extends AbstractCrudModule<T> {
    private final ICollection<P> parentCollection;
    private final String foreignField;
    private final String routeParameter;

    protected AbstractCrudChildEntity(final ICollection<T> collection, final ICollection<P> parentCollection, final String foreignField, final String routeParameter) {
        super(collection);
        this.parentCollection = parentCollection;
        this.foreignField = foreignField;
        this.routeParameter = routeParameter;
    }

    @Override
    protected void registerCrud(final String route) {
        super.registerCrud(route);
        this.registerAuthedRoute(HttpMethod.GET, route, this::handleReadAll);
    }

    @Override
    protected void handleCreate(final RoutingContext ctx) {
        getParentEntity(ctx).thenAccept(parentEntity -> {
            if (parentEntity == null) {
                this.end(ctx, 404, "Not Found", null);
                return;
            }
            ctx.put("parent", parentEntity);
            super.handleCreate(ctx);
        });
    }

    @Override
    protected void handleRead(final RoutingContext ctx) {
        getParentEntity(ctx).thenAccept(parentEntity -> {
            if (parentEntity == null) {
                this.end(ctx, 404, "Not Found", null);
                return;
            }
            ctx.put("parent", parentEntity);
            super.handleRead(ctx);
        });
    }

    protected void handleReadAll(final RoutingContext ctx) {
        getParentEntity(ctx).thenAccept(parentEntity -> {
            if (parentEntity == null) {
                this.end(ctx, 404, "Not Found", null);
                return;
            }
            ctx.put("parent", parentEntity);
            getEntities(ctx).thenAccept(entities ->
                    ctx.response().end(new JsonArray(List.copyOf(entities)).toBuffer())
            );
        });
    }

    @Override
    protected void handleUpdate(final RoutingContext ctx) {
        getParentEntity(ctx).thenAccept(parentEntity -> {
            if (parentEntity == null) {
                this.end(ctx, 404, "Not Found", null);
                return;
            }
            ctx.put("parent", parentEntity);
            super.handleUpdate(ctx);
        });
    }

    @Override
    protected void handleDelete(final RoutingContext ctx) {
        getParentEntity(ctx).thenAccept(parentEntity -> {
            if (parentEntity == null) {
                this.end(ctx, 404, "Not Found", null);
                return;
            }
            ctx.put("parent", parentEntity);
            super.handleDelete(ctx);
        });
    }

    protected Bson composeParentQuery(final RoutingContext ctx) {
        return Filters.eq(new ObjectId(ctx.pathParam(routeParameter)));
    }

    protected CompletionStage<P> getParentEntity(final RoutingContext ctx) {
        return parentCollection.findEntity(composeParentQuery(ctx));
    }

    @Override
    protected Bson composeQuery(final RoutingContext ctx) {
        return Filters.and(
                Filters.eq(new ObjectId(ctx.pathParam("id"))),
                Filters.eq(foreignField, new ObjectId(ctx.pathParam(routeParameter)))
        );
    }

    protected Bson composeQueryAll(final RoutingContext ctx) {
        // TODO: Pagination
        return Filters.eq(foreignField, new ObjectId(ctx.pathParam(routeParameter)));
    }

    protected CompletionStage<Collection<T>> getEntities(final RoutingContext ctx) {
        return getCollection().findEntities(composeQueryAll(ctx));
    }

    protected ICollection<P> getParentCollection() {
        return parentCollection;
    }
}
