package com.bcgogo.txn.service;

import com.bcgogo.common.Result;
import com.bcgogo.txn.dto.SmsRechargeCompleteDTO;
import com.bcgogo.txn.dto.SmsRechargeDTO;
import com.bcgogo.txn.dto.finance.SmsRechargeSearchCondition;
import com.bcgogo.txn.dto.finance.SmsRecordSearchCondition;

import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: MZDong
 * Date: 11-12-20
 * Time: 下午4:32
 * To change this template use File | Settings | File Templates.
 */
public interface ISmsRechargeService {

  //短信充值表
  public SmsRechargeDTO createSmsRecharge(SmsRechargeDTO smsRechargeDTO);

  public SmsRechargeDTO updateSmsRecharge(SmsRechargeDTO smsRechargeDTO);

  public SmsRechargeDTO getSmsRechargeById(long smsRechargeId);

  //根据店面ID获取短信充值的列表
  public List<SmsRechargeDTO> getSmsRechargeByShopId(long shopId);

  //根据店面ID获取短信充值记录数
  public int countShopSmsRecharge(long shopId);

  //根据店面ID、页码与每页条数获取短信充值的列表
  public List<SmsRechargeDTO> getShopSmsRechargeList(long shopId, int pageNo, int pageSize);

  //根据充值序号获取短信充值
  public SmsRechargeDTO getSmsRechargeByRechargeNumber(String rechargeNumber);

  //根据充值序号更新payTime
  public SmsRechargeDTO updateSmsRechargePayTime(long payTime, String rechargeNumber);

  //短信余额
//  public ShopBalanceDTO createSmsBalance(ShopBalanceDTO smsBalanceDTO);
//
//  public ShopBalanceDTO updateSmsBalance(ShopBalanceDTO smsBalanceDTO);
//
//  public ShopBalanceDTO getSmsBalanceById(long smsBalanceId);
//
//  public ShopBalanceDTO getSmsBalanceByShopId(long shopId);

  public void completeSmsRecharge(long smsRechargeId) throws Exception;

  void failSmsRecharge(Long smsRechargeId);

  public SmsRechargeCompleteDTO getSmsRechargeCompleteInfo(SmsRechargeDTO smsRechargeDTO, int currentPage, int pageSize);

  //店面充值历史记录
 public List<SmsRechargeDTO> getShopSmsRechargeList(String startTime, String endTime, String money, String other, Long shopId, int currentPage, int pageSize);

  // 店面充值历史记录数
 public  int countShopSmsRecharge(String startTime, String endTime, String money, String other, Long shopId);

 //短信充值记录（CRM短信销售单）
 public Result searchSmsRechargeResult(SmsRechargeSearchCondition smsRechargeSearchCondition);

 public void initSmsRechargeReceiptNo();

 Result statSmsRechargeByPaymentWay(SmsRechargeSearchCondition smsRechargeSearchCondition);

 Result getSmsPreferentialPolicy();

 void savePreferentialSetting(String ids, String rechargeAmounts, String presentAmounts);

 void  generateBcgogoReceivableRecord(SmsRechargeDTO smsRechargeDTO);

  Map<String,Double> shopSmsAccountStatistic(Long shopId);

}
