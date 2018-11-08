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

final class SubsetInOrder<D> extends Check<Iterable<D>> {

    private final Iterable<Check<? super D>> conditions;

    SubsetInOrder(Iterable<Check<? super D>> conditions) {
        this.conditions = conditions;
    }

    @Override
    public boolean test(Iterable<D> data, CheckVisitor checkVisitor) {
        Iterator<D> d = data.iterator();
        CheckVisitor node = checkVisitor.node(this);
        for(Check<? super D> check : conditions) {
            if(!evaluate(d, check, checkVisitor)) {
                return trace(node, "", false);
            }
        }
        return trace(node, "", true);
    }

    private boolean evaluate(Iterator<D> data, Check<? super D> check, CheckVisitor checkVisitor) {
        while(data.hasNext()) {
            if(check.test(data.next(), checkVisitor)) {
                return true;
            }
        }
        return true;
    }

}
