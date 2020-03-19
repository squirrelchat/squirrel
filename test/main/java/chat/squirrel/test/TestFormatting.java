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

package chat.squirrel.test;

import chat.squirrel.Squirrel;
import chat.squirrel.auth.MongoAuthHandler;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestFormatting {

    @Test
    public void testDiscriminator() {
        assertEquals("0069", Squirrel.formatDiscriminator(69));
        assertEquals("0420", Squirrel.formatDiscriminator(420));
        assertEquals("0121", Squirrel.formatDiscriminator(121));
        assertEquals("0000", Squirrel.formatDiscriminator(0));
        assertEquals("0001", Squirrel.formatDiscriminator(1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDiscriminatorFail() {
        Squirrel.formatDiscriminator(99999);
    }

    @Test
    public void testUsernames() {
        assertTrue(MongoAuthHandler.isValidUsername("Charles Hatant"));
        assertFalse(MongoAuthHandler.isValidUsername("    owo spaces \t"));
        assertFalse(MongoAuthHandler.isValidUsername("\bo\n\t\rols"));
        assertFalse(MongoAuthHandler.isValidUsername("hayyaya#0003"));
        assertFalse(MongoAuthHandler.isValidUsername("line\nb\reak"));
    }

}
