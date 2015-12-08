package com.bcgogo.enums;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 13-1-8
 * Time: 下午1:43
 * To change this template use File | Settings | File Templates.
 */
public enum InsuranceValidateScene {
  CHECK_ALL("CHECK_POLICY_NO,CHECK_REPORT_NO"),
  CHECK_POLICY_NO("CHECK_POLICY_NO"),
  CHECK_REPORT_NO("CHECK_REPORT_NO"),
  CHECK_REPAIR_ORDER_ID("CHECK_REPAIR_ORDER_ID"),
  CHECK_REPAIR_DRAFT_ORDER_ID("CHECK_REPAIR_DRAFT_ORDER_ID");
  private final String name;

  private InsuranceValidateScene(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
}
