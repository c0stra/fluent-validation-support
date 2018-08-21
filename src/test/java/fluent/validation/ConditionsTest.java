package fluent.validation;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static fluent.validation.Conditions.*;
import static org.testng.Assert.assertEquals;

public class ConditionsTest {

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

        };
    }

    @Test(dataProvider = "requirements")
    public <T> void test(Requirement<T, Boolean> requirement) {
        assertEquals((Boolean) requirement.condition.test(requirement.data), requirement.expectedResult);
    }

    private static <T> Object[] requirement(T data, Condition<? super T> condition, boolean expectedResult) {
        return new Object[] {new Requirement<>(data, condition, expectedResult,
                "condition `" + condition + "` should return " + expectedResult + " when applied on `" + data + "`")};
    }

}
