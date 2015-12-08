package com.bcgogo.payment.service;

import com.bcgogo.AbstractTest;
import com.bcgogo.payment.dto.ChinapayDTO;
import com.bcgogo.payment.dto.PaymentServiceJobDTO;
import com.bcgogo.payment.dto.TransactionDTO;
import com.bcgogo.payment.model.Status;
import com.bcgogo.service.ServiceManager;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class PaymentServiceTest extends AbstractTest {

  @Test
  public void testTransaction() throws Exception {
    IPaymentService ips = ServiceManager.getService(IPaymentService.class);

    TransactionDTO transactionDTO = new TransactionDTO();
    transactionDTO.setTransactionType(2L);
    transactionDTO.setParentId(4L);
    transactionDTO.setReferenceType(5L);
    transactionDTO.setReferenceId(6L);
    transactionDTO.setAmount(7L);
    transactionDTO.setCurrency(8L);
    transactionDTO.setPayMethod(9L);
    transactionDTO.setPayerId(10L);
    transactionDTO.setPspTransactionId("123456879");
    transactionDTO.setStatus("PENDING");


    transactionDTO = ips.createTransaction(transactionDTO);

    //check
    TransactionDTO dto = ips.getTransactionById(transactionDTO.getId());
    assertEquals(new Long(2L), dto.getTransactionType());
    assertEquals(new Long(4L), dto.getParentId());
    assertEquals(new Long(5L), dto.getReferenceType());
    assertEquals(new Long(6L), dto.getReferenceId());
    assertEquals(new Long(7L), dto.getAmount());
    assertEquals(new Long(8L), dto.getCurrency());
    assertEquals(new Long(9L), dto.getPayMethod());
    assertEquals(new Long(10L), dto.getPayerId());
    assertEquals("123456879", dto.getPspTransactionId());
    assertEquals("PENDING", dto.getStatus());


    //updata Transaction
    transactionDTO.setPspTransactionId("1112140215000001");
    ips.updateTransaction(transactionDTO);

    dto = ips.getTransactionById(transactionDTO.getId());
    assertEquals("1112140215000001", dto.getPspTransactionId());


    //successTransaction
    dto = ips.successTransaction(transactionDTO.getId());
    // getTransactionByBaseId
    List<TransactionDTO> transactionDTOList = ips.getTransactionByBaseId(transactionDTO.getBaseId(), Status.COMPLETED.toString());

    assertTrue(transactionDTOList.size() > 0);
  }

  @Test
  public void testChinapay() throws Exception {
    IPaymentService ips = ServiceManager.getService(IPaymentService.class);

    ChinapayDTO chinapayDTO = new ChinapayDTO();
    chinapayDTO.setTransId(0L);
    chinapayDTO.setMerId("1");
    chinapayDTO.setBusiId("2");
    chinapayDTO.setOrdId("3");
    chinapayDTO.setOrdAmt("4");
    chinapayDTO.setBgRetUrl("5");
    chinapayDTO.setPageRetUrl("6");
    chinapayDTO.setGateId("7");
    chinapayDTO.setParam1("8");
    chinapayDTO.setParam2("9");
    chinapayDTO.setParam3("10");
    chinapayDTO.setParam4("11");
    chinapayDTO.setParam5("12");
    chinapayDTO.setParam6("13");
    chinapayDTO.setParam7("14");
    chinapayDTO.setParam8("15");
    chinapayDTO.setParam9("16");
    chinapayDTO.setParam10("17");
    chinapayDTO.setOrdDesc("18");
    chinapayDTO.setShareType("19");
    chinapayDTO.setShareData("20");
    chinapayDTO.setPriv1("21");
    chinapayDTO.setCustomIp("22");
    chinapayDTO.setChkValue("23");
    chinapayDTO.setPayStat("24");
    chinapayDTO.setPayTime("25");

    chinapayDTO = ips.createChinapay(chinapayDTO);

    //check
    ChinapayDTO dto2 = ips.getChinapayById(chinapayDTO.getId());

    assertEquals(new Long(0L), dto2.getTransId());
    assertEquals("1", dto2.getMerId());
    assertEquals("2", dto2.getBusiId());
    assertEquals("3", dto2.getOrdId());
    assertEquals("4", dto2.getOrdAmt());
    assertEquals("5", dto2.getBgRetUrl());
    assertEquals("6", dto2.getPageRetUrl());
    assertEquals("7", dto2.getGateId());
    assertEquals("8", dto2.getParam1());
    assertEquals("9", dto2.getParam2());
    assertEquals("10", dto2.getParam3());
    assertEquals("11", dto2.getParam4());
    assertEquals("12", dto2.getParam5());
    assertEquals("13", dto2.getParam6());
    assertEquals("14", dto2.getParam7());
    assertEquals("15", dto2.getParam8());
    assertEquals("16", dto2.getParam9());
    assertEquals("17", dto2.getParam10());
    assertEquals("18", dto2.getOrdDesc());
    assertEquals("19", dto2.getShareType());
    assertEquals("20", dto2.getShareData());
    assertEquals("21", dto2.getPriv1());
    assertEquals("22", dto2.getCustomIp());
    assertEquals("23", dto2.getChkValue());
    assertEquals("24", dto2.getPayStat());
    assertEquals("25", dto2.getPayTime());

    chinapayDTO.setTransId(1L);

    ips.updateChinapay(chinapayDTO);

    dto2 = ips.getChinapayById(chinapayDTO.getId());
    assertEquals(new Long(1L), dto2.getTransId());

  }

  @Test
  public void testSequenceNo() throws Exception {
 /*   IPaymentService ips = ServiceManager.getService(IPaymentService.class);

    SequenceNoDTO sequenceNoDTO = new SequenceNoDTO();

    sequenceNoDTO.setSequenceNo("1");
    sequenceNoDTO.setTransId(1L);

    //create
    sequenceNoDTO = ips.createSequence(sequenceNoDTO);
    //check
    SequenceNoDTO dto3 = ips.getSequenceNoById(sequenceNoDTO.getId());
    assertEquals("1", dto3.getSequenceNo());
    assertEquals(new Long(1L), dto3.getTransId());

    //update
    sequenceNoDTO.setSequenceNo("123");
    ips.updateSequence(sequenceNoDTO);

    dto3 = ips.getSequenceNoById(sequenceNoDTO.getId());
    assertEquals("123", dto3.getSequenceNo());

    List<SequenceNoDTO> sequenceNoDTOList = ips.getSequenceNoByNo(sequenceNoDTO.getSequenceNo());

    assertTrue(sequenceNoDTOList.size() > 0);     */
  }

  @Test
  public void testPaymentServiceJob() throws Exception {
    IPaymentService ips = ServiceManager.getService(IPaymentService.class);

    PaymentServiceJobDTO paymentServiceJobDTO = new PaymentServiceJobDTO();
    paymentServiceJobDTO.setTransactionId(1L);
    paymentServiceJobDTO.setQueryTimes(20L);
    //create
    paymentServiceJobDTO = ips.createPaymentServiceJob(paymentServiceJobDTO);
    //check
    PaymentServiceJobDTO dto = ips.getPaymentServiceJobById(paymentServiceJobDTO.getId());

    assertEquals(new Long(1L), dto.getTransactionId());
    assertEquals(new Long(20L), dto.getQueryTimes());

    //update
    paymentServiceJobDTO.setQueryTimes(19L);
    ips.updatePaymentServiceJob(paymentServiceJobDTO);

    dto = ips.getPaymentServiceJobById(paymentServiceJobDTO.getId());
    assertEquals(new Long(19L), dto.getQueryTimes());
  }
}
