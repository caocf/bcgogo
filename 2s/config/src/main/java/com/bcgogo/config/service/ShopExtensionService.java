package com.bcgogo.config.service;

import com.bcgogo.config.dto.ShopBargainRecordDTO;
import com.bcgogo.config.dto.ShopExtensionLogDTO;
import com.bcgogo.config.model.*;
import com.bcgogo.enums.shop.BargainStatus;
import com.bcgogo.enums.shop.ShopOperateTaskScene;
import com.bcgogo.enums.shop.ShopState;
import com.bcgogo.enums.shop.ShopStatus;
import com.bcgogo.service.ServiceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: zhangjuntao
 * Date: 13-3-30
 * Time: 下午5:23
 */
@Component
public class ShopExtensionService implements IShopExtensionService {
  private static final Logger LOG = LoggerFactory.getLogger(ShopExtensionService.class);
  private static final Long DAY_MILLISECOND = 86400000L;

  @Autowired
  private ConfigDaoManager configDaoManager;

  @Override
  public void createShopExtensionLog(long shopId, int extensionDays, long operatorId, String reason) {
    ConfigWriter writer = configDaoManager.getWriter();
    Object status = writer.begin();
    try {
      Shop shop = writer.getById(Shop.class, shopId);
      if (ShopStatus.REGISTERED_TRIAL != shop.getShopStatus()) {
        LOG.warn("shop status is {},can't bu extension.", shop.getShopStatus());
        return;
      }
      if (shop.getTrialEndTime() == null) {
        LOG.warn("shop trial end time is null.");
        shop.setTrialEndTime(System.currentTimeMillis() + 15 * DAY_MILLISECOND);
      } else {
        ShopExtensionLog log = new ShopExtensionLog(shopId, extensionDays, operatorId, reason);
        log.setTrialEndTime(shop.getTrialEndTime() + extensionDays * DAY_MILLISECOND);
        shop.setTrialEndTime(log.getTrialEndTime());
        if (shop.getTrialEndTime() > System.currentTimeMillis() && ShopState.OVERDUE == shop.getShopState()){
          shop.setShopState(ShopState.ACTIVE);
          ServiceManager.getService(IShopService.class).createShopOperationTask(writer, ShopOperateTaskScene.ENABLE_REGISTERED_PAID_SHOP,shopId);
        }

        writer.save(log);
      }
      writer.update(shop);

      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public List<ShopExtensionLogDTO> getShopExtensionLogs(long shopId) {
    ConfigWriter writer = configDaoManager.getWriter();
    List<ShopExtensionLog> logs = writer.getShopExtensionLogs(shopId);
    List<ShopExtensionLogDTO> dtoList = new ArrayList<ShopExtensionLogDTO>();
    for (ShopExtensionLog log : logs) {
      dtoList.add(log.toDTO());
    }
    return dtoList;
  }

  @Override
  public Boolean hasShopExtensionLogs(long shopId) {
    ConfigWriter writer = configDaoManager.getWriter();
    return writer.countShopExtensionLogs(shopId) > 0;
  }


}
