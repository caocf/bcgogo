package com.bcgogo.txn.dto;

import com.bcgogo.enums.CustomerStatus;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.utils.JsonUtil;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.StringUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 会员消费统计返回结果封装
 * Created by IntelliJ IDEA.
 * User: LiuWei
 * Date: 12-11-12
 * Time: 下午5:24
 * To change this template use File | Settings | File Templates.
 */
public class MemberStatResultDTO implements Serializable {
  public Long shopId;
  public Long orderId;
  public OrderTypes orderType;
  public String orderIdStr;
  public Long customerId;
  public String customerName;
  public String memberNo;
  public String receiptNo;
  public String memberBalanceChange;
  public String memberServiceChange;
  public String memberServiceChangeShort;
  public double total;
  public double settledAmount;
  public double discount;
  public double debt;
  public String vestDateStr;
  public Long vestDate;
  public String orderTypeStr;
  public String consumeVehicleNo;
  public String consumeType;

  public String consumeContent;
  public String memberType;
  public String memberReturnContent;
  public double memberBalance;

  public String memberQueryType;

  public double resultTotal;
  public double resultSettledAmount;
  public double pageTotal;
  public double pageTotalSettledAmount;
  public double pageDebt;
  public double pageDiscount;
  public List<MemberStatResultDTO> orders = new ArrayList<MemberStatResultDTO>();

  public List<MemberStatResultDTO> getOrders() {
    return orders;
  }

  public void setOrders(List<MemberStatResultDTO> orders) {
    this.orders = orders;
  }

  public CustomerStatus getCustomerStatus() {
    return customerStatus;
  }

  public void setCustomerStatus(CustomerStatus customerStatus) {
    this.customerStatus = customerStatus;
  }

  public CustomerStatus customerStatus;

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public double getPageTotal() {
    return pageTotal;
  }

  public void setPageTotal(double pageTotal) {
    this.pageTotal = NumberUtil.toReserve(pageTotal,NumberUtil.MONEY_PRECISION);
  }

  public double getPageTotalSettledAmount() {
    return pageTotalSettledAmount;
  }

  public void setPageTotalSettledAmount(double pageTotalSettledAmount) {
    this.pageTotalSettledAmount = NumberUtil.toReserve(pageTotalSettledAmount,NumberUtil.MONEY_PRECISION);
  }

  public double getPageDebt() {
    return pageDebt;
  }

  public void setPageDebt(double pageDebt) {
    this.pageDebt =  NumberUtil.toReserve(pageDebt,NumberUtil.MONEY_PRECISION);
  }

  public double getPageDiscount() {
    return pageDiscount;
  }

  public void setPageDiscount(double pageDiscount) {
    this.pageDiscount = NumberUtil.toReserve(pageDiscount,NumberUtil.MONEY_PRECISION);
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

  public String getOrderIdStr() {
    return orderIdStr;
  }

  public void setOrderIdStr(String orderIdStr) {
    this.orderIdStr = orderIdStr;
  }

  public Long getCustomerId() {
    return customerId;
  }

  public void setCustomerId(Long customerId) {
    this.customerId = customerId;
  }

  public String getCustomerName() {
    return customerName;
  }

  public void setCustomerName(String customerName) {
    this.customerName = customerName;
  }

  public String getMemberNo() {
    return memberNo;
  }

  public void setMemberNo(String memberNo) {
    this.memberNo = memberNo;
  }

  public String getReceiptNo() {
    return receiptNo;
  }

  public void setReceiptNo(String receiptNo) {
    this.receiptNo = receiptNo;
  }

  public String getMemberBalanceChange() {
    return memberBalanceChange;
  }

  public void setMemberBalanceChange(String memberBalanceChange) {
    this.memberBalanceChange = memberBalanceChange;
  }

  public String getMemberServiceChange() {
    return memberServiceChange;
  }

  public void setMemberServiceChange(String memberServiceChange) {
    this.memberServiceChange = memberServiceChange;
    this.memberServiceChangeShort = StringUtil.getShortString(this.getMemberServiceChange(), 0, 25);
  }

  public double getTotal() {
    return total;
  }

  public void setTotal(double total) {
    this.total = NumberUtil.toReserve(total,NumberUtil.MONEY_PRECISION);
  }

  public double getSettledAmount() {
    return settledAmount;
  }

  public void setSettledAmount(double settledAmount) {
    this.settledAmount =  NumberUtil.toReserve(settledAmount,NumberUtil.MONEY_PRECISION);
  }

  public double getDiscount() {
    return discount;
  }

  public void setDiscount(double discount) {
    this.discount = NumberUtil.toReserve(discount,NumberUtil.MONEY_PRECISION);
  }

  public double getDebt() {
    return debt;
  }

  public void setDebt(double debt) {
    this.debt =  NumberUtil.toReserve(debt,NumberUtil.MONEY_PRECISION);
  }

  public String getVestDateStr() {
    return vestDateStr;
  }

  public void setVestDateStr(String vestDateStr) {
    this.vestDateStr = vestDateStr;
  }

  public Long getVestDate() {
    return vestDate;
  }

  public void setVestDate(Long vestDate) {
    this.vestDate = vestDate;
  }

  public String getOrderTypeStr() {
    return orderTypeStr;
  }

  public void setOrderTypeStr(String orderTypeStr) {
    this.orderTypeStr = orderTypeStr;
  }

  public String getConsumeVehicleNo() {
    return consumeVehicleNo;
  }

  public void setConsumeVehicleNo(String consumeVehicleNo) {
    this.consumeVehicleNo = consumeVehicleNo;
  }

  public String getConsumeType() {
    return consumeType;
  }

  public void setConsumeType(String consumeType) {
    this.consumeType = consumeType;
  }

  public String getConsumeContent() {
    return consumeContent;
  }

  public void setConsumeContent(String consumeContent) {
    this.consumeContent = consumeContent;
  }

  public String getMemberType() {
    return memberType;
  }

  public void setMemberType(String memberType) {
    this.memberType = memberType;
  }

  public String getMemberReturnContent() {
    return memberReturnContent;
  }

  public void setMemberReturnContent(String memberReturnContent) {
    this.memberReturnContent = memberReturnContent;
  }

  public double getMemberBalance() {
    return memberBalance;
  }

  public void setMemberBalance(double memberBalance) {
    this.memberBalance = NumberUtil.toReserve(memberBalance,NumberUtil.MONEY_PRECISION);
    if(memberBalance > 0){
      this.setMemberBalanceChange("增加" + memberBalance + "元");
    }else if(memberBalance < 0){

      this.setMemberBalanceChange("减少" + String.valueOf(0- memberBalance) + "元");
    }

  }

  public String getMemberQueryType() {
    return memberQueryType;
  }

  public void setMemberQueryType(String memberQueryType) {
    this.memberQueryType = memberQueryType;
  }

  public double getResultTotal() {
    return resultTotal;
  }

  public void setResultTotal(double resultTotal) {
    this.resultTotal = NumberUtil.toReserve(resultTotal,NumberUtil.MONEY_PRECISION);
  }

  public double getResultSettledAmount() {
    return resultSettledAmount;
  }

  public void setResultSettledAmount(double resultSettledAmount) {
    this.resultSettledAmount = NumberUtil.toReserve(resultSettledAmount,NumberUtil.MONEY_PRECISION);
  }

  public String getMemberServiceChangeShort() {
    return memberServiceChangeShort;
  }

  public void setMemberServiceChangeShort(String memberServiceChangeShort) {
    this.memberServiceChangeShort = StringUtil.getShortStringByNum(this.getMemberServiceChange(), 0, 10);
  }

  @Override
  public String toString() {
    return "MemberStatResultDTO{" +
        "shopId=" + shopId +
        ", orderId=" + orderId +
        ", orderType=" + orderType +
        ", orderIdStr='" + orderIdStr + '\'' +
        ", customerId=" + customerId +
        ", customerName='" + customerName + '\'' +
        ", memberNo='" + memberNo + '\'' +
        ", receiptNo='" + receiptNo + '\'' +
        ", memberBalanceChange='" + memberBalanceChange + '\'' +
        ", memberServiceChange='" + memberServiceChange + '\'' +
        ", total=" + total +
        ", settledAmount=" + settledAmount +
        ", discount=" + discount +
        ", debt=" + debt +
        ", vestDateStr='" + vestDateStr + '\'' +
        ", vestDate=" + vestDate +
        ", orderTypeStr='" + orderTypeStr + '\'' +
        ", consumeVehicleNo='" + consumeVehicleNo + '\'' +
        ", consumeType='" + consumeType + '\'' +
        ", consumeContent='" + consumeContent + '\'' +
        ", memberType='" + memberType + '\'' +
        ", memberReturnContent='" + memberReturnContent + '\'' +
        ", memberBalance=" + memberBalance +
        ", memberQueryType='" + memberQueryType + '\'' +
        ", resultTotal=" + resultTotal +
        ", resultSettledAmount=" + resultSettledAmount +
        ", pageTotal=" + pageTotal +
        ", pageTotalSettledAmount=" + pageTotalSettledAmount +
        ", pageDebt=" + pageDebt +
        ", pageDiscount=" + pageDiscount +
        ", memberStatResultDTOList=" + JsonUtil.listToJson(orders) +
        '}';
  }
}
