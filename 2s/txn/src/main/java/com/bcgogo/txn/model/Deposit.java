package com.bcgogo.txn.model;

import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.DepositDTO;
import com.bcgogo.utils.NumberUtil;
import org.springframework.beans.BeanUtils;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by IntelliJ IDEA.
 * User: zhangchuanlong
 * Date: 12-8-16
 * Time: 下午4:34
 * 定金表
 */
@Entity
@Table(name = "deposit")
public class Deposit extends LongIdentifier {
  /*店面ID*/
  private Long shopId;//shop_id
  /*现金*/
  private Double cash;//cash
  /*银行卡*/
  private Double bankCardAmount;//bank_card_amount
  /*支票*/
  private Double checkAmount;//check_amount;
  /*支票号码*/
  private String checkNo;//check_no;
  /*实付*/
  private Double actuallyPaid;//actually_paid
  /*供应商ID*/
  private Long supplierId;//supplier_id

  private String memo; //备注

  public Deposit() {
  }

  public Deposit(DepositDTO depositDTO) {
    this.actuallyPaid = NumberUtil.numberValue(depositDTO.getActuallyPaid(),0d);
    this.bankCardAmount = NumberUtil.numberValue(depositDTO.getBankCardAmount(),0d);
    this.cash = NumberUtil.numberValue(depositDTO.getCash(),0d);
    this.checkAmount = NumberUtil.numberValue(depositDTO.getCheckAmount(),0d);
    this.checkNo = depositDTO.getCheckNo();
    this.supplierId = depositDTO.getSupplierId();
    this.shopId = depositDTO.getShopId();
    this.memo = depositDTO.getMemo();
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

  @Column(name = "bank_card_amount")
  public Double getBankCardAmount() {
    return bankCardAmount;
  }

  public void setBankCardAmount(Double bankCardAmount) {
    this.bankCardAmount = bankCardAmount;
  }

  @Column(name = "check_amount")
  public Double getCheckAmount() {
    return checkAmount;
  }

  public void setCheckAmount(Double checkAmount) {
    this.checkAmount = checkAmount;
  }

  @Column(name = "check_no")
  public String getCheckNo() {
    return checkNo;
  }

  public void setCheckNo(String checkNo) {
    this.checkNo = checkNo;
  }

  @Column(name = "actually_paid")
  public Double getActuallyPaid() {
    return actuallyPaid;
  }

  public void setActuallyPaid(Double actuallyPaid) {
    this.actuallyPaid = actuallyPaid;
  }

  @Column(name = "supplier_id")
  public Long getSupplierId() {
    return supplierId;
  }

  public void setSupplierId(Long supplierId) {
    this.supplierId = supplierId;
  }

  @Column(name = "memo", length = 500)
  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }

  public DepositDTO toDTO() {
    DepositDTO depositDTO = new DepositDTO();
    depositDTO.setId(this.getId());
    depositDTO.setActuallyPaid(NumberUtil.numberValue(this.getActuallyPaid(),0d));
    depositDTO.setBankCardAmount(NumberUtil.numberValue(this.getBankCardAmount(),0d));
    depositDTO.setCash(NumberUtil.numberValue(this.getCash(),0d));
    depositDTO.setCheckAmount(NumberUtil.numberValue(this.getCheckAmount(),0d));
    depositDTO.setCheckNo(this.getCheckNo());
    depositDTO.setSupplierId(this.getSupplierId());
    depositDTO.setShopId(this.getShopId());
    depositDTO.setPayTime(this.getLastModified());
    depositDTO.setMemo(this.getMemo());
    return depositDTO;
  }

  public void FromDTO(DepositDTO depositDTO){
     BeanUtils.copyProperties(depositDTO, this, new String[]{"id", "payTime"});
  }

}
