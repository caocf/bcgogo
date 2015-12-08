package com.bcgogo.txn.service.finance;

import com.bcgogo.common.Result;
import com.bcgogo.exception.BcgogoException;
import com.bcgogo.txn.dto.finance.*;
import com.bcgogo.txn.model.TxnWriter;

import java.util.List;
import java.util.Map;

/**
 * User: ZhangJuntao
 * Date: 13-3-19
 * Time: 下午5:29
 */
public interface ISmsAccountService {
  /**
   * 客户短信账单
   *
   * @param condition SmsRecordSearchCondition
   */
  Result searchShopSmsAccountResult(SmsRecordSearchCondition condition);

  /**
   * 客户短信账单-详细
   *
   * @param condition SmsRecordSearchCondition
   */
  Result searchShopSmsRecordResult(SmsRecordSearchCondition condition);

  /**
   * 公司短信账单
   *
   * @param condition SmsRecordSearchCondition
   */
  Result searchBcgogoSmsRecordResult(SmsRecordSearchCondition condition);

  /**
   * 统计（短信充值 0元/0条；短信赠送0元/0条；短信消费 0元/0条；）
   *
   * @param condition SmsRecordSearchCondition
   */
  Result shopSmsRecordStatistics(SmsRecordSearchCondition condition);

  /**
   * 统计（充值总额：0；消费总条数：0；剩余总条数：0；）
   */
  Result shopSmsAccountStatistics(SmsRecordSearchCondition condition);

  /**
   * 短信总账单
   */
  List<BcgogoSmsAccountDTO> getBcgogoSmsTotalAccount();


  /**
   * bcgogo sms 充值
   *
   * @param dto BcgogoSmsRecordDTO
   */
  void createBcgogoRecharge(BcgogoSmsRecordDTO dto);

  /**
   * bcgogo sms 消费
   *
   * @param balance double 消费金额
   * @param number  long   消费条数
   */
  void createBcgogoConsumption(double balance, long number) throws Exception;

  /**
   * 客户消费
   *
   * @param shopId  long
   * @param balance double 消费金额
   * @param number  long   消费条数
   */
  void createShopSmsConsumption(long shopId, double balance, long number) throws Exception;

  /**
   * 退款
   *
   * @param dto ShopSmsRecordDTO
   */
  Result createShopSmsRefund(ShopSmsRecordDTO dto) throws BcgogoException;

  //客户充值 见 SmsRechargeService
  // void createShopSmsRecharge(ShopSmsRecordDTO dto) throws BcgogoException;

  /**
   * 客户注册赠送 推荐赠送
   *
   * @param dto ShopSmsRecordDTO
   */
  void createShopSmsHandsel(ShopSmsRecordDTO dto) throws BcgogoException;

  Result recharge(Long userId, Long shopId, Long payeeId, Double rechargeAmount);

  Double getPresentAmountByRechargeAmount(Double rechargeAmount);

  void initShopSmsAccount();

  void initShopSmsConsumeAccount();

  void saveOrUpdateShopSmsRecord(ShopSmsRecordDTO shopSmsRecordDTO);

  ShopSmsRecordDTO getShopSmsRecordBySmsId(Long shopId, Long smsId);

}
