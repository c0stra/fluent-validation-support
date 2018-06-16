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

import fluent.validation.detail.EvaluationLogger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.lang.Math.abs;
import static java.util.Arrays.asList;
import static java.util.Arrays.stream;

/**
 * Factory of ready to use most frequent conditions. There are typical conditions for following categories:
 *
 * 1. General condition builders for simple building of new conditions using predicate and description.
 * 2. General object conditions - e.g. isNull, notNull, equalTo, etc.
 * 3. Generalized logical operators (N-ary oneOf instead of binary or, N-ary allOf instead of binary and)
 * 4. Collection (Iterable) conditions + quantifiers
 * 5. Relational and range conditions for comparables
 * 6. String matching conditions (contains/startWith/endsWith as well as regexp matching)
 * 7. Basic XML conditions (XPath, attribute matching)
 * 8. Floating point comparison using a tolerance
 * 9. Builders for composition or collection of criteria.
 */
public final class Conditions {

    private Conditions() {}

    private static final Double DEFAULT_TOLERANCE = 0.0000001;
    private static final Condition<Object> IS_NULL = equalTo((Object) null);
    private static final Condition<Object> NOT_NULL = not(IS_NULL);
    private static final Condition<Object> ANYTHING = nullableCondition(data -> true, "anything");

    /* ------------------------------------------------------------------------------------------------------
     * Simple condition builders using predicate and description.
     * ------------------------------------------------------------------------------------------------------
     */

    /**
     * Define a transparent condition using provided predicate and string expectation description.
     *
     * @param predicate Predicate used to test the supplied data.
     * @param expectationDescription Function, that provides description of the expectation.
     * @param <D> Type of the data to be tested by the created expectation.
     * @return New expectation.
     */
    public static <D> Condition<D> nullableCondition(Predicate<D> predicate, String expectationDescription) {
        return new PredicateCondition<>(predicate, expectationDescription);
    }

    public static <D> Condition<D> require(Condition<? super D> requirement, Condition<? super D> condition) {
        return new RequireNotNull<>(requirement, condition);
    }

    public static <D> Condition<D> requireNotNull(Condition<D> condition) {
        return require(notNull(), condition);
    }

    public static <D> Condition<D> condition(Predicate<D> predicate, String expectationDescription) {
        return requireNotNull(nullableCondition(predicate, expectationDescription));
    }

    /* ------------------------------------------------------------------------------------------------------
     * Logical operators for composition of conditions.
     * ------------------------------------------------------------------------------------------------------
     */

    public static <D> Condition<D> not(Condition<D> positiveCondition) {
        return new NegativeCondition<>(positiveCondition);
    }

    public static <D> Condition<D> not(D positiveValue) {
        return not(equalTo(positiveValue));
    }

    /**
     * Create matcher, that matches, if tested object meets one of provided alternatives (or).
     *
     * @param alternatives Expected alternatives.
     * @param <D> Type of the data to test using this expectation.
     * @return Expectation with alternatives.
     */
    public static <D> Condition<D> oneOf(Collection<D> alternatives) {
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
    public static <D> Condition<D> oneOf(D... alternatives) {
        return oneOf(new HashSet<>(asList(alternatives)));
    }


    public static <D> Condition<D> anyOf(Iterable<Condition<? super D>> operands) {
        return new Operator<>(operands, true);
    }

    @SafeVarargs
    public static <D> Condition<D> anyOf(Condition<? super D>... operands) {
        return anyOf(asList(operands));
    }

    public static <D> Condition<D> allOf(Iterable<Condition<? super D>> operands) {
        return new Operator<>(operands, false);
    }

    @SafeVarargs
    public static <D> Condition<D> allOf(Condition<? super D>... operands) {
        return allOf(asList(operands));
    }

    /* ------------------------------------------------------------------------------------------------------
     * General object conditions.
     * ------------------------------------------------------------------------------------------------------
     */

    public static <D> Condition<D> equalTo(D expectedValue) {
        return nullableCondition(Predicate.isEqual(expectedValue), "<" + expectedValue + ">");
    }

    public static <D> Condition<D> is(D expectedValue) {
        return equalTo(expectedValue);
    }

    public static Condition<Object> isNull() {
        return IS_NULL;
    }

    public static Condition<Object> notNull() {
        return NOT_NULL;
    }

    public static Condition<Object> anything() {
        return ANYTHING;
    }

    public static <D> Condition<D> sameInstance(D expectedInstance) {
        return nullableCondition(expectedInstance == null ? Objects::isNull : data -> data == expectedInstance, "" + expectedInstance);
    }

    private static Condition<Object> instanceOf(String prefix, Class<?> expectedClass) {
        return nullableCondition(expectedClass::isInstance, prefix + " " + expectedClass);
    }

    public static Condition<Object> instanceOf(Class<?> expectedClass) {
        return instanceOf("instance of", expectedClass);
    }

    public static Condition<Object> isA(Class<?> expectedClass) {
        return instanceOf("is a", expectedClass);
    }

    public static Condition<Object> isAn(Class<?> expectedClass) {
        return instanceOf("is an", expectedClass);
    }

    public static Condition<Object> a(Class<?> expectedClass) {
        return isA(expectedClass);
    }

    public static Condition<Object> an(Class<?> expectedClass) {
        return isAn(expectedClass);
    }

    public static Condition<Object> sameClass(Class<?> expectedClass) {
        return has("class", Object::getClass).matching(equalTo(expectedClass));
    }

    /* ------------------------------------------------------------------------------------------------------
     * Conditions for iterables.
     * ------------------------------------------------------------------------------------------------------
     */

    public static <D> Condition<Iterable<D>> exists(Condition<? super D> condition) {
        return new Quantifier<>(condition, true);
    }

    public static <D> Condition<Iterable<D>> every(Condition<? super D> condition) {
        return new Quantifier<>(condition, false);
    }

    /**
     * Create matcher of empty array.
     * It returns true, if tested collection meets null or has no elements.
     *
     * @param <D> Type of the items in the collection to be tested.
     * @return Empty collection expectation.
     */
    public static <D> Condition<D[]> emptyArray() {
        return nullableCondition(data -> Objects.isNull(data) || data.length == 0, "is empty array");
    }

    /**
     * Create matcher of empty collection.
     * It returns true, if tested collection meets null or has no elements.
     *
     * @param <D> Type of the items in the collection to be tested.
     * @return Empty collection expectation.
     */
    public static <D> Condition<Collection<D>> emptyCollection() {
        return nullableCondition(data -> Objects.isNull(data) || data.isEmpty(), "is empty collection");
    }

    /**
     * Create matcher, that matches if tested collection meets subset of provided superset.
     *
     * @param superSet Superset of expected items.
     * @param <D> Type of the items in the collection to be tested.
     * @return Subset expectation.
     */
    public static <D> Condition<Collection<D>> subsetOf(Collection<D> superSet) {
        return condition(superSet::containsAll, "Superset of " + superSet);
    }

    /**
     * Create matcher, that matches if tested collection meets subset of provided superset.
     *
     * @param superSet Superset of expected items.
     * @param <D> Type of the items in the collection to be tested.
     * @return Subset expectation.
     */
    @SafeVarargs
    public static <D> Condition<Collection<D>> subsetOf(D... superSet) {
        return subsetOf(new HashSet<>(asList(superSet)));
    }

    /**
     * Create matcher of collection size.
     * If you want to test for emptiness using size 0, note different behavior from empty() for null. This expectation
     * will fail for it, but empty() will pass.
     *
     * @param size Expected size of the collection.
     * @param <D> Type of the items in the collection to be tested.
     * @return Collection size expectation.
     */
    public static <D> Condition<Collection<D>> hasSize(int size) {
        return condition(data -> data.size() == size, "has size " + size);
    }

    public static <D> Condition<Collection<D>> hasItems(Collection<D> items) {
        return condition(data -> data.containsAll(items), "Has items " + items);
    }

    @SafeVarargs
    public static <D> Condition<Collection<D>> hasItems(D... items) {
        return hasItems(asList(items));
    }

    public static <D> Condition<Iterable<D>> hasItemsInOrder(Iterable<Condition<? super D>> itemConditions) {
        return new SubsetInOrder<>(itemConditions);
    }

    @SafeVarargs
    public static <D> Condition<Iterable<D>> hasItemsInOrder(Condition<? super D>... itemConditions) {
        return Conditions.<D>hasItemsInOrder(asList(itemConditions));
    }

    public static <D> Condition<Iterable<D>> hasItemsInAnyOrder(Collection<Condition<? super D>> itemConditions) {
        return new SubsetAnyOrder<>(itemConditions);
    }

    @SafeVarargs
    public static <D> Condition<Iterable<D>> hasItemsInAnyOrder(Condition<? super D>... itemConditions) {
        return Conditions.<D>hasItemsInAnyOrder(asList(itemConditions));
    }

    public static <D> Condition<Iterable<D>> exactItemsInOrder(Iterable<Condition<? super D>> itemConditions) {
        return new CollectionInOrder<>(itemConditions);
    }

    @SafeVarargs
    public static <D> Condition<Iterable<D>> exactItemsInOrder(Condition<? super D>... itemConditions) {
        return Conditions.<D>exactItemsInOrder(asList(itemConditions));
    }

    @SafeVarargs
    public static <D> Condition<Iterable<D>> exactItemsInOrder(D... expectedItems) {
        return exactItemsInOrder(stream(expectedItems).map(Conditions::equalTo).collect(Collectors.toList()));
    }

    public static <D> Condition<Iterable<D>> exactItemsInAnyOrder(Iterable<Condition<? super D>> itemConditions) {
        return new CollectionAnyOrder<>(itemConditions);
    }

    @SafeVarargs
    public static <D> Condition<Iterable<D>> exactItemsInAnyOrder(Condition<? super D>... itemConditions) {
        return Conditions.<D>exactItemsInAnyOrder(asList(itemConditions));
    }

    /* ------------------------------------------------------------------------------------------------------
     * Composition of conditions using a function and condition for the result.
     * ------------------------------------------------------------------------------------------------------
     */

    public static <D, V> Condition<D> compose(String name, Function<? super D, V> function, Condition<? super V> condition) {
        return new FunctionCondition<>(name, function, condition);
    }

    public static <D, V> Builder<V, Condition<D>> has(String name, Function<? super D, V> function) {
        return condition -> requireNotNull(compose(name, function, condition));
    }

    public static <D, V> Builder<V, Condition<D>> nullableHas(String name, Function<? super D, V> function) {
        return condition -> compose(name, function, condition);
    }

    public static <V> Builder<V, Condition<V>> as(Class<V> type) {
        return condition -> require(instanceOf(type), compose("as " + type.getSimpleName(), type::cast, condition));
    }

    public static <D> Conditional<D> when(Condition<D> condition) {
        return new Conditional<>(condition);
    }

    public static final class Conditional<D> {
        private final Condition<D> condition;

        public Conditional(Condition<D> condition) {
            this.condition = condition;
        }

        public <E extends D> Condition<E> then(Condition<? super E> conditional) {
            return (data, detail) -> condition.test(data, detail) && conditional.test(data, detail);
        }
    }

    /* ------------------------------------------------------------------------------------------------------
     * XML conditions.
     * ------------------------------------------------------------------------------------------------------
     */


    public static Condition<Document> xpath(String xpath) throws XPathExpressionException {
        XPathExpression compile = XPathFactory.newInstance().newXPath().compile(xpath);
        return nullableCondition(document -> {
            try {
                return (boolean) compile.evaluate(document, XPathConstants.BOOLEAN);
            } catch (XPathExpressionException e) {
                throw new IllegalArgumentException(e);
            }
        }, xpath);
    }

    public static Builder<String, Condition<Element>> attribute(String name) {
        return has(name, e -> e.getAttribute(name));
    }

    public static Condition<Element> hasAttribute(String name) {
        return condition(e -> e.hasAttribute(name), "has " + name);
    }


    /* ------------------------------------------------------------------------------------------------------
     * String conditions.
     * ------------------------------------------------------------------------------------------------------
     */

    public static Condition<String> equalToCaseInsensitive(String expectedValue) {
        return condition(expectedValue::equalsIgnoreCase, "any case " + expectedValue);
    }

    public static Condition<String> emptyString() {
        return nullableCondition(data -> Objects.isNull(data) || data.isEmpty(), "Conditions empty string");
    }

    public static Condition<String> startsWith(String prefix) {
        return condition(data -> data.startsWith(prefix), "Starts with " + prefix);
    }

    public static Condition<String> startsWithCaseInsensitive(String prefix) {
        return condition(data -> data.toLowerCase().startsWith(prefix.toLowerCase()), "Starts with " + prefix);
    }

    public static Condition<String> endsWith(String suffix) {
        return condition(data -> data.startsWith(suffix), "Ends with %s" + suffix);
    }

    public static Condition<String> endsWithCaseInsensitive(String suffix) {
        return condition(data -> data.toLowerCase().startsWith(suffix.toLowerCase()), "Ends with " + suffix);
    }

    public static Condition<String> contains(String substring) {
        return condition(data -> data.contains(substring), "Contains "+ substring);
    }

    public static Condition<String> containsCaseInsensitive(String substring) {
        return condition(data -> data.toLowerCase().contains(substring.toLowerCase()), "Contains " + substring);
    }

    public static Condition<String> matches(Pattern pattern) {
        return condition(pattern.asPredicate(), "Matches /" + pattern + '/');
    }

    public static Condition<String> matchesPattern(String pattern) {
        return matches(Pattern.compile(pattern));
    }

    public static Condition<Throwable> message(Condition<? super String> condition) {
        return compose("message", Throwable::getMessage, condition);
    }

    /* ------------------------------------------------------------------------------------------------------
     * Comparison conditions.
     * ------------------------------------------------------------------------------------------------------
     */

    public static <D> Condition<D> lessThan(D operand, Comparator<D> comparator) {
        return condition(data -> comparator.compare(data, operand) < 0, "< " + operand);
    }

    public static <D> Condition<D> moreThan(D operand, Comparator<D> comparator) {
        return condition(data -> comparator.compare(data, operand) > 0, "> " + operand);
    }

    public static <D> Condition<D> equalOrLessThan(D operand, Comparator<D> comparator) {
        return condition(data -> comparator.compare(data, operand) < 0, "<= " + operand);
    }

    public static <D> Condition<D> equalOrMoreThan(D operand, Comparator<D> comparator) {
        return condition(data -> comparator.compare(data, operand) > 0, ">= " + operand);
    }

    public static <D extends Comparable<D>> Condition<D> lessThan(D operand) {
        return lessThan(operand, Comparable::compareTo);
    }

    public static <D extends Comparable<D>> Condition<D> moreThan(D operand) {
        return moreThan(operand, Comparable::compareTo);
    }

    public static <D extends Comparable<D>> Condition<D> equalOrLessThan(D operand) {
        return equalOrLessThan(operand, Comparable::compareTo);
    }

    public static <D extends Comparable<D>> Condition<D> equalOrMoreThan(D operand) {
        return equalOrMoreThan(operand, Comparable::compareTo);
    }

    public static <D extends Comparable<D>> Condition<D> between(D left, D right) {
        return between(left, right, Comparable::compareTo);
    }

    public static <D> Condition<D> between(D left, D right, Comparator<D> comparator) {
        if(comparator.compare(left, right) > 0) comparator = comparator.reversed();
        return allOf(moreThan(left, comparator), lessThan(right, comparator));
    }

    public static <D> Condition<D> betweenInclude(D left, D right, Comparator<D> comparator) {
        if(comparator.compare(left, right) > 0) comparator = comparator.reversed();
        return allOf(equalOrMoreThan(left, comparator), equalOrLessThan(right, comparator));
    }

    /* ------------------------------------------------------------------------------------------------------
     * Numeric conditions.
     * ------------------------------------------------------------------------------------------------------
     */

    public static Condition<Double> closeTo(double operand, double precision) {
        return condition(data -> abs(operand - data) < precision, "<" + operand + " ±" + precision + ">");
    }

    public static Condition<Float> closeTo(float operand, float precision) {
        return condition(data -> abs(operand - data) < precision, operand + " ±" + precision);
    }

    public static Condition<BigDecimal> closeTo(BigDecimal operand, BigDecimal precision) {
        return condition(data -> operand.subtract(data).abs().compareTo(precision) < 0, operand + " ±" + precision);
    }

    public static Condition<Double> equalTo(Double expectedValue) {
        return closeTo(expectedValue, DEFAULT_TOLERANCE);
    }

    public static Condition<Float> equalTo(Float expectedValue) {
        return closeTo(expectedValue, DEFAULT_TOLERANCE.floatValue());
    }

    public static Condition<BigDecimal> equalTo(BigDecimal expectedValue) {
        return closeTo(expectedValue, BigDecimal.valueOf(DEFAULT_TOLERANCE));
    }

    public static ThrowingCondition throwing(Condition<? super Throwable> condition) {
        return new ThrowingCondition(condition);
    }

    public static ThrowingCondition throwing(Class<? extends Throwable> condition) {
        return throwing(a(condition));
    }

    public static <D> ConditionBuilder<D> createBuilder() {
        return new EmptyConditionBuilder<>();
    }

    public static <D> ConditionBuilder<D> createBuilderWith(Condition<D> condition) {
        return new ConditionChainBuilder<>(condition);
    }

    public static <D> ConditionBuilder<D> which(Condition<D> condition) {
        return createBuilderWith(condition);
    }

    private static final class EmptyConditionBuilder<D> implements ConditionBuilder<D> {

        @Override public Condition<? super D> get() {
            return Conditions.anything();
        }

        @Override public <E extends D> ConditionBuilder<E> and(Condition<? super E> condition) {
            return new ConditionChainBuilder<E>(condition);
        }

    }

    final static class ConditionChainBuilder<D> implements ConditionBuilder<D> {

        private final Condition<? super D> condition;

        ConditionChainBuilder(Condition<? super D> condition) {
            this.condition = condition;
        }
        @Override public Condition<? super D> get() {
            return condition;
        }
        @Override public <E extends D> ConditionBuilder<E> and(Condition<? super E> condition) {
            return new ConditionChainBuilder<>(new LinkedCondition<>(get(), condition));
        }
        @Override public String toString() {
            return condition.toString();
        }
    }

    final static class LinkedCondition<D> implements Condition<D> {

        private final Condition<? super D> previous;
        private final Condition<? super D> condition;

        LinkedCondition(Condition<? super D> previous, Condition<? super D> condition) {
            this.previous = previous;
            this.condition = condition;
        }
        @Override public boolean test(D data, EvaluationLogger evaluationLogger) {
            return previous.test(data, evaluationLogger) & condition.test(data, evaluationLogger);
        }
        @Override public String toString() {
            return previous.toString() + " and " + condition;
        }
    }

}
