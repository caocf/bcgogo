package com.bcgogo.config.cache;

import com.bcgogo.config.model.ShopConfig;
import com.bcgogo.constant.MemcachePrefix;
import com.bcgogo.enums.ShopConfigScene;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: cfl
 * Date: 12-8-21
 * Time: 下午4:09
 * To change this template use File | Settings | File Templates.
 */
public class shopConfigCache {
  private static final Logger LOG = LoggerFactory.getLogger(shopConfigCache.class);

  private static final String CACHE_KEY_INVERVAL = "_";

  private static final String CACHE_KEY_PREFIX = "shop_config_";

  private static LinkedList<String> memberSwitchLinkedList = new LinkedList<String>();

  private static LinkedList<String> storageBinSwitchLinkedList = new LinkedList<String>();

  private static LinkedList<String> tradePriceSwitchLinkedList = new LinkedList<String>();

  private static Hashtable<String, ShopConfig> memberCachedShopConfig = new Hashtable();

  private static Hashtable<String, ShopConfig> storageBinCachedShopConfigTable = new Hashtable();

  private static Hashtable<String, ShopConfig> tradePriceCachedShopConfigTable = new Hashtable();

  public static Map<String,ShopConfig> shopConfigMap=new HashMap<String, ShopConfig>();

  public static LinkedList getLinkedListByScene(ShopConfigScene scene)
  {
    if (scene == ShopConfigScene.MEMBER) {
      return memberSwitchLinkedList;
    }
    if (ShopConfigScene.STORAGE_BIN.equals(scene)) {
      return storageBinSwitchLinkedList;
    }
    if (ShopConfigScene.TRADE_PRICE.equals(scene)) {
      return tradePriceSwitchLinkedList;
    }
    return null;
  }

  public static Hashtable<String,ShopConfig> getCacheShopConfigByScene(ShopConfigScene scene)
  {
    if(scene== ShopConfigScene.MEMBER)
    {
      return memberCachedShopConfig;
    }
    if(ShopConfigScene.STORAGE_BIN.equals(scene)){
      return  storageBinCachedShopConfigTable;
    }
    if(ShopConfigScene.TRADE_PRICE.equals(scene)){
      return tradePriceCachedShopConfigTable;
    }
    return null;
  }

  public static void removeShopConfigMapElem(Long shopId){
    try{
      if(MapUtils.isEmpty(shopConfigMap)||shopId==null) return;
      String key = getKey(ShopConfigScene.WX_WELCOME_WORD, shopId);
      shopConfigMap.remove(key);
    }catch (Exception e){
      LOG.error(e.getMessage(),e);
    }
  }

  protected static String getKey(ShopConfigScene scene, Long shopId) {
    return CACHE_KEY_PREFIX + StringUtil.truncValue(scene.toString()) + CACHE_KEY_INVERVAL + StringUtil.truncValue(String.valueOf(shopId));
  }

  protected static String getMemCacheKey(ShopConfigScene scene, Long shopId) {
    return MemcachePrefix.shopConfigSyncTime.getValue() + StringUtil.truncValue(scene.toString()) + CACHE_KEY_INVERVAL + StringUtil.truncValue(String.valueOf(shopId));
  }

}
