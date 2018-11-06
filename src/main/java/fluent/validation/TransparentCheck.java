package fluent.validation;

import fluent.validation.detail.CheckDetail;

public class TransparentCheck<T> implements Check<T> {

    private final CheckDetail checkDetail;
    private final Check<T> check;

    public TransparentCheck(CheckDetail checkDetail, Check<T> check) {
        this.checkDetail = checkDetail;
        this.check = check;
    }

    @Override
    public void assertData(T data) {
        assertData(data, checkDetail);
    }

    @Override
    public boolean test(T data, CheckDetail checkDetail) {
        return check.test(data, checkDetail);
    }

    @Override
    public <U extends T> Check<U> and(Check<? super U> operand) {
        return new TransparentCheck<>(checkDetail, check.and(operand));
    }

    @Override
    public <U extends T> Check<U> or(Check<? super U> operand) {
        return new TransparentCheck<>(checkDetail, check.or(operand));
    }

}
