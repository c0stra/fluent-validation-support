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

import fluent.validation.result.FailedResultVisitor;
import fluent.validation.result.ResultVisitor;

/**
 * Utility class responsible for assertion of the data.
 * Assertion means to check data using a `Check`, and fail with `AssertionFailure` when Check evaluation
 * returns false.
 * The AssertionFailure error message should describe the evaluation of a check. By default it is extracting
 * partial failures relevant to the over-all one.
 */
public final class Assert {
    private Assert() {}

    /**
     * Assert the data using provided check.
     *
     * @param data Tested data.
     * @param check Check to be applied.
     * @param <T> Type of the tested data.
     * @return The tested data, if the assertion passed.
     */
    public static <T> T that(T data, Check<? super T> check) {
        return that(data, check, new FailedResultVisitor());
    }


    /**
     * Assert the data using provided check.
     *
     * @param data Tested data.
     * @param check Check to be applied.
     * @param visitor Result visitor used to interpret the detailed information about check evaluation.
     * @param <T> Type of the tested data.
     * @return The tested data, if the assertion passed.
     */
    public static <T> T that(T data, Check<? super T> check, ResultVisitor visitor) {
        if(!Check.that(data, check, visitor)) {
            throw new AssertionFailure(visitor);
        }
        return data;
    }

}
