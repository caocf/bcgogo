package com.bcgogo.notification.model;

import com.bcgogo.enums.MessageScene;
import com.bcgogo.enums.MessageSwitchStatus;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.notification.dto.MessageSwitchDTO;

import javax.persistence.*;

/**
 * Created by IntelliJ IDEA.
 * User: cfl
 * Date: 12-7-30
 * Time: 下午4:58
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "message_switch")
public class MessageSwitch extends LongIdentifier{
  private Long shopId;
  private MessageScene scene;
  private MessageSwitchStatus status;

  @Column(name="shop_id")
  public Long getShopId() {
    return shopId;
  }

  @Enumerated(EnumType.STRING)
  @Column(name="scene")
  public MessageScene getScene() {
    return scene;
  }

  @Enumerated(EnumType.STRING)
  @Column(name="status")
  public MessageSwitchStatus getStatus() {
    return status;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public void setScene(MessageScene scene) {
    this.scene = scene;
  }

  public void setStatus(MessageSwitchStatus status) {
    this.status = status;
  }

  public MessageSwitchDTO toDTO()
  {
    MessageSwitchDTO messageSwitchDTO = new MessageSwitchDTO();
    messageSwitchDTO.setId(this.getId());
    messageSwitchDTO.setScene(this.getScene());
    messageSwitchDTO.setShopId(this.getShopId());
    messageSwitchDTO.setStatus(this.getStatus());
    return messageSwitchDTO;
  }
}
