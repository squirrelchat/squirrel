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

package chat.squirrel.test.utils;

import chat.squirrel.utils.Sanitizer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SanitizerTest {
    @Test
    void testValidUsername() {
        final String username = "Beauwzerre";
        assertEquals(username, Sanitizer.sanitize(username));
    }

    @Test
    void testGrossUsername() {
        final String username = "Be\u202Dauwze\u200Brre";
        final String expectedUsername = "Beauwzerre";
        assertEquals(expectedUsername, Sanitizer.sanitize(username));
    }

    @Test
    void testValidMessage() {
        final String message = "I'd just like to interject for a moment. What you're referring to as Coronavirus, is in fact, SARS-CoV-2, or as I've recently taken to calling it, COVID-19.\n" +
                "Coronavirus is not a virus unto itself, but rather a family of viruses including several strains which make use of the Coronavirus core traits and symptoms comprising a full virus as defined by WHO.";
        assertEquals(message, Sanitizer.sanitize(message, true));
    }

    @Test
    void testGrossMessage() {
        final String message = "I'd just like to interject for a moment.\u202D What you're referring to as Coronavirus, is in fact, SARS-CoV-2, or as I've recently taken to calling it, COVID-19.\n" +
                "Coronavirus is not a virus unto itself, but rather a family of viruses including several strains which make use of the Coronavirus core traits and symptoms comprising a full virus as defined by WHO.";
        final String expectedMessage = "I'd just like to interject for a moment. What you're referring to as Coronavirus, is in fact, SARS-CoV-2, or as I've recently taken to calling it, COVID-19.\n" +
                "Coronavirus is not a virus unto itself, but rather a family of viruses including several strains which make use of the Coronavirus core traits and symptoms comprising a full virus as defined by WHO.";
        assertEquals(expectedMessage, Sanitizer.sanitize(message, true));
    }

    @Test
    void testMessageZws() {
        final String message = "I'd just like to interject\u200B for a moment. What you're referring to as Coronavirus, is in fact, SARS-CoV-2, or as I've recently taken to calling it, COVID-19.\n" +
                "Coronavirus is not a virus unto itself, but rather a family of viruses including several strains which make use of the Coronavirus core traits and symptoms comprising a full virus as defined by WHO.";
        assertEquals(message, Sanitizer.sanitize(message, true));
    }
}
