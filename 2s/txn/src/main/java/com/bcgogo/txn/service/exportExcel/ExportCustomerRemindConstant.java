package com.bcgogo.txn.service.exportExcel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by XinyuQiu on 14-5-25.
 */
public class ExportCustomerRemindConstant {
  public static final String REMIND_ITEM = "提醒项目";
  public static final String VEHICLE_NO = "车牌号";
  public static final String VEHICLE_CONTACT = "车主名称";
  public static final String VEHICLE_CONTACT_MOBILE = "车主手机";
  public static final String CUSTOMER_NAME = "所属客户名";
  public static final String CUSTOMER_MOBILE = "客户手机";
  public static final String REMIND_INFO_DATE = "下次提醒时间";
  public static final String REMIND_INFO_MIL = "下次保养里程";
  public static final String CURRENT_MIL = "当前里程";
  public static final String CLOSE_MAINTENANCE = "距保养里程";
  public static final String STATUS = "状态";

  public static final String FILE_NAME = "客户服务提醒.xls";
  public static List<String> fieldList;

  static {
    fieldList = new ArrayList<String>();
    fieldList.add(REMIND_ITEM);
    fieldList.add(VEHICLE_NO);
    fieldList.add(VEHICLE_CONTACT);
    fieldList.add(VEHICLE_CONTACT_MOBILE);
    fieldList.add(CUSTOMER_NAME);
    fieldList.add(CUSTOMER_MOBILE);
    fieldList.add(REMIND_INFO_DATE);
    fieldList.add(REMIND_INFO_MIL);
    fieldList.add(CURRENT_MIL);
    fieldList.add(CLOSE_MAINTENANCE);
    fieldList.add(STATUS);
  }
}
