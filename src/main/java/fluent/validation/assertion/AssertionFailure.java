package fluent.validation.assertion;

public class AssertionFailure extends AssertionError {

    public AssertionFailure(Object detail) {
        super(detail);
    }

    @Override
    public String toString() {
        return "Assertion failed: " + getLocalizedMessage();
    }

}
