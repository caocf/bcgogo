package com.bcgogo.txn.dto;

import com.bcgogo.enums.DayType;
import com.bcgogo.enums.OrderStatus;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.StringUtil;

import java.io.Serializable;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: zhuyinjia
 * Date: 11-10-31
 * Time: 下午4:30
 * To change this template use File | Settings | File Templates.
 */
public class ReceptionRecordDTO implements Serializable {
  public ReceptionRecordDTO() {
  }

  private Long id;
  private Long receivableId;
  private String receivableNo;
  private double amount;
  private Long payeeId;
  private String payee;
  private Long receiveTime;
  private String memo;
  private Double memberBalancePay;
  private Double accumulatePointsPay;
  private Integer accumulatePoints;
  private List<ReceptionServiceTimesDTO> receptionServiceTimesDTOs;
  private String chequeNo;
  private Double cash; //现金支付金额
  private Double bankCard; //银行卡支付金额
  private Double cheque; //支票支付金额

  private Long memberId; //会员支付金额
  private Integer recordNum; //支付次数 每次自动增加1
  private Double originDebt;//原来欠款
  private Double discount;  //这次支付行为 折扣
  private Double remainDebt;//剩下的欠款
  private Long shopId;
  private Long orderId;
  private Long receptionDate;
  private Double strike;    //冲帐
  private Long toPayTime;
  private String toPayTimeStr;

  //流水统计扩展字段
  private String orderIdStr;
  private String receptionDateStr;
  private String customerName;
  private String vehicle;
  private String orderType;
  private String orderContent;
  private String shortOrderContent;
  private Double orderTotal;
  private Double deposit;  // commented by zhuj 预收款支付金额
  private OrderTypes orderTypeEnum;
  private OrderStatus orderStatusEnum;

  //流水统计扩展字段,流水统计页面下方列表:如果作废 非当天单据 流水统计下方列表要显示
  private DayType dayType;

  private Double memberDiscountRatio;
  private Double afterMemberDiscountTotal;
  private String memberNo;
  private Long saleReturnId;  //如果某个单据被销售退货单冲账 记录销售退货单id

  private String productNames;

  private Double statementAmount;//对账单支付

  private Long receivableHistoryId;

  private Double coupon;//代金券金额

  public String getMemberNo() {
    return memberNo;
  }

  public void setMemberNo(String memberNo) {
    this.memberNo = memberNo;
  }

  public Long getToPayTime() {
    return this.toPayTime;
  }

  public void setToPayTime(Long toPayTime)  {
    this.toPayTime = toPayTime;
    this.setToPayTimeStr(DateUtil.convertDateLongToDateString(DateUtil.DEFAULT,getToPayTime()));
  }

  public String getToPayTimeStr() {
    return toPayTimeStr;
  }

  public void setToPayTimeStr(String toPayTimeStr) {
    this.toPayTimeStr = toPayTimeStr;
  }

  public Long getSaleReturnId() {
    return saleReturnId;
  }

  public void setSaleReturnId(Long saleReturnId) {
    this.saleReturnId = saleReturnId;
  }

  public DayType getDayType() {
    return dayType;
  }

  public void setDayType(DayType dayType) {
    this.dayType = dayType;
  }

  public String getOrderIdStr() {
    return orderIdStr;
  }

  public void setOrderIdStr(String orderIdStr) {
    this.orderIdStr = orderIdStr;
  }

  public OrderTypes getOrderTypeEnum() {
    return orderTypeEnum;
  }

  public void setOrderTypeEnum(OrderTypes orderTypeEnum) {
    this.orderTypeEnum = orderTypeEnum;
  }

  public OrderStatus getOrderStatusEnum() {
    return orderStatusEnum;
  }

  public void setOrderStatusEnum(OrderStatus orderStatusEnum) {
    this.orderStatusEnum = orderStatusEnum;
  }


  public Double getDeposit() {
    return deposit;
  }

  public void setDeposit(Double deposit) {
    this.deposit = NumberUtil.toReserve(deposit,NumberUtil.MONEY_PRECISION);
  }

  public Double getOrderTotal() {
    return orderTotal;
  }

  public void setOrderTotal(Double orderTotal) {
    this.orderTotal = NumberUtil.toReserve(orderTotal,NumberUtil.MONEY_PRECISION);
  }

  public String getOrderType() {
    return orderType;
  }

  public void setOrderType(String orderType) {
    this.orderType = orderType;
  }

  public String getOrderContent() {
    return orderContent;
  }

  public void setOrderContent(String orderContent) {
    this.orderContent = orderContent;
    setShortOrderContent(StringUtil.getShortString(orderContent,0,8));
    }

  public String getShortOrderContent() {
    return shortOrderContent;
  }

  public void setShortOrderContent(String shortOrderContent) {
    this.shortOrderContent = shortOrderContent;
  }

  public String getReceptionDateStr() {
    return receptionDateStr;
  }

  public void setReceptionDateStr(String receptionDateStr) {
    this.receptionDateStr = receptionDateStr;
  }

  public String getCustomerName() {
    return customerName;
  }

  public void setCustomerName(String customerName) {
    this.customerName = customerName;
  }

  public String getVehicle() {
    return vehicle;
  }

  public void setVehicle(String vehicle) {
    this.vehicle = vehicle;
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

  public Long getReceptionDate() {
    return receptionDate;
  }

  public void setReceptionDate(Long receptionDate) {
    this.receptionDate = receptionDate;
  }

  public Double getCash() {
    return cash;
  }

  public void setCash(Double cash) {
    this.cash = NumberUtil.toReserve(cash,NumberUtil.MONEY_PRECISION);
  }

  public Double getBankCard() {
    return bankCard;
  }

  public void setBankCard(Double bankCard) {
    this.bankCard = NumberUtil.toReserve(bankCard,NumberUtil.MONEY_PRECISION);
  }

  public Double getCheque() {
    return cheque;
  }

  public void setCheque(Double cheque) {
    this.cheque = NumberUtil.toReserve(cheque,NumberUtil.MONEY_PRECISION);
  }

  public Long getMemberId() {
    return memberId;
  }

  public void setMemberId(Long memberId) {
    this.memberId = memberId;
  }

  public Integer getRecordNum() {
    return recordNum;
  }

  public void setRecordNum(Integer recordNum) {
    this.recordNum = recordNum;
  }

  public Double getOriginDebt() {
    return originDebt;
  }

  public void setOriginDebt(Double originDebt) {
    this.originDebt = NumberUtil.toReserve(originDebt,NumberUtil.MONEY_PRECISION);
  }

  public Double getDiscount() {
    return discount;
  }

  public void setDiscount(Double discount) {
    this.discount = NumberUtil.toReserve(discount,NumberUtil.MONEY_PRECISION);
  }

  public Double getRemainDebt() {
    return remainDebt;
  }

  public void setRemainDebt(Double remainDebt) {
    this.remainDebt = NumberUtil.toReserve(remainDebt,NumberUtil.MONEY_PRECISION);
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getReceivableId() {
    return receivableId;
  }

  public void setReceivableId(Long receivableId) {
    this.receivableId = receivableId;
  }

  public String getReceivableNo() {
    return receivableNo;
  }

  public void setReceivableNo(String receivableNo) {
    this.receivableNo = receivableNo;
  }

  public double getAmount() {
    return amount;
  }

  public void setAmount(double amount) {
    this.amount = NumberUtil.toReserve(amount,NumberUtil.MONEY_PRECISION);
  }

  public Long getPayeeId() {
    return payeeId;
  }

  public void setPayeeId(Long payeeId) {
    this.payeeId = payeeId;
  }

  public String getPayee() {
    return payee;
  }

  public void setPayee(String payee) {
    this.payee = payee;
  }

  public Long getReceiveTime() {
    return receiveTime;
  }

  public void setReceiveTime(Long receiveTime) {
    this.receiveTime = receiveTime;
  }

  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }

  public Double getMemberBalancePay() {
    return memberBalancePay;
  }

  public Double getAccumulatePointsPay() {
    return accumulatePointsPay;
  }

  public Integer getAccumulatePoints() {
    return accumulatePoints;
  }

  public String getChequeNo() {
    return chequeNo;
  }

  public void setMemberBalancePay(Double memberBalancePay) {
    this.memberBalancePay = NumberUtil.toReserve(memberBalancePay,NumberUtil.MONEY_PRECISION);
  }

  public void setAccumulatePointsPay(Double accumulatePointsPay) {
    this.accumulatePointsPay = NumberUtil.toReserve(accumulatePointsPay,NumberUtil.MONEY_PRECISION);
  }

  public void setAccumulatePoints(Integer accumulatePoints) {
    this.accumulatePoints = accumulatePoints;
  }

  public List<ReceptionServiceTimesDTO> getReceptionServiceTimesDTOs() {
    return receptionServiceTimesDTOs;
  }

  public void setReceptionServiceTimesDTOs(List<ReceptionServiceTimesDTO> receptionServiceTimesDTOs) {
    this.receptionServiceTimesDTOs = receptionServiceTimesDTOs;
  }

  public void setChequeNo(String chequeNo) {
    this.chequeNo = chequeNo;
  }

  public Double getStrike() {
    return strike;
  }

  public void setStrike(Double strike) {
    this.strike = strike;
  }

  public Double getMemberDiscountRatio() {
    return memberDiscountRatio;
  }

  public void setMemberDiscountRatio(Double memberDiscountRatio) {
    this.memberDiscountRatio = memberDiscountRatio;
  }

  public Double getAfterMemberDiscountTotal() {
    return afterMemberDiscountTotal;
  }

  public void setAfterMemberDiscountTotal(Double afterMemberDiscountTotal) {
    this.afterMemberDiscountTotal = afterMemberDiscountTotal;
  }

  public String getProductNames() {
    return productNames;
  }

  public void setProductNames(String productNames) {
    this.productNames = productNames;
  }

  public Double getStatementAmount() {
    return statementAmount;
  }

  public void setStatementAmount(Double statementAmount) {
    this.statementAmount = statementAmount;
  }

  public Long getReceivableHistoryId() {
    return receivableHistoryId;
  }

  public void setReceivableHistoryId(Long receivableHistoryId) {
    this.receivableHistoryId = receivableHistoryId;
  }

  public Double getCoupon() {
    if(null==coupon){
      return 0D;
    }
    return coupon;
  }

  public void setCoupon(Double coupon) {
    if(null==coupon){
      this.coupon = 0D;
    }
    else {
      this.coupon = coupon;
    }
  }

  @Override
  public String toString() {
    return "ReceptionRecordDTO{" +
        "id=" + id +
        ", receivableId=" + receivableId +
        ", receivableNo='" + receivableNo + '\'' +
        ", amount=" + amount +
        ", payeeId=" + payeeId +
        ", payee='" + payee + '\'' +
        ", receiveTime=" + receiveTime +
        ", memo='" + memo + '\'' +
        ", memberBalancePay=" + memberBalancePay +
        ", accumulatePointsPay=" + accumulatePointsPay +
        ", accumulatePoints=" + accumulatePoints +
        ", receptionServiceTimesDTOs=" + receptionServiceTimesDTOs +
        ", chequeNo='" + chequeNo + '\'' +
        ", cash=" + cash +
        ", bankCard=" + bankCard +
        ", cheque=" + cheque +
        ", memberId=" + memberId +
        ", recordNum=" + recordNum +
        ", originDebt=" + originDebt +
        ", discount=" + discount +
        ", remainDebt=" + remainDebt +
        ", shopId=" + shopId +
        ", orderId=" + orderId +
        ", receptionDate=" + receptionDate +
        ", strike=" + strike +
        ", orderIdStr='" + orderIdStr + '\'' +
        ", receptionDateStr='" + receptionDateStr + '\'' +
        ", customerName='" + customerName + '\'' +
        ", vehicle='" + vehicle + '\'' +
        ", orderType='" + orderType + '\'' +
        ", orderContent='" + orderContent + '\'' +
        ", shortOrderContent='" + shortOrderContent + '\'' +
        ", orderTotal=" + orderTotal +
        ", deposit=" + deposit +
        ", orderTypeEnum=" + orderTypeEnum +
        ", orderStatusEnum=" + orderStatusEnum +
        ", dayType=" + dayType +
        ", memberDiscountRatio=" + memberDiscountRatio +
        ", afterMemberDiscountTotal=" + afterMemberDiscountTotal +
        ", saleReturnId=" + saleReturnId +
        ", productNames='" + productNames + '\'' +
        ", statementAmount=" + statementAmount +
        ", receivableHistoryId=" + receivableHistoryId +
        '}';
  }
}
