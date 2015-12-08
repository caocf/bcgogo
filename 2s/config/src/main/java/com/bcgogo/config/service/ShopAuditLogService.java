package com.bcgogo.config.service;

import com.bcgogo.config.dto.ShopAuditLogDTO;
import com.bcgogo.config.model.ConfigDaoManager;
import com.bcgogo.config.model.ConfigWriter;
import com.bcgogo.config.model.ShopAuditLog;
import com.bcgogo.config.model.SmsDonationLog;
import com.bcgogo.enums.shop.AuditStatus;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.utils.CollectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: zhangjuntao
 * Date: 13-3-30
 * Time: 下午5:23
 */
@Component
public class ShopAuditLogService implements IShopAuditLogService {
  private static final Logger LOG = LoggerFactory.getLogger(ShopAuditLogService.class);
  @Autowired
  private ConfigDaoManager configDaoManager;

  @Override
  public ShopAuditLog createShopAuditLog(ShopAuditLog shopAuditLog) {
    ConfigWriter writer = configDaoManager.getWriter();
    Object status = writer.begin();
    try {
      writer.save(shopAuditLog);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
    return null;
  }

  @Override
  public List<ShopAuditLogDTO> getShopAuditLogDTOListByShopIdAndStatus(Long shopId, AuditStatus auditStatus) {
    ConfigWriter writer = configDaoManager.getWriter();
    List<ShopAuditLogDTO> shopAuditLogDTOList = new ArrayList<ShopAuditLogDTO>();
    List<ShopAuditLog> shopAuditLogs = writer.getShopAuditLogDTOListByShopIdAndStatus(shopId,auditStatus);
    if(CollectionUtil.isNotEmpty(shopAuditLogs)) {
      for(ShopAuditLog shopAuditLog : shopAuditLogs) {
        shopAuditLogDTOList.add(shopAuditLog.toDTO());
      }
    }
    return shopAuditLogDTOList;
  }
}
