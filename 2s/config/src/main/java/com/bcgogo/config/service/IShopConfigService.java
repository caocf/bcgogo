package com.bcgogo.config.service;

import com.bcgogo.config.dto.ShopConfigDTO;
import com.bcgogo.config.model.ShopConfig;
import com.bcgogo.enums.ShopConfigScene;
import com.bcgogo.enums.ShopConfigStatus;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: cfl
 * Date: 12-8-6
 * Time: 下午3:29
 * To change this template use File | Settings | File Templates.
 */
public interface IShopConfigService {
  public ShopConfig setShopConfig(Long shopId,ShopConfigScene scene,ShopConfigStatus status);

  /**
   * 获得一个个性化开关的值，首先从MemCacheKey中取，取不到去shopconfig表中去，取不到去config表中取默认值。
   * @param scene
   * @param shopId
   * @return
   */
  public ShopConfigStatus getConfigSwitchStatus(ShopConfigScene scene,Long shopId);

  boolean saveOrUpdateShopConfig(ShopConfigDTO configDTO);

  ShopConfigDTO getShopConfigDTOByShopIdAndScene(Long shopId,ShopConfigScene scene);

  public  boolean isStorageBinSwitchOn(Long shopId);

  public boolean isShopConfigSceneSwitchOn(ShopConfigScene shopConfigScene, Long shopId);

  public List<ShopConfigDTO> searchShopConfigDTOByShopAndScene(Long shopId, ShopConfigScene scene, Integer startPageNo, Integer maxRows);

  public int countShopConfigByScene(Long shopId, ShopConfigScene scene);


  boolean isTradePriceSwitchOn(Long shopId, Long shopVersionId);
}
