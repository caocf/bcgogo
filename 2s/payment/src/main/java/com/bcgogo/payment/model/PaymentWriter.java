package com.bcgogo.payment.model;

import com.bcgogo.service.GenericWriterDao;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.transaction.support.ResourceTransactionManager;

import java.util.List;

public class PaymentWriter extends GenericWriterDao {

  public PaymentWriter(ResourceTransactionManager transactionManager) {
    super(transactionManager);
  }

  public List<SequenceNo> getSequenceNoByNo(String sequenceNo) {
    Session session = this.getSession();

    try {
      Query q = SQL.getSequenceNoByNo(session, sequenceNo);

      return (List<SequenceNo>) q.list();
    } finally {
      release(session);
    }
  }

  public List<SequenceNo> getSequenceNoByTransId(Long transId) {
    Session session = this.getSession();

    try {
      Query q = SQL.getSequenceNoByTransId(session, transId);

      return (List<SequenceNo>) q.list();
    } finally {
      release(session);
    }
  }

  public List<Transaction> getTransactionByBaseId(Long baseId, String status) {
    Session session = this.getSession();

    try {
      Query q = SQL.getTransactionByBaseId(session, baseId, status);

      return (List<Transaction>) q.list();
    } finally {
      release(session);
    }
  }

  public List<PaymentServiceJob> getPaymentServiceJob() {
    Session session = this.getSession();

    try {
      Query q = SQL.getPaymentServiceJob(session);

      return (List<PaymentServiceJob>) q.list();
    } finally {
      release(session);
    }
  }

  public List<Transaction> getTransactionByRechargeId(long rechargeId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getTransactionByRechargeId(session, rechargeId);
      return (List<Transaction>) q.list();
    } finally {
      release(session);
    }
  }

  public ChinaPayParamLog getChinaPayParamLog(long ordId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getChinaPayParamLog(session, ordId);
      return (ChinaPayParamLog) q.uniqueResult();
    } finally {
      release(session);
    }
  }
}
