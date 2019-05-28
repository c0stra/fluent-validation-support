package fluent.validation.result;

import java.util.List;

public class MismatchResultVisitor implements ResultVisitor {

    private final StringBuilder builder = new StringBuilder();

    @Override
    public void predicateResult(Object expectation, Object actual, boolean result) {
        builder.append("expected: ").append(expectation).append(" but actual: <").append(actual).append('>');//.append('\n');
    }

    @Override
    public void targetResult(CheckDescription target, boolean result, Result dependency) {
        builder.append(target.description()).append(' ');
        dependency.accept(this);
    }

    @Override
    public void groupResult(Object description, Object actualValueDescription, boolean result, List<Result> itemResults) {
        builder.append(description).append(" but ").append(actualValueDescription);
        itemResults.stream().filter(Result::passed).forEach(r -> r.accept(this));
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
