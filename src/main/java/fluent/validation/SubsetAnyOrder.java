/*
 * Copyright © 2018 Ondrej Fischer. All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that
 * the following checks are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of checks and the
 *    following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of checks and the
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

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

final class SubsetAnyOrder<D> implements Check<Iterable<D>> {

    private final Collection<Check<? super D>> checks;

    SubsetAnyOrder(Collection<Check<? super D>> checks) {
        this.checks = checks;
    }

    @Override
    public boolean test(Iterable<D> data, CheckVisitor checkVisitor) {
        List<Check<? super D>> set = new LinkedList<>(checks);
        for (D item : data) {
            Iterator<Check<? super D>> i = set.iterator();
            while (i.hasNext()) {
                if(i.next().test(item, checkVisitor)) {
                    i.remove();
                    break;
                }
            }
            if(set.isEmpty()) {
                return true;
            }
        }
        return false;
    }

}
