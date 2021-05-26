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

package fluent.validation.result;

import fluent.validation.Check;

import java.util.ArrayList;
import java.util.List;

public interface ResultFactory {

    Result actual(Object actualValue, Result result);

    Result expectation(Object expectation, boolean value);

    Result named(Object name, Result result, boolean value);

    Result aggregation(Object prefix, String glue, List<Result> items, boolean value);

    default <D> TableAggregator<D> table(Object prefix, ArrayList<Check<? super D>> checks) {
        return new TableAggregator<D>() {
            private final List<Object> values = new ArrayList<>();
            private final List<TableInResult.Cell> results = new ArrayList<>();
            @Override public Result build(String description, int column, boolean value) {
                return new ActualValueInResult(description, new TableInResult(description, (List<Check<?>>) (List) checks, values, results, value));
            }
            @Override public Result build(String description, boolean value) {
                return new ActualValueInResult(description, new TableInResult(description, (List<Check<?>>) (List) checks, values, results, value));
            }
            @Override public Result cell(int row, int column, Result result) {
                results.add(new TableInResult.Cell(row, column, result));
                return result;
            }
            @Override public int column(Object item) {
                values.add(item);
                return values.size() - 1;
            }
        };
    }

    Result error(Throwable throwable);

    Result invert(Result result);

    default Aggregator aggregator(Object prefix, String glue) {
        return new Aggregator() {
            private final List<Result> items = new ArrayList<>();
            @Override public Result add(Result itemResult) {
                items.add(itemResult);
                return itemResult;
            }
            @Override public Result build(Object actualValueDescription, boolean result) {
                return actual(actualValueDescription, aggregation(prefix, glue, items, result));
            }
        };
    }

    default Aggregator aggregator(Object prefix) {
        return aggregator(prefix, ", ");
    }

    ResultFactory DEFAULT = new ResultFactory() {
        @Override public Result actual(Object actualValue, Result result) {
            return new ActualValueInResult(actualValue, result);
        }
        @Override public Result expectation(Object expectation, boolean value) {
            return new ExpectationInResult(expectation, value);
        }
        @Override public Result named(Object name, Result result, boolean value) {
            return new TransformationInResult(name, result, value);
        }
        @Override public Result aggregation(Object prefix, String glue, List<Result> items, boolean value) {
            return new AggregationInResult(prefix, glue, items, value);
        }
        @Override public Result error(Throwable throwable) {
            return new ErrorInResult(throwable);
        }
        @Override public Result invert(Result result) {
            return new InvertFailureIndicatorInResult(result);
        }
    };

}
