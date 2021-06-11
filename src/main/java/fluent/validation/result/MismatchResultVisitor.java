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

package fluent.validation.result;

import fluent.validation.Check;

import java.util.List;

public final class MismatchResultVisitor implements ResultVisitor {

    private final boolean failureIndicator;
    private final Object actualValueDescription;
    private final StringBuilder builder;
    private final String prefix;

    private MismatchResultVisitor(boolean failureIndicator, Object actualValueDescription, StringBuilder builder, String prefix) {
        this.failureIndicator = failureIndicator;
        this.actualValueDescription = actualValueDescription;
        this.builder = builder;
        this.prefix = prefix;
    }

    private MismatchResultVisitor(Object actualValueDescription, String prefix) {
        this(false, actualValueDescription, new StringBuilder(), prefix);
    }

    public MismatchResultVisitor() {
        this(null, "\n\t");
    }

    @Override
    public ResultVisitor visit(Result result) {
        if(result.failed()) {
            builder.append("expected: ");
            new ExpectationVisitor(builder).visit(result);
            result.accept(this);
        }
        return this;
    }

    @Override
    public void actual(Object actualValue, Result result) {
        result.accept(new MismatchResultVisitor(failureIndicator, actualValue, builder, prefix));
    }

    @Override
    public void expectation(Object expectation, boolean result) {
        builder.append(" but was: <").append(actualValueDescription).append('>');//.append('\n');
    }

    @Override
    public void transformation(Object name, Result dependency, boolean result) {
        //builder.append(name).append(' ');
        dependency.accept(this);
    }

    @Override
    public void aggregation(Object description, String glue, List<Result> itemResults, boolean result) {
        builder.append(" but was: ").append(actualValueDescription);
        itemResults.stream().filter(r -> r.passed() == failureIndicator).forEach(r -> {
            builder.append(prefix).append("+ ");
            new MismatchResultVisitor(failureIndicator, actualValueDescription, builder, prefix + '\n').visit(r);
        });
    }

    @Override
    public void tableAggregation(Object prefix, List<Check<?>> checks, List<?> items, List<TableInResult.Cell> results, boolean value) {
        builder.append(" but: ").append(actualValueDescription);
        boolean[] rows = new boolean[checks.size()];
        boolean[] cols = new boolean[items.size()];
        results.forEach(cell -> {
            if(cell.getResult().passed()) {
                rows[cell.getRow()] = true;
                cols[cell.getColumn()] = true;
            }
        });
        results.stream().filter(cell -> !rows[cell.getRow()] && !cols[cell.getColumn()]).forEach(r -> {
            builder.append(this.prefix).append("+ ");
            new MismatchResultVisitor(failureIndicator, items.get(r.getColumn()), builder, this.prefix + '\n').visit(r.getResult());
        });
    }

    @Override
    public void error(Throwable throwable) {
        builder.append(" has thrown ").append(throwable);
    }

    @Override
    public void invert(Result result) {
        result.accept(new MismatchResultVisitor(!failureIndicator, actualValueDescription, builder, prefix));
    }

    @Override
    public void soft(Result result) {
        result.accept(this);
    }

    @Override
    public String toString() {
        return builder.toString();
    }

}
