package com.bcgogo.pojo;

import com.bcgogo.pojo.enums.AppUserType;
import com.bcgogo.pojo.util.StringUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 15-8-19
 * Time: 下午5:29
 */
public class AppUserDTO implements Serializable {
  public final static String APP_GUEST = "app_guest";
  private Long id;
  private String userNo;//用户账号
  private String mobile;//手机号
  private String password;      //用户密码
  //用户修改密码
  private String oldPassword;//旧密码
  private String newPassword; //新密码
  private String email;//邮箱
  private String name;//客户名
  private String vehicleNo;//车牌号
  private String vehicleModel;//车型
  private Long vehicleModelId;//车型ID
  private String vehicleBrand;//车辆品牌
  private Long vehicleBrandId;//车辆品牌ID
  private Double nextMaintainMileage;//下次保养里程
  private Long nextExamineTime;        //下次验车时间
  private Long nextInsuranceTime;      //下次保险时间
  private Long lastExpenseShopId;//最后消费店铺id
  private Long registrationShopId;//注册店铺Id
  private String registrationShopName;//注册店铺
  private Long recommendShopId;//推荐店铺id
  private String recommendShopEmployee;//推荐店铺员工

  private Double currentMileage;//当前里程
  private List<AppVehicleDTO> appVehicleDTOs;

  private AppUserType appUserType; //用户类型

  private String gsmObdImeiMoblie;
  private String imei;//

  /**
   * App客户带出customer
   */
  private Long customerId;
  private List<Long> customerContactIds = new ArrayList<Long>();
  private Long customerShopId;
  private String deviceToken;//ios 用户的deviceToken
  private String umDeviceToken;//安卓 用户的友盟deviceToken



  public void setAppVehicleDTO(AppVehicleDTO dto) {
    setUserNo(dto.getUserNo());
    setVehicleNo(dto.getVehicleNo());
    setVehicleBrand(dto.getVehicleBrand());
    setVehicleBrandId(dto.getVehicleBrandId());
    setVehicleModel(dto.getVehicleModel());
    setVehicleModelId(dto.getVehicleModelId());
    setNextExamineTime(dto.getNextExamineTime());
    setNextMaintainMileage(dto.getNextMaintainMileage());
    setEmail(dto.getEmail());
    setName(dto.getContact());
    setMobile(dto.getMobile());
  }

  public boolean isSuccess(String result) {
    return StringUtil.isEmpty(result);
  }


  public String getOldPassword() {
    return oldPassword;
  }

  public void setOldPassword(String oldPassword) {
    this.oldPassword = oldPassword;
  }

  public String getNewPassword() {
    return newPassword;
  }

  public void setNewPassword(String newPassword) {
    this.newPassword = newPassword;
  }

  public String getUserNo() {
    return userNo;
  }

  public void setUserNo(String userNo) {
    this.userNo = userNo;
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

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getRecommendShopId() {
    return recommendShopId;
  }

  public void setRecommendShopId(Long recommendShopId) {
    this.recommendShopId = recommendShopId;
  }

  public String getRecommendShopEmployee() {
    return recommendShopEmployee;
  }

  public void setRecommendShopEmployee(String recommendShopEmployee) {
    this.recommendShopEmployee = recommendShopEmployee;
  }

  public Double getCurrentMileage() {
    return currentMileage;
  }

  public void setCurrentMileage(Double currentMileage) {
    this.currentMileage = currentMileage;
  }

  public String getUmDeviceToken() {
    return umDeviceToken;
  }

  public void setUmDeviceToken(String umDeviceToken) {
    this.umDeviceToken = umDeviceToken;
  }

  public Long getLastExpenseShopId() {
    return lastExpenseShopId;
  }

  public void setLastExpenseShopId(Long lastExpenseShopId) {
    this.lastExpenseShopId = lastExpenseShopId;
  }


  public List<AppVehicleDTO> getAppVehicleDTOs() {
    return appVehicleDTOs;
  }

  public void setAppVehicleDTOs(List<AppVehicleDTO> appVehicleDTOs) {
    this.appVehicleDTOs = appVehicleDTOs;
  }

  public Long getRegistrationShopId() {
    return registrationShopId;
  }

  public void setRegistrationShopId(Long registrationShopId) {
    this.registrationShopId = registrationShopId;
  }

  public String getRegistrationShopName() {
    return registrationShopName;
  }

  public void setRegistrationShopName(String registrationShopName) {
    this.registrationShopName = registrationShopName;
  }

  public Long getCustomerId() {
    return customerId;
  }

  public void setCustomerId(Long customerId) {
    this.customerId = customerId;
  }

  public Long getCustomerShopId() {
    return customerShopId;
  }

  public void setCustomerShopId(Long customerShopId) {
    this.customerShopId = customerShopId;
  }

  public List<Long> getCustomerContactIds() {
    return customerContactIds;
  }

  public void setCustomerContactIds(List<Long> customerContactIds) {
    this.customerContactIds = customerContactIds;
  }

  public AppUserType getAppUserType() {
    return appUserType;
  }

  public void setAppUserType(AppUserType appUserType) {
    this.appUserType = appUserType;
  }

  public String getGsmObdImeiMoblie() {
    return gsmObdImeiMoblie;
  }

  public void setGsmObdImeiMoblie(String gsmObdImeiMoblie) {
    this.gsmObdImeiMoblie = gsmObdImeiMoblie;
  }

  public String getImei() {
    return imei;
  }

  public void setImei(String imei) {
    this.imei = imei;
  }

  public String getDeviceToken() {
    return deviceToken;
  }

  public void setDeviceToken(String deviceToken) {
    this.deviceToken = deviceToken;
  }
}
