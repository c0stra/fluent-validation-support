package fluent.validation.result;

import java.util.List;

final class AggregationInResult extends Result {

    private final Object prefix;
    private final String glue;
    private final List<Result> items;

    AggregationInResult(Object prefix, String glue, List<Result> items, boolean value) {
        super(value);
        this.prefix = prefix;
        this.glue = glue;
        this.items = items;
    }

    @Override
    public void accept(ResultVisitor visitor) {
        visitor.aggregation(prefix, glue, items, passed());
    }

}
