package fluent.validation.tests;

import fluent.validation.utils.Requirements;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static fluent.validation.BasicChecks.has;

public class CompositionRequirementsTest extends Requirements {{

    testOf(1).using(has(Object::toString).equalTo("1")).shouldReturn(true);
    testOf(1).using(has(Object::toString).equalTo("2")).shouldReturn(false);
    testOf(null).using(has(Object::toString).equalTo("null")).shouldReturn(false);
    testOf(12).using(has(Objects::toString).having(String::length).equalTo(2)).shouldReturn(true);
    testOf(12).using(has(Objects::toString).having(String::length).equalTo(3)).shouldReturn(false);
    testOf(null).using(has(Objects::toString).having(String::length).equalTo(3)).shouldReturn(false);
    testOf(new HashMap<String, Object>()).using(has("get", (Map<String, Object> m) -> m.get("a")).having(Objects::toString).equalTo("b")).shouldReturn(false);

}}
