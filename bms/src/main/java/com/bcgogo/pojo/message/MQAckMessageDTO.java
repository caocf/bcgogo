package com.bcgogo.pojo.message;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 15-6-9
 * Time: 下午6:29
 */
public class MQAckMessageDTO {
  private Long sendTime;
  private List<MQAckMessageItemDTO> itemDTOs;

  public Long getSendTime() {
    return sendTime;
  }

  public void setSendTime(Long sendTime) {
    this.sendTime = sendTime;
  }

  public List<MQAckMessageItemDTO> getItemDTOs() {
    return itemDTOs;
  }

  public void setItemDTOs(List<MQAckMessageItemDTO> itemDTOs) {
    this.itemDTOs = itemDTOs;
  }
}
