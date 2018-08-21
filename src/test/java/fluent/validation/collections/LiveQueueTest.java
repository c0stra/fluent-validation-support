package fluent.validation.collections;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.Iterator;
import java.util.List;

import static fluent.validation.collections.LiveQueue.Mode.KEEP_ALL;
import static fluent.validation.collections.LiveQueue.Mode.REMOVE_ON_NEXT;
import static fluent.validation.collections.LiveQueue.lockOf;
import static fluent.validation.utils.Async.async;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.testng.Assert.*;

public class LiveQueueTest {

    @Test
    public void testAsyncAdd() {
        LiveQueue<String> strings = new LiveQueue<>(Duration.ofMillis(200));
        synchronized (lockOf(strings)) {
            async(() -> strings.add("A"));
            assertEquals(strings.peek(), "A");
            assertEquals(strings.poll(), "A");
            assertNull(strings.peek());
            assertTrue(strings.isEmpty());
        }
    }

    @DataProvider
    public static Object[][] iterableData() {
        return new Object[][] {
                {KEEP_ALL, asList("A", "B", "C")},
                {REMOVE_ON_NEXT, singletonList("C")}
        };
    }

    @Test(dataProvider = "iterableData")
    public void testAsyncIterable(LiveQueue.Mode mode, List<String> finalState) {
        LiveQueue<String> strings = new LiveQueue<>(Duration.ofMillis(200), mode);
        synchronized (lockOf(strings)) {
            async(() -> {
                strings.add("A");
                strings.add("B");
                strings.add("C");
            });
            Iterator<String> iterator = strings.iterator();
            assertTrue(iterator.hasNext());
            assertEquals(iterator.next(), "A");
            assertTrue(iterator.hasNext());
            assertEquals(iterator.next(), "B");
            assertTrue(iterator.hasNext());
            assertEquals(iterator.next(), "C");
            assertFalse(iterator.hasNext());
            assertEquals(strings, finalState);
        }
    }

    @Test
    public void testView() {
        LiveQueue<String> strings = new LiveQueue<>(Duration.ofMillis(2));
        LiveQueue<String> view = strings.withTimeout(Duration.ofMillis(1));
        view.add("A");
        view.add("B");
        assertEquals(view, asList("A", "B"));
        assertEquals(strings, asList("A", "B"));
    }

}
