package com.bcgogo.constant.pushMessage;

import com.bcgogo.enums.txn.pushMessage.PushMessageSourceType;

/**
 * User: ZhangJuntao
 * Date: 13-6-9
 * Time: 上午10:24
 */
public class ClientConstant {
  //===================================================关联消息=========================================================
  public final static String RELEVANCE_TITLE = "关联消息";
  public final static String RELEVANCE_RELATED_TITLE = "前往消息中心";
  public final static String RELEVANCE_RELATED_URL = "pushMessage.client?method=receiverPushMessageList&category=RelatedApplyMessage";
  public final static String RELEVANCE_RELATED_MSG = "{shopName}请求关联，前往处理吧！";


  public final static String SINGLE_MATCHING_RECOMMEND_CUSTOMER_RELATED_MSG = "{shopName}与您的客户{customerName}可能匹配，马上去关联吧！";

  public final static String SINGLE_MATCHING_RECOMMEND_SUPPLIER_RELATED_MSG = "{shopName}与您的供应商{supplierName}可能匹配，马上去关联吧！";


  public final static String FEEDBACK_URL = "client?method=feedbackUserAction";

  //===================================================订单消息=========================================================
  public final static String ORDER_TITLE = "订单消息";
  public final static String ORDER_RELATED_TITLE = "前往订单中心";
  public final static String ORDER_RELATED_URL = "orderCenter.client?method=showOrderCenter";

  public final static String ORDER_STATISTICS_SALE_NEW = "新订单共有{number}条待处理";
  public final static String ORDER_STATISTICS_SALE_NEW_URL = "orderCenter.client?method=getTodoOrders&type=TODO_SALE_ORDERS&currTab=NEW";
  public final static String ORDER_STATISTICS_SALE_RETURN_NEW = "新退货单共有{number}条待处理";
  public final static String ORDER_STATISTICS_SALE_RETURN_NEW_URL = "onlineSalesReturnOrder.client?method=toOnlineSalesReturnOrder";

  public final static String ORDER_STATISTICS_PURCHASE_SELLER_STOCK = "卖家备货中：{number}条";
  public final static String ORDER_STATISTICS_PURCHASE_SELLER_STOCK_URL = "orderCenter.client?method=getTodoOrders&type=TODO_PURCHASE_ORDERS&orderStatus=SELLER_STOCK";
  public final static String ORDER_STATISTICS_PURCHASE_SELLER_DISPATCH = "卖家已发货：{number}条";
  public final static String ORDER_STATISTICS_PURCHASE_SELLER_DISPATCH_URL = "orderCenter.client?method=getTodoOrders&type=TODO_PURCHASE_ORDERS&orderStatus=SELLER_DISPATCH";
  public final static String ORDER_STATISTICS_PURCHASE_SELLER_REFUSED = "卖家拒绝销售：{number}条";
  public final static String ORDER_STATISTICS_PURCHASE_SELLER_REFUSED_URL = "orderCenter.client?method=getTodoOrders&type=TODO_PURCHASE_ORDERS&orderStatus=SELLER_REFUSED";

  public final static String ORDER_SALE_NEW_TIP = "您有1条新订单，请前往查看!";
  public final static String ORDER_SALE_NEW_URL = "orderCenter.client?method=getTodoOrders&type=TODO_SALE_ORDERS&startTimeStr=&endTimeStr=&customerName=&supplierName=&receiptNo=&orderStatus=PENDING";

  public final static String ORDER_SALE_RETURN_NEW_TIP = "您有1条新退货单，请前往查看!";
  public final static String ORDER_SALE_RETURN_NEW_URL = "orderCenter.client?method=getTodoOrders&type=TODO_SALE_RETURN_ORDERS&startTimeStr=&endTimeStr=&customerName=&supplierName=&receiptNo=&orderStatus=PENDING";

  public final static String ORDER_PURCHASE_SELLER_STOCK_TIP = "您有1订单卖家已在备货中，请前往查看!";
  public final static String ORDER_PURCHASE_SELLER_STOCK_URL = "orderCenter.client?method=getTodoOrders&type=TODO_PURCHASE_ORDERS&startTimeStr=&endTimeStr=&customerName=&supplierName=&receiptNo=&orderStatus=SELLER_STOCK";

  public final static String ORDER_PURCHASE_SELLER_DISPATCH_TIP = "您有1订单卖家已发货，请前往查看!";
  public final static String ORDER_PURCHASE_SELLER_DISPATCH_URL = "orderCenter.client?method=getTodoOrders&type=TODO_PURCHASE_ORDERS&startTimeStr=&endTimeStr=&customerName=&supplierName=&receiptNo=&orderStatus=SELLER_DISPATCH";

  public final static String ORDER_PURCHASE_SELLER_REFUSED_TIP = "您有1订单卖家已拒绝，请前往查看!";
  public final static String ORDER_PURCHASE_SELLER_REFUSED_URL = "orderCenter.client?method=getTodoOrders&type=TODO_PURCHASE_ORDERS&startTimeStr=&endTimeStr=&customerName=&supplierName=&receiptNo=&orderStatus=SELLER_REFUSED";

  //===================================================系统消息=========================================================
  public final static String SYSTEM_TITLE = "系统消息";
  public final static String SYSTEM_RELATED_TITLE = "前往系统公告";
  public final static String SYSTEM_RELATED_URL = "sysReminder.client?method=toSysAnnouncement";

  public final static String SYSTEM_ANNOUNCEMENT_CONTENT = "一发软件发布新功能啦，赶紧体验吧！";

  public static final String SYSTEM_FESTIVAL_CONTENT = "{name}快到了，发短信祝福您的客户吧！";

  public final static String ACCESSORY_TITLE = "配件消息";
  public final static String ACCESSORY_RELATED_TITLE = "前往配件报价";
  public final static String ACCESSORY_RELATED_URL = "autoAccessoryOnline.client?method=toCommodityQuotations";


  public final static String BUYING_TITLE = "求购消息";
  public final static String BUYING_RELATED_TITLE = "前往求购咨询";
  public final static String BUYING_RELATED_URL = "preBuyOrder.client?method=preBuyInformation";

  public final static String APPOINT_ORDER_TITLE = "预约消息";
  public final static String APPOINT_ORDER_RELATED_TITLE = "前往预约中心";
  public final static String APPOINT_ORDER_RELATED_URL = "appoint.client?method=showAppointOrderList";
  public final static String APPOINT_ORDER_PENDING_CONTENT = "新预约：{number}条";
  public final static String APPOINT_ORDER_PENDING_RELATED_URL = "appoint.client?method=showAppointOrderList&scene=CLIENT_NEW_ORDER";
  public final static String APPOINT_ORDER_CANCELED_CONTENT = "处理中：{number}条";
  public final static String APPOINT_ORDER_CANCELED_RELATED_URL = "appoint.client?method=showAppointOrderList&scene=CLIENT_HANDLED_ORDER";
  public final static String APPOINT_ORDER_OVERDUE_AND_SOON_EXPIRE_CONTENT = "过期预约：{number}条";
  public final static String APPOINT_ORDER_OVERDUE_AND_SOON_EXPIRE_RELATED_URL = "appoint.client?method=showAppointOrderList&scene=CLIENT_OVERDUE_AND_SOON_ORDER";




  public static String getContactByPushMessageSourceType(PushMessageSourceType sourceType) {
    switch (sourceType) {
      case PURCHASE_SELLER_STOCK:
        //卖家备货
        return ORDER_PURCHASE_SELLER_STOCK_TIP;
      case PURCHASE_SELLER_DISPATCH:
        //已发货
        return ORDER_PURCHASE_SELLER_DISPATCH_TIP;
      case PURCHASE_SELLER_REFUSED:
        //拒绝
        return ORDER_PURCHASE_SELLER_REFUSED_TIP;
      case SALE_NEW:
        return ORDER_SALE_NEW_TIP;
      //新订单
      case SALE_RETURN_NEW:
        //新退货单
        return ORDER_SALE_RETURN_NEW_TIP;
      default:
        return null;
    }
  }

  public static String getUrlByPushMessageSourceType(PushMessageSourceType sourceType) {
    switch (sourceType) {
      case PURCHASE_SELLER_STOCK:
        //卖家备货
        return ORDER_PURCHASE_SELLER_STOCK_URL;
      case PURCHASE_SELLER_DISPATCH:
        //已发货
        return ORDER_PURCHASE_SELLER_DISPATCH_URL;
      case PURCHASE_SELLER_REFUSED:
        //拒绝
        return ORDER_PURCHASE_SELLER_REFUSED_URL;
      case SALE_NEW:
        return ORDER_SALE_NEW_URL;
      //新订单
      case SALE_RETURN_NEW:
        //新退货单
        return ORDER_SALE_RETURN_NEW_URL;
      default:
        return null;
    }
  }
}
