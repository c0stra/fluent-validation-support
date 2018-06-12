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

package fluent.validation.evaluation;

import fluent.validation.Condition;
import fluent.validation.detail.EvaluationLogger;

import java.util.Objects;

public interface Statement extends Condition<Context>, Conclusion {

    @Override
    default boolean test(Context data, EvaluationLogger evaluationLogger) {
        return Condition.trace(evaluationLogger, toString(), "is valid", data.isValid(this));
    }

    @Override
    default void conclude(Boolean aBoolean, Context context, EvaluationLogger logger) {
        context.set(this, aBoolean);
    }

    static Statement state(String description) {
        Objects.requireNonNull(description, "Description of statement cannot be null!");
        return new Statement() {
            @Override public String toString() {
                return description;
            }
            @Override public int hashCode() {
                return description.hashCode();
            }
            @Override public boolean equals(Object obj) {
                return obj != null && getClass() == obj.getClass() && description.equals(obj.toString());
            }
        };
    }

}
