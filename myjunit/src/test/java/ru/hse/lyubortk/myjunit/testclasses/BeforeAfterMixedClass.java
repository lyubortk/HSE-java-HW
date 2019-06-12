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

    @AfterClass
    public static void c2() {
        stringBuilder.append('c');
    }

    @BeforeClass
    public static void a2() {
        stringBuilder.append('a');
    }

    @Before
    public static void x() {
        stringBuilder.append('X');
    }

    @Test(expected = NullPointerException.class)
    public void b1() {
        stringBuilder.append('b');
    }

    @Test
    public void b2() {
        stringBuilder.append('b');
    }

    @Test
    public void b3() {
        stringBuilder.append('b');
    }

    @After
    public void z() {
        stringBuilder.append('Z');
    }
}
