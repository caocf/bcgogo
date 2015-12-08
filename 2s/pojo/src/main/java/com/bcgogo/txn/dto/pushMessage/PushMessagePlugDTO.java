package com.bcgogo.txn.dto.pushMessage;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: xzhu
 * Date: 13-6-5
 * Time: 上午10:27
 * To change this template use File | Settings | File Templates.
 */
public class PushMessagePlugDTO {
  private String uuid;//消息组件必须的
  private String title;//消息组件必须的
  private String requestInterval;//消息组件必须的
  private String hideInterval;//消息组件必须的

  private String serverTime;
  private List<PushMessageDTO> data;

  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getRequestInterval() {
    return requestInterval;
  }

  public void setRequestInterval(String requestInterval) {
    this.requestInterval = requestInterval;
  }

  public String getHideInterval() {
    return hideInterval;
  }

  public void setHideInterval(String hideInterval) {
    this.hideInterval = hideInterval;
  }

  public String getServerTime() {
    return serverTime;
  }

  public void setServerTime(String serverTime) {
    this.serverTime = serverTime;
  }

  public List<PushMessageDTO> getData() {
    return data;
  }

  public void setData(List<PushMessageDTO> data) {
    this.data = data;
  }
}
