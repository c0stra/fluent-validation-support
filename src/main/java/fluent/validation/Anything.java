package fluent.validation;

import fluent.validation.detail.CheckVisitor;

final class Anything<T> implements Check<T> {

    @Override
    public boolean test(T data, CheckVisitor checkVisitor) {
        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <U extends T> Check<U> and(Check<? super U> operand) {
        return (Check<U>) operand;
    }

    @Override
    public String toString() {
        return "anything";
    }

}
