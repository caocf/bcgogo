package com.bcgogo.txn.model.app;

import com.bcgogo.api.EnquiryDTO;
import com.bcgogo.enums.app.EnquiryStatus;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.enquiry.ShopEnquiryDTO;

import javax.persistence.*;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 13-10-23
 * Time: 上午9:24
 */
@Entity
@Table(name = "enquiry")
public class Enquiry extends LongIdentifier {
  private String appUserNo;
  private String description; //询价单描述
  private EnquiryStatus status;  //询价单状态
  private Long billId;  //询价单关联的我的账单Id
  private Long createTime;
  private Long lastUpdateTime;
  private String appUserName;
  private String vehicleNo;
  private String appUserMobile;

  @Column(name = "app_user_no")
  public String getAppUserNo() {
    return appUserNo;
  }

  public void setAppUserNo(String appUserNo) {
    this.appUserNo = appUserNo;
  }

    @Column(name = "description")
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @Column(name = "status")
  @Enumerated(EnumType.STRING)
  public EnquiryStatus getStatus() {
    return status;
  }

  public void setStatus(EnquiryStatus status) {
    this.status = status;
  }

  @Column(name = "bill_id")
  public Long getBillId() {
    return billId;
  }

  public void setBillId(Long billId) {
    this.billId = billId;
  }

  @Column(name = "create_time")
  public Long getCreateTime() {
    return createTime;
  }

  public void setCreateTime(Long createTime) {
    this.createTime = createTime;
  }

  @Column(name = "last_update_time")
  public Long getLastUpdateTime() {
    return lastUpdateTime;
  }

  public void setLastUpdateTime(Long lastUpdateTime) {
    this.lastUpdateTime = lastUpdateTime;
  }

  @Column(name = "app_user_name")
  public String getAppUserName() {
    return appUserName;
  }

  public void setAppUserName(String appUserName) {
    this.appUserName = appUserName;
  }

  @Column(name = "vehicle_no")
  public String getVehicleNo() {
    return vehicleNo;
  }

  public void setVehicleNo(String vehicleNo) {
    this.vehicleNo = vehicleNo;
  }

  @Column(name = "app_user_mobile")
  public String getAppUserMobile() {
    return appUserMobile;
  }

  public void setAppUserMobile(String appUserMobile) {
    this.appUserMobile = appUserMobile;
  }

  public EnquiryDTO toDTO(){
    EnquiryDTO enquiryDTO = new EnquiryDTO();
    enquiryDTO.setId(getId());
    enquiryDTO.setDescription(getDescription());
    enquiryDTO.setAppUserNo(getAppUserNo());
    enquiryDTO.setBillId(getBillId());
    enquiryDTO.setCreateTime(getCreateTime());
    enquiryDTO.setLastUpdateTime(getLastUpdateTime());
    enquiryDTO.setStatus(getStatus());
    enquiryDTO.setAppUserName(getAppUserName());
    enquiryDTO.setAppUserMobile(getAppUserMobile());
    enquiryDTO.setVehicleNo(getVehicleNo());
    return enquiryDTO;
  }

  public void fromDTO(EnquiryDTO enquiryDTO){
    if(enquiryDTO != null){
      this.setId(enquiryDTO.getId());
      this.setDescription(enquiryDTO.getDescription());
      this.setAppUserNo(enquiryDTO.getAppUserNo());
      this.setBillId(enquiryDTO.getBillId());
      this.setCreateTime(enquiryDTO.getCreateTime());
      this.setLastUpdateTime(enquiryDTO.getLastUpdateTime());
      this.setStatus(enquiryDTO.getStatus());
      this.setAppUserName(enquiryDTO.getAppUserName());
      this.setAppUserMobile(enquiryDTO.getAppUserMobile());
      this.setVehicleNo(enquiryDTO.getVehicleNo());
    }
  }

  public ShopEnquiryDTO toShopEnquiryDTO() {
    ShopEnquiryDTO shopEnquiryDTO = new ShopEnquiryDTO();
    shopEnquiryDTO.setId(getId());
    return shopEnquiryDTO;
  }
}
