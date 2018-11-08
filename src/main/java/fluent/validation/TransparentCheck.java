package fluent.validation;

import fluent.validation.detail.CheckVisitor;
import fluent.validation.detail.CheckVisitorDecorator;

class TransparentCheck<T> extends Check<T> {

    private final CheckVisitor checkVisitor;
    private final Check<T> check;

    TransparentCheck(CheckVisitor checkVisitor, Check<T> check) {
        this.checkVisitor = checkVisitor;
        this.check = check;
    }

    @Override
    public boolean test(T data, CheckVisitor checkVisitor) {
        return check.test(data, new CheckVisitorDecorator(checkVisitor, this.checkVisitor));
    }

    @Override
    public <U extends T> Check<U> and(Check<? super U> operand) {
        return new TransparentCheck<>(checkVisitor, check.and(operand));
    }

    @Override
    public <U extends T> Check<U> or(Check<? super U> operand) {
        return new TransparentCheck<>(checkVisitor, check.or(operand));
    }

}
