package fluent.validation.tests;

import fluent.validation.utils.Requirements;

import static fluent.validation.Checks.equalTo;

public class OperatorPrecedenceRequirementsTest extends Requirements {{

    testOf("A").using(equalTo("A").or(equalTo("B"))).shouldReturn(true);
    testOf("B").using(equalTo("A").or(equalTo("B")).or(equalTo("C"))).shouldReturn(true);

}}
