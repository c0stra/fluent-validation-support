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

import java.time.Duration;
import java.util.*;
import java.util.concurrent.BlockingQueue;

import static fluent.validation.BasicChecks.*;
import static fluent.validation.Repeater.repeat;
import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;

@Factory
public final class CollectionChecks {

    private CollectionChecks() {}

    /* ------------------------------------------------------------------------------------------------------
     * Checks for iterables.
     * ------------------------------------------------------------------------------------------------------
     */

    @SafeVarargs
    public static <T> Collection<Check<? super T>> items(T... expectedValues) {
        return stream(expectedValues).map(BasicChecks::equalTo).collect(toList());
    }

    @SafeVarargs
    public static <T> Collection<Check<? super T>> itemsMatching(Check<? super T>... expectedValues) {
        return asList(expectedValues);
    }

    public static <D> Check<Iterable<D>> exists(String elementName, Check<? super D> check) {
        return collection(contains(elementName, Collections.singleton(check)));
    }

    public static <D> Check<Iterable<D>> every(String elementName, Check<? super D> check) {
        return new Quantifier<>(elementName, Quantifier.Type.every, check);
    }

    public static <D> Check<D> repeatMax(Check<D> attemptCheck, int max) {
        return repeatMax(attemptCheck, max, Duration.ofSeconds(1));
    }

    public static <D> Check<D> repeatMax(Check<D> itemCheck, int max, Duration delay) {
        return has("Try max " + max + " times", (D i) -> repeat(i, max, delay)).matching(exists("Attempt", itemCheck));
    }

    public static <T> Check<Iterator<T>> startsWith(String elementName, Iterable<Check<? super T>> prefix) {
        return new SameOrderCheck<>(elementName, prefix, false, true);
    }

    public static <T> Check<Iterator<T>> contains(String elementName, Iterable<Check<? super T>> elementChecks) {
        return new SameOrderCheck<>(elementName, elementChecks, false, false);
    }

    public static <T> Check<Iterator<T>> equalTo(String elementName, Iterable<Check<? super T>> elementChecks) {
        return new SameOrderCheck<>(elementName, elementChecks, true, true);
    }

    public static <T> Check<Iterator<T>> startsWith(Iterable<Check<? super T>> prefix) {
        return startsWith("Item", prefix);
    }

    public static <T> Check<Iterator<T>> contains(Iterable<Check<? super T>> elementChecks) {
        return contains("Item", elementChecks);
    }

    public static <T> Check<Iterator<T>> equalTo(Iterable<Check<? super T>> elementChecks) {
        return equalTo("Item", elementChecks);
    }


    public static <T> Check<Iterator<T>> startsInAnyOrderWith(String elementName, Collection<Check<? super T>> prefixElementChecks) {
        return new AnyOrderCheck<>(elementName, prefixElementChecks, false, true, false);
    }

    public static <T> Check<Iterator<T>> containsInAnyOrder(String elementName, Collection<Check<? super T>> elementChecks) {
        return new AnyOrderCheck<>(elementName, elementChecks, false, false, false);
    }

    public static <T> Check<Iterator<T>> containsInAnyOrderOnly(String elementName, Collection<Check<? super T>> elementChecks) {
        return new AnyOrderCheck<>(elementName, elementChecks, false, false, true);
    }

    public static <T> Check<Iterator<T>> equalInAnyOrderTo(String elementName, Collection<Check<? super T>> elementChecks) {
        return new AnyOrderCheck<>(elementName, elementChecks, true, true, false);
    }

    public static <T> Check<Iterator<T>> startsInAnyOrderWith(Collection<Check<? super T>> prefixElementChecks) {
        return startsInAnyOrderWith("Item", prefixElementChecks);
    }

    public static <T> Check<Iterator<T>> containsInAnyOrder(Collection<Check<? super T>> elementChecks) {
        return containsInAnyOrder("Item", elementChecks);
    }

    public static <T> Check<Iterator<T>> containsInAnyOrderOnly(Collection<Check<? super T>> elementChecks) {
        return containsInAnyOrderOnly("Item", elementChecks);
    }

    public static <T> Check<Iterator<T>> equalInAnyOrderTo(Collection<Check<? super T>> elementChecks) {
        return equalInAnyOrderTo("Item", elementChecks);
    }


    public static <T> Check<Iterable<T>> collection(Check<Iterator<T>> check) {
        return requireNotNull(transform(Iterable::iterator, check));
    }

    public static <T> Check<T[]> array(Check<Iterator<T>> check) {
        return requireNotNull(transform(array -> asList(array).iterator(), check));
    }

    public static <T> Check<Queue<T>> queue(Check<Iterator<T>> check) {
        return requireNotNull(transform(Functions::queueIterator, check));
    }

    public static <T> Check<BlockingQueue<T>> blockingQueue(Check<Iterator<T>> check, Duration timeout) {
        return requireNotNull(transform(queue -> Functions.blockingQueueIterator(queue, timeout.toMillis()), check));
    }


    /**
     * Create matcher of empty collection.
     * It returns true, if tested collection meets null or has no elements.
     *
     * @param <D> Type of the items in the collection to be tested.
     * @return Empty collection expectation.
     */
    public static <D> Check<Collection<D>> emptyCollection() {
        return nullableCheck(data -> Objects.isNull(data) || data.isEmpty(), "is empty collection");
    }

    /**
     * Create matcher, that matches if tested collection meets subset of provided superset.
     *
     * @param superSet Superset of expected items.
     * @param <D> Type of the items in the collection to be tested.
     * @return Subset expectation.
     */
    public static <D> Check<Collection<D>> subsetOf(Collection<D> superSet) {
        return check(superSet::containsAll, "Superset of " + superSet);
    }

    /**
     * Create matcher, that matches if tested collection meets subset of provided superset.
     *
     * @param superSet Superset of expected items.
     * @param <D> Type of the items in the collection to be tested.
     * @return Subset expectation.
     */
    @SafeVarargs
    public static <D> Check<Collection<D>> subsetOf(D... superSet) {
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
    public static <D> Check<Collection<D>> hasSize(int size) {
        return check(data -> data.size() == size, "has size " + size);
    }

    public static <D> Check<Collection<D>> containsAll(Collection<D> items) {
        return check(data -> data.containsAll(items), "Has items " + items);
    }

    @SafeVarargs
    public static <D> Check<Collection<D>> containsAll(D... items) {
        return containsAll(asList(items));
    }

    public static <K, V> Check<Map<K, V>> mapHas(K key, Check<? super V> check) {
        return new MapItemCheck<>(key, check);
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

}
