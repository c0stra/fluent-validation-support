package fluent.validation.result;

import java.util.ArrayList;
import java.util.List;

public interface ResultFactory {

    Result actual(Object actualValue, Result result);

    Result expectation(Object expectation, boolean value);

    Result named(Object name, Result result, boolean value);

    Result aggregation(Object prefix, String glue, List<Result> items, boolean value);

    Result error(Throwable throwable);

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
    };

}
