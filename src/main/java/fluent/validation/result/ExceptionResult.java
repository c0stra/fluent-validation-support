package fluent.validation.result;

public class ExceptionResult extends Result {
    private final Throwable throwable;
    public ExceptionResult(Throwable throwable) {
        super(false);
        this.throwable = throwable;
    }

    @Override
    public void accept(ResultVisitor visitor) {
        visitor.exceptionResult(throwable, passed());
    }
}
