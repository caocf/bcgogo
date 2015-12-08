package com.bcgogo.txn.service;

import com.bcgogo.AbstractTest;
import org.junit.Test;

/**
 * Created by IntelliJ IDEA.
 * User: MZDong
 * Date: 11-12-20
 * Time: 下午2:08
 * To change this template use File | Settings | File Templates.
 */
public class SmsRechargeTest extends AbstractTest {

  @Test
  public void testGetInventoryByShopId() throws Exception {


  /* ISmsRechargeService smsRechargeService = ServiceManager.getService(ISmsRechargeService.class);

    SmsRechargeDTO smsRechargeDTO = new SmsRechargeDTO();
    smsRechargeDTO.setSmsBalance(1.0);
    smsRechargeDTO.setRechargeAmount(2.0);
    smsRechargeDTO.setShopId(100001L);
    smsRechargeDTO.setRechargeNumber("1111");
    smsRechargeDTO.setRechargeTime(156416541L);
    smsRechargeDTO.setState(1L);

    //SmsRecharge smsRecharge = new SmsRecharge(smsRechargeDTO);  从未使用

    smsRechargeDTO = smsRechargeService.createSmsRecharge(smsRechargeDTO);

    //assertEquals("id", smsRechargeDTO.getId());

    smsRechargeDTO.setState(5L);

    SmsRechargeDTO smsRechargeDTO2 = smsRechargeService.updateSmsRecharge(smsRechargeDTO);

    SmsRechargeDTO smsRechargeDTO3 = smsRechargeService.getSmsRechargeById(smsRechargeDTO.getId());
    //assertEquals("state", smsRechargeDTO.getState());

    smsRechargeService.getSmsRechargeByShopId(smsRechargeDTO.getShopId());


    SmsBalanceDTO smsBalanceDTO = new SmsBalanceDTO();
    smsBalanceDTO.setShopId(smsRechargeDTO.getShopId());
    smsBalanceDTO.setSmsBalance(11.23);
    smsRechargeService.createSmsBalance(smsBalanceDTO);
    assertNotNull("id", smsBalanceDTO.getId());

    SmsBalanceDTO smsBalanceDTO2 = smsRechargeService.getSmsBalanceByShopId(smsRechargeDTO.getShopId());
    assertNotNull("id", smsBalanceDTO2.getId());

    SmsRechargeDTO smsRechargeDTO4 = new SmsRechargeDTO();
    smsRechargeDTO4 = smsRechargeService.getSmsRechargeByRechargeNumber("1111");
    assertNotNull("id", smsRechargeDTO4.getId());

    //把当前时间转为长整型数字
    Calendar now = Calendar.getInstance();
    Date date = now.getTime();

    long payTime = Timestamp.valueOf(date.toLocaleString()).getTime();
    smsRechargeService.updateSmsRechargePayTime(payTime, "1111");
    SmsRechargeDTO smsRechargeDTO5 = smsRechargeService.getSmsRechargeByRechargeNumber("1111");
    assertNotNull("payTime", smsRechargeDTO5.getPayTime());

    */


  }

}
