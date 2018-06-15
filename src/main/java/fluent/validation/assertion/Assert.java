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

package fluent.validation.assertion;

import fluent.validation.Condition;
import test.Failure;
import fluent.validation.detail.EvaluationLogger;
import fluent.validation.detail.EvaluationLoggerService;
import fluent.validation.detail.Mismatch;

import java.util.Iterator;
import java.util.function.Supplier;

import static java.util.ServiceLoader.load;

/**
 * Assertion builder and factory. It allows to build assertion step by step, or apply all at once.
 *
 * Assertion in general in "focus" framework has following actors:
 *
 * Value to be tested
 * Condition used to perform the test
 * Supplier of test detail interceptor.
 *
 * This class allows following DSL to deal with assertions:
 *
 * <pre>
 * {@code
 *
 *  // 1. Out of the box static constructs, that use implicit tracer, only describing mismatch
 *  // ---------------------------------------------------------------------------------------
 *
 *  // Simplest usage to perform assertion out of the box pass data and condition.
 *  Assert.that(value, equalTo(expectedValue));
 *
 *  // DSL usable to perform the assertion separately from passing the data
 *  Assert.that(value).satisfy(equalTo(expectedValue));
 *
 *
 *  // 2. Non-static constructs available on prepared assert builder
 *  // -------------------------------------------------------------
 *
 *  // Simple assertion providing both, tested value, and criteria.
 *  assertion.assertThat(value, equalTo(expectedValue));
 *
 *  // DSL to perform the assertion separately from passing the data in.
 *  assertion.assertThat(value).satisfy(equalTo(expectedValue));
 *
 *  // 3. Prepare an assertion builder
 *  // -------------------------------
 *
 *  // Prepare default builder with implicit detail tracer.
 *  Assert.builder();
 *
 *  // Use custom tracer for the assertion.
 *  Assert.builder(loggerSupplier);
 *
 * }
 * </pre>
 */
public final class Assert {

    private static final EvaluationLoggerService DEFAULT_EVALUATION_LOGGER_SERVICE = loadLogger(load(EvaluationLoggerService.class).iterator());

    private final Supplier<EvaluationLogger> loggerSupplier;

    private Assert(Supplier<EvaluationLogger> loggerSupplier) {
        this.loggerSupplier = loggerSupplier;
    }

    private static EvaluationLoggerService loadLogger(Iterator<EvaluationLoggerService> i) {
        return i.hasNext() ? i.next() : Mismatch::new;
    }

    /**
     * Prepare assert subject that accepts a condition to assert, if it satisfies it.
     *
     * {@code
     *
     *  assertion.assertThat("Foo").satisfy(contains("F"));
     *
     * }
     *
     * @param value Value to be tested.
     * @return Assertion subject to accept condition for assertion.
     * @param <V> Type of the value to be tested.
     */
    public <V> AssertionSubject<V> assertThat(V value) {
        return new SubjectImpl<>(value);
    }

    /**
     * Simplest way to assert that a value satisfies the condition with default detail service.
     *
     * {@code
     *
     *  Assert.that("Foo", contains("F));
     *
     * }
     *
     * @param value Tested value
     * @param condition Condition used to test the value.
     * @param <V> Type of the value to be tested.
     */
    public static <V> void that(V value, Condition<? super V> condition) {
        that(value).satisfy(condition);
    }

    /**
     * Prepare assert subject that accepts a condition to assert, if it satisfies it.
     * Subject created this way uses default detail service.
     *
     * {@code
     *
     *  Assert.that("Foo").satisfy(contains("F));
     *
     * }
     *
     * @param value Tested value
     * @param <V> Type of the value to be tested.
     */
    public static <V> AssertionSubject<V> that(V value) {
        return assertion().assertThat(value);
    }

    /**
     * Create assert with provided detail supplier.
     *
     * @param detailSupplier
     * @return Assert with provided detail supplier.
     */
    public static Assert assertion(Supplier<EvaluationLogger> detailSupplier) {
        return new Assert(detailSupplier);
    }

    /**
     * Create assert with default detail supplier, specific for provided context.
     *
     * @param context Specific context.
     * @return Assert with default detail supplier.
     */
    public static Assert assertion(Object context) {
        return assertion(DEFAULT_EVALUATION_LOGGER_SERVICE.get(context));
    }

    /**
     * Create assert with default detail supplier.
     *
     * @return Assert with default detail supplier.
     */
    public static Assert assertion() {
        return assertion(DEFAULT_EVALUATION_LOGGER_SERVICE);
    }


    private class SubjectImpl<V> implements AssertionSubject<V> {

        private final V value;

        private SubjectImpl(V value) {
            this.value = value;
        }

        @Override
        public void satisfy(Condition<? super V> condition) {
            EvaluationLogger detail = loggerSupplier.get();
            if(!condition.test(value, detail)) {
                throw new Failure(detail);
            }
        }

    }

}
