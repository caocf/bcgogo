package com.bcgogo.txn.dto.assistantStat;

import com.bcgogo.enums.MemberOrderType;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.enums.assistantStat.AchievementOrderType;
import com.bcgogo.enums.assistantStat.AchievementStatType;
import com.bcgogo.utils.NumberUtil;

import java.io.Serializable;

/**
 * 员工业绩统计-统计信息封装
 * Created by IntelliJ IDEA.
 * User: lw
 * Date: 13-5-23
 * Time: 上午10:36
 * To change this template use File | Settings | File Templates.
 */
public class AssistantAchievementStatDTO implements Serializable {

  private Long id;

  private Long shopId;

  private int statYear;
  private int statMonth;

  private AchievementStatType achievementStatType;//统计类型 按员工 或者按部门
  private Long departmentId;
  private Long assistantId;
  private String assistantName;
  private String departmentName;
  private String assistantIdStr;
  private String departmentIdStr;

  private Double standardHours;//标准工时
  private Double standardService;//标准工时费
  private Double actualHours;//实际工时
  private Double actualService;//实际工时费
  private Double serviceAchievement;//服务工时费
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

  private Double member;
  private Long memberTimes;
  private Double memberAchievement;
  private Double memberAchievementByAssistant;//根据员工提成的配置 计算出的提成

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


  public AssistantAchievementStatDTO() {
    setStandardHours(0D);
    setStandardService(0D);

    setActualHours(0D);
    setActualService(0D);
    setServiceAchievement(0D);
    setServiceAchievementByAssistant(0D);

    setSale(0D);
    setSaleAchievement(0D);
    setSalesAchievementByAssistant(0D);
    setSalesProfit(0D);
    setSalesProfitAchievement(0D);
    setSalesProfitAchievementByAssistant(0D);

    setWash(0D);
    setWashTimes(0L);
    setWashAchievement(0D);
    setWashAchievementByAssistant(0D);

    setMember(0D);
    setMemberTimes(0L);
    setMemberAchievement(0D);
    setMemberAchievementByAssistant(0D);

    setMemberRenew(0D);
    setMemberRenewTimes(0L);
    setMemberRenewAchievement(0D);
    setMemberRenewAchievementByAssistant(0D);


    setBusinessAccount(0D);
    setBusinessAccountAchievement(0D);
    setBusinessAccountAchievementByAssistant(0D);

    setStatSum(0D);
    setAchievementSum(0D);
    setAchievementSumByAssistant(0D);
  }

  public AssistantAchievementStatDTO add(AssistantAchievementStatDTO assistantAchievementStatDTO) {
    this.setStandardHours(NumberUtil.doubleVal(this.getStandardHours()) + NumberUtil.doubleVal(assistantAchievementStatDTO.getStandardHours()));
    this.setStandardService(NumberUtil.doubleVal(this.getStandardService()) + NumberUtil.doubleVal(assistantAchievementStatDTO.getStandardService()));
    this.setActualHours(NumberUtil.doubleVal(this.getActualHours()) + NumberUtil.doubleVal(assistantAchievementStatDTO.getActualHours()));
    this.setActualService(NumberUtil.doubleVal(this.getActualService()) + NumberUtil.doubleVal(assistantAchievementStatDTO.getActualService()));
    this.setServiceAchievement(NumberUtil.doubleVal(this.getServiceAchievement()) + NumberUtil.doubleVal(assistantAchievementStatDTO.getServiceAchievement()));
    this.setServiceAchievementByAssistant(NumberUtil.doubleVal(this.getServiceAchievementByAssistant()) + NumberUtil.doubleVal(assistantAchievementStatDTO.getServiceAchievementByAssistant()));

    this.setSale(NumberUtil.doubleVal(this.getSale()) + NumberUtil.doubleVal(assistantAchievementStatDTO.getSale()));
    this.setSaleAchievement(NumberUtil.doubleVal(this.getSaleAchievement()) + NumberUtil.doubleVal(assistantAchievementStatDTO.getSaleAchievement()));
    this.setSalesAchievementByAssistant(NumberUtil.doubleVal(this.getSalesAchievementByAssistant()) + NumberUtil.doubleVal(assistantAchievementStatDTO.getSalesAchievementByAssistant()));
    this.setSalesProfit(NumberUtil.doubleVal(this.getSalesProfit()) + NumberUtil.doubleVal(assistantAchievementStatDTO.getSalesProfit()));
    this.setSalesProfitAchievement(NumberUtil.doubleVal(this.getSalesProfitAchievement()) + NumberUtil.doubleVal(assistantAchievementStatDTO.getSalesProfitAchievement()));
    this.setSalesProfitAchievementByAssistant(NumberUtil.doubleVal(this.getSalesProfitAchievementByAssistant()) + NumberUtil.doubleVal(assistantAchievementStatDTO.getSalesProfitAchievementByAssistant()));

    this.setWash(NumberUtil.doubleVal(this.getWash()) + NumberUtil.doubleVal(assistantAchievementStatDTO.getWash()));
    this.setWashTimes(NumberUtil.longValue(this.getWashTimes()) + NumberUtil.longValue(assistantAchievementStatDTO.getWashTimes()));
    this.setWashAchievement(NumberUtil.doubleVal(this.getWashAchievement()) + NumberUtil.doubleVal(assistantAchievementStatDTO.getWashAchievement()));
    this.setWashAchievementByAssistant(NumberUtil.doubleVal(this.getWashAchievementByAssistant()) + NumberUtil.doubleVal(assistantAchievementStatDTO.getWashAchievementByAssistant()));

    this.setMember(NumberUtil.doubleVal(this.getMember()) + NumberUtil.doubleVal(assistantAchievementStatDTO.getMember()));
    this.setMemberTimes(NumberUtil.longValue(this.getMemberTimes()) + NumberUtil.longValue(assistantAchievementStatDTO.getMemberTimes()));
    this.setMemberAchievement(NumberUtil.doubleVal(this.getMemberAchievement()) + NumberUtil.doubleVal(assistantAchievementStatDTO.getMemberAchievement()));
    this.setMemberAchievementByAssistant(NumberUtil.doubleVal(this.getMemberAchievementByAssistant()) + NumberUtil.doubleVal(assistantAchievementStatDTO.getMemberAchievementByAssistant()));

    this.setMemberRenew(NumberUtil.doubleVal(this.getMemberRenew()) + NumberUtil.doubleVal(assistantAchievementStatDTO.getMemberRenew()));
    this.setMemberRenewTimes(NumberUtil.longValue(this.getMemberRenewTimes()) + NumberUtil.longValue(assistantAchievementStatDTO.getMemberRenewTimes()));
    this.setMemberRenewAchievement(NumberUtil.doubleVal(this.getMemberRenewAchievement()) + NumberUtil.doubleVal(assistantAchievementStatDTO.getMemberRenewAchievement()));
    this.setMemberRenewAchievementByAssistant(NumberUtil.doubleVal(this.getMemberRenewAchievementByAssistant()) + NumberUtil.doubleVal(assistantAchievementStatDTO.getMemberRenewAchievementByAssistant()));

    this.setBusinessAccount(NumberUtil.doubleVal(this.getBusinessAccount()) + NumberUtil.doubleVal(assistantAchievementStatDTO.getBusinessAccount()));
    this.setBusinessAccountAchievement(NumberUtil.doubleVal(this.getBusinessAccountAchievement()) + NumberUtil.doubleVal(assistantAchievementStatDTO.getBusinessAccountAchievement()));
    this.setBusinessAccountAchievementByAssistant(NumberUtil.doubleVal(this.getBusinessAccountAchievementByAssistant()) + NumberUtil.doubleVal(assistantAchievementStatDTO.getBusinessAccountAchievementByAssistant()));



    this.setStatSum(NumberUtil.doubleVal(this.getStatSum()) + NumberUtil.doubleVal(assistantAchievementStatDTO.getStatSum()));
    this.setAchievementSum(NumberUtil.doubleVal(this.getAchievementSum()) + NumberUtil.doubleVal(assistantAchievementStatDTO.getAchievementSum()));
    this.setAchievementSumByAssistant(NumberUtil.doubleVal(this.getAchievementSumByAssistant()) + NumberUtil.doubleVal(assistantAchievementStatDTO.getAchievementSumByAssistant()));

    return this;
  }


  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public int getStatYear() {
    return statYear;
  }

  public void setStatYear(int statYear) {
    this.statYear = statYear;
  }

  public int getStatMonth() {
    return statMonth;
  }

  public void setStatMonth(int statMonth) {
    this.statMonth = statMonth;
  }

  public AchievementStatType getAchievementStatType() {
    return achievementStatType;
  }

  public void setAchievementStatType(AchievementStatType achievementStatType) {
    this.achievementStatType = achievementStatType;
  }

  public Long getDepartmentId() {
    return departmentId;
  }

  public void setDepartmentId(Long departmentId) {
    this.departmentId = departmentId;
  }

  public Long getAssistantId() {
    return assistantId;
  }

  public void setAssistantId(Long assistantId) {
    this.assistantId = assistantId;
  }

  public Double getStandardHours() {
    return standardHours;
  }

  public void setStandardHours(Double standardHours) {
    this.standardHours = NumberUtil.toReserve(standardHours,NumberUtil.PRECISION);
  }

  public Double getStandardService() {
    return standardService;
  }

  public void setStandardService(Double standardService) {
    this.standardService = NumberUtil.toReserve(standardService,NumberUtil.PRECISION);
  }

  public Double getActualHours() {
    return actualHours;
  }

  public void setActualHours(Double actualHours) {
    this.actualHours = NumberUtil.toReserve(actualHours,NumberUtil.PRECISION);
  }

  public Double getActualService() {
    return actualService;
  }

  public void setActualService(Double actualService) {
    this.actualService = NumberUtil.toReserve(actualService,NumberUtil.PRECISION);
  }

  public Double getServiceAchievement() {
    return serviceAchievement;
  }

  public void setServiceAchievement(Double serviceAchievement) {
    this.serviceAchievement = NumberUtil.toReserve(serviceAchievement,NumberUtil.PRECISION);
  }

  public Double getSale() {
    return sale;
  }

  public void setSale(Double sale) {
    this.sale = NumberUtil.toReserve(sale,NumberUtil.PRECISION);
  }

  public Double getSaleAchievement() {
    return saleAchievement;
  }

  public void setSaleAchievement(Double saleAchievement) {
    this.saleAchievement = NumberUtil.toReserve(saleAchievement,NumberUtil.PRECISION);
  }

  public Double getWash() {
    return wash;
  }

  public void setWash(Double wash) {
    this.wash = NumberUtil.toReserve(wash,NumberUtil.PRECISION);
  }

  public Long getWashTimes() {
    return washTimes;
  }

  public void setWashTimes(Long washTimes) {
    this.washTimes = washTimes;
  }

  public Double getWashAchievement() {
    return washAchievement;
  }

  public void setWashAchievement(Double washAchievement) {
    this.washAchievement = NumberUtil.toReserve(washAchievement,NumberUtil.PRECISION);
  }

  public Double getMember() {
    return member;
  }

  public void setMember(Double member) {
    this.member = NumberUtil.toReserve(member,NumberUtil.PRECISION);
  }

  public Long getMemberTimes() {
    return memberTimes;
  }

  public void setMemberTimes(Long memberTimes) {
    this.memberTimes = memberTimes;
  }

  public Double getMemberAchievement() {
    return memberAchievement;
  }

  public void setMemberAchievement(Double memberAchievement) {
    this.memberAchievement = NumberUtil.toReserve(memberAchievement,NumberUtil.PRECISION);
  }

  public Double getStatSum() {
    return statSum;
  }

  public void setStatSum(Double statSum) {
    this.statSum = NumberUtil.toReserve(statSum,NumberUtil.PRECISION);
  }

  public Double getAchievementSum() {
    return achievementSum;
  }

  public void setAchievementSum(Double achievementSum) {
    this.achievementSum = NumberUtil.toReserve(achievementSum,NumberUtil.PRECISION);
  }

  public Long getStatTime() {
    return statTime;
  }

  public void setStatTime(Long statTime) {
    this.statTime = statTime;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getDepartmentName() {
    return departmentName;
  }

  public void setDepartmentName(String departmentName) {
    this.departmentName = departmentName;
  }

  public String getAssistantName() {
    return assistantName;
  }

  public void setAssistantName(String assistantName) {
    this.assistantName = assistantName;
  }

  public String getAssistantIdStr() {
    return assistantIdStr;
  }

  public void setAssistantIdStr(String assistantIdStr) {
    this.assistantIdStr = assistantIdStr;
  }

  public String getDepartmentIdStr() {
    return departmentIdStr;
  }

  public void setDepartmentIdStr(String departmentIdStr) {
    this.departmentIdStr = departmentIdStr;
  }

  public Double getServiceAchievementByAssistant() {
    return serviceAchievementByAssistant;
  }

  public void setServiceAchievementByAssistant(Double serviceAchievementByAssistant) {
    this.serviceAchievementByAssistant = NumberUtil.toReserve(serviceAchievementByAssistant,NumberUtil.PRECISION);;
  }

  public Double getSalesAchievementByAssistant() {
    return salesAchievementByAssistant;
  }

  public void setSalesAchievementByAssistant(Double salesAchievementByAssistant) {
    this.salesAchievementByAssistant = NumberUtil.toReserve(salesAchievementByAssistant,NumberUtil.PRECISION);
  }

  public Double getWashAchievementByAssistant() {
    return washAchievementByAssistant;
  }

  public void setWashAchievementByAssistant(Double washAchievementByAssistant) {
    this.washAchievementByAssistant = NumberUtil.toReserve(washAchievementByAssistant,NumberUtil.PRECISION);
  }

  public Double getMemberAchievementByAssistant() {
    return memberAchievementByAssistant;
  }

  public void setMemberAchievementByAssistant(Double memberAchievementByAssistant) {
    this.memberAchievementByAssistant = NumberUtil.toReserve(memberAchievementByAssistant,NumberUtil.PRECISION);
  }

  public Double getBusinessAccount() {
    return businessAccount;
  }

  public void setBusinessAccount(Double businessAccount) {
    this.businessAccount = NumberUtil.toReserve(businessAccount,NumberUtil.PRECISION);
  }

  public Double getBusinessAccountAchievement() {
    return businessAccountAchievement;
  }

  public void setBusinessAccountAchievement(Double businessAccountAchievement) {
    this.businessAccountAchievement = NumberUtil.toReserve(businessAccountAchievement,NumberUtil.PRECISION);
  }

  public Double getBusinessAccountAchievementByAssistant() {
    return businessAccountAchievementByAssistant;
  }

  public void setBusinessAccountAchievementByAssistant(Double businessAccountAchievementByAssistant) {
    this.businessAccountAchievementByAssistant = NumberUtil.toReserve(businessAccountAchievementByAssistant,NumberUtil.PRECISION);
  }

  public Double getAchievementSumByAssistant() {
    return achievementSumByAssistant;
  }

  public void setAchievementSumByAssistant(Double achievementSumByAssistant) {
    this.achievementSumByAssistant = NumberUtil.toReserve(achievementSumByAssistant,NumberUtil.PRECISION);
  }

  public AchievementOrderType getAchievementOrderType() {
    return achievementOrderType;
  }

  public void setAchievementOrderType(AchievementOrderType achievementOrderType) {
    this.achievementOrderType = achievementOrderType;
  }

  public Long getServiceId() {
    return serviceId;
  }

  public void setServiceId(Long serviceId) {
    this.serviceId = serviceId;
  }

  public String getServiceName() {
    return serviceName;
  }

  public void setServiceName(String serviceName) {
    this.serviceName = serviceName;
  }

  public Double getMemberRenew() {
    return memberRenew;
  }

  public void setMemberRenew(Double memberRenew) {
    this.memberRenew = NumberUtil.toReserve(memberRenew,NumberUtil.PRECISION);
  }

  public Long getMemberRenewTimes() {
    return memberRenewTimes;
  }

  public void setMemberRenewTimes(Long memberRenewTimes) {
    this.memberRenewTimes = memberRenewTimes;
  }

  public Double getMemberRenewAchievement() {
    return memberRenewAchievement;
  }

  public void setMemberRenewAchievement(Double memberRenewAchievement) {
    this.memberRenewAchievement = NumberUtil.toReserve(memberRenewAchievement,NumberUtil.PRECISION);
  }

  public Double getMemberRenewAchievementByAssistant() {
    return memberRenewAchievementByAssistant;
  }

  public void setMemberRenewAchievementByAssistant(Double memberRenewAchievementByAssistant) {
    this.memberRenewAchievementByAssistant = NumberUtil.toReserve(memberRenewAchievementByAssistant,NumberUtil.PRECISION);
  }

  public Double getSalesProfitAchievement() {
    return salesProfitAchievement;
  }

  public void setSalesProfitAchievement(Double salesProfitAchievement) {
    this.salesProfitAchievement = NumberUtil.toReserve(salesProfitAchievement,NumberUtil.PRECISION);
  }

  public Double getSalesProfitAchievementByAssistant() {
    return salesProfitAchievementByAssistant;
  }

  public void setSalesProfitAchievementByAssistant(Double salesProfitAchievementByAssistant) {
    this.salesProfitAchievementByAssistant = NumberUtil.toReserve(salesProfitAchievementByAssistant,NumberUtil.PRECISION);
  }

  public Double getSalesProfit() {
    return salesProfit;
  }

  public void setSalesProfit(Double salesProfit) {
    this.salesProfit = NumberUtil.toReserve(salesProfit,NumberUtil.PRECISION);
  }
}
