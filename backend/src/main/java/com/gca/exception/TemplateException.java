package com.gca.exception;

public class TemplateException extends RuntimeException {
    private final ErrorType errorType;

    public TemplateException(String message, ErrorType errorType) {
        super(message);
        this.errorType = errorType;
    }

    public ErrorType getErrorType() {
        return errorType;
    }

    public enum ErrorType {
        NOT_FOUND,
        DUPLICATED
    }
}

