package com.bcgogo.remind.dto.message;

import com.bcgogo.enums.txn.message.ReceiverStatus;

/**
 * Created with IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 13-1-24
 * Time: 上午10:33
 */
public class Operator {
  private ReceiverStatus status;
  private String type;
  private int range;

  public ReceiverStatus getStatus() {
    return status;
  }

  public void setStatus(ReceiverStatus status) {
    this.status = status;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public int getRange() {
    return range;
  }

  public void setRange(int range) {
    this.range = range;
  }
}
