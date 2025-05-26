package com.gca.exception;

import java.io.Serial;
import java.io.Serializable;

public class GCAException extends RuntimeException implements Serializable {
    @Serial
    private static final long serialVersionUID = 99120482123058539L;
    private final ErrorType errorType;

    public GCAException(String message, ErrorType errorType) {
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

