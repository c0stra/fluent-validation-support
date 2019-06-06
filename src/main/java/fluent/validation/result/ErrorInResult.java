package fluent.validation.result;

public class ErrorInResult extends Result {
    private final Throwable error;

    public ErrorInResult(Throwable error) {
        super(false);
        this.error = error;
    }

    @Override
    public void accept(ResultVisitor visitor) {
        visitor.error(error);
    }
}
