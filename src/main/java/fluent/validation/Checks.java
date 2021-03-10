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
import java.util.*;

import static java.lang.Double.parseDouble;

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
public final class Checks {

    private Checks() {}

    private static final Double DEFAULT_TOLERANCE = parseDouble(System.getProperty("check.default.tolerance", "0.000001"));
    private static final Check<Object> IS_NULL = equalTo((Object) null);
    private static final Check<Object> NOT_NULL = not(IS_NULL);
    private static final Check<Object> ANYTHING = new Anything<>();

    /* ------------------------------------------------------------------------------------------------------
     * Simple check builders using predicate and description.
     * ------------------------------------------------------------------------------------------------------
     */

    /**
     * Define a transparent check using provided predicate and string expectation description.
     *
     * @param predicate Predicate used to test the supplied data.
     * @param expectationDescription Function, that provides description of the expectation.
     * @param <D> Type of the data to be tested by the created expectation.
     * @return New expectation.
     */
    public static <D> Check<D> nullableCondition(Predicate<D> predicate, String expectationDescription) {
        return BasicChecks.nullableCondition(predicate, expectationDescription);
    }

    public static <D> Check<D> require(Check<? super D> requirement, Check<? super D> check) {
        return BasicChecks.require(requirement, check);
    }

    public static <D> Check<D> requireNotNull(Check<D> check) {
        return BasicChecks.requireNotNull(check);
    }

    public static <D> Check<D> check(Predicate<D> predicate, String expectationDescription) {
        return BasicChecks.check(predicate, expectationDescription);
    }

    /* ------------------------------------------------------------------------------------------------------
     * Logical operators for composition of conditions.
     * ------------------------------------------------------------------------------------------------------
     */

    public static <D> Check<D> not(Check<D> positiveCheck) {
        return BasicChecks.not(positiveCheck);
    }

    public static <D> Check<D> not(D positiveValue) {
        return BasicChecks.not(positiveValue);
    }

    /**
     * Create matcher, that matches, if tested object meets one of provided alternatives (or).
     *
     * @param alternatives Expected alternatives.
     * @param <D> Type of the data to test using this expectation.
     * @return Expectation with alternatives.
     */
    public static <D> Check<D> oneOf(Collection<D> alternatives) {
        return BasicChecks.oneOf(alternatives);
    }

    /**
     * Create matcher, that matches, if tested object meets one of provided alternatives (or).
     *
     * @param alternatives Expected alternatives.
     * @param <D> Type of the data to test using this expectation.
     * @return Expectation with alternatives.
     */
    @SafeVarargs
    public static <D> Check<D> oneOf(D... alternatives) {
        return BasicChecks.oneOf(alternatives);
    }

    public static <D> Check<D> anyOf(Iterable<Check<? super D>> operands) {
        return BasicChecks.anyOf(operands);
    }

    @SafeVarargs
    public static <D> Check<D> anyOf(Check<? super D>... operands) {
        return BasicChecks.anyOf(operands);
    }

    public static <D> Check<D> allOf(Iterable<Check<? super D>> operands) {
        return BasicChecks.allOf(operands);
    }

    @SafeVarargs
    public static <D> Check<D> allOf(Check<? super D>... operands) {
        return BasicChecks.allOf(operands);
    }

    /* ------------------------------------------------------------------------------------------------------
     * General object conditions.
     * ------------------------------------------------------------------------------------------------------
     */

    public static <D> Check<D> equalTo(D expectedValue) {
        return BasicChecks.equalTo(expectedValue);
    }

    public static <D> Check<D> is(D expectedValue) {
        return BasicChecks.is(expectedValue);
    }

    public static Check<Object> isNull() {
        return BasicChecks.isNull();
    }

    public static Check<Object> notNull() {
        return BasicChecks.notNull();
    }

    public static Check<Object> anything() {
        return BasicChecks.anything();
    }

    public static <D> Check<D> sameInstance(D expectedInstance) {
        return BasicChecks.sameInstance(expectedInstance);
    }

    public static Check<Object> instanceOf(Class<?> expectedClass) {
        return BasicChecks.instanceOf(expectedClass);
    }

    public static Check<Object> isA(Class<?> expectedClass) {
        return BasicChecks.isA(expectedClass);
    }

    public static Check<Object> isAn(Class<?> expectedClass) {
        return BasicChecks.isAn(expectedClass);
    }

    public static Check<Object> a(Class<?> expectedClass) {
        return BasicChecks.a(expectedClass);
    }

    public static Check<Object> an(Class<?> expectedClass) {
        return BasicChecks.an(expectedClass);
    }

    public static Check<Object> sameClass(Class<?> expectedClass) {
        return BasicChecks.sameClass(expectedClass);
    }

    /**
     * Create matcher of empty array.
     * It returns true, if tested collection meets null or has no elements.
     *
     * @param <D> Type of the items in the collection to be tested.
     * @return Empty collection expectation.
     */
    public static <D> Check<D[]> emptyArray() {
        return BasicChecks.emptyArray();
    }

    /* ------------------------------------------------------------------------------------------------------
     * Composition of conditions using a function and check for the result.
     * ------------------------------------------------------------------------------------------------------
     */

    public static <D, V> Check<D> transform(Transformation<? super D, V> transformation, Check<? super V> check) {
        return BasicChecks.transform(transformation, check);
    }

    public static <D, V> Check<D> compose(String name, Transformation<? super D, V> transformation, Check<? super V> check) {
        return BasicChecks.compose(name, transformation, check);
    }

    public static <D, V> CheckBuilder<V, Check<D>> has(String name, Transformation<? super D, V> transformation) {
        return BasicChecks.has(name, transformation);
    }

    public static <D, V> CheckBuilder<V, Check<D>> nullableHas(String name, Transformation<? super D, V> transformation) {
        return BasicChecks.nullableHas(name, transformation);
    }

    public static <V> CheckBuilder<V, Check<V>> as(Class<V> type) {
        return BasicChecks.as(type);
    }


    public static Check<Throwable> message(Check<? super String> check) {
        return BasicChecks.message(check);
    }

    /* ------------------------------------------------------------------------------------------------------
     * Comparison conditions.
     * ------------------------------------------------------------------------------------------------------
     */

    public static <D> Check<D> lessThan(D operand, Comparator<D> comparator) {
        return ComparisonChecks.lessThan(operand, comparator);
    }

    public static <D> Check<D> moreThan(D operand, Comparator<D> comparator) {
        return ComparisonChecks.moreThan(operand, comparator);
    }

    public static <D> Check<D> equalOrLessThan(D operand, Comparator<D> comparator) {
        return ComparisonChecks.equalOrLessThan(operand, comparator);
    }

    public static <D> Check<D> equalOrMoreThan(D operand, Comparator<D> comparator) {
        return ComparisonChecks.equalOrMoreThan(operand, comparator);
    }

    public static <D extends Comparable<D>> Check<D> lessThan(D operand) {
        return ComparisonChecks.lessThan(operand);
    }

    public static <D extends Comparable<D>> Check<D> moreThan(D operand) {
        return ComparisonChecks.moreThan(operand);
    }

    public static <D extends Comparable<D>> Check<D> equalOrLessThan(D operand) {
        return ComparisonChecks.equalOrLessThan(operand);
    }

    public static <D extends Comparable<D>> Check<D> equalOrMoreThan(D operand) {
        return ComparisonChecks.equalOrMoreThan(operand);
    }

    public static <D extends Comparable<D>> Check<D> between(D left, D right) {
        return ComparisonChecks.between(left, right);
    }

    public static <D> Check<D> between(D left, D right, Comparator<D> comparator) {
        return ComparisonChecks.between(left, right, comparator);
    }

    public static <D> Check<D> betweenInclusive(D left, D right, Comparator<D> comparator) {
        return ComparisonChecks.betweenInclusive(left, right, comparator);
    }

    public static <D extends Comparable<D>> Check<D> betweenInclusive(D left, D right) {
        return ComparisonChecks.betweenInclusive(left, right);
    }

    /* ------------------------------------------------------------------------------------------------------
     * Numeric conditions.
     * ------------------------------------------------------------------------------------------------------
     */

    public static Check<Double> closeTo(double operand, double precision) {
        return NumericChecks.closeTo(operand, precision);
    }

    public static Check<Float> closeTo(float operand, float precision) {
        return NumericChecks.closeTo(operand, precision);
    }

    public static Check<BigDecimal> closeTo(BigDecimal operand, BigDecimal precision) {
        return NumericChecks.closeTo(operand, precision);
    }

    public static Check<Double> equalTo(Double expectedValue) {
        return NumericChecks.equalTo(expectedValue);
    }

    public static Check<Float> equalTo(Float expectedValue) {
        return NumericChecks.equalTo(expectedValue);
    }

    public static Check<BigDecimal> equalTo(BigDecimal expectedValue) {
        return NumericChecks.equalTo(expectedValue);
    }

    public static ThrowingCheck throwing(Check<? super Throwable> check) {
        return BasicChecks.throwing(check);
    }

    public static ThrowingCheck throwing(Class<? extends Throwable> condition) {
        return BasicChecks.throwing(condition);
    }

    public static <D> Check<D> createBuilder() {
        return BasicChecks.createBuilder();
    }

    public static <D> Check<D> createBuilderWith(Check<D> check) {
        return BasicChecks.createBuilderWith(check);
    }

    public static <D> Check<D> which(Check<D> check) {
        return BasicChecks.which(check);
    }

    public static <D> CheckDsl<D> dsl() {
        return BasicChecks.dsl();
    }

}
