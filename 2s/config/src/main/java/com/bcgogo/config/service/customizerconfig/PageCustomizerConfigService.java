package com.bcgogo.config.service.customizerconfig;

import com.bcgogo.config.CustomizerConfigInfo;
import com.bcgogo.config.CustomizerConfigResult;
import com.bcgogo.config.dto.PageCustomizerConfigDTO;
import com.bcgogo.config.model.ConfigDaoManager;
import com.bcgogo.config.model.ConfigWriter;
import com.bcgogo.config.model.PageCustomizerConfig;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.util.ConfigUtils;
import com.bcgogo.config.util.MemCacheAdapter;
import com.bcgogo.constant.MemcachePrefix;
import com.bcgogo.enums.config.PageCustomizerConfigScene;
import com.bcgogo.enums.config.PageCustomizerConfigShopId;
import com.bcgogo.enums.config.PageCustomizerConfigStatus;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.utils.CollectionUtil;
import com.bcgogo.utils.ShopConstant;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: ZhangJuntao
 * Date: 13-5-24
 * Time: 上午11:47
 */
@Component
public class PageCustomizerConfigService implements IPageCustomizerConfigService {
  private static final Logger LOG = LoggerFactory.getLogger(PageCustomizerConfigService.class);
  @Autowired
  private ConfigDaoManager configDaoManager;

  @Override
  public <T> void updatePageCustomizerConfig(Long shopId, PageCustomizerConfigDTO<T> dto,
                                             IPageCustomizerConfigContentParser<T> parser) {
    ConfigWriter writer = configDaoManager.getWriter();
    Object status = writer.begin();
    if (parser == null) {
      LOG.warn("updatePageCustomizerConfig parse is null.");
      return;
    }
    try {
      PageCustomizerConfig customizerConfig;
      dto.setContent(parser.parseDtoToJson(dto.getContentDto()));
      if (dto.getId() == null) {
        customizerConfig = new PageCustomizerConfig(dto, false);
        writer.save(customizerConfig);
        dto.setId(customizerConfig.getId());
      } else {
        customizerConfig = writer.getById(PageCustomizerConfig.class, dto.getId());
        if (!dto.getContent().equals(customizerConfig.getContent())) {
          //非默认shopId
          if (PageCustomizerConfigShopId.isNotSystemShopId(customizerConfig.getShopId())) {
            customizerConfig.setStatus(PageCustomizerConfigStatus.DELETED);
            writer.saveOrUpdate(customizerConfig);
          }
          customizerConfig = new PageCustomizerConfig(dto, false);
          customizerConfig.setShopId(shopId);
          writer.save(customizerConfig);
          dto.setId(customizerConfig.getId());
        }
      }
      writer.commit(status);
      resetMemCache(shopId);
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public <T> PageCustomizerConfigDTO<T> getPageCustomizerConfig(Long shopId, PageCustomizerConfigScene scene) {
    //config 开关
    if (!ConfigUtils.isCustomizerConfigOpen()) {
      return getPageCustomizerConfigByShopId(PageCustomizerConfigShopId.DEFAULT.getValue(), scene);
    }
    PageCustomizerConfigDTO<T> configDTO = this.getPageCustomizerConfigByShopId(shopId, scene);
    if (configDTO == null) {
      return getPageCustomizerConfigByShopId(PageCustomizerConfigShopId.DEFAULT.getValue(), scene);
    }
    return configDTO;
  }

  @Override
  public void deletePageCustomizerConfigByShopId(Long shopId) {
    MemCacheAdapter.delete(getMemCachePageConfigListKey(shopId));
    ConfigWriter writer = configDaoManager.getWriter();
    List<PageCustomizerConfig> customizerConfigList = writer.getPageCustomizerConfigByShopId(shopId);
    Object status = writer.begin();
    try {
      for (PageCustomizerConfig config : customizerConfigList) {
        writer.delete(PageCustomizerConfig.class, config.getId());
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public void restorePageConfig(Long shopId, PageCustomizerConfigScene scene) {
    ConfigWriter writer = configDaoManager.getWriter();
    List<PageCustomizerConfig> customizerConfigList = writer.getPageCustomizerConfigByShopId(shopId);
    for (PageCustomizerConfig config : customizerConfigList) {
      if (scene == config.getScene()) {
        Object status = writer.begin();
        try {
          config = writer.getById(PageCustomizerConfig.class, config.getId());
          config.setStatus(PageCustomizerConfigStatus.DELETED);
          writer.saveOrUpdate(config);
          writer.commit(status);
        } finally {
          writer.rollback(status);
        }
        break;
      }
    }
    resetMemCache(shopId);
  }

  @Override
  public boolean verifierPageOrderConfigByName(Long shopId, String resultName, String infoName) {
    String mapKey = getMemCachePageConfigMapKey(shopId);
    Map<String, CustomizerConfigInfo> map = (Map<String, CustomizerConfigInfo>) MemCacheAdapter.get(mapKey);
    if (MapUtils.isEmpty(map)) {
      map = new HashMap<String, CustomizerConfigInfo>();
      PageCustomizerConfigDTO<List<CustomizerConfigResult>> customizerConfigDTO = this.getPageCustomizerConfig(shopId, PageCustomizerConfigScene.ORDER);
      for (CustomizerConfigResult result : customizerConfigDTO.getContentDto()) {
        if (result == null || CollectionUtil.isEmpty(result.getConfigInfoList())) return false;
        for (CustomizerConfigInfo info : result.getConfigInfoList()) {
          if (info.getChecked()) map.put(getMapKey(shopId, result.getName(), info.getName()), info);
        }
      }
      MemCacheAdapter.set(mapKey, map);
    }
    CustomizerConfigInfo info = map.get(getMapKey(shopId, resultName, infoName));
    return info != null && info.getChecked();
  }

  private <T> PageCustomizerConfigDTO<T> getPageCustomizerConfigByShopId(Long shopId, PageCustomizerConfigScene scene) {
    List<PageCustomizerConfigDTO<T>> customizerConfigDTOs = (List<PageCustomizerConfigDTO<T>>) MemCacheAdapter.get(getMemCachePageConfigListKey(shopId));
    if (CollectionUtil.isEmpty(customizerConfigDTOs)) {
      customizerConfigDTOs = resetMemCache(shopId);
    }
    if (CollectionUtil.isNotEmpty(customizerConfigDTOs)) {
      for (PageCustomizerConfigDTO<T> dto : customizerConfigDTOs) {
        if (dto.getScene() == scene) return dto;
      }
    }
    return null;
  }

  private <T> List<PageCustomizerConfigDTO<T>> resetMemCache(Long shopId) {
    PageCustomizerConfigDTO<T> dto;
    List<PageCustomizerConfigDTO<T>> customizerConfigDTOs = null;
    ConfigWriter writer = configDaoManager.getWriter();
    List<PageCustomizerConfig> customizerConfigList = writer.getPageCustomizerConfigByShopId(shopId);
    customizerConfigDTOs = new ArrayList<PageCustomizerConfigDTO<T>>();

    for (PageCustomizerConfig config : customizerConfigList) {
      dto = config.toDTO();
      dto.setContentDto(ParserFactory.<T>getParserByName(config.getScene()).parseJsonToDto(config.getContent()));
      customizerConfigDTOs.add(dto);
    }
    MemCacheAdapter.set(getMemCachePageConfigListKey(shopId), customizerConfigDTOs);
    MemCacheAdapter.delete(getMemCachePageConfigMapKey(shopId));
    return customizerConfigDTOs;
  }


  private String getMemCachePageConfigListKey(Long shopId) {
    return MemcachePrefix.pageCustomizerConfig.getValue() + shopId;
  }

  private String getMemCachePageConfigMapKey(Long shopId) {
    return MemcachePrefix.pageCustomizerConfig.getValue() + "map_" + shopId;
  }

  private String getMapKey(Long shopId, String resultName, String infoName) {
    return shopId + "_" + resultName + "_" + infoName;
  }

}
