package com.bcgogo.enums;


import org.apache.commons.lang.StringUtils;

/**
 * 预约金取用方式
 * Created by IntelliJ IDEA.
 * User: zhuj
 * Date: 13-5-14
 * Time: 上午10:03
 * To change this template use File | Settings | File Templates.
 */
public enum DepositType {

  DEPOSIT("DEPOSIT", "充值", InOutFlag.IN_FLAG),
  SALES("SALES", "销售金", InOutFlag.OUT_FLAG),
  SALES_REPEAL("SALES_REPEAL", "销售作废退款", InOutFlag.IN_FLAG),
  SALES_BACK("SALES_BACK", "销售退货款", InOutFlag.IN_FLAG),
  SALES_BACK_REPEAL("SALES_BACK_REPEAL", "销售退货作废", InOutFlag.OUT_FLAG),
  INVENTORY("INVENTORY", "入库", InOutFlag.OUT_FLAG),
  INVENTORY_REPEAL("INVENTORY_REPEAL", "入库作废退款", InOutFlag.IN_FLAG),
  INVENTORY_BACK("INVENTORY_BACK", "入库退货退款", InOutFlag.IN_FLAG),
  INVENTORY_BACK_REPEAL("INVENTORY_BACK_REPEAL", "入库退货作废", InOutFlag.OUT_FLAG),
  COMPARE("COMPARE", "对账", InOutFlag.OUT_FLAG);

  /**
   * 预约金收、取方式的code
   */
  private String scene;
  /**
   * 预约金收、取方式的中文描述
   */
  private String value;
  /**
   * 出、入FLAG
   */
  private InOutFlag inOutFlag;

  public InOutFlag getInOutFlag() {
    return inOutFlag;
  }

  public void setInOutFlag(InOutFlag inOutFlag) {
    this.inOutFlag = inOutFlag;
  }

  public String getScene() {
    return scene;
  }

  public void setScene(String name) {
    this.scene = name;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  DepositType(String name, String value, InOutFlag InOutFlag) {
    this.scene = name;
    this.value = value;
    this.inOutFlag = InOutFlag;
  }

  public static DepositType getDepositTypeBySceneAndInOutFlag(String scene, InOutFlag inOutFlag) {
    for (DepositType depositType : DepositType.values()) {
      if (StringUtils.equals(scene, depositType.getScene()) && inOutFlag == depositType.getInOutFlag()) {
        return depositType;
      }
    }
    return null;
  }


}
