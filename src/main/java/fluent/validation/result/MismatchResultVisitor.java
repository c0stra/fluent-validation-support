package fluent.validation.result;

import java.util.List;

public class MismatchResultVisitor implements ResultVisitor {

    private final Object actualValueDescription;
    private final StringBuilder builder;

    private MismatchResultVisitor(Object actualValueDescription, StringBuilder builder) {
        this.actualValueDescription = actualValueDescription;
        this.builder = builder;
    }

    public MismatchResultVisitor(Object actualValueDescription) {
        this(actualValueDescription, new StringBuilder());
    }

    @Override
    public void actual(Object actualValue, Result result) {
        result.accept(new MismatchResultVisitor(actualValue, builder));
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
    public String toString() {
        return builder.toString();
    }

}
