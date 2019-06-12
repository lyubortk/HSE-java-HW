package ru.hse.lyubortk.myjunit.testclasses;

import ru.hse.lyubortk.myjunit.annotations.Test;

import java.io.IOException;

public class MixedTestsClass {
    @Test(expected = IOException.class)
    public void aFail() throws Exception {
        throw new Exception();
    }

    @Test()
    public void bPass() throws InterruptedException {
        Thread.sleep(10);
    }

    @Test(ignore = "test ignored")
    public void cIgnored() throws InterruptedException {
        Thread.sleep(1000);
    }
}
