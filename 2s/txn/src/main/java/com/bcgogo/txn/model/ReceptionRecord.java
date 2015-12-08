package com.bcgogo.txn.model;

import com.bcgogo.enums.DayType;
import com.bcgogo.enums.OrderStatus;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.ReceptionRecordDTO;
import com.bcgogo.utils.NumberUtil;
import org.hibernate.Session;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: MZDong
 * Date: 11-9-19
 * To change this template use File | Settings | File Templates.
 */

@Entity
@Table(name = "reception_record")
public class ReceptionRecord extends LongIdentifier {
  public ReceptionRecord() {
  }

  public ReceptionRecord fromDTO(ReceptionRecordDTO recordDTO) {
    if(recordDTO == null)
      return this;

    if (recordDTO.getId() != null) {
    setId(recordDTO.getId());
    }
    this.receivableId = recordDTO.getReceivableId();
    this.receivableNo = recordDTO.getReceivableNo();
    this.setAmount(recordDTO.getAmount());
    this.payeeId = recordDTO.getPayeeId();
    this.payee = recordDTO.getPayee();
    this.receiveTime = recordDTO.getReceiveTime();

    //会员相关
    this.setMemberBalancePay(recordDTO.getMemberBalancePay());
    this.setAccumulatePointsPay(recordDTO.getAccumulatePointsPay());
    this.accumulatePoints = recordDTO.getAccumulatePoints();
    this.chequeNo = recordDTO.getChequeNo();
    this.setCash(recordDTO.getCash());
    this.setBankCard(recordDTO.getBankCard());
    this.setCheque(recordDTO.getCheque());
    this.setDeposit(recordDTO.getDeposit()); // add by zhuj
    this.memberId = recordDTO.getMemberId();
    this.recordNum = recordDTO.getRecordNum();
    this.setOriginDebt(recordDTO.getOriginDebt());
    this.setDiscount(recordDTO.getDiscount());
    this.setRemainDebt(recordDTO.getRemainDebt());
    this.shopId = recordDTO.getShopId();
    this.orderId = recordDTO.getOrderId();
    this.receptionDate = recordDTO.getReceptionDate();

    this.orderId = recordDTO.getOrderId();
    this.shopId = recordDTO.getShopId();
    this.receptionDate = recordDTO.getReceptionDate();
    this.setOrderTotal(recordDTO.getOrderTotal());
    this.orderStatusEnum = recordDTO.getOrderStatusEnum();
    this.orderTypeEnum = recordDTO.getOrderTypeEnum();
    this.dayType = recordDTO.getDayType();
    this.memberDiscountRatio = recordDTO.getMemberDiscountRatio();
    this.afterMemberDiscountTotal =recordDTO.getAfterMemberDiscountTotal();
    this.setStrike(recordDTO.getStrike());
    this.saleReturnId = recordDTO.getSaleReturnId();
    this.statementAmount = recordDTO.getStatementAmount();
    this.receivableHistoryId = recordDTO.getReceivableHistoryId();
    this.toPayTime = recordDTO.getToPayTime();
    this.memo = recordDTO.getMemo();
    this.coupon = recordDTO.getCoupon();

    return this;
  }

  public ReceptionRecordDTO toDTO() {
    ReceptionRecordDTO receptionRecordDTO = new ReceptionRecordDTO();
    receptionRecordDTO.setId(getId());
    receptionRecordDTO.setReceivableId(getReceivableId());
    receptionRecordDTO.setReceivableNo(getReceivableNo());
    receptionRecordDTO.setAmount(getAmount());
    receptionRecordDTO.setPayeeId(getPayeeId());
    receptionRecordDTO.setPayee(getPayee());
    receptionRecordDTO.setReceiveTime(getReceiveTime());

    //会员相关
    receptionRecordDTO.setMemberBalancePay(getMemberBalancePay());
    receptionRecordDTO.setAccumulatePoints(getAccumulatePoints());
    receptionRecordDTO.setAccumulatePointsPay(getAccumulatePointsPay());
    receptionRecordDTO.setChequeNo(getChequeNo());
    receptionRecordDTO.setCash(getCash());
    receptionRecordDTO.setBankCard(getBankCard());
    receptionRecordDTO.setCheque(getCheque());
    receptionRecordDTO.setDeposit(getDeposit()); // add by zhuj 
    receptionRecordDTO.setMemberId(getMemberId());
    receptionRecordDTO.setRecordNum(getRecordNum());
    receptionRecordDTO.setOriginDebt(getOriginDebt());
    receptionRecordDTO.setDiscount(getDiscount());
    receptionRecordDTO.setRemainDebt(getRemainDebt());
    receptionRecordDTO.setShopId(getShopId());
    receptionRecordDTO.setOrderId(getOrderId());
    receptionRecordDTO.setReceptionDate(getReceptionDate() == null ? getCreationDate() : getReceptionDate());
    receptionRecordDTO.setOrderTotal(getOrderTotal());
    receptionRecordDTO.setOrderTypeEnum(getOrderTypeEnum());
    receptionRecordDTO.setOrderStatusEnum(getOrderStatusEnum());
    receptionRecordDTO.setDayType(getDayType());
    receptionRecordDTO.setStrike(getStrike());
    if (getOrderId() != null) {
      receptionRecordDTO.setOrderIdStr(String.valueOf(getOrderId()));
    }
    receptionRecordDTO.setMemberDiscountRatio(getMemberDiscountRatio());
    receptionRecordDTO.setAfterMemberDiscountTotal(getAfterMemberDiscountTotal());
    receptionRecordDTO.setSaleReturnId(getSaleReturnId());
    receptionRecordDTO.setStatementAmount(getStatementAmount());

    receptionRecordDTO.setReceivableHistoryId(getReceivableHistoryId());
    receptionRecordDTO.setToPayTime(getToPayTime());
    receptionRecordDTO.setMemo(getMemo());
    receptionRecordDTO.setCoupon(getCoupon());

    return receptionRecordDTO;
  }


  public ReceivableHistory toReceivableHistory() {
    ReceivableHistory receivableHistory = new ReceivableHistory();
    receivableHistory.setShopId(getShopId());
    receivableHistory.setTotal(getAfterMemberDiscountTotal() == null ? NumberUtil.doubleVal(getOrderTotal()) : getAfterMemberDiscountTotal().doubleValue());
    receivableHistory.setDiscount(NumberUtil.doubleVal(getDiscount()));
    receivableHistory.setDebt(NumberUtil.doubleVal(getRemainDebt()));
    receivableHistory.setCash(NumberUtil.doubleVal(getCash()));
    receivableHistory.setBankCardAmount(NumberUtil.doubleVal(getBankCard()));
    receivableHistory.setCheckAmount(NumberUtil.doubleVal(getCheque()));
    receivableHistory.setCheckNo("支票号".equals(getChequeNo()) ? "" : getChequeNo());
    receivableHistory.setDeposit(NumberUtil.doubleVal(getDeposit())); // add by zhuj
    receivableHistory.setMemberBalancePay(NumberUtil.doubleVal(getMemberBalancePay()));
    receivableHistory.setMemberId(getMemberId());
    receivableHistory.setMemberNo("");
    receivableHistory.setStrikeAmount(NumberUtil.doubleVal(getStrike()));
    receivableHistory.setSettledAmount(NumberUtil.doubleVal(getAmount()));
    receivableHistory.setReceivableDate(getReceptionDate() == null ? getCreationDate() : getReceptionDate());
    receivableHistory.setReceiver(getPayee());
    receivableHistory.setReceiverId(getPayeeId());
    return receivableHistory;
  }


  @Column(name = "receivable_id")
  public Long getReceivableId() {
    return receivableId;
  }

  public void setReceivableId(Long receivableId) {
    this.receivableId = receivableId;
  }

  @Column(name = "receivable_no", length = 20)
  public String getReceivableNo() {
    return receivableNo;
  }

  public void setReceivableNo(String receivableNo) {
    this.receivableNo = receivableNo;
  }

  @Column(name = "amount")
  public double getAmount() {
    return amount;
  }

  public void setAmount(double amount) {
    this.amount = amount;
  }

  @Column(name = "payee_id", length = 20)
  public Long getPayeeId() {
    return payeeId;
  }

  public void setPayeeId(Long payeeId) {
    this.payeeId = payeeId;
  }

  @Column(name="payee")
  public String getPayee() {
    return payee;
  }

  public void setPayee(String payee) {
    this.payee = payee;
  }

  @Column(name = "receive_time")
  public Long getReceiveTime() {
    return receiveTime;
  }

  public void setReceiveTime(Long receiveTime) {
    this.receiveTime = receiveTime;
  }

  private Long receivableId;
  private String receivableNo;
  private double amount;
  private Long payeeId;
  private String payee;         //收款人
  private Long receiveTime;
  private Double memberBalancePay;
  private Double accumulatePointsPay;
  private Integer accumulatePoints;
  private String chequeNo;

  private Double cash; //现金支付金额
  private Double bankCard; //银行卡支付金额
	private Double cheque; //支票支付金额
  private Double deposit; // 预收款支付金额

  private Long memberId; //会员支付金额
  private Integer recordNum; //支付次数 每次自动增加1
  private Double originDebt;//原来欠款
  private Double discount;  //这次支付行为 折扣
  private Double remainDebt;//剩下的欠款
  private Long shopId;
  private Long orderId;
  private Long receptionDate;
  private Double orderTotal;

  private Double strike;    //冲帐

  private OrderTypes orderTypeEnum;
  private OrderStatus orderStatusEnum;

  //流水统计扩展字段,流水统计页面下方列表:如果作废 非当天单据 流水统计下方列表要显示
  private DayType dayType;
  private Long saleReturnId;  //如果某个单据被销售退货单冲账 记录销售退货单id

  private Double memberDiscountRatio;
  private Double afterMemberDiscountTotal;

  private Double statementAmount;//对账单付款

  private Long receivableHistoryId;//收款历史id

  private Long toPayTime; //预计还款时间

  private String memo;//目前只给营业外记账用 用作记录营业外记账的内容

  private Double coupon;//代金券金额

  @Column(name = "to_pay_time")
  public Long getToPayTime() {
    return toPayTime;
  }

  public void setToPayTime(Long toPayTime) {
    this.toPayTime = toPayTime;
  }

  @Column(name = "sale_return_id")
  public Long getSaleReturnId() {
    return saleReturnId;
  }

  public void setSaleReturnId(Long saleReturnId) {
    this.saleReturnId = saleReturnId;
  }

  @Column(name = "day_type")
  @Enumerated(EnumType.STRING)
  public DayType getDayType() {
    return dayType;
  }

  public void setDayType(DayType dayType) {
    this.dayType = dayType;
  }

  @Column(name = "order_type_enum")
  @Enumerated(EnumType.STRING)
  public OrderTypes getOrderTypeEnum() {
    return orderTypeEnum;
  }

  public void setOrderTypeEnum(OrderTypes orderTypeEnum) {
    this.orderTypeEnum = orderTypeEnum;
  }

  @Column(name = "order_status_enum")
  @Enumerated(EnumType.STRING)
  public OrderStatus getOrderStatusEnum() {
    return orderStatusEnum;
  }

  public void setOrderStatusEnum(OrderStatus orderStatusEnum) {
    this.orderStatusEnum = orderStatusEnum;
  }

  @Column(name = "order_total")
  public Double getOrderTotal() {
    return orderTotal;
  }

  public void setOrderTotal(Double orderTotal) {
    this.orderTotal = NumberUtil.toReserve(orderTotal,NumberUtil.MONEY_PRECISION);
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

  @Column(name = "reception_date")
  public Long getReceptionDate() {
    return receptionDate;
  }

  public void setReceptionDate(Long receptionDate) {
    this.receptionDate = receptionDate;
  }

  @Column(name = "cash")
	public Double getCash() {
		return cash;
	}

	public void setCash(Double cash) {
		this.cash = NumberUtil.toReserve(cash,NumberUtil.MONEY_PRECISION);
	}

  @Column(name = "bank_card")
	public Double getBankCard() {
		return bankCard;
	}

	public void setBankCard(Double bankCard) {
		this.bankCard = NumberUtil.toReserve(bankCard,NumberUtil.MONEY_PRECISION);
	}

  @Column(name = "cheque")
	public Double getCheque() {
		return cheque;
	}

	public void setCheque(Double cheque) {
		this.cheque = NumberUtil.toReserve(cheque,NumberUtil.MONEY_PRECISION);
	}

  @Column(name = "member_id")
	public Long getMemberId() {
		return memberId;
	}

	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}

  @Column(name = "record_num")
	public Integer getRecordNum() {
		return recordNum;
	}

	public void setRecordNum(Integer recordNum) {
		this.recordNum = recordNum;
	}

  @Column(name = "origin_debt")
	public Double getOriginDebt() {
		return originDebt;
	}

	public void setOriginDebt(Double originDebt) {
		this.originDebt = NumberUtil.toReserve(originDebt,NumberUtil.MONEY_PRECISION);
	}

  @Column(name = "discount")
	public Double getDiscount() {
		return discount;
	}

	public void setDiscount(Double discount) {
		this.discount = NumberUtil.toReserve(discount,NumberUtil.MONEY_PRECISION);
	}

  @Column(name = "remain_debt")
	public Double getRemainDebt() {
		return remainDebt;
	}

	public void setRemainDebt(Double remainDebt) {
		this.remainDebt = NumberUtil.toReserve(remainDebt,NumberUtil.MONEY_PRECISION);
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
  @Column(name = "cheque_no")
  public String getChequeNo() {
    return chequeNo;
  }

  public void setMemberBalancePay(Double memberBalancePay) {
      this.memberBalancePay = NumberUtil.toReserve(memberBalancePay,NumberUtil.MONEY_PRECISION);
  }

  public void setAccumulatePoints(Integer accumulatePoints) {
      this.accumulatePoints = accumulatePoints;
  }

  public void setAccumulatePointsPay(Double accumulatePointsPay) {
      this.accumulatePointsPay = NumberUtil.toReserve(accumulatePointsPay,NumberUtil.MONEY_PRECISION);
  }

  public void setChequeNo(String chequeNo) {
    this.chequeNo = chequeNo;
  }

  @Column(name="strike")
  public Double getStrike() {
    return strike;
  }

  public void setStrike(Double strike) {
    this.strike = NumberUtil.toReserve(strike,NumberUtil.MONEY_PRECISION);
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
    this.afterMemberDiscountTotal = NumberUtil.toReserve(afterMemberDiscountTotal,NumberUtil.MONEY_PRECISION);
  }

  @Column(name="statement_amount")
  public Double getStatementAmount() {
    return statementAmount;
  }

  public void setStatementAmount(Double statementAmount) {
    this.statementAmount = statementAmount;
  }

  @Column(name="receivable_history_id")
  public Long getReceivableHistoryId() {
    return receivableHistoryId;
  }

  public void setReceivableHistoryId(Long receivableHistoryId) {
    this.receivableHistoryId = receivableHistoryId;
  }

  @Column(name ="deposit")
  public Double getDeposit() {
    return deposit;
  }

  public void setDeposit(Double deposit) {
    this.deposit = deposit;
  }

  @Column(name ="memo")
  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }

  @Column(name ="coupon")
  public Double getCoupon() {
    return coupon;
  }

  public void setCoupon(Double coupon) {
    this.coupon = coupon;
  }

  /**
   * ���XXId�ҵ������Ϣ�����õ�XX�ֶ�.
   */
  @Override
  public void onLoad(Session s, Serializable id) {
//    if(payeeId!=null){
//      setPayee(ServiceManager.getService(IUserService.class).getNameByUserId(payeeId));
//    }
  }
}
