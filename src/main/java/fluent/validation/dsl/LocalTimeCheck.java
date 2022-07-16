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

public final class LocalTimeCheck extends AbstractCheckDsl<LocalTimeCheck, LocalTime> {
    private LocalTimeCheck(Check<? super LocalTime> check) {
        super(check, LocalTimeCheck::new);
    }

    public static LocalTimeCheck localTimeWith() {
        return new LocalTimeCheck(anything());
    }

    public LocalTimeCheck hour(Check<? super Integer> check) {
        return has(LocalTime::getHour).matching(check);
    }

    public LocalTimeCheck hour(int expectedValue) {
        return hour(equalTo(expectedValue));
    }

    public LocalTimeCheck minute(Check<? super Integer> check) {
        return has(LocalTime::getMinute).matching(check);
    }

    public LocalTimeCheck minute(int expectedValue) {
        return minute(equalTo(expectedValue));
    }

    public LocalTimeCheck second(Check<? super Integer> check) {
        return has(LocalTime::getSecond).matching(check);
    }

    public LocalTimeCheck second(int expectedValue) {
        return second(equalTo(expectedValue));
    }

    public LocalTimeCheck nano(Check<? super Integer> check) {
        return has(LocalTime::getNano).matching(check);
    }

    public LocalTimeCheck nano(int expectedValue) {
        return nano(equalTo(expectedValue));
    }

}
