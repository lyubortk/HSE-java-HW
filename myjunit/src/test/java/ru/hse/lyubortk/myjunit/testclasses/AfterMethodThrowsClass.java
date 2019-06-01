package ru.hse.lyubortk.myjunit.testclasses;

import ru.hse.lyubortk.myjunit.annotations.After;
import ru.hse.lyubortk.myjunit.annotations.Test;

public class AfterMethodThrowsClass {
    @After
    public void a() {
        throw new IndexOutOfBoundsException();
    }

    @Test
    public void b() {

    }

}
