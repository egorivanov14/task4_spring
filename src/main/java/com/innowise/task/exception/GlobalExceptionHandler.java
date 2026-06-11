package com.innowise.task.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(AlreadyExistsException.class)
  public ResponseEntity<String> handleException(AlreadyExistsException e) {
    return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
  }

  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<String> handleException(NotFoundException e) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
  }

  @ExceptionHandler(InvalidRequestDataException.class)
  public ResponseEntity<String> handleException(InvalidRequestDataException e) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
  }

  @ExceptionHandler(UnauthorizedException.class)
  public ResponseEntity<String> handleException(UnauthorizedException e) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
  }

  @ExceptionHandler(InitializationFailedException.class)
  public ResponseEntity<String> handleException(InitializationFailedException e) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
  }
}