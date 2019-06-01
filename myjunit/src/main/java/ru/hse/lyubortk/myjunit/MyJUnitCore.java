package ru.hse.lyubortk.myjunit;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.hse.lyubortk.myjunit.annotations.*;
import ru.hse.lyubortk.myjunit.exceptions.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MyJUnitCore {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Wrong number of arguments.\n"
                               + "You should pass the name of the testing class");
            return;
        }

        Class<?> testingClass;
        try {
            testingClass = Class.forName(args[0]);
        } catch (ClassNotFoundException e) {
            System.out.println("Class not found");
            return;
        }

        List<MyJUnitTestResult> result;
        try {
            result = runClass(testingClass);
        } catch (StaticMethodException
                | MethodParametersException
                | InaccessibleConstructorException
                | AbstractClassException
                | ConstructorInvokationException
                | MethodInvokationException exception) {
            System.out.println("ERROR: " + exception.getMessage());
            return;
        }
        printResult(result);
    }

    public static List<MyJUnitTestResult> runClass(@NotNull Class<?> testingClass)
            throws StaticMethodException,
                   MethodParametersException,
                   InaccessibleConstructorException,
                   AbstractClassException,
                   ConstructorInvokationException,
                   MethodInvokationException {

        // this blank line improves readability a lot and is not discouraged by Google Style Guide
        Method[] classMethods = testingClass.getMethods();

        List<Method> beforeClassMethods = getAnnotatedMethods(classMethods, BeforeClass.class);
        List<Method> afterClassMethods = getAnnotatedMethods(classMethods, AfterClass.class);
        List<Method> beforeMethods = getAnnotatedMethods(classMethods, Before.class);
        List<Method> afterMethods = getAnnotatedMethods(classMethods, After.class);
        List<Method> testMethods = getAnnotatedMethods(classMethods, Test.class);

        if (checkNonStatic(beforeClassMethods) || checkNonStatic(afterClassMethods)) {
            throw new StaticMethodException(
                    "methods annotated with BeforeClass and AfterClass have to be static");
        }

        if (checkHasNoParameters(beforeClassMethods)
            || checkHasNoParameters(afterClassMethods)
            || checkHasNoParameters(beforeMethods)
            || checkHasNoParameters(afterMethods)
            || checkHasNoParameters(testMethods)) {
            throw new MethodParametersException(
                    "all annotated methods have to accept no arguments");
        }

        Constructor<?> testingClassConstructor = null;
        if (checkNonStatic(testMethods)) {
            try {
                testingClassConstructor = testingClass.getConstructor();
            } catch (NoSuchMethodException exception) {
                String message = "some test methods are not static therefore "
                               + "test class has to provide public constructor with no arguments";
                throw new InaccessibleConstructorException(message, exception);
            }
        }

        runNonTestMethods(null, beforeClassMethods, "BeforeClass");

        var methodsResults = new ArrayList<MyJUnitTestResult>();

        for (var method : testMethods) {
            Test annotation = method.getAnnotation(Test.class);
            if (!annotation.ignore().equals("")) {
                methodsResults.add(MyJUnitTestResult.createIgnored(method.getName(),
                        annotation.ignore()));
                continue;
            }

            Object instance = null;
            if (testingClassConstructor != null) {
                try {
                    instance = testingClassConstructor.newInstance();
                } catch (IllegalAccessException ignored) {
                    // constructor is public
                } catch (InstantiationException exception) {
                    throw new AbstractClassException("testing class is abstract", exception);
                } catch (InvocationTargetException exception) {
                    String message = "constructor has thrown "
                                     + exception.getTargetException().getClass().getName();
                    throw new ConstructorInvokationException(message, exception);
                }
            }

            runNonTestMethods(instance, beforeMethods, "Before");
            methodsResults.add(runTestMethod(method, instance));
            runNonTestMethods(instance, afterMethods, "After");
        }

        runNonTestMethods(null, afterClassMethods, "AfterClass");
        return methodsResults;
    }

    private static void printResult(@NotNull List<MyJUnitTestResult> results) {
        long passedNumber = getNumberWithStatus(results, MyJUnitTestResult.METHOD_STATUS.PASSED);
        long ignoredNumber = getNumberWithStatus(results, MyJUnitTestResult.METHOD_STATUS.IGNORED);
        long failedNumber = getNumberWithStatus(results, MyJUnitTestResult.METHOD_STATUS.FAILED);

        if (failedNumber == 0) {
            System.out.print("SUCCESS: ");
        } else {
            System.out.print("FAIL: ");
        }

        System.out.println("passed " + passedNumber
                           + " tests out of " + (passedNumber + ignoredNumber)
                           + ". " + ignoredNumber + " tests were ignored.");

        for (var result : results) {
            System.out.println();
            System.out.println(result.getMethodName());
            if (result.getStatus() != MyJUnitTestResult.METHOD_STATUS.PASSED) {
                System.out.println(result.getStatus() + ": " + result.getCause());
            } else {
                System.out.println(result.getStatus());
            }

            if (result.getStatus() != MyJUnitTestResult.METHOD_STATUS.IGNORED) {
                System.out.println("time: " + result.getTimeMillis() + "ms");
            }
        }
    }

    private static long getNumberWithStatus(@NotNull List<MyJUnitTestResult> results,
                                            @NotNull MyJUnitTestResult.METHOD_STATUS status) {
        return results.stream()
                .filter(result -> result.getStatus() == status)
                .count();
    }

    private static List<Method> getAnnotatedMethods(
            @NotNull Method[] methods, @NotNull Class<? extends Annotation> annotation) {
        return Arrays.stream(methods)
                .filter(method -> method.isAnnotationPresent(annotation))
                .collect(Collectors.toList());
    }

    private static boolean checkNonStatic(@NotNull List<Method> methods) {
        return !methods.stream().allMatch(method -> Modifier.isStatic(method.getModifiers()));
    }

    private static boolean checkHasNoParameters(@NotNull List<Method> methods) {
        return !methods.stream().allMatch(method -> method.getParameters().length == 0);
    }

    private static void runNonTestMethods(@Nullable Object instance,
                                          @NotNull List<Method> methods,
                                          @NotNull String type)
            throws MethodInvokationException {

        for (var method : methods) {
            try {
                method.invoke(instance);
            } catch (IllegalAccessException ignored) {
                //all methods are public
            } catch (InvocationTargetException exception) {
                String message = type + " method " + method.getName() + " has thrown "
                               + exception.getTargetException().getClass().getName();
                throw new MethodInvokationException(message, exception);
            }
        }
    }

    private static MyJUnitTestResult runTestMethod(@NotNull Method method,
                                                   @Nullable Object instance) {
        Test annotation = method.getAnnotation(Test.class);
        String expectedMessage;
        if (annotation.expected() == Test.DoesNotThrow.class) {
            expectedMessage = "Was not expected to throw;";
        } else {
            expectedMessage = "Expected: " + annotation.expected().getName() + ";";
        }

        boolean hasThrown = false;
        long startTimeMillis = System.currentTimeMillis();
        MyJUnitTestResult testResult = null;

        try {
            method.invoke(instance);
        } catch (IllegalAccessException ignored) {
            //all methods are public
        } catch (InvocationTargetException exception) {
            long timeMillis = System.currentTimeMillis() - startTimeMillis;
            hasThrown = true;
            if (exception.getTargetException().getClass() == annotation.expected()) {
                testResult = MyJUnitTestResult.createPassed(method.getName(), timeMillis);
            } else {
                String message = expectedMessage + " thrown: "
                                 + exception.getTargetException().getClass().getName();
                testResult = MyJUnitTestResult.createFailed(method.getName(), message, timeMillis);
            }
        }

        if (!hasThrown) {
            long timeMillis = System.currentTimeMillis() - startTimeMillis;
            if (annotation.expected() == Test.DoesNotThrow.class) {
                testResult = MyJUnitTestResult.createPassed(method.getName(), timeMillis);
            } else {
                testResult = MyJUnitTestResult.createFailed(method.getName(),
                        expectedMessage + " but method did not throw", timeMillis);
            }
        }
        return testResult;
    }
}
