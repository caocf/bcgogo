package com.bcgogo.enums.notification;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 13-12-24
 * Time: 下午4:03
 * To change this template use File | Settings | File Templates.
 */
public enum ContactGroupType {
  OTHERS("未分组"),
  MEMBER("有手机会员组"),
  APP_CUSTOMER("手机客户端组"),
  CUSTOMER("有手机客户组"),
  SUPPLIER("有手机供应商组");
  public static Set<ContactGroupType> mobileGroups = new HashSet<ContactGroupType>();
  static {
    mobileGroups.add(MEMBER);
    mobileGroups.add(CUSTOMER);
    mobileGroups.add(SUPPLIER);

  }
  private String type;

  private ContactGroupType(String type){
    this.type=type;
  }
  public String getType(){
    return this.type;
  }
}
