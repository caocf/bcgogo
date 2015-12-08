package com.bcgogo.config.service.UMPush;

import com.bcgogo.config.service.UMPush.UMPushConstant.UMPushType;

/**
 * Created by XinyuQiu on 14-4-29.
 */
public class UnicastUMNotification implements UMNotification {
  private String appkey;
  private String timestamp;
  private String validation_token;
  private String device_tokens;
  private UMPushType type;
  private UMPayLoad payload;
  private boolean production_mode = true;
  private String description;

  public String getAppkey() {
    return appkey;
  }

  public void setAppkey(String appkey) {
    this.appkey = appkey;
  }

  public String getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(String timestamp) {
    this.timestamp = timestamp;
  }

  public String getValidation_token() {
    return validation_token;
  }

  public void setValidation_token(String validation_token) {
    this.validation_token = validation_token;
  }

  public String getDevice_tokens() {
    return device_tokens;
  }

  public void setDevice_tokens(String device_tokens) {
    this.device_tokens = device_tokens;
  }

  public UMPushType getType() {
    return type;
  }

  public void setType(UMPushType type) {
    this.type = type;
  }

  public UMPayLoad getPayload() {
    return payload;
  }

  public void setPayload(UMPayLoad payload) {
    this.payload = payload;
  }

  public boolean isProduction_mode() {
    return production_mode;
  }

  public void setProduction_mode(boolean production_mode) {
    this.production_mode = production_mode;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }
}
