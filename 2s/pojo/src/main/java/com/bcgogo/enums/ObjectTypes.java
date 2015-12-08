package com.bcgogo.enums;

/**
 * Created with IntelliJ IDEA.
 * User: xzhu
 * Date: 12-11-12
 * Time: 下午2:54
 * 用到的时候可以自己增加  比如 产品的操作日志：PRODUCT("商品")  或者 CUSTOMER("客户")
 */
public enum ObjectTypes {
  STOREHOUSE("仓库"),
  ALLOCATE_RECORD("仓库调拨单"),
  PRE_BUY_ORDER("预购单"),
  QUOTED_PRE_BUY_ORDER("预购报价单"),
  PURCHASE_ORDER("采购单"),
  INVENTORY_ORDER("入库单"),
  SALE_ORDER("销售单"),
  REPAIR_ORDER("施工单"),
  WASH_ORDER("洗车单"),
  PURCHASE_RETURN_ORDER("入库退货单"),
  SALE_RETURN_ORDER("销售退货单"),
  MEMBER_CARD_BUY_ORDER("会员购卡单"),
  MEMBER_CARD_RETURN_ORDER("会员退卡单"),
  REPAIR_PICKING("维修领料"),
  INNER_PICKING("内部领料"),
  INNER_RETURN("内部退料"),
  BORROW_ORDER("外部借调"),
  PROMOTIONS("促销活动"),
  APPLY_SUPPLIER("客户申请批发商关联"),
  APPLY_CUSTOMER("批发商申请客户关联"),
  CANCEL_SHOP_RELATION("取消店铺关联"),
  CUSTOMER_CANCEL_WHOLESALER_SHOP_RELATION("客户取消店铺关联"),
  WHOLESALER_CANCEL_CUSTOMER_SHOP_RELATION("供应商取消店铺关联"),
  APPOINT_ORDER("预约单"),
  //bcgogo
  BCGOGO_SOFTWARE_RECEIVABLE_ORDER("BCGOGO软件收款单"),
  BCGOGO_HARDWARE_RECEIVABLE_ORDER("BCGOGO硬件收款单"),
  BCGOGO_SMS_RECHARGE_RECEIVABLE_ORDER("BCGOGO短信收款单")
  ;

  private final String name;
  private ObjectTypes(String name){
    this.name = name;
  }

  public String getName(){
    return name;
  }
}
