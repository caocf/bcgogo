package com.bcgogo.enums.app;

import java.util.HashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 13-10-22
 * Time: 上午9:49
 */
public enum  EnquiryTargetShopStatus {
  DISABLED("已删除"),
  SELECTED("已选中"),
  UNSELECTED("未选中"),
  SENT("已发送");

  private final String name;

  private EnquiryTargetShopStatus(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public static Set<EnquiryTargetShopStatus> ENABLED_STATUSES = new HashSet<EnquiryTargetShopStatus>();

  static {
    ENABLED_STATUSES.add(SELECTED);
    ENABLED_STATUSES.add(UNSELECTED);
    ENABLED_STATUSES.add(SENT);
  }
}
