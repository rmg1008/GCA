package com.gca.exception;

import java.io.Serial;

public class CommandException extends GCAException {

    @Serial
    private static final long serialVersionUID = 9128507007474014909L;

    public CommandException(String message, ErrorType errorType) {
        super(message, errorType);
    }
}