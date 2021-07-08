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

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

import static fluent.validation.result.Result.NAMESPACE;
import static java.util.Collections.emptyList;

@XmlRootElement(namespace = NAMESPACE)
public class TableInResult extends Result {

    @XmlAttribute
    private final Object prefix;

    @XmlElement
    private final List<Check<?>> rows;

    @XmlElement
    private final List<?> columns;

    @XmlElement
    private final List<Cell> results;

    public TableInResult(Object prefix, List<Check<?>> rows, List<?> columns, List<Cell> results, boolean result) {
        super(result);
        this.prefix = prefix;
        this.rows = rows;
        this.columns = columns;
        this.results = results;
    }

    @SuppressWarnings("unused")
    private TableInResult() {
        this(null, emptyList(), emptyList(), emptyList(), false);
    }

    @Override
    public void accept(ResultVisitor visitor) {
        visitor.tableAggregation(prefix, rows, columns, results, passed());
    }

    public static final class Cell {
        private final int row;
        private final int column;
        private final Result result;

        public Cell(int row, int column, Result result) {
            this.row = row;
            this.column = column;
            this.result = result;
        }

        public int getRow() {
            return row;
        }

        public int getColumn() {
            return column;
        }

        public Result getResult() {
            return result;
        }
    }

}
