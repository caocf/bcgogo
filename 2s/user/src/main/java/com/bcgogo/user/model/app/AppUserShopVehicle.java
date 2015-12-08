package com.bcgogo.user.model.app;

import com.bcgogo.api.AppUserShopVehicleDTO;
import com.bcgogo.api.ShopBindingDTO;
import com.bcgogo.enums.app.ObdUserVehicleStatus;
import com.bcgogo.model.LongIdentifier;

import javax.persistence.*;

/**
 * User: ZhangJuntao
 * Date: 13-12-10
 * Time: 下午6:24
 */
@Entity
@Table(name = "app_user_shop_vehicle")
public class AppUserShopVehicle extends LongIdentifier {
  private String appUserNo; //用户账号
  private Long shopId;
  private Long appVehicleId;
  private Long obdId;
  private ObdUserVehicleStatus status; //状态

  public AppUserShopVehicle() {
     super();
  }

  public AppUserShopVehicle(ShopBindingDTO bindingDTO) {
    setShopId(bindingDTO.getShopId());
    setAppUserNo(bindingDTO.getAppUserNo());
    setStatus(ObdUserVehicleStatus.BUNDLING);
    setAppVehicleId(bindingDTO.getVehicleId());
    setObdId(bindingDTO.getObdId());
  }

  @Column(name = "app_user_no")
  public String getAppUserNo() {
    return appUserNo;
  }

  public void setAppUserNo(String appUserNo) {
    this.appUserNo = appUserNo;
  }

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "app_vehicle_id")
  public Long getAppVehicleId() {
    return appVehicleId;
  }

  public void setAppVehicleId(Long appVehicleId) {
    this.appVehicleId = appVehicleId;
  }

  @Column(name = "obd_id")
  public Long getObdId() {
    return obdId;
  }

  public void setObdId(Long obdId) {
    this.obdId = obdId;
  }

  @Column(name = "status")
  @Enumerated(EnumType.STRING)
  public ObdUserVehicleStatus getStatus() {
    return status;
  }

  public void setStatus(ObdUserVehicleStatus status) {
    this.status = status;
  }

  public AppUserShopVehicleDTO toDTO(){
    AppUserShopVehicleDTO dto=new AppUserShopVehicleDTO();
    dto.setId(this.getId());
    dto.setAppUserNo(this.appUserNo);
    dto.setAppVehicleId(this.appVehicleId);
    dto.setObdId(this.obdId);
    dto.setShopId(this.shopId);
    dto.setStatus(this.status);
    return dto;
  }

  public void fromDTO(AppUserShopVehicleDTO dto){
    if(dto==null){
      return;
    }
    this.setId(dto.getId());
    this.setAppUserNo(dto.getAppUserNo());
    this.setAppVehicleId(dto.getAppVehicleId());
    this.setObdId(dto.getObdId());
    this.setShopId(dto.getShopId());
    this.setStatus(dto.getStatus());

  }
}
