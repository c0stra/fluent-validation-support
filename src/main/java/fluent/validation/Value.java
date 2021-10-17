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

import fluent.validation.result.Result;
import fluent.validation.result.ResultFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Special type of Check, which implements extraction of a value out of the tested data for later use.
 * It's not only extracting a value. It also enforcing a check on that value.
 *
 * The motivation fo this approach is:
 * Often the subject of the validation is a stream of events, or it needs to be retried due to asynchronous nature
 * of the tested system.
 * Simply calling some API, and immediate getting of the value could lead to get unexpected result, because the
 * system didn't get to the desired state yet.
 * Or in case of stream of events, we may look for a state which meets some complex checks. Once that one is identified,
 * we'd need to find out the index etc.
 *
 * With te Value, we simply plug the value extraction (a.k.a. capturing) into such complex check, and we can access the
 * value later.
 *
 * @param <D> Type of the value to capture.
 */
public class Value<D> {
    private final List<D> values = new ArrayList<>();

    public final Check<D> storeWhen(Check<? super D> condition) {
        return new Check<D>() {
            @Override
            public Result evaluate(D data, ResultFactory factory) {
                Result result = condition.evaluate(data, factory);
                if(result.passed()) {
                    values.add(data);
                }
                return result;
            }

            @Override
            public String toString() {
                return "Store value when " + condition;
            }
        };
    }

    public final D get() {
        if(values.isEmpty())
            throw new IllegalStateException("Value was not stored yet. Use method storeWhen(check) first or check, if evaluation of that method wasn't missed due to fast pass / fail composition or requirement not met in required check.");
        return values.get(values.size() - 1);
    }

    public final List<D> getAll() {
        if(values.isEmpty())
            throw new IllegalStateException("Value was not stored yet. Use method storeWhen(check) first or check, if evaluation of that method wasn't missed due to fast pass / fail composition or requirement not met in required check.");
        return new ArrayList<>(values);
    }

}
