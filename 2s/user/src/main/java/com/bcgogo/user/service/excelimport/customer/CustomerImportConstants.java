package com.bcgogo.user.service.excelimport.customer;

import org.omg.CORBA.PUBLIC_MEMBER;

import java.util.ArrayList;
import java.util.List;

/**
 * 导入客户使用常量
 * Created by IntelliJ IDEA.
 * User: ZouJianhong
 * Date: 12-4-6
 * Time: 上午11:34
 * To change this template use File | Settings | File Templates.
 */
public class CustomerImportConstants {

  /**
   * 字段名字
   */
  public class FieldName {
    public static final String NAME = "name";
    public static final String NAME_DESC = "客户名";
    public static final String SHORT_NAME = "shortName";
    public static final String SHORT_NAME_DESC = "简称";
    public static final String CITY = "city";
    public static final String CITY_DESC = "所在市";
    public static final String ADDRESS = "address";
    public static final String ADDRESS_DESC = "地址";
    public static final String CONTACT = "contact";
    public static final String CONTACT_DESC = "联系人";
    public static final String MOBILE = "mobile";
    public static final String MOBILE_DESC = "手机";
    public static final String LANDLINE = "landline";
    public static final String LANDLINE_DESC = "座机";
    public static final String FAX = "fax";
    public static final String FAX_DESC = "传真";
    public static final String AREA = "area";
    public static final String AREA_DESC = "区域";
    public static final String BIRTHDAY = "birthday";
    public static final String BIRTHDAY_DESC = "生日";
    public static final String QQ = "qq";
    public static final String QQ_DESC = "QQ";
    public static final String EMAIL = "email";
    public static final String EMAIL_DESC = "Email";
    public static final String BANK = "bank";
    public static final String BANK_DESC = "开户行";
    public static final String BANK_ACCOUNT_NAME = "bankAccountName";
    public static final String BANK_ACCOUNT_NAME_DESC = "开户名";
    public static final String ACCOUNT = "account";
    public static final String ACCOUNT_DESC = "账号";
    public static final String INVOICE_CATEGORY = "invoiceCategory";
    public static final String INVOICE_CATEGORY_DESC = "发票类型";
    public static final String SETTLEMENT_TYPE = "settlementType";
    public static final String SETTLEMENT_TYPE_DESC = "结算方式";
    public static final String CUSTOMER_KIND = "customerKind";
    public static final String CUSTOMER_KIND_DESC = "客户类别";

    public static final String VEHICLE_LICENCE_NO = "licenceNo";
    public static final String VEHICLE_LICENCE_NO_DESC = "车牌号";
    public static final String VEHICLE_BRAND = "brand";
    public static final String VEHICLE_BRAND_DESC = "车辆品牌";
    public static final String VEHICLE_MODEL = "model";
    public static final String VEHICLE_MODEL_DESC = "车辆型号";
    public static final String VEHICLE_YEAR = "year";
    public static final String VEHICLE_YEAR_DESC = "车辆年代";
    public static final String VEHICLE_ENGINE = "engine";
    public static final String VEHICLE_ENGINE_DESC = "车辆排量";
    public static final String VEHICLE_ENGINE_NO = "engineNo";
    public static final String VEHICLE_ENGINE_NO_DESC = "发动机号";
    public static final String VEHICLE_CHASSIS_NUMBER = "chassisNumber";
    public static final String VEHICLE_CHASSIS_NUMBER_DESC = "车架号";

    public static final String MEMBER_NO = "memberNo";
    public static final String MEMBER_NO_DESC = "会员号";
    public static final String TYPE = "type";
    public static final String TYPE_DESC = "会员类型";
    public static final String BALANCE = "balance";
    public static final String BALANCE_DESC = "储值金额";
    public static final String ACCUMULATE_POINTS = "accumulatePoints";
    public static final String ACCUMULATE_POINTS_DESC = "积分";
//    public static final String SERVICE_DISCOUNT = "serviceDiscount";
//    public static final String SERVICE_DISCOUNT_DESC = "服务折扣";
//    public static final String MATERIAL_DISCOUNT = "materialDiscount";
//    public static final String MATERIAL_DISCOUNT_DESC = "材料折扣";
    public static final String JOIN_DATE = "joinDate";
    public static final String JOIN_DATE_DESC = "办卡日期";
    public static final String DEADLINE = "deadline";
    public static final String DEADLINE_DESC = "失效日期";
    public static final String MEMBER_DISCOUNT = "memberDiscount";
    public static final String MEMBER_DISCOUNT_DESC = "会员折扣";
  }

  public static final String[] WHOLESALER_EXCLUDE_FIELDS = {
      FieldName.MEMBER_DISCOUNT_DESC,
      FieldName.MEMBER_NO_DESC,
      FieldName.TYPE_DESC,
      FieldName.BALANCE_DESC,
      FieldName.ACCUMULATE_POINTS_DESC,
      FieldName.JOIN_DATE_DESC,
      FieldName.DEADLINE_DESC,
      FieldName.MEMBER_DISCOUNT_DESC,
      FieldName.VEHICLE_LICENCE_NO_DESC,
      FieldName.VEHICLE_BRAND_DESC,
      FieldName.VEHICLE_MODEL_DESC,
      FieldName.VEHICLE_YEAR_DESC,
      FieldName.VEHICLE_ENGINE_DESC,
      FieldName.VEHICLE_ENGINE_NO_DESC,
      FieldName.VEHICLE_CHASSIS_NUMBER_DESC
  };

  public static final String[]  REPAIR_CUSTOMER_EXCLUDE_FIELDS = {
      FieldName.CITY_DESC
  };

  public static final String PROVINCE_AND_CITY_NO = "provinceAndCityNo";

  public static List<String> fieldList;

  static {
    fieldList = new ArrayList<String>();
    fieldList.add(FieldName.NAME + "_" + FieldName.NAME_DESC);
    fieldList.add(FieldName.SHORT_NAME + "_" + FieldName.SHORT_NAME_DESC);
    fieldList.add(FieldName.CITY + "_" + FieldName.CITY_DESC);
    fieldList.add(FieldName.ADDRESS + "_" + FieldName.ADDRESS_DESC);
    fieldList.add(FieldName.CONTACT + "_" + FieldName.CONTACT_DESC);
    fieldList.add(FieldName.MOBILE + "_" + FieldName.MOBILE_DESC);
    fieldList.add(FieldName.LANDLINE + "_" + FieldName.LANDLINE_DESC);
    fieldList.add(FieldName.FAX + "_" + FieldName.FAX_DESC);
    fieldList.add(FieldName.AREA + "_" + FieldName.AREA_DESC);
    fieldList.add(FieldName.BIRTHDAY + "_" + FieldName.BIRTHDAY_DESC);
    fieldList.add(FieldName.QQ + "_" + FieldName.QQ_DESC);
    fieldList.add(FieldName.EMAIL + "_" + FieldName.EMAIL_DESC);
    fieldList.add(FieldName.BANK + "_" + FieldName.BANK_DESC);
    fieldList.add(FieldName.BANK_ACCOUNT_NAME + "_" + FieldName.BANK_ACCOUNT_NAME_DESC);
    fieldList.add(FieldName.ACCOUNT + "_" + FieldName.ACCOUNT_DESC);
    fieldList.add(FieldName.INVOICE_CATEGORY + "_" + FieldName.INVOICE_CATEGORY_DESC);
    fieldList.add(FieldName.SETTLEMENT_TYPE + "_" + FieldName.SETTLEMENT_TYPE_DESC);
    fieldList.add(FieldName.CUSTOMER_KIND + "_" + FieldName.CUSTOMER_KIND_DESC);

    fieldList.add(FieldName.VEHICLE_LICENCE_NO + "_" + FieldName.VEHICLE_LICENCE_NO_DESC);
    fieldList.add(FieldName.VEHICLE_BRAND + "_" + FieldName.VEHICLE_BRAND_DESC);
    fieldList.add(FieldName.VEHICLE_MODEL + "_" + FieldName.VEHICLE_MODEL_DESC);
    fieldList.add(FieldName.VEHICLE_YEAR + "_" + FieldName.VEHICLE_YEAR_DESC);
    fieldList.add(FieldName.VEHICLE_ENGINE + "_" + FieldName.VEHICLE_ENGINE_DESC);
    fieldList.add(FieldName.VEHICLE_ENGINE_NO+ "_" + FieldName.VEHICLE_ENGINE_NO_DESC);
    fieldList.add(FieldName.VEHICLE_CHASSIS_NUMBER +"_"+ FieldName.VEHICLE_CHASSIS_NUMBER_DESC);

    fieldList.add(FieldName.MEMBER_NO + "_" + FieldName.MEMBER_NO_DESC);
    fieldList.add(FieldName.TYPE + "_" + FieldName.TYPE_DESC);
    fieldList.add(FieldName.BALANCE + "_" + FieldName.BALANCE_DESC);
    fieldList.add(FieldName.ACCUMULATE_POINTS + "_" + FieldName.ACCUMULATE_POINTS_DESC);
//    fieldList.add(FieldName.SERVICE_DISCOUNT + "_" + FieldName.SERVICE_DISCOUNT_DESC);
//    fieldList.add(FieldName.MATERIAL_DISCOUNT + "_" + FieldName.MATERIAL_DISCOUNT_DESC);
    fieldList.add(FieldName.JOIN_DATE + "_" + FieldName.JOIN_DATE_DESC);
    fieldList.add(FieldName.DEADLINE + "_" + FieldName.DEADLINE_DESC);
    fieldList.add(FieldName.MEMBER_DISCOUNT + "_" + FieldName.MEMBER_DISCOUNT_DESC);
  }

  public class CheckResultMessage {
    public static final String EMPTY_NAME = "客户名为空！";
    public static final String NAME_TOO_LONG = "客户名过长！";
    public static final String SHORT_NAME_TOO_LONG = "客户简称过长！";
    public static final String CITY_TOO_LONG = "所在市过长！";
    public static final String CITY_NAME_ERROR = "所在市填写不标准，请重新填写！";
    public static final String ADDRESS_TOO_LONG = "地址过长！";
    public static final String CONTACT_TOO_LONG = "联系人过长！";
    public static final String MOBILE_TOO_LONG = "手机过长！";
    public static final String MOBILE_TOO_Error = "手机格式不对！";
    public static final String LANDLINE_TOO_LONG = "座机过长！";
    public static final String FAX_TOO_LONG = "传真过长！";
    public static final String AREA_TOO_LONG = "地区过长！";
    public static final String BIRTHDAY_TOO_LONG = "生日过长！";
    public static final String QQ_TOO_LONG = "QQ过长！";
    public static final String EMAIL_TOO_LONG = "Email过长！";
    public static final String BANK_TOO_LONG = "开户行过长！";
    public static final String BANK_ACCOUNT_NAME_TOO_LONG = "开户名过长！";
    public static final String ACCOUNT_TOO_LONG = "账号过长！";
    public static final String INVOICE_CATEGORY_TOO_LONG = "客户简称过长！";
    public static final String SETTLEMENT_TYPE_TOO_LONG = "结算方式过长！";
    public static final String CUSTOMER_KIND_TOO_LONG = "客户类别过长！";

    public static final String VEHICLE_LICENCE_NO_TOO_LONG = "车牌号过长！";
    public static final String VEHICLE_BRAND_TOO_LONG = "车辆品牌过长！";
    public static final String VEHICLE_MODEL_TOO_LONG = "车辆型号过长！";
    public static final String VEHICLE_YEAR_TOO_LONG = "车辆年代过长！";
    public static final String VEHICLE_ENGINE_TOO_LONG = "车辆排量过长！";
    public static final String VEHICLE_ENGINE_NO_TOO_LONG = "发动机号过长！";
    public static final String VEHICLE_CHASSIS_NUMBER_TOO_LONG = "车架号过长";
    public static final String VEHICLE_CHASSIS_NUMBER_ENGLISH_AND_NUMBER = "车架号必须是英文或者数字";

    public static final String MEMBER_NO_TOO_LONG = "会员号过长！";
    public static final String MEMBER_TYPE_TOO_LONG = "会员类型过长！";
    public static final String MEMBER_NO_SCIENCE_PATTERN = "会员号是科学计数法!";

    public static final String NOT_NUMBER = "不是数字！";
    public static final String NOT_INTEGER = "不是整数！";
    public static final String ONE_TO_TEN = "必须在1-10之间！";

    public static final String FORMAT_NOT_EXPECT = "格式不对！";
    public static final String MONTH_FORMAT = "月份小于1或者大于12！";

    public static final String MEMBER_EXIST_IN_TABLE = "会员号在系统中已存在！";
    public static final String MOBILE_EXIST_IN_TABLE = "手机号在系统中已存在!";
    public static final String LAND_LINE_EXIST_IN_TABLE = "座机号在系统中已存在!";
    public static final String VEHICLE_EXIST_IN_TABLE = "车牌号在系统中已存在!";
  }

  public class FieldLength {
    public static final int FIELD_LENGTH_Ten= 20;
    public static final int FIELD_LENGTH_Twenty = 20;
    public static final int FIELD_LENGTH_Fifty= 50;
    public static final int FIELD_LENGTH_NAME = 100;
    public static final int FIELD_LENGTH_SHORT_NAME = 100;
    public static final int FIELD_LENGTH_CITY = 20;
    public static final int FIELD_LENGTH_ADDRESS = 50;
    public static final int FIELD_LENGTH_CONTACT = 200;
    public static final int FIELD_LENGTH_MOBILE = 100;
    public static final int FIELD_LENGTH_LANDLINE = 100;
    public static final int FIELD_LENGTH_FAX = 20;
    public static final int FIELD_LENGTH_AREA = 20;
    public static final int FIELD_LENGTH_BIRTHDAY = 20;
    public static final int FIELD_LENGTH_QQ = 20;
    public static final int FIELD_LENGTH_EMAIL = 50;
    public static final int FIELD_LENGTH_BANK = 20;
    public static final int FIELD_LENGTH_BANK_ACCOUNT_NAME = 20;
    public static final int FIELD_LENGTH_ACCOUNT = 20;
    public static final int FIELD_LENGTH_INVOICE_CATEGORY = 20;
    public static final int FIELD_LENGTH_SETTLEMENT_TYPE = 20;
    public static final int FIELD_LENGTH_CUSTOMER_KIND = 20;

    public static final int FIELD_LENGTH_VEHICLE_LINCENCE_NO = 50;
    public static final int FIELD_LENGTH_VEHICLE_BRAND = 50;
    public static final int FIELD_LENGTH_VEHICLE_MODEL = 50;
    public static final int FIELD_LENGTH_VEHICLE_YEAR = 10;
    public static final int FIELD_LENGTH_VEHICLE_ENGINE = 10;
    public static final int FIELD_LENGTH_VEHICLE_ENGINE_NO = 20;
    public static final int FIELD_LENGTH_VEHICLE_VIN = 20;

    public static final int FIELD_LENGTH_MEMBER_NO = 25;
    public static final int FIELD_LENGTH_MEMBER_TYPE = 50;
  }

}
