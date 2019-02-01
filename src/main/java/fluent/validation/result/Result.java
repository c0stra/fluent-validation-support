package fluent.validation.result;

public abstract class Result {

    private final boolean result;

    public Result(boolean result) {
        this.result = result;
    }

    public boolean passed() {
        return result;
    }

    public boolean failed() {
        return !passed();
    }

    public abstract void accept(ResultVisitor visitor);

    @Override
    public String toString() {
        ResultVisitor visitor = new MismatchResultVisitor();
        accept(visitor);
        return visitor.toString();
    }

}
