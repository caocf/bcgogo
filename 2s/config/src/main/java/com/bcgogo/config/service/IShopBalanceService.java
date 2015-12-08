package com.bcgogo.config.service;

import com.bcgogo.config.dto.ShopBalanceDTO;
import com.bcgogo.config.model.ConfigWriter;
import com.bcgogo.config.model.ShopBalance;

import java.util.List;

public interface IShopBalanceService {
  public ShopBalanceDTO getSmsBalanceById(long smsBalanceId);

  public ShopBalanceDTO getSmsBalanceByShopId(long shopId);

  boolean isSMSArrearage(long shopId);

  public ShopBalanceDTO createSmsBalance(ShopBalanceDTO shopBalanceDTO);

  public void createSmsBalanceForRegister(Long registerShopId, ConfigWriter writer);

  /**
   * 为邀请者 冲短信费用
   *
   * @param registerShopId
   */
  public void createSmsBalanceForInviterByRegisterShopId(Long registerShopId, double rechargeSms);

  public List<ShopBalance> getSmsBalanceListByShopId(long shopId);

  public ShopBalanceDTO updateSmsBalance(ShopBalanceDTO shopBalanceDTO);

  ShopBalanceDTO saveSmsBalance(ShopBalanceDTO shopBalanceDTO) throws Exception;

  /**
   * 增加短信费用
   *
   * @param shopId  Long
   * @param balance Double
   */
  void addSmsBalance(Long shopId, Double balance);

}
