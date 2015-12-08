package com.bcgogo.config.service;

import com.bcgogo.config.dto.ShopBalanceDTO;
import com.bcgogo.config.model.ConfigDaoManager;
import com.bcgogo.config.model.ConfigWriter;
import com.bcgogo.config.model.RegisterInfo;
import com.bcgogo.config.model.ShopBalance;
import com.bcgogo.config.model.SmsDonationLog;
import com.bcgogo.enums.DonationType;
import com.bcgogo.enums.shop.RegisterType;
import com.bcgogo.utils.CollectionUtil;
import com.bcgogo.utils.NumberUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.List;

@Component
public class ShopBalanceService implements IShopBalanceService {
  private static final Logger LOG = LoggerFactory.getLogger(ConfigService.class);
  @Autowired
  private ConfigDaoManager configDaoManager;

  @Override
  public ShopBalanceDTO getSmsBalanceById(long smsBalanceId) {
    ConfigWriter writer = configDaoManager.getWriter();

    ShopBalance shopBalance = writer.getById(ShopBalance.class, smsBalanceId);

    if (shopBalance == null) return null;
    return shopBalance.toDTO();
  }

  @Override
  public ShopBalanceDTO getSmsBalanceByShopId(long shopId) {
    ConfigWriter writer = configDaoManager.getWriter();
    List<ShopBalance> shopBalanceList = writer.getSmsBalanceByShopId(shopId);

    if (shopBalanceList.size() == 0) return null;
    return shopBalanceList.get(0).toDTO();
  }

  @Override
  public boolean isSMSArrearage(long shopId) {
    ShopBalanceDTO shopBalanceDTO = this.getSmsBalanceByShopId(shopId);
    return shopBalanceDTO == null || NumberUtil.round((shopBalanceDTO.getSmsBalance() - 5), 2) < 0;
  }

  @Override
  public List<ShopBalance> getSmsBalanceListByShopId(long shopId) {
    ConfigWriter writer = configDaoManager.getWriter();
    return writer.getSmsBalanceByShopId(shopId);
  }

  @Override
  public ShopBalanceDTO createSmsBalance(ShopBalanceDTO shopBalanceDTO) {
    if (shopBalanceDTO == null) return null;

    ConfigWriter writer = configDaoManager.getWriter();
    Object status = writer.begin();

    try {
      ShopBalance shopBalance = new ShopBalance(shopBalanceDTO);
      writer.save(shopBalance);
      writer.commit(status);

      shopBalanceDTO.setId(shopBalance.getId());

      return shopBalanceDTO;
    } finally {
      writer.rollback(status);
    }
  }

  //转为使用 createSmsBalanceForInviter
  @Deprecated
  public void createSmsBalanceForRegister(Long registerShopId, ConfigWriter writer) {
    RegisterInfo registerInfo = writer.getRegisterInfoByRegisterShopId(registerShopId);
    final double customerInviteRechargeSms = 500d;
    final double supplierInviteRechargeSms = 200d;
    double smsDonationValue = 0d;
    if (registerInfo == null) {
      LOG.info("getRegisterInfoByRegisterShopId:{} is null .", registerShopId);
      return;
    }
    if (registerInfo.getRegisterType() == RegisterType.CUSTOMER_INVITE) {
      smsDonationValue = customerInviteRechargeSms;
    } else if (registerInfo.getRegisterType() == RegisterType.SUPPLIER_INVITE) {
      smsDonationValue = supplierInviteRechargeSms;
    } else {
      LOG.info("registerType is {}", registerInfo.getRegisterType());
      return;
    }
    ShopBalanceDTO shopBalanceDTO = getSmsBalanceByShopId(registerInfo.getInviterShopId());
    ShopBalance shopBalance;
    if (shopBalanceDTO == null) {
      shopBalanceDTO = new ShopBalanceDTO();
      shopBalanceDTO.setShopId(registerInfo.getInviterShopId());
      shopBalanceDTO.setSmsBalance(smsDonationValue);
      shopBalanceDTO.setRechargeTotal(smsDonationValue);
      shopBalance = new ShopBalance(shopBalanceDTO);
    } else {
      shopBalanceDTO.setRechargeTotal((shopBalanceDTO.getRechargeTotal() == null ? 0d : shopBalanceDTO.getRechargeTotal()) + smsDonationValue);
      shopBalanceDTO.setSmsBalance((shopBalanceDTO.getSmsBalance() == null ? 0d : shopBalanceDTO.getSmsBalance()) + smsDonationValue);
      shopBalance = writer.getById(ShopBalance.class, shopBalanceDTO.getId());
      shopBalance.fromDTO(shopBalanceDTO);
    }
    writer.saveOrUpdate(shopBalance);
    // 短信赠送历史记录
    SmsDonationLog smsDonationLog = new SmsDonationLog();
    smsDonationLog.setShopId(registerInfo.getInviterShopId());
    smsDonationLog.setDonationType(DonationType.INVITE_PAID);
    smsDonationLog.setRegisterType(registerInfo.getRegisterType());
    smsDonationLog.setValue(smsDonationValue);
    smsDonationLog.setDonationTime(System.currentTimeMillis());
    writer.save(smsDonationLog);
  }

  public void createSmsBalanceForInviterByRegisterShopId(Long registerShopId, double rechargeSms) {
    ConfigWriter writer = configDaoManager.getWriter();
    Object status = writer.begin();
    try {
      RegisterInfo registerInfo = writer.getRegisterInfoByRegisterShopId(registerShopId);
      if (registerInfo == null) {
        LOG.info("getRegisterInfoByRegisterShopId:{} is null .", registerShopId);
        return;
      }
      ShopBalanceDTO shopBalanceDTO = getSmsBalanceByShopId(registerInfo.getInviterShopId());
      ShopBalance shopBalance;
      if (shopBalanceDTO == null) {
        shopBalanceDTO = new ShopBalanceDTO();
        shopBalanceDTO.setShopId(registerInfo.getInviterShopId());
        shopBalanceDTO.setSmsBalance(rechargeSms);
        shopBalanceDTO.setRechargeTotal(rechargeSms);
        shopBalance = new ShopBalance(shopBalanceDTO);
      } else {
        shopBalanceDTO.setRechargeTotal((shopBalanceDTO.getRechargeTotal() == null ? 0d : shopBalanceDTO.getRechargeTotal()) + rechargeSms);
        shopBalanceDTO.setSmsBalance((shopBalanceDTO.getSmsBalance() == null ? 0d : shopBalanceDTO.getSmsBalance()) + rechargeSms);
        shopBalance = writer.getById(ShopBalance.class, shopBalanceDTO.getId());
        shopBalance.fromDTO(shopBalanceDTO);
      }
      writer.saveOrUpdate(shopBalance);
      // 短信赠送历史记录
      SmsDonationLog smsDonationLog = new SmsDonationLog();
      smsDonationLog.setShopId(registerInfo.getInviterShopId());
      smsDonationLog.setDonationType(DonationType.INVITE_PAID);
      smsDonationLog.setRegisterType(registerInfo.getRegisterType());
      smsDonationLog.setValue(rechargeSms);
      smsDonationLog.setDonationTime(System.currentTimeMillis());
      writer.save(smsDonationLog);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public ShopBalanceDTO updateSmsBalance(ShopBalanceDTO shopBalanceDTO) {
    if (shopBalanceDTO == null) return null;

    ConfigWriter writer = configDaoManager.getWriter();
    Object status = writer.begin();

    try {
      Long id = shopBalanceDTO.getId();
      if (id == null) return null;

      ShopBalance shopBalance = writer.getById(ShopBalance.class, id);
      if (shopBalance == null) {
        return this.createSmsBalance(shopBalanceDTO);
      }

      shopBalance.fromDTO(shopBalanceDTO);

      writer.save(shopBalance);
      writer.commit(status);

      return shopBalanceDTO;
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public ShopBalanceDTO saveSmsBalance(ShopBalanceDTO shopBalanceDTO) throws Exception {
    if (shopBalanceDTO == null) return null;
    ConfigWriter writer = configDaoManager.getWriter();
    ShopBalance shopBalance = null;
    if (shopBalanceDTO.getId() != null) {
      shopBalance = writer.getById(ShopBalance.class, shopBalanceDTO.getId());
      shopBalance.setRechargeTotal(shopBalanceDTO.getRechargeTotal());
      shopBalance.setSmsBalance(shopBalanceDTO.getSmsBalance());
    } else {
      shopBalance = new ShopBalance(shopBalanceDTO);
    }
    Object status = writer.begin();
    try {
      writer.saveOrUpdate(shopBalance);
      writer.commit(status);
      return shopBalanceDTO;
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public void addSmsBalance(Long shopId, Double balance) {
    ConfigWriter writer = configDaoManager.getWriter();
    List<ShopBalance> shopBalances = writer.getSmsBalanceByShopId(shopId);
    ShopBalance shopBalance = null;
    if (CollectionUtil.isNotEmpty(shopBalances)) {
      shopBalance = shopBalances.get(0);
    }
    Object status = writer.begin();
    try {
      if (shopBalance == null) {
        shopBalance = new ShopBalance();
        shopBalance.setShopId(shopId);
        shopBalance.setSmsBalance(balance);
        shopBalance.setRechargeTotal(0D);
      } else {
        shopBalance.addSmsBalance(balance);
      }
      writer.saveOrUpdate(shopBalance);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }

  }

  public static void main(String[] args) {
    Calendar c = Calendar.getInstance();
    c.set(2013, 1, 26, 0, 0, 0);
    c.set(Calendar.MILLISECOND, 0);
    System.out.println(c.getTimeInMillis());
  }

}
