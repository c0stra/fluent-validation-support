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

import java.util.*;

final class AnyOrderCheck<D> extends Check<Iterator<D>> {

    private final String elementName;
    private final List<Check<? super D>> checks;
    private final boolean full;
    private final boolean exact;

    AnyOrderCheck(String elementName, Collection<Check<? super D>> checks, boolean full, boolean exact) {
        this.elementName = elementName;
        this.checks = new ArrayList<>(checks);
        this.full = full;
        this.exact = exact;
    }

    private boolean matchesAnyAndRemoves(D item, List<Check<? super D>> checks, Aggregator resultBuilder, ResultFactory factory) {
        Iterator<Check<? super D>> c = checks.iterator();
        while (c.hasNext()) {
            if (resultBuilder.add(c.next().evaluate(item, factory)).passed()) {
                c.remove();
                return true;
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
        final List<Check<? super D>> copy = new LinkedList<>(this.checks);
        while (data.hasNext()) {
            if(copy.isEmpty()) {
                return full ? resultBuilder.build("Extra items found", false) : resultBuilder.build("Prefix matched", true);
            }
            if (!matchesAnyAndRemoves(data.next(), copy, resultBuilder, factory) && exact) {
                return resultBuilder.build("Extra items found", false);
            }
        }
        return resultBuilder.build(copy.isEmpty()? "All checks satisfied": "" + copy.size() + " checks not satisfied", copy.isEmpty());
    }

    @Override
    public String toString() {
        return elementName + "s matching in any order " + checks;
    }

}
