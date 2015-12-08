package com.bcgogo.report.model;

import com.bcgogo.service.GenericWriterDao;
import com.bcgogo.txn.model.RepairOrder;
import com.bcgogo.txn.model.WashOrder;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.transaction.support.ResourceTransactionManager;

import java.util.List;

/**
 * User: Xiao Jian
 * Date: 12-1-5
 */

public class ReportWriter extends GenericWriterDao {

  public ReportWriter(ResourceTransactionManager transactionManager) {
    super(transactionManager);
  }

  public double countShopCarRepairIncome(long shopId, long startTime, long endTime) {
    Session session = this.getSession();

    try {
      Query q = SQL.countShopCarRepairIncome(session, shopId, startTime, endTime);

      Object o = q.uniqueResult();
      if (o == null) return 0.0d;
      return Double.parseDouble(o.toString());
    } finally {
      release(session);
    }

  }

  public double countShopCarWashingIncome(long shopId, long startTime, long endTime) {
    Session session = this.getSession();

    try {
      Query q = SQL.countShopCarWashingIncome(session, shopId, startTime, endTime);

      Object o = q.uniqueResult();
      if (o == null) return 0.0d;
      return Double.parseDouble(o.toString());
    } finally {
      release(session);
    }

  }

  public double countShopSalesIncome(long shopId, long startTime, long endTime) {
    Session session = this.getSession();

    try {
      Query q = SQL.countShopSalesIncome(session, shopId, startTime, endTime);

      Object o = q.uniqueResult();
      if (o == null) return 0.0d;
      return Double.parseDouble(o.toString());
    } finally {
      release(session);
    }

  }

  public double countShopPurchasingCost(long shopId, long startTime, long endTime) {
    Session session = this.getSession();

    try {
      Query q = SQL.countShopPurchasingCost(session, shopId, startTime, endTime);

      Object o = q.uniqueResult();
      if (o == null) return 0.0d;
      return Double.parseDouble(o.toString());
    } finally {
      release(session);
    }

  }

  public double countShopDiscount(long shopId, long startTime, long endTime) {
    Session session = this.getSession();
    try {
      Query q = SQL.countShopDiscount(session, shopId, startTime, endTime);
      Object o = q.uniqueResult();
      if (o == null) return 0.0d;
      return Double.parseDouble(o.toString());
    } finally {
      release(session);
    }
  }

  public List<RepairOrder> countAgentAchievements(long shopId, long startTime, long endTime) {
    Session session = this.getSession();
    try {
      Query q = SQL.countAgentAchievements(session, shopId, startTime, endTime);
      return (List<RepairOrder>) q.list();
    } finally {
      release(session);
    }
  }

  public double countItemAgentAchievements(long repair_order_id, long startTime, long endTime) {
    Session session = this.getSession();

    try {
      Query q = SQL.countItemAgentAchievements(session, repair_order_id, startTime, endTime);
      Object o = q.uniqueResult();
      if (o == null) return 0.0d;
      return Double.parseDouble(o.toString());
    } finally {
      release(session);
    }
  }

  public double countServiceAgentAchievements(long repair_order_id, long startTime, long endTime) {
    Session session = this.getSession();

    try {
      Query q = SQL.countServiceAgentAchievements(session, repair_order_id, startTime, endTime);
      Object o = q.uniqueResult();
      if (o == null) return 0.0d;
      return Double.parseDouble(o.toString());
    } finally {
      release(session);
    }

  }

  public List<WashOrder> countWashAgentAchievements(long shopId, long startTime, long endTime) {
    Session session = this.getSession();

    try {
      Query q = SQL.countWashAgentAchievements(session, shopId, startTime, endTime);
      return (List<WashOrder>) q.list();
    } finally {
      release(session);
    }

  }

  public double countShopRepairOrderServiceIncome(long shopId, long startTime, long endTime) {
    Session session = this.getSession();
    try {
      Query q = SQL.countShopRepairOrderServiceIncome(session, shopId, startTime, endTime);

      Object o = q.uniqueResult();
      if (o == null) return 0.0d;
      return Double.parseDouble(o.toString());
    } finally {
      release(session);
    }
  }

}
