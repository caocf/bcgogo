package com.bcgogo.user.service.excelimport.obd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by XinyuQiu on 14-6-30.
 */
public class ObdImportConstants {

  /**
   * 字段名字
   */
  public class FieldName {

    public static final String IMEI = "imei";
    public static final String IMEI_DESC = "IMEI号";
    public static final String OBD_VERSION = "obdVersion";
    public static final String OBD_VERSION_DESC = "软件版本";
    public static final String SPEC = "spec";
    public static final String SPEC_DESC = "规格";
    public static final String COLOR = "color";
    public static final String COLOR_DESC = "颜色";
    public static final String PACK = "pack";
    public static final String PACK_DESC = "包装";
    public static final String CRASH = "crash";
    public static final String CRASH_DESC = "碰撞报警";
    public static final String SHAKE = "shake";
    public static final String SHAKE_DESC = "震动报警";
    public static final String SIM_NO = "simNo";
    public static final String SIM_NO_DESC = "SIM卡编号";
    public static final String MOBILE = "mobile";
    public static final String MOBILE_DESC = "手机号码";
    public static final String USE_DATE = "useDate";
    public static final String USE_DATE_DESC = "开通年月";
    public static final String USE_PERIOD = "usePeriod";
    public static final String USE_PERIOD_DESC = "服务期";
    public static final String GSM_MIRROR = "type";   //新增ODB和后视镜的区别
    public static final String GSM_MIRROR_DESC = "类别";


  }
  public static List<String> headFieldList;
  public static Map<String, String> filedMap ;

  static {
    headFieldList = new ArrayList<String>();
//    headFieldList.add(FieldName.IMEI + "_" + FieldName.IMEI_DESC);
//    headFieldList.add(FieldName.OBD_VERSION + "_" + FieldName.OBD_VERSION_DESC);
//    headFieldList.add(FieldName.SPEC + "_" + FieldName.SPEC_DESC);
//    headFieldList.add(FieldName.COLOR + "_" + FieldName.COLOR_DESC);
//    headFieldList.add(FieldName.PACK + "_" + FieldName.PACK_DESC);
//    headFieldList.add(FieldName.CRASH + "_" + FieldName.CRASH_DESC);
//    headFieldList.add(FieldName.SHAKE + "_" + FieldName.SHAKE_DESC);
//    headFieldList.add(FieldName.SIM_NO + "_" + FieldName.SIM_NO_DESC);
//    headFieldList.add(FieldName.MOBILE + "_" + FieldName.MOBILE_DESC);
//    headFieldList.add(FieldName.USE_DATE + "_" + FieldName.USE_DATE_DESC);
//    headFieldList.add(FieldName.USE_PERIOD + "_" + FieldName.USE_PERIOD_DESC);
    headFieldList.add(FieldName.IMEI_DESC);
    headFieldList.add(FieldName.OBD_VERSION_DESC);
    headFieldList.add(FieldName.SPEC_DESC);
    headFieldList.add(FieldName.COLOR_DESC);
    headFieldList.add(FieldName.PACK_DESC);
    headFieldList.add(FieldName.CRASH_DESC);
    headFieldList.add(FieldName.SHAKE_DESC);
    headFieldList.add(FieldName.SIM_NO_DESC);
    headFieldList.add(FieldName.MOBILE_DESC);
    headFieldList.add(FieldName.USE_DATE_DESC);
    headFieldList.add(FieldName.USE_PERIOD_DESC);
    headFieldList.add(FieldName.GSM_MIRROR_DESC);

    filedMap = new HashMap<String, String>();
    filedMap.put(FieldName.IMEI,FieldName.IMEI_DESC);
    filedMap.put(FieldName.OBD_VERSION,FieldName.OBD_VERSION_DESC);
    filedMap.put(FieldName.SPEC,FieldName.SPEC_DESC);
    filedMap.put(FieldName.COLOR,FieldName.COLOR_DESC);
    filedMap.put(FieldName.PACK,FieldName.PACK_DESC);
    filedMap.put(FieldName.CRASH,FieldName.CRASH_DESC);
    filedMap.put(FieldName.SHAKE,FieldName.SHAKE_DESC);
    filedMap.put(FieldName.SIM_NO,FieldName.SIM_NO_DESC);
    filedMap.put(FieldName.MOBILE,FieldName.MOBILE_DESC);
    filedMap.put(FieldName.USE_DATE,FieldName.USE_DATE_DESC);
    filedMap.put(FieldName.USE_PERIOD,FieldName.USE_PERIOD_DESC);
    filedMap.put(FieldName.GSM_MIRROR,FieldName.GSM_MIRROR_DESC);
  }

  public class CheckResultMessage {

    public static final String IMEI_EMPTY = "导入数据存在OBD信息，但是IMei号为空！";
    public static final String IMEI_LENGTH_ILLEGAL = "IMei长度不对！";
    public static final String IMEI_EXIST_IN_TABLE = "IMEI号在系统中已经存在！";
    public static final String IMEI_NUMBER_ILLEGAL = "IMEI号字符类型不对,应该为15位数字！";


    public static final String OBD_VERSION_EMPTY = "导入数据存在OBD信息，但是软件版本为空！";
    public static final String OBD_VERSION_LENGTH_ILLEGAL = "软件版本长度不对，最大为30位！";

    public static final String COLOR_EMPTY = "导入数据存在OBD信息，但是颜色为空！";
    public static final String COLOR_LENGTH_ILLEGAL = "颜色长度不对，最大为30位！";

    public static final String SPEC_EMPTY = "导入数据存在OBD信息，但是规格为空！";
    public static final String SPEC_LENGTH_ILLEGAL = "规格长度不对，最大为30位！";

    public static final String PACK_EMPTY = "导入数据存在OBD信息，但是包装为空！";
    public static final String PACK_LENGTH_ILLEGAL = "包装长度不对，最大为30位！";

    public static final String CRASH_EMPTY = "导入数据存在OBD信息，但是碰撞报警为空！";
    public static final String CRASH_ILLEGAL = "碰撞报警内容不对，只能为YES或NO！";

    public static final String SHAKE_EMPTY = "导入数据存在OBD信息，但是震动报警为空！";
    public static final String SHAKE_ILLEGAL = "震动报警内容不对，只能为YES或NO！";

    public static final String SIM_NO_EMPTY = "导入数据存在SIM卡信息，但是SIM编号为空！";
    public static final String SIM_NO_LENGTH_ILLEGAL = "SIM编号长度不对，应该为20位数字！";
    public static final String SIM_NO_EXIST_IN_TABLE = "SIM编号在系统中已经存在！";
    public static final String SIM_NO_NUMBER_ILLEGAL = "SIM编号字符类型不对,应该为20位数字！";

    public static final String MOBILE_EMPTY = "导入数据存在SIM卡信息，但是手机号为空！";
    public static final String MOBILE_ILLEGAL = "手机号格式不正确！";
    public static final String MOBILE_EXIST_IN_TABLE = "手机号在系统中已经存在！";

    public static final String USE_DATA_EMPTY = "导入数据存在SIM卡信息，但是开通年月为空！";
    public static final String USE_DATA_ILLEGAL = "开通年月格式不正确，应该为（2014-06）的格式！";
    public static final String USE_PERIOD_ILLEGAL = "服务期格式不正确，应该为（1-5）的格式！";

  }

  public class FieldLength {
    public static final int FIELD_LENGTH_IMEI = 5;
    public static final int FIELD_LENGTH_OBD_VERSION = 30;
    public static final int FIELD_LENGTH_COLOR = 30;
    public static final int FIELD_LENGTH_SPEC = 30;
    public static final int FIELD_LENGTH_PACK = 30;
    public static final int FIELD_LENGTH_SIM_NO = 20;


    public static final int FIELD_LENGTH_NAME = 100;
    public static final int FIELD_LENGTH_ABBR = 100;
    public static final int FIELD_LENGTH_CITY = 20;
    public static final int FIELD_LENGTH_ADDRESS = 50;
    public static final int FIELD_LENGTH_CONTACT = 20;
    public static final int FIELD_LENGTH_MOBILE = 100;
    public static final int FIELD_LENGTH_LANDLINE = 100;
    public static final int FIELD_LENGTH_FAX = 20;
    public static final int FIELD_LENGTH_QQ = 20;
    public static final int FIELD_LENGTH_EMAIL = 50;
    public static final int FIELD_LENGTH_BANK = 20;
    public static final int FIELD_LENGTH_BANK_ACCOUNT_NAME = 20;
    public static final int FIELD_LENGTH_ACCOUNT = 20;
    public static final int FIELD_LENGTH_INVOICE_CATEGORY = 20;
    public static final int FIELD_LENGTH_BUSINESS_SCOPE = 500;

  }
}
