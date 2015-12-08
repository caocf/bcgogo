package com.bcgogo.enums.payment;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-10-22
 * Time: 下午3:47
 * 货款转账 状态
 */
public enum LoanTransfersStatus {
  LOAN_START("开始转账"),
  LOAN_IN("转账中"),
  LOAN_TO_CONFIRM("转账待确认"),
  LOAN_SUCCESS("转账成功"),
  LOAN_FAIL("转账失败");

  private String value;

  LoanTransfersStatus(String value) {
    this.value = value;
  }

  public String getValue() {
    return this.value;
  }
}
