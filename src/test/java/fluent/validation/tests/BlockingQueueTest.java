package fluent.validation.tests;

import fluent.validation.Assert;
import fluent.validation.UncheckedInterruptedException;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static fluent.validation.CollectionChecks.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BlockingQueueTest {

    @Test
    public void asynchronousTest() {
        BlockingQueue<String> queue = new LinkedBlockingQueue<>();
        new Thread(() -> {
            try {
                Thread.sleep(200);
                queue.add("A");
                Thread.sleep(200);
                queue.add("B");
                Thread.sleep(200);
                queue.add("C");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
        Assert.that(queue, blockingQueue(equalInAnyOrderTo(items("A", "C", "B")), Duration.ofSeconds(1)));
    }


    @Test
    public void synchronousTest() {
        BlockingQueue<String> queue = new LinkedBlockingQueue<>();
        queue.add("A");
        queue.add("B");
        queue.add("C");
        Assert.that(queue, blockingQueue(equalTo(items("A", "B", "C")), Duration.ofSeconds(1)));
    }


    @Test(expectedExceptions = UncheckedInterruptedException.class)
    public void testInterruptedExceptionInBlockingQueue() throws InterruptedException {
        BlockingQueue<Object> queue = mock(BlockingQueue.class);
        when(queue.poll(10, TimeUnit.MILLISECONDS)).thenThrow(new InterruptedException());
        Assert.that(queue, blockingQueue(equalTo(items("A")), Duration.ofMillis(10)));
    }

}
