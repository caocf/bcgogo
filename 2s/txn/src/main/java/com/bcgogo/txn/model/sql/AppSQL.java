package com.bcgogo.txn.model.sql;

import com.bcgogo.enums.app.AppUserBillStatus;
import org.hibernate.Query;
import org.hibernate.Session;

/**
 * User: ZhangJuntao
 * Date: 13-10-25
 * Time: 下午3:33
 */
public class AppSQL {

  public static Query getAppUserBillListByUserNo(Session session, String appUserNo, int pageSize, int currentPage) {
    return session.createQuery("from AppUserBill where appUserNo =:appUserNo and status=:status")
        .setParameter("appUserNo", appUserNo)
        .setParameter("status", AppUserBillStatus.SAVED)
        .setMaxResults(pageSize)
        .setFirstResult(pageSize * (currentPage - 1));
  }

  public static Query countAppUserBillListByUserNo(Session session, String appUserNo) {
    return session.createQuery("select count(a.id) from AppUserBill a where a.appUserNo =:appUserNo and  status=:status")
        .setParameter("status", AppUserBillStatus.SAVED)
        .setParameter("appUserNo", appUserNo);
  }
}
