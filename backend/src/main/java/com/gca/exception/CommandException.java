package com.gca.exception;

public class CommandException extends RuntimeException {
    private final ErrorType errorType;

    public CommandException(String message, ErrorType errorType) {
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

