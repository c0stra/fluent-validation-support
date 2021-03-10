package fluent.validation.tests;

import fluent.validation.tests.dsl.MyCheck;
import fluent.validation.utils.Requirements;

public class CheckDslRequirementsTest extends Requirements {{

    testOf("A").using(new MyCheck().withHashCode(65).withToString("A")).shouldReturn(true);
    testOf("A").using(new MyCheck().withToString("A").withHashCode(10)).shouldReturn(false);
    testOf("A").using(new MyCheck().withToString("B").or().withHashCode(65)).shouldReturn(true);

}}
