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

import fluent.validation.result.GroupResult;
import fluent.validation.result.Result;

import java.util.Iterator;

/**
 * Check, making sure, that an actual collection meets provided conditions in exact order:
 *   1st item matches 1st condition, 2nd item matches 2nd condition, etc. and there must not be any item missing or
 *   extra (length of actual collection needs to match length of collection of conditions).
 *
 * @param <D> Type of the items in the collection.
 */
final class CollectionEqualsInOrder<D> extends Check<Iterable<D>> {

    private final Iterable<Check<? super D>> conditions;

    CollectionEqualsInOrder(Iterable<Check<? super D>> conditions) {
        this.conditions = conditions;
    }

    @Override
    public Result evaluate(Iterable<D> data) {
        GroupResult.Builder resultBuilder = new GroupResult.Builder();
        Iterator<Check<? super D>> c = conditions.iterator();
        Iterator<D> d = data.iterator();
        while (c.hasNext() && d.hasNext()) {
            if(resultBuilder.add(c.next().evaluate(d.next())).failed()) {
                return resultBuilder.build(false);
            }
        }
        if(c.hasNext()) {
            return resultBuilder.build(false);
        }
        if(d.hasNext()) {
            return resultBuilder.build(false);
        }
        return resultBuilder.build(true);
    }

    @Override
    public String toString() {
        return "Items matching " + conditions;
    }

}