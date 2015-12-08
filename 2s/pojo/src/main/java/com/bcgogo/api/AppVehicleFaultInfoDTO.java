package com.bcgogo.api;

import com.bcgogo.enums.app.ErrorCodeTreatStatus;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 13-11-28
 * Time: 上午9:43
 */
public class AppVehicleFaultInfoDTO implements Serializable {
  private Long id;
  private Long appVehicleId;//vehicleId
  private String appUserNo;//用户账号
  private Long obdId;//obdId
  private Long reportTime;// 报告时间
  private String errorCode;// 错误编码
  private ErrorCodeTreatStatus status;//状态
  private String statusStr;//状态描述
  private String content;//错误信息
  private Long lastOperateTime;//最后操作时间
  private String category;    //故障分类
  private String backgroundInfo; //故障描述
  private ErrorCodeTreatStatus lastStatus;//需要操作的故障码的状态
  private String  reportTimeStr;
  private String idStr;
  private String flag;

  public String getFlag() {
    return flag;
  }

  public void setFlag(String flag) {
    this.flag = flag;
  }

  public String getIdStr() {
    return idStr;
  }

  public void setIdStr(String idStr) {
    this.idStr = idStr;
  }

  public String getReportTimeStr() {
    return reportTimeStr;
  }

  public void setReportTimeStr(String reportTimeStr) {
    this.reportTimeStr = reportTimeStr;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getAppVehicleId() {
    return appVehicleId;
  }

  public void setAppVehicleId(Long appVehicleId) {
    this.appVehicleId = appVehicleId;
  }

  public String getAppUserNo() {
    return appUserNo;
  }

  public void setAppUserNo(String appUserNo) {
    this.appUserNo = appUserNo;
  }

  public Long getObdId() {
    return obdId;
  }

  public void setObdId(Long obdId) {
    this.obdId = obdId;
  }

  public Long getReportTime() {
    return reportTime;
  }

  public void setReportTime(Long reportTime) {
    this.reportTime = reportTime;
  }

  public String getErrorCode() {
    return errorCode;
  }

  public void setErrorCode(String errorCode) {
    this.errorCode = errorCode;
  }

  public ErrorCodeTreatStatus getStatus() {
    return status;
  }

  public void setStatus(ErrorCodeTreatStatus status) {
    this.status = status;
    if(status != null){
      setStatusStr(status.getName());
    }else {
      setStatusStr("");
    }
  }

  public String getStatusStr() {
    return statusStr;
  }

  public void setStatusStr(String statusStr) {
    this.statusStr = statusStr;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public Long getLastOperateTime() {
    return lastOperateTime;
  }

  public void setLastOperateTime(Long lastOperateTime) {
    this.lastOperateTime = lastOperateTime;
  }

  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public String getBackgroundInfo() {
    return backgroundInfo;
  }

  public void setBackgroundInfo(String backgroundInfo) {
    this.backgroundInfo = backgroundInfo;
  }

  public void setCategoryBackgroundInfo(List<DictionaryFaultInfoDTO> dictionaryFaultInfoDTOs) {
    if(CollectionUtils.isNotEmpty(dictionaryFaultInfoDTOs)){
      String category = null,backgroundInfo = null;
      for(DictionaryFaultInfoDTO dictionaryFaultInfoDTO : dictionaryFaultInfoDTOs){
        if(dictionaryFaultInfoDTO != null){
          if(category == null && StringUtils.isNotBlank(dictionaryFaultInfoDTO.getCategory())){
            category = dictionaryFaultInfoDTO.getCategory();
          }
          if(backgroundInfo == null && StringUtils.isNotBlank(dictionaryFaultInfoDTO.getBackgroundInfo())){
            backgroundInfo = dictionaryFaultInfoDTO.getBackgroundInfo();
          }
        }
      }
      setBackgroundInfo(backgroundInfo);
      setCategory(category);
    }
  }

  public ErrorCodeTreatStatus getLastStatus() {
    return lastStatus;
  }

  public void setLastStatus(ErrorCodeTreatStatus lastStatus) {
    this.lastStatus = lastStatus;
  }

}
