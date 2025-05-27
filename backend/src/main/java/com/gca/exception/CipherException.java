package com.gca.exception;

public class CipherException extends RuntimeException {
    private final ErrorType errorType;

    public CipherException(String message, ErrorType errorType) {
        super(message);
        this.errorType = errorType;
    }

    public CipherException(String message, ErrorType errorType, Throwable cause) {
        super(message, cause);
        this.errorType = errorType;
    }

    public ErrorType getErrorType() {
        return errorType;
    }

    public enum ErrorType {
        ENCRYPTION,
        DECRYPTION,
        HASH
    }
}

