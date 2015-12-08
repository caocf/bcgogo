package com.bcgogo.admin.user.permission;

import com.bcgogo.config.dto.RegisterInfoDTO;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.dto.ShopOperateHistoryDTO;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.service.IShopService;
import com.bcgogo.enums.shop.RegisterType;
import com.bcgogo.enums.shop.ShopOperateType;
import com.bcgogo.enums.shop.ShopStatus;
import com.bcgogo.enums.txn.finance.SmsCategory;
import com.bcgogo.exception.BcgogoException;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.finance.ShopSmsRecordDTO;
import com.bcgogo.txn.service.finance.ISmsAccountService;
import com.bcgogo.txn.service.solr.IShopSolrWriterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 * User: hans
 * Date: 13-4-7
 * Time: 下午2:47
 * 店铺升级
 *
 * 已废弃，转入BcgogoReceivableService.afterFullReceived
 */
@Component
@Deprecated
public class ShopUpgradeHandler {
  private static final Logger LOG = LoggerFactory.getLogger(ShopUpgradeHandler.class);

  //todo 目前因为库的原因 需要发在一个事务中
  public void upgrade(ShopOperateHistoryDTO dto) throws BcgogoException {
    dto.setOperateTime(System.currentTimeMillis());
    if (dto.getTrialEndTime() != null) {
      dto.setTrialEndTime(dto.getTrialEndTime() + 24 * 60 * 60 * 1000 - 1);
    }
    // 注册短信推荐赠送
    createShopSmsHandsel(dto);
    // 创建 禁用&启用历史记录
    ServiceManager.getService(IShopService.class).createShopOperateHistory(dto);
    try {
      ServiceManager.getService(IShopSolrWriterService.class).reCreateShopIdSolrIndex(dto.getOperateShopId());
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }
  }

  /**
   * 供应商推荐成功奖励 500
   * 客户推荐成功奖励 200
   */
  private void createShopSmsHandsel(ShopOperateHistoryDTO dto) throws BcgogoException {
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    if (ShopOperateType.UPDATE_REGISTERED_TRIAL_SHOP != dto.getOperateType()) return;
    ShopDTO shopDTO = configService.getShopById(dto.getOperateShopId());
    if (!ShopStatus.isRegistrationPaid(shopDTO.getShopStatus())) return;
    RegisterInfoDTO infoDTO = configService.getRegisterInfoDTOByRegisterShopId(dto.getOperateShopId());
    final double customerInviteRechargeSms = 500d;
    final double supplierInviteRechargeSms = 200d;
    double smsDonationValue = 0d;
    if (infoDTO == null) {
      LOG.info("getRegisterInfoByRegisterShopId:{} is null .", dto.getOperateShopId());
      return;
    }
    if (infoDTO.getRegisterType() == RegisterType.CUSTOMER_INVITE) {
      smsDonationValue = customerInviteRechargeSms;
    } else if (infoDTO.getRegisterType() == RegisterType.SUPPLIER_INVITE) {
      smsDonationValue = supplierInviteRechargeSms;
    } else {
      LOG.info("registerType is {}", infoDTO.getRegisterType());
      return;
    }
    ShopSmsRecordDTO shopSmsRecordDTO = new ShopSmsRecordDTO();
    shopSmsRecordDTO.setSmsCategory(SmsCategory.RECOMMEND_HANDSEL);
    shopSmsRecordDTO.setNumber(Math.round(smsDonationValue * 10));
    shopSmsRecordDTO.setBalance(smsDonationValue);
    shopSmsRecordDTO.setShopId(infoDTO.getInviterShopId());
    shopSmsRecordDTO.setOperatorId(dto.getOperateUserId());
    ServiceManager.getService(ISmsAccountService.class).createShopSmsHandsel(shopSmsRecordDTO);
  }


}
