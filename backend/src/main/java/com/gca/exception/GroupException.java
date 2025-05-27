package com.gca.exception;

import java.io.Serial;

public class GroupException extends GCAException {

    @Serial
    private static final long serialVersionUID = -4490059974741105018L;

    public GroupException(String message, ErrorType errorType) {
        super(message, errorType);
    }
}