package com.bcgogo.api;

import com.bcgogo.enums.CustomerStatus;
import com.bcgogo.enums.DataKind;
import com.bcgogo.enums.RelationTypes;
import com.bcgogo.enums.app.AppUserType;
import com.bcgogo.user.dto.ContactDTO;
import com.bcgogo.user.dto.CustomerDTO;
import com.bcgogo.utils.RegexUtils;
import com.bcgogo.utils.StringUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 手机端用户信息
 * Created with IntelliJ IDEA.
 * User: lw
 * Date: 13-8-19
 * Time: 下午2:45
 * To change this template use File | Settings | File Templates.
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
  private DataKind dataKind;
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

  public String validate() {
    if (StringUtil.isEmpty(userNo)) {
      return "用户账号不能为空";
    }
    if (StringUtil.isEmpty(name)) {
      return "用户名不能为空";
    }
    return "";
  }

  public String validateUpdatePassword() {
    if (StringUtil.isEmpty(userNo)) {
      return "用户账号不能为空";
    }
    if (StringUtil.isEmpty(this.getOldPassword())) {
      return "请输入旧密码";
    } else if (this.getOldPassword().length() > 20) {
      return "旧密码最多20个字";
    }
    if (StringUtil.isEmpty(this.getNewPassword())) {
      return "请输入新密码";
    } else if (this.getNewPassword().length() > 20) {
      return "新密码最多20个字";
    }
    if (this.getOldPassword().equals(this.getNewPassword())) {
      return "确认新密码与旧密码相同，请重新输入";
    }
//    if (StringUtil.isEmpty(mobile)) {
//      return "你手机号为空，请联系客服";
//    }
    return "";
  }

  public String validateUpdateUserInfo() {
    if (StringUtil.isEmpty(userNo)) {
      return "用户账号不能为空";
    }
    if (StringUtil.isEmpty(this.getName())) {
      return "姓名不能为空";
    } else if (this.getName().length() > 20) {
      return "姓名最多20个字";
    }
    if (StringUtil.isNotEmpty(this.getMobile()) && RegexUtils.isNotMobile(this.getMobile())) {
      return "手机号格式错误，请重新输入";
    }
    return "";
  }


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

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("AppUserDTO{");
    sb.append("id=").append(id);
    sb.append(", userNo='").append(userNo).append('\'');
    sb.append(", mobile='").append(mobile).append('\'');
    sb.append(", password='").append(password).append('\'');
    sb.append(", oldPassword='").append(oldPassword).append('\'');
    sb.append(", newPassword='").append(newPassword).append('\'');
    sb.append(", email='").append(email).append('\'');
    sb.append(", name='").append(name).append('\'');
    sb.append(", vehicleNo='").append(vehicleNo).append('\'');
    sb.append(", vehicleModel='").append(vehicleModel).append('\'');
    sb.append(", vehicleModelId=").append(vehicleModelId);
    sb.append(", vehicleBrand='").append(vehicleBrand).append('\'');
    sb.append(", vehicleBrandId=").append(vehicleBrandId);
    sb.append(", nextMaintainMileage=").append(nextMaintainMileage);
    sb.append(", nextExamineTime=").append(nextExamineTime);
    sb.append(", nextInsuranceTime=").append(nextInsuranceTime);
    sb.append(", lastExpenseShopId=").append(lastExpenseShopId);
    sb.append(", registrationShopId=").append(registrationShopId);
    sb.append(", recommendShopId=").append(recommendShopId);
    sb.append(", recommendShopEmployee='").append(recommendShopEmployee).append('\'');
    sb.append(", currentMileage=").append(currentMileage);
    sb.append(", dataKind=").append(dataKind);
    sb.append(", appVehicleDTOs=").append(appVehicleDTOs);
    sb.append(", customerId=").append(customerId);
    sb.append(", customerContactIds=").append(customerContactIds);
    sb.append(", customerShopId=").append(customerShopId);
    sb.append('}');
    return sb.toString();
  }

  public Long getLastExpenseShopId() {
    return lastExpenseShopId;
  }

  public void setLastExpenseShopId(Long lastExpenseShopId) {
    this.lastExpenseShopId = lastExpenseShopId;
  }

  public DataKind getDataKind() {
    return dataKind;
  }

  public void setDataKind(DataKind dataKind) {
    this.dataKind = dataKind;
  }

  public CustomerDTO toCustomerDTO(Long shopId) {
    CustomerDTO dto = new CustomerDTO();
    dto.setShopId(shopId);
    dto.setName(getName());
    dto.setMobile(getMobile());
    dto.setStatus(CustomerStatus.ENABLED);
    dto.setContact(getName());
    dto.setRelationType(RelationTypes.UNRELATED);
    ContactDTO contactDTO = new ContactDTO();
    contactDTO.setName(getName());
    contactDTO.setMobile(getMobile());
    contactDTO.setMainContact(1);
    contactDTO.setDisabled(1);
    contactDTO.setShopId(shopId);
    contactDTO.setLevel(0);
    dto.setContacts(new ContactDTO[]{contactDTO});
    return dto;
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
