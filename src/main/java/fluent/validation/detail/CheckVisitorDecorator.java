package fluent.validation.detail;

import fluent.validation.Check;

public class CheckVisitorDecorator implements CheckVisitor {

    private final CheckVisitor visitor;
    private final CheckVisitor decorator;

    public CheckVisitorDecorator(CheckVisitor visitor, CheckVisitor decorator) {
        this.visitor = visitor;
        this.decorator = decorator;
    }

    @Override
    public void trace(String expectation, Object actualValue, boolean result) {
        decorator.trace(expectation, actualValue, result);
        visitor.trace(expectation, actualValue, result);
    }

    @Override
    public CheckVisitor node(Check<?> check) {
        return new CheckVisitorDecorator(visitor.node(check), decorator.node(check));
    }

    @Override
    public CheckVisitor label(Check<?> check) {
        return new CheckVisitorDecorator(visitor.label(check), decorator.label(check));
    }

    @Override
    public CheckVisitor negative(Check<?> check) {
        return new CheckVisitorDecorator(visitor.negative(check), decorator.negative(check));
    }

    @Override
    public void trace(Object data, boolean result) {
        decorator.trace(data, result);
        visitor.trace(data, result);
    }
}
