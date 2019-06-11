package fluent.validation.result;

import java.util.List;
import java.util.Objects;

import static java.util.stream.Collectors.joining;

public final class ExpectationVisitor implements ResultVisitor {

    private final StringBuilder builder;

    public ExpectationVisitor(StringBuilder builder) {
        this.builder = builder;
    }

    @Override
    public void actual(Object actualValue, Result result) {
        result.accept(this);
    }

    @Override
    public void expectation(Object expectation, boolean result) {
        builder.append(expectation);
    }

    @Override
    public void transformation(Object name, Result dependency, boolean result) {
        builder.append(name).append(' ');
        dependency.accept(this);
    }

    @Override
    public void aggregation(Object description, String glue, List<Result> itemResults, boolean result) {
        builder.append("(");
        for(int i = 0; i < itemResults.size(); i++) {
            if(i > 0) {
                builder.append(glue);
            }
            itemResults.get(i).accept(this);
        }
        builder.append(")");
    }

    @Override
    public void error(Throwable throwable) {

    }

    @Override
    public void invert(Result result) {
        result.accept(this);
    }

    @Override
    public String toString() {
        return builder.toString();
    }

}
