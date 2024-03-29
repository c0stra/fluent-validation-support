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
import java.math.BigInteger;

import static fluent.validation.BasicChecks.*;
import static fluent.validation.Transformation.dontTransformNull;
import static java.lang.Math.abs;

/**
 * Factory of ready to use most frequent checks used for validation of numbers.
 * The main groups are:
 *   1. Validation of floating point numbers, where FP precision needs to be taken into account
 *   2. Validation of invalid values (NaN)
 *   3. Validation of strings containing numbers using respective parse* methods.
 */
@Factory
public final class NumericChecks {

    private static final Double DEFAULT_TOLERANCE = Double.parseDouble(System.getProperty("check.default.tolerance", "0.000001"));

    private NumericChecks() {}

    /* ------------------------------------------------------------------------------------------------------
     * Numeric conditions.
     * ------------------------------------------------------------------------------------------------------
     */

    /**
     * Check, that a double number is close enough to the expected value, given the explicit tolerance.
     * This is the standard approach to validation of floating point number representations, because due to
     * the internal representation in the CPU/FPU, numbers coming from e.g. different calculation, but which
     * would have the same real value, can differ in the CPU/FPU representation.
     * Simplest example could be: 1/3 vs. (1 - 2/3). Results of these formulas will differ in computer representation,
     * although in real math they are equal.
     *
     * @param expectedValue Expected value.
     * @param precision Tolerance, which is still accepted as difference from the expected value.
     * @return Check of the double value.
     */
    public static Check<Double> closeTo(double expectedValue, double precision) {
        return check(data -> abs(expectedValue - data) < precision, "<" + expectedValue + " ±" + precision + ">");
    }

    /**
     * Check, that a float number is close enough to the expected value, given the explicit tolerance.
     * This is the standard approach to validation of floating point number representations, because due to
     * the internal representation in the CPU/FPU, numbers coming from e.g. different calculation, but which
     * would have the same real value, can differ in the CPU/FPU representation.
     * Simplest example could be: 1/3 vs. (1 - 2/3). Results of these formulas will differ in computer representation,
     * although in real math they are equal.
     *
     * @param expectedValue Expected value.
     * @param precision Tolerance, which is still accepted as difference from the expected value.
     * @return Check of the double value.
     */
    public static Check<Float> closeTo(float expectedValue, float precision) {
        return check(data -> abs(expectedValue - data) < precision, expectedValue + " ±" + precision);
    }

    /**
     * Check, that a BigDecimal number is close enough to the expected value, given the explicit tolerance.
     * This is the standard approach to validation of floating point number representations, because due to
     * the internal representation in the CPU/FPU, numbers coming from e.g. different calculation, but which
     * would have the same real value, can differ in the CPU/FPU representation.
     * Simplest example could be: 1/3 vs. (1 - 2/3). Results of these formulas will differ in computer representation,
     * although in real math they are equal.
     *
     * @param expectedValue Expected value.
     * @param precision Tolerance, which is still accepted as difference from the expected value.
     * @return Check of the double value.
     */
    public static Check<BigDecimal> closeTo(BigDecimal expectedValue, BigDecimal precision) {
        return check(data -> expectedValue.subtract(data).abs().compareTo(precision) < 0, expectedValue + " ±" + precision);
    }

    /**
     * Specific overloaded method for "tolerant" equalTo implementation for Double floating point values.
     * It's implemented using closeTo() with default tolerance, which is by default 0.000001, but can be specified
     * via system property to different value.
     *
     * @param expectedValue Expected value, from which the actual shouldn't differ more, than by the default tolerance.
     * @return Check of the double equality.
     * @see #closeTo(double, double)
     * @see #DEFAULT_TOLERANCE
     */
    public static Check<Double> equalTo(Double expectedValue) {
        return expectedValue == null ? sameInstance(null) : closeTo(expectedValue, DEFAULT_TOLERANCE);
    }

    /**
     * Specific overloaded method for "tolerant" equalTo implementation for Float floating point values.
     * It's implemented using closeTo() with default tolerance, which is by default 0.000001, but can be specified
     * via system property to different value.
     *
     * @param expectedValue Expected value, from which the actual shouldn't differ more, than by the default tolerance.
     * @return Check of the float equality.
     * @see #closeTo(float, float)
     * @see #DEFAULT_TOLERANCE
     */
    public static Check<Float> equalTo(Float expectedValue) {
        return expectedValue == null ? sameInstance(null) : closeTo(expectedValue, DEFAULT_TOLERANCE.floatValue());
    }

    /**
     * Specific overloaded method for "tolerant" equalTo implementation for BigDecimal real number values.
     * It's implemented using closeTo() with default tolerance, which is by default 0.000001, but can be specified
     * via system property to different value.
     *
     * @param expectedValue Expected value, from which the actual shouldn't differ more, than by the default tolerance.
     * @return Check of the BigDecimal equality.
     * @see #closeTo(BigDecimal, BigDecimal)
     * @see #DEFAULT_TOLERANCE
     */
    public static Check<BigDecimal> equalTo(BigDecimal expectedValue) {
        return expectedValue == null ? sameInstance(null) : closeTo(expectedValue, BigDecimal.valueOf(DEFAULT_TOLERANCE));
    }

    /**
     * Check of the string containing double number implemented by composition of parsing double from string and
     * application of check of the Double value.
     *
     * @param check Check of the double value.
     * @return Check applicable on String.
     */
    public static Check<String> parseDouble(Check<? super Double> check) {
        return compose(dontTransformNull(Double::parseDouble), check);
    }

    /**
     * Check of the string containing double number implemented by composition of parsing double from string and
     * expected Double value.
     *
     * @param expectedValue Expected double value.
     * @return Check applicable on String.
     */
    public static Check<String> parseDouble(Double expectedValue) {
        return parseDouble(equalTo(expectedValue));
    }

    /**
     * Check of the string containing double number implemented by composition of parsing double from string and
     * expected Double value.
     *
     * @param expectedValue Expected double value.
     * @return Check applicable on String.
     */
    public static Check<String> parseDouble(double expectedValue) {
        return parseDouble(equalTo(expectedValue));
    }

    public static Check<String> parseFloat(Check<? super Float> check) {
        return compose(dontTransformNull(Float::parseFloat), check);
    }

    public static Check<String> parseFloat(Float expectedValue) {
        return parseFloat(equalTo(expectedValue));
    }

    public static Check<String> parseFloat(float expectedValue) {
        return parseFloat(equalTo(expectedValue));
    }

    public static Check<String> parseInt(Check<? super Integer> check) {
        return compose(dontTransformNull(Integer::parseInt), check);
    }

    public static Check<String> parseInt(Integer expectedValue) {
        return parseInt(BasicChecks.equalTo(expectedValue));
    }

    public static Check<String> parseInt(int expectedValue) {
        return parseInt(BasicChecks.equalTo(expectedValue));
    }

    public static Check<String> parseLong(Check<? super Long> check) {
        return compose(dontTransformNull(Long::parseLong), check);
    }

    public static Check<String> parseLong(Long expectedValue) {
        return parseLong(BasicChecks.equalTo(expectedValue));
    }

    public static Check<String> parseLong(long expectedValue) {
        return parseLong(BasicChecks.equalTo(expectedValue));
    }

    public static Check<String> parseShort(Check<? super Short> check) {
        return compose(dontTransformNull(Short::parseShort), check);
    }

    public static Check<String> parseShort(Short expectedValue) {
        return parseShort(BasicChecks.equalTo(expectedValue));
    }

    public static Check<String> parseShort(short expectedValue) {
        return parseShort(BasicChecks.equalTo(expectedValue));
    }

    public static Check<String> parseBigDecimal(Check<? super BigDecimal> check) {
        return compose(dontTransformNull(BigDecimal::new), check);
    }

    public static Check<String> parseBigInt(Check<? super BigInteger> check) {
        return compose(dontTransformNull(BigInteger::new), check);
    }

    public static Check<String> parseByte(Check<? super Byte> check) {
        return compose(dontTransformNull(Byte::parseByte), check);
    }

    public static Check<String> parseByte(Byte expectedValue) {
        return parseByte(BasicChecks.equalTo(expectedValue));
    }

    public static Check<String> parseByte(byte expectedValue) {
        return parseByte(BasicChecks.equalTo(expectedValue));
    }

    public static Check<String> parseBoolean(Check<? super Boolean> check) {
        return compose(dontTransformNull(Boolean::parseBoolean), check);
    }

    public static Check<String> parseBoolean(Boolean expectedValue) {
        return parseBoolean(BasicChecks.equalTo(expectedValue));
    }

}
