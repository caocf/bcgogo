package com.bcgogo.config.service;

import com.bcgogo.api.ApiResponse;
import com.bcgogo.config.dto.AreaDTO;
import com.bcgogo.config.dto.ShopAdAreaDTO;
import com.bcgogo.enums.app.AreaType;
import com.bcgogo.user.dto.Node;

import java.util.List;
import java.util.Set;

/**
 * User: ZhangJuntao
 * Date: 13-11-4
 * Time: 下午5:00
 */
public interface IAreaService {
  ApiResponse obtainAppShopArea(Long provinceId, AreaType type);

  ApiResponse obtainJuheSupportArea();

  Set<String> getJuheCityCodeByBaiduCityCode(Integer[] baiduCityCodes);

  Set<Long> getAreaNoByJuheCityCode(String... juheCityCodes);

  List<ShopAdAreaDTO> getShopAdAreaDTOsByShopId(Long shopId);

  Node getShopAdAreaScopeByShopId(Long shopId);

  Node getCheckedShopAdAreaScope(Set<Long> ids);

  void saveOrUpdateShopAdArea(Long id, Set<Long> shopAdAreaIds);

  Node getShopRecommendScopeByShopId(Long shopId);

  Node getCheckedShopRecommendScope(Set<Long> ids);

  void saveOrUpdateShopRecommend(Long id, Set<Long> recommendIds);

  void saveOrUpdateArea(AreaDTO... areaDTOs);

}

