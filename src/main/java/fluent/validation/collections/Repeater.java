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

package fluent.validation.collections;

import java.time.Duration;
import java.util.Iterator;
import java.util.function.Supplier;

public final class Repeater<D> implements Iterable<D> {

    private final Supplier<? extends D> supplier;
    private final int maxAttempts;
    private final Runnable update;

    private Repeater(Supplier<? extends D> supplier, int maxAttempts, Runnable update) {
        this.supplier = supplier;
        this.maxAttempts = maxAttempts;
        this.update = update;
    }

    public static <D> Repeater<D> repeat(Supplier<? extends D> supplier, int maxAttempts, Duration delay) {
        return new Repeater<>(supplier, maxAttempts, () -> {
            try {
                Thread.sleep(delay.toMillis());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        });
    }

    public static <D> Repeater<D> repeat(Supplier<? extends D> supplier, int maxAttempts, Runnable update) {
        return new Repeater<>(supplier, maxAttempts, update);
    }

    @Override
    public Iterator<D> iterator() {
        return new Iterator<D>() {
            private int i = 0;
            @Override public boolean hasNext() {
                return i < maxAttempts;
            }
            @Override public D next() {
                if(i > 0) update.run();
                i++;
                return supplier.get();
            }
        };
    }

}
