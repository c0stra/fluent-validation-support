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

import fluent.validation.detail.CheckVisitor;

/**
 * Simple condition interface used in for validation of various data.
 *
 * It implements the Java 8 Predicate, providing simple #test(V value) method,  which simply tests, if value meets
 * a condition (pretty the same).
 *
 * But the main purpose of this class is to provide transparency during any complex condition evaluation, similar to
 * Hamcrest Matchers.
 * If you are familiar with Hamcrest matchers, it should be easy to get familiar with Checks too. There are only
 * following differences:
 *
 * 1. Evaluation detail is not limited to mismatch description. You can achieve full detail even for passed ones.
 * 2. This interface is type-safe on application (see test() methods are accepting class type argument, not Object).
 *    It may be limiting, but helps avoid test errors.
 * 3. Thanks to it being generic, it's very simple to simplify creation of any custom, or composed conditions with
 *    advantage of Java 8 functional interfaces (lambdas).
 *
 * @param <T> Type of the data to be tested using this condition.
 */
public interface Check<T> {

    /**
     * Simple evaluation of the condition on provided data.
     *
     * @param data Data to test by the condition.
     * @return Evaluation result.
     */
    default boolean test(T data) {
        return test(data, CheckVisitor.NONE);
    }

    /**
     * Evaluation of the condition on provided data, able to provide full detail of the evaluation using
     * provided detail collector.
     *
     * @param data Data to test by the condition.
     * @param checkVisitor Tracer of the evaluation detail.
     * @return Evaluation result.
     */
    boolean test(T data, CheckVisitor checkVisitor);

    default void assertData(T data) {
        if(!test(data)) {
            throw new AssertionFailure("Expected: " + this + ", actual: <" + data + ">");
        }
    }

    default void assertData(T data, CheckVisitor checkVisitor) {
        boolean result = test(data, checkVisitor);
        checkVisitor.trace(data, result);
        if(!result) {
            throw new AssertionFailure(checkVisitor.toString());
        }
    }

    /**
     * Name of the check.
     * @return The name.
     */
    default String name() {
        return toString();
    }

    /**
     * Compose this check with another one using logical AND operator.
     *
     * @param operand Another check.
     * @param <U> Type of the data tested by other check. It may cause up-cast.
     * @return Composed check.
     */
    default <U extends T> Check<U> and(Check<? super U> operand) {
        return new And<>(this, operand);
    }

    /**
     * Compose this check with another one using logical OR operator.
     *
     * @param operand Another check.
     * @param <U> Type of the data tested by other check. It may cause up-cast.
     * @return Composed check.
     */
    default <U extends T> Check<U> or(Check<? super U> operand) {
        return new Or<>(this, operand);
    }

    static <T> void that(T data, Check<? super T> check) {
        check.assertData(data);
    }

    static <T> void that(T data, Check<? super T> check, CheckVisitor logger) {
        check.assertData(data, logger);
    }

    /**
     * Shortcut method, that traces current node detail, and returns result.
     *
     * @param node Tracer node wrapping composed condition details.
     * @param actualValue Actual value.
     * @param result Result of the composed condition.
     * @return Result of the composed condition.
     */
    static boolean trace(CheckVisitor node, Object actualValue, boolean result) {
        node.trace(actualValue, result);
        return result;
    }

    /**
     * Shortcut method, that traces current leaf detail, and returns result.
     *
     * @param checkVisitor Tracer for tracing condition details.
     * @param expectation Description of the expectation.
     * @param actualValue Actual value.
     * @param result Result of the condition.
     * @return Result of the condition.
     */
    static boolean trace(CheckVisitor checkVisitor, String expectation, Object actualValue, boolean result) {
        checkVisitor.trace(expectation, actualValue, result);
        return result;
    }

}
