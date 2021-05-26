/*
 * BSD 2-Clause License
 *
 * Copyright (c) 2021, Ondrej Fischer
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package fluent.validation;

import fluent.validation.processor.Factory;

import java.math.BigDecimal;

import static fluent.validation.BasicChecks.check;
import static fluent.validation.BasicChecks.sameInstance;
import static java.lang.Double.parseDouble;
import static java.lang.Math.abs;

/**
 * Factory of ready to use most frequent conditions. There are typical conditions for following categories:
 *
 * 1. General check builders for simple building of new conditions using predicate and description.
 * 2. General object conditions - e.g. isNull, notNull, equalTo, etc.
 * 3. Generalized logical operators (N-ary oneOf instead of binary or, N-ary allOf instead of binary and)
 * 4. Collection (Iterable) conditions + quantifiers
 * 5. Relational and range conditions for comparables
 * 6. String matching conditions (contains/startWith/endsWith as well as regexp matching)
 * 7. Basic XML conditions (XPath, attribute matching)
 * 8. Floating point comparison using a tolerance
 * 9. Builders for composition or collection of criteria.
 */
@Factory
public final class NumericChecks {

    private static final Double DEFAULT_TOLERANCE = parseDouble(System.getProperty("check.default.tolerance", "0.000001"));

    private NumericChecks() {}

    /* ------------------------------------------------------------------------------------------------------
     * Numeric conditions.
     * ------------------------------------------------------------------------------------------------------
     */

    public static Check<Double> closeTo(double operand, double precision) {
        return check(data -> abs(operand - data) < precision, "<" + operand + " ±" + precision + ">");
    }

    public static Check<Float> closeTo(float operand, float precision) {
        return check(data -> abs(operand - data) < precision, operand + " ±" + precision);
    }

    public static Check<BigDecimal> closeTo(BigDecimal operand, BigDecimal precision) {
        return check(data -> operand.subtract(data).abs().compareTo(precision) < 0, operand + " ±" + precision);
    }

    public static Check<Double> equalTo(Double expectedValue) {
        return expectedValue == null ? sameInstance(null) : closeTo(expectedValue, DEFAULT_TOLERANCE);
    }

    public static Check<Float> equalTo(Float expectedValue) {
        return expectedValue == null ? sameInstance(null) : closeTo(expectedValue, DEFAULT_TOLERANCE.floatValue());
    }

    public static Check<BigDecimal> equalTo(BigDecimal expectedValue) {
        return expectedValue == null ? sameInstance(null) : closeTo(expectedValue, BigDecimal.valueOf(DEFAULT_TOLERANCE));
    }

}
