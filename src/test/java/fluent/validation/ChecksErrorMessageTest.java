package fluent.validation;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static fluent.validation.Checks.*;

public class ChecksErrorMessageTest {

    @DataProvider
    public static Object[][] requirements() {
        return new Object[][]{
                requirement("A", equalTo("B"), "Expected: <B>, actual: <A>"),
                requirement(1.0, equalTo(2.0), "Expected: <2.0 ±1.0E-7>, actual: <1.0>"),
                requirement("A", not("A"), "Expected: not <A>, actual: <A>"),
                requirement("A", not(allOf(equalTo("A"), equalTo("B"))), "Expected: not <A>, actual: <A>"),
                requirement("A", allOf(not("A"), not("B")), "Expected: not <A>, actual: <A>"),
                requirement(null, notNull(), "Expected: not <null>, actual: <null>"),
                requirement(null, not(anything()), "Expected: not anything, actual: <null>"),
                requirement("A", allOf(equalTo("A"), equalTo("B")), "Expected: <A> and <B>, but:\n\tExpected: <B>, actual: <A>"),
                requirement("A", allOf(equalTo("C"), equalTo("B")), "Expected: <C> and <B>, but:\n\tExpected: <C>, actual: <A>\n\tExpected: <B>, actual: <A>"),
                requirement("A", has("toString", Object::toString).equalTo("B"), "toString: Expected: <B>, actual: <A>"),
                requirement("A", createBuilderWith(has("toString", Object::toString).equalTo("B")).and(has("length", String::length).equalTo(4)), "Expected: toString <B>, actual: <A>\nExpected: length <4>, actual: <1>"),
        };
    }

    @Test(dataProvider = "requirements")
    public <T> void test(Requirement<T, String> requirement) {
        throwing(require(isAn(AssertionFailure.class), has("message", Throwable::getMessage).equalTo(requirement.expectedResult)))
                .assertData(() -> requirement.check.assertData(requirement.data), new MessageCheckDetail());
    }

    private static <T> Object[] requirement(T data, Check<? super T> check, String expectedMessage) {
        return new Object[]{new Requirement<>(data, check, expectedMessage,
                "assert of `" + data + "` using check `" + check + "` should fail with message \"" + expectedMessage + "\"")};
    }

}
