package fluent.validation.result;

public class PredicateResult extends Result {

    private final Object expectation;
    private final Object actual;

    public PredicateResult(boolean result, Object expectation, Object actual) {
        super(result);
        this.expectation = expectation;
        this.actual = actual;
    }

    @Override
    public void accept(ResultVisitor visitor) {
        visitor.predicateResult(expectation, actual, passed());
    }

}
