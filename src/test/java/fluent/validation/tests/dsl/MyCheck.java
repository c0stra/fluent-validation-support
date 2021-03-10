package fluent.validation.tests.dsl;

import fluent.validation.AbstractCheckDsl;
import fluent.validation.Check;

public class MyCheck extends AbstractCheckDsl<MyCheck, Object> {

    private MyCheck(Check<Object> check) {
        super(check, MyCheck::new);
    }

    public MyCheck() {
        super(MyCheck::new);
    }

    public MyCheck withHashCode(int expectedValue) {
        return withField("hashCode", Object::hashCode).equalTo(expectedValue);
    }

    public MyCheck withToString(String expectedValue) {
        return withField("toString", Object::toString).equalTo(expectedValue);
    }

}
