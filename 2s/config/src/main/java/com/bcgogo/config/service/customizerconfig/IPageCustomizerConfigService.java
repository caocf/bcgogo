package com.bcgogo.config.service.customizerconfig;

import com.bcgogo.config.dto.PageCustomizerConfigDTO;
import com.bcgogo.enums.config.PageCustomizerConfigScene;

/**
 * User: ZhangJuntao
 * Date: 13-5-24
 * Time: 下午4:05
 */
public interface IPageCustomizerConfigService {
  <T> void updatePageCustomizerConfig(Long shopId, PageCustomizerConfigDTO<T> customizerConfigDTOs,
                                      IPageCustomizerConfigContentParser<T> parser);

  <T> PageCustomizerConfigDTO<T> getPageCustomizerConfig(Long shopId, PageCustomizerConfigScene scene);

  void deletePageCustomizerConfigByShopId(Long shopId);

  void restorePageConfig(Long shopId, PageCustomizerConfigScene scene);

  boolean verifierPageOrderConfigByName(Long shopId, String resultName, String infoName);
}
