	package com.bcgogo.utils;

/**
 * Created by IntelliJ IDEA.
 * User: zhouxiaochen
 * Date: 12-1-6
 * Time: 上午11:39
 * To change this template use File | Settings | File Templates.
 */
@Deprecated
public class SearchConstant {
  //历史搜索单子的类型
  public static final String HISTORYSEARCH_ORDERTYPE_BUY = "1";//采购                  OrderTypes.PURCHASE
  public static final String HISTORYSEARCH_ORDERTYPE_STORAGE = "2"; //入库            OrderTypes.INVENTORY
  public static final String HISTORYSEARCH_ORDERTYPE_SALE = "3"; //商品销售            OrderTypes.SALE
  public static final String HISTORYSEARCH_ORDERTYPE_MAINTEANCE = "4"; //维修美容     OrderTypes.REPAIR
  public static final String HISTORYSEARCH_ORDERTYPE_WASH = "5"; //洗车                OrderTypes.WASH
  public static final String HISTORYSEARCH_ORDERTYPE_RECHARGED = "7"; //洗车充值       OrderTypes.RECHARGE
  public static final String HISTORYSEARCH_ORDERTYPE_MAINTEANCESALE = "6"; //材料销售  OrderTypes.REPAIR_SALE
  public static final String HISTORYSEARCH_ORDERTYPE_RETURN = "8"; //退货              OrderTypes.RETURN
  public static final String HISTORYSEARCH_ORDERTYPE_MEMBERWASH = "9"; //会员洗车      OrderTypes.WASH_MEMBER
  public static final String HISTORYSEARCH_ORDERTYPE_BUYCARD = "10"; //会员卡购卡续卡   OrderTypes.MEMBER_BUY_CARD
  public static final String HISTORYSEARCH_ORDERTYPE_WASHBEAUTY = "11"; //会员洗车 洗车美容单      OrderTypes.WASH_BEAUTY
  public static final String HISTORYSEARCH_ORDERTYPE_RETURNCARD = "12";   //会员退卡    OrderTypes.MEMBER_RETURN_CARD

  public static final String HISTORYSEARCH_ITEMTYPE_SERVICE = "1"; //服务内容          ItemTypes.SERVICE
  public static final String HISTORYSEARCH_ITEMTYPE_MATERIAL = "2"; //材料内容         ItemTypes.MATERIAL
  public static final String HISTORYSEARCH_ITEMTYPE_WASH = "3"; //洗车                 ItemTypes.WASH
  public static final String HISTORYSEARCH_ITEMTYPE_RECHARGED = "4"; //洗车 充值       ItemTypes.RECHARGE
  public static final String HISTORYSEARCH_ITEMTYPE_MEMBERWASH = "5"; //会员洗车       ItemTypes.WASH_MEMBER
  public static final String HISTORYSEARCH_ITEMTYPE_BUYCARD = "10"; //会员购卡续卡
  public static final String HISTORYSEARCH_ITEMTYPE_BUYCARD_SERVICE ="12";//会员购卡续卡服务变更
  public static final String HISTORYSEARCH_ITEMTYPE_RETURNCARD = "6";
  public static final String HISTORYSEARCH_ITEMTYPE_RETURNCARD_SERVICE = "7";
  public static final String HISTORYSEARCH_ITEMTYPE_OTHER_INCOME = "14";//销售单或者施工单中其他费用类型

  public static final Integer PRODUCT_PRODUCTSTATUS_MULTIPLE = 0; //多款
  public static final Integer PRODUCT_PRODUCTSTATUS_ALL = 1; //通用
  public static final Integer PRODUCT_PRODUCTSTATUS_SPECIAL = 3; //专车专用
  public static final Integer PRODUCT_PRODUCTSTATUS_NULL = 4; //产品类型为空，待定。

  public static final String PRODUCT_PRODUCTSTATUS_MULTIPLE_VALUE = "多款";
  public static final String PRODUCT_PRODUCTSTATUS_ALL_VALUE = "全部";

 // CurrentUsedConstant
  public static final String PRODUCT_NAME = "product_name";   //品名
  public static final String PRODUCT_BRAND = "product_brand";//产品 品牌
  public static final String VEHICLE_BRAND = "brand";         //车辆品牌


  public static final String HISTORYSEARCH_ORDER_STATUS_SAVE = "1";   //派单           OrderStatus.REPAIR_DISPATCH
  public static final String HISTORYSEARCH_ORDER_STATUS_FINISH = "2";//改单            OrderStatus.REPAIR_CHANGE
  public static final String HISTORYSEARCH_ORDER_STATUS_ACCOUNT = "3"; // 交车/结算 结算   OrderStatus.REPAIR_SETTLED

}
