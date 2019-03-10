package ru.hse.lyubortk.test2;

public class ClassWithTwoSameDependencies {

    public final ClassWithOneClassDependency first;
    public final ClassWithOneClassDependency second;

    public ClassWithTwoSameDependencies(ClassWithOneClassDependency a,
                                        ClassWithOneClassDependency b) {
        first = a;
        second = b;
    }

}
