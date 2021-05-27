package fluent.validation.tests;

import fluent.validation.CheckDsl;
import fluent.validation.Checks;
import fluent.validation.tests.dsl.MyCheck;
import fluent.validation.utils.Requirements;

import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class CheckDslRequirementsTest extends Requirements {{

    testOf("A").using(new MyCheck().withHashCode(65).withToString("A")).shouldReturn(true);
    testOf("A").using(new MyCheck().withToString("A").withHashCode(10)).shouldReturn(false);
    testOf("A").using(new MyCheck().withToString("B").or().withHashCode(65)).shouldReturn(true);
    testOf(null).using(new MyCheck().withToString("B").or().withHashCode(65)).shouldReturn(false);

    ZonedDateTime time = ZonedDateTime.of(2021, 5, 10, 8, 0, 21, 5332, ZoneId.of("GMT"));
    testOf(time).using(Checks.<ZonedDateTime>dsl().withField(ZonedDateTime::getDayOfMonth).equalTo(10).withField(ZonedDateTime::getMonth).equalTo(Month.MAY)).shouldReturn(true);
    testOf((ZonedDateTime) null).using(Checks.<ZonedDateTime>dsl().withField(ZonedDateTime::getDayOfMonth).equalTo(10).withField(ZonedDateTime::getMonth).equalTo(Month.MAY)).shouldReturn(false);
    testOf(time).using(Checks.<ZonedDateTime>dsl().withField(ZonedDateTime::getDayOfMonth).equalTo(10).withField(ZonedDateTime::getMonth).equalTo(Month.APRIL)).shouldReturn(false);

}}
