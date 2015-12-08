package com.bcgogo.txn.model.assistantStat;

import com.bcgogo.enums.OrderTypes;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.assistantStat.AssistantProductRecordDTO;
import com.bcgogo.utils.DateUtil;

import javax.persistence.*;

/**
 * 会员业绩统计-员工商品销售记录
 * Created by IntelliJ IDEA.
 * User: lw
 * Date: 13-5-21
 * Time: 下午10:05
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "assistant_product_record")
public class AssistantProductRecord extends LongIdentifier {

  private Long shopId;
  private Long orderId;
  private OrderTypes orderType;
  private String receiptNo;
  private Long itemId;
  private Long vestDate;

  private Long assistantId;
  private String assistantName;

  private Long departmentId;
  private String departmentName;

  private Long customerId;
  private String customer;


  private Long productId;
  private String productName;

  private Double amount;
  private Double price;
  private String unit;
  private Double total;

  private Double achievement;

  private Long productAchievementHistoryId;

  private Double achievementByAssistant;//根据员工的配置 获取的提成
  private String achievementCalculateWay;//员工的提成计算方式 用文字描述
  private String achievementByAssistantCalculateWay;//根据员工的配置 获取的提成 的计算方式 用文字描述

  private Double profit;//利润
  private Double profitAchievement;  //利润提成
  private Double profitAchievementByAssistant;//根据员工利润提成 计算出的 利润金额
  private String profitCalculateWay;//利润提成的计算方式
  private String profitByAssistantCalculateWay;//根据员工的配置 获取的利润提成的计算方式 用文字描述
  private Long statTime;//统计时间


  public AssistantProductRecordDTO toDTO() {
    AssistantProductRecordDTO assistantProductRecordDTO = new AssistantProductRecordDTO();
    assistantProductRecordDTO.setId(getId());
    assistantProductRecordDTO.setShopId(getShopId());
    assistantProductRecordDTO.setOrderId(getOrderId());
    assistantProductRecordDTO.setOrderType(getOrderType());
    assistantProductRecordDTO.setVestDate(getVestDate());
    assistantProductRecordDTO.setItemId(getItemId());
    assistantProductRecordDTO.setReceiptNo(getReceiptNo());

    assistantProductRecordDTO.setAssistantId(getAssistantId());
    assistantProductRecordDTO.setAssistantName(getAssistantName());
    assistantProductRecordDTO.setDepartmentId(getDepartmentId());
    assistantProductRecordDTO.setDepartmentName(getDepartmentName());

    assistantProductRecordDTO.setCustomer(getCustomer());
    assistantProductRecordDTO.setCustomerId(getCustomerId());

    assistantProductRecordDTO.setProductId(getProductId());
    assistantProductRecordDTO.setProductName(getProductName());

    assistantProductRecordDTO.setPrice(getPrice());
    assistantProductRecordDTO.setUnit(getUnit());
    assistantProductRecordDTO.setTotal(getTotal());
    assistantProductRecordDTO.setAmount(getAmount());

    assistantProductRecordDTO.setAchievement(getAchievement());
    assistantProductRecordDTO.setProductAchievementHistoryId(getProductAchievementHistoryId());

    if (getVestDate() != null) {
      assistantProductRecordDTO.setVestDateStr(DateUtil.dateLongToStr(getVestDate(), DateUtil.DATE_STRING_FORMAT_CN));
    }
    if (getOrderType() != null) {
      assistantProductRecordDTO.setOrderTypeStr(getOrderType().getName());
    }
    if (getOrderId() != null) {
      assistantProductRecordDTO.setOrderIdStr(getOrderId().toString());
    }

    assistantProductRecordDTO.setUrlStr();
    assistantProductRecordDTO.setAchievementByAssistant(getAchievementByAssistant());
    assistantProductRecordDTO.setAchievementByAssistantCalculateWay(getAchievementByAssistantCalculateWay());
    assistantProductRecordDTO.setAchievementCalculateWay(getAchievementCalculateWay());

    assistantProductRecordDTO.setProfit(getProfit());
    assistantProductRecordDTO.setProfitAchievement(getProfitAchievement());
    assistantProductRecordDTO.setProfitAchievementByAssistant(getProfitAchievementByAssistant());
    assistantProductRecordDTO.setProfitCalculateWay(getProfitCalculateWay());
    assistantProductRecordDTO.setProfitByAssistantCalculateWay(getProfitByAssistantCalculateWay());

    return assistantProductRecordDTO;
  }


  public AssistantProductRecord fromDTO(AssistantProductRecordDTO assistantProductRecordDTO) {
    this.setShopId(assistantProductRecordDTO.getShopId());
    this.setOrderId(assistantProductRecordDTO.getOrderId());
    this.setOrderType(assistantProductRecordDTO.getOrderType());
    this.setVestDate(assistantProductRecordDTO.getVestDate());
    this.setItemId(assistantProductRecordDTO.getItemId());
    this.setReceiptNo(assistantProductRecordDTO.getReceiptNo());
    this.setProductId(assistantProductRecordDTO.getProductId());
    this.setProductName(assistantProductRecordDTO.getProductName());

    this.setAssistantId(assistantProductRecordDTO.getAssistantId());
    this.setAssistantName(assistantProductRecordDTO.getAssistantName());
    this.setDepartmentId(assistantProductRecordDTO.getDepartmentId());
    this.setDepartmentName(assistantProductRecordDTO.getDepartmentName());

    this.setCustomer(assistantProductRecordDTO.getCustomer());
    this.setCustomerId(assistantProductRecordDTO.getCustomerId());

    this.setPrice(assistantProductRecordDTO.getPrice());
    this.setUnit(assistantProductRecordDTO.getUnit());
    this.setTotal(assistantProductRecordDTO.getTotal());
    this.setAmount(assistantProductRecordDTO.getAmount());

    this.setAchievement(assistantProductRecordDTO.getAchievement());
    this.setProductAchievementHistoryId(assistantProductRecordDTO.getProductAchievementHistoryId());

    this.setAchievementByAssistant(assistantProductRecordDTO.getAchievementByAssistant());
    this.setAchievementByAssistantCalculateWay(assistantProductRecordDTO.getAchievementByAssistantCalculateWay());
    this.setAchievementCalculateWay(assistantProductRecordDTO.getAchievementCalculateWay());

    this.setProfit(assistantProductRecordDTO.getProfit());
    this.setProfitAchievement(assistantProductRecordDTO.getProfitAchievement());
    this.setProfitAchievementByAssistant(assistantProductRecordDTO.getProfitAchievementByAssistant());
    this.setProfitCalculateWay(assistantProductRecordDTO.getProfitCalculateWay());
    this.setProfitByAssistantCalculateWay(assistantProductRecordDTO.getProfitByAssistantCalculateWay());

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

  @Column(name = "order_id")
  public Long getOrderId() {
    return orderId;
  }

  public void setOrderId(Long orderId) {
    this.orderId = orderId;
  }

  @Column(name = "order_type")
  @Enumerated(EnumType.STRING)
  public OrderTypes getOrderType() {
    return orderType;
  }

  public void setOrderType(OrderTypes orderType) {
    this.orderType = orderType;
  }

  @Column(name = "receipt_no")
  public String getReceiptNo() {
    return receiptNo;
  }

  public void setReceiptNo(String receiptNo) {
    this.receiptNo = receiptNo;
  }

  @Column(name = "item_id")
  public Long getItemId() {
    return itemId;
  }

  public void setItemId(Long itemId) {
    this.itemId = itemId;
  }

  @Column(name = "vest_date")
  public Long getVestDate() {
    return vestDate;
  }

  public void setVestDate(Long vestDate) {
    this.vestDate = vestDate;
  }


  @Column(name = "customer_id")
  public Long getCustomerId() {
    return customerId;
  }

  public void setCustomerId(Long customerId) {
    this.customerId = customerId;
  }

  @Column(name = "customer")
  public String getCustomer() {
    return customer;
  }

  public void setCustomer(String customer) {
    this.customer = customer;
  }

  @Column(name = "product_id")
  public Long getProductId() {
    return productId;
  }

  public void setProductId(Long productId) {
    this.productId = productId;
  }

  @Column(name = "amount")
  public Double getAmount() {
    return amount;
  }

  public void setAmount(Double amount) {
    this.amount = amount;
  }

  @Column(name = "product_name")
  public String getProductName() {
    return productName;
  }

  public void setProductName(String productName) {
    this.productName = productName;
  }

  @Column(name = "price")
  public Double getPrice() {
    return price;
  }

  public void setPrice(Double price) {
    this.price = price;
  }

  @Column(name = "unit")
  public String getUnit() {
    return unit;
  }

  public void setUnit(String unit) {
    this.unit = unit;
  }

  @Column(name = "total")
  public Double getTotal() {
    return total;
  }

  public void setTotal(Double total) {
    this.total = total;
  }

  @Column(name = "achievement")
  public Double getAchievement() {
    return achievement;
  }

  public void setAchievement(Double achievement) {
    this.achievement = achievement;
  }

  @Column(name = "product_achievement_history_id")
  public Long getProductAchievementHistoryId() {
    return productAchievementHistoryId;
  }

  public void setProductAchievementHistoryId(Long productAchievementHistoryId) {
    this.productAchievementHistoryId = productAchievementHistoryId;
  }

  @Column(name = "achievement_by_assistant")
  public Double getAchievementByAssistant() {
    return achievementByAssistant;
  }

  public void setAchievementByAssistant(Double achievementByAssistant) {
    this.achievementByAssistant = achievementByAssistant;
  }

  @Column(name = "achievement_calculate_way")
  public String getAchievementCalculateWay() {
    return achievementCalculateWay;
  }

  public void setAchievementCalculateWay(String achievementCalculateWay) {
    this.achievementCalculateWay = achievementCalculateWay;
  }

  @Column(name = "achievement_by_assistant_calculate_way")
  public String getAchievementByAssistantCalculateWay() {
    return achievementByAssistantCalculateWay;
  }

  public void setAchievementByAssistantCalculateWay(String achievementByAssistantCalculateWay) {
    this.achievementByAssistantCalculateWay = achievementByAssistantCalculateWay;
  }

  @Column(name = "profit_calculate_way")
  public String getProfitCalculateWay() {
    return profitCalculateWay;
  }

  public void setProfitCalculateWay(String profitCalculateWay) {
    this.profitCalculateWay = profitCalculateWay;
  }

  @Column(name = "profit_by_assistant_calculate_way")
  public String getProfitByAssistantCalculateWay() {
    return profitByAssistantCalculateWay;
  }

  public void setProfitByAssistantCalculateWay(String profitByAssistantCalculateWay) {
    this.profitByAssistantCalculateWay = profitByAssistantCalculateWay;
  }

  @Column(name = "profit_achievement")
  public Double getProfitAchievement() {
    return profitAchievement;
  }

  public void setProfitAchievement(Double profitAchievement) {
    this.profitAchievement = profitAchievement;
  }

  @Column(name = "profit_achievement_by_assistant")
  public Double getProfitAchievementByAssistant() {
    return profitAchievementByAssistant;
  }

  public void setProfitAchievementByAssistant(Double profitAchievementByAssistant) {
    this.profitAchievementByAssistant = profitAchievementByAssistant;
  }

  @Column(name = "profit")
  public Double getProfit() {
    return profit;
  }

  public void setProfit(Double profit) {
    this.profit = profit;
  }

  @Column(name = "stat_time")
  public Long getStatTime() {
    return statTime;
  }

  public void setStatTime(Long statTime) {
    this.statTime = statTime;
  }

}
