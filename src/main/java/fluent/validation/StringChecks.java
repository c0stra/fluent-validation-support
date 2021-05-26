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

import fluent.validation.processor.Factory;

import java.util.*;
import java.util.regex.Pattern;

import static fluent.validation.BasicChecks.*;

/**
 * Factory of ready to use most frequent conditions. There are typical conditions for following categories:
 *
 * 1. General check builders for simple building of new conditions using predicate and description.
 * 2. General object conditions - e.g. isNull, notNull, equalTo, etc.
 * 3. Generalized logical operators (N-ary oneOf instead of binary or, N-ary allOf instead of binary and)
 * 4. Collection (Iterable) conditions + quantifiers
 * 5. Relational and range conditions for comparables
 * 6. String matching conditions (contains/startWith/endsWith as well as regexp matching)
 * 7. Basic XML conditions (XPath, attribute matching)
 * 8. Floating point comparison using a tolerance
 * 9. Builders for composition or collection of criteria.
 */
@Factory
public final class StringChecks {
    private StringChecks() {}

    /* ------------------------------------------------------------------------------------------------------
     * String conditions.
     * ------------------------------------------------------------------------------------------------------
     */

    public static Check<String> equalCaseInsensitiveTo(String expectedValue) {
        return check(expectedValue::equalsIgnoreCase, "any case " + expectedValue);
    }

    public static Check<String> emptyString() {
        return nullableCheck(data -> Objects.isNull(data) || data.isEmpty(), "is empty string");
    }

    public static Check<String> startsWith(String prefix) {
        return check(data -> data.startsWith(prefix), "starts with <" + prefix + ">");
    }

    public static Check<String> startsCaseInsensitiveWith(String prefix) {
        return check(data -> data.toLowerCase().startsWith(prefix.toLowerCase()), "starts with " + prefix);
    }

    public static Check<String> endsWith(String suffix) {
        return check(data -> data.startsWith(suffix), "ends with %s" + suffix);
    }

    public static Check<String> endsCaseInsensitiveWith(String suffix) {
        return check(data -> data.toLowerCase().startsWith(suffix.toLowerCase()), "ends with " + suffix);
    }

    public static Check<String> contains(String substring) {
        return check(data -> data.contains(substring), "contains "+ substring);
    }

    public static Check<String> containsCaseInsensitive(String substring) {
        return check(data -> data.toLowerCase().contains(substring.toLowerCase()), "contains " + substring);
    }

    public static Check<String> matches(Pattern pattern) {
        return check(v -> pattern.matcher(v).find(), "matches /" + pattern + '/');
    }

    public static Check<String> matchesPattern(String pattern) {
        return matches(Pattern.compile(pattern));
    }

}
