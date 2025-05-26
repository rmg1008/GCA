package com.gca.exception;

import java.io.Serial;

public class ConfigException extends GCAException {

    @Serial
    private static final long serialVersionUID = 5587241376245299350L;

    public ConfigException(String message, ErrorType errorType) {
        super(message, errorType);
    }
}