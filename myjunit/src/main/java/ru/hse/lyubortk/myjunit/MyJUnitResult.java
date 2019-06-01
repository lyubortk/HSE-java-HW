package ru.hse.lyubortk.myjunit;

import java.util.Collections;
import java.util.List;

public class MyJUnitResult {
    private String className;
    private TESTING_STATUS status;
    private String cause;
    private int successMethodsNumber;
    private int failedMethodsNumber;
    private List<MethodResult> results;

    public enum TESTING_STATUS {
        SUCCESS,
        FAILED,
        ERROR
    }

    public static class MethodResult {
        private String methodName;
        private METHOD_STATUS status;
        private String cause;
        private long timeMillis;

        public enum METHOD_STATUS {
            SUCCESS,
            FAILED,
            IGNORED
        }

        private MethodResult() {
        }

        static MethodResult createSuccess(String methodName, long timeMillis) {
            var result = new MethodResult();
            result.methodName = methodName;
            result.status = METHOD_STATUS.SUCCESS;
            result.timeMillis = timeMillis;
            return result;
        }

        static MethodResult createFailed(String methodName, String cause, long timeMillis) {
            var result = new MethodResult();
            result.methodName = methodName;
            result.status = METHOD_STATUS.FAILED;
            result.cause = cause;
            result.timeMillis = timeMillis;
            return result;
        }

        static MethodResult createIgnored(String methodName, String cause) {
            var result = new MethodResult();
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

    private MyJUnitResult() {
    }

    static MyJUnitResult createSuccess(String name, int successMethodsNumber,
                                       List<MethodResult> results) {
        var result = new MyJUnitResult();
        result.className = name;
        result.status = TESTING_STATUS.SUCCESS;
        result.successMethodsNumber = successMethodsNumber;
        result.results = results;
        return result;
    }

    static MyJUnitResult createFailed(String name, int successMethodsNumber,
                                      int failedMethodsNumber, List<MethodResult> results) {
        var result = new MyJUnitResult();
        result.className = name;
        result.status = TESTING_STATUS.FAILED;
        result.successMethodsNumber = successMethodsNumber;
        result.failedMethodsNumber = failedMethodsNumber;
        result.results = results;
        return result;
    }

    static MyJUnitResult createError(String name, String cause) {
        var result = new MyJUnitResult();
        result.className = name;
        result.status = TESTING_STATUS.ERROR;
        result.cause = cause;
        result.results = Collections.emptyList();
        return result;
    }

    public String getClassName() {
        return className;
    }

    public TESTING_STATUS getStatus() {
        return status;
    }

    public String getCause() {
        return cause;
    }

    public int getSuccessMethodsNumber() {
        return successMethodsNumber;
    }

    public int getFailedMethodsNumber() {
        return failedMethodsNumber;
    }

    public List<MethodResult> getMethodsResults() {
        return Collections.unmodifiableList(results);
    }
}
