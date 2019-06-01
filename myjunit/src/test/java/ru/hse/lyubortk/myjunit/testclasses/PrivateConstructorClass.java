package ru.hse.lyubortk.myjunit.testclasses;

import ru.hse.lyubortk.myjunit.annotations.Test;

public class PrivateConstructorClass {
    private PrivateConstructorClass() {
    }

    @Test(expected = Exception.class)
    public void a() {
        throw new IndexOutOfBoundsException();
    }

}
