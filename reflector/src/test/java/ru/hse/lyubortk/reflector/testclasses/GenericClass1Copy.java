package ru.hse.lyubortk.reflector.testclasses;

import java.util.List;
import java.util.Map;

public class GenericClass1Copy<T, E extends List<T>> {
    T field1;
    E field2;

    GenericClass1Copy(T arg0) {
    }

    <B> GenericClass1Copy(B arg0, T arg1) {
    }

    public T genericMethod(List<? super E> list, Map<T, ? extends T> map) {
        throw new UnsupportedOperationException();
    }

    static class genericNestedClass<B> {
        B field1;
    }
}
