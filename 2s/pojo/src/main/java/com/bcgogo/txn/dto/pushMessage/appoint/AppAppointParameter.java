package com.bcgogo.txn.dto.pushMessage.appoint;

import org.apache.commons.lang.StringUtils;

/**
 * User: ZhangJuntao
 * Date: 13-9-12
 * Time: 上午10:51
 */
public class AppAppointParameter {
  private String appUserNo;
  private String vehicleNo;
  private Long shopId;  //接受者
  private Long appointOrderId;
  private Long applyTime;   //申请时间
  private String services;
  private String linkUrl;//点击消息url

  public AppAppointParameter() {
  }

  public AppAppointParameter(String appUserNo, String vehicleNo, Long shopId, Long appointOrderId, Long applyTime, String services, String linkUrl) {
    this.appUserNo = appUserNo;
    this.vehicleNo = vehicleNo;
    this.shopId = shopId;
    this.appointOrderId = appointOrderId;
    this.applyTime = applyTime;
    this.services = services;
    this.linkUrl = linkUrl;
  }

  public String getAppUserNo() {
    return appUserNo;
  }

  public void setAppUserNo(String appUserNo) {
    this.appUserNo = appUserNo;
  }

  public String getVehicleNo() {
    return vehicleNo;
  }

  public void setVehicleNo(String vehicleNo) {
    this.vehicleNo = vehicleNo;
  }

  public Long getApplyTime() {
    return applyTime;
  }

  public void setApplyTime(Long applyTime) {
    this.applyTime = applyTime;
  }

  public String getServices() {
    return services;
  }

  public void setServices(String services) {
    this.services = services;
  }

  public String getLinkUrl() {
    return linkUrl;
  }

  public void setLinkUrl(String linkUrl) {
    this.linkUrl = linkUrl;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public Long getAppointOrderId() {
    return appointOrderId;
  }

  public void setAppointOrderId(Long appointOrderId) {
    this.appointOrderId = appointOrderId;
  }

  public String validate() {
    if (shopId == null) return "shop id is null";
    if (appointOrderId == null) return "appoint order id is null";
    if (StringUtils.isBlank(appUserNo)) return "app user no is null";
    if (StringUtils.isBlank(services)) return "services is null";
    if (StringUtils.isBlank(getVehicleNo())) return "vehicle no is null";
    return "";
  }

}
