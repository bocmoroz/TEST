package org.test.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.test.dto.ResponseDto;
import org.test.exception.DeletionValidationException;
import org.test.exception.WarehouseValidationException;

import java.util.Arrays;

@RestControllerAdvice
@Slf4j
public class CommonExceptionHandlers {

    @ExceptionHandler(DeletionValidationException.class)
    public ResponseEntity<ResponseDto<Object>> handleDeletionValidationException(DeletionValidationException exception) {
        log.error("exception", exception);
        return new ResponseEntity<>(ResponseDto.fail(exception.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ResponseDto<Object>> handleMissingServletRequestParameterException(MissingServletRequestParameterException exception) {
        log.error("exception", exception);
        String errorMessage = String.format("Missing %s parameter in request with type %s", exception.getParameterName(), exception.getParameterType());
        return new ResponseEntity<>(ResponseDto.error(errorMessage), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ResponseDto<Object>> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException exception) {
        log.error("exception", exception);
        String errorMessage = String.format("HTTP Method '%s' is not supported for this route. Use %s", exception.getMethod(), Arrays.toString(exception.getSupportedMethods()));
        return new ResponseEntity<>(ResponseDto.error(errorMessage), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDto<Object>> handleException(Exception exception) {
        log.error("exception", exception);
        return new ResponseEntity<>(ResponseDto.error(exception.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
