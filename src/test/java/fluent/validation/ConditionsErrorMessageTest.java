package fluent.validation;

import fluent.validation.assertion.Assert;
import org.hamcrest.Matchers;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import test.Failure;

import static fluent.validation.Conditions.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class ConditionsErrorMessageTest {

    @DataProvider
    public static Object[][] errorData() {
        return new Object[][]{
                assertDescription("A", equalTo("B"), "Expected: <B>, actual: <A>"),
                assertDescription(1.0, equalTo(2.0), "Expected: <2.0 ±1.0E-7>, actual: <1.0>"),
                assertDescription("A", not("A"), "Expected: not <A>, actual: <A>"),
                assertDescription(null, notNull(), "Expected: not <null>, actual: <null>"),
                assertDescription(null, not(anything()), "Expected: not anything, actual: <null>"),
                assertDescription("A", allOf(equalTo("A"), equalTo("B")), "Expected: <A> and <B>, but:\n\tExpected: <B>, actual: <A>"),
                assertDescription("A", allOf(equalTo("C"), equalTo("B")), "Expected: <C> and <B>, but:\n\tExpected: <C>, actual: <A>\n\tExpected: <B>, actual: <A>"),
                assertDescription("A", has("toString", Object::toString).equalTo("B"), "Expected: toString <B>, actual: <A>"),
                assertDescription("A", createBuilderWith(has("toString", Object::toString).equalTo("B")).and(has("length", String::length).equalTo(4)), "Expected: toString <B>, actual: <A>\nExpected: length <4>, actual: <1>"),
        };
    }

    @Test(dataProvider = "errorData")
    public void assertShouldFailWith(Runnable def) {
        def.run();
    }

    private static <T> Object[] assertDescription(T data, Condition<? super T> condition, String expectedMessage) {
        return new Object[]{new Def<>(data, condition, expectedMessage)};
    }

    @Test
    public void testHamcrest() {
        assertThat("A", Matchers.allOf(Matchers.not("A"), Matchers.anyOf(Matchers.equalTo("B"), Matchers.equalTo("C"))));
    }

    private static class Def<T> implements Runnable {
        private final T data;
        private final Condition<? super T> condition;
        private final String message;

        private Def(T data, Condition<? super T> condition, String message) {
            this.data = data;
            this.condition = condition;
            this.message = message;
        }

        @Override
        public String toString() {
            return "\"" + message + "\" when " + condition + " applied on " + data;
        }

        @Override
        public void run() {
            Assert.that(() -> Assert.that(data).satisfy(condition), throwing(Failure.class).withMessage(message));
        }
    }

}
