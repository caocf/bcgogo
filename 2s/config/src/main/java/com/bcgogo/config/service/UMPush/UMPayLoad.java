package com.bcgogo.config.service.UMPush;

import com.bcgogo.config.service.UMPush.UMPushConstant.UMPushDisplayType;

/**
 * Created by XinyuQiu on 14-4-29.
 */
public class UMPayLoad {
  // 必填 消息类型，值为notification或者message
  //      notification-通知, message-消息.
  private UMPushDisplayType display_type;
  private UMPayLoadBody body;




  public UMPushDisplayType getDisplay_type() {
    return display_type;
  }

  public void setDisplay_type(UMPushDisplayType display_type) {
    this.display_type = display_type;
  }

  public UMPayLoadBody getBody() {
    return body;
  }

  public void setBody(UMPayLoadBody body) {
    this.body = body;
  }
}
