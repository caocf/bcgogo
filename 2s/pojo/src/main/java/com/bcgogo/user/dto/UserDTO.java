package com.bcgogo.user.dto;

import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.enums.YesNo;
import com.bcgogo.enums.user.DepartmentResponsibility;
import com.bcgogo.enums.user.Status;
import com.bcgogo.enums.user.UserType;
import com.bcgogo.user.UserRequest;
import com.bcgogo.utils.DateUtil;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;

public class UserDTO implements Serializable {
  static final long serialVersionUID = -7588980448693010399L;

  public UserDTO() {
  }

  private Long id;
  private String idStr;
  private Long shopId;
  private String shopIdStr;
  private String userNo;
  private String userName;
  private String password;
  private String name;
  private Long loginTimes;
  private Long lastTime;
  private String email;
  private String mobile;
  private String qq;
  private String memo;
  private Long syncTime;
  private Status statusEnum;
  private String status;
  private String statusValue;
  private Long userGroupId;
  private String userGroupName;
  private String userGroupIdStr;
  private String passwordWithoutEncrypt;
  private Long departmentId;//部门
  private String departmentName;
  private Long occupationId;//职位
  private String occupationName;
  private Long creationDate;
  private String createDateStr;
  private DepartmentResponsibility departmentResponsibility;//部门责任枚举
  private String departmentResponsibilityStr;//部门责任枚举
  private Long salesManId;
  private UserType userType;
  private YesNo isFinishUserGuide;
  private YesNo hasUserGuide;
  private String deviceToken;//ios 用户的deviceToken
  private String umDeviceToken;//安卓 用户的友盟deviceToken

  public UserDTO(Long id, Long shopId, String userNo, String userName, String password, String name, Long loginTimes, Long lastTime, String email, String mobile, String qq, Integer state, String memo, Long syncTime, Status status, Long userGroupId) {
    this.id = id;
    this.idStr = String.valueOf(id);
    this.shopId = shopId;
    this.userNo = userNo;
    this.userName = userName;
    this.password = password;
    this.name = name;
    this.loginTimes = loginTimes;
    this.lastTime = lastTime;
    this.email = email;
    this.mobile = mobile;
    this.qq = qq;
    this.memo = memo;
    this.syncTime = syncTime;
    this.statusEnum = status;
    this.userGroupId = userGroupId;
    this.userGroupIdStr = String.valueOf(userGroupId);
  }

  public SalesManDTO toSalesManDTO() {
    SalesManDTO salesManDTO = new SalesManDTO();
    salesManDTO.setShopId(this.getShopId());
    salesManDTO.setUsername(this.getUserName());
    salesManDTO.setName(this.getName());
    salesManDTO.setEmail(this.getEmail());
    salesManDTO.setMobile(this.getMobile());
    salesManDTO.setQq(this.getQq());
    salesManDTO.setMemo(this.getMemo());
    salesManDTO.setDepartmentId(this.getDepartmentId());
    salesManDTO.setUserGroupId(this.getUserGroupId());
    return salesManDTO;
  }

  public UserDTO(UserRequest request) {
    this.setShopId(request.getShopId());
    this.setUserNo(request.getUserNo());
    this.setUserName(request.getUserName());
    this.setPassword(request.getPassword());
    this.setName(request.getName());
    this.setLoginTimes(request.getLoginTimes());
    this.setLastTime(request.getLastTime());
    this.setEmail(request.getEmail());
    this.setQq(request.getQq());
    this.setMemo(request.getMemo());
  }

  public UserDTO fromShopDTO(ShopDTO shopDTO) {
    UserDTO userDTO = new UserDTO();
    userDTO.setShopId(shopDTO.getId());
    userDTO.setShopId(shopDTO.getId());
    userDTO.setName(shopDTO.getStoreManager());
    userDTO.setUserName(shopDTO.getLegalRep());
    userDTO.setEmail(shopDTO.getEmail());
    userDTO.setQq(shopDTO.getQq());
    userDTO.setMobile(shopDTO.getStoreManagerMobile());
    return userDTO;
  }

  public boolean isNeedToPushMessage(){
    return StringUtils.isNotBlank(getUmDeviceToken()) || StringUtils.isNotBlank(getDeviceToken());
  }

  public YesNo getHasUserGuide() {
    return hasUserGuide;
  }

  public void setHasUserGuide(YesNo hasUserGuide) {
    this.hasUserGuide = hasUserGuide;
  }

  public YesNo getFinishUserGuide() {
    return isFinishUserGuide;
  }

  public void setFinishUserGuide(YesNo finishUserGuide) {
    isFinishUserGuide = finishUserGuide;
  }

  public Long getSalesManId() {
    return salesManId;
  }

  public void setSalesManId(Long salesManId) {
    this.salesManId = salesManId;
  }

  public DepartmentResponsibility getDepartmentResponsibility() {
    return departmentResponsibility;
  }

  public void setDepartmentResponsibility(DepartmentResponsibility departmentResponsibility) {
    this.departmentResponsibility = departmentResponsibility;
  }

  public String getDepartmentResponsibilityStr() {
    return departmentResponsibilityStr;
  }

  public void setDepartmentResponsibilityStr(String departmentResponsibilityStr) {
    if (StringUtils.isNotBlank(departmentResponsibilityStr)) {
      this.setDepartmentResponsibility(DepartmentResponsibility.valueOf(departmentResponsibilityStr));
      this.departmentResponsibilityStr = departmentResponsibilityStr;
    }
  }

  public String getStatusValue() {
    return statusValue;
  }

  public void setStatusValue(String statusValue) {
    this.statusValue = statusValue;
  }

  public String getUserGroupName() {
    return userGroupName;
  }

  public void setUserGroupName(String userGroupName) {
    this.userGroupName = userGroupName;
  }

  public Long getCreationDate() {
    return creationDate;
  }

  public void setCreationDate(Long creationDate) {
    this.setCreateDateStr(DateUtil.convertDateLongToString(creationDate));
    this.creationDate = creationDate;
  }

  public String getCreateDateStr() {
    return createDateStr;
  }

  public void setCreateDateStr(String createDateStr) {
    this.createDateStr = createDateStr;
  }

  public Long getId() {
    return this.id;
  }

  public void setId(Long id) {
    this.idStr = String.valueOf(id);
    this.id = id;
  }

  public String getShopIdStr() {
    return shopIdStr;
  }

  public void setShopIdStr(String shopIdStr) {
    this.shopIdStr = shopIdStr;
  }

  public String getIdStr() {
    return idStr;
  }

  public void setIdStr(String idStr) {
    this.idStr = idStr;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    if (shopId != null) shopIdStr = shopId.toString();
    this.shopId = shopId;
  }

  public String getUserNo() {
    return userNo;
  }

  public void setUserNo(String userNo) {
    this.userNo = userNo;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    if (StringUtils.isBlank(userName)) {
      this.userName = null;
    } else {
    this.userName = userName;
  }
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Long getLoginTimes() {
    return loginTimes;
  }

  public void setLoginTimes(Long loginTimes) {
    this.loginTimes = loginTimes;
  }

  public Long getLastTime() {
    return lastTime;
  }

  public void setLastTime(Long lastTime) {
    this.lastTime = lastTime;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    if (StringUtils.isBlank(email)) {
      this.email = null;
    } else {
    this.email = email;
  }
  }

  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    if (StringUtils.isBlank(mobile)) {
      this.mobile = null;
    } else {
    this.mobile = mobile;
  }
  }

  public String getQq() {
    return qq;
  }

  public void setQq(String qq) {
    if (StringUtils.isBlank(qq)) {
      this.qq = null;
    } else {
    this.qq = qq;
  }
  }


  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    if (StringUtils.isBlank(memo)) {
      this.memo = null;
    } else {
    this.memo = memo;
  }
  }


  public String getPasswordWithoutEncrypt() {
    return passwordWithoutEncrypt;
  }

  public void setPasswordWithoutEncrypt(String passwordWithoutEncrypt) {
    this.passwordWithoutEncrypt = passwordWithoutEncrypt;
  }

  public Long getUserGroupId() {
    return userGroupId;
  }

  public void setUserGroupId(Long userGroupId) {
    this.userGroupIdStr = String.valueOf(userGroupId);
    this.userGroupId = userGroupId;
  }

  public Long getSyncTime() {
    return syncTime;
  }

  public void setSyncTime(Long syncTime) {
    this.syncTime = syncTime;
  }

  public String getStatus() {
    return status;
  }

  public Status getStatusEnum() {
    return statusEnum;
  }

  public void setStatusEnum(Status statusEnum) {
    if (statusEnum != null) this.setStatusValue(statusEnum.getValue());
    this.statusEnum = statusEnum;
  }

  public void setStatus(String status) {
    if (StringUtils.isNotBlank(status)) {
      this.setStatusEnum(Status.valueOf(status));
  }
  }

  public String getUserGroupIdStr() {
    return userGroupIdStr;
  }

  public void setUserGroupIdStr(String userGroupIdStr) {
    this.userGroupIdStr = userGroupIdStr;
  }

  public Long getDepartmentId() {
    return departmentId;
  }

  public void setDepartmentId(Long departmentId) {
    this.departmentId = departmentId;
  }

  public String getDepartmentName() {
    return departmentName;
  }

  public void setDepartmentName(String departmentName) {
    this.departmentName = departmentName;
  }

  public Long getOccupationId() {
    return occupationId;
  }

  public void setOccupationId(Long occupationId) {
    this.occupationId = occupationId;
  }

  public String getOccupationName() {
    return occupationName;
  }

  public void setOccupationName(String occupationName) {
    this.occupationName = occupationName;
  }

  public UserType getUserType() {
    return userType;
  }

  public void setUserType(UserType userType) {
    this.userType = userType;
  }

  public String getDeviceToken() {
    return deviceToken;
  }

  public void setDeviceToken(String deviceToken) {
    this.deviceToken = deviceToken;
  }

  public String getUmDeviceToken() {
    return umDeviceToken;
  }

  public void setUmDeviceToken(String umDeviceToken) {
    this.umDeviceToken = umDeviceToken;
  }
}
