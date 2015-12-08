package com.bcgogo.txn.service.importexcel;

import java.util.ArrayList;
import java.util.List;

/**
 * 存储库存导入常量
 * Created by IntelliJ IDEA.
 * User: ZouJianhong
 * Date: 12-4-19
 * Time: 下午3:21
 * To change this template use File | Settings | File Templates.
 */
public class InventoryImportConstants {

   /**
   * 字段名字
   */
  public class FieldName {

    public static final String PRODUCT_NAME = "productName";
    public static final String PRODUCT_NAME_DESC = "商品名";
    public static final String PRODUCT_BRAND = "productBrand";
    public static final String PRODUCT_BRAND_DESC = "商品品牌";
    public static final String PRODUCT_SPEC = "productSpec";
    public static final String PRODUCT_SPEC_DESC = "商品规格";
    public static final String PRODUCT_MODEL = "productModel";
    public static final String PRODUCT_MODEL_DESC = "商品型号";
    public static final String VEHICLE_BRAND = "vehicleBrand";
    public static final String VEHICLE_BRAND_DESC = "车辆品牌";
    public static final String VEHICLE_MODEL = "vehicleModel";
    public static final String VEHICLE_MODEL_DESC = "车型";
    public static final String VEHICLE_YEAR = "vehicleYear";
    public static final String VEHICLE_YEAR_DESC = "车辆年代";
    public static final String VEHICLE_ENGINE = "vehicleEngine";
    public static final String VEHICLE_ENGINE_DESC = "车辆排量";
    public static final String INVENTORY_AMOUNT = "inventoryAmount";
    public static final String INVENTORY_AMOUNT_DESC = "库存数量";
    public static final String PURCHASE_PRICE = "purchasePrice";
    public static final String PURCHASE_PRICE_DESC = "入库价";
    public static final String PURCHASE_TIME = "purchaseTime";
    public static final String PURCHASE_TIME_DESC = "入库时间";
    public static final String SALE_PRICE = "salePrice";
    public static final String SALE_PRICE_DESC = "销售价";
    public static final String COMMODITY_CODE = "commodityCode";
    public static final String COMMODITY_CODE_DESC = "商品编码";
    public static final String UNIT = "unit";
    public static final String UNIT_DESC = "单位";
    public static final String STORAGE_BIN ="storageBin";
    public static final String STORAGE_BIN_DESC = "仓位";
    public static final String TRADE_PRICE = "tradePrice";
    public static final String TRADE_PRICE_DESC = "批发价";
    public static final String STORE_HOUSE = "storeHouse";
    public static final String STORE_HOUSE_DESC = "仓库名称";
  }

  public static List<String> fieldList;

  static {
    fieldList = new ArrayList<String>();
    fieldList.add(FieldName.PRODUCT_NAME + "_" + FieldName.PRODUCT_NAME_DESC);
    fieldList.add(FieldName.PRODUCT_BRAND + "_" + FieldName.PRODUCT_BRAND_DESC);
    fieldList.add(FieldName.PRODUCT_SPEC + "_" + FieldName.PRODUCT_SPEC_DESC);
    fieldList.add(FieldName.PRODUCT_MODEL + "_" + FieldName.PRODUCT_MODEL_DESC);
    fieldList.add(FieldName.VEHICLE_BRAND + "_" + FieldName.VEHICLE_BRAND_DESC);
    fieldList.add(FieldName.VEHICLE_MODEL + "_" + FieldName.VEHICLE_MODEL_DESC);
    fieldList.add(FieldName.VEHICLE_YEAR + "_" + FieldName.VEHICLE_YEAR_DESC);
    fieldList.add(FieldName.VEHICLE_ENGINE + "_" + FieldName.VEHICLE_ENGINE_DESC);
    fieldList.add(FieldName.INVENTORY_AMOUNT + "_" + FieldName.INVENTORY_AMOUNT_DESC);
    fieldList.add(FieldName.PURCHASE_PRICE + "_" + FieldName.PURCHASE_PRICE_DESC);
    fieldList.add(FieldName.PURCHASE_TIME + "_" + FieldName.PURCHASE_TIME_DESC);
    fieldList.add(FieldName.SALE_PRICE + "_" + FieldName.SALE_PRICE_DESC);
    fieldList.add(FieldName.COMMODITY_CODE + "_" +FieldName.COMMODITY_CODE_DESC);
    fieldList.add(FieldName.UNIT + "_" +FieldName.UNIT_DESC);
    fieldList.add(FieldName.STORAGE_BIN + "_" +FieldName.STORAGE_BIN_DESC);
    fieldList.add(FieldName.TRADE_PRICE + "_" +FieldName.TRADE_PRICE_DESC);
    fieldList.add(FieldName.STORE_HOUSE + "_" +FieldName.STORE_HOUSE_DESC);
  }

  public class CheckResultMessage {
    public static final String EMPTY_PRODUCT_NAME = "商品名为空！";
    public static final String PRODUCT_NAME_TOO_LONG = "商品名过长！";
    public static final String PRODUCT_BRAND_TOO_LONG = "商品品牌过长！";
    public static final String PRODUCT_SPEC_TOO_LONG = "商品规格过长！";
    public static final String PRODUCT_MODEL_TOO_LONG = "商品型号过长！";
    public static final String VEHICLE_BRAND_TOO_LONG = "车辆品牌过长！";
    public static final String VEHICLE_MODEL_TOO_LONG = "车辆型号过长！";
    public static final String VEHICLE_YEAR_TOO_LONG = "车辆年代过长！";
    public static final String VEHICLE_ENGINE_TOO_LONG = "车辆排量过长！";
    public static final String INVENTORY_AMOUNT_NOT_NUMBER = "库存数量格式不正确！";
    public static final String PURCHASE_PRICE_NOT_NUMBER = "入库价格格式不正确！";
    public static final String PURCHASE_TIME_NOT_NUMBER = "入库时间格式不正确！";
    public static final String SALE_PRICE_NOT_NUMBER = "销售价格式不正确！";
    public static final String COMMODITY_CODE_TOO_LONG = "商品编码过长！";
    public static final String COMMODITY_CODE_SPECIAL_CHARACTERS = "商品编号含有特殊字符！";
    public static final String UNIT_TOO_LONG = "单位过长";
    public static final String STORAGE_BIN_TOO_LONG = "仓位过长";
    public static final String TRADE_PRICE_NOT_NUMBER="批发价格式不正确";
    public static final String STORE_HOUSE_TOO_LONG = "仓库名称过长";
    public static final String COMMODITY_CODE_EXIST_IN_TABLE = "商品编号在系统中已存在！";
  }

  public class FieldLength {
    public static final int FIELD_LENGTH_PRODUCT_NAME = 200;
    public static final int FIELD_LENGTH_PRODUCT_BRAND = 200;
    public static final int FIELD_LENGTH_PRODUCT_SPEC = 2000;
    public static final int FIELD_LENGTH_PRODUCT_MODEL = 50;
    public static final int FIELD_LENGTH_VEHICLE_BRAND = 50;
    public static final int FIELD_LENGTH_VEHICLE_MODEL = 50;
    public static final int FIELD_LENGTH_VEHICLE_YEAR = 10;
    public static final int FIELD_LENGTH_VEHICLE_ENGINE = 10;
    public static final int FIELD_LENGTH_COMMODITY_CODE=50;
    public static final int FIELD_LENGTH_UNIT=10;
    public static final int FIELD_LENGTH_STORAGE_BIN = 15;
    public static final int FIELD_LENGTH_STORE_HOUSE = 10;
  }

  public class DefaultValue{
    public static final String DEFAULT_VALUE_PRODUCT_VEHHICLE_BRAND = "";
  }
}
