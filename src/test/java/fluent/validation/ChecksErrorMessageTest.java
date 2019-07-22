package fluent.validation;

import fluent.validation.result.ResultFactory;
import fluent.validation.utils.ErrorMessageMismatchVisitor;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Collections;

import static fluent.validation.BasicChecks.*;
import static fluent.validation.CollectionChecks.*;
import static fluent.validation.StringChecks.startsWith;

public class ChecksErrorMessageTest {

    @DataProvider
    public static Object[][] requirements() {
        return new Object[][]{
                requirement("A", equalTo("B"), "expected: <B> but was: <A>"),
                requirement(1.0, equalTo(2.0), "expected: <2.0 ±1.0E-7> but was: <1.0>"),
                requirement("A", not("A"), "expected: not <A> but was: <A>"),
                requirement("A", not(allOf(equalTo("A"), startsWith("A"))), "expected: not (<A> and starts with <A>) but was: A\n" +
                        "\t+ expected: <A> but was: <A>\n" +
                        "\t+ expected: starts with <A> but was: <A>"),
                requirement("A", allOf(not("A"), not("B")), "expected: (not <A> and not <B>) but was: A\n" +
                        "\t+ expected: not <A> but was: <A>"),
                requirement(null, notNull(), "expected: not <null> but was: <null>"),
                requirement(null, not(anything()), "expected: not anything but was: <null>"),
                requirement("A", allOf(equalTo("A"), equalTo("B")), "expected: (<A> and <B>) but was: A\n" +
                        "\t+ expected: <B> but was: <A>"),
                requirement("A", allOf(equalTo("C"), equalTo("B")), "expected: (<C> and <B>) but was: A\n" +
                        "\t+ expected: <C> but was: <A>\n" +
                        "\t+ expected: <B> but was: <A>"),
                requirement("A", has("toString", Object::toString).equalTo("B"), "expected: toString <B> but was: <A>"),
                requirement("A", createBuilderWith(has("toString", Object::toString).equalTo("B")).and(has("length", String::length).equalTo(4)), "expected: (toString <B> and length <4>) but was: A\n" +
                        "\t+ expected: toString <B> but was: <A>\n" +
                        "\t+ expected: length <4> but was: <1>"),
                requirement(Collections.singleton("A"), exists("String", equalTo("B")), "expected: (<B>) but was: <B> not matched by any Item\n" +
                        "\t+ expected: <B> but was: <<B> not matched by any Item>")
        };
    }

    @Test(dataProvider = "requirements")
    public <T> void test(Requirement<T, String> requirement) {
        Assert.that(
                () -> Assert.that(requirement.data, requirement.check),
                throwing(require(isAn(AssertionFailure.class), has("Error message", Throwable::getMessage).equalTo(requirement.expectedResult))),
                ResultFactory.DEFAULT,
                new ErrorMessageMismatchVisitor()
        );
    }

    private static <T> Object[] requirement(T data, Check<? super T> check, String expectedMessage) {
        return new Object[]{new Requirement<>(data, check, expectedMessage,
                "assert of `" + data + "` using check `" + check + "` should fail with message \"" + expectedMessage + "\"")};
    }

}
