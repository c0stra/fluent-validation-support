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
import fluent.validation.result.ResultFactory;

import java.util.*;

import static fluent.validation.Transformation.dontTransformNull;
import static java.util.Arrays.asList;

/**
 * Factory of ready to use basic checks.
 * These include:
 *    1. General object checks
 *    2. Logical operators (horizontal composition)
 *    3. Transformation checks (vertical composition)
 *    4. Exception checks
 *    5. Value extractor
 */
@Factory
public final class BasicChecks {

    private BasicChecks() {}

    private static final Check<Object> IS_NULL = sameInstance(null);
    private static final Check<Object> NOT_NULL = not(IS_NULL);
    private static final Check<Object> ANYTHING = new Anything<>();

    /* ------------------------------------------------------------------------------------------------------
     * Simple check builders using predicate and description.
     * ------------------------------------------------------------------------------------------------------ */

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
     * General object conditions.
     * ------------------------------------------------------------------------------------------------------
     */

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


    /* ------------------------------------------------------------------------------------------------------
     * Logical operators for composition of conditions.
     * ------------------------------------------------------------------------------------------------------ */

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

    @SuppressWarnings("unchecked")
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

    /**
     * General OR operator of multiple checks.
     * The created check returns true if any of the provided operands (checks) returns true on the tested data,
     * and only if none of the operands returns true, then the resulting check returns false.
     *
     * Corner case:
     * If no operands are provided, then it returns false (as none of them evaluated to true).
     *
     * @param operands Individual checks to evaluate and apply logical or on their results.
     * @param <D> Type of the tested data.
     * @return New check with the described logic.
     */
    public static <D> Check<D> anyOf(Iterable<Check<? super D>> operands) {
        return multipleOperands(operands, false);
    }

    /**
     * General OR operator of multiple checks.
     * The created check returns true if any of the provided operands (checks) returns true on the tested data,
     * and only if none of the operands returns true, then the resulting check returns false.
     *
     * Corner case:
     * If no operands are provided, then it returns false (as none of them evaluated to true).
     *
     * @param operands Individual checks to evaluate and apply logical or on their results.
     * @param <D> Type of the tested data.
     * @return New check with the described logic.
     */
    @SafeVarargs
    public static <D> Check<D> anyOf(Check<? super D>... operands) {
        return anyOf((Iterable<Check<? super D>>)asList(operands));
    }

    /**
     * General AND operator of multiple checks.
     * The created check returns true if all of the provided operands (checks) returns true on the tested data,
     * and if any of the operands returns false, then the resulting check returns false.
     *
     * Corner case:
     * If no operands are provided, then it returns true (as none of them evaluated to false).
     *
     * @param operands Individual checks to evaluate and apply logical and on their results.
     * @param <D> Type of the tested data.
     * @return New check with the described logic.
     */
    public static <D> Check<D> allOf(Iterable<Check<? super D>> operands) {
        return multipleOperands(operands, true);
    }

    /**
     * General AND operator of multiple checks.
     * The created check returns true if all of the provided operands (checks) returns true on the tested data,
     * and if any of the operands returns false, then the resulting check returns false.
     *
     * Corner case:
     * If no operands are provided, then it returns true (as none of them evaluated to false).
     *
     * @param operands Individual checks to evaluate and apply logical and on their results.
     * @param <D> Type of the tested data.
     * @return New check with the described logic.
     */
    @SafeVarargs
    public static <D> Check<D> allOf(Check<? super D>... operands) {
        return allOf((Iterable<Check<? super D>>)asList(operands));
    }


    /* ------------------------------------------------------------------------------------------------------
     * Composition of conditions using a transformation and check for the result.
     * ------------------------------------------------------------------------------------------------------ */

    /**
     * Lowest level transformation.
     * Create a new check using transformation of the original object, e.g. by accessing it's field, and partial check
     * applied on the result of the transformation.
     *
     * @param transformation Function transforming the original object.
     * @param check Check to apply on the result of the transformation.
     * @param <D> Type of the original tested object.
     * @param <V> Type of the transformation result.
     * @return Composed check applicable on the original (whole) object.
     */
    public static <D, V> Check<D> transform(Transformation<? super D, V> transformation, Check<? super V> check) {
        return new TransformedCheck<>(transformation, check);
    }

    /**
     * Convenient composition using transformation of the original object, and check on the result.
     *
     * @param name Name (description) of the transformation.
     * @param transformation Function transforming the original object.
     * @param check Check to apply on the result of the transformation.
     * @param <D> Type of the original tested object.
     * @param <V> Type of the transformation result.
     * @return Composed check applicable on the original (whole) object.
     */
    public static <D, V> Check<D> compose(String name, Transformation<? super D, V> transformation, Check<? super V> check) {
        return name == null ? transform(transformation, check) : new NamedCheck<>(name, transform(transformation, check));
    }

    /**
     * Simplified composition using transformation of the original object, and check on the result.
     *
     * @param transformation Function transforming the original object.
     * @param check Check to apply on the result of the transformation.
     * @param <D> Type of the original tested object.
     * @param <V> Type of the transformation result.
     * @return Composed check applicable on the original (whole) object.
     */
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
        return condition -> compose(name, dontTransformNull(transformation), condition);
    }

    public static <D, V> TransformationBuilder<V, Check<D>> nullableHas(Transformation<? super D, V> transformation) {
        return nullableHas(transformation.getMethodName(), transformation);
    }

    /**
     * Fluent builder of a check, that will assure, that provided object is instance of required class, and if yes,
     * it will cast it to that class, and apply provided check on the required type.
     *
     * @param type Required Java class / type
     * @param <V> Type to check, and cast to.
     * @return Builder of a object check, that accepts check on the required type.
     */
    public static <V> TransformationBuilder<V, Check<V>> as(Class<V> type) {
        return condition -> require(instanceOf(type), compose("as " + type.getSimpleName(), type::cast, condition));
    }


    /* ------------------------------------------------------------------------------------------------------
     * Checks for exceptions.
     * ------------------------------------------------------------------------------------------------------ */

    /**
     * Check of exception (throwable) message.
     *
     * @param check String check of the message content.
     * @return Check on Throwable.
     */
    public static Check<Throwable> message(Check<? super String> check) {
        return compose("message", Throwable::getMessage, check);
    }

    /**
     * Check of a code, which should throw an exception (throwable).
     * The created check will actually run the code passed to test, and return true if the code throws exception, which
     * matches specified criteria.
     *
     * @param check Check on the throwable.
     * @return ThrowingCheck applicable on provided code, allowing to build further criteria.
     */
    public static ThrowingCheck throwing(Check<? super Throwable> check) {
        return new ThrowingCheck(check);
    }

    /**
     * Check of a code, which should throw an exception (throwable) of expected type.
     * The created check will actually run the code passed to test, and return true if the code throws exception of the
     * required type.
     *
     * @param expectedType Expected type of the thrown exception.
     * @return ThrowingCheck applicable on provided code, allowing to build further criteria.
     */
    public static ThrowingCheck throwing(Class<? extends Throwable> expectedType) {
        return throwing(a(expectedType));
    }


    /* ------------------------------------------------------------------------------------------------------
     * Builders and other helpers.
     * ------------------------------------------------------------------------------------------------------ */

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

    public static <D> Value<D> value() {
        return new Value<>();
    }

    public static <D> Check<D> softCheck(Check<D> check) {
        return new SoftCheck<>(check);
    }

    public static <D> Check<D> customResultFactory(Check<D> check, ResultFactory customResultFactory) {
        return new CustomResultFactoryCheck<>(check, customResultFactory);
    }

}
