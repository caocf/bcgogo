package com.bcgogo.user.model.app;

import com.bcgogo.enums.app.ErrorCodeTreatStatus;
import com.bcgogo.model.LongIdentifier;

import javax.persistence.*;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 13-11-28
 * Time: 上午10:53
 */

@Entity
@Table(name = "app_vehicle_fault_info_operate_log")
public class AppVehicleFaultInfoOperateLog extends LongIdentifier {

  private Long appVehicleFaultInfoId;
  private String operateUserNo;
  private ErrorCodeTreatStatus lastStatus;
  private ErrorCodeTreatStatus newStatus;
  private Long operateTime;

  @Column(name = "app_vehicle_fault_info_id")
  public Long getAppVehicleFaultInfoId() {
    return appVehicleFaultInfoId;
  }

  public void setAppVehicleFaultInfoId(Long appVehicleFaultInfoId) {
    this.appVehicleFaultInfoId = appVehicleFaultInfoId;
  }

  @Column(name = "operate_user_no")
  public String getOperateUserNo() {
    return operateUserNo;
  }

  public void setOperateUserNo(String operateUserNo) {
    this.operateUserNo = operateUserNo;
  }

  @Column(name = "last_status")
  @Enumerated(EnumType.STRING)
  public ErrorCodeTreatStatus getLastStatus() {
    return lastStatus;
  }

  public void setLastStatus(ErrorCodeTreatStatus lastStatus) {
    this.lastStatus = lastStatus;
  }

  @Column(name = "new_status")
  @Enumerated(EnumType.STRING)
  public ErrorCodeTreatStatus getNewStatus() {
    return newStatus;
  }

  public void setNewStatus(ErrorCodeTreatStatus newStatus) {
    this.newStatus = newStatus;
  }

  @Column(name = "operate_time")
  public Long getOperateTime() {
    return operateTime;
  }

  public void setOperateTime(Long operateTime) {
    this.operateTime = operateTime;
  }

}
