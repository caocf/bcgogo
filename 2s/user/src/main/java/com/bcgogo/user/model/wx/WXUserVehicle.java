package com.bcgogo.user.model.wx;

import com.bcgogo.enums.DeletedType;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.wx.user.WXUserVehicleDTO;

import javax.persistence.*;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 14-9-4
 * Time: 下午5:00
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "wx_user_vehicle")
public class WXUserVehicle extends LongIdentifier{
  private String openId;
  private String vehicleNo;
  private String vin;
  private String engineNo;
  private Long province;     //省
  private Long city;          //市
  private DeletedType deleted=DeletedType.FALSE;

  public WXUserVehicleDTO toDTO(){
    WXUserVehicleDTO userVehicleDTO=new WXUserVehicleDTO();
    userVehicleDTO.setId(getId());
    userVehicleDTO.setOpenId(this.getOpenId());
    userVehicleDTO.setVehicleNo(this.getVehicleNo());
    userVehicleDTO.setVin(this.getVin());
    userVehicleDTO.setEngineNo(this.getEngineNo());
    userVehicleDTO.setProvince(getProvince());
    userVehicleDTO.setCity(getCity());
    userVehicleDTO.setDeleted(this.getDeleted());
    return userVehicleDTO;
  }

  public void fromDTO(WXUserVehicleDTO dto){
    this.setId(dto.getId());
    this.setOpenId(dto.getOpenId());
    this.setVehicleNo(dto.getVehicleNo());
    this.setVin(dto.getVin());
    this.setEngineNo(dto.getEngineNo());
    this.setProvince(dto.getProvince());
    this.setCity(dto.getCity());
    this.setDeleted(dto.getDeleted());
  }

  @Column(name = "open_id")
  public String getOpenId() {
    return openId;
  }

  public void setOpenId(String openId) {
    this.openId = openId;
  }

  @Column(name = "vehicle_no")
  public String getVehicleNo() {
    return vehicleNo;
  }

  public void setVehicleNo(String vehicleNo) {
    this.vehicleNo = vehicleNo;
  }

  @Column(name = "vin")
  public String getVin() {
    return vin;
  }

  public void setVin(String vin) {
    this.vin = vin;
  }

  @Column(name = "engine_no")
  public String getEngineNo() {
    return engineNo;
  }

  public void setEngineNo(String engineNo) {
    this.engineNo = engineNo;
  }

  @Column(name = "province")
  public Long getProvince() {
    return province;
  }

  public void setProvince(Long province) {
    this.province = province;
  }

  @Column(name = "city")
  public Long getCity() {
    return city;
  }

  public void setCity(Long city) {
    this.city = city;
  }




  @Column(name = "deleted")
  @Enumerated(EnumType.STRING)
  public DeletedType getDeleted() {
    return deleted;
  }

  public void setDeleted(DeletedType deleted) {
    this.deleted = deleted;
  }

}
