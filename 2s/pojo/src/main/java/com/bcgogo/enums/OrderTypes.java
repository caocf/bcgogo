package com.bcgogo.enums;

import com.bcgogo.utils.BcgogoI18N;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Jimuchen
 * Date: 12-6-12
 * Time: 下午5:51
 * To change this template use File | Settings | File Templates.
 */
public enum OrderTypes {
  PURCHASE("采购单"),
  INVENTORY("入库单"),
  SALE("销售单"),
  REPAIR("施工单"),
  REPAIR_SECONDARY("施工附表"),
  REPAIR_SALE("材料销售单"),
  WASH("洗车单"),
  RETURN("入库退货单"),
  SALE_RETURN("销售退货单"),
  ALL("所有单据"),
  //会员相关
  WASH_MEMBER("会员洗车单"),
  RECHARGE("会员充值单"),
  MEMBER_BUY_CARD("会员购卡续卡"),
  MEMBER_RETURN_CARD("会员退卡"),
  ORDER_TYPE_MEMBER_CARD_ORDER("购卡续卡"),
  WASH_BEAUTY("洗车美容单"),

  //对账单
  CUSTOMER_STATEMENT_ACCOUNT("客户对账单"),
  SUPPLIER_STATEMENT_ACCOUNT("供应商对账单"),
  CUSTOMER_STATEMENT_DEBT("客户对账结算"),
  SUPPLIER_STATEMENT_DEBT("供应商对账结算"),

  //预约单
  APPOINT_ORDER("预约单"),
  //询价单
  ENQUIRY_ORDER("询价单"),

  //打印专用
  DEBT("欠款结算单"),
  PAYABLE("应付款结算单"),
  DEPOSIT("定金结算单"),
  BIZSTAT("营收统计单"),
  WASH_TICKET("洗车小票"),
  WASH_AUTO_TICKET("云打印洗车小票"),
  INVENTORY_PRINT("库存打印单"),
  PAYABLE_STATISTICAL("应付统计打印单"),
  RECEIVABLE_STATISTICAL("应收统计打印单"),
  BIZSTAT_SALES_DETAIL("营业统计销售详情"),
  BIZSTAT_REPAIR_DETAIL("营业统计施工详情"),
  BIZSTAT_WASH_DETAIL("营业统计洗车详情"),
  BUSINESS_MEMBER_CARD_ORDER("会员购卡记录统计单"),
  BUSINESS_MEMBER_CONSUME("会员消费记录统计单"),
  BUSINESS_MEMBER_RETURN("会员退卡记录单"),
  RUNNING_DAY_INCOME("流水日收入统计打印"),
  RUNNING_MONTH_INCOME("流水月收入统计打印"),
  RUNNING_YEAR_INCOME("流水年收入统计打印"),
  RUNNING_DAY_EXPEND("流水日支出统计打印"),
  RUNNING_MONTH_EXPEND("流水月支出统计打印"),
  RUNNING_YEAR_EXPEND("流水年支出统计打印"),
  SALE_RETURN_BUSINESS_STATISTICS("销售退货统计单"),
  INVENTORY_RETURN_BUSINESS_STATISTICS("入库退货统计单"),
  CUSTOMER_SUPPLIER_STATEMENT_ACCOUNT("对账单打印"),
  ASSISTENT_STAT("员工提成统计"),
  ASSISTENT_MEMBER_CARD_STAT("会员卡业绩统计"),
  ASSISTENT_SERVICE_STAT("车辆施工业绩统计"),
  ASSISTENT_WASH_STAT("洗车施工业绩统计"),
  ASSISTENT_PRODUCT_STAT("商品销售业绩统计"),
  //bcgogo
  BCGOGO_SOFTWARE_RECEIVABLE_ORDER("BCGOGO软件收款单"),
  BCGOGO_HARDWARE_RECEIVABLE_ORDER("BCGOGO硬件收款单"),
  BCGOGO_SMS_RECHARGE_RECEIVABLE_ORDER("BCGOGO短信收款单"),
  ASSISTANT_BUSINESS_ACCOUNT_STAT("营业外记账业绩统计"),


  //营业外记账
  BUSINESS_ACCOUNT("营业外记账"),
  INVENTORY_CHECK("库存盘点"),
  CUSTOMER_BUSINESS_STATISTICS("客户交易统计单"),
  SUPPLIER_BUSINESS_STATISTICS("供应商交易统计单"),
  PRODUCT_CATEGORY_SALES_STATISTICS("商品分类销售额统计"),
  BUSINESS_CATEGORY_SALES_STATISTICS("营业分类销售额统计"),
  SERVICE_SALES_STATISTICS("服务/施工内容统计"),
  PRE_PAY("预付款单"),
  PRE_RECEIVE("预收款单"),
  PRE_PAY_STATISTICS("预付款统计"),
  PRE_RECEIVE_STATISTICS("预收款统计"),


  REPAIR_PICKING("维修领料"),
  INNER_PICKING("内部领料"),
  INNER_RETURN("内部退料"),
  ALLOCATE_RECORD("仓库调拨"),
  BORROW_ORDER("借调单"),
  RETURN_ORDER("借调归还单"),
  INSURANCE("保险理赔"),
  INSURANCE_PREVIEW("保险理赔预览单"),

  PRE_BUY_ORDER("预购"),
  QUOTED_PRE_BUY_ORDER("预购报价"),
  QUALIFIED_CREDENTIAL("合格证"),
  PENDING_PURCHASE_ORDER("采购订单"), //批发商店铺看到的对方采购单
  REPAIR_DRAFT_ORDER("采购单草稿"), //采购单草稿，预约单上用
  ENQUIRY("询价单"), //询价单

  APP_ONLINE_ORDER("在线交易"),
  APP_ONFIELD_ORDER("线下交易"),
  APP_GIVE_ORDER("代金券发放")
  ;





  private static Map<String, OrderTypes> lookup = new HashMap<String, OrderTypes>();
  private static Map<String,  OrderTypes> enumNameMap = new HashMap<String, OrderTypes>();
  static{
    for(OrderTypes orderTypes : OrderTypes.values()){
      lookup.put(orderTypes.getName(), orderTypes);
      enumNameMap.put(orderTypes.name(),orderTypes);
    }
  }

  private final String name;
  private OrderTypes(String name){
    this.name=name;
  }

  public String getName() {
    return name;
  }

  public static String[] getInquiryCenterOrderTypes() {
    List<String> list = new ArrayList<String>();
    list.add(PURCHASE.toString());
    list.add(INVENTORY.toString());
    list.add(SALE.toString());
    list.add(REPAIR.toString());
    list.add(WASH_BEAUTY.toString());
    list.add(RETURN.toString());
    list.add(SALE_RETURN.toString());
    list.add(MEMBER_BUY_CARD.toString());
    list.add(MEMBER_RETURN_CARD.toString());
    return list.toArray(new String[list.size()]);
  }

  public static String[] getCustomerConsumeOrder() {
    List<String> list = new ArrayList<String>();
    list.add(SALE.toString());
    list.add(REPAIR.toString());
    list.add(WASH_BEAUTY.toString());
//    list.add(MEMBER_BUY_CARD.toString());
    return list.toArray(new String[list.size()]);
  }

  public static OrderTypes parseName(String orderType) {
    OrderTypes orderTypes = lookup.get(orderType);
    if(orderTypes == null){
      throw new IllegalArgumentException("OrderType " + orderType +"不存在!");
    }
    return orderTypes;
  }

  public static OrderTypes parseEnum(String orderType) {
    return enumNameMap.get(orderType);
  }



  public static Map<OrderTypes, String> getServicesLocaleMap(Locale locale){
    Map<OrderTypes, String> map = new LinkedHashMap<OrderTypes, String>();
    List<OrderTypes> serviceTypes = new ArrayList<OrderTypes>();
    serviceTypes.add(OrderTypes.REPAIR);
    serviceTypes.add(OrderTypes.SALE);
    serviceTypes.add(OrderTypes.WASH);
    for(OrderTypes serviceType:serviceTypes){
      map.put(serviceType, BcgogoI18N.getMessageByKey("ServiceTypes_" + serviceType.toString(), locale));
    }
    return map;
  }

}
