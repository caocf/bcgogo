package com.bcgogo.notification.model;

import com.bcgogo.enums.ExeStatus;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.remind.dto.message.MessageDTO;
import com.bcgogo.txn.dto.PromotionMsgJobDTO;
import com.bcgogo.utils.JsonUtil;
import com.bcgogo.utils.StringUtil;

import javax.persistence.*;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 13-7-17
 * Time: 下午2:37
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "promotion_msg_job")
public class PromotionMsgJob extends LongIdentifier{

  private Long shopId;
  private Long userId;
  private String promotionsId;    //array
  private String messageJson;
  private Long exeTime;    //启动时间
  private Long expireTime;  //过期时间
  private ExeStatus exeStatus;

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "user_id")
  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  @Column(name = "promotions_id")
  public String getPromotionsId() {
    return promotionsId;
  }

  public void setPromotionsId(String promotionsId) {
    this.promotionsId = promotionsId;
  }


  @Column(name = "exe_time")
  public Long getExeTime() {
    return exeTime;
  }

  public void setExeTime(Long exeTime) {
    this.exeTime = exeTime;
  }

  @Column(name = "expire_time")
  public Long getExpireTime() {
    return expireTime;
  }

  public void setExpireTime(Long expireTime) {
    this.expireTime = expireTime;
  }

  @Column(name = "message_json")
  public String getMessageJson() {
    return messageJson;
  }

  public void setMessageJson(String messageJson) {
    this.messageJson = messageJson;
  }

  @Enumerated(EnumType.STRING)
  @Column(name = "exe_status")
  public ExeStatus getExeStatus() {
    return exeStatus;
  }

  public void setExeStatus(ExeStatus exeStatus) {
    this.exeStatus = exeStatus;
  }

  public PromotionMsgJobDTO toDTO(){
    PromotionMsgJobDTO jobDTO=new PromotionMsgJobDTO();
    jobDTO.setId(this.getId());
    jobDTO.setShopId(this.getShopId());
    jobDTO.setPromotionsIdList(this.getPromotionsId());
    jobDTO.setExeTime(this.getExeTime());
    jobDTO.setExpireTime(this.getExpireTime());
    if(StringUtil.isNotEmpty(this.getMessageJson()))
    jobDTO.setMessageDTO((MessageDTO)JsonUtil.jsonToObject(this.getMessageJson(), MessageDTO.class));
    return jobDTO;
  }

}
