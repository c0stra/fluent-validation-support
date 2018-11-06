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

import fluent.validation.Check;

public final class Mismatch implements CheckDetail {

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
    public void trace(String expectation, Object actualValue, boolean result) {
        if(result == indicateFailure) {
            builder.append(desc).append("Expected: ")
                    .append(expectation)
                    .append(", actual: <")
                    .append(actualValue).append('>');
        }
    }

    @Override
    public Node node(Check<?> name) {
        return new MismatchNode(name.toString());
    }

    @Override
    public CheckDetail label(Check<?> name) {
        builder.append(name.name()).append(": ");
        return this;
    }

    @Override
    public CheckDetail prefix(Check<?> check) {
        return new Mismatch(indicateFailure, builder, check.name() + " ");
    }

    @Override
    public CheckDetail negative() {
        return new Mismatch(true, builder, desc);
    }

    @Override
    public String toString() {
        return builder.toString();
    }


    private class MismatchNode implements Node {

        StringBuilder optional;

        public MismatchNode(String name) {
            optional = new StringBuilder("Expected: ").append(name).append(", but:");
        }

        @Override
        public CheckDetail detailFailingOn(boolean newIndicateFailure) {
            return new Mismatch(newIndicateFailure != indicateFailure, optional, "\n\t");
        }

        @Override
        public void trace(Object actualData, boolean result) {
            if(result == indicateFailure) {
                builder.append(optional);
            }
        }

    }
}
