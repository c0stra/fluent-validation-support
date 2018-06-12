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

package fluent.validation.detail;

public final class Mismatch implements EvaluationLogger {

    private final boolean indicateFailure;
    private final StringBuilder builder;
    private final String desc;

    private Mismatch(boolean indicateFailure, StringBuilder builder, String name) {
        this.indicateFailure = indicateFailure;
        this.builder = builder;
        this.desc = name;
    }

    public Mismatch() {
        this(false, new StringBuilder(), "");
    }

    @Override
    public void trace(String expectationDescription, Object actualData, boolean result) {
        if(result == indicateFailure) {
            builder.append("Expected: ")
                    .append(desc)
                    .append(expectationDescription)
                    .append(" but actual: ")
                    .append(actualData);
        }
    }

    @Override
    public Node node(String name) {
        return new Node() {
            StringBuilder b = new StringBuilder();
            @Override
            public EvaluationLogger detailFailingOn(boolean newIndicateFailure) {
                return new Mismatch(newIndicateFailure != indicateFailure, b, desc + name + " ");
            }

            @Override
            public void trace(Object actualData, boolean result) {
                if(result == indicateFailure) {
                    builder.append(b);
                }
            }

        };
    }

    @Override
    public String toString() {
        return builder.toString();
    }

}
