package com.bcgogo.txn.model.assistantStat;

import com.bcgogo.enums.OrderTypes;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.assistantStat.AssistantServiceRecordDTO;
import com.bcgogo.utils.DateUtil;

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
@Table(name = "assistant_service_record")
public class AssistantServiceRecord extends LongIdentifier {

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

  private Long serviceId;
  private String serviceName;

  private String vehicle;

  private Long customerId;
  private String customer;

  private Double standardHours;
  private Double standardService;
  private Double actualHours;
  private Double actualService;

  private Double achievement;

  private Long serviceAchievementHistoryId;

  private Double achievementByAssistant;//根据员工的配置 获取的提成
  private String achievementCalculateWay;//员工的提成计算方式 用文字描述
  private String achievementByAssistantCalculateWay;//根据员工的配置 获取的提成 的计算方式 用文字描述
  private Long statTime;//统计时间

  public AssistantServiceRecord() {
  }

  public AssistantServiceRecordDTO toDTO() {
    AssistantServiceRecordDTO assistantServiceRecordDTO = new AssistantServiceRecordDTO();
    assistantServiceRecordDTO.setId(getId());
    assistantServiceRecordDTO.setShopId(getShopId());
    assistantServiceRecordDTO.setOrderId(getOrderId());
    assistantServiceRecordDTO.setOrderType(getOrderType());
    assistantServiceRecordDTO.setVestDate(getVestDate());
    assistantServiceRecordDTO.setItemId(getItemId());
    assistantServiceRecordDTO.setReceiptNo(getReceiptNo());

    assistantServiceRecordDTO.setAssistantId(getAssistantId());
    assistantServiceRecordDTO.setAssistantName(getAssistantName());
    assistantServiceRecordDTO.setDepartmentId(getDepartmentId());
    assistantServiceRecordDTO.setDepartmentName(getDepartmentName());

    assistantServiceRecordDTO.setCustomer(getCustomer());
    assistantServiceRecordDTO.setCustomerId(getCustomerId());

    assistantServiceRecordDTO.setServiceId(getServiceId());
    assistantServiceRecordDTO.setServiceName(getServiceName());

    assistantServiceRecordDTO.setVehicle(getVehicle());
    assistantServiceRecordDTO.setStandardHours(getStandardHours());
    assistantServiceRecordDTO.setStandardService(getStandardService());
    assistantServiceRecordDTO.setActualHours(getActualHours());
    assistantServiceRecordDTO.setActualService(getActualService());

    assistantServiceRecordDTO.setAchievement(getAchievement());
    assistantServiceRecordDTO.setServiceAchievementHistoryId(getServiceAchievementHistoryId());

    if (getVestDate() != null) {
      assistantServiceRecordDTO.setVestDateStr(DateUtil.dateLongToStr(getVestDate(), DateUtil.DATE_STRING_FORMAT_CN));
    }

    if (getOrderType() != null) {
      assistantServiceRecordDTO.setOrderTypeStr(getOrderType().getName());
    }

    if (getOrderId() != null) {
      assistantServiceRecordDTO.setOrderIdStr(getOrderId().toString());
    }
    assistantServiceRecordDTO.setUrlStr();

    assistantServiceRecordDTO.setAchievementByAssistant(getAchievementByAssistant());
    assistantServiceRecordDTO.setAchievementCalculateWay(getAchievementCalculateWay());
    assistantServiceRecordDTO.setAchievementByAssistantCalculateWay(getAchievementByAssistantCalculateWay());

    return assistantServiceRecordDTO;
  }


  public AssistantServiceRecord fromDTO(AssistantServiceRecordDTO assistantServiceRecordDTO) {
    this.setShopId(assistantServiceRecordDTO.getShopId());
    this.setOrderId(assistantServiceRecordDTO.getOrderId());
    this.setOrderType(assistantServiceRecordDTO.getOrderType());
    this.setVestDate(assistantServiceRecordDTO.getVestDate());
    this.setItemId(assistantServiceRecordDTO.getItemId());
    this.setReceiptNo(assistantServiceRecordDTO.getReceiptNo());

    this.setAssistantId(assistantServiceRecordDTO.getAssistantId());
    this.setAssistantName(assistantServiceRecordDTO.getAssistantName());
    this.setDepartmentId(assistantServiceRecordDTO.getDepartmentId());
    this.setDepartmentName(assistantServiceRecordDTO.getDepartmentName());

    this.setCustomer(assistantServiceRecordDTO.getCustomer());
    this.setCustomerId(assistantServiceRecordDTO.getCustomerId());
    this.setServiceId(assistantServiceRecordDTO.getServiceId());
    this.setServiceName(assistantServiceRecordDTO.getServiceName());

    this.setVehicle(assistantServiceRecordDTO.getVehicle());
    this.setStandardHours(assistantServiceRecordDTO.getStandardHours());
    this.setStandardService(assistantServiceRecordDTO.getStandardService());
    this.setActualHours(assistantServiceRecordDTO.getActualHours());
    this.setActualService(assistantServiceRecordDTO.getActualService());

    this.setAchievement(assistantServiceRecordDTO.getAchievement());
    this.setServiceAchievementHistoryId(assistantServiceRecordDTO.getServiceAchievementHistoryId());

    this.setAchievementByAssistant(assistantServiceRecordDTO.getAchievementByAssistant());
    this.setAchievementCalculateWay(assistantServiceRecordDTO.getAchievementCalculateWay());
    this.setAchievementByAssistantCalculateWay(assistantServiceRecordDTO.getAchievementByAssistantCalculateWay());

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

  @Column(name = "service_id")
  public Long getServiceId() {
    return serviceId;
  }

  public void setServiceId(Long serviceId) {
    this.serviceId = serviceId;
  }

  @Column(name = "service_name")
  public String getServiceName() {
    return serviceName;
  }

  public void setServiceName(String serviceName) {
    this.serviceName = serviceName;
  }

  @Column(name = "vehicle")
  public String getVehicle() {
    return vehicle;
  }

  public void setVehicle(String vehicle) {
    this.vehicle = vehicle;
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

  @Column(name = "achievement")
  public Double getAchievement() {
    return achievement;
  }

  public void setAchievement(Double achievement) {
    this.achievement = achievement;
  }

  @Column(name = "service_achievement_history_id")
  public Long getServiceAchievementHistoryId() {
    return serviceAchievementHistoryId;
  }

  public void setServiceAchievementHistoryId(Long serviceAchievementHistoryId) {
    this.serviceAchievementHistoryId = serviceAchievementHistoryId;
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

  @Column(name = "stat_time")
  public Long getStatTime() {
    return statTime;
  }

  public void setStatTime(Long statTime) {
    this.statTime = statTime;
  }
}
