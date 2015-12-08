package com.bcgogo.config.cache;

import com.bcgogo.config.model.Config;
import com.bcgogo.config.model.ConfigDaoManager;
import com.bcgogo.config.model.ConfigWriter;
import com.bcgogo.config.util.MemCacheAdapter;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Hashtable;
import java.util.List;
import java.util.Set;

/**
 * 管理本地config缓存
 * Created by IntelliJ IDEA.
 * User: ZouJianhong
 * Date: 12-5-21
 * Time: 下午5:08
 * To change this template use File | Settings | File Templates.
 */
public class ConfigCacheManager {
  private static final Logger LOG = LoggerFactory.getLogger(ConfigCacheManager.class);

  private static final String CACHE_KEY_INVERVAL = "_";
  private static final String CACHE_KEY_PREFIX = "config_";

  public static Long SYNC_INTERVAL = 60000L;

  private static Hashtable<String, Config> cachedConfig = new Hashtable();

  /**
   * 根据name和shopId获得config配置项
   *
   * @param name
   * @param shopId
   * @return
   */
  public static Config getConfig(String name, Long shopId) {
    String key = getKey(name, shopId);
    Config config = cachedConfig.get(key);
//    if (LOG.isDebugEnabled()) {
//      if (config == null)
//        LOG.debug("Local cache config is null.");
//      else
//        LOG.debug("Local cache config " + config.getName() + " value is " + config.getValue() + ".");
//    }
    if (config == null) {
      return null;
    }
    if (System.currentTimeMillis() - config.getSyncTime() >= SYNC_INTERVAL) {
      String value = (String) MemCacheAdapter.get(key);
      if (NumberUtil.longValue(value, 0L) > config.getSyncTime()) {
        refreshSingle(name, shopId);
        config = cachedConfig.get(key);
      }
      config.setSyncTime(System.currentTimeMillis());
    }
    return config;
  }

  /**
   * 新增或更新一个config配置项
   *
   * @param name
   * @param value
   * @param shopId
   * @return
   */
  public static boolean setConfig(String name, String value, Long shopId) {
    if (StringUtil.isEmpty(name) || shopId == null) {
      return false;
    }
    Config config = new Config();
    config.setName(name);
    config.setValue(value);
    config.setShopId(shopId);
    config.setSyncTime(System.currentTimeMillis());
    cachedConfig.put(getKey(name, shopId), config);
    return true;
  }

  /**
   * 新增或更新一个config配置项
   *
   * @param config
   * @return
   */
  public static boolean setConfig(Config config) {
    if (config == null) {
      return false;
    }
    return setConfig(config.getName(), config.getValue(), config.getShopId());
  }

  /**
   * 移除一个config配置项
   *
   * @param name
   * @param shopId
   * @return
   */
  public static boolean removeConfig(String name, Long shopId) {
    if (StringUtil.isEmpty(name) || shopId == null) {
      return false;
    }
    cachedConfig.remove(getKey(name, shopId));
    return true;
  }

  /**
   * 本地内存移除一个config配置项
   *
   * @param config
   * @return
   */
  public static boolean removeConfig(Config config) {
    if (config == null) {
      return false;
    }
    return removeConfig(config.getName(), config.getShopId());
  }

  /**
   * 本地内存移除所有配置项
   *
   * @return
   */
  public static boolean removeAll() {
    Set<String> keys = cachedConfig.keySet();
    if (keys == null || keys.isEmpty()) {
      return true;
    }
    for (String key : keys) {
      cachedConfig.remove(key);
    }
    return true;
  }

  private static String getKey(String name, Long shopId) {
    return CACHE_KEY_PREFIX + StringUtil.truncValue(name) + CACHE_KEY_INVERVAL + StringUtil.truncValue(String.valueOf(shopId));
  }

  /**
   * 添加一个配置项到本地内存中
   *
   * @param config
   * @return
   */
  public static boolean addConfig(Config config) {
    if (config == null) {
      return false;
    }
    return setConfig(config);
  }

  /**
   * 本地内存刷新一个配置项
   *
   * @param config
   * @return
   */
  public static boolean refreshSingle(Config config) {
    if (config == null) {
      return false;
    }
    refreshSingle(config.getName(), config.getShopId());
    return true;
  }

  /**
   * 本地内存刷新一个配置项
   *
   * @param name
   * @param shopId
   * @return
   */
  public static boolean refreshSingle(String name, Long shopId) {
    if (StringUtil.isEmpty(name) || shopId == null) {
      return false;
    }
    ConfigDaoManager configDaoManager = ServiceManager.getService(ConfigDaoManager.class);
    ConfigWriter writer = configDaoManager.getWriter();
    Config config = writer.getConfig(name, shopId);
    setConfig(config);
    return true;
  }

  /**
   * 本地内存刷新多个配置项
   *
   * @param configList
   * @return
   */
  public static boolean refreshMulti(List<Config> configList) {
    if (configList == null || configList.isEmpty()) {
      return false;
    }
    for (Config config : configList) {
      if (config == null) {
        continue;
      }
      setConfig(config);
    }
    return true;
  }

  /**
   * 本地内存刷新所有配置项
   *
   * @return
   */
  public static boolean refreshAll() {
    ConfigDaoManager configDaoManager = ServiceManager.getService(ConfigDaoManager.class);
    ConfigWriter writer = configDaoManager.getWriter();
    List<Config> configList = writer.getAllConfig(-1);
    if (configList == null || configList.isEmpty()) {
      return true;
    }
    if (!refreshMulti(configList)) {
      return false;
    }
    return true;
  }

}
