package fluent.validation;

import fluent.validation.detail.CheckDetail;

public class Anything<T> implements Check<T> {

    @Override
    public boolean test(T data, CheckDetail checkDetail) {
        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <U extends T> Check<U> and(Check<? super U> operand) {
        return (Check<U>) operand;
    }

}
