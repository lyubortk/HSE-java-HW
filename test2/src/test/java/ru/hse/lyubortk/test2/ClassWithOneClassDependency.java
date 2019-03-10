package ru.hse.lyubortk.test2;

public class ClassWithOneClassDependency {

    public final ClassWithoutDependencies dependency;
    public static int numberOfInstances = 0;

    public ClassWithOneClassDependency(ClassWithoutDependencies dependency) {
        this.dependency = dependency;
        numberOfInstances++;
    }
}