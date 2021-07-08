package fluent.validation.tests;

import fluent.validation.utils.Requirements;

import static fluent.validation.Checks.emptyArray;
import static fluent.validation.Checks.emptyArrayOrNull;
import static fluent.validation.CollectionChecks.*;
import static fluent.validation.utils.Mocks.newArray;

public class ArrayCheckRequirementsTest extends Requirements {{

    testOf (newArray("A", "C", "D")). using (array(equalTo(items("A", "C", "D")))). shouldReturn (true);
    testOf (newArray("A", "D", "C")). using (array(equalTo(items("A", "C", "D")))). shouldReturn (false);
    testOf (newArray("A", "C")). using (array(equalTo(items("A", "C", "D")))). shouldReturn (false);
    testOf (newArray("A", "C", "D", "E")). using (array(equalTo(items("A", "C", "D")))). shouldReturn (false);
    testOf (newArray("A", "C", "E", "D")). using (array(equalTo(items("A", "C", "D")))). shouldReturn (false);
    testOf (newArray("E", "A", "C", "D")). using (array(equalTo(items("A", "C", "D")))). shouldReturn (false);

    testOf (newArray("A", "C", "D")). using (array(equalInAnyOrderTo(items("A", "C", "D")))). shouldReturn (true);
    testOf (newArray("A", "D", "C")). using (array(equalInAnyOrderTo(items("A", "C", "D")))). shouldReturn (true);
    testOf (newArray("A", "C")). using (array(equalInAnyOrderTo(items("A", "C", "D")))). shouldReturn (false);
    testOf (newArray("A", "C", "D", "E")). using (array(equalInAnyOrderTo(items("A", "C", "D")))). shouldReturn (false);
    testOf (newArray("A", "C", "E", "D")). using (array(equalInAnyOrderTo(items("A", "C", "D")))). shouldReturn (false);
    testOf (newArray("E", "A", "C", "D")). using (array(equalInAnyOrderTo(items("A", "C", "D")))). shouldReturn (false);
    testOf (newArray()). using (array(equalInAnyOrderTo(items()))). shouldReturn (true);
    testOf (newArray()). using (array(equalInAnyOrderTo(items("A", "C", "D")))). shouldReturn (false);

    testOf (newArray("A", "C", "D")). using (array(contains(items("A", "C", "D")))). shouldReturn (true);
    testOf (newArray("A", "D", "C")). using (array(contains(items("A", "C", "D")))). shouldReturn (false);
    testOf (newArray("A", "C")). using (array(contains(items("A", "C", "D")))). shouldReturn (false);
    testOf (newArray("A", "C", "D", "E")). using (array(contains(items("A", "C", "D")))). shouldReturn (true);
    testOf (newArray("A", "C", "E", "D")). using (array(contains(items("A", "C", "D")))). shouldReturn (true);
    testOf (newArray("E", "A", "C", "D")). using (array(contains(items("A", "C", "D")))). shouldReturn (true);
    testOf (newArray("A", "D", "C", "E")). using (array(contains(items("A", "C", "D")))). shouldReturn (false);
    testOf (newArray("C", "A", "E", "D")). using (array(contains(items("A", "C", "D")))). shouldReturn (false);
    testOf (newArray("E", "D", "C", "A")). using (array(contains(items("A", "C", "D")))). shouldReturn (false);

    testOf (newArray("A", "C", "D")). using (array(containsInAnyOrder(items("A", "C", "D")))). shouldReturn (true);
    testOf (newArray("A", "D", "C")). using (array(containsInAnyOrder(items("A", "C", "D")))). shouldReturn (true);
    testOf (newArray("A", "C")). using (array(containsInAnyOrder(items("A", "C", "D")))). shouldReturn (false);
    testOf (newArray("A", "C", "D", "E")). using (array(containsInAnyOrder(items("A", "C", "D")))). shouldReturn (true);
    testOf (newArray("A", "C", "E", "D")). using (array(containsInAnyOrder(items("A", "C", "D")))). shouldReturn (true);
    testOf (newArray("E", "A", "C", "D")). using (array(containsInAnyOrder(items("A", "C", "D")))). shouldReturn (true);
    testOf (newArray("A", "D", "C", "E")). using (array(containsInAnyOrder(items("A", "C", "D")))). shouldReturn (true);
    testOf (newArray("C", "A", "E", "D")). using (array(containsInAnyOrder(items("A", "C", "D")))). shouldReturn (true);
    testOf (newArray("E", "D", "C", "A")). using (array(containsInAnyOrder(items("A", "C", "D")))). shouldReturn (true);


    testOf (newArray("A", "C", "D")). using (array(startsWith(items("A", "C", "D")))). shouldReturn (true);
    testOf (newArray("A", "D", "C")). using (array(startsWith(items("A", "C", "D")))). shouldReturn (false);
    testOf (newArray("A", "C")). using (array(startsWith(items("A", "C", "D")))). shouldReturn (false);
    testOf (newArray("A", "C", "D", "E")). using (array(startsWith(items("A", "C", "D")))). shouldReturn (true);
    testOf (newArray("A", "C", "E", "D")). using (array(startsWith(items("A", "C", "D")))). shouldReturn (false);
    testOf (newArray("E", "A", "C", "D")). using (array(startsWith(items("A", "C", "D")))). shouldReturn (false);
    testOf (newArray("A", "D", "C", "E")). using (array(startsWith(items("A", "C", "D")))). shouldReturn (false);
    testOf (newArray("C", "A", "E", "D")). using (array(startsWith(items("A", "C", "D")))). shouldReturn (false);
    testOf (newArray("E", "D", "C", "A")). using (array(startsWith(items("A", "C", "D")))). shouldReturn (false);

    testOf (newArray("A", "C", "D")). using (array(startsInAnyOrderWith(items("A", "C", "D")))). shouldReturn (true);
    testOf (newArray("A", "D", "C")). using (array(startsInAnyOrderWith(items("A", "C", "D")))). shouldReturn (true);
    testOf (newArray("A", "C")). using (array(startsInAnyOrderWith(items("A", "C", "D")))). shouldReturn (false);
    testOf (newArray("A", "C", "D", "E")). using (array(startsInAnyOrderWith(items("A", "C", "D")))). shouldReturn (true);
    testOf (newArray("A", "C", "E", "D")). using (array(startsInAnyOrderWith(items("A", "C", "D")))). shouldReturn (false);
    testOf (newArray("E", "A", "C", "D")). using (array(startsInAnyOrderWith(items("A", "C", "D")))). shouldReturn (false);
    testOf (newArray("A", "D", "C", "E")). using (array(startsInAnyOrderWith(items("A", "C", "D")))). shouldReturn (true);
    testOf (newArray("C", "A", "E", "D")). using (array(startsInAnyOrderWith(items("A", "C", "D")))). shouldReturn (false);
    testOf (newArray("E", "D", "C", "A")). using (array(startsInAnyOrderWith(items("A", "C", "D")))). shouldReturn (false);

    testOf (newArray("A", "B")). using (emptyArray()). shouldReturn (false);
    testOf (newArray()). using (emptyArray()). shouldReturn (true);
    testOf ((Object[]) null). using (emptyArray()). shouldReturn (false);

    testOf (newArray("A", "B")). using (emptyArrayOrNull()). shouldReturn (false);
    testOf (newArray()). using (emptyArrayOrNull()). shouldReturn (true);
    testOf ((Object[]) null). using (emptyArrayOrNull()). shouldReturn (true);

}}
