/*
 * BSD 2-Clause License
 *
 * Copyright (c) 2022, Ondrej Fischer
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package fluent.validation;

/**
 * Fluent DSL to compose a complex check using partial checks applicable either on the tested object directly, or using
 * some transformation (a.k.a. field access).
 *
 * All directly chained checks are evaluated as logical and.
 * Using the method or() is allowing to use also logical or, with the priority of operators applied (and has precedence).
 *
 * @param <L> Type of the final DSL class / interface re-using / implementing this interface.
 * @param <D> Type of the data object to be checked with the resulting complex check.
 */
public interface CheckDsl<L, D> extends Check<D> {

    /**
     * Add check of the root object to the
     * @param check Additional check to add to the general logical and.
     * @return The fluent DSL allowing further building of the check.
     */
    L with(Check<? super D> check);

    /**
     * Add a check of a transformed value, e.g. field access.
     *
     * @param name Name of the transformation / field
     * @param transformation Transformation function to apply in order to check the result.
     * @param <V> Type of the transformed value.
     * @return The fluent DSL allowing further building of the check.
     */
    @Deprecated
    default <V> TransformationBuilder<V, L> withField(String name, Transformation<? super D, V> transformation) {
        return has(name, transformation);
    }

    /**
     * Add a check of a transformed value, e.g. field access. This variant will try to "guess" the name of the
     * transformation / field.
     *
     * @param transformation Transformation function to apply in order to check the result.
     * @param <V> Type of the transformed value.
     * @return The fluent DSL allowing further building of the check.
     */
    @Deprecated
    default <V> TransformationBuilder<V, L> withField(Transformation<? super D, V> transformation) {
        return has(transformation);
    }

    /**
     * Add a check of a transformed value, e.g. field access.
     *
     * @param name Name of the transformation / field
     * @param transformation Transformation function to apply in order to check the result.
     * @param <V> Type of the transformed value.
     * @return The fluent DSL allowing further building of the check.
     */
    <V> TransformationBuilder<V, L> has(String name, Transformation<? super D, V> transformation);

    /**
     * Add a check of a transformed value, e.g. field access. This variant will try to "guess" the name of the
     * transformation / field.
     *
     * @param transformation Transformation function to apply in order to check the result.
     * @param <V> Type of the transformed value.
     * @return The fluent DSL allowing further building of the check.
     */
    <V> TransformationBuilder<V, L> has(Transformation<? super D, V> transformation);

    /**
     * Allow chaining of checks using logical or. Keep in mind that sequence of directly chained field checks are always
     * evaluated with logical and. So e.g.
     *
     * {@code
     *      check.withField(MyClass::getFirstName).equalTo("John").withField(MyClass::getLastName).equalTo("Doe")
     *           .or()
     *           .withField(MyClass::getFirstName).equalTo("Jane").withField(MyClass::getLastName).equalTo("Dee")
     * }
     *
     * evaluates as:
     *
     *    (firstName == "John" and lastName == "Doe") or (firstName == "Jane" and lastName == "Dee")
     *
     * @return The fluent DSL allowing further building of the check.
     */
    L or();

}
