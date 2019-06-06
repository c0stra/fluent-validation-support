package fluent.validation.result;

public interface Aggregator {
    Result add(Result itemResult);

    Result build(Object actualValueDescription, boolean result);
}
