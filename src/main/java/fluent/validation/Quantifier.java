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

import fluent.validation.result.Aggregator;
import fluent.validation.result.Result;
import fluent.validation.result.ResultFactory;

final class Quantifier<D> implements Check<Iterable<D>> {

    private final String elementName;
    private final Type type;
    private final Check<? super D> check;

    Quantifier(String elementName, Type type, Check<? super D> check) {
        this.elementName = elementName;
        this.type = type;
        this.check = check;
    }

    @Override
    public Result evaluate(Iterable<D> data, ResultFactory factory) {
        Aggregator itemResults = factory.aggregator(this);
        boolean end = type == Type.exists;
        for(D item : data) {
            if(itemResults.add(check.evaluate(item, factory)).passed() == end) {
                return end ? itemResults.build(elementName + " " + check + " found", true) : itemResults.build(item + " doesn't match " + check, false);
            }
        }
        return end ? itemResults.build("No " + elementName + " " + check + " found", false) : itemResults.build("All " + elementName + "s matched " + check, true);
    }

    @Override
    public String toString() {
        return type + " " + elementName + " " + check;
    }

    enum Type {exists, every}

}
