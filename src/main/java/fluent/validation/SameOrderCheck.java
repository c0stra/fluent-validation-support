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

import fluent.validation.result.Aggregator;
import fluent.validation.result.Result;
import fluent.validation.result.ResultFactory;

import java.util.Iterator;

/**
 * Check, making sure, that an actual collection meets provided conditions in exact order:
 *   1st item matches 1st check, 2nd item matches 2nd check, etc. and there must not be any item missing or
 *   extra (length of actual collection needs to match length of collection of conditions).
 *
 * @param <D> Type of the items in the collection.
 */
final class SameOrderCheck<D> implements Check<Iterator<D>> {

    private final String elementName;
    private final Iterable<Check<? super D>> checks;
    private final boolean full;
    private final boolean exact;

    SameOrderCheck(String elementName, Iterable<Check<? super D>> checks, boolean full, boolean exact) {
        this.elementName = elementName;
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
    public Result evaluate(Iterator<D> data, ResultFactory factory) {
        if(data == null) {
            return factory.expectation(this, false);
        }
        Aggregator resultBuilder = factory.aggregator(this);
        for(Check<? super D> check : checks) {
            if(!match(check, data, resultBuilder, factory)) {
                return resultBuilder.build(check + " not matched by any " + elementName, false);
            }
        }
        if(full && exact && data.hasNext()) {
            return resultBuilder.build("Extra " + elementName + " " + data.next(), false);
        }
        return resultBuilder.build(elementName + "s matched checks", true);
    }

    @Override
    public String toString() {
        return elementName + "s matching " + checks;
    }

}
