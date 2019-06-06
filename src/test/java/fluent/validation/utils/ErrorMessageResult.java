package fluent.validation.utils;

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
            @Override public void actual(Object actualValue, Result result) {

            }
            @Override public void expectation(Object expectation, boolean result) {
                visitor.expectation("---\n" + expectation + "\n---", result);
            }
            @Override public void transformation(Object name, Result dependency, boolean value) {
                visitor.transformation(name, dependency, value);
            }
            @Override public void aggregation(Object prefix, String glue, List<Result> itemResults, boolean result) {
                visitor.aggregation(prefix, glue, itemResults, result);
            }
            @Override public void error(Throwable throwable) {
                visitor.error(throwable);
            }
            @Override public void invert(Result result) {
                visitor.invert(result);
            }

        });
    }

}
