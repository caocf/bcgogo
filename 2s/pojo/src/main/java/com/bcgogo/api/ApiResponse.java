package com.bcgogo.api;

import com.bcgogo.enums.app.MessageCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

/**
 * User: ZhangJuntao
 * Date: 13-8-8
 * Time: 下午1:54
 */
public class ApiResponse implements Serializable {
  private static final Logger LOG = LoggerFactory.getLogger(ApiResponse.class);
  private String status;
  private int msgCode;
  private String message;
  private Object debug = null;

  public ApiResponse() {
    super();
  }

  public ApiResponse(ApiResponse response) {
    setApiResponse(response);
  }

  public void setApiResponse(ApiResponse response) {
    this.setErrorCode(response.getMsgCode());
    this.setStatus(response.getStatus());
    this.setMessage(response.getMessage());
    this.setDebug(response.getDebug());
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public int getMsgCode() {
    return msgCode;
  }

  public void setErrorCode(int msgCode) {
    this.msgCode = msgCode;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public void setMessageCode(MessageCode messageCode) {
    this.msgCode = messageCode.getCode();
    this.message = messageCode.getValue();
    this.status = messageCode.getStatus();
  }

  public Object getDebug() {
    return debug;
  }

  public void setDebug(Object debug) {
    String isProductionEnvironment = System.getProperty("is.production.environment");
    if (LOG.isDebugEnabled() && (isProductionEnvironment == null || isProductionEnvironment.equals("false"))) {
      this.debug = "debug:your params [" + debug + "]";
    }
  }

  @Override
  public String toString() {
    return "ApiResponse{" +
      "status='" + status + '\'' +
      ", msgCode=" + msgCode +
      ", message='" + message + '\'' +
      ", debug=" + debug +
      '}';
  }
}
