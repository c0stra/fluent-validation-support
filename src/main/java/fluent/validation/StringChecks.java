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
public final class StringChecks {
    private StringChecks() {}

    /* ------------------------------------------------------------------------------------------------------
     * String conditions.
     * ------------------------------------------------------------------------------------------------------
     */

    public static Check<String> equalToCaseInsensitive(String expectedValue) {
        return condition(expectedValue::equalsIgnoreCase, "any case " + expectedValue);
    }

    public static Check<String> emptyString() {
        return nullableCondition(data -> Objects.isNull(data) || data.isEmpty(), "is empty string");
    }

    public static Check<String> startsWith(String prefix) {
        return condition(data -> data.startsWith(prefix), "starts with <" + prefix + ">");
    }

    public static Check<String> startsWithCaseInsensitive(String prefix) {
        return condition(data -> data.toLowerCase().startsWith(prefix.toLowerCase()), "starts with " + prefix);
    }

    public static Check<String> endsWith(String suffix) {
        return condition(data -> data.startsWith(suffix), "ends with %s" + suffix);
    }

    public static Check<String> endsWithCaseInsensitive(String suffix) {
        return condition(data -> data.toLowerCase().startsWith(suffix.toLowerCase()), "ends with " + suffix);
    }

    public static Check<String> contains(String substring) {
        return condition(data -> data.contains(substring), "contains "+ substring);
    }

    public static Check<String> containsCaseInsensitive(String substring) {
        return condition(data -> data.toLowerCase().contains(substring.toLowerCase()), "contains " + substring);
    }

    public static Check<String> matches(Pattern pattern) {
        return condition(pattern.asPredicate(), "matches /" + pattern + '/');
    }

    public static Check<String> matchesPattern(String pattern) {
        return matches(Pattern.compile(pattern));
    }

}
