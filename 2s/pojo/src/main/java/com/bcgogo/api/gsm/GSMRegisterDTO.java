package com.bcgogo.api.gsm;

import com.bcgogo.api.AppVehicleDTO;
import com.bcgogo.api.LoginDTO;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.enums.app.AppUserType;
import com.bcgogo.enums.app.ObdType;
import com.bcgogo.enums.app.ValidateMsg;
import com.bcgogo.enums.user.Status;
import com.bcgogo.user.dto.VehicleDTO;
import com.bcgogo.utils.EncryptionUtil;
import com.bcgogo.utils.RegexUtils;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;

/**
 * Gsm卡手机端OBD用户注册数据封装
 * User: lw
 * Date: 14-3-11
 * Time: 上午11:18
 */
public class GSMRegisterDTO implements Serializable {

  private String mobile;       //用户手机号
  private String password;      //用户密码
  private String imei;  //GSM卡唯一标识号
  private String sessionId;
  private String userNo;

  private LoginDTO loginInfo;
  private AppUserType appUserType; //用户类型

  //unnecessary
  private String vehicleNo;    //用户车牌号
  private String vehicleModel; //用户车型
  private Long vehicleModelId;  //用户车型ID
  private String vehicleBrand;  //用户车辆品牌信息
  private Long vehicleBrandId;  //用户车辆品牌ID
  private Double nextMaintainMileage;  //下次保养里程数
  private Long nextMaintainTime;  //下次保养时间
  private Long nextInsuranceTime;
  private Long nextExamineTime;  //下次验车时间
  private Double currentMileage;  //当前里程数
  private Double maintainPeriod;//保养周期
  private Double lastMaintainMileage;//上次保养里程数

  private String vehicleVin;//手机端车辆vin码 车架号
  private String registNo; //登记证书号    车辆登记证书号
  private String engineNo; //发动机编号    发动机号

  private Double oilPrice;//油价

  private String juheCityName;//该车牌号手机端查询设置的聚合城市名称
  private String juheCityCode;//该车牌号手机端查询设置的聚合城市编码

  public GSMRegisterDTO() {

  }

  public AppVehicleDTO toAppVehicleDTO() {
    AppVehicleDTO dto = new AppVehicleDTO();
    dto.setUserNo(this.getUserNo());
    dto.setStatus(Status.active);
    if (StringUtils.isNotBlank(this.getVehicleNo())) {
      dto.setVehicleNo(this.getVehicleNo());
    }
    dto.setCurrentMileage(getCurrentMileage());
    dto.setLastMaintainMileage(this.getLastMaintainMileage());
    dto.setMaintainPeriod(this.getMaintainPeriod());
    dto.setMobile(this.getMobile());
    dto.setNextExamineTime(this.getNextExamineTime());
    dto.setNextMaintainTime(getNextMaintainTime());
    dto.setEngineNo(getEngineNo());
    dto.setVehicleVin(getVehicleVin());
    dto.setRegistNo(getRegistNo());
    dto.setJuheCityCode(getJuheCityCode());
    dto.setJuheCityName(getJuheCityName());
    return dto;
  }

  public boolean isSuccess(String result) {
    return StringUtil.isEmpty(result);
  }

  public void computeMD5() {
    setPassword(EncryptionUtil.computeMD5Improved(getPassword()));
  }


  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getImei() {
    return imei;
  }

  public void setImei(String imei) {
    this.imei = imei;
  }

  public String validateImei() {
    if (StringUtil.isNotEmpty(mobile) && RegexUtils.isNotMobile(mobile)) {
      return ValidateMsg.MOBILE_ILLEGAL.getValue();
    } else if (StringUtil.isEmpty(mobile)) {
      return ValidateMsg.MOBILE_EMPTY.getValue();
    }

    if (StringUtil.isEmpty(password)) {
      return ValidateMsg.PASSWORD_EMPTY.getValue();
    } else if (password.length() > 20) {
      return ValidateMsg.PASSWORD_TOO_LONG.getValue();
    }

    if (this.getAppUserType() == null) {
      return ValidateMsg.APP_USER_TYPE_EMPTY.getValue();
    }

    if (StringUtil.isEmpty(imei)) {
      return ValidateMsg.IMEI_EMPTY.getValue();
    } else if (password.length() > 20) {
      return ValidateMsg.IMEI_TOO_LONG.getValue();
    }

    return "";
  }


  public String validate() {

    String validateResult = validateImei();
    if (StringUtil.isNotEmpty(validateResult)) {
      return validateResult;
    }

    if (loginInfo == null) {
      return ValidateMsg.APP_USER_LOGIN_INFO_EMPTY.getValue();
    } else {
      loginInfo.setUserNo(getImei());
      loginInfo.setPassword(getPassword());
      String result = loginInfo.validate();
      if (!loginInfo.isSuccess(result)) {
        return result;
      }
    }


    return "";
  }

  public String getSessionId() {
    return sessionId;
  }

  public void setSessionId(String sessionId) {
    this.sessionId = sessionId;
  }

  public LoginDTO getLoginInfo() {
    return loginInfo;
  }

  public void setLoginInfo(LoginDTO loginInfo) {
    this.loginInfo = loginInfo;
  }

  public LoginDTO toLoginDTO() {
    loginInfo.setSessionId(getSessionId());
    return loginInfo;
  }

  public AppUserType getAppUserType() {
    return appUserType;
  }

  public void setAppUserType(AppUserType appUserType) {
    this.appUserType = appUserType;
  }

  public void setAppUserTypeByObdType(ObdType obdType) {
    AppUserType appUserType = null;
    if (ObdType.POBD.equals(obdType)) {
      appUserType = AppUserType.POBD;
    } else if (ObdType.SGSM.equals(obdType)) {
      appUserType = AppUserType.SGSM;
    } else {
      appUserType = AppUserType.GSM;
    }
    this.appUserType = appUserType;
  }


  public String getUserNo() {
    return userNo;
  }

  public void setUserNo(String userNo) {
    this.userNo = userNo;
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

  public Long getNextMaintainTime() {
    return nextMaintainTime;
  }

  public void setNextMaintainTime(Long nextMaintainTime) {
    this.nextMaintainTime = nextMaintainTime;
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

  public Double getCurrentMileage() {
    return currentMileage;
  }

  public void setCurrentMileage(Double currentMileage) {
    this.currentMileage = currentMileage;
  }

  public Double getMaintainPeriod() {
    return maintainPeriod;
  }

  public void setMaintainPeriod(Double maintainPeriod) {
    this.maintainPeriod = maintainPeriod;
  }


  public Double getLastMaintainMileage() {
    return lastMaintainMileage;
  }

  public void setLastMaintainMileage(Double lastMaintainMileage) {
    this.lastMaintainMileage = lastMaintainMileage;
  }

  public Double getOilPrice() {
    return oilPrice;
  }

  public void setOilPrice(Double oilPrice) {
    this.oilPrice = oilPrice;
  }

  public String getRegistNo() {
    return registNo;
  }

  public void setRegistNo(String registNo) {
    this.registNo = registNo;
  }

  public String getEngineNo() {
    return engineNo;
  }

  public void setEngineNo(String engineNo) {
    this.engineNo = engineNo;
  }

  public String getVehicleVin() {
    return vehicleVin;
  }

  public void setVehicleVin(String vehicleVin) {
    this.vehicleVin = vehicleVin;
  }

  public String getJuheCityName() {
    return juheCityName;
  }

  public void setJuheCityName(String juheCityName) {
    this.juheCityName = juheCityName;
  }

  public String getJuheCityCode() {
    return juheCityCode;
  }

  public void setJuheCityCode(String juheCityCode) {
    this.juheCityCode = juheCityCode;
  }

}
