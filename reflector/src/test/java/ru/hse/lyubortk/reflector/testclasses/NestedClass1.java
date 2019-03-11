package ru.hse.lyubortk.reflector.testclasses;

public class NestedClass1 {
    Inner1 inner1;
    protected Inner2 inner2;
    private Nested1 nested1;
    public Nested2 nested2;

    NestedClass1(Inner1 inner1, Nested2 nested2) {
    }

    public class Inner1 {
    }

    public static class Nested1 {
    }

    private class Inner2 {
    }

    protected static class Nested2 {
    }
}
