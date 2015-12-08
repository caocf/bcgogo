package com.bcgogo.user.dto;

import com.bcgogo.enums.MessageSendNecessaryType;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: WeiLingfeng
 * Date: 12-10-22
 * Time: 下午8:01
 * To change this template use File | Settings | File Templates.
 */
public class UserLimitDTO implements Serializable {
  public UserLimitDTO(){}

  private Long id;
  private Long shopId;
  private String name;
  private String type;

  private String scene;
  private MessageSendNecessaryType necessary;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getScene() {
    return scene;
  }

  public void setScene(String scene) {
    this.scene = scene;
  }

  public MessageSendNecessaryType getNecessary() {
    return necessary;
  }

  public void setNecessary(MessageSendNecessaryType necessary) {
    this.necessary = necessary;
  }
}
