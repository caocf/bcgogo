package com.bcgogo.utils;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: zhouxiaochen
 * Date: 11-12-28
 * Time: 下午5:19
 * To change this template use File | Settings | File Templates.
 */

@Deprecated
public class TxnConstant {
  //日期格式
  public static final String FORMAT_STANDARD_YEAR_MONTH_DATE = "yyyy-MM-dd";
  public static final String FORMAT_STANDARD_MONTH_DATE = "MM-dd";
  public static final String FORMAT_STANDARD_HOUR_MINUTE_SECOND = "HH:mm:ss";
  public static final String FORMAT_STANDARD_YEAR_MONTH_DATE_HOUR_MINUTE_SECOND = "yyyy-MM-dd HH:mm:ss";
  public static final String FORMAT_STANDARD_YEAR_MONTH_DATE_HOUR_MINUTE = "yyyy-MM-dd HH:mm";
//  public static final String FORMAT_CHINESE_YEAR_MONTH_DATE = "yyyy" + BcgogoI18N.getMessageByKey("bcgogo.year")
//      + "MM" + BcgogoI18N.getMessageByKey("bcgogo.month") + "dd" + BcgogoI18N.getMessageByKey("bcgogo.date");

  //维修美容的类型
  public static final Map SERVICE_TYPE_MAP = new LinkedHashMap();
  public static final String SERVICE_TYPE_MAINTENANCE = "1";              //OrderTypes.REPAIR
  public static final String SERVICE_TYPE_SALES = "2";                     //OrderTypes.SALE
  public static final String SERVICE_TYPE_WASH = "3";                      //OrderTypes.WASH

  public static Map<String, String> getServiceTypeMap(Locale sessionLocale) {        //OrderTypes.getServiceLocaleMap(locale)
    SERVICE_TYPE_MAP.put(SERVICE_TYPE_MAINTENANCE, BcgogoI18N.getMessageByKey("service.type.mainteance", sessionLocale));
    SERVICE_TYPE_MAP.put(SERVICE_TYPE_SALES, BcgogoI18N.getMessageByKey("service.type.sales", sessionLocale));
    SERVICE_TYPE_MAP.put(SERVICE_TYPE_WASH, BcgogoI18N.getMessageByKey("service.type.wash", sessionLocale));
    return SERVICE_TYPE_MAP;
  }

  //剩余油量
  public static final Map FUEL_NUMBER_MAP = new LinkedHashMap();
  public static final String FUEL_NUMBER_ONE = "1";                        //FuelMeter.EMPTY
  public static final String FUEL_NUMBER_TWO = "2";                        //FuelMeter.ONEQT
  public static final String FUEL_NUMBER_THREE = "3";                      //FuelMeter.HALF
  public static final String FUEL_NUMBER_FOUR = "4";                       //FuelMeter.THREEQT
  public static final String FUEL_NUMBER_FIVE = "5";                       //FuelMeter.FULL

  public static Map<String, String> getFuelNumberMap(Locale sessionLocale) {        //FuelMeter.getLocaleMap(locale)
    FUEL_NUMBER_MAP.put(FUEL_NUMBER_ONE, BcgogoI18N.getMessageByKey("fuel.number.one", sessionLocale));
    FUEL_NUMBER_MAP.put(FUEL_NUMBER_TWO, BcgogoI18N.getMessageByKey("fuel.number.two", sessionLocale));
    FUEL_NUMBER_MAP.put(FUEL_NUMBER_THREE, BcgogoI18N.getMessageByKey("fuel.number.three", sessionLocale));
    FUEL_NUMBER_MAP.put(FUEL_NUMBER_FOUR, BcgogoI18N.getMessageByKey("fuel.number.four", sessionLocale));
    FUEL_NUMBER_MAP.put(FUEL_NUMBER_FIVE, BcgogoI18N.getMessageByKey("fuel.number.five", sessionLocale));
    return FUEL_NUMBER_MAP;
  }

  //维修事件提醒类型            1:待交付2:缺料待修3:还款4:短信，5:来料待修  //短信未用到
  public static final Long REPAIR_REMIND_EVENT_PENDING = 1l;              //RepairRemindEventTypes.PENDING
  public static final Long REPAIR_REMIND_EVENT_LACK = 2l;                 //RepairRemindEventTypes.LACK
  public static final Long REPAIR_REMIND_EVENT_DEBT = 3l;                 //RepairRemindEventTypes.DEBT
  public static final Long REPAIR_REMIND_EVENT_FINISH = 4l;               //RepairRemindEventTypes.FINISH
  public static final Long REPAIR_REMIND_EVENT_INCOMING = 5l;             //RepairRemindEventTypes.INCOMING

  //更多客户信息
  public static final Map INVOICE_CATAGORY = new LinkedHashMap();
  public static final String INVOICE_CATAGORY_NORMAL = "1";               //CustInfoInvoice.NORMAL    普通发票
  public static final String INVOICE_CATAGORY_INCREMENT = "2";            //CustInfoInvoice.VAT      增值税发票
  public static final String INVOICE_CATAGORY_INNER = "3";                //CustInfoInvoice.INNER     内部
  public static final String INVOICE_CATAGORY_OTHER = "4";                //CustInfoInvoice.OTHER     其他

  public static Map<String, String> getInvoiceCatagoryMap(Locale sessionLocale) {           //CustInfoInvoice.getLocaleMap
    INVOICE_CATAGORY.put(INVOICE_CATAGORY_NORMAL, BcgogoI18N.getMessageByKey("customer.invoicecategory.normal", sessionLocale));
    INVOICE_CATAGORY.put(INVOICE_CATAGORY_INCREMENT, BcgogoI18N.getMessageByKey("customer.invoicecategory.increment", sessionLocale));
    INVOICE_CATAGORY.put(INVOICE_CATAGORY_INNER, BcgogoI18N.getMessageByKey("customer.invoicecategory.inner", sessionLocale));
    INVOICE_CATAGORY.put(INVOICE_CATAGORY_OTHER, BcgogoI18N.getMessageByKey("customer.invoicecategory.other", sessionLocale));
    return INVOICE_CATAGORY;
  }

  public static final Map AREA = new LinkedHashMap();
  public static final String AREA_LOCAL = "1";                     //CustInfoArea.LOCAL
  public static final String AREA_NONLOCAL = "2";                  //CustInfoArea.NONLOCAL

  public static Map<String, String> getAreaMap(Locale sessionLocale) {           //CustInfoArea.getLocaleMap
    AREA.put(AREA_LOCAL, BcgogoI18N.getMessageByKey("customer.area.local", sessionLocale));
    AREA.put(AREA_NONLOCAL, BcgogoI18N.getMessageByKey("customer.area.nonlocal", sessionLocale));
    return AREA;
  }

  public static final Map CUSTOMER_TYPE = new LinkedHashMap();
  public static final String CUSTOMER_TYPE_NORMAL = "1";          //CustomerTypes.NORMAL
  public static final String CUSTOMER_TYPE_UNIT = "2";            //CustomerTypes.UNIT
  public static final String CUSTOMER_TYPE_BIGCUSTOMER = "3";    //CustomerTypes.BIG
  public static final String CUSTOMER_TYPE_OTHER = "4";           //CustomerTypes.OTHER

  public static Map<String, String> getCustomerTypeMap(Locale sessionLocale) {        //CustomerTypes.getLocaleMap
    CUSTOMER_TYPE.put(CUSTOMER_TYPE_NORMAL, BcgogoI18N.getMessageByKey("customer.type.normal", sessionLocale));
    CUSTOMER_TYPE.put(CUSTOMER_TYPE_UNIT, BcgogoI18N.getMessageByKey("customer.type.unit", sessionLocale));
    CUSTOMER_TYPE.put(CUSTOMER_TYPE_BIGCUSTOMER, BcgogoI18N.getMessageByKey("customer.type.bigcustomer", sessionLocale));
    CUSTOMER_TYPE.put(CUSTOMER_TYPE_OTHER, BcgogoI18N.getMessageByKey("customer.type.other", sessionLocale));
    return CUSTOMER_TYPE;
  }

  //使用时请注意于下面的ordertype保持一致
  public static final Long ORDER_TYPE_REPAIR = 4l;			 //OrderTypes.REPAIR
  public static final Long ORDER_TYPE_SALE = 3l;			//OrderTypes.SALE
//  public static final Long ORDER_TYPE_PURCHASE = 3l;		 //OrderTypes.PURCHASE

  public static final Map SETTLEMENT_TYPE = new LinkedHashMap();
  public static final String SETTLEMENT_TYPE_CASH = "1";            //CustInfoSettleType.CASH   现金
  public static final String SETTLEMENT_TYPE_MONTH = "2";           //CustInfoSettleType.MONTH   月结
  public static final String SETTLEMENT_TYPE_ARRIVE = "3";          //CustInfoSettleType.ARRIVAL  货到付款
  public static final String SETTLEMENT_TYPE_QUARTER = "4";         //CustInfoSettleType.QUARTER    季付

  public static Map<String, String> getSettlementTypeMap(Locale sessionLocale) {       //CustInfoSettleType.getLocaleMap
    SETTLEMENT_TYPE.put(SETTLEMENT_TYPE_CASH, BcgogoI18N.getMessageByKey("settlement.type.cash", sessionLocale));
    SETTLEMENT_TYPE.put(SETTLEMENT_TYPE_MONTH, BcgogoI18N.getMessageByKey("settlement.type.month", sessionLocale));
    SETTLEMENT_TYPE.put(SETTLEMENT_TYPE_ARRIVE, BcgogoI18N.getMessageByKey("settlement.type.arrive", sessionLocale));
    SETTLEMENT_TYPE.put(SETTLEMENT_TYPE_QUARTER, BcgogoI18N.getMessageByKey("settlement.type.quarter", sessionLocale));
    return SETTLEMENT_TYPE;
  }

  public class OrderType {
    public static final String ORDER_TYPE_PURCHASE = "1";           //OrderTypes.PURCHASE
    public static final String ORDER_TYPE_INVENTORY = "2";          //OrderTypes.INVENTORY
    public static final String ORDER_TYPE_SALE = "3";                //OrderTypes.SALE
    public static final String ORDER_TYPE_REPAIR = "4";              //OrderTypes.REPAIR
    public static final String ORDER_TYPE_WASH = "5";                //OrderTypes.WASH
    public static final String ORDER_TYPE_RETURN = "8";            //OrderTypes.RETURN
    public static final String ORDER_TYPE_SALE_MEMBER_CARD = "10";
    public static final String ORDER_TYPE_WASH_BEAUTY = "11";
    public static final String ORDER_TYPE_RETURN_MEMBER_CARD = "13";

  }

  public class OrderTypeValue {
    public static final String ORDER_TYPE_PURCHASE = "采购单";
    public static final String ORDER_TYPE_INVENTORY = "入库单";
    public static final String ORDER_TYPE_SALE = "销售单";
    public static final String ORDER_TYPE_REPAIR = "施工单";
    public static final String ORDER_TYPE_WASH = "洗车单";
    public static final String ORDER_TYPE_RETURN = "退货单";
    public static final String ORDER_TYPE_DEBT = "欠款结算单";
    public static final String ORDER_TYPE_PAYABLE = "应付款结算单";
    public static final String ORDER_TYPE_DEPOSIT = "定金结算单";
    public static final String ORDER_TYPE_BUSINESSSTAT = "营收统计单";
    public static final String ORDER_TYPE_WASH_SMALL_TICKET = "洗车小票";             //OrderTypes.WASH_TICKET
    public static final String ORDER_TYPE_MEMBER_CARD_ORDER = "会员购卡续卡单";
    public static final String ORDER_TYPE_WASH_BEAUTY = "洗车美容单";
  }

  public static final String ITEM_TYPE_MEMBER_CARD_ORDER_SERVICE = "12";

  public static final String PWD_DEFAULT_RENDER_STR = "^^^^^^";

  public class OrderStatusInIntemIndex {
    public static final String ITEMINDEX_ORDERSTATUS_DFAULT = "1";//采购单下单未交付    OrderStatus.PURCHASE_ORDER_WAITING
    public static final String ITEMINDEX_ORDERSTATUS_FINISH = "2";//采购单已经入库      OrderStatus.PURCHASE_ORDER_DONE
    public static final String ITEMINDEX_ORDERSTATUS_REPEAL = "3";//采购单已经作废s     OrderStatus.PURCHASE_ORDER_REPEAL
  }


  public static Map<Integer, String> REPAIR_ORDER_STATUS_MAP = null;
  public static Map<String, String> ORDER_TYPE_MAP = null;

  public static Map<String, String> REPAIR_ORDER_STATUS_STRING_MAPPING = null;

  public static Map<String, String> sortCommandMap = null;  //库存查询、商品首页、库存明细中商品车型的排序状态

  public static Map<String,String> sortCommandMap_DB = null;//库存查询，商品首页，库存告急中用到的search.inventorySearchIndex中 排序

  static {
    REPAIR_ORDER_STATUS_MAP = new HashMap<Integer, String>();
    REPAIR_ORDER_STATUS_MAP.put(1, "施工中");                    //OrderStatus.REPAIR_DISPATCH
    REPAIR_ORDER_STATUS_MAP.put(2, "已完工");                    //OrderStatus.REPAIR_DONE
    REPAIR_ORDER_STATUS_MAP.put(3, "已结算");                    //OrderStatus.REPAIR_SETTLED
    REPAIR_ORDER_STATUS_MAP.put(4, "已作废");                    //OrderStatus.REPAIR_REPEAL

    REPAIR_ORDER_STATUS_STRING_MAPPING = new HashMap<String, String>();
    REPAIR_ORDER_STATUS_STRING_MAPPING.put("1", "施工中");
    REPAIR_ORDER_STATUS_STRING_MAPPING.put("2", "已完工");
    REPAIR_ORDER_STATUS_STRING_MAPPING.put("3", "已结算");
    REPAIR_ORDER_STATUS_STRING_MAPPING.put("4", "已作废");


    ORDER_TYPE_MAP = new HashMap<String, String>();
    ORDER_TYPE_MAP.put("1", "采购单");
    ORDER_TYPE_MAP.put("2", "入库单");
    ORDER_TYPE_MAP.put("3", "销售单");
    ORDER_TYPE_MAP.put("4", "维修美容");
    ORDER_TYPE_MAP.put("5", "洗车");
    ORDER_TYPE_MAP.put("8", "退货单");
    ORDER_TYPE_MAP.put("7", "洗车卡充值");                 //OrderTypes.RECHARGE
    ORDER_TYPE_MAP.put("9", "洗车");
    ORDER_TYPE_MAP.put("10","购卡续卡");

    sortCommandMap = new HashMap<String, String>();
    sortCommandMap.put("commodityCodeAsc", "commodity_code asc");
    sortCommandMap.put("commodityCodeDesc", "commodity_code desc");
    sortCommandMap.put("nameAsc", "product_name_fl_sort asc,product_name_exact asc");
    sortCommandMap.put("nameDesc", "product_name_fl_sort desc,product_name_exact desc");
    sortCommandMap.put("brandAsc", "product_brand_fl_sort asc,product_brand_exact asc");
    sortCommandMap.put("brandDesc", "product_brand_fl_sort desc,product_brand_exact desc");
    sortCommandMap.put("specAsc", "product_spec_fl_sort asc,product_spec_exact asc");
    sortCommandMap.put("specDesc", "product_spec_fl_sort desc,product_spec_exact desc");
    sortCommandMap.put("modelAsc", "product_model_fl_sort asc,product_model_exact asc");
    sortCommandMap.put("modelDesc", "product_model_fl_sort desc,product_model_exact desc");
    sortCommandMap.put("vehicleModelAsc", "product_vehicle_model_fl_sort asc,product_vehicle_model_exact asc");
    sortCommandMap.put("vehicleModelDesc", "product_vehicle_model_fl_sort desc,product_vehicle_model_exact desc");
    sortCommandMap.put("inventoryAmountDesc", "inventory_amount desc");
    sortCommandMap.put("inventoryAmountAsc", "inventory_amount asc");

    sortCommandMap.put("storageTimeDesc", "storage_time desc");
    sortCommandMap.put("storageTimeAsc", "storage_time asc");

    sortCommandMap.put("last30SalesDesc", "last_30_sales desc");
    sortCommandMap.put("last30SalesAsc", "last_30_sales asc");

    sortCommandMap.put("inventoryAveragePriceDesc", "inventory_average_price desc");
    sortCommandMap.put("inventoryAveragePriceAsc", "inventory_average_price asc");
    sortCommandMap.put("inSalesAmountDesc", "in_sales_amount desc");
    sortCommandMap.put("inSalesAmountAsc", "in_sales_amount asc");
    sortCommandMap.put("lastInSalesTimeAsc", "last_in_sales_time asc");
    sortCommandMap.put("lastInSalesTimeDesc", "last_in_sales_time desc");
    sortCommandMap.put("tradePriceDesc", "trade_price desc");
    sortCommandMap.put("tradePriceAsc", "trade_price asc");
    sortCommandMap.put("inSalesPriceDesc", "in_sales_price desc");
    sortCommandMap.put("inSalesPriceAsc", "in_sales_price asc");
    sortCommandMap.put("recommendedPriceDesc", "recommendedprice desc");
    sortCommandMap.put("recommendedPriceAsc", "recommendedprice asc");
    sortCommandMap.put("vehicleBrandAsc", "product_vehicle_brand_fl_sort asc,product_vehicle_brand_exact asc");
    sortCommandMap.put("vehicleBrandDesc", "product_vehicle_brand_fl_sort desc,product_vehicle_brand_exact desc");
    sortCommandMap.put("productKindAsc", "product_kind_fl_sort asc,product_kind_exact asc");
    sortCommandMap.put("productKindDesc", "product_kind_fl_sort desc,product_kind_exact desc");
    sortCommandMap.put("shopNameDesc", "shop_name desc");
    sortCommandMap.put("shopNameAsc", "shop_name asc");

    sortCommandMap.put("vehicleLastConsumeTimeDesc", "vehicle_last_consume_time desc");
    sortCommandMap.put("vehicleLastConsumeTimeAsc", "vehicle_last_consume_time asc");

    sortCommandMap.put("vehicleMaintainTimeDesc", "maintain_time desc");
    sortCommandMap.put("vehicleMaintainTimeAsc", "maintain_time asc");

    sortCommandMap.put("vehicleInsureTimeDesc", "insure_time desc");
    sortCommandMap.put("vehicleInsureTimeAsc", "insure_time asc");
    sortCommandMap.put("vehicleCreatedTimeDesc", "created_time desc");
    sortCommandMap.put("vehicleCreatedTimeAsc", "created_time asc");



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
    sortCommandMap_DB.put("lowerLimit","lowerLimit");
    sortCommandMap_DB.put("upperLimit","upperLimit");

//    sortCommandMap_DB.put("nameAsc", "product_name asc");
//    sortCommandMap_DB.put("nameDesc", "product_name desc");
//    sortCommandMap_DB.put("brandAsc", "product_brand asc");
//    sortCommandMap_DB.put("brandDesc", "product_brand desc");
//    sortCommandMap_DB.put("specAsc", "product_spec asc");
//    sortCommandMap_DB.put("specDesc", "product_spec desc");
//    sortCommandMap_DB.put("modelAsc", "product_model asc");
//    sortCommandMap_DB.put("modelDesc", "product_model desc");
//    sortCommandMap_DB.put("vehicleModelAsc", "model asc");
//    sortCommandMap_DB.put("vehicleModelDesc", "model desc");
//    sortCommandMap_DB.put("lowerLimit","lowerLimit");
//    sortCommandMap_DB.put("upperLimit","upperLimit");
  }

  public static final Long REPAIR_ORDER_STATUS_INVOICE = 1l; //派单        //OrderStatus.REPAIR_DISPATCH
  public static final Long REPAIR_ORDER_STATUS_DONE = 2l;    //完工        //OrderStatus.REPAIR_DONE
  public static final Long REPAIR_ORDER_STATUS_SETTLED = 3l; //结算       //OrderStatus.REPAIR_SETTLED    OrderStatus.WASH_SETTLED
  public static final Long REPAIR_ORDER_STATUS_REPEAL = 4l;               //OrderStatus.REPAIR_REPEAL     OrderStatus.WASH_REPEAL

  public static final String WASH_ORDER_STATUS = "已结算";                 //OrderStatus.WASH_SETTLED
  public static final String SALE_ORDER_STATUS = "已结算";                 //OrderStatus.SALE_DONE
  public static final String SALE_ORDER_REPEAL = "已作废";                 //OrderStatus.SALE_REPEAL

	//采购单状态
  public static final Long PURCHASE_ORDER_TYPE_WAITING = 1L;              //OrderStatus.PURCHASE_ORDER_WAITING
  public static final Long PURCHASE_ORDER_TYPE_DONE = 2L;                 //OrderStatus.PURCHASE_ORDER_DONE
  public static final Long PURCHASE_ORDER_TYPE_CANCELED = 3L;            //OrderStatus.PURCHASE_ORDER_REPEAL
  public static final Long SELLER_STOCK = 4L;            //OrderStatus.SELLER_STOCK("卖家备货中"),
  public static final Long SELLER_DISPATCH = 5L;            //OrderStatus.SELLER_DISPATCH("卖家已发货"),
  public static final Long PURCHASE_SELLER_STOP = 6L;            //OrderStatus.PURCHASE_SELLER_STOP("卖家终止销售"),


    //入库单状态
  public static final Long PURCHASE_INVENTORY_STATUS_DONE = 1l;   //已经入库      //OrderStatus.PURCHASE_INVENTORY_DONE
  public static final Long PURCHASE_INVENTORY_STATUS_CANCELED=2l; //入库单取消    //OrderStatus.PURCHASE_INVENTORY_REPEAL

  public static final String PURCHASE_INVENTORY_MESSAGE_SHORTAGE="库存不足无法作废";
  public static final String PURCHASE_INVENTORY_MESSAGE_CANCELED="该单据已经作废";

     //退货单状态
  public static final Long PURCHASE_RETURN_DONE = 1l;   //已经退货
   public static final Long PURCHASE_RETURN_REPEAL = 2l;   //已经退货
    //销售单状态
  public static final Long SALE_ORDER_TYPE_FINISH = 1L;                //OrderStatus.SALE_DONE
  public static final Long SALE_ORDER_TYPE_CANCELED = 2L;              //OrderStatus.SALE_REPEAL
	public static final Long STOCKING =3L;                                //OrderStatus.STOCKING("备货中"),
	public static final Long DISPATCH =4L;//              //OrderStatus.DISPATCH("已发货"),
	public static final Long SELLER_STOP =5L;//              //OrderStatus.SELLER_STOP("终止销售"),
	public static final Long SALE_DEBT_DONE =6L;//              //OrderStatus.SALE_DEBT_DONE("欠款结算"),

	//共用状态
	public static final Long PENDING =7L;//              //OrderStatus.PENDING("待处理"),  销售单，销售退货单待处理
	public static final Long SELLER_PENDING =8L;//              //OrderStatus.SELLER_PENDING("待卖家处理"), 采购单，退货单，待卖家处理
	public static final Long STOP =9L;//              //OrderStatus.STOP("买家终止交易")    //采购单待处理，作废销售单状态，退货单待处理作废
	public static final Long SELLER_REFUSED =10L;//              //OrderStatus.SELLER_REFUSED("卖家已拒绝"), //采购单，退货单被拒绝的状态


  //老版本洗车单在washorder中orderType类型
  public static final Long BUY_WASH_CARD = 0L;
  //购卡单状态
  public static final Long MEMBERCARD_ORDER_STATUS = 3L;

  public class TextSymbol {
    public static final String ETC = "...";
    public static final String PAUSE_MARK = "、";
    public static final String COMMA = "，";
    public static final String SEMICOLON = "；";
  }

  //五种单子 店员没有填写时，保存到orderIndex表和solr中默认为(未填写)

  public static final String ASSISTANT_NAME = "未填写";


  public class UnitStatus{
    public static final String UNIT_IS_BLANK ="0";                  //库存单位，销售单位均为空时    UnitStatus.BLANK
    public static final String UNIT_IS_PRODUCT_STOAGE_UNIT = "1";//同时存在库存单位，销售单位，且使用库存大单位状态   UnitStatus.USE_STORAGE_UNIT
    public static final String UNIT_IS_PRODUCT_SELL_UNIT = "2";//同时存在库存单位，销售单位，且使用销售小单位    UnitStatus.USE_SELL_UNIT
    public static final String UNIT_STATUS_ERROR ="3";         //用户使用单位异常                UnitStatus.ERROR
  }

  public class ReceivableStatus{
    public static final long RECEIVABLE_STATUS_FINISH = 0;       //ReceivableStatus.FINISH
    public static final long RECEIVABLE_STATUS_REPEAL = 1;       //ReceivableStatus.REPEAL
  }

  public class DebtStatus{
    public static final String DEBT_STATUS_ARREARS = "欠款";	//DebtStatus.ARREARS
    public static final String DEBT_STATUS_SETTLE = "结清";		//DebtStatus.SETTLED
    public static final String DEBT_STATUS_REPEAL = "作废";		//DebtStatus.REPEAL
  }

  //库存不足，超出上限
  public static final String INVENTORY_LOWER_LIMIT = "lowerLimit";
  public static final String INVENTORY_UPPER_LIMIT = "upperLimit";

  //进销存首页，商品查询首页，生成单据默认数量
  public static final Double ORDER_DEFAULT_AMOUNT = 1D;

  //库存统计在memcache 中存放更新时间          todo 需要抽取到一个memcache参数枚举类中,优先以memcache 为主，如果memcache未配置，或者配置出错再读取这个值
  public static final Long MEMCACHE_INVENTORY_COUNT_ACTIVETIME = 20 * 60 * 1000L;
  //solr 查询库存金额时，遍历每次分页的大小
  public static final Integer SOLR_INVENTORY_SUM_MAXROWS = 50;

  public static final String  TIME_CARD= "timeCard";//计次卡

  public static final String VALUE_CARD = "valueCard";//储值卡

  public static final String CARD_STATUS_DISABLED = "disabled";//卡无效状态

  public static final String CARD_STATUS_ENABLED = "enabled";//卡有效状态

  public static final String NO_PRINT_TEMPLATE = "<html><head><title></title></head><body>没有可用的模板</body><html>";

  public static final Integer UNLIMITED = -1;//会员上用来表示无限的意思

  public static final String MEMBER_CONFIG_SWITCH = "on";//config表里会员总开关

  public static final String SHOP_PLAN_TOTALROWS = "totalRows";

  public static final String SHOP_PLAN_ACTIVITY_NO_EXPIRED= "activityNoExpired";

  public static final String SHOP_PLAN_ACTIVITY_EXPIRED="activityExpired";

  public static final String SHOP_PLAN_REMINDED = "reminded";

  public static final String ALL_PERSON = "所有联系人";

  public static final String ALL_CUSTOMER = "所有客户";

  public static final String ALL_SUPPLIER = "所有供应商";

  public static final String ALL_PHONE_CONTACTS = "所有手机联系人";

  public static final String ALL_MEMBER = "会员";

  public static final String PURCHASE_ORDER_FIELD_CREATED = "created";
  public static final String PURCHASE_ORDER_FIELD_INVENTORY_VEST_DATE = "inventory_vest_date";

}

