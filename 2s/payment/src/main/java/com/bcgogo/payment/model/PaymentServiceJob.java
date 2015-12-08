package com.bcgogo.payment.model;

import com.bcgogo.model.LongIdentifier;
import com.bcgogo.payment.dto.PaymentServiceJobDTO;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "payment_service_job")
public class PaymentServiceJob extends LongIdentifier {

  public Long transactionId;
  public Long queryTimes;


  @Column(name = "transaction_id")
  public Long getTransactionId() {
    return transactionId;
  }

  public void setTransactionId(Long transactionId) {
    this.transactionId = transactionId;
  }

  @Column(name = "query_times")
  public Long getQueryTimes() {
    return queryTimes;
  }

  public void setQueryTimes(Long queryTimes) {
    this.queryTimes = queryTimes;
  }


  public PaymentServiceJob() {

  }

  public PaymentServiceJob(PaymentServiceJobDTO paymentServiceJobDTO) {
    this.setId(paymentServiceJobDTO.getId());
    this.setTransactionId(paymentServiceJobDTO.getTransactionId());
    this.setQueryTimes(paymentServiceJobDTO.getQueryTimes());
  }

  public PaymentServiceJob fromDTO(PaymentServiceJobDTO paymentServiceJobDTO) {
    this.setId(paymentServiceJobDTO.getId());
    this.setTransactionId(paymentServiceJobDTO.getTransactionId());
    this.setQueryTimes(paymentServiceJobDTO.getQueryTimes());

    return this;
  }

  public PaymentServiceJobDTO toDTO() {
    PaymentServiceJobDTO paymentServiceJobDTO = new PaymentServiceJobDTO();
    paymentServiceJobDTO.setId(this.getId());
    paymentServiceJobDTO.setTransactionId(this.getTransactionId());
    paymentServiceJobDTO.setQueryTimes(this.getQueryTimes());

    return paymentServiceJobDTO;
  }
}


