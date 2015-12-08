package com.bcgogo.exception;

/**
 * Created by IntelliJ IDEA.
 * User: XJ
 * Date: 9/27/11
 * Time: 1:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class BcgogoException extends Exception {
  public BcgogoException() {
    super();
  }

  public BcgogoException(String message) {
    super(message);
  }

  public BcgogoException(String message, Throwable cause) {
    super(message, cause);
  }

  public BcgogoException(Throwable cause) {
    super(cause);
  }

  public BcgogoException(BcgogoExceptionType type) {
    this(type.getMessage());
    this.type = type;
  }

  BcgogoExceptionType type;

  public BcgogoExceptionType getType() {
    return type;
  }
}
