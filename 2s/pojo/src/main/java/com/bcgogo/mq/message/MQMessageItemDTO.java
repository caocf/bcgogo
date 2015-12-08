package com.bcgogo.mq.message;

import com.bcgogo.constant.MQConstant;
import com.bcgogo.enums.txn.pushMessage.PushMessageType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 15-6-10
 * Time: 上午10:30
 */
public class MQMessageItemDTO implements Serializable {
  private Long msgId;
  private Long createTime;
  private String title;
  private String content;
  private String toUserName;
  private String fromUserName;
  private int type;

  public MQMessageItemDTO() {
  }

  public MQTalkMessageDTO toMQTalkMessageDTO() {
    MQTalkMessageDTO messageDTO = new MQTalkMessageDTO();
    messageDTO.setToUserName(getToUserName());
    messageDTO.setFromUserName(getFromUserName());
    messageDTO.setContent(getContent());
    messageDTO.setTime(getCreateTime());
    return messageDTO;
  }

  public MQMessageDTO toMQMessageDTO() {
    MQMessageDTO messageDTO = new MQMessageDTO();
    messageDTO.setSendTime(getCreateTime());
    MQMessageItemDTO itemDTO = new MQMessageItemDTO();
    itemDTO.setToUserName(getToUserName());
    itemDTO.setFromUserName(getFromUserName());
    itemDTO.setContent(getContent());
    itemDTO.setType(MQConstant.pushMessageTypeMap.get(PushMessageType.MSG_FROM_WX_USER_TO_MIRROR.toString()));
    List<MQMessageItemDTO> itemDTOs = new ArrayList<MQMessageItemDTO>();
    itemDTOs.add(itemDTO);
    messageDTO.setItemDTOs(itemDTOs);
    return messageDTO;
  }

  public Long getMsgId() {
    return msgId;
  }

  public void setMsgId(Long msgId) {
    this.msgId = msgId;
  }

  public Long getCreateTime() {
    return createTime;
  }

  public void setCreateTime(Long createTime) {
    this.createTime = createTime;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
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

  public int getType() {
    return type;
  }

  public void setType(int type) {
    this.type = type;
  }
}
