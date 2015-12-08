package com.bcgogo.txnRead.model;

import com.bcgogo.enums.txn.preBuyOrder.BusinessChanceType;
import com.bcgogo.utils.DateUtil;
import org.hibernate.Query;
import org.hibernate.Session;

import java.text.ParseException;

/**
 * Created with IntelliJ IDEA.
 * User: xzhu
 * Date: 13-6-24
 * Time: 下午1:31
 * To change this template use File | Settings | File Templates.
 */
class SQL {
  /**
   * @param session
   * @param shopId
   * @return
   */
  public static Query getValidPreBuyOrderItemByShopId(Session session, Long shopId,BusinessChanceType... businessChanceType) {
    StringBuffer sb = new StringBuffer("select pb from PreBuyOrder p,PreBuyOrderItem pb where p.id=pb.preBuyOrderId and p.shopId=:shopId and p.businessChanceType in(:businessChanceType) and p.deleted='FALSE'");
    try {
      sb.append(" and p.endDate>=").append(DateUtil.getTheDayTime());
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return session.createQuery(sb.toString()).setLong("shopId", shopId).setParameterList("businessChanceType",businessChanceType);
  }

  public static Query getLastWeekSalesByShopId(Session session, Long shopId, long startTime,long endTime ) {
    return session.createQuery(" select productId, sum(amount)  from SalesStat where shopId=:shopId and statTime<:endTime and statTime>=:startTime  group by productId ")
        .setLong("shopId", shopId).setLong("endTime", endTime).setLong("startTime", startTime);
  }
  public static Query getLastWeekSalesChangeByShopId(Session session, Long shopId, long startTime,long endTime ) {
    return session.createQuery(" select productId, sum(amount)  from SalesStatChange where shopId=:shopId and statTime<:endTime and statTime>=:startTime  group by productId ")
        .setLong("shopId", shopId).setLong("endTime", endTime).setLong("startTime", startTime);
  }

    public static Query getLatestSalesStatBeforeTime(Session session, Long shopId, Long productId, long startTimeOfTimeDay) {
    return session.createQuery("from SalesStat where shopId=:shopId and productId=:productId and statTime<:statTime order by statTime desc")
        .setLong("shopId", shopId).setLong("productId", productId).setLong("statTime", startTimeOfTimeDay).setMaxResults(1);
  }

}
