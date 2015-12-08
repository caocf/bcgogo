package com.bcgogo.txn.model.assistantStat;

import com.bcgogo.enums.MemberOrderType;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.enums.assistantStat.AchievementOrderType;
import com.bcgogo.enums.assistantStat.AchievementStatType;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.assistantStat.AssistantAchievementStatDTO;
import com.bcgogo.utils.NumberUtil;

import javax.persistence.*;

/**
 * 会员业绩统计-店铺未配置的员工业绩统计项
 * Created by IntelliJ IDEA.
 * User: lw
 * Date: 13-5-21
 * Time: 下午10:05
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "assistant_achievement_stat")
public class AssistantAchievementStat extends LongIdentifier {

  private Long shopId;

  private int statYear;
  private int statMonth;

  private AchievementStatType achievementStatType;//统计类型 按员工 或者按部门
  private Long departmentId;
  private Long assistantId;
  private String assistantName;
  private String departmentName;

  private Double standardHours;//标准工时
  private Double standardService;//标准工时费
  private Double actualHours;//实际工时
  private Double actualService;//实际工时费
  private Double serviceAchievement;//服务提成
  private Double serviceAchievementByAssistant;//根据员工提成的配置 计算出的提成

  private Double sale;
  private Double saleAchievement;
  private Double salesAchievementByAssistant;//根据员工提成的配置 计算出的提成
  private Double salesProfit;//销售利润
  private Double salesProfitAchievement;//销售利润提成
  private Double salesProfitAchievementByAssistant; //根据员工提成的配置 计算出的销售利润提成

  private Double wash;
  private Long washTimes;
  private Double washAchievement;
  private Double washAchievementByAssistant;//根据员工提成的配置 计算出的提成


  private Double member;  //会员购卡额
  private Long memberTimes; //会员购卡次数
  private Double memberAchievement;//会员购卡提成
  private Double memberAchievementByAssistant;//根据员工提成的配置 计算出的会员购卡提成


  private Double memberRenew;  //会员续卡额
  private Long memberRenewTimes;  //会员续卡次数
  private Double memberRenewAchievement;//会员续卡提成
  private Double memberRenewAchievementByAssistant;//根据员工提成的配置 计算出的会员续卡提成


  private Double businessAccount; //营业外记账的总和
  private Double businessAccountAchievement; //营业外记账的提成
  private Double businessAccountAchievementByAssistant;//根据员工提成的配置 计算出的提成

  private Double statSum;
  private Double achievementSum;
  private Double achievementSumByAssistant;//根据员工提成的配置 计算出的总提成

  private Long statTime;

  private AchievementOrderType achievementOrderType;//类别统计
  private Long serviceId;
  private String serviceName;

  public AssistantAchievementStatDTO toDTO() {
    AssistantAchievementStatDTO statDTO = new AssistantAchievementStatDTO();
    statDTO.setId(getId());
    statDTO.setShopId(getShopId());
    statDTO.setStatYear(getStatYear());
    statDTO.setStatMonth(getStatMonth());
    statDTO.setAchievementStatType(getAchievementStatType());
    statDTO.setDepartmentId(getDepartmentId());
    statDTO.setDepartmentIdStr(getDepartmentId() == null ? null : getDepartmentId().toString());
    statDTO.setAssistantIdStr(getAssistantId() == null ? null : getAssistantId().toString());
    statDTO.setAssistantId(getAssistantId());
    statDTO.setStandardHours(getStandardHours());
    statDTO.setStandardService(getStandardService());
    statDTO.setActualHours(getActualHours());
    statDTO.setActualService(getActualService());
    statDTO.setServiceAchievement(getServiceAchievement());
    statDTO.setServiceAchievementByAssistant(getServiceAchievementByAssistant());


    statDTO.setSale(getSale());
    statDTO.setSaleAchievement(getSaleAchievement());
    statDTO.setSalesAchievementByAssistant(getSalesAchievementByAssistant());
    statDTO.setSalesProfit(getSalesProfit());
    statDTO.setSalesProfitAchievement(getSalesProfitAchievement());
    statDTO.setSalesProfitAchievementByAssistant(getSalesProfitAchievementByAssistant());

    statDTO.setWash(getWash());
    statDTO.setWashTimes(getWashTimes());
    statDTO.setWashAchievement(getWashAchievement());
    statDTO.setWashAchievementByAssistant(getWashAchievementByAssistant());

    statDTO.setMember(getMember());
    statDTO.setMemberTimes(getMemberTimes());
    statDTO.setMemberAchievement(getMemberAchievement());
    statDTO.setMemberAchievementByAssistant(getMemberAchievementByAssistant());

    statDTO.setMemberRenew(getMemberRenew());
    statDTO.setMemberRenewTimes(getMemberRenewTimes());
    statDTO.setMemberRenewAchievement(getMemberRenewAchievement());
    statDTO.setMemberRenewAchievementByAssistant(getMemberRenewAchievementByAssistant());

    statDTO.setBusinessAccount(getBusinessAccount());
    statDTO.setBusinessAccountAchievementByAssistant(getBusinessAccountAchievementByAssistant());
    statDTO.setBusinessAccountAchievement(getBusinessAccountAchievement());

    statDTO.setStatSum(getStatSum());
    statDTO.setAchievementSum(getAchievementSum());
    statDTO.setAchievementSumByAssistant(getAchievementSumByAssistant());

    statDTO.setStatTime(getStatTime());

    statDTO.setDepartmentName(getDepartmentName());
    statDTO.setAssistantName(getAssistantName());

    statDTO.setAchievementOrderType(getAchievementOrderType());
    statDTO.setServiceId(getServiceId());
    statDTO.setServiceName(getServiceName());

    return statDTO;

  }


  public AssistantAchievementStat fromDTO(AssistantAchievementStatDTO statDTO) {
    this.setId(statDTO.getId());
    this.setShopId(statDTO.getShopId());
    this.setStatYear(statDTO.getStatYear());
    this.setStatMonth(statDTO.getStatMonth());
    this.setAchievementStatType(statDTO.getAchievementStatType());
    this.setDepartmentId(statDTO.getDepartmentId());
    this.setAssistantId(statDTO.getAssistantId());

    this.setStandardHours(NumberUtil.toReserve(statDTO.getStandardHours(), NumberUtil.PRECISION));
    this.setStandardService(NumberUtil.toReserve(statDTO.getStandardService(), NumberUtil.PRECISION));
    this.setActualHours(NumberUtil.toReserve(statDTO.getActualHours(), NumberUtil.PRECISION));
    this.setActualService(NumberUtil.toReserve(statDTO.getActualService(), NumberUtil.PRECISION));
    this.setServiceAchievement(NumberUtil.toReserve(statDTO.getServiceAchievement(), NumberUtil.PRECISION));
    this.setServiceAchievementByAssistant(NumberUtil.toReserve(statDTO.getServiceAchievementByAssistant(), NumberUtil.PRECISION));


    this.setSale(NumberUtil.toReserve(statDTO.getSale(), NumberUtil.PRECISION));
    this.setSaleAchievement(NumberUtil.toReserve(statDTO.getSaleAchievement(), NumberUtil.PRECISION));
    this.setSalesAchievementByAssistant(NumberUtil.toReserve(statDTO.getSalesAchievementByAssistant(), NumberUtil.PRECISION));
    this.setSalesProfit(NumberUtil.toReserve(statDTO.getSalesProfit(), NumberUtil.PRECISION));
    this.setSalesProfitAchievement(NumberUtil.toReserve(statDTO.getSalesProfitAchievement(), NumberUtil.PRECISION));
    this.setSalesProfitAchievementByAssistant(NumberUtil.toReserve(statDTO.getSalesProfitAchievementByAssistant(), NumberUtil.PRECISION));

    this.setWash(NumberUtil.toReserve(statDTO.getWash(), NumberUtil.PRECISION));
    this.setWashTimes(NumberUtil.longValue(statDTO.getWashTimes()));
    this.setWashAchievement(NumberUtil.toReserve(statDTO.getWashAchievement(), NumberUtil.PRECISION));
    this.setWashAchievementByAssistant(NumberUtil.toReserve(statDTO.getWashAchievementByAssistant(), NumberUtil.PRECISION));

    this.setMember(NumberUtil.toReserve(statDTO.getMember(), NumberUtil.PRECISION));
    this.setMemberTimes(NumberUtil.longValue(statDTO.getMemberTimes()));
    this.setMemberAchievement(NumberUtil.toReserve(statDTO.getMemberAchievement(), NumberUtil.PRECISION));
    this.setMemberAchievementByAssistant(NumberUtil.toReserve(statDTO.getMemberAchievementByAssistant(), NumberUtil.PRECISION));


    this.setBusinessAccount(NumberUtil.toReserve(statDTO.getBusinessAccount(), NumberUtil.PRECISION));
    this.setBusinessAccountAchievementByAssistant(NumberUtil.toReserve(statDTO.getBusinessAccountAchievementByAssistant(), NumberUtil.PRECISION));
    this.setBusinessAccountAchievement(NumberUtil.toReserve(statDTO.getBusinessAccountAchievement(), NumberUtil.PRECISION));

    this.setStatSum(NumberUtil.toReserve(statDTO.getStatSum(), NumberUtil.PRECISION));
    this.setAchievementSum(NumberUtil.toReserve(statDTO.getAchievementSum(), NumberUtil.PRECISION));
    this.setAchievementSumByAssistant(NumberUtil.toReserve(statDTO.getAchievementSumByAssistant(), NumberUtil.PRECISION));

    this.setStatTime(statDTO.getStatTime());
    this.setAchievementOrderType(statDTO.getAchievementOrderType());
    this.setServiceId(statDTO.getServiceId());

    this.setDepartmentName(statDTO.getDepartmentName());
    this.setAssistantName(statDTO.getAssistantName());
    this.setServiceName(statDTO.getServiceName());

    this.setMemberRenew(statDTO.getMemberRenew());
    this.setMemberRenewTimes(statDTO.getMemberRenewTimes());
    this.setMemberRenewAchievement(statDTO.getMemberRenewAchievement());
    this.setMemberRenewAchievementByAssistant(statDTO.getMemberRenewAchievementByAssistant());

    return this;
  }


  @Column(name = "stat_year")
  public int getStatYear() {
    return statYear;
  }

  public void setStatYear(int statYear) {
    this.statYear = statYear;
  }

  @Column(name = "stat_month")
  public int getStatMonth() {
    return statMonth;
  }

  public void setStatMonth(int statMonth) {
    this.statMonth = statMonth;
  }

  @Column(name = "stat_type")
  @Enumerated(EnumType.STRING)
  public AchievementStatType getAchievementStatType() {
    return achievementStatType;
  }

  public void setAchievementStatType(AchievementStatType achievementStatType) {
    this.achievementStatType = achievementStatType;
  }

  @Column(name = "department_id")
  public Long getDepartmentId() {
    return departmentId;
  }

  public void setDepartmentId(Long departmentId) {
    this.departmentId = departmentId;
  }

  @Column(name = "assistant_id")
  public Long getAssistantId() {
    return assistantId;
  }

  public void setAssistantId(Long assistantId) {
    this.assistantId = assistantId;
  }

  @Column(name = "standard_hours")
  public Double getStandardHours() {
    return standardHours;
  }

  public void setStandardHours(Double standardHours) {
    this.standardHours = standardHours;
  }

  @Column(name = "standard_service")
  public Double getStandardService() {
    return standardService;
  }

  public void setStandardService(Double standardService) {
    this.standardService = standardService;
  }

  @Column(name = "actual_hours")
  public Double getActualHours() {
    return actualHours;
  }

  public void setActualHours(Double actualHours) {
    this.actualHours = actualHours;
  }

  @Column(name = "actual_service")
  public Double getActualService() {
    return actualService;
  }

  public void setActualService(Double actualService) {
    this.actualService = actualService;
  }

  @Column(name = "service_achievement")
  public Double getServiceAchievement() {
    return serviceAchievement;
  }

  public void setServiceAchievement(Double serviceAchievement) {
    this.serviceAchievement = serviceAchievement;
  }

  @Column(name = "sale")
  public Double getSale() {
    return sale;
  }

  public void setSale(Double sale) {
    this.sale = sale;
  }

  @Column(name = "sale_achievement")
  public Double getSaleAchievement() {
    return saleAchievement;
  }

  public void setSaleAchievement(Double saleAchievement) {
    this.saleAchievement = saleAchievement;
  }

  @Column(name = "wash")
  public Double getWash() {
    return wash;
  }

  public void setWash(Double wash) {
    this.wash = wash;
  }

  @Column(name = "wash_times")
  public Long getWashTimes() {
    return washTimes;
  }

  public void setWashTimes(Long washTimes) {
    this.washTimes = washTimes;
  }

  @Column(name = "wash_achievement")
  public Double getWashAchievement() {
    return washAchievement;
  }

  public void setWashAchievement(Double washAchievement) {
    this.washAchievement = washAchievement;
  }

  @Column(name = "member")
  public Double getMember() {
    return member;
  }

  public void setMember(Double member) {
    this.member = member;
  }

  @Column(name = "member_times")
  public Long getMemberTimes() {
    return memberTimes;
  }

  public void setMemberTimes(Long memberTimes) {
    this.memberTimes = memberTimes;
  }

  @Column(name = "member_achievement")
  public Double getMemberAchievement() {
    return memberAchievement;
  }

  public void setMemberAchievement(Double memberAchievement) {
    this.memberAchievement = memberAchievement;
  }

  @Column(name = "stat_sum")
  public Double getStatSum() {
    return statSum;
  }

  public void setStatSum(Double statSum) {
    this.statSum = statSum;
  }

  @Column(name = "achievement_sum")
  public Double getAchievementSum() {
    return achievementSum;
  }

  public void setAchievementSum(Double achievementSum) {
    this.achievementSum = achievementSum;
  }

  @Column(name = "stat_time")
  public Long getStatTime() {
    return statTime;
  }

  public void setStatTime(Long statTime) {
    this.statTime = statTime;
  }

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "assistant")
  public String getAssistantName() {
    return assistantName;
  }

  public void setAssistantName(String assistantName) {
    this.assistantName = assistantName;
  }

  @Column(name = "department")
  public String getDepartmentName() {
    return departmentName;
  }

  public void setDepartmentName(String departmentName) {
    this.departmentName = departmentName;
  }

  @Column(name = "achievement_order_type")
  @Enumerated(EnumType.STRING)
  public AchievementOrderType getAchievementOrderType() {
    return achievementOrderType;
  }

  public void setAchievementOrderType(AchievementOrderType achievementOrderType) {
    this.achievementOrderType = achievementOrderType;
  }

  @Column(name = "service_id")
  public Long getServiceId() {
    return serviceId;
  }

  public void setServiceId(Long serviceId) {
    this.serviceId = serviceId;
  }

  @Column(name = "service_achievement_by_assistant")
  public Double getServiceAchievementByAssistant() {
    return serviceAchievementByAssistant;
  }

  public void setServiceAchievementByAssistant(Double serviceAchievementByAssistant) {
    this.serviceAchievementByAssistant = serviceAchievementByAssistant;
  }

  @Column(name = "sales_achievement_by_assistant")
  public Double getSalesAchievementByAssistant() {
    return salesAchievementByAssistant;
  }

  public void setSalesAchievementByAssistant(Double salesAchievementByAssistant) {
    this.salesAchievementByAssistant = salesAchievementByAssistant;
  }

  @Column(name = "wash_achievement_by_assistant")
  public Double getWashAchievementByAssistant() {
    return washAchievementByAssistant;
  }

  public void setWashAchievementByAssistant(Double washAchievementByAssistant) {
    this.washAchievementByAssistant = washAchievementByAssistant;
  }

  @Column(name = "member_achievement_by_assistant")
  public Double getMemberAchievementByAssistant() {
    return memberAchievementByAssistant;
  }

  public void setMemberAchievementByAssistant(Double memberAchievementByAssistant) {
    this.memberAchievementByAssistant = memberAchievementByAssistant;
  }

  @Column(name = "business_account")
  public Double getBusinessAccount() {
    return businessAccount;
  }

  public void setBusinessAccount(Double businessAccount) {
    this.businessAccount = businessAccount;
  }

  @Column(name = "business_account_achievement")
  public Double getBusinessAccountAchievement() {
    return businessAccountAchievement;
  }

  public void setBusinessAccountAchievement(Double businessAccountAchievement) {
    this.businessAccountAchievement = businessAccountAchievement;
  }

  @Column(name = "business_account_achievement_by_assistant")
  public Double getBusinessAccountAchievementByAssistant() {
    return businessAccountAchievementByAssistant;
  }

  public void setBusinessAccountAchievementByAssistant(Double businessAccountAchievementByAssistant) {
    this.businessAccountAchievementByAssistant = businessAccountAchievementByAssistant;
  }

  @Column(name = "achievement_sum_by_assistant")
  public Double getAchievementSumByAssistant() {
    return achievementSumByAssistant;
  }

  public void setAchievementSumByAssistant(Double achievementSumByAssistant) {
    this.achievementSumByAssistant = achievementSumByAssistant;
  }

  @Column(name = "service_name")
  public String getServiceName() {
    return serviceName;
  }

  public void setServiceName(String serviceName) {
    this.serviceName = serviceName;
  }

  @Column(name = "member_renew")
  public Double getMemberRenew() {
    return memberRenew;
  }

  public void setMemberRenew(Double memberRenew) {
    this.memberRenew = memberRenew;
  }

  @Column(name = "member_renew_times")
  public Long getMemberRenewTimes() {
    return memberRenewTimes;
  }

  public void setMemberRenewTimes(Long memberRenewTimes) {
    this.memberRenewTimes = memberRenewTimes;
  }

  @Column(name = "member_renew_achievement")
  public Double getMemberRenewAchievement() {
    return memberRenewAchievement;
  }

  public void setMemberRenewAchievement(Double memberRenewAchievement) {
    this.memberRenewAchievement = memberRenewAchievement;
  }

  @Column(name = "member_renew_achievement_by_assistant")
  public Double getMemberRenewAchievementByAssistant() {
    return memberRenewAchievementByAssistant;
  }

  public void setMemberRenewAchievementByAssistant(Double memberRenewAchievementByAssistant) {
    this.memberRenewAchievementByAssistant = memberRenewAchievementByAssistant;
  }

  @Column(name = "sales_profit_achievement")
  public Double getSalesProfitAchievement() {
    return salesProfitAchievement;
  }

  public void setSalesProfitAchievement(Double salesProfitAchievement) {
    this.salesProfitAchievement = salesProfitAchievement;
  }

  @Column(name = "sales_profit_achievement_by_assistant")
  public Double getSalesProfitAchievementByAssistant() {
    return salesProfitAchievementByAssistant;
  }

  public void setSalesProfitAchievementByAssistant(Double salesProfitAchievementByAssistant) {
    this.salesProfitAchievementByAssistant = salesProfitAchievementByAssistant;
  }

  @Column(name = "sales_profit")
  public Double getSalesProfit() {
    return salesProfit;
  }

  public void setSalesProfit(Double salesProfit) {
    this.salesProfit = salesProfit;
  }
}
