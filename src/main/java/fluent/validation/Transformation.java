package fluent.validation;

public interface Transformation<F, T> {

    T apply(F from) throws Exception;

}
