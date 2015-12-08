package com.bcgogo.enums;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Jimuchen
 * Date: 12-6-13
 * Time: 下午12:38
 * To change this template use File | Settings | File Templates.
 */
public enum OrderStatus {
  REPAIR_DISPATCH("施工中"),              //以前使用 REPAIR_ORDER_STATUS_INVOICE
  REPAIR_CHANGE("改单"),
  REPAIR_DONE("已完工"),
  REPAIR_SETTLED("已结算"),
  REPAIR_REPEAL("已作废"),

  WASH_SETTLED("已结算"),
  WASH_REPEAL("已作废"),

  STATEMENT_ACCOUNTED("已对账"),//所有单据通过对账单进行了对账，单据状态就是已对账 不能作废



  STOCKING("备货中"),
	DISPATCH("已发货"),
  SELLER_STOP("终止销售"),         //销售单备货，发货之后终止交易
	SALE_DONE("已结算"),
	SALE_DEBT_DONE("欠款结算"),
	SALE_REPEAL("已作废"),

  PURCHASE_INVENTORY_DONE("已入库"),
  PURCHASE_INVENTORY_REPEAL("已作废"),

	SELLER_STOCK("卖家备货中"),
	SELLER_DISPATCH("卖家已发货"),
	PURCHASE_SELLER_STOP("卖家终止销售"),         //销售单接受或者发货之后采购单的状态
  PURCHASE_ORDER_WAITING("待入库"),
  PURCHASE_ORDER_DONE("已入库"),
  PURCHASE_ORDER_REPEAL("已作废"),

  MEMBERCARD_ORDER_STATUS("已结算"),



  SETTLED("已结算"),
  REPEAL("已作废"),

  PENDING("待处理"),                  //销售单待处理,领料单待处理
  SELLER_PENDING("待卖家处理"),      //采购单待卖家处理   ，入库退货单待处理
  WAITING_STORAGE("待入库"),
  SELLER_ACCEPTED("卖家已接受"),
  REFUSED("已拒绝"),  //销售单拒绝采购单状态
  SELLER_REFUSED("卖家已拒绝"), //采购单被拒绝的状态
  STOP("买家终止交易"),    //采购单待处理，作废销售单状态

  //领料单Item状态
  OUT_STORAGE("已出库"),
  RETURN_STORAGE("已退料"),
  WAIT_OUT_STORAGE("未出库"),
  WAIT_RETURN_STORAGE("未退料"),


  //保险理赔单状态
  UNSETTLED("待结算"),

  //网上商城交易状态
  APP_ORDER_SUCCESS("交易成功"),
  APP_ORDER_DONE("交易关闭"),
  //订单后台状态
  ADMIN_ORDER_SUBMIT("客户提交"),
  ADMIN_ORDER_CONFIRM("已确认"),

 ;

  private final String name;
  private final String sellerName;

  private static Map<String, OrderStatus> enumValueMap = new HashMap<String, OrderStatus>();
  private static Map<String, OrderStatus> enumNameMap = new HashMap<String, OrderStatus>();
  static{
    for(OrderStatus orderStatus : OrderStatus.values()){
      enumValueMap.put(orderStatus.getName(), orderStatus);
      enumNameMap.put(orderStatus.name(), orderStatus);
    }
  }

  public static OrderStatus parseName(String orderStatus) {
    return enumValueMap.get(orderStatus);
  }

  public static OrderStatus parseEnum(String orderStatus) {
    return enumNameMap.get(orderStatus);
  }


  private OrderStatus(String name){
    this.name = name;
    this.sellerName="";
  }
  private OrderStatus(String name,String sellerName){
    this.name = name;
    this.sellerName = sellerName;
  }

  public String getName(){
    return name;
  }
  public String getSellerName(){
    return sellerName;
  }

}
