package com.bcgogo.api.response;

import com.bcgogo.api.ApiResponse;
import com.bcgogo.api.PushAppMessageDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * User: ZhangJuntao
 * Date: 13-8-19
 * Time: 下午4:08
 */
public class MessageResponse extends ApiResponse {
  private String message;
  private List<PushAppMessageDTO> messageList = new ArrayList<PushAppMessageDTO>();

  public MessageResponse() {
    super();
  }

  public MessageResponse(ApiResponse response) {
    super(response);
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public List<PushAppMessageDTO> getMessageList() {
    return messageList;
  }

  public void setMessageList(List<PushAppMessageDTO> messageList) {
    this.messageList = messageList;
  }
}
