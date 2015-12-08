package com.bcgogo.api.response;

import com.bcgogo.api.ApiResponse;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 13-12-18
 * Time: 下午3:09
 */
public class ApiOBDMessageResponse extends ApiResponse {
 private Object messageInfo;

  public ApiOBDMessageResponse() {
    super();
  }

  public ApiOBDMessageResponse(ApiResponse response) {
    super(response);
  }

  public Object getMessageInfo() {
    return messageInfo;
  }

  public void setMessageInfo(Object messageInfo) {
    this.messageInfo = messageInfo;
  }
}
