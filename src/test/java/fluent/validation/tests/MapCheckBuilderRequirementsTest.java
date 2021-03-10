package fluent.validation.tests;

import fluent.validation.MapCheckBuilder;
import fluent.validation.utils.Requirements;

import java.util.HashMap;

import static fluent.validation.Checks.equalTo;

public class MapCheckBuilderRequirementsTest extends Requirements {{

    testOf(new HashMap<>()).using(new MapCheckBuilder<>().with("A", equalTo("X"))).shouldReturn(false);
    testOf(new HashMap<String, String>() {{ put("A", "X"); }}).using(new MapCheckBuilder<String, String>().with("A", equalTo("X"))).shouldReturn(true);
    testOf(new HashMap<String, String>() {{ put("A", "X"); }}).using(new MapCheckBuilder<String, String>().with("A", "X")).shouldReturn(true);

}}
