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

import java.time.Duration;
import java.util.Iterator;

public final class Repeater<T> implements Iterable<T> {

    private final T item;
    private final int max;
    private final int increment;
    private final long delayInMillis;

    private Repeater(T item, int max, int increment, long delayInMillis) {
        this.item = item;
        this.max = max;
        this.increment = increment;
        this.delayInMillis = delayInMillis;
    }

    public static <T> Repeater<T> repeat(T item, int max, long delayInMillis) {
        return new Repeater<>(item, max, 1, delayInMillis);
    }

    public static <T> Repeater<T> repeatForever(T item, long delayInMillis) {
        return new Repeater<>(item, 1, 0, delayInMillis);
    }

    public static <T> Repeater<T> repeatForever(T item, Duration delay) {
        return new Repeater<>(item, 1, 0, delay.toMillis());
    }

    public static <T> Repeater<T> repeat(T item, int max, Duration delay) {
        return repeat(item, max, delay.toMillis());
    }

    public static <T> Repeater<T> repeat(T item, int max) {
        return repeat(item, max, 1000);
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private int attempt = 0;
            @Override public boolean hasNext() {
                return attempt < max;
            }
            @Override public T next() {
                if(attempt > 0 && delayInMillis > 0) try {
                    Thread.sleep(delayInMillis);
                } catch (InterruptedException e) {
                    throw new UncheckedInterruptedException("Delay before repeating " + item + " interrupted", e);
                }
                attempt += increment;
                return item;
            }
        };
    }

}
