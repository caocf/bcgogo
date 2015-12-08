package com.bcgogo.payment.model;

import com.bcgogo.model.LongIdentifier;
import com.bcgogo.payment.dto.SequenceNoDTO;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by IntelliJ IDEA.
 * User: sunyingzi
 * Date: 11-12-14
 * Time: 下午5:50
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "sequence_no")
public class SequenceNo extends LongIdentifier {
  private Long transId;
  private String sequenceNo;

  @Column(name = "trans_id")
  public Long getTransId() {
    return transId;
  }

  public void setTransId(Long transId) {
    this.transId = transId;
  }

  @Column(name = "sequence_no", length = 20)
  public String getSequenceNo() {
    return sequenceNo;
  }

  public void setSequenceNo(String sequenceNo) {
    this.sequenceNo = sequenceNo;
  }


  public SequenceNo() {
  }

  public SequenceNo(SequenceNoDTO sequenceNoDTO) {

    this.setId(sequenceNoDTO.getId());
    this.setTransId(sequenceNoDTO.getTransId());
    this.setSequenceNo(sequenceNoDTO.getSequenceNo());
  }

  public SequenceNo fromDTO(SequenceNoDTO sequenceNoDTO) {

    this.setId(sequenceNoDTO.getId());
    this.setTransId(sequenceNoDTO.getTransId());
    this.setSequenceNo(sequenceNoDTO.getSequenceNo());
    return this;
  }

  public SequenceNoDTO toDTO() {
    SequenceNoDTO sequenceNoDTO = new SequenceNoDTO();

    sequenceNoDTO.setId(this.getId());
    sequenceNoDTO.setTransId(this.getTransId());
    sequenceNoDTO.setSequenceNo(this.getSequenceNo());

    return sequenceNoDTO;
  }
}
