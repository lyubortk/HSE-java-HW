import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CountdownLatch {
    private final Lock baseLock;
    private final Condition awaitCondition;
    private final Condition countdownCondition;
    private int count;

    CountdownLatch(int count) {
        if (count < 0) {
            throw new IllegalArgumentException();
        }
        baseLock = new ReentrantLock();
        awaitCondition = baseLock.newCondition();
        countdownCondition = baseLock.newCondition();
        this.count = count;
    }

    public void await() {
        baseLock.lock();
        while (count != 0) {
            awaitCondition.awaitUninterruptibly();
        }
        baseLock.unlock();
    }

    public void countDown() {
        baseLock.lock();
        while (count == 0) {
            countdownCondition.awaitUninterruptibly();
        }
        count--;
        if (count == 0) {
            awaitCondition.signalAll();
        }
        baseLock.unlock();
    }

    public void countUp() {
        baseLock.lock();
        count++;
        if (count == 1) {
            countdownCondition.signal();
        }
        baseLock.unlock();
    }
}
