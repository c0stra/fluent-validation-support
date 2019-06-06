package fluent.validation;

import fluent.validation.result.Result;
import fluent.validation.result.ResultFactory;

import static fluent.validation.BasicChecks.allOf;
import static fluent.validation.BasicChecks.equalTo;
import static fluent.validation.BasicChecks.has;

class ThrowingCheck extends Check<Runnable> {

    private final Check<? super Throwable> check;

    ThrowingCheck(Check<? super Throwable> check) {
        this.check = check;
    }

    @Override
    public Result evaluate(Runnable data, ResultFactory factory) {
        try {
            data.run();
            return factory.expectation("no exception thrown", false);
        } catch (Throwable throwable) {
            Result result = check.evaluate(throwable, factory);
            return factory.named("throwing", factory.actual(throwable, result), result.passed());
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
