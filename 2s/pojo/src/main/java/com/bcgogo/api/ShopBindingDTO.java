package com.bcgogo.api;

import com.bcgogo.enums.app.ValidateMsg;
import com.bcgogo.utils.StringUtil;

/**
 * User: ZhangJuntao
 * Date: 13-12-10
 * Time: 下午5:35
 */
public class ShopBindingDTO {
  private String appUserNo;
  private Long shopId;
  private Long orgShopId;
  private Long vehicleId;
  private Long obdId;

  public ShopBindingDTO() {
  }

  public ShopBindingDTO(OBDBindingDTO obdBindingDTO, ObdDTO obdDTO, AppVehicleDTO appVehicleDTO) {
    this.setAppUserNo(obdBindingDTO.getUserNo());
    this.setObdId(obdDTO.getId());
    this.setShopId(obdDTO.getSellShopId());
    this.setVehicleId(appVehicleDTO.getVehicleId());
  }

  public ShopBindingDTO( AppVehicleDTO appVehicleDTO) {
    this.setAppUserNo(appVehicleDTO.getUserNo());
    this.setShopId(appVehicleDTO.getBindingShopId());
    this.setVehicleId(appVehicleDTO.getVehicleId());
    this.setOrgShopId(appVehicleDTO.getOrgBindingShopId());
  }

  public String validate() {
    if (StringUtil.isEmpty(appUserNo)) {
      return ValidateMsg.APP_USER_NO_EMPTY.getValue();
    }
    if (shopId == null) {
      return ValidateMsg.SHOP_ID_IS_NULL.getValue();
    }
    return "";
  }

  public ShopBindingDTO(String appUserNo, Long shopId, Long orgShopId, Long vehicleId, Long obdId) {
    this.appUserNo = appUserNo;
    this.shopId = shopId;
    this.orgShopId = orgShopId;
    this.vehicleId = vehicleId;
    this.obdId = obdId;
  }

  public boolean isSuccess(String vResult) {
    return StringUtil.isEmpty(vResult);
  }

  public String getAppUserNo() {
    return appUserNo;
  }

  public void setAppUserNo(String appUserNo) {
    this.appUserNo = appUserNo;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public Long getVehicleId() {
    return vehicleId;
  }

  public void setVehicleId(Long vehicleId) {
    this.vehicleId = vehicleId;
  }

  public Long getObdId() {
    return obdId;
  }

  public void setObdId(Long obdId) {
    this.obdId = obdId;
  }

  public Long getOrgShopId() {
    return orgShopId;
  }

  public void setOrgShopId(Long orgShopId) {
    this.orgShopId = orgShopId;
  }

  @Override
  public String toString() {
    return "ShopBindingDTO{" +
        "appUserNo='" + appUserNo + '\'' +
        ", shopId=" + shopId +
        ", orgShopId=" + orgShopId +
        ", vehicleId=" + vehicleId +
        ", obdId=" + obdId +
        '}';
  }
}
