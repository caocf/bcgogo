package com.bcgogo.userreport.model;

import com.bcgogo.service.GenericWriterDao;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.transaction.support.ResourceTransactionManager;

/**
 * User: Xiao Jian
 * Date: 12-1-5
 */

public class UserReportWriter extends GenericWriterDao {

  public UserReportWriter(ResourceTransactionManager transactionManager) {
    super(transactionManager);
  }

  public long countShopCustomer(long shopId) {
    Session session = this.getSession();

    try {
      Query q = SQL.countShopCustomer(session, shopId);

      Object o = q.uniqueResult();
      if (o == null) return 0l;
      return Long.parseLong(o.toString());
    } finally {
      release(session);
    }

  }

  public long countShopCustomerMobile(long shopId) {
    Session session = this.getSession();

    try {
      Query q = SQL.countShopCustomerMobile(session, shopId);

      Object o = q.uniqueResult();
      if (o == null) return 0l;
      return Long.parseLong(o.toString());
    } finally {
      release(session);
    }

  }

  public long countShopCustomerInsurance(long shopId) {
    Session session = this.getSession();

    try {
      Query q = SQL.countShopCustomerInsurance(session, shopId);

      Object o = q.uniqueResult();
      if (o == null) return 0l;
      return Long.parseLong(o.toString());
    } finally {
      release(session);
    }

  }

  public long countShopCustomerInspection(long shopId) {
    Session session = this.getSession();

    try {
      Query q = SQL.countShopCustomerInspection(session, shopId);

      Object o = q.uniqueResult();
      if (o == null) return 0l;
      return Long.parseLong(o.toString());
    } finally {
      release(session);
    }

  }

  public long countShopCustomerBirthday(long shopId) {
    Session session = this.getSession();

    try {
      Query q = SQL.countShopCustomerBirthday(session, shopId);

      Object o = q.uniqueResult();
      if (o == null) return 0l;
      return Long.parseLong(o.toString());
    } finally {
      release(session);
    }

  }

}
