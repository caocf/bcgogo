package com.bcgogo.config.service;

import com.bcgogo.config.cache.ShopConfigCacheManager;
import com.bcgogo.config.dto.ShopConfigDTO;
import com.bcgogo.config.model.ConfigDaoManager;
import com.bcgogo.config.model.ConfigWriter;
import com.bcgogo.config.model.ShopConfig;
import com.bcgogo.config.util.MemCacheAdapter;
import com.bcgogo.constant.MemcachePrefix;
import com.bcgogo.enums.ShopConfigScene;
import com.bcgogo.enums.ShopConfigStatus;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.utils.ShopConstant;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: cfl
 * Date: 12-8-6
 * Time: 下午3:31
 * To change this template use File | Settings | File Templates.
 */
@Component
public class ShopConfigService implements IShopConfigService{
  private static final Logger LOG = LoggerFactory.getLogger(ShopConfigService.class);

  @Override
  public ShopConfig setShopConfig(Long shopId ,ShopConfigScene scene ,ShopConfigStatus switchStatus)
  {
    ConfigWriter writer = configDaoManager.getWriter();
    Object status = writer.begin();
    try {
      ShopConfig shopConfig = writer.getShopConfig(scene, shopId);
      if (null == shopConfig) {
        shopConfig = new ShopConfig();
        shopConfig.setScene(scene);
        shopConfig.setShopId(shopId);
        shopConfig.setStatus(switchStatus);
      } else {
        shopConfig.setStatus(switchStatus);
      }
      writer.saveOrUpdate(shopConfig);
      writer.commit(status);
      ShopConfigCacheManager.addShopConfig(shopConfig);
      MemCacheAdapter.set(MemcachePrefix.shopConfig.getValue()+scene.toString()+shopId,shopConfig);
      //在Memcached中添加变更标记
      MemCacheAdapter.set(shopConfig.assembleKey(), String.valueOf(System.currentTimeMillis()));

      return shopConfig;
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public boolean saveOrUpdateShopConfig(ShopConfigDTO configDTO){
    ConfigWriter writer = configDaoManager.getWriter();
    Object status = writer.begin();
    try {
      ShopConfig shopConfig=new ShopConfig();
      if (configDTO.getId()!=null) {
        shopConfig = writer.getById(ShopConfig.class, configDTO.getId());
        if(shopConfig==null) return false;
      }
      shopConfig.fromDTO(configDTO);
      writer.saveOrUpdate(shopConfig);
      writer.commit(status);
//      ShopConfigCacheManager.removeShopConfigMapElem(configDTO.getShopId());
      return true;
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public ShopConfigDTO getShopConfigDTOByShopIdAndScene(Long shopId,ShopConfigScene scene){
    ConfigDaoManager configDaoManager = ServiceManager.getService(ConfigDaoManager.class);
    ConfigWriter writer = configDaoManager.getWriter();
    ShopConfig shopConfig=writer.getShopConfig(scene, shopId);
    return shopConfig!=null?shopConfig.toDTO():null;
  }

  @Override
  public ShopConfigStatus getConfigSwitchStatus(ShopConfigScene scene,Long shopId)
  {
    ShopConfig shopConfig = ShopConfigCacheManager.getShopConfig(shopId,scene);
    if (null != shopConfig)
    {
      return shopConfig.getStatus();
    }

    shopConfig = (ShopConfig)MemCacheAdapter.get(MemcachePrefix.shopConfig.getValue()+scene.toString()+shopId);

    if(null != shopConfig)
    {
      ShopConfigCacheManager.addShopConfig(shopConfig);
      return shopConfig.getStatus();
    }

    ConfigWriter writer = configDaoManager.getWriter();
    shopConfig = writer.getShopConfig(scene, shopId);
    if (null != shopConfig)
    {
      ShopConfigCacheManager.addShopConfig(shopConfig);
      MemCacheAdapter.set(MemcachePrefix.shopConfig.getValue()+scene.toString()+shopId,shopConfig);
      return shopConfig.getStatus();
    }
    //如果没有店铺个性化配置，读取config中默认配置。
    String defaultConfig = ServiceManager.getService(IConfigService.class).getConfig(scene.toString(),-1L);
    if(StringUtils.isNotBlank(defaultConfig)){
      if(ShopConfigStatus.ON.toString().equals(defaultConfig.toUpperCase())){
        shopConfig = setShopConfig(shopId,scene, ShopConfigStatus.ON);
      }else {
        shopConfig = setShopConfig(shopId,scene, ShopConfigStatus.OFF);
      }
    }else {
      shopConfig = setShopConfig(shopId,scene, ShopConfigStatus.ON);
    }
    return shopConfig.getStatus();
  }

  @Override
  public  boolean isStorageBinSwitchOn(Long shopId) {
    return isShopConfigSceneSwitchOn(ShopConfigScene.STORAGE_BIN, shopId);
  }

  @Override
  public boolean isTradePriceSwitchOn(Long shopId, Long shopVersionId) {
    ShopConfigStatus status ;
    ShopConfig shopConfig = ShopConfigCacheManager.getShopConfig(shopId,ShopConfigScene.TRADE_PRICE);
    if (null != shopConfig) {
      status = shopConfig.getStatus();
      return ShopConfigStatus.ON.equals(status);
    }
    shopConfig = (ShopConfig) MemCacheAdapter.get(MemcachePrefix.shopConfig.getValue() + ShopConfigScene.TRADE_PRICE.toString() + shopId);
    if (null != shopConfig) {
      ShopConfigCacheManager.addShopConfig(shopConfig);
      status = shopConfig.getStatus();
      return ShopConfigStatus.ON.equals(status);
    }
    ConfigWriter writer = configDaoManager.getWriter();
    shopConfig = writer.getShopConfig(ShopConfigScene.TRADE_PRICE, shopId);
    if (null != shopConfig) {
      ShopConfigCacheManager.addShopConfig(shopConfig);
      MemCacheAdapter.set(MemcachePrefix.shopConfig.getValue() + ShopConfigScene.TRADE_PRICE.toString() + shopId, shopConfig);
      status = shopConfig.getStatus();
      return ShopConfigStatus.ON.equals(status);
    }
    String defaultConfig = getDefaultConfig(shopId, shopVersionId, ShopConfigScene.TRADE_PRICE);
    if (StringUtils.isNotBlank(defaultConfig)) {
      if (ShopConfigStatus.ON.toString().equals(defaultConfig.toUpperCase())) {
        shopConfig = setShopConfig(shopId, ShopConfigScene.TRADE_PRICE, ShopConfigStatus.ON);
      } else {
        shopConfig = setShopConfig(shopId, ShopConfigScene.TRADE_PRICE, ShopConfigStatus.OFF);
      }
    } else {
      shopConfig = setShopConfig(shopId, ShopConfigScene.TRADE_PRICE, ShopConfigStatus.OFF);
    }
    status = shopConfig.getStatus();
    return ShopConfigStatus.ON.equals(status);
  }

  private String getDefaultConfig (Long shopId,Long shopVersionId,ShopConfigScene shopConfigScene){
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    String defaultConfig = ShopConfigStatus.OFF.name();
    if(ShopConfigScene.TRADE_PRICE.equals(shopConfigScene)){
      String wholesalerShopVersionIds =  configService.getConfig("WholesalerShopVersions", ShopConstant.BC_SHOP_ID);
      if(StringUtils.isNotEmpty(wholesalerShopVersionIds) && shopVersionId != null &&wholesalerShopVersionIds.contains(shopVersionId.toString())){
        defaultConfig = ShopConfigStatus.ON.name();
      }
    } else {
      defaultConfig = configService.getConfig(shopConfigScene.toString(),ShopConstant.BC_SHOP_ID);
    }
    if(StringUtils.isEmpty(defaultConfig)){
      defaultConfig = ShopConfigStatus.OFF.name();
    }
    return defaultConfig;
  }

  @Override
  public boolean isShopConfigSceneSwitchOn(ShopConfigScene shopConfigScene, Long shopId) {
    if (shopId != null) {
      ShopConfigStatus status = getConfigSwitchStatus(shopConfigScene, shopId);
      if (status != null && ShopConfigStatus.ON.equals(status)) {
        return true;
      } else {
        return false;
      }
    } else {
      return false;
    }
  }

  /**
   * 根据shopid和scene查询shopConfig信息，
   * 当shopId为空时候，查询此scene的全部信息
   * @param shopId
   * @param scene
   * @return
   */
  @Override
  public List<ShopConfigDTO> searchShopConfigDTOByShopAndScene(Long shopId,ShopConfigScene scene,Integer startPageNo, Integer maxRows)
  {
    ConfigWriter writer = configDaoManager.getWriter();

    List<ShopConfig> shopConfigs = writer.searchShopConfigDTOByShopAndScene(shopId,scene,startPageNo,maxRows);

    if(CollectionUtils.isEmpty(shopConfigs))
    {
      return null;
    }

    List<ShopConfigDTO> shopConfigDTOs = new ArrayList<ShopConfigDTO>();

    for(ShopConfig shopConfig : shopConfigs)
    {
      shopConfigDTOs.add(shopConfig.toDTO());
    }

    return shopConfigDTOs;
  }

  public int countShopConfigByScene(Long shopId,ShopConfigScene scene)
  {
    ConfigWriter writer = configDaoManager.getWriter();

    return writer.countShopConfigByScene(shopId,scene);
  }

  @Autowired
  private ConfigDaoManager configDaoManager;
}
