package com.bcgogo.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * User: Jimuchen
 * Date: 12-6-21
 * Time: 下午7:07
 */
public class RfTxnConstant {
  public static final String FORMAT_CHINESE_YEAR_MONTH_DATE = "yyyy" + BcgogoI18N.getMessageByKey("bcgogo.year")
      + "MM" + BcgogoI18N.getMessageByKey("bcgogo.month") + "dd" + BcgogoI18N.getMessageByKey("bcgogo.date");

  public static Map<String, String> sortCommandMap = null;  //库存查询、商品首页、库存明细中商品车型的排序状态
  public static Map<String, String> sortCommandMap_DB = null;//库存查询，商品首页，库存告急中用到的search.inventorySearchIndex中 排序

  static {
    sortCommandMap = new HashMap<String, String>();
    sortCommandMap.put("nameAsc", "product_name_exact asc");
    sortCommandMap.put("nameDesc", "product_name_exact desc");
    sortCommandMap.put("brandAsc", "product_brand_exact asc");
    sortCommandMap.put("brandDesc", "product_brand_exact desc");
    sortCommandMap.put("specAsc", "product_spec_exact asc");
    sortCommandMap.put("specDesc", "product_spec_exact desc");
    sortCommandMap.put("modelAsc", "product_model_exact asc");
    sortCommandMap.put("modelDesc", "product_model_exact desc");
    sortCommandMap.put("vehicleModelAsc", "product_vehicle_model_exact asc");
    sortCommandMap.put("vehicleModelDesc", "product_vehicle_model_exact desc");

    sortCommandMap_DB = new HashMap<String, String>();
    sortCommandMap_DB.put("nameAsc", "productName asc");
    sortCommandMap_DB.put("nameDesc", "productName desc");
    sortCommandMap_DB.put("brandAsc", "productBrand asc");
    sortCommandMap_DB.put("brandDesc", "productBrand desc");
    sortCommandMap_DB.put("specAsc", "productSpec asc");
    sortCommandMap_DB.put("specDesc", "productSpec desc");
    sortCommandMap_DB.put("modelAsc", "productModel asc");
    sortCommandMap_DB.put("modelDesc", "productModel desc");
    sortCommandMap_DB.put("vehicleModelAsc", "model asc");
    sortCommandMap_DB.put("vehicleModelDesc", "model desc");
    sortCommandMap_DB.put("lowerLimit", "lowerLimit");
    sortCommandMap_DB.put("upperLimit", "upperLimit");
  }

  public class TextSymbol {
    public static final String ETC = "...";
    public static final String PAUSE_MARK = "、";
    public static final String COMMA = "，";
    public static final String SEMICOLON = "；";
  }

  public static final String PURCHASE_INVENTORY_MESSAGE_SHORTAGE = "库存不足无法作废";
  public static final String PURCHASE_INVENTORY_MESSAGE_SALE = "已经销售，无法作废";
  public static final String PURCHASE_INVENTORY_MESSAGE_CANCELED = "该单据已经作废";
  public static final String ASSISTANT_NAME = "未填写";

  //库存不足，超出上限
  public static final String INVENTORY_LOWER_LIMIT = "lowerLimit";
  public static final String INVENTORY_UPPER_LIMIT = "upperLimit";

  //进销存首页，商品查询首页，生成单据默认数量
  public static final Double ORDER_DEFAULT_AMOUNT = 1D;

  //库存统计在memcache 中存放更新时间
  public static final Long MEMCACHE_INVENTORY_COUNT_ACTIVETIME = 60 * 60 * 1000L;
//  public static final Long MEMCACHE_INVENTORY_COUNT_ACTIVETIME = 60L;
  //solr 查询库存金额时，遍历每次分页的大小
  public static final Integer SOLR_INVENTORY_SUM_MAXROWS = 50;
}
