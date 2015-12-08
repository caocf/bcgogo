package com.bcgogo.user.service.wx.impl;

import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.util.MemCacheAdapter;
import com.bcgogo.constant.Constant;
import com.bcgogo.enums.sms.SmsSendScene;
import com.bcgogo.notification.service.WXService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.model.UserDaoManager;
import com.bcgogo.user.model.UserWriter;
import com.bcgogo.user.model.wx.WXAccount;
import com.bcgogo.user.model.wx.WXShopAccount;
import com.bcgogo.user.service.wx.IWXAccountService;
import com.bcgogo.user.service.wx.WXHelper;
import com.bcgogo.utils.*;
import com.bcgogo.wx.WXConstant;
import com.bcgogo.wx.WXShopAccountSearchCondition;
import com.bcgogo.wx.WXShopBillDTO;
import com.bcgogo.wx.user.WXAccountDTO;
import com.bcgogo.wx.user.WXShopAccountDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 14-12-18
 * Time: 上午11:11
 */
@Component
public class WXAccountService implements IWXAccountService {

  @Autowired
  private UserDaoManager daoManager;

  @Override
  public List<WXAccountDTO> getWXAccountDTOByCondition(WXShopAccountSearchCondition condition) throws IOException {
    if (condition == null) {
      return null;
    }
    UserWriter writer = daoManager.getWriter();
    List<WXAccount> accounts = writer.getWXAccountByCondition(condition);
    if (CollectionUtil.isEmpty(accounts)) return null;
    List<WXAccountDTO> accountDTOs = new ArrayList<WXAccountDTO>();
    for (WXAccount account : accounts) {
      accountDTOs.add(account.toDTO());
    }
    return accountDTOs;
  }

  @Override
  public WXAccountDTO getWXAccountDTOById(Long id) throws IOException {
    UserWriter writer = daoManager.getWriter();
    WXAccount account = writer.getById(WXAccount.class, id);
    return account != null ? account.toDTO() : null;
  }

  @Override
  public WXAccountDTO getWXAccountDTOByPublicNo(String publicNo) throws IOException {
    UserWriter writer = daoManager.getWriter();
    WXAccount account = writer.getWXAccountByPublicNo(publicNo);
    return account != null ? account.toDTO() : null;
  }

 @Override
  public WXAccountDTO getWXAccountByOpenId(String openId) throws IOException {
    UserWriter writer = daoManager.getWriter();
    WXAccount account = writer.getWXAccountByOpenId(openId);
   return account != null ? account.toDTO() : null;
  }


  @Override
  public WXAccountDTO getWXAccountDTOByShopId(Long shopId) throws IOException {
    if (shopId == null) return null;
    WXShopAccountSearchCondition condition = new WXShopAccountSearchCondition();
    condition.setShopId(shopId);
    return CollectionUtil.getFirst(getWXAccountDTOByCondition(condition));
  }

  /**
   * 从缓存获取公共号secret帐号
   *
   * @param shopId
   * @return
   * @throws Exception
   */
  @Override
  public WXAccountDTO getDecryptedWXAccountByShopId(Long shopId) throws Exception {
    WXAccountDTO accountDTO = getWXAccountDTOByShopId(shopId);
    if (accountDTO == null) return null;
    String appSecret = new String(EncryptionUtil.decrypt(accountDTO.getAppSecretByte(), WXHelper.getSecretKey()));
    accountDTO.setSecret(appSecret);
    return accountDTO;
  }

  @Override
  public WXAccountDTO getDecryptedWXAccountByPublicNo(String publicNo) throws Exception {
    WXAccountDTO accountDTO = getWXAccountDTOByPublicNo(publicNo);
    if (accountDTO == null) return null;
    String secret = new String(EncryptionUtil.decrypt(accountDTO.getAppSecretByte(), WXHelper.getSecretKey()));
    accountDTO.setSecret(secret);
    return accountDTO;
  }

  @Override
  public WXAccountDTO getCachedWXAccount(String publicNo) throws Exception {
    WXAccountDTO accountDTO = (WXAccountDTO) MemCacheAdapter.get(WXConstant.KEY_PREFIX_ACCOUNT + publicNo);
    if (accountDTO == null) {
      accountDTO = getWXAccountDTOByPublicNo(publicNo);
      if (accountDTO == null) return null;
      MemCacheAdapter.set(WXConstant.KEY_PREFIX_ACCOUNT + publicNo, accountDTO, new Date(System.currentTimeMillis() + WXConstant.M_EXPIRE_ACCOUNT));
    }
    String secret = new String(EncryptionUtil.decrypt(accountDTO.getAppSecretByte(), WXHelper.getSecretKey()));
    accountDTO.setSecret(secret);
    return accountDTO;
  }


  @Override
  public WXAccountDTO getDefaultWXAccount() throws Exception {
    String publicNo = getEvnPublicNo();
    WXAccountDTO accountDTO = (WXAccountDTO) MemCacheAdapter.get(WXConstant.KEY_PREFIX_ACCOUNT + publicNo);
    if (accountDTO == null) {
      accountDTO = getWXAccountDTOByPublicNo(publicNo);
      if (accountDTO == null) return null;
      MemCacheAdapter.set(WXConstant.KEY_PREFIX_ACCOUNT + publicNo, accountDTO, new Date(System.currentTimeMillis() + WXConstant.M_EXPIRE_ACCOUNT));
    }
    String secret = new String(EncryptionUtil.decrypt(accountDTO.getAppSecretByte(), WXHelper.getSecretKey()));
    accountDTO.setSecret(secret);
    return accountDTO;
  }

  private String getEvnPublicNo() {
    String defaultPublicNo = null;
    String evn_mode = ServiceManager.getService(IConfigService.class).getConfig("evn_mode", ShopConstant.BC_SHOP_ID);
    if (Constant.EVN_MODE_OFFICIAL.equals(evn_mode)) {
      defaultPublicNo = WXConstant.OFFICIAL_PUBLIC_ID;
    } else if (Constant.EVN_MODE_DEVELOP.equals(evn_mode)) {
      defaultPublicNo = WXConstant.YI_FA_PUBLIC_ID;
    } else {
      defaultPublicNo = WXConstant.WEI_NI_YOU_ZHI_PUBLIC_ID;
    }
    return defaultPublicNo;
  }

  @Override
  public void saveOrUpdateWXAccount(WXAccountDTO accountDTO) {
    UserWriter writer = daoManager.getWriter();
    Object status = writer.begin();
    try {
      WXAccount account = null;
      if (accountDTO.getId() == null) {
        account = new WXAccount();
      } else {
        account = writer.getById(WXAccount.class, accountDTO.getId());
      }
      account.fromDTO(accountDTO);
      writer.saveOrUpdate(account);
      writer.commit(status);
      accountDTO.setId(account.getId());
    } finally {
      writer.rollback(status);
    }
  }


  @Override
  public List<WXShopAccountDTO> getWXShopAccountDTO(Long shopId, Long accountId) {
    List<WXShopAccount> shopAccounts = getWXShopAccount(shopId, accountId);
    if (CollectionUtil.isEmpty(shopAccounts)) return null;
    List<WXShopAccountDTO> shopAccountDTOs = new ArrayList<WXShopAccountDTO>();
    for (WXShopAccount account : shopAccounts) {
      shopAccountDTOs.add(account.toDTO());
    }
    return shopAccountDTOs;
  }


  @Override
  public List<WXShopAccount> getWXShopAccount(Long shopId, Long accountId) {
    UserWriter writer = daoManager.getWriter();
    return writer.getWXShopAccount(shopId, accountId);
  }

  @Override
  public WXShopAccountDTO getWXShopAccountDTOById(Long id) {
    UserWriter writer = daoManager.getWriter();
    WXShopAccount shopAccount = writer.getById(WXShopAccount.class, id);
    return shopAccount != null ? shopAccount.toDTO() : null;
  }

  @Override
  public WXShopAccount getWXShopAccountByShopId(Long shopId) {
    UserWriter writer = daoManager.getWriter();
    return CollectionUtil.getFirst(writer.getWXShopAccount(shopId, null));
  }

  @Override
  public WXShopAccountDTO getWXShopAccountDTOByShopId(Long shopId) {
    WXShopAccount shopAccount = getWXShopAccountByShopId(shopId);
    return shopAccount != null ? shopAccount.toDTO() : null;
  }


  @Override
  public List<WXAccountDTO> getAllWXAccount() throws IOException {
    UserWriter writer = daoManager.getWriter();
    List<WXAccount> accounts = writer.getAllWXAccount();
    if (CollectionUtil.isEmpty(accounts)) return null;
    List<WXAccountDTO> accountDTOs = new ArrayList<WXAccountDTO>();
    for (WXAccount account : accounts) {
      accountDTOs.add(account.toDTO());
    }
    return accountDTOs;
  }


  /**
   * @param shopId
   * @throws Exception
   */
  @Override
  public void createDefaultWXShopAccount(Long shopId) throws Exception {
    IWXAccountService accountService = ServiceManager.getService(WXAccountService.class);
    Long defaultAccountId = accountService.getDefaultWXAccount().getId();
    WXShopAccountDTO shopAccountDTO = new WXShopAccountDTO();
    shopAccountDTO.setShopId(shopId);
    shopAccountDTO.setAccountId(defaultAccountId);
    shopAccountDTO.setExpireDate(DateUtil.getStartTimeOfMonth(1));
    shopAccountDTO.setBalance(WXConstant.DEFAULT_WX_GIFT_TOTAL);
    saveOrUpdateWXShopAccountDTO(shopAccountDTO);
    //save WXShopBill
    WXShopBillDTO billDTO = new WXShopBillDTO();
    billDTO.setShopId(shopId);
    billDTO.setVestDate(System.currentTimeMillis());
    billDTO.setScene(SmsSendScene.WX_GIFT);
    billDTO.setTotal(WXConstant.DEFAULT_WX_GIFT_TOTAL);
    ServiceManager.getService(WXService.class).saveOrUpdateWXShopBill(billDTO);
  }

  @Override
  public void saveOrUpdateWXShopAccountDTO(WXShopAccountDTO... shopAccountDTOs) {
    if (ArrayUtil.isEmpty(shopAccountDTOs)) return;
    UserWriter writer = daoManager.getWriter();
    Object status = writer.begin();
    try {
      for (WXShopAccountDTO shopAccountDTO : shopAccountDTOs) {
        WXShopAccount shopAccount = null;
        if (shopAccountDTO.getId() == null) {
          shopAccount = new WXShopAccount();
        } else {
          shopAccount = writer.getById(WXShopAccount.class, shopAccountDTO.getId());
        }
        shopAccount.fromDTO(shopAccountDTO);
        writer.saveOrUpdate(shopAccount);
        shopAccountDTO.setId(shopAccount.getId());
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public int countWXAccount(WXShopAccountSearchCondition condition) {
    UserWriter writer = daoManager.getWriter();
    return writer.countWXAccount(condition);
  }

  @Override
  public List<WXAccountDTO> getWXAccountDTO(WXShopAccountSearchCondition condition) throws IOException {
    if (condition == null || (condition.getPager() != null && condition.getPager().getTotalRows() <= 0)) {
      return null;
    }
    UserWriter writer = daoManager.getWriter();
    List<WXAccount> accounts = writer.getWXAccount(condition);
    if (CollectionUtil.isEmpty(accounts)) return null;
    List<WXAccountDTO> accountDTOs = new ArrayList<WXAccountDTO>();
    for (WXAccount account : accounts) {
      accountDTOs.add(account.toDTO());
    }
    return accountDTOs;
  }

  @Override
  public int countWXShopAccount(WXShopAccountSearchCondition condition) {
    UserWriter writer = daoManager.getWriter();
    return writer.countWXShopAccount(condition);
  }

  @Override
  public List<WXShopAccountDTO> getWXShopAccountDTO(WXShopAccountSearchCondition condition) throws IOException {
    if (condition == null || (condition.getPager() != null && condition.getPager().getTotalRows() <= 0)) {
      return null;
    }
    UserWriter writer = daoManager.getWriter();
    List<WXShopAccount> shopAccounts = writer.getWXShopAccount(condition);
    if (CollectionUtil.isEmpty(shopAccounts)) return null;
    List<WXShopAccountDTO> shopAccountDTOs = new ArrayList<WXShopAccountDTO>();
    for (WXShopAccount shopAccount : shopAccounts) {
      shopAccountDTOs.add(shopAccount.toDTO());
    }
    return shopAccountDTOs;
  }


}
