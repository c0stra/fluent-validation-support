package fluent.validation.result;

import java.util.List;

public final class MismatchResultVisitor implements ResultVisitor {

    private final boolean failureIndicator;
    private final Object actualValueDescription;
    private final StringBuilder builder;

    private MismatchResultVisitor(boolean failureIndicator, Object actualValueDescription, StringBuilder builder) {
        this.failureIndicator = failureIndicator;
        this.actualValueDescription = actualValueDescription;
        this.builder = builder;
    }

    public MismatchResultVisitor(Object actualValueDescription) {
        this(false, actualValueDescription, new StringBuilder());
    }

    @Override
    public void actual(Object actualValue, Result result) {
        result.accept(new MismatchResultVisitor(failureIndicator, actualValue, builder));
    }

    @Override
    public void expectation(Object expectation, boolean result) {
        builder.append("expected: ").append(expectation).append(" but actual: <").append(actualValueDescription).append('>');//.append('\n');
    }

    @Override
    public void transformation(Object name, Result dependency, boolean result) {
        builder.append(name).append(' ');
        dependency.accept(this);
    }

    @Override
    public void aggregation(Object description, String glue, List<Result> itemResults, boolean result) {
        builder.append(description).append(" but ").append(actualValueDescription);
        itemResults.stream().filter(Result::passed).forEach(r -> r.accept(this));
    }

    @Override
    public void error(Throwable throwable) {

    }

    @Override
    public void invert(Result result) {
        result.accept(new MismatchResultVisitor(!failureIndicator, actualValueDescription, builder));
    }

    @Override
    public String toString() {
        return builder.toString();
    }

}
