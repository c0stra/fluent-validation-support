package fluent.validation;

class Requirement<T, E> {

    final T data;
    final Condition<? super T> condition;
    final E expectedResult;
    private final String toString;

    Requirement(T data, Condition<? super T> condition, E expectedResult, String toString) {
        this.data = data;
        this.condition = condition;
        this.expectedResult = expectedResult;
        this.toString = toString;
    }

    @Override
    public String toString() {
        return toString;
    }

}
