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
public final class ComparisonChecks {

    private ComparisonChecks() {}

    /* ------------------------------------------------------------------------------------------------------
     * Comparison conditions.
     * ------------------------------------------------------------------------------------------------------
     */

    public static <D> Check<D> lessThan(D operand, Comparator<D> comparator) {
        return check(data -> comparator.compare(data, operand) < 0, "< " + operand);
    }

    public static <D> Check<D> moreThan(D operand, Comparator<D> comparator) {
        return check(data -> comparator.compare(data, operand) > 0, "> " + operand);
    }

    public static <D> Check<D> equalOrLessThan(D operand, Comparator<D> comparator) {
        return check(data -> comparator.compare(data, operand) <= 0, "<= " + operand);
    }

    public static <D> Check<D> equalOrMoreThan(D operand, Comparator<D> comparator) {
        return check(data -> comparator.compare(data, operand) >= 0, ">= " + operand);
    }

    public static <D extends Comparable<D>> Check<D> lessThan(D operand) {
        return lessThan(operand, Comparable::compareTo);
    }

    public static <D extends Comparable<D>> Check<D> moreThan(D operand) {
        return moreThan(operand, Comparable::compareTo);
    }

    public static <D extends Comparable<D>> Check<D> equalOrLessThan(D operand) {
        return equalOrLessThan(operand, Comparable::compareTo);
    }

    public static <D extends Comparable<D>> Check<D> equalOrMoreThan(D operand) {
        return equalOrMoreThan(operand, Comparable::compareTo);
    }

    public static <D extends Comparable<D>> Check<D> between(D left, D right) {
        return between(left, right, Comparable::compareTo);
    }

    public static <D> Check<D> between(D left, D right, Comparator<D> comparator) {
        if(comparator.compare(left, right) > 0) comparator = comparator.reversed();
        return moreThan(left, comparator).and(lessThan(right, comparator));
    }

    public static <D> Check<D> betweenInclusive(D left, D right, Comparator<D> comparator) {
        if(comparator.compare(left, right) > 0) comparator = comparator.reversed();
        return equalOrMoreThan(left, comparator).and(equalOrLessThan(right, comparator));
    }

    public static <D extends Comparable<D>> Check<D> betweenInclusive(D left, D right) {
        return betweenInclusive(left, right, Comparable::compareTo);
    }

}
