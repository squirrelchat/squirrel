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

package chat.squirrel.modules.users;

import chat.squirrel.modules.AbstractModule;
import io.vertx.core.http.HttpMethod;

public class ModuleRelationship extends AbstractModule {
    @Override
    public void initialize() {
        /*
         * @todo: Write actual docs Let 2 users, User A (id 1) and User B (id 2)
         *
         * User A: POST /users/self/relationships with as body { username: 'User B',
         * discriminator: '0001' } - There is no type, so we assume it's a friend
         * request. - Type 0: Friend Request - Type 1: Block member
         *
         * Blocking immediately returns 204 (overrides friend) - 404 response code if
         * target doesn't exist. - 400 response code if malformed request/target is self
         *
         * User A is able to add User B as a friend according to privacy settings,
         * aren't friend and one doesn't have the other one blocked: - Gateway event to
         * User B, 202 response code to User A. - Else, 403 response code. - 404
         * response code if target doesn't exist. - 400 response code if malformed
         * request/target is self or a bot.
         *
         * User B: POST /users/self/relationships/1 (without body) - If there is a
         * pending friend request, it'll get accepted, User A will receive a gateway
         * event and User B gets a 204 status code. - Else, 400 status code.
         *
         * User A: DELETE /users/self/relationships/2 - If there is a pending request to
         * User B, it'll be deleted. - If User B is a friend of User A or is blocked by
         * User A, relationship will be removed. - Else, 400 status code.
         */
        this.registerAuthedRoute(HttpMethod.POST, "/users/self/relationships", this::notImplemented);
        this.registerAuthedRoute(HttpMethod.POST, "/users/self/relationships/:id", this::notImplemented);
        this.registerAuthedRoute(HttpMethod.DELETE, "/users/self/relationships/:id", this::notImplemented);
    }
}
