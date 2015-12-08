package com.bcgogo.enums.user;

/**
 * User: ZhangJuntao
 * Date: 12-7-12
 * Time: 下午2:20
 * 用户组 类型
 */
public enum UserGroupType {
  BCGOGO_ADMIN("BCGOGO管理员"),       //BCGOGO管理员
  SHOP_GROUPNAME_ADMIN("老板/财务"), //店铺用户组名称-店铺最高权限
  RECEPTION("前台"),
  WAREHOUSE("仓管"),
  MANAGER("经理/店长");

  private String value;

  UserGroupType(String value) {
    this.value = value;
  }

  public String getValue() {
    return this.value;
  }
}
