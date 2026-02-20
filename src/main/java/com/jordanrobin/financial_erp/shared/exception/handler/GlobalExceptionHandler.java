package com.jordanrobin.financial_erp.shared.exception.handler;

import com.jordanrobin.financial_erp.shared.exception.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleAnnotated(RuntimeException ex) {
        ResponseStatus status = ex.getClass().getAnnotation(ResponseStatus.class);
        HttpStatus httpStatus = (status != null) ? status.value() : HttpStatus.INTERNAL_SERVER_ERROR;
        return ResponseEntity.status(httpStatus).body(new ErrorResponse(ex.getMessage()));
    }
}
