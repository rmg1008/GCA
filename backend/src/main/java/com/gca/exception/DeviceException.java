package com.gca.exception;

import java.io.Serial;

public class DeviceException extends GCAException{

    @Serial
    private static final long serialVersionUID = -56829807481218007L;

    public DeviceException(String message, ErrorType errorType) {
        super(message, errorType);
    }
}