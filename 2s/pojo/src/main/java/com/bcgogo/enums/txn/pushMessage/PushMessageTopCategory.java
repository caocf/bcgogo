package com.bcgogo.enums.txn.pushMessage;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: xzhu
 * Date: 13-6-5
 * Time: 下午5:02
 * To change this template use File | Settings | File Templates.
 */
public enum PushMessageTopCategory {
  ApplyMessage("请求消息"),
  NoticeMessage("通知消息"),
  StationMessage("站内消息"),
  ;

  private final String name;

  private PushMessageTopCategory(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }


  public static Map<PushMessageTopCategory, List<PushMessageCategory>> pushMessageCategoryMap = new HashMap<PushMessageTopCategory, List<PushMessageCategory>>();
  static{
    pushMessageCategoryMap.put(NoticeMessage,getNoticeMessageCategorys());
    pushMessageCategoryMap.put(ApplyMessage,getInvitedMessageCategorys());
    pushMessageCategoryMap.put(StationMessage,getStationMessageCategorys());
  }

  //通知
  private static List<PushMessageCategory> getNoticeMessageCategorys() {
    return Arrays.asList(new PushMessageCategory[]{
        PushMessageCategory.RelatedNoticeMessage,
        PushMessageCategory.AppointNoticeMessage,
        PushMessageCategory.QuotedBuyingIgnoredNoticeMessage,
        PushMessageCategory.ProductPushStatNoticeMessage,
        PushMessageCategory.BuyingPushStatNoticeMessage,
        PushMessageCategory.SystemNoticeMessage
    });
  }
  //请求
  private static List<PushMessageCategory> getInvitedMessageCategorys() {
    return Arrays.asList(new PushMessageCategory[]{
        PushMessageCategory.RelatedApplyMessage
    });
  }
  //站内
  private static List<PushMessageCategory> getStationMessageCategorys() {
    return Arrays.asList(new PushMessageCategory[]{
        PushMessageCategory.QuotedBuyingInformationStationMessage,
        PushMessageCategory.RecommendProductStationMessage,
        PushMessageCategory.MatchStationMessage,
        PushMessageCategory.BuyingInformationStationMessage
    });
  }

  public static PushMessageTopCategory valueOfPushMessageCategory(PushMessageCategory pushMessageCategory){
    if(pushMessageCategory==null) return null;
    for(Map.Entry<PushMessageTopCategory,List<PushMessageCategory>> entry : pushMessageCategoryMap.entrySet()){
      if(entry.getValue().contains(pushMessageCategory)){
        return entry.getKey();
      }
    }
    return null;
  }

}