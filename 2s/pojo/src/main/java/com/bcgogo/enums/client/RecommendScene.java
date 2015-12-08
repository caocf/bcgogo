package com.bcgogo.enums.client;

import com.bcgogo.enums.txn.pushMessage.PushMessageType;

import java.util.HashMap;
import java.util.Map;

/**
 * User: ZhangJuntao
 * Date: 13-6-6
 * Time: 下午4:38
 */
public enum RecommendScene {
  //配件 求购
  ACCESSORY_OR_BUYING,
  //订单
  ORDER,
  //关联
  RELEVANCE,
  //系统
  SYSTEM,
  //预约单
  APPOINT_ORDER,

  ;//

  private static Map<PushMessageType, RecommendScene> pushMessageTypeMapping = new HashMap<PushMessageType, RecommendScene>();

  static {
    pushMessageTypeMapping.put(PushMessageType.SALE_NEW, ORDER);
    pushMessageTypeMapping.put(PushMessageType.SALE_RETURN_NEW, ORDER);
    pushMessageTypeMapping.put(PushMessageType.PURCHASE_SELLER_STOCK, ORDER);
    pushMessageTypeMapping.put(PushMessageType.PURCHASE_SELLER_DISPATCH, ORDER);
    pushMessageTypeMapping.put(PushMessageType.PURCHASE_SELLER_REFUSED, ORDER);

    pushMessageTypeMapping.put(PushMessageType.APPLY_SUPPLIER, RELEVANCE);
    pushMessageTypeMapping.put(PushMessageType.APPLY_CUSTOMER, RELEVANCE);
    pushMessageTypeMapping.put(PushMessageType.MATCHING_RECOMMEND_CUSTOMER, RELEVANCE);
    pushMessageTypeMapping.put(PushMessageType.MATCHING_RECOMMEND_SUPPLIER, RELEVANCE);

    pushMessageTypeMapping.put(PushMessageType.ANNOUNCEMENT, SYSTEM);
    pushMessageTypeMapping.put(PushMessageType.FESTIVAL, SYSTEM);

    pushMessageTypeMapping.put(PushMessageType.BUYING_INFORMATION, ACCESSORY_OR_BUYING);
    pushMessageTypeMapping.put(PushMessageType.BUSINESS_CHANCE_LACK, ACCESSORY_OR_BUYING);
    pushMessageTypeMapping.put(PushMessageType.BUSINESS_CHANCE_SELL_WELL, ACCESSORY_OR_BUYING);
    pushMessageTypeMapping.put(PushMessageType.ACCESSORY, ACCESSORY_OR_BUYING);
    pushMessageTypeMapping.put(PushMessageType.ACCESSORY_PROMOTIONS, ACCESSORY_OR_BUYING);
    pushMessageTypeMapping.put(PushMessageType.RECOMMEND_ACCESSORY_BY_QUOTED, ACCESSORY_OR_BUYING);

    pushMessageTypeMapping.put(PushMessageType.APP_CANCEL_APPOINT, APPOINT_ORDER);
    pushMessageTypeMapping.put(PushMessageType.APP_APPLY_APPOINT, APPOINT_ORDER);
    pushMessageTypeMapping.put(PushMessageType.OVERDUE_APPOINT_TO_SHOP, APPOINT_ORDER);
    pushMessageTypeMapping.put(PushMessageType.SOON_EXPIRE_APPOINT_TO_SHOP, APPOINT_ORDER);

  }


  public static RecommendScene getByPushMessageType(PushMessageType type) {
    return pushMessageTypeMapping.get(type);
  }

}
