package com.bcgogo.user.model.app;

import com.bcgogo.api.AppVehicleFaultInfoDTO;
import com.bcgogo.api.DictionaryFaultInfoDTO;
import com.bcgogo.api.VehicleFaultDTO;
import com.bcgogo.enums.app.ErrorCodeTreatStatus;
import com.bcgogo.model.LongIdentifier;
import org.apache.commons.collections.CollectionUtils;

import javax.persistence.*;
import java.util.List;

/**
 * User: ZhangJuntao
 * Date: 13-8-20
 * Time: 下午12:00
 */
@Entity
@Table(name = "app_vehicle_fault_info")
public class AppVehicleFaultInfo extends LongIdentifier {
  private Long appVehicleId;//vehicleId
  private String appUserNo;//用户账号
  private Long obdId;//obdId
  private Long reportTime;// 报告时间
  private String errorCode;// 错误编码
  private ErrorCodeTreatStatus status;//状态
  private String content;//错误信息
  private Long lastOperateTime;//最后操作时间

  public AppVehicleFaultInfo(VehicleFaultDTO dto) {
    setAppUserNo(dto.getUserNo());
    setReportTime(dto.getReportTime());
  }

  public AppVehicleFaultInfo() {
  }

  @Column(name = "app_vehicle_id")
  public Long getAppVehicleId() {
    return appVehicleId;
  }

  public void setAppVehicleId(Long appVehicleId) {
    this.appVehicleId = appVehicleId;
  }

  @Column(name = "app_user_no")
  public String getAppUserNo() {
    return appUserNo;
  }

  public void setAppUserNo(String appUserNo) {
    this.appUserNo = appUserNo;
  }

  @Column(name = "obd_id")
  public Long getObdId() {
    return obdId;
  }

  public void setObdId(Long obdId) {
    this.obdId = obdId;
  }


  @Column(name = "report_time")
  public Long getReportTime() {
    return reportTime;
  }

  public void setReportTime(Long reportTime) {
    this.reportTime = reportTime;
  }

  @Column(name = "error_code")
  public String getErrorCode() {
    return errorCode;
  }

  public void setErrorCode(String errorCode) {
    this.errorCode = errorCode;
  }

  @Column(name = "status")
  @Enumerated(EnumType.STRING)
  public ErrorCodeTreatStatus getStatus() {
    return status;
  }

  public void setStatus(ErrorCodeTreatStatus status) {
    this.status = status;
  }

  @Column(name = "content")
  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  @Column(name = "last_operate_time")
  public Long getLastOperateTime() {
    return lastOperateTime;
  }

  public void setLastOperateTime(Long lastOperateTime) {
    this.lastOperateTime = lastOperateTime;
  }

  public void setFaultInfoContent(List<DictionaryFaultInfoDTO> dictionaryFaultInfoDTOs) {
    if(CollectionUtils.isNotEmpty(dictionaryFaultInfoDTOs)){
      StringBuilder sb = new StringBuilder();
      for(DictionaryFaultInfoDTO dictionaryFaultInfoDTO : dictionaryFaultInfoDTOs){
        if(dictionaryFaultInfoDTO != null){
          if(sb.length()>0){
            sb.append("\r\n");
          }
          sb.append(dictionaryFaultInfoDTO.getDescription());
        }
      }
      setContent(sb.toString());
    }
  }

  public AppVehicleFaultInfoDTO toDTO() {
    AppVehicleFaultInfoDTO dto = new AppVehicleFaultInfoDTO();
    dto.setId(getId());
    dto.setAppUserNo(getAppUserNo());
    dto.setErrorCode(getErrorCode());
    dto.setAppVehicleId(getAppVehicleId());
    dto.setContent(getContent());
    dto.setStatus(getStatus());
    dto.setLastOperateTime(getLastOperateTime());
    dto.setObdId(getObdId());
    dto.setReportTime(getReportTime());
    return dto;
  }

}
