package com.bcgogo.notification.dto;

import com.bcgogo.enums.MessageScene;
import com.bcgogo.enums.MessageSwitchStatus;

/**
 * Created by IntelliJ IDEA.
 * User: cfl
 * Date: 12-7-30
 * Time: 下午6:02
 * To change this template use File | Settings | File Templates.
 */
public class MessageSwitchDTO {
  private Long id;
  private Long shopId;
  private MessageScene scene;
  private MessageSwitchStatus status;

  public Long getId() {
    return id;
  }

  public Long getShopId() {
    return shopId;
  }

  public MessageScene getScene() {
    return scene;
  }

  public MessageSwitchStatus getStatus() {
    return status;
  }

  public void setId(Long id) {
    this.id = id;
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
}
