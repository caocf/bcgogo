package com.bcgogo.txn.model;

import com.bcgogo.enums.OrderTypes;
import com.bcgogo.enums.ReceivableStatus;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.ReceivableDTO;
import com.bcgogo.txn.dto.StatementAccount.OrderDebtType;
import com.bcgogo.utils.NumberUtil;

import javax.persistence.*;

/**
 * Created by IntelliJ IDEA.
 * User: MZDong
 * Date: 11-9-19
 * To change this template use File | Settings | File Templates.
 */

@Entity
@Table(name = "receivable")
public class Receivable extends LongIdentifier {
  public Receivable() {
  }

  public Receivable fromDTO(ReceivableDTO receivableDTO) {
    if(receivableDTO == null)
      return this;
    setId(receivableDTO.getId());
    this.shopId = receivableDTO.getShopId();
    this.date = receivableDTO.getDate();
    this.no = receivableDTO.getNo();
    this.orderTypeEnum = receivableDTO.getOrderType();
    this.orderId = receivableDTO.getOrderId();
    this.orderNo = receivableDTO.getOrderNo();
    this.lastPayeeId = receivableDTO.getLastPayeeId();
    this.lastPayee = receivableDTO.getLastPayee();
    this.lastReceiveDate = receivableDTO.getLastReceiveDate();
    this.statusEnum = receivableDTO.getStatus();
    this.settledAmount = receivableDTO.getSettledAmount();
    this.debt = receivableDTO.getDebt();
    this.discount = receivableDTO.getDiscount();
    this.total = receivableDTO.getTotal();
    this.memberBalancePay = receivableDTO.getMemberBalancePay();
    this.accumulatePointsPay =receivableDTO.getAccumulatePointsPay();
    this.accumulatePoints = receivableDTO.getAccumulatePoints();
    this.cash = (receivableDTO.getCash() == null ? 0 : receivableDTO.getCash());
    this.bankCard = (receivableDTO.getBankCard() == null ? 0 : receivableDTO.getBankCard());
    this.cheque = (receivableDTO.getCheque() == null ? 0 : receivableDTO.getCheque());
    this.deposit = (receivableDTO.getDeposit() == null ? 0 : receivableDTO.getDeposit());
    this.memberId = receivableDTO.getMemberId();
    this.setOrderTypeEnum(receivableDTO.getOrderType());
    this.setMemberNo(receivableDTO.getMemberNo());
    this.setStrike(receivableDTO.getStrike());
    this.memberDiscountRatio = receivableDTO.getMemberDiscountRatio();
    this.afterMemberDiscountTotal = receivableDTO.getAfterMemberDiscountTotal();
    this.customerId = receivableDTO.getCustomerId();
    this.vestDate = receivableDTO.getVestDate();
    this.receiptNo = receivableDTO.getReceiptNo();
    this.statementAmount = receivableDTO.getStatementAmount();
    this.orderDebtType = receivableDTO.getOrderDebtType();
    this.statementAccountOrderId = receivableDTO.getStatementAccountOrderId();
    this.remindTime = receivableDTO.getRemindTime();
    this.coupon = receivableDTO.getCoupon();
    return this;
  }

  public ReceivableDTO toDTO() {
    ReceivableDTO receivableDTO = new ReceivableDTO();
    receivableDTO.setId(getId());
    receivableDTO.setShopId(getShopId());
    receivableDTO.setDate(getDate());
    receivableDTO.setNo(getNo());
    receivableDTO.setOrderType(getOrderTypeEnum());
    receivableDTO.setOrderId(getOrderId());
    receivableDTO.setOrderNo(getOrderNo());
    receivableDTO.setLastPayeeId(getLastPayeeId());
    receivableDTO.setLastPayee(getLastPayee());
    receivableDTO.setLastReceiveDate(getLastReceiveDate());
    receivableDTO.setStatus(getStatusEnum());
    receivableDTO.setSettledAmount(getSettledAmount());
    receivableDTO.setDebt(getDebt());
    receivableDTO.setDiscount(getDiscount());
    receivableDTO.setTotal(getTotal());

    receivableDTO.setAccumulatePoints(this.getAccumulatePoints());
    receivableDTO.setAccumulatePointsPay(this.getAccumulatePointsPay());
    receivableDTO.setBankCard(this.getBankCard());
    receivableDTO.setCash(this.getCash());
    receivableDTO.setCheque(this.getCheque());
    receivableDTO.setDeposit(this.getDeposit());
    receivableDTO.setMemberBalancePay(this.getMemberBalancePay());
    receivableDTO.setMemberId(this.getMemberId());
    receivableDTO.setCreationDate(this.getCreationDate());
    receivableDTO.setMemberNo(this.getMemberNo());
    receivableDTO.setStrike(this.getStrike());
    receivableDTO.setMemberDiscountRatio(this.getMemberDiscountRatio());
    receivableDTO.setAfterMemberDiscountTotal(this.getAfterMemberDiscountTotal());
    receivableDTO.setCustomerId(this.getCustomerId());

    receivableDTO.setVestDate(this.getVestDate());
    receivableDTO.setReceiptNo(this.getReceiptNo());
    receivableDTO.setStatementAmount(getStatementAmount());
    receivableDTO.setOrderDebtType(getOrderDebtType());
    receivableDTO.setStatementAccountOrderId(getStatementAccountOrderId());
    receivableDTO.setRemindTime(getRemindTime());
    receivableDTO.setCoupon(getCoupon());
    return receivableDTO;
  }

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "date")
  public Long getDate() {
    return date;
  }

  public void setDate(Long date) {
    this.date = date;
  }

  @Column(name = "no", length = 20)
  public String getNo() {
    return no;
  }

  public void setNo(String no) {
    this.no = no;
  }

  @Column(name = "order_type")
  public Long getOrderType() {
    return orderType;
  }

  public void setOrderType(Long orderType) {
    this.orderType = orderType;
  }

  @Column(name="order_type_enum")
  @Enumerated(EnumType.STRING)
  public OrderTypes getOrderTypeEnum() {
    return orderTypeEnum;
  }

  public void setOrderTypeEnum(OrderTypes orderTypeEnum) {
    this.orderTypeEnum = orderTypeEnum;
  }

  @Column(name = "order_id")
  public Long getOrderId() {
    return orderId;
  }

  public void setOrderId(Long orderId) {
    this.orderId = orderId;
  }

  @Column(name = "order_no", length = 20)
  public String getOrderNo() {
    return orderNo;
  }

  public void setOrderNo(String orderNo) {
    this.orderNo = orderNo;
  }

  @Column(name = "last_payee_id")
  public Long getLastPayeeId() {
    return lastPayeeId;
  }

  public void setLastPayeeId(Long lastPayeeId) {
    this.lastPayeeId = lastPayeeId;
  }

  @Column(name="last_payee")
  public String getLastPayee() {
    return lastPayee;
  }

  public void setLastPayee(String lastPayee) {
    this.lastPayee = lastPayee;
  }

  @Column(name = "last_receive_date")
  public Long getLastReceiveDate() {
    return lastReceiveDate;
  }

  public void setLastReceiveDate(Long lastReceiveDate) {
    this.lastReceiveDate = lastReceiveDate;
  }

  @Column(name = "status")
  public Long getStatus() {
    return status;
  }

  public void setStatus(Long status) {
    this.status = status;
  }

  @Column(name = "status_enum")
  @Enumerated(EnumType.STRING)
  public ReceivableStatus getStatusEnum() {
    return statusEnum;
  }

  public void setStatusEnum(ReceivableStatus statusEnum) {
    this.statusEnum = statusEnum;
  }

  @Column(name = "settled_amount")
  public double getSettledAmount() {
    return settledAmount;
  }

  public void setSettledAmount(double settledAmount) {
    this.settledAmount = NumberUtil.toReserve(settledAmount,NumberUtil.MONEY_PRECISION);
  }

  @Column(name = "debt")
  public double getDebt() {
    return debt;
  }

  public void setDebt(double debt) {
    this.debt = NumberUtil.toReserve(debt,NumberUtil.MONEY_PRECISION);
  }

  @Column(name = "discount")
  public Double getDiscount() {
    return discount;
  }

  public void setDiscount(Double discount) {
    this.discount = NumberUtil.toReserve(discount,NumberUtil.MONEY_PRECISION);
  }

  @Column(name = "total")
  public Double getTotal() {
    return total;
  }

  public void setTotal(Double total) {
    this.total = NumberUtil.toReserve(total,NumberUtil.MONEY_PRECISION);
  }

  @Column(name="member_balance_pay")
  public Double getMemberBalancePay() {
      return memberBalancePay;
  }
  @Column(name="accumulate_points_pay")
  public Double getAccumulatePointsPay() {
      return accumulatePointsPay;
  }
  @Column(name="accumulate_points")
  public Integer getAccumulatePoints() {
      return accumulatePoints;
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

  @Column(name="cash")
  public Double getCash() {
    return cash;
  }

  @Column(name="bank_card")
  public Double getBankCard() {
    return bankCard;
  }

  @Column(name="cheque")
  public Double getCheque() {
    return cheque;
  }

  public void setCash(Double cash) {
    this.cash = NumberUtil.toReserve(cash,NumberUtil.MONEY_PRECISION);
  }

  public void setBankCard(Double bankCard) {
    this.bankCard = NumberUtil.toReserve(bankCard,NumberUtil.MONEY_PRECISION);
  }

  public void setCheque(Double cheque) {
    this.cheque = NumberUtil.toReserve(cheque,NumberUtil.MONEY_PRECISION);
  }

  @Column(name="member_id")
	public Long getMemberId() {
		return memberId;
	}

	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}
  @Column(name="member_no")
  public String getMemberNo() {
    return memberNo;
  }

  public void setMemberNo(String memberNo) {
    this.memberNo = memberNo;
  }

  @Column(name="member_discount_ratio")
  public Double getMemberDiscountRatio() {
    return memberDiscountRatio;
  }

  public void setMemberDiscountRatio(Double memberDiscountRatio) {
    this.memberDiscountRatio = memberDiscountRatio;
  }

  @Column(name="after_member_discount_total")
  public Double getAfterMemberDiscountTotal() {
    return afterMemberDiscountTotal;
  }

  public void setAfterMemberDiscountTotal(Double afterMemberDiscountTotal) {
    this.afterMemberDiscountTotal = afterMemberDiscountTotal;
  }


  @Column(name="strike")
  public Double getStrike() {
    return strike;
  }

  public void setStrike(Double strike) {
    this.strike = strike;
  }

  @Column(name = "customer_id")
  public Long getCustomerId() {
    return customerId;
  }

  public void setCustomerId(Long customerId) {
    this.customerId = customerId;
  }

  @Column(name = "receipt_no")
  public String getReceiptNo() {
    return receiptNo;
  }

  public void setReceiptNo(String receiptNo) {
    this.receiptNo = receiptNo;
  }

  @Column(name = "vest_date")
  public Long getVestDate() {
    return vestDate;
  }

  public void setVestDate(Long vestDate) {
    this.vestDate = vestDate;
  }

  @Column(name = "statement_amount")
  public Double getStatementAmount() {
    return statementAmount;
  }

  public void setStatementAmount(Double statementAmount) {
    this.statementAmount = statementAmount;
  }

  @Column(name = "debt_type")
  @Enumerated(EnumType.STRING)
  public OrderDebtType getOrderDebtType() {
    return orderDebtType;
  }

  public void setOrderDebtType(OrderDebtType orderDebtType) {
    this.orderDebtType = orderDebtType;
  }

  @Column(name = "statement_account_order_id")
  public Long getStatementAccountOrderId() {
    return statementAccountOrderId;
  }

  public void setStatementAccountOrderId(Long statementAccountOrderId) {
    this.statementAccountOrderId = statementAccountOrderId;
  }

  @Column(name = "remind_time")
  public Long getRemindTime() {
    return remindTime;
  }

  public void setRemindTime(Long remindTime) {
    this.remindTime = remindTime;
  }

  @Column(name = "deposit")
  public Double getDeposit() {
    return deposit;
  }

  public void setDeposit(Double deposit) {
    this.deposit = deposit;
  }

  @Column(name = "coupon")
  public Double getCoupon() {
    return coupon;
  }

  public void setCoupon(Double coupon) {
    this.coupon = coupon;
  }

  private Long shopId;
  private Long date;
  private String no;
  /**
   * 1:维修单
   * 2:销售单
   * 3:洗车单
   */
  private Long orderType;
  private OrderTypes orderTypeEnum;
  private Long orderId;
  private String orderNo;
  private Long lastPayeeId;
  private String lastPayee;   //最后收款人
  private Long lastReceiveDate;
  private Long status;
  private ReceivableStatus statusEnum;
  private double settledAmount;
  private double debt;
  private Double discount;
  private Double total;

  private Double memberBalancePay;
  private Double accumulatePointsPay;
  private Integer accumulatePoints;
  private Double cash;
  private Double bankCard;
  private Double cheque;
  private Double deposit; //add by zhuj 预收款支付
  private Long memberId;
  private String memberNo;//支付时使用的会员号码
  private Double strike;  //冲帐

  private Double memberDiscountRatio;
  private Double afterMemberDiscountTotal;

  private Long customerId;//客户id
  private String receiptNo;//单据号码
  private Long vestDate;//单据结算时间
  private Double statementAmount;//对账单结算 使用对账结算金额；
  private OrderDebtType orderDebtType;//客户欠款类型
  private Long  statementAccountOrderId;//对账单id
  private Long remindTime;//单据还款时间

  private Double coupon; //代金券消费金额 add by litao

  @Override
  public String toString() {
    return "Receivable{" +
        "shopId=" + shopId +
        ", date=" + date +
        ", no='" + no + '\'' +
        ", orderType=" + orderType +
        ", orderTypeEnum=" + orderTypeEnum +
        ", orderId=" + orderId +
        ", orderNo='" + orderNo + '\'' +
        ", lastPayeeId=" + lastPayeeId +
        ", lastPayee='" + lastPayee + '\'' +
        ", lastReceiveDate=" + lastReceiveDate +
        ", status=" + status +
        ", statusEnum=" + statusEnum +
        ", settledAmount=" + settledAmount +
        ", debt=" + debt +
        ", discount=" + discount +
        ", total=" + total +
        ", memberBalancePay=" + memberBalancePay +
        ", accumulatePointsPay=" + accumulatePointsPay +
        ", accumulatePoints=" + accumulatePoints +
        ", cash=" + cash +
        ", bankCard=" + bankCard +
        ", cheque=" + cheque +
        ", deposit=" + deposit +
        ", memberId=" + memberId +
        ", memberNo='" + memberNo + '\'' +
        ", strike=" + strike +
        ", memberDiscountRatio=" + memberDiscountRatio +
        ", afterMemberDiscountTotal=" + afterMemberDiscountTotal +
        ", customerId=" + customerId +
        ", receiptNo='" + receiptNo + '\'' +
        ", vestDate=" + vestDate +
        ", statementAmount=" + statementAmount +
        ", orderDebtType=" + orderDebtType +
        ", statementAccountOrderId=" + statementAccountOrderId +
        ", remindTime=" + remindTime +
        '}';
  }
}
