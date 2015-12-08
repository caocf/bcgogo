package com.bcgogo.payment.service;

import com.bcgogo.AbstractTest;

/**
 * Created by IntelliJ IDEA.
 * User: sunyingzi
 * Date: 11-12-21
 * Time: 上午10:03
 * To change this template use File | Settings | File Templates.
 */
public class ChinapayServiceTest extends AbstractTest {
/*  @Test
  public void testPay() {
    IChinapayService chinapayService = ServiceManager.getService(IChinapayService.class);
    this.setPath();
    ChinapayDTO chinapayDTO = new ChinapayDTO();
    chinapayDTO = chinapayService.pay(1L, 1L, 1L, 1L, "Desc");

    assertNotNull(chinapayDTO);
  }

 @Test
  public void testReceive() {
    IChinapayService chinapayService = ServiceManager.getService(IChinapayService.class);
    this.setPath();
    IPaymentService ips = ServiceManager.getService(IPaymentService.class);

    TransactionDTO transactionDTO = new TransactionDTO();
    transactionDTO.setTransactionType(1L);//1表示支付
    transactionDTO.setReferenceType(1L);
    transactionDTO.setReferenceId(1L);
    transactionDTO.setAmount(10000L); //支付金额
    transactionDTO.setCurrency(1L);//固定值，1为人民币
    transactionDTO.setPayMethod(1L);//1表示银联支付
    transactionDTO.setPayerId(1L);//支付人
    transactionDTO.setStatus(Status.PENDING.toString());//订单状态

    //Create Transaction
    transactionDTO = ips.createTransaction(transactionDTO);
    assertNotNull(transactionDTO);

    SequenceNoDTO sequenceNoDTO = new SequenceNoDTO();

    sequenceNoDTO.setSequenceNo("0000010005010401");
    sequenceNoDTO.setTransId(transactionDTO.getId());

    //create
    sequenceNoDTO = ips.createSequence(sequenceNoDTO);
    assertNotNull(sequenceNoDTO);

    ChinapayDTO chinapayDTO = new ChinapayDTO();

    chinapayDTO.setMerId("808080092191652");
    chinapayDTO.setOrdId("0000010005010401");
    chinapayDTO.setOrdAmt("10000");
    chinapayDTO.setCuryId("156");
    chinapayDTO.setInterfaceVersion("20100401");
    chinapayDTO.setGateId("0001");
    chinapayDTO.setOrdDesc("短信充值");
    chinapayDTO.setShareType("0001");
    chinapayDTO.setShareData("00022265^10000;");
    chinapayDTO.setChkValue("4305D630C971FEB7A9085AC574EF8A588B4A22BAD003F01897ACE87BAD505FF6DBF30A0BD01BFFF20455A2C32FB58E3767712B55FEDBA14EEBE4AFAB05B2149A2B2110332853AC065CA3BB7F05502B786E4B6893FD772520448A22B700EA758CC5BAB95A2471FA99B5456BED955438BABA3997053628F7BC6C72C469A3414F1C");
    chinapayDTO.setPayStat("1001");
    chinapayDTO.setPayTime("2011-12-29 09:23:18");

    transactionDTO = null;
    transactionDTO = chinapayService.receive(chinapayDTO);

    assertNotNull(transactionDTO);

  }

  @Test
  public void testPgReceive() {
    IChinapayService chinapayService = ServiceManager.getService(IChinapayService.class);

    this.setPath();
    IPaymentService ips = ServiceManager.getService(IPaymentService.class);

    SequenceNoDTO sequenceNoDTO = new SequenceNoDTO();

    sequenceNoDTO.setSequenceNo("1");
    sequenceNoDTO.setTransId(1L);

    //create
    sequenceNoDTO = ips.createSequence(sequenceNoDTO);

    ChinapayDTO chinapayDTO = new ChinapayDTO();

    chinapayDTO.setMerId("808080092191652");
    chinapayDTO.setOrdId(sequenceNoDTO.getSequenceNo());
    chinapayDTO.setOrdAmt("10000");
    chinapayDTO.setCuryId("156");
    chinapayDTO.setInterfaceVersion("20100401");
    chinapayDTO.setGateId("0001");
    chinapayDTO.setOrdDesc("短信充值");
    chinapayDTO.setShareType("0001");
    chinapayDTO.setShareData("00022265^10000;");
    chinapayDTO.setChkValue("4305D630C971FEB7A9085AC574EF8A588B4A22BAD003F01897ACE87BAD505FF6DBF30A0BD01BFFF20455A2C32FB58E3767712B55FEDBA14EEBE4AFAB05B2149A2B2110332853AC065CA3BB7F05502B786E4B6893FD772520448A22B700EA758CC5BAB95A2471FA99B5456BED955438BABA3997053628F7BC6C72C469A3414F1C");
    chinapayDTO.setPayStat("1001");
    chinapayDTO.setPayTime("2011-12-29 09:23:18");


    chinapayDTO = chinapayService.pgReceive(chinapayDTO);

    assertNotNull(chinapayDTO);
  }

  @Test
  public void testQuery() {

    IChinapayService chinapayService = ServiceManager.getService(IChinapayService.class);

    this.setPath();
    IPaymentService ips = ServiceManager.getService(IPaymentService.class);

    SequenceNoDTO sequenceNoDTO = new SequenceNoDTO();
    sequenceNoDTO.setTransId(1L);
    sequenceNoDTO.setSequenceNo("0000010005010401");
    //create sequenceNo
    sequenceNoDTO = ips.createSequence(sequenceNoDTO);

    //Get QueryString
    String queryString = chinapayService.getQueryString(sequenceNoDTO.getSequenceNo());

    assertNotNull(queryString);
    //Query
    ChinapayDTO chinapayDTO = chinapayService.query(queryString);

    assertNotNull(chinapayDTO);
  }


  public void setPath() {
    IConfigService configService = ServiceManager.getService(IConfigService.class);

    configService.setConfig("MerId", "808080092191652", -1L);
    configService.setConfig("CurId", "156", -1L);
    configService.setConfig("Version", "20100401", -1L);
    configService.setConfig("GateId", "0001", -1L);
    configService.setConfig("ShareType", "0001", -1L);
    configService.setConfig("ShareA", "00022265^", -1L);
    configService.setConfig("ShareB", "00022266^", -1L);
    configService.setConfig("SmsRechargeQueryTimes", "20", -1L);


    //得到EDU支付前台地址
    configService.setConfig("PayPgUrl", "http://bianmin-test.chinapay.com/cpeduinterface/OrderGet.do", -1L);
    configService.setConfig("QueryBgUrl", "http://bianmin-test.chinapay.com/cpeduinterface/QueryGet.do", -1L);
    //得到商户私钥存放路径
    configService.setConfig("MerPriKeyPath", "D:\\bcgogo\\web\\src\\main\\java\\com\\bcgogo\\payment\\key\\MerPrK_808080092191652_20110921163550.key", -1L);
    //得到CHINAPAY公钥存放路径
    configService.setConfig("ChinaPayPubKeyPath", "D:\\bcgogo\\web\\src\\main\\java\\com\\bcgogo\\payment\\key\\PgPubk.key", -1L);
    // 返回商户前台地址
    configService.setConfig("ReturnMerPgUrl", "http://117.83.56.58:8080/web/payment.do?method=pgreceive", -1L);
    //返回商户后台地址
    configService.setConfig("ReturnMerBgUrl", "http://117.83.56.58:8080/web/payment.do?method=receive", -1L);
  }      */
}
