package fluent.validation.result;

final class ActualValueInResult extends Result {

    private final Object actualValue;
    private final Result result;

    ActualValueInResult(Object actualValue, Result result) {
        super(result.passed());
        this.actualValue = actualValue;
        this.result = result;
    }

    @Override
    public void accept(ResultVisitor visitor) {
        visitor.actual(actualValue, result);
    }
}
