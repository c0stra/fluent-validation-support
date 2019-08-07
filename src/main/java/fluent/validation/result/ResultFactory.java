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
            private final List<D> values = new ArrayList<>();
            private final List<TableInResult.Cell> results = new ArrayList<>();
            @Override public Result build(String description, int column, boolean value) {
                return new ActualValueInResult(description, new TableInResult(description, (List<Check<?>>) (List) checks, values, results, value));
            }
            @Override public Result build(String description, boolean value) {
                return new ActualValueInResult(description, new TableInResult(description, (List<Check<?>>) (List) checks, values, results, value));
            }
            @Override public void cell(int row, int column, Result result) {
                results.add(new TableInResult.Cell(row, column, result));
            }
            @Override public void satisfy(String description, int row, int column, Result result) {

            }
            @Override public int column(D item) {
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
