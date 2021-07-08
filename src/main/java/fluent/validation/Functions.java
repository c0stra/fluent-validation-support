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

import java.util.*;
import java.util.concurrent.BlockingQueue;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

public final class Functions {
    private Functions() {}

    @SafeVarargs
    public static <D> Set<D> setOf(D... values) {
        switch (values.length) {
            case 0:
                return Collections.emptySet();
            case 1:
                return Collections.singleton(values[0]);
            default:
                return Collections.unmodifiableSet(new LinkedHashSet<>(Arrays.asList(values)));
        }
    }

    public static Set<String> splitToSet(String value, String splitter) {
        return setOf(value.split(splitter));
    }

    /* --------------------------------------------------------------------------------------------------------
     * Set of functions used to transform various inputs to Iterator, so any Check<Iterator<?>> can be applied.
     * -------------------------------------------------------------------------------------------------------- */

    /**
     * Transform Iterable to Iterator (simply by calling it's iterator() method).
     * Keep in mind the difference between iterator() and queueIterator applied on Queue.
     * iterator will iterate current collection in the queue, and throw exception on content change, but
     * queueIterator takes into account new elements added to the queue while iterating.
     *
     * @param iterable Any iterable.
     * @param <D> Type of the elements in the iterable.
     * @return Iterator.
     */
    public static <D> Iterator<D> iterator(Iterable<D> iterable) {
        return iterable.iterator();
    }

    /**
     * Transform Queue to Iterator. It provides custom implementation, that takes into account changes (new elements
     * added) during the iteration.
     * Keep in mind the difference between iterator() and queueIterator applied on Queue.
     * iterator will iterate current collection in the queue, and throw exception on content change, but
     * queueIterator takes into account new elements added to the queue while iterating.
     *
     * @param queue Any iterable.
     * @param <D> Type of the elements in the iterable.
     * @return Iterator.
     */
    public static <D> Iterator<D> queueIterator(Queue<D> queue) {
        return new Iterator<D>() {
            @Override
            public boolean hasNext() {
                return !queue.isEmpty();
            }

            @Override
            public D next() {
                return queue.poll();
            }
        };
    }

    /**
     * Adapt BlockingQueue to Iterator. It uses blocking poll() when attempt to get next element, so it allows testing
     * of asynchronous streams, making the iterable check to wait for future updates within given timeout.
     *
     * @param queue Blocking queue.
     * @param timeout timeout in milliseconds indicating end of the queue.
     * @param <D> Type of the elements in the iterable.
     * @return Iterator.
     */
    public static <D> Iterator<D> blockingQueueIterator(BlockingQueue<D> queue, long timeout) {
        return new Iterator<D>() {
            private D data = poll(timeout);

            @Override
            public boolean hasNext() {
                return data != null;
            }

            @Override
            public D next() {
                D last = data;
                data = poll(timeout);
                return last;
            }

            private D poll(long timeout) {
                try {
                    return queue.poll(timeout, MILLISECONDS);
                } catch (InterruptedException e) {
                    throw new UncheckedInterruptedException(e, e);
                }
            }

        };
    }

}
