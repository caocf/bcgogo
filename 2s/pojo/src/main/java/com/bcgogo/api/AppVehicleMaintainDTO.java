package com.bcgogo.api;

import com.bcgogo.enums.txn.message.Status;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.StringUtil;

import java.io.Serializable;

/**
 * 手机端车辆保养信息
 * Created with IntelliJ IDEA.
 * User: lw
 * Date: 13-8-19
 * Time: 上午11:10
 * To change this template use File | Settings | File Templates.
 */
public class AppVehicleMaintainDTO implements Serializable {
  private String mobile;            //车辆手机号码
  private String email;                  //电子邮件
  private String userNo;              //app用户账号
  private Double nextMaintainMileage;    //下次保养里程数
  private Long nextExamineTime;        //下次验车时间
  private Long nextInsuranceTime;      //下次保险时间
  private Long vehicleId;
  private Double currentMileage;//当前里程


  public String validate() {
    if (StringUtil.isEmpty(userNo)) {
      return "用户账号不能为空";
    }
    if (vehicleId == null) {
      return "车辆不存在";
    }
    return "";
  }

  public boolean isSuccess(String result) {
    return StringUtil.isEmpty(result);
  }

  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getUserNo() {
    return userNo;
  }

  public void setUserNo(String userNo) {
    this.userNo = userNo;
  }

  public Double getNextMaintainMileage() {
    return nextMaintainMileage;
  }

  public void setNextMaintainMileage(Double nextMaintainMileage) {
    this.nextMaintainMileage = nextMaintainMileage;
  }

  public Long getNextExamineTime() {
    return nextExamineTime;
  }

  public void setNextExamineTime(Long nextExamineTime) {
    this.nextExamineTime = nextExamineTime;
  }

  public Long getNextInsuranceTime() {
    return nextInsuranceTime;
  }

  public void setNextInsuranceTime(Long nextInsuranceTime) {
    this.nextInsuranceTime = nextInsuranceTime;
  }

  public Long getVehicleId() {
    return vehicleId;
  }

  public void setVehicleId(Long vehicleId) {
    this.vehicleId = vehicleId;
  }

  public Double getCurrentMileage() {
    return currentMileage;
  }

  public void setCurrentMileage(Double currentMileage) {
    this.currentMileage = currentMileage;
  }

  @Override
  public String toString() {
    return "AppVehicleMaintainDTO{" +
        "mobile='" + mobile + '\'' +
        ", email='" + email + '\'' +
        ", userNo='" + userNo + '\'' +
        ", nextMaintainMileage=" + nextMaintainMileage +
        ", nextExamineTime=" + nextExamineTime +
        ", nextInsuranceTime=" + nextInsuranceTime +
        ", vehicleId=" + vehicleId +
        ", currentMileage=" + currentMileage +
        '}';
  }
}
