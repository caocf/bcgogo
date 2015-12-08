package com.bcgogo.txn.service.exportExcel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: king
 * Date: 13-12-13
 * Time: 下午5:14
 * To change this template use File | Settings | File Templates.
 */
public class AssistantStatDetailConstant {
  public static final String ASSISTANT_NAME = "员工";
  public static final String DEPARTMENT = "部门";
  public static final String DATE = "日期";
  public static final String VEHICLE = "车辆";
  public static final String CUSTOMER = "客户";
  public static final String CONTENT = "内容";
  public static final String STANDARD_HOUR = "标准工时";
  public static final String STANDARD_SERVICE = "工时单价";
  public static final String ACTUAL_HOUR = "实际工时";
  public static final String ACTUAL_SERVICE = "金额";
  public static final String ACHIEVEMENT = "提成";
  public static final String RECEIPT_NO = "单据";
  public static final String TYPE = "类型";
  public static final String PRODUCT_NAME = "品名";
  public static final String COUNT = "数量";
  public static final String UNIT_PRICE = "单价";
  public static final String INCOME = "收入";
  public static final String PROFIT = "利润";
  public static final String PROFIT_ACHIEVEMENT = "利润提成";
  public static final String CARD_NO = "卡号";
  public static final String CARD_NAME = "卡名";
  public static final String CARD_TYPE = "卡类型";
  public static final String CARD_AMOUNT = "卡额";
  public static final String CUSTOMER_NAME = "客户名称";
  public static final String BUYCARD_OR_RETURN = "购卡/退卡";
  public static final String DOC_NO = "凭证号";
  public static final String BUSINESS_CATEGORY = "营业分类";
  public static List<String> repairFieldList;
  public static List<String> washFieldList;
  public static List<String> salesFieldList;
  public static List<String> memberFieldList;
  public static List<String> businessAccountFieldList;
  static {
    repairFieldList = new ArrayList<String>();
    repairFieldList.add(ASSISTANT_NAME);
    repairFieldList.add(DEPARTMENT);
    repairFieldList.add(DATE);
    repairFieldList.add(VEHICLE);
    repairFieldList.add(CUSTOMER);
    repairFieldList.add(CONTENT);
    repairFieldList.add(STANDARD_HOUR);
    repairFieldList.add(STANDARD_SERVICE);
    repairFieldList.add(ACTUAL_HOUR);
    repairFieldList.add(ACTUAL_SERVICE);
    repairFieldList.add(ACHIEVEMENT);
    repairFieldList.add(RECEIPT_NO);

    washFieldList = new ArrayList<String>();
    washFieldList.add(ASSISTANT_NAME);
    washFieldList.add(DEPARTMENT);
    washFieldList.add(DATE);
    washFieldList.add(VEHICLE);
    washFieldList.add(CUSTOMER);
    washFieldList.add(CONTENT);
    washFieldList.add(ACTUAL_SERVICE);
    washFieldList.add(ACHIEVEMENT);
    washFieldList.add(RECEIPT_NO);

    salesFieldList = new ArrayList<String>();
    salesFieldList.add(ASSISTANT_NAME);
    salesFieldList.add(DEPARTMENT);
    salesFieldList.add(DATE);
    salesFieldList.add(TYPE);
    salesFieldList.add(CUSTOMER);
    salesFieldList.add(PRODUCT_NAME);
    salesFieldList.add(COUNT);
    salesFieldList.add(UNIT_PRICE);
    salesFieldList.add(INCOME);
    salesFieldList.add(ACHIEVEMENT);
    salesFieldList.add(PROFIT);
    salesFieldList.add(PROFIT_ACHIEVEMENT);
    salesFieldList.add(RECEIPT_NO);

    memberFieldList = new ArrayList<String>();
    memberFieldList.add(ASSISTANT_NAME);
    memberFieldList.add(DEPARTMENT);
    memberFieldList.add(DATE);
    memberFieldList.add(CARD_NO);
    memberFieldList.add(CARD_NAME);
    memberFieldList.add(CARD_TYPE);
    memberFieldList.add(CARD_AMOUNT);
    memberFieldList.add(CUSTOMER_NAME);
    memberFieldList.add(BUYCARD_OR_RETURN);
    memberFieldList.add(ACTUAL_SERVICE);
    memberFieldList.add(ACHIEVEMENT);

    businessAccountFieldList = new ArrayList<String>();
    businessAccountFieldList.add(DEPARTMENT);
    businessAccountFieldList.add(ASSISTANT_NAME);
    businessAccountFieldList.add(DATE);
    businessAccountFieldList.add(TYPE);
    businessAccountFieldList.add(DOC_NO);
    businessAccountFieldList.add(CONTENT);
    businessAccountFieldList.add(BUSINESS_CATEGORY);
    businessAccountFieldList.add(ACTUAL_SERVICE);
  }
}
