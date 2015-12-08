package com.bcgogo.mq.message;


import java.io.Serializable;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 2015-6-5
 * Time: 16:31
 */
public class MQMessageDTO implements Serializable{
  private String appUserNo;
  private Long sendTime;
  private List<MQMessageItemDTO> itemDTOs;

  public String getAppUserNo() {
    return appUserNo;
  }

  public void setAppUserNo(String appUserNo) {
    this.appUserNo = appUserNo;
  }

  public Long getSendTime() {
    return sendTime;
  }

  public void setSendTime(Long sendTime) {
    this.sendTime = sendTime;
  }

  public List<MQMessageItemDTO> getItemDTOs() {
    return itemDTOs;
  }

  public void setItemDTOs(List<MQMessageItemDTO> itemDTOs) {
    this.itemDTOs = itemDTOs;
  }
}