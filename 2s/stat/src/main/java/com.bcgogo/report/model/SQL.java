package com.bcgogo.report.model;


import com.bcgogo.txn.model.RepairOrder;
import com.bcgogo.txn.model.WashOrder;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.type.StandardBasicTypes;

import java.util.Calendar;


/**
 * User: xiajian
 * Date: 12-1-5
 */

public class SQL {

  /**
   * 根据店面Id,时间段统计修车收入
   *
   * @param session
   * @param shopId
   * @param startTime
   * @param endTime
   * @return
   */
  public static Query countShopCarRepairIncome(Session session, long shopId, long startTime, long endTime) {
    return session.createSQLQuery("select sum(total) as total from repair_order where shop_id = :shopId and created >= :startTime and created < :endTime")
        .addScalar("total", StandardBasicTypes.DOUBLE)
        .setLong("shopId", shopId)
        .setLong("startTime", startTime)
        .setLong("endTime", endTime);
  }

  /**
   * 根据店面Id,时间段统计洗车收入
   *
   * @param session
   * @param shopId
   * @param startTime
   * @param endTime
   * @return
   */
  public static Query countShopCarWashingIncome(Session session, long shopId, long startTime, long endTime) {
    return session.createSQLQuery("select sum(cash_num) from wash_order where shop_id = :shopId and created >= :startTime and created < :endTime")
        .setLong("shopId", shopId)
        .setLong("startTime", startTime)
        .setLong("endTime", endTime);
  }

  /**
   * 根据店面Id,时间段统计销售收入
   *
   * @param session
   * @param shopId
   * @param startTime
   * @param endTime
   * @return
   */
  public static Query countShopSalesIncome(Session session, long shopId, long startTime, long endTime) {
    return session.createSQLQuery("select sum(total) from sales_order where shop_id = :shopId and created >= :startTime and created < :endTime")
        .setLong("shopId", shopId)
        .setLong("startTime", startTime)
        .setLong("endTime", endTime);
  }

  /**
   * 根据店面Id,时间段统计采购入库成本
   *
   * @param session
   * @param shopId
   * @param startTime
   * @param endTime
   * @return
   */
  public static Query countShopPurchasingCost(Session session, long shopId, long startTime, long endTime) {
    return session.createSQLQuery("select sum(total) from sales_order where shop_id = :shopId and created >= :startTime and created < :endTime")
        .setLong("shopId", shopId)
        .setLong("startTime", startTime)
        .setLong("endTime", endTime);
  }


   /**
   * 根据店面Id,时间段统计施工单中服务费用收入
   *
   * @param session
   * @param shopId
   * @param startTime
   * @param endTime
   * @return
   */
  public static Query countShopRepairOrderServiceIncome(Session session, long shopId, long startTime, long endTime) {
    return session.createSQLQuery("select sum(total) as total from repair_order_service where shop_id = :shopId and created >= :startTime and created < :endTime")
        .addScalar("total", StandardBasicTypes.DOUBLE)
        .setLong("shopId", shopId)
        .setLong("startTime", startTime)
        .setLong("endTime", endTime);
  }
    /**
   * 根据店面id 统计这段时间的折扣总费用
   * @param session
   * @param shopId
   * @param startTime
   * @param endTime
   * @return
   */
  public static Query countShopDiscount(Session session, long shopId, long startTime, long endTime) {
    return session.createSQLQuery("select sum(discount) from receivable where shop_id = :shopId and created >= :startTime and created < :endTime")
        .setLong("shopId", shopId)
        .setLong("startTime", startTime)
        .setLong("endTime", endTime);
  }
  public static Query countAgentAchievements(Session session, long shopId, long startTime, long endTime) {
    return session.createSQLQuery("select * from repair_order re where re.shop_id = :shopId and re.status = 3 and re.created >= :startTime and re.created < :endTime ")
        .addEntity(RepairOrder.class)
        .setLong("shopId", shopId)
        .setLong("startTime",startTime).setLong("endTime",endTime);
  }

  public static Query countItemAgentAchievements(Session session, long repair_order_id, long startTime, long endTime) {
    return session.createSQLQuery("select sum(total) from repair_order_item re where re.repair_order_id = :repair_order_id ")
        .setLong("repair_order_id", repair_order_id);
  }

  public static Query countServiceAgentAchievements(Session session, long repair_order_id, long startTime, long endTime) {
    return session.createSQLQuery("select sum(total) from repair_order_service re where re.repair_order_id = :repair_order_id ")
        .setLong("repair_order_id", repair_order_id);
  }

  public static Query countWashAgentAchievements(Session session, long shopId, long startTime, long endTime) {

    Calendar calendar = Calendar.getInstance();
    calendar.set(Calendar.HOUR_OF_DAY, 0);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 0);
    long start = calendar.getTimeInMillis();
    calendar.set(Calendar.HOUR_OF_DAY, 23);
    calendar.set(Calendar.MINUTE, 59);
    calendar.set(Calendar.SECOND, 59);
    long end = calendar.getTimeInMillis();

    return session.createSQLQuery("select wo.* from wash_order wo where wo.shop_id=:shopId and wo.created >=:start and wo.created <=:end and order_type<>0")
        .addEntity(WashOrder.class).setLong("shopId", shopId).setLong("start", start).setLong("end", end);
  }

}
