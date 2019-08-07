package fluent.validation.result;

import fluent.validation.Check;

import java.util.List;

public class TableInResult extends Result {

    private final Object prefix;
    private final List<Check<?>> rows;
    private final List<?> columns;
    private final List<Cell> results;

    public TableInResult(Object prefix, List<Check<?>> rows, List<?> columns, List<Cell> results, boolean result) {
        super(result);
        this.prefix = prefix;
        this.rows = rows;
        this.columns = columns;
        this.results = results;
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
