package fluent.validation.tests;

import fluent.validation.utils.Requirements;

import static fluent.validation.XmlChecks.parseXml;
import static fluent.validation.XmlChecks.matchesXPath;

public class XmlChecksTest extends Requirements {{

    testOf ("<root></root>"). using (parseXml(matchesXPath("/root")) ). shouldReturn (true );
    testOf ("<root></root>"). using (parseXml(matchesXPath("/other"))). shouldReturn (false);

}}
