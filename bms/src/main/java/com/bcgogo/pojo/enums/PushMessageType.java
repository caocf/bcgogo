package com.bcgogo.pojo.enums;


import java.util.Arrays;
import java.util.List;

public enum PushMessageType {
  //上架
  ACCESSORY,
  RECOMMEND_ACCESSORY_BY_QUOTED,
  //促销
  ACCESSORY_PROMOTIONS,
  //配件消息生成数量
  ACCESSORY_MATCH_RESULT,
  //配件匹配
  BUYING_MATCH_ACCESSORY,
  //求购咨询
  BUYING_INFORMATION,//就是  BUSINESS_CHANCE_NORMAL
  BUSINESS_CHANCE_LACK,
  BUSINESS_CHANCE_SELL_WELL,
  //求购咨询消息生成数量
  BUYING_INFORMATION_MATCH_RESULT,
  //报价
  QUOTED_BUYING_INFORMATION,
  //报价未被采纳
  QUOTED_BUYING_IGNORED,
  //客户匹配推荐
  MATCHING_RECOMMEND_CUSTOMER,
  //供应商匹配推荐
  MATCHING_RECOMMEND_SUPPLIER,


  //请求客户
  APPLY_CUSTOMER,
  //请求供应商
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

  //公告
  ANNOUNCEMENT,
  //节日
  FESTIVAL,

  //=================APP=============
  CUSTOM_MESSAGE_2_APP,
  //店铺接收的
  //APP预约取消消息
  APP_CANCEL_APPOINT,
  //APP预约申请消息
  APP_APPLY_APPOINT,
  //店铺过期预约单
  OVERDUE_APPOINT_TO_SHOP,
  //店铺快过期预约单
  SOON_EXPIRE_APPOINT_TO_SHOP,
  //系统默认接受
  SYS_ACCEPT_APPOINT,
  //app提交询价单
  APP_SUBMIT_ENQUIRY,


  //App接收的
  //店铺预约修改消息
  SHOP_CHANGE_APPOINT,
  //店铺预约结束消息
  SHOP_FINISH_APPOINT,
  //店铺接受预约单
  SHOP_ACCEPT_APPOINT,
  //店铺报价的消息
  SHOP_QUOTE_TO_APP,

  //店铺公告的消息
  SHOP_ADVERT_TO_APP,

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


  //店铺预约拒绝消息
  SHOP_REJECT_APPOINT,
  //店铺预约取消消息
  SHOP_CANCEL_APPOINT,
  //APP过期预约单
  OVERDUE_APPOINT_TO_APP,
  //APP快过期预约单
  SOON_EXPIRE_APPOINT_TO_APP,
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
   //故障消息
  VEHICLE_FAULT_2_APP,

      //对话消息 微信端-->客户端
  MSG_FROM_WX_USER,
  //对话消息 4s店铺-->客户端
  MSG_FROM_SHOP,
   //对话消息 客户端--> 微信端
  MSG_FROM_MIRROR_TO_WX_USER,

   //违章记录
  VIOLATE_REGULATION_RECORD_2_APP,

  PROMOTIONS_MESSAGE,//"促销"
  WARN_MESSAGE,//("提醒")

  CANCEL_ASSOCIATION_NOTICE,//"取消关联通知"
  ASSOCIATION_REJECT_NOTICE,//关联拒绝通知
  CUSTOMER_ACCEPT_TO_SUPPLIER,    //供应商申请添加客户店，客户接受后给供应商的通知              //客户id
  SUPPLIER_ACCEPT_TO_CUSTOMER,   //客户申请添加供应商，供应商接受后给客户的通知              //供应商id
  SUPPLIER_ACCEPT_TO_SUPPLIER,        //客户申请添加供应商，供应商接受后给供应商的通知   //客户id
  CUSTOMER_ACCEPT_TO_CUSTOMER    //供应商申请添加客户店，客户接受后给客户的通知            //供应商id
  ;

  public static PushMessageType[] getAppUserPushMessage() {
    return new PushMessageType[]{SHOP_CHANGE_APPOINT, SHOP_FINISH_APPOINT, SHOP_ACCEPT_APPOINT,CUSTOM_MESSAGE_2_APP,
        SHOP_REJECT_APPOINT, SHOP_CANCEL_APPOINT, OVERDUE_APPOINT_TO_APP, SOON_EXPIRE_APPOINT_TO_APP, APP_VEHICLE_MAINTAIN_MILEAGE,
        APP_VEHICLE_MAINTAIN_TIME, APP_VEHICLE_INSURANCE_TIME, APP_VEHICLE_EXAMINE_TIME,SHOP_QUOTE_TO_APP,VEHICLE_FAULT_2_APP,SHOP_ADVERT_TO_APP,
        VIOLATE_REGULATION_RECORD_2_APP
    };
  }

  public static ActionType lookupActionType(PushMessageType type, boolean isCommented) {
    switch (type) {
      case SHOP_FINISH_APPOINT:
        if (isCommented) return null;
        return ActionType.COMMENT_SHOP;
      case APP_VEHICLE_MAINTAIN_MILEAGE:
      case APP_VEHICLE_MAINTAIN_TIME:
      case APP_VEHICLE_INSURANCE_TIME:
      case APP_VEHICLE_EXAMINE_TIME:
        return ActionType.SEARCH_SHOP;
      case SHOP_CANCEL_APPOINT:
      case OVERDUE_APPOINT_TO_APP:
      case SOON_EXPIRE_APPOINT_TO_APP:
      case SHOP_ACCEPT_APPOINT:
      case SYS_ACCEPT_APPOINT:
        return ActionType.ORDER_DETAIL;
      case SHOP_QUOTE_TO_APP:
        return ActionType.ENQUIRY_ORDER;
      case SHOP_ADVERT_TO_APP:
              return ActionType.ADVERT_DETAIL;
      case VIOLATE_REGULATION_RECORD_2_APP:
        return ActionType.VIOLATE_REGULATION_QUERY;
      default:
        return null;
    }
  }

  public static PushMessageType[] getPushMessageTypesByScheduleCreated() {
    return new PushMessageType[]{ACCESSORY,
        //促销
        ACCESSORY_PROMOTIONS,
        //配件消息生成数量
        ACCESSORY_MATCH_RESULT,
        //配件匹配
        BUYING_MATCH_ACCESSORY,
        //求购咨询
        BUYING_INFORMATION,
        BUSINESS_CHANCE_SELL_WELL,
        BUSINESS_CHANCE_LACK,
        //求购咨询消息生成数量
        BUYING_INFORMATION_MATCH_RESULT,
        //客户匹配推荐
        MATCHING_RECOMMEND_CUSTOMER,
        //供应商匹配推荐
        MATCHING_RECOMMEND_SUPPLIER};
  }

  public static List<PushMessageType> getHaveRedirectShopUrlPushMessageTypes(){
    return Arrays.asList(new PushMessageType[]{
    APPLY_CUSTOMER,
    APPLY_SUPPLIER,
    SUPPLIER_ACCEPT_TO_SUPPLIER,
    SUPPLIER_ACCEPT_TO_CUSTOMER,
    CUSTOMER_ACCEPT_TO_SUPPLIER,
    CUSTOMER_ACCEPT_TO_CUSTOMER,
    CANCEL_ASSOCIATION_NOTICE,
    ASSOCIATION_REJECT_NOTICE,
    QUOTED_BUYING_IGNORED,
    QUOTED_BUYING_INFORMATION,
    ACCESSORY,
    ACCESSORY_PROMOTIONS,
    RECOMMEND_ACCESSORY_BY_QUOTED,
    BUYING_MATCH_ACCESSORY,
    BUYING_INFORMATION,
    MATCHING_RECOMMEND_CUSTOMER,
    MATCHING_RECOMMEND_SUPPLIER,
    BUSINESS_CHANCE_SELL_WELL,
    BUSINESS_CHANCE_LACK
    });
  }

  public static List<PushMessageType> getHaveRedirectShopProductDetailUrlPushMessageTypes(){
    return Arrays.asList(new PushMessageType[]{
        QUOTED_BUYING_INFORMATION,
        ACCESSORY,
        ACCESSORY_PROMOTIONS,
        RECOMMEND_ACCESSORY_BY_QUOTED,
        ACCESSORY_MATCH_RESULT,
        BUYING_MATCH_ACCESSORY
    });
  }
  public static List<PushMessageType> getHaveRedirectBuyingInformationDetailUrlPushMessageTypes(){
    return Arrays.asList(new PushMessageType[]{
        BUYING_INFORMATION,BUSINESS_CHANCE_SELL_WELL,BUSINESS_CHANCE_LACK
    });
  }
  public static List<PushMessageType> getHaveRedirectPreBuyOrderUrlPushMessageTypes(){
    return Arrays.asList(new PushMessageType[]{
        BUYING_INFORMATION_MATCH_RESULT
    });
  }
  public static List<PushMessageType> getHaveRedirectCustomerUrlPushMessageTypes(){
    return Arrays.asList(new PushMessageType[]{
        MATCHING_RECOMMEND_CUSTOMER
    });
  }
  public static List<PushMessageType> getHaveRedirectSupplierUrlPushMessageTypes(){
    return Arrays.asList(new PushMessageType[]{
        MATCHING_RECOMMEND_SUPPLIER
    });
  }
  public static List<PushMessageType> getSendSmsPushMessageTypes(){
    return Arrays.asList(
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
        APP_VEHICLE_OVERDUE_EXAMINE_TIME_2_SHOP);
  }
}
