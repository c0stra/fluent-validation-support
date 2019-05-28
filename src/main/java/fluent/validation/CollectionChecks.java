package fluent.validation;

import java.util.*;

import static fluent.validation.BasicChecks.*;
import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;

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
        return new Exists<>(elementName, check);
    }

    public static <D> Check<Iterable<D>> every(String elementName, Check<? super D> check) {
        return new Every<>(elementName, check);
    }

    public static <T> Check<Iterable<T>> collectionStartsWith(Collection<Check<? super T>> prefix) {
        return new CollectionCheckInOrder<>(prefix, false, true);
    }

    public static <T> Check<Iterable<T>> collectionStartsInAnyOrderWith(Collection<Check<? super T>> prefix) {
        return new CollectionCheckInAnyOrder<>(prefix, false, true);
    }

    public static <D> Check<Iterable<D>> collectionContains(Collection<Check<? super D>> itemConditions) {
        return new CollectionCheckInOrder<>(itemConditions, false, false);
    }

    public static <D> Check<Iterable<D>> collectionContainsInAnyOrder(Collection<Check<? super D>> itemChecks) {
        return new CollectionCheckInAnyOrder<>(itemChecks, false, false);
    }

    public static <D> Check<Iterable<D>> collectionEqualTo(Iterable<Check<? super D>> itemConditions) {
        return new CollectionCheckInOrder<>(itemConditions, true, true);
    }

    public static <D> Check<Iterable<D>> collectionEqualInAnyOrderTo(Collection<Check<? super D>> itemConditions) {
        return new CollectionCheckInAnyOrder<>(itemConditions, true, true);
    }



    public static <T> Check<T[]> arrayStartsWith(Collection<Check<? super T>> prefix) {
        return compose("", Arrays::asList, collectionStartsWith(prefix));
    }

    public static <T> Check<T[]> arrayStartsInAnyOrderWith(Collection<Check<? super T>> prefix) {
        return compose("", Arrays::asList, collectionStartsInAnyOrderWith(prefix));
    }

    public static <D> Check<D[]> arrayContains(Collection<Check<? super D>> itemConditions) {
        return compose("", Arrays::asList, collectionContains(itemConditions));
    }

    public static <D> Check<D[]> arrayContainsInAnyOrder(Collection<Check<? super D>> itemChecks) {
        return compose("", Arrays::asList, collectionContainsInAnyOrder(itemChecks));
    }

    public static <D> Check<D[]> arrayEqualTo(Iterable<Check<? super D>> itemConditions) {
        return compose("", Arrays::asList, collectionEqualTo(itemConditions));
    }

    public static <D> Check<D[]> arrayEqualInAnyOrderTo(Collection<Check<? super D>> itemConditions) {
        return compose("", Arrays::asList, collectionEqualInAnyOrderTo(itemConditions));
    }


    public static <T> Check<Queue<T>> queueStartsWith(Collection<Check<? super T>> prefix) {
        return new QueueCheckInOrder<>(prefix, false, true);
    }

    public static <T> Check<Queue<T>> queueStartsInAnyOrderWith(Collection<Check<? super T>> prefix) {
        return new QueueCheckInAnyOrder<>(prefix, false, true);
    }

    public static <D> Check<Queue<D>> queueContains(Collection<Check<? super D>> itemConditions) {
        return new QueueCheckInOrder<>(itemConditions, false, false);
    }

    public static <D> Check<Queue<D>> queueContainsInAnyOrder(Collection<Check<? super D>> itemChecks) {
        return new QueueCheckInAnyOrder<>(itemChecks, false, false);
    }

    public static <D> Check<Queue<D>> queueEqualTo(Iterable<Check<? super D>> itemConditions) {
        return new QueueCheckInOrder<>(itemConditions, true, true);
    }

    public static <D> Check<Queue<D>> queueEqualInAnyOrderTo(Collection<Check<? super D>> itemConditions) {
        return new QueueCheckInAnyOrder<>(itemConditions, true, true);
    }

    /**
     * Create matcher of empty collection.
     * It returns true, if tested collection meets null or has no elements.
     *
     * @param <D> Type of the items in the collection to be tested.
     * @return Empty collection expectation.
     */
    public static <D> Check<Collection<D>> emptyCollection() {
        return nullableCondition(data -> Objects.isNull(data) || data.isEmpty(), "is empty collection");
    }

    /**
     * Create matcher, that matches if tested collection meets subset of provided superset.
     *
     * @param superSet Superset of expected items.
     * @param <D> Type of the items in the collection to be tested.
     * @return Subset expectation.
     */
    public static <D> Check<Collection<D>> subsetOf(Collection<D> superSet) {
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
        return condition(data -> data.size() == size, "has size " + size);
    }

    public static <D> Check<Collection<D>> containsAll(Collection<D> items) {
        return condition(data -> data.containsAll(items), "Has items " + items);
    }

    @SafeVarargs
    public static <D> Check<Collection<D>> containsAll(D... items) {
        return containsAll(asList(items));
    }

}
