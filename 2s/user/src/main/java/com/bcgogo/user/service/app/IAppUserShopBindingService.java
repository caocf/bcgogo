package com.bcgogo.user.service.app;

import com.bcgogo.api.ShopBindingDTO;
import com.bcgogo.api.ShopBindingInfo;
import com.bcgogo.common.Result;
import com.bcgogo.user.model.UserWriter;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * User: ZhangJuntao
 * Date: 13-12-10
 * Time: 下午5:38
 */
public interface IAppUserShopBindingService {
  Map<Long, Long> getVehicleIdBindingShopIdMap(String userNo, Long... vehicleId);

  Long getBindingShopId(String userNo, Long vehicleId);

  Set<Long> getBindingShopIds(String userNo);

  List<ShopBindingInfo> getBindingShop(String userNo);

  Result binding(ShopBindingDTO bindingDTO, UserWriter writer);

  Result binding(ShopBindingDTO bindingDTO);

  void unbinding(String appUserNo, Long vehicleId, Long shopId);

  void unbinding(String appUserNo, Long vehicleId, UserWriter writer);

  void unbinding(String appUserNo, Long vehicleId, Long shopId, UserWriter writer);

  Map<Long, Long> getShopBindingVehicleIdShopIdMap(String appUserNo, Set<Long> vehicleIdSet);

}
