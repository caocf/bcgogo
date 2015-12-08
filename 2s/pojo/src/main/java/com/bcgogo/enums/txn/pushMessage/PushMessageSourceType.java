package com.bcgogo.enums.txn.pushMessage;

import java.util.HashMap;
import java.util.Map;

public enum PushMessageSourceType {
  PUSH_MESSAGE,
  PRODUCT,
  PRE_BUY_ORDER_ITEM,
  QUOTED_PRE_BUY_ORDER_ITEM,
  MATCHING_RECOMMEND_CUSTOMER_SHOP,
  MATCHING_RECOMMEND_SUPPLIER_SHOP,

  //公告
  ANNOUNCEMENT,
  //节日
  FESTIVAL,

  //客户推荐
  APPLY_CUSTOMER,
  //供应商 推荐
  APPLY_SUPPLIER,

  //卖家备货
  PURCHASE_SELLER_STOCK,
  //已发货
  PURCHASE_SELLER_DISPATCH,
  //拒绝
  PURCHASE_SELLER_REFUSED,
  //新订单
  SALE_NEW,
  //新退货单
  SALE_RETURN_NEW,

  //店铺接受预约单
  SHOP_ACCEPT_APPOINT,
  SYS_ACCEPT_APPOINT,
  //店铺预约拒绝消息
  SHOP_REJECT_APPOINT,
  //店铺预约取消消息
  SHOP_CANCEL_APPOINT,
  //店铺预约修改消息
  SHOP_CHANGE_APPOINT,
  //店铺预约结束消息
  SHOP_FINISH_APPOINT,
  //店铺报价的消息
  SHOP_QUOTE_TO_APP,
  //app提交询价单
  APP_SUBMIT_ENQUIRY,
  //APP预约取消消息
  APP_CANCEL_APPOINT,
  //APP预约申请消息
  APP_APPLY_APPOINT,
  //过期预约单
  OVERDUE_APPOINT_TO_APP,
  SOON_EXPIRE_APPOINT_TO_APP,
  OVERDUE_APPOINT_TO_SHOP,
  SOON_EXPIRE_APPOINT_TO_SHOP,
  //保养里程
  APP_VEHICLE_MAINTAIN_MILEAGE,
  //保养时间
  APP_VEHICLE_MAINTAIN_TIME,
  //保险时间
  APP_VEHICLE_INSURANCE_TIME,
  //验车时间
  APP_VEHICLE_EXAMINE_TIME,
  //故障消息
  VEHICLE_FAULT_2_SHOP,
  //保养里程
  APP_VEHICLE_SOON_EXPIRE_MAINTAIN_MILEAGE_2_SHOP,
  //保养时间
  APP_VEHICLE_SOON_EXPIRE_MAINTAIN_TIME_2_SHOP,
  //保险时间
  APP_VEHICLE_SOON_EXPIRE_INSURANCE_TIME_2_SHOP,
  //验车时间
  APP_VEHICLE_SOON_EXPIRE_EXAMINE_TIME_2_SHOP,

  //保养里程
  APP_VEHICLE_OVERDUE_MAINTAIN_MILEAGE_2_SHOP,
  //保养时间
  APP_VEHICLE_OVERDUE_MAINTAIN_TIME_2_SHOP,
  //保险时间
  APP_VEHICLE_OVERDUE_INSURANCE_TIME_2_SHOP,
  //验车时间
  APP_VEHICLE_OVERDUE_EXAMINE_TIME_2_SHOP,
    //故障消息
  VEHICLE_FAULT_2_APP,

  //店铺公告消息
  SHOP_ADVERT_TO_APP,

    //违章记录
    VIOLATE_REGULATION_RECORD_2_APP,

    //对话消息 微信端-->客户端
  MSG_FROM_WX_USER_TO_MIRROR,
      //对话消息 客户端--> 微信端
  MSG_FROM_MIRROR_TO_WX_USER,
  //对话消息 4s店铺-->客户端
  MSG_FROM_SHOP,
  ;

  private static Map<PushMessageSourceType, PushMessageType> pushMessageTypeMapping = new HashMap<PushMessageSourceType, PushMessageType>();

  static {
    pushMessageTypeMapping.put(SALE_NEW, PushMessageType.SALE_NEW);
    pushMessageTypeMapping.put(SALE_RETURN_NEW, PushMessageType.SALE_RETURN_NEW);

    pushMessageTypeMapping.put(PURCHASE_SELLER_STOCK, PushMessageType.PURCHASE_SELLER_STOCK);
    pushMessageTypeMapping.put(PURCHASE_SELLER_DISPATCH, PushMessageType.PURCHASE_SELLER_DISPATCH);
    pushMessageTypeMapping.put(PURCHASE_SELLER_REFUSED, PushMessageType.PURCHASE_SELLER_REFUSED);

    pushMessageTypeMapping.put(APPLY_SUPPLIER, PushMessageType.APPLY_SUPPLIER);
    pushMessageTypeMapping.put(APPLY_CUSTOMER, PushMessageType.APPLY_CUSTOMER);

    pushMessageTypeMapping.put(ANNOUNCEMENT, PushMessageType.ANNOUNCEMENT);
    pushMessageTypeMapping.put(FESTIVAL, PushMessageType.FESTIVAL);

  }


  public static PushMessageType getPushMessageType(PushMessageSourceType sourceType) {
    return pushMessageTypeMapping.get(sourceType);
  }

}
