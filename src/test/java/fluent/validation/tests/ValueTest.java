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

package fluent.validation.tests;

import fluent.validation.Assert;
import fluent.validation.AssertionFailure;
import fluent.validation.Checks;
import fluent.validation.Value;
import org.testng.annotations.Test;

import static fluent.validation.Checks.*;
import static fluent.validation.Items.items;

public class ValueTest {

    @Test
    public void testStoredValue() {
        Value<String> stringValue = value();
        Assert.that(123, Checks.has(Object::toString).matching(stringValue.storeWhen(isNotNull())));
        Assert.that(stringValue.get(), is("123"));
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testNotStoredValue() {
        Value<String> stringValue = value();
        Assert.that(stringValue.get(), is("123"));
    }

    @Test
    public void testNullValue() {
        Value<Object> value = value();
        Assert.that(null, value.storeWhen(anything()));
        Assert.that(value.get(), isNull());
    }

    @Test
    public void testMultipleStore() {
        Value<String> value = value();
        Assert.that("A", value.storeWhen(isNotNull()));
        Assert.that("A", value.storeWhen(isNotNull()));
        Assert.that("B", value.storeWhen(isNotNull()));
        Assert.that(value.get(), equalTo("B"));
    }

    @Test
    public void testMultipleValues() {
        Value<String> value = value();
        Assert.that("A", value.storeWhen(isNotNull()));
        Assert.that("A", value.storeWhen(isNotNull()));
        Assert.that("B", value.storeWhen(isNotNull()));
        Assert.that(value.getAll(), collection(equalTo(items("A", "A", "B"))));
    }

    @Test(expectedExceptions = AssertionFailure.class, expectedExceptionsMessageRegExp = ".*expected: \\(<A>, <B>\\).*")
    public void testMultipleValuesNegative() {
        Value<String> value = value();
        Assert.that("A", value.storeWhen(isNotNull()));
        Assert.that("A", value.storeWhen(isNotNull()));
        Assert.that("B", value.storeWhen(isNotNull()));
        Assert.that(value.getAll(), collection(equalTo(items("A", "B"))));
    }

}
