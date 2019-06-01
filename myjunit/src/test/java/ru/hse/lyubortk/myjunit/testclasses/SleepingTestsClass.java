package ru.hse.lyubortk.myjunit.testclasses;

import ru.hse.lyubortk.myjunit.annotations.Test;

import java.io.IOException;

public class SleepingTestsClass {
    @Test()
    public void a() throws InterruptedException {
        Thread.sleep(1000);
    }

    @Test()
    public void b() throws InterruptedException {
        Thread.sleep(1000);
    }
}
