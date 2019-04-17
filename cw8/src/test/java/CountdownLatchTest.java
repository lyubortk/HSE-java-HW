import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class CountdownLatchTest {
    @RepeatedTest(20)
    void testNoDeadlocks() {
        assertTimeout(Duration.ofSeconds(1), () -> checkDeadlocks());
    }

    void checkDeadlocks() {
        var latch = new CountdownLatch(100);
        for(int i = 0; i < 100; i++) {
            var actor = new Thread(() -> {
                latch.countDown();
                latch.await();
            });
            actor.start();
        }
    }

    @RepeatedTest(20)
    void testAwaits() {
        AtomicInteger counter = new AtomicInteger(100);
        var latch = new CountdownLatch(100);
        for(int i = 0; i < 100; i++) {
            var actor = new Thread(() -> {
                counter.decrementAndGet();
                latch.countDown();
                latch.await();
                assertEquals(0, counter.get());
            });
            actor.start();
        }
    }

    @Test
    void checkAids() {
        var latch = new CountdownLatch(0);

    }


}
