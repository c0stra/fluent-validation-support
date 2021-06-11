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

package fluent.validation;

import fluent.validation.dsl.LocalDateCheck;
import fluent.validation.dsl.LocalDateTimeCheck;
import fluent.validation.dsl.LocalTimeCheck;
import fluent.validation.dsl.ZonedDateTimeCheck;
import fluent.validation.processor.Factory;

import java.time.*;
import java.time.format.DateTimeFormatter;

import static fluent.validation.BasicChecks.compose;

@Factory
public final class DateTimeChecks {
    private DateTimeChecks() {}

    public static Check<LocalDateTime> date(Check<? super LocalDate> dateCheck) {
        return compose(LocalDateTime::toLocalDate, dateCheck);
    }

    public static Check<LocalDateTime> time(Check<? super LocalTime> dateCheck) {
        return compose(LocalDateTime::toLocalTime, dateCheck);
    }

    public static Check<LocalDateTime> zoned(Check<? super ZonedDateTime> zonedDateTimeCheck, ZoneId zoneId) {
        return compose(local -> local.atZone(zoneId), zonedDateTimeCheck);
    }

    public static Check<LocalDateTime> zoned(Check<? super ZonedDateTime> zonedDateTimeCheck) {
        return zoned(zonedDateTimeCheck, ZoneId.systemDefault());
    }

    public static LocalDateTimeCheck localDateTimeWith() {
        return LocalDateTimeCheck.localDateTimeWith();
    }

    public static LocalDateCheck localDateWith() {
        return LocalDateCheck.localDateWith();
    }

    public static LocalTimeCheck localTimeWith() {
        return LocalTimeCheck.localTimeWith();
    }

    public static ZonedDateTimeCheck zonedDateTimeWith() {
        return ZonedDateTimeCheck.zonedDateTimeWith();
    }

    public static Check<String> parseLocalDateTime(Check<? super LocalDateTime> check) {
        return parseLocalDateTime(DateTimeFormatter.ISO_LOCAL_DATE_TIME, check);
    }

    public static Check<String> parseLocalDateTime(String format, Check<? super LocalDateTime> check) {
        return parseLocalDateTime(DateTimeFormatter.ofPattern(format), check);
    }

    public static Check<String> parseLocalDateTime(DateTimeFormatter format, Check<? super LocalDateTime> check) {
        return compose("parse using " + format, string -> LocalDateTime.parse(string, format), check);
    }

    public static Check<String> parseZonedDateTime(Check<? super ZonedDateTime> check) {
        return parseZonedDateTime(DateTimeFormatter.ISO_LOCAL_DATE_TIME, check);
    }

    public static Check<String> parseZonedDateTime(String format, Check<? super ZonedDateTime> check) {
        return parseZonedDateTime(DateTimeFormatter.ofPattern(format), check);
    }

    public static Check<String> parseZonedDateTime(DateTimeFormatter format, Check<? super ZonedDateTime> check) {
        return compose(string -> ZonedDateTime.parse(string, format), check);
    }

}
