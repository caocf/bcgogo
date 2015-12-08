package com.bcgogo.txn.dto;

import com.bcgogo.enums.ExeStatus;
import com.bcgogo.remind.dto.message.MessageDTO;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 13-7-17
 * Time: 下午2:58
 * To change this template use File | Settings | File Templates.
 */
public class PromotionMsgJobDTO {
   private Long id;
  private Long shopId;
  private Long userId;
  private String userName;
  private String promotionsIdList;
  private MessageDTO messageDTO;
  private Long exeTime;    //启动时间
  private Long expireTime;  //过期时间
  private ExeStatus exeStatus;

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

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public String getPromotionsIdList() {
    return promotionsIdList;
  }

  public void setPromotionsIdList(String promotionsIdList) {
    this.promotionsIdList = promotionsIdList;
  }

  public MessageDTO getMessageDTO() {
    return messageDTO;
  }

  public void setMessageDTO(MessageDTO messageDTO) {
    this.messageDTO = messageDTO;
  }

  public Long getExeTime() {
    return exeTime;
  }

  public void setExeTime(Long exeTime) {
    this.exeTime = exeTime;
  }

  public Long getExpireTime() {
    return expireTime;
  }

  public void setExpireTime(Long expireTime) {
    this.expireTime = expireTime;
  }

  public ExeStatus getExeStatus() {
    return exeStatus;
  }

  public void setExeStatus(ExeStatus exeStatus) {
    this.exeStatus = exeStatus;
  }
}
