package fluent.validation.result;

final class ErrorInResult extends Result {
    private final Throwable error;

    ErrorInResult(Throwable error) {
        super(false);
        this.error = error;
    }

    @Override
    public void accept(ResultVisitor visitor) {
        visitor.error(error);
    }
}
