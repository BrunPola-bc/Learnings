package com.brunpola.cv_management.exceptions.base;

public abstract class NotFoundException extends RuntimeException {
  public NotFoundException(String message) {
    super(message);
  }
}
