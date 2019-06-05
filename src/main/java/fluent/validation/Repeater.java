package fluent.validation;

import java.time.Duration;
import java.util.Iterator;

public final class Repeater<T> implements Iterable<T> {

    private final T item;
    private final int max;
    private final long delay;

    private Repeater(T item, int max, long delayInMillis) {
        this.item = item;
        this.max = max;
        this.delay = delayInMillis;
    }

    public static <T> Repeater<T> repeat(T item, int max, long delayInMillis) {
        return new Repeater<>(item, max, delayInMillis);
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
            @Override
            public boolean hasNext() {
                return attempt < max;
            }

            @Override
            public T next() {
                if(attempt++ > 0) {
                    try {
                        Thread.sleep(delay);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                return item;
            }
        };
    }

}
