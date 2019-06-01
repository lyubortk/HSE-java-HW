package ru.hse.lyubortk.myjunit;

import org.jetbrains.annotations.NotNull;
import ru.hse.lyubortk.myjunit.exceptions.*;

import java.util.List;

public class MyJUnitConsole {
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
            result = MyJUnitCore.runClass(testingClass);
        } catch (MethodIsNotStaticException
                | MethodParametersException
                | ConstructorInaccessibleException
                | ClassIsAbstractException
                | ConstructorInvokationException
                | MethodInvokationException exception) {
            System.out.println("ERROR: " + exception.getMessage());
            return;
        }
        printResult(result);
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
}
