package fluent.validation;

import fluent.validation.result.MismatchResultVisitor;
import fluent.validation.result.Result;
import fluent.validation.result.ResultFactory;
import fluent.validation.result.ResultVisitor;

public final class Assert {
    private Assert() {}

    /**
     * Assert the data using provided check.
     *
     * @param data Tested data.
     * @param check Check to be applied.
     * @param <T> Type of the tested data.
     */
    public static <T> void that(T data, Check<? super T> check) {
        that(data, check, ResultFactory.DEFAULT);
    }

    /**
     * Assert the data using provided check.
     *
     * @param data Tested data.
     * @param check Check to be applied.
     * @param <T> Type of the tested data.
     */
    public static <T> void that(T data, Check<? super T> check, ResultFactory resultFactory) {
        that(data, check, resultFactory, new MismatchResultVisitor());
    }

    public static <T> void that(T data, Check<? super T> check, ResultFactory resultFactory, ResultVisitor visitor) {
        Result result = resultFactory.actual(data, Check.evaluate(data, check, resultFactory));
        if(result.failed()) {
            throw new AssertionFailure(visitor.visit(result));
        }
    }

}
