package fluent.validation;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Collections;

import static fluent.validation.BasicChecks.*;
import static fluent.validation.StringChecks.*;
import static fluent.validation.ComparisonChecks.*;
import static fluent.validation.NumericChecks.*;
import static fluent.validation.CollectionChecks.*;
import static java.util.Arrays.asList;
import static org.testng.Assert.assertEquals;

public class ChecksTest {

    @DataProvider
    public static Object[][] requirements() {
        return new Object[][]{
                requirement(new Object(), anything(), true),
                requirement(null, anything(), true),

                requirement(null, isNull(), true),
                requirement(new Object(), isNull(), false),

                requirement(null, notNull(), false),
                requirement(new Object(), notNull(), true),

                requirement("A", equalTo("A"), true),
                requirement("A", equalTo("B"), false),
                requirement("A", equalTo("B"), false),
                requirement(null, equalTo("A"), false),
                requirement(null, equalTo((Object) null), true),

                requirement("A", sameInstance("A"), true),
                requirement(new String("A"), sameInstance(new String("A")), false),
                requirement("A", sameInstance("B"), false),
                requirement(null, equalTo("A"), false),

                requirement("A", not("B"), true),
                requirement("A", not("A"), false),
                requirement(null, not("A"), true),
                requirement(null, not((Object) null), false),

                requirement("A", oneOf("A", "B"), true),
                requirement("A", oneOf("B", "C"), false),
                requirement("A", oneOf(), false),
                requirement(null, oneOf(null, "A"), true),
                requirement(null, oneOf("A", "B"), false),

                requirement("A", anyOf(equalTo("A"), equalTo("B")), true),
                requirement("C", anyOf(equalTo("A"), equalTo("B")), false),
//                requirement("A", anyOf(), false),
                requirement(null, anyOf(equalTo("A"), equalTo("B")), false),

                requirement("A", allOf(equalTo("A"), notNull()), true),
                requirement("C", allOf(equalTo("A"), notNull()), false),
//                requirement("A", allOf(), true),
                requirement(null, allOf(equalTo("A"), notNull()), false),
//                requirement(null, allOf(), true),
                requirement(asList("A", "B", "C"), exists("String", equalTo("B")), true),
                requirement(asList("A", "B", "C"), exists("String", equalTo("D")), false),
                requirement(asList("B", "B", "B"), every("String", equalTo("B")), true),
                requirement(asList("D", "B", "D"), every("String", equalTo("D")), false),

        };
    }

    @Test(dataProvider = "requirements")
    public <T> void test(Requirement<T, Boolean> requirement) {
        assertEquals((Boolean) Check.that(requirement.data, requirement.check), requirement.expectedResult);
    }

    private static <T> Object[] requirement(T data, Check<? super T> check, boolean expectedResult) {
        return new Object[] {new Requirement<>(data, check, expectedResult,
                "check `" + check + "` should return " + expectedResult + " when applied on `" + data + "`")};
    }

}
