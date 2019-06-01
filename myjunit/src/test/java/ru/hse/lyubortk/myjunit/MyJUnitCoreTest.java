package ru.hse.lyubortk.myjunit;

import org.junit.jupiter.api.Test;
import ru.hse.lyubortk.myjunit.exceptions.*;
import ru.hse.lyubortk.myjunit.testclasses.*;

import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MyJUnitCoreTest {
    @Test
    void testThrowingMethods() {
        List<MyJUnitTestResult> results =
                assertDoesNotThrow(() -> MyJUnitCore.runClass(ThrowingTestsClass.class));
        assertEquals(3, results.size());
        assertAllPassed(results);
    }

    @Test
    void testNotThrowingMethods() {
        List<MyJUnitTestResult> results =
                assertDoesNotThrow(() -> MyJUnitCore.runClass(NotThrowingTestsClass.class));
        assertEquals(2, results.size());
        assertAllPassed(results);
    }

    @Test
    void testIgnoredMethods() {
        List<MyJUnitTestResult> results =
                assertDoesNotThrow(() -> MyJUnitCore.runClass(IgnoredTestsClass.class));
        assertEquals(3, results.size());
        results.sort(Comparator.comparing(MyJUnitTestResult::getMethodName));

        for (int i = 0; i < 3; i++) {
            assertEquals(MyJUnitTestResult.STATUS.IGNORED, results.get(i).getStatus());
            assertEquals("test ignored " + (i + 1), results.get(i).getCause());
        }
        assertEquals("", IgnoredTestsClass.stringBuilder.toString());
        IgnoredTestsClass.stringBuilder = new StringBuilder();
    }

    @Test
    void testTime() {
        List<MyJUnitTestResult> results =
                assertDoesNotThrow(() -> MyJUnitCore.runClass(SleepingTestsClass.class));
        assertEquals(2, results.size());

        // in case garbage collector runs or something else happens
        assertTrue(500 < results.get(0).getTimeMillis()
                   && results.get(0).getTimeMillis() < 1500);
        assertTrue(500 < results.get(1).getTimeMillis()
                   && results.get(1).getTimeMillis() < 1500);
    }

    @Test
    void testMixed() {
        List<MyJUnitTestResult> results =
                assertDoesNotThrow(() -> MyJUnitCore.runClass(MixedTestsClass.class));
        assertEquals(3, results.size());

        results.sort(Comparator.comparing(MyJUnitTestResult::getMethodName));
        assertEquals(MyJUnitTestResult.STATUS.FAILED, results.get(0).getStatus());
        assertEquals(MyJUnitTestResult.STATUS.PASSED, results.get(1).getStatus());
        assertEquals(MyJUnitTestResult.STATUS.IGNORED, results.get(2).getStatus());
    }

    @Test
    void testBeforeAndAfterClass() {
        List<MyJUnitTestResult> results =
                assertDoesNotThrow(() -> MyJUnitCore.runClass(BeforeAfterMixedClass.class));
        assertEquals(3, results.size());
        assertEquals("aaXbZXbZXbZcc", BeforeAfterMixedClass.stringBuilder.toString());
        BeforeAfterMixedClass.stringBuilder = new StringBuilder();
    }


    @Test
    void testMultipleAnnotationsClass() {
        List<MyJUnitTestResult> results =
                assertDoesNotThrow(() -> MyJUnitCore.runClass(MultipleAnnotationsClass.class));
        assertEquals(1, results.size());
        assertEquals("aaa", MultipleAnnotationsClass.stringBuilder.toString());
        MultipleAnnotationsClass.stringBuilder = new StringBuilder();
    }

    @Test
    void testExtendingClass() {
        List<MyJUnitTestResult> results =
                assertDoesNotThrow(() -> MyJUnitCore.runClass(ExtendingClass.class));
        assertEquals(4, results.size());
        assertEquals("aaXbZXbZXbZXbZcc", ExtendingClass.stringBuilder.toString());
        ExtendingClass.stringBuilder = new StringBuilder();
    }

    @Test
    void testAbstractClass() {
        assertThrows(ClassIsAbstractException.class,
                () -> MyJUnitCore.runClass(AbstractClass.class));
    }

    @Test
    void testThrowingConstructor() {
        assertThrows(ConstructorInvokationException.class,
                () -> MyJUnitCore.runClass(ThrowingConstructorClass.class));
    }

    @Test
    void testPrivateConstructor() {
        assertThrows(ConstructorInaccessibleException.class,
                () -> MyJUnitCore.runClass(PrivateConstructorClass.class));
    }

    @Test
    void testThrowingBeforeAfterMethodException() {
        assertThrows(MethodInvocationException.class,
                () -> MyJUnitCore.runClass(AfterMethodThrowsClass.class));
    }

    @Test
    void testBeforeAfterClassMethodsAreStatic() {
        assertThrows(MethodIsNotStaticException.class,
                () -> MyJUnitCore.runClass(BeforeClassMethodIsNotStaticClass.class));
    }

    @Test
    void testNumberOfMethodParameters() {
        assertThrows(MethodParametersException.class,
                () -> MyJUnitCore.runClass(ManyParametersTestsClass.class));
    }

    private void assertAllPassed(List<MyJUnitTestResult> results) {
        for (MyJUnitTestResult result : results) {
            assertEquals(MyJUnitTestResult.STATUS.PASSED, result.getStatus());
        }
    }
}