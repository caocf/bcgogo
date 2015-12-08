package com.bcgogo.payment.dto;

import java.io.Serializable;

public class PaymentServiceJobDTO implements Serializable {
  public Long id;
  public Long transactionId;
  public Long queryTimes;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getTransactionId() {
    return transactionId;
  }

  public void setTransactionId(Long transactionId) {
    this.transactionId = transactionId;
  }

  public Long getQueryTimes() {
    return queryTimes;
  }

  public void setQueryTimes(Long queryTimes) {
    this.queryTimes = queryTimes;
  }

}
