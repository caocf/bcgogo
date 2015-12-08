package com.bcgogo.txn.model;

import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.SupplierReturnPayableDTO;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by IntelliJ IDEA.
 * User: zhangchuanlong
 * Date: 12-8-25
 * Time: 下午4:39
 * 供应商退货款
 */
@Entity
@Table(name = "supplier_return_payable")
public class SupplierReturnPayable extends LongIdentifier {
  //        <!-- 店面ID-->、
  private Long shopId;
  //            <!-- 现金-->
  private Double cash;
  //            <!-- 定金-->
  private Double deposit;
  //            <!-- 总额-->
  private Double total;
  //            <!-- 退货单ID-->
  private Long purchaseReturnId;
  //            <!-- 供应商ID-->
  private Long supplierId;
  //            <!-- 材料品名-->
  private String materialName;
  //  冲账
  private Double strikeAmount;

  private Long payeeId;
  private String payee;   //退货收款人

  private Double accountDiscount;   //优惠
  private Double accountDebtAmount;//欠款
  private Double settledAmount;//实收

  //迁移状态
  private String moveStatus;
  public void fromSupplierReturnPayableDTO(SupplierReturnPayableDTO supplierReturnPayableDTO) {
    //        <!-- 店面ID-->、
    this.shopId = supplierReturnPayableDTO.getShopId();
    //            <!-- 现金-->
    this.cash = supplierReturnPayableDTO.getCash();
    //            <!-- 定金-->
    this.deposit = supplierReturnPayableDTO.getDeposit();
    //            <!-- 总额-->
    this.total = supplierReturnPayableDTO.getTotal();
    //            <!-- 退货单ID-->
    this.purchaseReturnId = supplierReturnPayableDTO.getPurchaseReturnId();
    //            <!-- 供应商ID-->
    this.supplierId = supplierReturnPayableDTO.getSupplierId();
    //            <!-- 材料品名-->
    this.materialName = supplierReturnPayableDTO.getMaterialName();

    this.strikeAmount = supplierReturnPayableDTO.getStrikeAmount();
    this.payee = supplierReturnPayableDTO.getPayee();
    this.payeeId = supplierReturnPayableDTO.getPayeeId();
    this.settledAmount = supplierReturnPayableDTO.getSettledAmount();
    this.accountDebtAmount = supplierReturnPayableDTO.getAccountDebtAmount();
    this.accountDiscount = supplierReturnPayableDTO.getAccountDiscount();
  }

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "cash")
  public Double getCash() {
    return cash;
  }

  public void setCash(Double cash) {
    this.cash = cash;
  }

  @Column(name = "deposit")
  public Double getDeposit() {
    return deposit;
  }

  public void setDeposit(Double deposit) {
    this.deposit = deposit;
  }

  @Column(name = "total")
  public Double getTotal() {
    return total;
  }

  public void setTotal(Double total) {
    this.total = total;
  }

  @Column(name = "purchase_return_id")
  public Long getPurchaseReturnId() {
    return purchaseReturnId;
  }

  public void setPurchaseReturnId(Long purchaseReturnId) {
    this.purchaseReturnId = purchaseReturnId;
  }

  @Column(name = "supplier_id")
  public Long getSupplierId() {
    return supplierId;
  }

  public void setSupplierId(Long supplierId) {
    this.supplierId = supplierId;
  }

  @Column(name = "material_name")
  public String getMaterialName() {
    return materialName;
  }

  public void setMaterialName(String materialName) {
    this.materialName = materialName;
  }

  @Column(name = "strike_amount")
  public Double getStrikeAmount() {
    return strikeAmount;
  }

  public void setStrikeAmount(Double strikeAmount) {
    this.strikeAmount = strikeAmount;
  }

  @Column(name="payee_id")
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

  @Column(name="account_discount")
  public Double getAccountDiscount() {
    return accountDiscount;
  }

  public void setAccountDiscount(Double accountDiscount) {
    this.accountDiscount = accountDiscount;
  }

  @Column(name="account_debt_amount")
  public Double getAccountDebtAmount() {
    return accountDebtAmount;
  }

  public void setAccountDebtAmount(Double accountDebtAmount) {
    this.accountDebtAmount = accountDebtAmount;
  }

  @Column(name="settled_amount")
  public Double getSettledAmount() {
    return settledAmount;
  }

  public void setSettledAmount(Double settledAmount) {
    this.settledAmount = settledAmount;
  }

  @Column(name="move_status")
  public String getMoveStatus() {
    return moveStatus;
  }

  public void setMoveStatus(String moveStatus) {
    this.moveStatus = moveStatus;
  }

  public SupplierReturnPayableDTO toSupplierReturnPayableDTO() {
    SupplierReturnPayableDTO supplierReturnPayableDTO = new SupplierReturnPayableDTO();
    supplierReturnPayableDTO.setCash(this.getCash());
    supplierReturnPayableDTO.setDeposit(this.getDeposit());
    supplierReturnPayableDTO.setMaterialName(this.getMaterialName());
    supplierReturnPayableDTO.setPurchaseReturnId(this.getPurchaseReturnId());
    supplierReturnPayableDTO.setShopId(this.getShopId());
    supplierReturnPayableDTO.setSupplierId(this.getSupplierId());
    supplierReturnPayableDTO.setTotal(this.getTotal());
    supplierReturnPayableDTO.setStrikeAmount(this.getStrikeAmount());
    supplierReturnPayableDTO.setPayee(getPayee());
    supplierReturnPayableDTO.setPayeeId(getPayeeId());
    supplierReturnPayableDTO.setSettledAmount(getSettledAmount());
    supplierReturnPayableDTO.setAccountDiscount(getAccountDiscount());
    supplierReturnPayableDTO.setAccountDebtAmount(getAccountDebtAmount());
    return supplierReturnPayableDTO;
  }
}
