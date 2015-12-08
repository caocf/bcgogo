package com.bcgogo.payment.model;

import com.bcgogo.enums.payment.ChinaPayParamStatus;
import org.hibernate.Query;
import org.hibernate.Session;

public class SQL {

  public static Query getSequenceNoByNo(Session session, String sequenceNo) {
    return session.createQuery("select sn from SequenceNo as sn where sn.sequenceNo  = :sequenceNo")
        .setString("sequenceNo", sequenceNo);
  }

  public static Query getSequenceNoByTransId(Session session, long transId) {
    return session.createQuery("select sn from SequenceNo as sn where sn.transId  = :transId")
        .setLong("transId", transId);
  }

  public static Query getTransactionByBaseId(Session session, Long baseId, String status) {
    return session.createQuery("select t from Transaction as t where t.baseId  = :baseId and t.status=:status")
        .setLong("baseId", baseId).setString("status", status);
  }

  public static Query getPaymentServiceJob(Session session) {
    return session.createQuery("select psj from PaymentServiceJob as psj");
  }

  public static Query getTransactionByRechargeId(Session session, long rechargeId) {
    return session.createQuery("select t from Transaction t where t.referenceId  = :referenceId ").setLong("referenceId", rechargeId);
  }

  public static Query getChinaPayParamLog(Session session, long ordId) {
    return session.createQuery("from ChinaPayParamLog where ordId =:ordId")
        .setLong("ordId", ordId);
  }
}
