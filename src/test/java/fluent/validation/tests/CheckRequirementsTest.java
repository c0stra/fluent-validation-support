package fluent.validation.tests;

import fluent.validation.utils.Requirements;

import java.time.Duration;

import static fluent.validation.Checks.*;
import static fluent.validation.Checks.check;
import static fluent.validation.CollectionChecks.repeatMax;
import static fluent.validation.utils.Mocks.predicateMock;
import static java.util.Arrays.asList;

public class CheckRequirementsTest extends Requirements {{

    testOf (new Object()). using (anything()). shouldReturn (true);
    testOf (null). using (anything()). shouldReturn (true);

    testOf (null). using (isNull()). shouldReturn (true);
    testOf (new Object()). using (isNull()). shouldReturn (false);

    testOf (null). using (isNotNull()). shouldReturn (false);
    testOf (new Object()). using (isNotNull()). shouldReturn (true);

    testOf ("A"). using (equalTo("A")). shouldReturn (true);
    testOf ("A"). using (equalTo("B")). shouldReturn (false);
    testOf (null). using (equalTo("A")). shouldReturn (false);
    testOf (null). using (equalTo((Object) null)). shouldReturn (true);
    testOf ("A"). using (is("A")). shouldReturn (true);
    testOf ("A"). using (is("B")). shouldReturn (false);
    testOf (null). using (is("A")). shouldReturn (false);
    testOf (null). using (is(null)). shouldReturn (true);

    testOf ("A"). using (sameInstance("A")). shouldReturn (true);
    testOf (new String("A")). using (sameInstance(new String("A"))). shouldReturn (false);
    testOf ("A"). using (sameInstance("B")). shouldReturn (false);
    testOf (null). using (sameInstance("A")). shouldReturn (false);

    testOf ("A"). using (not("B")). shouldReturn (true);
    testOf ("A"). using (not("A")). shouldReturn (false);
    testOf (null). using (not("A")). shouldReturn (true);
    testOf (null). using (not((Object) null)). shouldReturn (false);

    testOf ("A"). using (oneOf("A", "B")). shouldReturn (true);
    testOf ("A"). using (oneOf("B", "C")). shouldReturn (false);
    testOf ("A"). using (oneOf()). shouldReturn (false);
    testOf (null). using (oneOf(null, "A")). shouldReturn (true);
    testOf (null). using (oneOf("A", "B")). shouldReturn (false);

    testOf ("A"). using (anyOf(equalTo("A"), equalTo("B"))). shouldReturn (true);
    testOf ("C"). using (anyOf(equalTo("A"), equalTo("B"))). shouldReturn (false);
    testOf ("A"). using (anyOf()). shouldReturn (false);
    testOf (null). using (anyOf(equalTo("A"), equalTo("B"))). shouldReturn (false);

    testOf  ("A") .  using  (allOf(equalTo("A"), isNotNull())).  shouldReturn  (true );
    testOf  ("C") .  using  (allOf(equalTo("A"), isNotNull())).  shouldReturn  (false);
    testOf  ("A") .  using  (allOf())                         .  shouldReturn  (true );
    testOf  (null).  using  (allOf(equalTo("A"), isNotNull())).  shouldReturn  (false);
    testOf  (null).  using  (allOf())                         .  shouldReturn  (true );

    testOf (asList("A", "B", "C")). using (exists("String", equalTo("B"))). shouldReturn (true);
    testOf (asList("A", "B", "C")). using (exists("String", equalTo("D"))). shouldReturn (false);
    testOf (asList("B", "B", "B")). using (every("String", equalTo("B"))) . shouldReturn (true);
    testOf (asList("D", "B", "D")). using (every("String", equalTo("D"))) . shouldReturn (false);

    testOf ("A"). using (repeatMax(check(predicateMock(false, false, true), "matching check"), 3, Duration.ZERO)). shouldReturn (true );
    testOf ("A"). using (repeatMax(check(predicateMock(false, false, true), "matching check"), 2, Duration.ZERO)). shouldReturn (false);

    testOf (8). using (instanceOf(Number.class)) . shouldReturn (true);
    testOf (8). using (instanceOf(Integer.class)). shouldReturn (true);
    testOf (8). using (instanceOf(Double.class)) . shouldReturn (false);

    testOf (8). using (sameClass(Number.class)) . shouldReturn (false);
    testOf (8). using (sameClass(Integer.class)). shouldReturn (true);
    testOf (8). using (sameClass(Double.class)) . shouldReturn (false);

}}
