package com.bcgogo.notification.dto;

import com.bcgogo.enums.MessageScene;
import com.bcgogo.enums.MessageSendNecessaryType;
import com.bcgogo.enums.MessageSwitchStatus;
import com.bcgogo.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: dongnan
 * Date: 12-7-19
 * Time: 下午3:56
 * To change this template use File | Settings | File Templates.
 */
public class MessageTemplateDTO {
  private Long id;
  private String idStr;
  private Long shopId;
  private String type;
  private String typeStr;
  private String content;
  private String contentStr;
  private Long syncTime;
  private String name;
  private MessageScene scene;
  private MessageSwitchStatus status;
  private MessageSendNecessaryType necessary;
  private List<String> messageSceneList=new ArrayList<String>();
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public Long getSyncTime() {
    return syncTime;
  }

  public void setSyncTime(Long syncTime) {
    this.syncTime = syncTime;
  }


  public String getTypeStr() {
    return typeStr;
  }

  public void setTypeStr(String typeStr) {
    this.typeStr = typeStr;
  }

  public MessageSwitchStatus getStatus() {
    return status;
  }

  public void setStatus(MessageSwitchStatus status) {
    this.status = status;
  }

  public String getName() {
    return name;
  }

  public MessageScene getScene() {
    return scene;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setScene(MessageScene scene) {
    this.scene = scene;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
    this.idStr= StringUtil.valueOf(id);
  }

  public String getIdStr() {
    return idStr;
  }

  public void setIdStr(String idStr) {
    this.idStr = idStr;
  }

  public MessageSendNecessaryType getNecessary() {
    return necessary;
  }

  public void setNecessary(MessageSendNecessaryType necessary) {
    this.necessary = necessary;
  }

  public String getContentStr() {
    if (this.getContent()!=null&&this.getContent().length() > 20) {
      this.setContentStr(this.getContent().substring(0,20)+"...");
    } else {
      this.setContentStr(this.getContent());
    }
    return contentStr;
  }

  public void setContentStr(String contentStr) {
    this.contentStr = contentStr;
  }

  public List<String> getMessageSceneList() {
    return messageSceneList;
  }

  public void setMessageSceneList(List<String> messageSceneList) {
    this.messageSceneList = messageSceneList;
  }

}
