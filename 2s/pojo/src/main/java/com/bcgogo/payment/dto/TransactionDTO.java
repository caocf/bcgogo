package com.bcgogo.payment.dto;

import java.io.Serializable;

public class TransactionDTO implements Serializable {
  public Long id;
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

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getTransactionType() {
    return transactionType;
  }

  public void setTransactionType(Long transactionType) {
    this.transactionType = transactionType;
  }

  public Long getBaseId() {
    return baseId;
  }

  public void setBaseId(Long baseId) {
    this.baseId = baseId;
  }

  public Long getParentId() {
    return parentId;
  }

  public void setParentId(Long parentId) {
    this.parentId = parentId;
  }

  public Long getReferenceType() {
    return referenceType;
  }

  public void setReferenceType(Long referenceType) {
    this.referenceType = referenceType;
  }

  public Long getReferenceId() {
    return referenceId;
  }

  public void setReferenceId(Long referenceId) {
    this.referenceId = referenceId;
  }

  public Long getAmount() {
    return amount;
  }

  public void setAmount(Long amount) {
    this.amount = amount;
  }

  public Long getCurrency() {
    return currency;
  }

  public void setCurrency(Long currency) {
    this.currency = currency;
  }

  public Long getPayMethod() {
    return payMethod;
  }

  public void setPayMethod(Long payMethod) {
    this.payMethod = payMethod;
  }

  public Long getPayerId() {
    return payerId;
  }

  public void setPayerId(Long payerId) {
    this.payerId = payerId;
  }

  public String getPspTransactionId() {
    return pspTransactionId;
  }

  public void setPspTransactionId(String pspTransactionId) {
    this.pspTransactionId = pspTransactionId;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

}
