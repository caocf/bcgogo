package com.bcgogo.txn.dto;

import com.bcgogo.enums.OrderTypes;
import com.bcgogo.enums.ReceivableStatus;
import com.bcgogo.txn.dto.StatementAccount.OrderDebtType;
import com.bcgogo.utils.NumberUtil;

import java.io.Serializable;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: zhuyinjia
 * Date: 11-10-31
 * Time: 下午4:27
 * To change this template use File | Settings | File Templates.
 */
public class ReceivableDTO implements Serializable {
  public ReceivableDTO() {
  }

  private Long id;
  private Long shopId;
  private Long date;
  private String no;
  /**
   * 1:维修单
   * 2:销售单
   * 3:洗车单
   * 10:购卡续卡单
   */
  private OrderTypes orderType;
  private Long orderId;
  private String orderNo;
  private Long lastPayeeId;
  private String lastPayee;
  private Long lastReceiveDate;
  private ReceivableStatus status;
  private String memo;
  private double settledAmount;
  private double debt;
  private ReceptionRecordDTO[] recordDTOs;

  private String lastUpdate;
  private double discount;
  private double total;

  private Double memberBalancePay;  //会员储值支付金额
  private Double accumulatePointsPay; //积分支付金额
  private Integer accumulatePoints;  //积分
  private List<ReceivableServiceTimesDTO> receivableServiceTimesDTOs;

  private Double cash;   //现金支付金额
  private Double bankCard;    //银行卡支付金额
  private Double cheque;//支票支付金额
  private Double deposit; // 预收款支付金额

  private Long memberId; //会员id
  private Long creationDate;
  private String memberNo; //会员号码
  private Double strike;

  private Double memberDiscountRatio;
  private Double afterMemberDiscountTotal;

  private Long customerId;//客户id
  private String receiptNo;//单据号码
  private Long vestDate;   //单据结算时间
  private Double statementAmount;//对账单结算 使用对账结算金额；
  private OrderDebtType orderDebtType;//客户欠款类型

  private String bankCheckNo;//支票号码

  private Long  statementAccountOrderId;//对账单id
  private Long remindTime;//单据还款时间
  private Double coupon; //代金券消费金额 add by litao

  public Long getRemindTime() {
    return remindTime;
  }

  public void setRemindTime(Long remindTime) {
    this.remindTime = remindTime;
  }

  public String getBankCheckNo() {
    return bankCheckNo;
  }

  public void setBankCheckNo(String bankCheckNo) {
    this.bankCheckNo = bankCheckNo;
  }

  public OrderDebtType getOrderDebtType() {
    return orderDebtType;
  }

  public void setOrderDebtType(OrderDebtType orderDebtType) {
    this.orderDebtType = orderDebtType;
  }

  public Double getStatementAmount() {
    return statementAmount;
  }

  public void setStatementAmount(Double statementAmount) {
    this.statementAmount = statementAmount;
  }

  public Long getVestDate() {
    return vestDate;
  }

  public void setVestDate(Long vestDate) {
    this.vestDate = vestDate;
  }

  public String getReceiptNo() {
    return receiptNo;
  }

  public void setReceiptNo(String receiptNo) {
    this.receiptNo = receiptNo;
  }

  public String getMemberNo() {
    return memberNo;
  }

  public void setMemberNo(String memberNo) {
    this.memberNo = memberNo;
  }

  public Long getCreationDate() {
    return creationDate;
  }

  public void setCreationDate(Long creationDate) {
    this.creationDate = creationDate;
  }



  public Long getMemberId() {
		return memberId;
	}

	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}

  public double getDebt() {
    return debt;
  }

  public void setDebt(double debt) {
    this.debt = debt;
  }

  public double getSettledAmount() {
    return settledAmount;
  }

  public void setSettledAmount(double settledAmount) {
    this.settledAmount = settledAmount;
  }

  public ReceptionRecordDTO[] getRecordDTOs() {
    return recordDTOs;
  }

  public void setRecordDTOs(ReceptionRecordDTO[] recordDTOs) {
    this.recordDTOs = recordDTOs;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public Long getDate() {
    return date;
  }

  public void setDate(Long date) {
    this.date = date;
  }

  public String getNo() {
    return no;
  }

  public void setNo(String no) {
    this.no = no;
  }

  public OrderTypes getOrderType() {
    return orderType;
  }

  public void setOrderType(OrderTypes orderType) {
    this.orderType = orderType;
  }

  public Long getOrderId() {
    return orderId;
  }

  public void setOrderId(Long orderId) {
    this.orderId = orderId;
  }

  public String getOrderNo() {
    return orderNo;
  }

  public void setOrderNo(String orderNo) {
    this.orderNo = orderNo;
  }

  public void setCash(Double cash) {
    this.cash = cash;
  }

  public void setBankCard(Double bankCard) {
    this.bankCard = bankCard;
  }

  public void setCheque(Double cheque) {
    this.cheque = cheque;
  }

  public Long getLastPayeeId() {
    return lastPayeeId;
  }

  public void setLastPayeeId(Long lastPayeeId) {
    this.lastPayeeId = lastPayeeId;
  }

  public String getLastPayee() {
    return lastPayee;
  }

  public void setLastPayee(String lastPayee) {
    this.lastPayee = lastPayee;
  }

  public Long getLastReceiveDate() {
    return lastReceiveDate;
  }

  public void setLastReceiveDate(Long lastReceiveDate) {
    this.lastReceiveDate = lastReceiveDate;
  }

  public ReceivableStatus getStatus() {
    return status;
  }

  public void setStatus(ReceivableStatus status) {
    this.status = status;
  }

  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }

  public String getLastUpdate(){
      return lastUpdate;
  }

  public void setLastUpdate(String lastUpdate){
      this.lastUpdate = lastUpdate;
  }

  public double getDiscount() {
    return discount;
  }

  public void setDiscount(double discount) {
    this.discount = discount;
  }

  public double getTotal() {
    return total;
  }

  public void setTotal(double total) {
    this.total = total;
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

  public List<ReceivableServiceTimesDTO> getReceivableServiceTimesDTOs() {
      return receivableServiceTimesDTOs;
  }

  public void setMemberBalancePay(Double memberBalancePay) {
      this.memberBalancePay = memberBalancePay;
  }

  public void setAccumulatePointsPay(Double accumulatePointsPay) {
      this.accumulatePointsPay = accumulatePointsPay;
  }

  public void setAccumulatePoints(Integer accumulatePoints) {
      this.accumulatePoints = accumulatePoints;
  }

  public void setReceivableServiceTimesDTOs(List<ReceivableServiceTimesDTO> receivableServiceTimesDTOs) {
      this.receivableServiceTimesDTOs = receivableServiceTimesDTOs;
  }

  public Double getCash() {
    return cash;
  }

  public Double getBankCard() {
    return bankCard;
  }

  public Double getCheque() {
    return cheque;
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

  public Double getStrike() {
    return strike;
  }

  public void setStrike(Double strike) {
    this.strike = strike;
  }

  public Long getCustomerId() {
    return customerId;
  }

  public void setCustomerId(Long customerId) {
    this.customerId = customerId;
  }

  public Double getDeposit() {
    return deposit;
  }

  public void setDeposit(Double deposit) {
    this.deposit = deposit;
  }

  public Double getCoupon() {
    if (null==coupon){
      return 0D;
    }
    return coupon;
  }

  public void setCoupon(Double coupon) {
    this.coupon = coupon;
  }

  /**
   * 实收记录转换成统一对账记录
   * 会员退卡不含有欠款 暂不处理
   * 作废单据暂不处理
   * 销售退货单更改为正值
   * @return
   */
  public StatementAccountOrderDTO toStatementAccountOrderDTO(StatementAccountOrderDTO statementAccountOrderDTO) {
    OrderDebtType orderDebtType = this.getOrderDebtType();
    if (orderDebtType == null) {
      return null;
    }
    if(statementAccountOrderDTO == null){
      statementAccountOrderDTO = new StatementAccountOrderDTO();
    }
    statementAccountOrderDTO.setVestDate(this.getVestDate());
    statementAccountOrderDTO.setReceiptNo(this.getReceiptNo());
    statementAccountOrderDTO.setOrderType(this.getOrderType());
    statementAccountOrderDTO.setReceivableId(this.getId());
    statementAccountOrderDTO.setOrderId(this.getOrderId());
    statementAccountOrderDTO.setOrderIdStr(this.getOrderId() == null?"":this.getOrderId().toString());
    statementAccountOrderDTO.setAccountMemberNo(this.getMemberNo());
    statementAccountOrderDTO.setAccountMemberId(this.getMemberId());

    if (orderDebtType == OrderDebtType.CUSTOMER_DEBT_PAYABLE) {
      statementAccountOrderDTO.setTotal(0 - this.getTotal());
      statementAccountOrderDTO.setSettledAmount(0 - this.getSettledAmount());
      statementAccountOrderDTO.setDiscount(0 - this.getDiscount());
      statementAccountOrderDTO.setDebt(0 - this.getDebt());
      statementAccountOrderDTO.setStatementAmount(0 - NumberUtil.doubleVal(getStatementAmount()));

      statementAccountOrderDTO.setCashAmount(0 - NumberUtil.doubleVal(this.getCash()));
      statementAccountOrderDTO.setBankAmount(0 - NumberUtil.doubleVal(this.getBankCard()));
      statementAccountOrderDTO.setBankCheckAmount(0 - NumberUtil.doubleVal(this.getCheque()));
      statementAccountOrderDTO.setDepositAmount(0 - NumberUtil.doubleVal(this.getDeposit())); // add by zhuj
      statementAccountOrderDTO.setMemberAmount(0 - NumberUtil.doubleVal(this.getMemberBalancePay()));
      statementAccountOrderDTO.setStatementAmount(0 - NumberUtil.doubleVal(this.getStatementAmount()));

    } else if (orderDebtType == OrderDebtType.CUSTOMER_DEBT_RECEIVABLE) {
      statementAccountOrderDTO.setTotal(this.getTotal());
      statementAccountOrderDTO.setSettledAmount(this.getSettledAmount());
      statementAccountOrderDTO.setDiscount(this.getDiscount());
      statementAccountOrderDTO.setDebt(this.getDebt());
      statementAccountOrderDTO.setStatementAmount(NumberUtil.doubleVal(getStatementAmount()));

      statementAccountOrderDTO.setCashAmount(NumberUtil.doubleVal(this.getCash()));
      statementAccountOrderDTO.setBankAmount(NumberUtil.doubleVal(this.getBankCard()));
      statementAccountOrderDTO.setBankCheckAmount(NumberUtil.doubleVal(this.getCheque()));
      statementAccountOrderDTO.setDepositAmount(NumberUtil.doubleVal(this.getDeposit())); // add by zhuj
      statementAccountOrderDTO.setMemberAmount(NumberUtil.doubleVal(this.getMemberBalancePay()));
      statementAccountOrderDTO.setStatementAmount(NumberUtil.doubleVal(this.getStatementAmount()));

    } else {
      return null;
    }
    statementAccountOrderDTO.setOrderDebtType(orderDebtType);
    return statementAccountOrderDTO;
  }

  public ReceivableHistoryDTO toReceivableHistoryDTO() {
    ReceivableHistoryDTO receivableHistoryDTO = new ReceivableHistoryDTO();
    receivableHistoryDTO.setShopId(getShopId());
    receivableHistoryDTO.setTotal(NumberUtil.doubleVal(getTotal()));
    receivableHistoryDTO.setDiscount(NumberUtil.doubleVal(getDiscount()));
    receivableHistoryDTO.setDebt(NumberUtil.doubleVal(getDebt()));
    receivableHistoryDTO.setCash(NumberUtil.doubleVal(getCash()));
    receivableHistoryDTO.setBankCardAmount(NumberUtil.doubleVal(getBankCard()));
    receivableHistoryDTO.setCheckAmount(NumberUtil.doubleVal(getCheque()));
    receivableHistoryDTO.setCheckNo(getBankCheckNo());
    receivableHistoryDTO.setDeposit(getDeposit());
    receivableHistoryDTO.setMemberBalancePay(NumberUtil.doubleVal(getMemberBalancePay()));
    receivableHistoryDTO.setMemberId(getMemberId());
    receivableHistoryDTO.setMemberNo(getMemberNo());
    receivableHistoryDTO.setStrikeAmount(NumberUtil.doubleVal(getStrike()));
    receivableHistoryDTO.setSettledAmount(NumberUtil.doubleVal(getSettledAmount()));
    receivableHistoryDTO.setCustomerId(getCustomerId());
    receivableHistoryDTO.setReceivableDate(getVestDate());
    receivableHistoryDTO.setReceiver(getLastPayee());
    receivableHistoryDTO.setReceiverId(getLastPayeeId());

    return receivableHistoryDTO;
  }

  public Long getStatementAccountOrderId() {
    return statementAccountOrderId;
  }

  public void setStatementAccountOrderId(Long statementAccountOrderId) {
    this.statementAccountOrderId = statementAccountOrderId;
  }
}
