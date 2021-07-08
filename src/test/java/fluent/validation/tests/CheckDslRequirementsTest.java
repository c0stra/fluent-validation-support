package fluent.validation.tests;

import fluent.validation.Checks;
import fluent.validation.utils.Requirements;

import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class CheckDslRequirementsTest extends Requirements {{

    ZonedDateTime time = ZonedDateTime.of(2021, 5, 10, 8, 0, 21, 5332, ZoneId.of("GMT"));
    ZonedDateTime NULL = null;

    testOf (time). using (Checks.<ZonedDateTime>dsl().withField(ZonedDateTime::getDayOfMonth).equalTo(10).withField(ZonedDateTime::getMonth).equalTo(Month.MAY)).                            shouldReturn (true);
    testOf (time). using (Checks.<ZonedDateTime>dsl().withField(ZonedDateTime::getDayOfMonth).having(Object::toString).equalTo("10").withField(ZonedDateTime::getMonth).equalTo(Month.MAY)). shouldReturn (true);
    testOf (NULL). using (Checks.<ZonedDateTime>dsl().withField(ZonedDateTime::getDayOfMonth).equalTo(10).withField(ZonedDateTime::getMonth).equalTo(Month.MAY)).                            shouldReturn (false);
    testOf (time). using (Checks.<ZonedDateTime>dsl().withField(ZonedDateTime::getDayOfMonth).equalTo(10).withField(ZonedDateTime::getMonth).equalTo(Month.APRIL)).                          shouldReturn (false);

}}
