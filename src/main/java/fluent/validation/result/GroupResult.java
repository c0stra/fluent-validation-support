package fluent.validation.result;

import java.util.ArrayList;
import java.util.List;

public class GroupResult extends Result {

    private final Object description;
    private final List<Result> itemResults;

    public GroupResult(boolean result, Object description, List<Result> itemResults) {
        super(result);
        this.description = description;
        this.itemResults = itemResults;
    }

    @Override
    public void accept(ResultVisitor visitor) {
        visitor.groupResult(description, passed(), itemResults);
    }

    public static class Builder {
        private final Object description;
        private final List<Result> itemResults = new ArrayList<>();

        public Builder(Object description) {
            this.description = description;
        }

        public Result add(Result itemResult) {
            itemResults.add(itemResult);
            return itemResult;
        }
        public Result build(boolean result) {
            return new GroupResult(result, description, itemResults);
        }
    }

}
