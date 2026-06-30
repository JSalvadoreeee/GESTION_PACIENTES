package com.SaludPlus.gestion_pacientes.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleDuplicatedEntry(SQLIntegrityConstraintViolationException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", "Dato Duplicado");
        response.put("mensaje", "El RUN o el Correo ingresado ya existe en el sistema.");
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneralError(Exception ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", "Error Interno");
        response.put("mensaje", "Ocurrió un error inesperado: " + ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}