package com.bcgogo.api.response;

import com.bcgogo.api.ApiResponse;
import com.bcgogo.api.AppShopCommentDTO;
import com.bcgogo.api.AppShopDTO;

import java.util.List;

/**
 * User: lw
 * Date: 14-5-26
 * Time: 下午4:08
 */
public class ApiMsgContentResponse extends ApiResponse {
  private String msgContent;//短信内容

  public ApiMsgContentResponse() {
    super();
  }

  public ApiMsgContentResponse(ApiResponse response) {
    super(response);
  }

  public String getMsgContent() {
    return msgContent;
  }

  public void setMsgContent(String msgContent) {
    this.msgContent = msgContent;
  }
}
