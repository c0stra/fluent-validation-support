package fluent.validation;

import org.testng.annotations.Test;

import static fluent.validation.Checks.equalTo;
import static org.testng.Assert.assertTrue;

public class OperatorPrecedenceTest {

    @Test
    public void testTopLevelAndAfterOr() {
        // (A or B) and C would return false.
        // Proper precedence A or (B and C) return true.
        assertTrue(equalTo("A").or(equalTo("B")).and(equalTo("C")).test("A"));
    }

    @Test
    public void testAndPropagatedAfterTwoOrS() {
        // ((A or B) or C) and D would return false.
        // Proper precedence A or B or (C and D) return true.
        assertTrue(equalTo("A").or(equalTo("B")).or(equalTo("C")).and(equalTo("D")).test("B"));
    }

}
