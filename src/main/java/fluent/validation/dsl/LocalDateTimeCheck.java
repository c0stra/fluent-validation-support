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

public final class LocalDateTimeCheck extends AbstractCheckDsl<LocalDateTimeCheck, LocalDateTime> {
    private LocalDateTimeCheck(Check<? super LocalDateTime> check) {
        super(check, LocalDateTimeCheck::new);
    }

    public static LocalDateTimeCheck localDateTimeWith() {
        return new LocalDateTimeCheck(anything());
    }

    public LocalDateTimeCheck date(Check<? super LocalDate> check) {
        return withField(LocalDateTime::toLocalDate).matching(check);
    }

    public LocalDateTimeCheck time(Check<? super LocalTime> check) {
        return withField(LocalDateTime::toLocalTime).matching(check);
    }

    public LocalDateTimeCheck year(Check<? super Integer> check) {
        return withField(LocalDateTime::getYear).matching(check);
    }

    public LocalDateTimeCheck year(int expectedValue) {
        return year(equalTo(expectedValue));
    }

    public LocalDateTimeCheck month(Check<? super Month> check) {
        return withField(LocalDateTime::getMonth).matching(check);
    }

    public LocalDateTimeCheck month(Month expectedValue) {
        return month(equalTo(expectedValue));
    }

    public LocalDateTimeCheck monthValue(Check<? super Integer> check) {
        return withField(LocalDateTime::getMonthValue).matching(check);
    }

    public LocalDateTimeCheck month(int expectedValue) {
        return monthValue(equalTo(expectedValue));
    }

    public LocalDateTimeCheck day(Check<? super Integer> check) {
        return withField(LocalDateTime::getDayOfMonth).matching(check);
    }

    public LocalDateTimeCheck day(int expectedValue) {
        return day(equalTo(expectedValue));
    }

    public LocalDateTimeCheck dayOfYear(Check<? super Integer> check) {
        return withField(LocalDateTime::getDayOfYear).matching(check);
    }

    public LocalDateTimeCheck dayOfYear(int expectedValue) {
        return dayOfYear(equalTo(expectedValue));
    }

    public LocalDateTimeCheck dayOfWeek(Check<? super DayOfWeek> check) {
        return withField(LocalDateTime::getDayOfWeek).matching(check);
    }

    public LocalDateTimeCheck dayOfWeek(DayOfWeek expectedValue) {
        return dayOfWeek(equalTo(expectedValue));
    }

    public LocalDateTimeCheck hour(Check<? super Integer> check) {
        return withField(LocalDateTime::getHour).matching(check);
    }

    public LocalDateTimeCheck hour(int expectedValue) {
        return hour(equalTo(expectedValue));
    }

    public LocalDateTimeCheck minute(Check<? super Integer> check) {
        return withField(LocalDateTime::getMinute).matching(check);
    }

    public LocalDateTimeCheck minute(int expectedValue) {
        return minute(equalTo(expectedValue));
    }

    public LocalDateTimeCheck second(Check<? super Integer> check) {
        return withField(LocalDateTime::getSecond).matching(check);
    }

    public LocalDateTimeCheck second(int expectedValue) {
        return second(equalTo(expectedValue));
    }

    public LocalDateTimeCheck nano(Check<? super Integer> check) {
        return withField(LocalDateTime::getNano).matching(check);
    }

    public LocalDateTimeCheck nano(int expectedValue) {
        return nano(equalTo(expectedValue));
    }

}
