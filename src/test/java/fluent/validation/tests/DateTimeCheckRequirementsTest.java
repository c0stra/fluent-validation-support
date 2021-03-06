/*
 * BSD 2-Clause License
 *
 * Copyright (c) 2021, Ondrej Fischer
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package fluent.validation.tests;

import fluent.validation.utils.Requirements;

import java.time.*;

import static fluent.validation.DateTimeChecks.*;
import static java.time.DayOfWeek.*;
import static java.time.Month.APRIL;
import static java.time.Month.MAY;

public class DateTimeCheckRequirementsTest extends Requirements {{

    LocalDateTime localDateTime = LocalDateTime.of(2021, 4, 17, 21, 45, 18, 10324);

    testOf ((LocalDateTime) null). using  (localDateTimeWith().year(2021))   . shouldReturn(false);
    testOf (localDateTime)       . using  (localDateTimeWith().year(2021))   . shouldReturn(true);
    testOf (localDateTime)       . using  (localDateTimeWith().year(2020))   . shouldReturn(false);
    testOf (localDateTime)       . using  (localDateTimeWith().month(4))     . shouldReturn(true);
    testOf (localDateTime)       . using  (localDateTimeWith().month(APRIL)) . shouldReturn(true);
    testOf (localDateTime)       . using  (localDateTimeWith().month(6))     . shouldReturn(false);
    testOf (localDateTime)       . using  (localDateTimeWith().month(MAY))   . shouldReturn(false);
    testOf (localDateTime)       . using  (localDateTimeWith().day(17))      . shouldReturn(true);
    testOf (localDateTime)       . using  (localDateTimeWith().day(8))       . shouldReturn(false);
    testOf (localDateTime)       . using  (localDateTimeWith().dayOfYear(107)). shouldReturn(true);
    testOf (localDateTime)       . using  (localDateTimeWith().dayOfYear(8)) . shouldReturn(false);
    testOf (localDateTime)       . using  (localDateTimeWith().dayOfWeek(SATURDAY)). shouldReturn(true);
    testOf (localDateTime)       . using  (localDateTimeWith().dayOfWeek(FRIDAY)) . shouldReturn(false);

    testOf("2021/04/17 21:45:18.012").using(parseLocalDateTime("yyyy/MM/dd HH:mm:ss.SSS", localDateTimeWith().year(2021).month(4).day(17))).shouldReturn(true);

    ZonedDateTime zonedDateTime = ZonedDateTime.of(2021, 4, 17, 21, 45, 18, 10324, ZoneId.systemDefault());
    testOf ((ZonedDateTime) null). using  (zonedDateTimeWith().year(2021))   . shouldReturn(false);
    testOf (zonedDateTime). using  (zonedDateTimeWith().year(2021))   . shouldReturn(true);
    testOf (zonedDateTime). using  (zonedDateTimeWith().year(2020))   . shouldReturn(false);
    testOf (zonedDateTime). using  (zonedDateTimeWith().month(4))     . shouldReturn(true);
    testOf (zonedDateTime). using  (zonedDateTimeWith().month(APRIL)) . shouldReturn(true);
    testOf (zonedDateTime). using  (zonedDateTimeWith().month(6))     . shouldReturn(false);
    testOf (zonedDateTime). using  (zonedDateTimeWith().month(MAY))   . shouldReturn(false);
    testOf (zonedDateTime). using  (zonedDateTimeWith().day(17))      . shouldReturn(true);
    testOf (zonedDateTime). using  (zonedDateTimeWith().day(8))       . shouldReturn(false);
    testOf (zonedDateTime). using  (zonedDateTimeWith().dayOfYear(107)). shouldReturn(true);
    testOf (zonedDateTime). using  (zonedDateTimeWith().dayOfYear(8)) . shouldReturn(false);
    testOf (zonedDateTime). using  (zonedDateTimeWith().dayOfWeek(SATURDAY)). shouldReturn(true);
    testOf (zonedDateTime). using  (zonedDateTimeWith().dayOfWeek(FRIDAY)) . shouldReturn(false);


    LocalDate localDate = LocalDate.of(2021, 4, 17);

    testOf ((LocalDate) null). using  (localDateWith().year(2021))   . shouldReturn(false);
    testOf (localDate)       . using  (localDateWith().year(2021))   . shouldReturn(true);
    testOf (localDate)       . using  (localDateWith().year(2020))   . shouldReturn(false);
    testOf (localDate)       . using  (localDateWith().month(4))     . shouldReturn(true);
    testOf (localDate)       . using  (localDateWith().month(APRIL)) . shouldReturn(true);
    testOf (localDate)       . using  (localDateWith().month(6))     . shouldReturn(false);
    testOf (localDate)       . using  (localDateWith().month(MAY))   . shouldReturn(false);
    testOf (localDate)       . using  (localDateWith().day(17))      . shouldReturn(true);
    testOf (localDate)       . using  (localDateWith().day(8))       . shouldReturn(false);
    testOf (localDate)       . using  (localDateWith().dayOfYear(107)). shouldReturn(true);
    testOf (localDate)       . using  (localDateWith().dayOfYear(8)) . shouldReturn(false);
    testOf (localDate)       . using  (localDateWith().dayOfWeek(SATURDAY)). shouldReturn(true);
    testOf (localDate)       . using  (localDateWith().dayOfWeek(FRIDAY)) . shouldReturn(false);

}}