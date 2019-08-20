package fluent.validation;

import org.testng.annotations.Test;

public class AbstractCheckDslTest {

    private static class MyCheck extends AbstractCheckDsl<MyCheck, Object> {

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
        Assert.that("A", new MyCheck().withHashCode(65).withToString("A"));
    }

    @Test(expectedExceptions = AssertionFailure.class)
    public void testContradiction() {
        Assert.that("A", new MyCheck().withToString("A").withHashCode(10));
    }

    @Test
    public void testOrFields() {
        Assert.that("A", new MyCheck().withToString("B").or().withHashCode(65));
    }

}