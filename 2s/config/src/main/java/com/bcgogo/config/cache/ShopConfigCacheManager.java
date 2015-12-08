package com.bcgogo.config.cache;

import com.bcgogo.config.model.ConfigDaoManager;
import com.bcgogo.config.model.ConfigWriter;
import com.bcgogo.config.model.ShopConfig;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.util.MemCacheAdapter;
import com.bcgogo.constant.MemcachePrefix;
import com.bcgogo.enums.ConcurrentScene;
import com.bcgogo.enums.ShopConfigScene;
import com.bcgogo.enums.ShopConfigStatus;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: cfl
 * Date: 12-8-6
 * Time: 下午3:41
 * To change this template use File | Settings | File Templates.
 */
public class ShopConfigCacheManager extends shopConfigCache{
  private static final Logger LOG = LoggerFactory.getLogger(ShopConfigCacheManager.class);

  public static Long SYNC_INTERVAL = 60000L;


  private static final int SHOP_CONFIG_CACHE_SIZE = 10000;

  private static Object lock = new Object();
  private static Object lockLinkListRemove = new Object();
  private static Object lookRemoveShopConfigLinkListRemove = new Object();


  /**
   *  缓存到内存,区别于getShopConfig缓存方式
   * @param shopId
   * @param scene
   * @return
   */
  public static ShopConfig getConfig(Long shopId,ShopConfigScene scene) {
    if(shopId==null||scene==null) return null;
//    String key = getKey(scene, shopId);
//    ShopConfig config=shopConfigCache.shopConfigMap.get(key);
//    if(config==null){
//      config=getDBShopConfig(shopId,scene);
//      shopConfigCache.shopConfigMap.put(key,config);
//    }
    return  getDBShopConfig(shopId,scene);
  }


  /**
   * 根据scene和shopId获得shop_config配置项
   *
   * @param shopId
   * @param scene
   * @return
   */
  public static ShopConfig getShopConfig(Long shopId,ShopConfigScene scene) {
    Hashtable<String,ShopConfig> cachedShopConfig = getCacheShopConfigByScene(scene);
    if(null == cachedShopConfig)  return null;
    String key = getKey(scene, shopId);
    ShopConfig shopConfig = cachedShopConfig.get(key);
    if (shopConfig == null) return null;
    LinkedList linkedList = getLinkedListByScene(scene);
    if(null != linkedList){
      synchronized(lockLinkListRemove){
        linkedList.remove(key);
        linkedList.addLast(key);
      }
    }
    if (System.currentTimeMillis() - shopConfig.getSyncTime() >= SYNC_INTERVAL) {
      String value = (String) MemCacheAdapter.get(getMemCacheKey(scene,shopId));
      if (NumberUtil.longValue(value, 0L) > shopConfig.getSyncTime()) {
        refreshSingle(scene, shopId);
        shopConfig = cachedShopConfig.get(key);
      }
    }
    return shopConfig;
  }

  /**
   * 本地内存刷新一个配置项
   *
   * @param scene
   * @param shopId
   * @return
   */
  public static boolean refreshSingle(ShopConfigScene scene, Long shopId) {
    if (null == scene || shopId == null) {
      return false;
    }
    ShopConfig shopConfig=getDBShopConfig(shopId,scene);
    if(shopConfig!=null){
      setShopConfig(shopConfig);
      return true;
    }else {
      return false;
    }
  }

  public static ShopConfig getDBShopConfig(Long shopId,ShopConfigScene scene){
    ConfigDaoManager configDaoManager = ServiceManager.getService(ConfigDaoManager.class);
    ConfigWriter writer = configDaoManager.getWriter();
    return writer.getShopConfig(scene, shopId);
  }

  /**
   * 新增或更新一个shop_config配置项
   *
   * @param shopConfig
   * @return
   */
  public static boolean setShopConfig(ShopConfig shopConfig) {
    if (shopConfig == null) {
      return false;
    }
    return setShopConfig(shopConfig.getScene(), shopConfig.getStatus(), shopConfig.getShopId());
  }

  /**
   * 新增或更新一个shop_config配置项
   *
   * @param scene
   * @param switchStatus
   * @param shopId
   * @return
   */
  public static boolean setShopConfig(ShopConfigScene scene, ShopConfigStatus switchStatus, Long shopId) {
    if (null == scene || shopId == null) {
      return false;
    }
    Hashtable<String,ShopConfig> cachedShopConfig = getCacheShopConfigByScene(scene);
    LinkedList linkedList = getLinkedListByScene(scene);
    synchronized(lock){
      if(null != linkedList && null == cachedShopConfig.get(getKey(scene, shopId))
        && SHOP_CONFIG_CACHE_SIZE == linkedList.size()){
        cachedShopConfig.remove(linkedList.getFirst());
        linkedList.remove(linkedList.getFirst());
        linkedList.addLast(getKey(scene, shopId));
      }
    }
    ShopConfig shopConfig = new ShopConfig();
    shopConfig.setScene(scene);
    shopConfig.setStatus(switchStatus);
    shopConfig.setShopId(shopId);
    shopConfig.setSyncTime(System.currentTimeMillis());
    cachedShopConfig.put(getKey(scene, shopId), shopConfig);
    return true;
  }

  /**
   * 移除一个shop_config配置项
   *
   * @param scene
   * @param shopId
   * @return
   */
  public static boolean removeShopConfig(ShopConfigScene scene, Long shopId) {
    if (null == scene || shopId == null) {
      return false;
    }
    Hashtable<String,ShopConfig> cachedShopConfig = getCacheShopConfigByScene(scene);
    cachedShopConfig.remove(getKey(scene, shopId));
    LinkedList linkedList = getLinkedListByScene(scene);
    if(null != linkedList)
    {
      synchronized (lookRemoveShopConfigLinkListRemove)
      {
        linkedList.remove(getKey(scene, shopId));
      }

    }
    return true;
  }

  /**
   * 本地内存移除一个shop_config配置项
   *
   * @param shopConfig
   * @return
   */
  public static boolean removeShopConfig(ShopConfig shopConfig) {
    if (shopConfig == null) {
      return false;
    }
    return removeShopConfig(shopConfig.getScene(), shopConfig.getShopId());
  }


  /**
   * 本地内存按场景移除所有配置项
   *
   * @return
   */
  public static boolean removeAllByScene(ShopConfigScene scene) {
    Hashtable<String,ShopConfig> cachedShopConfig = getCacheShopConfigByScene(scene);
    Set<String> keys = cachedShopConfig.keySet();
    if (keys == null || keys.isEmpty()) {
      return true;
    }
    for (String key : keys) {
      cachedShopConfig.remove(key);
    }

    LinkedList linkedList = getLinkedListByScene(scene);
    if(null != linkedList)
    {
      linkedList.removeAll(linkedList);
    }


    return true;
  }

  /**
   * 本地内存移除所有场景配置项
   */

  public static boolean removeAll() {

    for(ShopConfigScene scene : ShopConfigScene.values())
    {
      removeAllByScene(scene);
    }

    return true;
  }


  /**
   * 添加一个配置项到本地内存中
   *
   * @param shopConfig
   * @return
   */
  public static boolean addShopConfig(ShopConfig shopConfig) {
    if (shopConfig == null) {
      return false;
    }
    return setShopConfig(shopConfig);
  }

  /**
   * 本地内存刷新一个配置项
   *
   * @param shopConfig
   * @return
   */
  public static boolean refreshSingle(ShopConfig shopConfig) {
    if (shopConfig == null) {
      return false;
    }
    refreshSingle(shopConfig.getScene(), shopConfig.getShopId());
    return true;
  }

  /**
   * 本地内存刷新多个配置项
   *
   * @param shopConfigList
   * @return
   */
  public static boolean refreshMulti(List<ShopConfig> shopConfigList) {
    if (shopConfigList == null || shopConfigList.isEmpty()) {
      return false;
    }
    for (ShopConfig shopConfig : shopConfigList) {
      if (shopConfig == null) {
        continue;
      }
      setShopConfig(shopConfig);
    }
    return true;
  }




}
