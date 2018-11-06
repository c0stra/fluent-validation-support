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

public final class MismatchVisitor implements CheckVisitor {

    private final StringBuilder builder = new StringBuilder();

    @Override
    public void trace(String expectation, Object actualValue, boolean result) {
        builder.append("Expected: ").append(expectation).append(", actual: <").append(actualValue).append('>');
    }

    @Override
    public CheckVisitor node(Check<?> name) {
        return NoVisitor.NONE.node(name);
    }

    @Override
    public CheckVisitor label(Check<?> name) {
        return NoVisitor.NONE;
    }

    @Override
    public void trace(Object data, boolean result) {
        builder.append(", but actual: <").append(data).append('>');
    }

    @Override
    public String toString() {
        return builder.toString();
    }

}