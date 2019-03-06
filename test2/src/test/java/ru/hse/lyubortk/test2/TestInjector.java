package ru.hse.lyubortk.test2;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;


public class TestInjector {

    @Test
    public void injectorShouldInitializeClassWithoutDependencies()
            throws Exception {
        Object object = Injector.initialize("ru.hse.lyubortk.test2.ClassWithoutDependencies", Collections.emptyList());
        assertTrue(object instanceof ClassWithoutDependencies);
    }

    @Test
    public void injectorShouldInitializeClassWithOneClassDependency()
            throws Exception {
        Object object = Injector.initialize(
                "ru.hse.lyubortk.test2.ClassWithOneClassDependency",
                Collections.singletonList("ru.hse.lyubortk.test2.ClassWithoutDependencies")
        );
        assertTrue(object instanceof ClassWithOneClassDependency);
        ClassWithOneClassDependency instance = (ClassWithOneClassDependency) object;
        assertTrue(instance.dependency != null);
    }

    @Test
    public void injectorShouldInitializeClassWithOneInterfaceDependency()
            throws Exception {
        Object object = Injector.initialize(
                "ru.hse.lyubortk.test2.ClassWithOneInterfaceDependency",
                Collections.singletonList("ru.hse.lyubortk.test2.InterfaceImpl")
        );
        assertTrue(object instanceof ClassWithOneInterfaceDependency);
        ClassWithOneInterfaceDependency instance = (ClassWithOneInterfaceDependency) object;
        assertTrue(instance.dependency instanceof InterfaceImpl);
    }

    @Test
    public void injectorInitializesOnlyOnce() throws Exception {
        Object object = Injector.initialize(
                "ru.hse.lyubortk.test2.ClassWithTwoSameDependencies",
                Arrays.asList("ru.hse.lyubortk.test2.ClassWithOneClassDependency",
                        "ru.hse.lyubortk.test2.ClassWithoutDependencies")
        );
        assertTrue(object instanceof ClassWithTwoSameDependencies);
        ClassWithTwoSameDependencies instance = (ClassWithTwoSameDependencies) object;
        assertNotNull(instance.first);
        assertSame(instance.first, instance.second);
        assertEquals(1, ClassWithOneClassDependency.numberOfInstances);
    }

    @Test
    public void injectorImplementationNotFoundException() {
        assertThrows(ImplementationNotFoundException.class, () -> Injector.initialize(
                "ru.hse.lyubortk.test2.ClassWithTwoSameDependencies",
                Arrays.asList("ru.hse.lyubortk.test2.ClassWithOneClassDependency")
        ));
    }
}
