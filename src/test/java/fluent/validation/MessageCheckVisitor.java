package fluent.validation;

import fluent.validation.detail.CheckVisitor;

public class MessageCheckVisitor implements CheckVisitor {

    private final StringBuilder builder = new StringBuilder();

    @Override
    public void trace(String expectation, Object actualValue, boolean result) {
        builder.append("Expectation:\n---------------------\n").append(expectation).append("\n---------------------\n")
                .append("Actual:\n---------------------\n").append(actualValue).append("\n---------------------\n");
    }

    @Override
    public CheckVisitor node(Check<?> nodeName) {
        return this;
    }

    @Override
    public CheckVisitor label(Check<?> name) {
        return this;
    }

    @Override
    public void trace(Object data, boolean result) {

    }

    @Override
    public String toString() {
        return builder.toString();
    }
}
