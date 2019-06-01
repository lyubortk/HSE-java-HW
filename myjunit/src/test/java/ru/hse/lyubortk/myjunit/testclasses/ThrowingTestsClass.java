package ru.hse.lyubortk.myjunit.testclasses;

import ru.hse.lyubortk.myjunit.annotations.Test;

import java.io.IOException;

public class ThrowingTestsClass {
    @Test(expected = IOException.class)
    public static void bSimple() throws IOException {
        throw new IOException();
    }

    @Test(expected = NullPointerException.class)
    public void aSimple() {
        throw new NullPointerException();
    }

    @Test(expected = Exception.class)
    public void cSubclass() {
        throw new IndexOutOfBoundsException();
    }
}
