package com.bcgogo.user.dto;

import com.bcgogo.enums.assistantStat.AchievementMemberType;
import com.bcgogo.enums.user.SalesManStatus;
import com.bcgogo.enums.Sex;
import com.bcgogo.enums.user.Status;
import com.bcgogo.txn.dto.assistantStat.AssistantAchievementHistoryDTO;
import com.bcgogo.utils.DateUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by IntelliJ IDEA.
 * User: zyj
 * Date: 12-2-21
 * Time: 下午2:52
 */
public class SalesManDTO {
  private static final Logger LOG = LoggerFactory.getLogger(SalesManDTO.class);
  public static final String defaultDepartment = "总经办";
  public static final String defaultEmptyDepartment = "默认部门";
  private Long id;
  private String idStr;
  private String salesManCode;
  private String name;
  private String mobile;
  private double monthTarget;
  private double monthActual;
  private int month;
  private double yearTarget;
  private double yearActual;
  private String year;
  private SalesManStatus status;
  private String statusStr;
  private String statusValue;
  private Long shopId;
  private Long agentId;
  private String address;
  private String qq;
  private String email;
  private Sex sex;
  private String sexStr;

  @Deprecated
  private String department;
  private String position;
  private String identityCard;
  private Double salary;   //基本工资
  private Double allowance; //津贴
  private Long careerDate;  //入职日期
  private String careerDateStr;
  private String memo;
  private Long departmentId;
  private String departmentIdStr;  //部门
  private String departmentName;
  private String userGroupName;
  private String userGroupIdStr;  //用户组 （职位）
  private String username;
  private String userNo;
  private Long userId;
  private String userIdStr;
  private Status userStatus;
  private String userStatusStr;
  private String userStatusValue;
  private Long occupationId;
  private Long userGroupId;//职位暂时使用用户组id
  private String userType;


  //员工业绩配置相关
  private Double washBeautyAchievement; //员工业绩配置 洗车每次的提成金额
  private Double serviceAchievement;//员工业绩配置 施工每次的提成比率
  private Double salesAchievement; //员工业绩配置 销售、销售退货每次的提成比率
  private Double salesProfitAchievement;      //员工业绩配置 销售利润货每次的提成比率

  private AchievementMemberType memberNewType;//员工工业绩配置 购卡 统计类型
  private Double memberNewAchievement;   //员工业绩配置 购卡 提成金额

  private AchievementMemberType memberRenewType;//员工业绩配置 续卡 统计类型
  private Double memberReNewAchievement; //员工业绩配置 续卡 提成金额




  public String getSexStr() {
    return sexStr;
  }

  public void setSexStr(String sexStr) {
     this.sexStr = sexStr;
    if("MALE".equals(sexStr)) {
      this.sex = Sex.parseName("男");
    } else if("FEMALE".equals(sexStr)) {
      this.sex = Sex.parseName("女");
    }

  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    if (userId != null) this.setUserIdStr(userId.toString());
    this.userId = userId;
  }

  public String getUserIdStr() {
    return userIdStr;
  }

  public void setUserIdStr(String userIdStr) {
    this.userIdStr = userIdStr;
  }

  public String getQq() {
    return qq;
  }

  public void setQq(String qq) {
    this.qq = qq;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getUserStatusValue() {
    return userStatusValue;
  }

  public void setUserStatusValue(String userStatusValue) {
    this.userStatusValue = userStatusValue;
  }

  public Status getUserStatus() {
    return userStatus;
  }

  public void setUserStatus(Status userStatus) {
    if (userStatus != null) this.setStatusValue(userStatus.getValue());
    this.userStatus = userStatus;
  }

  public String getUserStatusStr() {
    return userStatusStr;
  }

  public void setUserStatusStr(String userStatusStr) {
    if (StringUtils.isNotBlank(userStatusStr)) {
      this.setUserStatus(Status.valueOf(userStatusStr));
    }
    this.userStatusStr = userStatusStr;
  }

  public String getUserNo() {
    return userNo;
  }

  public void setUserNo(String userNo) {
    this.userNo = userNo;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getStatusStr() {
    return statusStr;
  }

  public void setStatusStr(String statusStr) {
    if (StringUtils.isNotBlank(statusStr)) {
      this.setStatus(SalesManStatus.valueOf(statusStr));
    }
    this.statusStr = statusStr;
  }

  public String getCareerDateStr() {
    return careerDateStr;
  }

  public void setCareerDateStr(String careerDateStr) {
    this.careerDateStr = careerDateStr;
  }

  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }

  public Sex getSex() {
    return sex;
  }

  public void setSex(String sex){
    if(sex == null || "".equals(sex)) {
      return;
    }
    this.sex = Sex.parseName(sex);
    setSexStr(sex);
  }

  @Deprecated
  public String getDepartment() {
    return department;
  }

  @Deprecated
  public void setDepartment(String department) {
    this.department = department;
  }

  public String getPosition() {
    return position;
  }

  public void setPosition(String position) {
    this.position = position;
  }

  public String getIdentityCard() {
    return identityCard;
  }

  public void setIdentityCard(String identityCard) {
    this.identityCard = identityCard;
  }

  public Double getSalary() {
    return salary;
  }

  public void setSalary(Double salary) {
    this.salary = salary;
  }

  public Double getAllowance() {
    return allowance;
  }

  public void setAllowance(Double allowance) {
    this.allowance = allowance;
  }

  public Long getCareerDate() {
    return careerDate;
  }

  public void setCareerDate(Long careerDate) {
    this.setCareerDateStr(DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_DAY, careerDate));
    this.careerDate = careerDate;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public Long getAgentId() {
    return agentId;
  }

  public void setAgentId(Long agentId) {
    this.agentId = agentId;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public double getYearActual() {
    return yearActual;
  }

  public void setYearActual(double yearActual) {
    this.yearActual = yearActual;
  }

  public double getMonthActual() {
    return monthActual;
  }

  public void setMonthActual(double monthActual) {
    this.monthActual = monthActual;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
    this.idStr = String.valueOf(id);
  }

  public String getSalesManCode() {
    return salesManCode;
  }

  public void setSalesManCode(String salesManCode) {
    this.salesManCode = salesManCode;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
  }

  public double getMonthTarget() {
    return monthTarget;
  }

  public void setMonthTarget(double monthTarget) {
    this.monthTarget = monthTarget;
  }

  public int getMonth() {
    return month;
  }

  public void setMonth(int month) {
    this.month = month;
  }

  public double getYearTarget() {
    return yearTarget;
  }

  public void setYearTarget(double yearTarget) {
    this.yearTarget = yearTarget;
  }

  public String getYear() {
    return year;
  }

  public void setYear(String year) {
    this.year = year;
  }

  public String getStatusValue() {
    return statusValue;
  }

  public void setStatusValue(String statusValue) {
    this.statusValue = statusValue;
  }

  public SalesManStatus getStatus() {
    return status;
  }

  public void setStatus(SalesManStatus status) {
    if (status != null) {
      this.setStatusValue(status.getName());
    }
    this.status = status;
  }

  public String getIdStr() {
    return idStr;
  }

  public void setIdStr(String idStr) {
    this.idStr = idStr;
  }

  public Long getDepartmentId() {
    return departmentId;
  }

  public void setDepartmentId(Long departmentId) {
    if (departmentId != null) this.setDepartmentIdStr(departmentId.toString());
    this.departmentId = departmentId;
  }

  public Long getUserGroupId() {
    return userGroupId;
  }

  public void setUserGroupId(Long userGroupId) {
    if (userGroupId != null) this.setUserGroupIdStr(userGroupId.toString());
    this.userGroupId = userGroupId;
  }

  public String getUserGroupIdStr() {
    return userGroupIdStr;
  }

  public void setUserGroupIdStr(String userGroupIdStr) {
    this.userGroupIdStr = userGroupIdStr;
  }

  public String getDepartmentIdStr() {
    return departmentIdStr;
  }

  public void setDepartmentIdStr(String departmentIdStr) {
    this.departmentIdStr = departmentIdStr;
  }

  public String getDepartmentName() {
    return departmentName;
  }

  public void setDepartmentName(String departmentName) {
    this.departmentName = departmentName;
  }

  public String getUserGroupName() {
    return userGroupName;
  }

  public void setUserGroupName(String userGroupName) {
    this.userGroupName = userGroupName;
  }

  public Long getOccupationId() {
    return occupationId;
  }

  public void setOccupationId(Long occupationId) {
    this.occupationId = occupationId;
  }

  public String getUserType() {
    return userType;
  }

  public void setUserType(String userType) {
    this.userType = userType;
  }

  public Double getWashBeautyAchievement() {
    return washBeautyAchievement;
  }

  public void setWashBeautyAchievement(Double washBeautyAchievement) {
    this.washBeautyAchievement = washBeautyAchievement;
  }

  public Double getServiceAchievement() {
    return serviceAchievement;
  }

  public void setServiceAchievement(Double serviceAchievement) {
    this.serviceAchievement = serviceAchievement;
  }

  public Double getSalesAchievement() {
    return salesAchievement;
  }

  public void setSalesAchievement(Double salesAchievement) {
    this.salesAchievement = salesAchievement;
  }

  public Double getSalesProfitAchievement() {
    return salesProfitAchievement;
  }

  public void setSalesProfitAchievement(Double salesProfitAchievement) {
    this.salesProfitAchievement = salesProfitAchievement;
  }

  public AchievementMemberType getMemberNewType() {
    return memberNewType;
  }

  public void setMemberNewType(AchievementMemberType memberNewType) {
    this.memberNewType = memberNewType;
  }

  public Double getMemberNewAchievement() {
    return memberNewAchievement;
  }

  public void setMemberNewAchievement(Double memberNewAchievement) {
    this.memberNewAchievement = memberNewAchievement;
  }

  public AchievementMemberType getMemberRenewType() {
    return memberRenewType;
  }

  public void setMemberRenewType(AchievementMemberType memberRenewType) {
    this.memberRenewType = memberRenewType;
  }

  public Double getMemberReNewAchievement() {
    return memberReNewAchievement;
  }

  public void setMemberReNewAchievement(Double memberReNewAchievement) {
    this.memberReNewAchievement = memberReNewAchievement;
  }


  public SalesManDTO fromDTO(AssistantAchievementHistoryDTO historyDTO) {
    if (historyDTO == null) {
      return this;
    }
    this.setWashBeautyAchievement(historyDTO.getWashBeautyAchievement());
    this.setServiceAchievement(historyDTO.getServiceAchievement());
    this.setSalesAchievement(historyDTO.getSalesAchievement());
    this.setSalesProfitAchievement(historyDTO.getSalesProfitAchievement());

    this.setMemberNewType(historyDTO.getMemberNewType());
    this.setMemberNewAchievement(historyDTO.getMemberNewAchievement());
    this.setMemberRenewType(historyDTO.getMemberRenewType());
    this.setMemberReNewAchievement(historyDTO.getMemberReNewAchievement());
    return this;
  }

  @Override
  public String toString() {
    return "SalesManDTO{" +
        "id=" + id +
        ", idStr='" + idStr + '\'' +
        ", salesManCode='" + salesManCode + '\'' +
        ", name='" + name + '\'' +
        ", mobile='" + mobile + '\'' +
        ", monthTarget=" + monthTarget +
        ", monthActual=" + monthActual +
        ", month=" + month +
        ", yearTarget=" + yearTarget +
        ", yearActual=" + yearActual +
        ", year='" + year + '\'' +
        ", status=" + status +
        ", statusStr='" + statusStr + '\'' +
        ", statusValue='" + statusValue + '\'' +
        ", shopId=" + shopId +
        ", agentId=" + agentId +
        ", address='" + address + '\'' +
        ", qq='" + qq + '\'' +
        ", email='" + email + '\'' +
        ", sex=" + sex +
        ", sexStr='" + sexStr + '\'' +
        ", department='" + department + '\'' +
        ", position='" + position + '\'' +
        ", identityCard='" + identityCard + '\'' +
        ", salary=" + salary +
        ", allowance=" + allowance +
        ", careerDate=" + careerDate +
        ", careerDateStr='" + careerDateStr + '\'' +
        ", memo='" + memo + '\'' +
        ", departmentId=" + departmentId +
        ", departmentIdStr='" + departmentIdStr + '\'' +
        ", departmentName='" + departmentName + '\'' +
        ", userGroupName='" + userGroupName + '\'' +
        ", userGroupIdStr='" + userGroupIdStr + '\'' +
        ", username='" + username + '\'' +
        ", userNo='" + userNo + '\'' +
        ", userId=" + userId +
        ", userIdStr='" + userIdStr + '\'' +
        ", userStatus=" + userStatus +
        ", userStatusStr='" + userStatusStr + '\'' +
        ", userStatusValue='" + userStatusValue + '\'' +
        ", occupationId=" + occupationId +
        ", userGroupId=" + userGroupId +
        ", userType='" + userType + '\'' +
        ", washBeautyAchievement=" + washBeautyAchievement +
        ", serviceAchievement=" + serviceAchievement +
        ", salesAchievement=" + salesAchievement +
        ", salesProfitAchievement=" + salesProfitAchievement +
        ", memberNewType=" + memberNewType +
        ", memberNewAchievement=" + memberNewAchievement +
        ", memberRenewType=" + memberRenewType +
        ", memberReNewAchievement=" + memberReNewAchievement +
        '}';
  }
}
