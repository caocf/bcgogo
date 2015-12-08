package com.bcgogo.payment.service;


import com.bcgogo.enums.payment.ChinaPayParamStatus;
import com.bcgogo.payment.dto.ChinapayDTO;
import com.bcgogo.payment.dto.PaymentServiceJobDTO;
import com.bcgogo.payment.dto.SequenceNoDTO;
import com.bcgogo.payment.dto.TransactionDTO;
import com.bcgogo.payment.model.ChinaPayParamLog;

import java.util.List;


public interface IPaymentService {
  //--methods related Transaction----------------------------------------------
  public TransactionDTO createTransaction(TransactionDTO transactionDTO);

  public TransactionDTO updateTransaction(TransactionDTO transactionDTO);

  public TransactionDTO getTransactionById(long transactionId);

  public List<TransactionDTO> getTransactionByRechargeId(long rechargeId);

  public List<TransactionDTO> getTransactionByBaseId(long baseId, String status);

  public List<SequenceNoDTO> getSequenceNoByTransId(Long transId);

  public TransactionDTO successTransaction(long baseId);


  //--methods related Chinapay----------------------------------------------
  public ChinapayDTO createChinapay(ChinapayDTO chinapayDTO);

  public ChinapayDTO updateChinapay(ChinapayDTO chinapayDTO);

  public ChinapayDTO getChinapayById(long chinapayId);


  //--methods related SequenceNo----------------------------------------------
  public SequenceNoDTO createSequence(SequenceNoDTO sequenceNoDTO);

  public SequenceNoDTO updateSequence(SequenceNoDTO sequenceNoDTO);

  public SequenceNoDTO getSequenceNoById(long sequenceNoId);

  public List<SequenceNoDTO> getSequenceNoByNo(String sequenceNo);

  //--methods related PaymentServiceJob----------------------------------------------
  public PaymentServiceJobDTO createPaymentServiceJob(PaymentServiceJobDTO paymentServiceJobDTO);

  public PaymentServiceJobDTO updatePaymentServiceJob(PaymentServiceJobDTO paymentServiceJobDTO);

  public PaymentServiceJobDTO getPaymentServiceJobById(long paymentServiceJobId);

  public List<PaymentServiceJobDTO> getPaymentServiceJob();

  //-- methods related ChinaPayParamLog----------------------------------------------
  Long createChinaPayParamLog(ChinaPayParamLog log);

  void updateChinaPayParamLogChinaPayStatus(long ordId, ChinaPayParamStatus chinaPayParamStatus);

  void updateChinaPayParamLog(long id,long ordId);

  ChinaPayParamLog getChinaPayParamLog(long ordId);


}
