package com.bcgogo.enums.Product;

/**
 * 后台CRM标准产品采购统计类型
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 13-2-7
 * Time: 下午5:16
 * To change this template use File | Settings | File Templates.
 */
public enum NormalProductStatType {
  WEEK("一周"),
  MONTH("一个月"),
  THREE_MONTH("三个月"),
  HALF_YEAR("半年"),
  YEAR("一年");
  private String normalProductStatType;
  private NormalProductStatType(String normalProductStatType)
  {
    this.normalProductStatType = normalProductStatType;
  }

  public String getNormalProductStatType() {
    return normalProductStatType;
  }
}
