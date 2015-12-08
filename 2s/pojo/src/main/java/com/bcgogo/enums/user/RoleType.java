package com.bcgogo.enums.user;

import com.bcgogo.user.dto.permission.ShopVersionDTO;
import org.apache.commons.collections.MapUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * User: ZhangJuntao
 * Date: 12-7-12
 * Time: 下午2:20
 * 特殊 role
 */
public enum RoleType {
  BASE,                    //基本
  SYSTEM,                  //系统级别
  LOGIN_BASE,             //登录后基本权限
  LARGE_WHOLESALER_SHOP_BASE, //大型批发商基本权限
  SMALL_WHOLESALER_SHOP_BASE, //标准批发商基本权限
  WHOLESALER_SHOP_BASE, //批发商基本权限
  INTEGRATED_SHOP_BASE, //综合基本权限
  TXN_SHOP_BASE,         //汽配版基本权限
  WASH_SHOP_BASE,        //微型版基本权限
  ADVANCED_SHOP_BASE,   //高级版基本权限
  FOUR_S_SHOP_BASE,//4S版基本权限
  REPAIR_SHOP_BASE;       //初级版基本权限

  public static boolean isBaseRole(String name) {
    return BASE.toString().equals(name) || LOGIN_BASE.toString().equals(name)
        || LARGE_WHOLESALER_SHOP_BASE.toString().equals(name)
        || WHOLESALER_SHOP_BASE.toString().equals(name)
        || INTEGRATED_SHOP_BASE.toString().equals(name)
        || TXN_SHOP_BASE.toString().equals(name)
        || WASH_SHOP_BASE.toString().equals(name)
        || ADVANCED_SHOP_BASE.toString().equals(name)
        || SMALL_WHOLESALER_SHOP_BASE.toString().equals(name)
        || REPAIR_SHOP_BASE.toString().equals(name)|| FOUR_S_SHOP_BASE.toString().equals(name);
  }

  private static Map<Long, RoleType> lookup = new HashMap<Long, RoleType>();

  static {
    //每个shop version的id
    lookup.put(10000010017531653L, TXN_SHOP_BASE);
    lookup.put(10000010017531654L, INTEGRATED_SHOP_BASE);
    lookup.put(10000010017531655L, REPAIR_SHOP_BASE);
    lookup.put(10000010017531656L, WASH_SHOP_BASE);
    lookup.put(10000010017531657L, WHOLESALER_SHOP_BASE);
    lookup.put(10000010037193619L, LARGE_WHOLESALER_SHOP_BASE);
    lookup.put(10000010039823882L, ADVANCED_SHOP_BASE);
    lookup.put(10000010037193620L, SMALL_WHOLESALER_SHOP_BASE);
    lookup.put(100010010000000L, FOUR_S_SHOP_BASE);

  }

  public static RoleType shopVersionBaseRoleMapping(Long versionId) {
    return lookup.get(versionId);
  }

}
