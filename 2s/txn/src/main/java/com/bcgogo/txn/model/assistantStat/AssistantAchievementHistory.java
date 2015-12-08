package com.bcgogo.txn.model.assistantStat;

import com.bcgogo.enums.assistantStat.AchievementMemberType;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.assistantStat.AssistantAchievementHistoryDTO;

import javax.persistence.*;

/**
 * 会员业绩统计-会员员工业绩提成记录表
 * Created by IntelliJ IDEA.
 * User: lw
 * Date: 13-5-21
 * Time: 下午10:05
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "assistant_achievement_history")
public class AssistantAchievementHistory extends LongIdentifier {

  private Long shopId;
  private Long assistantId;
  private String assistantName;
  private Long departmentId;
  private String departmentName;
  private Long departmentChangeTime;//更改时间
  private Long changeUserId;//更改时用户id

  private Double washBeautyAchievement; //员工业绩配置 洗车每次的提成金额
  private Double serviceAchievement;//员工业绩配置 施工每次的提成比率
  private Double salesAchievement; //员工业绩配置 销售、销售退货每次的提成比率
  private Double salesProfitAchievement;      //员工业绩配置 销售利润货每次的提成比率

  private AchievementMemberType memberNewType;//员工工业绩配置 购卡 统计类型
  private Double memberNewAchievement;   //员工业绩配置 购卡 提成金额

  private AchievementMemberType memberRenewType;//员工业绩配置 续卡 统计类型
  private Double memberReNewAchievement; //员工业绩配置 续卡 提成金额

  private Long achievementChangeTime;    //员工业绩配置 更改时间





  public AssistantAchievementHistoryDTO toDTO() {
    AssistantAchievementHistoryDTO historyDTO = new AssistantAchievementHistoryDTO();
    historyDTO.setShopId(getShopId());
    historyDTO.setAssistantId(getAssistantId());
    historyDTO.setAssistantName(getAssistantName());
    historyDTO.setDepartmentId(getDepartmentId());
    historyDTO.setDepartmentName(getDepartmentName());
    historyDTO.setDepartmentChangeTime(getDepartmentChangeTime());
    historyDTO.setChangeUserId(getChangeUserId());

    historyDTO.setWashBeautyAchievement(getWashBeautyAchievement());
    historyDTO.setServiceAchievement(getServiceAchievement());
    historyDTO.setSalesAchievement(getSalesAchievement());
    historyDTO.setSalesProfitAchievement(getSalesProfitAchievement());

    historyDTO.setMemberNewType(getMemberNewType());
    historyDTO.setMemberNewAchievement(getMemberNewAchievement());
    historyDTO.setMemberRenewType(getMemberRenewType());
    historyDTO.setMemberReNewAchievement(getMemberReNewAchievement());

    historyDTO.setAchievementChangeTime(getAchievementChangeTime());

    return historyDTO;
  }

  public AssistantAchievementHistory fromDTO(AssistantAchievementHistoryDTO historyDTO) {
    if (historyDTO.getId() != null) {
      this.setId(getId());
    }

    this.setShopId(historyDTO.getShopId());
    this.setAssistantId(historyDTO.getAssistantId());
    this.setAssistantName(historyDTO.getAssistantName());
    this.setDepartmentId(historyDTO.getDepartmentId());
    this.setDepartmentName(historyDTO.getDepartmentName());
    this.setDepartmentChangeTime(historyDTO.getDepartmentChangeTime());
    this.setChangeUserId(historyDTO.getChangeUserId());

    this.setWashBeautyAchievement(historyDTO.getWashBeautyAchievement());
    this.setServiceAchievement(historyDTO.getServiceAchievement());
    this.setSalesAchievement(historyDTO.getSalesAchievement());
    this.setSalesProfitAchievement(historyDTO.getSalesProfitAchievement());

    this.setMemberNewType(historyDTO.getMemberNewType());
    this.setMemberNewAchievement(historyDTO.getMemberNewAchievement());
    this.setMemberRenewType(historyDTO.getMemberRenewType());
    this.setMemberReNewAchievement(historyDTO.getMemberReNewAchievement());

    this.setAchievementChangeTime(historyDTO.getAchievementChangeTime());

    return this;
  }

  @Column(name = "assistant_id")
  public Long getAssistantId() {
    return assistantId;
  }

  public void setAssistantId(Long assistantId) {
    this.assistantId = assistantId;
  }

  @Column(name = "assistant_name")
  public String getAssistantName() {
    return assistantName;
  }

  public void setAssistantName(String assistantName) {
    this.assistantName = assistantName;
  }

  @Column(name = "department_id")
  public Long getDepartmentId() {
    return departmentId;
  }

  public void setDepartmentId(Long departmentId) {
    this.departmentId = departmentId;
  }

  @Column(name = "department_name")
  public String getDepartmentName() {
    return departmentName;
  }

  public void setDepartmentName(String departmentName) {
    this.departmentName = departmentName;
  }

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "department_change_time")
  public Long getDepartmentChangeTime() {
    return departmentChangeTime;
  }

  public void setDepartmentChangeTime(Long departmentChangeTime) {
    this.departmentChangeTime = departmentChangeTime;
  }

  @Column(name = "change_user_id")
  public Long getChangeUserId() {
    return changeUserId;
  }

  public void setChangeUserId(Long changeUserId) {
    this.changeUserId = changeUserId;
  }

  @Column(name = "wash_beauty_achievement")
  public Double getWashBeautyAchievement() {
    return washBeautyAchievement;
  }

  public void setWashBeautyAchievement(Double washBeautyAchievement) {
    this.washBeautyAchievement = washBeautyAchievement;
  }

  @Column(name = "service_achievement")
  public Double getServiceAchievement() {
    return serviceAchievement;
  }

  public void setServiceAchievement(Double serviceAchievement) {
    this.serviceAchievement = serviceAchievement;
  }

  @Column(name = "sales_achievement")
  public Double getSalesAchievement() {
    return salesAchievement;
  }

  public void setSalesAchievement(Double salesAchievement) {
    this.salesAchievement = salesAchievement;
  }

  @Column(name = "sales_profit_achievement")
  public Double getSalesProfitAchievement() {
    return salesProfitAchievement;
  }

  public void setSalesProfitAchievement(Double salesProfitAchievement) {
    this.salesProfitAchievement = salesProfitAchievement;
  }

  @Column(name = "member_new_type")
  @Enumerated(EnumType.STRING)
  public AchievementMemberType getMemberNewType() {
    return memberNewType;
  }

  public void setMemberNewType(AchievementMemberType memberNewType) {
    this.memberNewType = memberNewType;
  }

  @Column(name = "member_new_achievement")
  public Double getMemberNewAchievement() {
    return memberNewAchievement;
  }

  public void setMemberNewAchievement(Double memberNewAchievement) {
    this.memberNewAchievement = memberNewAchievement;
  }

  @Column(name = "member_renew_type")
  @Enumerated(EnumType.STRING)
  public AchievementMemberType getMemberRenewType() {
    return memberRenewType;
  }

  public void setMemberRenewType(AchievementMemberType memberRenewType) {
    this.memberRenewType = memberRenewType;
  }

  @Column(name = "member_renew_achievement")
  public Double getMemberReNewAchievement() {
    return memberReNewAchievement;
  }

  public void setMemberReNewAchievement(Double memberReNewAchievement) {
    this.memberReNewAchievement = memberReNewAchievement;
  }

  @Column(name = "achievement_change_time")
  public Long getAchievementChangeTime() {
    return achievementChangeTime;
  }

  public void setAchievementChangeTime(Long achievementChangeTime) {
    this.achievementChangeTime = achievementChangeTime;
  }
}
