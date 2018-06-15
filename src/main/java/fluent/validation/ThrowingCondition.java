package fluent.validation;

import fluent.validation.detail.EvaluationLogger;

import static fluent.validation.Conditions.allOf;
import static fluent.validation.Conditions.equalTo;
import static fluent.validation.Conditions.has;

public class ThrowingCondition implements Condition<Runnable> {

    private final Condition<? super Throwable> condition;

    public ThrowingCondition(Condition<? super Throwable> condition) {
        this.condition = condition;
    }

    @Override
    public boolean test(Runnable data, EvaluationLogger evaluationLogger) {
        try {
            data.run();
            return false;
        } catch (Throwable throwable) {
            return condition.test(throwable, evaluationLogger);
        }
    }

    public ThrowingCondition withMessage(Condition<? super String> condition) {
        return new ThrowingCondition(allOf(this.condition, has("message", Throwable::getMessage).matching(condition)));
    }

    public ThrowingCondition withMessage(String expectedValue) {
        return withMessage(equalTo(expectedValue));
    }

    @Override
    public String toString() {
        return "throwing " + condition;
    }
}
