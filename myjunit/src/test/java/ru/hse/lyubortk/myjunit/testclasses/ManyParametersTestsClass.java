package ru.hse.lyubortk.myjunit.testclasses;

import ru.hse.lyubortk.myjunit.annotations.Test;

import java.io.IOException;

public class ManyParametersTestsClass {
    @Test(expected = IOException.class)
    public void aFail(String string) throws Exception {
        throw new Exception();
    }
}
