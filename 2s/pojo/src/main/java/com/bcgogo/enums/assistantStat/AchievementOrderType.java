package com.bcgogo.enums.assistantStat;

/**
 * Created with IntelliJ IDEA.
 * User: lw
 * Date: 13-11-13
 * Time: 下午2:14
 * To change this template use File | Settings | File Templates.
 */

//提成统计中按类别统计
public enum AchievementOrderType {
  WASH_BEAUTY("洗车美容"),
  REPAIR_SERVICE("施工"),
  SALES("商品销售"),
  MEMBER("会员卡销售"),
  ALL("全部"),
  BUSINESS_ACCOUNT("营业外记账");
  private final String name;

  private AchievementOrderType(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
}
