package com.bcgogo.txn.dto.assistantStat;

import com.bcgogo.enums.OrderTypes;
import com.bcgogo.utils.NumberUtil;

import java.io.Serializable;

/**
 * 员工业绩记录基础类
 * Created by IntelliJ IDEA.
 * User: lw
 * Date: 13-5-23
 * Time: 下午1:07
 * To change this template use File | Settings | File Templates.
 */
public class AssistantRecordDTO implements Serializable {

  private Long id;
  private Long shopId;
  private Long orderId;
  private OrderTypes orderType;
  private Long vestDate;
  private String receiptNo;
  private String vestDateStr;

  private Long assistantId;
  private String assistantName;

  private Long departmentId;
  private String departmentName;

  private Long customerId;
  private String customer;

  private Double achievement;
  private String orderTypeStr;
  private String orderIdStr;

  private Double achievementByAssistant;//根据员工的配置 获取的提成
  private String achievementCalculateWay;//员工的提成计算方式 用文字描述
  private String achievementByAssistantCalculateWay;//根据员工的配置 获取的提成 的计算方式 用文字描述
  private Double profit;//利润
  private Double profitAchievement;  //利润提成
  private Double profitAchievementByAssistant;//根据员工利润提成 计算出的 利润金额
  private String profitCalculateWay;//利润提成的计算方式
  private String profitByAssistantCalculateWay;//根据员工的配置 获取的利润提成的计算方式 用文字描述

  private String url;

  public void setUrlStr() {
    String urlStr = "#";
    if (orderType != null && getOrderId() != null) {
      if (orderType == OrderTypes.REPAIR) {
        urlStr = "txn.do?method=getRepairOrder&menu-uid=VEHICLE_CONSTRUCTION_REPAIR&repairOrderId=" + this.getOrderId().toString();
      } else if (orderType == OrderTypes.SALE) {
        urlStr = "sale.do?method=getSalesOrder&menu-uid=WEB.TXN.SALE_MANAGE.SALE&salesOrderId=" + this.getOrderId().toString();
      } else if (orderType == OrderTypes.PURCHASE) {
        urlStr = "RFbuy.do?method=show&id=" + this.getOrderId().toString();
      } else if (orderType == OrderTypes.INVENTORY) {
        urlStr = "storage.do?method=getPurchaseInventory&menu-uid=WEB.TXN.PURCHASE_MANAGE.STORAGE&purchaseInventoryId=" + this.getOrderId().toString()
            + "&type=txn&menu-uid=WEB.TXN.PURCHASE_MANAGE.STORAGE";
      } else if (orderType == OrderTypes.WASH_BEAUTY) {
        urlStr = "washBeauty.do?method=getWashBeautyOrder&washBeautyOrderId=" + this.getOrderId().toString();
      } else if (orderType == OrderTypes.RETURN) {
        urlStr = "goodsReturn.do?method=showReturnStorageByPurchaseReturnId&purchaseReturnId=" + this.getOrderId().toString();
      } else if (orderType == OrderTypes.SALE_RETURN) {
        urlStr = "salesReturn.do?method=showSalesReturnOrderBySalesReturnOrderId&salesReturnOrderId=" + this.getOrderId().toString();
      }
    } else {
      urlStr = "#";
    }
    this.setUrl(urlStr);
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public Long getOrderId() {
    return orderId;
  }

  public void setOrderId(Long orderId) {
    this.orderId = orderId;
  }

  public OrderTypes getOrderType() {
    return orderType;
  }

  public void setOrderType(OrderTypes orderType) {
    this.orderType = orderType;
  }

  public Long getVestDate() {
    return vestDate;
  }

  public void setVestDate(Long vestDate) {
    this.vestDate = vestDate;
  }

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

  public Long getCustomerId() {
    return customerId;
  }

  public void setCustomerId(Long customerId) {
    this.customerId = customerId;
  }

  public String getCustomer() {
    return customer;
  }

  public void setCustomer(String customer) {
    this.customer = customer;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Double getAchievement() {
    return achievement;
  }

  public void setAchievement(Double achievement) {
    this.achievement = NumberUtil.toReserve(achievement, NumberUtil.PRECISION);
  }

  public String getReceiptNo() {
    return receiptNo;
  }

  public void setReceiptNo(String receiptNo) {
    this.receiptNo = receiptNo;
  }

  public String getVestDateStr() {
    return vestDateStr;
  }

  public void setVestDateStr(String vestDateStr) {
    this.vestDateStr = vestDateStr;
  }

  public String getOrderTypeStr() {
    return orderTypeStr;
  }

  public void setOrderTypeStr(String orderTypeStr) {
    this.orderTypeStr = orderTypeStr;
  }

  public String getOrderIdStr() {
    return orderIdStr;
  }

  public void setOrderIdStr(String orderIdStr) {
    this.orderIdStr = orderIdStr;
  }

  public Double getAchievementByAssistant() {
    return achievementByAssistant;
  }

  public void setAchievementByAssistant(Double achievementByAssistant) {
    this.achievementByAssistant = NumberUtil.toReserve(achievementByAssistant,NumberUtil.PRECISION);
  }

  public String getAchievementCalculateWay() {
    return achievementCalculateWay;
  }

  public void setAchievementCalculateWay(String achievementCalculateWay) {
    this.achievementCalculateWay = achievementCalculateWay;
  }

  public String getAchievementByAssistantCalculateWay() {
    return achievementByAssistantCalculateWay;
  }

  public void setAchievementByAssistantCalculateWay(String achievementByAssistantCalculateWay) {
    this.achievementByAssistantCalculateWay = achievementByAssistantCalculateWay;
  }

  public String getProfitCalculateWay() {
    return profitCalculateWay;
  }

  public void setProfitCalculateWay(String profitCalculateWay) {
    this.profitCalculateWay = profitCalculateWay;
  }

  public String getProfitByAssistantCalculateWay() {
    return profitByAssistantCalculateWay;
  }

  public void setProfitByAssistantCalculateWay(String profitByAssistantCalculateWay) {
    this.profitByAssistantCalculateWay = profitByAssistantCalculateWay;
  }

  public Double getProfitAchievement() {
    return profitAchievement;
  }

  public void setProfitAchievement(Double profitAchievement) {
    this.profitAchievement = NumberUtil.toReserve(profitAchievement,NumberUtil.PRECISION);
  }

  public Double getProfitAchievementByAssistant() {
    return profitAchievementByAssistant;
  }

  public void setProfitAchievementByAssistant(Double profitAchievementByAssistant) {
    this.profitAchievementByAssistant = NumberUtil.toReserve(profitAchievementByAssistant,NumberUtil.PRECISION);
  }

  public Double getProfit() {
    return profit;
  }

  public void setProfit(Double profit) {
    this.profit = NumberUtil.toReserve(profit,NumberUtil.PRECISION);
  }
}
