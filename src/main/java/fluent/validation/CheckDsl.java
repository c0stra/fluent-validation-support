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

import fluent.validation.result.Result;
import fluent.validation.result.ResultFactory;

import java.util.function.Function;

import static fluent.validation.BasicChecks.anything;
import static fluent.validation.BasicChecks.has;

public class CheckDsl<L, D> extends Check<D> {

    private final Check<? super D> check;

    private final Function<Check<D>, L> factory;

    protected CheckDsl(Check<? super D> check, Function<Check<D>, L> factory) {
        this.check = check;
        this.factory = factory;
    }

    protected CheckDsl(Function<Check<D>, L> factory) {
        this(anything(), factory);
    }

    public L with(Check<? super D> check) {
        return factory.apply(this.check.and(check));
    }

    public <V> Builder<V, L> withField(String name, Function<? super D, V> function) {
        return condition -> with(has(name, function).matching(condition));
    }

    public L or() {
        return factory.apply(check.or(anything()));
    }

    @Override
    public Result evaluate(D data, ResultFactory factory) {
        return check.evaluate(data, factory);
    }

    @Override
    public String toString() {
        return check.toString();
    }

    public static class Final<D> extends CheckDsl<Final<D>, D> {

        Final(Check<? super D> check) {
            super(check, Final::new);
        }

        Final() {
            super(Final::new);
        }

    }

}
