package fluent.validation.result;

public interface TableAggregator<D> {

    Result build(String description, int column, boolean value);

    Result build(String description, boolean value);

    void cell(int row, int column, Result result);

    int column(Object item);

}
