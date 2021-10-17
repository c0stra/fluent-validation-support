/*
 * BSD 2-Clause License
 *
 * Copyright (c) 2021, Ondrej Fischer
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

import fluent.api.End;
import fluent.validation.result.Result;
import fluent.validation.result.ResultFactory;
import fluent.validation.result.ResultVisitor;

/**
 * Simple check used in for validation of various data.
 *
 * It tests, if value meets a criteria described by the check.
 *
 * But the main purpose of this class is to provide transparency during any complex check evaluation, similar to
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
 * @param <T> Type of the data to be tested using this check.
 */
public interface Check<T> {

    /**
     * Abstract method to delegate implementation of specific check logic to subclasses.
     * @param data Tested data.
     * @param factory Result factory responsible for creating result tree from current node.
     * @return representation of outcome of the test.
     */
    @End(message = "Check is not used. Pass it either to Assert.that(), Check.that() or Check.evaluate().")
    Result evaluate(T data, ResultFactory factory);

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

    /**
     * Assert the data using provided check.
     *
     * @param data Tested data.
     * @param check Check to be applied.
     * @param <T> Type of the tested data.
     */
    @End(message = "Check is not used. Pass it either to Assert.that(), Check.that() or Check.evaluate().")
    static <T> boolean that(T data, Check<? super T> check) {
        return evaluate(data, check).passed();
    }

    static <T> boolean that(T data, Check<? super T> check, ResultVisitor visitor) {
        Result result = evaluate(data, check);
        visitor.visit(result);
        return result.passed();
    }

    static <T> Result evaluate(T data, Check<? super T> check) {
        return ResultFactory.DEFAULT.actual(data, check.evaluate(data, ResultFactory.DEFAULT));
    }

}
