package com.bcgogo.api;

import com.bcgogo.constant.pushMessage.PushMessageParamsKeyConstant;
import com.bcgogo.enums.app.ActionType;
import com.bcgogo.enums.txn.pushMessage.PushMessageType;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.JsonUtil;

import java.io.Serializable;
import java.util.Map;

/**
 * User: ZhangJuntao
 * Date: 13-8-27
 * Time: 下午6:16
 */
public class PushAppMessageDTO implements Serializable{
  private Long id;//唯一标识号      long
  private PushMessageType type;//消息类型
  private String content;//内容描述   String
  private ActionType actionType;//操作类型       String
  private String params;//actionType所依赖的业务数据     String
  private String title;
  private Long sendTime;
  private String sendTimeStr;

  public PushAppMessageDTO() {
  }

  public PushAppMessageDTO(Long id, String content, PushMessageType type, String params, String title,boolean isCommented,Long sendTime) {
    this.id = id;
    this.content = content;
    this.type = type;
    actionType = PushMessageType.lookupActionType(type,isCommented);
    Map<String,String> paramMap = JsonUtil.jsonToStringMap(params);
    this.params = paramMap.get(PushMessageParamsKeyConstant.AppParams);
    this.title = title;
    this.setSendTime(sendTime);
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public PushMessageType getType() {
    return type;
  }

  public void setType(PushMessageType type) {
    this.type = type;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public ActionType getActionType() {
    return actionType;
  }

  public void setActionType(ActionType actionType) {
    this.actionType = actionType;
  }

  public String getParams() {
    return params;
  }

  public void setParams(String params) {
    this.params = params;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public Long getSendTime() {
    return sendTime;
  }

  public void setSendTime(Long sendTime) {
    this.sendTime = sendTime;
    this.sendTimeStr= DateUtil.convertDateLongToDateString(DateUtil.ALL,sendTime);
  }

  public String getSendTimeStr() {
    return sendTimeStr;
  }

  public void setSendTimeStr(String sendTimeStr) {
    this.sendTimeStr = sendTimeStr;
  }
}
