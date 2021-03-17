package fluent.validation.tests;

import fluent.validation.utils.Requirements;

import static fluent.validation.XmlChecks.asXml;
import static fluent.validation.XmlChecks.matchesXPath;

public class XmlChecksTest extends Requirements {{

    testOf("<root></root>").using(asXml(matchesXPath("/root"))).shouldReturn(true);
    testOf("<root></root>").using(asXml(matchesXPath("/other"))).shouldReturn(false);

}}
