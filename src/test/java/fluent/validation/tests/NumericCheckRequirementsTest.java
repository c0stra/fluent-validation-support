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

import static fluent.validation.Checks.*;

public class NumericCheckRequirementsTest extends Requirements {{

    testOf (3.2). using (closeTo(3.0, 0.5)). shouldReturn (true);
    testOf (3.2). using (closeTo(3.0, 0.1)). shouldReturn (false);
    testOf (3.2f). using (closeTo(3.0f, 0.5f)). shouldReturn (true);
    testOf (3.2f). using (closeTo(3.0f, 0.1f)). shouldReturn (false);

    testOf ("5.3"). using (parseDouble(equalTo(5.3))). shouldReturn (true);
    testOf ("-5.3"). using (parseDouble(equalTo(-5.3))). shouldReturn (true);
    testOf ("0.53e1"). using (parseDouble(equalTo(5.3))). shouldReturn (true);
    testOf ("5.3"). using (parseDouble(equalTo(5.7))). shouldReturn (false);
    testOf ((String) null). using (parseDouble(isNull())). shouldReturn (true);
    testOf ((String) null). using (parseDouble(equalTo(5.3))). shouldReturn (false);
    testOf (""). using (parseDouble(equalTo(5.7))). shouldReturn (false);
    testOf ("AAA"). using (parseDouble(equalTo(5.7))). shouldReturn (false);

    assertOf ("AAA"). using (parseDouble(5.3)). shouldFailWith ("expected: parseDouble  has thrown java.lang.NumberFormatException: For input string: \"AAA\"");

    testOf ("5.3"). using (parseFloat(5.3f)). shouldReturn (true);
    testOf ("-5.3"). using (parseFloat(-5.3f)). shouldReturn (true);
    testOf ("0.53e1"). using (parseFloat(5.3f)). shouldReturn (true);
    testOf ("5.3"). using (parseFloat(5.7f)). shouldReturn (false);
    testOf ((String) null). using (parseFloat(isNull())). shouldReturn (true);
    testOf ((String) null). using (parseFloat(equalTo(5.3f))). shouldReturn (false);

    testOf ("5"). using (parseInt(5)). shouldReturn (true);
    testOf ("5"). using (parseInt(7)). shouldReturn (false);
    testOf ((String) null). using (parseInt(isNull())). shouldReturn (true);
    testOf ((String) null). using (parseInt(5)). shouldReturn (false);

    testOf ("5"). using (parseLong(5)). shouldReturn (true);
    testOf ("5"). using (parseLong(7)). shouldReturn (false);
    testOf ((String) null). using (parseLong(isNull())). shouldReturn (true);
    testOf ((String) null). using (parseLong(5)). shouldReturn (false);

}}
