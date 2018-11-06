package fluent.validation;

import org.testng.annotations.Test;

public class CheckDslTest {

    private static class MyCheck extends CheckDsl<MyCheck, Object> {

        private MyCheck(Check<Object> check) {
            super(check, MyCheck::new);
        }

        private MyCheck() {
            super(MyCheck::new);
        }

        public MyCheck withHashCode(int expectedValue) {
            return withField("hashCode", Object::hashCode).equalTo(expectedValue);
        }

        public MyCheck withToString(String expectedValue) {
            return withField("toString", Object::toString).equalTo(expectedValue);
        }

    }

    @Test
    public void testWithField() {
        new MyCheck().withHashCode(65).withToString("A").assertData("A");
    }

    @Test(expectedExceptions = AssertionFailure.class)
    public void testContradiction() {
        new MyCheck().withToString("A").withHashCode(10).assertData("A");
    }

    @Test
    public void testOrFields() {
        new MyCheck().withToString("B").or().withHashCode(65).assertData("A");
    }

}
