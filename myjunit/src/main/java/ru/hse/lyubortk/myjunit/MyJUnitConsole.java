package ru.hse.lyubortk.myjunit;

import org.jetbrains.annotations.NotNull;
import ru.hse.lyubortk.myjunit.exceptions.*;

import java.util.List;

/**
 * This class runs tests with {@link ru.hse.lyubortk.myjunit.MyJUnitCore} and prints results
 * to the console.
 */
public class MyJUnitConsole {
    /** Accepts the name of the testing class and tries to test it */
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
                | ConstructorInvocationException
                | MethodInvocationException exception) {
            System.out.println("ERROR: " + exception.getMessage());
            return;
        }
        printResult(result);
    }

    private static void printResult(@NotNull List<MyJUnitTestResult> results) {
        long passedNumber = getNumberWithStatus(results, MyJUnitTestResult.Status.PASSED);
        long ignoredNumber = getNumberWithStatus(results, MyJUnitTestResult.Status.IGNORED);
        long failedNumber = getNumberWithStatus(results, MyJUnitTestResult.Status.FAILED);

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
            if (result.getStatus() != MyJUnitTestResult.Status.PASSED) {
                System.out.println(result.getStatus() + ": " + result.getCause());
            } else {
                System.out.println(result.getStatus());
            }

            if (result.getStatus() != MyJUnitTestResult.Status.IGNORED) {
                System.out.println("time: " + result.getTimeMillis() + "ms");
            }
        }
    }

    private static long getNumberWithStatus(@NotNull List<MyJUnitTestResult> results,
                                            @NotNull MyJUnitTestResult.Status status) {
        return results.stream()
                .filter(result -> result.getStatus() == status)
                .count();
    }
}
