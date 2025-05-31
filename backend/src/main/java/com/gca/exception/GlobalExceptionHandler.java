package com.gca.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleException(Exception ex) {
        Map<String, String> body = new HashMap<>();
        body.put("message", ex.getMessage());
        LOGGER.error("Error interno: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    @ExceptionHandler(GCAException.class)
    public ResponseEntity<String> handleGCAException(GCAException ex) {
        HttpStatus status = switch (ex.getErrorType()) {
            case NOT_FOUND -> HttpStatus.NOT_FOUND;
            case DUPLICATED -> HttpStatus.CONFLICT;
        };
        LOGGER.error("Error de GCA: {}", ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), status);
    }

    @ExceptionHandler(CipherException.class)
    public ResponseEntity<String> handleCipherException(CipherException ex) {
        HttpStatus status = switch (ex.getErrorType()) {
            case ENCRYPTION, DECRYPTION, HASH -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
        LOGGER.error("Error de cifrado: {}", ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), status);
    }
}
