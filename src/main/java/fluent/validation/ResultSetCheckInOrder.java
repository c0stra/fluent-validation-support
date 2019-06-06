/*
 * Copyright © 2018 Ondrej Fischer. All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that
 * the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the
 *    following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the
 *    following disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * 3. The name of the author may not be used to endorse or promote products derived from this software without
 *     specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY [LICENSOR] "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE
 * GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 */

package fluent.validation;

import fluent.validation.result.CheckDescription;
import fluent.validation.result.Aggregator;
import fluent.validation.result.Result;
import fluent.validation.result.ResultFactory;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Check, making sure, that an actual collection meets provided conditions in exact order:
 *   1st item matches 1st condition, 2nd item matches 2nd condition, etc. and there must not be any item missing or
 *   extra (length of actual collection needs to match length of collection of conditions).
 */
final class ResultSetCheckInOrder extends Check<ResultSet> implements CheckDescription {

    private final Iterable<Check<? super ResultSet>> checks;
    private final boolean full;
    private final boolean exact;

    ResultSetCheckInOrder(Iterable<Check<? super ResultSet>> checks, boolean full, boolean exact) {
        this.checks = checks;
        this.full = full;
        this.exact = exact;
    }

    private boolean match(Check<? super ResultSet> check, ResultSet data, Aggregator resultBuilder, ResultFactory factory) throws SQLException {
        while (data.next()){
            if(resultBuilder.add(check.evaluate(data, factory)).passed()) {
                return true;
            }
            if(exact) {
                return false;
            }
        }
        return false;
    }

    @Override
    public Result evaluate(ResultSet data, ResultFactory factory) {
        if(data == null) {
            return factory.expectation(this, false);
        }
        Aggregator resultBuilder = factory.aggregator(this);
        try {
            for(Check<? super ResultSet> check : checks) {
                if(!match(check, data, resultBuilder, factory)) {
                    return resultBuilder.build(check + " not matched by any item", false);
                }
            }
            if(full && exact && !data.next()) {
                return resultBuilder.build("Extra record found", false);
            }

        } catch (SQLException e) {
            return resultBuilder.build(e, false);
        }
        return resultBuilder.build("Items matched checks", true);
    }

    @Override
    public String toString() {
        return "Items matching " + checks;
    }

    @Override
    public String description() {
        return toString();
    }
}
