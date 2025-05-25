package com.gca.exception;

public class ConfigException extends RuntimeException {
    private final ErrorType errorType;

    public ConfigException(String message, ErrorType errorType) {
        super(message);
        this.errorType = errorType;
    }

    public ErrorType getErrorType() {
        return errorType;
    }

    public enum ErrorType {
        NOT_FOUND
    }
}

