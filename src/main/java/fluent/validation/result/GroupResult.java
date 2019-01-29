package fluent.validation.result;

import java.util.ArrayList;
import java.util.List;

public class GroupResult extends Result {

    private final List<Result> itemResults;

    public GroupResult(boolean result, List<Result> itemResults) {
        super(result);
        this.itemResults = itemResults;
    }

    public static class Builder {
        private final List<Result> itemResults = new ArrayList<>();
        public Result add(Result itemResult) {
            itemResults.add(itemResult);
            return itemResult;
        }
        public Result build(boolean result) {
            return new GroupResult(result, itemResults);
        }
    }

}
