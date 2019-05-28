package fluent.validation;

import fluent.validation.result.Result;
import fluent.validation.result.ResultFactory;
import fluent.validation.utils.ErrorMessageResult;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Collections;

import static fluent.validation.BasicChecks.*;
import static fluent.validation.CollectionChecks.*;

public class ChecksErrorMessageTest {

    @DataProvider
    public static Object[][] requirements() {
        return new Object[][]{
                requirement("A", equalTo("B"), "expected: <B> but actual: <A>"),
                requirement(1.0, equalTo(2.0), "expected: <2.0 ±1.0E-7> but actual: <1.0>"),
                requirement("A", not("A"), "not expected: <A> but actual: <A>"),
                requirement("A", not(allOf(equalTo("A"), startsWith("A"))), "not expected: <A> and starts with <A> but actual: <A>"),
                requirement("A", allOf(not("A"), not("B")), "Expected: not <A> and not <B> but actual: <A>"),
                requirement(null, notNull(), "not expected: <null> but actual: <null>"),
                requirement(null, not(anything()), "not expected: anything but actual: <null>"),
                requirement("A", allOf(equalTo("A"), equalTo("B")), "expected: <A> and <B> but actual: <A>"),
                requirement("A", allOf(equalTo("C"), equalTo("B")), "expected: <C> and <B> but actual: <A>"),
                requirement("A", has("toString", Object::toString).equalTo("B"), "toString expected: <B> but actual: <A>"),
                requirement("A", createBuilderWith(has("toString", Object::toString).equalTo("B")).and(has("length", String::length).equalTo(4)), "toString expected: <B> but actual: <A> and length expected: <4> but actual: <1>"),
                requirement(Collections.singleton("A"), exists("String", equalTo("B")), "exists String <B> but No String <B> found")
        };
    }

    @Test(dataProvider = "requirements")
    public <T> void test(Requirement<T, String> requirement) {
        Check.that(
                () -> Check.that(requirement.data, requirement.check),
                throwing(require(isAn(AssertionFailure.class), has("Error message", Throwable::getMessage).matching(
                        new Check<String>() {
                            @Override protected Result evaluate(String data, ResultFactory factory) {
                                return new ErrorMessageResult(equalTo(requirement.expectedResult).evaluate(data, factory));
                            }
                        }))));
    }

    private static <T> Object[] requirement(T data, Check<? super T> check, String expectedMessage) {
        return new Object[]{new Requirement<>(data, check, expectedMessage,
                "assert of `" + data + "` using check `" + check + "` should fail with message \"" + expectedMessage + "\"")};
    }

}
