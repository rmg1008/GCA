package com.gca.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleException(Exception ex) {
        Map<String, String> body = new HashMap<>();
        body.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    @ExceptionHandler(CommandException.class)
    public ResponseEntity<String> handleCommandException(CommandException ex) {
        HttpStatus status = switch (ex.getErrorType()) {
            case NOT_FOUND -> HttpStatus.NOT_FOUND;
            case DUPLICATED -> HttpStatus.CONFLICT;
        };
        return new ResponseEntity<>(ex.getMessage(), status);
    }

    @ExceptionHandler(TemplateException.class)
    public ResponseEntity<String> handleTemplateException(TemplateException ex) {
        HttpStatus status = switch (ex.getErrorType()) {
            case NOT_FOUND -> HttpStatus.NOT_FOUND;
            case DUPLICATED -> HttpStatus.CONFLICT;
        };
        return new ResponseEntity<>(ex.getMessage(), status);
    }

    @ExceptionHandler(ConfigException.class)
    public ResponseEntity<String> handleConfigException(ConfigException ex) {
        HttpStatus status = switch (ex.getErrorType()) {
            case NOT_FOUND -> HttpStatus.NOT_FOUND;
        };
        return new ResponseEntity<>(ex.getMessage(), status);
    }

    @ExceptionHandler(CipherException.class)
    public ResponseEntity<String> handleCipherException(CipherException ex) {
        HttpStatus status = switch (ex.getErrorType()) {
            case ENCRYPTION, DECRYPTION, HASH -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
        return new ResponseEntity<>(ex.getMessage(), status);
    }

}
