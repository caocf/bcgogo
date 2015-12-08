package com.bcgogo.notification.model;

import com.bcgogo.enums.DeletedType;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.wx.WXMsgReceiverDTO;

import javax.persistence.*;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 14-9-28
 * Time: 下午2:04
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "wx_msg_receiver")
public class WXMsgReceiver extends LongIdentifier {
  private Long msgId;    //本地数据库中wxmsg表的local id
  private String openId;//微信用户的openId
  private DeletedType deleted=DeletedType.FALSE;

  public void fromDTO(WXMsgReceiverDTO receiverDTO){
    this.setId(receiverDTO.getId());
    this.setMsgId(receiverDTO.getMsgId());
    this.setOpenId(receiverDTO.getOpenId());
    this.setDeleted(receiverDTO.getDeleted());
  }

  @Column(name = "msg_id")
  public Long getMsgId() {
    return msgId;
  }

  public void setMsgId(Long msgId) {
    this.msgId = msgId;
  }

  @Column(name = "deleted")
  @Enumerated(EnumType.STRING)
  public DeletedType getDeleted() {
    return deleted;
  }

  public void setDeleted(DeletedType deleted) {
    this.deleted = deleted;
  }

  @Column(name = "open_id")
  public String getOpenId() {
    return openId;
  }

  public void setOpenId(String openId) {
    this.openId = openId;
  }
}
