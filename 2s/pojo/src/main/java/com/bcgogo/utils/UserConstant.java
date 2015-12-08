package com.bcgogo.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: Lucien
 * Date: 12-4-6
 * Time: 上午11:30
 * To change this template use File | Settings | File Templates.
 */
public class UserConstant {
  //保养
  public final static Long MAINTAIN_TIME = 4L;
  //保险
  public final static Long INSURE_TIME = 0L;
  //验车
  public final static Long EXAMINE_TIME = 1L;
  //生日
  public final static Long BIRTH_TIME = 2L;
  //自定义
  public final static Long APPOINT_SERVICE = 5L;
  //会员服务到期
  public final static Long MEMBER_SERVICE = 6L;
  //保养（下次保养里程）
  public final static Long MAINTAIN_MILEAGE = 7L;

  public static final List<String> status = new ArrayList<String>();

  public static final Set<Long> VEHICLE_REMINDS = new HashSet<Long>();

  /**
   * 提醒中客户类型 全体和非全体
   */
  public class CustomerType {
    public final static String ALL_CUSTOMER = "all";
    public final static String NORM_CUSTOMER = "normal";
  }

  static {
    status.add(Status.ACTIVITY);
    status.add(Status.REMINDED);

    VEHICLE_REMINDS.add(MAINTAIN_TIME);
    VEHICLE_REMINDS.add(INSURE_TIME);
    VEHICLE_REMINDS.add(EXAMINE_TIME);
  }

  /**
   * remind的所有状态
   */
  public class Status {
    public final static String ACTIVITY = "activity";
    public final static String CANCELED = "canceled";
    public final static String REMINDED = "reminded";
  }

  /**
   * 客户服务提醒的类型
   */
  public class CustomerRemindType {
    public final static String MAINTAIN_TIME = "保养";
    public final static String INSURE_TIME = "保险";
    public final static String EXAMINE_TIME = "验车";
    public final static String BIRTH_TIME = "生日";
    public final static String MEMBER_SERVICE = "会员服务到期";
    public final static String APPOINT_SERVICE = "自定义预约服务";
  }

  public static String getCustomerRemindType(Long customerServiceRemindType){
    if (UserConstant.INSURE_TIME.equals(customerServiceRemindType)) {
      return UserConstant.CustomerRemindType.INSURE_TIME;      //保险
    } else if (UserConstant.EXAMINE_TIME.equals(customerServiceRemindType)) {
      return UserConstant.CustomerRemindType.EXAMINE_TIME;     //验车
    } else if (UserConstant.MAINTAIN_TIME.equals(customerServiceRemindType)) {
      return UserConstant.CustomerRemindType.MAINTAIN_TIME;    //保养
    } else if (UserConstant.MAINTAIN_MILEAGE.equals(customerServiceRemindType)) {
      return UserConstant.CustomerRemindType.MAINTAIN_TIME;    //保养（保养里程）
    } else if (UserConstant.BIRTH_TIME.equals(customerServiceRemindType)) {
      return UserConstant.CustomerRemindType.BIRTH_TIME;       //生日
    } else if (UserConstant.APPOINT_SERVICE.equals(customerServiceRemindType)) {
      return UserConstant.CustomerRemindType.APPOINT_SERVICE; //自定义预约服务
    } else {
      return null;
    }
  }
  /**
   * 客户记录查询，
   */
  public class SearchDate {
    public static final String TODAY = "today";               //当天
    public static final String YESTERDAY = "yesterday";      //昨天
    public static final String MONTH = "month";     //当月
  }

  /**
   * remind需要提醒状态
   *
   * @return
   */
  public static List<String> getStatus() {
    return status;
  }
  //customer or supplier type
  public class CSType{
    public static final String CUSTOMER="customer";
    public static final String SUPPLIER="supplier";
  }

}
