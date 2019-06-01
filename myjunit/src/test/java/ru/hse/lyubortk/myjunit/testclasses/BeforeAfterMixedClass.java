package ru.hse.lyubortk.myjunit.testclasses;

import ru.hse.lyubortk.myjunit.annotations.*;

public class BeforeAfterMixedClass {
    public static StringBuilder stringBuilder = new StringBuilder();

    @BeforeClass
    public static void a1() {
        stringBuilder.append('a');
    }

    @AfterClass
    public static void c1() {
        stringBuilder.append('c');
    }

    @Test(expected = NullPointerException.class)
    public void b1() {
        stringBuilder.append('b');
    }

    @AfterClass
    public static void c2() {
        stringBuilder.append('c');
    }

    @Test
    public void b2() {
        stringBuilder.append('b');
    }

    @BeforeClass
    public static void a2() {
        stringBuilder.append('a');
    }

    @Test
    public void b3() {
        stringBuilder.append('b');
    }

    @Before
    public static void x() {
        stringBuilder.append('X');
    }

    @After
    public void z() {
        stringBuilder.append('Z');
    }
}
