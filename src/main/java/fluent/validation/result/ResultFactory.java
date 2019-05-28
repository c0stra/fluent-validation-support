package fluent.validation.result;

import java.util.List;

public interface ResultFactory {

    Result predicateResult(CheckDescription expectation, Object actual, boolean result);

    Result targetResult(CheckDescription target, Object actual, boolean result, Result dependency);

    Result groupResult(CheckDescription description, Object actualValueDescription, boolean result, List<Result> itemResults);

    Result exceptionResult(Throwable throwable, boolean result);

    GroupResultBuilder groupBuilder(CheckDescription description);

    ResultFactory DEFAULT = new ResultFactory() {
        @Override public Result predicateResult(CheckDescription expectation, Object actual, boolean result) {
            return new PredicateResult(expectation, actual, result);
        }

        @Override public Result targetResult(CheckDescription target, Object actual, boolean result, Result dependency) {
            return new TargetResult(target, result, dependency);
        }

        @Override public Result groupResult(CheckDescription description, Object actualValueDescription, boolean result, List<Result> itemResults) {
            return new GroupResult(result, description, actualValueDescription, itemResults);
        }

        @Override public Result exceptionResult(Throwable throwable, boolean result) {
            return new ExceptionResult(throwable);
        }

        @Override
        public GroupResultBuilder groupBuilder(CheckDescription description) {
            return new GroupResult.Builder(description);
        }
    };

}
