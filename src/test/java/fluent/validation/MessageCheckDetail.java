package fluent.validation;

import fluent.validation.detail.CheckDetail;

public class MessageCheckDetail implements CheckDetail {

    private final StringBuilder builder = new StringBuilder();

    @Override
    public void trace(String expectation, Object actualValue, boolean result) {
        builder.append("Expectation:\n---------------------\n").append(expectation).append("\n---------------------\n")
                .append("Actual:\n---------------------\n").append(actualValue).append("\n---------------------\n");
    }

    @Override
    public Node node(Check<?> nodeName) {
        return new Node() {
            @Override
            public CheckDetail detailFailingOn(boolean indicateFailure) {
                return MessageCheckDetail.this;
            }

            @Override
            public void trace(Object actualData, boolean result) {

            }
        };
    }

    @Override
    public CheckDetail label(Check<?> name) {
        return this;
    }

    @Override
    public String toString() {
        return builder.toString();
    }
}
