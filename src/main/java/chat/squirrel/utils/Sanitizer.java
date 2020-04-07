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

package chat.squirrel.utils;

import java.util.regex.Pattern;

public class Sanitizer {
    private static final Pattern BLACKLISTED = Pattern.compile("[\u202D\u202E]", Pattern.CASE_INSENSITIVE);
    private static final Pattern ZERO_WIDTH_SPACE = Pattern.compile("[\u200B\u200C\u200D\u2060\u180E]", Pattern.CASE_INSENSITIVE);

    /**
     * Sanitizes a string without allowing zero width spaces (ZWS).
     *
     * @param string String to sanitize
     * @return Sanitized string.
     * @see Sanitizer#sanitize(String, boolean)
     */
    public static String sanitize(final String string) {
        return sanitize(string, false);
    }

    /**
     * Sanitizes a string.
     *
     * @param string   String to sanitize
     * @param allowZws Whether zero width spaces (ZWS) should be allowed or not.
     * @return Sanitized string.
     */
    public static String sanitize(final String string, final boolean allowZws) {
        final String sanitized = BLACKLISTED.matcher(string).replaceAll("");
        if (!allowZws) {
            return ZERO_WIDTH_SPACE.matcher(sanitized).replaceAll("");
        }
        return sanitized;
    }
}
