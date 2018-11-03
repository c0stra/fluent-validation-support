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

import fluent.validation.detail.EvaluationLogger;

import static fluent.validation.Check.trace;

final class Operator<D> implements Check<D> {

    public enum BooleanOperator {
        AND {
            @Override boolean evaluate(boolean left, boolean right) {
                return left & right;
            }
        }, OR {
            @Override boolean evaluate(boolean left, boolean right) {
                return left | right;
            }
        };
        abstract boolean evaluate(boolean left, boolean right);

        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }
    }

    private final Check<? super D> left;
    private final Check<? super D> right;
    private final BooleanOperator operator;

    Operator(Check<? super D> left, Check<? super D> right, BooleanOperator operator) {
        this.left = left;
        this.right = right;
        this.operator = operator;
    }

    @Override
    public boolean test(D data, EvaluationLogger evaluationLogger) {
        EvaluationLogger.Node node = evaluationLogger.node(this);
        boolean result = operator.evaluate(left.test(data, node.detailFailingOn(false)), right.test(data, node.detailFailingOn(false)));
        return trace(node, "", result);
    }

    @Override
    public String name() {
        return operator.toString();
    }

    @Override
    public String toString() {
        return left + " " + operator + " " + right;
    }

}
