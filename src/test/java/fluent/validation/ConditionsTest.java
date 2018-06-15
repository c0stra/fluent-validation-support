package fluent.validation;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static fluent.validation.Conditions.*;
import static org.testng.Assert.assertEquals;

public class ConditionsTest {

    @DataProvider
    public static Object[][] data() {
        return new Object[][]{
                def(new Object(), anything(), true),
                def(null, anything(), true),

                def(null, isNull(), true),
                def(new Object(), isNull(), false),

                def(null, notNull(), false),
                def(new Object(), notNull(), true),

                def("A", equalTo("A"), true),
                def("A", equalTo("B"), false),
                def(null, equalTo("A"), false),
                def(null, equalTo((Object) null), true),

                def("A", not("B"), true),
                def("A", not("A"), false),
                def(null, not("A"), true),
                def(null, not((Object) null), false),

                def("A", oneOf("A", "B"), true),
                def("A", oneOf("B", "C"), false),
                def("A", oneOf(), false),
                def(null, oneOf(null, "A"), true),
                def(null, oneOf("A", "B"), false),

                def("A", anyOf(equalTo("A"), equalTo("B")), true),
                def("C", anyOf(equalTo("A"), equalTo("B")), false),
                def("A", anyOf(), false),
                def(null, anyOf(equalTo("A"), equalTo("B")), false),

                def("A", allOf(equalTo("A"), notNull()), true),
                def("C", allOf(equalTo("A"), notNull()), false),
                def("A", allOf(), true),
                def(null, allOf(equalTo("A"), notNull()), false),
                def(null, allOf(), true),

        };
    }

    @Test(dataProvider = "data")
    public void condition(Runnable runnable) {
        runnable.run();
    }

    private static <T> Object[] def(T data, Condition<? super T> condition, boolean expectedResult) {
        return new Object[] {new Def<>(data, condition, expectedResult)};
    }

    private static class Def<T> implements Runnable {

        private final T data;
        private final Condition<? super T> condition;
        private final boolean expectedResult;

        private Def(T data, Condition<? super T> condition, boolean expectedResult) {
            this.data = data;
            this.condition = condition;
            this.expectedResult = expectedResult;
        }

        @Override
        public void run() {
            assertEquals(condition.test(data), expectedResult);
        }

        @Override
        public String toString() {
            return "" + condition + " should " + (expectedResult ? "pass" : "fail") + " on " + data;
        }

    }

}