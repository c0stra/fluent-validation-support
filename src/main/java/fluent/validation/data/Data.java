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

package fluent.validation.data;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Helper methods for generating combinations of data (typically test data).
 *
 * The class provides rather partial helper methods to be used with Java 8 streams in order to get stream of data,
 * produced as combination of all provided subsets.
 * So by applying these helpers with collections on the input stream, we get new stream, which contains all combinations
 * of every input element with every item from the provided collection.
 * Combination of multiple collections can be achieved by simple chaining:
 *
 * {@code
 *     A.stream().flatMap(combineWith(B)).flatMap(combineWith(C)).iterator();
 * }
 */
public class Data {

    private static Object[] mergeArrays(Object[] a, Object[] b) {
        return writeArrays(a, b, a.length + b.length, a.length);
    }

    private static Object[] writeArrays(Object[] a, Object[] b, int l, int s) {
        Object[] c = new Object[l];
        System.arraycopy(a, 0, c, 0, s);
        System.arraycopy(b, 0, c, s, b.length);
        return c;
    }

    /**
     * Totally general algorithm to combine potentially typesafe heterogeneous input data collections into a structured
     * representation of combination of their elements.
     * @param collection Collection to combine ev
     * @param byFunction Function to create representation of the combination o element from original stream, and element
     *                   from the collection.
     * @param <A> Type of elements in original (input) stream.
     * @param <B> Type of elements in the collection to combine with.
     * @param <C> Type of the representation of a combination.
     * @return Function, that implements the combination, to be used as parameter of input stream's flatMap() method.
     */
    public static <A, B, C> Function<A, Stream<C>> combineWith(Collection<B> collection, BiFunction<A, B, C> byFunction) {
        return a -> collection.stream().map(b -> byFunction.apply(a, b));
    }

    /**
     * Simple function to combine original stream of arrays with elements in provided collection of arrays so, that
     * arrays are concatenated into new one.
     *
     * @param collection Collection of arrays.
     * @return Implementation of combination of original stream with provided collection.
     */
    public static Function<Object[], Stream<Object[]>> combineWith(Collection<Object[]> collection) {
        return combineWith(collection, Data::mergeArrays);
    }

    /**
     * Expansion algorithm allowing definition of data as sparse array representing a tree.
     *
     * Sparse array definition is missing leading elements, which are common with previous records.
     *
     * @param definition Stream of data rows represented as sparse array.
     * @return Stream of records expanded to full length.
     */
    public static Stream<Object[]> expand(Stream<Object[]> definition) {
        AtomicReference<Object[]> last = new AtomicReference<>();
        return definition.map(o -> last.accumulateAndGet(o, (p, c) -> p == null ? c : writeArrays(p, c, p.length, p.length - c.length)));
    }

}
