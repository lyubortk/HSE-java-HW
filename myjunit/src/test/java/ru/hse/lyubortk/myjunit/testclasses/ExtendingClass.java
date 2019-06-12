package ru.hse.lyubortk.myjunit.testclasses;

import ru.hse.lyubortk.myjunit.annotations.Test;

public class ExtendingClass extends BeforeAfterMixedClass {
    @Test
    public void newName() {
        stringBuilder.append("b");
    }
}
