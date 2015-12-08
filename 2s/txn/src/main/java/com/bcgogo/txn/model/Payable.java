package com.bcgogo.txn.model;

import com.bcgogo.enums.OrderTypes;
import com.bcgogo.enums.PayStatus;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.PayableDTO;
import com.bcgogo.txn.dto.PurchaseReturnDTO;
import com.bcgogo.txn.dto.PurchaseReturnItemDTO;
import com.bcgogo.txn.dto.SupplierReturnPayableDTO;
import com.bcgogo.txn.dto.StatementAccount.OrderDebtType;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.NumberUtil;

import javax.persistence.*;

/**
 * Created by IntelliJ IDEA.
 * User: MZDong
 * Date: 11-9-19
 * 应付款表
 */

@Entity
@Table(name = "payable")
public class Payable extends LongIdentifier {
  /*店面ID*/
  private Long shopId;
  /*付款时间*/
  private Long payTime;
  /*材料品名*/
  private String materialName;
  /*金额*/
  private Double amount;
  /*已付金额*/
  private Double paidAmount;
  /*欠款挂账*/
  private Double creditAmount;
  /*供应商ID*/
  private Long supplierId;
  /*入库单ID*/
  private Long purchaseInventoryId;
  /*状态：1：作废，2：可用*/
  private PayStatus status;
  /*扣款*/
  private Double deduction;

  /*现金*/
  private Double cash;
  /*银行卡*/
  private Double bankCard;
  /*支票*/
  private Double cheque;

  /*定金*/
  private Double deposit;
  private String receiptNo;

  //冲账
  private Double strikeAmount;

  private String lastPayer;   //收款人
  private Long lastPayerId;

  private OrderDebtType orderDebtType;//单据欠款类型
  private OrderTypes orderType;  //单据类型
  private Double statementAccount;//对账单付款
  private Long statementAccountOrderId;//对账单id


  public Payable() {
  }

  public Payable(PayableDTO payableDTO) {
    this.shopId = payableDTO.getShopId();
    this.payTime = payableDTO.getPayTime();
    this.materialName = payableDTO.getMaterialName();
    this.amount = payableDTO.getAmount();
    this.paidAmount = NumberUtil.numberValue(payableDTO.getPaidAmount(), 0d);
    this.creditAmount = NumberUtil.numberValue(payableDTO.getCreditAmount(), 0d);
    this.supplierId = payableDTO.getSupplierId();
    this.purchaseInventoryId = payableDTO.getPurchaseInventoryId();
    this.status = payableDTO.getStatus();
    this.cash = payableDTO.getCash();
    this.bankCard = payableDTO.getBankCard();
    this.cheque = payableDTO.getCheque();
    this.deposit = payableDTO.getDeposit();
    this.deduction = payableDTO.getDeduction();
    this.receiptNo=payableDTO.getReceiptNo();
    this.strikeAmount = payableDTO.getStrikeAmount();
    this.lastPayer = payableDTO.getLastPayer();
    this.lastPayerId = payableDTO.getLastPayerId();
    this.orderDebtType = payableDTO.getOrderDebtType();
    this.orderType = payableDTO.getOrderType();
    this.statementAccount = payableDTO.getStatementAccount();
    this.statementAccountOrderId = payableDTO.getStatementAccountOrderId();
    this.orderType = payableDTO.getOrderType();
  }

  public Payable fromDTO(PayableDTO payableDTO,boolean setId) {
    if(setId){
      this.setId(payableDTO.getId());
    }
    this.shopId = payableDTO.getShopId();
    this.payTime = payableDTO.getPayTime();
    this.materialName = payableDTO.getMaterialName();
    this.amount = payableDTO.getAmount();
    this.paidAmount = NumberUtil.numberValue(payableDTO.getPaidAmount(), 0d);
    this.creditAmount = NumberUtil.numberValue(payableDTO.getCreditAmount(), 0d);
    this.supplierId = payableDTO.getSupplierId();
    this.purchaseInventoryId = payableDTO.getPurchaseInventoryId();
    this.status = payableDTO.getStatus();
    this.cash = payableDTO.getCash();
    this.bankCard = payableDTO.getBankCard();
    this.cheque = payableDTO.getCheque();
    this.deposit = payableDTO.getDeposit();
    this.deduction = payableDTO.getDeduction();
    this.strikeAmount = payableDTO.getStrikeAmount();
    this.lastPayer = payableDTO.getLastPayer();
    this.lastPayerId = payableDTO.getLastPayerId();
    this.orderDebtType = payableDTO.getOrderDebtType();
    this.orderType = payableDTO.getOrderType();
    this.statementAccount = payableDTO.getStatementAccount();
    this.statementAccountOrderId = payableDTO.getStatementAccountOrderId();
    this.orderType = payableDTO.getOrderType();
    return this;
  }

  /**
   * 初始化专用
   * @param payableHistoryRecord
   */
  public Payable(PayableHistoryRecord payableHistoryRecord) {
    this.shopId = payableHistoryRecord.getShopId();
    this.payTime = payableHistoryRecord.getPayTime();
    this.materialName = payableHistoryRecord.getMaterialName();
    this.amount = NumberUtil.doubleVal(payableHistoryRecord.getAmount());
    this.paidAmount = NumberUtil.doubleVal(payableHistoryRecord.getActuallyPaid());
    this.creditAmount = NumberUtil.doubleVal(payableHistoryRecord.getCreditAmount());
    this.supplierId = payableHistoryRecord.getSupplierId();
    this.purchaseInventoryId = payableHistoryRecord.getPurchaseInventoryId();
    this.status = PayStatus.USE;
    this.cash = NumberUtil.doubleVal(payableHistoryRecord.getCash());
    this.bankCard = NumberUtil.doubleVal(payableHistoryRecord.getBankCardAmount());
    this.cheque = NumberUtil.doubleVal(payableHistoryRecord.getCheckAmount());
    this.deposit = NumberUtil.doubleVal(payableHistoryRecord.getDepositAmount());
    this.deduction = NumberUtil.doubleVal(payableHistoryRecord.getDeduction());

    this.strikeAmount = NumberUtil.doubleVal(payableHistoryRecord.getStrikeAmount());
    this.lastPayer = payableHistoryRecord.getPayer();
    this.lastPayerId = payableHistoryRecord.getPayerId();
    this.orderDebtType = OrderDebtType.SUPPLIER_DEBT_RECEIVABLE;
    this.orderType = OrderTypes.RETURN;
    this.statementAccount = NumberUtil.doubleVal(payableHistoryRecord.getStrikeAmount());
    this.statementAccountOrderId = null;
  }


  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "pay_time")
  public Long getPayTime() {
    return payTime;
  }

  public void setPayTime(Long payTime) {
    this.payTime = payTime;
  }

  @Column(name = "material_name")
  public String getMaterialName() {
    return materialName;
  }

  public void setMaterialName(String materialName) {
    this.materialName = materialName;
  }

  @Column(name = "paid_amount")
  public Double getPaidAmount() {
    return paidAmount;
  }

  public void setPaidAmount(Double paidAmount) {
    this.paidAmount = NumberUtil.toReserve(paidAmount,NumberUtil.MONEY_PRECISION);
  }

  @Column(name = "amount")
  public Double getAmount() {
    return amount;
  }

  public void setAmount(Double amount) {
    this.amount = NumberUtil.toReserve(amount,NumberUtil.MONEY_PRECISION);
  }

  @Column(name = "credit_amount")
  public Double getCreditAmount() {
    return creditAmount;
  }

  public void setCreditAmount(Double creditAmount) {
    this.creditAmount = NumberUtil.toReserve(creditAmount,NumberUtil.MONEY_PRECISION);
  }

  @Column(name = "supplier_id")
  public Long getSupplierId() {
    return supplierId;
  }

  public void setSupplierId(Long supplierId) {
    this.supplierId = supplierId;
  }

  @Column(name = "purchase_inventory_id")
  public Long getPurchaseInventoryId() {
    return purchaseInventoryId;
  }

  public void setPurchaseInventoryId(Long purchaseInventoryId) {
    this.purchaseInventoryId = purchaseInventoryId;
  }

  @Column(name = "status")
  @Enumerated(EnumType.STRING)
  public PayStatus getStatus() {
    return status;
  }

  public void setStatus(PayStatus status) {
    this.status = status;
  }

  @Column(name = "deduction")
  public Double getDeduction() {
    return deduction;
  }

  public void setDeduction(Double deduction) {
    this.deduction = NumberUtil.toReserve(deduction,NumberUtil.MONEY_PRECISION);
  }


  @Column(name = "cash")
  public Double getCash() {
    return cash;
  }

  @Column(name = "bank_card")
  public Double getBankCard() {
    return bankCard;
  }

  @Column(name = "cheque")
  public Double getCheque() {
    return cheque;
  }

  @Column(name = "deposit")
  public Double getDeposit() {
    return deposit;
  }

  @Column(name="strike_amount")
  public Double getStrikeAmount() {
    return strikeAmount;
  }

  public void setDeposit(Double deposit) {
    this.deposit = NumberUtil.toReserve(deposit, NumberUtil.MONEY_PRECISION);
  }

  public void setCash(Double cash) {
    this.cash = NumberUtil.toReserve(cash, NumberUtil.MONEY_PRECISION);
  }

  public void setBankCard(Double bankCard) {
    this.bankCard = NumberUtil.toReserve(bankCard, NumberUtil.MONEY_PRECISION);
  }

  public void setCheque(Double cheque) {
    this.cheque = NumberUtil.toReserve(cheque, NumberUtil.MONEY_PRECISION);
  }

  public void setStrikeAmount(Double strikeAmount) {
    this.strikeAmount = strikeAmount;
  }

  @Column(name = "receipt_no")
  public String getReceiptNo() {
    return receiptNo;
  }

  public void setReceiptNo(String receiptNo) {
    this.receiptNo = receiptNo;
  }

  @Column(name="last_payer")
  public String getLastPayer() {
    return lastPayer;
  }

  public void setLastPayer(String lastPayer) {
    this.lastPayer = lastPayer;
  }

  @Column(name="last_payer_id")
  public Long getLastPayerId() {
    return lastPayerId;
  }

  public void setLastPayerId(Long lastPayerId) {
    this.lastPayerId = lastPayerId;
  }

  @Column(name="debt_type")
  @Enumerated(EnumType.STRING)
  public OrderDebtType getOrderDebtType() {
    return orderDebtType;
  }

  public void setOrderDebtType(OrderDebtType orderDebtType) {
    this.orderDebtType = orderDebtType;
  }

  @Column(name="order_type")
  @Enumerated(EnumType.STRING)
  public OrderTypes getOrderType() {
    return orderType;
  }

  public void setOrderType(OrderTypes orderType) {
    this.orderType = orderType;
  }

  @Column(name="statement_amount")
  public Double getStatementAccount() {
    return statementAccount;
  }

  public void setStatementAccount(Double statementAccount) {
    this.statementAccount = statementAccount;
  }

  @Column(name = "statement_account_order_id")
  public Long getStatementAccountOrderId() {
    return statementAccountOrderId;
  }

  public void setStatementAccountOrderId(Long statementAccountOrderId) {
    this.statementAccountOrderId = statementAccountOrderId;
  }

  public PayableDTO toDTO() {
    PayableDTO payableDTO = new PayableDTO();
    payableDTO.setAmount(NumberUtil.numberValue(this.getAmount(), 0d));
    payableDTO.setCreditAmount(NumberUtil.numberValue(this.getCreditAmount(), 0d));
    payableDTO.setId(this.getId());
    payableDTO.setMaterialName(this.getMaterialName());
    payableDTO.setPaidAmount(NumberUtil.numberValue(this.getPaidAmount(), 0d));
    payableDTO.setPayTime(this.getPayTime());
    payableDTO.setPayTimeStr(DateUtil.convertDateLongToDateString(DateUtil.YEAR_MONTH_DATE, getPayTime()));
    payableDTO.setShopId(this.getShopId());
    payableDTO.setSupplierId(this.getSupplierId());
    payableDTO.setPurchaseInventoryId(this.getPurchaseInventoryId());
    payableDTO.setStatus(this.getStatus());
    payableDTO.setDeduction(NumberUtil.numberValue(this.getDeduction(), 0d));
    payableDTO.setCash(this.getCash());
    payableDTO.setBankCard(this.getBankCard());
    payableDTO.setCheque(this.getCheque());
    payableDTO.setDeposit(this.getDeposit());
    payableDTO.setReceiptNo(getReceiptNo());
    if(this.getPurchaseInventoryId() != null){
      payableDTO.setPurchaseInventoryIdStr(String.valueOf(this.getPurchaseInventoryId()));
    }
    payableDTO.setStrikeAmount(this.getStrikeAmount());
    payableDTO.setLastPayer(getLastPayer());
    payableDTO.setLastPayerId(getLastPayerId());
    payableDTO.setOrderType(this.getOrderType());
    payableDTO.setOrderDebtType(getOrderDebtType());
    payableDTO.setOrderType(getOrderType());
    payableDTO.setStatementAccount(getStatementAccount());
    payableDTO.setStatementAccountOrderId(getStatementAccountOrderId());
    return payableDTO;
  }

  //初始化专用
  public Payable(SupplierReturnPayable supplierReturnPayable)
  {
    if(null == supplierReturnPayable)
    {
      return;
    }

    this.orderType = OrderTypes.RETURN;
    this.shopId = supplierReturnPayable.getShopId();
    this.materialName = supplierReturnPayable.getMaterialName();
    this.amount = 0-NumberUtil.numberValue(supplierReturnPayable.getTotal(), 0D);
    double cash = NumberUtil.numberValue(supplierReturnPayable.getCash(), 0d);
    double deposit = NumberUtil.numberValue(supplierReturnPayable.getDeposit(), 0d);
    double strikeAmount = NumberUtil.numberValue(supplierReturnPayable.getStrikeAmount(), 0d);
    this.strikeAmount = 0-NumberUtil.numberValue(supplierReturnPayable.getStrikeAmount(),0D);
    double paidAmount = 0-NumberUtil.round(cash+deposit+strikeAmount,NumberUtil.MONEY_PRECISION);
    this.paidAmount = paidAmount;
    this.creditAmount = 0D;
    this.supplierId = supplierReturnPayable.getSupplierId();
    this.purchaseInventoryId = supplierReturnPayable.getPurchaseReturnId();
    this.status = PayStatus.USE;
    this.cash = 0-NumberUtil.numberValue(supplierReturnPayable.getCash(), 0D);
    this.bankCard = 0D;
    this.cheque = 0D;
    this.deposit = 0-NumberUtil.numberValue(supplierReturnPayable.getDeposit(), 0D);
    this.deduction = 0D;

    this.lastPayer = supplierReturnPayable.getPayee();
    this.lastPayerId = supplierReturnPayable.getPayeeId();
    this.orderDebtType = OrderDebtType.SUPPLIER_DEBT_RECEIVABLE;
  }

  public void fromSupplierReturnPayable(SupplierReturnPayableDTO supplierReturnPayableDTO)
  {
    this.orderType = OrderTypes.RETURN;
    this.shopId = supplierReturnPayableDTO.getShopId();
    this.materialName = supplierReturnPayableDTO.getMaterialName();
    this.amount = -supplierReturnPayableDTO.getTotal();
    this.paidAmount = -supplierReturnPayableDTO.getSettledAmount();
    this.creditAmount = -NumberUtil.numberValue(supplierReturnPayableDTO.getAccountDebtAmount(), 0D);
    this.supplierId = supplierReturnPayableDTO.getSupplierId();
    this.purchaseInventoryId = supplierReturnPayableDTO.getPurchaseReturnId();
    this.status = PayStatus.USE;
    this.cash = -NumberUtil.numberValue(supplierReturnPayableDTO.getCash(), 0D);
    this.deposit = -NumberUtil.numberValue(supplierReturnPayableDTO.getDeposit(), 0D);
    this.deduction = -NumberUtil.numberValue(supplierReturnPayableDTO.getAccountDiscount(), 0D);
    this.strikeAmount = -NumberUtil.numberValue(supplierReturnPayableDTO.getStrikeAmount(), 0D);
    this.lastPayer = supplierReturnPayableDTO.getPayee();
    this.lastPayerId = supplierReturnPayableDTO.getPayeeId();
    this.receiptNo = supplierReturnPayableDTO.getReceiptNo();
    this.payTime = supplierReturnPayableDTO.getVestDate();
    this.bankCard = -NumberUtil.numberValue(supplierReturnPayableDTO.getBankAmount(), 0D);
    this.cheque = -NumberUtil.numberValue(supplierReturnPayableDTO.getBankCheckAmount(), 0D);
    this.orderDebtType = OrderDebtType.SUPPLIER_DEBT_RECEIVABLE;
    this.payTime = supplierReturnPayableDTO.getReturnDate();
  }

  public void fromPurchaseReturnDTO(PurchaseReturnDTO purchaseReturnDTO) {
    StringBuffer materialName = new StringBuffer();
    PurchaseReturnItemDTO purchaseReturnItemDTO[] = purchaseReturnDTO.getItemDTOs();
    for (PurchaseReturnItemDTO p : purchaseReturnItemDTO) {
      materialName.append(p.getProductName()).append("；");
    }
    this.setOrderType(OrderTypes.RETURN);
    this.setShopId(purchaseReturnDTO.getShopId());
    this.setMaterialName(materialName.toString());
    this.setAmount(-purchaseReturnDTO.getTotal());
    this.setPaidAmount(-purchaseReturnDTO.getSettledAmount());
    this.setCreditAmount(-NumberUtil.numberValue(purchaseReturnDTO.getAccountDebtAmount(), 0d));
    this.setSupplierId(purchaseReturnDTO.getSupplierId());
    this.setPurchaseInventoryId(purchaseReturnDTO.getId());
    this.setStatus(PayStatus.USE);
    this.setCash(-NumberUtil.numberValue(purchaseReturnDTO.getCash(), 0D));
    this.setDeposit(-NumberUtil.numberValue(purchaseReturnDTO.getDepositAmount(), 0D));
    this.setDeduction(-NumberUtil.numberValue(purchaseReturnDTO.getAccountDiscount(), 0D));
    this.setStrikeAmount(-NumberUtil.numberValue(purchaseReturnDTO.getStrikeAmount(), 0D));
    this.setLastPayer(purchaseReturnDTO.getUserName());
    this.setLastPayerId(purchaseReturnDTO.getUserId());
    this.setReceiptNo(purchaseReturnDTO.getReceiptNo());
    this.setPayTime(purchaseReturnDTO.getVestDate());
    this.setBankCard(-NumberUtil.numberValue(purchaseReturnDTO.getBankAmount(), 0D));
    this.setCheque(-NumberUtil.numberValue(purchaseReturnDTO.getBankCheckAmount(), 0D));
    this.setOrderDebtType(OrderDebtType.SUPPLIER_DEBT_RECEIVABLE);
    this.setPayTime(purchaseReturnDTO.getVestDate());
  }
}
