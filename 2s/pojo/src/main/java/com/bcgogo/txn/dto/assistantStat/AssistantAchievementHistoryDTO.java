package com.bcgogo.txn.dto.assistantStat;

import com.bcgogo.enums.assistantStat.AchievementMemberType;

/**
 * 员工业绩提成历史（部门）
 * Created by IntelliJ IDEA.
 * User: lw
 * Date: 13-5-21
 * Time: 下午10:41
 * To change this template use File | Settings | File Templates.
 */
public class AssistantAchievementHistoryDTO extends AssistantAchievementBaseDTO{
  private Long assistantId;
  private String assistantName;
  private Long departmentId;//部门id
  private String departmentName;//部门
  private Long departmentChangeTime; //员工部门更改时间

  private Double washBeautyAchievement; //员工业绩配置 洗车每次的提成金额
  private Double serviceAchievement;//员工业绩配置 施工每次的提成比率
  private Double salesAchievement; //员工业绩配置 销售、销售退货每次的提成比率
  private Double salesProfitAchievement;      //员工业绩配置 销售利润货每次的提成比率


  private AchievementMemberType memberNewType;//员工工业绩配置 购卡 统计类型
  private Double memberNewAchievement;   //员工业绩配置 购卡 提成金额

  private AchievementMemberType memberRenewType;//员工业绩配置 续卡 统计类型
  private Double memberReNewAchievement; //员工业绩配置 续卡 提成金额

  private Long achievementChangeTime;    //员工业绩配置 更改时间


  public Long getAssistantId() {
    return assistantId;
  }

  public void setAssistantId(Long assistantId) {
    this.assistantId = assistantId;
  }

  public String getAssistantName() {
    return assistantName;
  }

  public void setAssistantName(String assistantName) {
    this.assistantName = assistantName;
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

  public Long getDepartmentChangeTime() {
    return departmentChangeTime;
  }

  public void setDepartmentChangeTime(Long departmentChangeTime) {
    this.departmentChangeTime = departmentChangeTime;
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

  public Long getAchievementChangeTime() {
    return achievementChangeTime;
  }

  public void setAchievementChangeTime(Long achievementChangeTime) {
    this.achievementChangeTime = achievementChangeTime;
  }
}
