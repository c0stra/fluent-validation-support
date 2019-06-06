package fluent.validation.result;

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

    void error(Throwable error);
}
