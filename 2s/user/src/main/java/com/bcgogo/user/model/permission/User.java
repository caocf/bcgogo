package com.bcgogo.user.model.permission;

import com.bcgogo.enums.YesNo;
import com.bcgogo.enums.user.DepartmentResponsibility;
import com.bcgogo.enums.user.Status;
import com.bcgogo.enums.user.UserType;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.user.dto.UserDTO;

import javax.persistence.*;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-6-11
 * Time: 下午8:52
 */
@Entity
@Table(name = "user")
public class User extends LongIdentifier {
  private Long shopId;
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
  private Status status;     //状态
  private Long departmentId;//部门
  private Long occupationId;//职位
  private Long salesManId;
  private UserType userType;
  private YesNo hasUserGuide;
  private YesNo isFinishUserGuide;
  //瞬时态
  private Long userGroupId;
  private String userGroupName;
  private String occupationName;
  private String departmentName;
  private String deviceToken;//ios 用户的deviceToken
  private String umDeviceToken;//安卓 用户的友盟deviceToken

  private DepartmentResponsibility departmentResponsibility;

  public static final String VALID_USER_NAME_REGEX = ".*";
  public static final String VALID_MOBILE_REGEX = ".*";
  public static final String VALID_EMAIL_REGEX = ".*";

  public User() {
  }


  public User(UserDTO userDTO) {
    this.setId(userDTO.getId());
    this.setShopId(userDTO.getShopId());
    this.setUserNo(userDTO.getUserNo());
    this.setUserName(userDTO.getUserName());
    this.setPassword(userDTO.getPassword());
    this.setName(userDTO.getName());
    this.setLoginTimes(userDTO.getLoginTimes());
    this.setLastTime(userDTO.getLastTime());
    this.setEmail(userDTO.getEmail());
    this.setMobile(userDTO.getMobile());
    this.setQq(userDTO.getQq());
    this.setDepartmentId(userDTO.getDepartmentId());
    this.setDepartmentName(userDTO.getDepartmentName());
    this.setOccupationId(userDTO.getOccupationId());
    this.setOccupationName(userDTO.getOccupationName());
    this.setUserGroupId(userDTO.getUserGroupId());
    this.setUserGroupName(userDTO.getUserGroupName());
    this.setMemo(userDTO.getMemo());
    this.setStatus(userDTO.getStatusEnum());
    this.setDepartmentResponsibility(userDTO.getDepartmentResponsibility());
    this.setSalesManId(userDTO.getSalesManId());
    this.setUserType(userDTO.getUserType());
    this.setHasUserGuide(userDTO.getHasUserGuide());
    this.setFinishUserGuide(userDTO.getFinishUserGuide());
    this.setDeviceToken(userDTO.getDeviceToken());
    this.setUmDeviceToken(userDTO.getUmDeviceToken());
  }

  public User fromDTO(UserDTO userDTO) {
    this.setId(userDTO.getId());
    this.setShopId(userDTO.getShopId());
    this.setUserNo(userDTO.getUserNo());
    this.setUserName(userDTO.getUserName());
    this.setPassword(userDTO.getPassword());
    this.setName(userDTO.getName());
    this.setLoginTimes(userDTO.getLoginTimes());
    this.setLastTime(userDTO.getLastTime());
    this.setEmail(userDTO.getEmail());
    this.setMobile(userDTO.getMobile());
    this.setQq(userDTO.getQq());
    this.setMemo(userDTO.getMemo());
    if(userDTO.getUserGroupId() != null) {
      this.setUserGroupId(userDTO.getUserGroupId());
    }
    this.setUserGroupName(userDTO.getUserGroupName());
    this.setDepartmentId(userDTO.getDepartmentId());
    this.setDepartmentName(userDTO.getDepartmentName());
    this.setOccupationId(userDTO.getOccupationId());
    this.setOccupationName(userDTO.getOccupationName());
    this.setStatus(userDTO.getStatusEnum());
    this.setDepartmentResponsibility(userDTO.getDepartmentResponsibility());
    this.setSalesManId(userDTO.getSalesManId());
    this.setUserType(userDTO.getUserType());
    this.setHasUserGuide(userDTO.getHasUserGuide());
    this.setFinishUserGuide(userDTO.getFinishUserGuide());
    this.setDeviceToken(userDTO.getDeviceToken());
    this.setUmDeviceToken(userDTO.getUmDeviceToken());
    return this;
  }

  public UserDTO toDTO() {
    UserDTO userDTO = new UserDTO();
    userDTO.setId(this.getId());
    userDTO.setShopId(this.getShopId());
    userDTO.setUserNo(this.getUserNo());
    userDTO.setUserName(this.getUserName());
    userDTO.setPassword(this.getPassword());
    userDTO.setName(this.getName());
    userDTO.setLoginTimes(this.getLoginTimes());
    userDTO.setLastTime(this.getLastTime());
    userDTO.setEmail(this.getEmail());
    userDTO.setMobile(this.getMobile());
    userDTO.setQq(this.getQq());
    userDTO.setMemo(this.getMemo());
    userDTO.setUserGroupId(this.getUserGroupId());
    userDTO.setUserGroupName(this.getUserGroupName());
    userDTO.setDepartmentId(this.getDepartmentId());
    userDTO.setDepartmentName(this.getDepartmentName());
    userDTO.setOccupationId(this.getOccupationId());
    userDTO.setOccupationName(this.getOccupationName());
    userDTO.setStatusEnum(this.getStatus());
    userDTO.setCreationDate(this.getCreationDate());
    userDTO.setDepartmentResponsibility(this.getDepartmentResponsibility());
    userDTO.setSalesManId(this.getSalesManId());
    userDTO.setUserType(this.getUserType());
    userDTO.setHasUserGuide(this.getHasUserGuide());
    userDTO.setFinishUserGuide(this.getFinishUserGuide());
    userDTO.setDeviceToken(this.getDeviceToken());
    userDTO.setUmDeviceToken(this.getUmDeviceToken());
    return userDTO;
  }

  public User(User user, Long userGroupId) {
    this.setId(user.getId());
    this.setShopId(user.getShopId());
    this.setUserNo(user.getUserNo());
    this.setUserName(user.getUserName());
    this.setPassword(user.getPassword());
    this.setName(user.getName());
    this.setLoginTimes(user.getLoginTimes());
    this.setLastTime(user.getLastTime());
    this.setEmail(user.getEmail());
    this.setMobile(user.getMobile());
    this.setQq(user.getQq());
    this.setMemo(user.getMemo());
    this.setUserGroupId(userGroupId);
    this.setDepartmentId(user.getDepartmentId());
    this.setOccupationId(user.getOccupationId());
    this.setStatus(user.getStatus());
    this.setDepartmentResponsibility(user.getDepartmentResponsibility());
    this.setSalesManId(user.getSalesManId());
    this.setUserType(user.getUserType());
    this.setHasUserGuide(user.getHasUserGuide());
    this.setFinishUserGuide(user.getFinishUserGuide());
    this.setUmDeviceToken(user.getUmDeviceToken());
    this.setDeviceToken(user.getDeviceToken());
  }

  @Enumerated(EnumType.STRING)
  @Column(name = "has_user_guide")
  public YesNo getHasUserGuide() {
    return hasUserGuide;
  }

  public void setHasUserGuide(YesNo hasUserGuide) {
    this.hasUserGuide = hasUserGuide;
  }

  @Enumerated(EnumType.STRING)
  @Column(name = "is_finish_user_guide")
  public YesNo getFinishUserGuide() {
    return isFinishUserGuide;
  }

  public void setFinishUserGuide(YesNo finishUserGuide) {
    isFinishUserGuide = finishUserGuide;
  }

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "user_no", length = 20)
  public String getUserNo() {
    return userNo;
  }

  public void setUserNo(String userNo) {
    this.userNo = userNo;
  }

  @Column(name = "user_name", length = 20)
  public String getUserName() {
    return userName;
  }

  public void setUserName(String username) {
    this.userName = username;
  }

  @Column(name = "password", length = 100)
  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  @Column(name = "name", length = 20)
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Column(name = "login_times")
  public Long getLoginTimes() {
    return loginTimes;
  }

  public void setLoginTimes(Long loginTimes) {
    this.loginTimes = loginTimes;
  }

  @Column(name = "last_time")
  public Long getLastTime() {
    return lastTime;
  }

  public void setLastTime(Long lastTime) {
    this.lastTime = lastTime;
  }

  @Column(name = "email", length = 50)
  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  @Column(name = "mobile", length = 20)
  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
  }

  @Column(name = "qq", length = 20)
  public String getQq() {
    return qq;
  }

  public void setQq(String qq) {
    this.qq = qq;
  }

  @Column(name = "memo", length = 500)
  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }

  @Enumerated(EnumType.STRING)
  @Column(name = "status")
  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  @Column(name = "department_id")
  public Long getDepartmentId() {
    return departmentId;
  }

  public void setDepartmentId(Long departmentId) {
    this.departmentId = departmentId;
  }

  @Column(name = "occupation_id")
  public Long getOccupationId() {
    return occupationId;
  }

  public void setOccupationId(Long occupationId) {
    this.occupationId = occupationId;
  }

  @Column(name = "department_responsibility")
  @Enumerated(EnumType.STRING)
  public DepartmentResponsibility getDepartmentResponsibility() {
    return departmentResponsibility;
  }

  public void setDepartmentResponsibility(DepartmentResponsibility departmentResponsibility) {
    this.departmentResponsibility = departmentResponsibility;
  }

  @Column(name = "sales_man_id")
  public Long getSalesManId() {
    return salesManId;
  }

  public void setSalesManId(Long salesManId) {
    this.salesManId = salesManId;
  }

  @Column(name = "user_type")
  @Enumerated(EnumType.STRING)
  public UserType getUserType() {
    return userType;
  }

  public void setUserType(UserType userType) {
    this.userType = userType;
  }

  @Transient
  public String getUserGroupName() {
    return userGroupName;
  }

  public void setUserGroupName(String userGroupName) {
    this.userGroupName = userGroupName;
  }

  @Transient
  public Long getUserGroupId() {
    return userGroupId;
  }

  public void setUserGroupId(Long userGroupId) {
    this.userGroupId = userGroupId;
  }

  @Transient
  public String getOccupationName() {
    return occupationName;
  }

  public void setOccupationName(String occupationName) {
    this.occupationName = occupationName;
  }

  @Transient
  public String getDepartmentName() {
    return departmentName;
  }

  public void setDepartmentName(String departmentName) {
    this.departmentName = departmentName;
  }

  @Column(name = "device_token")
  public String getDeviceToken() {
    return deviceToken;
  }

  public void setDeviceToken(String deviceToken) {
    this.deviceToken = deviceToken;
  }

  @Column(name = "um_device_token")
  public String getUmDeviceToken() {
    return umDeviceToken;
  }

  public void setUmDeviceToken(String umDeviceToken) {
    this.umDeviceToken = umDeviceToken;
  }
}
