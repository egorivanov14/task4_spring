package com.innowise.task.exception;

public class InitializationFailedException extends RuntimeException {
  public InitializationFailedException(String message) {
    super(message);
  }
}