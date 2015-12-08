package com.bcgogo.api;

import com.bcgogo.enums.app.AppUserType;
import com.bcgogo.enums.app.ValidateMsg;
import com.bcgogo.enums.user.Status;
import com.bcgogo.utils.EncryptionUtil;
import com.bcgogo.utils.RegexUtils;
import com.bcgogo.utils.StringUtil;

import java.io.Serializable;

/**
 * User: ZhangJuntao
 * Date: 13-8-8
 * Time: 下午1:51
 */
public class RegistrationDTO implements Serializable {
  //necessary
  private String userNo;        //用户账号
  private String password;      //用户密码
  //  private String email;        //用户电子邮箱
  private String mobile;       //用户手机号
  private String name;          //姓名
  private String sessionId;

  //unnecessary
  private String vehicleNo;    //用户车牌号
  private String vehicleModel; //用户车型
  private Long vehicleModelId;  //用户车型ID
  private String vehicleBrand;  //用户车辆品牌信息
  private Long shopId;           //为用户安装注册APP的店面ID
  private String shopEmployee;  //为用户安装注册APP的店面员工
  private Long vehicleBrandId;  //用户车辆品牌ID
  private Double nextMaintainMileage;  //下次保养里程数
  private Long nextMaintainTime;  //下次保养里程数
  private Long nextInsuranceTime;
  private Long nextExamineTime;  //下次验车时间
  private Double currentMileage;  //当前里程数

  private LoginDTO loginInfo;

  private AppUserType appUserType = AppUserType.BLUE_TOOTH; //用户类型


  public RegistrationDTO() {
  }

  public RegistrationDTO(String userNo, String password, String mobile, String name) {
    this.userNo = userNo;
    this.password = password;
    this.mobile = mobile;
    this.name = name;
  }

  public String validate() {
    if (StringUtil.isEmpty(userNo)) {
      return ValidateMsg.APP_USER_NO_EMPTY.getValue();
    } else if (userNo.length() < 5) {
      return ValidateMsg.APP_USER_NO_TOO_SHORT.getValue();
    } else if (userNo.length() > 50) {
      return ValidateMsg.APP_USER_NO_TOO_LONG.getValue();
    } else if (!RegexUtils.isMobile(userNo) && !RegexUtils.isVehicleNo(userNo)) {
      return ValidateMsg.APP_USER_NO_ILLEGAL.getValue();
    }
    if (StringUtil.isEmpty(password)) {
      return ValidateMsg.PASSWORD_EMPTY.getValue();
    }else if (password.length() > 20) {
      return ValidateMsg.PASSWORD_TOO_LONG.getValue();
    }
//    if (StringUtil.isEmpty(mobile)) {
//      return ValidateMsg.MOBILE_EMPTY.getValue();
//    } else if (RegexUtils.isNotMobile(mobile)) {
//      return ValidateMsg.MOBILE_ILLEGAL.getValue();
//    }
    if (StringUtil.isNotEmpty(mobile) && RegexUtils.isNotMobile(mobile)) {
      return ValidateMsg.MOBILE_ILLEGAL.getValue();
    }
    if (StringUtil.isEmpty(name)) {
      setName(getUserNo());
    }
    if (StringUtil.isNotEmpty(vehicleNo)) {
      if (!RegexUtils.isVehicleNo(vehicleNo)) {
        return ValidateMsg.VEHICLE_NO_ILLEGAL.getValue();
      }
    }
    if (loginInfo == null) {
      return ValidateMsg.APP_USER_LOGIN_INFO_EMPTY.getValue();
    } else {
      loginInfo.setUserNo(getUserNo());
      loginInfo.setPassword(getPassword());
      String result = loginInfo.validate();
      if (!loginInfo.isSuccess(result)) {
        return result;
      }
    }
    return "";
  }

  public void computeMD5() {
    setPassword(EncryptionUtil.computeMD5Improved(getPassword()));
  }

  public AppVehicleDTO toAppVehicleDTO() {
    AppVehicleDTO dto = new AppVehicleDTO();
    dto.setUserNo(this.getUserNo());
    dto.setVehicleNo(this.getVehicleNo());
    dto.setVehicleBrand(this.getVehicleBrand());
    dto.setVehicleBrandId(this.getVehicleBrandId());
    dto.setVehicleModel(this.getVehicleModel());
    dto.setVehicleModelId(this.getVehicleModelId());
    dto.setNextExamineTime(this.getNextExamineTime());
    dto.setNextMaintainMileage(this.getNextMaintainMileage());
    dto.setNextInsuranceTime(this.getNextInsuranceTime());
    dto.setCurrentMileage(getCurrentMileage());
    dto.setNextMaintainTime(getNextMaintainTime());
//    dto.setEmail(this.getEmail());
//    dto.setContact(this.getName());
//    dto.setMobile(this.getMobile());
    dto.setStatus(Status.active);
    return dto;
  }

  public LoginDTO toLoginDTO() {
//    loginInfo.setUserNo(this.getUserNo());
//    loginInfo.setPassword(this.getPassword());
    loginInfo.setSessionId(getSessionId());
//    loginInfo.setImageVersionEnum(ImageVersion.IV_640X960);
    return loginInfo;
  }

  public boolean isSuccess(String result) {
    return StringUtil.isEmpty(result);
  }

  public String getUserNo() {
    return userNo;
  }

  public void setUserNo(String userNo) {
    this.userNo = userNo;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

//  public String getEmail() {
//    return email;
//  }
//
//  public void setEmail(String email) {
//    this.email = email;
//  }


  public Long getNextMaintainTime() {
    return nextMaintainTime;
  }

  public void setNextMaintainTime(Long nextMaintainTime) {
    this.nextMaintainTime = nextMaintainTime;
  }

  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getVehicleNo() {
    return vehicleNo;
  }

  public void setVehicleNo(String vehicleNo) {
    this.vehicleNo = vehicleNo;
  }

  public String getVehicleModel() {
    return vehicleModel;
  }

  public void setVehicleModel(String vehicleModel) {
    this.vehicleModel = vehicleModel;
  }

  public Long getVehicleModelId() {
    return vehicleModelId;
  }

  public void setVehicleModelId(Long vehicleModelId) {
    this.vehicleModelId = vehicleModelId;
  }

  public String getVehicleBrand() {
    return vehicleBrand;
  }

  public void setVehicleBrand(String vehicleBrand) {
    this.vehicleBrand = vehicleBrand;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public String getShopEmployee() {
    return shopEmployee;
  }

  public void setShopEmployee(String shopEmployee) {
    this.shopEmployee = shopEmployee;
  }

  public Long getVehicleBrandId() {
    return vehicleBrandId;
  }

  public void setVehicleBrandId(Long vehicleBrandId) {
    this.vehicleBrandId = vehicleBrandId;
  }

  public Double getNextMaintainMileage() {
    return nextMaintainMileage;
  }

  public void setNextMaintainMileage(Double nextMaintainMileage) {
    this.nextMaintainMileage = nextMaintainMileage;
  }

  public Long getNextInsuranceTime() {
    return nextInsuranceTime;
  }

  public void setNextInsuranceTime(Long nextInsuranceTime) {
    this.nextInsuranceTime = nextInsuranceTime;
  }

  public Long getNextExamineTime() {
    return nextExamineTime;
  }

  public void setNextExamineTime(Long nextExamineTime) {
    this.nextExamineTime = nextExamineTime;
  }

  public String getSessionId() {
    return sessionId;
  }

  public void setSessionId(String sessionId) {
    this.sessionId = sessionId;
  }

  public Double getCurrentMileage() {
    return currentMileage;
  }

  public void setCurrentMileage(Double currentMileage) {
    this.currentMileage = currentMileage;
  }

  @Override
  public String toString() {
    return "RegistrationDTO{" +
        "userNo='" + userNo + '\'' +
        ", password='" + password + '\'' +
        ", mobile='" + mobile + '\'' +
        ", name='" + name + '\'' +
        ", sessionId='" + sessionId + '\'' +
        ", vehicleNo='" + vehicleNo + '\'' +
        ", vehicleModel='" + vehicleModel + '\'' +
        ", vehicleModelId=" + vehicleModelId +
        ", vehicleBrand='" + vehicleBrand + '\'' +
        ", shopId=" + shopId +
        ", shopEmployee='" + shopEmployee + '\'' +
        ", vehicleBrandId=" + vehicleBrandId +
        ", nextMaintainMileage=" + nextMaintainMileage +
        ", nextMaintainTime=" + nextMaintainTime +
        ", nextInsuranceTime=" + nextInsuranceTime +
        ", nextExamineTime=" + nextExamineTime +
        ", currentMileage=" + currentMileage +
        ", loginInfo=" + loginInfo +
        '}';
  }

  public LoginDTO getLoginInfo() {
    return loginInfo;
  }

  public void setLoginInfo(LoginDTO loginInfo) {
    this.loginInfo = loginInfo;
  }


  public AppUserType getAppUserType() {
    return appUserType;
  }

  public void setAppUserType(AppUserType appUserType) {
    this.appUserType = appUserType;
  }
}
