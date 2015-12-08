package com.bcgogo.user.service.excelimport.supplier;

import java.util.ArrayList;
import java.util.List;

/**
 * 导入供应商使用常量
 * Created by IntelliJ IDEA.
 * User: ZouJianhong
 * Date: 12-4-17
 * Time: 下午3:23
 * To change this template use File | Settings | File Templates.
 */
public class SupplierImportConstants {

  /**
   * 字段名字
   */
  public class FieldName {

    public static final String NAME = "name";
    public static final String NAME_DESC = "供应商名";
    public static final String ABBR = "abbr";
    public static final String ABBR_DESC = "简称";
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
    public static final String BUSINESS_SCOPE = "businessScope";
    public static final String BUSINESS_SCOPE_DESC = "经营范围";

  }
  public static final String PROVINCE_AND_CITY_NO = "provinceAndCityNo";
  public static List<String> fieldList;

  static {
    fieldList = new ArrayList<String>();
    fieldList.add(FieldName.NAME + "_" + FieldName.NAME_DESC);
    fieldList.add(FieldName.ABBR + "_" + FieldName.ABBR_DESC);
    fieldList.add(FieldName.CITY + "_" + FieldName.CITY_DESC);
    fieldList.add(FieldName.ADDRESS + "_" + FieldName.ADDRESS_DESC);
    fieldList.add(FieldName.CONTACT + "_" + FieldName.CONTACT_DESC);
    fieldList.add(FieldName.MOBILE + "_" + FieldName.MOBILE_DESC);
    fieldList.add(FieldName.LANDLINE + "_" + FieldName.LANDLINE_DESC);
    fieldList.add(FieldName.FAX + "_" + FieldName.FAX_DESC);
    fieldList.add(FieldName.QQ + "_" + FieldName.QQ_DESC);
    fieldList.add(FieldName.EMAIL + "_" + FieldName.EMAIL_DESC);
    fieldList.add(FieldName.BANK + "_" + FieldName.BANK_DESC);
    fieldList.add(FieldName.BANK_ACCOUNT_NAME + "_" + FieldName.BANK_ACCOUNT_NAME_DESC);
    fieldList.add(FieldName.ACCOUNT + "_" + FieldName.ACCOUNT_DESC);
    fieldList.add(FieldName.INVOICE_CATEGORY + "_" + FieldName.INVOICE_CATEGORY_DESC);
    fieldList.add(FieldName.BUSINESS_SCOPE + "_" + FieldName.BUSINESS_SCOPE_DESC);
  }

  public class CheckResultMessage {
    public static final String EMPTY_NAME = "供应商名为空！";
    public static final String NAME_TOO_LONG = "供应商名过长！";
    public static final String ABBR_TOO_LONG = "供应商简称过长！";
    public static final String CITY_TOO_LONG = "所在市过长！";
    public static final String CITY_NAME_ERROR = "所在市填写不标准，请重新填写！";
    public static final String ADDRESS_TOO_LONG = "地址过长！";
    public static final String CONTACT_TOO_LONG = "联系人过长！";
    public static final String MOBILE_TOO_LONG = "手机过长！";
    public static final String LANDLINE_TOO_LONG = "座机过长！";
    public static final String LAND_LINE_EXIST_IN_TABLE = "座机号在系统中已存在!";
    public static final String FAX_TOO_LONG = "传真过长！";
    public static final String QQ_TOO_LONG = "QQ过长！";
    public static final String EMAIL_TOO_LONG = "Email过长！";
    public static final String BANK_TOO_LONG = "开户行过长！";
    public static final String BANK_ACCOUNT_NAME_TOO_LONG = "开户名过长！";
    public static final String ACCOUNT_TOO_LONG = "账号过长！";
    public static final String INVOICE_CATEGORY_TOO_LONG = "发票类型过长！";
    public static final String BUSINESS_SCOPE_TOO_LONG = "车辆排量过长！";
    public static final String MOBILE_EXIST_IN_TABLE = "手机号在系统中已经存在！";
  }

  public class FieldLength {
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
