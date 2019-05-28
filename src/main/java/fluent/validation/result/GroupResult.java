package fluent.validation.result;

import java.util.ArrayList;
import java.util.List;

public class GroupResult extends Result {

    private final Object description;
    private final Object actualValueDescription;
    private final List<Result> itemResults;

    public GroupResult(boolean result, Object description, Object actualValueDescription, List<Result> itemResults) {
        super(result);
        this.description = description;
        this.actualValueDescription = actualValueDescription;
        this.itemResults = itemResults;
    }

    @Override
    public void accept(ResultVisitor visitor) {
        visitor.groupResult(description, actualValueDescription, passed(), itemResults);
    }

    static class Builder implements GroupResultBuilder {
        private final Object description;
        private final List<Result> itemResults = new ArrayList<>();

        Builder(Object description) {
            this.description = description;
        }

        @Override
        public Result add(Result itemResult) {
            itemResults.add(itemResult);
            return itemResult;
        }

        @Override
        public Result build(Object actualValueDescription, boolean result) {
            return new GroupResult(result, description, actualValueDescription, itemResults);
        }
    }

}
