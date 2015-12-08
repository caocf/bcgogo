package com.bcgogo.user.model;

import com.bcgogo.enums.MessageSendNecessaryType;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.user.dto.UserLimitDTO;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by IntelliJ IDEA.
 * User: WeiLingfeng
 * Date: 12-10-22
 * Time: 下午7:22
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "user_limit")
public class UserLimit extends LongIdentifier {
  private Long shopId;
  private String name;
  private String type;

  private String scene;
  private MessageSendNecessaryType necessary;

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name="name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Column(name = "type")
  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

//  @Enumerated(EnumType.STRING)
  @Column(name="scene")
  public String getScene() {
    return scene;
  }

  public void setScene(String scene) {
    this.scene = scene;
  }

  @Column(name="necessary")
  public MessageSendNecessaryType getNecessary() {
    return necessary;
  }

  public void setNecessary(MessageSendNecessaryType necessary) {
    this.necessary = necessary;
  }

  public UserLimitDTO toDTO(){
    UserLimitDTO userLimitDTO = new UserLimitDTO();
    userLimitDTO.setId(this.getId());
    userLimitDTO.setShopId(this.getShopId());
    userLimitDTO.setName(this.getName());
    userLimitDTO.setNecessary(this.getNecessary());
    userLimitDTO.setScene(this.getScene());
    userLimitDTO.setType(this.getType());
    return userLimitDTO;
  }

}