package com.bcgogo.mq.message;

import com.bcgogo.enums.txn.pushMessage.PushMessageType;
import com.bcgogo.utils.DateUtil;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 15-6-14
 * Time: 上午9:26
 */
public class MQTalkMessageDTO implements Serializable{
  private Long time;
  private String timeStr;
  private String content;
  private String toUserName;
  private String fromUserName;
  private String appUserNo;
  private PushMessageType type;


  public Long getTime() {
    return time;
  }

  public void setTime(Long time) {
    this.time = time;
    this.timeStr= DateUtil.convertDateLongToString(time,DateUtil.ALL);
  }

  public String getTimeStr() {
    return timeStr;
  }

  public void setTimeStr(String timeStr) {
    this.timeStr = timeStr;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public String getToUserName() {
    return toUserName;
  }

  public void setToUserName(String toUserName) {
    this.toUserName = toUserName;
  }

  public String getFromUserName() {
    return fromUserName;
  }

  public void setFromUserName(String fromUserName) {
    this.fromUserName = fromUserName;
  }

  public String getAppUserNo() {
    return appUserNo;
  }

  public void setAppUserNo(String appUserNo) {
    this.appUserNo = appUserNo;
  }

  public PushMessageType getType() {
    return type;
  }

  public void setType(PushMessageType type) {
    this.type = type;
  }
}
