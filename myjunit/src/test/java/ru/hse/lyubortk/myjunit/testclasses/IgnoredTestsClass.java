package ru.hse.lyubortk.myjunit.testclasses;

import ru.hse.lyubortk.myjunit.annotations.Test;

import java.io.IOException;

public class IgnoredTestsClass {
    public static StringBuilder stringBuilder = new StringBuilder();

    @Test(expected = IOException.class, ignore = "test ignored 2")
    public static void b() throws IOException {
        stringBuilder.append("a");
    }

    @Test(ignore = "test ignored 1")
    public void a() {
        stringBuilder.append("a");
        throw new NullPointerException();
    }

    @Test(ignore = "test ignored 3")
    public void c() {
        stringBuilder.append("a");
    }
}
