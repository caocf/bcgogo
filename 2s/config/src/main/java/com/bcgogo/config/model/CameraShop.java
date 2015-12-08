package com.bcgogo.config.model;

import com.bcgogo.camera.CameraShopDTO;
import com.bcgogo.enums.DeletedType;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.NumberUtil;

import javax.persistence.*;
import java.text.ParseException;

/**
 * Created by IntelliJ IDEA.
 * User: zhangjie
 * Date: 14-12-25
 * To change this template use File | Settings | File Templates.
 */

@Entity
@Table(name = "camera_shop")
public class CameraShop extends LongIdentifier {
  private Long camera_id;
//  private String white_vehicle_nos;
  private Long install_date;
  private String status;
  private DeletedType deleted= DeletedType.FALSE;
  private Long shop_id;


  @Column(name = "shop_id")
  public Long getShop_id() {
    return shop_id;
  }

  public void setShop_id(Long shop_id) {
    this.shop_id = shop_id;
  }

  @Column(name = "deleted")
  @Enumerated(EnumType.STRING)
  public DeletedType getDeleted() {
    return deleted;
  }

  public void setDeleted(DeletedType deleted) {
    this.deleted = deleted;
  }

  @Column(name = "camera_id")
  public Long getCamera_id() {
    return camera_id;
  }

  public void setCamera_id(Long camera_id) {
    this.camera_id = camera_id;
  }

//  @Column(name = "white_vehicle_nos")
//  public String getWhite_vehicle_nos() {
//    return white_vehicle_nos;
//  }
//
//  public void setWhite_vehicle_nos(String white_vehicle_nos) {
//    this.white_vehicle_nos = white_vehicle_nos;
//  }

  @Column(name = "install_date")
  public Long getInstall_date() {
    return install_date;
  }

  public void setInstall_date(Long install_date) {
    this.install_date = install_date;
  }

  @Column(name = "status")
  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public CameraShop fromCameraShopDTO(CameraShopDTO cameraShopDTO) throws ParseException {
    this.setId(NumberUtil.longValue(cameraShopDTO.getId()));
    this.setInstall_date(DateUtil.convertDateStringToDateLong(DateUtil.DATE_STRING_FORMAT_ALL, cameraShopDTO.getInstall_date()));
    if("已绑定".equals(cameraShopDTO.getStatus())){
      this.setStatus("binding");
    } else{
      this.setStatus("nobinding");
    }
    this.setDeleted(cameraShopDTO.getDeleted());
    this.setCamera_id(cameraShopDTO.getCamera_id());
    this.setShop_id(cameraShopDTO.getShop_id());
    return this;
  }
}

