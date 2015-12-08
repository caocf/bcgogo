package com.bcgogo.config.service;

import com.bcgogo.common.Result;
import com.bcgogo.config.dto.ShopBargainRecordDTO;
import com.bcgogo.config.model.*;
import com.bcgogo.enums.shop.BargainStatus;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.finance.HardwareSoftwareAccountRecordDTO;
import com.bcgogo.user.dto.UserDTO;
import com.bcgogo.utils.CollectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: zhangjuntao
 * Date: 13-3-30
 * Time: 下午5:23
 */
@Component
public class ShopBargainService implements IShopBargainService {
  private static final Logger LOG = LoggerFactory.getLogger(ShopBargainService.class);
  @Autowired
  private ConfigDaoManager configDaoManager;

  @Override
  public void createShopBargainRecord(ShopBargainRecordDTO dto) {
    ConfigWriter writer = configDaoManager.getWriter();
    Object status = writer.begin();
    try {
      Shop shop = writer.getById(Shop.class, dto.getShopId());
      ShopBargainRecord record = new ShopBargainRecord();
      record.fromDTO(dto);
      record.setOriginalPrice(shop.getSoftPrice());
      writer.save(record);
      shop.setBargainStatus(dto.getBargainStatus());
      shop.setBargainPrice(dto.getApplicationPrice());
      if (BargainStatus.AUDIT_PASS.equals(dto.getBargainStatus())) {
        shop.setSoftPrice(dto.getApplicationPrice());
      }
      writer.update(shop);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public List<ShopBargainRecordDTO> getShopBargainRecordsByShopId(long shopId) {
    ConfigWriter writer = configDaoManager.getWriter();
    List<ShopBargainRecord> records = writer.getShopBargainRecordsByShopId(shopId);
    List<ShopBargainRecordDTO> dtoList = new ArrayList<ShopBargainRecordDTO>();
    for (ShopBargainRecord record : records) {
      dtoList.add(record.toDTO());
    }
    return dtoList;
  }

  @Override
  public Map<Long,ShopBargainRecordDTO> getShopAuditPassBargainRecordMapByShopId(Long... shopId) {
    Map<Long,ShopBargainRecordDTO> map = new HashMap<Long, ShopBargainRecordDTO>();
    ConfigWriter writer = configDaoManager.getWriter();
    List<ShopBargainRecord> shopBargainRecordList = writer.getShopAuditPassBargainRecordByShopId(shopId);
    List<ShopBargainRecordDTO> dtoList = new ArrayList<ShopBargainRecordDTO>();
    for (ShopBargainRecord record : shopBargainRecordList) {
      map.put(record.getShopId(),record.toDTO());
    }
    return map;
  }

  @Override
  public void auditShopBargainRecord(long shopId, Long userId, BargainStatus bargainStatus, String reason) throws Exception {
    ConfigWriter writer = configDaoManager.getWriter();
    Object status = writer.begin();
    try {
      ShopBargainRecord record = writer.getShopBargainRecordByShopId(shopId, BargainStatus.PENDING_REVIEW);
      if (record == null) throw new Exception("there is no record for audit.[shopId=" + shopId + "]");
      if (bargainStatus == null) throw new Exception("bargainStatus is null.");
      record.setAuditorId(userId);
      record.setAuditTime(System.currentTimeMillis());
      record.setBargainStatus(bargainStatus);
      record.setAuditReason(reason);
      writer.saveOrUpdate(record);
      Shop shop = writer.getById(Shop.class, record.getShopId());
      shop.setBargainStatus(bargainStatus);
      if (BargainStatus.AUDIT_PASS == bargainStatus) {
        shop.setSoftPrice(shop.getBargainPrice());
        //更新待支付记录
      }
      writer.update(shop);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }
}
