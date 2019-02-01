package fluent.validation.result;

public class BinaryOperationResult extends Result {

    private final String operator;
    private final Result leftResult;
    private final Result rightResult;

    public BinaryOperationResult(boolean result, String operator, Result leftResult, Result rightResult) {
        super(result);
        this.operator = operator;
        this.leftResult = leftResult;
        this.rightResult = rightResult;
    }

    @Override
    public void accept(ResultVisitor visitor) {
        visitor.binaryOperationResult(operator, passed(), leftResult, rightResult);
    }
}
