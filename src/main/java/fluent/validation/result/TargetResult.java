package fluent.validation.result;

public class TargetResult extends Result {

    private final Object target;
    private final Result dependency;

    public TargetResult(boolean result, Object target, Result dependency) {
        super(result);
        this.target = target;
        this.dependency = dependency;
    }

    @Override
    public void accept(ResultVisitor visitor) {
        visitor.targetResult(target, passed(), dependency);
    }
}
