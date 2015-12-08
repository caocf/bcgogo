package com.bcgogo.enums.shop;

/**
 * User: ZhangJuntao
 * Date: 13-3-30
 * Time: 下午5:50
 */
public enum BargainStatus {
  NO_BARGAIN("无议价"),
  PENDING_REVIEW("待审核"),
  AUDIT_REFUSE("审核拒绝"),
  AUDIT_PASS("审核通过");

  private String value;

  BargainStatus(String value) {
    this.value = value;
  }

  public String getValue() {
    return this.value;
  }
}
