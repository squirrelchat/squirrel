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

package chat.squirrel.entities;

import java.util.Collection;

/**
 * A basic Guild
 */
public class Guild extends AbstractEntity {
    private String name;
    private Collection<Member> members;
    private Collection<Role> roles;

    /**
     * @return The display name of the Guild
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The display name of the Guild
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return The {@link Member}s that are apart of this Guild
     */
    public Collection<Member> getMembers() {
        return members;
    }

    /**
     * @param members The {@link Member}s that are apart of this Guild
     */
    public void setMembers(Collection<Member> members) {
        this.members = members;
    }

    /**
     * @return The {@link Role}s that are created in this Guild
     */
    public Collection<Role> getRoles() {
        return roles;
    }

    /**
     * @param roles The {@link Role}s that are created in this Guild
     */
    public void setRoles(Collection<Role> roles) {
        this.roles = roles;
    }

    /**
     * Guild permissions
     */
    public enum Permissions {
        /**
         * Allow Member to change own nickname
         */
        CHANGE_NICKNAME,
        /**
         * Ban {@link Member}s from the Guild
         */
        BAN,
        /**
         * Kick {@link Member} from this Guild
         */
        KICK,
        /**
         * TODO i forgot what this is supposed to be
         */
        ROLE_IMMUNITY,
        /**
         * Manage the Guild's Channels
         */
        MANAGE_CHANNELS
    }
}
