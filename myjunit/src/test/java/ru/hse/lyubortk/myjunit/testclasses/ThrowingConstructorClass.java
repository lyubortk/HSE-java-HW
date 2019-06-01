package ru.hse.lyubortk.myjunit.testclasses;

import ru.hse.lyubortk.myjunit.annotations.Test;

public class ThrowingConstructorClass {
    public ThrowingConstructorClass() {
        throw new RuntimeException();
    }

    @Test(expected = Exception.class)
    public void a() {
        throw new IndexOutOfBoundsException();
    }
}
