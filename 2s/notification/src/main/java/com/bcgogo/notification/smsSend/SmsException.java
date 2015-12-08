package com.bcgogo.notification.smsSend;

import com.bcgogo.exception.BcgogoException;
import com.bcgogo.exception.BcgogoExceptionType;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-3-30
 * Time: 下午2:23
 * 短信异常类
 */
public class SmsException extends BcgogoException {
  public SmsException() {
    super();
  }

  public SmsException(String message) {
    super(message);
  }

  public SmsException(String message, Throwable cause) {
    super(message, cause);
  }

  public SmsException(Throwable cause) {
    super(cause);
  }

  public SmsException(BcgogoExceptionType type) {
    this(type.getMessage());
    this.type = type;
  }

  BcgogoExceptionType type;

  public BcgogoExceptionType getType() {
    return type;
  }

}
