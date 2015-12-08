package com.bcgogo.txn.service;

import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.model.TemBlance;
import com.bcgogo.txn.model.TxnDaoManager;
import com.bcgogo.txn.model.TxnWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: sl
 * Date: 12-2-6
 * Time: 下午5:09
 * To change this template use File | Settings | File Templates.
 */
@Component
public class TemBalance implements ITemBalance {
  private static final Logger LOG = LoggerFactory.getLogger(TemBalance.class);
  @Autowired
  private TxnDaoManager txnDaoManager;


  public void saveBlance(Long shopId, float smsBalance, float rechargeAmount) throws Exception {
    IConfigService configService = ServiceManager.getService(IConfigService.class);

    try {
      ShopDTO shop = configService.getShopById(shopId);
      TemBlance temBlance = new TemBlance();
      temBlance.setShopId(shopId);
      temBlance.setSmsBalance(smsBalance);
      temBlance.setRechargeAmount(rechargeAmount);
      temBlance.setName(shop.getName());
      temBlance.setAddress(shop.getAddress());
      temBlance.setLegalRep(shop.getLegalRep());
      temBlance.setMobile(shop.getMobile());
      TxnWriter writer = txnDaoManager.getWriter();
      Object status = writer.begin();
      try {

        writer.save(temBlance);
        writer.commit(status);
      } finally {
        writer.rollback(status);
      }

    } catch (Exception e) {
      LOG.error(e.getMessage(),e);
    }
  }

  public void deleteBlance(Long id) {
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      TemBlance temBlance = writer.getById(TemBlance.class, id);
      writer.delete(temBlance);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  public List<TemBlance> getSmsByShopId(int pageNo, int pageSize) {
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.getSmsByShopId(pageNo, pageSize);
  }


  public int coutSms() {
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.coutSms();
  }
}
