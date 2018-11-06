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

import fluent.validation.detail.CheckVisitor;

import java.util.Iterator;

import static fluent.validation.Check.trace;

/**
 * Check, making sure, that an actual collection meets provided conditions in exact order:
 *   1st item matches 1st condition, 2nd item matches 2nd condition, etc. and there must not be any item missing or
 *   extra (length of actual collection needs to match length of collection of conditions).
 *
 * @param <D> Type of the items in the collection.
 */
final class CollectionInOrder<D> implements Check<Iterable<D>> {

    private final Iterable<Check<? super D>> conditions;

    CollectionInOrder(Iterable<Check<? super D>> conditions) {
        this.conditions = conditions;
    }

    @Override
    public boolean test(Iterable<D> data, CheckVisitor checkVisitor) {
        Iterator<Check<? super D>> c = conditions.iterator();
        Iterator<D> d = data.iterator();
        CheckVisitor.Node node = checkVisitor.node(this);
        while (c.hasNext() && d.hasNext()) {
            Check<? super D> check = c.next();
            if(!check.test(d.next(), node.detailFailingOn(false))) {
                return trace(node, "No item matching " + check, false);
            }
        }
        if(c.hasNext()) {
            return trace(node, "No item matching " + c.next(), false);
        }
        if(d.hasNext()) {
            return trace(node, "Extra items " + d.next(), false);
        }
        return trace(node, "Matched", true);
    }

    @Override
    public String name() {
        return "Items matching";
    }

    @Override
    public String toString() {
        return "Items matching " + conditions;
    }
}
