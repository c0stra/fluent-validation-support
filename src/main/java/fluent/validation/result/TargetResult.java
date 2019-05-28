package fluent.validation.result;

public class TargetResult extends Result {

    private final CheckDescription target;
    private final Result dependency;

    public TargetResult(CheckDescription target, boolean result, Result dependency) {
        super(result);
        this.target = target;
        this.dependency = dependency;
    }

    @Override
    public void accept(ResultVisitor visitor) {
        visitor.targetResult(target, passed(), dependency);
    }
}
