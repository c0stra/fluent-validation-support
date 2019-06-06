package fluent.validation.result;

final class TransformationInResult extends Result {

    private final Object name;
    private final Result result;

    TransformationInResult(Object name, Result result, boolean value) {
        super(value);
        this.name = name;
        this.result = result;
    }

    @Override
    public void accept(ResultVisitor visitor) {
        visitor.transformation(name, result, passed());
    }
}
