package com.bcgogo.txn.service.exportExcel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by XinyuQiu on 14-5-26.
 */
public class ExportVehicleListConstant {
  public static final String VEHICLE_NO = "车牌号";
  public static final String VEHICLE_CONTACT_NAME = "车主";
  public static final String VEHICLE_MOBILE = "车主手机";
  public static final String CUSTOMER_NAME = "客户名";
  public static final String CUSTOMER_MOBILE = "客户手机";
  public static final String VEHICLE_INFO = "车辆信息";
  public static final String TOTAL_COST_TIME = "累计消费次数";
  public static final String TOTAL_COST_MONEY = "累计消费金额";
  public static final String LAST_COST_DATE = "最后消费日期";
  public static final String CURRENT_MIL = "当前里程";
  public static final String NEXT_MAINTAIN_MIL = "下次保养里程";
  public static final String NEXT_MAINTAIN_DATE = "下次保养时间";
  public static final String CLOSE_NEXT_MAINTAIN_MIL = "距下次保养里程";
  public static final String CLOSE_NEXT_MAINTAIN_DATE = "距下次保养时间";
  public static final String CLOSE_NEXT_INSURE = "下次保险时间";

  public static final String FILE_NAME = "车辆列表导出.xls";
  public static List<String> fieldList;

  static {
    fieldList = new ArrayList<String>();
    fieldList.add(VEHICLE_NO);
    fieldList.add(VEHICLE_CONTACT_NAME);
    fieldList.add(VEHICLE_MOBILE);
    fieldList.add(CUSTOMER_NAME);
    fieldList.add(CUSTOMER_MOBILE);
    fieldList.add(VEHICLE_INFO);
    fieldList.add(TOTAL_COST_TIME);
    fieldList.add(TOTAL_COST_MONEY);
    fieldList.add(LAST_COST_DATE);
    fieldList.add(CURRENT_MIL);
    fieldList.add(NEXT_MAINTAIN_MIL);
    fieldList.add(NEXT_MAINTAIN_DATE);
    fieldList.add(CLOSE_NEXT_MAINTAIN_MIL);
    fieldList.add(CLOSE_NEXT_MAINTAIN_DATE);
    fieldList.add(CLOSE_NEXT_INSURE);
  }
}
