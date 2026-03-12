package com.AgentInsight.CustomException;

public class PerformanceDataException extends RuntimeException {
    public PerformanceDataException(String message, Throwable cause) {
        super(message, cause);
    }
    public PerformanceDataException(String message) {
        super(message);
    }
}