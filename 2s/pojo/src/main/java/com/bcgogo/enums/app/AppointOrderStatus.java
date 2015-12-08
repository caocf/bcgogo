package com.bcgogo.enums.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 预约单状态
 * Created with IntelliJ IDEA.
 * User: lw
 * Date: 13-9-2
 * Time: 上午10:20
 * To change this template use File | Settings | File Templates.
 */
public enum AppointOrderStatus {
  PENDING("待确认"),                  //通过手机端预约的 是待处理
  ACCEPTED("已接受"),                //店铺接受手机端预约  或者店铺直接录入预约单
  REFUSED("已拒绝"),                 //店面拒绝手机端的预约
  CANCELED("已取消"),                 //店铺取消预约单
  TO_DO_REPAIR("待施工"),
  HANDLED("已施工");                 //预约单已经生成施工单或者洗车美容单

  private static Map<String, AppointOrderStatus> enumValueMap = new HashMap<String, AppointOrderStatus>();
  private static Map<String, AppointOrderStatus> enumNameMap = new HashMap<String, AppointOrderStatus>();
  static{
    for(AppointOrderStatus orderStatus : AppointOrderStatus.values()){
      enumValueMap.put(orderStatus.getName(), orderStatus);
      enumNameMap.put(orderStatus.name(), orderStatus);
    }
  }
  /**
   * pre-status for modify order
   * @return
   */
  public static List<AppointOrderStatus> getModifyPreStatus() {
    List<AppointOrderStatus> statusList = new ArrayList<AppointOrderStatus>();
    statusList.add(ACCEPTED);
    return statusList;
  }

  public static AppointOrderStatus parseName(String orderStatus) {
    return enumValueMap.get(orderStatus);
  }

  public static AppointOrderStatus parseEnum(String orderStatus) {
    return enumNameMap.get(orderStatus);
  }

  /**
   * pre-status for cancel order
   * @return
   */
  public static List<AppointOrderStatus> getCancelPreStatus() {
    List<AppointOrderStatus> statusList = new ArrayList<AppointOrderStatus>();
    statusList.add(PENDING);
    statusList.add(ACCEPTED);
    return statusList;
  }

  private final String name;

  private AppointOrderStatus(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
}
