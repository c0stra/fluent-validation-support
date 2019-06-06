package fluent.validation.result;

public class ExpectationInResult extends Result {

    private final Object expectation;

    public ExpectationInResult(Object expectation, boolean result) {
        super(result);
        this.expectation = expectation;
    }

    @Override
    public void accept(ResultVisitor visitor) {
        visitor.expectation(expectation, passed());
    }
}
