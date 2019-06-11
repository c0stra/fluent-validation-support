package fluent.validation.utils;

import fluent.validation.result.Result;
import fluent.validation.result.ResultVisitor;

import java.util.List;

public class ErrorMessageMismatchVisitor implements ResultVisitor {

    private Object actualValue = null;
    private Object expectedValue = null;

    @Override
    public void actual(Object actualValue, Result result) {
        this.actualValue = actualValue;
        result.accept(this);
    }

    @Override
    public void expectation(Object expectation, boolean value) {
        this.expectedValue = expectation;
    }

    @Override
    public void transformation(Object name, Result result, boolean value) {
        result.accept(this);
    }

    @Override
    public void aggregation(Object prefix, String glue, List<Result> items, boolean value) {

    }

    @Override
    public void error(Throwable error) {

    }

    @Override
    public void invert(Result result) {
        result.accept(this);
    }

    @Override
    public String toString() {
        return "Error message expected:\n---\n" + expectedValue + "\n---\nActual:\n---\n" + actualValue + "\n---\n";
    }
}
