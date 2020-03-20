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

import chat.squirrel.metrics.UniformCalculator;
import io.vertx.core.json.JsonObject;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestMetrics {
    private final double[] sample = new double[]{978, 76, 0, 928, 117, 783, 560, 179, 186, 633, 817, 412, 366, 68,
            594, 604, 373, 717, 903, 5};

    @Test
    public void testUniformCalculator() {
        final UniformCalculator calc = new UniformCalculator(sample);
        System.out.println(JsonObject.mapFrom(calc).encodePrettily());

        assertEquals("Max", 978, calc.getMax(), 0.0);
        assertEquals("Min", 0.0, calc.getMin(), 0.0);
        assertEquals("Stdev", 330.047, calc.getStdDev(), 0.1);
        assertEquals("Mean", 464.95, calc.getMean(), 0.001);
        assertEquals("25% quartile", 163.5, calc.getQuantile(0.25), 0.1);
        assertEquals("75% quartile", 733.5, calc.getQuantile(0.75), 0.1);
        assertEquals("Media", 486, calc.getMedian(), 0.1);
    }

}
