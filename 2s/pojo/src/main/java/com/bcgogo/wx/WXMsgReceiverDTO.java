package com.bcgogo.wx;

import com.bcgogo.enums.DeletedType;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 14-10-30
 * Time: 下午4:43
 */
public class WXMsgReceiverDTO {
  private Long id;
  private Long msgId;    //本地数据库中wxmsg表的local id
  private String openId;//微信用户的openId
  private DeletedType deleted=DeletedType.FALSE;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getMsgId() {
    return msgId;
  }

  public void setMsgId(Long msgId) {
    this.msgId = msgId;
  }

  public String getOpenId() {
    return openId;
  }

  public void setOpenId(String openId) {
    this.openId = openId;
  }

  public DeletedType getDeleted() {
    return deleted;
  }

  public void setDeleted(DeletedType deleted) {
    this.deleted = deleted;
  }
}
