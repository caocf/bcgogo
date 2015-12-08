package com.bcgogo.userreport.model;

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
   * 根据店面Id统计客户数
   *
   * @param session
   * @param shopId
   * @return
   */
  public static Query countShopCustomer(Session session, long shopId) {
    return session.createSQLQuery("select count(id) as amount from customer where shop_id = :shopId")
        .addScalar("amount", StandardBasicTypes.LONG)
        .setLong("shopId", shopId);
  }

  /**
   * 根据店面Id统计注册了手机号码的客户数
   *
   * @param session
   * @param shopId
   * @return
   */
  public static Query countShopCustomerMobile(Session session, long shopId) {
    return session.createSQLQuery("select count(id) as amount from customer where shop_id = :shopId and mobile is not null and trim(mobile) != ''")
        .addScalar("amount", StandardBasicTypes.LONG)
        .setLong("shopId", shopId);
  }

  /**
   * 根据店面Id统计三个月保险到期的客户数
   *
   * @param session
   * @param shopId
   * @return
   */
  public static Query countShopCustomerInsurance(Session session, long shopId) {
    Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.MONTH, 3);
    calendar.add(Calendar.DATE, 1);

    return session.createSQLQuery("select count(id) as amount from customer_service_job where shop_id = :shopId and remind_type = 0 and remind_time < :remindTime")
        .addScalar("amount", StandardBasicTypes.LONG)
        .setLong("shopId", shopId)
        .setLong("remindTime", calendar.getTimeInMillis());
  }

  /**
   * 根据店面Id统计三个月验车到期的客户数
   *
   * @param session
   * @param shopId
   * @return
   */
  public static Query countShopCustomerInspection(Session session, long shopId) {
    Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.MONTH, 3);
    calendar.add(Calendar.DATE, 1);

    return session.createSQLQuery("select count(id) as amount from customer_service_job where shop_id = :shopId and remind_type = 1 and remind_time < :remindTime")
        .addScalar("amount", StandardBasicTypes.LONG)
        .setLong("shopId", shopId)
        .setLong("remindTime", calendar.getTimeInMillis());
  }

  /**
   * 根据店面Id统计半个月内过生日的客户数
   *
   * @param session
   * @param shopId
   * @return
   */
  public static Query countShopCustomerBirthday(Session session, long shopId) {
    Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.DATE, 16);

    return session.createSQLQuery("select count(id) as amount from customer_service_job where shop_id = :shopId and remind_type = 2 and remind_time < :remindTime")
        .addScalar("amount", StandardBasicTypes.LONG)
        .setLong("shopId", shopId)
        .setLong("remindTime", calendar.getTimeInMillis());
  }
}
