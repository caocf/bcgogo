package com.bcgogo.constant;

/**
 * User: ZhangJuntao
 * Date: 12-6-18
 * Time: 上午11:38
 * 权限枚举
 */
@Deprecated
public enum PermissionConstants {
  /**
   * SHOP 版本类型
   */
  TXN_SHOP("进销存"),
  INTEGRATED_SHOP("综合店"),
  CHAIN_SHOP("连锁店"),
  BCGOGO_SHOP("BCGOGO店"),
	REPAIR_SHOP("汽修店"),

  /**
   * userGroup
   */
  BCGOGO_ADMIN("BCGOGO管理员"),   //BCGOGO管理员
  BCGOGO_SALEMAN("BCGOGO销售人员"),   //BCGOGO销售
  SHOP_GROUPNAME_ADMIN("老板/财务"), //店铺用户组名称-店铺最高权限
  /**
   * role
   */
  BASE_ROLE ("web_basic_login"),
  BCGOGO_SYSTEM_ROLE ("web_bcgogoSystem");

  private String value;

  PermissionConstants(String value) {
    this.value = value;
  }

  public String getValue() {
    return this.value;
  }
}
