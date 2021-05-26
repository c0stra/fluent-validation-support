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
import fluent.validation.result.TableAggregator;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static java.util.stream.Collectors.toCollection;
import static java.util.stream.IntStream.range;

final class ResultSetCheckInAnyOrder extends Check<ResultSet> {

    private final ArrayList<Check<? super ResultSet>> checks;
    private final boolean full;
    private final boolean exact;

    ResultSetCheckInAnyOrder(Collection<Check<? super ResultSet>> checks, boolean full, boolean exact) {
        this.checks = new ArrayList<>(checks);
        this.full = full;
        this.exact = exact;
    }

    private boolean matchesAnyAndRemoves(ResultSet item, List<Integer> rows, int column, TableAggregator<ResultSet> table, ResultFactory factory) {
        Iterator<Integer> c = rows.iterator();
        while (c.hasNext()) {
            int row = c.next();
            Result result = checks.get(row).evaluate(item, factory);
            table.cell(row, column, result);
            if (result.passed()) {
                c.remove();
                return true;
            }
        }
        return false;
    }

    @Override
    public Result evaluate(ResultSet data, ResultFactory factory) {
        if(data == null) {
            return factory.expectation(this, false);
        }
        TableAggregator<ResultSet> resultBuilder = factory.table(this, checks);
        List<Integer> rows = range(0, checks.size()).boxed().collect(toCollection(LinkedList::new));
        try {
            while (data.next()) {
                int column = resultBuilder.column("record " + data.getRow());
                if(rows.isEmpty()) {
                    return full ? resultBuilder.build("Extra items found", false) : resultBuilder.build("Prefix matched", true);
                }
                if (!matchesAnyAndRemoves(data, rows, column, resultBuilder, factory) && exact) {
                    return resultBuilder.build("Extra items found", false);
                }
            }
        } catch (SQLException e) {
            return resultBuilder.build(e.getMessage(), false);
        }
        return resultBuilder.build(rows.isEmpty() ? "All checks satisfied": "" + rows.size() + " checks not satisfied", rows.isEmpty());
    }

    @Override
    public String toString() {
        return "Records matching in any order " + checks;
    }

}
