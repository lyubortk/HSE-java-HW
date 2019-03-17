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
        <T> Inner2(T a, T b) {
        }

        Inner2(int a, int b, double c) {
        }

        Inner2() {
        }
    }

    protected static class Nested2 implements Interface1 {
        <T> Nested2(T a, T b) {
        }

        Nested2(int a, int b, double c) {
        }

        Nested2() {
        }
    }

    private interface Interface1{
    }
}
