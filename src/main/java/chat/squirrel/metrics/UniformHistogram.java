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

package chat.squirrel.metrics;

import java.util.Random;

import org.bson.codecs.pojo.annotations.BsonIgnore;

/**
 * Inspired from delight's UniformReservoir.
 * 
 * @see <a href=
 *      "https://github.com/javadelight/delight-metrics/blob/cdeb9513b42a930b58cf2930da2d3be8e8e5f6a9/src/main/java/com/codahale/metrics/UniformReservoir.java">delight-metrics
 *      UniformReservoir</a>
 */
public class UniformHistogram extends AbstractHistogram implements Histogram {
    private int count = 0;
    private final double[] values;
    private final Random rng;

    public UniformHistogram() {
        this(null);
    }

    public UniformHistogram(String name) {
        this(name, 1024);
    }

    public UniformHistogram(String name, int size) {
        this(name, size, new Random());
    }

    public UniformHistogram(String name, int size, Random rng) {
        super(name);
        this.values = new double[size];
        this.rng = rng;
    }

    @Override
    public void addValue(double value) {
        count++;
        if (count <= values.length) {
            values[count - 1] = value;
        } else {
            final double r = nextDouble(count);
            if (r < values.length) {
                values[(int) r] = value;
            }
        }
    }

    @Override
    public void addValue(long value) {
        this.addValue((double) value); // XXX is this a good idea?
    }

    @Override
    public int size() {
        final int c = count;
        if (c > values.length) {
            return values.length;
        }
        return c;
    }

    /**
     * Returns a random double
     * 
     * @param max the exclusive maximum
     * @return a random double between 0 (inclusive) and max (exclusive)
     */
    private double nextDouble(double max) {
        return rng.nextDouble() * max;
    }

    @Override
    @BsonIgnore
    public Calculator getCalculator() {
        return new UniformCalculator(values);
    }

}
