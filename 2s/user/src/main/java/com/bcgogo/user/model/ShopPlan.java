package com.bcgogo.user.model;

import com.bcgogo.enums.PlansRemindStatus;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.remind.dto.ShopPlanDTO;

import javax.persistence.*;

/**
 * Created by IntelliJ IDEA.
 * User: sl
 * Date: 12-4-9
 * Time: 下午2:57
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "shop_plan")
public class ShopPlan extends LongIdentifier {
  private Long shopId;
  private String remindType;
  private String content;
  private String customerIds;
  private String customerNames;
  private String customerType;
  private Long remindTime;
  private PlansRemindStatus status;
  private String contact;
  private String userInfo;
  @Enumerated(EnumType.STRING)
  @Column(name = "status")
  public PlansRemindStatus getStatus() {
    return status;
  }

  public void setStatus(PlansRemindStatus status) {
    this.status = status;
  }

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }
   @Column(name = "remind_type")
  public String getRemindType() {
    return remindType;
  }

  public void setRemindType(String remindType) {
    this.remindType = remindType;
  }
   @Column(name = "content")
  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }
   @Column(name = "customer_ids")
  public String getCustomerIds() {
    return customerIds;
  }

  public void setCustomerIds(String customerIds) {
    this.customerIds = customerIds;
  }
   @Column(name = "customer_names")
  public String getCustomerNames() {
    return customerNames;
  }

  public void setCustomerNames(String customerNames) {
    this.customerNames = customerNames;
  }
   @Column(name = "customer_type")
  public String getCustomerType() {
    return customerType;
  }

  public void setCustomerType(String customerType) {
    this.customerType = customerType;
  }
   @Column(name = "remind_time")
  public Long getRemindTime() {
    return remindTime;
  }

  public void setRemindTime(Long remindTime) {
    this.remindTime = remindTime;
  }

  @Column(name="contact")
  public String getContact() {
    return contact;
  }

  public void setContact(String contact) {
    this.contact = contact;
  }

  @Column(name="user_info")
  public String getUserInfo() {
    return userInfo;
  }

  public void setUserInfo(String userInfo) {
    this.userInfo = userInfo;
  }

  public ShopPlan(){}

  public ShopPlan fromDTO(ShopPlanDTO shopPlanDTO){
    if(shopPlanDTO == null)
      return this;
    setId(shopPlanDTO.getId());
    setRemindType(shopPlanDTO.getRemindType());
    setCustomerType(shopPlanDTO.getCustomerType());
    setContent(shopPlanDTO.getContent());
    setCustomerNames(shopPlanDTO.getCustomerNames());
    setCustomerIds(shopPlanDTO.getCustomerIds());
    setRemindTime(shopPlanDTO.getRemindTime());
    setStatus(shopPlanDTO.getStatus());
    setShopId(shopPlanDTO.getShopId());
    setContact(shopPlanDTO.getContact());
    setUserInfo(shopPlanDTO.getUserInfo());
    return this;
  }

  public ShopPlanDTO toDTO(){
    ShopPlanDTO shopPlanDTO = new ShopPlanDTO();
    shopPlanDTO.setCustomerIds(getCustomerIds());
    shopPlanDTO.setCustomerType(getCustomerType());
    shopPlanDTO.setShopId(getShopId());
    shopPlanDTO.setContent(getContent());
    shopPlanDTO.setCustomerNames(getCustomerNames());
    shopPlanDTO.setId(getId());
    shopPlanDTO.setRemindTime(getRemindTime());
    shopPlanDTO.setRemindType(getRemindType());
    shopPlanDTO.setStatus(getStatus());
    shopPlanDTO.setContact(getContact());
    shopPlanDTO.setUserInfo(getUserInfo());
    return shopPlanDTO;
  }

  public ShopPlan(ShopPlanDTO shopPlanDTO)
  {
    if(null == shopPlanDTO)
    {
      return;
    }
    setId(shopPlanDTO.getId());
    setRemindType(shopPlanDTO.getRemindType());
    setCustomerType(shopPlanDTO.getCustomerType());
    setContent(shopPlanDTO.getContent());
    setCustomerNames(shopPlanDTO.getCustomerNames());
    setCustomerIds(shopPlanDTO.getCustomerIds());
    setRemindTime(shopPlanDTO.getRemindTime());
    setStatus(shopPlanDTO.getStatus());
    setShopId(shopPlanDTO.getShopId());
    setContact(shopPlanDTO.getContact());
    setUserInfo(shopPlanDTO.getUserInfo());
  }
}
