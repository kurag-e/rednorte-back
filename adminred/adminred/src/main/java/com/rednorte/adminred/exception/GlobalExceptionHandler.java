package com.rednorte.adminred.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class) 
    public ResponseEntity<ApiError> notFound(ResourceNotFoundException ex){ 
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), null); 
    }

    @ExceptionHandler(BusinessException.class) 
    public ResponseEntity<ApiError> business(BusinessException ex){ 
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), null); 
    }

    @ExceptionHandler(MethodArgumentNotValidException.class) 
    public ResponseEntity<ApiError> validation(MethodArgumentNotValidException ex){ 
        Map<String,String> d=new HashMap<>(); 
        ex.getBindingResult().getFieldErrors().forEach(e->d.put(e.getField(), e.getDefaultMessage())); 
        return build(HttpStatus.BAD_REQUEST, "Error de validacion", d); 
    }
    
    private ResponseEntity<ApiError> build(HttpStatus s, String msg, Map<String,String> d){ 
        return ResponseEntity.status(s).body(new ApiError(LocalDateTime.now(), s.value(), s.getReasonPhrase(), msg, d)); 
    }
}

