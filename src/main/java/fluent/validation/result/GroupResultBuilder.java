package fluent.validation.result;

public interface GroupResultBuilder {
    Result add(Result itemResult);

    Result build(Object actualValueDescription, boolean result);
}
