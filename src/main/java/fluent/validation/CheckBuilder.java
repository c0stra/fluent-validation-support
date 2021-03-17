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

/**
 * Simple builder of a check. It is general interface, that can be used in different situations, like composing
 * a complex check using partial ones, or collecting a complex set of conditions.
 *
 * @param <V> Type of the value, for which this builder receives a check.
 * @param <R> Return type.
 */
@FunctionalInterface
public interface CheckBuilder<V, R> {

    /**
     * Set check, that a value must match.
     *
     * @param check Check to be met.
     * @return Followup of the builder.
     */
    R matching(Check<? super V> check);

    /**
     * Set expected value, to which the actual one needs to be equal.
     *
     * @param expectedValue Expected value.
     * @return Followup of the builder.
     */
    default R equalTo(V expectedValue) {
        return matching(BasicChecks.equalTo(expectedValue));
    }

    /**
     * Chain additional transformation, ending up with null safe chaining.
     *
     * @param name Description of the transformation (typically name of a field to access).
     * @param transformation Functional interface to provide transformation logic.
     * @param <U> Type of the transformed value.
     * @return Return check builder for building the check further.
     */
    default <U> CheckBuilder<U, R> having(String name, Transformation<V, U> transformation) {
        return check -> matching(BasicChecks.compose(name, transformation, check));
    }

    /**
     * Chain additional transformation, ending up with null safe chaining.
     *
     * @param transformation Functional interface to provide transformation logic.
     * @param <U> Type of the transformed value.
     * @return Return check builder for building the check further.
     */
    default <U> CheckBuilder<U, R> having(Transformation<V, U> transformation) {
        return having(transformation.getMethodName(), transformation);
    }

}
