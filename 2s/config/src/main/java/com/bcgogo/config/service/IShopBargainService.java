package com.bcgogo.config.service;

import com.bcgogo.config.dto.ShopBargainRecordDTO;
import com.bcgogo.enums.shop.BargainStatus;

import java.util.List;
import java.util.Map;

/**
 * User: ZhangJuntao
 * Date: 13-3-30
 * Time: 下午5:19
 */
public interface IShopBargainService {

  void createShopBargainRecord(ShopBargainRecordDTO dto);

  List<ShopBargainRecordDTO> getShopBargainRecordsByShopId(long shopId);

  Map<Long,ShopBargainRecordDTO> getShopAuditPassBargainRecordMapByShopId(Long... shopId);

  void auditShopBargainRecord(long shopId, Long userId, BargainStatus status,String reason) throws Exception;
}
