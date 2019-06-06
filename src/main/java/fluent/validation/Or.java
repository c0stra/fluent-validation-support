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

import fluent.validation.result.CheckDescription;
import fluent.validation.result.Result;
import fluent.validation.result.ResultFactory;

import static java.util.Arrays.asList;

final class Or<D> extends Check<D> implements CheckDescription {

    private final Check<? super D> left;
    private final Check<? super D> right;

    Or(Check<? super D> left, Check<? super D> right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public Result evaluate(D data, ResultFactory factory) {
        Result leftResult = left.evaluate(data, factory);
        Result rightResult = right.evaluate(data, factory);
        return factory.aggregation("", " or ", asList(leftResult, rightResult), leftResult.passed() || rightResult.passed());
    }

    @Override
    public String toString() {
        return left + " or " + right;
    }

    @Override
    public <U extends D> Check<U> and(Check<? super U> operand) {
        // Building the expression tree with operator priority reflected:
        // A or B and C => A or (B and C)
        return left.or(right.and(operand));
    }

    @Override
    public String description() {
        return "or";
    }
}
