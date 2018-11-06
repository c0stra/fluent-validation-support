package fluent.validation;

class Requirement<T, E> {

    final T data;
    final Check<? super T> check;
    final E expectedResult;
    private final String toString;

    Requirement(T data, Check<? super T> check, E expectedResult, String toString) {
        this.data = data;
        this.check = check;
        this.expectedResult = expectedResult;
        this.toString = toString;
    }

    @Override
    public String toString() {
        return toString;
    }

}
