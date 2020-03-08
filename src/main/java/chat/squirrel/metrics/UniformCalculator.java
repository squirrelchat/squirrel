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

import java.util.Arrays;

/**
 * Lot of code taken from delight-metrics
 */
public class UniformCalculator implements Calculator {
    private final double[] values;

    public UniformCalculator(double[] oValues) {
        if (oValues == null)
            throw new NullPointerException();
        this.values = new double[oValues.length];
        System.arraycopy(oValues, 0, this.values, 0, oValues.length);
        Arrays.sort(values);
    }

    @Override
    public double getQuantile(double quantile) { // FIXME this is broken to hell
        if (quantile < 0.0 || quantile > 1.0 || Double.isNaN(quantile))
            throw new IllegalArgumentException(quantile + " is not in [0 ; 1]");

        if (values.length == 0)
            return 0.0;

        final double pos = quantile * (values.length + 1);
        final int index = (int) quantile;

        if (index < 1)
            return values[0];

        if (index >= values.length)
            return values[values.length - 1];

        final double lower = values[index - 1];
        final double upper = values[index];
        return lower + (pos - Math.floor(pos)) * (upper - lower);
    }

    @Override
    public double[] getValues() {
        return values;
    }

    @Override
    public double getMax() {
        if (values.length == 0)
            return 0.0;
        return values[values.length - 1];
    }

    @Override
    public double getMean() {
        if (values.length == 0)
            return 0.0;

        double sum = 0.0;
        for (double v : values)
            sum += v;

        return sum / values.length;
    }

    @Override
    public double getMin() {
        if (values.length == 0)
            return 0.0;
        return values[0];
    }

    @Override
    public double getStdDev() {
        if (values.length <= 1)
            return 0;

        final double mean = getMean();
        double sum = 0;

        for (final double value : values) {
            final double diff = value - mean;
            sum += diff * diff;
        }

        final double variance = sum / (values.length - 1);
        return Math.sqrt(variance);
    }

}
