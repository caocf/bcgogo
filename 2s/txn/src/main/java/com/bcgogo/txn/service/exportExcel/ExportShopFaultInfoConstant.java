package com.bcgogo.txn.service.exportExcel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by XinyuQiu on 14-5-26.
 */
public class ExportShopFaultInfoConstant {
  public static final String DATE = "发生时间";
  public static final String FAULT_TYPE = "类型";
  public static final String VEHICLE_NO = "车牌号";
  public static final String FAULT_CODE_DESCRIPTION = "详细描述";
  public static final String VEHICLE = "车辆信息";
  public static final String APP_MOBILE = "客户端手机号";
  public static final String CUSTOMER_NAME = "客户名";
  public static final String CUSTOMER_MOBILE = "客户手机";
  public static final String STATUS = "状态";

  public static final String FILE_NAME = "事故故障提醒.xls";
  public static List<String> fieldList;

  static {
    fieldList = new ArrayList<String>();
    fieldList.add(DATE);
    fieldList.add(FAULT_TYPE);
    fieldList.add(VEHICLE_NO);
    fieldList.add(FAULT_CODE_DESCRIPTION);
    fieldList.add(VEHICLE);
    fieldList.add(APP_MOBILE);
    fieldList.add(CUSTOMER_NAME);
    fieldList.add(CUSTOMER_MOBILE);
    fieldList.add(STATUS);
  }
}
