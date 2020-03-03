/*
 * Copyright (c) 2020-present Bowser65 & vinceh121, All rights reserved.
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

package chat.squirrel.entities.impl;

import java.util.Collection;
import java.util.Collections;

import javax.annotation.Nonnull;

import chat.squirrel.entities.AbstractEntity;
import chat.squirrel.entities.Guild;
import chat.squirrel.entities.Role;

/**
 * A standard {@link Guild} Role
 */
public class RoleImpl extends AbstractEntity implements Role {
    private int color;
    private String name;
    private Collection<String> permissions = Collections.emptySet();

    /**
     * @return The RGB color corresponding to this role
     */
    @Override
    public int getColor() {
        return this.color;
    }

    /**
     * @return The display name of this Role
     */
    @Override
    public String getName() {
        return this.name;
    }

    @Override
    @Nonnull
    public Collection<String> getPermissions() {
        return this.permissions;
    }

    /**
     * @param color The RGB color corresponding to this role
     */
    @Override
    public void setColor(final int color) {
        this.color = color;
    }

    /**
     * @param name The display name of this Role
     */
    @Override
    public void setName(final String name) {
        this.name = name;
    }

    @Override
    public void setPermissions(@Nonnull final Collection<String> permissions) {
        this.permissions = permissions;
    }
}
