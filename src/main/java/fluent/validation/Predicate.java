package fluent.validation;

public interface Predicate<D> {

    boolean test(D data) throws Exception;

}
