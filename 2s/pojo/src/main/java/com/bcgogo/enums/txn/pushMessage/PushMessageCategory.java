package com.bcgogo.enums.txn.pushMessage;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: xzhu
 * Date: 13-6-5
 * Time: 下午5:02
 * To change this template use File | Settings | File Templates.
 */
public enum PushMessageCategory {
  RelatedApplyMessage("关联请求"),

  RelatedNoticeMessage("关联申请处理"),
  BuyingPushStatNoticeMessage("求购推送统计"),
  ProductPushStatNoticeMessage("商品推送统计"),
  QuotedBuyingIgnoredNoticeMessage("报价未采纳提示"),
  AppointNoticeMessage("预约提醒"),
  SystemNoticeMessage("系统通知"),


  QuotedBuyingInformationStationMessage("求购报价提醒"),
  RecommendProductStationMessage("推荐商品消息"),
  MatchStationMessage("匹配关联消息"),
  BuyingInformationStationMessage("客户商机消息")



  ;

  private final String name;

  private PushMessageCategory(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }


  public static Map<PushMessageCategory, List<PushMessageType>> pushMessageCategoryTypeMap = new HashMap<PushMessageCategory, List<PushMessageType>>();
  static{
    pushMessageCategoryTypeMap.put(RelatedApplyMessage,getRelatedApplyMessageTypes());

    pushMessageCategoryTypeMap.put(RelatedNoticeMessage,getRelatedNoticeMessageTypes());
    pushMessageCategoryTypeMap.put(AppointNoticeMessage,getAppointNoticeMessageTypes());
    pushMessageCategoryTypeMap.put(QuotedBuyingIgnoredNoticeMessage,Arrays.asList(PushMessageType.QUOTED_BUYING_IGNORED));
    pushMessageCategoryTypeMap.put(ProductPushStatNoticeMessage,Arrays.asList(PushMessageType.ACCESSORY_MATCH_RESULT));
    pushMessageCategoryTypeMap.put(BuyingPushStatNoticeMessage,Arrays.asList(PushMessageType.BUYING_INFORMATION_MATCH_RESULT));
    pushMessageCategoryTypeMap.put(SystemNoticeMessage,getSystemNoticeMessageTypes());


    pushMessageCategoryTypeMap.put(QuotedBuyingInformationStationMessage,Arrays.asList(PushMessageType.QUOTED_BUYING_INFORMATION));
    pushMessageCategoryTypeMap.put(RecommendProductStationMessage,getRecommendProductStationMessageTypes());
    pushMessageCategoryTypeMap.put(MatchStationMessage,getMatchStationMessageTypes());
    pushMessageCategoryTypeMap.put(BuyingInformationStationMessage,Arrays.asList(
        PushMessageType.BUYING_INFORMATION,
        PushMessageType.BUSINESS_CHANCE_LACK,
        PushMessageType.BUSINESS_CHANCE_SELL_WELL,
        PushMessageType.VEHICLE_FAULT_2_SHOP,
        //保养里程
        PushMessageType.APP_VEHICLE_SOON_EXPIRE_MAINTAIN_MILEAGE_2_SHOP,
        //保养时间
        PushMessageType.APP_VEHICLE_SOON_EXPIRE_MAINTAIN_TIME_2_SHOP,
        //保险时间
        PushMessageType.APP_VEHICLE_SOON_EXPIRE_INSURANCE_TIME_2_SHOP,
        //验车时间
        PushMessageType.APP_VEHICLE_SOON_EXPIRE_EXAMINE_TIME_2_SHOP,
        //保养里程
        PushMessageType.APP_VEHICLE_OVERDUE_MAINTAIN_MILEAGE_2_SHOP,
        //保养时间
        PushMessageType.APP_VEHICLE_OVERDUE_MAINTAIN_TIME_2_SHOP,
        //保险时间
        PushMessageType.APP_VEHICLE_OVERDUE_INSURANCE_TIME_2_SHOP,
        //验车时间
        PushMessageType.APP_VEHICLE_OVERDUE_EXAMINE_TIME_2_SHOP
        ));
  }
  //关联请求
  private static List<PushMessageType> getRelatedApplyMessageTypes() {
    return Arrays.asList(new PushMessageType[]{
        PushMessageType.APPLY_CUSTOMER,
        PushMessageType.APPLY_SUPPLIER
    });
  }


  //系统通知
  private static List<PushMessageType> getSystemNoticeMessageTypes() {
    return Arrays.asList(new PushMessageType[]{
        PushMessageType.ANNOUNCEMENT,
        PushMessageType.FESTIVAL
    });
  }
  //系统通知
  private static List<PushMessageType> getRelatedNoticeMessageTypes() {
    return Arrays.asList(new PushMessageType[]{
        PushMessageType.CANCEL_ASSOCIATION_NOTICE,//"取消关联通知"
        PushMessageType.ASSOCIATION_REJECT_NOTICE,//关联拒绝通知
        PushMessageType.CUSTOMER_ACCEPT_TO_SUPPLIER,    //供应商申请添加客户店，客户接受后给供应商的通知              //客户id
        PushMessageType.SUPPLIER_ACCEPT_TO_CUSTOMER,   //客户申请添加供应商，供应商接受后给客户的通知              //供应商id
        PushMessageType.SUPPLIER_ACCEPT_TO_SUPPLIER,        //客户申请添加供应商，供应商接受后给供应商的通知   //客户id
        PushMessageType.CUSTOMER_ACCEPT_TO_CUSTOMER    //供应商申请添加客户店，客户接受后给客户的通知            //供应商id
    });
  }
  //预约提醒
  private static List<PushMessageType> getAppointNoticeMessageTypes() {
    return Arrays.asList(new PushMessageType[]{
        PushMessageType.APP_CANCEL_APPOINT,
        PushMessageType.APP_APPLY_APPOINT,
        PushMessageType.OVERDUE_APPOINT_TO_SHOP,
        PushMessageType.SOON_EXPIRE_APPOINT_TO_SHOP,
        PushMessageType.SYS_ACCEPT_APPOINT
    });
  }

  //推荐商品消息
  private static List<PushMessageType> getRecommendProductStationMessageTypes() {
    return Arrays.asList(new PushMessageType[]{
        PushMessageType.BUYING_MATCH_ACCESSORY,
        PushMessageType.ACCESSORY,
        PushMessageType.ACCESSORY_PROMOTIONS,
        PushMessageType.RECOMMEND_ACCESSORY_BY_QUOTED,
        PushMessageType.PROMOTIONS_MESSAGE,
        PushMessageType.WARN_MESSAGE,
    });
  }
  //匹配关联消息
  private static List<PushMessageType> getMatchStationMessageTypes() {
    return Arrays.asList(new PushMessageType[]{
        PushMessageType.MATCHING_RECOMMEND_CUSTOMER,
        PushMessageType.MATCHING_RECOMMEND_SUPPLIER
    });
  }


  public static PushMessageCategory valueOfPushMessageType(PushMessageType pushMessageType){
    if(pushMessageType==null) return null;
    for(Map.Entry<PushMessageCategory,List<PushMessageType>> entry : pushMessageCategoryTypeMap.entrySet()){
      if(entry.getValue().contains(pushMessageType)){
        return entry.getKey();
      }
    }
    return null;
  }

  public static List<PushMessageType> getAllPushMessageTypeListInCategory(){
    List<PushMessageType> pushMessageTypeList = new ArrayList<PushMessageType>();
    for(Map.Entry<PushMessageCategory,List<PushMessageType>> entry:pushMessageCategoryTypeMap.entrySet()){
      pushMessageTypeList.addAll(entry.getValue());
    }
    return pushMessageTypeList;
  }
}