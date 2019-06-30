package fluent.validation.tests;

import fluent.validation.Assert;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static fluent.validation.CollectionChecks.*;

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
        Assert.that(queue, queueEqualInAnyOrderTo(items("A", "C", "B"), Duration.ofSeconds(1)));
    }


    @Test
    public void synchronousTest() {
        BlockingQueue<String> queue = new LinkedBlockingQueue<>();
        queue.add("A");
        queue.add("B");
        queue.add("C");
        Assert.that(queue, queueEqualTo(items("A", "B", "C"), Duration.ofSeconds(1)));
    }

}
