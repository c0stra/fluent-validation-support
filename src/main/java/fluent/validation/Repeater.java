package fluent.validation;

import java.time.Duration;
import java.util.Iterator;

public final class Repeater<T> implements Iterable<T> {

    private final T item;
    private final int max;
    private final int increment;
    private final long delayInMillis;

    private Repeater(T item, int max, int increment, long delayInMillis) {
        this.item = item;
        this.max = max;
        this.increment = increment;
        this.delayInMillis = delayInMillis;
    }

    public static <T> Repeater<T> repeat(T item, int max, long delayInMillis) {
        return new Repeater<>(item, max, 1, delayInMillis);
    }

    public static <T> Repeater<T> repeatForever(T item, long delayInMillis) {
        return new Repeater<>(item, 1, 0, delayInMillis);
    }

    public static <T> Repeater<T> repeatForever(T item, Duration delay) {
        return new Repeater<>(item, 1, 0, delay.toMillis());
    }

    public static <T> Repeater<T> repeat(T item, int max, Duration delay) {
        return repeat(item, max, delay.toMillis());
    }

    public static <T> Repeater<T> repeat(T item, int max) {
        return repeat(item, max, 1000);
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private int attempt = 0;
            @Override public boolean hasNext() {
                return attempt < max;
            }
            @Override public T next() {
                if(attempt > 0 && delayInMillis > 0) try {
                    Thread.sleep(delayInMillis);
                } catch (InterruptedException e) {
                    throw new UncheckedInterruptedException("Delay before repeating " + item + " interrupted", e);
                }
                attempt += increment;
                return item;
            }
        };
    }

}
