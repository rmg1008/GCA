package com.gca.exception;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.http.HttpStatus;

class GlobalExceptionHandlerTest {

    GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();

    @ParameterizedTest
    @EnumSource(value = CommandException.ErrorType.class)
    void testHandleCommandException(CommandException.ErrorType errorType) {
        CommandException exception = new CommandException("Command exception", errorType);

        var response = globalExceptionHandler.handleCommandException(exception);

        HttpStatus expectedStatus = switch (errorType) {
            case NOT_FOUND -> HttpStatus.NOT_FOUND;
            case DUPLICATED -> HttpStatus.CONFLICT;
        };

        Assertions.assertEquals("Command exception", response.getBody());
        Assertions.assertEquals(expectedStatus, response.getStatusCode());
    }

    @ParameterizedTest
    @EnumSource(value = TemplateException.ErrorType.class)
    void testHandleTemplateException(TemplateException.ErrorType errorType) {
        TemplateException exception = new TemplateException("Template exception", errorType);

        var response = globalExceptionHandler.handleTemplateException(exception);

        HttpStatus expectedStatus = switch (errorType) {
            case NOT_FOUND -> HttpStatus.NOT_FOUND;
            case DUPLICATED -> HttpStatus.CONFLICT;
        };

        Assertions.assertEquals("Template exception", response.getBody());
        Assertions.assertEquals(expectedStatus, response.getStatusCode());
    }

    @Test
    void testHandleConfigException() {
        ConfigException exception = new ConfigException("Config exception", ConfigException.ErrorType.NOT_FOUND);

        var response = globalExceptionHandler.handleConfigException(exception);

        Assertions.assertEquals("Config exception", response.getBody());
        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @ParameterizedTest
    @EnumSource(value = CipherException.ErrorType.class)
    void testHandleCipherException(CipherException.ErrorType errorType) {
        CipherException exception = new CipherException("Cipher exception", errorType);

        var response = globalExceptionHandler.handleCipherException(exception);

        HttpStatus expectedStatus = switch (errorType) {
            case ENCRYPTION, DECRYPTION, HASH -> HttpStatus.INTERNAL_SERVER_ERROR;
        };

        Assertions.assertEquals("Cipher exception", response.getBody());
        Assertions.assertEquals(expectedStatus, response.getStatusCode());
    }

}
