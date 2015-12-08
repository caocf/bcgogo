package com.bcgogo.config.service;

import com.bcgogo.config.dto.ShopAuditLogDTO;
import com.bcgogo.config.model.ShopAuditLog;
import com.bcgogo.enums.shop.AuditStatus;

import java.util.List;

/**
 * User: ZhangJuntao
 * Date: 13-3-30
 * Time: 下午5:19
 */
public interface IShopAuditLogService {

  ShopAuditLog createShopAuditLog(ShopAuditLog shopAuditLog);

  List<ShopAuditLogDTO> getShopAuditLogDTOListByShopIdAndStatus(Long shopId, AuditStatus auditStatus);

}
