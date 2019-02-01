package fluent.validation.result;

import java.util.List;

public interface ResultVisitor {

    void predicateResult(Object expectation, Object actual, boolean result);

    void targetResult(Object target, boolean result, Result dependency);

    void groupResult(Object description, boolean result, List<Result> itemResults);

    void exceptionResult(Throwable throwable, boolean result);

    void binaryOperationResult(String operator, boolean passed, Result leftResult, Result rightResult);

}
