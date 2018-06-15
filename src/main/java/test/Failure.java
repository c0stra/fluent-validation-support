package test;

public class Failure extends AssertionError {

    public Failure(Object detail) {
        super(detail);
    }

}
