package fluent.validation;

import fluent.validation.result.Result;

import static fluent.validation.Checks.allOf;
import static fluent.validation.Checks.equalTo;
import static fluent.validation.Checks.has;

class ThrowingCheck extends Check<Runnable> {

    private final Check<? super Throwable> check;

    ThrowingCheck(Check<? super Throwable> check) {
        this.check = check;
    }

    @Override
    public Result evaluate(Runnable data) {
        try {
            data.run();
            return new Result(false) {

            };
        } catch (Throwable throwable) {
            Result result = check.evaluate(throwable);
            return new Result(result.passed()) {

            };
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
