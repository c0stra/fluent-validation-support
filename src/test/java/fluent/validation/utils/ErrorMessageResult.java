package fluent.validation.utils;

import fluent.validation.result.CheckDescription;
import fluent.validation.result.Result;
import fluent.validation.result.ResultVisitor;

import java.util.List;

public class ErrorMessageResult extends Result {

    private final Result result;

    public ErrorMessageResult(Result result) {
        super(result.passed());
        this.result = result;
    }

    @Override
    public void accept(ResultVisitor visitor) {
        result.accept(new ResultVisitor() {

            @Override
            public void predicateResult(Object expectation, Object actual, boolean result) {
                visitor.predicateResult("---\n" + expectation + "\n---", "---\n" + actual + "\n---", result);
            }

            @Override
            public void targetResult(CheckDescription target, boolean result, Result dependency) {
                visitor.targetResult(target, result, dependency);
            }

            @Override
            public void groupResult(Object description, Object actualValueDescription, boolean result, List<Result> itemResults) {
                visitor.groupResult(description, actualValueDescription, result, itemResults);
            }

            @Override
            public void exceptionResult(Throwable throwable, boolean result) {
                visitor.exceptionResult(throwable, result);
            }

            @Override
            public void binaryOperationResult(String operator, boolean passed, Result leftResult, Result rightResult) {
                visitor.binaryOperationResult(operator, passed, leftResult, rightResult);
            }

        });
    }

}
