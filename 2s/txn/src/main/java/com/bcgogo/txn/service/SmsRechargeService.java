package com.bcgogo.txn.service;

import com.bcgogo.common.Result;
import com.bcgogo.config.ShopSearchCondition;
import com.bcgogo.config.dto.ShopBalanceDTO;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.model.ShopBalance;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.service.IShopBalanceService;
import com.bcgogo.config.service.IShopService;
import com.bcgogo.constant.SmsRechargeConstants;
import com.bcgogo.enums.DeletedType;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.enums.PaymentWay;
import com.bcgogo.enums.RechargeMethod;
import com.bcgogo.enums.sms.StatType;
import com.bcgogo.enums.txn.finance.*;
import com.bcgogo.exception.BcgogoException;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.SmsRechargeCompleteDTO;
import com.bcgogo.txn.dto.SmsRechargeDTO;
import com.bcgogo.txn.dto.finance.BcgogoReceivableOrderDTO;
import com.bcgogo.txn.dto.finance.ShopSmsRecordDTO;
import com.bcgogo.txn.dto.finance.SmsRechargeSearchCondition;
import com.bcgogo.txn.dto.finance.SmsRecordSearchCondition;
import com.bcgogo.txn.model.PreferentialPolicy;
import com.bcgogo.txn.model.SmsRecharge;
import com.bcgogo.txn.model.TxnDaoManager;
import com.bcgogo.txn.model.TxnWriter;
import com.bcgogo.txn.model.finance.*;
import com.bcgogo.user.dto.UserDTO;
import com.bcgogo.user.service.IUserService;
import com.bcgogo.user.service.permission.IUserCacheService;
import com.bcgogo.utils.CollectionUtil;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.ShopConstant;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: MZDong
 * Date: 11-12-20
 * Time: 下午4:33
 * To change this template use File | Settings | File Templates.
 */
@Component
public class SmsRechargeService implements ISmsRechargeService {
  private static final Logger LOG = LoggerFactory.getLogger(SmsRechargeService.class);

  @Override
  public SmsRechargeDTO createSmsRecharge(SmsRechargeDTO smsRechargeDTO) {
    if (smsRechargeDTO == null) return null;

    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();

    try {
      SmsRecharge smsRecharge = new SmsRecharge(smsRechargeDTO);
      writer.save(smsRecharge);
      writer.commit(status);

      smsRechargeDTO.setId(smsRecharge.getId());

      return smsRechargeDTO;
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public SmsRechargeDTO updateSmsRecharge(SmsRechargeDTO smsRechargeDTO) {
    if (smsRechargeDTO == null) return null;

    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();

    try {
      Long id = smsRechargeDTO.getId();
      if (id == null) return null;

      SmsRecharge smsRecharge = writer.getById(SmsRecharge.class, id);
      if (smsRecharge == null) return null;

      smsRecharge.fromDTO(smsRechargeDTO);
      writer.save(smsRecharge);
      writer.commit(status);

      return smsRechargeDTO;
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public SmsRechargeDTO getSmsRechargeById(long smsRechargeId) {
    TxnWriter writer = txnDaoManager.getWriter();

    SmsRecharge smsRecharge = writer.getById(SmsRecharge.class, smsRechargeId);

    if (smsRecharge == null) return null;
    SmsRechargeDTO smsRechargeDTO = smsRecharge.toDTO();
    if(smsRecharge.getPayeeId() != null) {
      UserDTO userDTO = ServiceManager.getService(IUserCacheService.class).getUserById(smsRecharge.getPayeeId());
      if(userDTO != null) {
        smsRechargeDTO.setPayeeName(userDTO.getName());
      }
    }
    return smsRechargeDTO;
  }

  //根据店面ID获取短信充值的列表
  @Override
  public List<SmsRechargeDTO> getSmsRechargeByShopId(long shopId) {
    TxnWriter writer = txnDaoManager.getWriter();

    List<SmsRechargeDTO> smsRechargeDTOList = new ArrayList<SmsRechargeDTO>();

    for (SmsRecharge smsRecharge : writer.getSmsRechargeByShopId(shopId)) {
      smsRechargeDTOList.add(smsRecharge.toDTO());
    }

    return smsRechargeDTOList;
  }

  //根据店面ID获取短信充值记录数
  @Override
  public int countShopSmsRecharge(long shopId) {
    TxnWriter writer = txnDaoManager.getWriter();

    return writer.countShopSmsRecharge(shopId);
  }

  //根据店面ID、页码与每页条数获取短信充值的列表
  @Override
  public List<SmsRechargeDTO> getShopSmsRechargeList(long shopId, int pageNo, int pageSize) {
    TxnWriter writer = txnDaoManager.getWriter();

    List<SmsRechargeDTO> smsRechargeDTOList = new ArrayList<SmsRechargeDTO>();
    List<SmsRecharge> smsRechargeList = writer.getShopSmsRechargeList(shopId, pageNo, pageSize);
    for (SmsRecharge smsRecharge : smsRechargeList) {
      smsRechargeDTOList.add(smsRecharge.toDTO());
    }
    return smsRechargeDTOList;
  }

  @Override
  public SmsRechargeDTO getSmsRechargeByRechargeNumber(String rechargeNumber) {
    TxnWriter writer = txnDaoManager.getWriter();

    List<SmsRecharge> listSmsRecharge = writer.getSmsRechargeByRechargeNumber(rechargeNumber);

    if (listSmsRecharge.size() == 0) return null;
    return listSmsRecharge.get(0).toDTO();
  }

  @Override
  public SmsRechargeDTO updateSmsRechargePayTime(long payTime, String rechargeNumber) {
    TxnWriter writer = txnDaoManager.getWriter();
    writer.updateSmsRechargePayTime(payTime, rechargeNumber);

    List<SmsRecharge> listSmsRecharge = writer.getSmsRechargeByRechargeNumber(rechargeNumber);

    if (listSmsRecharge.size() == 0) return null;
    return listSmsRecharge.get(0).toDTO();
  }

  public void createShopSmsRecharge(SmsRecharge smsRecharge, TxnWriter writer) throws BcgogoException {
    ShopSmsRecordDTO dto = new ShopSmsRecordDTO();
    dto.setShopId(smsRecharge.getShopId());
    dto.setBalance(smsRecharge.getRechargeAmount());
    dto.setNumber(Math.round(smsRecharge.getRechargeAmount() * 10));
    dto.setSmsCategory(SmsCategory.SHOP_RECHARGE);
    ShopSmsRecordDTO handSelDTO = null;
    if(NumberUtil.doubleVal(smsRecharge.getPresentAmount()) > 0) {
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
  public void completeSmsRecharge(long smsRechargeId) throws Exception {
    IShopBalanceService shopBalanceService = ServiceManager.getService(IShopBalanceService.class);
    ShopBalance shopBalance;
    SmsRecharge smsRecharge;
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      smsRecharge = writer.getById(SmsRecharge.class, smsRechargeId);
      if (smsRecharge == null) return;
      if (smsRecharge.getState() == SmsRechargeConstants.RechargeState.RECHARGE_STATE_COMPLETE) return;
      createShopSmsRecharge(smsRecharge, writer);
      List<ShopBalance> shopBalanceList = shopBalanceService.getSmsBalanceListByShopId(smsRecharge.getShopId());
      if (CollectionUtils.isEmpty(shopBalanceList)) {
        if (LOG.isDebugEnabled())
          LOG.debug("第一次");
        shopBalance = new ShopBalance();
        shopBalance.setShopId(smsRecharge.getShopId());
        shopBalance.setSmsBalance(NumberUtil.doubleVal(smsRecharge.getRechargeAmount()) + NumberUtil.doubleVal(smsRecharge.getPresentAmount()));
        shopBalance.setRechargeTotal(smsRecharge.getRechargeAmount());
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
      smsRecharge.setState(SmsRechargeConstants.RechargeState.RECHARGE_STATE_COMPLETE);
      smsRecharge.setStatus(BcgogoReceivableStatus.PENDING_REVIEW);
      smsRecharge.setSmsBalance(shopBalance.getSmsBalance());
      smsRecharge.setReceiptNo(ServiceManager.getService(ITxnService.class).getBcgogoOrderReceiptNo(ShopConstant.BC_ADMIN_SHOP_ID, OrderTypes.BCGOGO_SMS_RECHARGE_RECEIVABLE_ORDER,System.currentTimeMillis()));
      writer.save(smsRecharge);
      writer.commit(status);
      LOG.info("After smsBalance table smsBalance is " + shopBalance.getSmsBalance());
      LOG.info("After smsBalance table rechargeTotal is " + shopBalance.getRechargeTotal());
    } finally {
      writer.rollback(status);
    }
    shopBalanceService.saveSmsBalance(shopBalance.toDTO());
    //自动生成记录
    this.generateBcgogoReceivableRecord(this.getSmsRechargeById(smsRechargeId));
  }

  @Override
  public void failSmsRecharge(Long smsRechargeId) {
    SmsRecharge smsRecharge;
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      smsRecharge = writer.getById(SmsRecharge.class, smsRechargeId);
      if (smsRecharge == null) return;
      smsRecharge.setState(SmsRechargeConstants.RechargeState.RECHARGE_STATE_FAIL);
      smsRecharge.setStatus(BcgogoReceivableStatus.TO_BE_PAID);
      writer.save(smsRecharge);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  /**
   * 充值完成后获取返回结果页面的信息封装类
   *
   * @param smsRechargeDTO
   * @param currentPage
   * @param pageSize
   * @return
   */
  @Override
  public SmsRechargeCompleteDTO getSmsRechargeCompleteInfo(SmsRechargeDTO smsRechargeDTO, int currentPage, int pageSize) {
    SmsRechargeCompleteDTO smsRechargeCompleteDTO = new SmsRechargeCompleteDTO();
    IUserService userService = ServiceManager.getService(IUserService.class);
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    ISmsRechargeService smsRechargeService = ServiceManager.getService(ISmsRechargeService.class);
    IShopBalanceService shopBalanceService = ServiceManager.getService(IShopBalanceService.class);
    //用户名称
    UserDTO userDTO = userService.getUserByUserId(smsRechargeDTO.getUserId());
    smsRechargeCompleteDTO.setUserName(userDTO == null ? "" : userDTO.getUserName());
    //店铺名称
    ShopDTO shopDTO = configService.getShopById(smsRechargeDTO.getShopId());
    smsRechargeCompleteDTO.setShopName(shopDTO == null ? "" : shopDTO.getName());
    //短信余额、充值总金额
    ShopBalanceDTO shopBalanceDTO = shopBalanceService.getSmsBalanceByShopId(smsRechargeDTO.getShopId());
    smsRechargeCompleteDTO.setSmsBalance(shopBalanceDTO == null ? -1 : shopBalanceDTO.getSmsBalance());
    smsRechargeCompleteDTO.setRechargeTotal(shopBalanceDTO == null ? -1 : shopBalanceDTO.getRechargeTotal());
    //充值记录总数
    int rechargeHistoryTotal = smsRechargeService.countShopSmsRecharge(smsRechargeDTO.getShopId());
    smsRechargeCompleteDTO.setRechargeHistoryTotal(rechargeHistoryTotal);
    //充值记录列表
    if (rechargeHistoryTotal > 0) {
      smsRechargeCompleteDTO.setSmsRechargeDTOList(smsRechargeService.getShopSmsRechargeList(smsRechargeDTO.getShopId(), currentPage, pageSize));
    }
    return smsRechargeCompleteDTO;
  }

  @Override
  public List<SmsRechargeDTO> getShopSmsRechargeList(String startTime, String endTime, String money, String other, Long shopId, int pageNo, int pageSize) {
    TxnWriter writer = txnDaoManager.getWriter();
    List<SmsRechargeDTO> smsRechargeDTOList = writer.getShopSmsRechargeList(startTime, endTime, money, shopId, other.trim(), pageNo, pageSize);
    return smsRechargeDTOList;
  }

  @Override
  public int countShopSmsRecharge(String startTime, String endTime, String money, String other, Long shopId) {
    TxnWriter writer = txnDaoManager.getWriter();

    return writer.countShopSmsRecharge(startTime, endTime, money, other.trim(), shopId);
  }

  @Override
  public Result searchSmsRechargeResult(SmsRechargeSearchCondition condition) {
    Result result = new Result(true);
    List<Long> shopIds = new ArrayList<Long>();
    Set<Long> payeeIds = new HashSet<Long>();
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
    result.setTotal(writer.countSmsRecharge(condition));
    List<SmsRecharge> smsRechargeList = writer.searchSmsRechargeResult(condition);
    List<SmsRechargeDTO> smsRechargeDTOList = new ArrayList<SmsRechargeDTO>();
    if(CollectionUtil.isNotEmpty(smsRechargeList)) {
      shopIds.clear();
      for (SmsRecharge smsRecharge : smsRechargeList) {
        if (smsRecharge.getShopId() != null) shopIds.add(smsRecharge.getShopId());
        if (smsRecharge.getPayeeId() != null) payeeIds.add(smsRecharge.getPayeeId());
      }
      Map<Long, ShopDTO> shopDTOMap = new HashMap<Long, ShopDTO>();
      Map<Long, UserDTO> userDTOMap = new HashMap<Long, UserDTO>();
      if (CollectionUtil.isNotEmpty(shopIds)) {
        shopDTOMap = ServiceManager.getService(IShopService.class).getShopByShopIds(shopIds.toArray(new Long[shopIds.size()]));
      }
      if(CollectionUtil.isNotEmpty(payeeIds)) {
        userDTOMap = ServiceManager.getService(IUserCacheService.class).getUserMap(payeeIds);
      }
      for(SmsRecharge smsRecharge : smsRechargeList) {
         SmsRechargeDTO smsRechargeDTO = smsRecharge.toDTO();
         ShopDTO shopDTO = shopDTOMap.get(smsRecharge.getShopId());
         if (shopDTO != null){
           smsRechargeDTO.setShopName(shopDTO.getName());
         }
         UserDTO userDTO = userDTOMap.get(smsRecharge.getPayeeId());
         if(userDTO != null) {
          smsRechargeDTO.setPayeeName(userDTO.getName());
         }
         smsRechargeDTOList.add(smsRechargeDTO);
      }
      result.setData(smsRechargeDTOList);
    }
    return result;
  }

  @Override
  public void initSmsRechargeReceiptNo() {
    SmsRechargeSearchCondition condition = new SmsRechargeSearchCondition();
    condition.setStart(0);
    condition.setLimit(Integer.MAX_VALUE);
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      List<SmsRecharge> smsRechargeList = writer.searchSmsRechargeResult(condition);
      if(CollectionUtil.isNotEmpty(smsRechargeList)) {
         for(int i = smsRechargeList.size() - 1; i >=0; i--) {
           SmsRecharge smsRecharge = smsRechargeList.get(i);
           if(smsRecharge.getPayTime() == null) {
             smsRecharge.setPayTime(smsRecharge.getRechargeTime());
           }
           smsRecharge.setReceiptNo(ServiceManager.getService(ITxnService.class).getBcgogoOrderReceiptNo(ShopConstant.BC_ADMIN_SHOP_ID, OrderTypes.BCGOGO_SMS_RECHARGE_RECEIVABLE_ORDER,smsRecharge.getPayTime()));
           smsRecharge.setStatus(BcgogoReceivableStatus.PENDING_REVIEW);
           smsRecharge.setRechargeMethod(RechargeMethod.CUSTOMER_RECHARGE);
           writer.update(smsRecharge);
         }
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public Result statSmsRechargeByPaymentWay(SmsRechargeSearchCondition condition) {
     Result result = new Result(true);
    Map<String,String> statMap = new HashMap<String, String>();
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
    List<Object[]> objectsList = writer.statSmsRechargeByPaymentWay(condition);

    if(CollectionUtil.isNotEmpty(objectsList)) {
       int totalNum = 0;
       double chinaPayAmount = 0.0;
       double cashPayAmount = 0.0;
       for(Object[] objects : objectsList) {
         PaymentWay way = (PaymentWay)objects[0];
         if(PaymentWay.CHINA_PAY.equals(way)) {
           chinaPayAmount += NumberUtil.doubleVal(objects[2]);
         } else if(PaymentWay.CASH.equals(way)) {
           cashPayAmount += NumberUtil.doubleVal(objects[2]);
         }
         totalNum += NumberUtil.intValue(objects[1]);
       }
      statMap.put("result","共有记录<span style=\"color: #0000FF;\"> " + totalNum + " </span>条 共计<span style=\"color: #008000;\"> " + NumberUtil.round(chinaPayAmount + cashPayAmount, 0) + " </span>元(银联<span style=\"color: #000000;\">" + chinaPayAmount + "</span>元；现金<span style=\"color: #000000;\">" + cashPayAmount + "</span>元)");
    }
     result.setData(statMap);
     return result;
  }

  @Override
  public Result getSmsPreferentialPolicy() {
    Result result = new Result(true);
    TxnWriter writer = txnDaoManager.getWriter();
    List<PreferentialPolicy> preferentialPolicyList = writer.getSmsPreferentialPolicy();
    if(CollectionUtil.isNotEmpty(preferentialPolicyList)) {
      result.setData(preferentialPolicyList);
      result.setTotal(preferentialPolicyList.size());
    } else {
      result.setTotal(0);
    }
    return result;
  }

  @Override
  public void savePreferentialSetting(String ids, String rechargeAmounts, String presentAmounts) {
    TxnWriter writer = txnDaoManager.getWriter();
    String[] idArray,rechargeAmountArray,presentAmountArray;
    Set<Long> preferentialPolicyDBIds = new HashSet<Long>();
    Result result = this.getSmsPreferentialPolicy();
    Map<Long,PreferentialPolicy> preferentialPolicyMapDB = new HashMap<Long, PreferentialPolicy>();
    if(result != null && result.getData() != null) {
      List<PreferentialPolicy> preferentialPolicyList =  (List<PreferentialPolicy>)result.getData();
      for(PreferentialPolicy preferentialPolicy : preferentialPolicyList) {
        preferentialPolicyDBIds.add(preferentialPolicy.getId());
        preferentialPolicyMapDB.put(preferentialPolicy.getId(),preferentialPolicy);
      }
    }
    Object status = writer.begin();
    try {
      if(StringUtil.isNotEmpty(rechargeAmounts)) {
        idArray = ids.split(",");
        rechargeAmountArray = rechargeAmounts.split(",");
        presentAmountArray = presentAmounts.split(",");
        for(int i = 0; i < rechargeAmountArray.length; i++) {
          if(StringUtil.isNotEmpty(idArray[i]) && !"null".equals(idArray[i])) {
            PreferentialPolicy preferentialPolicy = preferentialPolicyMapDB.get(NumberUtil.longValue(idArray[i]));
            if(preferentialPolicy.getRechargeAmount() != NumberUtil.doubleVal(rechargeAmountArray[i]) || preferentialPolicy.getPresentAmount() != NumberUtil.doubleVal(presentAmountArray[i])) {
              //更新
              preferentialPolicy.setRechargeAmount(NumberUtil.doubleVal(rechargeAmountArray[i]));
              preferentialPolicy.setPresentAmount(NumberUtil.doubleVal(presentAmountArray[i]));
              writer.update(preferentialPolicy);
            }
          } else {
            //保存
            PreferentialPolicy preferentialPolicy = new PreferentialPolicy();
            preferentialPolicy.setRechargeAmount(NumberUtil.doubleVal(rechargeAmountArray[i]));
            preferentialPolicy.setPresentAmount(NumberUtil.doubleVal(presentAmountArray[i]));
            preferentialPolicy.setDeletedType(DeletedType.FALSE);
            writer.save(preferentialPolicy);
          }
        }
        //需要删除的
        for(Long id : preferentialPolicyDBIds) {
          if(!ArrayUtils.contains(idArray,id.toString())) {
            PreferentialPolicy preferentialPolicy = preferentialPolicyMapDB.get(id);
            preferentialPolicy.setDeletedType(DeletedType.TRUE);
            writer.update(preferentialPolicy);
          }
        }

      } else if(preferentialPolicyMapDB.size() > 0){
        //需要删除的
        for(Long id : preferentialPolicyMapDB.keySet()) {
          PreferentialPolicy preferentialPolicy = preferentialPolicyMapDB.get(id);
          preferentialPolicy.setDeletedType(DeletedType.TRUE);
          writer.update(preferentialPolicy);
        }
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }

  }

  @Override
  public void generateBcgogoReceivableRecord(SmsRechargeDTO smsRechargeDTO) {
    if(smsRechargeDTO == null) {
      LOG.error("smsRechargeDTO is null");
      return;
    }
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      BcgogoReceivableOrder bcgogoReceivableOrder = new BcgogoReceivableOrder();
      bcgogoReceivableOrder.fromSmsRecharge(smsRechargeDTO);
      writer.save(bcgogoReceivableOrder);
      BcgogoReceivableRecord record = new BcgogoReceivableRecord();
      record.fromSmsRecharge(smsRechargeDTO);
      writer.save(record);
      BcgogoReceivableOrderRecordRelation recordRelation = new BcgogoReceivableOrderRecordRelation();
      recordRelation.setShopId(smsRechargeDTO.getShopId());
      recordRelation.setBcgogoReceivableOrderId(bcgogoReceivableOrder.getId());
      recordRelation.setBcgogoReceivableRecordId(record.getId());
      recordRelation.setReceivableMethod(ReceivableMethod.FULL);
      if(PaymentWay.CHINA_PAY.equals(smsRechargeDTO.getPaymentWay())) {
        recordRelation.setPaymentMethod(PaymentMethod.ONLINE_PAYMENT);
      } else if(PaymentWay.CASH.equals(smsRechargeDTO.getPaymentWay())) {
        recordRelation.setPaymentMethod(PaymentMethod.DOOR_CHARGE);
      }
      recordRelation.setPaymentType(PaymentType.SMS_RECHARGE);
      recordRelation.setAmount(smsRechargeDTO.getRechargeAmount());
      recordRelation.setSmsRechargeId(smsRechargeDTO.getId());
      writer.save(recordRelation);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public Map<String, Double> shopSmsAccountStatistic(Long shopId) {
    Map<String, Double> result = new HashMap<String, Double>();
    if(shopId == null)  return result;
    List<Long> shopIds = new ArrayList<Long>();
    shopIds.add(shopId);
    SmsRecordSearchCondition condition = new SmsRecordSearchCondition();
    condition.setShopIds(shopIds);
    condition.setStatType(StatType.ONE_TIME);
    TxnWriter writer = txnDaoManager.getWriter();
    Object[] recharge = writer.shopSmsRecordStatistics(condition, SmsCategory.SHOP_RECHARGE,SmsCategory.CRM_RECHARGE);
    Object[] handsel = writer.shopSmsRecordStatistics(condition, SmsCategory.REGISTER_HANDSEL, SmsCategory.RECOMMEND_HANDSEL, SmsCategory.RECHARGE_HANDSEL);
    Object[] consume = writer.shopSmsRecordStatistics(condition, SmsCategory.SHOP_CONSUME);
    Object[] refund = writer.shopSmsRecordStatistics(condition, SmsCategory.REFUND);
    result.put("rechargeTotal",NumberUtil.doubleValue(recharge[0],0));
    result.put("presentTotal",NumberUtil.doubleValue(handsel[0],0));
    result.put("consumeTotal",NumberUtil.doubleValue(consume[0],0) - NumberUtil.doubleValue(refund[0],0));
    return result;
  }

  @Autowired
  private TxnDaoManager txnDaoManager;

}
