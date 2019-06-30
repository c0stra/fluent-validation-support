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

import fluent.validation.result.*;

import java.util.Iterator;

/**
 * Check, making sure, that an actual collection meets provided conditions in exact order:
 *   1st item matches 1st check, 2nd item matches 2nd check, etc. and there must not be any item missing or
 *   extra (length of actual collection needs to match length of collection of conditions).
 *
 * @param <D> Type of the items in the collection.
 */
final class CollectionCheckInOrder<D> extends Check<Iterable<D>> {

    private final Iterable<Check<? super D>> checks;
    private final boolean full;
    private final boolean exact;

    CollectionCheckInOrder(Iterable<Check<? super D>> checks, boolean full, boolean exact) {
        this.checks = checks;
        this.full = full;
        this.exact = exact;
    }

    private boolean match(Check<? super D> check, Iterator<D> d, Aggregator resultBuilder, ResultFactory factory) {
        while (d.hasNext()) {
            D item = d.next();
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
    public Result evaluate(Iterable<D> data, ResultFactory factory) {
        if(data == null) {
            return factory.expectation(this, false);
        }
        Aggregator resultBuilder = factory.aggregator(this);
        Iterator<D> d = data.iterator();
        for(Check<? super D> check : checks) {
            if(!match(check, d, resultBuilder, factory)) {
                return resultBuilder.build(check + " not matched by any item", false);
            }
        }
        if(full && exact && d.hasNext()) {
            return resultBuilder.build("Extra item " + d.next(), false);
        }
        return resultBuilder.build("Items matched checks", true);
    }

    @Override
    public String toString() {
        return "Items matching " + checks;
    }

}
