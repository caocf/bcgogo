package com.bcgogo.payment.model;

import com.bcgogo.model.LongIdentifier;
import com.bcgogo.payment.dto.TransactionDTO;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by IntelliJ IDEA.
 * User: sunyingzi
 * Date: 11-12-13
 * Time: 上午9:15
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "transaction")
public class Transaction extends LongIdentifier {

  public Long transactionType;
  public Long baseId;
  public Long parentId;
  public Long referenceType;
  public Long referenceId;
  public Long amount;
  public Long currency;
  public Long payMethod;
  public Long payerId;
  public String pspTransactionId;
  public String status;

  @Column(name = "transaction_type")
  public Long getTransactionType() {
    return transactionType;
  }

  public void setTransactionType(Long transactionType) {
    this.transactionType = transactionType;
  }

  @Column(name = "base_id")
  public Long getBaseId() {
    return baseId;
  }

  public void setBaseId(Long baseId) {
    this.baseId = baseId;
  }

  @Column(name = "parent_id")
  public Long getParentId() {
    return parentId;
  }

  public void setParentId(Long parentId) {
    this.parentId = parentId;
  }

  @Column(name = "reference_type")
  public Long getReferenceType() {
    return referenceType;
  }

  public void setReferenceType(Long referenceType) {
    this.referenceType = referenceType;
  }

  @Column(name = "reference_id")
  public Long getReferenceId() {
    return referenceId;
  }

  public void setReferenceId(Long referenceId) {
    this.referenceId = referenceId;
  }

  @Column(name = "amount")
  public Long getAmount() {
    return amount;
  }

  public void setAmount(Long amount) {
    this.amount = amount;
  }

  @Column(name = "currency")
  public Long getCurrency() {
    return currency;
  }

  public void setCurrency(Long currency) {
    this.currency = currency;
  }

  @Column(name = "pay_method")
  public Long getPayMethod() {
    return payMethod;
  }

  public void setPayMethod(Long payMethod) {
    this.payMethod = payMethod;
  }

  @Column(name = "payer_id")
  public Long getPayerId() {
    return payerId;
  }

  public void setPayerId(Long payerId) {
    this.payerId = payerId;
  }

  @Column(name = "psp_transaction_id", length = 50)
  public String getPspTransactionId() {
    return pspTransactionId;
  }

  public void setPspTransactionId(String pspTransactionId) {
    this.pspTransactionId = pspTransactionId;
  }

  @Column(name = "status", length = 20)
  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public Transaction() {
  }

  public Transaction(TransactionDTO transactionDTO) {

    this.setId(transactionDTO.getId());
    this.setTransactionType(transactionDTO.getTransactionType());
    this.setBaseId(transactionDTO.getBaseId());
    this.setParentId(transactionDTO.getParentId());
    this.setReferenceType(transactionDTO.getReferenceType());
    this.setReferenceId(transactionDTO.getReferenceId());
    this.setAmount(transactionDTO.getAmount());
    this.setCurrency(transactionDTO.getCurrency());
    this.setPayMethod(transactionDTO.getPayMethod());
    this.setPayerId(transactionDTO.getPayerId());
    this.setPspTransactionId(transactionDTO.getPspTransactionId());
    this.setStatus(transactionDTO.getStatus());
  }

  public Transaction fromDTO(TransactionDTO transactionDTO) {

    //this.setId(billDTO.getId());
    this.setId(transactionDTO.getId());
    this.setTransactionType(transactionDTO.getTransactionType());
    this.setBaseId(transactionDTO.getBaseId());
    this.setParentId(transactionDTO.getParentId());
    this.setReferenceType(transactionDTO.getReferenceType());
    this.setReferenceId(transactionDTO.getReferenceId());
    this.setAmount(transactionDTO.getAmount());
    this.setCurrency(transactionDTO.getCurrency());
    this.setPayMethod(transactionDTO.getPayMethod());
    this.setPayerId(transactionDTO.getPayerId());
    this.setPspTransactionId(transactionDTO.getPspTransactionId());
    this.setStatus(transactionDTO.getStatus());
    return this;
  }

  public TransactionDTO toDTO() {

    TransactionDTO transactionDTO = new TransactionDTO();
    transactionDTO.setId(this.getId());
    transactionDTO.setTransactionType(this.getTransactionType());
    transactionDTO.setBaseId(this.getBaseId());
    transactionDTO.setParentId(this.getParentId());
    transactionDTO.setReferenceType(this.getReferenceType());
    transactionDTO.setReferenceId(this.getReferenceId());
    transactionDTO.setAmount(this.getAmount());
    transactionDTO.setCurrency(this.getCurrency());
    transactionDTO.setPayMethod(this.getPayMethod());
    transactionDTO.setPayerId(this.getPayerId());
    transactionDTO.setPspTransactionId(this.getPspTransactionId());
    transactionDTO.setStatus(this.getStatus());
    return transactionDTO;
  }
}
