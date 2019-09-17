package fluent.validation.tests;

import fluent.validation.Assert;
import fluent.validation.Check;
import fluent.validation.UncheckedInterruptedException;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import static fluent.validation.CollectionChecks.*;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.*;

public class CollectionChecksTest {

    @DataProvider
    public static Object[][] collectionCheckData() {
        return new Object[][] {
                {collection(equalTo(items("A", "C", "D"))), asList("A", "C", "D"), true},
                {collection(equalTo(items("A", "C", "D"))), asList("A", "D", "C"), false},
                {collection(equalTo(items("A", "C", "D"))), asList("A", "C"), false},
                {collection(equalTo(items("A", "C", "D"))), asList("A", "C", "D", "E"), false},
                {collection(equalTo(items("A", "C", "D"))), asList("A", "C", "E", "D"), false},
                {collection(equalTo(items("A", "C", "D"))), asList("E", "A", "C", "D"), false},

                {collection(equalInAnyOrderTo(items("A", "C", "D"))), asList("A", "C", "D"), true},
                {collection(equalInAnyOrderTo(items("A", "C", "D"))), asList("A", "D", "C"), true},
                {collection(equalInAnyOrderTo(items("A", "C", "D"))), asList("A", "C"), false},
                {collection(equalInAnyOrderTo(items("A", "C", "D"))), asList("A", "C", "D", "E"), false},
                {collection(equalInAnyOrderTo(items("A", "C", "D"))), asList("A", "C", "E", "D"), false},
                {collection(equalInAnyOrderTo(items("A", "C", "D"))), asList("E", "A", "C", "D"), false},
                {collection(equalInAnyOrderTo(items())), emptyList(), true},
                {collection(equalInAnyOrderTo(items("A", "C", "D"))), emptyList(), false},

                {collection(contains(items("A", "C", "D"))), asList("A", "C", "D"), true},
                {collection(contains(items("A", "C", "D"))), asList("A", "D", "C"), false},
                {collection(contains(items("A", "C", "D"))), asList("A", "C"), false},
                {collection(contains(items("A", "C", "D"))), asList("A", "C", "D", "E"), true},
                {collection(contains(items("A", "C", "D"))), asList("A", "C", "E", "D"), true},
                {collection(contains(items("A", "C", "D"))), asList("E", "A", "C", "D"), true},
                {collection(contains(items("A", "C", "D"))), asList("A", "D", "C", "E"), false},
                {collection(contains(items("A", "C", "D"))), asList("C", "A", "E", "D"), false},
                {collection(contains(items("A", "C", "D"))), asList("E", "D", "C", "A"), false},

                {collection(containsInAnyOrder(items("A", "C", "D"))), asList("A", "C", "D"), true},
                {collection(containsInAnyOrder(items("A", "C", "D"))), asList("A", "D", "C"), true},
                {collection(containsInAnyOrder(items("A", "C", "D"))), asList("A", "C"), false},
                {collection(containsInAnyOrder(items("A", "C", "D"))), asList("A", "C", "D", "E"), true},
                {collection(containsInAnyOrder(items("A", "C", "D"))), asList("A", "C", "E", "D"), true},
                {collection(containsInAnyOrder(items("A", "C", "D"))), asList("E", "A", "C", "D"), true},
                {collection(containsInAnyOrder(items("A", "C", "D"))), asList("A", "D", "C", "E"), true},
                {collection(containsInAnyOrder(items("A", "C", "D"))), asList("C", "A", "E", "D"), true},
                {collection(containsInAnyOrder(items("A", "C", "D"))), asList("E", "D", "C", "A"), true},


                {collection(startsWith(items("A", "C", "D"))), asList("A", "C", "D"), true},
                {collection(startsWith(items("A", "C", "D"))), asList("A", "D", "C"), false},
                {collection(startsWith(items("A", "C", "D"))), asList("A", "C"), false},
                {collection(startsWith(items("A", "C", "D"))), asList("A", "C", "D", "E"), true},
                {collection(startsWith(items("A", "C", "D"))), asList("A", "C", "E", "D"), false},
                {collection(startsWith(items("A", "C", "D"))), asList("E", "A", "C", "D"), false},
                {collection(startsWith(items("A", "C", "D"))), asList("A", "D", "C", "E"), false},
                {collection(startsWith(items("A", "C", "D"))), asList("C", "A", "E", "D"), false},
                {collection(startsWith(items("A", "C", "D"))), asList("E", "D", "C", "A"), false},

                {collection(startsInAnyOrderWith(items("A", "C", "D"))), asList("A", "C", "D"), true},
                {collection(startsInAnyOrderWith(items("A", "C", "D"))), asList("A", "D", "C"), true},
                {collection(startsInAnyOrderWith(items("A", "C", "D"))), asList("A", "C"), false},
                {collection(startsInAnyOrderWith(items("A", "C", "D"))), asList("A", "C", "D", "E"), true},
                {collection(startsInAnyOrderWith(items("A", "C", "D"))), asList("A", "C", "E", "D"), false},
                {collection(startsInAnyOrderWith(items("A", "C", "D"))), asList("E", "A", "C", "D"), false},
                {collection(startsInAnyOrderWith(items("A", "C", "D"))), asList("A", "D", "C", "E"), true},
                {collection(startsInAnyOrderWith(items("A", "C", "D"))), asList("C", "A", "E", "D"), false},
                {collection(startsInAnyOrderWith(items("A", "C", "D"))), asList("E", "D", "C", "A"), false},
        };
    }

    @Test(dataProvider = "collectionCheckData")
    public void collectionCheckTest(Check<Iterable<String>> check, List<String> data, boolean expectedResult) {
        assertEquals(Check.that(data, check), expectedResult);
    }

    @DataProvider
    public static Object[][] queueCheckData() {
        return new Object[][] {
                {queue(equalTo(items("A", "C", "D"))), asQueue("A", "C", "D"), true},
                {queue(equalTo(items("A", "C", "D"))), asQueue("A", "D", "C"), false},
                {queue(equalTo(items("A", "C", "D"))), asQueue("A", "C"), false},
                {queue(equalTo(items("A", "C", "D"))), asQueue("A", "C", "D", "E"), false},
                {queue(equalTo(items("A", "C", "D"))), asQueue("A", "C", "E", "D"), false},
                {queue(equalTo(items("A", "C", "D"))), asQueue("E", "A", "C", "D"), false},

                {queue(equalInAnyOrderTo(items("A", "C", "D"))), asQueue("A", "C", "D"), true},
                {queue(equalInAnyOrderTo(items("A", "C", "D"))), asQueue("A", "D", "C"), true},
                {queue(equalInAnyOrderTo(items("A", "C", "D"))), asQueue("A", "C"), false},
                {queue(equalInAnyOrderTo(items("A", "C", "D"))), asQueue("A", "C", "D", "E"), false},
                {queue(equalInAnyOrderTo(items("A", "C", "D"))), asQueue("A", "C", "E", "D"), false},
                {queue(equalInAnyOrderTo(items("A", "C", "D"))), asQueue("E", "A", "C", "D"), false},

                {queue(contains(items("A", "C", "D"))), asQueue("A", "C", "D"), true},
                {queue(contains(items("A", "C", "D"))), asQueue("A", "D", "C"), false},
                {queue(contains(items("A", "C", "D"))), asQueue("A", "C"), false},
                {queue(contains(items("A", "C", "D"))), asQueue("A", "C", "D", "E"), true},
                {queue(contains(items("A", "C", "D"))), asQueue("A", "C", "E", "D"), true},
                {queue(contains(items("A", "C", "D"))), asQueue("E", "A", "C", "D"), true},
                {queue(contains(items("A", "C", "D"))), asQueue("A", "D", "C", "E"), false},
                {queue(contains(items("A", "C", "D"))), asQueue("C", "A", "E", "D"), false},
                {queue(contains(items("A", "C", "D"))), asQueue("E", "D", "C", "A"), false},

                {queue(containsInAnyOrder(items("A", "C", "D"))), asQueue("A", "C", "D"), true},
                {queue(containsInAnyOrder(items("A", "C", "D"))), asQueue("A", "D", "C"), true},
                {queue(containsInAnyOrder(items("A", "C", "D"))), asQueue("A", "C"), false},
                {queue(containsInAnyOrder(items("A", "C", "D"))), asQueue("A", "C", "D", "E"), true},
                {queue(containsInAnyOrder(items("A", "C", "D"))), asQueue("A", "C", "E", "D"), true},
                {queue(containsInAnyOrder(items("A", "C", "D"))), asQueue("E", "A", "C", "D"), true},
                {queue(containsInAnyOrder(items("A", "C", "D"))), asQueue("A", "D", "C", "E"), true},
                {queue(containsInAnyOrder(items("A", "C", "D"))), asQueue("C", "A", "E", "D"), true},
                {queue(containsInAnyOrder(items("A", "C", "D"))), asQueue("E", "D", "C", "A"), true},


                {queue(startsWith(items("A", "C", "D"))), asQueue("A", "C", "D"), true},
                {queue(startsWith(items("A", "C", "D"))), asQueue("A", "D", "C"), false},
                {queue(startsWith(items("A", "C", "D"))), asQueue("A", "C"), false},
                {queue(startsWith(items("A", "C", "D"))), asQueue("A", "C", "D", "E"), true},
                {queue(startsWith(items("A", "C", "D"))), asQueue("A", "C", "E", "D"), false},
                {queue(startsWith(items("A", "C", "D"))), asQueue("E", "A", "C", "D"), false},
                {queue(startsWith(items("A", "C", "D"))), asQueue("A", "D", "C", "E"), false},
                {queue(startsWith(items("A", "C", "D"))), asQueue("C", "A", "E", "D"), false},
                {queue(startsWith(items("A", "C", "D"))), asQueue("E", "D", "C", "A"), false},

                {queue(startsInAnyOrderWith(items("A", "C", "D"))), asQueue("A", "C", "D"), true},
                {queue(startsInAnyOrderWith(items("A", "C", "D"))), asQueue("A", "D", "C"), true},
                {queue(startsInAnyOrderWith(items("A", "C", "D"))), asQueue("A", "C"), false},
                {queue(startsInAnyOrderWith(items("A", "C", "D"))), asQueue("A", "C", "D", "E"), true},
                {queue(startsInAnyOrderWith(items("A", "C", "D"))), asQueue("A", "C", "E", "D"), false},
                {queue(startsInAnyOrderWith(items("A", "C", "D"))), asQueue("E", "A", "C", "D"), false},
                {queue(startsInAnyOrderWith(items("A", "C", "D"))), asQueue("A", "D", "C", "E"), true},
                {queue(startsInAnyOrderWith(items("A", "C", "D"))), asQueue("C", "A", "E", "D"), false},
                {queue(startsInAnyOrderWith(items("A", "C", "D"))), asQueue("E", "D", "C", "A"), false},
        };
    }

    @Test(dataProvider = "queueCheckData")
    public void queueCheckTest(Check<Queue<String>> check, Queue<String> data, boolean expectedResult) {
        assertEquals(Check.that(data, check), expectedResult);
    }

    private static Queue<String> asQueue(String... values) {
        return new LinkedList<>(asList(values));
    }

    @Test
    public void arrayCheck() {
        assertTrue(Check.that(new String[] {"A", "B"}, array(equalTo(items("A", "B")))));
    }

    @Test(expectedExceptions = UncheckedInterruptedException.class)
    public void testInterruptedExceptionInBlockingQueue() throws InterruptedException {
        BlockingQueue<Object> queue = mock(BlockingQueue.class);
        when(queue.poll(10, TimeUnit.MILLISECONDS)).thenThrow(new InterruptedException());
        Assert.that(queue, blockingQueue(equalTo(items("A")), Duration.ofMillis(10)));
    }

}
