package ru.hse.lyubortk.myjunit;


public class MyJUnitTestResult {
    private String methodName;
    private METHOD_STATUS status;
    private String cause;
    private long timeMillis;

    public enum METHOD_STATUS {
        PASSED,
        FAILED,
        IGNORED
    }

    private MyJUnitTestResult() {
    }

    static MyJUnitTestResult createPassed(String methodName, long timeMillis) {
        var result = new MyJUnitTestResult();
        result.methodName = methodName;
        result.status = METHOD_STATUS.PASSED;
        result.timeMillis = timeMillis;
        return result;
    }

    static MyJUnitTestResult createFailed(String methodName, String cause, long timeMillis) {
        var result = new MyJUnitTestResult();
        result.methodName = methodName;
        result.status = METHOD_STATUS.FAILED;
        result.cause = cause;
        result.timeMillis = timeMillis;
        return result;
    }

    static MyJUnitTestResult createIgnored(String methodName, String cause) {
        var result = new MyJUnitTestResult();
        result.methodName = methodName;
        result.status = METHOD_STATUS.IGNORED;
        result.cause = cause;
        return result;
    }

    public String getMethodName() {
        return methodName;
    }

    public METHOD_STATUS getStatus() {
        return status;
    }

    public String getCause() {
        return cause;
    }

    public long getTimeMillis() {
        return timeMillis;
    }
}