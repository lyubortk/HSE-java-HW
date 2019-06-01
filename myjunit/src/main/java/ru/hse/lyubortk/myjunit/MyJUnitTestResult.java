package ru.hse.lyubortk.myjunit;

/**
 * Instances of this class are generated for every @Test method after testing
 */
public class MyJUnitTestResult {
    private String methodName;
    private STATUS status;
    private String cause;
    private long timeMillis;

    /**
     * Represents one test's results
     */
    public enum STATUS {
        PASSED,
        FAILED,
        IGNORED
    }

    private MyJUnitTestResult() {
    }

    static MyJUnitTestResult createPassed(String methodName, long timeMillis) {
        var result = new MyJUnitTestResult();
        result.methodName = methodName;
        result.status = STATUS.PASSED;
        result.timeMillis = timeMillis;
        return result;
    }

    static MyJUnitTestResult createFailed(String methodName, String cause, long timeMillis) {
        var result = new MyJUnitTestResult();
        result.methodName = methodName;
        result.status = STATUS.FAILED;
        result.cause = cause;
        result.timeMillis = timeMillis;
        return result;
    }

    static MyJUnitTestResult createIgnored(String methodName, String cause) {
        var result = new MyJUnitTestResult();
        result.methodName = methodName;
        result.status = STATUS.IGNORED;
        result.cause = cause;
        return result;
    }

    public String getMethodName() {
        return methodName;
    }

    public STATUS getStatus() {
        return status;
    }

    /**
     * Returns info about FAILED or IGNORED test
     */
    public String getCause() {
        return cause;
    }

    /**
     * Returns test running time
     */
    public long getTimeMillis() {
        return timeMillis;
    }
}