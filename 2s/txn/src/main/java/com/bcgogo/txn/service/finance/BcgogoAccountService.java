package com.bcgogo.txn.service.finance;

import com.bcgogo.common.Result;
import com.bcgogo.config.ShopSearchCondition;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.IShopService;
import com.bcgogo.enums.txn.finance.PaymentMethod;
import com.bcgogo.enums.txn.finance.PaymentType;
import com.bcgogo.exception.BcgogoException;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.finance.*;
import com.bcgogo.txn.model.TxnDaoManager;
import com.bcgogo.txn.model.TxnWriter;
import com.bcgogo.txn.model.finance.*;
import com.bcgogo.user.dto.UserDTO;
import com.bcgogo.user.service.permission.IUserCacheService;
import com.bcgogo.utils.CollectionUtil;
import com.bcgogo.utils.NumberUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * User: ZhangJuntao
 * Date: 13-3-19
 * Time: 下午5:29
 */
@Component
public class BcgogoAccountService implements IBcgogoAccountService {
  private static final Logger LOG = LoggerFactory.getLogger(BcgogoAccountService.class);
  @Autowired
  private TxnDaoManager txnDaoManager;

  @Override
  public Result searchHardwareSoftwareAccountResult(AccountSearchCondition condition) throws BcgogoException {
    Result result = new Result(true);
    List<Long> shopIds = new ArrayList<Long>();
    if (StringUtils.isNotBlank(condition.getShopName())) {
      ShopSearchCondition shopSearchCondition = new ShopSearchCondition();
      shopSearchCondition.setName(condition.getShopName());
      shopIds = ServiceManager.getService(IShopService.class).getShopIdByShopCondition(shopSearchCondition);
      if (CollectionUtil.isEmpty(shopIds)) {
        result.setTotal(0);
        return result;
      }
      condition.setShopIds(shopIds);
    }
    TxnWriter writer = txnDaoManager.getWriter();
    result.setTotal(writer.countHardwareSoftwareAccountResult(condition));
    List<HardwareSoftwareAccountDTO> dtoList = new ArrayList<HardwareSoftwareAccountDTO>();
    List<HardwareSoftwareAccount> accountList = writer.searchHardwareSoftwareAccountResult(condition);
    for (HardwareSoftwareAccount account : accountList) {
      if (account.getShopId() != null) shopIds.add(account.getShopId());
    }
    ShopDTO shopDTO;
    HardwareSoftwareAccountDTO accountDTO;
    Map<Long, ShopDTO> shopDTOMap = null;
    Map<Long, List<HardwareSoftwareAccountOrderDTO>> accountOrderMap = null;
    if (CollectionUtil.isNotEmpty(shopIds)) {
      shopDTOMap = ServiceManager.getService(IShopService.class).getShopByShopIds(shopIds.toArray(new Long[shopIds.size()]));
      accountOrderMap = getAccountItems(shopIds.toArray(new Long[shopIds.size()]));
    }
    for (HardwareSoftwareAccount account : accountList) {
      accountDTO = account.toDTO();
      if (shopDTOMap != null) {
        shopDTO = shopDTOMap.get(account.getShopId());
        if (shopDTO != null) accountDTO.setShopName(shopDTO.getName());
      }
      if (accountOrderMap != null) {
        accountDTO.setOrders(accountOrderMap.get(account.getShopId()));
      }
      dtoList.add(accountDTO);
    }
    result.setData(dtoList);
    return result;
  }

  private Map<Long, List<HardwareSoftwareAccountOrderDTO>> getAccountItems(Long... shopIds) {
    Map<Long, List<HardwareSoftwareAccountOrderDTO>> map = new HashMap<Long, List<HardwareSoftwareAccountOrderDTO>>();
    List<HardwareSoftwareAccountRecordDTO> recordDTOList;
    List<HardwareSoftwareAccountOrderDTO> accountOrderDTOList;
    HardwareSoftwareAccountOrderDTO accountOrderDTO;
    TxnWriter writer = txnDaoManager.getWriter();
    List<BcgogoReceivableOrder> orderList = writer.getBcgogoReceivableOrderByShopIds(shopIds);
    List<HardwareSoftwareAccountRecordDTO> recordList = writer.getHardwareSoftwareAccountRecordByShopIds(shopIds);
    Map<Long, List<HardwareSoftwareAccountRecordDTO>> recordMap = new HashMap<Long, List<HardwareSoftwareAccountRecordDTO>>();
    Set<Long> userIds = new HashSet<Long>();
    Set<Long> instalmentPlanIds = new HashSet<Long>();
    for (HardwareSoftwareAccountRecordDTO recordDTO : recordList) {
      userIds.add(recordDTO.getAuditorId());
      userIds.add(recordDTO.getOperatorId());
      userIds.add(recordDTO.getPayeeId());
      userIds.add(recordDTO.getSubmitterId());
    }
    for (BcgogoReceivableOrder order : orderList) {
      instalmentPlanIds.add(order.getInstalmentPlanId());
    }
    UserDTO userDTO;
    Map<Long, UserDTO> userDTOMap = ServiceManager.getService(IUserCacheService.class).getUserMap(userIds);
    Map<Long, InstalmentPlanDTO> instalmentPlanDTOMap = null;
    if (CollectionUtil.isNotEmpty(instalmentPlanIds)) {
      instalmentPlanDTOMap = ServiceManager.getService(IBcgogoReceivableService.class).getInstalmentPlanDetailMap(instalmentPlanIds.toArray(new Long[instalmentPlanIds.size()]));
    }
    for (HardwareSoftwareAccountRecordDTO recordDTO : recordList) {
      recordDTOList = recordMap.get(recordDTO.getBcgogoReceivableOrderId());
      if (recordDTOList == null) {
        recordDTOList = new ArrayList<HardwareSoftwareAccountRecordDTO>();
      }
      userDTO = userDTOMap.get(recordDTO.getSubmitterId());
      if (userDTO != null) recordDTO.setSubmitterName(userDTO.getName());

      userDTO = userDTOMap.get(recordDTO.getOperatorId());
      if (userDTO != null) recordDTO.setOperatorName(userDTO.getName());

      userDTO = userDTOMap.get(recordDTO.getPayeeId());
      if (userDTO != null) recordDTO.setPayeeName(userDTO.getName());

      userDTO = userDTOMap.get(recordDTO.getAuditorId());
      if (userDTO != null) recordDTO.setAuditorName(userDTO.getName());

      recordDTOList.add(recordDTO);
      recordMap.put(recordDTO.getBcgogoReceivableOrderId(), recordDTOList);
    }

    for (BcgogoReceivableOrder order : orderList) {
      accountOrderDTOList = map.get(order.getShopId());
      if (accountOrderDTOList == null) {
        accountOrderDTOList = new ArrayList<HardwareSoftwareAccountOrderDTO>();
      }

      accountOrderDTO = order.toHardwareSoftwareAccountOrderDTO();
      accountOrderDTO.setRecords(recordMap.get(order.getId()));
      if (instalmentPlanDTOMap != null)
        accountOrderDTO.setInstalmentPlanDTO(instalmentPlanDTOMap.get(order.getInstalmentPlanId()));
      accountOrderDTO.combineContent();
      accountOrderDTOList.add(accountOrderDTO);
      map.put(order.getShopId(), accountOrderDTOList);
    }
    return map;
  }

  @Override
  public HardwareSoftwareAccountDTO getHardwareSoftwareAccountByShopId(long shopId) {
    TxnWriter writer = txnDaoManager.getWriter();
    HardwareSoftwareAccount account = writer.getHardwareSoftwareAccountByShopId(shopId);
    if (account == null) {
      Object status = writer.begin();
      try {
        account = new HardwareSoftwareAccount(shopId);
        writer.save(account);
        writer.commit(status);
      } finally {
        writer.rollback(status);
      }
    }
    return account.toDTO();
  }

  @Override
  public HardwareSoftwareAccountDTO checkSoftwareAccountByShopId(Long shopId, Double total) {
    TxnWriter writer = txnDaoManager.getWriter();
    HardwareSoftwareAccount account = writer.getHardwareSoftwareAccountByShopId(shopId);
    if (account == null) {
      Object status = writer.begin();
      try {
        account = new HardwareSoftwareAccount(shopId);
        account.createSoftwarePayable(total);
        writer.save(account);
        writer.commit(status);
      } finally {
        writer.rollback(status);
      }
    }
    return account.toDTO();
  }

  @Override
  public void updateHardwareSoftwareAccount(HardwareSoftwareAccountDTO dto) throws BcgogoException {
    if (dto.getShopId() == null) throw new BcgogoException("shopId is null");
    TxnWriter writer = txnDaoManager.getWriter();
    HardwareSoftwareAccount account = null;
    if (dto.getId() != null) {
      account = writer.getById(HardwareSoftwareAccount.class, dto.getId());
    }
    if (account == null) account = createHardwareSoftwareAccount(dto.getShopId(), writer);
    account.fromDTO(dto);
    Object status = writer.begin();
    try {
      writer.saveOrUpdate(account);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public void updateHardwareSoftwareAccount(HardwareSoftwareAccountDTO dto, TxnWriter writer) throws BcgogoException {
    if (dto.getShopId() == null) throw new BcgogoException("shopId is null");
    HardwareSoftwareAccount account = null;
    if (dto.getId() != null) {
      account = writer.getById(HardwareSoftwareAccount.class, dto.getId());
    }
    if (account == null) account = createHardwareSoftwareAccount(dto.getShopId(), writer);
    account.fromDTO(dto);
    writer.saveOrUpdate(account);
  }

  @Override
  public Map<String, Object> countHardwareSoftwareAccount(AccountSearchCondition condition) {
    Map<String, Object> result = new HashMap<String, Object>();
    result.put("success", true);
    TxnWriter writer = txnDaoManager.getWriter();
    List<Long> shopIds;
    if (StringUtils.isNotBlank(condition.getShopName())) {
      ShopSearchCondition shopSearchCondition = new ShopSearchCondition();
      shopSearchCondition.setName(condition.getShopName());
      shopIds = ServiceManager.getService(IShopService.class).getShopIdByShopCondition(shopSearchCondition);
      if (CollectionUtil.isEmpty(shopIds)) {
        result.put("hardwareReceivedAmount", "￥0（现金￥0；银联￥0）");
        result.put("hardwareReceivableAmount", "￥0");
        result.put("hardwareTotalAmount", "￥0");
        result.put("softwareReceivableAmount", "￥0");
        result.put("softwareReceivedAmount", "￥0（现金￥0；银联￥0）");
        result.put("softwareTotalAmount", "￥0");
        result.put("totalReceivedAmount", "￥0（现金￥0；银联￥0）");
        result.put("totalReceivableAmount", "￥0");
        result.put("totalAmount", "￥0");
        return result;
      }
      condition.setShopIds(shopIds);
    }
    Object[] totalAccount = writer.countHardwareSoftwareAccount(condition);
    Object[] softwareAccount = writer.countSoftwareAccount(condition);
    Object[] hardwareAccount = writer.countHardwareAccount(condition);
    result.put("hardwareReceivedAmount", "￥" + (hardwareAccount[2] == null ? 0 : NumberUtil.round((Double) hardwareAccount[2], 2)) + "（现金￥" +
        writer.countHardwareSoftwarePaidAmountAccount(PaymentMethod.DOOR_CHARGE, PaymentType.HARDWARE, condition) + "；银联￥" +
        writer.countHardwareSoftwarePaidAmountAccount(PaymentMethod.ONLINE_PAYMENT, PaymentType.HARDWARE, condition) + "）");
    result.put("hardwareReceivableAmount", "￥" + (hardwareAccount[1] == null ? 0 : NumberUtil.round((Double) hardwareAccount[1], 2)));
    result.put("hardwareTotalAmount", "￥" + (hardwareAccount[0] == null ? 0 : NumberUtil.round((Double) hardwareAccount[0], 2)));
    result.put("softwareReceivableAmount", "￥" + (softwareAccount[1] == null ? 0 : NumberUtil.round((Double) softwareAccount[1], 2)));
    result.put("softwareReceivedAmount", "￥" + (softwareAccount[2] == null ? 0 : NumberUtil.round((Double) softwareAccount[2], 2))
        + "（现金￥" + NumberUtil.round(writer.countHardwareSoftwarePaidAmountAccount(PaymentMethod.DOOR_CHARGE, PaymentType.SOFTWARE, condition), 2)
        + "；银联￥" + NumberUtil.round(writer.countHardwareSoftwarePaidAmountAccount(PaymentMethod.ONLINE_PAYMENT, PaymentType.SOFTWARE, condition), 2) + "）");
    result.put("softwareTotalAmount", "￥" + (softwareAccount[0] == null ? 0 : NumberUtil.round((Double) softwareAccount[0], 2)));
    result.put("totalReceivedAmount", "￥" + (totalAccount[2] == null ? 0 : NumberUtil.round((Double) totalAccount[2], 2))
        + "（现金￥" + NumberUtil.round(writer.countHardwareSoftwarePaidAmountAccount(PaymentMethod.DOOR_CHARGE, null, condition), 2)
        + "；银联￥" + NumberUtil.round(writer.countHardwareSoftwarePaidAmountAccount(PaymentMethod.ONLINE_PAYMENT, null, condition), 2) + "）");
    result.put("totalReceivableAmount", "￥" + (totalAccount[1] == null ? 0 : NumberUtil.round((Double) totalAccount[1], 2)));
    result.put("totalAmount", "￥" + (totalAccount[0] == null ? 0 : NumberUtil.round((Double) totalAccount[0], 2)));
    return result;
  }

  private HardwareSoftwareAccount createHardwareSoftwareAccount(long shopId, TxnWriter writer) {
    if (writer == null) writer = txnDaoManager.getWriter();
    HardwareSoftwareAccount account = new HardwareSoftwareAccount(shopId);
    writer.save(account);
    return account;
  }

}
