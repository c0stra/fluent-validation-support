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
    public static Object[][] requirements() {
        return new Object[][]{
                requirement("A", equalTo("B"), "Expected: <B>, actual: <A>"),
                requirement(1.0, equalTo(2.0), "Expected: <2.0 ±1.0E-7>, actual: <1.0>"),
                requirement("A", not("A"), "Expected: not <A>, actual: <A>"),
                requirement(null, notNull(), "Expected: not <null>, actual: <null>"),
                requirement(null, not(anything()), "Expected: not anything, actual: <null>"),
                requirement("A", allOf(equalTo("A"), equalTo("B")), "Expected: <A> and <B>, but:\n\tExpected: <B>, actual: <A>"),
                requirement("A", allOf(equalTo("C"), equalTo("B")), "Expected: <C> and <B>, but:\n\tExpected: <C>, actual: <A>\n\tExpected: <B>, actual: <A>"),
                requirement("A", has("toString", Object::toString).equalTo("B"), "Expected: toString <B>, actual: <A>"),
                requirement("A", createBuilderWith(has("toString", Object::toString).equalTo("B")).and(has("length", String::length).equalTo(4)), "Expected: toString <B>, actual: <A>\nExpected: length <4>, actual: <1>"),
        };
    }

    @Test(dataProvider = "requirements")
    public <T> void assertShould(Requirement<T, String> requirement) {
        Assert.that(
                () -> Assert.that(requirement.data).satisfy(requirement.condition),
                throwing(Failure.class).withMessage(requirement.expectedResult)
        );
    }

    private static <T> Object[] requirement(T data, Condition<? super T> condition, String expectedMessage) {
        return new Object[]{new Requirement<>(data, condition, expectedMessage,
                "fail with \"" + expectedMessage + "\" when " + condition + " applied on " + data)};
    }

    @Test
    public void testHamcrest() {
        assertThat("A", Matchers.allOf(Matchers.not("A"), Matchers.anyOf(Matchers.equalTo("B"), Matchers.equalTo("C"))));
    }

}
