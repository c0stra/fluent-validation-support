package fluent.validation.result;

final class InvertFailureIndicatorInResult extends Result {
    private final Result cause;

    InvertFailureIndicatorInResult(Result cause) {
        super(cause.passed());
        this.cause = cause;
    }

    @Override
    public void accept(ResultVisitor visitor) {
        visitor.invert(cause);
    }
}
