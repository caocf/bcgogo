package com.bcgogo.enums.shop;

import java.util.HashMap;
import java.util.Map;

/**
 * User: ZhangJuntao
 * Date: 13-9-11
 * Time: 上午11:10
 */
public enum ShopType {
  SHOP_AUTO_REPAIR,
  SHOP_AUTO_PARTS,
  SHOP_4S,;
  private static Map<String,ShopType> shopTypeAndShopVersionMapping = new HashMap<String,ShopType>();

  static {
    shopTypeAndShopVersionMapping.put("TXN_SHOP",SHOP_AUTO_PARTS);
    shopTypeAndShopVersionMapping.put("WHOLESALER_SHOP",SHOP_AUTO_PARTS);
    shopTypeAndShopVersionMapping.put("LARGE_WHOLESALER_SHOP",SHOP_AUTO_PARTS);
    shopTypeAndShopVersionMapping.put("SMALL_WHOLESALER_SHOP",SHOP_AUTO_PARTS);

    shopTypeAndShopVersionMapping.put("INTEGRATED_SHOP",SHOP_AUTO_REPAIR);
    shopTypeAndShopVersionMapping.put("REPAIR_SHOP",SHOP_AUTO_REPAIR);
    shopTypeAndShopVersionMapping.put("WASH_SHOP",SHOP_AUTO_REPAIR);
    shopTypeAndShopVersionMapping.put("ADVANCED_SHOP",SHOP_AUTO_REPAIR);
    shopTypeAndShopVersionMapping.put("FOUR_S_SHOP",SHOP_4S);
  }

  public static ShopType lookupShopType(String shopVersionName) {
     return shopTypeAndShopVersionMapping.get(shopVersionName);
  }
}
