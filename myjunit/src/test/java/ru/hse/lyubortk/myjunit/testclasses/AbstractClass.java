package ru.hse.lyubortk.myjunit.testclasses;

import ru.hse.lyubortk.myjunit.annotations.Test;

import java.io.IOException;

public abstract class AbstractClass {
    @Test(expected = Exception.class)
    public void cSubclass() {
        throw new IndexOutOfBoundsException();
    }

}
