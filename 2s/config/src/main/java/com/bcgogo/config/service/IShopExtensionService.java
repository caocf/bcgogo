package com.bcgogo.config.service;

import com.bcgogo.config.dto.ShopBargainRecordDTO;
import com.bcgogo.config.dto.ShopExtensionLogDTO;
import com.bcgogo.enums.shop.BargainStatus;

import java.util.List;

/**
 * User: ZhangJuntao
 * Date: 13-3-30
 * Time: 下午5:19
 */
public interface IShopExtensionService {

  void createShopExtensionLog(long shopId, int extensionDays, long operatorId, String reason);

  List<ShopExtensionLogDTO> getShopExtensionLogs(long shopId);

  Boolean hasShopExtensionLogs(long shopId);
}
