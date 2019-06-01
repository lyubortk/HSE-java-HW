package ru.hse.lyubortk.myjunit.testclasses;

import ru.hse.lyubortk.myjunit.annotations.AfterClass;
import ru.hse.lyubortk.myjunit.annotations.BeforeClass;
import ru.hse.lyubortk.myjunit.annotations.Test;

public class MultipleAnnotationsClass {
    public static StringBuilder stringBuilder = new StringBuilder();

    @AfterClass
    @BeforeClass
    @Test
    public static void a() {
        stringBuilder.append('a');
    }
}
