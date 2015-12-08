package com.bcgogo.payment.dto;

import java.io.Serializable;

public class SequenceNoDTO implements Serializable {
  private Long id;
  private Long transId;
  private String sequenceNo;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getTransId() {
    return transId;
  }

  public void setTransId(Long transId) {
    this.transId = transId;
  }


  public String getSequenceNo() {
    return sequenceNo;
  }

  public void setSequenceNo(String sequenceNo) {
    this.sequenceNo = sequenceNo;
  }

}
