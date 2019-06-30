/*
 * Copyright © 2018 Ondrej Fischer. All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that
 * the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the
 *    following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the
 *    following disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * 3. The name of the author may not be used to endorse or promote products derived from this software without
 *     specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY [LICENSOR] "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE
 * GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 */

package fluent.validation;

import java.math.BigDecimal;

import static fluent.validation.BasicChecks.check;
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
public final class NumericChecks {
    private NumericChecks() {}

    private static final Double DEFAULT_TOLERANCE = 0.0000001;

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
        return closeTo(expectedValue, DEFAULT_TOLERANCE);
    }

    public static Check<Float> equalTo(Float expectedValue) {
        return closeTo(expectedValue, DEFAULT_TOLERANCE.floatValue());
    }

    public static Check<BigDecimal> equalTo(BigDecimal expectedValue) {
        return closeTo(expectedValue, BigDecimal.valueOf(DEFAULT_TOLERANCE));
    }

}
