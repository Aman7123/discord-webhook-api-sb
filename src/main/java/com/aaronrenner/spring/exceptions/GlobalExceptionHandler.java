package com.aaronrenner.spring.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import java.time.LocalDateTime;

//TODO: Duplicate entry error mapped as bad request, probly
@ControllerAdvice
public class GlobalExceptionHandler {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException exception) {
        LOGGER.error(exception.getMessage());
        ErrorResponse response = new ErrorResponse( LocalDateTime.now(),
                                                    HttpStatus.NOT_FOUND.value(),
                                                    exception.getMessage());

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleBadRequestException(HttpMessageNotReadableException exception) {
    	LOGGER.error(exception.getMessage());
        ErrorResponse response = new ErrorResponse( LocalDateTime.now(),
                                                    HttpStatus.BAD_REQUEST.value(),
                                                    exception.getMessage());

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequestException(BadRequestException exception) {
    	LOGGER.error(exception.getMessage());
        ErrorResponse response = new ErrorResponse( LocalDateTime.now(),
                                                    HttpStatus.BAD_REQUEST.value(),
                                                    exception.getMessage());

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InternalServerError.class)
    public ResponseEntity<ErrorResponse> handleInternalServerError(InternalServerError exception) {
    	LOGGER.error(exception.getMessage());
        ErrorResponse response = new ErrorResponse( LocalDateTime.now(),
                                                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                                                    exception.getMessage());

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(DuplicateException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateEntryError(DuplicateException exception) {
    	LOGGER.error(exception.getMessage());
        ErrorResponse response = new ErrorResponse( LocalDateTime.now(),
                HttpStatus.CONFLICT.value(),
                exception.getMessage());

        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }
    
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedException(UnauthorizedException exception) {
    	LOGGER.error(exception.getMessage());
        ErrorResponse response = new ErrorResponse( LocalDateTime.now(),
                                                    HttpStatus.UNAUTHORIZED.value(),
                                                    exception.getMessage());

        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }
}
