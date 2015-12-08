package com.bcgogo.notification.dto;

import com.bcgogo.enums.notification.SmsType;
import com.bcgogo.notification.SmsHelper;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 14-1-11
 * Time: 下午11:09
 * To change this template use File | Settings | File Templates.
 */
public class SmsIndexDTO {
  private Long id;
  private Long shopId;
  private Long smsId;
  private SmsType smsType;
  private String content;

  public void fromSmsDTO(SmsDTO smsDTO){
    if(smsDTO==null) return;
    this.setSmsId(smsDTO.getId());
    this.setShopId(smsDTO.getShopId());
    this.setSmsType(smsDTO.getSmsType());
    this.setContent(SmsHelper.genSmsIndexContent(smsDTO));
  }

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

  public Long getSmsId() {
    return smsId;
  }

  public void setSmsId(Long smsId) {
    this.smsId = smsId;
  }

  public SmsType getSmsType() {
    return smsType;
  }

  public void setSmsType(SmsType smsType) {
    this.smsType = smsType;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }
}
