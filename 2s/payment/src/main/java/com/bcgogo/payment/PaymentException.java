package com.bcgogo.payment;

import com.bcgogo.exception.BcgogoException;
import com.bcgogo.exception.BcgogoExceptionType;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-3-30
 * Time: 下午2:23
 * To change this template use File | Settings | File Templates.
 */
public class PaymentException extends BcgogoException {
  public PaymentException() {
    super();
  }

  public PaymentException(String message) {
    super(message);
  }

  public PaymentException(String message, Throwable cause) {
    super(message, cause);
  }

  public PaymentException(Throwable cause) {
    super(cause);
  }

  public PaymentException(BcgogoExceptionType type) {
    this(type.getMessage());
    this.type = type;
  }

  BcgogoExceptionType type;

  public BcgogoExceptionType getType() {
    return type;
  }

}
