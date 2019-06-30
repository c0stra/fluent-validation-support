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

import fluent.validation.result.Aggregator;
import fluent.validation.result.Result;
import fluent.validation.result.ResultFactory;

import java.util.concurrent.BlockingQueue;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * Check, making sure, that an actual collection meets provided conditions in exact order:
 *   1st item matches 1st condition, 2nd item matches 2nd condition, etc. and there must not be any item missing or
 *   extra (length of actual collection needs to match length of collection of conditions).
 *
 * @param <D> Type of the items in the collection.
 */
final class BlockingQueueCheckInOrder<D> extends Check<BlockingQueue<D>> {

    private final Iterable<Check<? super D>> checks;
    private final long timeoutMillis;
    private final boolean full;
    private final boolean exact;

    BlockingQueueCheckInOrder(Iterable<Check<? super D>> checks, long timeoutMillis, boolean full, boolean exact) {
        this.checks = checks;
        this.timeoutMillis = timeoutMillis;
        this.full = full;
        this.exact = exact;
    }

    private D get(BlockingQueue<D> data) {
        try {
            return data.poll(timeoutMillis, MILLISECONDS);
        } catch (InterruptedException e) {
            throw new CheckInterruptedException("Check of " + this + " was interrupted", e);
        }
    }

    private boolean match(Check<? super D> check, BlockingQueue<D> data, Aggregator resultBuilder, ResultFactory factory) {
        for(D item = get(data); item != null; item = get(data)) {
            if(resultBuilder.add(check.evaluate(item, factory)).passed()) {
                return true;
            }
            if(exact) {
                return false;
            }
        }
        return false;
    }

    @Override
    public Result evaluate(BlockingQueue<D> data, ResultFactory factory) {
        if(data == null) {
            return factory.expectation(this, false);
        }
        Aggregator resultBuilder = factory.aggregator(this);
        for(Check<? super D> check : checks) {
            if(!match(check, data, resultBuilder, factory)) {
                return resultBuilder.build(check + " not matched by any item", false);
            }
        }
        if(full && exact) {
            D nextItem = get(data);
            if(nextItem != null) {
                return resultBuilder.build("Extra item " + nextItem, false);
            }
        }
        return resultBuilder.build("Items matched checks", true);
    }

    @Override
    public String toString() {
        return "Items in queue matching " + checks;
    }

}
