package ru.hse.temp;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MaybeTest {

    @Test
    void justTest() {
        var just = Maybe.just("123");
        assertEquals("123", just.get());
        assertTrue(just.isPresent());
    }

    @Test
    void nothingTest() {
        var nothing = Maybe.nothing();
        assertFalse(nothing.isPresent());
    }

    static int len(String str) {
        return str.length();
    }

    @Test
    void mapTest() {
        var kek = Maybe.just("1234").map(MaybeTest::len);
        assertEquals(Integer.valueOf(4), kek.get());

        Maybe<String> mda = Maybe.nothing();
        Maybe<Integer> zez = mda.map(MaybeTest::len);
        assertFalse(zez.isPresent());
    }
}