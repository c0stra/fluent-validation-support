package fluent.validation.evaluation;

import fluent.validation.Check;
import fluent.validation.result.Result;

public class Analyzer {

    private final Context context;

    public Analyzer(Context context) {
        this.context = context;
    }

    private <T> void log(Result result, T data, Check<? super T> check, Conclusion conclusion) {
        System.out.println(result.toString());
        if(result.passed()) conclusion.conclude(true, context);
    }

    public <T> Def when(T data, Check<? super T> check) {
        return conclusion -> log(Check.evaluate(data, check), data, check, conclusion);
    }

    public Def when(Statement statement) {
        return conclusion -> {};
    }

    public interface Def {
        void then(Conclusion conclusion);
        default Def and(Statement statement) { return this;}
        default <T> Def and(T data, Check<? super T> check) { return this;}
    }

}
