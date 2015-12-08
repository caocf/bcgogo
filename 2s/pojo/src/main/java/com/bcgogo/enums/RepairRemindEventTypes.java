package com.bcgogo.enums;

/**
 * Created by IntelliJ IDEA.
 * User: Jimuchen
 * Date: 12-6-13
 * Time: 下午7:25
 * To change this template use File | Settings | File Templates.
 */
public enum RepairRemindEventTypes {
  PENDING("待交付"),         //待交付
  LACK("缺料待修"),            //缺料待修
  DEBT("还款"),            //还款
  FINISH("完工"),          //短信
  INCOMING("来料待修"),        //来料待修
  WAIT_OUT_STORAGE("待领料"),        //待领料
  OUT_STORAGE("领料待修");        //已出库

  private final String name;
  private RepairRemindEventTypes(String name){
    this.name=name;
  }

  public String getName() {
    return name;
  }
}
