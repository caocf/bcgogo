package com.bcgogo.txn.model;

import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.ReceivableHistoryDTO;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 客户付款历史记录实体类
 * Created by IntelliJ IDEA.
 * User: liuWei
 * Date: 13-1-8
 * Time: 下午2:09
 * To change this template use File | Settings | File Templates.
 */

@Entity
@Table(name = "receivable_history")
public class ReceivableHistory extends LongIdentifier {
  /*店面ID*/
  private Long shopId;
  private Double total;
  /* 扣款*/
  private Double discount;
  /*欠款挂账*/
  private Double debt;
  /*现金*/
  private Double cash;
  /*银行卡*/
  private Double bankCardAmount;
  /*支票*/
  private Double checkAmount;
  /*支票号码*/
  private String checkNo;
  /** 预收款 */
  private Double deposit; // add by zhuj
  /*会员支付*/
  private Double memberBalancePay;
  /*会员id*/
  private Long memberId;
  /*会员号码*/
  private String memberNo;
  /*冲账*/
  private Double strikeAmount;
  /*实收*/
  private Double settledAmount;
  /*客户id*/
  private Long customerId;
  /*付款时间*/
  private Long receivableDate;

  private String receiver;     //结算人
  private Long receiverId;//结算人id

  public ReceivableHistory() {

  }

  public ReceivableHistory(ReceivableHistoryDTO receivableHistoryDTO) {
    setId(receivableHistoryDTO.getId());
    setShopId(receivableHistoryDTO.getShopId());
    setTotal(receivableHistoryDTO.getTotal());
    setDiscount(receivableHistoryDTO.getDiscount());
    setDebt(receivableHistoryDTO.getDebt());
    setCash(receivableHistoryDTO.getCash());
    setBankCardAmount(receivableHistoryDTO.getBankCardAmount());
    setCheckAmount(receivableHistoryDTO.getCheckAmount());
    setCheckNo(receivableHistoryDTO.getCheckNo());
    setDeposit(receivableHistoryDTO.getDeposit()); // add by zhuj
    setMemberBalancePay(receivableHistoryDTO.getMemberBalancePay());
    setMemberId(receivableHistoryDTO.getMemberId());
    setMemberNo(receivableHistoryDTO.getMemberNo());
    setStrikeAmount(receivableHistoryDTO.getStrikeAmount());
    setSettledAmount(receivableHistoryDTO.getSettledAmount());
    setCustomerId(receivableHistoryDTO.getCustomerId());
    setReceivableDate(receivableHistoryDTO.getReceivableDate());
    setReceiver(receivableHistoryDTO.getReceiver());
    setReceiverId(receivableHistoryDTO.getReceiverId());
  }


  public ReceivableHistory fromDTO(ReceivableHistoryDTO receivableHistoryDTO, boolean setId) {
    if (receivableHistoryDTO == null) {
      return this;
    }
    if (setId) {
      setId(receivableHistoryDTO.getId());
    }
    setShopId(receivableHistoryDTO.getShopId());
    setTotal(receivableHistoryDTO.getTotal());
    setDiscount(receivableHistoryDTO.getDiscount());
    setDebt(receivableHistoryDTO.getDebt());
    setCash(receivableHistoryDTO.getCash());
    setBankCardAmount(receivableHistoryDTO.getBankCardAmount());
    setCheckAmount(receivableHistoryDTO.getCheckAmount());
    setCheckNo(receivableHistoryDTO.getCheckNo());
    setDeposit(receivableHistoryDTO.getDeposit()); // add by zhuj
    setMemberBalancePay(receivableHistoryDTO.getMemberBalancePay());
    setMemberId(receivableHistoryDTO.getMemberId());
    setMemberNo(receivableHistoryDTO.getMemberNo());
    setStrikeAmount(receivableHistoryDTO.getStrikeAmount());
    setSettledAmount(receivableHistoryDTO.getSettledAmount());
    setCustomerId(receivableHistoryDTO.getCustomerId());
    setReceivableDate(receivableHistoryDTO.getReceivableDate());
    setReceiver(receivableHistoryDTO.getReceiver());
    setReceiverId(receivableHistoryDTO.getReceiverId());

    return this;
  }

  public ReceivableHistoryDTO toDTO() {
    ReceivableHistoryDTO receivableHistoryDTO = new ReceivableHistoryDTO();
    receivableHistoryDTO.setShopId(this.getShopId());
    receivableHistoryDTO.setTotal(this.getTotal());
    receivableHistoryDTO.setDiscount(this.getDiscount());
    receivableHistoryDTO.setDebt(this.getDebt());
    receivableHistoryDTO.setCash(this.getCash());
    receivableHistoryDTO.setBankCardAmount(this.getBankCardAmount());
    receivableHistoryDTO.setCheckAmount(this.getCheckAmount());
    receivableHistoryDTO.setCheckNo(this.getCheckNo());
    receivableHistoryDTO.setDeposit(this.getDeposit()); // add by zhuj
    receivableHistoryDTO.setMemberBalancePay(this.getMemberBalancePay());
    receivableHistoryDTO.setMemberId(this.getMemberId());
    receivableHistoryDTO.setMemberNo(this.getMemberNo());
    receivableHistoryDTO.setStrikeAmount(this.getStrikeAmount());
    receivableHistoryDTO.setSettledAmount(this.getSettledAmount());
    receivableHistoryDTO.setCustomerId(this.getCustomerId());
    receivableHistoryDTO.setReceivableDate(this.getReceivableDate());
    receivableHistoryDTO.setReceiver(this.getReceiver());
    receivableHistoryDTO.setReceiverId(this.getReceiverId());
    return receivableHistoryDTO;
  }

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "total")
  public Double getTotal() {
    return total;
  }

  public void setTotal(Double total) {
    this.total = total;
  }

  @Column(name = "discount")
  public Double getDiscount() {
    return discount;
  }

  public void setDiscount(Double discount) {
    this.discount = discount;
  }

  @Column(name = "debt")
  public Double getDebt() {
    return debt;
  }

  public void setDebt(Double debt) {
    this.debt = debt;
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

  @Column(name = "member_balance_pay")
  public Double getMemberBalancePay() {
    return memberBalancePay;
  }

  public void setMemberBalancePay(Double memberBalancePay) {
    this.memberBalancePay = memberBalancePay;
  }

  @Column(name = "member_id")
  public Long getMemberId() {
    return memberId;
  }

  public void setMemberId(Long memberId) {
    this.memberId = memberId;
  }

  @Column(name = "member_no")
  public String getMemberNo() {
    return memberNo;
  }

  public void setMemberNo(String memberNo) {
    this.memberNo = memberNo;
  }

  @Column(name = "strike_amount")
  public Double getStrikeAmount() {
    return strikeAmount;
  }

  public void setStrikeAmount(Double strikeAmount) {
    this.strikeAmount = strikeAmount;
  }

  @Column(name = "settled_amount")
  public Double getSettledAmount() {
    return settledAmount;
  }

  public void setSettledAmount(Double settledAmount) {
    this.settledAmount = settledAmount;
  }

  @Column(name = "customer_id")
  public Long getCustomerId() {
    return customerId;
  }

  public void setCustomerId(Long customerId) {
    this.customerId = customerId;
  }

  @Column(name = "receivable_date")
  public Long getReceivableDate() {
    return receivableDate;
  }

  public void setReceivableDate(Long receivableDate) {
    this.receivableDate = receivableDate;
  }

  @Column(name = "receiver")
  public String getReceiver() {
    return receiver;
  }

  public void setReceiver(String receiver) {
    this.receiver = receiver;
  }

  @Column(name = "receiver_id")
  public Long getReceiverId() {
    return receiverId;
  }

  public void setReceiverId(Long receiverId) {
    this.receiverId = receiverId;
  }
  
  @Column(name= "deposit")
  public Double getDeposit() {
    return deposit;
  }

  public void setDeposit(Double deposit) {
    this.deposit = deposit;
  }

}
