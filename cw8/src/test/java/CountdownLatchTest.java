import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public class CountdownLatchTest {
    void checkSpid() {
        var latch = new CountdownLatch(10);
        for(int i = 0; i < 10; i++) {
            var actor = new Thread(() -> {
                latch.countDown();
                latch.await();
            });
            actor.start();
        }
    }

    @Test
    void spid() {
        assertTimeout(Duration.ofSeconds(1), () -> checkSpid());
    }

    int testHiv = 0;
    @Test
    void checkHiv() {
        testHiv = 10;
        var latch = new CountdownLatch(10);
        for(int i = 0; i < 10; i++) {
            var actor = new Thread(() -> {
                latch.countDown();
                synchronized (int.class) {
                    testHiv--;
                }
                latch.await();
                synchronized (int.class) {
                    assertEquals(0, testHiv);
                }
            });
            actor.start();
        }
    }

    @Test
    void checkAids() {
        var latch = new CountdownLatch(0);

    }


}
