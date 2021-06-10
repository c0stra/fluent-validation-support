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

public final class LocalDateCheck extends AbstractCheckDsl<LocalDateCheck, LocalDate> {
    private LocalDateCheck(Check<? super LocalDate> check) {
        super(check, LocalDateCheck::new);
    }

    public static LocalDateCheck localDateWith() {
        return new LocalDateCheck(anything());
    }

    public LocalDateCheck year(Check<? super Integer> check) {
        return withField(LocalDate::getYear).matching(check);
    }

    public LocalDateCheck year(int expectedValue) {
        return year(equalTo(expectedValue));
    }

    public LocalDateCheck month(Check<? super Month> check) {
        return withField(LocalDate::getMonth).matching(check);
    }

    public LocalDateCheck month(Month expectedValue) {
        return month(equalTo(expectedValue));
    }

    public LocalDateCheck monthValue(Check<? super Integer> check) {
        return withField(LocalDate::getMonthValue).matching(check);
    }

    public LocalDateCheck month(int expectedValue) {
        return monthValue(equalTo(expectedValue));
    }

    public LocalDateCheck day(Check<? super Integer> check) {
        return withField(LocalDate::getDayOfMonth).matching(check);
    }

    public LocalDateCheck day(int expectedValue) {
        return day(equalTo(expectedValue));
    }

    public LocalDateCheck dayOfYear(Check<? super Integer> check) {
        return withField(LocalDate::getDayOfYear).matching(check);
    }

    public LocalDateCheck dayOfYear(int expectedValue) {
        return dayOfYear(equalTo(expectedValue));
    }

    public LocalDateCheck dayOfWeek(Check<? super DayOfWeek> check) {
        return withField(LocalDate::getDayOfWeek).matching(check);
    }

    public LocalDateCheck dayOfWeek(DayOfWeek expectedValue) {
        return dayOfWeek(equalTo(expectedValue));
    }

}
