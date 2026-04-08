package com.brunpola.cv_management.exceptions.base;

/**
 * Base exception for resources that cannot be found.
 *
 * <p>All specific "not found" exceptions should extend this class to provide a consistent way of
 * handling missing resources across the application.
 */
public abstract class NotFoundException extends RuntimeException {

  /**
   * Creates a new NotFoundException with a custom message.
   *
   * @param message detailed error message
   */
  protected NotFoundException(String message) {
    super(message);
  }
}
