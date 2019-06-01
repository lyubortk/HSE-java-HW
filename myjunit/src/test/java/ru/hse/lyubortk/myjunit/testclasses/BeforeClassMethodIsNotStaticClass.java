package ru.hse.lyubortk.myjunit.testclasses;

import ru.hse.lyubortk.myjunit.annotations.After;
import ru.hse.lyubortk.myjunit.annotations.BeforeClass;

public class BeforeClassMethodIsNotStaticClass {
    @BeforeClass
    public void a() {
    }

}
