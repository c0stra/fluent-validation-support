package fluent.validation;

import fluent.validation.detail.CheckDetail;

import static fluent.validation.Checks.allOf;
import static fluent.validation.Checks.equalTo;
import static fluent.validation.Checks.has;

public class ThrowingCheck implements Check<Runnable> {

    private final Check<? super Throwable> check;

    public ThrowingCheck(Check<? super Throwable> check) {
        this.check = check;
    }

    @Override
    public boolean test(Runnable data, CheckDetail checkDetail) {
        try {
            data.run();
            return false;
        } catch (Throwable throwable) {
            return check.test(throwable, checkDetail);
        }
    }

    public ThrowingCheck withMessage(Check<? super String> check) {
        return new ThrowingCheck(allOf(this.check, has("message", Throwable::getMessage).matching(check)));
    }

    public ThrowingCheck withMessage(String expectedValue) {
        return withMessage(equalTo(expectedValue));
    }

    @Override
    public String toString() {
        return "throwing " + check;
    }
}
