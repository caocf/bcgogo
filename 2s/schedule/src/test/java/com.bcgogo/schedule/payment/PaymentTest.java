package com.bcgogo.schedule.payment;

import com.bcgogo.AbstractTest;
import com.bcgogo.config.cache.ConfigCacheManager;
import com.bcgogo.config.dto.ShopBalanceDTO;
import com.bcgogo.constant.ChinaPayConstants;
import com.bcgogo.constant.SmsRechargeConstants;
import com.bcgogo.payment.dto.ChinapayDTO;
import com.bcgogo.payment.service.IChinapayService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.SmsRechargeDTO;
import com.bcgogo.utils.NumberUtil;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-6-6
 * Time: 下午7:45
 * To change this template use File | Settings | File Templates.
 */
public class PaymentTest extends AbstractTest {

  @Before
  public void setUp() throws Exception {

  }

  @Test
  public void chinapayQueryScheduleSuccessTest() throws Exception {
    Long shopId = createShop();
    configService.setConfig("MOCK_PAYMENT_PAYSTAT", "1001", -1L); //PayStat： 1111未支付，1001支付成功，其余失败
    Long smsRechargeId = createRechargeWithConfirmState(shopId, 100d);
    checkChinaPay();
    SmsRechargeDTO smsRechargeDTO = smsRechargeService.getSmsRechargeById(smsRechargeId);
    Assert.assertEquals(100d, smsRechargeDTO.getSmsBalance());
    ShopBalanceDTO shopBalanceDTO = shopBalanceService.getSmsBalanceByShopId(shopId);
    Assert.assertEquals(100d, shopBalanceDTO.getSmsBalance());
    Assert.assertEquals(100d, shopBalanceDTO.getRechargeTotal());
  }

  @Test
  public void chinapayQueryScheduleFailTest() throws Exception {
    Long shopId = createShop();
    configService.setConfig("MOCK_PAYMENT_PAYSTAT", "1111", -1L); //PayStat： 1111未支付，1001支付成功，其余失败
    ConfigCacheManager.refreshAll();
    Long smsRechargeId = createRechargeWithConfirmState(shopId, 100d);
    SmsRechargeDTO smsRechargeDTO = smsRechargeService.getSmsRechargeById(smsRechargeId);
    Assert.assertEquals(0d, smsRechargeDTO.getSmsBalance());
    checkChinaPay();
    ShopBalanceDTO shopBalanceDTO = shopBalanceService.getSmsBalanceByShopId(shopId);
    Assert.assertEquals(null, shopBalanceDTO);
  }


  @Test
  public void batchCheckSmsRecharge() throws Exception {
    //产生500条 smsRecharge数据
    Long shopId = createShop();
    for (int i = 0; i < 220; i++) {
      createRechargeWithConfirmState(shopId, 100d);
    }
    ShopBalanceDTO shopBalanceDTO = null;
    configService.setConfig("MOCK_PAYMENT_PAYSTAT", "1001", -1L); //PayStat： 1111未支付，1001支付成功，其余失败
    ConfigCacheManager.refreshAll();
    checkChinaPay();
    shopBalanceDTO = shopBalanceService.getSmsBalanceByShopId(shopId);
    Assert.assertEquals(100d * 220, shopBalanceDTO.getSmsBalance());
    Assert.assertEquals(100d * 220, shopBalanceDTO.getRechargeTotal());
  }

  /**
   * 充值失败，处理机制
   * 模拟银联扣了金额，但是并没有发给我们请求，导致用户账户充值没有表现
   * 主动check 此order
   */
  public Long createRechargeWithConfirmState(Long shopId, double rechargeAmount) {
    chinapayService = ServiceManager.getService(IChinapayService.class);
    //数据初始化
    // 创建一条smsRecharge数据
    SmsRechargeDTO smsRechargeDTO = createRecharge(shopId);
    Long rechargeTime = smsRechargeDTO.getRechargeTime();
    smsRechargeDTO = smsRechargeService.getSmsRechargeById(smsRechargeDTO.getId());
    Assert.assertEquals(rechargeTime, smsRechargeDTO.getRechargeTime());
    Assert.assertEquals(rechargeAmount, smsRechargeDTO.getRechargeAmount());
    ChinapayDTO chinapayDTO = chinapayService.pay(smsRechargeDTO.getId(), NumberUtil.yuanToFen(rechargeAmount), shopId, SmsRechargeConstants.CHINA_PAY_ORDER_DEC_SMS, ChinaPayConstants.SMS_BG_RET_URL, ChinaPayConstants.SMS_PAGE_RET_URL);
    //更新充值单状态为已提交银联、更新充值单序号
    smsRechargeDTO.setRechargeNumber(chinapayDTO.getOrdId());
    smsRechargeDTO.setState(SmsRechargeConstants.RechargeState.RECHARGE_STATE_COMMIT);
    smsRechargeDTO.setStatusDesc(SmsRechargeConstants.RechargeStatusDesc.RECHARGE_STATUS_DESC_CONFIRM);
    smsRechargeDTO = smsRechargeService.updateSmsRecharge(smsRechargeDTO);
    Assert.assertEquals(SmsRechargeConstants.RechargeState.RECHARGE_STATE_COMMIT, smsRechargeDTO.getState(), 0);
    Assert.assertEquals(0d, smsRechargeDTO.getSmsBalance());
    return smsRechargeDTO.getId();
  }

  public void checkChinaPay() {
    //充值失败，处理机制 主动check 此order 更新账户余额
    ChinapayQuerySchedule chinapayQuerySchedule = new ChinapayQuerySchedule();
    chinapayQuerySchedule.processQueryJobs();
  }

  //todo 测试里面有成功和失败
  /*@Test
  public void batchCheckSmsRecharge1() throws Exception {
    //产生500条 smsRecharge数据
    Long shopId = createShop();
    List<Long> smsRechargeIdList = new ArrayList<Long>();
    for (int i = 0; i < 220; i++) {
      smsRechargeIdList.add(createRechargeWithConfirmState(shopId, 100d));
    }
    SmsRechargeDTO smsRechargeDTO = null;
    SmsBalanceDTO smsBalanceDTO = null;
    configService.setConfig("MOCK_PAYMENT_PAYSTAT", "1001", -1L); //PayStat： 1111未支付，1001支付成功，其余失败
    int i = 0, j = 0;
    for (Long smsRechargeId : smsRechargeIdList) {
      //对其进行校验 假设所有偶数充值成功，所有奇数充值失败
      if (i % 2 == 0) {
        configService.setConfig("MOCK_PAYMENT_PAYSTAT", "1001", -1L); //PayStat： 1111未支付，1001支付成功，其余失败
      } else {
        configService.setConfig("MOCK_PAYMENT_PAYSTAT", "1111", -1L); //PayStat： 1111未支付，1001支付成功，其余失败
      }
      checkChinaPay();
      smsRechargeDTO = smsRechargeService.getSmsRechargeById(smsRechargeId);
      smsBalanceDTO = smsRechargeService.getSmsBalanceByShopId(shopId);
      if (i % 2 == 0) {
        j++;
        Assert.assertEquals(100d, smsRechargeDTO.getSmsBalance());
      } else {
        Assert.assertEquals(0d, smsRechargeDTO.getSmsBalance());
      }
      Assert.assertEquals(100d * j, smsBalanceDTO.getRechargeTotal());
      i++;
    }
    smsBalanceDTO = smsRechargeService.getSmsBalanceByShopId(shopId);
    Assert.assertEquals(100d * 220, smsBalanceDTO.getSmsBalance());
    Assert.assertEquals(100d * 220, smsBalanceDTO.getRechargeTotal());
  }*/

}
