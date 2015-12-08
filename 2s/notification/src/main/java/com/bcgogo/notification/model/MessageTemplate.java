package com.bcgogo.notification.model;

import com.bcgogo.enums.MessageScene;
import com.bcgogo.enums.MessageSendNecessaryType;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.notification.dto.MessageTemplateDTO;

import javax.persistence.*;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-4-6
 * Time: 下午3:30
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "message_template")
public class MessageTemplate extends LongIdentifier {
  private Long shopId;
  private String type;
  private String content;
  private Long syncTime;
  private String name;
  private MessageScene scene;
  private MessageSendNecessaryType necessary;

  public MessageTemplate(){}

  public MessageTemplate(MessageTemplateDTO templateDTO){
    if(templateDTO==null) return;
    this.shopId=templateDTO.getShopId();
    this.name=templateDTO.getName();
    this.content=templateDTO.getContent();
    this.type=templateDTO.getType();
  }

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "type")
  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  @Column(name = "content", length = 4000)
  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  @Transient
  public Long getSyncTime() {
    return syncTime;
  }

  public void setSyncTime(Long syncTime) {
    this.syncTime = syncTime;
  }

  public String assembleKey() {
    return "msgTemplate_" + type + "_" + String.valueOf(shopId);
  }

  @Column(name="name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Enumerated(EnumType.STRING)
  @Column(name="scene")
  public MessageScene getScene() {
    return scene;
  }

  public void setScene(MessageScene scene) {
    this.scene = scene;
  }

  @Enumerated(EnumType.STRING)
  @Column(name = "necessary")
  public MessageSendNecessaryType getNecessary() {
    return necessary;
  }

  public void setNecessary(MessageSendNecessaryType necessary) {
    this.necessary = necessary;
  }

  public void  fromDTO(MessageTemplateDTO templateDTO){
    if(templateDTO==null) return;
    this.shopId=templateDTO.getShopId();
    this.name=templateDTO.getName();
    this.content=templateDTO.getContent();
    this.type=templateDTO.getType();
  }

  public MessageTemplateDTO toDTO(){
    MessageTemplateDTO msgTemplateDTO=new MessageTemplateDTO();
    msgTemplateDTO.setId(this.getId());
    msgTemplateDTO.setShopId(this.getShopId());
    msgTemplateDTO.setType(this.getType());
    msgTemplateDTO.setContent(this.getContent());
    msgTemplateDTO.setContentStr(msgTemplateDTO.getContentStr());
    msgTemplateDTO.setSyncTime(this.getSyncTime());
    msgTemplateDTO.setName(this.getName());
    msgTemplateDTO.setScene(this.getScene());
    msgTemplateDTO.setNecessary(this.getNecessary());
    return msgTemplateDTO;
  }
}
