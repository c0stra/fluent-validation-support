package fluent.validation.tests;

import fluent.validation.Assert;
import fluent.validation.Check;
import fluent.validation.CheckInterruptedException;
import fluent.validation.CollectionChecks;
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.*;

public class CollectionChecksTest {

    @DataProvider
    public static Object[][] collectionCheckData() {
        return new Object[][] {
                {collectionEqualTo(items("A", "C", "D")), asList("A", "C", "D"), true},
                {collectionEqualTo(items("A", "C", "D")), asList("A", "D", "C"), false},
                {collectionEqualTo(items("A", "C", "D")), asList("A", "C"), false},
                {collectionEqualTo(items("A", "C", "D")), asList("A", "C", "D", "E"), false},
                {collectionEqualTo(items("A", "C", "D")), asList("A", "C", "E", "D"), false},
                {collectionEqualTo(items("A", "C", "D")), asList("E", "A", "C", "D"), false},

                {collectionEqualInAnyOrderTo(items("A", "C", "D")), asList("A", "C", "D"), true},
                {collectionEqualInAnyOrderTo(items("A", "C", "D")), asList("A", "D", "C"), true},
                {collectionEqualInAnyOrderTo(items("A", "C", "D")), asList("A", "C"), false},
                {collectionEqualInAnyOrderTo(items("A", "C", "D")), asList("A", "C", "D", "E"), false},
                {collectionEqualInAnyOrderTo(items("A", "C", "D")), asList("A", "C", "E", "D"), false},
                {collectionEqualInAnyOrderTo(items("A", "C", "D")), asList("E", "A", "C", "D"), false},

                {collectionContains(items("A", "C", "D")), asList("A", "C", "D"), true},
                {collectionContains(items("A", "C", "D")), asList("A", "D", "C"), false},
                {collectionContains(items("A", "C", "D")), asList("A", "C"), false},
                {collectionContains(items("A", "C", "D")), asList("A", "C", "D", "E"), true},
                {collectionContains(items("A", "C", "D")), asList("A", "C", "E", "D"), true},
                {collectionContains(items("A", "C", "D")), asList("E", "A", "C", "D"), true},
                {collectionContains(items("A", "C", "D")), asList("A", "D", "C", "E"), false},
                {collectionContains(items("A", "C", "D")), asList("C", "A", "E", "D"), false},
                {collectionContains(items("A", "C", "D")), asList("E", "D", "C", "A"), false},

                {collectionContainsInAnyOrder(items("A", "C", "D")), asList("A", "C", "D"), true},
                {collectionContainsInAnyOrder(items("A", "C", "D")), asList("A", "D", "C"), true},
                {collectionContainsInAnyOrder(items("A", "C", "D")), asList("A", "C"), false},
                {collectionContainsInAnyOrder(items("A", "C", "D")), asList("A", "C", "D", "E"), true},
                {collectionContainsInAnyOrder(items("A", "C", "D")), asList("A", "C", "E", "D"), true},
                {collectionContainsInAnyOrder(items("A", "C", "D")), asList("E", "A", "C", "D"), true},
                {collectionContainsInAnyOrder(items("A", "C", "D")), asList("A", "D", "C", "E"), true},
                {collectionContainsInAnyOrder(items("A", "C", "D")), asList("C", "A", "E", "D"), true},
                {collectionContainsInAnyOrder(items("A", "C", "D")), asList("E", "D", "C", "A"), true},


                {collectionStartsWith(items("A", "C", "D")), asList("A", "C", "D"), true},
                {collectionStartsWith(items("A", "C", "D")), asList("A", "D", "C"), false},
                {collectionStartsWith(items("A", "C", "D")), asList("A", "C"), false},
                {collectionStartsWith(items("A", "C", "D")), asList("A", "C", "D", "E"), true},
                {collectionStartsWith(items("A", "C", "D")), asList("A", "C", "E", "D"), false},
                {collectionStartsWith(items("A", "C", "D")), asList("E", "A", "C", "D"), false},
                {collectionStartsWith(items("A", "C", "D")), asList("A", "D", "C", "E"), false},
                {collectionStartsWith(items("A", "C", "D")), asList("C", "A", "E", "D"), false},
                {collectionStartsWith(items("A", "C", "D")), asList("E", "D", "C", "A"), false},

                {collectionStartsInAnyOrderWith(items("A", "C", "D")), asList("A", "C", "D"), true},
                {collectionStartsInAnyOrderWith(items("A", "C", "D")), asList("A", "D", "C"), true},
                {collectionStartsInAnyOrderWith(items("A", "C", "D")), asList("A", "C"), false},
                {collectionStartsInAnyOrderWith(items("A", "C", "D")), asList("A", "C", "D", "E"), true},
                {collectionStartsInAnyOrderWith(items("A", "C", "D")), asList("A", "C", "E", "D"), false},
                {collectionStartsInAnyOrderWith(items("A", "C", "D")), asList("E", "A", "C", "D"), false},
                {collectionStartsInAnyOrderWith(items("A", "C", "D")), asList("A", "D", "C", "E"), true},
                {collectionStartsInAnyOrderWith(items("A", "C", "D")), asList("C", "A", "E", "D"), false},
                {collectionStartsInAnyOrderWith(items("A", "C", "D")), asList("E", "D", "C", "A"), false},
        };
    }

    @Test(dataProvider = "collectionCheckData")
    public void collectionCheckTest(Check<Iterable<String>> check, List<String> data, boolean expectedResult) {
        assertEquals(Check.that(data, check), expectedResult);
    }

    @DataProvider
    public static Object[][] queueCheckData() {
        return new Object[][] {
                {queueEqualTo(items("A", "C", "D")), asQueue("A", "C", "D"), true},
                {queueEqualTo(items("A", "C", "D")), asQueue("A", "D", "C"), false},
                {queueEqualTo(items("A", "C", "D")), asQueue("A", "C"), false},
                {queueEqualTo(items("A", "C", "D")), asQueue("A", "C", "D", "E"), false},
                {queueEqualTo(items("A", "C", "D")), asQueue("A", "C", "E", "D"), false},
                {queueEqualTo(items("A", "C", "D")), asQueue("E", "A", "C", "D"), false},

                {queueEqualInAnyOrderTo(items("A", "C", "D")), asQueue("A", "C", "D"), true},
                {queueEqualInAnyOrderTo(items("A", "C", "D")), asQueue("A", "D", "C"), true},
                {queueEqualInAnyOrderTo(items("A", "C", "D")), asQueue("A", "C"), false},
                {queueEqualInAnyOrderTo(items("A", "C", "D")), asQueue("A", "C", "D", "E"), false},
                {queueEqualInAnyOrderTo(items("A", "C", "D")), asQueue("A", "C", "E", "D"), false},
                {queueEqualInAnyOrderTo(items("A", "C", "D")), asQueue("E", "A", "C", "D"), false},

                {queueContains(items("A", "C", "D")), asQueue("A", "C", "D"), true},
                {queueContains(items("A", "C", "D")), asQueue("A", "D", "C"), false},
                {queueContains(items("A", "C", "D")), asQueue("A", "C"), false},
                {queueContains(items("A", "C", "D")), asQueue("A", "C", "D", "E"), true},
                {queueContains(items("A", "C", "D")), asQueue("A", "C", "E", "D"), true},
                {queueContains(items("A", "C", "D")), asQueue("E", "A", "C", "D"), true},
                {queueContains(items("A", "C", "D")), asQueue("A", "D", "C", "E"), false},
                {queueContains(items("A", "C", "D")), asQueue("C", "A", "E", "D"), false},
                {queueContains(items("A", "C", "D")), asQueue("E", "D", "C", "A"), false},

                {queueContainsInAnyOrder(items("A", "C", "D")), asQueue("A", "C", "D"), true},
                {queueContainsInAnyOrder(items("A", "C", "D")), asQueue("A", "D", "C"), true},
                {queueContainsInAnyOrder(items("A", "C", "D")), asQueue("A", "C"), false},
                {queueContainsInAnyOrder(items("A", "C", "D")), asQueue("A", "C", "D", "E"), true},
                {queueContainsInAnyOrder(items("A", "C", "D")), asQueue("A", "C", "E", "D"), true},
                {queueContainsInAnyOrder(items("A", "C", "D")), asQueue("E", "A", "C", "D"), true},
                {queueContainsInAnyOrder(items("A", "C", "D")), asQueue("A", "D", "C", "E"), true},
                {queueContainsInAnyOrder(items("A", "C", "D")), asQueue("C", "A", "E", "D"), true},
                {queueContainsInAnyOrder(items("A", "C", "D")), asQueue("E", "D", "C", "A"), true},


                {queueStartsWith(items("A", "C", "D")), asQueue("A", "C", "D"), true},
                {queueStartsWith(items("A", "C", "D")), asQueue("A", "D", "C"), false},
                {queueStartsWith(items("A", "C", "D")), asQueue("A", "C"), false},
                {queueStartsWith(items("A", "C", "D")), asQueue("A", "C", "D", "E"), true},
                {queueStartsWith(items("A", "C", "D")), asQueue("A", "C", "E", "D"), false},
                {queueStartsWith(items("A", "C", "D")), asQueue("E", "A", "C", "D"), false},
                {queueStartsWith(items("A", "C", "D")), asQueue("A", "D", "C", "E"), false},
                {queueStartsWith(items("A", "C", "D")), asQueue("C", "A", "E", "D"), false},
                {queueStartsWith(items("A", "C", "D")), asQueue("E", "D", "C", "A"), false},

                {queueStartsInAnyOrderWith(items("A", "C", "D")), asQueue("A", "C", "D"), true},
                {queueStartsInAnyOrderWith(items("A", "C", "D")), asQueue("A", "D", "C"), true},
                {queueStartsInAnyOrderWith(items("A", "C", "D")), asQueue("A", "C"), false},
                {queueStartsInAnyOrderWith(items("A", "C", "D")), asQueue("A", "C", "D", "E"), true},
                {queueStartsInAnyOrderWith(items("A", "C", "D")), asQueue("A", "C", "E", "D"), false},
                {queueStartsInAnyOrderWith(items("A", "C", "D")), asQueue("E", "A", "C", "D"), false},
                {queueStartsInAnyOrderWith(items("A", "C", "D")), asQueue("A", "D", "C", "E"), true},
                {queueStartsInAnyOrderWith(items("A", "C", "D")), asQueue("C", "A", "E", "D"), false},
                {queueStartsInAnyOrderWith(items("A", "C", "D")), asQueue("E", "D", "C", "A"), false},
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
        assertTrue(Check.that(new String[] {"A", "B"}, arrayEqualTo(items("A", "B"))));
    }

    @Test(expectedExceptions = CheckInterruptedException.class)
    public void testInterruptedExceptionInBlockingQueue() throws InterruptedException {
        BlockingQueue<Object> queue = mock(BlockingQueue.class);
        when(queue.poll(10, TimeUnit.MILLISECONDS)).thenThrow(new InterruptedException());
        Assert.that(queue, queueEqualTo(items("A"), Duration.ofMillis(10)));
    }

}
