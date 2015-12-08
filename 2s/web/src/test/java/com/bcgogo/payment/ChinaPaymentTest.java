package com.bcgogo.payment;

import com.bcgogo.AbstractTest;
import com.bcgogo.admin.LoanTransfersController;
import com.bcgogo.config.cache.ConfigCacheManager;
import com.bcgogo.config.dto.ShopBalanceDTO;
import com.bcgogo.constant.ChinaPayConstants;
import com.bcgogo.constant.SmsRechargeConstants;
import com.bcgogo.enums.payment.LoanTransfersStatus;
import com.bcgogo.payment.dto.ChinapayDTO;
import com.bcgogo.payment.service.IChinapayService;
import com.bcgogo.search.model.SearchDaoManager;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.GoodStorageController;
import com.bcgogo.txn.RFGoodBuyController;
import com.bcgogo.txn.TxnController;
import com.bcgogo.txn.dto.LoanTransfersDTO;
import com.bcgogo.txn.dto.SmsRechargeDTO;
import com.bcgogo.txn.model.TxnWriter;
import com.bcgogo.txn.service.payment.ILoanTransfersService;
import com.bcgogo.utils.NumberUtil;
import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.text.NumberFormat;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-5-30
 * Time: 上午9:56
 * china pay 后台check 注重测试 批量操作
 * 充值失败，处理机制
 * 模拟银联扣了金额，但是并没有发给我们请求，导致用户账户充值没有表现
 * 主动check 此order
 */
public class ChinaPaymentTest extends AbstractTest {
  LoanTransfersController loanTransfersController = new LoanTransfersController();
  private ILoanTransfersService loanTransfersService;

  @Before
  public void setUp() throws Exception {
    txnController = new TxnController();
    goodsStorageController = new GoodStorageController();
    loanTransfersService = ServiceManager.getService(ILoanTransfersService.class);
    buyController = new RFGoodBuyController();
    request = new MockHttpServletRequest();
    response = new MockHttpServletResponse();
    searchDaoManager = ServiceManager.getService(SearchDaoManager.class);
  }

  @After
  public void tearDown() throws Exception {
    request = null;
    response = null;
  }

  @Test
  public void chinapayQueryScheduleSuccessTest() throws Exception {
    //有一个balance账户 余额100
    Long shopId = createShop();
    configService.setConfig("MOCK_PAYMENT_PAYSTAT", "1001", -1L); //PayStat： 1111未支付，1001支付成功，其余失败
    //短信充值
    Long smsRechargeId = createRechargeWithConfirmState(shopId, 100d);
    //货款转账
    LoanTransfersDTO loanTransfersDTO = createLoanTransfersDTO(shopId);
    //货款转账中
    Assert.assertEquals(LoanTransfersStatus.LOAN_IN, loanTransfersDTO.getStatus());

    //定时钟check
    checkChinaPay();

    //短信充值中
    SmsRechargeDTO smsRechargeDTO = smsRechargeService.getSmsRechargeById(smsRechargeId);
    Assert.assertEquals(200d, smsRechargeDTO.getSmsBalance());
    ShopBalanceDTO shopBalanceDTO = shopBalanceService.getSmsBalanceByShopId(shopId);
    Assert.assertEquals(200d, shopBalanceDTO.getSmsBalance());
    Assert.assertEquals(200d, shopBalanceDTO.getRechargeTotal());
    //货款转账成功
    loanTransfersDTO = getLoanTransfersByTransfersNumber(loanTransfersDTO.getTransfersNumber());
    Assert.assertEquals(LoanTransfersStatus.LOAN_SUCCESS, loanTransfersDTO.getStatus());

  }

  @Test
  public void chinapayQueryScheduleFailTest() throws Exception {
    Long shopId = createShop();
    configService.setConfig("MOCK_PAYMENT_PAYSTAT", "1111", -1L); //PayStat： 1111未支付，1001支付成功，其余失败
    ConfigCacheManager.refreshAll();
    //短信充值
    Long smsRechargeId = createRechargeWithConfirmState(shopId, 100d);
    //货款转账
    LoanTransfersDTO loanTransfersDTO = createLoanTransfersDTO(shopId);

    //短信充值中
    SmsRechargeDTO smsRechargeDTO = smsRechargeService.getSmsRechargeById(smsRechargeId);
    Assert.assertEquals(0d, smsRechargeDTO.getSmsBalance());
    ShopBalanceDTO shopBalanceDTO = shopBalanceService.getSmsBalanceByShopId(shopId);
    Assert.assertEquals(100d, shopBalanceDTO.getSmsBalance());

    //货款转账中
    Assert.assertEquals(LoanTransfersStatus.LOAN_IN, loanTransfersDTO.getStatus());

    //定时钟check
    checkChinaPay();

    //短信充值失败
    shopBalanceDTO = shopBalanceService.getSmsBalanceByShopId(shopId);
    Assert.assertEquals(100d, shopBalanceDTO.getSmsBalance());

    //货款转账失败
    loanTransfersDTO = getLoanTransfersByTransfersNumber(loanTransfersDTO.getTransfersNumber());
    Assert.assertEquals(LoanTransfersStatus.LOAN_FAIL, loanTransfersDTO.getStatus());
  }

  //批量测试
  @Test
  public void batchCheckSmsRecharge() throws Exception {
    //产生500条 smsRecharge数据
    Long shopId = createShop();
    for (int i = 0; i < 220; i++) {
      createRechargeWithConfirmState(shopId, 100d);
      createLoanTransfersDTO(shopId);
    }
    ShopBalanceDTO shopBalanceDTO = null;
    configService.setConfig("MOCK_PAYMENT_PAYSTAT", "1001", -1L); //PayStat： 1111未支付，1001支付成功，其余失败
    ConfigCacheManager.refreshAll();
    checkChinaPay();
    //短信测试结果
    shopBalanceDTO = shopBalanceService.getSmsBalanceByShopId(shopId);
    Assert.assertEquals(100d * 220d + 100d, shopBalanceDTO.getSmsBalance(), 0.001d);
    Assert.assertEquals(100d * 220d + 100d, shopBalanceDTO.getRechargeTotal(), 0.001d);
    //货款转账测试结果
    loanTransfersController.showPage(request, response);
    Double totalAmount = Double.valueOf( NumberFormat.getInstance().parse((String) request.getAttribute("totalAmount")).toString());
    Assert.assertEquals(100d * 220d, totalAmount, 0.001);
  }

  private Long createRechargeWithConfirmState(Long shopId, double rechargeAmount) {
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


  private LoanTransfersDTO createLoanTransfersDTO(Long shopId) {
    LoanTransfersDTO loanTransfersDTO = new LoanTransfersDTO();
    loanTransfersDTO.setAmount(100d);
    loanTransfersDTO.setMemo("test");
    loanTransfersDTO.setShopId(shopId);
    loanTransfersController.saveLoan(request, response, loanTransfersDTO);
    String transfersNumber = (String) request.getAttribute("transfersNumber");
    return getLoanTransfersByTransfersNumber(transfersNumber);
  }


  private LoanTransfersDTO getLoanTransfersByTransfersNumber(String transfersNumber) {
    TxnWriter writer = txnDaoManager.getWriter();
    LoanTransfersDTO loanTransfersDTO = writer.getLoanTransfersByTransfersNumber(transfersNumber);
    return loanTransfersDTO;
  }

  private void checkChinaPay() {
    //充值失败，处理机制 主动手动check 此order 更新账户余额
    request.setParameter("timePeriod", "1");
    chinapayController.check(request, response);
  }

}
