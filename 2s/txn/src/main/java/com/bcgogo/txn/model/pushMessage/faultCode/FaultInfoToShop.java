package com.bcgogo.txn.model.pushMessage.faultCode;

import com.bcgogo.api.DictionaryFaultInfoDTO;
import com.bcgogo.api.VehicleFaultDTO;
import com.bcgogo.enums.FaultAlertType;
import com.bcgogo.enums.YesNo;
import com.bcgogo.enums.txn.Status;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.pushMessage.faultCode.FaultInfoToShopDTO;
import com.bcgogo.user.dto.CustomerDTO;
import com.bcgogo.user.dto.VehicleDTO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import javax.persistence.*;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 14-2-10
 * Time: 上午11:41
 */
@Entity
@Table(name = "fault_info_to_shop")
public class FaultInfoToShop extends LongIdentifier {
  private Long shopId;
  private Long appVehicleFaultInfoId;
  private Long faultCodeInfoId;
  private String faultCode;
  private String faultCodeDescription;
  private String faultCodeCategory;

  private String appUserNo;
  private Long appVehicleId;
  private String vehicleNo;
  private String vehicleBrand;
  private String vehicleModel;
  private String mobile;

  private Long faultCodeReportTime;

  private YesNo isSendMessage = YesNo.NO;
  private YesNo isCreateAppointOrder = YesNo.NO;
  private Status status;

  private Long appointOrderId;
  private FaultAlertType faultAlertType;
  private String lon; //经度
  private String lat; //纬度


  public void fromDTO(FaultInfoToShopDTO dto) {
    if (dto != null) {
      setShopId(dto.getShopId());
      setAppVehicleFaultInfoId(dto.getAppVehicleFaultInfoId());
      setFaultCodeInfoId(dto.getFaultCodeInfoId());
      setFaultCode(dto.getFaultCode());
      setFaultCodeDescription(dto.getFaultCodeDescription());
      setFaultCodeCategory(dto.getFaultCodeCategory());
      setAppUserNo(dto.getAppUserNo());
      setAppVehicleId(dto.getAppVehicleId());
      setVehicleNo(dto.getVehicleNo());
      setVehicleBrand(dto.getVehicleBrand());
      setVehicleModel(dto.getVehicleModel());
      setMobile(dto.getMobile());
      setFaultCodeReportTime(dto.getFaultCodeReportTime());
      setIsSendMessage(dto.getIsSendMessage());
      setIsCreateAppointOrder(dto.getIsCreateAppointOrder());
      setStatus(dto.getStatus());
      setAppointOrderId(dto.getAppointOrderId());
      setFaultAlertType(dto.getFaultAlertType());
      setLon(dto.getLon());
      setLat(dto.getLat());
    }
  }

  public void fromVehicleDTOAndVehicleFaultDTO(VehicleDTO vehicleDTO,CustomerDTO customerDTO, VehicleFaultDTO faultDTO) {
    if (vehicleDTO != null) {
      setShopId(vehicleDTO.getShopId());
      setVehicleNo(vehicleDTO.getLicenceNo());
      setVehicleBrand(vehicleDTO.getBrand());
      setVehicleModel(vehicleDTO.getModel());
      setIsSendMessage(YesNo.NO);
      setIsCreateAppointOrder(YesNo.NO);
      setStatus(Status.ACTIVE);
      setFaultAlertType(FaultAlertType.FAULT_CODE);
    }

//    if(customerDTO != null){
//      setMobile(customerDTO.getMobile());
//    }

    if(faultDTO !=null){
      setFaultCodeReportTime(faultDTO.getReportTime() == null ? System.currentTimeMillis() : faultDTO.getReportTime());
    }

  }

  public void setFaultCodeInfo(String faultCode, List<DictionaryFaultInfoDTO> dictionaryFaultInfoDTOs) {
    if (StringUtils.isNotEmpty(faultCode)) {
      setFaultCode(faultCode);
    }
    if (CollectionUtils.isNotEmpty(dictionaryFaultInfoDTOs)) {
      StringBuilder sb = new StringBuilder();
      for (DictionaryFaultInfoDTO dictionaryFaultInfoDTO : dictionaryFaultInfoDTOs) {
        if (dictionaryFaultInfoDTO != null) {
          if (sb.length() > 0) {
            sb.append("\r\n");
          }
          sb.append(dictionaryFaultInfoDTO.getDescription());
        }
      }
      setFaultCodeDescription(sb.toString());
    }

    if (CollectionUtils.isNotEmpty(dictionaryFaultInfoDTOs)) {
      String category = null;
      for (DictionaryFaultInfoDTO dictionaryFaultInfoDTO : dictionaryFaultInfoDTOs) {
        if (dictionaryFaultInfoDTO != null) {
          if (category == null && StringUtils.isNotBlank(dictionaryFaultInfoDTO.getCategory())) {
            category = dictionaryFaultInfoDTO.getCategory();
          }
        }
      }
      setFaultCodeCategory(category);
    }
  }

  public FaultInfoToShopDTO toDTO() {
    FaultInfoToShopDTO dto = new FaultInfoToShopDTO();
    dto.setId(getId());
    dto.setShopId(getShopId());
    dto.setAppVehicleFaultInfoId(appVehicleFaultInfoId);
    dto.setFaultCodeInfoId(faultCodeInfoId);
    dto.setFaultCode(faultCode);
    dto.setFaultCodeCategory(faultCodeCategory);
    dto.setFaultCodeDescription(faultCodeDescription);
    dto.setAppUserNo(appUserNo);
    dto.setAppVehicleId(appVehicleId);
    dto.setVehicleNo(vehicleNo);
    dto.setVehicleBrand(vehicleBrand);
    dto.setVehicleModel(vehicleModel);
    dto.setMobile(mobile);
    dto.setFaultCodeReportTime(faultCodeReportTime);
    dto.setIsSendMessage(isSendMessage);
    dto.setIsCreateAppointOrder(isCreateAppointOrder);
    dto.setStatus(status);
    dto.setAppointOrderId(appointOrderId);
      dto.setFaultAlertType(faultAlertType);
      dto.setLon(lon);
      dto.setLat(lat);
    return dto;
  }

  @Column(name = "app_vehicle_fault_info_id")
  public Long getAppVehicleFaultInfoId() {
    return appVehicleFaultInfoId;
  }

  public void setAppVehicleFaultInfoId(Long appVehicleFaultInfoId) {
    this.appVehicleFaultInfoId = appVehicleFaultInfoId;
  }

  @Column(name = "fault_code_info_id")
  public Long getFaultCodeInfoId() {
    return faultCodeInfoId;
  }

  public void setFaultCodeInfoId(Long faultCodeInfoId) {
    this.faultCodeInfoId = faultCodeInfoId;
  }

  @Column(name = "fault_code")
  public String getFaultCode() {
    return faultCode;
  }

  public void setFaultCode(String faultCode) {
    this.faultCode = faultCode;
  }

  @Column(name = "fault_code_description")
  public String getFaultCodeDescription() {
    return faultCodeDescription;
  }

  public void setFaultCodeDescription(String faultCodeDescription) {
    this.faultCodeDescription = faultCodeDescription;
  }

  @Column(name = "fault_code_category")
  public String getFaultCodeCategory() {
    return faultCodeCategory;
  }

  public void setFaultCodeCategory(String faultCodeCategory) {
    this.faultCodeCategory = faultCodeCategory;
  }

  @Column(name = "app_user_no")
  public String getAppUserNo() {
    return appUserNo;
  }

  public void setAppUserNo(String appUserNo) {
    this.appUserNo = appUserNo;
  }

  @Column(name = "app_vehicle_id")
  public Long getAppVehicleId() {
    return appVehicleId;
  }

  public void setAppVehicleId(Long appVehicleId) {
    this.appVehicleId = appVehicleId;
  }

  @Column(name = "vehicle_no")
  public String getVehicleNo() {
    return vehicleNo;
  }

  public void setVehicleNo(String vehicleNo) {
    this.vehicleNo = vehicleNo;
  }

  @Column(name = "vehicle_brand")
  public String getVehicleBrand() {
    return vehicleBrand;
  }

  public void setVehicleBrand(String vehicleBrand) {
    this.vehicleBrand = vehicleBrand;
  }

  @Column(name = "vehicle_model")
  public String getVehicleModel() {
    return vehicleModel;
  }

  public void setVehicleModel(String vehicleModel) {
    this.vehicleModel = vehicleModel;
  }

  @Column(name = "mobile")
  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
  }

  @Column(name = "fault_code_report_time")
  public Long getFaultCodeReportTime() {
    return faultCodeReportTime;
  }

  public void setFaultCodeReportTime(Long faultCodeReportTime) {
    this.faultCodeReportTime = faultCodeReportTime;
  }

  @Column(name = "is_send_message")
  @Enumerated(EnumType.STRING)
  public YesNo getIsSendMessage() {
    return isSendMessage;
  }

  public void setIsSendMessage(YesNo isSendMessage) {
    this.isSendMessage = isSendMessage;
  }

  @Column(name = "is_create_appoint_order")
  @Enumerated(EnumType.STRING)
  public YesNo getIsCreateAppointOrder() {
    return isCreateAppointOrder;
  }

  public void setIsCreateAppointOrder(YesNo isCreateAppointOrder) {
    this.isCreateAppointOrder = isCreateAppointOrder;
  }

  @Column(name = "status")
  @Enumerated(EnumType.STRING)
  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "appoint_order_id")
  public Long getAppointOrderId() {
    return appointOrderId;
  }

  public void setAppointOrderId(Long appointOrderId) {
    this.appointOrderId = appointOrderId;
  }

  @Column(name = "fault_alert_type")
  @Enumerated(EnumType.STRING)
  public FaultAlertType getFaultAlertType() {
    return faultAlertType;
  }

  public void setFaultAlertType(FaultAlertType faultAlertType) {
    this.faultAlertType = faultAlertType;
  }

  @Column(name = "lon")
  public String getLon() {
    return lon;
  }

  public void setLon(String lon) {
    this.lon = lon;
  }

  @Column(name = "lat")
  public String getLat() {
    return lat;
  }

  public void setLat(String lat) {
    this.lat = lat;
  }



}
