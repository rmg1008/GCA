package com.gca.exception;

import java.io.Serial;

public class TemplateException extends GCAException {

    @Serial
    private static final long serialVersionUID = 4512696129916648976L;

    public TemplateException(String message, ErrorType errorType) {
        super(message, errorType);
    }
}

