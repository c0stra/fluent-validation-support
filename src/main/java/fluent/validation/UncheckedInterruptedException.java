package fluent.validation;

public class UncheckedInterruptedException extends RuntimeException {

    public UncheckedInterruptedException(Object what, Throwable cause) {
        super(what.toString(), cause);
    }

}
