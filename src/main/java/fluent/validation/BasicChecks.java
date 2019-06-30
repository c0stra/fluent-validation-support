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

import static java.lang.Math.abs;
import static java.util.Arrays.asList;

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
public final class BasicChecks {

    private BasicChecks() {}

    private static final Double DEFAULT_TOLERANCE = 0.0000001;
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
        return new PredicateCheck<>(expectationDescription, predicate);
    }

    public static <D> Check<D> require(Check<? super D> requirement, Check<? super D> check) {
        return new DoubleCheck<>(requirement, check);
    }

    public static <D> Check<D> requireNotNull(Check<D> check) {
        return require(notNull(), check);
    }

    public static <D> Check<D> check(Predicate<D> predicate, String expectationDescription) {
        return requireNotNull(nullableCondition(predicate, expectationDescription));
    }

    /* ------------------------------------------------------------------------------------------------------
     * Logical operators for composition of conditions.
     * ------------------------------------------------------------------------------------------------------
     */

    public static <D> Check<D> not(Check<D> positiveCheck) {
        return new NegativeCheck<>(positiveCheck);
    }

    public static <D> Check<D> not(D positiveValue) {
        return not(equalTo(positiveValue));
    }

    /**
     * Create matcher, that matches, if tested object meets one of provided alternatives (or).
     *
     * @param alternatives Expected alternatives.
     * @param <D> Type of the data to test using this expectation.
     * @return Expectation with alternatives.
     */
    public static <D> Check<D> oneOf(Collection<D> alternatives) {
        return nullableCondition(alternatives::contains, "One of " + alternatives);
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
        return oneOf(new HashSet<>(asList(alternatives)));
    }

    private static <D> Check<D> multipleOperands(Iterable<Check<? super D>> operands, boolean andOperator) {
        Iterator<Check<? super D>> iterator = operands.iterator();
        if(!iterator.hasNext()) {
            throw new IllegalArgumentException("No operand supplied to " + (andOperator ? "AND" : "OR"));
        }
        Check<D> next = (Check<D>) iterator.next();
        while(iterator.hasNext()) {
            // Here operator precedence is explicit.
            next = andOperator ? new And<>(next, iterator.next()) : new Or<>(next, iterator.next());
        }
        return next;
    }

    public static <D> Check<D> anyOf(Iterable<Check<? super D>> operands) {
        return multipleOperands(operands, false);
    }

    @SafeVarargs
    public static <D> Check<D> anyOf(Check<? super D>... operands) {
        return anyOf(asList(operands));
    }

    public static <D> Check<D> allOf(Iterable<Check<? super D>> operands) {
        return multipleOperands(operands, true);
    }

    @SafeVarargs
    public static <D> Check<D> allOf(Check<? super D>... operands) {
        return allOf(asList(operands));
    }

    /* ------------------------------------------------------------------------------------------------------
     * General object conditions.
     * ------------------------------------------------------------------------------------------------------
     */

    public static <D> Check<D> equalTo(D expectedValue) {
        return nullableCondition(expectedValue == null ? Objects::isNull : expectedValue::equals, "<" + expectedValue + ">");
    }

    public static <D> Check<D> is(D expectedValue) {
        return equalTo(expectedValue);
    }

    public static Check<Object> isNull() {
        return IS_NULL;
    }

    public static Check<Object> notNull() {
        return NOT_NULL;
    }

    public static Check<Object> anything() {
        return ANYTHING;
    }

    public static <D> Check<D> sameInstance(D expectedInstance) {
        return nullableCondition(expectedInstance == null ? Objects::isNull : data -> data == expectedInstance, "" + expectedInstance);
    }

    private static Check<Object> instanceOf(String prefix, Class<?> expectedClass) {
        return nullableCondition(expectedClass::isInstance, prefix + " " + expectedClass);
    }

    public static Check<Object> instanceOf(Class<?> expectedClass) {
        return instanceOf("instance of", expectedClass);
    }

    public static Check<Object> isA(Class<?> expectedClass) {
        return instanceOf("is a", expectedClass);
    }

    public static Check<Object> isAn(Class<?> expectedClass) {
        return instanceOf("is an", expectedClass);
    }

    public static Check<Object> a(Class<?> expectedClass) {
        return isA(expectedClass);
    }

    public static Check<Object> an(Class<?> expectedClass) {
        return isAn(expectedClass);
    }

    public static Check<Object> sameClass(Class<?> expectedClass) {
        return has("class", Object::getClass).matching(equalTo(expectedClass));
    }

    /**
     * Create matcher of empty array.
     * It returns true, if tested collection meets null or has no elements.
     *
     * @param <D> Type of the items in the collection to be tested.
     * @return Empty collection expectation.
     */
    public static <D> Check<D[]> emptyArray() {
        return nullableCondition(data -> Objects.isNull(data) || data.length == 0, "is empty array");
    }

    /* ------------------------------------------------------------------------------------------------------
     * Composition of conditions using a function and check for the result.
     * ------------------------------------------------------------------------------------------------------
     */

    public static <D, V> Check<D> compose(String name, Transformation<? super D, V> transformation, Check<? super V> check) {
        return new NamedCheck<>(name, new TransformedCheck<>(transformation, check));
    }

    public static <D, V> Builder<V, Check<D>> has(String name, Transformation<? super D, V> transformation) {
        return condition -> requireNotNull(compose(name, transformation, condition));
    }

    public static <D, V> Builder<V, Check<D>> nullableHas(String name, Transformation<? super D, V> transformation) {
        return condition -> compose(name, transformation, condition);
    }

    public static <V> Builder<V, Check<V>> as(Class<V> type) {
        return condition -> require(instanceOf(type), compose("as " + type.getSimpleName(), type::cast, condition));
    }


    public static Check<Throwable> message(Check<? super String> check) {
        return compose("message", Throwable::getMessage, check);
    }

    /* ------------------------------------------------------------------------------------------------------
     * Comparison conditions.
     * ------------------------------------------------------------------------------------------------------
     */

    public static <D> Check<D> lessThan(D operand, Comparator<D> comparator) {
        return check(data -> comparator.compare(data, operand) < 0, "< " + operand);
    }

    public static <D> Check<D> moreThan(D operand, Comparator<D> comparator) {
        return check(data -> comparator.compare(data, operand) > 0, "> " + operand);
    }

    public static <D> Check<D> equalOrLessThan(D operand, Comparator<D> comparator) {
        return check(data -> comparator.compare(data, operand) < 0, "<= " + operand);
    }

    public static <D> Check<D> equalOrMoreThan(D operand, Comparator<D> comparator) {
        return check(data -> comparator.compare(data, operand) > 0, ">= " + operand);
    }

    public static <D extends Comparable<D>> Check<D> lessThan(D operand) {
        return lessThan(operand, Comparable::compareTo);
    }

    public static <D extends Comparable<D>> Check<D> moreThan(D operand) {
        return moreThan(operand, Comparable::compareTo);
    }

    public static <D extends Comparable<D>> Check<D> equalOrLessThan(D operand) {
        return equalOrLessThan(operand, Comparable::compareTo);
    }

    public static <D extends Comparable<D>> Check<D> equalOrMoreThan(D operand) {
        return equalOrMoreThan(operand, Comparable::compareTo);
    }

    public static <D extends Comparable<D>> Check<D> between(D left, D right) {
        return between(left, right, Comparable::compareTo);
    }

    public static <D> Check<D> between(D left, D right, Comparator<D> comparator) {
        if(comparator.compare(left, right) > 0) comparator = comparator.reversed();
        return allOf(moreThan(left, comparator), lessThan(right, comparator));
    }

    public static <D> Check<D> betweenInclude(D left, D right, Comparator<D> comparator) {
        if(comparator.compare(left, right) > 0) comparator = comparator.reversed();
        return allOf(equalOrMoreThan(left, comparator), equalOrLessThan(right, comparator));
    }

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

    public static ThrowingCheck throwing(Check<? super Throwable> check) {
        return new ThrowingCheck(check);
    }

    public static ThrowingCheck throwing(Class<? extends Throwable> condition) {
        return throwing(a(condition));
    }

    public static <D> Check<D> createBuilder() {
        return new Anything<>();
    }

    public static <D> Check<D> createBuilderWith(Check<D> check) {
        return check;
    }

    public static <D> Check<D> which(Check<D> check) {
        return createBuilderWith(check);
    }

    public static <D> CheckDsl.Final<D> dsl() {
        return new CheckDsl.Final<>();
    }

}
