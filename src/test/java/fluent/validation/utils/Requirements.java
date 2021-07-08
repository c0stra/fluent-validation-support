package fluent.validation.utils;

import fluent.api.End;
import fluent.api.Start;
import fluent.validation.AssertionFailure;
import fluent.validation.Check;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static fluent.validation.BasicChecks.throwing;
import static java.lang.reflect.Array.get;
import static java.lang.reflect.Array.getLength;
import static java.util.stream.Collectors.joining;
import static java.util.stream.IntStream.range;

public class Requirements {

    private final List<Requirement> requirements = new ArrayList<>();

    private static String valueOf(Object value) {
        if(value == null) return null;
        if(!value.getClass().isArray()) return value.toString();
        return range(0, getLength(value)).mapToObj(i -> valueOf(get(value, i))).collect(joining(", ", "[", "]"));
    }

    @Start("Requirement description not finished.")
    protected <D> Data<D, Response> testOf(D data) {
        return check -> expectedResult -> requirements.add(new Requirement(
                "Check of " + valueOf(data) + " using " + check + " should return " + expectedResult,
                () -> Assert.assertEquals(Check.that(data, check), expectedResult, "Check of " + valueOf(data) + " using " + check)
        ));
    }

    @Start("Requirement description not finished.")
    protected <D> Data<D, Failure> assertOf(D data) {
        return check -> expectedMessage -> requirements.add(new Requirement(
                "Assert of " + valueOf(data) + " using " + check + " should fail with " + expectedMessage,
                () -> fluent.validation.Assert.that(
                        () -> fluent.validation.Assert.that(data, check),
                        throwing(AssertionFailure.class).withMessage(expectedMessage),
                        new ErrorMessageMismatchVisitor()
                )
        ));
    }

    @DataProvider
    public Iterator<Object[]> data() {
        return requirements.stream().map(r -> new Object[]{r}).iterator();
    }

    @Test(dataProvider = "data")
    public void test(Requirement requirement) {
        requirement.runnable.run();
    }

    public interface Data<D, R> {
        R using(Check<? super D> check);
    }

    public interface Response {
        @End void shouldReturn(boolean expectedResult);
    }

    public interface Failure {
        @End void shouldFailWith(String errorMessage);
    }

    private static final class Requirement {
        private final String description;
        private final Runnable runnable;

        private Requirement(String description, Runnable runnable) {
            this.description = description;
            this.runnable = runnable;
        }

        @Override
        public String toString() {
            return description;
        }
    }

}
