package com.bcgogo.pojox.api;

import com.bcgogo.pojox.enums.YesNo;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 2015-11-26
 * Time: 10:12
 */
public class AppUserCustomerDTO {
  private Long id;
  private String appUserNo;//手机端用户账号
   private Long customerId;//web客户id
   private Long shopId;//web客户的shopId
   private Long matchTime;//匹配时间
   private Long taskId;//app_user_customer_update_task表的id
   private Long shopVehicleId;
   private Long appVehicleId;
   private YesNo isMobileMatch;
   private YesNo isVehicleNoMatch;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getAppUserNo() {
    return appUserNo;
  }

  public void setAppUserNo(String appUserNo) {
    this.appUserNo = appUserNo;
  }

  public Long getCustomerId() {
    return customerId;
  }

  public void setCustomerId(Long customerId) {
    this.customerId = customerId;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public Long getMatchTime() {
    return matchTime;
  }

  public void setMatchTime(Long matchTime) {
    this.matchTime = matchTime;
  }

  public Long getTaskId() {
    return taskId;
  }

  public void setTaskId(Long taskId) {
    this.taskId = taskId;
  }

  public Long getShopVehicleId() {
    return shopVehicleId;
  }

  public void setShopVehicleId(Long shopVehicleId) {
    this.shopVehicleId = shopVehicleId;
  }

  public Long getAppVehicleId() {
    return appVehicleId;
  }

  public void setAppVehicleId(Long appVehicleId) {
    this.appVehicleId = appVehicleId;
  }

  public YesNo getIsMobileMatch() {
    return isMobileMatch;
  }

  public void setIsMobileMatch(YesNo isMobileMatch) {
    this.isMobileMatch = isMobileMatch;
  }

  public YesNo getIsVehicleNoMatch() {
    return isVehicleNoMatch;
  }

  public void setIsVehicleNoMatch(YesNo isVehicleNoMatch) {
    this.isVehicleNoMatch = isVehicleNoMatch;
  }
}
