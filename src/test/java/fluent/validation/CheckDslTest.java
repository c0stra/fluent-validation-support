package fluent.validation;

import org.testng.annotations.Test;

import static fluent.validation.Checks.equalTo;

public class CheckDslTest {

    public static class MyCheck extends CheckDsl<MyCheck, Object> {

        protected MyCheck(Check<Object> check) {
            super(check, MyCheck::new);
        }

        protected MyCheck() {
            super(MyCheck::new);
        }

        public MyCheck withHashCode(Check<? super Integer> check) {
            return withField("hashCode", Object::hashCode).matching(check);
        }

        public MyCheck withToString(Check<? super String> check) {
            return withField("toString", Object::toString).matching(check);
        }

    }

    @Test
    public void testWithField() {
        new MyCheck().withHashCode(equalTo(65)).assertData("A");
    }

}