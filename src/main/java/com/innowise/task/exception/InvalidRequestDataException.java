package com.innowise.task.exception;

public class InvalidRequestDataException extends RuntimeException {
  public InvalidRequestDataException(String message) {
    super(message);
  }
}