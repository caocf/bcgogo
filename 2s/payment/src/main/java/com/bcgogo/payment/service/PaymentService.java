package com.bcgogo.payment.service;

import com.bcgogo.enums.payment.ChinaPayParamStatus;
import com.bcgogo.payment.dto.ChinapayDTO;
import com.bcgogo.payment.dto.PaymentServiceJobDTO;
import com.bcgogo.payment.dto.SequenceNoDTO;
import com.bcgogo.payment.dto.TransactionDTO;
import com.bcgogo.payment.model.*;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

@Component
public class PaymentService implements IPaymentService {
  private static final Logger LOG = LoggerFactory.getLogger(PaymentService.class);

  @Autowired
  private PaymentDaoManager paymentDaoManager;

  @Override
  public TransactionDTO createTransaction(TransactionDTO transactionDTO) {
    if (transactionDTO == null) return null;

    PaymentWriter writer = paymentDaoManager.getWriter();
    Object status = writer.begin();

    try {
      Transaction transaction = new Transaction(transactionDTO);

      writer.save(transaction);

      transaction.setBaseId(transaction.getId());

      writer.save(transaction);

      writer.commit(status);

      transactionDTO.setBaseId(transaction.getBaseId());
      transactionDTO.setId(transaction.getId());

      return transactionDTO;
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public TransactionDTO updateTransaction(TransactionDTO transactionDTO) {
    if (transactionDTO == null) return null;

    PaymentWriter writer = paymentDaoManager.getWriter();
    Object status = writer.begin();

    try {
      Long id = transactionDTO.getId();
      if (id == null) return null;

      Transaction transaction = writer.getById(Transaction.class, id);
      if (transaction == null) return null;

      transaction.fromDTO(transactionDTO);

      writer.save(transaction);
      writer.commit(status);

      return transactionDTO;
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public TransactionDTO getTransactionById(long transactionId) {
    PaymentWriter writer = paymentDaoManager.getWriter();

    Transaction transaction = writer.getById(Transaction.class, transactionId);

    if (transaction == null) return null;
    return transaction.toDTO();
  }

  @Override
  public List<TransactionDTO> getTransactionByRechargeId(long rechargeId) {
    PaymentWriter writer = paymentDaoManager.getWriter();
    List<Transaction> transactionList = writer.getTransactionByRechargeId(rechargeId);
    if (CollectionUtils.isEmpty(transactionList)) return null;
    List<TransactionDTO> transactionDTOList = new ArrayList<TransactionDTO>();
    for (Transaction t : transactionList) {
      transactionDTOList.add(t.toDTO());
    }
    return transactionDTOList;
  }

  @Override
  public List<TransactionDTO> getTransactionByBaseId(long baseId, String status) {
    PaymentWriter writer = paymentDaoManager.getWriter();

    List<TransactionDTO> listTransactionDTO = new ArrayList<TransactionDTO>();
    for (Transaction transaction : writer.getTransactionByBaseId(baseId, status)) {
      listTransactionDTO.add(transaction.toDTO());
    }

    return listTransactionDTO;
  }

  @Override
  public TransactionDTO successTransaction(long baseId) {
    PaymentWriter writer = paymentDaoManager.getWriter();
    Object status = writer.begin();

    try {
      List<Transaction> listTransaction = writer.getTransactionByBaseId(baseId, Status.COMPLETED.name());

      if (listTransaction.size() > 0) {
        return listTransaction.get(0).toDTO();

      } else {
        TransactionDTO transactionDTO = this.getTransactionById(baseId);

        if (transactionDTO == null) return null;

        transactionDTO.setId(null);

        Transaction transaction = new Transaction(transactionDTO);
        transaction.setTransactionType(2L);//2表示完成支付
        transaction.setStatus(Status.COMPLETED.toString());

        writer.save(transaction);

        writer.commit(status);

        return transaction.toDTO();
      }
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public ChinapayDTO createChinapay(ChinapayDTO chinapayDTO) {
    if (chinapayDTO == null) return null;

    PaymentWriter writer = paymentDaoManager.getWriter();
    Object status = writer.begin();

    try {
      Chinapay chinapay = new Chinapay(chinapayDTO);

      writer.save(chinapay);
      writer.commit(status);

      chinapayDTO.setId(chinapay.getId());

      return chinapayDTO;

    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public ChinapayDTO updateChinapay(ChinapayDTO chinapayDTO) {
    if (chinapayDTO == null) return null;

    PaymentWriter writer = paymentDaoManager.getWriter();
    Object status = writer.begin();

    try {
      Long id = chinapayDTO.getId();
      if (id == null) return null;

      Chinapay chinapay = writer.getById(Chinapay.class, id);
      if (chinapay == null) return null;

      chinapay.fromDTO(chinapayDTO);

      writer.save(chinapay);
      writer.commit(status);

      return chinapayDTO;

    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public ChinapayDTO getChinapayById(long chinapayId) {
    PaymentWriter writer = paymentDaoManager.getWriter();
    Chinapay chinapay = writer.getById(Chinapay.class, chinapayId);

    if (chinapay == null) return null;

    return chinapay.toDTO();
  }

  /**
   * 生成订单号 sequenceNo
   *
   * @param sequenceNoDTO
   * @return
   */
  @Override
  public SequenceNoDTO createSequence(SequenceNoDTO sequenceNoDTO) {
    if (sequenceNoDTO == null) return null;

    PaymentWriter writer = paymentDaoManager.getWriter();
    Object status = writer.begin();

    try {
      SequenceNo sequenceNo = new SequenceNo(sequenceNoDTO);

      writer.save(sequenceNo);

      String no = sequenceNo.getId().toString();
//      测试 订单号的第五至第九位必须是商户号的最后五位，即“92212”   0000 00000 0000000
//      no = no.length() > 16 ? no.substring(no.length() - 16) : new DecimalFormat("0000000000000000").format(no);
//      no = no.substring(5, 9) + "92212" + no.substring(9, no.length());
//      sequenceNo.setSequenceNo(no);
      sequenceNo.setSequenceNo(no.length() > 16 ? no.substring(no.length() - 16) : new DecimalFormat("0000000000000000").format(no));

      writer.save(sequenceNo);

      writer.commit(status);

      sequenceNoDTO.setId(sequenceNo.getId());
      sequenceNoDTO.setSequenceNo(sequenceNo.getSequenceNo());

      return sequenceNoDTO;

    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public SequenceNoDTO updateSequence(SequenceNoDTO sequenceNoDTO) {
    if (sequenceNoDTO == null) return null;

    PaymentWriter writer = paymentDaoManager.getWriter();
    Object status = writer.begin();

    try {
      Long id = sequenceNoDTO.getId();
      if (id == null) return null;

      SequenceNo sequenceNo = writer.getById(SequenceNo.class, id);
      if (sequenceNo == null) return null;

      sequenceNo.fromDTO(sequenceNoDTO);

      writer.save(sequenceNo);
      writer.commit(status);

      return sequenceNoDTO;

    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public SequenceNoDTO getSequenceNoById(long sequenceId) {
    PaymentWriter writer = paymentDaoManager.getWriter();
    SequenceNo sequenceNo = writer.getById(SequenceNo.class, sequenceId);

    if (sequenceNo == null) return null;

    return sequenceNo.toDTO();
  }

  @Override
  public List<SequenceNoDTO> getSequenceNoByNo(String no) {
    PaymentWriter writer = paymentDaoManager.getWriter();

    List<SequenceNoDTO> listSequenceNoDTO = new ArrayList<SequenceNoDTO>();
    for (SequenceNo sequenceNo : writer.getSequenceNoByNo(no)) {
      listSequenceNoDTO.add(sequenceNo.toDTO());
    }

    return listSequenceNoDTO;
  }

  @Override
  public List<SequenceNoDTO> getSequenceNoByTransId(Long transId) {
    PaymentWriter writer = paymentDaoManager.getWriter();

    List<SequenceNoDTO> listSequenceNoDTO = new ArrayList<SequenceNoDTO>();
    List<SequenceNo> sequenceNoList = writer.getSequenceNoByTransId(transId);
    if (CollectionUtils.isEmpty(sequenceNoList)) return null;
    for (SequenceNo sequenceNo : sequenceNoList) {
      listSequenceNoDTO.add(sequenceNo.toDTO());
    }

    return listSequenceNoDTO;
  }

  @Override
  public PaymentServiceJobDTO createPaymentServiceJob(PaymentServiceJobDTO paymentServiceJobDTO) {
    if (paymentServiceJobDTO == null) return null;

    PaymentWriter writer = paymentDaoManager.getWriter();
    Object status = writer.begin();

    try {
      PaymentServiceJob paymentServiceJob = new PaymentServiceJob(paymentServiceJobDTO);

      writer.save(paymentServiceJob);
      writer.commit(status);

      paymentServiceJobDTO.setId(paymentServiceJob.getId());

      return paymentServiceJobDTO;

    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public PaymentServiceJobDTO updatePaymentServiceJob(PaymentServiceJobDTO paymentServiceJobDTO) {
    if (paymentServiceJobDTO == null) return null;

    PaymentWriter writer = paymentDaoManager.getWriter();
    Object status = writer.begin();

    try {
      Long id = paymentServiceJobDTO.getId();
      if (id == null) return null;

      PaymentServiceJob paymentServiceJob = writer.getById(PaymentServiceJob.class, id);
      if (paymentServiceJob == null) return null;

      paymentServiceJob.fromDTO(paymentServiceJobDTO);

      writer.save(paymentServiceJob);
      writer.commit(status);

      return paymentServiceJobDTO;

    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public PaymentServiceJobDTO getPaymentServiceJobById(long paymentServiceJobId) {
    PaymentWriter writer = paymentDaoManager.getWriter();
    PaymentServiceJob paymentServiceJob = writer.getById(PaymentServiceJob.class, paymentServiceJobId);

    if (paymentServiceJob == null) return null;

    return paymentServiceJob.toDTO();
  }

  @Override
  public List<PaymentServiceJobDTO> getPaymentServiceJob() {
    PaymentWriter writer = paymentDaoManager.getWriter();

    List<PaymentServiceJobDTO> paymentServiceJobDTOList = new ArrayList<PaymentServiceJobDTO>();

    for (PaymentServiceJob paymentServiceJob : writer.getPaymentServiceJob()) {
      paymentServiceJobDTOList.add(paymentServiceJob.toDTO());
    }

    return paymentServiceJobDTOList;
  }

  @Override
  public Long createChinaPayParamLog(ChinaPayParamLog log) {
    PaymentWriter writer = paymentDaoManager.getWriter();
    Object status = writer.begin();
    try {
      writer.save(log);
      writer.commit(status);
      return log.getId();
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public void updateChinaPayParamLogChinaPayStatus(long ordId, ChinaPayParamStatus chinaPayParamStatus) {
    PaymentWriter writer = paymentDaoManager.getWriter();
    ChinaPayParamLog log = writer.getChinaPayParamLog(ordId);
    Object status = writer.begin();
    try {
      log.setChinaPayParamStatus(chinaPayParamStatus);
      writer.saveOrUpdate(log);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public void updateChinaPayParamLog(long id, long ordId) {
    PaymentWriter writer = paymentDaoManager.getWriter();
    ChinaPayParamLog log = writer.getById(ChinaPayParamLog.class, id);
    Object status = writer.begin();
    try {
      log.setOrdId(ordId);
      writer.saveOrUpdate(log);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public ChinaPayParamLog getChinaPayParamLog(long ordId) {
    PaymentWriter writer = paymentDaoManager.getWriter();
    return writer.getChinaPayParamLog(ordId);
  }

}
