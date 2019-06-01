package ru.hse.lyubortk.myjunit;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.hse.lyubortk.myjunit.annotations.*;

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

        MyJUnitResult result = runClass(testingClass);
        printResult(result);
    }

    public static MyJUnitResult runClass(@NotNull Class<?> testingClass) {
        Method[] classMethods = testingClass.getMethods();

        List<Method> beforeClassMethods = getAnnotatedMethods(classMethods, BeforeClass.class);
        List<Method> afterClassMethods = getAnnotatedMethods(classMethods, AfterClass.class);
        List<Method> beforeMethods = getAnnotatedMethods(classMethods, Before.class);
        List<Method> afterMethods = getAnnotatedMethods(classMethods, After.class);
        List<Method> testMethods = getAnnotatedMethods(classMethods, Test.class);

        if (!checkStatic(beforeClassMethods) || !checkStatic(afterClassMethods)) {
            return MyJUnitResult.createError(testingClass.getName(),
                    "methods annotated with BeforeClass and AfterClass have to be static");
        }

        if (!checkNumberOfParameters(beforeClassMethods)
            || !checkNumberOfParameters(afterClassMethods)
            || !checkNumberOfParameters(beforeMethods)
            || !checkNumberOfParameters(afterMethods)
            || !checkNumberOfParameters(testMethods)) {
            return MyJUnitResult.createError(testingClass.getName(),
                    "all annotated methods have to accept no arguments");
        }

        Constructor<?> testingClassConstructor = null;
        if (!checkStatic(testMethods)) {
            try {
                testingClassConstructor = testingClass.getConstructor();
            } catch (NoSuchMethodException exception) {
                return MyJUnitResult.createError(testingClass.getName(),
                        "some test methods are not static therefore "
                        + "test class has to provide public constructor with no arguments");
            }
        }

        MyJUnitResult result;
        if ((result = runNonTestMethods(testingClass.getName(), null,
                beforeClassMethods, "BeforeClass")) != null) {
            return result;
        }

        int failedMethodsNumber = 0;
        var methodsResults = new ArrayList<MyJUnitResult.MethodResult>();

        for (var method: testMethods) {
            Test annotation = method.getAnnotation(Test.class);
            if (!annotation.ignore().equals("")) {
                methodsResults.add(MyJUnitResult.MethodResult.createIgnored(method.getName(),
                        annotation.ignore()));
                continue;
            }

            Object instance = null;
            if (testingClassConstructor != null) {
                try {
                    instance = testingClassConstructor.newInstance();
                } catch (IllegalAccessException ignored) {
                    //constructor is public
                } catch (InstantiationException exception) {
                    return MyJUnitResult.createError(testingClass.getName(),
                            "testing class is abstract");
                } catch (InvocationTargetException exception) {
                    return MyJUnitResult.createError(testingClass.getName(),
                            "constructor has thrown "
                            + exception.getTargetException().getClass().getName());
                }
            }

            if ((result = runNonTestMethods(testingClass.getName(), instance,
                    beforeMethods, "Before")) != null) {
                return result;
            }

            MyJUnitResult.MethodResult methodResult = runTestMethod(method, instance);
            if (methodResult.getStatus() == MyJUnitResult.MethodResult.METHOD_STATUS.FAILED) {
                failedMethodsNumber++;
            }
            methodsResults.add(methodResult);

            if ((result = runNonTestMethods(testingClass.getName(), instance,
                    afterMethods, "After")) != null) {
                return result;
            }
        }

        if ((result = runNonTestMethods(testingClass.getName(), null,
                afterClassMethods, "AfterClass")) != null) {
            return result;
        }

        if (failedMethodsNumber != 0) {
            return MyJUnitResult.createFailed(testingClass.getName(),
                    testMethods.size(), failedMethodsNumber, methodsResults);
        }
        return MyJUnitResult.createSuccess(testingClass.getName(),
                testMethods.size(), methodsResults);
    }

    private static void printResult(@NotNull MyJUnitResult result) {
        if (result.getStatus() == MyJUnitResult.TESTING_STATUS.ERROR) {
            System.out.println(result.getStatus() + ": " + result.getCause());
            return;
        }

        System.out.println(result.getStatus() + ": " + result.getSuccessMethodsNumber()
                + " methods passed out of " + result.getFailedMethodsNumber());

        for (var methodResult: result.getMethodsResults()) {
            System.out.println();
            System.out.println(methodResult.getMethodName());
            if (methodResult.getStatus() != MyJUnitResult.MethodResult.METHOD_STATUS.SUCCESS) {
                System.out.println(methodResult.getStatus() + ": " + methodResult.getCause());
            } else {
                System.out.println(methodResult.getStatus());
            }

            if (methodResult.getStatus() != MyJUnitResult.MethodResult.METHOD_STATUS.IGNORED) {
                System.out.println("time: " + methodResult.getTimeMillis() + "ms");
            }
        }
    }

    private static List<Method> getAnnotatedMethods(
            @NotNull Method[] methods, @NotNull Class<? extends Annotation> annotation) {
        return Arrays.stream(methods)
                .filter(method -> method.isAnnotationPresent(annotation))
                .collect(Collectors.toList());
    }

    private static boolean checkStatic(@NotNull List<Method> methods) {
        return methods.stream().allMatch(method -> Modifier.isStatic(method.getModifiers()));
    }

    private static boolean checkNumberOfParameters(@NotNull List<Method> methods) {
        return methods.stream().allMatch(method -> method.getParameters().length == 0);
    }

    private static MyJUnitResult runNonTestMethods(@NotNull String className,
                                                   @Nullable Object instance,
                                                   @NotNull List<Method> methods,
                                                   @NotNull String type) {
        for (var method: methods) {
            try {
                method.invoke(instance);
            } catch (IllegalAccessException ignored) {
                //all methods are public
            } catch (InvocationTargetException exception) {
                String cause = type + " method " + method.getName() + " has thrown "
                               + exception.getTargetException().getClass().getName();
                return MyJUnitResult.createError(className, cause);
            }
        }
        return null;
    }

    private static MyJUnitResult.MethodResult runTestMethod(@NotNull Method method,
                                                            @Nullable Object instance) {
        Test annotation = method.getAnnotation(Test.class);
        String expectedMessage;
        if (annotation.expected() == Test.DoesNotThrow.class) {
            expectedMessage = "Was not expected to throw;";
        } else {
            expectedMessage = "Expected: " + annotation.expected().getName();
        }

        boolean hasThrown = false;
        long startTimeMillis = System.currentTimeMillis();
        MyJUnitResult.MethodResult methodResult = null;

        try {
            method.invoke(instance);
        } catch (IllegalAccessException ignored) {
            //all methods are public
        } catch (InvocationTargetException exception) {
            long timeMillis = startTimeMillis - System.currentTimeMillis();
            hasThrown = true;
            if (exception.getTargetException().getClass() == annotation.expected()) {
                methodResult = MyJUnitResult.MethodResult.createSuccess(method.getName(),
                        timeMillis);
            } else {
                methodResult = MyJUnitResult.MethodResult.createFailed(
                        method.getName(),
                        expectedMessage + " thrown "
                        + exception.getTargetException().getClass().getName(),
                        timeMillis
                );
            }
        }

        if (!hasThrown) {
            long timeMillis = startTimeMillis - System.currentTimeMillis();
            if (annotation.expected() == Test.DoesNotThrow.class) {
                methodResult = MyJUnitResult.MethodResult.createSuccess(method.getName(),
                        timeMillis);
            } else {
                methodResult = MyJUnitResult.MethodResult.createFailed(method.getName(),
                        expectedMessage + " but method did not throw", timeMillis);
            }
        }
        return methodResult;
    }
}
