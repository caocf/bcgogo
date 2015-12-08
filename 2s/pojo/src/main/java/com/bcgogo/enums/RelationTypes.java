package com.bcgogo.enums;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 13-1-17
 * Time: 下午4:57
 * To change this template use File | Settings | File Templates.
 */
public enum RelationTypes {
  RECOMMEND_RELATED("被申请关联"),   //客户申请供应商关联，供应商店铺下的客户类型。
  APPLY_RELATED("申请关联"),      //客户申请供应商关联，该客户下的供应商关联类型

  REGISTER_RELATED("注册关联"),   //批发商注册客户 客户是注册关联
  BE_REGISTERED("被注册关联"),   //客户的批发商是被注册关联

  INVITE_RELATED("邀请关联"),
  BE_INVITED_RELATED("被邀请关联"),

  UNRELATED("非关联"),
  RELATED("已关联"),
  CUSTOMER_COLLECTION("客户收藏供应商"),
  SUPPLIER_COLLECTION("供应商收藏客户");

  // customerShop-->wholesalerShop  customerShop<-->wholesalerShop  客户收藏供应商或者客户供应商关联
  public static  List<RelationTypes> CUSTOMER_RELATE_TO_WHOLESALER_LIST;
  public static List<String> CUSTOMER_RELATE_TO_WHOLESALER_STRING_LIST;

  // wholesalerShop-->customerShop  wholesalerShop<-->customerShop  供应商收藏客户或者供应商客户关联
  public static List<RelationTypes> WHOLESALER_RELATE_TO_CUSTOMER_LIST;
  public static List<String> WHOLESALER_RELATE_TO_CUSTOMER_STRING_LIST;

  static {
    if (CUSTOMER_RELATE_TO_WHOLESALER_LIST == null) {
      CUSTOMER_RELATE_TO_WHOLESALER_LIST = new ArrayList<RelationTypes>();
      CUSTOMER_RELATE_TO_WHOLESALER_LIST.add(RELATED);
      CUSTOMER_RELATE_TO_WHOLESALER_LIST.add(CUSTOMER_COLLECTION);
    }
    if (CUSTOMER_RELATE_TO_WHOLESALER_STRING_LIST == null) {
      CUSTOMER_RELATE_TO_WHOLESALER_STRING_LIST = new ArrayList<String>();
      CUSTOMER_RELATE_TO_WHOLESALER_STRING_LIST.add(RELATED.name());
      CUSTOMER_RELATE_TO_WHOLESALER_STRING_LIST.add(CUSTOMER_COLLECTION.name());
    }

    if (WHOLESALER_RELATE_TO_CUSTOMER_LIST == null) {
      WHOLESALER_RELATE_TO_CUSTOMER_LIST = new ArrayList<RelationTypes>();
      WHOLESALER_RELATE_TO_CUSTOMER_LIST.add(RELATED);
      WHOLESALER_RELATE_TO_CUSTOMER_LIST.add(SUPPLIER_COLLECTION);
    }
    if (WHOLESALER_RELATE_TO_CUSTOMER_STRING_LIST == null) {
      WHOLESALER_RELATE_TO_CUSTOMER_STRING_LIST = new ArrayList<String>();
      WHOLESALER_RELATE_TO_CUSTOMER_STRING_LIST.add(RELATED.name());
      WHOLESALER_RELATE_TO_CUSTOMER_STRING_LIST.add(SUPPLIER_COLLECTION.name());
    }
  }

  private final String name;

  private RelationTypes(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
}
