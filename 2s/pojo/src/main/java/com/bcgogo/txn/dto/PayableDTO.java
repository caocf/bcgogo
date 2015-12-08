package com.bcgogo.txn.dto;

import com.bcgogo.enums.OrderTypes;
import com.bcgogo.enums.PayStatus;
import com.bcgogo.txn.dto.StatementAccount.OrderDebtType;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.NumberUtil;

/**
 * Created by IntelliJ IDEA.
 * User: zhangchuanlong
 * Date: 12-8-16
 * Time: 下午5:24
 * 应付款DTO
 */
public class PayableDTO {
  private Long id;
  private String idStr;
  /*店面ID*/
  private Long shopId;
  /*付款时间*/
  private Long payTime;
  private String payTimeStr;
  /*材料品名*/
  private String materialName;
  /*金额*/
  private Double amount;
  /*已付金额*/
  private Double paidAmount;
  private Double creditAmount;
  private Long supplierId;
  private String supplierName;
  private Long purchaseInventoryId;
  private String purchaseInventoryIdStr;
  private PayStatus status;
  private Double deduction;
  private Double cash;
  private Double bankCard;
  private Double cheque;
  private Double deposit;
  private String receiptNo;

  //冲账
  private Double strikeAmount;

  private Double totalCreditAmount;

  private String supplierIdStr;

  private String lastPayer;   //收款人
  private Long lastPayerId;
  private OrderTypes orderType;

  private OrderDebtType orderDebtType;//单据欠款类型

  private Double statementAccount;//对账单付款

  private Long statementAccountOrderId;//对账单id

  public OrderTypes getOrderType() {
    return orderType;
  }

  public void setOrderType(OrderTypes orderType) {
    this.orderType = orderType;
  }

  public OrderDebtType getOrderDebtType() {
    return orderDebtType;
  }

  public void setOrderDebtType(OrderDebtType orderDebtType) {
    this.orderDebtType = orderDebtType;
  }

  public Double getCash() {
    return cash;
  }

  public void setCash(Double cash) {
    this.cash = cash;
  }

  public Double getBankCard() {
    return bankCard;
  }

  public void setBankCard(Double bankCard) {
    this.bankCard = bankCard;
  }

  public String getReceiptNo() {
    return receiptNo;
  }

  public void setReceiptNo(String receiptNo) {
    this.receiptNo = receiptNo;
  }

  public Double getCheque() {
    return cheque;
  }

  public void setCheque(Double cheque) {
    this.cheque = cheque;
  }

  public Double getDeposit() {
    return deposit;
  }

  public void setDeposit(Double deposit) {
    this.deposit = deposit;
  }

  public Long getId() {
    return id;

  }

  public void setId(Long id) {
    this.id = id;
    this.idStr = id == null ? null : id.toString();
  }

  public String getIdStr() {
    return idStr;
  }

  public void setIdStr(String idStr) {
    this.idStr = idStr;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public Long getPayTime() {
    return payTime;
  }

  public void setPayTime(Long payTime) {
    this.payTime = payTime;
    this.payTimeStr = DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_DEFAULT, payTime);
  }

  public String getMaterialName() {
    return materialName;
  }

  public void setMaterialName(String materialName) {
    this.materialName = materialName;
  }

  public Double getAmount() {
    return amount;
  }

  public void setAmount(Double amount) {
    this.amount = amount;
  }

  public Double getPaidAmount() {
    return paidAmount;
  }

  public void setPaidAmount(Double paidAmount) {
    this.paidAmount = paidAmount;
  }

  public Double getCreditAmount() {
    return creditAmount;
  }

  public void setCreditAmount(Double creditAmount) {
    this.creditAmount = creditAmount;
  }

  public Long getSupplierId() {
    return supplierId;
  }

  public void setSupplierId(Long supplierId) {
    this.supplierId = supplierId;
    if(null != supplierId)
    {
      this.supplierIdStr = this.supplierId.toString();
    }
  }

  public String getSupplierName() {
    return supplierName;
  }

  public void setSupplierName(String supplierName) {
    this.supplierName = supplierName;
  }

  public Long getPurchaseInventoryId() {
    return purchaseInventoryId;
  }

  public void setPurchaseInventoryId(Long purchaseInventoryId) {
    this.purchaseInventoryId = purchaseInventoryId;
  }

  public String getPayTimeStr() {
    return payTimeStr;
  }

  public void setPayTimeStr(String payTimeStr) {
    this.payTimeStr = payTimeStr;
  }

  public PayStatus getStatus() {
    return status;
  }

  public void setStatus(PayStatus status) {
    this.status = status;
  }

  public Double getDeduction() {
    return deduction;
  }

  public void setDeduction(Double deduction) {
    this.deduction = deduction;
  }

  public String getPurchaseInventoryIdStr() {
    return purchaseInventoryIdStr;
  }

  public void setPurchaseInventoryIdStr(String purchaseInventoryIdStr) {
    this.purchaseInventoryIdStr = purchaseInventoryIdStr;
  }

  public Double getStrikeAmount() {
    return strikeAmount;
  }

  public void setStrikeAmount(Double strikeAmount) {
    this.strikeAmount = strikeAmount;
  }

  public Double getTotalCreditAmount() {
    return totalCreditAmount;
  }

  public void setTotalCreditAmount(Double totalCreditAmount) {
    this.totalCreditAmount = NumberUtil.round(totalCreditAmount,NumberUtil.MONEY_PRECISION);
  }

  public String getSupplierIdStr() {
    return supplierIdStr;
  }

  public void setSupplierIdStr(String supplierIdStr) {
    this.supplierIdStr = supplierIdStr;
  }

  public String getLastPayer() {
    return lastPayer;
  }

  public void setLastPayer(String lastPayer) {
    this.lastPayer = lastPayer;
  }

  public Long getLastPayerId() {
    return lastPayerId;
  }

  public void setLastPayerId(Long lastPayerId) {
    this.lastPayerId = lastPayerId;
  }

  public Double getStatementAccount() {
    return statementAccount;
  }

  public void setStatementAccount(Double statementAccount) {
    this.statementAccount = statementAccount;
  }

  public Long getStatementAccountOrderId() {
    return statementAccountOrderId;
  }

  public void setStatementAccountOrderId(Long statementAccountOrderId) {
    this.statementAccountOrderId = statementAccountOrderId;
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

    if (statementAccountOrderDTO == null) {
      statementAccountOrderDTO = new StatementAccountOrderDTO();
    }
    statementAccountOrderDTO.setVestDate(this.getPayTime());
    statementAccountOrderDTO.setReceiptNo(this.getReceiptNo());
    statementAccountOrderDTO.setOrderType(this.getOrderType());
    statementAccountOrderDTO.setPayableId(this.getId());
    statementAccountOrderDTO.setOrderId(this.getPurchaseInventoryId());
    statementAccountOrderDTO.setOrderIdStr(this.getPurchaseInventoryId() == null?"":this.getPurchaseInventoryId().toString());

    if (orderDebtType == OrderDebtType.SUPPLIER_DEBT_RECEIVABLE) {
      statementAccountOrderDTO.setTotal(0 - NumberUtil.doubleVal(getAmount()));
      statementAccountOrderDTO.setSettledAmount(0 - NumberUtil.doubleVal(getPaidAmount()));
      statementAccountOrderDTO.setDiscount(0 - NumberUtil.doubleVal(getDeduction()));
      statementAccountOrderDTO.setDebt(0 - NumberUtil.doubleVal(getCreditAmount()));
      statementAccountOrderDTO.setStatementAmount(0 - NumberUtil.doubleVal(getStatementAccount()));

      statementAccountOrderDTO.setCashAmount(0 - NumberUtil.doubleVal(this.getCash()));
      statementAccountOrderDTO.setBankAmount(0 - NumberUtil.doubleVal(this.getBankCard()));
      statementAccountOrderDTO.setBankCheckAmount(0 - NumberUtil.doubleVal(this.getCheque()));
      statementAccountOrderDTO.setDepositAmount(0 - NumberUtil.doubleVal(this.getDeposit()));

    } else if (orderDebtType == OrderDebtType.SUPPLIER_DEBT_PAYABLE) {
      statementAccountOrderDTO.setTotal(NumberUtil.doubleVal(getAmount()));
      statementAccountOrderDTO.setSettledAmount(NumberUtil.doubleVal(getPaidAmount()));
      statementAccountOrderDTO.setDiscount(NumberUtil.doubleVal(getDeduction()));
      statementAccountOrderDTO.setDebt(NumberUtil.doubleVal(getCreditAmount()));
      statementAccountOrderDTO.setStatementAmount(NumberUtil.doubleVal(getStatementAccount()));

      statementAccountOrderDTO.setCashAmount(NumberUtil.doubleVal(this.getCash()));
      statementAccountOrderDTO.setBankAmount(NumberUtil.doubleVal(this.getBankCard()));
      statementAccountOrderDTO.setBankCheckAmount(NumberUtil.doubleVal(this.getCheque()));
      statementAccountOrderDTO.setDepositAmount(NumberUtil.doubleVal(this.getDeposit()));

    } else {
      return null;
    }
    statementAccountOrderDTO.setOrderDebtType(orderDebtType);
    return statementAccountOrderDTO;
  }

  public PayableHistoryDTO toPayableHistoryDTO() {
    PayableHistoryDTO payableHistoryDTO = new PayableHistoryDTO();

    payableHistoryDTO.setShopId(getShopId());
    payableHistoryDTO.setDeduction(NumberUtil.doubleVal(getDeduction()));
    payableHistoryDTO.setCreditAmount(NumberUtil.doubleVal(getCreditAmount()));
    payableHistoryDTO.setCash(NumberUtil.doubleVal(getCash()));
    payableHistoryDTO.setBankCardAmount(NumberUtil.doubleVal(getBankCard()));
    payableHistoryDTO.setCheckAmount(NumberUtil.doubleVal(getCheque()));
    payableHistoryDTO.setCheckNo("");
    payableHistoryDTO.setDepositAmount(NumberUtil.doubleVal(getDeposit()));
    payableHistoryDTO.setActuallyPaid(-NumberUtil.doubleVal(getPaidAmount()));
    payableHistoryDTO.setSupplierId(getSupplierId());
    payableHistoryDTO.setStrikeAmount(NumberUtil.doubleVal(getStrikeAmount()));
    payableHistoryDTO.setPayer(getLastPayer());
    payableHistoryDTO.setPayerId(getLastPayerId());
    payableHistoryDTO.setPayTime(getPayTime());

    return payableHistoryDTO;
  }


  @Override
  public String toString() {
    return "PayableDTO{" +
        "id=" + id +
        ", idStr='" + idStr + '\'' +
        ", shopId=" + shopId +
        ", payTime=" + payTime +
        ", payTimeStr='" + payTimeStr + '\'' +
        ", materialName='" + materialName + '\'' +
        ", amount=" + amount +
        ", paidAmount=" + paidAmount +
        ", creditAmount=" + creditAmount +
        ", supplierId=" + supplierId +
        ", supplierName='" + supplierName + '\'' +
        ", purchaseInventoryId=" + purchaseInventoryId +
        ", purchaseInventoryIdStr='" + purchaseInventoryIdStr + '\'' +
        ", status=" + status +
        ", deduction=" + deduction +
        ", cash=" + cash +
        ", bankCard=" + bankCard +
        ", cheque=" + cheque +
        ", deposit=" + deposit +
        ", receiptNo='" + receiptNo + '\'' +
        ", strikeAmount=" + strikeAmount +
        ", totalCreditAmount=" + totalCreditAmount +
        ", supplierIdStr='" + supplierIdStr + '\'' +
        ", lastPayer='" + lastPayer + '\'' +
        ", lastPayerId=" + lastPayerId +
        ", orderType=" + orderType +
        ", orderDebtType=" + orderDebtType +
        ", statementAccount=" + statementAccount +
        ", statementAccountOrderId=" + statementAccountOrderId +
        '}';
  }
}
