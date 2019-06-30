package fluent.validation;

public class CheckInterruptedException extends RuntimeException {

    public CheckInterruptedException(Object what, Throwable cause) {
        super(what.toString(), cause);
    }

}
