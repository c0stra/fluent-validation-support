package fluent.validation.result;

import java.util.List;

public class MismatchResultVisitor implements ResultVisitor {

    private final StringBuilder builder = new StringBuilder();

    @Override
    public void predicateResult(Object expectation, Object actual, boolean result) {
        builder.append("Expected: ").append(expectation).append(" but actual: ").append(actual).append('\n');
    }

    @Override
    public void targetResult(Object target, boolean result, Result dependency) {
        dependency.accept(this);
    }

    @Override
    public void groupResult(Object description, boolean result, List<Result> itemResults) {
        builder.append(description).append(' ');
        itemResults.stream().filter(r -> r.passed()).forEach(r -> r.accept(this));
    }

    @Override
    public void exceptionResult(Throwable throwable, boolean result) {

    }

    @Override
    public void binaryOperationResult(String operator, boolean passed, Result leftResult, Result rightResult) {
        leftResult.accept(this);
        builder.append(' ').append(operator).append(' ');
        rightResult.accept(this);
    }

    @Override
    public String toString() {
        return builder.toString();
    }

}
