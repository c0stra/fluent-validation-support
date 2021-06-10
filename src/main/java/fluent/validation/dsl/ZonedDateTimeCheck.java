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

package fluent.validation.dsl;

import fluent.validation.AbstractCheckDsl;
import fluent.validation.Check;

import java.time.*;

import static fluent.validation.BasicChecks.anything;
import static fluent.validation.BasicChecks.equalTo;

public final class ZonedDateTimeCheck extends AbstractCheckDsl<ZonedDateTimeCheck, ZonedDateTime> {
    private ZonedDateTimeCheck(Check<? super ZonedDateTime> check) {
        super(check, ZonedDateTimeCheck::new);
    }

    public static ZonedDateTimeCheck zonedDateTimeWith() {
        return new ZonedDateTimeCheck(anything());
    }

    public ZonedDateTimeCheck date(Check<? super LocalDate> check) {
        return withField(ZonedDateTime::toLocalDate).matching(check);
    }

    public ZonedDateTimeCheck time(Check<? super LocalTime> check) {
        return withField(ZonedDateTime::toLocalTime).matching(check);
    }

    public ZonedDateTimeCheck year(Check<? super Integer> check) {
        return withField(ZonedDateTime::getYear).matching(check);
    }

    public ZonedDateTimeCheck year(int expectedValue) {
        return year(equalTo(expectedValue));
    }

    public ZonedDateTimeCheck month(Check<? super Month> check) {
        return withField(ZonedDateTime::getMonth).matching(check);
    }

    public ZonedDateTimeCheck month(Month expectedValue) {
        return month(equalTo(expectedValue));
    }

    public ZonedDateTimeCheck monthValue(Check<? super Integer> check) {
        return withField(ZonedDateTime::getMonthValue).matching(check);
    }

    public ZonedDateTimeCheck month(int expectedValue) {
        return monthValue(equalTo(expectedValue));
    }

    public ZonedDateTimeCheck day(Check<? super Integer> check) {
        return withField(ZonedDateTime::getDayOfMonth).matching(check);
    }

    public ZonedDateTimeCheck day(int expectedValue) {
        return day(equalTo(expectedValue));
    }

    public ZonedDateTimeCheck dayOfYear(Check<? super Integer> check) {
        return withField(ZonedDateTime::getDayOfYear).matching(check);
    }

    public ZonedDateTimeCheck dayOfYear(int expectedValue) {
        return dayOfYear(equalTo(expectedValue));
    }

    public ZonedDateTimeCheck dayOfWeek(Check<? super DayOfWeek> check) {
        return withField(ZonedDateTime::getDayOfWeek).matching(check);
    }

    public ZonedDateTimeCheck dayOfWeek(DayOfWeek expectedValue) {
        return dayOfWeek(equalTo(expectedValue));
    }

    public ZonedDateTimeCheck hour(Check<? super Integer> check) {
        return withField(ZonedDateTime::getHour).matching(check);
    }

    public ZonedDateTimeCheck hour(int expectedValue) {
        return hour(equalTo(expectedValue));
    }

    public ZonedDateTimeCheck minute(Check<? super Integer> check) {
        return withField(ZonedDateTime::getMinute).matching(check);
    }

    public ZonedDateTimeCheck minute(int expectedValue) {
        return minute(equalTo(expectedValue));
    }

    public ZonedDateTimeCheck second(Check<? super Integer> check) {
        return withField(ZonedDateTime::getSecond).matching(check);
    }

    public ZonedDateTimeCheck second(int expectedValue) {
        return second(equalTo(expectedValue));
    }

    public ZonedDateTimeCheck nano(Check<? super Integer> check) {
        return withField(ZonedDateTime::getNano).matching(check);
    }

    public ZonedDateTimeCheck nano(int expectedValue) {
        return nano(equalTo(expectedValue));
    }

}
