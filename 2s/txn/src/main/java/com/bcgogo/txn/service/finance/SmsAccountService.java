package com.bcgogo.txn.service.finance;

import com.bcgogo.common.Result;
import com.bcgogo.config.ShopSearchCondition;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.model.ShopBalance;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.service.IShopBalanceService;
import com.bcgogo.config.service.IShopService;
import com.bcgogo.constant.SmsRechargeConstants;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.enums.PaymentWay;
import com.bcgogo.enums.RechargeMethod;
import com.bcgogo.enums.sms.SenderType;
import com.bcgogo.enums.sms.SmsSendScene;
import com.bcgogo.enums.sms.StatType;
import com.bcgogo.enums.txn.finance.BcgogoReceivableStatus;
import com.bcgogo.enums.txn.finance.SmsCategory;
import com.bcgogo.exception.BcgogoException;
import com.bcgogo.notification.model.OutBox;
import com.bcgogo.notification.service.INotificationService;
import com.bcgogo.notification.smsSend.SmsUtil;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.finance.*;
import com.bcgogo.txn.model.PreferentialPolicy;
import com.bcgogo.txn.model.SmsRecharge;
import com.bcgogo.txn.model.TxnDaoManager;
import com.bcgogo.txn.model.TxnWriter;
import com.bcgogo.txn.model.finance.BcgogoSmsRecord;
import com.bcgogo.txn.model.finance.ShopSmsAccount;
import com.bcgogo.txn.model.finance.ShopSmsRecord;
import com.bcgogo.txn.service.ISmsRechargeService;
import com.bcgogo.txn.service.ITxnService;
import com.bcgogo.utils.*;
import org.apache.commons.collections.CollectionUtils;
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
public class SmsAccountService implements ISmsAccountService {
  private static final Logger LOG = LoggerFactory.getLogger(SmsAccountService.class);
  private static final Long ONE_DAY_MILLISECOND = 86400000L;
  @Autowired
  private TxnDaoManager txnDaoManager;

  @Override
  public Result searchShopSmsAccountResult(SmsRecordSearchCondition condition) {
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
    result.setTotal(writer.countShopSmsAccountResult(condition));
    List<ShopSmsAccount> accountList = writer.searchShopSmsAccountResult(condition);
    Map<Long, ShopDTO> shopDTOMap = null;
    shopIds.clear();
    for (ShopSmsAccount account : accountList) {
      shopIds.add(account.getShopId());
    }
    if (CollectionUtil.isNotEmpty(shopIds)) {
      shopDTOMap = ServiceManager.getService(IShopService.class).getShopByShopIds(shopIds.toArray(new Long[shopIds.size()]));
    }
    List<ShopSmsAccountDTO> accountDTOList = new ArrayList<ShopSmsAccountDTO>();
    ShopSmsAccountDTO accountDTO;
    ShopDTO shopDTO;
    for (ShopSmsAccount account : accountList) {
      accountDTO = account.toDTO();
      if (shopDTOMap != null) {
        shopDTO = shopDTOMap.get(accountDTO.getShopId());
        if (shopDTO != null) accountDTO.setShopName(shopDTO.getName());
      }
      accountDTOList.add(accountDTO);
    }
    result.setData(accountDTOList);
    return result;
  }

  @Override
  public Result searchShopSmsRecordResult(SmsRecordSearchCondition condition) {
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
    result.setTotal(writer.countShopSmsRecordResult(condition));
    List<ShopSmsRecord> recordList = writer.searchShopSmsRecordResult(condition);
    Map<Long, ShopDTO> shopDTOMap = null;
    shopIds.clear();
    for (ShopSmsRecord record : recordList) {
      shopIds.add(record.getShopId());
    }
    if (CollectionUtil.isNotEmpty(shopIds)) {
      shopDTOMap = ServiceManager.getService(IShopService.class).getShopByShopIds(shopIds.toArray(new Long[shopIds.size()]));
    }
    List<ShopSmsRecordDTO> recordDTOList = new ArrayList<ShopSmsRecordDTO>();
    ShopSmsRecordDTO recordDTO;
    ShopDTO shopDTO;
    for (ShopSmsRecord record : recordList) {
      recordDTO = record.toDTO();
      if (shopDTOMap != null) {
        shopDTO = shopDTOMap.get(recordDTO.getShopId());
        if (shopDTO != null) recordDTO.setShopName(shopDTO.getName());
      }
      if(record.getSmsCategory().equals(SmsCategory.SHOP_CONSUME) && record.getSmsSendScene() != null) {
        recordDTO.setSmsSendSceneStr(record.getSmsSendScene().getName());
      }
      recordDTOList.add(recordDTO);
    }
    result.setData(recordDTOList);
    return result;
  }

  @Override
  public Result searchBcgogoSmsRecordResult(SmsRecordSearchCondition condition) {
    Result result = new Result(true);
    TxnWriter writer = txnDaoManager.getWriter();
    result.setTotal(writer.countBcgogoSmsRecordResult(condition));
    List<BcgogoSmsRecord> recordList = writer.searchBcgogoSmsRecordResult(condition);
    List<BcgogoSmsRecordDTO> recordDTOList = new ArrayList<BcgogoSmsRecordDTO>();
    BcgogoSmsRecordDTO recordDTO;
    for (BcgogoSmsRecord record : recordList) {
      recordDTO = record.toDTO();
      recordDTOList.add(recordDTO);
    }
    result.setData(recordDTOList);
    return result;
  }

  @Override
  public Result shopSmsRecordStatistics(SmsRecordSearchCondition condition) {
    Result result = new Result(true);
    Object[] recharge = new Object[2], handsel = new Object[2], consume = new Object[2];
    List<Long> shopIds;
    if (StringUtils.isNotBlank(condition.getShopName())) {
      ShopSearchCondition shopSearchCondition = new ShopSearchCondition();
      shopSearchCondition.setName(condition.getShopName());
      shopIds = ServiceManager.getService(IShopService.class).getShopIdByShopCondition(shopSearchCondition);
      if (CollectionUtil.isEmpty(shopIds)) {
        result.setData("短信充值0元/0条；短信赠送0元/0条；短信消费0元/0条；");
        return result;
      }
      condition.setShopIds(shopIds);
    }
    TxnWriter writer = txnDaoManager.getWriter();
    if (!ArrayUtil.isEmpty(condition.getSmsCategories())) {
      for (SmsCategory category : condition.getSmsCategories()) {
        if (category == SmsCategory.SHOP_RECHARGE) {
          recharge = writer.shopSmsRecordStatistics(condition, SmsCategory.SHOP_RECHARGE, SmsCategory.CRM_RECHARGE);
        }
        if (category == SmsCategory.REGISTER_HANDSEL) {
          handsel = writer.shopSmsRecordStatistics(condition, SmsCategory.REGISTER_HANDSEL, SmsCategory.RECOMMEND_HANDSEL, SmsCategory.RECHARGE_HANDSEL);
        }
        if (category == SmsCategory.SHOP_CONSUME) {
          consume = writer.shopSmsRecordStatistics(condition, SmsCategory.SHOP_CONSUME);
        }
      }
    } else {
      recharge = writer.shopSmsRecordStatistics(condition, SmsCategory.SHOP_RECHARGE, SmsCategory.CRM_RECHARGE);
      handsel = writer.shopSmsRecordStatistics(condition, SmsCategory.REGISTER_HANDSEL, SmsCategory.RECOMMEND_HANDSEL, SmsCategory.RECHARGE_HANDSEL);
      consume = writer.shopSmsRecordStatistics(condition, SmsCategory.SHOP_CONSUME);
    }
    result.setData("短信充值" + (recharge[0] == null ? 0 : NumberUtil.round((Double) recharge[0], 2)) + "元/" + (recharge[1] == null ? 0 : recharge[1]) + "条；短信赠送"
        + (handsel[0] == null ? 0 : NumberUtil.round((Double) handsel[0], 2)) + "元/" + (handsel[1] == null ? 0 : handsel[1]) + "条；短信消费 "
        + (consume[0] == null ? 0 : NumberUtil.round((Double) consume[0], 2)) + "元/" + (consume[1] == null ? 0 : consume[1]) + "条；");
    return result;
  }

  @Override
  public Result shopSmsAccountStatistics(SmsRecordSearchCondition condition) {
    Result result = new Result(true);
    TxnWriter writer = txnDaoManager.getWriter();
    List<Long> shopIds;
    if (StringUtils.isNotBlank(condition.getShopName())) {
      ShopSearchCondition shopSearchCondition = new ShopSearchCondition();
      shopSearchCondition.setName(condition.getShopName());
      shopIds = ServiceManager.getService(IShopService.class).getShopIdByShopCondition(shopSearchCondition);
      if (CollectionUtil.isEmpty(shopIds)) {
        result.setData("充值总额：￥0；消费总条数：0；剩余总条数：0");
        return result;
      }
      condition.setShopIds(shopIds);
    }
    Object[] statistics = writer.shopSmsAccountStatistics(condition);
    result.setData("充值总额：￥" + (statistics[0] == null ? 0 : NumberUtil.round((Double) statistics[0], 2)) + "；消费总条数："
        +(statistics[1] == null ? 0 : statistics[1]) + "；剩余总条数：" + (statistics[2] == null ? 0 : statistics[2]));
    return result;
  }

  public List<BcgogoSmsAccountDTO> getBcgogoSmsTotalAccount() {
    List<BcgogoSmsAccountDTO> list = new ArrayList<BcgogoSmsAccountDTO>();
    BcgogoSmsAccountDTO bcgogo = new BcgogoSmsAccountDTO();
    BcgogoSmsAccountDTO shop = new BcgogoSmsAccountDTO();
    TxnWriter writer = txnDaoManager.getWriter();

    Object[] totalRecharges,totalCrmRecharges;
    Long number;

    totalRecharges = writer.countSmsTotalRecharge(SmsCategory.BCGOGO_RECHARGE);
    number = writer.countSmsAccountNumberBySmsCategory(SmsCategory.REFUND);
    bcgogo.setRefundNumber(number);
    bcgogo.setTotalRechargeBalance((Double) totalRecharges[0]);
    bcgogo.setTotalRechargeNumber((Long) totalRecharges[1]);
    bcgogo.setHandSelNumber(writer.countSmsAccountNumberBySmsCategory(SmsCategory.getHandsel()));
    bcgogo.setConsumptionNumber(writer.countSmsAccountNumberBySmsCategory(SmsCategory.BCGOGO_CONSUME) - number);
    bcgogo.calculateBcgogoSurplus();
    bcgogo.setType("BCGOGO");

    totalRecharges = writer.countSmsTotalRecharge(SmsCategory.SHOP_RECHARGE);
    totalCrmRecharges = writer.countSmsTotalRecharge(SmsCategory.CRM_RECHARGE);
    shop.setRefundNumber(number);
    shop.setTotalRechargeBalance(NumberUtil.doubleVal(totalRecharges[0]) + NumberUtil.doubleVal(totalCrmRecharges[0]));
    shop.setTotalRechargeNumber(NumberUtil.longValue(totalRecharges[1]) + NumberUtil.longValue(totalCrmRecharges[1]));
    shop.setConsumptionNumber(writer.countSmsAccountNumberBySmsCategory(SmsCategory.SHOP_CONSUME) - number);
    totalRecharges = writer.countShopSmsTotalRecharge();
    shop.setSurplusNumber((Long) totalRecharges[1]);
    shop.setType("SHOP");

    list.add(shop);
    list.add(bcgogo);
    return list;
  }

  @Override
  public void createBcgogoRecharge(BcgogoSmsRecordDTO dto) {
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      BcgogoSmsRecord bcgogoSmsRecord = new BcgogoSmsRecord(dto);
      writer.save(bcgogoSmsRecord);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public void createBcgogoConsumption(double balance, long number) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      Date date = new Date();
      Long startTime = DateUtil.convertDateDateShortToDateLong("yyyy-MM-dd", date);
      Long endTime = startTime + ONE_DAY_MILLISECOND;
      BcgogoSmsRecord bcgogoSmsRecord = writer.getCurrentDayBcgogoSmsRecord(startTime, endTime, SmsCategory.BCGOGO_CONSUME);
      if (bcgogoSmsRecord == null) {
        bcgogoSmsRecord = new BcgogoSmsRecord(balance, number);
        bcgogoSmsRecord.setSmsCategory(SmsCategory.BCGOGO_CONSUME);
        bcgogoSmsRecord.setOperateTime(System.currentTimeMillis());
      } else {
        bcgogoSmsRecord.setBalance(bcgogoSmsRecord.getBalance() + balance);
        bcgogoSmsRecord.setNumber(bcgogoSmsRecord.getNumber() + number);
      }
      writer.saveOrUpdate(bcgogoSmsRecord);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public void createShopSmsConsumption(long shopId, double balance, long number) throws Exception {
    LOG.warn("createShopSmsConsumption start/.........");
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      Date date = new Date();
      Long startTime = DateUtil.convertDateDateShortToDateLong("yyyy-MM-dd", date);
      Long endTime = startTime + ONE_DAY_MILLISECOND;
      //bcgogo账单中每天生成一条 店铺消费短信记录
      BcgogoSmsRecord bcgogoSmsRecord = writer.getCurrentDayBcgogoSmsRecord(startTime, endTime, SmsCategory.SHOP_CONSUME);
      if (bcgogoSmsRecord == null) {
        bcgogoSmsRecord = new BcgogoSmsRecord(balance, number);
        bcgogoSmsRecord.setSmsCategory(SmsCategory.SHOP_CONSUME);
        bcgogoSmsRecord.setOperateTime(System.currentTimeMillis());
      } else {
        bcgogoSmsRecord.setBalance(bcgogoSmsRecord.getBalance() + balance);
        bcgogoSmsRecord.setNumber(bcgogoSmsRecord.getNumber() + number);
      }
      //shop账单详细 对应店铺每天生成一条 消费短信记录
      ShopSmsRecord shopSmsRecord = writer.getCurrentDayShopSmsRecord(shopId, startTime, endTime, SmsCategory.SHOP_CONSUME,StatType.DAY);
      if (shopSmsRecord == null) {
        shopSmsRecord = new ShopSmsRecord(shopId, balance, number);
        shopSmsRecord.setStatType(StatType.DAY);
        shopSmsRecord.setSmsCategory(SmsCategory.SHOP_CONSUME);
      } else {
        shopSmsRecord.setBalance(shopSmsRecord.getBalance() + balance);
        shopSmsRecord.setNumber(shopSmsRecord.getNumber() + number);
      }
      //shop账单 中对应店铺统计更新
      ShopSmsAccount account = getShopSmsAccountByShopId(shopId, writer);
      account.setConsumptionBalance(account.getConsumptionBalance() + balance);
      account.setConsumptionNumber(account.getConsumptionNumber() + number);

      account.setCurrentBalance(account.getCurrentBalance() - balance);
      account.setCurrentNumber(account.getCurrentNumber() - number);

      writer.saveOrUpdate(shopSmsRecord);
      writer.saveOrUpdate(bcgogoSmsRecord);
      writer.saveOrUpdate(account);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public Result createShopSmsRefund(ShopSmsRecordDTO dto) throws BcgogoException {
    Result result = new Result(true);
    if (dto.getShopId() == null) throw new BcgogoException("shop id is null.");
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      ShopSmsAccount account = getShopSmsAccountByShopId(dto.getShopId(), writer);
      if (NumberUtil.isZero(account.getConsumptionBalance())) {
        LOG.warn("shop[id={}] ShopSmsAccount consumption balance is 0,can't do the refund!", dto.getShopId());
        result.setSuccess(false);
        result.setMsg("该店铺消费总额为0，不能退费");
        return result;
      }
      if(NumberUtil.round(account.getConsumptionBalance(),1) < NumberUtil.round(dto.getBalance(),1)) {
        result.setSuccess(false);
        result.setMsg("退费金额不能大于消费金额");
        return result;
      }
      account.setConsumptionBalance(account.getConsumptionBalance() - dto.getBalance());
      account.setConsumptionNumber(account.getConsumptionNumber() - dto.getNumber());

      account.setCurrentBalance(dto.getBalance() + account.getCurrentBalance());
      account.setCurrentNumber(dto.getNumber() + account.getCurrentNumber());
      writer.save(new ShopSmsRecord(dto));
      writer.save(new BcgogoSmsRecord(dto));
      writer.saveOrUpdate(account);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
    ServiceManager.getService(IShopBalanceService.class).addSmsBalance(dto.getShopId(), dto.getBalance());
    return result;
  }

  @Override
  public void createShopSmsHandsel(ShopSmsRecordDTO dto) throws BcgogoException {
    if (dto.getShopId() == null) throw new BcgogoException("shop id is null.");
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      ShopSmsAccount account = getShopSmsAccountByShopId(dto.getShopId(), writer);
      account.setHandSelBalance(account.getHandSelBalance() + dto.getBalance());
      account.setHandSelNumber(account.getRechargeNumber() + dto.getNumber());

      account.setCurrentBalance(dto.getBalance() + account.getCurrentBalance());
      account.setCurrentNumber(dto.getNumber() + account.getCurrentNumber());

      writer.save(new ShopSmsRecord(dto));
      writer.saveOrUpdate(new BcgogoSmsRecord(dto));
      writer.saveOrUpdate(account);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  private ShopSmsAccount getShopSmsAccountByShopId(long shopId, TxnWriter writer) {
    ShopSmsAccount account = writer.getShopSmsAccountByShopId(shopId);
    if (account == null) {
      account = new ShopSmsAccount(shopId);
    }
    writer.save(account);
    return account;
  }

  @Override
  public Result recharge(Long userId, Long shopId, Long payeeId, Double rechargeAmount) {
    Result result = new Result(true);
    IShopBalanceService shopBalanceService = ServiceManager.getService(IShopBalanceService.class);
    ShopBalance shopBalance;
    SmsRecharge smsRecharge = new SmsRecharge();
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      smsRecharge.setShopId(shopId);
      smsRecharge.setUserId(userId);
      smsRecharge.setPayeeId(payeeId);
      smsRecharge.setRechargeAmount(rechargeAmount);
      smsRecharge.setPresentAmount(this.getPresentAmountByRechargeAmount(rechargeAmount));
      createShopSmsRecharge(smsRecharge, writer);
      List<ShopBalance> shopBalanceList = shopBalanceService.getSmsBalanceListByShopId(smsRecharge.getShopId());
      if (CollectionUtils.isEmpty(shopBalanceList)) {
        if (LOG.isDebugEnabled())
          LOG.debug("第一次");
        shopBalance = new ShopBalance();
        shopBalance.setShopId(smsRecharge.getShopId());
        shopBalance.setSmsBalance(smsRecharge.getRechargeAmount() + smsRecharge.getPresentAmount());
        shopBalance.setRechargeTotal(smsRecharge.getRechargeAmount() + smsRecharge.getPresentAmount());
      } else {
        if (LOG.isDebugEnabled())
          LOG.debug("不是第一次");
        shopBalance = shopBalanceList.get(0);
        if (LOG.isDebugEnabled()) {
          if (shopBalance.getSmsBalance() == null)
            LOG.debug("shopBalance.getShopBalance() == null");
          else
            LOG.debug("shopBalance.getShopBalance() != null");

          if (smsRecharge.getRechargeAmount() == null)
            LOG.debug("smsRecharge.getRechargeAmount() == null");
          else
            LOG.debug("smsRecharge.getRechargeAmount() != null");

          if (shopBalance.getRechargeTotal() == null) {
            LOG.debug("shopBalance.getRechargeTotal() == null");
          } else {
            LOG.debug("shopBalance.getRechargeTotal() != null");
          }
        }
        LOG.info("Before smsBalance table smsBalance is " + shopBalance.getSmsBalance());
        LOG.info("Before smsBalance table rechargeTotal is " + shopBalance.getRechargeTotal());
        shopBalance.setSmsBalance(NumberUtil.doubleVal(shopBalance.getSmsBalance()) + NumberUtil.doubleVal(smsRecharge.getRechargeAmount()) + NumberUtil.doubleVal(smsRecharge.getPresentAmount()));
        shopBalance.setRechargeTotal(NumberUtil.doubleVal(shopBalance.getRechargeTotal()) + NumberUtil.doubleVal(smsRecharge.getRechargeAmount()));
      }
      smsRecharge.setPayTime(System.currentTimeMillis());
      smsRecharge.setRechargeTime(smsRecharge.getPayTime());
      smsRecharge.setState(SmsRechargeConstants.RechargeState.RECHARGE_STATE_COMPLETE);
      smsRecharge.setStatus(BcgogoReceivableStatus.PENDING_REVIEW);
      smsRecharge.setSmsBalance(shopBalance.getSmsBalance());
      smsRecharge.setPaymentWay(PaymentWay.CASH);
      smsRecharge.setRechargeMethod(RechargeMethod.CRM_RECHARGE);
      smsRecharge.setReceiptNo(ServiceManager.getService(ITxnService.class).getBcgogoOrderReceiptNo(ShopConstant.BC_ADMIN_SHOP_ID, OrderTypes.BCGOGO_SMS_RECHARGE_RECEIVABLE_ORDER,System.currentTimeMillis()));
      writer.save(smsRecharge);
      writer.commit(status);
      shopBalanceService.saveSmsBalance(shopBalance.toDTO());
      LOG.info("After smsBalance table smsBalance is " + shopBalance.getSmsBalance());
      LOG.info("After smsBalance table rechargeTotal is " + shopBalance.getRechargeTotal());
    } catch (Exception e) {
      LOG.error(e.getMessage(),e);
      result.setSuccess(false);
    }
    finally {
      writer.rollback(status);
    }
    ISmsRechargeService smsRechargeService = ServiceManager.getService(ISmsRechargeService.class);
    smsRechargeService.generateBcgogoReceivableRecord(smsRechargeService.getSmsRechargeById(smsRecharge.getId()));
    return result;
  }

  public void createShopSmsRecharge(SmsRecharge smsRecharge, TxnWriter writer) {
    ShopSmsRecordDTO dto = new ShopSmsRecordDTO();
    dto.setShopId(smsRecharge.getShopId());
    dto.setBalance(smsRecharge.getRechargeAmount());
    dto.setNumber(Math.round(smsRecharge.getRechargeAmount() * 10));
    dto.setSmsCategory(SmsCategory.CRM_RECHARGE);
    ShopSmsRecordDTO handSelDTO = null;
    if(smsRecharge.getPresentAmount() > 0) {
      handSelDTO = new ShopSmsRecordDTO();
      handSelDTO.setShopId(smsRecharge.getShopId());
      handSelDTO.setBalance(smsRecharge.getPresentAmount());
      handSelDTO.setNumber(Math.round(smsRecharge.getPresentAmount() * 10));
      handSelDTO.setSmsCategory(SmsCategory.RECHARGE_HANDSEL);
    }
    ShopSmsAccount account = writer.getShopSmsAccountByShopId(dto.getShopId());
    if (account == null) {
      account = new ShopSmsAccount(dto.getShopId());
    }
    writer.save(account);
    account.setRechargeBalance(account.getRechargeBalance() + dto.getBalance());
    account.setRechargeNumber(account.getRechargeNumber() + dto.getNumber());
    if(handSelDTO != null) {
      account.setHandSelBalance(account.getHandSelBalance() + handSelDTO.getBalance());
      account.setHandSelNumber(account.getHandSelNumber() + handSelDTO.getNumber());
      account.setCurrentBalance(dto.getBalance() + account.getCurrentBalance() + handSelDTO.getBalance());
      account.setCurrentNumber(dto.getNumber() + account.getCurrentNumber() + handSelDTO.getNumber());
    } else {
      account.setCurrentBalance(dto.getBalance() + account.getCurrentBalance());
      account.setCurrentNumber(dto.getNumber() + account.getCurrentNumber());
    }

    writer.save(new ShopSmsRecord(dto));
    if(handSelDTO != null) {
      writer.save(new ShopSmsRecord(handSelDTO));
    }
    BcgogoSmsRecord bcgogoSmsRecord =new BcgogoSmsRecord(dto);
    bcgogoSmsRecord.setRechargeTime(System.currentTimeMillis());
    writer.saveOrUpdate(bcgogoSmsRecord);
    if(handSelDTO != null) {
      BcgogoSmsRecord bcgogoSmsRecord2 =new BcgogoSmsRecord(handSelDTO);
      writer.saveOrUpdate(bcgogoSmsRecord2);
    }
    writer.saveOrUpdate(account);
  }

  @Override
  public Double getPresentAmountByRechargeAmount(Double rechargeAmount) {
    ISmsRechargeService smsRechargeService = ServiceManager.getService(ISmsRechargeService.class);
    TxnWriter writer = txnDaoManager.getWriter();
    List<PreferentialPolicy> preferentialPolicyList = writer.getSmsPreferentialPolicy();
    if(CollectionUtil.isEmpty(preferentialPolicyList)) {
      return 0.0;
    }
    for(int i = 0; i < preferentialPolicyList.size(); i++) {
      PreferentialPolicy preferentialPolicy = preferentialPolicyList.get(i);
      if(preferentialPolicy.getRechargeAmount() > rechargeAmount) {
        if(i == 0) {
          return 0.0;
        } else {
          return preferentialPolicyList.get(i - 1).getPresentAmount();
        }
      }
    }
    return preferentialPolicyList.get(preferentialPolicyList.size() - 1).getPresentAmount();
  }

  @Override
  public void initShopSmsAccount() {
    TxnWriter writer = txnDaoManager.getWriter();
    SmsRecordSearchCondition condition = new SmsRecordSearchCondition();
    condition.setLimit(Integer.MAX_VALUE);
    List<ShopSmsAccount> accountList = writer.searchShopSmsAccountResult(condition);
    List<Long> shopIds = new ArrayList<Long>();
    Object status = writer.begin();
    try {
      for(ShopSmsAccount shopSmsAccount : accountList) {
        shopIds.clear();
        shopIds.add(shopSmsAccount.getShopId());
        condition.setShopIds(shopIds);
        Object[] consume = writer.shopSmsRecordStatistics(condition,SmsCategory.SHOP_CONSUME);
        if(NumberUtil.doubleVal(consume[0]) < NumberUtil.doubleVal(shopSmsAccount.getConsumptionBalance())) {
          //当大于时，不新增一条负值的记录，由生产上测试，再根据具体情况update  record
          ShopSmsRecord shopSmsRecord = new ShopSmsRecord();
          shopSmsRecord.setShopId(shopSmsAccount.getShopId());
          shopSmsRecord.setSmsCategory(SmsCategory.SHOP_CONSUME);
          shopSmsRecord.setBalance(NumberUtil.doubleVal(shopSmsAccount.getConsumptionBalance()) - NumberUtil.doubleVal(consume[0]));
          shopSmsRecord.setNumber(NumberUtil.longValue(shopSmsAccount.getConsumptionNumber()) - NumberUtil.longValue(NumberUtil.longValue(consume[1])));
          shopSmsRecord.setOperateTime(System.currentTimeMillis());
          shopSmsRecord.setFlag(false);
          shopSmsRecord.setStatType(StatType.DAY);
          writer.save(shopSmsRecord);
        }
      }

      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public void initShopSmsConsumeAccount() {
    ISmsAccountService smsAccountService = ServiceManager.getService(ISmsAccountService.class);
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      SmsRecordSearchCondition condition = new SmsRecordSearchCondition();
      condition.setLimit(Integer.MAX_VALUE);
      Result result = smsAccountService.searchShopSmsAccountResult(condition);
      if(result != null && result.getData() != null) {
         List<ShopSmsAccountDTO> shopSmsAccountDTOList = (List<ShopSmsAccountDTO>)result.getData();
        if(CollectionUtil.isNotEmpty(shopSmsAccountDTOList)) {
          for(ShopSmsAccountDTO account : shopSmsAccountDTOList) {
            ShopSmsRecord shopSmsRecord = new ShopSmsRecord();
            shopSmsRecord.setShopId(account.getShopId());
            shopSmsRecord.setStatType(StatType.ONE_TIME);
            shopSmsRecord.setSmsSendScene(SmsSendScene.INIT);
            shopSmsRecord.setBalance(account.getConsumptionBalance());
            shopSmsRecord.setFlag(false);
            shopSmsRecord.setNumber(account.getConsumptionNumber());
            shopSmsRecord.setOperateTime(System.currentTimeMillis());
            shopSmsRecord.setSmsCategory(SmsCategory.SHOP_CONSUME);
            writer.save(shopSmsRecord);
          }
        }
      }
      writer.commit(status);
    } catch (Exception e) {
        LOG.error(e.getMessage(),e);
    } finally {
      writer.rollback(status);
    }

  }

  @Override
  public void saveOrUpdateShopSmsRecord(ShopSmsRecordDTO shopSmsRecordDTO) {
    if(shopSmsRecordDTO == null) return;
    ShopSmsRecord shopSmsRecord = null;
    TxnWriter writer = txnDaoManager.getWriter();
    if(shopSmsRecordDTO.getId() == null) {
      shopSmsRecord = new ShopSmsRecord(shopSmsRecordDTO);
    } else {
      shopSmsRecord = writer.getById(ShopSmsRecord.class, shopSmsRecordDTO.getId());
      shopSmsRecord.fromDTO(shopSmsRecordDTO);
    }
    Object status = writer.begin();
    try {
      writer.saveOrUpdate(shopSmsRecord);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }

  }

  @Override
  public ShopSmsRecordDTO getShopSmsRecordBySmsId(Long shopId, Long smsId) {
    if(shopId == null || smsId == null) return null;
    TxnWriter writer = txnDaoManager.getWriter();
    ShopSmsRecord shopSmsRecord = writer.getShopSmsRecordBySmsId(shopId,smsId);
    if(shopSmsRecord != null) {
      return shopSmsRecord.toDTO();
    }
    return null;
  }
}
