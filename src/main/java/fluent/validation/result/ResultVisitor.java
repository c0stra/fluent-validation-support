package fluent.validation.result;

import fluent.validation.Check;

import java.util.List;

public interface ResultVisitor {

    default ResultVisitor visit(Result result) {
        result.accept(this);
        return this;
    }

    void actual(Object actualValue, Result result);

    void expectation(Object expectation, boolean value);

    void transformation(Object name, Result result, boolean value);

    void aggregation(Object prefix, String glue, List<Result> items, boolean value);

    void tableAggregation(Object prefix, List<Check<?>> checks, List<?> items, List<TableInResult.Cell> results, boolean value);

    void error(Throwable error);

    void invert(Result result);

}
