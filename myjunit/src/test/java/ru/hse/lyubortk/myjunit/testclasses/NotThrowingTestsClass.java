package ru.hse.lyubortk.myjunit.testclasses;

import ru.hse.lyubortk.myjunit.annotations.Test;

import java.io.IOException;

public class NotThrowingTestsClass {
    int a;

    @Test()
    public static void a() {
    }

    @Test(expected = Test.DoesNotThrow.class)
    public void b() {
        // make sure compiler generates implicit arguments for method
        a = 3;
    }
}
