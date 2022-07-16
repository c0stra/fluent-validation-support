package fluent.validation.tests;

import fluent.validation.Checks;
import fluent.validation.utils.Requirements;

import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static fluent.validation.BasicChecks.has;

public class CheckBuilderRequirementsTest extends Requirements {{

    ZonedDateTime time = ZonedDateTime.of(2021, 5, 10, 8, 0, 21, 5332, ZoneId.of("GMT"));
    ZonedDateTime NULL = null;

    testOf (time). using (Checks.<ZonedDateTime>dsl().has(ZonedDateTime::getDayOfMonth).equalTo(10).has(ZonedDateTime::getMonth).equalTo(Month.MAY)).                            shouldReturn (true);
    testOf (time). using (Checks.<ZonedDateTime>dsl().has(ZonedDateTime::getDayOfMonth).having(Object::toString).equalTo("10").has(ZonedDateTime::getMonth).equalTo(Month.MAY)). shouldReturn (true);
    testOf (NULL). using (Checks.<ZonedDateTime>dsl().has(ZonedDateTime::getDayOfMonth).equalTo(10).has(ZonedDateTime::getMonth).equalTo(Month.MAY)).                            shouldReturn (false);
    testOf (time). using (Checks.<ZonedDateTime>dsl().has(ZonedDateTime::getDayOfMonth).equalTo(10).has(ZonedDateTime::getMonth).equalTo(Month.APRIL)).                          shouldReturn (false);

    testOf (time). using (has(ZonedDateTime::getDayOfMonth).equalTo(10).has(ZonedDateTime::getMonth).equalTo(Month.MAY)).                            shouldReturn (true);
    testOf (time). using (has(ZonedDateTime::getDayOfMonth).having(Object::toString).equalTo("10").has(ZonedDateTime::getMonth).equalTo(Month.MAY)). shouldReturn (true);
    testOf (NULL). using (has(ZonedDateTime::getDayOfMonth).equalTo(10).has(ZonedDateTime::getMonth).equalTo(Month.MAY)).                            shouldReturn (false);
    testOf (time). using (has(ZonedDateTime::getDayOfMonth).equalTo(10).has(ZonedDateTime::getMonth).equalTo(Month.APRIL)).                          shouldReturn (false);

}}
