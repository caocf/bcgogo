package com.bcgogo.notification.model;

import com.bcgogo.enums.notification.SmsType;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.notification.SmsHelper;
import com.bcgogo.notification.dto.SmsDTO;
import com.bcgogo.notification.dto.SmsIndexDTO;

import javax.persistence.*;

/**
 * 搜索短信专用,所有内容拼成一个字段
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 14-1-10
 * Time: 下午5:43
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "sms_index")
public class SmsIndex extends LongIdentifier {
  private Long shopId;
  private Long smsId;
  private SmsType smsType;
  private String content;

  public SmsIndex(){}



  public SmsIndexDTO toDTO(){
    SmsIndexDTO smsIndexDTO=new SmsIndexDTO();
    smsIndexDTO.setId(this.getId());
    smsIndexDTO.setShopId(this.getShopId());
    smsIndexDTO.setSmsId(this.getId());
    smsIndexDTO.setSmsType(this.smsType);
    smsIndexDTO.setContent(this.getContent());
    return smsIndexDTO;
  }

  public void fromDTO(SmsIndexDTO smsIndexDTO){
    this.setShopId(smsIndexDTO.getShopId());
    this.setSmsId(smsIndexDTO.getSmsId());
    this.setSmsType(smsIndexDTO.getSmsType());
    this.setContent(smsIndexDTO.getContent());
  }

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "sms_id")
  public Long getSmsId() {
    return smsId;
  }

  public void setSmsId(Long smsId) {
    this.smsId = smsId;
  }

  @Enumerated(EnumType.STRING)
  @Column(name = "sms_type")
  public SmsType getSmsType() {
    return smsType;
  }

  public void setSmsType(SmsType smsType) {
    this.smsType = smsType;
  }

  @Column(name = "content")
  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }
}
