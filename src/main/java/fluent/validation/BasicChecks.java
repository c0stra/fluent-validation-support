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

import java.util.*;

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
@Factory
public final class BasicChecks {

    private BasicChecks() {}

    private static final Check<Object> IS_NULL = sameInstance(null);
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
    public static <D> Check<D> nullableCheck(Predicate<D> predicate, String expectationDescription) {
        return new PredicateCheck<>(expectationDescription, predicate);
    }

    /**
     * Require, that tested data meet provided requirement before application of a check.
     * If the requirement is not met, composed check returns false, and the "actual" check is not evaluated.
     * If the requirement is met, then "actual" check is evaluated, and the result is returned.
     *
     * Error description:
     * If requirement is not met, then error contains description of it's mismatch.
     * If requirement is met, then error contains only mismatch of the "actual" check, while requirement stays silent.
     *
     * The behavior is like Java's && operator, which doesn't evaluate second operand if first one is false.
     * The and() operator check as opposed to this one, evaluates always both operands to get mismatch details from
     * both of them. Keep that in mind and properly decide, in which situation to use which operator.
     *
     * @param requirement Check, that must return true in order to evaluate the "actual" one.
     * @param check "Actual" check to be evaluated on tested data, if requirement is met.
     * @param <D> Type of the data to be tested by the created check.
     * @return Composed check.
     */
    public static <D> Check<D> require(Check<? super D> requirement, Check<? super D> check) {
        return new DoubleCheck<>(requirement, check);
    }

    /**
     * Require, that tested value is not null, before application of the provided check on it.
     * If the tested value is null, then it returns false without evaluating the check.
     * If the tested value is not null, then the provided check is applied, and result is returned.
     *
     * @param check Check to apply on the value, if it's not null.
     * @param <D> Type of the data to be tested by the created check.
     * @return Composed check.
     */
    public static <D> Check<D> requireNotNull(Check<D> check) {
        return require(isNotNull(), check);
    }

    /**
     * Build a custom check using it's description and pure Java predicate. As the Predicate doesn't
     * provide any transparency, it's enriched with the description.
     *
     * @param predicate Implementation of the check logic.
     * @param expectationDescription Description of the expectation represented by this check.
     * @param <D> Type of the data to be checked.
     * @return Custom check.
     */
    public static <D> Check<D> check(Predicate<D> predicate, String expectationDescription) {
        return requireNotNull(nullableCheck(predicate, expectationDescription));
    }

    /* ------------------------------------------------------------------------------------------------------
     * Logical operators for composition of conditions.
     * ------------------------------------------------------------------------------------------------------
     */

    /**
     * Check, that provided positive check doesn't match checked data.
     *
     * @param positiveCheck Positive check to evaluate, what shouldn't match.
     * @param <D> Type of the data to be tested by the created check.
     * @return Negated expectation.
     */
    public static <D> Check<D> not(Check<D> positiveCheck) {
        return new NegativeCheck<>(positiveCheck);
    }

    /**
     * Check, that provided positive value isn't equal to checked data.
     *
     * @param positiveValue Positive value, which shouldn't be equal to checked data.
     * @param <D> Type of the data to be tested by the created check.
     * @return Negated expectation.
     */
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
        return nullableCheck(alternatives::contains, "One of " + alternatives);
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
            return BasicChecks.nullableCheck(data -> andOperator, "empty " + (andOperator ? "allOf" : "anyOf") + " formula");
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
        return anyOf((Iterable<Check<? super D>>)asList(operands));
    }

    public static <D> Check<D> allOf(Iterable<Check<? super D>> operands) {
        return multipleOperands(operands, true);
    }

    @SafeVarargs
    public static <D> Check<D> allOf(Check<? super D>... operands) {
        return allOf((Iterable<Check<? super D>>)asList(operands));
    }

    /* ------------------------------------------------------------------------------------------------------
     * General object conditions.
     * ------------------------------------------------------------------------------------------------------
     */

    /**
     * Create a check, that checks if the tested value is equal to expected value.
     * It will return true as long as the equals() method does.
     *
     * @param expectedValue Expected value.
     * @param <D> Type of the tested data.
     * @return New check.
     */
    public static <D> Check<D> equalTo(D expectedValue) {
        return expectedValue == null ? sameInstance(null) : nullableCheck(expectedValue::equals, "<" + expectedValue + ">");
    }

    /**
     * Alias for `equalTo`, which may be more readable in some constructions.
     *
     * @param expectedValue Expected value.
     * @param <D> Type of the tested data.
     * @return New check.
     * @see #equalTo(Object)
     */
    public static <D> Check<D> is(D expectedValue) {
        return equalTo(expectedValue);
    }

    /**
     * Check, if tested data is null.
     * @return New check.
     */
    public static Check<Object> isNull() {
        return IS_NULL;
    }

    /**
     * Check, if tested data is not null.
     * @return New check.
     */
    public static Check<Object> isNotNull() {
        return NOT_NULL;
    }

    /**
     * Check, that doesn't care of the tested value, and always returns true.
     * @return New check.
     */
    public static Check<Object> anything() {
        return ANYTHING;
    }

    /**
     * Create a check, that checks if the tested value is exactly the same instance as expected.
     * This check doesn't use `equals()` method, but reference comparison operator ==.
     *
     * @param expectedInstance Expected instance.
     * @param <D> Type of the tested data.
     * @return New check.
     */
    public static <D> Check<D> sameInstance(D expectedInstance) {
        return nullableCheck(data -> data == expectedInstance, "<" + expectedInstance + ">");
    }

    private static Check<Object> instanceOf(String prefix, Class<?> expectedClass) {
        return nullableCheck(expectedClass::isInstance, prefix + " " + expectedClass);
    }

    /**
     * Check, that tested data is instance of provided class (either the class itself or any subclass).
     *
     * @param expectedClass Expected class.
     * @return New check.
     */
    public static Check<Object> instanceOf(Class<?> expectedClass) {
        return instanceOf("instance of", expectedClass);
    }

    /**
     * Alias of `instanceOf` which may be more readable in some context.
     *
     * @param expectedClass Expected class.
     * @return New check.
     */
    public static Check<Object> isA(Class<?> expectedClass) {
        return instanceOf("is a", expectedClass);
    }

    /**
     * Alias of `instanceOf` which may be more readable in some context.
     *
     * @param expectedClass Expected class.
     * @return New check.
     */
    public static Check<Object> isAn(Class<?> expectedClass) {
        return instanceOf("is an", expectedClass);
    }

    /**
     * Alias of `instanceOf` which may be more readable in some context.
     *
     * @param expectedClass Expected class.
     * @return New check.
     */
    public static Check<Object> a(Class<?> expectedClass) {
        return isA(expectedClass);
    }

    /**
     * Alias of `instanceOf` which may be more readable in some context.
     *
     * @param expectedClass Expected class.
     * @return New check.
     */
    public static Check<Object> an(Class<?> expectedClass) {
        return isAn(expectedClass);
    }

    /**
     * Check, that tested data is exactly of the provided type, but not any subcalss of it.
     * @param expectedClass Expected class.
     * @return New check.
     */
    public static Check<Object> sameClass(Class<?> expectedClass) {
        return has("class", Object::getClass).matching(equalTo(expectedClass));
    }

    /**
     * Create matcher of empty array.
     * It returns true, if tested collection meets null or has no elements.
     *
     * @param <D> Type of the items in the tested array.
     * @return Empty array expectation.
     */
    public static <D> Check<D[]> emptyArray() {
        return check(data -> data.length == 0, "is empty array");
    }

    /**
     * Check, that provided data is either null or empty array.
     *
     * @param <D> Type of the items in the tested array.
     * @return Empty array expectation.
     */
    public static <D> Check<D[]> emptyArrayOrNull() {
        return nullableCheck(data -> Objects.isNull(data) || data.length == 0, "is empty array");
    }

    /* ------------------------------------------------------------------------------------------------------
     * Composition of conditions using a function and check for the result.
     * ------------------------------------------------------------------------------------------------------
     */

    public static <D, V> Check<D> transform(Transformation<? super D, V> transformation, Check<? super V> check) {
        return new TransformedCheck<>(transformation, check);
    }

    public static <D, V> Check<D> compose(String name, Transformation<? super D, V> transformation, Check<? super V> check) {
        return name == null ? transform(transformation, check) : new NamedCheck<>(name, transform(transformation, check));
    }

    public static <D, V> Check<D> compose(Transformation<? super D, V> transformation, Check<? super V> check) {
        return new NamedCheck<>(transformation.getMethodName(), transform(transformation, check));
    }

    public static <D, V> TransformationBuilder<V, Check<D>> has(String name, Transformation<? super D, V> transformation) {
        return condition -> requireNotNull(compose(name, transformation, condition));
    }

    public static <D, V> TransformationBuilder<V, Check<D>> has(Transformation<? super D, V> transformation) {
        return condition -> requireNotNull(compose(transformation.getMethodName(), transformation, condition));
    }

    public static <D, V> TransformationBuilder<V, Check<D>> nullableHas(String name, Transformation<? super D, V> transformation) {
        return condition -> compose(name, transformation, condition);
    }

    public static <V> TransformationBuilder<V, Check<V>> as(Class<V> type) {
        return condition -> require(instanceOf(type), compose("as " + type.getSimpleName(), type::cast, condition));
    }


    public static Check<Throwable> message(Check<? super String> check) {
        return compose("message", Throwable::getMessage, check);
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

    public static <D> CheckDsl<D> dsl() {
        return new CheckDsl<>();
    }

}
