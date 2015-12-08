package com.bcgogo.txn.service.importexcel.order;

import java.util.ArrayList;
import java.util.List;

/**
 * 导入单据使用常量
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 12-10-29
 * Time: 上午11:34
 * To change this template use File | Settings | File Templates.
 */
public class OrderImportConstants {

  /**
   * 字段名字
   */
  public class FieldName {

    public static final String RECEIPT = "receipt";
    public static final String RECEIPT_DESC = "单据编号";
    public static final String PRODUCT_CODE = "productCode";
    public static final String PRODUCT_CODE_DESC = "商品编号";
    public static final String PRODUCT_NAME = "productName";
    public static final String PRODUCT_NAME_DESC = "商品名";
    public static final String BRAND = "brand";
    public static final String BRAND_DESC = "品牌/产地";
    public static final String SPEC = "spec";
    public static final String SPEC_DESC = "规格";
    public static final String MODEL = "model";
    public static final String MODEL_DESC = "型号";

    public static final String VEHICLE_BRAND = "vehicleBrand";
    public static final String VEHICLE_BRAND_DESC = "车辆品牌";
    public static final String VEHICLE_MODEL = "vehicleModel";
    public static final String VEHICLE_MODEL_DESC = "车辆车型";
    public static final String UNIT = "unit";
    public static final String UNIT_DESC = "单位";
    public static final String PRICE = "price";
    public static final String PRICE_DESC = "单价";
    public static final String AMOUNT = "amount";
    public static final String AMOUNT_DESC = "数量";
    public static final String ORDER_STATUS = "orderStatus";
    public static final String ORDER_STATUS_DESC = "单据状态";
    public static final String VEST_DATE = "vestDate";
    public static final String VEST_DATE_DESC = "结算日期";

    public static final String VEHICLE = "vehicle";
    public static final String VEHICLE_DESC = "车牌号";
    public static final String CUSTOMER_SUPPLIER_NAME = "customerSupplierName";
    public static final String CUSTOMER_SUPPLIER_NAME_DESC = "客户/供应商";
    public static final String CONTACT = "contact";
    public static final String CONTACT_DESC = "联系人";
    public static final String MOBILE = "mobile";
    public static final String MOBILE_DESC = "手机号";
    public static final String MEMBER_TYPE = "memberType";
    public static final String MEMBER_TYPE_DESC = "会员类型";
    public static final String MEMBER_CARD_NO = "memberCardNo";
    public static final String MEMBER_CARD_NO_DESC = "会员卡号";
    public static final String PAY_PER_PROJECT = "payPerProject";
    public static final String PAY_PER_PROJECT_DESC = "计次收费项目";
    public static final String SALES_MAN = "salesMan";
    public static final String SALES_MAN_DESC = "销售人";
    public static final String SERVICE_TOTAL = "serviceTotal";
    public static final String SERVICE_TOTAL_DESC = "工时费";
    public static final String SERVICE_WORKER = "serviceWorker";
    public static final String SERVICE_WORKER_DESC = "施工人";
    public static final String SERVICE_CONTENT = "serviceContent";
    public static final String SERVICE_CONTENT_DESC = "施工内容";
    public static final String IN_TIME = "inTime";
    public static final String IN_TIME_DESC = "进厂时间";
    public static final String OUT_TIME = "outTime";
    public static final String OUT_TIME_DESC = "预计出厂时间";
    public static final String PAY_WAY = "payWay";
    public static final String PAY_WAY_DESC = "结算方式";
    public static final String TOTAL = "total";
    public static final String TOTAL_DESC = "总计";
    public static final String ACTUALLY_PAID = "actuallyPaid";
    public static final String ACTUALLY_PAID_DESC = "实收";
    public static final String DEBT = "debt";
    public static final String DEBT_DESC = "欠款";
    public static final String MEMO = "memo";
    public static final String MEMO_DESC = "备注";


  }

  public static final String[] WHOLESALER_EXCLUDE_FIELDS = {
      FieldName.MEMBER_TYPE_DESC,
      FieldName.MEMBER_CARD_NO_DESC,
      FieldName.PAY_PER_PROJECT_DESC,
      FieldName.SERVICE_TOTAL_DESC,
      FieldName.SERVICE_WORKER_DESC,
      FieldName.SERVICE_CONTENT_DESC,
      FieldName.IN_TIME_DESC,
      FieldName.OUT_TIME_DESC
  };

  public static List<String> fieldList;

  public static List<String> inventoryFieldList;
  public static List<String> saleFieldList;
  public static List<String> washFieldList;
  static {
    fieldList = new ArrayList<String>();
    fieldList.add(FieldName.RECEIPT + "_" + FieldName.RECEIPT_DESC);
    fieldList.add(FieldName.PRODUCT_CODE + "_" + FieldName.PRODUCT_CODE_DESC);
    fieldList.add(FieldName.PRODUCT_NAME + "_" + FieldName.PRODUCT_NAME_DESC);
    fieldList.add(FieldName.BRAND + "_" + FieldName.BRAND_DESC);
    fieldList.add(FieldName.SPEC + "_" + FieldName.SPEC_DESC);
    fieldList.add(FieldName.MODEL + "_" + FieldName.MODEL_DESC);
    fieldList.add(FieldName.VEHICLE_BRAND + "_" + FieldName.VEHICLE_BRAND_DESC);
    fieldList.add(FieldName.VEHICLE_MODEL + "_" + FieldName.VEHICLE_MODEL_DESC);
    fieldList.add(FieldName.PRICE + "_" + FieldName.PRICE_DESC);
    fieldList.add(FieldName.AMOUNT + "_" + FieldName.AMOUNT_DESC);
    fieldList.add(FieldName.UNIT + "_" + FieldName.UNIT_DESC);
    fieldList.add(FieldName.ORDER_STATUS + "_" + FieldName.ORDER_STATUS_DESC);
    fieldList.add(FieldName.VEST_DATE + "_" + FieldName.VEST_DATE_DESC);
    fieldList.add(FieldName.VEHICLE + "_" + FieldName.VEHICLE_DESC);
    fieldList.add(FieldName.CUSTOMER_SUPPLIER_NAME + "_" + FieldName.CUSTOMER_SUPPLIER_NAME_DESC);
    fieldList.add(FieldName.CONTACT + "_" + FieldName.CONTACT_DESC);
    fieldList.add(FieldName.MOBILE + "_" + FieldName.MOBILE_DESC);
    fieldList.add(FieldName.MEMBER_TYPE + "_" + FieldName.MEMBER_TYPE_DESC);
    fieldList.add(FieldName.MEMBER_CARD_NO + "_" + FieldName.MEMBER_CARD_NO_DESC);
    fieldList.add(FieldName.PAY_PER_PROJECT + "_" + FieldName.PAY_PER_PROJECT_DESC);
    fieldList.add(FieldName.SALES_MAN + "_" + FieldName.SALES_MAN_DESC);
    fieldList.add(FieldName.SERVICE_TOTAL + "_" + FieldName.SERVICE_TOTAL_DESC);
    fieldList.add(FieldName.SERVICE_WORKER + "_" + FieldName.SERVICE_WORKER_DESC);
    fieldList.add(FieldName.SERVICE_CONTENT + "_" + FieldName.SERVICE_CONTENT_DESC);
    fieldList.add(FieldName.IN_TIME + "_" + FieldName.IN_TIME_DESC);
    fieldList.add(FieldName.OUT_TIME + "_" + FieldName.OUT_TIME_DESC);
    fieldList.add(FieldName.PAY_WAY + "_" + FieldName.PAY_WAY_DESC);
    fieldList.add(FieldName.TOTAL + "_" + FieldName.TOTAL_DESC);
    fieldList.add(FieldName.ACTUALLY_PAID + "_" + FieldName.ACTUALLY_PAID_DESC);
    fieldList.add(FieldName.DEBT + "_" + FieldName.DEBT_DESC);
    fieldList.add(FieldName.MEMO + "_" + FieldName.MEMO_DESC);

    inventoryFieldList = new ArrayList<String>();
    inventoryFieldList.add(FieldName.RECEIPT + "_" + FieldName.RECEIPT_DESC);
    inventoryFieldList.add(FieldName.PRODUCT_CODE + "_" + FieldName.PRODUCT_CODE_DESC);
    inventoryFieldList.add(FieldName.PRODUCT_NAME + "_" + FieldName.PRODUCT_NAME_DESC);
    inventoryFieldList.add(FieldName.BRAND + "_" + FieldName.BRAND_DESC);
    inventoryFieldList.add(FieldName.SPEC + "_" + FieldName.SPEC_DESC);
    inventoryFieldList.add(FieldName.MODEL + "_" + FieldName.MODEL_DESC);
    inventoryFieldList.add(FieldName.VEHICLE_BRAND + "_" + FieldName.VEHICLE_BRAND_DESC);
    inventoryFieldList.add(FieldName.VEHICLE_MODEL + "_" + FieldName.VEHICLE_MODEL_DESC);
    inventoryFieldList.add(FieldName.PRICE + "_" + FieldName.PRICE_DESC);
    inventoryFieldList.add(FieldName.AMOUNT + "_" + FieldName.AMOUNT_DESC);
    inventoryFieldList.add(FieldName.UNIT + "_" + FieldName.UNIT_DESC);
    inventoryFieldList.add(FieldName.ORDER_STATUS + "_" + FieldName.ORDER_STATUS_DESC);
    inventoryFieldList.add(FieldName.VEST_DATE + "_" + "入库时间");
    inventoryFieldList.add(FieldName.CUSTOMER_SUPPLIER_NAME + "_" + FieldName.CUSTOMER_SUPPLIER_NAME_DESC);
    inventoryFieldList.add(FieldName.CONTACT + "_" + FieldName.CONTACT_DESC);
    inventoryFieldList.add(FieldName.MOBILE + "_" + FieldName.MOBILE_DESC);
    inventoryFieldList.add(FieldName.TOTAL + "_" + FieldName.TOTAL_DESC);
    inventoryFieldList.add(FieldName.ACTUALLY_PAID + "_" + FieldName.ACTUALLY_PAID_DESC);
    inventoryFieldList.add(FieldName.DEBT + "_" + FieldName.DEBT_DESC);
    inventoryFieldList.add(FieldName.MEMO + "_" + FieldName.MEMO_DESC);

    saleFieldList = new ArrayList<String>();
    saleFieldList.add(FieldName.RECEIPT + "_" + FieldName.RECEIPT_DESC);
    saleFieldList.add(FieldName.PRODUCT_CODE + "_" + FieldName.PRODUCT_CODE_DESC);
    saleFieldList.add(FieldName.PRODUCT_NAME + "_" + FieldName.PRODUCT_NAME_DESC);
    saleFieldList.add(FieldName.BRAND + "_" + FieldName.BRAND_DESC);
    saleFieldList.add(FieldName.SPEC + "_" + FieldName.SPEC_DESC);
    saleFieldList.add(FieldName.MODEL + "_" + FieldName.MODEL_DESC);
    saleFieldList.add(FieldName.VEHICLE_BRAND + "_" + FieldName.VEHICLE_BRAND_DESC);
    saleFieldList.add(FieldName.VEHICLE_MODEL + "_" + FieldName.VEHICLE_MODEL_DESC);
    saleFieldList.add(FieldName.PRICE + "_" + FieldName.PRICE_DESC);
    saleFieldList.add(FieldName.AMOUNT + "_" + FieldName.AMOUNT_DESC);
    saleFieldList.add(FieldName.UNIT + "_" + FieldName.UNIT_DESC);
    saleFieldList.add(FieldName.ORDER_STATUS + "_" + FieldName.ORDER_STATUS_DESC);
    saleFieldList.add(FieldName.VEST_DATE + "_" + FieldName.VEST_DATE_DESC);
    saleFieldList.add(FieldName.CUSTOMER_SUPPLIER_NAME + "_" + FieldName.CUSTOMER_SUPPLIER_NAME_DESC);
    saleFieldList.add(FieldName.CONTACT + "_" + FieldName.CONTACT_DESC);
    saleFieldList.add(FieldName.MOBILE + "_" + FieldName.MOBILE_DESC);
    saleFieldList.add(FieldName.MEMBER_TYPE + "_" + FieldName.MEMBER_TYPE_DESC);
    saleFieldList.add(FieldName.MEMBER_CARD_NO + "_" + FieldName.MEMBER_CARD_NO_DESC);
    saleFieldList.add(FieldName.PAY_PER_PROJECT + "_" + FieldName.PAY_PER_PROJECT_DESC);
    saleFieldList.add(FieldName.SALES_MAN + "_" + FieldName.SALES_MAN_DESC);
    saleFieldList.add(FieldName.PAY_WAY + "_" + FieldName.PAY_WAY_DESC);
    saleFieldList.add(FieldName.TOTAL + "_" + FieldName.TOTAL_DESC);
    saleFieldList.add(FieldName.ACTUALLY_PAID + "_" + FieldName.ACTUALLY_PAID_DESC);
    saleFieldList.add(FieldName.DEBT + "_" + FieldName.DEBT_DESC);
    saleFieldList.add(FieldName.MEMO + "_" + FieldName.MEMO_DESC);

    washFieldList = new ArrayList<String>();
    washFieldList.add(FieldName.RECEIPT + "_" + FieldName.RECEIPT_DESC);
    washFieldList.add(FieldName.ORDER_STATUS + "_" + FieldName.ORDER_STATUS_DESC);
    washFieldList.add(FieldName.VEST_DATE + "_" + FieldName.VEST_DATE_DESC);
    washFieldList.add(FieldName.VEHICLE + "_" + FieldName.VEHICLE_DESC);
    washFieldList.add(FieldName.CUSTOMER_SUPPLIER_NAME + "_" + FieldName.CUSTOMER_SUPPLIER_NAME_DESC);
    washFieldList.add(FieldName.CONTACT + "_" + FieldName.CONTACT_DESC);
    washFieldList.add(FieldName.MOBILE + "_" + FieldName.MOBILE_DESC);
    washFieldList.add(FieldName.MEMBER_TYPE + "_" + FieldName.MEMBER_TYPE_DESC);
    washFieldList.add(FieldName.MEMBER_CARD_NO + "_" + FieldName.MEMBER_CARD_NO_DESC);
    washFieldList.add(FieldName.PAY_PER_PROJECT + "_" + FieldName.PAY_PER_PROJECT_DESC);
    washFieldList.add(FieldName.SERVICE_TOTAL + "_" + FieldName.SERVICE_TOTAL_DESC);
    washFieldList.add(FieldName.SERVICE_WORKER + "_" + FieldName.SERVICE_WORKER_DESC);
    washFieldList.add(FieldName.SERVICE_CONTENT + "_" + FieldName.SERVICE_CONTENT_DESC);
    washFieldList.add(FieldName.PAY_WAY + "_" + FieldName.PAY_WAY_DESC);
    washFieldList.add(FieldName.TOTAL + "_" + FieldName.TOTAL_DESC);
    washFieldList.add(FieldName.ACTUALLY_PAID + "_" + FieldName.ACTUALLY_PAID_DESC);
    washFieldList.add(FieldName.DEBT + "_" + FieldName.DEBT_DESC);
    washFieldList.add(FieldName.MEMO + "_" + FieldName.MEMO_DESC);

  }

  public class CheckResultMessage {
    public static final String EMPTY_PRODUCT_NAME = "商品名为空！";
    public static final String EMPTY_RECEIPT_NAME = "单据号为空！";
    public static final String ERROR_RECEIPT_FORMAT = "单据号只能输入数字！";
    public static final String PRODUCT_NAME_TOO_LONG = "商品名过长！";
    public static final String RECEIPT_TOO_LONG = "单据编号过长！";
    public static final String PRODUCT_CODE_TOO_LONG = "商品编号过长！";
    public static final String SUPPLIER_NAME_TOO_LONG = "客户或供应商名过长！";
    public static final String SPEC_TOO_LONG = "规格过长！";
    public static final String CONTACT_TOO_LONG = "联系人过长！";
    public static final String MOBILE_TOO_LONG = "手机号过长！";
    public static final String BRAND_TOO_LONG = "品牌过长！";
    public static final String UNIT_TOO_LONG = "单位过长！";
    public static final String MODEL_TOO_LONG = "型号过长！";
    public static final String VEHICLE_TOO_LONG = "车牌号过长！";
    public static final String MEMBER_CARD_NO_TOO_LONG = "会员卡号过长！";
    public static final String MEMBER_TYPE_TOO_LONG = "会员类型过长！";
    public static final String PAY_PER_PROJECT_TOO_LONG = "计次消费项目字段过长！";
    public static final String VEHICLE_MODEL_TOO_LONG = "适用车辆车型过长！";
    public static final String VEHICLE_BRAND_TOO_LONG = "适用车辆品牌过长！";

    public static final String SALES_MAN_TOO_LONG = "销售人字段过长！";
    public static final String PAY_WAY_TOO_LONG = "结算方式字段过长！";
    public static final String SERVICE_TOTAL_TOO_LONG = "工时费字段过长！";
    public static final String SERVICE_CONTENT_TOO_LONG = "施工内容过长！";
    public static final String SERVICE_WORKER_TOO_LONG = "施工人过长！";
    public static final String IN_TIME_TOO_LONG = "进厂时间过长！";
    public static final String OUT_TIME_TOO_LONG = "出厂时间过长！";


    public static final String DEBT_NOT_NUMBER="欠款不是数字";

    public static final String AMOUNT_NOT_NUMBER="商品数量不是数字";
    public static final String TRADE_PRICE_NOT_NUMBER="批发价不是数字";
    public static final String INVENTORY_AVERAGE_PRICE_NOT_NUMBER="批发价不是数字";
    public static final String PRICE_NOT_NUMBER="商品价格不是数字";
    public static final String UPPER_LIMIT_NOT_NUMBER="库存上限不是数字";
    public static final String LATEST_INVENTORY_PRICE ="最近入库价不是数字";
    public static final String SALES_PRICE_NOT_NUMBER="销售价不是数字";
    public static final String TOTAL_NOT_NUMBER="总计不是数字";
    public static final String SERVICE_TOTAL_NOT_NUMBER="工时费不是数字";
    public static final String ACTUALLY_PAID_NOT_NUMBER="实收不是数字";

    public static final String VEST_DATE_TOO_LONG = "日期字段过长！";
    public static final String ORDER_STATUS_TOO_LONG = "单据状态过长！";
    public static final String ORDER_TYPE_TOO_LONG = "单据类型过长！" ;
    public static final String MEMO_TOO_LONG = "备注过长！" ;

    public static final String ERROR_DATE_FOMART = "日期格式不正确！";

  }

  public class FieldLength {

    public static final int FIELD_LENGTH_PRODUCT_NAME = 100;
    public static final int FIELD_LENGTH_CONTACT = 50;
    public static final int FIELD_LENGTH_MOBILE = 50;
    public static final int FIELD_LENGTH_MEMBER_TYPE = 50;
    public static final int FIELD_LENGTH_SUPPLIER_NAME = 50;
    public static final int FIELD_LENGTH_RECEIPT_NAME = 50;
    public static final int FIELD_LENGTH_PRODUCT_CODE = 50;
    public static final int FIELD_LENGTH_SPEC = 50;
    public static final int FIELD_LENGTH_BRAND = 50;
    public static final int FIELD_LENGTH_UNIT = 50;
    public static final int FIELD_LENGTH_VEHICLE = 12;
    public static final int FIELD_LENGTH_ORDER_STATUS = 30;
    public static final int FIELD_LENGTH_MEMO = 500;
    public static final int FIELD_LENGTH_MEMBER_CARD_NO = 50;
    public static final int FIELD_LENGTH_ORDER_TYPE = 30;
    public static final int FIELD_LENGTH_PAY_PER_PROJECT = 50;
    public static final int FIELD_LENGTH_MODEL = 50;
    public static final int FIELD_LENGTH_SALES_MAN = 50;
    public static final int FIELD_LENGTH_SERVICE_CONTENT = 100;
    public static final int FIELD_LENGTH_VEHICLE_MODEL = 50;
    public static final int FIELD_LENGTH_VEHICLE_BRAND = 50;

    public static final int FIELD_LENGTH_STANDARD_DATE_FORMAT = 10;


//    public static final int FIELD_LENGTH_INVENTORY_AMOUNT = 100;
//    public static final int FIELD_LENGTH_INVENTORY_PRICE = 50;
//    public static final int FIELD_LENGTH_SALES_PRICE = 50;

  }

}
