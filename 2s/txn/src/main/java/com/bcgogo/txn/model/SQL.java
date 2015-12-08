package com.bcgogo.txn.model;


import com.bcgogo.common.Pager;
import com.bcgogo.common.Pair;
import com.bcgogo.common.Sort;
import com.bcgogo.constant.SmsRechargeConstants;
import com.bcgogo.enums.*;
import com.bcgogo.enums.Product.NormalProductStatType;
import com.bcgogo.enums.Product.ProductRelevanceStatus;
import com.bcgogo.enums.app.*;
import com.bcgogo.enums.assistantStat.AchievementStatType;
import com.bcgogo.enums.assistantStat.AssistantRecordType;
import com.bcgogo.enums.common.ObjectStatus;
import com.bcgogo.enums.notification.OperatorType;
import com.bcgogo.enums.payment.LoanTransfersStatus;
import com.bcgogo.enums.shop.ShopKind;
import com.bcgogo.enums.sms.StatType;
import com.bcgogo.enums.supplierComment.CommentRecordType;
import com.bcgogo.enums.supplierComment.CommentStatus;
import com.bcgogo.enums.txn.AdvertStatus;
import com.bcgogo.enums.txn.finance.*;
import com.bcgogo.enums.txn.preBuyOrder.BusinessChanceType;
import com.bcgogo.enums.txn.pushMessage.*;
import com.bcgogo.payment.dto.RechargeSearchDTO;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.product.productManage.ProductSearchCondition;
import com.bcgogo.remind.dto.message.SearchMessageCondition;
import com.bcgogo.search.dto.OrderSearchConditionDTO;
import com.bcgogo.search.dto.PreBuyOrderSearchCondition;
import com.bcgogo.search.dto.QuotedPreBuyOrderSearchConditionDTO;
import com.bcgogo.search.dto.RecOrPayIndexDTO;
import com.bcgogo.stat.dto.CostStatConditionDTO;
import com.bcgogo.stat.dto.SalesStatCondition;
import com.bcgogo.txn.dto.*;
import com.bcgogo.txn.dto.StatementAccount.OrderDebtType;
import com.bcgogo.txn.dto.assistantStat.AssistantStatSearchDTO;
import com.bcgogo.txn.dto.enquiry.EnquirySearchConditionDTO;
import com.bcgogo.txn.dto.finance.AccountSearchCondition;
import com.bcgogo.txn.dto.finance.BcgogoReceivableSearchCondition;
import com.bcgogo.txn.dto.finance.SmsRechargeSearchCondition;
import com.bcgogo.txn.dto.finance.SmsRecordSearchCondition;
import com.bcgogo.txn.dto.pushMessage.TalkMessageCondition;
import com.bcgogo.txn.dto.pushMessage.faultCode.FaultInfoSearchConditionDTO;
import com.bcgogo.txn.dto.secondary.RepairOrderSecondaryCondition;
import com.bcgogo.txn.dto.supplierComment.CommentRecordDTO;
import com.bcgogo.txn.model.app.AppointOrder;
import com.bcgogo.txn.model.pushMessage.PushMessage;
import com.bcgogo.txn.model.pushMessage.PushMessageReceiver;
import com.bcgogo.utils.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.CacheMode;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.type.StandardBasicTypes;

import java.text.ParseException;
import java.util.*;

class SQL {
  /**
   *  获得对账单号
   */
  public static Query getStatementAccountOrderNo(Session session, Long shopId, Long statementAccountOrderId) {

    return session.createQuery("select sa.receiptNo from StatementAccountOrder sa where sa.id=:statementAccountId and sa.shopId=:shopId")
        .setLong("statementAccountId",statementAccountOrderId).setLong("shopId",shopId);
  }
  /**
   * 根据订单类型和订单ID查询该单据历史结算记录
   * @param session
   * @param shopId
   * @param orderTypeEnum
   * @param orderId
   * @return
   */
  public static Query getSettledRecord(Session session, Long shopId, OrderTypes orderTypeEnum, Long orderId) {
    Query query = null;
    if(OrderTypes.INVENTORY.equals(orderTypeEnum)) {
      query = session.createQuery("from PayableHistoryRecord phr where phr.shopId=:shopId and phr.purchaseInventoryId=:purchaseInventoryId")
          .setLong("shopId",shopId).setLong("purchaseInventoryId",orderId);
    } else if(OrderTypes.RETURN.equals(orderTypeEnum)) {
      query = session.createQuery("from PayableHistoryRecord phr where phr.shopId=:shopId and phr.purchaseInventoryId=:purchaseReturnId")
          .setLong("shopId",shopId).setLong("purchaseReturnId",orderId);
    } else {
      query = session.createQuery("from ReceptionRecord rr where rr.shopId=:shopId and rr.orderId=:orderId")
          .setLong("shopId",shopId).setLong("orderId",orderId);
    }
    return query;
  }

  /**
   * 根据采购单ID查询采购单货品表
   *
   * @param session
   * @param purchaseOrderId
   *
   * @return
   */
  public static Query getPurchaseOrderItemsByOrderId(Session session, Long purchaseOrderId) {
    return session.createQuery("from PurchaseOrderItem p where p.purchaseOrderId=:purchaseOrderId")
        .setLong("purchaseOrderId", purchaseOrderId);
  }

  /**
   * 根据入度库单ID查询入库单货品表
   *
   * @param session
   * @param purchaseInventoryId
   * @return
   */
  public static Query getPurchaseInventoryItemsByInventoryId(Session session, Long purchaseInventoryId) {
    return session.createQuery("from PurchaseInventoryItem p where p.purchaseInventoryId=:purchaseInventoryId")
        .setLong("purchaseInventoryId", purchaseInventoryId);
  }

  public static Query getPurchaseReturnItemsByReturnId(Session session, Long purchaseReturnId) {
    return session.createQuery("from PurchaseReturnItem p where p.purchaseReturnId=:purchaseReturnId")
        .setLong("purchaseReturnId", purchaseReturnId);
  }


  /**
   * 根据销售单ID查询销售单货品表
   *
   * @param session
   * @param salesOrderId
   * @return
   */
  public static Query getSalesOrderItemsByOrderId(Session session, Long salesOrderId) {
    return session.createQuery("from SalesOrderItem p where p.salesOrderId=:salesOrderId")
        .setLong("salesOrderId", salesOrderId);
  }

  /**
   * 根据汽修车饰单ID查询汽修车饰单货品表
   *
   * @param session
   * @param repairOrderId
   * @return
   */
  public static Query getRepairOrderItemsByRepairOrderId(Session session, Long repairOrderId) {
    return session.createQuery("from RepairOrderItem r where r.repairOrderId=:repairOrderId")
        .setLong("repairOrderId", repairOrderId);
  }

  /**
   * 根据汽修车饰单ID查询汽修车饰单服务项目表
   *
   * @param session
   * @param repairOrderId
   * @return
   */
  public static Query getRepairOrderServicesByRepairOrderId(Session session, Long repairOrderId) {
    return session.createQuery("from RepairOrderService r where r.repairOrderId=:repairOrderId")
        .setLong("repairOrderId", repairOrderId);
  }


  /**
   * 根据施工单ID查询施工单货品表
   *
   * @param session
   * @param repairOrderId
   * @return
   */
  public static Query getRepairOrderItemsByOrderId(Session session, Long repairOrderId) {
    return session.createQuery("from RepairOrderItem p where p.repairOrderId=:repairOrderId")
        .setLong("repairOrderId", repairOrderId);
  }

  /**
   * 根据施工单ID查询施工单服务表
   *
   * @param session
   * @param repairOrderId
   * @return
   */
  public static Query getRepairOrderServicesByOrderId(Session session, Long repairOrderId) {
    return session.createQuery("from RepairOrderService p where p.repairOrderId=:repairOrderId")
        .setLong("repairOrderId", repairOrderId);
  }


  /**
   * 根据店面ID单据类型和单据Id查询收款单
   *
   * @param session
   * @param shopId
   * @param orderTypeEnum
   * @param orderId
   * @return
   */
  public static Query getReceivableByShopIdAndOrderTypeAndOrderId(Session session, Long shopId, OrderTypes orderTypeEnum, Long orderId) {
//    Long orderType = ConstantEnumMapping.parseOrderTypeEnumToReceivableConstant(orderTypeEnum);
    return session.createQuery("from Receivable r where r.shopId=:shopId and r.orderId =:orderId")
        .setLong("shopId", shopId).setLong("orderId", orderId);
  }

  /**
   * 根据收款单ID查询收款单记录
   *
   * @param session
   * @param receivableId
   * @return
   */
  public static Query getReceptionRecordsByReceivalbeId(Session session, Long receivableId) {
    return session.createQuery("from ReceptionRecord r where r.receivableId =:receivableId")
        .setLong("receivableId", receivableId);
  }

  /**
   * 根据店面Id,维修单Id,事件类型查询维修事件提醒表
   *
   * @param session
   * @param shopId
   * @param repairOrderId
   * @param eventTypeEnum
   * @return
   */
  public static Query getRepairRemindEventByShopIdAndOrderIdAndType(Session session, Long shopId, Long repairOrderId, RepairRemindEventTypes eventTypeEnum) {
    StringBuffer hql = new StringBuffer("from RepairRemindEvent r where r.shopId =:shopId ");
    if (null != repairOrderId)
      hql.append(" and r.repairOrderId =:repairOrderId");
    if (null != eventTypeEnum)
      hql.append(" and r.eventTypeEnum = :eventTypeEnum");
    hql.append(" order by r.finishTime desc");
    Query query = session.createQuery(hql.toString()).setLong("shopId", shopId);
    if (null != repairOrderId)
      query = query.setLong("repairOrderId", repairOrderId);
    if (null != eventTypeEnum)
      query = query.setParameter("eventTypeEnum", eventTypeEnum);
    return query;
  }


  public static Query getRepairRemindEvents(Session session, Long shopId, RepairRemindEventTypes eventType, Long pagNo, Long pageSize) {
    Long currentTime = null;
    try {
      currentTime = DateUtil.getToday(DateUtil.YEAR_MONTH_DATE, new Date()) -1;
    } catch (Exception e) {
      e.printStackTrace();
    }
    StringBuffer hql = new StringBuffer();
    hql.append("(");
    hql.append("select r.* from repair_remind_event r where r.shop_id =:shopId ");
    hql.append(" and r.finish_time >= " + currentTime);
    hql.append(" and r.finish_time < " + (currentTime+2*24*3600*1000));
    hql.append(" order by r.finish_time");
    if (null != eventType){
      hql.append(" and r.event_type_enum =:eventType");
    }
    hql.append(")");
    hql.append(" union all ");
    hql.append("(");
    hql.append("select r.* from repair_remind_event r where r.shop_id =:shopId ");
    hql.append(" and (r.finish_time < " + currentTime);
    hql.append(" or r.finish_time >= " + (currentTime+2*24*3600*1000) + " or r.finish_time is null) ");
    hql.append(" order by r.finish_time desc");
    if (null != eventType){
      hql.append(" and r.event_type_enum =:eventType");
    }
    hql.append(")");
    Query query = session.createSQLQuery(hql.toString()).addEntity(RepairRemindEvent.class).setLong("shopId", shopId);
    if (null != eventType){
      query = query.setParameter("eventType", eventType);
    }
    if (pagNo != null) {
      int firstResult = pagNo.intValue() * pageSize.intValue();
      query.setFirstResult(firstResult).setMaxResults(pageSize.intValue());
    }
    return query;
  }

  public static Query getAllRepairRemindEventsByCustomerIds(Session session, Long shopId, Long[] customerIds) {
    String hql= "from RepairRemindEvent  where shopId =:shopId and customerId in (:customerIds) ";
    return session.createQuery(hql.toString()).setLong("shopId", shopId).setParameterList("customerIds",customerIds);
  }

  public static Query countRepairRemindEvents(Session session, Long shopId, RepairRemindEventTypes eventType) {
    StringBuffer hql = new StringBuffer("select count(*) from RepairRemindEvent r where r.shopId =:shopId ");

    if (null != eventType)
      hql.append(" and r.eventTypeEnum =:eventType");

    Query query = session.createQuery(hql.toString()).setLong("shopId", shopId);

    if (null != eventType)
      query = query.setParameter("eventType", eventType);
    return query;
  }

  public static Query getRepairRemindEventsByProductId(Session session, Long shopId, RepairRemindEventTypes eventTypeEnum, Long productId) {
    StringBuffer hql = new StringBuffer();
    hql.append("from RepairRemindEvent r where r.shopId =:shopId and r.productId =:productId");
    if (null != eventTypeEnum) {
      hql.append(" and r.eventTypeEnum =:eventType");
    }
    hql.append(" order by r.finishTime desc");

    Query query = session.createQuery(hql.toString()).setLong("shopId", shopId).setLong("productId", productId);
    if (null != eventTypeEnum)
      query = query.setParameter("eventType", eventTypeEnum);
    return query;
  }


  /**
   * 根据店面Id,客户Id,车辆id查询车辆预约服务
   *
   * @param session
   * @param shopId
   * @param customerId
   * @param vehicleId
   * @return
   */
  public static Query getScheduleServiceEventByShopIdAndCustomerIdAndVehicleId(Session session, Long shopId, Long customerId, Long vehicleId) {
    StringBuffer hql = new StringBuffer("from ScheduleServiceEvent s where s.shopId =:shopId ");
    if (null != customerId) {
      hql.append(" and s.customerId =:customerId ");
    }
    if (null != vehicleId) {
      hql.append(" and s.vechicleId =:vehicleId ");
    }
    Query query = session.createQuery(hql.toString()).setLong("shopId", shopId);
    if (null != customerId) {
      query.setLong("customerId", customerId);
    }
    if (null != vehicleId) {
      query.setLong("vehicleId", vehicleId);
    }
    return query;
  }

  /**
   * 根据店面Id,事件类型,产品id更新维修事件提醒表
   *
   * @param session
   * @param shopId
   * @param eventType
   * @param productId
   * @return
   */
  public static Query updateRepairRemindEventByShopIdAndTypeAndProductId(Session session, Long shopId,
                                                                         RepairRemindEventTypes eventType, Long productId,
                                                                         RepairRemindEventTypes targetEventType, Long repairOrderId) {
    StringBuffer hql = new StringBuffer();
    hql.append("update RepairRemindEvent r set r.eventTypeEnum=:targetEventTypeEnum ")
        .append("where r.shopId =:shopId and r.eventTypeEnum =:eventType and r.eventContent =:eventContent");
    if (null != repairOrderId)
      hql.append(" and r.repairOrderId=:repairOrderId");
    Query query = session.createQuery(hql.toString()).setLong("shopId", shopId)
        .setParameter("eventType", eventType)
        .setLong("eventContent", productId)
        .setParameter("targetEventTypeEnum", targetEventType);
    if (null != repairOrderId)
      query = query.setLong("repairOrderId", repairOrderId);
    return query;
  }


  /**
   * 根据店面Id,事件类型,维修单id删除维修事件提醒表
   *
   * @param session
   * @param shopId
   * @param eventTypeEnum
   * @return
   */
  public static Query deleteRepairRemindEventByShopIdAndRepairOrderIdAndType(Session session, Long shopId, Long repairOrderId, RepairRemindEventTypes eventTypeEnum) {
    return session.createQuery("delete from RepairRemindEvent r where r.shopId =:shopId and r.repairOrderId =:repairOrderId and r.eventTypeEnum =:eventType")
        .setLong("shopId", shopId)
        .setLong("repairOrderId", repairOrderId)
        .setParameter("eventType", eventTypeEnum);
  }

  public static Query deleteRepairRemindEventByShopIdAndRepairOrderIdAndType(Session session, Long shopId, Long repairOrderId) {
    return session.createQuery("delete from RepairRemindEvent r where r.shopId =:shopId and r.repairOrderId =:repairOrderId")
        .setLong("shopId", shopId)
        .setLong("repairOrderId", repairOrderId);
  }

  /**
   * 根据店面Id,事件类型,事件内容,维修单id删除维修事件提醒表
   *
   * @param session
   * @param shopId
   * @param eventTypeEnum
   * @return
   */
  public static Query deleteRepairRemindEventByShopIdAndRepairOrderIdAndTypeAndContent(Session session, Long shopId, Long repairOrderId, RepairRemindEventTypes eventTypeEnum, Long eventContent) {
    return session.createQuery("delete from RepairRemindEvent r where r.shopId =:shopId and r.repairOrderId =:repairOrderId and r.eventTypeEnum =:eventType and r.eventContent =:eventContent")
        .setLong("shopId", shopId)
        .setLong("repairOrderId", repairOrderId)
        .setParameter("eventType", eventTypeEnum)
        .setLong("eventContent", eventContent);
  }

  /**
   * 根据shopId,productId查询货品采购价记录表
   *
   * @param session
   * @param shopId
   * @param productId
   * @return
   */
  public static Query getLatestPurchasePriceByShopIdAndProductId(Session session, Long shopId, Long productId) {
    return session.createQuery("from PurchasePrice p where p.shopId=:shopId and p.productId=:productId order by id desc")
        .setLong("shopId", shopId).setLong("productId", productId);
  }

  /**
   * 根据店面Id,采购单id删除采购入库事件提醒表
   *
   * @param shopId
   * @param purchaseOrderId
   * @return
   */
  public static Query deleteInventoryRemindEventByShopIdAndPurchaseOrderId(Session session, Long shopId, Long purchaseOrderId) {
    return session.createQuery("delete from InventoryRemindEvent i where i.shopId =:shopId and i.purchaseOrderId =:purchaseOrderId")
        .setLong("shopId", shopId).setLong("purchaseOrderId", purchaseOrderId);
  }

  public static Query countInventoryRemindEventByShopIdAndPageNoAndPageSize(Session session, Long shopId) {
    return session.createQuery("select count(i) from InventoryRemindEvent i where i.shopId =:shopId").setLong("shopId", shopId);
  }

  public static Query getInventoryRemindEventByShopIdAndPageNoAndPageSize(Session session, Long shopId, Integer pageNo, Integer pageSize) {
    Query q = session.createQuery("from InventoryRemindEvent i where i.shopId =:shopId").setLong("shopId", shopId);
    if(pageNo!=null && pageSize!=null){
      q.setFirstResult(pageNo * pageSize).setMaxResults(pageSize);
    }
    return q;
  }


  public static Query updateRepairOrderStatus(Session session, Long shopId, Long orderId, OrderStatus status) {
    return session.createQuery("update RepairOrder s set s.statusEnum =:statusEnum where s.shopId =:shopId and s.id =:id")
        .setLong("shopId", shopId).setParameter("statusEnum", status).setLong("id", orderId);
  }

  public static Query getUnbalancedAccountRepairOrderByVehicleNumber(Session session, Long shopId, Long vehicleId, Long orderId) {
    StringBuffer sql = new StringBuffer("");
    sql.append("select ro.* from repair_order ro where ro.shop_id=:shopId and ")
        .append("ro.vechicle_id=:vehicleId and ro.status_enum in ('REPAIR_DISPATCH','REPAIR_DONE') ");
    if (orderId != null) {
      sql.append(" and ro.id=:orderId ");
    }
    sql.append("order by ro.created desc limit 1 ");
    Query query = session.createSQLQuery(sql.toString()).addEntity(RepairOrder.class).setLong("shopId", shopId).
        setLong("vehicleId", vehicleId);
    if (orderId != null) {
      query.setLong("orderId", orderId);
    }
    return query;
  }

  public static Query countRepairRemindEventByShopId(Session session, Long shopId, RepairRemindEventTypes eventTypeEnum, Long[] productIds) {
    StringBuffer sql = new StringBuffer();
    sql.append("select count(*) from RepairRemindEvent r where r.shopId = :shopId and r.eventTypeEnum =:eventType");
    if (null != productIds && productIds.length == 1) {
      sql.append(" and r.productId = :productId");
    } else if (null != productIds && productIds.length > 1) {
      sql.append(" and r.productId in(").append(StringUtils.join(productIds, ",")).append(")");
    }
    Query query = session.createQuery(sql.toString()).setLong("shopId", shopId).setParameter("eventType", eventTypeEnum);

    if (null != productIds && productIds.length == 1) {
      query.setLong("productId", productIds[0]);
    }
    return query;
  }

  public static Query getRepairRemindEventByShopId(Session session, Long shopId, RepairRemindEventTypes eventType,
                                                   Long[] productIds, int pageNo, int pageSize) {
    StringBuffer sql = new StringBuffer();
    sql.append("select r from RepairRemindEvent r where r.shopId = :shopId and r.eventTypeEnum =:eventType");
    if (null != productIds && productIds.length == 1) {
      sql.append(" and r.productId = :productId");
    } else if (null != productIds && productIds.length > 1) {
      sql.append(" and r.productId in(").append(StringUtils.join(productIds, ",")).append(")");
    }
    sql.append(" order by r.finishTime");

    Query query = session.createQuery(sql.toString()).setLong("shopId", shopId)
        .setParameter("eventType", eventType);

    if (null != productIds && productIds.length == 1) {
      query.setLong("productId", productIds[0]);
    }
    query.setFirstResult(pageNo * pageSize).setMaxResults(pageSize);
    return query;
  }
  /*
 根据客户ID获取客户洗车单列表
  */
  public static Query getWashOrderByCustomer(Session session, long customerId) {
    return session.createSQLQuery("SELECT * FROM wash_order WHERE customer_id=:customerId ORDER BY created DESC").addEntity(WashOrder.class)
        .setLong("customerId", customerId);
  }

  /*
 查询客户当天洗车次数
  */
  public static Query getTodayWashTimes(Session session, long customerId) {
    Calendar cal = Calendar.getInstance();
    cal.set(Calendar.HOUR_OF_DAY, 0);
    cal.set(Calendar.MINUTE, 0);
    cal.set(Calendar.SECOND, 0);
    long start = cal.getTimeInMillis();
    cal.set(Calendar.HOUR_OF_DAY, 23);
    cal.set(Calendar.MINUTE, 59);
    cal.set(Calendar.SECOND, 59);
    long end = cal.getTimeInMillis();

    return session.createSQLQuery("SELECT wo.* FROM wash_order wo WHERE wo.customer_id=:customerId AND wo.created >=:start AND wo.created <=:end AND order_type<>0")
        .addEntity(WashOrder.class).setLong("customerId", customerId).setLong("start", start).setLong("end", end);
  }


  public static Query getDebtByShopIdAndOrderId(Session session, Long shopId, Long orderId) {
    StringBuffer hql = new StringBuffer("from Debt d where d.shopId =:shopId ");
    if (null != orderId)
      hql.append(" and d.orderId =:orderId");
    Query query = session.createQuery(hql.toString()).setLong("shopId", shopId);
    if (null != orderId)
      query = query.setLong("orderId", orderId);
    return query;
  }

  public static Query getDebtByShopIdAndCustomerIdAndOrderId(Session session, Long shopId, Long customerId, Long orderId) {
    String hql = "from Debt d where d.shopId =:shopId and d.customerId =:customerId and d.orderId=:orderId";
    return session.createQuery(hql).setLong("shopId", shopId).setLong("customerId", customerId).setLong("orderId", orderId);
  }

  public static Query getAllDebtsByCustomerIds(Session session, Long shopId,Long[] customerIds) {
    String hql = "from Debt d where d.shopId =:shopId and d.customerId in (:customerIds) ";
    return session.createQuery(hql).setLong("shopId", shopId).setParameterList("customerIds", customerIds);
  }

  public static Query getAllReceivablesByCustomerIds(Session session, Long shopId,Long[] customerIds) {
    String hql = "from Receivable r where r.shopId =:shopId and r.customerId in (:customerIds) ";
    return session.createQuery(hql).setLong("shopId", shopId).setParameterList("customerIds", customerIds);
  }

  public static Query getAllPayablesBySupplierIds(Session session, Long shopId,Long[] supplierIds) {
    String hql = "from Payable d where d.shopId =:shopId and d.supplierId in (:supplierIds) ";
    return session.createQuery(hql).setLong("shopId", shopId).setParameterList("supplierIds", supplierIds);
  }

  public static Query countNoSettlementRepairOrder(Session session, Long shopId) {
    List<RepairRemindEventTypes> remindEventTypeses = new ArrayList<RepairRemindEventTypes>();
    remindEventTypeses.add(RepairRemindEventTypes.PENDING);
    remindEventTypeses.add(RepairRemindEventTypes.LACK);
    remindEventTypeses.add(RepairRemindEventTypes.INCOMING);
    remindEventTypeses.add(RepairRemindEventTypes.WAIT_OUT_STORAGE);
    remindEventTypeses.add(RepairRemindEventTypes.OUT_STORAGE);
    return session.createQuery("select count(*) from RepairRemindEvent ro where ro.shopId = :shopId and ro.eventTypeEnum in(:events)")
        .setLong("shopId", shopId).setParameterList("events",remindEventTypeses);
  }


  /*
 * 短信充值——开始
 * */
  //根据店面ID获取短信充值的列表
  public static Query getSmsRechargeByShopId(Session session, long shopId) {

    return session.createQuery("select sr from SmsRecharge as sr where sr.shopId = :shopId order by sr.rechargeTime desc")
        .setLong("shopId", shopId);
  }

  //根据店面ID获取短信充值记录数
  public static Query countShopSmsRecharge(Session session, long shopId) {

    return session.createSQLQuery("SELECT COUNT(id) AS amount FROM sms_recharge AS sr WHERE sr.shop_id = :shopId AND sr.state=2")
        .addScalar("amount", StandardBasicTypes.LONG)
        .setLong("shopId", shopId);
  }

  //根据店面ID、页码与每页条数获取短信充值的列表
  public static Query getShopSmsRechargeList(Session session, long shopId, int pageNo, int pageSize) {

    return session.createQuery("select sr from SmsRecharge as sr where sr.shopId = :shopId and sr.state=2 order by sr.rechargeTime desc")
        .setLong("shopId", shopId)
        .setFirstResult((pageNo - 1) * pageSize)
        .setMaxResults(pageSize);
  }

  //根据店面ID获取短信充值的列表
  public static Query getSmsRechargeByRechargeNumber(Session session, String rechargeNumber) {

    return session.createQuery("select sr from SmsRecharge as sr where sr.rechargeNumber = :rechargeNumber")
        .setString("rechargeNumber", rechargeNumber);
  }

  //根据店面ID获取短信充值的列表
  public static Query getSmsBalanceByShopId(Session session, long shopId) {

    return session.createQuery("select sb from SmsBalance as sb where sb.shopId = :shopId")
        .setLong("shopId", shopId);
  }

  //根据充值序号更新payTime
  public static Query updateSmsRechargePayTime(Session session, Long payTime, String rechargeNumber) {
    return session.createQuery("UPDATE SmsRecharge sr SET sr.payTime = :payTime WHERE sr.rechargeNumber = :rechargeNumber").setLong("payTime", payTime).setString("rechargeNumber", rechargeNumber);
  }

  /*
 * 短信充值——结束
 * */

  public static Query countInventoryRemindEventNumber(Session session, Long shopId) {
    return session.createQuery("select count(*) from InventoryRemindEvent  ire where ire.shopId = :shopId").setLong("shopId", shopId);
  }

  public static Query countInventoryNumber(Session session, Long shopId) {
    return session.createQuery("select count(*) from Inventory  ir where ir.shopId = :shopId").setLong("shopId", shopId);
  }

  public static Query getServiceByShopId(Session session, Long shopId) {
    return session.createQuery("from Service s where s.shopId in (1,:shopId) and (s.status is null or s.status <> :status) " +
        "order by shopId desc")
        .setLong("shopId", shopId).setParameter("status", ServiceStatus.DISABLED);
  }

   public static Query getServiceListById(Session session,Long shopId,Long... serviceIds) {
    return session.createQuery("from Service s where s.shopId in (1,:shopId) and s.id in (:serviceIds) and (s.status is null or s.status <> :status)")
        .setLong("shopId", shopId).setParameterList("serviceIds",serviceIds).setParameter("status", ServiceStatus.DISABLED);
  }

  public static Query getServiceByShopIdAndSearchKey(Session session, Long shopId, String searchKey) {
    StringBuffer sb = new StringBuffer();
    sb.append("from Service s where s.shopId in (1,:shopId) and (s.status is null or s.status <> :status) ");
    if (StringUtils.isNotBlank(searchKey)) {
      sb.append(" and s.name like :searchKey ");
    }
    sb.append("order by shopId desc , name asc ");
    Query query = session.createQuery(sb.toString()).setLong("shopId", shopId).setParameter("status", ServiceStatus.DISABLED);
    if (StringUtils.isNotBlank(searchKey)) {
      query.setString("searchKey", "%" + searchKey + "%");
    }
    return query;
  }

  public static Query getServiceByShopIdAndName(Session session, Long shopId, String name) {
    return session.createQuery("from Service s where s.shopId in (1,:shopId) and s.name = :name " +
        "and (s.status is null or s.status <> :status) order by shopId desc ")
        .setLong("shopId", shopId).setString("name", name).setParameter("status", ServiceStatus.DISABLED);
  }

  public static Query getSmsByShopId(Session session, int pageNo, int pageSize) {
    return session.createQuery("from TemBlance").setFirstResult(pageSize * pageNo).setMaxResults(pageSize);
  }

  public static Query coutSms(Session session) {
    return session.createQuery("select count(*) from TemBlance");
  }

  public static Query getLackProductIdsByRepairOderId(Session session, Long repairOrderId, Long shopId, RepairRemindEventTypes eventType) {
    return session.createQuery("from RepairRemindEvent r where r.repairOrderId=:repairOrderId and r.shopId=:shopId and r.eventTypeEnum=:eventType")
        .setLong("repairOrderId", repairOrderId).setLong("shopId", shopId)
        .setParameter("eventType", eventType);
  }

  public static Query countWashAgentAchievements(Session session, long shopId, long startTime, long endTime) {
    return session.createSQLQuery("SELECT wo.* FROM wash_order wo WHERE wo.shop_id =:shopId AND wo.created >= :startTime AND wo.created < :endTime ")
        .addEntity(WashOrder.class).setLong("shopId", shopId).setLong("startTime", startTime).setLong("endTime", endTime);
  }

  public static Query getBusinessStatByYearMonthDay(Session session, Long shopId, Long year, Long month, Long day) {
    return session.createQuery("from BusinessStat r where r.shopId=:shopId and r.statYear=:year and r.statMonth =:month and r.statDay =:day order by r.statTime desc ")
        .setLong("shopId", shopId).setLong("year", year).setLong("month", month).setLong("day", day);

  }

  public static Query getExpendDetailByYearMonthDay(Session session, Long shopId, Long year, Long month, Long day) {
    return session.createQuery("from ExpendDetail r where r.shopId=:shopId and r.year=:year and r.month =:month ").setLong("shopId", shopId).setLong("year", year).setLong("month", month);
  }

  public static Query getExpendDetailByYearMonth(Session session, Long shopId, Long year, Long month, Long day) {
    return session.createQuery("from ExpendDetail r where r.shopId=:shopId and r.year=:year and r.month >= 1  and r.month <=:month ")
        .setLong("shopId", shopId).setLong("year", year).setLong("month", month);
  }

  public static Query getExpendDetailByYearFromStartMonthToEndMonth(Session session, Long shopId, Long year, Long startMonth, Long endMonth) {
    return session.createQuery("from ExpendDetail r where r.shopId=:shopId and r.year=:year and r.month >=:startMonth  and r.month <=:endMonth ")
        .setLong("shopId", shopId).setLong("year", year).setLong("startMonth", startMonth).setLong("endMonth", endMonth);
  }

  public static Query getLatestBusinessStat(Session session, Long shopId, Long year, int size) {
    if (year == null) {
      return session.createQuery("from BusinessStat r where r.shopId=:shopId order by r.statTime desc ").setLong("shopId", shopId).setFirstResult(0).setMaxResults(size);
    }
    return session.createQuery("from BusinessStat r where r.shopId=:shopId and r.statYear=:year order by r.statTime desc ").setLong("shopId", shopId).setLong("year", year).setFirstResult(0).setMaxResults(size);
  }
//  public static Query getLatestBusinessStat(Session session, Long shopId, Long year, int size) {
//    return session.createQuery("from BusinessStat r where r.shopId=:shopId and r.statYear=:year order by r.statTime desc ").setLong("shopId", shopId).setLong("year", year).setFirstResult(0).setMaxResults(size);
//  }

  public static Query getEarliestBusinessStat(Session session, Long shopId, Long year, int size) {
    return session.createQuery("from BusinessStat r where r.shopId=:shopId and r.statYear=:year order by r.statTime ASC ").setLong("shopId", shopId).setLong("year", year).setFirstResult(0).setMaxResults(size);
  }

  public static Query getLatestBusinessStatMonth(Session session, Long shopId, Long year, Long month, int size) {
    return session.createQuery("from BusinessStat r where r.shopId=:shopId and r.statYear=:year and r.statMonth <=:month order by r.statTime desc ")
        .setLong("shopId", shopId).setLong("year", year).setLong("month", month).setFirstResult(0).setMaxResults(size);
  }

  public static Query deleteBusinessStatByYearMonthDay(Session session, Long shopId, Long year, Long month, Long day) {
    return session.createQuery(" delete from BusinessStat r where r.shopId =:shopId and r.statYear =:year and r.statMonth =:month and r.statDay =:day ")
        .setLong("shopId", shopId).setLong("year", year).setLong("month", month).setLong("day", day);
  }


  public static Query getBusinessStatChangeByYear(Session session, Long shopId, Long year) {
    return session.createQuery(" from BusinessStatChange r where r.shopId =:shopId and r.statYear =:year ")
        .setLong("shopId", shopId).setLong("year", year);
  }

  public static Query getBusinessStatMonth(Session session, Long shopId, Long year, String queryString) {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(" from BusinessStat r where r.shopId= ");
    stringBuilder.append(shopId);
    stringBuilder.append(" and r.statYear= ");
    stringBuilder.append(year);
    stringBuilder.append(queryString);
    stringBuilder.append(" order by r.statTime ASC ");

    return session.createQuery(stringBuilder.toString());
  }

  public static Query getBusinessStatMonthEveryDay(Session session, Long shopId, Long year, Long month, Long day) {
    return session.createQuery("from BusinessStat r where r.shopId=:shopId and r.statYear=:year and r.statMonth =:month and r.statDay >= 1 and r.statDay <=:day order by r.statTime asc ")
        .setLong("shopId", shopId).setLong("year", year).setLong("month", month).setLong("day", day);

  }

  public static Query deleteExpendDetailByYearMonth(Session session, Long shopId, Long year, Long month, Long expendDetailId) {

    StringBuffer sb = new StringBuffer();
    sb.append(" delete from ExpendDetail r where r.shopId=:shopId and r.year=:year and r.month =:month ");
    if (expendDetailId != null) {
      sb.append(" and r.id !=:expendDetailId");
    }
    Query query = session.createQuery(sb.toString())
        .setLong("shopId", shopId).setLong("year", year).setLong("month", month);
    if (expendDetailId != null) {
      query.setLong("expendDetailId", expendDetailId);
    }
    return query;
  }

//  public static Query deleteBusinessStatByYearMonthDay(Session session, Long shopId, Long year, Long month, Long day) {
//    return session.createQuery(" delete from BusinessStat r where r.shopId =:shopId and r.statYear =:year and r.statMonth =:month and r.statDay =:day ")
//        .setLong("shopId", shopId).setLong("year", year).setLong("month", month).setLong("day", day);
//  }

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

    return session.createSQLQuery(" SELECT re.*  FROM repair_order_service re WHERE re.repair_order_id IN ( " +
        "SELECT id FROM repair_order ro WHERE ro.shop_id =:shopId AND ro.status = 3  AND ro.created >= :startTime AND ro.created < :endTime ) ").addEntity(RepairOrderService.class)
        .setLong("shopId", shopId)
        .setLong("startTime", startTime)
        .setLong("endTime", endTime);
  }

  /**
   * 根据店面Id,时间段统计施工单中销售费用收入
   *
   * @param session
   * @param shopId
   * @param startTime
   * @param endTime
   * @return
   */
  public static Query countShopRepairOrderSalesIncome(Session session, long shopId, long startTime, long endTime) {

    return session.createSQLQuery(" SELECT re.*  FROM repair_order_item re WHERE re.repair_order_id IN ( " +
        "SELECT id FROM repair_order ro WHERE ro.shop_id =:shopId AND ro.status = 3  AND ro.created >= :startTime AND ro.created < :endTime ) ").addEntity(RepairOrderItem.class)
        .setLong("shopId", shopId)
        .setLong("startTime", startTime)
        .setLong("endTime", endTime);
  }

  /**
   * 根据店面Id,时间段统计销售单中销售费用收入
   *
   * @param session
   * @param shopId
   * @param startTime
   * @param endTime
   * @return
   */
  public static Query countShopSalesIncome(Session session, long shopId, long startTime, long endTime) {
    return session.createSQLQuery("SELECT *  FROM sales_order_item WHERE shop_id = :shopId AND created >= :startTime AND created < :endTime").addEntity(SalesOrderItem.class)
        .setLong("shopId", shopId)
        .setLong("startTime", startTime)
        .setLong("endTime", endTime);
  }

  /**
   * 根据店面Id,时间段统计销售单中销售费用收入
   *
   * @param session
   * @param shopId
   * @param startTime
   * @param endTime
   * @return
   */
  public static Query countShopSalesIncomeByShopId(Session session, long shopId, long startTime, long endTime) {
    return session.createSQLQuery("SELECT i.*  FROM sales_order_item i WHERE i.sales_order_id IN ( " +
        " SELECT id FROM sales_order so WHERE so.shop_id = :shopId AND so.created >= :startTime AND so.created < :endTime ) ").addEntity(SalesOrderItem.class)
        .setLong("shopId", shopId)
        .setLong("startTime", startTime)
        .setLong("endTime", endTime);
  }

  /**
   * 根据店面Id,时间段统计销售单中销售费用收入
   *
   * @param session
   * @param shopId
   * @param startTime
   * @param endTime
   * @return
   */
  public static Query countWashOrderList(Session session, long shopId, long startTime, long endTime) {
    return session.createSQLQuery("SELECT * FROM wash_order WHERE shop_id = :shopId AND created >= :startTime AND created < :endTime").addEntity(WashOrder.class)
        .setLong("shopId", shopId)
        .setLong("startTime", startTime)
        .setLong("endTime", endTime);
  }

  public static Query getSalesOrderDTOList(Session session, long shopId, long startTime, long endTime, int pageNo, int pageSize, String arrayType) {
    StringBuffer sb = new StringBuffer();
    sb.append(" select * from (select id,receipt_no,other_income_total,other_total_cost_price,created,shop_id,customer,total,total_cost_price,vest_date,'S' " +
        "from sales_order where shop_id =:shopId and vest_date >=:startTime and vest_date <:endTime and ( status_enum =:status or status_enum =:debtStatus ) " +
        " union all " +
        " select id,receipt_no,0,0,created,shop_id,customer,total,total_cost_price,vest_date,'R' " +
        " from sales_return where shop_id =:salesReturnShopId and vest_date >=:returnStartTime and vest_date <:returnEndTime and status =:salesReturnStatus" +
        " )b ");
    if (StringUtil.isEmpty(arrayType)) {
      arrayType = " order by created desc ";
    }
    sb.append(arrayType);

    Query query = session.createSQLQuery(sb.toString());
    query.setLong("shopId", shopId)
        .setLong("startTime", startTime)
        .setLong("endTime", endTime)
        .setLong("salesReturnShopId", shopId)
        .setLong("returnStartTime", startTime)
        .setLong("returnEndTime", endTime)
        .setParameter("status", OrderStatus.SALE_DONE.toString())
        .setParameter("debtStatus", OrderStatus.SALE_DEBT_DONE.toString())
        .setParameter("salesReturnStatus", OrderStatus.SETTLED.toString())
        .setFirstResult((pageNo - 1) * pageSize)
        .setMaxResults(pageSize);
    return query;

  }

  /**
   *
   * @param session
   * @param shopId
   * @param startTime
   * @param endTime
   * @return
   */
  public static Query getSalesOrderDTOListByVestDate(Session session, long shopId, long startTime, long endTime) {
    StringBuffer sb = new StringBuffer();
    sb.append("  from SalesOrder where shopId = :shopId and vestDate >= :startTime and vestDate < :endTime and ( statusEnum =:status or statusEnum =:debtStatus ) ");
    return session.createQuery(sb.toString())
        .setLong("shopId", shopId)
        .setLong("startTime", startTime)
        .setLong("endTime", endTime)
        .setParameter("status",OrderStatus.SALE_DONE)
        .setParameter("debtStatus",OrderStatus.SALE_DEBT_DONE);
  }

  public static Query getHundredCostPriceNUllSalesOrderDTOList(Session session) {
    return session.createQuery("select so from SalesOrder so where totalCostPrice is null ")
        .setMaxResults(100);
  }

  @Deprecated
  public static Query countSalesOrder(Session session, long shopId, long startTime, long endTime) {
    return session.createSQLQuery("SELECT COUNT(*) FROM sales_order WHERE shop_id = :shopId AND vest_date >= :startTime AND vest_date < :endTime")
        .setLong("shopId", shopId)
        .setLong("startTime", startTime)
        .setLong("endTime", endTime);
  }

  public static Query countSalesOrder(Session session, Long shopId) {
    StringBuffer sb = new StringBuffer();
    sb.append("select count(*) from sales_order where total_cost_price is null");
    if (null != shopId) {
      sb.append(" and shop_id =: shopId");
    }
    Query q = session.createSQLQuery(sb.toString());

    if (null != shopId) {
      q.setLong("shop_id", shopId.longValue());
    }
    return q;
  }

  @Deprecated
  public static Query countRepairOrder(Session session, Long shopId) {
    StringBuffer sb = new StringBuffer();
    sb.append("select count(*) from repair_order where total_cost_price is null and status_enum = 'REPAIR_SETTLED'");
    if (null != shopId) {
      sb.append(" and shop_id =: shopId");
    }
    Query q = session.createSQLQuery(sb.toString());

    if (null != shopId) {
      q.setLong("shop_id", shopId.longValue());
    }
    return q;
  }

  public static Query getRepairOrders(Session session,RepairOrderDTO repairOrderIndex) {
    StringBuffer sb=new StringBuffer();
    sb.append("from RepairOrder where shopId=:shopId");
    if(repairOrderIndex.getCustomerId()!=null){
      sb.append(" and customerId =:customerId");
    }
    Query query= session.createQuery(sb.toString()).setLong("shopId",repairOrderIndex.getShopId());
    if(repairOrderIndex.getCustomerId()!=null){
      query.setLong("customerId",repairOrderIndex.getCustomerId());
    }
    return query;
  }

  public static Query getRepairRemindEventByRepairOrderId(Session session,Long shopId,Long repairOrderId) {
    String hql="from RepairRemindEvent where shopId=:shopId and repairOrderId=:repairOrderId";
    return session.createQuery(hql).setLong("shopId",shopId).setLong("repairOrderId",repairOrderId);
  }


  public static Query getCustomerOrSupplierOrders(Session session, Long shopId,OrderTypes orderType,Long[] customerOrSupplierIds) {
    String sql="";
    switch (orderType) {
      case REPAIR:
        sql="from RepairOrder where shopId=:shopId and customerId in (:customerOrSupplierIds) ";
        break;
      case INVENTORY:
        sql="from PurchaseInventory where shopId=:shopId and supplierId in (:customerOrSupplierIds) ";
        break;
      case PURCHASE:
        sql="from PurchaseOrder where shopId=:shopId and supplierId in (:customerOrSupplierIds) ";
        break;
      case RETURN:
        sql="from PurchaseReturn where shopId=:shopId and supplierId in (:customerOrSupplierIds) ";
        break;
      case SALE:
        sql="from SalesOrder where shopId=:shopId and customerId in (:customerOrSupplierIds) ";
        break;
      case WASH_BEAUTY:
        sql="from WashBeautyOrder where shopId=:shopId and customerId in (:customerOrSupplierIds) ";
        break;
      case MEMBER_RETURN_CARD:
        sql="from MemberCardReturn where shopId=:shopId and customerId in (:customerOrSupplierIds) ";
        break;
      case MEMBER_BUY_CARD:
        sql="from MemberCardOrder where shopId=:shopId and customerId in (:customerOrSupplierIds) ";
        break;
      case SALE_RETURN:
        sql="from SalesReturn where shopId=:shopId and customerId in (:customerOrSupplierIds) ";
        break;
      case CUSTOMER_STATEMENT_ACCOUNT:
        sql="from StatementAccountOrder where shopId=:shopId and customerOrSupplierId in (:customerOrSupplierIds) ";
        break;
      case SUPPLIER_STATEMENT_ACCOUNT:
        sql="from StatementAccountOrder where shopId=:shopId and customerOrSupplierId in (:customerOrSupplierIds) ";
        break;
      case BORROW_ORDER:
        sql="from BorrowOrder where shopId=:shopId and borrowerId in (:customerOrSupplierIds) ";
        break;
      case APPOINT_ORDER:
        sql = "from AppointOrder where shopId =:shopId and customerId in (:customerOrSupplierIds)";
        break;
      default:
        return null;
    }
    return session.createQuery(sql).setLong("shopId",shopId).setParameterList("customerOrSupplierIds",customerOrSupplierIds);
  }

  public static Query getRepairOrderDTOList(Session session, long shopId, long startTime, long endTime, int pageNo, int pageSize, String arrayType,OrderStatus orderStatus) {
    StringBuffer sb = new StringBuffer();
    sb.append(" select id,creationDate,shopId,receiptNo,otherIncomeTotal,otherTotalCostPrice,total,totalCostPrice,vechicleId,vestDate from RepairOrder where shopId = :shopId and vestDate >= :startTime and vestDate < :endTime and statusEnum =:orderStatus");
    if (StringUtil.isEmpty(arrayType)) {
      arrayType = " order by creationDate desc ";
    }
    sb.append(arrayType);
    return session.createQuery(sb.toString())
        .setLong("shopId", shopId)
        .setLong("startTime", startTime)
        .setLong("endTime", endTime)
        .setParameter("orderStatus", OrderStatus.REPAIR_SETTLED)
        .setFirstResult((pageNo - 1) * pageSize)
        .setMaxResults(pageSize);

  }

  public static Query getHundredCostPriceNUllRepairOrderDTOList(Session session) {
    return session.createSQLQuery("SELECT * FROM repair_order WHERE total_cost_price IS NULL AND status = 3").addEntity(RepairOrder.class)
        .setMaxResults(100);
  }

  public static Query getPurchaseOrderById(Session session, Long id, Long shopId) {
    return session.createQuery(" select po from PurchaseOrder po where po.id =:id and po.shopId =:shopId")
        .setLong("id", id).setLong("shopId", shopId);
  }

  public static Query getPurchaseOrderBySupplierShopIdAndIds(Session session, Long supplierShopId, Long... ids) {
    return session.createQuery(" select po from PurchaseOrder po where po.id in(:ids) and po.supplierShopId =:supplierShopId")
        .setParameterList("ids", ids).setLong("supplierShopId", supplierShopId);
  }

  public static Query getpurchaseInventoryById(Session session, Long purchaseInventoryId, Long shopId) {
    return session.createQuery(" select pi from PurchaseInventory pi where pi.id =:id and pi.shopId =:shopId")
        .setLong("id", purchaseInventoryId).setLong("shopId", shopId);
  }

  public static Query getPurchaseInventoryByShopIdAndSupplierId(Session session, Long shopId, Long supplierId) {
    return session.createQuery(" select pi from PurchaseInventory pi where pi.supplierId =:supplierId and pi.shopId =:shopId")
        .setLong("supplierId", supplierId).setLong("shopId", shopId);
  }

  public static Query getPurchaseOrderByShopIdAndSupplierId(Session session, Long shopId, Long supplierId) {
    return session.createQuery(" select po from PurchaseOrder po where po.supplierId =:supplierId and po.shopId =:shopId")
        .setLong("supplierId", supplierId).setLong("shopId", shopId);
  }

  public static Query getSalesOrderById(Session session, Long salesOrderId, Long shopId) {
    return session.createQuery(" select so from SalesOrder so where so.id =:id and so.shopId =:shopId")
        .setLong("id", salesOrderId).setLong("shopId", shopId);
  }

  public static Query getSalesOrderByPurchaseOrderId(Session session, Long purchaseOrderId, Long supplierShopId) {
    return session.createQuery(" select so from SalesOrder so where so.purchaseOrderId =:purchaseOrderId and so.shopId =:supplierShopId")
        .setLong("purchaseOrderId", purchaseOrderId).setLong("supplierShopId", supplierShopId);
  }

  public static Query getSalesOrderByPurchaseOrderId(Session session, Long purchaseOrderId) {
    return session.createQuery(" select so from SalesOrder so where so.purchaseOrderId =:purchaseOrderId ")
        .setLong("purchaseOrderId", purchaseOrderId);
  }

  public static Query getRepairRemindEventByProductIdAndEnentType(Session session, Long shopId, RepairRemindEventTypes eventType, Long productId) {
    StringBuffer hql = new StringBuffer("select r from RepairRemindEvent r where r.shopId =:shopId and r.productId =:productId");
    if (null != eventType) {
      hql.append(" and r.eventTypeEnum =:eventType");
    }
    Query query = session.createQuery(hql.toString()).setLong("shopId", shopId).setLong("productId", productId);
    if (null != eventType) {
      query = query.setParameter("eventType", eventType);
    }
    return query;
  }

  public static Query getRepairRemindEventByProductIdsAndEnentType(Session session, Long shopId, RepairRemindEventTypes eventType, Set<Long> productIds) {
    StringBuffer hql = new StringBuffer("select r from RepairRemindEvent r where r.shopId =:shopId and r.productId in(:productIds)");
    if (null != eventType) {
      hql.append(" and r.eventTypeEnum =:eventType");
    }
    Query query = session.createQuery(hql.toString()).setLong("shopId", shopId).setParameterList("productIds", productIds);
    if (null != eventType) {
      query = query.setParameter("eventType", eventType);
    }
    return query;
  }

  public static Query getRepairRemindEventByProductIdAndStorehouse(Session session, Long shopId, RepairRemindEventTypes eventType, Long productId,Long storehouseId) {
    StringBuffer hql = new StringBuffer("select r from RepairRemindEvent r,RepairOrder ro where r.repairOrderId = ro.id and ro.storehouseId=:storehouseId and r.shopId =:shopId and r.productId =:productId");
    if (null != eventType) {
      hql.append(" and r.eventTypeEnum =:eventType");
    }
    Query query = session.createQuery(hql.toString()).setLong("shopId", shopId).setLong("storehouseId",storehouseId).setLong("productId", productId);
    if (null != eventType) {
      query = query.setParameter("eventType", eventType);
    }
    return query;
  }

  public static Query getRepairRemindEventByProductIdsAndStorehouse(Session session, Long shopId, RepairRemindEventTypes eventType, Set<Long> productIds,Long storehouseId) {
    StringBuffer hql = new StringBuffer("select r from RepairRemindEvent r,RepairOrder ro where r.repairOrderId = ro.id and ro.storehouseId=:storehouseId and r.shopId =:shopId and r.productId in(:productIds)");
    if (null != eventType) {
      hql.append(" and r.eventTypeEnum =:eventType");
    }
    Query query = session.createQuery(hql.toString()).setLong("shopId", shopId).setLong("storehouseId",storehouseId).setParameterList("productIds", productIds);
    if (null != eventType) {
      query = query.setParameter("eventType", eventType);
    }
    return query;
  }

  public static Query getInventoryRemindEventByProductId(Session session, Long shopId, Long productId) {
    return session.createQuery("select i from InventoryRemindEvent i where i.shopId =:shopId and i.content =:productId")
        .setLong("shopId", shopId).setString("productId", productId.toString());
  }

  public static Query getInventoryRemindEventByPurchaseOrderId(Session session, Long shopId, Long purchaseOrderId) {
    return session.createQuery("select i from InventoryRemindEvent i where i.shopId =:shopId and i.purchaseOrderId =:purchaseOrderId")
        .setLong("shopId", shopId).setLong("purchaseOrderId", purchaseOrderId);
  }

  public static Query getPurchaseOrderItemByOrderIdAndProductId(Session session, Long purchaseOrderId, Long productId) {
    return session.createQuery("select p from PurchaseOrderItem p where p.purchaseOrderId =:purchaseOrderId and p.productId =:productId")
        .setLong("purchaseOrderId", purchaseOrderId).setLong("productId", productId);
  }


  public static Query getRepairOrderDTOListByVestDate(Session session, long shopId, long startTime, long endTime,OrderStatus orderStatus) {

    StringBuffer sb = new StringBuffer();
    sb.append(" select count(*),sum(total),sum(afterMemberDiscountTotal) from RepairOrder where shopId = :shopId and vestDate >= :startTime and vestDate < :endTime and statusEnum = :orderStatus ");
    return session.createQuery(sb.toString())
        .setLong("shopId", shopId)
        .setLong("startTime", startTime)
        .setLong("endTime", endTime)
        .setParameter("orderStatus",OrderStatus.REPAIR_SETTLED);
  }

  //充值历史记录
  public static SQLQuery getShopSmsRechargeList(Session session, Long startTime, Long endTime, String money, Long shopId, String other, int pageNo, int pageSize) {
    StringBuffer hql = new StringBuffer("select s.id as id, s.shop_id as shopId ,s.recharge_time as rechargeTime,s.recharge_number as rechargeNumber,s.sms_balance as smsBalance,s.recharge_amount as rechargeAmount,s.state as state," +
        "s.pay_time as payTime,u.user_name as userName  from sms_recharge as s,bcuser.user as u where s.user_id=u.id ");
    if (other != null && !"".equals(other)) {
      hql.append("and u.user_name  like:userName ");
    }
    if (money != null && !"".equals(money)) {
      hql.append("and s.recharge_amount=:money ");
    }
    if (shopId != null) {
      hql.append("and s.shop_id=:shopId ");
    }
    if (startTime != null) {
      hql.append(" and s.recharge_time >=:startTime  ");
    }
    if (endTime != null) {
      hql.append(" and s.recharge_time<=:endTime ");
    }
    SQLQuery sqlQuery = (SQLQuery) session.createSQLQuery(hql.toString()).setLong("shopId", shopId);
    if (other != null && !"".equals(other)) {
      sqlQuery.setString("userName", "%" + other + "%");
    }
    if (money != null && !"".equals(money)) {
      sqlQuery.setDouble("money", Double.parseDouble(money));
    }
    if (startTime != null) {
      sqlQuery.setLong("startTime", startTime);
    }
    if (endTime != null) {
      sqlQuery.setLong("endTime", endTime);
    }
    return (SQLQuery) sqlQuery.setFirstResult((pageNo - 1) * pageSize).setMaxResults(pageSize);
  }

  //充值历史记录个数
  public static Query countShopSmsRecharge(Session session, Long startTime, Long endTime, String money, String other, Long shopId) {

    StringBuffer hql = new StringBuffer("select s.id as id, s.shop_id as shopId ,s.recharge_time as rechargeTime,s.recharge_number as rechargeNumber,s.sms_balance as smsBalance,s.recharge_amount as rechargeAmount,s.state as state," +
        "s.pay_time as payTime,u.user_name as userName  from sms_recharge as s,bcuser.user as u where s.user_id=u.id ");
    if (other != null && !"".equals(other)) {
      hql.append("and u.user_name  like:userName ");
    }
    if (money != null && !"".equals(money)) {
      hql.append("and s.recharge_amount=:money ");
    }
    if (shopId != null) {
      hql.append("and s.shop_id=:shopId ");
    }
    if (startTime != null) {
      hql.append(" and s.recharge_time >=:startTime  ");
    }
    if (endTime != null) {
      hql.append(" and s.recharge_time<=:endTime ");
    }
    SQLQuery sqlQuery = (SQLQuery) session.createSQLQuery(hql.toString()).setLong("shopId", shopId);
    if (other != null && !"".equals(other)) {
      sqlQuery.setString("userName", "%" + other + "%");
    }
    if (money != null && !"".equals(money)) {
      sqlQuery.setDouble("money", Double.parseDouble(money));
    }
    if (startTime != null) {
      sqlQuery.setLong("startTime", startTime);
    }
    if (endTime != null) {
      sqlQuery.setLong("endTime", endTime);
    }
    return sqlQuery;
  }

  public static Query countSalesAgentAchievements(Session session, long shopId, long startTime, long endTime) {
    return session.createSQLQuery("SELECT wo.* FROM sales_order wo WHERE wo.shop_id =:shopId AND wo.status_enum =:status AND wo.vest_date >= :startTime AND wo.vest_date < :endTime ")
        .addEntity(SalesOrder.class).setLong("shopId", shopId)
        .setParameter("status", OrderStatus.SALE_DONE.toString()).setLong("startTime", startTime).setLong("endTime", endTime);
  }

  public static Query countSalesReturnAchievements(Session session, long shopId, long startTime, long endTime) {
    return session.createSQLQuery("SELECT wo.* FROM sales_return wo WHERE wo.shop_id =:shopId AND wo.status =:status AND wo.vest_date >= :startTime AND wo.vest_date < :endTime ")
        .addEntity(SalesReturn.class).setLong("shopId", shopId)
        .setParameter("status", OrderStatus.SETTLED.toString()).setLong("startTime", startTime).setLong("endTime", endTime);
  }

  public static Query getWashOrderListByAssistantName(Session session, String assistantName, long startTime, long endTime) {
//    return session.createQuery("select bs from BizStat as bs where bs.shopId = :shopId and bs.statYear = :statYear " +
//        "and bs.statWeek is null and bs.statDay is null and bs.statMonth = :statMonth and bs.assistant is not null")
//        .setLong("shopId", shopId)
//        .setLong("statYear", statYear)
//        .setLong("statMonth", statMonth);

    // return session.createQuery("select wo from WashOrder as wo where wo.washWorker =:washWorker ").setString("washWorker",assistantName);
    return session.createSQLQuery("SELECT wo.* FROM wash_order wo WHERE wo.wash_worker=:washWorker AND wo.created >= :startTime AND wo.created < :endTime ")
        .addEntity(WashOrder.class).setString("washWorker", assistantName).setLong("startTime", startTime).setLong("endTime", endTime);
  }

  public static Query getRepairOrderListByAssistantName(Session session, String assistantName, long startTime, long endTime) {
//    return session.createQuery("select bs from BizStat as bs where bs.shopId = :shopId and bs.statYear = :statYear " +
//        "and bs.statWeek is null and bs.statDay is null and bs.statMonth = :statMonth and bs.assistant is not null")
//        .setLong("shopId", shopId)
//        .setLong("statYear", statYear)
//        .setLong("statMonth", statMonth);

    // return session.createQuery("select wo from WashOrder as wo where wo.washWorker =:washWorker ").setString("washWorker",assistantName);
    return session.createSQLQuery(" SELECT wo.* FROM repair_order wo WHERE (wo.service_worker =:washWorker OR wo.product_saler =:productSaler) AND wo.created >= :startTime AND wo.created < :endTime ")
        .addEntity(RepairOrder.class).setString("washWorker", assistantName).setString("productSaler", assistantName).setLong("startTime", startTime).setLong("endTime", endTime);
  }

  public static Query getSalesOrderListByAssistantName(Session session, String assistantName, long startTime, long endTime) {
//    return session.createQuery("select bs from BizStat as bs where bs.shopId = :shopId and bs.statYear = :statYear " +
//        "and bs.statWeek is null and bs.statDay is null and bs.statMonth = :statMonth and bs.assistant is not null")
//        .setLong("shopId", shopId)
//        .setLong("statYear", statYear)
//        .setLong("statMonth", statMonth);

    // return session.createQuery("select wo from WashOrder as wo where wo.washWorker =:washWorker ").setString("washWorker",assistantName);
    return session.createSQLQuery("SELECT wo.* FROM sales_order wo WHERE wo.goods_saler =:goodsSaler AND wo.created >= :startTime AND wo.created < :endTime ")
        .addEntity(SalesOrder.class).setString("goodsSaler", assistantName).setLong("startTime", startTime).setLong("endTime", endTime);
  }


  public static Query countAgentAchievements(Session session, long shopId, long startTime, long endTime) {
    return session.createSQLQuery("SELECT * FROM repair_order re WHERE re.shop_id = :shopId AND re.status_enum =:status AND re.vest_date >= :startTime AND re.vest_date < :endTime ")
        .addEntity(RepairOrder.class)
        .setLong("shopId", shopId)
        .setParameter("status", OrderStatus.REPAIR_SETTLED.toString())
        .setLong("startTime", startTime).setLong("endTime", endTime);
  }

  public static Query countServiceAgentAchievements(Session session, long repair_order_id, long startTime, long endTime) {
    return session.createSQLQuery("SELECT SUM(total) FROM repair_order_service re WHERE re.repair_order_id = :repair_order_id ")
        .setLong("repair_order_id", repair_order_id);
  }

  public static Query countItemAgentAchievements(Session session, long repair_order_id, long startTime, long endTime) {
    return session.createSQLQuery("SELECT SUM(total) FROM repair_order_item re WHERE re.repair_order_id = :repair_order_id ")
        .setLong("repair_order_id", repair_order_id);
  }

  public static Query getpurchaseInventoryByShopIdAndPurchasseInventoryId(Session session, Long shopId, Long purchaseInventoryId) {
    return session.createQuery("select pi from PurchaseInventory as pi where pi.shopId =:shopId and pi.id =:purchaseInventoryId ")
        .setLong("shopId", shopId).setLong("purchaseInventoryId", purchaseInventoryId);
  }

  public static Query updatePurchaseInventoryStatus(Session session, Long purchaseInventoryDTOId, OrderStatus purchaseInventoryDTOStatus) {
    return session.createQuery("update PurchaseInventory pi set " +
        "pi.statusEnum = :purchaseInventoryDTOStatusEnum where pi.id = :purchaseInventoryDTOId")
        .setParameter("purchaseInventoryDTOStatusEnum", purchaseInventoryDTOStatus)
        .setLong("purchaseInventoryDTOId", purchaseInventoryDTOId);
  }

  public static Query updateInventoryAmount(Session session, Long shopId, Long productLocalInfoId, Double amount) {
    return session.createQuery("update Inventory i set i.amount = :amount where i.shopId = :shopId and i.id = :productLocalInfoId")
        .setDouble("amount", amount).setLong("shopId", shopId).setLong("productLocalInfoId", productLocalInfoId);
  }

  public static Query updatePurchaseOrderStatus(Session session, Long shopId, Long id, OrderStatus statusEnum) {
    return session.createQuery("update PurchaseOrder po set po.statusEnum = :statusEnum " +
        "where po.shopId =:shopId and po.id =:id")
        .setLong("shopId", shopId).setParameter("statusEnum", statusEnum).setLong("id", id);
  }

  public static Query getRepairOrderItemsByRepairOrderIdAndProductId(Session session, Long repairOrderId, Long productId) {
    return session.createQuery("from RepairOrderItem roi where roi.repairOrderId =:repairOrderId and roi.productId =:productId")
        .setLong("repairOrderId", repairOrderId).setLong("productId", productId);
  }

  public static Query getRepairOrderItemsByRepairOrderIdAndProductIdAndStoreHouse(Session session, Long repairOrderId, Long productId,Long storehouseId) {
    return session.createQuery("select roi from RepairOrderItem roi,RepairOrder ro where ro.id=roi.repairOrderId and ro.storehouseId=:storehouseId and roi.repairOrderId =:repairOrderId and roi.productId =:productId")
        .setLong("repairOrderId", repairOrderId).setLong("productId", productId).setLong("storehouseId",storehouseId);
  }

  public static Query updateSaleOrderStatus(Session session, Long shopId, Long saleOrderId, OrderStatus saleOrderStatus) {
    return session.createQuery("update SalesOrder s set s.statusEnum =:statusEnum where s.shopId =:shopId and s.id =:id")
        .setLong("shopId", shopId)
        .setParameter("statusEnum", saleOrderStatus)
        .setLong("id", saleOrderId);
  }

  /**
   * 根据店面ID,维修单ID,商品ID删除记录
   *
   * @param shopId
   * @param productId
   * @param repairOrderId
   * @return
   */
  public static Query deleteRepairRemindEventByShopIdAndAndRepairOrderIdAndProductId(Session session, Long shopId, Long repairOrderId, Long productId) {
    return session.createQuery("delete from RepairRemindEvent r where r.shopId =:shopId and r.repairOrderId =:repairOrderId and r.productId =:productId")
        .setLong("shopId", shopId)
        .setLong("repairOrderId", repairOrderId)
        .setLong("productId", productId);
  }

  public static Query getInventoryByIdAndShopId(Session session, Long productId, Long shopId) {
    StringBuffer sb = new StringBuffer();
    sb.append("select i from Inventory i where i.shopId =:shopId and i.id =:productId");
    return session.createQuery(sb.toString()).setLong("shopId", shopId).setLong("productId", productId);
  }

  public static Query getInventoryByshopIdAndProductIds(Session session, Long shopId, Long... productId) {
    StringBuffer sb = new StringBuffer("select i from Inventory i where i.shopId =:shopId and i.id in (:productId)");
    return session.createQuery(sb.toString()).setLong("shopId", shopId).setParameterList("productId", productId);
  }

  public static Query getInventoryByProductIds(Session session, Long... productId) {
    StringBuffer sb = new StringBuffer("from Inventory i where i.id in (:productId)");
    return session.createQuery(sb.toString()).setParameterList("productId", productId);
  }




  public static Query getSmsRechargesByState(Session session, RechargeSearchDTO rechargeSearchDTO) {
    return session.createSQLQuery("SELECT * FROM sms_recharge s WHERE s.state=:state AND s.created>:timePeriod  ORDER BY s.id ASC ")
        .addEntity(SmsRecharge.class).setLong("state", rechargeSearchDTO.getSmsRechargeState()).setLong("timePeriod", rechargeSearchDTO.getTimePeriod())
        .setFirstResult(0).setMaxResults(rechargeSearchDTO.getPager().getPageSize());
  }

  public static Query getSmsRechargesByIds(Session session, Long... ids) {
    return session.createQuery("select s from SmsRecharge s where s.id in(:ids)").setParameterList("ids", ids);
  }

  public static Query getSmsRechargesByStatus(Session session, Long shopId, int start, int pageSize, Long loanTransferTime) {
    StringBuffer s = new StringBuffer();
    s.append("select s.id from SmsRecharge s where s.state=:state and s.creationDate>:timePeriod");
    if (shopId != null) {
      s.append(" and s.shopId=:shopId");
    }
    s.append("  order by s.id desc ");
    Query q = session.createQuery(s.toString());
    q.setLong("state", SmsRechargeConstants.RechargeState.RECHARGE_STATE_COMMIT).setLong("timePeriod", loanTransferTime).setFirstResult(start).setMaxResults(pageSize);
    if (shopId != null) {
      q.setLong("shopId", shopId);
    }
    return q;
  }

  public static Query countSmsRechargesByConditions(Session session, RechargeSearchDTO rechargeSearchDTO) {
    return session.createSQLQuery("SELECT COUNT(*) FROM sms_recharge s  WHERE s.state=:state AND s.created>:timePeriod")
        .setLong("state", rechargeSearchDTO.getSmsRechargeState()).setLong("timePeriod", rechargeSearchDTO.getTimePeriod());
  }

  public static Query getShopPrintTemplateDTOByShopIdAndType(Session session, Long shopId, OrderTypes type) {
    return session.createQuery("select spt from ShopPrintTemplate spt where spt.shopId in (:shopId,-1) and spt.orderTypeEnum =:type order by spt.shopId desc ")
        .setLong("shopId", shopId).setParameter("type", type);
  }

  public static Query countPrintTemplateDTOByName(Session session, Long shopId, String name) {
    return session.createQuery("select count(id) from PrintTemplate pt where pt.name =:name")
        .setString("name", name);
  }

  public static Query getPrintTemplateDTOByType(Session session, OrderTypes type) {
    return session.createQuery("select pt from PrintTemplate pt where pt.orderTypeEnum =:type ")
        .setParameter("type", type);
  }

  public static Query getSalesOrderCountAndSum(Session session, long shopId, long startTime, long endTime) {
    StringBuffer sb = new StringBuffer();
    sb.append(" select * from ( ( select count(*),sum(total),sum(after_member_discount_total) from sales_order where shop_id = :shopId and vest_date >= :startTime and vest_date < :endTime and ( status_enum =:status or status_enum =:debtStatus ) )");
    sb.append(" union all ");
    //TODO: 销售退货单暂无会员折扣概念
    sb.append(" ( select count(*),sum(total),sum(total) from sales_return where shop_id = :salesReturnShopId and vest_date >= :returnStartTime and vest_date < :returnEndTime and status =:salesReturnStatus ))a ");
    return session.createSQLQuery(sb.toString())
        .setLong("shopId", shopId)
        .setLong("startTime", startTime)
        .setLong("endTime", endTime)
        .setLong("salesReturnShopId", shopId)
        .setLong("returnStartTime", startTime)
        .setLong("returnEndTime", endTime)
        .setParameter("status", OrderStatus.SALE_DONE.toString())
        .setParameter("debtStatus", OrderStatus.SALE_DEBT_DONE.toString())
        .setParameter("salesReturnStatus", OrderStatus.SETTLED.toString());
  }



  public static Query getSmsBalance(Session session, Pager pager) {
    return session.createSQLQuery("SELECT * FROM sms_balance s ORDER BY s.id ASC limit :rowStart, :pageSize ").addEntity(SmsBalance.class)
        .setInteger("rowStart", pager.getRowStart()).setInteger("pageSize", pager.getPageSize());
  }

  public static Query countSmsBalance(Session session) {
    return session.createQuery("select count(*) from SmsBalance s");
  }

  public static Query getSalesOrderDTOListByCustomerId(Session session, long shopId, long customerId) {
    StringBuffer sb = new StringBuffer();
    sb.append("  from SalesOrder where shopId = :shopId and customerId =:customerId and and ( statusEnum =:status or statusEnum =:debtStatus ) ");
    return session.createQuery(sb.toString())
        .setLong("shopId", shopId)
        .setLong("customerId", customerId)
        .setParameter("status", OrderStatus.SALE_DONE)
        .setParameter("debtStatus", OrderStatus.SALE_DEBT_DONE);
  }


  public static Query getRepairOrderDTOListByCustomerId(Session session, long shopId, long customerId) {
    StringBuffer sb = new StringBuffer();
    sb.append(" select * from repair_order where shop_id = :shopId and status_enum =:status and customer_id =:customerId ");
    return session.createSQLQuery(sb.toString()).addEntity(RepairOrder.class)
        .setLong("shopId", shopId)
        .setParameter("status", OrderStatus.REPAIR_SETTLED)
        .setLong("customerId", customerId);
  }

  public static Query countReceivableDTOByShopId(Session session, Long shopId) {
    if (shopId != null) {
      return session.createQuery("select count(*) from Receivable as c where c.shopId = :shopId")
          .setLong("shopId", shopId);
    } else {
      return session.createQuery("select count(*) from Receivable ");
    }

  }

  public static Query getReceivableDTOList(Session session, Long shopId, int pageNo, int pageSize) {
    if (shopId != null) {
      return session.createQuery("from Receivable r where  r.shopId=:shopId ")
          .setLong("shopId", shopId).setFirstResult(pageNo * pageSize).setMaxResults(pageSize);
    } else {
      return session.createQuery("from Receivable").setFirstResult(pageNo * pageSize).setMaxResults(pageSize);
    }
  }

  public static Query getAllReceivables(Session session, Long shopId, Long customerId) {
    String hql="from Receivable r where r.shopId =:shopId and customerId =:customerId ";
    return session.createQuery(hql).setLong("shopId", shopId).setLong("customerId",customerId);

  }

  public static Query getPurchaseOrderDTOListByShopId(Session session, long shopId, long startTime, long endTime) {

    StringBuffer sb = new StringBuffer();
    sb.append(" select * from purchase_order where shop_id = :shopId and created >= :startTime and created < :endTime ");
    return session.createSQLQuery(sb.toString()).addEntity(PurchaseOrder.class)
        .setLong("shopId", shopId)
        .setLong("startTime", startTime)
        .setLong("endTime", endTime);
  }

  public static Query getPurchaseInventoryByShopId(Session session, long shopId, OrderStatus status) {
    StringBuffer sb = new StringBuffer();
    sb.append("from PurchaseInventory r where  r.shopId=:shopId ");
    if (status != null) {
      sb.append(" and r.statusEnum =:status");
    }
    Query query = session.createQuery(sb.toString()).setLong("shopId", shopId);
    if (status != null) {
      query.setParameter("status", status);
    }
    return query;
  }


  public static Query getRepairOrderDTOListByCreated(Session session, long shopId, long startTime, long endTime) {
    StringBuffer sb = new StringBuffer();
    sb.append(" select * from repair_order where shop_id = :shopId and created >= :startTime and created < :endTime ");
    return session.createSQLQuery(sb.toString()).addEntity(RepairOrder.class)
        .setLong("shopId", shopId)
        .setLong("startTime", startTime)
        .setLong("endTime", endTime);
  }

  public static Query countPurchaseInventoryByShopId(Session session, long shopId, long startTime, long endTime) {
    StringBuffer sb = new StringBuffer();
    sb.append(" select count(*) from purchase_inventory where shop_id = :shopId and created >= :startTime and created < :endTime ");
    return session.createSQLQuery(sb.toString())
        .setLong("shopId", shopId)
        .setLong("startTime", startTime)
        .setLong("endTime", endTime);
  }

  public static Query getPurchaseInventoryDTOList(Session session, long shopId, int pageNo, int pageSize, long startTime, long endTime) {
    StringBuffer sb = new StringBuffer();
    sb.append(" select * from purchase_inventory where shop_id = :shopId and created >= :startTime and created < :endTime ");
    return session.createSQLQuery(sb.toString()).addEntity(PurchaseInventory.class)
        .setLong("shopId", shopId)
        .setLong("startTime", startTime)
        .setLong("endTime", endTime)
        .setFirstResult(pageNo * pageSize).setMaxResults(pageSize);
  }

  public static Query getRepairOrderItemsByProductId(Session session, long shopId, long productId) {
    return session.createQuery("from RepairOrderItem roi where roi.shopId =:shopId and roi.productId =:productId")
        .setLong("shopId", shopId).setLong("productId", productId);
  }

  public static Query getSalesOrderItemsByProductId(Session session, long shopId, long productId) {
    return session.createQuery("from SalesOrderItem  roi where roi.shopId =:shopId and roi.productId =:productId")
        .setLong("shopId", shopId).setLong("productId", productId);
  }

  public static Query getPurchaseInventoryItemsByProductId(Session session, long orderId, long productId) {
    return session.createQuery("from PurchaseInventoryItem  roi where roi.purchaseInventoryId =:orderId and roi.productId =:productId")
        .setLong("productId", productId).setLong("orderId", orderId);
  }

  public static Query getPurchaseOrderItemsByProductId(Session session, long orderId, long productId) {
    return session.createQuery("from PurchaseOrderItem  roi where roi.purchaseOrderId =:orderId and roi.productId =:productId")
        .setLong("productId", productId).setLong("orderId", orderId);
  }

  public static Query getPurchaseReturnItemsByProdctId(Session session, long orderId, long productId) {
    return session.createQuery("from PurchaseReturnItem  roi where roi.purchaseReturnId =:orderId and  roi.productId =:productId")
        .setLong("productId", productId).setLong("orderId", orderId);
  }

  public static Query getPurchaseOrderByShopId(Session session, long shopId, Long status) {
    StringBuffer sb = new StringBuffer();
    sb.append("from PurchaseOrder r where  r.shopId=:shopId ");
    if (status != null) {
      sb.append(" and r.status =:status");
    }
    Query query = session.createQuery(sb.toString()).setLong("shopId", shopId);
    if (status != null) {
      query.setLong("status", status);
    }
    return query;
  }

  public static Query getPurchaseReturnByShopId(Session session, long shopId, Long status) {
    StringBuffer sb = new StringBuffer();
    sb.append("from PurchaseReturn r where  r.shopId=:shopId ");
    if (status != null) {
      sb.append(" and r.status =:status");
    }
    Query query = session.createQuery(sb.toString()).setLong("shopId", shopId);
    if (status != null) {
      query.setLong("status", status);
    }
    return query;
  }

  public static Query getrepairOrdersByShopId(Session session, long shopId, OrderStatus status) {
    return session.createQuery("from RepairOrder r where r.shopId =:shopId and r.statusEnum =:status")
        .setLong("shopId", shopId).setParameter("status", status);
  }

  public static Query getRepairOrderById(Session session, long id, long shopId) {
    return  session.createQuery(" from RepairOrder r where r.shopId =:shopId and r.id =:id")
        .setLong("shopId", shopId).setLong("id", id);
  }

  public static Query getSalesOrderByShopId(Session session, long shopId, OrderStatus status) {
    return session.createQuery("from SalesOrder r where r.shopId =:shopId and r.statusEnum =:status")
        .setLong("shopId", shopId).setParameter("status", status);
  }

  public static Query countSalesOrderByVestDate(Session session, long shopId, long startTime, long endTime) {
    StringBuffer sb = new StringBuffer();
    sb.append(" select count(*) from SalesOrder where shopId = :shopId " +
        "and vestDate >=:startTime and vestDate < :endTime and ( statusEnum =:status or statusEnum =:debtStatus ) ");
    return session.createQuery(sb.toString())
        .setLong("shopId", shopId)
        .setLong("startTime", startTime)
        .setLong("endTime", endTime)
        .setParameter("status",OrderStatus.SALE_DONE)
        .setParameter("debtStatus",OrderStatus.SALE_DEBT_DONE);
  }

  public static Query getSalesOrderListByPager(Session session, long shopId, long startTime, long endTime, Pager pager) {
    StringBuffer sb = new StringBuffer();
    sb.append(" from SalesOrder where shopId = :shopId " +
        "and vestDate >=:startTime and vestDate < :endTime and ( statusEnum =:status or statusEnum =:debtStatus ) ");
    return session.createQuery(sb.toString())
        .setLong("shopId", shopId)
        .setLong("startTime", startTime)
        .setLong("endTime", endTime)
        .setParameter("status",OrderStatus.SALE_DONE)
        .setParameter("debtStatus",OrderStatus.SALE_DEBT_DONE)
        .setFirstResult(pager.getRowStart())
        .setMaxResults(pager.getPageSize());
  }

  public static Query countRepairOrderByVestDate(Session session, long shopId, long startTime, long endTime) {
    StringBuffer sb = new StringBuffer();
    sb.append(" select count(*) from repair_order where shop_id = :shopId " +
        "and vest_date >=:startTime and vest_date < :endTime");
    return session.createSQLQuery(sb.toString())
        .setLong("shopId", shopId)
        .setLong("startTime", startTime)
        .setLong("endTime", endTime);
  }

  public static Query getRepairOrderListByPager(Session session, long shopId, long startTime, long endTime, Pager pager) {
    StringBuffer sb = new StringBuffer();
    sb.append(" select * from repair_order where shop_id = :shopId " +
        "and vest_date >=:startTime and vest_date < :endTime");
    return session.createSQLQuery(sb.toString()).addEntity(RepairOrder.class)
        .setLong("shopId", shopId)
        .setLong("startTime", startTime)
        .setLong("endTime", endTime)
        .setFirstResult(pager.getRowStart())
        .setMaxResults(pager.getPageSize());
  }

  public static Query getRepealOrderByShopIdAndOrderId(Session session, long shopId, long orderId) {
    StringBuffer sb = new StringBuffer();
    sb.append(" select * from repeal_order where shop_id = :shopId and order_id =:orderId ");
    return session.createSQLQuery(sb.toString()).addEntity(RepealOrder.class)
        .setLong("shopId", shopId)
        .setLong("orderId", orderId);
  }

  public static Query getTotalPayable(Session session,RecOrPayIndexDTO recOrPayIndexDTO){
    StringBuffer sb = new StringBuffer();
    sb.append("select sum(creditAmount) from Payable p where p.status<>'REPEAL'  and p.creditAmount>0 and p.shopId=:shopId and p.orderType =:orderType ");
    if (recOrPayIndexDTO.getCustomerOrSupplierId() != null) {
      sb.append(" and p.supplierId=:supplierId");
    }
    if (recOrPayIndexDTO.getStartDate() != null) {
      sb.append(" and p.payTime>=:startDate");
    }
    if (recOrPayIndexDTO.getEndDate() != null) {
      sb.append(" and p.payTime<=:endDate");
    }
    if(StringUtil.isNotEmpty(recOrPayIndexDTO.getReceiptNo())){
      sb.append(" and p.receiptNo like:receiptNo");
    }
    if(null!=recOrPayIndexDTO.getCustomerOrSupplierIds()&&recOrPayIndexDTO.getCustomerOrSupplierIds().size()!=0){
      sb.append(" and p.supplierId in (:supplierIds)");
    }
    Query q = session.createQuery(sb.toString()).setLong("shopId",recOrPayIndexDTO.getShopId()).setString("orderType",OrderTypes.INVENTORY.toString());
    if (recOrPayIndexDTO.getCustomerOrSupplierId() != null) {
      q.setLong("supplierId", recOrPayIndexDTO.getCustomerOrSupplierId());
    }
    if (recOrPayIndexDTO.getStartDate() != null) {
      q.setLong("startDate", recOrPayIndexDTO.getStartDate());
    }
    if (recOrPayIndexDTO.getEndDate() != null) {
      q.setLong("endDate", recOrPayIndexDTO.getEndDate());
    }
    if(StringUtil.isNotEmpty(recOrPayIndexDTO.getReceiptNo())){
      q.setString("receiptNo","%"+recOrPayIndexDTO.getReceiptNo()+"%");
    }
    if(null!=recOrPayIndexDTO.getCustomerOrSupplierIds()&&recOrPayIndexDTO.getCustomerOrSupplierIds().size()!=0){
      q.setParameterList("supplierIds",recOrPayIndexDTO.getCustomerOrSupplierIds());
    }

    return q;
  }

  public static Query getAllPayables(Session session){
    StringBuffer sb = new StringBuffer();
    sb.append("from Payable p where p.status<>'REPEAL'  ");
    Query q = session.createQuery(sb.toString());
    return q;
  }

  public static Query getPayables(Session session,RecOrPayIndexDTO recOrPayIndexDTO){
    StringBuffer sb = new StringBuffer();
    sb.append("from Payable p where p.status<>'REPEAL'  and p.creditAmount>0 and p.shopId=:shopId and p.orderType =:orderType ");
    if (recOrPayIndexDTO.getCustomerOrSupplierId() != null) {
      sb.append(" and p.supplierId=:supplierId");
    }
    if (recOrPayIndexDTO.getStartDate() != null) {
      sb.append(" and p.payTime>=:startDate");
    }
    if (recOrPayIndexDTO.getEndDate() != null) {
      sb.append(" and p.payTime<=:endDate");
    }
    if(StringUtil.isNotEmpty(recOrPayIndexDTO.getReceiptNo())){
      sb.append(" and p.receiptNo like:receiptNo");
    }
    if(null!=recOrPayIndexDTO.getCustomerOrSupplierIds()&&recOrPayIndexDTO.getCustomerOrSupplierIds().size()!=0){
      sb.append(" and p.supplierId in (:supplierIds)");
    }
    sb.append(" order by payTime desc ");
    Query q = session.createQuery(sb.toString()).setLong("shopId",recOrPayIndexDTO.getShopId()).setString("orderType",OrderTypes.INVENTORY.toString());
    if (recOrPayIndexDTO.getCustomerOrSupplierId() != null) {
      q.setLong("supplierId", recOrPayIndexDTO.getCustomerOrSupplierId());
    }
    if (recOrPayIndexDTO.getStartDate() != null) {
      q.setLong("startDate", recOrPayIndexDTO.getStartDate());
    }
    if (recOrPayIndexDTO.getEndDate() != null) {
      q.setLong("endDate", recOrPayIndexDTO.getEndDate());
    }
    if(StringUtil.isNotEmpty(recOrPayIndexDTO.getReceiptNo())){
      q.setString("receiptNo","%"+recOrPayIndexDTO.getReceiptNo()+"%");
    }
    if(null!=recOrPayIndexDTO.getCustomerOrSupplierIds()&&recOrPayIndexDTO.getCustomerOrSupplierIds().size()!=0){
      q.setParameterList("supplierIds",recOrPayIndexDTO.getCustomerOrSupplierIds());
    }
    if(null!=recOrPayIndexDTO.getPager()){
      q.setFirstResult(recOrPayIndexDTO.getPager().getRowStart()).setMaxResults(recOrPayIndexDTO.getPager().getPageSize());
    }
    return q;
  }

  public static Query getLastWashOrderDTO(Session session, Long shopId, Long customerId) {
    return session.createQuery("from WashOrder wo where wo.shopId =:shopId and wo.customerId =:customerId and wo.orderType <> :orderType order by wo.vestDate desc")
        .setLong("shopId", shopId).setLong("customerId", customerId).setLong("orderType", TxnConstant.BUY_WASH_CARD);
  }

  public static Query countServices(Session session, Long shopId) {
    return session.createQuery("select count(*) from Service s where s.shopId = :shopId and (s.status is null or s.status <> :status)")
        .setLong("shopId", shopId).setParameter("status", ServiceStatus.DISABLED);
  }

  public static Query getServiceByShopId(Session session, Long shopId, int pageNo, int maxPageSize) {
    return session.createQuery("from Service s where s.shopId =:shopId and (s.status is null or s.status <> :status)")
        .setLong("shopId", shopId).setParameter("status", ServiceStatus.DISABLED).setFirstResult((pageNo - 1) * maxPageSize).setMaxResults(maxPageSize);
  }

  public static Query getAllServiceIdsByShopId(Session session, Long shopId, int start, int rows) {
    return session.createQuery("select id from Service s where s.shopId =:shopId ").setLong("shopId", shopId).setFirstResult(start).setMaxResults(rows);
  }

  public static Query getLastMemberCardOrderByCustomerId(Session session, Long shopId, Long customerId) {
    return session.createQuery("from MemberCardOrder mco where mco.shopId =:shopId and mco.customerId =:customerId" +
        " order by vestDate desc").setLong("shopId", shopId).setLong("customerId", customerId);
  }

  public static Query getLastMemberCardOrderItemByOrderId(Session session, Long shopId, Long orderId) {
    return session.createQuery("from MemberCardOrderItem m where m.shopId =:shopId and m.memberCardOrderId =:memberCardOrderId" +
        " order by created desc").setLong("shopId", shopId).setLong("memberCardOrderId", orderId);
  }

  public static Query getAllServiceByShopId(Session session, Long shopId) {
    return session.createQuery("from Service s where s.shopId =:shopId or s.shopId = -1 and (s.status is null or s.status <> :status)")
        .setLong("shopId", shopId).setParameter("status", ServiceStatus.DISABLED);
  }

  public static Query getServiceByServiceName(Session session, Long shopId, String serviceName, Long pageNo, Long pageSize) {
    //TODO 以后存入solr中，性能问题
    int firstResultIndex = (pageNo.intValue() - 1) * pageSize.intValue();
    StringBuffer sb = new StringBuffer();
    sb.append("from Service s where (s.status is null or s.status <> :status) and ");
    if (serviceName != null && !"".equals(serviceName)) {
      sb.append("upper(REPLACE(s.name, ' ','')) like:serviceName and ");
    }
    sb.append("(s.shopId =:shopId or s.shopId = -1)");
    Query q = session.createQuery(sb.toString()).setLong("shopId", shopId).setParameter("status", ServiceStatus.DISABLED).setFirstResult(firstResultIndex).setMaxResults(pageSize.intValue());
    if (serviceName != null && !"".equals(serviceName)) {
      q.setString("serviceName", "%" + StringUtil.toTrimAndUpperCase(serviceName) + "%");
    }
    return q;
  }

  public static Query getCategoryIdByServiceId(Session session, Long serviceId) {
    return session.createQuery("from CategoryItemRelation c where c.serviceId =:serviceId")
        .setLong("serviceId", serviceId);
  }

  public static Query getCategoryByIdAndCategoryName(Session session, Long id, String categoryName, String categoryType) {
    StringBuffer sb = new StringBuffer();
    sb.append("from Category c where c.id=:id and c.categoryType =:categoryType and (c.status is null or c.status != :status");
    if (categoryName != null && !"".equals(categoryName)) {
      sb.append(" and c.categoryName =:categoryName");
    }
    Query q = session.createQuery(sb.toString()).setLong("id", id).setString("categoryType", categoryType).setString("status",CategoryStatus.DISABLED.toString());
    if (categoryName != null && !"".equals(categoryName)) {
      q.setString("categoryName", categoryName);
    }
    return q;
  }

  public static Query getCategoryByShopId(Session session, Long shopId) {
    return session.createQuery("from Category c where (c.shopId =:shopId or c.shopId = -1) and (c.status is null or c.status != :status) ")
        .setLong("shopId", shopId).setString("status",CategoryStatus.DISABLED.toString());
  }

  public static Query getServiceNoPercentage(Session session, Long shopId, Long pageNo, Long pageSize) {
    int firstResultIndex = (pageNo.intValue() - 1) * pageSize.intValue();
    return session.createQuery("from Service s where (s.shopId =:shopId or s.shopId = -1) and (s.status is null or s.status <> :status) " +
        "and s.percentage is null and (s.percentageAmount is null or s.percentageAmount = 0)")
        .setLong("shopId", shopId).setParameter("status", ServiceStatus.DISABLED).setFirstResult(firstResultIndex).setMaxResults(pageSize.intValue());
  }

  public static Query getCategoryByName(Session session, Long shopId, String name, CategoryType type) {
    return session.createQuery("from Category c where c.categoryName =:name and c.categoryType =:type" +
        " and (c.shopId =:shopId or c.shopId = -1)").setString("name", name).setString("type", type.toString())
        .setLong("shopId", shopId);
  }

  public static Query getCategoryByNames(Session session, Long shopId, CategoryType type, String... names) {
    return session.createQuery("from Category c where (c.shopId =:shopId or c.shopId = -1) and c.categoryName in(:names) and c.categoryType =:type" )
        .setParameterList("names", names).setString("type", type.toString()).setLong("shopId", shopId);
  }

  public static Query getMemberServiceByMemberIdAndServiceId(Session session, Long memberId, Long serviceId) {
    return session.createQuery("from MemberService ms where ms.memberId =:memberId and ms.serviceId =:serviceId")
        .setLong("memberId", memberId).setLong("serviceId", serviceId);
  }

  public static Query getCategoryItemRelationByCAndSId(Session session, Long categoryId, Long serviceId) {
    return session.createQuery("from CategoryItemRelation c where c.categoryId =:categoryId and c.serviceId =:serviceId")
        .setLong("categoryId", categoryId).setLong("serviceId", serviceId);
  }

  public static Query getServiceNoCategory(Session session, Long shopId, String serviceName, Long pageNo, Long pageSize) {
    int firstResultIndex = (pageNo.intValue() - 1) * pageSize.intValue();
    StringBuffer sb = new StringBuffer();
    sb.append("SELECT s.*");
    sb.append(" FROM service s LEFT JOIN category_item_relation cr on s.id = cr.service_id ");
    sb.append(" WHERE cr.id IS NULL AND s.shop_id =:shopId AND s.status =:serviceStatus ");
    if (StringUtils.isNotBlank(serviceName)) {
      sb.append(" AND s.name like :serviceName ");
    }
    Query query = session.createSQLQuery(sb.toString()).addEntity(Service.class);
    query.setLong("shopId", shopId).setParameter("serviceStatus", ServiceStatus.ENABLED.name());
    if (StringUtils.isNotBlank(serviceName)) {
      query.setParameter("serviceName", "%" + serviceName + "%");
    }
    query.setFirstResult(firstResultIndex).setMaxResults(pageSize.intValue());
    return query;

  }

  public static Query getServiceByCategoryName(Session session, Long shopId, String categoryName, CategoryType categoryType, String serviceName, Long pageNo, Long pageSize) {
    //TODO 以后存入solr中，性能问题
    int firstResultIndex = (pageNo.intValue() - 1) * pageSize.intValue();
    StringBuffer sb = new StringBuffer();
    sb.append("from Service s where (s.shopId =:shopId or s.shopId = -1) and (s.status is null or s.status <> :status) and ");
    sb.append("s.id in(select cir.serviceId from CategoryItemRelation cir where cir.categoryId in(");
    sb.append("select c.id from Category c where (c.shopId =:shopId or c.shopId = -1) and ");
    sb.append("upper(REPLACE(c.categoryName, ' ','')) like :categoryName and c.categoryType =:categoryType))");
    if (serviceName != null && !"".equals(serviceName)) {
      sb.append(" and upper(REPLACE(s.name, ' ','')) like:serviceName");
    }
    Query q = session.createQuery(sb.toString());
    q.setLong("shopId", shopId).setParameter("status", ServiceStatus.DISABLED).setString("categoryName", "%" + StringUtil.toTrimAndUpperCase(categoryName) + "%").setString("categoryType", categoryType.toString())
        .setFirstResult(firstResultIndex).setMaxResults(pageSize.intValue());
    if (serviceName != null && !"".equals(serviceName)) {
      q.setString("serviceName", "%" + StringUtil.toTrimAndUpperCase(serviceName) + "%");
    }
    return q;
  }

  public static Query countServiceByCategory(Session session, Long shopId, String categoryName, CategoryType categoryType, String serviceName) {
    StringBuffer sb = new StringBuffer();
    sb.append("select count(*) from Service s where (s.shopId =:shopId or s.shopId = -1) and (s.status is null or s.status <> :status) and ");
    sb.append("s.id in(select cir.serviceId from CategoryItemRelation cir where cir.categoryId in(");
    sb.append("select c.id from Category c where (c.shopId =:shopId or c.shopId = -1) and ");
    sb.append("c.categoryName =:categoryName and c.categoryType =:categoryType))");
    if (serviceName != null && !"".equals(serviceName)) {
      sb.append(" and s.name like:serviceName");
    }
    Query q = session.createQuery(sb.toString());
    q.setLong("shopId", shopId).setParameter("status", ServiceStatus.DISABLED).setString("categoryName", categoryName).setString("categoryType", categoryType.toString());
    if (serviceName != null && !"".equals(serviceName)) {
      q.setString("serviceName", "%" + serviceName + "%");
    }
    return q;
  }

  public static Query countServiceByServiceName(Session session, Long shopId, String serviceName) {
    StringBuffer sb = new StringBuffer();
    sb.append("select count(*) from Service s where (s.status is null or s.status <> :status) and ");
    if (serviceName != null && !"".equals(serviceName)) {
      sb.append("s.name like:serviceName and ");
    }
    sb.append("(s.shopId =:shopId or s.shopId = -1)");
    Query q = session.createQuery(sb.toString()).setLong("shopId", shopId).setParameter("status", ServiceStatus.DISABLED);
    if (serviceName != null && !"".equals(serviceName)) {
      q.setString("serviceName", "%" + serviceName + "%");
    }
    return q;
  }

  public static Query countServiceNoCategory(Session session, Long shopId, String serviceName) {
     StringBuffer sb = new StringBuffer();
     sb.append("SELECT count(s.id) as count");
     sb.append(" FROM service s LEFT JOIN category_item_relation cr on s.id = cr.service_id ");
     sb.append(" WHERE cr.id IS NULL AND s.shop_id =:shopId AND s.status =:serviceStatus ");
     if (StringUtils.isNotBlank(serviceName)) {
       sb.append(" AND s.name like :serviceName ");
     }
     Query query = session.createSQLQuery(sb.toString()).addScalar("count",StandardBasicTypes.LONG);
     query.setLong("shopId", shopId).setParameter("serviceStatus", ServiceStatus.ENABLED.name());
     if (StringUtils.isNotBlank(serviceName)) {
       query.setParameter("serviceName", "%" + serviceName + "%");
     }
     return query;
  }

  public static Query countServiceNoPercentage(Session session, Long shopId) {
    return session.createQuery("select count(*) from Service s where (s.shopId =:shopId or s.shopId = -1) " +
        "and (s.status is null or s.status <> :status) and s.percentage is null and (s.percentageAmount is null or s.percentageAmount=0)")
        .setLong("shopId", shopId).setParameter("status", ServiceStatus.DISABLED);
  }

  public static Query getServiceByServiceNameAndShopId(Session session, Long shopId, String serviceName) {
    StringBuffer sb = new StringBuffer();
    sb.append("from Service s where (s.status is null or s.status <> :status) and ");
    if (!StringUtil.isEmpty(serviceName)) {
      sb.append("s.name =:serviceName and ");
    }
    sb.append(" s.shopId =:shopId ");
    Query q = session.createQuery(sb.toString()).setLong("shopId", shopId)
        .setParameter("status", ServiceStatus.DISABLED);
    if (!StringUtil.isEmpty(serviceName)) {
      q.setString("serviceName", serviceName);
    }
    return q;
  }

  public static Query getServiceByNames(Session session, Long shopId, boolean isIncludeDisabled, String... serviceNames) {
    StringBuffer sb = new StringBuffer();
    sb.append("from Service s where s.shopId =:shopId and ");
    if(!isIncludeDisabled){
      sb.append(" (s.status is null or s.status =:status) and");
    }
    if (serviceNames!=null && !ArrayUtils.isEmpty(serviceNames)) {
      sb.append("s.name in(:serviceNames) ");
    }

    Query q = session.createQuery(sb.toString()).setLong("shopId", shopId);
    if(!isIncludeDisabled) {
      q.setParameter("status", ServiceStatus.ENABLED);
    }
    if (serviceNames!=null && !ArrayUtils.isEmpty(serviceNames)) {
      q.setParameterList("serviceNames", serviceNames);
    }
    return q;
  }

  public static Query getServiceByWashBeauty(Session session, Long shopId, CategoryType categoryType) {

    return session.createQuery("from Service s " +
        " where (s.shopId =:shopId or s.shopId = -1) and (s.status is null or s.status <> :status)" +
        " and (s.id in" +
          "(select cir.serviceId from CategoryItemRelation cir, Category c where cir.categoryId =c.id " +
          "and (c.shopId =:shopId or c.shopId = -1) and (c.categoryName = '洗车' or c.categoryName = '美容') " +
          "and c.categoryType =:categoryType) " +
        "or (s.timeType =:timeType)) order by useTimes desc ").setLong("shopId",shopId)
        .setParameter("status",ServiceStatus.DISABLED).setString("categoryType",categoryType.toString()).setString("timeType",ServiceTimeType.YES.toString());
  }

  public static Query getWashService(Session session, Long shopId) {
    return session.createQuery("from Service s where s.shopId =:shopId and s.name = '洗车'").setLong("shopId", shopId);
  }


  public static Query getMemberCardOrderDTOById(Session session, Long shopId, Long orderId) {
    return session.createQuery("from MemberCardOrder mco where mco.shopId = :shopId and mco.id=:orderId")
        .setLong("shopId", shopId).setLong("orderId", orderId);
  }

  public static Query getMemberCardOrderItemDTOByOrderId(Session session, Long shopId, Long orderId) {
    return session.createQuery("from MemberCardOrderItem mcoi where mcoi.shopId = :shopId and mcoi.memberCardOrderId =:orderId")
        .setLong("shopId", shopId).setLong("orderId", orderId);
  }

  public static Query getMemberCardOrderServiceDTOByOrderId(Session session, Long shopId, Long orderId) {
    return session.createQuery("from MemberCardOrderService mcos where mcos.shopId =:shopId and mcos.memberCardOrderId =:orderId")
        .setLong("shopId", shopId).setLong("orderId", orderId);
  }


  public static Query getWashBeautyOrderDTOById(Session session, Long shopId, Long orderId) {
    StringBuilder sb=new StringBuilder("from WashBeautyOrder wbo where wbo.id = :orderId");
    if(shopId!=null){
      sb.append(" and wbo.shopId = :shopId");
    }
    Query query= session.createQuery(sb.toString()).setLong("orderId", orderId);
    if(shopId!=null){
      query.setLong("shopId", shopId);
    }
    return query;
  }

  public static Query getWashBeautyOrderItemDTOByOrderId(Session session, Long shopId, Long orderId) {
    return session.createQuery("from WashBeautyOrderItem wbot where wbot.shopId = :shopId and wbot.washBeautyOrderId = :orderId")
        .setLong("shopId", shopId).setLong("orderId", orderId);
  }

  public static Query getAllServiceDTOByShopId(Session session, Long shopId) {
    return session.createQuery("from Service s where s.shopId = :shopId and (s.status is null or s.status <> :status)")
        .setLong("shopId", shopId).setParameter("status", ServiceStatus.DISABLED);
  }

  public static Query searchSuggestionForServices(Session session, Long shopId, String searchKey) {
    //TODO 以后存入solr中，性能问题
    return session.createQuery("from Service s where s.shopId = :shopId and upper(REPLACE(s.name,' ','')) like :searchKey ")
        .setLong("shopId", shopId).setString("searchKey", "%" + StringUtil.toTrimAndUpperCase(searchKey) + "%");
  }

  public static Query getObscureServiceByName(Session session, Long shopId, String serviceName) {
    //TODO 以后存入solr中，性能问题
    return session.createQuery("from Service s where s.shopId = :shopId and (s.status is null or s.status <> :status) and upper(REPLACE(s.name,' ','')) like :name")
        .setLong("shopId", shopId).setParameter("status", ServiceStatus.DISABLED).setString("name", "%" + StringUtil.toTrimAndUpperCase(serviceName) + "%");
  }

  public static Query getObscureCategoryByName(Session session, Long shopId, String categoryName) {
    //TODO 以后存入solr中，性能问题
    return session.createQuery("from Category c where c.shopId in (:shopId,-1) and upper(REPLACE(c.categoryName,' ','')) like :categoryName and (c.status is null or c.status != :status)")
        .setLong("shopId", shopId).setString("categoryName", "%" + StringUtil.toTrimAndUpperCase(categoryName) + "%").setString("status",CategoryStatus.DISABLED.toString());
  }

  public static Query getMemberOrderCountAndSum(Session session, long shopId, long startTime, long endTime,OrderSearchConditionDTO orderSearchConditionDTO) {
    StringBuffer sb = new StringBuffer();
    sb.append(" select count(m.id),sum(m.total),sum(r.settled_amount) from member_card_order m, receivable r where m.shop_id = :shopId and m.id = r.order_id and m.vest_date >= :startTime and m.vest_date < :endTime");
    if (!ArrayUtils.isEmpty(orderSearchConditionDTO.getCustomerOrSupplierIds())) {
      sb.append(" and m.customer_id in(:customerIds)");
    }
    Query query = session.createSQLQuery(sb.toString())
        .setLong("shopId", shopId)
        .setLong("startTime", startTime)
        .setLong("endTime", endTime);
    if (!ArrayUtils.isEmpty(orderSearchConditionDTO.getCustomerOrSupplierIds())) {
      query.setParameterList("customerIds", Arrays.asList(orderSearchConditionDTO.getCustomerOrSupplierIds()));
    }
    return query;

  }

  public static Query getMemberOrderListByPagerTimeArrayType(Session session, long shopId, long startTime, long endTime, Pager pager, String arrayType,OrderSearchConditionDTO orderSearchConditionDTO) {
    StringBuffer sb = new StringBuffer();
    if (StringUtil.isEmpty(arrayType)) {
      arrayType = " order by created desc ";
    }
    if (arrayType.equals(" order by total desc") || arrayType.equals(" order by total asc")) {
      sb.append(" select m.* from member_card_order m ,receivable r where  m.id = r.order_id and m.shop_id = r.shop_id and m.shop_id =:shopId " +
          " and m.vest_date >= :memberStartTime and m.vest_date < :memberEndTime " +
          " and r.created >= :startTime and r.created < :endTime ");
      if (!ArrayUtils.isEmpty(orderSearchConditionDTO.getCustomerOrSupplierIds())) {
        sb.append(" and m.customer_id in(:customerIds)");
      }
      if (arrayType.equals(" order by total desc")) {
        sb.append(" order by r.settled_amount desc ");
      } else {
        sb.append(" order by r.settled_amount asc ");
      }
      Query query = session.createSQLQuery(sb.toString()).addEntity(MemberCardOrder.class)
          .setLong("shopId", shopId)
          .setLong("memberStartTime", startTime)
          .setLong("memberEndTime", endTime)
          .setLong("startTime", startTime)
          .setLong("endTime", endTime)
          .setFirstResult(pager.getRowStart()).setMaxResults(pager.getPageSize());
      if (!ArrayUtils.isEmpty(orderSearchConditionDTO.getCustomerOrSupplierIds())) {
        query.setParameterList("customerIds",  Arrays.asList(orderSearchConditionDTO.getCustomerOrSupplierIds()));
      }
      return query;
    }


    sb.append(" select * from member_card_order where shop_id = :shopId and vest_date >= :startTime and vest_date < :endTime");
    if (!ArrayUtils.isEmpty(orderSearchConditionDTO.getCustomerOrSupplierIds())) {
      sb.append(" and customer_id in(:customerIds)");
    }
    sb.append(arrayType);
    Query query = session.createSQLQuery(sb.toString()).addEntity(MemberCardOrder.class)
        .setLong("shopId", shopId)
        .setLong("startTime", startTime)
        .setLong("endTime", endTime).setFirstResult(pager.getRowStart()).setMaxResults(pager.getPageSize());
    if (!ArrayUtils.isEmpty(orderSearchConditionDTO.getCustomerOrSupplierIds())) {
      query.setParameterList("customerIds", Arrays.asList(orderSearchConditionDTO.getCustomerOrSupplierIds()));
    }
    return query;
  }


  public static Query countMemberAgentAchievements(Session session, long shopId, long startTime, long endTime) {
    return session.createSQLQuery("SELECT wo.* FROM member_card_order wo WHERE wo.shop_id =:shopId AND wo.vest_date >= :startTime AND wo.vest_date < :endTime ")
        .addEntity(MemberCardOrder.class).setLong("shopId", shopId).setLong("startTime", startTime).setLong("endTime", endTime);
  }

  public static Query countWashBeautyAgentAchievements(Session session, long shopId, long startTime, long endTime) {
    return session.createSQLQuery("SELECT wo.* FROM wash_beauty_order wo WHERE wo.shop_id =:shopId AND wo.vest_date >= :startTime AND wo.vest_date < :endTime ")
        .addEntity(WashBeautyOrder.class).setLong("shopId", shopId).setLong("startTime", startTime).setLong("endTime", endTime);
  }

  public static Query getServiceByShopIdAndNameRemovalTrimAndUpper(Session session, Long shopId, String serviceName, Long serviceId) {
    if (null == serviceId) {
      return session.createQuery("from Service s where s.shopId=:shopId and (s.status is null or s.status <> :status) and upper(REPLACE(s.name,' ','')) = :serviceName")
          .setLong("shopId", shopId).setParameter("status", ServiceStatus.DISABLED).setString("serviceName", StringUtil.toTrimAndUpperCase(serviceName));
    } else {
      return session.createQuery("from Service s where s.shopId=:shopId and (s.status is null or s.status <> :status) and upper(REPLACE(s.name,' ','')) = :serviceName and s.id != :serviceId")
          .setLong("shopId", shopId).setParameter("status", ServiceStatus.DISABLED).setString("serviceName", StringUtil.toTrimAndUpperCase(serviceName)).setLong("serviceId", serviceId);
    }

  }

  public static Query getReceivableByShopIdAndOrderId(Session session, long shopId, long orderId) {
    return session.createQuery("from Receivable r where  r.shopId=:shopId and r.orderId=:orderId")
        .setLong("shopId", shopId).setLong("orderId", orderId);
  }


  public static Query getServiceById(Session session, Long shopId, Long serviceId) {
    return session.createQuery("from Service s where s.id = :serviceId and s.shopId = :shopId")
        .setLong("shopId", shopId).setLong("serviceId", serviceId);
  }

  public static Query getRFServiceByServiceNameAndShopId(Session session, Long shopId, String serviceName) {
    //TODO 以后存入solr中，性能问题
    return session.createQuery("from Service s where s.shopId =:shopId and upper(REPLACE(s.name, ' ','')) =:serviceName")
        .setLong("shopId", shopId).setString("serviceName", StringUtil.toTrimAndUpperCase(serviceName));
  }


  public static Query getReceptionByShopIdAndPager(Session session, long shopId, Pager pager) {
    return session.createQuery("from ReceptionRecord  r where  r.shopId=:shopId ")
        .setLong("shopId", shopId).setFirstResult(pager.getRowStart()).setMaxResults(pager.getPageSize());
  }

  public static Query getRunningStatDTOByShopIdYearMonthDay(Session session, long shopId, long statYear, long statMonth, long statDay) {
    StringBuilder sb = new StringBuilder(" select * from running_stat where shop_id=:shopId and stat_year=:statYear ");
    sb.append(" and stat_month =:statMonth ");
    sb.append(" and stat_day =:statDay ");
    return session.createSQLQuery(sb.toString()).addEntity(RunningStat.class)
        .setLong("shopId", shopId)
        .setLong("statYear", statYear)
        .setLong("statMonth", statMonth)
        .setLong("statDay", statDay);
  }

  public static Query getLastRunningStatDTOByShopId(Session session, Long shopId, Long statYear) {
    if (statYear == null) {
      StringBuilder sb = new StringBuilder("select * from running_stat where shop_id=:shopId");
      sb.append(" order by stat_date desc ");
      return session.createSQLQuery(sb.toString()).addEntity(RunningStat.class)
          .setLong("shopId", shopId).setFirstResult(0).setMaxResults(1);
    }
    StringBuilder sb = new StringBuilder("select * from running_stat where shop_id=:shopId and stat_year=:statYear ");
    sb.append(" order by stat_date desc ");
    return session.createSQLQuery(sb.toString()).addEntity(RunningStat.class)
        .setLong("shopId", shopId).setLong("statYear", statYear).setFirstResult(0).setMaxResults(1);
  }

  public static Query getRepealOrderListByPager(Session session, long shopId, long startTime, long endTime, Pager pager) {
    StringBuffer sb = new StringBuffer();
    sb.append(" select * from repeal_order where shop_id = :shopId " +
        "and repeal_date >=:startTime and repeal_date < :endTime");
    return session.createSQLQuery(sb.toString()).addEntity(RepealOrder.class)
        .setLong("shopId", shopId)
        .setLong("startTime", startTime)
        .setLong("endTime", endTime)
        .setFirstResult(pager.getRowStart())
        .setMaxResults(pager.getPageSize());
  }

  public static Query countRepealOrderByRepealDate(Session session, long shopId, long startTime, long endTime) {
    StringBuffer sb = new StringBuffer();
    sb.append(" select count(*) from repeal_order where shop_id = :shopId " +
        "and repeal_date >=:startTime and repeal_date < :endTime");
    return session.createSQLQuery(sb.toString())
        .setLong("shopId", shopId)
        .setLong("startTime", startTime)
        .setLong("endTime", endTime);
  }


  public static Query getRunningStatByYearMonthDay(Session session, Long shopId, Integer year, Integer month, Integer day, Integer size, Sort sort) {
    StringBuffer sb = new StringBuffer();
    sb.append(" select * from running_stat  r where r.shop_id=:shopId ");
    if (year != null) {
      sb.append(" and r.stat_year=:year ");
    }
    if (month != null) {
      sb.append(" and r.stat_month=:month ");
    }
    if (day != null) {
      sb.append(" and r.stat_day=:day ");
    }

    if (sort != null) {
      sb.append(sort.toOrderString());
    }


    Query query = session.createSQLQuery(sb.toString()).addEntity(RunningStat.class);
    query.setLong("shopId", shopId);

    if (year != null) {
      query.setInteger("year", year);
    }
    if (month != null) {
      query.setInteger("month", month);
    }
    if (day != null) {
      query.setInteger("day", day);
    }
    if (size != null) {
      query.setFirstResult(0).setMaxResults(size);
    }

    return query;

  }

  public static Query countPurchaseInventoryOrderByCreated(Session session, long shopId, long startTime, long endTime) {
    StringBuffer sb = new StringBuffer();
    sb.append(" select count(*) from purchase_inventory where shop_id = :shopId " +
        "and created >=:startTime and created < :endTime");
    return session.createSQLQuery(sb.toString())
        .setLong("shopId", shopId)
        .setLong("startTime", startTime)
        .setLong("endTime", endTime);
  }

  public static Query getInventoryOrderListByPager(Session session, long shopId, long startTime, long endTime, Pager pager) {
    StringBuffer sb = new StringBuffer();
    sb.append(" select * from purchase_inventory where shop_id = :shopId " +
        "and created >=:startTime and created < :endTime");
    return session.createSQLQuery(sb.toString()).addEntity(PurchaseInventory.class)
        .setLong("shopId", shopId)
        .setLong("startTime", startTime)
        .setLong("endTime", endTime)
        .setFirstResult(pager.getRowStart())
        .setMaxResults(pager.getPageSize());
  }


  public static Query countPurchaseReturnOrderByCreated(Session session, long shopId, long startTime, long endTime) {
    StringBuffer sb = new StringBuffer();
    sb.append(" select count(*) from purchase_return where shop_id = :shopId " +
        "and created >=:startTime and created < :endTime");
    return session.createSQLQuery(sb.toString())
        .setLong("shopId", shopId)
        .setLong("startTime", startTime)
        .setLong("endTime", endTime);
  }

  public static Query getPurchaseReturnOrderListByPager(Session session, long shopId, long startTime, long endTime, Pager pager) {
    StringBuffer sb = new StringBuffer();
    sb.append(" select * from purchase_return where shop_id = :shopId " +
        "and created >=:startTime and created < :endTime");
    return session.createSQLQuery(sb.toString()).addEntity(PurchaseReturn.class)
        .setLong("shopId", shopId)
        .setLong("startTime", startTime)
        .setLong("endTime", endTime)
        .setFirstResult(pager.getRowStart())
        .setMaxResults(pager.getPageSize());
  }

  public static Query getRunningStatMonth(Session session, Long shopId, Long year, String queryString) {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(" from RunningStat r where r.shopId= ");
    stringBuilder.append(shopId);
    stringBuilder.append(" and r.statYear= ");
    stringBuilder.append(year);
    stringBuilder.append(queryString);
    stringBuilder.append(" order by r.statDate ASC ");

    return session.createQuery(stringBuilder.toString());
  }

  public static Query countReceptionRecordByReceptionDate(Session session, long shopId, long startTime, long endTime) {
    StringBuffer sb = new StringBuffer();
    sb.append(" select count(*),sum(amount) from ReceptionRecord where shopId = :shopId " +
        " and receptionDate >=:startTime and receptionDate < :endTime and orderId is not null and orderId > 0 and orderTypeEnum is not null " +
        " and dayType != :dayType and dayType != :statementDayType  ");
    return session.createQuery(sb.toString())
        .setLong("shopId", shopId)
        .setLong("startTime", startTime)
        .setLong("endTime", endTime)
        .setParameter("dayType",DayType.TODAY).setParameter("statementDayType",DayType.STATEMENT_ACCOUNT);
  }


  public static Query getReceptionRecordByReceptionDate(Session session, long shopId, long startTime, long endTime, Pager pager) {
    StringBuffer sb = new StringBuffer();
    sb.append(" from ReceptionRecord where shopId = :shopId " +
        " and receptionDate >=:startTime and receptionDate < :endTime and orderId is not null and orderId > 0 and orderTypeEnum is not null " +
        " and dayType != :dayType and dayType != :statementDayType ");
    return session.createQuery(sb.toString())
        .setLong("shopId", shopId)
        .setLong("startTime", startTime)
        .setLong("endTime", endTime)
        .setParameter("dayType",DayType.TODAY).setParameter("statementDayType",DayType.STATEMENT_ACCOUNT)
        .setFirstResult(pager.getRowStart())
        .setMaxResults(pager.getPageSize());

  }

  public static Query getReindexRepairOrderId(Session session, Long shopId, long startId, int pageSize) {
    StringBuffer sb = new StringBuffer("select max(c.id) from (select id  from repair_order where id>:startId  limit :pageSize) as c");
    if (shopId != null) {
      sb.append(" and shop_id=:shopId");
    }
    sb.append(" order by id asc");
    Query query = session.createSQLQuery(sb.toString()).setLong("startId", startId);
    if (shopId != null) {
      query.setLong("shopId", shopId);
    }
    query.setLong("pageSize", pageSize);
    return query;
  }


  public static Query getReceivableDTOByShopIdAndArrayOrderId(Session session, Long shopId, Long... orderId) {
    StringBuffer sb = new StringBuffer("from Receivable where orderId in(:orderId)");
    if (shopId != null) {
      sb.append(" and shopId=:shopId");
    }
    Query query = session.createQuery(sb.toString()).setParameterList("orderId", orderId);
    if (shopId != null) {
      query.setLong("shopId", shopId);
    }
    return query;
  }

  public static Query getRepairOrderItemByShopIdAndArrayOrderId(Session session, Long shopId, Long... orderId) {
    StringBuffer sb = new StringBuffer("from RepairOrderItem where repairOrderId in(:orderId)");
    if (shopId != null) {
      sb.append(" and shopId=:shopId");
    }
    Query query = session.createQuery(sb.toString()).setParameterList("orderId", orderId);
    if (shopId != null) {
      query.setLong("shopId", shopId);
    }
    return query;
  }

  public static Query getRepairOrderServicesByShopIdAndArrayOrderId(Session session, Long shopId, Long... orderId) {
    StringBuffer sb = new StringBuffer("from RepairOrderService where repairOrderId in(:orderId)");
    if (shopId != null) {
      sb.append(" and shopId=:shopId");
    }
    Query query = session.createQuery(sb.toString()).setParameterList("orderId", orderId);
    if (shopId != null) {
      query.setLong("shopId", shopId);
    }
    return query;
  }

  public static Query getAllocateRecordOrderIds(Session session, Long shopId, int start, int pageSize) {
    StringBuffer sb = new StringBuffer("select r.id from AllocateRecord r");
    if (shopId != null) {
      sb.append(" where r.shopId=:shopId");
    }
    Query query = session.createQuery(sb.toString());
    if (shopId != null) {
      query.setLong("shopId", shopId);
    }

    query.setFirstResult(start).setMaxResults(pageSize);
    return query;
  }

  public static Query getReturnBorrowOrderIds(Session session, Long shopId, int start, int pageSize) {
    StringBuffer sb = new StringBuffer("select r.id from ReturnOrder r");
    if (shopId != null) {
      sb.append(" where r.shopId=:shopId");
    }
    Query query = session.createQuery(sb.toString());
    if (shopId != null) {
      query.setLong("shopId", shopId);
    }

    query.setFirstResult(start).setMaxResults(pageSize);
    return query;
  }

  public static Query getBorrowOrderIds(Session session, Long shopId, int start, int pageSize) {
    StringBuffer sb = new StringBuffer("select r.id from BorrowOrder r");
    if (shopId != null) {
      sb.append(" where r.shopId=:shopId");
    }
    Query query = session.createQuery(sb.toString());
    if (shopId != null) {
      query.setLong("shopId", shopId);
    }

    query.setFirstResult(start).setMaxResults(pageSize);
    return query;
  }

  public static Query getInnerReturnOrderIds(Session session, Long shopId, int start, int pageSize) {
    StringBuffer sb = new StringBuffer("select r.id from InnerReturn r");
    if (shopId != null) {
      sb.append(" where r.shopId=:shopId");
    }
    Query query = session.createQuery(sb.toString());
    if (shopId != null) {
      query.setLong("shopId", shopId);
    }

    query.setFirstResult(start).setMaxResults(pageSize);
    return query;
  }
  public static Query getInnerPickingOrderIds(Session session, Long shopId, int start, int pageSize) {
    StringBuffer sb = new StringBuffer("select r.id from InnerPicking r");
    if (shopId != null) {
      sb.append(" where r.shopId=:shopId");
    }
    Query query = session.createQuery(sb.toString());
    if (shopId != null) {
      query.setLong("shopId", shopId);
    }

    query.setFirstResult(start).setMaxResults(pageSize);
    return query;
  }
  public static Query getInventoryCheckOrderIds(Session session, Long shopId, int start, int pageSize) {
    StringBuffer sb = new StringBuffer("select r.id from InventoryCheck r");
    if (shopId != null) {
      sb.append(" where r.shopId=:shopId");
    }
    Query query = session.createQuery(sb.toString());
    if (shopId != null) {
      query.setLong("shopId", shopId);
    }

    query.setFirstResult(start).setMaxResults(pageSize);
    return query;
  }

  public static Query getRepairOrderIds(Session session, Long shopId, int start, int pageSize) {
    StringBuffer sb = new StringBuffer("select r.id from RepairOrder r");
    if (shopId != null) {
      sb.append(" where r.shopId=:shopId");
    }
    Query query = session.createQuery(sb.toString());
    if (shopId != null) {
      query.setLong("shopId", shopId);
    }

    query.setFirstResult(start).setMaxResults(pageSize);
    return query;
  }

  public static Query getPurchaseInventoryByShopIdAndArrayOrderId(Session session, Long shopId, Long... orderIds) {
    StringBuffer sb = new StringBuffer("from PurchaseInventory where id in (:orderIds)");
    if (shopId != null) {
      sb.append(" and shopId=:shopId");
    }
    Query query = session.createQuery(sb.toString()).setParameterList("orderIds", orderIds);
    if (shopId != null) {
      query.setLong("shopId", shopId);
    }
    return query;
  }

  public static Query getPurchaseOrdersByShopIdAndOrderIds(Session session, Long shopId, Long... orderIds) {
    StringBuffer sb = new StringBuffer("from PurchaseOrder where id in (:orderIds)");
    if (shopId != null) {
      sb.append(" and shopId=:shopId");
    }
    Query query = session.createQuery(sb.toString()).setParameterList("orderIds", orderIds);
    if (shopId != null) {
      query.setLong("shopId", shopId);
    }
    return query;
  }

  public static Query getPurchaseReturnsByShopIdAndOrderIds(Session session, Long shopId, Long... orderIds) {
    StringBuffer sb = new StringBuffer("from PurchaseReturn where id in (:orderIds)");
    if (shopId != null) {
      sb.append(" and shopId=:shopId");
    }
    Query query = session.createQuery(sb.toString()).setParameterList("orderIds", orderIds);
    if (shopId != null) {
      query.setLong("shopId", shopId);
    }
    return query;
  }
  public static Query getSalesReturnsByShopIdAndOrderIds(Session session, Long shopId, Long... orderIds) {
    StringBuffer sb = new StringBuffer("from SalesReturn where id in (:orderIds)");
    if (shopId != null) {
      sb.append(" and shopId=:shopId");
    }
    Query query = session.createQuery(sb.toString()).setParameterList("orderIds", orderIds);
    if (shopId != null) {
      query.setLong("shopId", shopId);
    }
    return query;
  }
  public static Query getMemberCardOrderByShopIdAndArrayOrderId(Session session, Long shopId, Long... orderIds) {
    StringBuffer sb = new StringBuffer("from MemberCardOrder where id in (:orderIds)");
    if (shopId != null) {
      sb.append(" and shopId=:shopId");
    }
    Query query = session.createQuery(sb.toString()).setParameterList("orderIds", orderIds);
    if (shopId != null) {
      query.setLong("shopId", shopId);
    }
    return query;
  }

  public static Query getSalesOrdersByShopIdAndOrderIds(Session session, Long shopId, Long... orderIds) {
    StringBuffer sb = new StringBuffer("from SalesOrder where id in (:orderIds)");
    if (shopId != null) {
      sb.append(" and shopId=:shopId");
    }
    Query query = session.createQuery(sb.toString()).setParameterList("orderIds", orderIds);
    if (shopId != null) {
      query.setLong("shopId", shopId);
    }
    return query;
  }
  public static Query getPreBuyOrdersByShopIdAndOrderIds(Session session, Long shopId, Long... orderIds) {
    StringBuffer sb = new StringBuffer("from PreBuyOrder where id in (:orderIds)");
    if (shopId != null) {
      sb.append(" and shopId=:shopId");
    }
    Query query = session.createQuery(sb.toString()).setParameterList("orderIds", orderIds);
    if (shopId != null) {
      query.setLong("shopId", shopId);
    }
    return query;
  }
  public static Query getQuotedPreBuyOrdersByShopIdAndOrderIds(Session session, Long shopId, Long... orderIds) {
    StringBuffer sb = new StringBuffer("from QuotedPreBuyOrder where id in (:orderIds)");
    if (shopId != null) {
      sb.append(" and shopId=:shopId");
    }
    Query query = session.createQuery(sb.toString()).setParameterList("orderIds", orderIds);
    if (shopId != null) {
      query.setLong("shopId", shopId);
    }
    return query;
  }
  public static Query getRepairOrdersByShopIdAndOrderIds(Session session, Long shopId, Long... orderIds) {
    StringBuffer sb = new StringBuffer("from RepairOrder where id in (:orderIds)");
    if (shopId != null) {
      sb.append(" and shopId=:shopId");
    }
    Query query = session.createQuery(sb.toString()).setParameterList("orderIds", orderIds);
    if (shopId != null) {
      query.setLong("shopId", shopId);
    }
    return query;
  }

  public static Query getWashBeautyOrderByShopIdAndArrayOrderId(Session session, Long shopId, Long... orderIds) {
    StringBuffer sb = new StringBuffer("from WashBeautyOrder where id in (:orderIds)");
    if (shopId != null) {
      sb.append(" and shopId=:shopId");
    }
    Query query = session.createQuery(sb.toString()).setParameterList("orderIds", orderIds);
    if (shopId != null) {
      query.setLong("shopId", shopId);
    }
    return query;
  }

  public static Query getWashBeautyOrderItemByShopIdAndArrayOrderId(Session session, Long shopId, Long... orderIds) {
    StringBuffer sb = new StringBuffer("from WashBeautyOrderItem where washBeautyOrderId in (:orderIds) ");
    if (shopId != null) {
      sb.append(" and shopId=:shopId");
    }
    Query query = session.createQuery(sb.toString()).setParameterList("orderIds", orderIds);
    if (shopId != null) {
      query.setLong("shopId", shopId);
    }
    return query;
  }


  public static Query getPurchaseInventoryItemDTOsByOrderIds(Session session, Long... ids) {
    StringBuffer sb = new StringBuffer();
    sb.append("from PurchaseInventoryItem pi where pi.purchaseInventoryId in (:ids) order by pi.purchaseInventoryId asc");
    return  session.createQuery(sb.toString()).setParameterList("ids", ids);
  }

  public static Query getPurchaseInventoryIds(Session session, Long shopId, int start, int size) {
    StringBuffer sb = new StringBuffer();
    sb.append("select pi.id from PurchaseInventory pi ");
    if (shopId != null) {
      sb.append(" where pi.shopId =:shopId");
    }
    sb.append(" order by pi.id asc");
    Query q = session.createQuery(sb.toString()).setFirstResult(start).setMaxResults(size);
    if (shopId != null) {
      q.setLong("shopId", shopId);
    }
    return q;
  }

  public static Query getWashBeautyOrderIds(Session session, Long shopId, int start, int pageSize) {
    StringBuffer sb = new StringBuffer("select id from WashBeautyOrder");
    if (shopId != null) {
      sb.append(" where shopId=:shopId");
    }
    Query query = session.createQuery(sb.toString());
    if (shopId != null) {
      query.setLong("shopId", shopId);
    }
    return query.setFirstResult(start).setMaxResults(pageSize);
  }

  public static Query getMemberCardOrderIds(Session session, Long shopId, int start, int pageSize) {
    StringBuffer sb = new StringBuffer("select id from MemberCardOrder");
    if (shopId != null) {
      sb.append(" where shopId=:shopId");
    }
    Query query = session.createQuery(sb.toString());
    if (shopId != null) {
      query.setLong("shopId", shopId);
    }
    return query.setFirstResult(start).setMaxResults(pageSize);
  }

  public static Query getMemberReturnCardOrderIds(Session session, Long shopId, int start, int pageSize) {
    StringBuffer sb = new StringBuffer("select id from MemberCardReturn");
    if (shopId != null) {
      sb.append(" where shopId=:shopId");
    }
    Query query = session.createQuery(sb.toString());
    if (shopId != null) {
      query.setLong("shopId", shopId);
    }
    return query.setFirstResult(start).setMaxResults(pageSize);
  }

  public static Query getServiceByServiceIdSet(Session session, Long shopId, Set<Long> serviceIds) {
    StringBuffer sb = new StringBuffer("from Service where id in (:serviceIds)");
    if (shopId != null) {
      sb.append(" and shopId=:shopId");
    }
    Query query = session.createQuery(sb.toString()).setParameterList("serviceIds", serviceIds);
    if (shopId != null) {
      query.setLong("shopId", shopId);
    }
    return query;
  }

  public static Query getLastServiceHistories(Session session, Long shopId, Set<Long> serviceIds) {
    StringBuffer sb = new StringBuffer();
    sb.append("select sh.* from service s LEFT JOIN service_history sh ");
    sb.append(" on s.id = sh.service_id and s.version = sh.history_version and s.shop_id = sh.shop_id ");
    sb.append(" where s.id in(:serviceIds) and s.shop_id =:shopId and sh.id is not null");
    Query query = session.createSQLQuery(sb.toString()).addEntity(ServiceHistory.class).setParameterList("serviceIds", serviceIds);
    query.setLong("shopId", shopId);
    return query;
  }

  public static Query getPurchaseReturnItemDTOsByOrderIds(Session session, Long... ids) {
    StringBuffer sb = new StringBuffer();
    sb.append("from PurchaseReturnItem pi where pi.purchaseReturnId in (:ids) ");
    return  session.createQuery(sb.toString()).setParameterList("ids", ids);
  }

  public static Query getSalesReturnItemDTOsByOrderIds(Session session, Long... ids) {
    StringBuffer sb = new StringBuffer();
    sb.append("from SalesReturnItem si where si.salesReturnId in (:ids) ");
    return  session.createQuery(sb.toString()).setParameterList("ids", ids);
  }
  public static Query getPurchaseReturnIds(Session session, Long shopId, int start, int size) {
    StringBuffer sb = new StringBuffer();
    sb.append("select pr.id from PurchaseReturn pr ");
    if (shopId != null) {
      sb.append(" where pr.shopId =:shopId");
    }
    Query q = session.createQuery(sb.toString()).setFirstResult(start).setMaxResults(size);
    if (shopId != null) {
      q.setLong("shopId", shopId);
    }
    return q;
  }

  public static Query getSalesReturnIds(Session session, Long shopId, int start, int size) {
    StringBuffer sb = new StringBuffer();
    sb.append("select sr.id from SalesReturn sr ");
    if (shopId != null) {
      sb.append(" where sr.shopId =:shopId");
    }
    Query q = session.createQuery(sb.toString()).setFirstResult(start).setMaxResults(size);
    if (shopId != null) {
      q.setLong("shopId", shopId);
    }
    return q;
  }

  public static Query getPurchaseReturnByNo(Session session, Long shopId, String no) {
    StringBuffer sb = new StringBuffer();
    sb.append("from PurchaseReturn pi where pi.shopId =:shopId and pi.no = :no");
    Query q = session.createQuery(sb.toString())
        .setLong("shopId", shopId)
        .setString("no", no);
    return q;
  }


  public static Query getPurchaseOrderItems(Session session, Long... ids) {
    StringBuffer sb = new StringBuffer();
    sb.append("from PurchaseOrderItem pi where pi.purchaseOrderId in (:ids) order by pi.purchaseOrderId asc");
    return session.createQuery(sb.toString()).setParameterList("ids", ids);
  }

  public static Query getPurchaseOrderIds(Session session, Long shopId, int start, int size) {
    StringBuffer sb = new StringBuffer();
    sb.append("select po.id from PurchaseOrder po ");
    if (shopId != null) {
      sb.append(" where po.shopId =:shopId");
    }
    sb.append(" order by po.id asc");
    Query q = session.createQuery(sb.toString()).setFirstResult(start).setMaxResults(size);
    if (shopId != null) {
      q.setLong("shopId", shopId);
    }
    return q;
  }

  public static Query getInventoryCheckItems(Session session, Long... orderId) {
   String sql="from InventoryCheckItem i where i.inventoryCheckId in (:orderId) ";
    return session.createQuery(sql).setParameterList("orderId", orderId);
  }

  public static Query getInventoryCheckItemByProductIds(Session session,Long shopId,Pager pager,Long... productIds) {
    String sql="from InventoryCheckItem i where  i.productId in (:productIds) order by created desc";
    Query query=session.createQuery(sql).setParameterList("productIds", productIds);
    query.setFirstResult(pager.getRowStart()).setMaxResults(pager.getPageSize());
    return query;
  }

  public static Query getInventoryCheckItemCountByProductIds(Session session,Long shopId, Long... productIds) {
    String sql="select count(i) from InventoryCheckItem i where  i.productId in (:productIds) ";
    return session.createQuery(sql).setParameterList("productIds", productIds);
  }

  public static Query getAllocateRecordItems(Session session, Long... orderId) {
    StringBuffer sb = new StringBuffer();
    sb.append("from AllocateRecordItem i where i.allocateRecordId in (:orderId) ");
    return session.createQuery(sb.toString()).setParameterList("orderId", orderId);
  }

  public static Query getSalesOrderItems(Session session, Long... ids) {
    StringBuffer sb = new StringBuffer();
    sb.append("from SalesOrderItem pi where pi.salesOrderId in (:ids) order by pi.salesOrderId asc");
    return session.createQuery(sb.toString()).setParameterList("ids", ids);
  }

  public static Query getSalesOrderIds(Session session, Long shopId, int start, int size) {
    StringBuffer sb = new StringBuffer();
    sb.append("select s.id from SalesOrder s ");
    if (shopId != null) {
      sb.append(" where s.shopId =:shopId");
    }
    sb.append(" order by s.id asc");
    Query q = session.createQuery(sb.toString()).setFirstResult(start).setMaxResults(size);
    if (shopId != null) {
      q.setLong("shopId", shopId);
    }
    return q;
  }
  public static Query getPreBuyOrderIds(Session session, Long shopId, int start, int size) {
    StringBuffer sb = new StringBuffer();
    sb.append("select p.id from PreBuyOrder p where p.deleted='FALSE'");
    if (shopId != null) {
      sb.append(" and p.shopId =:shopId");
    }
    Query q = session.createQuery(sb.toString()).setFirstResult(start).setMaxResults(size);
    if (shopId != null) {
      q.setLong("shopId", shopId);
    }
    return q;
  }
  public static Query getQuotedPreBuyOrderIds(Session session, Long shopId, int start, int size) {
    StringBuffer sb = new StringBuffer();
    sb.append("select p.id from QuotedPreBuyOrder p where p.deleted='FALSE'");
    if (shopId != null) {
      sb.append(" and p.shopId =:shopId");
    }
    Query q = session.createQuery(sb.toString()).setFirstResult(start).setMaxResults(size);
    if (shopId != null) {
      q.setLong("shopId", shopId);
    }
    return q;
  }

  public static Query getQuotedPreBuyOrders(Session session, Long shopId, int start, int size) {
    StringBuffer sb = new StringBuffer();
    sb.append("from QuotedPreBuyOrder p where p.deleted='FALSE'");
    if (shopId != null) {
      sb.append(" and p.shopId =:shopId");
    }
    Query q = session.createQuery(sb.toString()).setFirstResult(start).setMaxResults(size);
    if (shopId != null) {
      q.setLong("shopId", shopId);
    }
    return q;
  }


  public static Query getQuotedPreBuyOrdersByItemId(Session session, Long shopId, Long... quotedBuyOrderItemId) {
    StringBuffer sb = new StringBuffer();
    sb.append("select p from QuotedPreBuyOrder p,QuotedPreBuyOrderItem qi where p.deleted='FALSE' and p.id=qi.quotedPreBuyOrderId and qi.id in(:quotedBuyOrderItemId)");
    if (shopId != null) {
      sb.append(" and p.shopId =:shopId");
    }
    Query q = session.createQuery(sb.toString());
    if (shopId != null) {
      q.setLong("shopId", shopId);
    }
    q.setParameterList("quotedBuyOrderItemId",quotedBuyOrderItemId);
    return q;
  }
  public static Query getPreBuyOrderItems(Session session,Long... orderIds) {
    StringBuffer sb = new StringBuffer();
    sb.append("from PreBuyOrderItem pi where pi.preBuyOrderId in (:orderIds) ");
    Query q =session.createQuery(sb.toString()).setParameterList("orderIds", orderIds);
    return q;
  }
  public static Query getPreBuyOrder(Session session,Long... orderIds) {
    StringBuffer sb = new StringBuffer();
    sb.append("from PreBuyOrder p where p.id in (:orderIds) ");
    return session.createQuery(sb.toString()).setParameterList("orderIds", orderIds);
  }
  public static Query getQuotedPreBuyOrderItems(Session session,Long... orderIds) {
    StringBuffer sb = new StringBuffer();
    sb.append("from QuotedPreBuyOrderItem pi where pi.quotedPreBuyOrderId in (:orderIds) ");
    Query q =session.createQuery(sb.toString()).setParameterList("orderIds", orderIds);
    return q;
  }

  public static Query getMemberCardOrderItemsByShopIdAndArrayOrderId(Session session, Long shopId, Long... orderIds) {
    StringBuffer sb = new StringBuffer("from MemberCardOrderItem where memberCardOrderId in (:orderIds) ");
    if (shopId != null) {
      sb.append(" and shopId=:shopId");
    }
    Query query = session.createQuery(sb.toString()).setParameterList("orderIds", orderIds);
    if (shopId != null) {
      query.setLong("shopId", shopId);
    }
    return query;
  }

  public static Query getMemberCardOrderServicesByShopIdAndArrayOrderId(Session session, Long shopId, Long... orderIds) {
    StringBuffer sb = new StringBuffer("from MemberCardOrderService where memberCardOrderId in (:orderIds) ");
    if (shopId != null) {
      sb.append(" and shopId=:shopId");
    }
    Query query = session.createQuery(sb.toString()).setParameterList("orderIds", orderIds);
    if (shopId != null) {
      query.setLong("shopId", shopId);
    }
    return query;
  }

  public static Query getPurchaseReturnById(Session session, Long shopId, Long id) {
    return session.createQuery("from PurchaseReturn where id =:id and shopId =:shopId").setLong("id", id).setLong("shopId", shopId);

  }

  /**
   * 条件查询应付款总数
   *
   * @param session
   * @param supplierId 供应商ID
   * @param fromTime   开始时间
   * @param toTime     结束时间
   * @return
   * @author zhangchuanlong
   */
  public static Query countSearchPayable(Session session, Long shopId, Long supplierId, Long fromTime, Long toTime) {
    StringBuffer sb = new StringBuffer();
    sb.append("select count(*) from payable  p where p.shop_id=:shopId and p.order_type =:orderType");
    if (supplierId != null) {
      sb.append(" and p.supplier_id=:supplierId");
    }
    if (fromTime != null) {
      sb.append(" and p.pay_time>=:fromTime");
    }
    if (toTime != null) {
      sb.append(" and p.pay_time<=:toTime");
    }
    sb.append(" and p.status<>'REPEAL'");
    sb.append(" and p.credit_amount>0");
    Query q = session.createSQLQuery(sb.toString());
    q.setLong("shopId", shopId).setString("orderType",OrderTypes.INVENTORY.toString());
    if (supplierId != null) {
      q.setLong("supplierId", supplierId);
    }
    if (fromTime != null) {
      q.setLong("fromTime", fromTime);
    }
    if (toTime != null) {
      q.setLong("toTime", toTime);
    }
    return q;
  }

  /**
   * 分页查询应付款记录
   *
   * @param session
   * @param shopId
   * @param supplierId 供应商ID
   * @param fromTime   开始时间
   * @param toTime     结束时间
   * @param sort       排序
   * @param pager      分页
   * @return
   * @author zhangchuanlong
   */
  public static Query searchPayable(Session session, Long shopId, Long supplierId, Long fromTime, Long toTime, Sort sort, Pager pager) {
    StringBuffer sb = new StringBuffer();
    sb.append("select * from payable  p where p.shop_id=:shopId and p.order_type =:orderType");
    if (supplierId != null) {
      sb.append(" and p.supplier_id=:supplierId");
    }
    if (fromTime != null) {
      sb.append(" and p.pay_time>=:fromTime");
    }
    if (toTime != null) {
      sb.append(" and p.pay_time<=:toTime");
    }
    sb.append(" and p.status<>'REPEAL'");
    sb.append(" and p.credit_amount>0");
    if (sort != null) {
      sb.append(sort.toOrderString());
    }  else {
      sb.append(" order by p.pay_time desc");
    }
    Query q = session.createSQLQuery(sb.toString()).addEntity(Payable.class);
    q.setLong("shopId", shopId).setString("orderType",OrderTypes.INVENTORY.toString());
    if (supplierId != null) {
      q.setLong("supplierId", supplierId);
    }
    if (fromTime != null) {
      q.setLong("fromTime", fromTime);
    }
    if (toTime != null) {
      q.setLong("toTime", toTime);
    }
    return q.setFirstResult(pager.getRowStart()).setMaxResults(pager.getPageSize());
  }

  /**
   * 获取单个单据付款记录
   *
   * @param session
   * @param purchaseInventoryId 入库单ID
   * @param payableHistoryDTOid 付款历史ID
   * @return
   * @author zhangchuanlong
   */
  public static Query searchPayableHistoryRecord(Session session, Long purchaseInventoryId, Long payableHistoryDTOid, Long shopId) {
    StringBuffer hql = new StringBuffer("from PayableHistoryRecord p where p.shopId =:shopId and p.status is not null ");
    if (purchaseInventoryId == null || payableHistoryDTOid == null) return null;
    hql.append(" and p.purchaseInventoryId = :purchaseInventoryId and p.payableHistoryId=:payableHistoryId");
    return session.createQuery(hql.toString()).setLong("shopId", shopId).setLong("purchaseInventoryId", purchaseInventoryId).setLong("payableHistoryId", payableHistoryDTOid);
  }

  /**
   * 根据shopId查询总应付款额
   *
   * @param session
   * @param shopId
   * @return
   * @author zhangchuanlong
   */
  public static Query getTotalPayableByShopId(Session session, Long shopId) {
    return session.createSQLQuery("SELECT SUM(credit_amount) FROM payable WHERE shop_id = :shopId AND  status<>'REPEAL' AND order_type =:orderType")
        .setLong("shopId", shopId).setString("orderType",OrderTypes.INVENTORY.toString());
  }

  /**
   * 根据shopId获得店面总定金额
   *
   * @param session
   * @param shopId
   * @return
   */
  public static Query getTotaDepositByShopId(Session session, Long shopId) {
    return session.createSQLQuery("SELECT SUM(actually_paid) FROM deposit WHERE shop_id = :shopId")
        .setLong("shopId", shopId);
  }

  /**
   * 根据供应商ID获取每个供应商的总付款
   *
   * @param session
   * @param supplierId 供应商ID
   * @param shopId
   * @return
   */
  public static Query getSumPayableBySupplierId(Session session, Long supplierId, Long shopId,OrderDebtType debtType) {
    return session.createSQLQuery("SELECT SUM(credit_amount),SUM(paid_amount),count(*) FROM payable WHERE shop_id = :shopId AND supplier_id=:supplierId AND status<>'REPEAL' AND debt_type = :debtType")
        .setLong("shopId", shopId).setLong("supplierId", supplierId).setString("debtType",debtType.toString());
  }

    public static Query getSumReceivableByCustomerId(Session session, Long customerId, Long shopId,OrderDebtType debtType) {
        return session.createSQLQuery("SELECT SUM(debt) FROM receivable WHERE shop_id = :shopId AND customer_id=:customerId AND status_enum<>'REPEAL' AND debt_type = :debtType")
                .setLong("shopId", shopId).setLong("customerId", customerId).setString("debtType",debtType.toString());
    }

  /**
   * 根据供应商ID获取每个供应商的总付款
   *
   * @param session
   * @param supplierId 供应商ID
   * @param shopId
   * @return
   */
  public static Query getStatementOrderSumPayable(Session session, Long supplierId, Long shopId,OrderDebtType debtType) {
    return session.createSQLQuery("SELECT SUM(credit_amount),SUM(paid_amount),count(*) FROM payable WHERE shop_id = :shopId AND supplier_id=:supplierId AND status<>'REPEAL' AND debt_type = :debtType and order_type=:orderType ")
        .setLong("shopId", shopId).setLong("supplierId", supplierId).setString("debtType", debtType.toString()).setString("orderType", OrderTypes.SUPPLIER_STATEMENT_ACCOUNT.toString());
  }


  /**
   * 根据供应商ID获取每个供应商的总付款
   *
   * @param session
   * @param supplierIdList 供应商ID
   * @param shopId
   * @return
   */
  public static Query getSumPayableBySupplierIdList(Session session, List<Long> supplierIdList, Long shopId,OrderDebtType debtType) {
    StringBuilder sb = new StringBuilder("SELECT supplier_id,SUM(credit_amount),SUM(paid_amount) FROM payable WHERE supplier_id in(:supplierId) AND status<>'REPEAL' AND debt_type = :debtType ");
    if(shopId!=null){
      sb.append(" AND shop_id = :shopId ");
    }
    sb.append(" GROUP BY supplier_id");
    Query query = session.createSQLQuery(sb.toString()).setParameterList("supplierId", supplierIdList).setString("debtType",debtType.toString());
    if(shopId!=null){
      query.setLong("shopId", shopId);
    }
    return query;
  }
  /**
   * 根据供应商ID获取每个供应商的总付款
   *
   * @param session
   * @param supplierIdList 供应商ID
   * @param shopId
   * @return
   */
  public static Query getStatementOrderSumPayable(Session session, List<Long> supplierIdList, Long shopId,OrderDebtType debtType) {
    StringBuilder sb = new StringBuilder("SELECT supplier_id,SUM(credit_amount),SUM(paid_amount) FROM payable WHERE supplier_id in(:supplierId) AND status<>'REPEAL' AND debt_type = :debtType and order_type=:orderType ");
    if(shopId!=null){
      sb.append(" AND shop_id = :shopId ");
    }
    sb.append(" GROUP BY supplier_id");
    Query query = session.createSQLQuery(sb.toString()).setParameterList("supplierId", supplierIdList).setString("debtType",debtType.toString()).setString("orderType", OrderTypes.SUPPLIER_STATEMENT_ACCOUNT.toString());
    if(shopId!=null){
      query.setLong("shopId", shopId);
    }
    return query;
  }


  /**
   * 根据供应商ID获取每个供应商的总定金
   *
   * @param session
   * @param supplierId 供应商ID
   * @param shopId
   * @return
   */
  public static Query getSumDepositBySupplierId(Session session, Long supplierId, Long shopId) {
    Query q =  session.createSQLQuery("SELECT  SUM(actually_paid)  FROM deposit WHERE shop_id = :shopId AND supplier_id=:supplierId")
        .setLong("shopId", shopId).setLong("supplierId", supplierId);
    q.setCacheable(false);
    q.setCacheMode(CacheMode.REFRESH);
    return q;
  }

  /**
   * 根据shopId和supplierId查询供应商总付款记录
   *
   * @param session
   * @param shopId
   * @param supplierId
   * @return
   */
  public static Query getTotalCountOfPayable(Session session, Long shopId, Long supplierId) {
    StringBuffer sb = new StringBuffer();
    sb.append("select count(*) from payable  p where p.shop_id=:shopId and p.order_type =:orderType");
    if (supplierId != null) {
      sb.append(" and p.supplier_id=:supplierId");
    }
    sb.append(" and p.status<>'REPEAL'");
    sb.append(" and p.credit_amount>0");
    Query q = session.createSQLQuery(sb.toString());
    q.setLong("shopId", shopId).setString("orderType",OrderTypes.INVENTORY.toString());
    if (supplierId != null) {
      q.setLong("supplierId", supplierId);
    }
    return q;
  }

  /**
   * 查询供应商付款历史记录总记录数
   *
   * @param session
   * @param shopId
   * @param supplierId
   * @param startTime
   * @param endTime
   * @return
   */
  public static Query getTotalCountOfPayableHistoryRecord(Session session, Long shopId, Long supplierId, Long startTime, Long endTime) {
    StringBuffer sb = new StringBuffer();
    sb.append("select count(*) from payable_history_record  p where p.shop_id=:shopId " +
        "and ( payment_type_enum =:inventory or payment_type_enum =:inventoryReturn) ");
    if (supplierId != null) {
      sb.append(" and p.supplier_id=:supplierId");
    }
    if (startTime != null) {
      sb.append(" and p.created>=:startTime");
    }
    if (endTime != null) {
      sb.append(" and p.created<=:endTime");
    }
    Query q = session.createSQLQuery(sb.toString());
    q.setLong("shopId", shopId);
    if (supplierId != null) {
      q.setLong("supplierId", supplierId);
    }
    if (startTime != null) {
      q.setLong("startTime", startTime);
    }
    if (endTime != null) {
      q.setLong("endTime", endTime);
    }
    q.setString("inventory",PaymentTypes.INVENTORY.toString()).setString("inventoryReturn", PaymentTypes.INVENTORY_RETURN.toString());
    return q;
  }

  /**
   * 分页查询付款历史记录并排序
   *
   * @param session
   * @param shopId
   * @param supplierId
   * @param fromTimeLong
   * @param toTimeLong
   * @param sort
   * @param pager
   * @return
   */
  public static Query getPayableHistoryRecord(Session session, Long shopId, Long supplierId, Long fromTimeLong, Long toTimeLong, Sort sort, Pager pager) {
    StringBuffer sb = new StringBuffer();
    sb.append("select * from payable_history_record where shop_id=:shopId " +
        "and ( payment_type_enum =:inventory or payment_type_enum =:inventoryReturn) ");
    if (supplierId != null) {
      sb.append(" and supplier_id=:supplierId");
    }
    if (fromTimeLong != null) {
      sb.append(" and created>=:startTime");
    }
    if (toTimeLong != null) {
      sb.append(" and created<=:endTime");
    }
    if (sort != null) {
      sb.append(sort.toOrderString());
    } else {
      sb.append(" order by created desc");
    }
    Query q = session.createSQLQuery(sb.toString()).addEntity(PayableHistoryRecord.class);
    q.setLong("shopId", shopId);
    if (supplierId != null) {
      q.setLong("supplierId", supplierId);
    }
    if (fromTimeLong != null) {
      q.setLong("startTime", fromTimeLong);
    }
    if (toTimeLong != null) {
      q.setLong("endTime", toTimeLong);
    }
    q.setString("inventory",PaymentTypes.INVENTORY.toString()).setString("inventoryReturn", PaymentTypes.INVENTORY_RETURN.toString());
    return q.setFirstResult(pager.getRowStart()).setMaxResults(pager.getPageSize());
  }

  public static Query getAllPayableHistoryRecordBySupplierIds(Session session,Long shopId,Long[] supplierIds) {
    String hql="from PayableHistoryRecord where shopId =:shopId and supplierId in (:supplierIds)";
    return session.createQuery(hql).setLong("shopId",shopId).setParameterList("supplierIds",supplierIds);
  }

  public static Query getAllPayableHistoryBySupplierIds(Session session,Long shopId,Long[] supplierIds) {
    String hql="from PayableHistory where shopId =:shopId and supplierId in (:supplierIds)";
    return session.createQuery(hql).setLong("shopId",shopId).setParameterList("supplierIds",supplierIds);
  }

  public static Query getPayableHistoryRecord(Session session, Long shopId, Long supplierId, Long inventoryId,PaymentTypes paymentTypes) {

    StringBuffer hql = new StringBuffer("from PayableHistoryRecord where shopId = :shopId ");
    if (supplierId != null) {
      hql.append(" and supplierId=:supplierId ");
    }

    if (inventoryId != null) {
      hql.append(" and purchaseInventoryId=:inventoryId ");
    }
    if (paymentTypes != null) {
      hql.append(" and paymentType =:paymentTypes ");
    }

    Query q = session.createQuery(hql.toString()).setLong("shopId", shopId);
    if (supplierId != null) {
      q.setLong("supplierId", supplierId);
    }

    if (inventoryId != null) {
      q.setLong("inventoryId", inventoryId);
    }
    if (paymentTypes != null) {
      q.setParameter("paymentTypes", paymentTypes);
    }
    return q;
  }

  /**
   * 根据供应商ID 查询对应的定金
   *
   * @param session
   * @param shopId
   * @param supplierId
   * @return
   */
  public static Query getDepositBySupplierId(Session session, Long shopId, Long supplierId) {
    StringBuffer hql = new StringBuffer("from Deposit d where d.shopId=:shopId");
    if (supplierId != null) {
      hql.append(" and d.supplierId=:supplierId");
    }
    Query q = session.createQuery(hql.toString()).setLong("shopId", shopId);
    if (supplierId != null) {
      q.setLong("supplierId", supplierId);
    }
    return q;
  }

  public static Query getDepositsBySupplierIds(Session session, Long shopId,Long[] supplierIds) {
    StringBuffer hql = new StringBuffer("from Deposit d where d.shopId=:shopId and d.supplierId in(:supplierIds)");
    return session.createQuery(hql.toString()).setLong("shopId", shopId).setParameterList("supplierIds",supplierIds);
  }

  /**
   *      根据 shopId,purchaseInventoryID,supplierID查询应付款
   *
   * @param session
   * @param shopId           店面ID
   * @param purchaseInventoryId  入库单ID
   * @param supplierId                  供应商ID
   * @return
   */
  public static Query getInventoryPayable(Session session, Long shopId, Long purchaseInventoryId, Long supplierId) {
    StringBuffer hql = new StringBuffer("from Payable p where p.shopId=:shopId and p.orderType =:orderType");
    if (purchaseInventoryId != null) {
      hql.append(" and p.purchaseInventoryId=:purchaseInventoryId");
    }
    if (supplierId != null) {
      hql.append(" and p.supplierId=:supplierId");
    }
    Query q = session.createQuery(hql.toString()).setLong("shopId", shopId)
        .setString("orderType",OrderTypes.INVENTORY.toString());
    if (supplierId != null) {
      q.setLong("supplierId", supplierId);
    }
    if (purchaseInventoryId != null) {
      q.setLong("purchaseInventoryId", purchaseInventoryId);
    }
    return q;
  }

  /**
   *  根据shopiD,供应商ID，入库单ID 查找对应的付款历史记录
   *
   * @param session
   * @param shopId
   * @param supplierId
   * @param purchaseInventoryId
   * @return
   */
  public static Query getPayHistoryRecordForRepeal(Session session, Long shopId, Long supplierId, Long purchaseInventoryId) {
    StringBuffer hql = new StringBuffer("from PayableHistoryRecord p where p.shopId=:shopId and p.status is not null ");
    if (purchaseInventoryId != null) {
      hql.append(" and p.purchaseInventoryId=:purchaseInventoryId");
    }
    if (supplierId != null) {
      hql.append(" and p.supplierId=:supplierId order by p.creationDate desc ");
    }
    Query q = session.createQuery(hql.toString()).setLong("shopId", shopId);
    if (supplierId != null) {
      q.setLong("supplierId", supplierId);
    }
    if (purchaseInventoryId != null) {
      q.setLong("purchaseInventoryId", purchaseInventoryId);
    }
    return q;
  }

  /**
   * 查询所有入库但进行应付款数据初始化
   *
   * @param session
   * @return
   */
  public static Query getPurchaseInventory(Session session) {
    StringBuffer sb = new StringBuffer();
    sb.append("from PurchaseInventory");
    Query query = session.createQuery(sb.toString());
    return query;
  }


  public static Query getDraftOrderById(Session session, Long shopId, Long draftOrderId) {
    return session.createQuery("from DraftOrder where shopId=:shopId and id =:draftOrderId ").setLong("shopId", shopId).setLong("draftOrderId", draftOrderId);
  }

  public static Query getDraftOrdersByCustomerOrSupplierId(Session session, Long shopId, Long customerId) {
    return session.createQuery("from DraftOrder where shopId=:shopId and customerOrSupplierId =:customerId ").
        setLong("shopId", shopId).setLong("customerId", customerId);
  }

  public static Query getDraftOrderByVehicle(Session session,Long shopId,String vehicle) {
    return session.createQuery("from DraftOrder where shopId=:shopId and status='DRAFT_SAVED' and vehicle =:vehicle ").setLong("shopId", shopId).setString("vehicle",vehicle);
  }

  public static Query getDraftOrderByTxnOrderId(Session session,Long shopId,Long txnOrderId) {
    return session.createQuery("from DraftOrder where shopId=:shopId and status='DRAFT_SAVED' and txnOrderId =:txnOrderId ").setLong("shopId", shopId).setLong("txnOrderId",txnOrderId);
  }

  public static Query getItemsByDraftOrderId(Session session, Long draftOrderId) {
    return session.createQuery("from DraftOrderItem where draftOrderId =:draftOrderId ").setLong("draftOrderId", draftOrderId);
  }

  public static Query deleteDraftOrderItemsByDraftOrderId(Session session, Long shopId, Long draftOrderId) {
    return session.createQuery("delete from DraftOrderItem where draftOrderId =:draftOrderId and shopId =:shopId ").setLong("draftOrderId", draftOrderId).setLong("shopId", shopId);
  }

  public static Query countDraftOrderByOrderType(Session session, Long shopId, Long userId, String[] orderTypes, Long startTime, Long endTime) {
    StringBuffer sb = new StringBuffer("select order_type_enum,count(order_type_enum) from draft_order where status='DRAFT_SAVED' and shop_id =:shopId  and user_id=:userId and order_type_enum in (:orderTypes) ");
    if (startTime != null) {
      sb.append(" and save_time >= :startTime");
    }
    if (endTime != null) {
      sb.append(" and save_time <=:endTime");
    }
    sb.append(" group by order_type_enum");
    Query query = session.createSQLQuery(sb.toString()).setLong("shopId", shopId).setLong("userId", userId).setParameterList("orderTypes", orderTypes);
    if (startTime != null) {
      query.setLong("startTime", startTime);
    }
    if (endTime != null) {
      query.setLong("endTime", endTime);
    }
    return query;
  }

  public static Query countDraftOrders(Session session, Long shopId, Long userId, Long vehicleId, String[] orderTypes, Long startTime, Long endTime) {
    StringBuffer sb = new StringBuffer("select count(*) from draft_order where status='DRAFT_SAVED' and shop_id =:shopId and user_id=:userId and order_type_enum in (:orderTypes) ");
    if(vehicleId != null){
      sb.append(" and vechicle_id = :vehicleId");
    }
    if (startTime != null) {
      sb.append(" and save_time >= :startTime");
    }
    if (endTime != null) {
      sb.append(" and save_time <=:endTime");
    }
    sb.append(" order by save_time desc");
    Query query = session.createSQLQuery(sb.toString()).setLong("shopId", shopId).setLong("userId", userId).setParameterList("orderTypes", orderTypes);
    if(vehicleId != null){
      query.setLong("vehicleId", vehicleId);
    }
    if (startTime != null) {
      query.setLong("startTime", startTime);
    }
    if (endTime != null) {
      query.setLong("endTime", endTime);
    }
    return query;
  }

  public static Query getDraftOrders(Session session, Long shopId, Long userId, Long vehicleId, Pager pager, String[] orderTypes, Long startTime, Long endTime) {
    StringBuffer sb = new StringBuffer("select * from draft_order where status='DRAFT_SAVED' and shop_id =:shopId and user_id=:userId and order_type_enum in (:orderTypes)");
    if(vehicleId != null){
      sb.append(" and vechicle_id = :vehicleId");
    }
    if (startTime != null) {
      sb.append(" and save_time >= :startTime");
    }
    if (endTime != null) {
      sb.append(" and save_time <= :endTime");
    }
    sb.append(" order by save_time desc");
    Query query = session.createSQLQuery(sb.toString()).addEntity(DraftOrder.class).setLong("shopId", shopId).setLong("userId", userId).setParameterList("orderTypes", orderTypes);
    if(vehicleId != null){
      query.setLong("vehicleId", vehicleId);
    }
    if (startTime != null) {
      query.setLong("startTime", startTime);
    }
    if (endTime != null) {
      query.setLong("endTime", endTime);
    }
    return query.setFirstResult((pager.getCurrentPage() - 1) * pager.getPageSize()).setMaxResults(pager.getPageSize());
  }


  public static Query getSupplierRecord(Session session, Long shopId, Long supplierId) {
    return session.createQuery("from SupplierRecord where shopId = :shopId and supplierId = :supplierId")
        .setLong("shopId", shopId).setLong("supplierId", supplierId);
  }

  public static Query getDepositForReindex(Session session, Long shopId, List<Long> ids) {
    StringBuffer sb = new StringBuffer("from Deposit");
    sb.append(" where supplierId in :ids");
    if (shopId != null) {
      sb.append(" and shopId=:shopId");
    }
    Query query = session.createQuery(sb.toString()).setParameterList("ids", ids);
    if (shopId != null) {
      query.setLong("shopId", shopId);
    }
    return query;
  }

  public static Query getSupplierRecordForReindex(Session session, Long shopId, List<Long> supplierIds) {
    StringBuffer sb = new StringBuffer("from SupplierRecord");
    sb.append(" where supplierId in :supplierIds");
    if (shopId != null) {
      sb.append(" and shopId=:shopId");
    }
    Query query = session.createQuery(sb.toString()).setParameterList("supplierIds", supplierIds);
    if (shopId != null) {
      query.setLong("shopId", shopId);
    }
    return query;
  }

  public static Query getInventoryByIds(Session session, Long shopId, Long... productIds) {
    return session.createQuery("from Inventory i where i.shopId =:shopId and i.id in(:productIds)")
        .setLong("shopId", shopId).setParameterList("productIds", productIds);
  }

  public static Query getBusinessStatChangeOfDay(Session session, Long shopId, long year, long month, long day) {
    return session.createQuery("from BusinessStatChange r where r.shopId=:shopId and r.statYear=:year and r.statMonth =:month and r.statDay =:day order by r.statTime desc ")
        .setLong("shopId", shopId).setLong("year", year).setLong("month", month).setLong("day", day);

  }

  public static Query sumBusinessStatChangeForMonth(Session session, Long shopId, long year, long month) {
    return session.createQuery("select  sum(r.statSum),sum(r.sales),sum(r.service),sum(r.productCost),sum(r.otherIncome),sum(r.rentExpenditure),sum(r.utilitiesExpenditure),sum(r.salaryExpenditure),sum(r.otherExpenditure),sum(r.wash)" +
        " from BusinessStatChange r where r.shopId=:shopId and r.statYear=:year and r.statMonth =:month")
        .setLong("shopId", shopId).setLong("year", year).setLong("month", month);
  }

  public static Query sumBusinessStatChangeForYear(Session session, Long shopId, long year) {
    return session.createQuery("select  sum(r.statSum),sum(r.sales),sum(r.service),sum(r.productCost),sum(r.otherIncome),sum(r.rentExpenditure),sum(r.utilitiesExpenditure),sum(r.salaryExpenditure),sum(r.otherExpenditure),sum(r.wash) " +
        " from BusinessStatChange r where r.shopId=:shopId and r.statYear=:year")
        .setLong("shopId", shopId).setLong("year", year);
  }

  public static Query getDayBusinessStatChange(Session session, Long shopId, long year, long month) {
    return session.createQuery("from BusinessStatChange r where r.shopId=:shopId and r.statYear=:year and r.statMonth =:month order by r.statTime desc ")
        .setLong("shopId", shopId).setLong("year", year).setLong("month", month);
  }

  public static Query getMonthBusinessStatChange(Session session, Long shopId, long year) {
    return session.createQuery("select r.statMonth,sum(r.statSum) from BusinessStatChange r where r.shopId=:shopId and r.statYear=:year group by r.statMonth ")
        .setLong("shopId", shopId).setLong("year", year);
  }

  public static Query getBusinessStatChangeByYearMonth(Session session, Long shopId,Long[] year, Long[] month) {
    return session.createQuery("select r.statYear,r.statMonth,sum(r.statSum) from BusinessStatChange r where r.shopId=:shopId and r.statYear in(:year) and r.statMonth in(:month) group by r.statYear,r.statMonth ")
        .setLong("shopId", shopId).setParameterList("year", year).setParameterList("month",month);
  }
  public static Query getBusinessStatChangeByYearMonthDay(Session session, Long shopId,Long[] year, Long[] month,Long[] day) {
    return session.createQuery("select r.statYear,r.statMonth,r.statDay,sum(r.statSum) from BusinessStatChange r where r.shopId=:shopId and r.statYear in(:year) and r.statMonth in(:month) and r.statDay in(:day) group by r.statYear,r.statMonth,r.statDay ")
        .setLong("shopId", shopId).setParameterList("year", year).setParameterList("month",month).setParameterList("day",day);
  }
  public static Query getBusinessStatMapByYearMonth(Session session, Long shopId,String... yearMonth) {
    StringBuffer sb = new StringBuffer("select b.statYear,b.statMonth,b.statSum from BusinessStat b where b.shopId=:shopId and (b.statYear,b.statMonth,b.statTime) in ");
    sb.append("(select r.statYear,r.statMonth,max(r.statTime) from BusinessStat r where r.shopId=:shopId ");
    if(yearMonth!=null && yearMonth.length>0){
      sb.append(" and( ");
      String[] temp = null;
      for (int i = 0; i < yearMonth.length; i++) {
        temp = yearMonth[i].split("-");
        sb.append(" ( r.statYear = ").append(temp[0]).append(" and r.statMonth = ").append(Integer.valueOf(temp[1])).append(" ) ");
        if (i < yearMonth.length-1) {
          sb.append(" or ");
        }
      }
      sb.append(" ) group by r.statYear,r.statMonth)");
    }
    sb.append(")");
    return session.createQuery(sb.toString()).setLong("shopId", shopId);
  }
  public static Query getBusinessStatByYearMonthDay(Session session, Long shopId,String yearMonthDayStart,String yearMonthDayEnd) {
    StringBuffer sb = new StringBuffer("select r.statYear,r.statMonth,r.statDay,r.statSum from BusinessStat r where r.shopId=:shopId ");
    String[] startDate = yearMonthDayStart.split("-");
    String[] endDate = yearMonthDayEnd.split("-");
    sb.append(" and (");
    sb.append(" (r.statYear=").append(startDate[0]).append(" and r.statMonth=").append(startDate[1]).append(" and r.statDay>=").append(startDate[2]).append(")");
    sb.append(" or ");
    sb.append(" (r.statYear=").append(endDate[0]).append(" and r.statMonth=").append(endDate[1]).append(" and r.statDay<=").append(endDate[2]).append(")");
    sb.append(" )");
    return session.createQuery(sb.toString()).setLong("shopId", shopId);
  }
  public static Query getReceptionRecordByOrderId(Session session, long shopId, long orderId,OrderTypes orderTypes) {
    StringBuffer sb = new StringBuffer();
    sb.append(" from ReceptionRecord where shopId = :shopId " +
        " and orderId is not null and orderId =:orderId ");
    if (orderTypes != null) {
      sb.append(" and orderTypeEnum=:orderType");
    }
    sb.append(" order by recordNum desc ");

    Query query = session.createQuery(sb.toString());
    query.setLong("shopId", shopId).setLong("orderId", orderId);
    if (orderTypes != null) {
      query.setParameter("orderType", orderTypes);
    }
    return query;

  }


  public static Query countPayHistoryRecordByPayTime(Session session, long shopId, long startTime, long endTime) {
    StringBuffer sb = new StringBuffer();
    sb.append(" select count(*) from PayableHistoryRecord where shopId = :shopId " +
        "and payTime >=:startTime and payTime < :endTime and paymentType is not null and dayType != :dayType and dayType != :statementDayType ");
    return session.createQuery(sb.toString())
        .setLong("shopId", shopId)
        .setLong("startTime", startTime)
        .setLong("endTime", endTime)
        .setParameter("dayType",DayType.TODAY).setParameter("statementDayType",DayType.STATEMENT_ACCOUNT);
  }


  public static Query getPayHistoryRecordByPayTime(Session session, long shopId, long startTime, long endTime, Pager pager) {
    StringBuffer sb = new StringBuffer();
    sb.append(" from PayableHistoryRecord where shopId = :shopId " +
        "and payTime >=:startTime and payTime < :endTime and paymentType is not null and dayType != :dayType and dayType != :statementDayType  ");
    return session.createQuery(sb.toString())
        .setLong("shopId", shopId)
        .setLong("startTime", startTime)
        .setLong("endTime", endTime)
        .setParameter("dayType",DayType.TODAY).setParameter("statementDayType",DayType.STATEMENT_ACCOUNT)
        .setFirstResult(pager.getRowStart())
        .setMaxResults(pager.getPageSize());

  }

  public static Query getDepositDTOListBySHopId(Session session, long shopId, long startTime, long endTime) {
    StringBuffer sb = new StringBuffer();
    sb.append(" select * from deposit where shop_id = :shopId and created >=:startTime and created < :endTime ");
    return session.createSQLQuery(sb.toString()).addEntity(Deposit.class)
        .setLong("shopId", shopId)
        .setLong("startTime", startTime)
        .setLong("endTime", endTime);
  }

  public static Query sumDebtByIds(Session session, Long shopId, List<Long> ids) {
    return session.createQuery("select sum(d.debt) from Receivable d where d.shopId=:shopId and d.id in :ids and d.statusEnum =:status")
        .setLong("shopId", shopId).setParameterList("ids", ids).setString("status",ReceivableStatus.FINISH.toString());
  }


  public static Query getSupplierReturnPayableByPurchaseReturnId(Session session, Long shopId, Long... purchaseReturnId) {
    StringBuffer sb = new StringBuffer("from SupplierReturnPayable where purchaseReturnId in(:purchaseReturnId) ");
    if (shopId != null) {
      sb.append(" and shopId=:shopId ");
    }
    Query query = session.createQuery(sb.toString()).setParameterList("purchaseReturnId", purchaseReturnId);
    if (shopId != null) {
      query.setLong("shopId", shopId);
    }
    return query;
  }

  public static Query getAllSupplierReturnPayableBySupplierIds(Session session, Long shopId,Long[] supplierIds) {
    String hql ="from SupplierReturnPayable where shopId=:shopId and supplierId in(:supplierIds) ";
    return session.createQuery(hql).setLong("shopId",shopId).setParameterList("supplierIds", supplierIds);
  }

  public static Query getPayableByPurchaseInventoryId(Session session, Long shopId, Long... purchaseInventoryId) {
    StringBuffer sb = new StringBuffer("from Payable where purchaseInventoryId in(:purchaseInventoryId) ");
    if (shopId != null) {
      sb.append(" and shopId=:shopId ");
    }
    Query query = session.createQuery(sb.toString()).setParameterList("purchaseInventoryId", purchaseInventoryId);
    if (shopId != null) {
      query.setLong("shopId", shopId);
    }
    return query;
  }

  public static Query getLastOrderReceiptNo(Session session, Long shopId, OrderTypes types, String receiptNoNotNo) {
    String sql = "";
    if (OrderTypes.SALE == types) {
      sql = "SELECT so.receipt_no FROM sales_order so WHERE so.shop_id = :shopId AND so.receipt_no IS NOT NULL AND so.receipt_no LIKE :receiptNoNotNo ORDER BY so.receipt_no DESC";
    } else if (OrderTypes.PURCHASE == types) {
      sql = "SELECT po.receipt_no FROM purchase_order po WHERE po.shop_id = :shopId AND po.receipt_no IS NOT NULL AND po.receipt_no LIKE :receiptNoNotNo ORDER BY po.receipt_no DESC";
    } else if (OrderTypes.INVENTORY == types) {
      sql = "SELECT pi.receipt_no FROM purchase_inventory pi WHERE pi.shop_id = :shopId AND pi.receipt_no IS NOT NULL AND pi.receipt_no LIKE :receiptNoNotNo ORDER BY pi.receipt_no DESC";
    } else if (OrderTypes.REPAIR == types) {
      sql = "SELECT ro.receipt_no FROM repair_order ro WHERE ro.shop_id = :shopId AND ro.receipt_no IS NOT NULL AND ro.receipt_no LIKE :receiptNoNotNo ORDER BY ro.receipt_no DESC";
    } else if (OrderTypes.WASH_BEAUTY == types) {
      sql = "SELECT wbo.receipt_no FROM wash_beauty_order wbo WHERE wbo.shop_id = :shopId AND wbo.receipt_no IS NOT NULL AND wbo.receipt_no LIKE :receiptNoNotNo ORDER BY wbo.receipt_no DESC";
    } else if (OrderTypes.RETURN == types) {
      sql = "SELECT pr.receipt_no FROM purchase_return pr WHERE pr.shop_id = :shopId AND pr.receipt_no IS NOT NULL AND pr.receipt_no LIKE :receiptNoNotNo ORDER BY pr.receipt_no DESC";
    }
    return session.createSQLQuery(sql).addScalar("receipt_no", StandardBasicTypes.STRING)
        .setLong("shopId", shopId).setString("receiptNoNotNo", receiptNoNotNo + "%");
  }

  public static Query getOrderDTONoReceiptNo(Session session, OrderTypes types, int num, int pageNo) {
    String sql = "";
    if (OrderTypes.SALE == types) {
      sql = "SELECT * FROM sales_order so ORDER BY so.created ASC ";
      return session.createSQLQuery(sql).addEntity(SalesOrder.class).setFirstResult(pageNo * num).setMaxResults(num);
    } else if (OrderTypes.PURCHASE == types) {
      sql = "SELECT * FROM purchase_order so ORDER BY so.created ASC";
      return session.createSQLQuery(sql).addEntity(PurchaseOrder.class).setFirstResult(pageNo * num).setMaxResults(num);
    } else if (OrderTypes.INVENTORY == types) {
      sql = "SELECT * FROM purchase_inventory so ORDER BY so.created ASC";
      return session.createSQLQuery(sql).addEntity(PurchaseInventory.class).setFirstResult(pageNo * num).setMaxResults(num);
    } else if (OrderTypes.REPAIR == types) {
      sql = "SELECT * FROM repair_order so ORDER BY so.created ASC";
      return session.createSQLQuery(sql).addEntity(RepairOrder.class).setFirstResult(pageNo * num).setMaxResults(num);
    } else if (OrderTypes.WASH_BEAUTY == types) {
      sql = "SELECT * FROM wash_beauty_order so ORDER BY so.created ASC";
      return session.createSQLQuery(sql).addEntity(WashBeautyOrder.class).setFirstResult(pageNo * num).setMaxResults(num);
    } else if (OrderTypes.RETURN == types) {
      sql = "SELECT * FROM purchase_return so ORDER BY so.created ASC";
      return session.createSQLQuery(sql).addEntity(PurchaseReturn.class).setFirstResult(pageNo * num).setMaxResults(num);
    }
    return null;
  }

  public static Query countOrderNoReceiptNo(Session session, Long shopId, OrderTypes types) {
    if (OrderTypes.SALE == types) {
      if (null == shopId) {
        return session.createSQLQuery("SELECT COUNT(*) FROM sales_order");
      } else {
        return session.createSQLQuery("SELECT COUNT(*) FROM sales_order s WHERE s.shop_id=:shopId")
            .setLong("shopId", shopId);
      }
    } else if (OrderTypes.PURCHASE == types) {
      if (null == shopId) {
        return session.createSQLQuery("SELECT COUNT(*) FROM purchase_order");
      } else {
        return session.createSQLQuery("SELECT COUNT(*) FROM purchase_order s WHERE s.shop_id=:shopId")
            .setLong("shopId", shopId);
      }
    } else if (OrderTypes.INVENTORY == types) {
      if (null == shopId) {
        return session.createSQLQuery("SELECT COUNT(*) FROM purchase_inventory");
      } else {
        return session.createSQLQuery("SELECT COUNT(*) FROM purchase_inventory s WHERE s.shop_id=:shopId")
            .setLong("shopId", shopId);
      }
    } else if (OrderTypes.REPAIR == types) {
      if (null == shopId) {
        return session.createSQLQuery("SELECT COUNT(*) FROM repair_order");
      } else {
        return session.createSQLQuery("SELECT COUNT(*) FROM repair_order s WHERE s.shop_id=:shopId")
            .setLong("shopId", shopId);
      }
    } else if (OrderTypes.WASH_BEAUTY == types) {
      if (null == shopId) {
        return session.createSQLQuery("SELECT COUNT(*) FROM wash_beauty_order");
      } else {
        return session.createSQLQuery("SELECT COUNT(*) FROM wash_beauty_order s WHERE s.shop_id=:shopId")
            .setLong("shopId", shopId);
      }
    } else if (OrderTypes.RETURN == types) {
      if (null == shopId) {
        return session.createSQLQuery("SELECT COUNT(*) FROM purchase_return");
      } else {
        return session.createSQLQuery("SELECT COUNT(*) FROM purchase_return s WHERE s.shop_id=:shopId")
            .setLong("shopId", shopId);
      }
    }
    return null;
  }

  public static Query getReceiptNOByShopIdAndType(Session session, Long shopId, OrderTypes types) {
    return session.createSQLQuery("SELECT * FROM receipt_no pn WHERE pn.shop_id = :shopId AND pn.type = :types")
        .addEntity(ReceiptNo.class).setLong("shopId", shopId).setString("types", types.toString());
  }

  public static Query getTxnOrderIdByReceiptNo(Session session,ReceiptNoDTO receiptNoDTO){
    String sql = "";
    OrderTypes types=receiptNoDTO.getTypes();
    if(OrderTypes.SALE == types) {
      sql = "SELECT so.id FROM sales_order so WHERE shop_id=:shopId AND receiptNO=:receiptNo ";
      return session.createSQLQuery(sql).addEntity(SalesOrder.class);
    }else if(OrderTypes.PURCHASE ==types){
      sql = "SELECT so.id FROM purchase_order so WHERE shop_id=:shopId AND receiptNO=:receiptNo ";
      return session.createSQLQuery(sql).addEntity(PurchaseOrder.class).setLong("shopId",receiptNoDTO.getShopId()).setString("receiptNo",receiptNoDTO.getReceiptNo());
    }else if(OrderTypes.INVENTORY == types){
      sql = "SELECT so.id FROM purchase_inventory so WHERE shop_id=:shopId AND receiptNO=:receiptNo ";
      return session.createSQLQuery(sql).addEntity(PurchaseInventory.class).setLong("shopId",receiptNoDTO.getShopId()).setString("receiptNo",receiptNoDTO.getReceiptNo());
    }else if(OrderTypes.REPAIR == types){
      sql = "SELECT so.id FROM repair_order so WHERE shop_id=:shopId AND receiptNO=:receiptNo ";
      return session.createSQLQuery(sql).addEntity(RepairOrder.class).setLong("shopId",receiptNoDTO.getShopId()).setString("receiptNo",receiptNoDTO.getReceiptNo());
    }else if(OrderTypes.WASH_BEAUTY == types){
      sql = "SELECT so.id FROM wash_beauty_order so WHERE shop_id=:shopId AND receiptNO=:receiptNo ";
      return session.createSQLQuery(sql).addEntity(WashBeautyOrder.class).setLong("shopId",receiptNoDTO.getShopId()).setString("receiptNo",receiptNoDTO.getReceiptNo());
    }else if(OrderTypes.RETURN == types){
      sql = "SELECT so.id FROM purchase_return so WHERE shop_id=:shopId AND receiptNO=:receiptNo ";
      return session.createSQLQuery(sql).addEntity(PurchaseReturn.class).setLong("shopId",receiptNoDTO.getShopId()).setString("receiptNo",receiptNoDTO.getReceiptNo());
    }
    return null;
  }

  public static Query getRunningStatChangeDTOByShopIdYearMonthDay(Session session, long shopId, long statYear, long statMonth, long statDay) {
    StringBuilder sb = new StringBuilder(" select * from running_stat_change where shop_id=:shopId and stat_year=:statYear ");
    sb.append(" and stat_month =:statMonth ");
    sb.append(" and stat_day =:statDay ");
    return session.createSQLQuery(sb.toString()).addEntity(RunningStatChange.class)
        .setLong("shopId", shopId)
        .setLong("statYear", statYear)
        .setLong("statMonth", statMonth)
        .setLong("statDay", statDay);
  }

  public static Query getRunningStatChangeByYearMonth(Session session, Long shopId, Long year, Long month) {

    StringBuffer hql = new StringBuffer();
    hql.append(" from RunningStatChange r where r.shopId=:shopId ");
    if (year != null) {
      hql.append(" and r.statYear=:year ");
    }
    if (month != null) {
      hql.append(" and r.statMonth =:month ");
    }
    Query q = session.createQuery(hql.toString()).setLong("shopId", shopId);
    if (year != null) {
      q.setLong("year", year);
    }
    if (month != null) {
      q.setLong("month", month);
    }
    return q;
  }


  public static Query sumRunningStatChangeForYearMonth(Session session, Long shopId, Long year, Long month) {

    StringBuffer hql = new StringBuffer();
    hql.append(" from RunningStatChange r where r.shopId=:shopId ");
    if (year != null) {
      hql.append(" and r.statYear=:year ");
    }
    if (month != null) {
      hql.append(" and r.statMonth =:month ");
    }
    Query q = session.createQuery(hql.toString()).setLong("shopId", shopId);
    if (year != null) {
      q.setLong("year", year);
    }
    if (month != null) {
      q.setLong("month", month);
    }
    return q;
  }


  public static Query getMonthRunningStatChangeMap(Session session, Long shopId, long year) {
    return session.createQuery("select r.statMonth,sum(r.cashIncome),sum(r.chequeIncome),sum(r.unionPayIncome), " +
        " sum(r.cashExpenditure),sum(r.chequeExpenditure),sum(r.unionPayExpenditure)," +
        " sum(r.memberPayIncome),sum(r.debtNewIncome),sum(r.debtWithdrawalIncome),sum(r.depositPayIncome)," +
        " sum(r.customerDepositExpenditure),sum(r.debtNewExpenditure),sum(r.debtWithdrawalExpenditure)," +
        " sum(r.depositPayExpenditure),SUM(COALESCE(r.couponIncome,0)),SUM(COALESCE(r.couponExpenditure,0)) " +
        " from RunningStatChange r where r.shopId=:shopId and r.statYear=:year group by r.statMonth ")
        .setLong("shopId", shopId).setLong("year", year);
  }


  public static Query getRepairOrderTemplateByShopIdAndTemplateNameAndStatus(Session session, Long shopId, String templateName, RepairOrderTemplateStatus status) {

    String hql = new String("from RepairOrderTemplate template where template.shopId =:shopId and template.templateName =:templateName and template.status =:status");
    Query query = session.createQuery(hql);
    return query.setLong("shopId", shopId)
        .setString("templateName", templateName)
        .setParameter("status", status);
  }

  public static Query getRepairOrderTemplateServicesByRepairOrderTemplateId(Session session, Long repairOrderTemplateId) {
    return session.createQuery("from RepairOrderTemplateService r where r.repairOrderTemplateId=:repairOrderTemplateId")
        .setLong("repairOrderTemplateId", repairOrderTemplateId);
  }



  public static Query getRepairOrderTemplateItemsByRepairOrderTemplateId(Session session, Long repairOrderTemplateId) {
    return session.createQuery("from RepairOrderTemplateItem r where r.repairOrderTemplateId=:repairOrderTemplateId")
        .setLong("repairOrderTemplateId", repairOrderTemplateId);
  }


  public static Query getRepairOrderTemplateByShopIdAndStatus(Session session, Long shopId, RepairOrderTemplateStatus status) {
    String hql = new String("from RepairOrderTemplate template where template.shopId =:shopId  and template.status =:status order by template.usageCounter,last_update desc");
    Query query = session.createQuery(hql);
    return query.setLong("shopId", shopId)
        .setParameter("status", status);
  }


  public static Query getTop5RepairOrderTemplateByShopId(Session session, Long shopId) {
    String hql = new String("from RepairOrderTemplate template where template.shopId =:shopId  and template.status =:status order by template.usageCounter,last_update desc");
    Query query = session.createQuery(hql);
    return query.setLong("shopId", shopId)
        .setParameter("status", RepairOrderTemplateStatus.ENABLED)
        .setFirstResult(0)
        .setMaxResults(5);
  }

  public static Query getBusinessChangeInfoToPrint(Session session,Long shopId,Long startTime,Long endTime)
  {
    endTime = endTime+86399000L;
    StringBuffer sb = new StringBuffer();
    sb.append(" select sum(other_income),sum(rent_expenditure),sum(salary_expenditure),sum(utilities_expenditure),sum(other_expenditure)");
    sb.append(",sum(sales),sum(wash),sum(service),sum(product_cost),sum(stat_sum) ");
    sb.append("from business_stat_change where shop_id=:shopId and stat_time>=:startTime and stat_time <= :endTime");

    return session.createSQLQuery(sb.toString()).setLong("shopId",shopId).setLong("startTime",startTime).setLong("endTime",endTime);
  }


  public static Query countRepairOrderOfNotSettled(Session session,Long shopId,Long customerId)
  {
    List<OrderStatus> list = new ArrayList<OrderStatus>();
    list.add(OrderStatus.REPAIR_SETTLED);
    list.add(OrderStatus.REPAIR_REPEAL);

    return session.createQuery("select count(*) from RepairOrder ro where ro.shopId =:shopId and ro.customerId=:customerId and ro.statusEnum not in (:statusEnum)")
        .setLong("shopId",shopId).setLong("customerId",customerId).setParameterList("statusEnum",list);
  }


  public static Query getDraftOrder(Session session,Long shopId,List<OrderTypes> orderTypesList,Long customerId)
  {

    return session.createQuery("from DraftOrder do where do.shopId=:shopId and do.customerOrSupplierId = :customerId and do.orderTypeEnum in (:orderTypesList) and do.status =:status")
        .setLong("shopId",shopId).setLong("customerId",customerId).setParameterList("orderTypesList",orderTypesList).setParameter("status",DraftOrderStatus.DRAFT_SAVED);
  }

  public static Query getRepairOrderReceiptNoOfNotSettled(Session session,Long shopId,Long customerId)
  {
    List<OrderStatus> list = new ArrayList<OrderStatus>();
    list.add(OrderStatus.REPAIR_SETTLED);
    list.add(OrderStatus.REPAIR_REPEAL);
    return session.createQuery("select ro from RepairOrder ro where ro.shopId =:shopId and ro.customerId=:customerId and ro.statusEnum not in (:statusEnum)")
        .setLong("shopId",shopId).setLong("customerId",customerId).setParameterList("statusEnum",list);
  }

  public static Query getCategoryByServiceId(Session session, Long shopId,Long... serviceId) {

    if(null== shopId)
    {
      return session.createQuery("select cir.serviceId,s from Category s,CategoryItemRelation cir where " +
          "s.id =cir.categoryId and  cir.serviceId in(:serviceId)")
          .setParameterList("serviceId",serviceId);
    }
    else
    {
      return session.createQuery("select cir.serviceId,s from Category s,CategoryItemRelation cir where (s.shopId =:shopId or s.shopId = -1) and " +
          "s.id =cir.categoryId and  cir.serviceId in(:serviceId)")
          .setLong("shopId", shopId).setParameterList("serviceId",serviceId);
    }

  }

  public static Query getServiceCategory(Session session, Long shopId, CategoryType categoryType, Long pageNo, Long pageSize) {
    int firstResultIndex = (pageNo.intValue() - 1) * pageSize.intValue();
    return session.createQuery("from Service s where (s.shopId =:shopId or s.shopId = -1) and (s.status is null or s.status <> :status) and " +
        "s.id in(select cir.serviceId from CategoryItemRelation cir where cir.categoryId in(" +
        "select c.id from Category c where (c.shopId =:shopId or c.shopId = -1) and " +
        "c.categoryType =:categoryType))").setLong("shopId", shopId).setString("categoryType", categoryType.toString())
        .setParameter("status",ServiceStatus.DISABLED).setFirstResult(firstResultIndex).setMaxResults(pageSize.intValue());
  }


  public static Query countServiceCategory(Session session, Long shopId, CategoryType categoryType) {
    return session.createQuery("select count(*) from Service s where (s.shopId =:shopId or s.shopId = -1) and (s.status is null or s.status <> :status) and " +
        "s.id  in(select cir.serviceId from CategoryItemRelation cir where cir.categoryId in(" +
        "select c.id from Category c where (c.shopId =:shopId or c.shopId = -1) and " +
        "c.categoryType =:categoryType))").setLong("shopId", shopId).setParameter("status",ServiceStatus.DISABLED).setString("categoryType", categoryType.toString());
  }

  public static Query getServiceHasCategory(Session session, Long shopId, String serviceName, String categoryName, Long pageNo, Long pageSize) {
    int firstResultIndex = (pageNo.intValue() - 1) * pageSize.intValue();
    StringBuffer sb = new StringBuffer();
    sb.append("SELECT s.*");
    sb.append(" FROM service s LEFT JOIN category_item_relation cr on s.id = cr.service_id ");
    sb.append(" LEFT JOIN category c on cr.category_id = c.id ");
    sb.append(" WHERE cr.id IS NOT NUll AND c.id IS NOT NULL AND s.shop_id =:shopId AND s.status =:serviceStatus ");
    sb.append(" AND (c.shop_id =:shopId or c.shop_id = -1) AND c.category_type =:categoryType ");
    if (StringUtils.isNotBlank(serviceName)) {
      sb.append(" AND s.name like :serviceName ");
    }
    if (StringUtils.isNotBlank(categoryName)) {
      sb.append(" AND c.category_name like :categoryName");
    }
    Query query = session.createSQLQuery(sb.toString()).addEntity(Service.class);
    query.setLong("shopId", shopId).setParameter("serviceStatus", ServiceStatus.ENABLED.name())
        .setParameter("categoryType", CategoryType.BUSINESS_CLASSIFICATION.name());
    if (StringUtils.isNotBlank(serviceName)) {
      query.setParameter("serviceName", "%" + serviceName + "%");
    }
    if (StringUtils.isNotBlank(categoryName)) {
      query.setParameter("categoryName", "%" + categoryName + "%");
    }
    query.setFirstResult(firstResultIndex).setMaxResults(pageSize.intValue());
    return query;
  }

  public static Query countServiceHasCategory(Session session, Long shopId, String serviceName, String categoryName ) {
    StringBuffer sb = new StringBuffer();
    sb.append("SELECT count(s.id) as count ");
    sb.append(" FROM service s LEFT JOIN category_item_relation cr on s.id = cr.service_id ");
    sb.append(" LEFT JOIN category c on cr.category_id = c.id ");
    sb.append(" WHERE cr.id IS NOT NUll AND c.id IS NOT NULL AND s.shop_id =:shopId AND s.status =:serviceStatus ");
    sb.append(" AND (c.shop_id =:shopId or c.shop_id = -1) AND c.category_type =:categoryType ");
    if (StringUtils.isNotBlank(serviceName)) {
      sb.append(" AND s.name like :serviceName ");
    }
    if (StringUtils.isNotBlank(categoryName)) {
      sb.append(" AND c.category_name like :categoryName");
    }
    Query query = session.createSQLQuery(sb.toString()).addScalar("count", StandardBasicTypes.LONG);
    query.setLong("shopId", shopId).setParameter("serviceStatus", ServiceStatus.ENABLED.name())
        .setParameter("categoryType", CategoryType.BUSINESS_CLASSIFICATION.name());
    if (StringUtils.isNotBlank(serviceName)) {
      query.setParameter("serviceName", "%" + serviceName + "%");
    }
    if (StringUtils.isNotBlank(categoryName)) {
      query.setParameter("categoryName", "%" + categoryName + "%");
    }
    return query;
  }

  public static Query countUndoneRepairOrderByVehicleId(Session session, Long shopId, Long vechicleId) {
    List<OrderStatus> list = new ArrayList<OrderStatus>();
    list.add(OrderStatus.REPAIR_SETTLED);
    list.add(OrderStatus.REPAIR_REPEAL);
    return session.createQuery("select count(*) from RepairOrder ro where ro.shopId =:shopId and ro.vechicleId=:vechicleId and ro.statusEnum not in (:statusEnum)")
        .setLong("shopId", shopId).setLong("vechicleId", vechicleId).setParameterList("statusEnum", list);
  }

  public static Query getLatestMemberCardOrder(Session session, Long shopId, Long customerId) {
    return session.createQuery("from MemberCardOrder where shopId = :shopId and customerId = :customerId order by creationDate desc")
        .setLong("shopId", shopId).setLong("customerId", customerId);
  }

  public static Query getMemberCardReturnById(Session session, Long shopId, Long id) {
    return session.createQuery("from MemberCardReturn where shopId = :shopId and id = :id")
        .setLong("shopId", shopId).setLong("id", id);
  }

  public static Query getMemberCardReturnItemByOrderId(Session session, Long shopId, Long orderId) {
    return session.createQuery("from MemberCardReturnItem where shopId = :shopId and memberCardReturnId = :id")
        .setLong("shopId", shopId).setLong("id", orderId);
  }

  public static Query getMemberCardReturnServiceByOrderId(Session session, Long shopId, Long orderId) {
    return session.createQuery("from MemberCardReturnService where shopId = :shopId and memberCardReturnId = :id")
        .setLong("shopId", shopId).setLong("id", orderId);
  }

  public static Query getUnsettledRepairOrderDTOsByProductId(Session session, Long shopId, Long productId) {
    StringBuffer sb = new StringBuffer();
    sb.append("select r from RepairOrder r,RepairOrderItem ri where r.id = ri.repairOrderId");
    sb.append(" and r.statusEnum in(:status) and ri.productId = :productId");
    return session.createQuery(sb.toString()).setParameterList("status", OrderUtil.repairOrderInProgress).setLong("productId", productId);
  }

  public static Query getUnsettledPurchaseOrderDTOsByProductId(Session session, Long shopId, Long productId) {
    StringBuffer sb = new StringBuffer();
    sb.append("select p from PurchaseOrder p,PurchaseOrderItem pi where p.id = pi.purchaseOrderId");
    sb.append(" and p.statusEnum in(:status) and pi.productId = :productId");
    return session.createQuery(sb.toString()).setParameterList("status", OrderUtil.purchaseOrderInProgress).setLong("productId", productId);
  }

  public static Query getUnsettledSalesOrderDTOsByProductId(Session session, Long shopId, Long productId) {
    StringBuffer sb = new StringBuffer();
    sb.append("select s from SalesOrder s,SalesOrderItem si where s.id = si.salesOrderId");
    sb.append(" and s.statusEnum in(:status) and si.productId = :productId");
    return session.createQuery(sb.toString()).setParameterList("status", OrderUtil.salesOrderInProgress).setLong("productId", productId);
  }

  public static Query getUnsettledReturnOrderByProductId(Session session, Long shopId, Long productId) {
    StringBuffer sb = new StringBuffer();
    sb.append("select p from PurchaseReturn p,PurchaseReturnItem pi where p.id = pi.purchaseReturnId");
    sb.append(" and p.status in(:status) and pi.productId = :productId");
    return session.createQuery(sb.toString()).setParameterList("status", OrderUtil.purchaseReturnInProgress).setLong("productId", productId);
  }

  public static Query getUnsettledSalesReturnByProductId(Session session, Long shopId, Long productId) {
    StringBuffer sb = new StringBuffer();
    sb.append("select s from SalesReturn s,SalesReturnItem si where s.id = si.salesReturnId");
    sb.append(" and s.status in(:status) and si.productId = :productId");
    return session.createQuery(sb.toString()).setParameterList("status", OrderUtil.salesReturnInProgress).setLong("productId", productId);
  }

  public static Query getServiceByIds(Session session,List<Long> idList)
  {
    return session.createQuery("from Service s where s.id in(:idList)").setParameterList("idList",idList);
  }


  /**
   * 分页查询应付款记录
   *
   * @param session
   * @param shopId
   * @param supplierId 供应商ID
   * @return
   * @author zhangchuanlong
   */
  public static Query searchPayable(Session session, Long shopId, Long supplierId) {
    StringBuffer sb = new StringBuffer();
    sb.append("select * from payable  p where p.shop_id=:shopId and p.order_type =:orderType");

    sb.append(" and p.supplier_id=:supplierId");


    sb.append(" and p.status<>'REPEAL'");
    sb.append(" and p.credit_amount>0");

    sb.append(" order by p.pay_time desc");

    Query q = session.createSQLQuery(sb.toString()).addEntity(Payable.class);
    q.setLong("shopId", shopId).setString("orderType",OrderTypes.INVENTORY.toString());

    q.setLong("supplierId", supplierId);


    return q;
  }

  public static Query getMemberCardConsumeTotal(Session session, Long shopId, Long memberId) {
    return session.createQuery("select sum(memberBalancePay) from Receivable where memberId=:memberId and shopId=:shopId and statusEnum=:status")
        .setLong("memberId", memberId).setLong("shopId", shopId).setParameter("status", ReceivableStatus.FINISH);
  }


  public static Query getPayableHistoryRecordByPaymentType(Session session,PaymentTypes paymentTypes,Long shopId) {
    StringBuffer hql = new StringBuffer("from PayableHistoryRecord p where shopId =:shopId  ");
    if (paymentTypes == null) {
      hql.append(" and paymentType is null ");
    } else {
      hql.append(" and paymentType =:paymentType ");
    }
    Query query = session.createQuery(hql.toString()).setLong("shopId", shopId);

    if (paymentTypes == null) {
      return query;
    } else {
      query.setParameter("paymentType", paymentTypes);
    }

    return query;
  }

  public static Query getPayHistoryRecordByPayTime(Session session, long shopId, long startTime, long endTime) {
    StringBuffer sb = new StringBuffer();
    sb.append(" from PayableHistoryRecord where shopId = :shopId " +
        "and payTime >=:startTime and payTime < :endTime and paymentType is not null and dayType != :dayType");
    return session.createQuery(sb.toString())
        .setLong("shopId", shopId)
        .setLong("startTime", startTime)
        .setLong("endTime", endTime).setParameter("dayType",DayType.TODAY);
  }


  public static Query getReceptionRecordByReceptionDate(Session session, long shopId, long startTime, long endTime) {

    StringBuffer sb = new StringBuffer();
    sb.append(" from ReceptionRecord where shopId = :shopId " +
        " and receptionDate >=:startTime and receptionDate < :endTime and orderId is not null and orderId > 0 and orderTypeEnum is not null " +
        " and dayType != :dayType ");
    return session.createQuery(sb.toString())
        .setLong("shopId", shopId)
        .setLong("startTime", startTime)
        .setLong("endTime", endTime)
        .setParameter("dayType",DayType.TODAY);
  }


  public static Query getPayableHistoryRecordListByPurchaseReturnId(Session session, Long shopId, Long purchaseReturnId,PaymentTypes paymentType) {
    StringBuffer sb = new StringBuffer();
    sb.append(" from PayableHistoryRecord where shopId =:shopId and purchaseReturnId =:purchaseReturnId and paymentType=:paymentType");
    return session.createQuery(sb.toString())
        .setLong("shopId", shopId)
        .setLong("purchaseReturnId", purchaseReturnId)
        .setParameter("paymentType",paymentType);
  }

  public static Query getMemberReturnListByReturnDate(Session session, long shopId, long startTime, long endTime) {
    return session.createQuery(" from MemberCardReturn wo where wo.shopId =:shopId and wo.returnDate >= :startTime and wo.returnDate < :endTime ")
        .setLong("shopId", shopId).setLong("startTime", startTime).setLong("endTime", endTime);
  }

  public static Query getLoanTransfersByTransfersNumber(Session session, String transfersNumber) {
    return session.createQuery("select t from LoanTransfers t where t.transfersNumber =:transfersNumber").setString("transfersNumber", transfersNumber);
  }

  public static Query getLoanTransfersByShopId(Session session, Long shopId, Pager pager) {
    return session.createQuery("select t from LoanTransfers t where t.shopId =:shopId and t.status=:status order by t.lastModified desc")
        .setLong("shopId", shopId).setFirstResult(pager.getRowStart()).setMaxResults(pager.getPageSize())
        .setString("status", LoanTransfersStatus.LOAN_SUCCESS.name());
  }

  public static Query countLoanTransfersByShopId(Session session, Long shopId) {
    return session.createQuery("select count(t) from LoanTransfers t where t.shopId =:shopId and t.status=:status").setLong("shopId", shopId).setString("status", LoanTransfersStatus.LOAN_SUCCESS.name());
  }

  public static Query sumLoanTransfersTotalAmountByShopId(Session session, Long shopId) {
    return session.createQuery("select sum(t.amount) from LoanTransfers t where t.shopId =:shopId and t.status=:status")
        .setLong("shopId", shopId)
        .setString("status", LoanTransfersStatus.LOAN_SUCCESS.name());
  }

  public static Query getLoanTransfersIdsByStatus(Session session, Long shopId, int start, int pageSize, Long loanTransferTime) {
    StringBuffer sb = new StringBuffer("select t.id from LoanTransfers t where t.status=:status and t.transfersTime>:loanTransferTime ");
    if (shopId != null) {
      sb.append(" and shopId=:shopId ");
    }
    sb.append(" order by t.id desc ");
    Query query = session.createQuery(sb.toString()).setLong("loanTransferTime", loanTransferTime).setFirstResult(start).setMaxResults(pageSize).setString("status", LoanTransfersStatus.LOAN_IN.name());
    if (shopId != null) {
      query.setLong("shopId", shopId);
    }
    return query;
  }


  public static Query getLoanTransfersIdsById(Session session, Long... ids) {
    return session.createQuery("select t from LoanTransfers t where t.id in(:ids)").setParameterList("ids", ids);
  }

  public static Query getMemberCardReturnOrdersByIds(Session session, Long... orderIds) {
    return session.createQuery("select m from MemberCardReturn m where m.id in(:ids)").setParameterList("ids", orderIds);
  }

  public static Query getMemberCardReturnItemsByIds(Session session, Long... orderIds) {
    return session.createQuery("select m from MemberCardReturnItem m where m.memberCardReturnId in(:ids)").setParameterList("ids", orderIds);
  }

  public static Query getReceptionRecordsByIds(Session session, Long... orderIds) {
    return session.createQuery("select r from ReceptionRecord r where r.orderId in(:ids)").setParameterList("ids", orderIds);
  }

  public static Query getMemberCardReturnServicesByIds(Session session, Long... orderIds) {
    return session.createQuery("select m from MemberCardReturnService m where m.memberCardReturnId in(:ids)").setParameterList("ids", orderIds);
  }

  public static Query getPurchaseReturn(Session session,Long shopId) {
    StringBuffer hql = new StringBuffer("from PurchaseReturn where shopId =:shopId ");
    Query query = session.createQuery(hql.toString()).setLong("shopId", shopId);
    return query;
  }

  public static Query deletePayHistoryRecord(Session session,Long shopId) {
    StringBuffer hql = new StringBuffer(" delete from PayableHistoryRecord where shopId =:shopId and ( paymentType =:paymentTypeCash or paymentType=:paymentType or paymentType=:inventoryReturn ) ");
    Query query = session.createQuery(hql.toString());
    query.setLong("shopId", shopId).setParameter("paymentTypeCash", PaymentTypes.INVENTORY_RETURN_CASH).setParameter("paymentType", PaymentTypes.INVENTORY_RETURN_DEPOSIT).setParameter("inventoryReturn",PaymentTypes.INVENTORY_RETURN);
    return query;
  }

  public static Query getTotalDebtByShopId(Session session,Long shopId,OrderDebtType type) {
    StringBuffer hql = new StringBuffer(" select sum(debt) from Receivable where shopId =:shopId and orderTypeEnum is not  null and statusEnum =:status and orderDebtType= :type");
    Query query = session.createQuery(hql.toString());
    query.setLong("shopId", shopId).setParameter("status",ReceivableStatus.FINISH).setString("type",type.toString());
    return query;
  }

  public static Query getDebtOrReceivableErrorRepairOrder(Session session) {
    StringBuffer sqlStr = new StringBuffer();
    sqlStr.append("select a.id from txn.repair_order a  left join txn.receivable b on a.id = b.order_id where  a.vest_date > 0  and b.id is null ");
    return session.createSQLQuery(sqlStr.toString()).addScalar("id",StandardBasicTypes.LONG);
  }

  public static Query getRepairOrderItemIds(Session session, Long shopId, int start, int pageSize) {
    StringBuffer sb = new StringBuffer("select r.id from RepairOrderItem r");
    if (shopId != null) {
      sb.append(" where r.shopId=:shopId");
    }
    Query query = session.createQuery(sb.toString());
    if (shopId != null) {
      query.setLong("shopId", shopId);
    }

    query.setFirstResult(start).setMaxResults(pageSize);
    return query;
  }

  /**
   * 根据施工单ID查询施工单货品表
   *
   * @param session
   * @param shopId
   * @param ids
   * @return
   */
  public static Query getRepairOrderItemsById(Session session,Long shopId,  Long... ids) {
    StringBuffer sb = new StringBuffer("from RepairOrderItem p where p.id in(:ids)");
    if (shopId != null) {
      sb.append(" and p.shopId=:shopId");
    }
    Query query = session.createQuery(sb.toString());
    if (shopId != null) {
      query.setLong("shopId", shopId);
    }
    return query.setParameterList("ids", ids);
  }

  public static Query getRepairOrderServiceItemIds(Session session, Long shopId, int start, int pageSize) {
    StringBuffer sb = new StringBuffer("select r.id from RepairOrderService r");
    if (shopId != null) {
      sb.append(" where r.shopId=:shopId");
    }
    Query query = session.createQuery(sb.toString());
    if (shopId != null) {
      query.setLong("shopId", shopId);
    }

    query.setFirstResult(start).setMaxResults(pageSize);
    return query;
  }

  /**
   * 根据施工单ID查询施工单货品表
   *
   * @param session
   * @param shopId
   * @param ids
   * @return
   */
  public static Query getRepairOrderServiceItemsById(Session session,Long shopId,  Long... ids) {
    StringBuffer sb = new StringBuffer("from RepairOrderService p where p.id in(:ids)");
    if (shopId != null) {
      sb.append(" and p.shopId=:shopId");
    }
    Query query = session.createQuery(sb.toString());
    if (shopId != null) {
      query.setLong("shopId", shopId);
    }
    return query.setParameterList("ids", ids);
  }

  public static Query getPurchaseInventoryOrderItemIds(Session session, Long shopId, int start, int pageSize) {
    StringBuffer sb = new StringBuffer("select r.id from PurchaseInventoryItem r");
    if (shopId != null) {
      sb.append(" where r.shopId=:shopId");
    }
    Query query = session.createQuery(sb.toString());
    if (shopId != null) {
      query.setLong("shopId", shopId);
    }

    query.setFirstResult(start).setMaxResults(pageSize);
    return query;
  }

  /**
   * 根据施工单ID查询施工单货品表
   *
   * @param session
   * @param shopId
   * @param ids
   * @return
   */
  public static Query getPurchaseInventoryOrderItemById(Session session,Long shopId,  Long... ids) {
    StringBuffer sb = new StringBuffer("from PurchaseInventoryItem p where p.id in(:ids)");
    if (shopId != null) {
      sb.append(" and p.shopId=:shopId");
    }
    Query query = session.createQuery(sb.toString());
    if (shopId != null) {
      query.setLong("shopId", shopId);
    }
    return query.setParameterList("ids", ids);
  }

  public static Query getSaleOrderItemIds(Session session, Long shopId, int start, int pageSize) {
    StringBuffer sb = new StringBuffer("select r.id from SalesOrderItem r");
    if (shopId != null) {
      sb.append(" where r.shopId=:shopId");
    }
    Query query = session.createQuery(sb.toString());
    if (shopId != null) {
      query.setLong("shopId", shopId);
    }

    query.setFirstResult(start).setMaxResults(pageSize);
    return query;
  }

  /**
   * 根据施工单ID查询施工单货品表
   *
   * @param session
   * @param shopId
   * @param ids
   * @return
   */
  public static Query getSaleOrderItemsById(Session session,Long shopId,  Long... ids) {
    StringBuffer sb = new StringBuffer("from SalesOrderItem p where p.id in(:ids)");
    if (shopId != null) {
      sb.append(" and p.shopId=:shopId");
    }
    Query query = session.createQuery(sb.toString());
    if (shopId != null) {
      query.setLong("shopId", shopId);
    }
    return query.setParameterList("ids", ids);
  }

  public static Query getWashBeautyOrderItemIds(Session session, Long shopId, int start, int pageSize) {
    StringBuffer sb = new StringBuffer("select r.id from WashBeautyOrderItem r");
    if (shopId != null) {
      sb.append(" where r.shopId=:shopId");
    }
    Query query = session.createQuery(sb.toString());
    if (shopId != null) {
      query.setLong("shopId", shopId);
    }

    query.setFirstResult(start).setMaxResults(pageSize);
    return query;
  }

  /**
   * 根据施工单ID查询施工单货品表
   *
   * @param session
   * @param shopId
   * @param ids
   * @return
   */
  public static Query getWashBeautyOrderItemsById(Session session,Long shopId,  Long... ids) {
    StringBuffer sb = new StringBuffer("from WashBeautyOrderItem p where p.id in(:ids)");
    if (shopId != null) {
      sb.append(" and p.shopId=:shopId");
    }
    Query query = session.createQuery(sb.toString());
    if (shopId != null) {
      query.setLong("shopId", shopId);
    }
    return query.setParameterList("ids", ids);
  }

  public static Query getCategoryById(Session session,Long shopId,Long categoryId)
  {
    return session.createQuery("select c from Category c where c.id=:categoryId and (c.shopId=:shopId or c.shopId = -1)")
        .setLong("shopId",shopId).setLong("categoryId",categoryId);
  }

  public static Query getCategoriesByIds(Session session, Long shopId, Set<Long> categoryIds) {
    return session.createQuery("select c from Category c where c.id in(:categoryIds) and (c.shopId=:shopId or c.shopId = -1)")
        .setLong("shopId", shopId).setParameterList("categoryIds", categoryIds);
  }

  public static Query vagueGetCategoryByShopIdAndName(Session session, Long shopId,String keyWord) {
    if(StringUtils.isBlank(keyWord))
    {
      return session.createQuery("from Category c where (c.shopId =:shopId or c.shopId = -1) and (c.status is null or c.status != :status)")
          .setLong("shopId", shopId).setString("status",CategoryStatus.DISABLED.toString());
    }
    else
    {
      return session.createQuery("from Category c where (c.shopId =:shopId or c.shopId = -1) and c.categoryName like :keyWord and (c.status is null or c.status != :status)")
          .setLong("shopId", shopId).setString("keyWord","%"+keyWord+"%").setString("status",CategoryStatus.DISABLED.toString());
    }
  }

  public static Query getCategoryByShopIdAndName(Session session, Long shopId,String keyWord) {

    return session.createQuery("from Category c where (c.shopId =:shopId or c.shopId = -1) and c.categoryName =:keyWord")
        .setLong("shopId", shopId).setString("keyWord",keyWord);
  }

  public static Query getCategoryItemRelationByServiceId(Session session,Long serviceId)
  {
    return session.createQuery("from CategoryItemRelation cir where cir.serviceId=:serviceId").setLong("serviceId",serviceId);
  }

  public static Query getCategoryItemRelationByServiceIds(Session session,Long... serviceId)
  {
    return session.createQuery("from CategoryItemRelation cir where cir.serviceId in(:serviceId)").setParameterList("serviceId",serviceId);
  }

  public static Query getCategoryItemRelation(Session session)
  {
    return session.createQuery("select cir from CategoryItemRelation as cir");
  }

  public static Query getWashBeautyOrderItem(Session session)
  {
    return session.createQuery("select wboi from WashBeautyOrderItem as wboi");
  }

  public static Query getRepairOrderService(Session session)
  {
    return session.createQuery("select ros from RepairOrderService as ros");
  }


  public static Query getPurchaseInventoryStatChange(Session session, Long shopId, Long productId, int year, int month, int day) {
    return session.createQuery("from PurchaseInventoryStatChange where shopId=:shopId and productId=:productId and statYear=:year and statMonth=:month and statDay=:day")
        .setLong("shopId", shopId).setLong("productId", productId).setInteger("year", year).setInteger("month", month).setInteger("day", day);
  }

  public static Query getPurchaseInventoryStat(Session session, Long shopId, Long productId, int year, int month, int day) {
    return session.createQuery("from PurchaseInventoryStat where shopId=:shopId and productId=:productId and statYear=:year and statMonth=:month and statDay=:day")
        .setLong("shopId", shopId).setLong("productId", productId).setInteger("year", year).setInteger("month", month).setInteger("day", day);
  }

  public static Query getLatestPurchaseInventoryStatBeforeTime(Session session, Long shopId, Long productId, long startTimeOfTimeDay) {
    return session.createQuery("from PurchaseInventoryStat where shopId=:shopId and productId=:productId and statTime<:statTime order by statTime desc")
        .setLong("shopId", shopId).setLong("productId", productId).setLong("statTime", startTimeOfTimeDay).setMaxResults(1);
  }

  public static Query getSupplierTranStat(Session session, Long shopId, Long supplierId, int year, int month, int day) {
    return session.createQuery("from SupplierTranStat where shopId=:shopId and supplierId=:supplierId and statYear=:year and statMonth=:month and statDay=:day")
        .setLong("shopId", shopId).setLong("supplierId", supplierId).setInteger("year", year).setInteger("month", month).setInteger("day", day);
  }

  public static Query getLatestSupplierTranStatBeforeTime(Session session, Long shopId, Long supplierId, long startTimeOfTimeDay) {
    return session.createQuery("from SupplierTranStat where shopId=:shopId and supplierId=:supplierId and statTime<:statTime order by statTime desc")
        .setLong("shopId", shopId).setLong("supplierId", supplierId).setLong("statTime", startTimeOfTimeDay).setMaxResults(1);
  }

  public static Query getSupplierTranStatChange(Session session, Long shopId, Long supplierId, int year, int month, int day) {
    return session.createQuery("from SupplierTranStatChange where shopId=:shopId and supplierId=:supplierId and statYear=:year and statMonth=:month and statDay=:day")
        .setLong("shopId", shopId).setLong("supplierId", supplierId).setInteger("year", year).setInteger("month", month).setInteger("day", day);
  }


  public static Query getPurchaseReturnMonthStat(Session session, Long shopId, Long supplierId, Long productId, int year, int month) {
    return session.createQuery("from PurchaseReturnMonthStat where shopId=:shopId and supplierId=:supplierId and productId=:productId and statYear=:year and statMonth=:month ")
        .setLong("shopId", shopId).setLong("supplierId", supplierId).setLong("productId", productId).setInteger("year", year).setInteger("month", month).setMaxResults(1);
  }

  public static Query getLatestPurchaseReturnStatBeforeTime(Session session, Long shopId, Long supplierId, Long productId, long startTimeOfTimeDay) {
    return session.createQuery("from PurchaseReturnStat where shopId=:shopId and supplierId=:supplierId and productId=:productId and statTime<:statTime order by statTime desc")
        .setLong("shopId", shopId).setLong("supplierId", supplierId).setLong("productId", productId).setLong("statTime", startTimeOfTimeDay).setMaxResults(1);
  }

  public static Query getPurchaseReturnStatChange(Session session, Long shopId, Long supplierId, Long productId, int year, int month, int day) {
    return session.createQuery("from PurchaseReturnStatChange where shopId=:shopId and supplierId=:supplierId and productId=:productId and statYear=:year and statMonth=:month and statDay=:day")
        .setLong("shopId", shopId).setLong("supplierId", supplierId).setLong("productId", productId).setInteger("year", year).setInteger("month", month).setInteger("day", day);
  }

  public static Query getSalesStat(Session session, Long shopId, Long productId, int year, int month, int day) {
    return session.createQuery("from SalesStat where shopId=:shopId and productId=:productId and statYear=:year and statMonth=:month and statDay=:day")
        .setLong("shopId", shopId).setLong("productId", productId).setInteger("year", year).setInteger("month", month).setInteger("day", day);
  }

  public static Query getLatestSalesStatBeforeTime(Session session, Long shopId, Long productId, long startTimeOfTimeDay) {
    return session.createQuery("from SalesStat where shopId=:shopId and productId=:productId and statTime<:statTime order by statTime desc")
        .setLong("shopId", shopId).setLong("productId", productId).setLong("statTime", startTimeOfTimeDay).setMaxResults(1);
  }

  public static Query getSalesStatChange(Session session, Long shopId, Long productId, int year, int month, int day) {
    return session.createQuery("from SalesStatChange where shopId=:shopId and productId=:productId and statYear=:year and statMonth=:month and statDay=:day")
        .setLong("shopId", shopId).setLong("productId", productId).setInteger("year", year).setInteger("month", month).setInteger("day", day);
  }

  public static Query getFirstPurchaseInventoryByVestDate(Session session, Long shopId) {
    return session.createQuery("from PurchaseInventory where shopId=:shopId and statusEnum=:status order by vestDate")
        .setLong("shopId", shopId).setParameter("status", OrderStatus.PURCHASE_INVENTORY_DONE).setMaxResults(1);
  }

  public static Query getPurchaseInventoryByVestDate(Session session, Long shopId, long start, long end) {
    return session.createQuery("from PurchaseInventory where shopId=:shopId and vestDate >= :start and vestDate< :end and statusEnum=:status order by vestDate")
        .setLong("shopId", shopId).setLong("start", start).setLong("end", end).setParameter("status", OrderStatus.PURCHASE_INVENTORY_DONE);
  }

  public static Query getLatestPurchaseInventoryStatInRange(Session session, Long shopId, Long productId, long fromTime, long endTime) {
    return session.createQuery("from PurchaseInventoryStat where shopId=:shopId and productId=:productId and statTime>=:fromTime and statTime<:endTime order by statTime desc")
        .setLong("shopId", shopId).setLong("productId", productId).setLong("fromTime", fromTime).setLong("endTime", endTime).setMaxResults(1);
  }

  public static Query getLatestInventoryStatBeforeTime(Session session, Long shopId, Long productId, long fromTime) {
    return session.createQuery("from PurchaseInventoryStat where shopId=:shopId and productId=:productId and statTime<:fromTime order by statTime desc")
        .setLong("shopId", shopId).setLong("productId", productId).setLong("fromTime", fromTime).setMaxResults(1);
  }

  public static Query getPurchaseInventoryStatChangeInRange(Session session, Long shopId, Long productId, long fromTime, long endTime) {
    return session.createSQLQuery("SELECT shop_id, product_id, SUM(times) AS times, SUM(amount) AS amount, SUM(total) AS total " +
        "FROM purchase_inventory_stat_change WHERE shop_id=:shopId AND product_id=:productId AND stat_time>=:fromTime AND stat_time<:endTime " +
        "GROUP BY product_id, shop_id")
        .setLong("shopId", shopId).setLong("productId", productId).setLong("fromTime", fromTime).setLong("endTime", endTime);
  }

  public static Query getLatestSupplierTranStatInRange(Session session, Long shopId, Long supplierId, long fromTime, long endTime) {
    return session.createQuery("from SupplierTranStat where shopId=:shopId and supplierId=:supplierId and statTime>=:fromTime and statTime<:endTime order by statTime desc")
        .setLong("shopId", shopId).setLong("supplierId", supplierId).setLong("fromTime", fromTime).setLong("endTime", endTime).setMaxResults(1);
  }

  public static Query getSupplierTranStatChangeInRange(Session session, Long shopId, Long supplierId, long fromTime, long endTime) {
    return session.createSQLQuery("SELECT shop_id, supplier_id, SUM(times) AS times, SUM(total) AS total " +
        "FROM supplier_tran_stat_change WHERE shop_id=:shopId AND supplier_id = :supplierId AND stat_time>=:fromTime AND stat_time<:endTime " +
        "GROUP BY supplier_id, shop_id")
        .setLong("shopId", shopId).setLong("supplierId", supplierId).setLong("fromTime", fromTime).setLong("endTime", endTime);
  }

  public static Query getSupplierTranMonthStat(Session session, Long shopId, Long supplierId, int year, int month) {
    return session.createQuery("from SupplierTranMonthStat where shopId = :shopId and supplierId=:supplierId and statYear=:year and statMonth=:month")
        .setLong("shopId", shopId).setLong("supplierId", supplierId).setInteger("year", year).setInteger("month", month);
  }

  public static Query queryTopSupplierTranMonthStat(Session session, Long shopId, int year, int month, boolean allYear, int topLimit) {
    return session.createQuery("from SupplierTranMonthStat where shopId=:shopId and statYear=:year and statMonth = :month order by total desc")
        .setLong("shopId", shopId).setInteger("year", year).setInteger("month", month).setMaxResults(topLimit);
  }

  public static Query queryTopSupplierTranYearStat(Session session, Long shopId, int year, int topLimit) {
    return session.createQuery("select shopId, supplierId, statYear, sum(times) as times, sum(total) as total " +
        "from SupplierTranMonthStat where shopId = :shopId and statYear=:year " +
        "group by supplierId, shopId, statYear " +
        "order by total desc")
        .setLong("shopId", shopId).setInteger("year", year).setMaxResults(topLimit);
  }

  public static Query querySupplierTranTotal(Session session, Long shopId, int year, int month, boolean allYear) {
    StringBuffer sb = new StringBuffer();
    sb.append("select sum(total) from SupplierTranMonthStat where shopId=:shopId and statYear=:year ");
    if(!allYear){
      sb.append("and statMonth=:month ");
    }
    sb.append("group by shopId, statYear ");
    if(!allYear){
      sb.append(",statMonth");
    }
    Query q = session.createQuery(sb.toString()).setLong("shopId", shopId).setInteger("year", year);
    if(!allYear){
      q.setInteger("month", month);
    }
    return q;
  }

  public static Query queryTopPurchaseInventoryMonthStat(Session session, Long shopId, int year, int month, boolean allYear, String[] queryFields, int topLimit) {
    StringBuffer sb = new StringBuffer();
    sb.append("select productName, productBrand, vehicleBrand, vehicleModel, sum(times), sum(amount), sum(total) as total ");
    sb.append("from PurchaseInventoryMonthStat ");
    sb.append("where statYear=:year ");
    if(!allYear){
      sb.append("and statMonth=:month ");
    }
    sb.append("and shopId = :shopId and productName!='\0' ");
    sb.append("group by productName ");
    if(allYear){
      sb.append(", statYear ");
    }
    if(ArrayUtils.contains(queryFields, CostStatConditionDTO.FIELD_BRAND)){
      sb.append(", productBrand ");
    }
    if(ArrayUtils.contains(queryFields, CostStatConditionDTO.FIELD_VEHICLE_MODEL)){
      sb.append(", vehicleBrand, vehicleModel ");
    }
    sb.append("order by total desc ");
    Query q = session.createQuery(sb.toString()).setLong("shopId", shopId).setInteger("year", year);
    if(!allYear){
      q.setInteger("month", month);
    }
    return q.setMaxResults(topLimit);
  }

  public static Query queryPurchaseInventoryTotal(Session session, Long shopId, int year, int month, boolean allYear) {
    StringBuffer sb = new StringBuffer();
    sb.append("select sum(total) from PurchaseInventoryMonthStat where shopId=:shopId and statYear=:year ");
    if(!allYear){
      sb.append("and statMonth=:month ");
    }
    sb.append("group by shopId, statYear ");
    if(!allYear){
      sb.append(",statMonth");
    }
    Query q = session.createQuery(sb.toString()).setLong("shopId", shopId).setInteger("year", year);
    if(!allYear){
      q.setInteger("month", month);
    }
    return q;
  }

  public static Query getPurchaseInventoryMonthStat(Session session, Long shopId, BcgogoOrderItemDto itemDTO, int year, int month) {
    return session.createQuery("from PurchaseInventoryMonthStat where shopId=:shopId and productName =:name and productBrand=:brand " +
        "and vehicleBrand =:vehicleBrand and vehicleModel = :vehicleModel and statYear = :year and statMonth = :month")
        .setLong("shopId", shopId).setString("name", StringUtils.defaultIfEmpty(itemDTO.getProductName(), ""))
        .setString("brand", StringUtils.defaultIfEmpty(itemDTO.getBrand(), ""))
        .setString("vehicleBrand", StringUtils.defaultIfEmpty(itemDTO.getVehicleBrand(), ""))
        .setString("vehicleModel", StringUtils.defaultIfEmpty(itemDTO.getVehicleModel(), ""))
        .setInteger("year", year).setInteger("month", month);
  }

  public static Query getProductModifyLogByStatus(Session session, ProductModifyOperations productModifyOperation, StatProcessStatus[] status) {
    return session.createQuery("from ProductModifyLog where statProcessStatus in :status and operationType = :productModifyOperation order by creationDate")
        .setParameterList("status", status).setParameter("productModifyOperation", productModifyOperation);
  }

public static Query getProductModifyLogDTOByRelevanceStatus(Session session,ProductRelevanceStatus relevanceStatus,Long... productId) {
    StringBuilder sb=new StringBuilder("from ProductModifyLog where productId in(:productId)");
    if(relevanceStatus!=null){
      sb.append(" and relevanceStatus = :relevanceStatus");
    }
    sb.append("  order by creationDate");
    Query query=session.createQuery(sb.toString()).setParameterList("productId", productId);
    if(relevanceStatus!=null){
      query.setParameter("relevanceStatus", relevanceStatus);
    }
    return query;
  }


  public static Query getPurchaseInventoryMonthStatByProperties(Session session, Long shopId, String name, String brand, String vehicleBrand, String vehicleModel) {
    return session.createQuery("from PurchaseInventoryMonthStat where shopId = :shopId and UPPER(productName)=:name " +
        "and UPPER(productBrand)=:brand and UPPER(vehicleBrand) =:vehicleBrand and UPPER(vehicleModel) = :vehicleModel")
        .setLong("shopId", shopId).setString("name", StringUtil.toUpperCase(name)).setString("brand", StringUtil.toUpperCase(brand))
        .setString("vehicleBrand", StringUtil.toUpperCase(vehicleBrand)).setString("vehicleModel", StringUtil.toUpperCase(vehicleModel));

  }

  public static Query batchUpdateProductModifyLogStatus(Session session, List<Long> ids, StatProcessStatus doneStatus) {
    return session.createQuery("update ProductModifyLog set statProcessStatus=:doneStatus where id in :ids")
        .setParameterList("ids", ids).setParameter("doneStatus", doneStatus);
  }

  public static Query getFirstPurchaseReturnByVestDate(Session session, Long shopId) {
    return session.createQuery("from PurchaseReturn where shopId=:shopId and status =:status  order by creationDate")
        .setLong("shopId", shopId).setParameter("status",OrderStatus.SETTLED).setMaxResults(1);
  }

  public static Query getPurchaseReturnByVestDate(Session session, Long shopId, long startTime, long endTime) {
    return session.createQuery("from PurchaseReturn where shopId=:shopId and status =:status and creationDate >= :start and creationDate< :end")
        .setLong("shopId", shopId).setLong("start", startTime).setParameter("status",OrderStatus.SETTLED).setLong("end", endTime);
  }

  public static Query getFirstSalesOrderByVestDate(Session session, Long shopId) {
    return session.createQuery("from SalesOrder where shopId=:shopId and (statusEnum=:status or statusEnum=:debtStatus) order by vestDate ")
        .setLong("shopId", shopId).setParameter("status", OrderStatus.SALE_DONE).setParameter("debtStatus", OrderStatus.SALE_DEBT_DONE).setMaxResults(1);
  }

  public static Query getFirstRepairOrderByVestDate(Session session, Long shopId) {
    return session.createQuery("from RepairOrder where shopId=:shopId and statusEnum=:status order by vestDate ")
        .setLong("shopId", shopId).setParameter("status", OrderStatus.REPAIR_SETTLED).setMaxResults(1);
  }

  public static Query getRepairOrderListByVestDate(Session session,Long shopId,long startTime,long endTime) {
    return session.createQuery(" from RepairOrder where shopId=:shopId and statusEnum=:status " +
        "and vestDate >=:startTime and vestDate <:endTime ")
        .setLong("shopId", shopId).setParameter("status", OrderStatus.REPAIR_SETTLED).setLong("startTime", startTime).setLong("endTime", endTime);
  }

  public static Query getSalesMonthStatByYearMonth(Session session, Long shopId, Long productId, int year, int month) {
    return session.createQuery("from SalesMonthStat where shopId=:shopId and productId=:productId and statYear=:year and statMonth=:month order by statTime desc ")
        .setLong("shopId", shopId).setLong("productId", productId).setInteger("year", year).setInteger("month", month);
  }

  public static Query querySalesStatByCondition(Session session, Long shopId,SalesStatCondition salesStatCondition) {
    StringBuffer sb = new StringBuffer();

    if(salesStatCondition.getAllYear().booleanValue() && salesStatCondition.getMonthStr().equals(StatConstant.ALL_MONTH)) {
      sb.append(" select productId,sum(amount) as amount,sum(total) as total from SalesMonthStat " +
          " where shopId=:shopId and statYear=:year and statMonth >:startMonth and statMonth <:endMonth group by productId ");
      if (salesStatCondition.getMoneyOrAmount().equals(StatConstant.QUERY_BY_MONEY)) {
        sb.append(" order by total desc ");
      } else {
        sb.append(" order by amount desc ");
      }
      Query query = session.createQuery(sb.toString());
      return query.setLong("shopId", shopId).setInteger("year", salesStatCondition.getYear()).setInteger("startMonth", 0).setInteger("endMonth", 13)
          .setMaxResults(StatConstant.QUERY_SIZE);
    }

    sb.append("select productId,sum(amount),sum(total) from SalesMonthStat where shopId=:shopId and statYear=:year and statMonth=:month group by productId ");
    if(salesStatCondition.getMoneyOrAmount().equals(StatConstant.QUERY_BY_MONEY)){
      sb.append(" order by total desc ");
    }else{
      sb.append(" order by amount desc ");
    }
    Query query = session.createQuery(sb.toString());
    return query.setLong("shopId", shopId).setInteger("year", salesStatCondition.getYear()).setInteger("month", salesStatCondition.getMonth()).setMaxResults(StatConstant.QUERY_SIZE);
  }



  public static Query queryBadSalesStatByCondition(Session session, Long shopId,SalesStatCondition salesStatCondition) {
    StringBuffer sb = new StringBuffer();
    Long currentTime = System.currentTimeMillis();

    sb.append("select id,amount,inventoryAveragePrice from Inventory where shopId=:shopId and ((:currentTime - lastSalesTime ) >:time or lastSalesTime is null)  and inventoryAveragePrice is not null and amount > 0 ");

    if (salesStatCondition.getMoneyOrAmount().equals(StatConstant.QUERY_BY_MONEY)) {
      sb.append(" order by amount*inventoryAveragePrice desc ");
    } else {
      sb.append(" order by amount desc ");
    }

    Query query = session.createQuery(sb.toString());
    return query.setLong("shopId", shopId).setLong("currentTime", currentTime).setLong("time", salesStatCondition.getLastSaleTime()).setMaxResults(StatConstant.QUERY_SIZE);
  }


  public static Query countBadSalesStatByCondition(Session session, Long shopId,SalesStatCondition salesStatCondition) {
    StringBuffer sb = new StringBuffer();
    Long currentTime = System.currentTimeMillis();

    sb.append("select sum(amount),sum(inventoryAveragePrice*amount),count(id) from Inventory where shopId=:shopId and ((:currentTime - lastSalesTime ) >:time or lastSalesTime is null) and inventoryAveragePrice is not null and amount > 0 ");

    Query query = session.createQuery(sb.toString());
    return query.setLong("shopId", shopId).setLong("currentTime", currentTime).setLong("time", salesStatCondition.getLastSaleTime()).setMaxResults(StatConstant.QUERY_SIZE);
  }


  public static Query countSalesStatByCondition(Session session, Long shopId,SalesStatCondition salesStatCondition) {
    StringBuffer sb = new StringBuffer();


    if(salesStatCondition.getAllYear().booleanValue() && salesStatCondition.getMonthStr().equals(StatConstant.ALL_MONTH)) {
      sb.append(" select sum(amount),sum(total),count(productId) from SalesMonthStat where shopId=:shopId " +
          " and statYear=:year and statMonth >:startMonth and statMonth <:endMonth  ");
      Query query = session.createQuery(sb.toString());
      return query.setLong("shopId", shopId).setInteger("year", salesStatCondition.getYear()).setInteger("startMonth", 0).setInteger("endMonth", 13);
    }

    sb.append(" select sum(amount),sum(total),count(productId) from SalesMonthStat where shopId=:shopId and statYear=:year and statMonth=:month ");

    Query query = session.createQuery(sb.toString());
    return query.setLong("shopId", shopId).setInteger("year", salesStatCondition.getYear()).setInteger("month", salesStatCondition.getMonth());
  }


  public static Query getInventoryByShopId(Session session, Long shopId,Pager pager) {
    return session.createQuery(" from Inventory  ir where ir.shopId = :shopId").setLong("shopId", shopId).setFirstResult(pager.getRowStart()).setMaxResults(pager.getPageSize());
  }

  public static Query getSalesVestDateByShopId(Session session, Long shopId,Long productId,Sort sort) {
    StringBuffer sb = new StringBuffer();
    sb.append(" select a.vestDate from SalesOrder a ,SalesOrderItem b where a.shopId =:shopId and a.id = b.salesOrderId and b.productId =:productId and (a.statusEnum=:statusEnum or a.statusEnum=:debtStatusEnum) ")
        .append(sort.toOrderString());
    return session.createQuery(sb.toString()).setLong("productId", productId).setLong("shopId", shopId).setParameter("statusEnum", OrderStatus.SALE_DONE).setParameter("debtStatusEnum", OrderStatus.SALE_DEBT_DONE).setFirstResult(0).setMaxResults(1);
  }

  public static Query getRepairVestDateByShopId(Session session, Long shopId,Long productId,Sort sort) {
    StringBuffer sb = new StringBuffer();
    sb.append(" select a.vestDate from RepairOrder a ,RepairOrderItem b where a.shopId =:shopId and b.shopId =:itemShopId and a.id = b.repairOrderId and b.productId =:productId and a.statusEnum=:statusEnum ")
        .append(sort.toOrderString());
    return session.createQuery(sb.toString()).setLong("productId", productId).setLong("shopId", shopId).setLong("itemShopId", shopId).setParameter("statusEnum", OrderStatus.REPAIR_SETTLED).setFirstResult(0).setMaxResults(1);
  }

  public static Query queryTopPurchaseInventoryLastTwelveMonthStat(Session session, Long shopId, int limit){
    return session.createSQLQuery("SELECT * FROM price_fluctuation_stat p WHERE p.shop_id =:shopId ORDER BY total DESC").addEntity(PriceFluctuationStat.class).setLong("shopId", shopId).setMaxResults(limit);
  }

  public static Query queryAllProductPriceFluctuation(Session session, Long startTime, Long endTime){
    StringBuffer sb = new StringBuffer();

    sb.append("select curr_shop_id, curr_product_id, curr_total - ifnull(pre_total,0) + ifnull(chg.change_total,0) as final_total, ");
    sb.append("curr_amount - ifnull(pre_amount,0) + ifnull(change_amount,0) as final_amount, ");
    sb.append("curr_times - ifnull(pre_times,0) + ifnull(change_times,0) as final_times ");
    sb.append("from ");
    sb.append("(select shop_id as curr_shop_id, product_id as curr_product_id, stat_time as curr_stat_time, max(times) as curr_times, ");
    sb.append("max(amount) as curr_amount, max(total) as curr_total from purchase_inventory_stat ");
    sb.append("where stat_time >= ").append(startTime).append(" and stat_time< ").append(endTime).append(" group by product_id) as curr ");
    sb.append("left join ");
    sb.append("(select shop_id as pre_shop_id, product_id as pre_product_id, stat_time as pre_stat_time, max(times) as pre_times, ");
    sb.append("max(amount) as pre_amount, max(total) as pre_total from purchase_inventory_stat ");
    sb.append("where stat_time < ").append(startTime).append(" group by product_id) as pre ");
    sb.append("on curr.curr_product_id = pre.pre_product_id ");
    sb.append("left join ");
    sb.append("(select shop_id as change_shop_id, product_id as change_product_Id, stat_time as change_stat_time, ");
    sb.append("sum(times) as change_times, sum(amount) as change_amount, sum(total) as change_total from purchase_inventory_stat_change ");
    sb.append("where stat_time >= ").append(startTime).append(" and stat_time< ").append(endTime).append(" group by product_id) as chg ");
    sb.append("on curr.curr_product_id = chg.change_product_id ");
    sb.append("order by curr_shop_id ");

    Query q = session.createSQLQuery(sb.toString());
    return q;
  }

//  public static Query getPriceFluctuationLineChartData(Session session,Long shopId, Long productId,Long startTime,Long endTime){
//    StringBuffer sb = new StringBuffer();
//    sb.append("select (curr_total - ifnull(pre_total,0) + ifnull(chg.change_total,0)) / (curr_amount - ifnull(pre_amount,0) + ifnull(change_amount,0)) as avgPrice ");
//    sb.append("from ");
//    sb.append("(select product_id as curr_product_id, max(amount) as curr_amount, max(total) as curr_total from purchase_inventory_stat ");
//    sb.append("where shop_id=").append(shopId).append(" and product_id=").append(productId);
//    sb.append(" and  stat_time >= ").append(startTime).append(" and stat_time< ").append(endTime).append(" group by product_id) as curr ");
//    sb.append("left join ");
//    sb.append("(select product_id as pre_product_id, max(amount) as pre_amount, max(total) as pre_total from purchase_inventory_stat ");
//    sb.append("where shop_id=").append(shopId).append(" and product_id=").append(productId);
//    sb.append(" and stat_time < ").append(startTime).append(" group by product_id) as pre ");
//    sb.append("on curr.curr_product_id = pre.pre_product_id ");
//    sb.append("left join ");
//    sb.append("(select product_id as change_product_Id, sum(amount) as change_amount, sum(total) as change_total from purchase_inventory_stat_change ");
//    sb.append("where shop_id=").append(shopId).append(" and product_id=").append(productId);
//    sb.append(" and stat_time >= ").append(startTime).append(" and stat_time< ").append(endTime).append(" group by product_id) as chg ");
//    sb.append("on curr.curr_product_id = chg.change_product_id ");
//    Query q = session.createSQLQuery(sb.toString());
//    return q;
//  }

  public static Query getPurchaseInventoryStatByProductId(Session session,Long shopId, Long productId,Long startTime,Long endTime){
    StringBuffer sb = new StringBuffer();
    sb.append("select amount,total,stat_time from purchase_inventory_stat where shop_id =:shopId and product_id =:productId and stat_time>= :startTime and stat_time< :endTime order by stat_time");
    Query q = session.createSQLQuery(sb.toString()).setLong("productId",productId).setLong("shopId",shopId).setLong("startTime",startTime).setLong("endTime",endTime);
    return q;
  }

  public static Query getPurchaseInventoryStatChangeByProductId(Session session,Long shopId, Long productId,Long startTime,Long endTime){
    StringBuffer sb = new StringBuffer();
    sb.append("select amount,total,stat_time from purchase_inventory_stat_change where shop_id =:shopId and product_id =:productId and stat_time>= :startTime and stat_time< :endTime order by stat_time");
    Query q = session.createSQLQuery(sb.toString()).setLong("productId",productId).setLong("shopId",shopId).setLong("startTime",startTime).setLong("endTime",endTime);
    return q;
  }

  public static Query countTotalReturnByCondition(Session session, Long shopId,SalesStatCondition salesStatCondition) {
    StringBuffer sb = new StringBuffer();

    if (salesStatCondition.getAllYear().booleanValue() && salesStatCondition.getMonthStr().equals(StatConstant.ALL_MONTH)) {
      sb.append(" select sum(amount),sum(total),count(distinct supplierId),count(distinct productId) from PurchaseReturnMonthStat where shopId=:shopId " +
          " and statYear=:year and statMonth >:startMonth and statMonth <:endMonth  ");
      Query query = session.createQuery(sb.toString());
      return query.setLong("shopId", shopId).setInteger("year", salesStatCondition.getYear()).setInteger("startMonth", 0).setInteger("endMonth", 13);
    }
    sb.append(" select sum(amount),sum(total),count(distinct supplierId),count(distinct productId) from PurchaseReturnMonthStat where shopId=:shopId and statYear=:year and statMonth=:month ");
    Query query = session.createQuery(sb.toString());
    return query.setLong("shopId", shopId).setInteger("year", salesStatCondition.getYear()).setInteger("month", salesStatCondition.getMonth());
  }

  public static Query queryPurchaseReturnByCondition(Session session, Long shopId,SalesStatCondition salesStatCondition) {
    StringBuffer sb = new StringBuffer();

    if(salesStatCondition.getProductOrSupplier().equals(StatConstant.QUERY_BY_PRODUCT)) {
      if (salesStatCondition.getAllYear().booleanValue() && salesStatCondition.getMonthStr().equals(StatConstant.ALL_MONTH)) {
        sb.append(" select productId, sum(total),sum(amount),sum(times),count(supplierId) from PurchaseReturnMonthStat where shopId=:shopId " +
            " and statYear=:year and statMonth >:startMonth and statMonth <:endMonth ");
        sb.append(" group by productId order by sum(total) desc ");
        Query query = session.createQuery(sb.toString());
        return query.setLong("shopId", shopId).setInteger("year", salesStatCondition.getYear()).setInteger("startMonth", 0).setInteger("endMonth", 13).setMaxResults(StatConstant.QUERY_SIZE);
      }else {
        sb.append(" select productId, sum(total),sum(amount),sum(times),count(supplierId) from PurchaseReturnMonthStat where shopId=:shopId " +
            " and statYear=:year and statMonth=:month ");
        sb.append(" group by productId order by sum(total) desc ");
        Query query = session.createQuery(sb.toString());
        return query.setLong("shopId", shopId).setInteger("year", salesStatCondition.getYear()).setInteger("month", salesStatCondition.getMonth()).setMaxResults(StatConstant.QUERY_SIZE);
      }
    }else {
      if (salesStatCondition.getAllYear().booleanValue() && salesStatCondition.getMonthStr().equals(StatConstant.ALL_MONTH)) {
        sb.append(" select supplierId, sum(total),sum(amount),sum(times),count(productId) from PurchaseReturnMonthStat where shopId=:shopId " +
            " and statYear=:year and statMonth >:startMonth and statMonth <:endMonth ");
        sb.append(" group by supplierId order by sum(total) desc ");
        Query query = session.createQuery(sb.toString());
        return query.setLong("shopId", shopId).setInteger("year", salesStatCondition.getYear()).setInteger("startMonth", 0).setInteger("endMonth", 13).setMaxResults(StatConstant.QUERY_SIZE);
      } else {
        sb.append(" select supplierId, sum(total),sum(amount),sum(times),count(productId) from PurchaseReturnMonthStat where shopId=:shopId " +
            " and statYear=:year and statMonth=:month ");
        sb.append(" group by supplierId order by sum(total) desc ");
        Query query = session.createQuery(sb.toString());
        return query.setLong("shopId", shopId).setInteger("year", salesStatCondition.getYear()).setInteger("month", salesStatCondition.getMonth()).setMaxResults(StatConstant.QUERY_SIZE);
      }
    }
  }

  public static Query getPurchaseReturnStat(Session session, Long shopId, Long supplierId, Long productId, int year, int month,int day) {
    return session.createQuery("from PurchaseReturnStat where shopId=:shopId and supplierId=:supplierId and productId=:productId and statYear=:year and statMonth=:month and statDay=:day order by statTime desc")
        .setLong("shopId", shopId).setLong("supplierId", supplierId).setLong("productId", productId).setInteger("year", year).setInteger("month", month).setInteger("day", day).setMaxResults(1);
  }

  public static Query getAllPurchaseReturnMonthStatBySupplierIds(Session session,Long shopId,Long[] supplierIds) {
    return session.createQuery("from PurchaseReturnMonthStat where shopId=:shopId and supplierId in(:supplierIds)")
        .setLong("shopId", shopId).setParameterList("supplierIds", supplierIds);
  }

  public static Query getAllSupplierTranMonthStatBySupplierIds(Session session,Long shopId,Long[] supplierIds) {
    return session.createQuery("from SupplierTranMonthStat where shopId=:shopId and supplierId in(:supplierIds)")
        .setLong("shopId", shopId).setParameterList("supplierIds", supplierIds);
  }

  public static Query getFirstPurchaseInventoryCreationDateByProductIdShopId(Session session, Long shopId, Long productId) {
    return session.createQuery("select pi.creationDate from PurchaseInventory pi, PurchaseInventoryItem pii " +
        "where pi.id = pii.purchaseInventoryId and pii.productId = :productId and pi.shopId = :shopId and pi.statusEnum=:status order by pi.creationDate")
        .setLong("productId", productId).setLong("shopId", shopId).setParameter("status", OrderStatus.PURCHASE_INVENTORY_DONE).setMaxResults(1);
  }

  public static Query getPurchaseInventoryByProductIdCreationDate(Session session, Long shopId, Long productId, Long startTime, Long endTime) {
    return session.createQuery("select pi from PurchaseInventory pi, PurchaseInventoryItem pii " +
        "where pi.id = pii.purchaseInventoryId and pii.productId = :productId and pi.shopId = :shopId and pi.creationDate>=:startTime and pi.creationDate<:endTime " +
        "and pi.statusEnum=:status ")
        .setLong("productId", productId).setLong("shopId", shopId).setLong("startTime", startTime).setLong("endTime", endTime)
        .setParameter("status", OrderStatus.PURCHASE_INVENTORY_DONE);
  }

  public static Query getFirstPurchaseReturnCreationDateByProductIdShopId(Session session, Long shopId, Long productId) {
    return session.createQuery("select pr.creationDate from PurchaseReturn pr, PurchaseReturnItem pri " +
        "where pr.id = pri.purchaseReturnId and pri.productId = :productId and pr.shopId = :shopId and pr.status=:status order by pr.creationDate")
        .setLong("productId", productId).setLong("shopId", shopId).setParameter("status", OrderStatus.SETTLED).setMaxResults(1);
  }

  public static Query getPurchaseReturnByProductIdCreationDate(Session session, Long shopId, Long productId, Long startTime, Long endTime) {
    return session.createQuery("select pr from PurchaseReturn pr, PurchaseReturnItem pri " +
        "where pr.id = pri.purchaseReturnId and pri.productId = :productId and pr.shopId = :shopId and pr.creationDate>=:startTime and pr.creationDate<:endTime " +
        "and pr.status=:status ")
        .setLong("productId", productId).setLong("shopId", shopId).setLong("startTime", startTime).setLong("endTime", endTime)
        .setParameter("status", OrderStatus.SETTLED);
  }

  public static Query getImportedOrderCountByConditions(Session session,ImportedOrderDTO importedOrderIndex) {
    StringBuffer sb = new StringBuffer();
    sb.append("select count(distinct o.id) from ImportedOrder o,ImportedOrderItem i where o.id=i.orderId and o.shopId =:shopId ");
    if(importedOrderIndex.getStartDate()!=null){
      sb.append(" and o.vestDate >=:startDate");
    }
    if(importedOrderIndex.getEndDate()!=null){
      sb.append(" and o.vestDate <=:endDate");
    }

    if(StringUtil.isNotEmpty(importedOrderIndex.getVehicle())){
      sb.append(" and o.vehicle like :vehicle");
    }
    if(StringUtil.isNotEmpty(importedOrderIndex.getCustomerSupplierName())){
      sb.append(" and o.customerSupplierName like :customerSupplierName");
    }
    if(StringUtil.isNotEmpty(importedOrderIndex.getMemberType())){
      sb.append(" and o.memberType like :memberType");
    }
    if(StringUtil.isNotEmpty(importedOrderIndex.getMemberCardNo())){
      sb.append(" and o.memberCardNo like :memberCardNo");
    }
    if(!StringUtil.isAllEmpty(importedOrderIndex.getOrderTypes())){
      sb.append(" and o.orderTypeStr in (:orderTypes)");
    }
    if(!StringUtil.isAllEmpty(importedOrderIndex.getSalesMan())){
      sb.append(" and o.salesMan in (:salesMan)") ;
    }
    if(!StringUtil.isAllEmpty(importedOrderIndex.getServiceWorkers())){
      sb.append(" and i.serviceWorker in (:serviceWorkers)") ;
    }
    if(StringUtil.isNotEmpty(importedOrderIndex.getProductName())){
      sb.append(" and i.productName like :productName");
    }
    if(StringUtil.isNotEmpty(importedOrderIndex.getBrand())){
      sb.append(" and i.brand like :brand");
    }
    if(StringUtil.isNotEmpty(importedOrderIndex.getVehicleBrand())){
      sb.append(" and i.vehicleBrand like :vehicleBrand");
    }
    if(StringUtil.isNotEmpty(importedOrderIndex.getVehicleModel())){
      sb.append(" and i.vehicleModel like :vehicleModel");
    }
    if(StringUtil.isNotEmpty(importedOrderIndex.getSpec())){
      sb.append(" and i.spec like :spec");
    }
    if(StringUtil.isNotEmpty(importedOrderIndex.getModel())){
      sb.append(" and i.model like :model");
    }

    if(StringUtil.isNotEmpty(importedOrderIndex.getProductCode())){
      sb.append(" and i.productCode like :productCode");
    }
    if(StringUtil.isNotEmpty(importedOrderIndex.getPayPerProject())){
      sb.append(" and o.payPerProject like :payPerProject");
    }
    if(StringUtil.isNotEmpty(importedOrderIndex.getContact())){
      sb.append(" and o.contact like :contact");
    }
    if(importedOrderIndex.getAmountLower()!=null){
      sb.append(" and amountLower >= :amountLower");
    }
    if(importedOrderIndex.getAmountUpper()!=null){
      sb.append(" and amountUpper <= :amountUpper");
    }
    if(StringUtils.isNotBlank(importedOrderIndex.getReceipt())){
      sb.append(" and receipt=:receipt");
    }

//    importedOrderDTO.setPayMethod(searchConditionDTO.getPayMethod());
//    importedOrderDTO.setStartDate(searchConditionDTO.getStartTime());
//    importedOrderDTO.setEndDate(searchConditionDTO.getEndTime());
//    importedOrderDTO.setOrderTypes(searchConditionDTO.getOrderType());
    Query query= session.createQuery(sb.toString()).setLong("shopId",importedOrderIndex.getShopId());
    if(importedOrderIndex.getStartDate()!=null){
      query.setLong("startDate",importedOrderIndex.getStartDate());
    }
    if(importedOrderIndex.getEndDate()!=null){
      query.setLong("endDate",importedOrderIndex.getEndDate());
    }
    if(StringUtil.isNotEmpty(importedOrderIndex.getProductName())){
      query.setString("productName","%"+importedOrderIndex.getProductName()+"%");
    }
    if(StringUtil.isNotEmpty(importedOrderIndex.getVehicle())){
      query.setString("vehicle","%"+importedOrderIndex.getVehicle()+"%");
    }
    if(!StringUtil.isAllEmpty(importedOrderIndex.getSalesMan())){
      query.setParameterList("salesMan",importedOrderIndex.getSalesMan());
    }
    if(StringUtil.isNotEmpty(importedOrderIndex.getCustomerSupplierName())){
      query.setString("customerSupplierName","%"+importedOrderIndex.getCustomerSupplierName()+"%");
    }
    if(StringUtil.isNotEmpty(importedOrderIndex.getMemberType())){
      query.setString("memberType","%"+importedOrderIndex.getMemberType()+"%");
    }
    if(StringUtil.isNotEmpty(importedOrderIndex.getMemberCardNo())){
      query.setString("memberCardNo","%"+importedOrderIndex.getMemberCardNo()+"%");
    }
    if(StringUtil.isNotEmpty(importedOrderIndex.getBrand())){
      query.setString("brand","%"+importedOrderIndex.getBrand()+"%");
    }
    if(StringUtil.isNotEmpty(importedOrderIndex.getVehicleBrand())){
      query.setString("vehicleBrand","%"+importedOrderIndex.getVehicleBrand()+"%");
    }
    if(StringUtil.isNotEmpty(importedOrderIndex.getVehicleModel())){
      query.setString("vehicleModel","%"+importedOrderIndex.getVehicleModel()+"%");
    }
    if(StringUtil.isNotEmpty(importedOrderIndex.getSpec())){
      query.setString("spec","%"+importedOrderIndex.getSpec()+"%");
    }
    if(StringUtil.isNotEmpty(importedOrderIndex.getModel())){
      query.setString("model","%"+importedOrderIndex.getModel()+"%");
    }
    if(StringUtil.isNotEmpty(importedOrderIndex.getContact())){
      query.setString("contact","%"+importedOrderIndex.getContact()+"%");
    }
    if(StringUtil.isNotEmpty(importedOrderIndex.getProductCode())){
      query.setString("productCode","%"+importedOrderIndex.getProductCode()+"%");
    }
    if(!StringUtil.isAllEmpty(importedOrderIndex.getServiceWorkers())){
      query.setParameterList("serviceWorkers",importedOrderIndex.getServiceWorkers());
    }
    if(StringUtil.isNotEmpty(importedOrderIndex.getPayPerProject())){
      query.setString("payPerProject","%"+importedOrderIndex.getPayPerProject()+"%");
    }
    if(!StringUtil.isAllEmpty(importedOrderIndex.getOrderTypes())){
      query.setParameterList("orderTypes",importedOrderIndex.getOrderTypes());
    }
    if(importedOrderIndex.getAmountLower()!=null){
      query.setDouble("amountLower",importedOrderIndex.getAmountLower());
    }
    if(importedOrderIndex.getAmountUpper()!=null){
      query.setDouble("amountUpper",importedOrderIndex.getAmountUpper());
    }
    if(StringUtil.isNotEmpty(importedOrderIndex.getProductCode())){
      query.setString("productCode","%"+importedOrderIndex.getProductCode()+"%");
    }
    if(StringUtils.isNotBlank(importedOrderIndex.getReceipt())){
      query.setString("receipt", importedOrderIndex.getReceipt().trim());
    }
    return query;
  }

  public static Query getAllImportedOrderTemp(Session session,Long shopId) {
    return session.createQuery("from ImportedOrderTemp where shopId =:shopId").setLong("shopId",shopId);
  }

  public static Query getImportedOrderTempByReceipt(Session session,Long shopId,String receipt) {
    return session.createQuery("from ImportedOrderTemp where shopId =:shopId and receipt =:receipt")
        .setLong("shopId",shopId).setString("receipt",receipt);
  }

  public static Query getImportedOrderItemByOrderId(Session session,Long shopId,Long orderId) {
    return session.createQuery("from ImportedOrderItem where shopId =:shopId and orderId =:orderId")
        .setLong("shopId",shopId).setLong("orderId",orderId);
  }

  public static Query deleteImportedOrderTempByShopId(Session session,Long shopId) {
    return session.createQuery("delete from ImportedOrderTemp where shopId =:shopId ").setLong("shopId",shopId);
  }

  public static Query getImportedOrderByConditions(Session session,ImportedOrderDTO importedOrderIndex) {
    StringBuffer sb = new StringBuffer();
    sb.append("select distinct o from ImportedOrder o,ImportedOrderItem i where o.id= i.orderId and o.shopId =:shopId ");
    if(importedOrderIndex.getStartDate()!=null){
      sb.append(" and o.vestDate >=:startDate");
    }
    if(importedOrderIndex.getEndDate()!=null){
      sb.append(" and o.vestDate <=:endDate");
    }
    if(!StringUtil.isAllEmpty(importedOrderIndex.getOrderTypes())){
      sb.append(" and o.orderTypeStr in (:orderTypes)");
    }
    if(StringUtil.isNotEmpty(importedOrderIndex.getVehicle())){
      sb.append(" and o.vehicle like :vehicle");
    }
    if(StringUtil.isNotEmpty(importedOrderIndex.getCustomerSupplierName())){
      sb.append(" and o.customerSupplierName like :customerSupplierName");
    }
    if(StringUtil.isNotEmpty(importedOrderIndex.getMemberType())){
      sb.append(" and o.memberType like :memberType");
    }
    if(StringUtil.isNotEmpty(importedOrderIndex.getMemberCardNo())){
      sb.append(" and o.memberCardNo like :memberCardNo");
    }
    if(!StringUtil.isAllEmpty(importedOrderIndex.getSalesMan())){
      sb.append(" and o.salesMan in (:salesMan)") ;
    }
    if(StringUtil.isNotEmpty(importedOrderIndex.getProductName())){
      sb.append(" and i.productName like :productName");
    }
    if(StringUtil.isNotEmpty(importedOrderIndex.getBrand())){
      sb.append(" and i.brand like :brand");
    }
    if(StringUtil.isNotEmpty(importedOrderIndex.getVehicleBrand())){
      sb.append(" and i.vehicleBrand like :vehicleBrand");
    }
    if(StringUtil.isNotEmpty(importedOrderIndex.getVehicleModel())){
      sb.append(" and i.vehicleModel like :vehicleModel");
    }
    if(StringUtil.isNotEmpty(importedOrderIndex.getSpec())){
      sb.append(" and i.spec like :spec");
    }
    if(StringUtil.isNotEmpty(importedOrderIndex.getModel())){
      sb.append(" and i.model like :model");
    }

    if(StringUtil.isNotEmpty(importedOrderIndex.getProductCode())){
      sb.append(" and i.productCode like :productCode");
    }
    if(!StringUtil.isAllEmpty(importedOrderIndex.getServiceWorkers())){
      sb.append(" and i.serviceWorker in (:serviceWorkers)") ;
    }
    if(StringUtil.isNotEmpty(importedOrderIndex.getPayPerProject())){
      sb.append(" and o.payPerProject like :payPerProject");
    }
    if(StringUtil.isNotEmpty(importedOrderIndex.getContact())){
      sb.append(" and o.contact like :contact");
    }
    if(StringUtil.isNotEmpty(importedOrderIndex.getMobile())){
      sb.append(" and o.mobile like :mobile");
    }
    if(importedOrderIndex.getAmountLower()!=null){
      sb.append(" and amountLower >= :amountLower");
    }
    if(importedOrderIndex.getAmountUpper()!=null){
      sb.append(" and amountUpper <= :amountUpper");
    }
    if(StringUtils.isNotBlank(importedOrderIndex.getReceipt())){
      sb.append(" and receipt=:receipt");
    }

//    importedOrderDTO.setPayMethod(searchConditionDTO.getPayMethod());
//    importedOrderDTO.setStartDate(searchConditionDTO.getStartTime());
//    importedOrderDTO.setEndDate(searchConditionDTO.getEndTime());
//    importedOrderDTO.setOrderTypes(searchConditionDTO.getOrderType());
    Query query= session.createQuery(sb.toString()).setLong("shopId",importedOrderIndex.getShopId());

    if(importedOrderIndex.getStartDate()!=null){
      query.setLong("startDate",importedOrderIndex.getStartDate());
    }
    if(importedOrderIndex.getEndDate()!=null){
      query.setLong("endDate",importedOrderIndex.getEndDate());
    }
    if(!StringUtil.isAllEmpty(importedOrderIndex.getOrderTypes())){
      query.setParameterList("orderTypes",importedOrderIndex.getOrderTypes());
    }
    if(StringUtil.isNotEmpty(importedOrderIndex.getProductName())){
      query.setString("productName","%"+importedOrderIndex.getProductName()+"%");
    }
    if(StringUtil.isNotEmpty(importedOrderIndex.getVehicle())){
      query.setString("vehicle","%"+importedOrderIndex.getVehicle()+"%");
    }
    if(StringUtil.isNotEmpty(importedOrderIndex.getCustomerSupplierName())){
      query.setString("customerSupplierName","%"+importedOrderIndex.getCustomerSupplierName()+"%");
    }
    if(StringUtil.isNotEmpty(importedOrderIndex.getMemberType())){
      query.setString("memberType","%"+importedOrderIndex.getMemberType()+"%");
    }
    if(StringUtil.isNotEmpty(importedOrderIndex.getMemberCardNo())){
      query.setString("memberCardNo","%"+importedOrderIndex.getMemberCardNo()+"%");
    }
    if(StringUtil.isNotEmpty(importedOrderIndex.getBrand())){
      query.setString("brand","%"+importedOrderIndex.getBrand()+"%");
    }
    if(StringUtil.isNotEmpty(importedOrderIndex.getVehicleBrand())){
      query.setString("vehicleBrand","%"+importedOrderIndex.getVehicleBrand()+"%");
    }
    if(StringUtil.isNotEmpty(importedOrderIndex.getVehicleModel())){
      query.setString("vehicleModel","%"+importedOrderIndex.getVehicleModel()+"%");
    }
    if(StringUtil.isNotEmpty(importedOrderIndex.getSpec())){
      query.setString("spec","%"+importedOrderIndex.getSpec()+"%");
    }
    if(StringUtil.isNotEmpty(importedOrderIndex.getModel())){
      query.setString("model","%"+importedOrderIndex.getModel()+"%");
    }
    if(!StringUtil.isAllEmpty(importedOrderIndex.getServiceWorkers())){
      query.setParameterList("serviceWorkers",importedOrderIndex.getServiceWorkers());
    }
    if(StringUtil.isNotEmpty(importedOrderIndex.getContact())){
      query.setString("contact","%"+importedOrderIndex.getContact()+"%");
    }
    if(StringUtil.isNotEmpty(importedOrderIndex.getMobile())){
      query.setString("mobile","%"+importedOrderIndex.getMobile()+"%");
    }
    if(StringUtil.isNotEmpty(importedOrderIndex.getProductCode())){
      query.setString("productCode","%"+importedOrderIndex.getProductCode()+"%");
    }
    if(StringUtil.isNotEmpty(importedOrderIndex.getPayPerProject())){
      query.setString("payPerProject","%"+importedOrderIndex.getPayPerProject()+"%");
    }
    if(!StringUtil.isAllEmpty(importedOrderIndex.getSalesMan())){
      query.setParameterList("salesMan",importedOrderIndex.getSalesMan());
    }
//    if(StringUtil.isNotEmpty(importedOrderIndex.getStartDate())){
//      sb.append()
//    }
    if(importedOrderIndex.getAmountLower()!=null){
      query.setDouble("amountLower",importedOrderIndex.getAmountLower());
    }
    if(importedOrderIndex.getAmountUpper()!=null){
      query.setDouble("amountUpper",importedOrderIndex.getAmountUpper());
    }
    if(StringUtil.isNotEmpty(importedOrderIndex.getProductCode())){
      query.setString("productCode","%"+importedOrderIndex.getProductCode()+"%");
    }
    if(null!=importedOrderIndex.getPager()){
      query.setFirstResult(importedOrderIndex.getPager().getRowStart()).setMaxResults(importedOrderIndex.getPager().getPageSize());
    }
    if(StringUtils.isNotBlank(importedOrderIndex.getReceipt())){
      query.setString("receipt", importedOrderIndex.getReceipt().trim());
    }
    return query;
  }

  public static Query getImportedOrderStatByOrderType(Session session,ImportedOrderDTO importedOrderIndex) {
    StringBuffer sb = new StringBuffer();
    sb.append("select  o.orderTypeStr,count(distinct o.id) from ImportedOrder o,ImportedOrderItem i where o.id= i.orderId and o.shopId =:shopId ");
    if(importedOrderIndex.getStartDate()!=null){
      sb.append(" and o.vestDate >=:startDate");
    }
    if(importedOrderIndex.getEndDate()!=null){
      sb.append(" and o.vestDate <=:endDate");
    }
    if(!StringUtil.isAllEmpty(importedOrderIndex.getOrderTypes())){
      sb.append(" and o.orderTypeStr in (:orderTypes)");
    }
    if(StringUtil.isNotEmpty(importedOrderIndex.getVehicle())){
      sb.append(" and o.vehicle like :vehicle");
    }
    if(StringUtil.isNotEmpty(importedOrderIndex.getCustomerSupplierName())){
      sb.append(" and o.customerSupplierName like :customerSupplierName");
    }
    if(StringUtil.isNotEmpty(importedOrderIndex.getMemberType())){
      sb.append(" and o.memberType like :memberType");
    }
    if(StringUtil.isNotEmpty(importedOrderIndex.getMemberCardNo())){
      sb.append(" and o.memberCardNo like :memberCardNo");
    }
    if(StringUtil.isNotEmpty(importedOrderIndex.getProductName())){
      sb.append(" and i.productName like :productName");
    }
    if(StringUtil.isNotEmpty(importedOrderIndex.getBrand())){
      sb.append(" and i.brand like :brand");
    }
    if(StringUtil.isNotEmpty(importedOrderIndex.getVehicleBrand())){
      sb.append(" and i.vehicleBrand like :vehicleBrand");
    }
    if(StringUtil.isNotEmpty(importedOrderIndex.getVehicleModel())){
      sb.append(" and i.vehicleModel like :vehicleModel");
    }
    if(StringUtil.isNotEmpty(importedOrderIndex.getSpec())){
      sb.append(" and i.spec like :spec");
    }
    if(StringUtil.isNotEmpty(importedOrderIndex.getModel())){
      sb.append(" and i.model like :model");
    }

    if(StringUtil.isNotEmpty(importedOrderIndex.getProductCode())){
      sb.append(" and i.productCode like :productCode");
    }
    if(StringUtil.isNotEmpty(importedOrderIndex.getPayPerProject())){
      sb.append(" and o.payPerProject like :payPerProject");
    }
    if(StringUtil.isNotEmpty(importedOrderIndex.getContact())){
      sb.append(" and o.contact like :contact");
    }
    if(importedOrderIndex.getAmountLower()!=null){
      sb.append(" and amountLower >= :amountLower");
    }
    if(importedOrderIndex.getAmountUpper()!=null){
      sb.append(" and amountUpper <= :amountUpper");
    }

//    importedOrderDTO.setPayMethod(searchConditionDTO.getPayMethod());
//    importedOrderDTO.setStartDate(searchConditionDTO.getStartTime());
//    importedOrderDTO.setEndDate(searchConditionDTO.getEndTime());
//    importedOrderDTO.setOrderTypes(searchConditionDTO.getOrderType());
    sb.append(" group by o.orderTypeStr");
    Query query= session.createQuery(sb.toString()).setLong("shopId",importedOrderIndex.getShopId());

    if(importedOrderIndex.getStartDate()!=null){
      query.setLong("startDate",importedOrderIndex.getStartDate());
    }
    if(importedOrderIndex.getEndDate()!=null){
      query.setLong("endDate",importedOrderIndex.getEndDate());
    }
    if(!StringUtil.isAllEmpty(importedOrderIndex.getOrderTypes())){
      query.setParameterList("orderTypes",importedOrderIndex.getOrderTypes());
    }
    if(StringUtil.isNotEmpty(importedOrderIndex.getProductName())){
      query.setString("productName","%"+importedOrderIndex.getProductName()+"%");
    }
    if(StringUtil.isNotEmpty(importedOrderIndex.getVehicle())){
      query.setString("vehicle","%"+importedOrderIndex.getVehicle()+"%");
    }
    if(StringUtil.isNotEmpty(importedOrderIndex.getCustomerSupplierName())){
      query.setString("customerSupplierName","%"+importedOrderIndex.getCustomerSupplierName()+"%");
    }
    if(StringUtil.isNotEmpty(importedOrderIndex.getMemberType())){
      query.setString("memberType","%"+importedOrderIndex.getMemberType()+"%");
    }
    if(StringUtil.isNotEmpty(importedOrderIndex.getMemberCardNo())){
      query.setString("memberCardNo","%"+importedOrderIndex.getMemberCardNo()+"%");
    }
    if(StringUtil.isNotEmpty(importedOrderIndex.getBrand())){
      query.setString("brand","%"+importedOrderIndex.getBrand()+"%");
    }
    if(StringUtil.isNotEmpty(importedOrderIndex.getVehicleBrand())){
      query.setString("vehicleBrand","%"+importedOrderIndex.getVehicleBrand()+"%");
    }
    if(StringUtil.isNotEmpty(importedOrderIndex.getVehicleModel())){
      query.setString("vehicleModel","%"+importedOrderIndex.getVehicleModel()+"%");
    }
    if(StringUtil.isNotEmpty(importedOrderIndex.getSpec())){
      query.setString("spec","%"+importedOrderIndex.getSpec()+"%");
    }
    if(StringUtil.isNotEmpty(importedOrderIndex.getModel())){
      query.setString("model","%"+importedOrderIndex.getModel()+"%");
    }
    if(StringUtil.isNotEmpty(importedOrderIndex.getContact())){
      query.setString("contact","%"+importedOrderIndex.getContact()+"%");
    }
    if(StringUtil.isNotEmpty(importedOrderIndex.getProductCode())){
      query.setString("productCode","%"+importedOrderIndex.getProductCode()+"%");
    }
    if(StringUtil.isNotEmpty(importedOrderIndex.getPayPerProject())){
      query.setString("payPerProject","%"+importedOrderIndex.getPayPerProject()+"%");
    }
//    if(StringUtil.isNotEmpty(importedOrderIndex.getStartDate())){
//      sb.append()
//    }
    if(importedOrderIndex.getAmountLower()!=null){
      query.setDouble("amountLower",importedOrderIndex.getAmountLower());
    }
    if(importedOrderIndex.getAmountUpper()!=null){
      query.setDouble("amountUpper",importedOrderIndex.getAmountUpper());
    }
    if(StringUtil.isNotEmpty(importedOrderIndex.getProductCode())){
      query.setString("productCode","%"+importedOrderIndex.getProductCode()+"%");
    }
//    if(null!=importedOrderIndex.getPager()){
//      query.setFirstResult(importedOrderIndex.getPager().getRowStart()).setMaxResults(importedOrderIndex.getPager().getPageSize());
//    }
    return query;
  }

  public static Query getSalesOrderByProductIdCreationDate(Session session, Long shopId, Long productId, Long startTime, Long endTime) {
    return session.createQuery("select pi from SalesOrder pi, SalesOrderItem pii " +
        "where pi.id = pii.salesOrderId and pii.productId = :productId and pi.shopId = :shopId and pi.creationDate>=:startTime and pi.creationDate<:endTime " +
        "and (pi.statusEnum=:statusEnum or pi.statusEnum=:debtStatusEnum) ")
        .setLong("productId", productId).setLong("shopId", shopId).setLong("startTime", startTime).setLong("endTime", endTime)
        .setParameter("statusEnum", OrderStatus.SALE_DONE).setParameter("debtStatusEnum", OrderStatus.SALE_DEBT_DONE);
  }

  public static Query getRepairOrderByProductIdCreationDate(Session session, Long shopId, Long productId, Long startTime, Long endTime) {
    return session.createQuery("select pi from RepairOrder pi, RepairOrderItem pii " +
        "where pi.id = pii.repairOrderId and pii.productId = :productId and pi.shopId = :shopId and pi.creationDate>=:startTime and pi.creationDate<:endTime " +
        "and pi.statusEnum=:status ")
        .setLong("productId", productId).setLong("shopId", shopId).setLong("startTime", startTime).setLong("endTime", endTime)
        .setParameter("status", OrderStatus.REPAIR_SETTLED);
  }

  public static Query getCategoryByShopIdAndNameForInit(Session session,Long shopId,String name)
  {
    return session.createQuery("select c from Category c where c.shopId =:shopId and c.categoryName = :name")
        .setLong("shopId",shopId).setString("name",name);
  }

  public static Query getPurchaseInventoryIdFromPayableHistory(Session session, Long shopId, Long purchaseReturnId, PaymentTypes paymentType) {
    return session.createQuery("select p.purchaseInventoryId from PayableHistoryRecord p where p.shopId=:shopId and p.paymentType=:paymentType and p.purchaseReturnId=:purchaseReturnId")
        .setLong("shopId", shopId).setLong("purchaseReturnId", purchaseReturnId).setParameter("paymentType", paymentType);
  }
  public static Query getCategoryByNameNotDefault(Session session,String name)
  {
    return session.createQuery("select c from Category c where c.categoryName = :name and c.shopId != -1")
        .setString("name",name);
  }

  public static Query getCategoryItemRelationByCategoryId(Session session,Long categoryId)
  {
    return session.createQuery("select cir from CategoryItemRelation cir where cir.categoryId = :categoryId")
        .setLong("categoryId",categoryId);
  }

  public static Query getVehicleServeMonthStat(Session session, Long shopId, String brand, String model, int year, int month) {
    return session.createQuery("from VehicleServeMonthStat where shopId=:shopId and UPPER(brand)=:brand and UPPER(model) =:model " +
        "and statYear =:year and statMonth=:month").setLong("shopId", shopId).setString("brand",StringUtil.toUpperCase(brand))
        .setString("model", StringUtil.toUpperCase(model)).setInteger("year", year).setInteger("month", month);
  }

  public static Query getFirstWashBeautyOrderByVestDate(Session session, Long shopId) {
    return session.createQuery("from WashBeautyOrder where shopId = :shopId order by vestDate").setLong("shopId", shopId).setMaxResults(1);
  }

  public static Query getWashBeautyOrderByVestDate(Session session, Long shopId, long startTime, long endTime) {
    return session.createQuery("from WashBeautyOrder where shopId = :shopId and vestDate>=:startTime and vestDate<:endTime")
        .setLong("shopId", shopId).setLong("startTime", startTime).setLong("endTime", endTime);
  }

  public static Query queryTopVehicleServeMonthStat(Session session, Long shopId, int year, int month, int topLimit) {
    return session.createQuery("from VehicleServeMonthStat where shopId=:shopId and statYear=:year and statMonth = :month " +
        "and (brand != :empty or model != :empty) and totalTimes != 0 order by totalTimes desc")
        .setLong("shopId", shopId).setInteger("year", year).setInteger("month", month).setString("empty", "").setMaxResults(topLimit);
  }

  public static Query queryTopVehicleServeYearStat(Session session, Long shopId, int year, int topLimit){
    return session.createQuery("select shopId, brand, model, statYear, sum(washTimes) as washTimes, " +
        "sum(repairTimes) as repairTimes, sum(totalTimes) as totalTimes, sum(washTotal) as washTotal, sum(repairTotal) as repairTotal, " +
        "sum(totalConsume) as totalConsume from VehicleServeMonthStat where shopId=:shopId and statYear=:year " +
        "and (brand != :empty or model != :empty) and totalTimes != 0 " +
        "group by shopId, statYear, upper(brand), upper(model) order by totalTimes desc")
        .setLong("shopId", shopId).setInteger("year", year).setString("empty", "").setMaxResults(topLimit);
  }

  public static Query queryVehicleServeTotal(Session session, Long shopId, int year, int month, boolean allYear) {
    StringBuffer sb = new StringBuffer();
    sb.append("select sum(totalTimes) from VehicleServeMonthStat where shopId=:shopId and statYear=:year ");
    if(!allYear){
      sb.append("and statMonth=:month ");
    }
    sb.append("group by shopId, statYear ");
    if(!allYear){
      sb.append(",statMonth");
    }
    Query q = session.createQuery(sb.toString()).setLong("shopId", shopId).setInteger("year", year);
    if(!allYear){
      q.setInteger("month", month);
    }
    return q;
  }

  public static Query getVehicleServeMonthStatByBrandModel(Session session, Long shopId, String brand, String model) {
    return session.createQuery("from VehicleServeMonthStat where shopId=:shopId and UPPER(brand)=:brand and UPPER(model)=:model")
        .setLong("shopId", shopId).setString("brand", StringUtil.toUpperCase(brand)).setString("model", StringUtil.toUpperCase(model));
  }

  public static Query getFirstRepairOrderCreationTimeByVehicleId(Session session, Long shopId, Long vehicleId) {
    return session.createQuery("select creationDate from RepairOrder ro where shopId = :shopId and vechicleId =:vehicleId order by creationDate ")
        .setLong("shopId", shopId).setLong("vehicleId", vehicleId).setMaxResults(1);
  }

  public static Query getFirstWashBeautyOrderCreationTimeByVehicleId(Session session, Long shopId, Long vehicleId) {
    return session.createQuery("select creationDate from WashBeautyOrder ro where shopId = :shopId and vechicleId =:vehicleId order by creationDate ")
        .setLong("shopId", shopId).setLong("vehicleId", vehicleId).setMaxResults(1);
  }

  public static Query getVehicleServeMonthStatByBrandModelYearMonth(Session session, Long shopId, String brand, String model, int year, int month) {
    StringBuffer sb = new StringBuffer();
    sb.append("from VehicleServeMonthStat where shopId =:shopId ");
    if(brand==null){
      sb.append("and brand is null ");
    }else{
      sb.append("and UPPER(brand)=:brand ");
    }
    if(model==null){
      sb.append("and model is null ");
    }else{
      sb.append("and UPPER(model)=:model ");
    }
    sb.append("and statYear=:year and statMonth=:month");

    Query q = session.createQuery(sb.toString());
    q.setLong("shopId", shopId);
    if(brand!=null){
      q.setString("brand", StringUtil.toUpperCase(brand));
    }
    if(model!=null){
      q.setString("model", StringUtil.toUpperCase(model));
    }
    q.setInteger("year", year).setInteger("month", month);
    return q;
  }

  public static Query getRepairOrderListByCreationDate(Session session, Long shopId, long begin, long end) {
    return session.createQuery("from RepairOrder where shopId = :shopId and creationDate>=:begin and creationDate<:end")
        .setLong("shopId", shopId).setLong("begin", begin).setLong("end", end);
  }

  public static Query getWashBeautyOrderByCreationDate(Session session, Long shopId, long begin, long end) {
    return session.createQuery("from WashBeautyOrder where shopId = :shopId and creationDate>=:begin and creationDate<:end")
        .setLong("shopId", shopId).setLong("begin", begin).setLong("end", end);
  }

  public static Query deleteVehicleServeMonthStat(Session session) {
    return session.createQuery("delete from VehicleServeMonthStat");
  }

  public static Query deletePurchaseInventoryMonthStat(Session session) {
    return session.createQuery("delete from PurchaseInventoryMonthStat");
  }

  public static Query getPurchaseInventoryByCreationDate(Session session, Long shopId, long begin, long end) {
    return session.createQuery("from PurchaseInventory where shopId=:shopId and creationDate >= :begin and creationDate< :end and statusEnum=:status order by creationDate")
        .setLong("shopId", shopId).setLong("begin", begin).setLong("end", end).setParameter("status", OrderStatus.PURCHASE_INVENTORY_DONE);
  }

  public static Query getPurchaseInventoryMonthStatByPropertiesYearMonth(Session session, Long shopId, String name, String brand, String vehicleBrand, String vehicleModel, int statYear, int statMonth) {
    return session.createQuery("from PurchaseInventoryMonthStat where shopId = :shopId and UPPER(productName)=:name " +
        "and UPPER(productBrand)=:brand and UPPER(vehicleBrand) =:vehicleBrand and UPPER(vehicleModel)= :vehicleModel " +
        "and statYear = :statYear and statMonth=:statMonth")
        .setLong("shopId", shopId).setString("name", StringUtil.toUpperCase(name)).setString("brand", StringUtil.toUpperCase(brand))
        .setString("vehicleBrand", StringUtil.toUpperCase(vehicleBrand)).setString("vehicleModel", StringUtil.toUpperCase(vehicleModel))
        .setInteger("statYear", statYear).setInteger("statMonth", statMonth);
  }
  public static Query getMemberReturnOrderCountAndSum(Session session, long shopId, long startTime, long endTime,OrderSearchConditionDTO orderSearchConditionDTO) {
    StringBuffer sb = new StringBuffer();
    sb.append(" select count(m.id),sum(m.total) from member_card_return m where m.shop_id = :shopId  and m.return_date >= :startTime and m.return_date < :endTime");
    if (!ArrayUtils.isEmpty(orderSearchConditionDTO.getCustomerOrSupplierIds())) {
      sb.append(" and m.customer_id in(:customerIds)");
    }
    Query query = session.createSQLQuery(sb.toString())
        .setLong("shopId", shopId)
        .setLong("startTime", startTime)
        .setLong("endTime", endTime);
    if (!ArrayUtils.isEmpty(orderSearchConditionDTO.getCustomerOrSupplierIds())) {
      query.setParameterList("customerIds", Arrays.asList(orderSearchConditionDTO.getCustomerOrSupplierIds()));
    }
    return query;

  }



  public static Query getMemberReturnListByPagerTimeArrayType(Session session, long shopId, long startTime, long endTime, Pager pager, String arrayType,OrderSearchConditionDTO orderSearchConditionDTO) {
    StringBuffer sb = new StringBuffer();
    if (StringUtil.isEmpty(arrayType)) {
      arrayType = " order by created desc ";
    }
    sb.append(" select * from member_card_return where shop_id = :shopId and return_date >= :startTime and return_date < :endTime");
    if (!ArrayUtils.isEmpty(orderSearchConditionDTO.getCustomerOrSupplierIds())) {
      sb.append(" and customer_id in(:customerIds)");
    }
    sb.append(arrayType);
    Query query = session.createSQLQuery(sb.toString()).addEntity(MemberCardReturn.class)
        .setLong("shopId", shopId)
        .setLong("startTime", startTime)
        .setLong("endTime", endTime).setFirstResult(pager.getRowStart()).setMaxResults(pager.getPageSize());
    if (!ArrayUtils.isEmpty(orderSearchConditionDTO.getCustomerOrSupplierIds())) {
      query.setParameterList("customerIds", Arrays.asList(orderSearchConditionDTO.getCustomerOrSupplierIds()));
    }
    return query;
  }

  public static Query getSupplierRecordDTOBySupplierId(Session session,Long shopId,Long supplierId)
  {
    return session.createQuery("from SupplierRecord sr where sr.shopId=:shopId and sr.supplierId = :supplierId")
        .setLong("shopId",shopId).setLong("supplierId",supplierId);
  }

  public static Query getEnabledCategoryById(Session session,Long shopId,Long categoryId)
  {
    return session.createQuery("from Category c where (c.shopId = :shopId or c.shopId = -1) and c.id = :categoryId and (c.status is null or c.status != :status)")
        .setLong("shopId",shopId).setLong("categoryId",categoryId).setString("status",CategoryStatus.DISABLED.toString());
  }

  public static Query getMemberReceivableByOrderId(Session session,Long shopId,Long memberId){
    return session.createQuery("from Receivable  where shopId = :shopId and memberId = :memberId")
        .setLong("shopId",shopId).setLong("memberId",memberId);
  }




  public static Query getSalesReturnItemsBySalesReturnId(Session session, Long salesReturnId) {
    return session.createQuery("from SalesReturnItem p where p.salesReturnId=:salesReturnId")
        .setLong("salesReturnId", salesReturnId);
  }

  public static Query getSalesReturnDTOByPurchaseReturnOrderId(Session session, Long purchaseReturnOrderId) {
    return session.createQuery("from SalesReturn p where p.purchaseReturnOrderId=:purchaseReturnOrderId")
        .setLong("purchaseReturnOrderId", purchaseReturnOrderId);
  }

  public static Query getSalesReturnByPurchaseReturnOrderIdAndShopId(Session session,Long shopId, Long purchaseReturnOrderId) {
    return session.createQuery("from SalesReturn p where p.purchaseReturnOrderId=:purchaseReturnOrderId and p.shopId =:shopId")
        .setLong("purchaseReturnOrderId", purchaseReturnOrderId)
        .setLong("shopId",shopId);
  }

  public static Query getSalesReturnDTOById(Session session,Long shopId, Long id) {
    return session.createQuery("from SalesReturn p where p.shopId=:shopId and p.id=:id")
        .setLong("shopId", shopId)
        .setLong("id", id);
  }

  public static Query getTodoSalesOrderCount(Session session, Long shopId, Long startTime, Long endTime, List<Long> customerIdList, String receiptNo, String orderStatus) {
    StringBuffer sb = new StringBuffer();
    if(OrderStatus.SALE_DEBT_DONE.toString().equals(orderStatus)) {
      orderStatus = OrderStatus.SALE_DONE.toString();
      sb.append("select count(s.id) from sales_order s,receivable r where s.id=r.order_id and r.debt >0 and ");
    } else if(OrderStatus.SALE_DONE.toString().equals(orderStatus)) {
      sb.append("select count(s.id) from sales_order s,receivable r where s.id=r.order_id and r.debt =0 and ");
    } else if("allTodo".equals(orderStatus) || "inProgress".equals(orderStatus)) {
      sb.append("select count(s.id) from sales_order s left join receivable r on s.id=r.order_id where ");
    } else {
      sb.append("select count(s.id) from sales_order s where ");
    }
    if(startTime!=null){
      sb.append("s.created >= :startTime and ");
    }
    if(endTime!=null){
      sb.append("s.created <= :endTime and ");
    }
    if(CollectionUtils.isNotEmpty(customerIdList)){
      sb.append("s.customer_id in ("+(StringUtils.join(customerIdList.toArray(),","))+") and ");
    }
    if(StringUtil.isNotEmpty(receiptNo)){
      sb.append("s.receipt_no like :receiptNo and ");
    }
    if("allTodo".equals(orderStatus)){
      sb.append("(s.status_enum in ('"+OrderStatus.PENDING.toString()+"','"+OrderStatus.STOCKING.toString()+"','"+OrderStatus.DISPATCH.toString()+"') or ( s.status_enum = 'SALE_DONE' and s.id=r.order_id and r.debt >0)) and ");
    }else if("inProgress".equals(orderStatus)){
      sb.append("(s.status_enum in ('"+OrderStatus.STOCKING.toString()+"','"+OrderStatus.DISPATCH.toString()+"') or ( s.status_enum = 'SALE_DONE' and s.id=r.order_id and r.debt >0)) and ");
    }else if("all".equals(orderStatus)){
      sb.append("1=1 and ");
    }else{
      sb.append("s.status_enum = :orderStatus and ");
    }
    sb.append("s.shop_id =:shopId and ");
    sb.append("s.purchase_order_id is not null ");
    sb.append("order by s.last_update desc");

    Query q = session.createSQLQuery(sb.toString()).setLong("shopId",shopId);
    if(startTime!=null){
      q.setLong("startTime",startTime);
    }
    if(endTime!=null){
      q.setLong("endTime",endTime);
    }
    if(StringUtil.isNotEmpty(receiptNo)){
      q.setString("receiptNo","%"+receiptNo+"%");
    }
    if(StringUtil.isNotEmpty(orderStatus) && !"allTodo".equals(orderStatus) && !"inProgress".equals(orderStatus) && !"all".equals(orderStatus)){
      q.setString("orderStatus",orderStatus);
    }
    return q;
  }

  public static Query getTodoSalesOrderDTOListByCondition(Session session, Long shopId, Long startTime, Long endTime, List<Long> customerIdList, String receiptNo, String orderStatus, Pager pager){
    StringBuffer sb = new StringBuffer();
    if(OrderStatus.SALE_DEBT_DONE.toString().equals(orderStatus)) {
      orderStatus = OrderStatus.SALE_DONE.toString();
      sb.append("select s.* from sales_order s,receivable r where s.id=r.order_id and r.debt >0 and ");
    } else if(OrderStatus.SALE_DONE.toString().equals(orderStatus)) {
      sb.append("select s.* from sales_order s,receivable r where s.id=r.order_id and r.debt =0 and ");
    } else if("allTodo".equals(orderStatus) || "inProgress".equals(orderStatus)) {
      sb.append("select s.* from sales_order s left join receivable r on  s.id=r.order_id where ");
    } else {
      sb.append("select s.* from sales_order s where ");
    }

    if(startTime!=null){
      sb.append("s.created >= :startTime and ");
    }
    if(endTime!=null){
      sb.append("s.created <= :endTime and ");
    }
    if(CollectionUtils.isNotEmpty(customerIdList)){
      sb.append("s.customer_id in ("+(StringUtils.join(customerIdList.toArray(),","))+") and ");
    }
    if(StringUtil.isNotEmpty(receiptNo)){
      sb.append("s.receipt_no like :receiptNo and ");
    }
    if("allTodo".equals(orderStatus)){
      sb.append("(s.status_enum in ('"+OrderStatus.PENDING.toString()+"','"+OrderStatus.STOCKING.toString()+"','"+OrderStatus.DISPATCH.toString()+"') or ( s.status_enum = 'SALE_DONE' and s.id=r.order_id and r.debt >0)) and ");
    }else if("inProgress".equals(orderStatus)){
      sb.append("(s.status_enum in ('"+OrderStatus.STOCKING.toString()+"','"+OrderStatus.DISPATCH.toString()+"') or ( s.status_enum = 'SALE_DONE' and s.id=r.order_id and r.debt >0)) and ");
    }else if("all".equals(orderStatus)){
      sb.append("1=1 and ");
    }else{
      sb.append("s.status_enum = :orderStatus and ");
    }
    sb.append("s.shop_id =:shopId and ");
    sb.append("s.purchase_order_id is not null ");
    sb.append("order by s.last_update desc");

    Query q = session.createSQLQuery(sb.toString()).addEntity(SalesOrder.class).setLong("shopId",shopId);
    if(startTime!=null){
      q.setLong("startTime",startTime);
    }
    if(endTime!=null){
      q.setLong("endTime",endTime);
    }
    if(StringUtil.isNotEmpty(receiptNo)){
      q.setString("receiptNo", "%" + receiptNo + "%");
    }
    if(StringUtil.isNotEmpty(orderStatus) && !"allTodo".equals(orderStatus) && !"inProgress".equals(orderStatus) && !"all".equals(orderStatus)){
      q.setString("orderStatus",orderStatus);
    }
    if(pager!=null){
      q.setFirstResult(pager.getRowStart()).setMaxResults(pager.getPageSize());
    }

    return q;
  }

  public static Query getAllTodoSalesOrderDTOList(Session session, Long shopId, List<Long> customerIdList){
    StringBuffer sb = new StringBuffer();
    sb.append("select * from sales_order s where ");
    if(CollectionUtils.isNotEmpty(customerIdList)){
      sb.append("s.customer_id in ("+(StringUtils.join(customerIdList.toArray(),","))+") and ");
    }
    sb.append("s.status_enum in ('"+OrderStatus.PENDING.toString()+"','"+OrderStatus.STOCKING.toString()+"') and ");
    sb.append("s.shop_id =:shopId and ");
    sb.append("s.purchase_order_id is not null ");
    sb.append("order by s.last_update");
    Query q = session.createSQLQuery(sb.toString()).addEntity(SalesOrder.class).setLong("shopId",shopId);
    return q;
  }

  public static Query getTodoSalesReturnOrderCount(Session session, Long shopId, Long startTime, Long endTime, List<Long> customerIdList, String receiptNo, String orderStatus) {
    StringBuffer sb = new StringBuffer();
    sb.append("select count(s.id) from sales_return s where ");
    if(startTime!=null){
      sb.append("s.created >= :startTime and ");
    }
    if(endTime!=null){
      sb.append("s.created <= :endTime and ");
    }
    if(CollectionUtils.isNotEmpty(customerIdList)){
      sb.append("s.customer_id in ("+(StringUtils.join(customerIdList.toArray(),","))+") and ");
    }
    if(StringUtil.isNotEmpty(receiptNo)){
      sb.append("s.receipt_no like :receiptNo and ");
    }
    if("allTodo".equals(orderStatus)){
      sb.append("s.status in ('"+OrderStatus.PENDING.toString()+"','"+OrderStatus.WAITING_STORAGE.toString()+"') and ");
    }else if("all".equals(orderStatus)){
      sb.append("1=1 and ");
    }else{
      sb.append("s.status = :orderStatus and ");
    }
    sb.append("s.shop_id =:shopId order by s.last_update desc");

    Query q = session.createSQLQuery(sb.toString()).setLong("shopId",shopId);
    if(startTime!=null){
      q.setLong("startTime",startTime);
    }
    if(endTime!=null){
      q.setLong("endTime",endTime);
    }
    if(StringUtil.isNotEmpty(receiptNo)){
      q.setString("receiptNo","%"+receiptNo+"%");
    }
    if(StringUtil.isNotEmpty(orderStatus) && !"allTodo".equals(orderStatus) && !"all".equals(orderStatus)){
      q.setString("orderStatus",orderStatus);
    }

    return q;
  }

  public static Query getTodoSalesReturnOrderDTOListByCondition(Session session, Long shopId, Long startTime, Long endTime, List<Long> customerIdList, String receiptNo, String orderStatus, Pager pager){
    StringBuffer sb = new StringBuffer();
    sb.append("select * from sales_return s where ");
    if(startTime!=null){
      sb.append("s.created >= :startTime and ");
    }
    if(endTime!=null){
      sb.append("s.created <= :endTime and ");
    }
    if(CollectionUtils.isNotEmpty(customerIdList)){
      sb.append("s.customer_id in ("+(StringUtils.join(customerIdList.toArray(),","))+") and ");
    }
    if(StringUtil.isNotEmpty(receiptNo)){
      sb.append("s.receipt_no like :receiptNo and ");
    }
    if("allTodo".equals(orderStatus)){
      sb.append("s.status in ('"+OrderStatus.PENDING.toString()+"','"+OrderStatus.WAITING_STORAGE.toString()+"') and ");
    }else if("all".equals(orderStatus)){
      sb.append("1=1 and ");
    }else{
      sb.append("s.status = :orderStatus and ");
    }
    sb.append("s.shop_id =:shopId and 1=1 order by s.last_update desc");

    Query q = session.createSQLQuery(sb.toString()).addEntity(SalesReturn.class).setLong("shopId",shopId);
    if(startTime!=null){
      q.setLong("startTime",startTime);
    }
    if(endTime!=null){
      q.setLong("endTime",endTime);
    }
    if(StringUtil.isNotEmpty(receiptNo)){
      q.setString("receiptNo","%"+receiptNo+"%");
    }
    if(StringUtil.isNotEmpty(orderStatus) && !"allTodo".equals(orderStatus) && !"all".equals(orderStatus)){
      q.setString("orderStatus",orderStatus);
    }
    q.setFirstResult(pager.getRowStart()).setMaxResults(pager.getPageSize());

    return q;
  }

  public static Query getTodoPurchaseOrderCount(Session session, Long shopId, Long startTime, Long endTime, List<Long> supplierIdList, String receiptNo, String orderStatus, String timeField) {
    StringBuffer sb = new StringBuffer();
    sb.append("select count(s.id) from purchase_order s where ");
    if(startTime!=null){
      sb.append("s.").append(timeField).append(" >= :startTime and ");
    }
    if(endTime!=null){
      sb.append("s.").append(timeField).append(" <= :endTime and ");
    }
    if(CollectionUtils.isNotEmpty(supplierIdList)){
      sb.append("s.supplier_id in ("+(StringUtils.join(supplierIdList.toArray(),","))+") and ");
    }
    if(StringUtil.isNotEmpty(receiptNo)){
      sb.append("s.receipt_no like :receiptNo and ");
    }
    if("allTodo".equals(orderStatus)){
      sb.append("s.status_enum in ('"+OrderStatus.SELLER_PENDING.toString()+"','"+OrderStatus.SELLER_STOCK.toString()+"','"+OrderStatus.SELLER_DISPATCH.toString()+"','"+OrderStatus.SELLER_REFUSED+"') and ");
    }else if("inProgress".equals(orderStatus)){
      sb.append("s.status_enum in ('"+OrderStatus.SELLER_STOCK.toString()+"','"+OrderStatus.SELLER_DISPATCH.toString()+"','"+OrderStatus.SELLER_REFUSED+"') and ");
    }else if("all".equals(orderStatus)){
      sb.append("1=1 and ");
    }else{
      sb.append("s.status_enum = :orderStatus and ");
    }
    sb.append("s.supplier_shop_id is not null and ");
    sb.append("s.shop_id =:shopId");

    Query q = session.createSQLQuery(sb.toString()).setLong("shopId",shopId);
    if(startTime!=null){
      q.setLong("startTime",startTime);
    }
    if(endTime!=null){
      q.setLong("endTime",endTime);
    }
    if(StringUtil.isNotEmpty(receiptNo)){
      q.setString("receiptNo","%"+receiptNo+"%");
    }
    if(StringUtil.isNotEmpty(orderStatus) && !"allTodo".equals(orderStatus) && !"inProgress".equals(orderStatus) && !"all".equals(orderStatus)){
      q.setString("orderStatus",orderStatus);
    }

    return q;
  }

  public static Query getTodoPurchaseOrderDTOListByCondition(Session session, Long shopId, Long startTime, Long endTime, List<Long> supplierIdList, String receiptNo, String orderStatus, Pager pager, String timeField){
    StringBuffer sb = new StringBuffer();
    sb.append("select * from purchase_order s where ");
    if(startTime!=null){
      sb.append("s.").append(timeField).append(" >= :startTime and ");
    }
    if(endTime!=null){
      sb.append("s.").append(timeField).append(" <= :endTime and ");
    }
    if(CollectionUtils.isNotEmpty(supplierIdList)){
      sb.append("s.supplier_id in ("+(StringUtils.join(supplierIdList.toArray(),","))+") and ");
    }
    if(StringUtil.isNotEmpty(receiptNo)){
      sb.append("s.receipt_no like :receiptNo and ");
    }
    if("allTodo".equals(orderStatus)){
      sb.append("s.status_enum in ('"+OrderStatus.SELLER_PENDING.toString()+"','"+OrderStatus.SELLER_STOCK.toString()+"','"+OrderStatus.SELLER_DISPATCH.toString()+"','"+OrderStatus.SELLER_REFUSED+"') and ");
    }else if("inProgress".equals(orderStatus)){
      sb.append("s.status_enum in ('"+OrderStatus.SELLER_STOCK.toString()+"','"+OrderStatus.SELLER_DISPATCH.toString()+"','"+OrderStatus.SELLER_REFUSED+"') and ");
    }else if("all".equals(orderStatus)){
      sb.append("1=1 and ");
    }else{
      sb.append("s.status_enum = :orderStatus and ");
    }
    sb.append("s.supplier_shop_id is not null and ");
    sb.append("s.shop_id =:shopId order by s.last_update desc");

    Query q = session.createSQLQuery(sb.toString()).addEntity(PurchaseOrder.class).setLong("shopId",shopId);
    if(startTime!=null){
      q.setLong("startTime",startTime);
    }
    if(endTime!=null){
      q.setLong("endTime",endTime);
    }
    if(StringUtil.isNotEmpty(receiptNo)){
      q.setString("receiptNo","%"+receiptNo+"%");
    }
    if(StringUtil.isNotEmpty(orderStatus) && !"allTodo".equals(orderStatus) && !"inProgress".equals(orderStatus) && !"all".equals(orderStatus)){
      q.setString("orderStatus",orderStatus);
    }
    q.setFirstResult(pager.getRowStart()).setMaxResults(pager.getPageSize());

    return q;
  }

  public static Query getTodoPurchaseReturnOrderCount(Session session, Long shopId, Long startTime, Long endTime, List<Long> supplierIdList, String receiptNo, String orderStatus) {
    StringBuffer sb = new StringBuffer();
    sb.append("select count(s.id) from purchase_return s where ");
    if(startTime!=null){
      sb.append("s.created >= :startTime and ");
    }
    if(endTime!=null){
      sb.append("s.created <= :endTime and ");
    }
    if(CollectionUtils.isNotEmpty(supplierIdList)){
      sb.append("s.supplier_id in ("+(StringUtils.join(supplierIdList.toArray(),","))+") and ");
    }
    if(StringUtil.isNotEmpty(receiptNo)){
      sb.append("s.receipt_no like :receiptNo and ");
    }
    if("allTodo".equals(orderStatus)){
      sb.append("s.status in ('"+OrderStatus.SELLER_PENDING.toString()+"','"+OrderStatus.SELLER_ACCEPTED.toString()+"','"+OrderStatus.SELLER_REFUSED.toString()+"') and ");
    }else if("inProgress".equals(orderStatus)){
      sb.append("s.status in ('"+OrderStatus.SELLER_ACCEPTED.toString()+"','"+OrderStatus.SELLER_REFUSED.toString()+"') and ");
    }else if("all".equals(orderStatus)){
      sb.append("1=1 and ");
    }else{
      sb.append("s.status = :orderStatus and ");
    }
    sb.append("s.shop_id =:shopId and 1=1 order by s.last_update desc");

    Query q = session.createSQLQuery(sb.toString()).setLong("shopId",shopId);
    if(startTime!=null){
      q.setLong("startTime",startTime);
    }
    if(endTime!=null){
      q.setLong("endTime",endTime);
    }
    if(StringUtil.isNotEmpty(receiptNo)){
      q.setString("receiptNo","%"+receiptNo+"%");
    }
    if(StringUtil.isNotEmpty(orderStatus) && !"allTodo".equals(orderStatus) && !"inProgress".equals(orderStatus) && !"all".equals(orderStatus)){
      q.setString("orderStatus",orderStatus);
    }

    return q;
  }

  public static Query getTodoPurchaseReturnOrderDTOListByCondition(Session session, Long shopId, Long startTime, Long endTime, List<Long> supplierIdList, String receiptNo, String orderStatus, Pager pager){
    StringBuffer sb = new StringBuffer();
    sb.append("select * from purchase_return s where ");
    if(startTime!=null){
      sb.append("s.created >= :startTime and ");
    }
    if(endTime!=null){
      sb.append("s.created <= :endTime and ");
    }
    if(CollectionUtils.isNotEmpty(supplierIdList)){
      sb.append("s.supplier_id in ("+(StringUtils.join(supplierIdList.toArray(),","))+") and ");
    }
    if(StringUtil.isNotEmpty(receiptNo)){
      sb.append("s.receipt_no like :receiptNo and ");
    }
    if("allTodo".equals(orderStatus)){
      sb.append("s.status in ('"+OrderStatus.SELLER_PENDING.toString()+"','"+OrderStatus.SELLER_ACCEPTED.toString()+"','"+OrderStatus.SELLER_REFUSED.toString()+"') and ");
    }else if("inProgress".equals(orderStatus)){
      sb.append("s.status in ('"+OrderStatus.SELLER_ACCEPTED.toString()+"','"+OrderStatus.SELLER_REFUSED.toString()+"') and ");
    }else if("all".equals(orderStatus)){
      sb.append("1=1 and ");
    }else{
      sb.append("s.status = :orderStatus and ");
    }
    sb.append("s.shop_id =:shopId and 1=1 order by s.last_update desc");

    Query q = session.createSQLQuery(sb.toString()).addEntity(PurchaseReturn.class).setLong("shopId",shopId);
    if(startTime!=null){
      q.setLong("startTime",startTime);
    }
    if(endTime!=null){
      q.setLong("endTime",endTime);
    }
    if(StringUtil.isNotEmpty(receiptNo)){
      q.setString("receiptNo","%"+receiptNo+"%");
    }
    if(StringUtil.isNotEmpty(orderStatus) && !"allTodo".equals(orderStatus) && !"inProgress".equals(orderStatus) && !"all".equals(orderStatus)){
      q.setString("orderStatus",orderStatus);
    }
    q.setFirstResult(pager.getRowStart()).setMaxResults(pager.getPageSize());

    return q;
  }


  public static Query getMessageReceiver(Session session) {
    return session.createQuery(" from MessageReceiver");
  }

    /**
   * 初始化用的
   *
   * @param session
   * @return
   */
  @Deprecated
  public static Query getAllNotices(Session session) {
    return session.createQuery("from Notice");
  }
  /**
   * 初始化用的
   *
   * @param session
   * @return
   */
  @Deprecated
  public static Query getAllMessages(Session session) {
    return session.createQuery("from Message");
  }
  /**
   * 初始化用的
   *
   * @param session
   * @return
   */
  @Deprecated
  public static Query getAllNoticeReceiverByNoticeId(Session session,Long noticeId) {
    Query query = session.createQuery("from NoticeReceiver where noticeId=:noticeId");
    return query.setLong("noticeId",noticeId);
  }
  /**
   * 初始化用的
   *
   * @param session
   * @return
   */
  @Deprecated
  public static Query getAllMessageReceiverByMessageId(Session session,Long messageId) {
    Query query = session.createQuery("from MessageReceiver where messageId=:messageId");
    return query.setLong("messageId",messageId);
  }
  /**
   * 初始化用的
   *
   * @param session
   * @return
   */
  @Deprecated
  public static Query getAllMessageUserReceiverByMessageReceiverId(Session session,Long messageReceiverId) {
    Query query = session.createQuery("from MessageUserReceiver where messageReceiverId=:messageReceiverId");
    return query.setLong("messageReceiverId",messageReceiverId);
  }

  public static Query searchSenderPushMessages(Session session,SearchMessageCondition searchMessageCondition) throws Exception{
    StringBuilder sql = new StringBuilder();
    if (StringUtils.isNotEmpty(searchMessageCondition.getReceiver())) {
      sql.append("select p from PushMessage p,PushMessageReceiver pmr where p.id=pmr.messageId and p.type in(:types) and p.shopId=:shopId and p.deleted=:deleted ");
      if (searchMessageCondition.getDayRange() != null) {
        sql.append(" and p.createTime>=:createTime");
      }
      sql.append(" and pmr.localReceiverName like:localReceiverName");
    }else{
      sql.append("select p from PushMessage p where p.type in(:types) and p.shopId=:shopId and p.deleted=:deleted ");
      if (searchMessageCondition.getDayRange() != null) {
        sql.append(" and p.createTime>=:createTime");
      }
    }
    sql.append(" order by p.createTime desc");

    Query query = session.createQuery(sql.toString()).setLong("shopId", searchMessageCondition.getShopId()).setParameter("deleted", DeletedType.FALSE).setParameterList("types",Arrays.asList(new PushMessageType[]{PushMessageType.WARN_MESSAGE,PushMessageType.PROMOTIONS_MESSAGE}));
    if (StringUtils.isNotEmpty(searchMessageCondition.getReceiver())) {
      query.setString("localReceiverName",  "%"+searchMessageCondition.getReceiver()+ "%");
    }
    if(searchMessageCondition.getDayRange()!=null){
      query.setLong("createTime",DateUtil.getTheDayTime()-searchMessageCondition.getDayRange().getValue()*DateUtil.DAY_MILLION_SECONDS);
    }

    return query.setFirstResult((searchMessageCondition.getStartPageNo() - 1) * searchMessageCondition.getMaxRows()).setMaxResults(searchMessageCondition.getMaxRows());
  }

  public static Query countSenderPushMessages(Session session, SearchMessageCondition searchMessageCondition) throws Exception{
    StringBuilder sql = new StringBuilder();
    if (StringUtils.isNotEmpty(searchMessageCondition.getReceiver())) {
      sql.append("select count(distinct p.id) from PushMessage p,PushMessageReceiver pmr where p.id=pmr.messageId and p.type in(:types) and p.shopId=:shopId and p.deleted=:deleted ");
      if (searchMessageCondition.getDayRange() != null) {
        sql.append(" and p.createTime>=:createTime");
      }
      sql.append(" and pmr.localReceiverName like:localReceiverName");
    }else{
      sql.append("select count(distinct p.id) from PushMessage p where p.type in(:types) and p.shopId=:shopId and p.deleted=:deleted ");
      if (searchMessageCondition.getDayRange() != null) {
        sql.append(" and p.createTime>=:createTime");
      }
    }

    Query query = session.createQuery(sql.toString()).setLong("shopId", searchMessageCondition.getShopId()).setParameter("deleted", DeletedType.FALSE).setParameterList("types",Arrays.asList(new PushMessageType[]{PushMessageType.WARN_MESSAGE,PushMessageType.PROMOTIONS_MESSAGE}));
    if (StringUtils.isNotEmpty(searchMessageCondition.getReceiver())) {
      query.setString("localReceiverName",  "%"+searchMessageCondition.getReceiver()+ "%");
    }
    if(searchMessageCondition.getDayRange()!=null){
      query.setLong("createTime",DateUtil.getTheDayTime()-searchMessageCondition.getDayRange().getValue()*DateUtil.DAY_MILLION_SECONDS);
    }

    return query;
  }


  public static Query getPurchaseInventoryIdByPurchaseOrderId(Session session, Long shopId, Long purchaseOrderId){
    return session.createQuery("select pi from PurchaseInventory pi where pi.shopId =:shopId and pi.purchaseOrderId =:purchaseOrderId").setLong("shopId",shopId).setLong("purchaseOrderId",purchaseOrderId);
  }

  public static Query getPurchaseInventoryIdByPurchaseOrderIds(Session session, Long shopId, Long... purchaseOrderId){
    return session.createQuery("select pi from PurchaseInventory pi where pi.shopId =:shopId and pi.purchaseOrderId in(:purchaseOrderId)")
        .setLong("shopId",shopId).setParameterList("purchaseOrderId",purchaseOrderId);
  }

  public static Query getLackSalesOrderItemByProductIds(Session session, Long shopId, Long... productIds){
    StringBuffer sb = new StringBuffer();
    sb.append("select s.* from sales_order_item s,sales_order o ");
    sb.append("where s.sales_order_id = o.id ");
    sb.append(" and o.shop_id = :shopId ");
    if(!ArrayUtils.isEmpty(productIds)){
      sb.append("and product_id in(:productIds) ");
    }
    sb.append("and o.status_enum = '"+OrderStatus.STOCKING.toString()+"' ");
    sb.append("and s.amount > s.reserved order by s.created");
    Query q = session.createSQLQuery(sb.toString()).addEntity(SalesOrderItem.class);
    q.setLong("shopId",shopId);
    if(!ArrayUtils.isEmpty(productIds)){
      q.setParameterList("productIds",productIds);
    }
    return q;
  }


  public static Query getLackSalesOrderItemByProductIdsAndStorehouse(Session session, Long shopId,Long storehouseId, Long... productIds){
    StringBuffer sb = new StringBuffer();
    sb.append(" select s.* from sales_order_item s,sales_order o ");
    sb.append(" where o.shop_id =:shopId and s.sales_order_id = o.id and o.storehouse_id=:storehouseId ");
    if(!ArrayUtils.isEmpty(productIds)){
      sb.append(" and product_id in(:productIds) ");
    }
    sb.append(" and o.status_enum in ('"+OrderStatus.PENDING.toString()+"','"+OrderStatus.STOCKING.toString()+"') ");
    sb.append(" and s.amount > s.reserved order by s.created");
    Query q = session.createSQLQuery(sb.toString()).addEntity(SalesOrderItem.class);
    q.setLong("storehouseId",storehouseId);
    q.setLong("shopId",shopId);
    if(!ArrayUtils.isEmpty(productIds)){
      q.setParameterList("productIds",productIds);
    }
    return q;
  }

  public static Query getPayableDTOByOrderId(Session session, Long shopId, Long orderId, boolean containRepeal) {
    StringBuffer hql = new StringBuffer("select p from Payable p where p.shopId = :shopId and p.purchaseInventoryId =:orderId ");
    if(!containRepeal){
      hql.append("and p.status != :status");
    }
    Query q = session.createQuery(hql.toString()).setLong("shopId", shopId).setLong("orderId", orderId);
    if(!containRepeal){
      q.setString("status", PayStatus.REPEAL.toString());
    }
    return q;
  }

  public static Query vagueGetOtherIncomeKind(Session session,Long shopId,String keyWord) {
    if (StringUtils.isBlank(keyWord)) {
      return session.createQuery("select oik from OtherIncomeKind oik where (oik.shopId =:shopId or oik.shopId =:defaultShopId ) and oik.status =:status order by useTimes desc ")
          .setLong("shopId", shopId).setString("status", KindStatus.ENABLE.toString()).setLong("defaultShopId", ShopConstant.BC_SHOP_ID);
    } else {
      return session.createQuery("select oik from OtherIncomeKind oik where (oik.shopId =:shopId or oik.shopId =:defaultShopId ) and oik.status =:status and oik.kindName like :keyWord order by useTimes desc ")
          .setLong("shopId", shopId).setString("status", KindStatus.ENABLE.toString()).setString("keyWord", "%" + keyWord + "%").setLong("defaultShopId", ShopConstant.BC_SHOP_ID);
    }
  }

  public static Query getOtherIncomeKindById(Session session,Long shopId,Long id)
  {
    return session.createQuery("select oik from OtherIncomeKind oik where (oik.shopId =:shopId or oik.shopId =:defaultShopId ) and oik.id=:id")
        .setLong("shopId",shopId).setLong("id",id).setLong("defaultShopId", ShopConstant.BC_SHOP_ID);
  }

  public static Query getOtherIncomeKindByName(Session session,Long shopId,String name)
  {
    return session.createQuery("select oik from OtherIncomeKind oik where (oik.shopId =:shopId or oik.shopId =:defaultShopId ) and oik.kindName=:kindName")
        .setLong("shopId",shopId).setString("kindName",name).setLong("defaultShopId", ShopConstant.BC_SHOP_ID);
  }

  public static Query getOtherIncomeKindByNames(Session session, Long shopId, Set<String> names) {
    return session.createQuery("select oik from OtherIncomeKind oik where (oik.shopId =:shopId or oik.shopId =:defaultShopId ) and oik.kindName in (:names)")
        .setLong("shopId", shopId).setParameterList("names", names).setLong("defaultShopId", ShopConstant.BC_SHOP_ID);
  }

  public static Query getSaleOtherIncomeItemByOrderId(Session session,Long shopId,Long orderId)
  {
    return session.createQuery("select s from SalesOrderOtherIncomeItem s where s.shopId = :shopId and s.orderId = :orderId")
        .setLong("shopId",shopId).setLong("orderId",orderId);
  }

  public static Query deleteDraftOrderOtherIncomeItemsByDraftOrderId(Session session,Long shopId,Long orderId)
  {
    return session.createQuery("delete from DraftOrderOtherIncomeItem dot where dot.shopId=:shopId and dot.orderId = :orderId")
        .setLong("shopId",shopId).setLong("orderId",orderId);
  }

  public static Query getOtherIncomeItemsByDraftOrderId(Session session,Long orderId)
  {
    return session.createQuery("select dot from DraftOrderOtherIncomeItem dot where dot.orderId = :orderId")
        .setLong("orderId",orderId);
  }

  public static Query getRepairOtherIncomeItemByOrderId(Session session,Long shopId,Long orderId)
  {
    return session.createQuery("select r from RepairOrderOtherIncomeItem r where r.shopId = :shopId and r.orderId = :orderId")
        .setLong("shopId",shopId).setLong("orderId",orderId);
  }

  public static Query getRepairOrderTemplateOtherIncomeItem(Session session,Long shopId,Long templateId)
  {
    return session.createQuery("select r from RepairOrderTemplateOtherIncomeItem r where r.shopId = :shopId and r.repairOrderTemplateId =:templateId")
        .setLong("shopId",shopId).setLong("templateId",templateId);
  }

  public static Query getSalesOrderOtherIncomeItems(Session session, Long... ids) {
    StringBuffer sb = new StringBuffer();
    sb.append("from SalesOrderOtherIncomeItem si where si.orderId in (:ids) order by si.orderId asc");
    return session.createQuery(sb.toString()).setParameterList("ids", ids);
  }

  public static Query getRepairOrderOtherIncomeItemDTOByShopIdAndArrayOrderId(Session session, Long shopId, Long... orderId) {
    StringBuffer sb = new StringBuffer("from RepairOrderOtherIncomeItem where orderId in(:orderId)");
    if (shopId != null) {
      sb.append(" and shopId=:shopId");
    }
    Query query = session.createQuery(sb.toString()).setParameterList("orderId", orderId);
    if (shopId != null) {
      query.setLong("shopId", shopId);
    }
    return query;
  }

  public static Query getSupplierHistoryOrderList(Session session, Long supplierId, Long shopId, Long startTime, Long endTime, List<String> orderTypeList, Pager pager){
    StringBuffer sb = new StringBuffer();
    sb.append("select temp.id, temp.type, temp.vest_date, temp.total, temp.receipt_no ");
    sb.append("from (");
    String unionStr = "";
    String s1 = "";
    String s2 = "";
    String s3 = "";
    if(orderTypeList.contains(OrderTypes.PURCHASE.toString())){
      s1 = s1 + " union all ";
      s1 = s1 + "select p.id as id, 'PURCHASE' as type, p.vest_date as vest_date, p.total as total, p.receipt_no as receipt_no from purchase_order p where p.shop_id=:shopId and p.supplier_id=:supplierId ";
      if(startTime!=null){
        s1 = s1 + " and p.vest_date>=" + startTime;
      }
      if(endTime!=null){
        s1 = s1 + " and p.vest_date<=" + endTime;
      }
    }
    if(orderTypeList.contains(OrderTypes.INVENTORY.toString())){
      s2 = s2 + " union all ";
      s2 = s2 + "select i.id as id, 'INVENTORY' as type, i.vest_date as vest_date, i.total as total, i.receipt_no as receipt_no from purchase_inventory i where i.shop_id=:shopId and i.supplier_id=:supplierId and (i.status_enum is null or i.status_enum =:inventoryStatus)  ";
      if(startTime!=null){
        s2 = s2 + " and i.vest_date>=" + startTime;
      }
      if(endTime!=null){
        s2 = s2 + " and i.vest_date<=" + endTime;
      }
    }
    if(orderTypeList.contains(OrderTypes.RETURN.toString())){
      s3 = s3 + " union all ";
      s3 = s3 + "select r.id as id, 'RETURN' as type, r.vest_date as vest_date, r.total as total, r.receipt_no as receipt_no from purchase_return r where r.shop_id=:shopId and r.supplier_id=:supplierId and (r.status is null or r.status=:returnStatus) ";
      if(startTime!=null){
        s3 = s3 + " and r.vest_date>=" + startTime;
      }
      if(endTime!=null){
        s3 = s3 + " and r.vest_date<=" + endTime;
      }
    }
    unionStr = s1 + s2 + s3;
    unionStr = unionStr.substring(11);
    sb.append(unionStr);
    sb.append(") temp ");
    sb.append("order by temp.vest_date desc");

    Query q = session.createSQLQuery(sb.toString()).setLong("shopId",shopId).setLong("supplierId",supplierId);
    if(orderTypeList.contains(OrderTypes.INVENTORY.toString())){
      q.setParameter("inventoryStatus",OrderStatus.PURCHASE_INVENTORY_DONE.toString());
    }
    if(orderTypeList.contains(OrderTypes.RETURN.toString())){
      q.setParameter("returnStatus", OrderStatus.SETTLED.toString());
    }
    if(pager!=null){
      q.setFirstResult(pager.getRowStart()).setMaxResults(pager.getPageSize());
    }
    return q;
  }

  public static Query getSupplierTotalMoneyByTimeRangeAndOrderType(Session session, Long shopId, Long supplierId, Long startTime, Long endTime, String orderType){
    //todo 只有入库单在单据list的时候加了状态
    StringBuffer sb = new StringBuffer();
    String tableName = "";
    String statusSQL = "";
    List<OrderStatus> statusList = new ArrayList<OrderStatus>();
    if(orderType.equals(OrderTypes.PURCHASE.toString())){
      tableName = "PurchaseOrder";
//      statusSQL = " and statusEnum in (:status)";
//      statusList.add(OrderStatus.PURCHASE_ORDER_WAITING);
//      statusList.add(OrderStatus.SELLER_DISPATCH);
    }else if(orderType.equals(OrderTypes.INVENTORY.toString())){
      tableName = "PurchaseInventory";
      statusSQL = " and statusEnum in (:status)";
      statusList.add(OrderStatus.PURCHASE_INVENTORY_DONE);
    } else if (orderType.equals(OrderTypes.RETURN.toString())) {
      tableName = "PurchaseReturn";
//      statusSQL = " and status in (:status)";
//      statusList.add(OrderStatus.RETURN_STORAGE);
    }

    sb.append("select ROUND(sum(p.total),2) ");
    sb.append("from " + tableName + " p ");
    sb.append("where p.shopId = :shopId ");
    sb.append("and p.supplierId = :supplierId ");
    if(startTime!=null){
      sb.append(" and p.vestDate >=" + startTime);
    }
    if(endTime!=null){
      sb.append(" and p.vestDate <=" + endTime);
    }
    if (orderType.equals(OrderTypes.INVENTORY.toString())) {
      sb.append(statusSQL);
  }


    Query query = session.createQuery(sb.toString()).setLong("shopId", shopId).setLong("supplierId", supplierId);
    if (orderType.equals(OrderTypes.INVENTORY.toString())) {
      query.setParameterList("status", statusList);
    }
    return query;
  }


  public static Query getProductHistoryByProductLocalInfoIdAndVersions(Session session, Long productLocalInfoId, Long shopId, Long productVersion, Long productLocalInfoVersion, Long inventoryVersion) {
    StringBuffer sb = new StringBuffer("from ProductHistory where productLocalInfoId = :productLocalInfoId and shopId = :shopId and productVersion = :productVersion and productLocalInfoVersion = :productLocalInfoVersion ");
    if(inventoryVersion != null){
      sb.append("and inventoryVersion = :inventoryVersion");
    }
    Query q = session.createQuery(sb.toString());
    q.setLong("productLocalInfoId", productLocalInfoId).setLong("shopId", shopId).setLong("productVersion", productVersion)
        .setLong("productLocalInfoVersion", productLocalInfoVersion);
    if(inventoryVersion != null){
      q.setLong("inventoryVersion", inventoryVersion);
    }
    return q;
  }

  public static Query getProductHistoriesByProductLocalInfoIdAndVersions(Session session, Collection<ProductHistory> productHistories) {
    StringBuffer sb = new StringBuffer();
    List<ProductHistory> searchConditon = new ArrayList<ProductHistory>(productHistories);
    for (int i = 0; i < searchConditon.size(); i++) {
      sb.append("select * from product_history where product_local_info_id = :p" + i + " and shop_id = :s" + i
          + " and product_version = :pv" + i + " and product_local_info_version = :plv" + i + " and inventory_version = :i" + i);
      if (i < searchConditon.size() - 1) {
        sb.append(" union all ");
      }
    }
    Query q = session.createSQLQuery(sb.toString()).addEntity(ProductHistory.class);
    for (int i = 0; i < searchConditon.size(); i++) {
      ProductHistory productHistory = searchConditon.get(i);
      q.setLong("p" + i, productHistory.getProductLocalInfoId())
          .setLong("s" + i, productHistory.getShopId()).setLong("pv" + i, productHistory.getProductVersion())
          .setLong("plv" + i, productHistory.getProductLocalInfoVersion())
          .setLong("i" + i, productHistory.getInventoryVersion());
    }
    return q;
  }

   public static Query getProductHistoryById(Session session, Long productHistoryId, Long shopId) {
    return session.createQuery("from ProductHistory where id=:productHistoryId and shopId=:shopId")
        .setLong("productHistoryId", productHistoryId).setLong("shopId", shopId);
  }

  public static Query getServiceHistoryByIdAndVersion(Session session, Long serviceId, Long shopId, Long version) {
    return session.createQuery("from ServiceHistory where serviceId = :serviceId and shopId = :shopId and historyVersion=:version")
        .setLong("serviceId", serviceId).setLong("shopId", shopId).setLong("version", version);
  }

  public static Query getServiceHistoryById(Session session, Long id, Long shopId) {
    return session.createQuery("from ServiceHistory where id = :id and shopId = :shopId")
        .setLong("id", id).setLong("shopId", shopId);
  }

  public static Query getRepairPicks(Session session, RepairPickingDTO searchCondition) {
    List<OrderStatus> allSearchStatus = new ArrayList<OrderStatus>();
    allSearchStatus.add(OrderStatus.WAIT_OUT_STORAGE);
    allSearchStatus.add(OrderStatus.WAIT_RETURN_STORAGE);
    allSearchStatus.add(OrderStatus.OUT_STORAGE);
    allSearchStatus.add(OrderStatus.RETURN_STORAGE);
    StringBuffer sb = new StringBuffer();
    sb.append("select distinct rp from RepairPicking rp,RepairPickingItem rpi where rp.shopId = :shopId ");
    sb.append(" and rp.id = rpi.repairPickingId ");
    if (StringUtils.isNotBlank(searchCondition.getReceiptNo())) {
      sb.append(" and rp.receiptNo like :receiptNo ");
    }
    if (StringUtils.isNotBlank(searchCondition.getRepairOrderReceiptNo())) {
      sb.append(" and rp.repairOrderReceiptNo like :repairOrderReceiptNo ");
    }
    if (StringUtils.isNotBlank(searchCondition.getProductSeller())) {
          sb.append(" and rp.productSeller like :productSeller ");
    }
    if (StringUtils.isNotBlank(searchCondition.getPickingMan())) {
         sb.append(" and rpi.pickingMan like :pickingMan ");
    }
    if (CollectionUtils.isNotEmpty(searchCondition.getSearchStatus())) {
      sb.append(" and rpi.status in (:status)");
    }else {
      sb.append(" and rpi.status in (:allSearchStatus)");
    }
    if (searchCondition.getStorehouseId() != null) {
      sb.append(" and rp.storehouseId = :storehouseId");
    }
    if (searchCondition.getStartTime() != null) {
      sb.append(" and rp.vestDate >=:startTime");
    }
    if (searchCondition.getEndTime() != null) {
      sb.append(" and rp.vestDate <:endTime");
    }
    sb.append(" order by rp.vestDate desc");
    Query q = session.createQuery(sb.toString()).setLong("shopId", searchCondition.getShopId());
    if (StringUtils.isNotBlank(searchCondition.getReceiptNo())) {
      q.setString("receiptNo", "%" + searchCondition.getReceiptNo() + "%");
    }
    if (StringUtils.isNotBlank(searchCondition.getRepairOrderReceiptNo())) {
      q.setString("repairOrderReceiptNo", "%" + searchCondition.getRepairOrderReceiptNo().toUpperCase() + "%");
    }
    if (StringUtils.isNotBlank(searchCondition.getProductSeller())) {
      q.setString("productSeller", "%" + searchCondition.getProductSeller() + "%");
    }
    if (StringUtils.isNotBlank(searchCondition.getPickingMan())) {
      q.setString("pickingMan", "%" + searchCondition.getPickingMan() + "%");
    }
    if (CollectionUtils.isNotEmpty(searchCondition.getSearchStatus())) {
      q.setParameterList("status", searchCondition.getSearchStatus());
    }else {
      q.setParameterList("allSearchStatus", allSearchStatus);
    }
    if (searchCondition.getStorehouseId() != null) {
      q.setLong("storehouseId", searchCondition.getStorehouseId());
    }
    if (searchCondition.getStartTime() != null) {
      q.setLong("startTime", searchCondition.getStartTime());
    }
    if (searchCondition.getEndTime() != null) {
      q.setLong("endTime", searchCondition.getEndTime());
    }
    int firstResult = NumberUtil.intValue(searchCondition.getPageNo()) == 0? 0:
        (searchCondition.getPageNo()-1)* NumberUtil.intValue(searchCondition .getPageSize());
    q.setFirstResult(firstResult).setMaxResults(NumberUtil.intValue(searchCondition .getPageSize()));
    return q;
  }

  public static Query getInnerPickings(Session session, InnerPickingDTO searchCondition) {
    StringBuffer sb = new StringBuffer();
    sb.append("from InnerPicking where shopId = :shopId ");
    if (StringUtils.isNotBlank(searchCondition.getReceiptNo())) {
      sb.append(" and receiptNo like :receiptNo ");
    }
    if (searchCondition.getStorehouseId() != null) {
      sb.append(" and storehouseId = :storehouseId");
    }
    if(StringUtils.isNotBlank(searchCondition.getOperationMan())){
      sb.append(" and operationMan like :operationMan");
    }
    if(StringUtils.isNotBlank(searchCondition.getPickingMan())){
      sb.append(" and pickingMan like :pickingMan");
    }
    if (searchCondition.getStartTime() != null) {
      sb.append(" and vestDate >=:startTime");
    }
    if (searchCondition.getEndTime() != null) {
      sb.append(" and vestDate <:endTime");
    }
    if (StringUtils.isNotBlank(searchCondition.getSortStatus())) {
      sb.append(" order by ").append(searchCondition.getSortStatus());
    } else {
      sb.append(" order by vestDate Desc");
    }
    Query q = session.createQuery(sb.toString()).setLong("shopId", searchCondition.getShopId());
    if (StringUtils.isNotBlank(searchCondition.getReceiptNo())) {
      q.setString("receiptNo", "%" + searchCondition.getReceiptNo() + "%");
    }
    if (searchCondition.getStorehouseId() != null) {
      q.setLong("storehouseId", searchCondition.getStorehouseId());
    }
    if (StringUtils.isNotBlank(searchCondition.getOperationMan())) {
      q.setString("operationMan", "%" + searchCondition.getOperationMan() + "%");
    }
    if (StringUtils.isNotBlank(searchCondition.getPickingMan())) {
      q.setString("pickingMan", "%" + searchCondition.getPickingMan() + "%");
    }
    if (searchCondition.getStartTime() != null) {
      q.setLong("startTime", searchCondition.getStartTime());
    }
    if (searchCondition.getEndTime() != null) {
      q.setLong("endTime", searchCondition.getEndTime());
    }
    int firstResult = NumberUtil.intValue(searchCondition.getPageNo()) == 0? 0:
        (searchCondition.getPageNo()-1)* NumberUtil.intValue(searchCondition .getPageSize());
    q.setFirstResult(firstResult).setMaxResults(NumberUtil.intValue(searchCondition .getPageSize()));
    return q;
  }

  public static Query getInnerReturns(Session session, InnerReturnDTO searchCondition) {
    StringBuffer sb = new StringBuffer();
    sb.append("from InnerReturn where shopId = :shopId ");
    if (StringUtils.isNotBlank(searchCondition.getReceiptNo())) {
      sb.append(" and receiptNo like :receiptNo ");
    }
    if (searchCondition.getStorehouseId() != null) {
      sb.append(" and storehouseId = :storehouseId");
    }
    if(StringUtils.isNotBlank(searchCondition.getOperationMan())){
      sb.append(" and operationMan like :operationMan");
    }
    if(StringUtils.isNotBlank(searchCondition.getPickingMan())){
      sb.append(" and pickingMan like :pickingMan");
    }
    if (searchCondition.getStartTime() != null) {
      sb.append(" and vestDate >=:startTime");
    }
    if (searchCondition.getEndTime() != null) {
      sb.append(" and vestDate <:endTime");
    }
    if(StringUtils.isNotBlank(searchCondition.getSortStatus())){
      sb.append(" order by ").append(searchCondition.getSortStatus());
    } else {
      sb.append(" order by vestDate Desc");
    }

    Query q = session.createQuery(sb.toString()).setLong("shopId", searchCondition.getShopId());
    if (StringUtils.isNotBlank(searchCondition.getReceiptNo())) {
      q.setString("receiptNo", "%" + searchCondition.getReceiptNo() + "%");
    }
    if (searchCondition.getStorehouseId() != null) {
      q.setLong("storehouseId", searchCondition.getStorehouseId());
    }
    if (StringUtils.isNotBlank(searchCondition.getOperationMan())) {
      q.setString("operationMan", "%" + searchCondition.getOperationMan() + "%");
    }
    if (StringUtils.isNotBlank(searchCondition.getPickingMan())) {
      q.setString("pickingMan", "%" + searchCondition.getPickingMan() + "%");
    }
    if (searchCondition.getStartTime() != null) {
      q.setLong("startTime", searchCondition.getStartTime());
    }
    if (searchCondition.getEndTime() != null) {
      q.setLong("endTime", searchCondition.getEndTime());
    }
    int firstResult = NumberUtil.intValue(searchCondition.getPageNo()) == 0? 0:
        (searchCondition.getPageNo()-1)* NumberUtil.intValue(searchCondition .getPageSize());
    q.setFirstResult(firstResult).setMaxResults(NumberUtil.intValue(searchCondition .getPageSize()));
    return q;
  }

  public static Query countBorrowOrders(Session session, BorrowOrderDTO searchCondition) {
    StringBuffer sb = new StringBuffer();
    sb.append("select count(*) from BorrowOrder where shopId = :shopId ");
    if (StringUtils.isNotBlank(searchCondition.getReceiptNo())) {
      sb.append(" and receiptNo like :receiptNo ");
    }
    if (searchCondition.getStorehouseId() != null) {
      sb.append(" and storehouseId = :storehouseId");
    }
    if (StringUtils.isNotBlank(searchCondition.getOperator())) {
      sb.append(" and operator like :operator");
    }
    if (StringUtils.isNotBlank(searchCondition.getBorrower())) {
      sb.append(" and borrower like :borrower");
    }
    if(searchCondition.getReturnStatus()!=null){
      sb.append(" and returnStatus = :returnStatus");
    }
    if(StringUtil.isNotEmpty(searchCondition.getBorrowerType())){
      sb.append(" and borrowerType = :borrowerType");
    }
    if (searchCondition.getStartTime() != null) {
      sb.append(" and vestDate >=:startTime");
    }
    if (searchCondition.getEndTime() != null) {
      sb.append(" and vestDate <:endTime");
    }
    Query q = session.createQuery(sb.toString()).setLong("shopId", searchCondition.getShopId());
    if (StringUtils.isNotBlank(searchCondition.getReceiptNo())) {
      q.setString("receiptNo", "%" + searchCondition.getReceiptNo() + "%");
    }
    if (searchCondition.getStorehouseId() != null) {
      q.setLong("storehouseId", searchCondition.getStorehouseId());
    }
    if (StringUtils.isNotBlank(searchCondition.getOperator())) {
      q.setString("operator", "%" + searchCondition.getOperator() + "%");
    }
    if (StringUtils.isNotBlank(searchCondition.getBorrower())) {
      q.setString("borrower", "%" + searchCondition.getBorrower() + "%");
    }
    if(searchCondition.getReturnStatus()!=null){
      q.setString("returnStatus",searchCondition.getReturnStatus().toString());
    }
    if(StringUtil.isNotEmpty(searchCondition.getBorrowerType())){
      q.setString("borrowerType", searchCondition.getBorrowerType());
    }
    if (searchCondition.getStartTime() != null) {
      q.setLong("startTime", searchCondition.getStartTime());
    }
    if (searchCondition.getEndTime() != null) {
      q.setLong("endTime", searchCondition.getEndTime());
    }
    return q;
  }

  public static Query getBorrowOrders(Session session, BorrowOrderDTO searchCondition) {
    StringBuffer sb = new StringBuffer();
    sb.append("from BorrowOrder where shopId = :shopId");
    if(searchCondition.getId()!=null){
      sb.append(" and id=:orderId ");
    }
    if (StringUtils.isNotBlank(searchCondition.getReceiptNo())) {
      sb.append(" and receiptNo like :receiptNo");
    }
    if (searchCondition.getStorehouseId() != null) {
      sb.append(" and storehouseId = :storehouseId");
    }
    if(StringUtils.isNotBlank(searchCondition.getOperator())){
      sb.append(" and operator like :operator");
    }
    if(StringUtils.isNotBlank(searchCondition.getBorrower())){
      sb.append(" and borrower like :borrower");
    }
    if(searchCondition.getReturnStatus()!=null){
      sb.append(" and returnStatus = :returnStatus");
    }
    if(StringUtil.isNotEmpty(searchCondition.getBorrowerType())){
      sb.append(" and borrowerType = :borrowerType");
    }
    if (searchCondition.getStartTime() != null) {
      sb.append(" and vestDate >=:startTime");
    }
    if (searchCondition.getEndTime() != null) {
      sb.append(" and vestDate <:endTime");
    }
    if (StringUtils.isNotBlank(searchCondition.getSortStatus())) {
      sb.append(" order by ").append(searchCondition.getSortStatus());
    } else {
      sb.append(" order by vestDate Desc");
    }
    Query q = session.createQuery(sb.toString()).setLong("shopId", searchCondition.getShopId());
    if(searchCondition.getId()!=null){
      q.setLong("orderId",searchCondition.getId());
    }
    if (StringUtils.isNotBlank(searchCondition.getReceiptNo())) {
      q.setString("receiptNo", "%" + searchCondition.getReceiptNo() + "%");
    }
    if (searchCondition.getStorehouseId() != null) {
      q.setLong("storehouseId", searchCondition.getStorehouseId());
    }
    if (StringUtils.isNotBlank(searchCondition.getOperator())) {
      q.setString("operator", "%" + searchCondition.getOperator() + "%");
    }
    if (StringUtils.isNotBlank(searchCondition.getBorrower())) {
      q.setString("borrower", "%" + searchCondition.getBorrower() + "%");
    }
    if(searchCondition.getReturnStatus()!=null){
      q.setString("returnStatus",searchCondition.getReturnStatus().toString());
    }
    if(StringUtil.isNotEmpty(searchCondition.getBorrowerType())){
      q.setString("borrowerType", searchCondition.getBorrowerType());
    }
    if (searchCondition.getStartTime() != null) {
      q.setLong("startTime", searchCondition.getStartTime());
    }
    if (searchCondition.getEndTime() != null) {
      q.setLong("endTime", searchCondition.getEndTime());
    }
    int firstResult = NumberUtil.intValue(searchCondition.getStartPageNo()) == 0? 0:
        (searchCondition.getStartPageNo()-1)* NumberUtil.intValue(searchCondition .getPageSize());
    q.setFirstResult(firstResult).setMaxResults(NumberUtil.intValue(searchCondition .getPageSize()));
    return q;
  }

  public static Query getBorrowOrderStat(Session session, BorrowOrderDTO searchCondition) {
    StringBuffer sb = new StringBuffer();
    sb.append("select returnStatus,count(returnStatus) from BorrowOrder where shopId = :shopId");
    if(searchCondition.getId()!=null){
      sb.append(" and id=:orderId ");
    }
    if (StringUtils.isNotBlank(searchCondition.getReceiptNo())) {
      sb.append(" and receiptNo like :receiptNo");
    }
    if (searchCondition.getStorehouseId() != null) {
      sb.append(" and storehouseId = :storehouseId");
    }
    if(StringUtils.isNotBlank(searchCondition.getOperator())){
      sb.append(" and operator like :operator");
    }
    if(StringUtils.isNotBlank(searchCondition.getBorrower())){
      sb.append(" and borrower like :borrower");
    }
    if(searchCondition.getReturnStatus()!=null){
      sb.append(" and returnStatus = :returnStatus");
    }
    if(StringUtil.isNotEmpty(searchCondition.getBorrowerType())){
      sb.append(" and borrowerType = :borrowerType");
    }
    if (searchCondition.getStartTime() != null) {
      sb.append(" and vestDate >=:startTime");
    }
    if (searchCondition.getEndTime() != null) {
      sb.append(" and vestDate <:endTime");
    }
   sb.append(" group by returnStatus");
    Query q = session.createQuery(sb.toString()).setLong("shopId", searchCondition.getShopId());
    if(searchCondition.getId()!=null){
      q.setLong("orderId",searchCondition.getId());
    }
    if (StringUtils.isNotBlank(searchCondition.getReceiptNo())) {
      q.setString("receiptNo", "%" + searchCondition.getReceiptNo() + "%");
    }
    if (searchCondition.getStorehouseId() != null) {
      q.setLong("storehouseId", searchCondition.getStorehouseId());
    }
    if (StringUtils.isNotBlank(searchCondition.getOperator())) {
      q.setString("operator", "%" + searchCondition.getOperator() + "%");
    }
    if (StringUtils.isNotBlank(searchCondition.getBorrower())) {
      q.setString("borrower", "%" + searchCondition.getBorrower() + "%");
    }
    if(searchCondition.getReturnStatus()!=null){
      q.setString("returnStatus",searchCondition.getReturnStatus().toString());
    }
    if(StringUtil.isNotEmpty(searchCondition.getBorrowerType())){
      q.setString("borrowerType", searchCondition.getBorrowerType());
    }
    if (searchCondition.getStartTime() != null) {
      q.setLong("startTime", searchCondition.getStartTime());
    }
    if (searchCondition.getEndTime() != null) {
      q.setLong("endTime", searchCondition.getEndTime());
    }
    return q;
  }

  public static Query getBorrowOrderByBorrower(Session session,Long shopId,String borrower) {
    StringBuffer sb=new StringBuffer("select b from BorrowOrder b where shopId=:shopId");
    if(StringUtil.isNotEmpty(borrower)){
      sb.append(" and borrower like :borrower");
    }

    sb.append(" group by borrower");
    Query query=session.createQuery(sb.toString()).setLong("shopId",shopId);
    if(StringUtil.isNotEmpty(borrower)){
      query.setString("borrower","%"+borrower+"%");
    }
    query.setFirstResult(0).setMaxResults(10);
    return query;
  }

   public static Query countBorrowOrderByOrderType(Session session, Long shopId, Long userId, String[] orderTypes, Long startTime, Long endTime) {
    StringBuffer sb = new StringBuffer("select order_type_enum,count(order_type_enum) from draft_order where status='DRAFT_SAVED' and shop_id =:shopId  and user_id=:userId and order_type_enum in (:orderTypes) ");
    if (startTime != null) {
      sb.append(" and save_time >= :startTime");
    }
    if (endTime != null) {
      sb.append(" and save_time <=:endTime");
    }
    sb.append(" group by order_type_enum");
    Query query = session.createSQLQuery(sb.toString()).setLong("shopId", shopId).setLong("userId", userId).setParameterList("orderTypes", orderTypes);
    if (startTime != null) {
      query.setLong("startTime", startTime);
    }
    if (endTime != null) {
      query.setLong("endTime", endTime);
    }
    return query;
  }

  public static Query getReturnOrderByBorrowOrderId(Session session,Long shopId,Long borrowOrderId){
    String hql="from ReturnOrder where shopId=:shopId and borrowOrderId=:borrowOrderId order by vestDate desc";
    return session.createQuery(hql).setLong("shopId",shopId).setLong("borrowOrderId",borrowOrderId);
  }

  public static Query getReturnOrderByBorrowOrderIds(Session session,Long shopId,Long... borrowOrderId){
    String hql="from ReturnOrder where shopId=:shopId and borrowOrderId in(:borrowOrderId)";
    return session.createQuery(hql).setLong("shopId",shopId).setParameterList("borrowOrderId",borrowOrderId);
  }

  public static Query getBorrowOrderById(Session session,Long shopId,Long borrowOrderId){
    String hql="from BorrowOrder where shopId=:shopId and id=:borrowOrderId";
    return session.createQuery(hql).setLong("shopId",shopId).setLong("borrowOrderId",borrowOrderId);
  }

  public static Query getBorrowOrderItemByOrderId(Session session,Long shopId,Long... borrowOrderId){
    String hql="from BorrowOrderItem where shopId=:shopId and orderId in(:borrowOrderId)";
    return session.createQuery(hql).setLong("shopId",shopId).setParameterList("borrowOrderId",borrowOrderId);
  }

  public static Query getBorrowOrderItemByIds(Session session,Long shopId,List<Long> itemIdList){
    String hql="from BorrowOrderItem where shopId=:shopId and id in (:itemIdList)";
    return session.createQuery(hql).setLong("shopId",shopId).setParameterList("itemIdList",itemIdList);
  }

  public static Query getInsuranceOrderDTOs(Session session, InsuranceOrderDTO searchCondition) {
    StringBuffer sb = new StringBuffer();
    sb.append("from InsuranceOrder where shopId = :shopId ");
    if (StringUtils.isNotBlank(searchCondition.getPolicyNo())) {
      sb.append(" and policyNo like :policyNo ");
    }
    if (searchCondition.getInsuranceCompanyId() != null) {
      sb.append(" and insuranceCompanyId = :insuranceCompanyId");
    }
    if(StringUtils.isNotBlank(searchCondition.getLicenceNo())){
      sb.append(" and licenceNo like :licenceNo");
    }
    if(StringUtils.isNotBlank(searchCondition.getCustomer())){
      sb.append(" and customer like :customer");
    }
    if(searchCondition.getStatus() != null){
      sb.append(" and status = :status");
    }
    if (searchCondition.getStartTime() != null) {
      sb.append(" and accidentDate >=:startTime");
    }
    if (searchCondition.getEndTime() != null) {
      sb.append(" and accidentDate <:endTime");
    }
    if(StringUtils.isNotBlank(searchCondition.getSortStatus())){
      sb.append(" order by ").append(searchCondition.getSortStatus());
    } else {
      sb.append(" order by accidentDate Desc");
    }

    Query q = session.createQuery(sb.toString()).setLong("shopId", searchCondition.getShopId());
    if (StringUtils.isNotBlank(searchCondition.getPolicyNo())) {
      q.setString("policyNo", "%" + searchCondition.getPolicyNo() + "%");
    }
    if (searchCondition.getInsuranceCompanyId() != null) {
      q.setLong("insuranceCompanyId", searchCondition.getInsuranceCompanyId());
    }
    if (StringUtils.isNotBlank(searchCondition.getLicenceNo())) {
      q.setString("licenceNo", "%" + searchCondition.getLicenceNo() + "%");
    }
    if (StringUtils.isNotBlank(searchCondition.getCustomer())) {
      q.setString("customer", "%" + searchCondition.getCustomer() + "%");
    }
    if (searchCondition.getStatus() != null) {
      q.setParameter("status",searchCondition.getStatus());
    }
    if (searchCondition.getStartTime() != null) {
      q.setLong("startTime", searchCondition.getStartTime());
    }
    if (searchCondition.getEndTime() != null) {
      q.setLong("endTime", searchCondition.getEndTime());
    }
    int firstResult = NumberUtil.intValue(searchCondition.getStartPageNo()) == 0? 0:
        (searchCondition.getStartPageNo()-1)* NumberUtil.intValue(searchCondition .getPageSize());
    q.setFirstResult(firstResult).setMaxResults(NumberUtil.intValue(searchCondition .getPageSize()));
    return q;
  }


  public static Query sumInsuranceOrderClaims(Session session, InsuranceOrderDTO searchCondition) {
    StringBuffer sb = new StringBuffer();
    sb.append("select sum(claims)+sum(personalClaims) from InsuranceOrder where shopId = :shopId ");
    if (StringUtils.isNotBlank(searchCondition.getPolicyNo())) {
      sb.append(" and policyNo like :policyNo ");
    }
    if (searchCondition.getInsuranceCompanyId() != null) {
      sb.append(" and insuranceCompanyId = :insuranceCompanyId");
    }
    if(StringUtils.isNotBlank(searchCondition.getLicenceNo())){
      sb.append(" and licenceNo like :licenceNo");
    }
    if(StringUtils.isNotBlank(searchCondition.getCustomer())){
      sb.append(" and customer like :customer");
    }
      if(searchCondition.getStatus() != null){
          sb.append(" and status = :status");
      }
    if (searchCondition.getStartTime() != null) {
      sb.append(" and accidentDate >=:startTime");
    }
    if (searchCondition.getEndTime() != null) {
      sb.append(" and accidentDate <:endTime");
    }
    Query q = session.createQuery(sb.toString()).setLong("shopId", searchCondition.getShopId());
    if (StringUtils.isNotBlank(searchCondition.getPolicyNo())) {
      q.setString("policyNo", "%" + searchCondition.getPolicyNo() + "%");
    }
    if (searchCondition.getInsuranceCompanyId() != null) {
      q.setLong("insuranceCompanyId", searchCondition.getInsuranceCompanyId());
    }
    if (StringUtils.isNotBlank(searchCondition.getLicenceNo())) {
      q.setString("licenceNo", "%" + searchCondition.getLicenceNo() + "%");
    }
    if (StringUtils.isNotBlank(searchCondition.getCustomer())) {
      q.setString("customer", "%" + searchCondition.getCustomer() + "%");
    }

      if (searchCondition.getStatus() != null) {
          q.setParameter("status",  searchCondition.getStatus());
      }
    if (searchCondition.getStartTime() != null) {
      q.setLong("startTime", searchCondition.getStartTime());
    }
    if (searchCondition.getEndTime() != null) {
      q.setLong("endTime", searchCondition.getEndTime());
    }
    return q;
  }

  public static Query countInsuranceOrderDTOs(Session session, InsuranceOrderDTO searchCondition) {
    StringBuffer sb = new StringBuffer();
    sb.append("select count(*) from InsuranceOrder where shopId = :shopId ");
    if (StringUtils.isNotBlank(searchCondition.getPolicyNo())) {
      sb.append(" and policyNo like :policyNo ");
    }
    if (searchCondition.getInsuranceCompanyId() != null) {
      sb.append(" and insuranceCompanyId = :insuranceCompanyId");
    }
    if(StringUtils.isNotBlank(searchCondition.getLicenceNo())){
      sb.append(" and licenceNo like :licenceNo");
    }
    if(StringUtils.isNotBlank(searchCondition.getCustomer())){
      sb.append(" and customer like :customer");
    }
      if(searchCondition.getStatus() != null){
          sb.append(" and status = :status");
      }
    if (searchCondition.getStartTime() != null) {
      sb.append(" and accidentDate >=:startTime");
    }
    if (searchCondition.getEndTime() != null) {
      sb.append(" and accidentDate <:endTime");
    }
    Query q = session.createQuery(sb.toString()).setLong("shopId", searchCondition.getShopId());
    if (StringUtils.isNotBlank(searchCondition.getPolicyNo())) {
      q.setString("policyNo", "%" + searchCondition.getPolicyNo() + "%");
    }
    if (searchCondition.getInsuranceCompanyId() != null) {
      q.setLong("insuranceCompanyId", searchCondition.getInsuranceCompanyId());
    }
    if (StringUtils.isNotBlank(searchCondition.getLicenceNo())) {
      q.setString("licenceNo", "%" + searchCondition.getLicenceNo() + "%");
    }
    if (StringUtils.isNotBlank(searchCondition.getCustomer())) {
      q.setString("customer", "%" + searchCondition.getCustomer() + "%");
    }
      if (searchCondition.getStatus() != null) {
          q.setParameter("status", searchCondition.getStatus());
      }
    if (searchCondition.getStartTime() != null) {
      q.setLong("startTime", searchCondition.getStartTime());
    }
    if (searchCondition.getEndTime() != null) {
      q.setLong("endTime", searchCondition.getEndTime());
    }
    return q;
  }

  public static Query countInnerPickings(Session session, InnerPickingDTO searchCondition) {
    StringBuffer sb = new StringBuffer();
    sb.append("select count(*) from InnerPicking where shopId = :shopId ");
    if (StringUtils.isNotBlank(searchCondition.getReceiptNo())) {
      sb.append(" and receiptNo like :receiptNo ");
    }
    if (searchCondition.getStorehouseId() != null) {
      sb.append(" and storehouseId = :storehouseId");
    }
    if (StringUtils.isNotBlank(searchCondition.getOperationMan())) {
      sb.append(" and operationMan like :operationMan");
    }
    if (StringUtils.isNotBlank(searchCondition.getPickingMan())) {
      sb.append(" and pickingMan like :pickingMan");
    }
    if (searchCondition.getStartTime() != null) {
      sb.append(" and vestDate >=:startTime");
    }
    if (searchCondition.getEndTime() != null) {
      sb.append(" and vestDate <:endTime");
    }
    Query q = session.createQuery(sb.toString()).setLong("shopId", searchCondition.getShopId());
    if (StringUtils.isNotBlank(searchCondition.getReceiptNo())) {
      q.setString("receiptNo", "%" + searchCondition.getReceiptNo() + "%");
    }
    if (searchCondition.getStorehouseId() != null) {
      q.setLong("storehouseId", searchCondition.getStorehouseId());
    }
    if (StringUtils.isNotBlank(searchCondition.getOperationMan())) {
      q.setString("operationMan", "%" + searchCondition.getOperationMan() + "%");
    }
    if (StringUtils.isNotBlank(searchCondition.getPickingMan())) {
      q.setString("pickingMan", "%" + searchCondition.getPickingMan() + "%");
    }
    if (searchCondition.getStartTime() != null) {
      q.setLong("startTime", searchCondition.getStartTime());
    }
    if (searchCondition.getEndTime() != null) {
      q.setLong("endTime", searchCondition.getEndTime());
    }
    return q;
  }

  public static Query countInnerReturns(Session session, InnerReturnDTO searchCondition) {
    StringBuffer sb = new StringBuffer();
    sb.append("select count(*) from InnerReturn where shopId = :shopId ");
    if (StringUtils.isNotBlank(searchCondition.getReceiptNo())) {
      sb.append(" and receiptNo like :receiptNo ");
    }
    if (searchCondition.getStorehouseId() != null) {
      sb.append(" and storehouseId = :storehouseId");
    }
    if (StringUtils.isNotBlank(searchCondition.getOperationMan())) {
      sb.append(" and operationMan like :operationMan");
    }
    if (StringUtils.isNotBlank(searchCondition.getPickingMan())) {
      sb.append(" and pickingMan like :pickingMan");
    }
    if (searchCondition.getStartTime() != null) {
      sb.append(" and vestDate >=:startTime");
    }
    if (searchCondition.getEndTime() != null) {
      sb.append(" and vestDate <:endTime");
    }
    Query q = session.createQuery(sb.toString()).setLong("shopId", searchCondition.getShopId());
    if (StringUtils.isNotBlank(searchCondition.getReceiptNo())) {
      q.setString("receiptNo", "%" + searchCondition.getReceiptNo() + "%");
    }
    if (searchCondition.getStorehouseId() != null) {
      q.setLong("storehouseId", searchCondition.getStorehouseId());
    }
    if (StringUtils.isNotBlank(searchCondition.getOperationMan())) {
      q.setString("operationMan", "%" + searchCondition.getOperationMan() + "%");
    }
    if (StringUtils.isNotBlank(searchCondition.getPickingMan())) {
      q.setString("pickingMan", "%" + searchCondition.getPickingMan() + "%");
    }
    if (searchCondition.getStartTime() != null) {
      q.setLong("startTime", searchCondition.getStartTime());
    }
    if (searchCondition.getEndTime() != null) {
      q.setLong("endTime", searchCondition.getEndTime());
    }
    return q;
  }

  public static Query sumInnerPickings(Session session, Long shopId) {
    StringBuffer sb = new StringBuffer();
    sb.append("select count(*) from InnerPicking where shopId = :shopId ");
    return session.createQuery(sb.toString()).setLong("shopId",shopId);
  }

  public static Query sumInnerReturns(Session session, Long shopId) {
    StringBuffer sb = new StringBuffer();
    sb.append("select count(*) from InnerReturn where shopId = :shopId ");
    return session.createQuery(sb.toString()).setLong("shopId",shopId);
  }

  public static Query sumInsuranceOrderDTOs(Session session, Long shopId) {
    return session.createQuery("select count(*) from InsuranceOrder where shopId = :shopId").setLong("shopId",shopId);
  }

  public static Query countRepairPicks(Session session, RepairPickingDTO searchCondition) {
    List<OrderStatus> allSearchStatus = new ArrayList<OrderStatus>();
    allSearchStatus.add(OrderStatus.WAIT_OUT_STORAGE);
    allSearchStatus.add(OrderStatus.WAIT_RETURN_STORAGE);
    allSearchStatus.add(OrderStatus.OUT_STORAGE);
    allSearchStatus.add(OrderStatus.RETURN_STORAGE);
    StringBuffer sb = new StringBuffer();
    sb.append("select count(distinct rp.id) from RepairPicking rp,RepairPickingItem rpi where rp.shopId = :shopId ");
    sb.append(" and rp.id = rpi.repairPickingId ");
    if (StringUtils.isNotBlank(searchCondition.getReceiptNo())) {
      sb.append(" and rp.receiptNo like :receiptNo ");
    }
    if (StringUtils.isNotBlank(searchCondition.getRepairOrderReceiptNo())) {
      sb.append(" and rp.repairOrderReceiptNo like :repairOrderReceiptNo ");
    }
    if (StringUtils.isNotBlank(searchCondition.getProductSeller())) {
      sb.append(" and rp.productSeller like :productSeller ");
    }
    if (StringUtils.isNotBlank(searchCondition.getPickingMan())) {
      sb.append(" and rpi.pickingMan like :pickingMan ");
    }
    if (CollectionUtils.isNotEmpty(searchCondition.getSearchStatus())) {
      sb.append(" and rpi.status in (:status)");
    } else {
      sb.append(" and rpi.status in (:allSearchStatus)");
    }
    if (searchCondition.getStorehouseId() != null) {
      sb.append(" and rp.storehouseId = :storehouseId");
    }
    if (searchCondition.getStartTime() != null) {
      sb.append(" and rp.vestDate >=:startTime");
    }
    if (searchCondition.getEndTime() != null) {
      sb.append(" and rp.vestDate <:endTime");
    }
    Query q = session.createQuery(sb.toString()).setLong("shopId", searchCondition.getShopId());
    if (StringUtils.isNotBlank(searchCondition.getReceiptNo())) {
      q.setString("receiptNo", "%" + searchCondition.getReceiptNo() + "%");
    }
    if (StringUtils.isNotBlank(searchCondition.getRepairOrderReceiptNo())) {
      q.setString("repairOrderReceiptNo", "%" + searchCondition.getRepairOrderReceiptNo() + "%");
    }
    if (StringUtils.isNotBlank(searchCondition.getProductSeller())) {
      q.setString("productSeller", "%" + searchCondition.getProductSeller() + "%");
    }
    if (StringUtils.isNotBlank(searchCondition.getPickingMan())) {
      q.setString("pickingMan", "%" + searchCondition.getPickingMan() + "%");
    }
    if (CollectionUtils.isNotEmpty(searchCondition.getSearchStatus())) {
      q.setParameterList("status", searchCondition.getSearchStatus());
    } else {
      q.setParameterList("allSearchStatus", allSearchStatus);
    }
    if (searchCondition.getStorehouseId() != null) {
      q.setLong("storehouseId", searchCondition.getStorehouseId());
    }
    if (searchCondition.getStartTime() != null) {
      q.setLong("startTime", searchCondition.getStartTime());
    }
    if (searchCondition.getEndTime() != null) {
      q.setLong("endTime", searchCondition.getEndTime());
    }
    return q;
  }

  public static Query getRepairPickingItemsByOrderIds(Session session, Long... orderIds) {
    StringBuffer sb  = new StringBuffer();
    sb.append(" from RepairPickingItem where repairPickingId in (:orderIds)");
    return  session.createQuery(sb.toString()).setParameterList("orderIds",orderIds);
  }

  public static Query getRepairPicksByIds(Session session, Long shopId, Long... ids) {
    StringBuffer sb = new StringBuffer();
    sb.append(" from RepairPicking where shopId = :shopId and id in (:ids)");
    return session.createQuery(sb.toString()).setParameterList("ids", ids).setLong("shopId",shopId);
  }

  public static Query getRepairPickingByRepairOrderId(Session session, Long shopId, Long repairOrderId) {
    StringBuffer sb = new StringBuffer();
    sb.append(" from RepairPicking where shopId = :shopId and repairOrderId = :repairOrderId");
    return session.createQuery(sb.toString()).setLong("repairOrderId", repairOrderId).setLong("shopId",shopId);
  }
  public static Query getRepairPickingsByRepairOrderIds(Session session, Long shopId, Long... repairOrderId) {
    StringBuffer sb = new StringBuffer();
    sb.append(" from RepairPicking where shopId = :shopId and repairOrderId in(:repairOrderId)");
    return session.createQuery(sb.toString()).setParameterList("repairOrderId", repairOrderId).setLong("shopId",shopId);
  }

  public static Query searchStoreHouses(Session session, Long shopId, int start, int pageSize) {
    StringBuffer sb = new StringBuffer("from StoreHouse s where s.shopId=:shopId and s.deleted=:deleted order by s.lastModified desc");
    Query query = session.createQuery(sb.toString());
    query.setLong("shopId", shopId).setParameter("deleted",DeletedType.FALSE);
    return query.setFirstResult(start).setMaxResults(pageSize);
  }
  public static Query getAllStoreHousesByShopId(Session session, Long shopId) {
    StringBuffer sb = new StringBuffer("from StoreHouse s where s.shopId=:shopId and s.deleted=:deleted order by s.lastModified desc");
    Query query = session.createQuery(sb.toString());
    query.setLong("shopId", shopId).setParameter("deleted",DeletedType.FALSE);
    return query;
  }

  public static Query getStoreHouseInventoryDTO(Session session, Long storehouseId, Long productLocalInfoId) {
    StringBuffer sb = new StringBuffer("from StoreHouseInventory si where si.storehouseId=:storehouseId and si.productLocalInfoId=:productLocalInfoId ");
    Query query = session.createQuery(sb.toString());
    return query.setLong("storehouseId", storehouseId).setLong("productLocalInfoId", productLocalInfoId);
  }

  public static Query getStoreHouseInventoryByStorehouseAndProductIds(Session session,Long shopId, Long storehouseId, Long... productLocalInfoId) {
    StringBuffer sb = new StringBuffer("select si from StoreHouse s,StoreHouseInventory si where s.id=si.storehouseId and s.shopId=:shopId and s.deleted=:deleted and si.storehouseId=:storehouseId and si.productLocalInfoId in(:productLocalInfoId) ");
    Query query = session.createQuery(sb.toString());
    return query.setLong("storehouseId", storehouseId).setLong("shopId", shopId).setParameter("deleted",DeletedType.FALSE).setParameterList("productLocalInfoId",productLocalInfoId);
  }

  public static Query sumStoreHouseInventoryInOtherStorehouseByProductIds(Session session,Long shopId, Long excludeStorehouseId, Long... productLocalInfoId) {
    StringBuffer sb = new StringBuffer("select sum(si.amount) from StoreHouse s,StoreHouseInventory si where s.id=si.storehouseId and s.shopId=:shopId and s.deleted=:deleted and si.storehouseId<>:storehouseId and si.productLocalInfoId in(:productLocalInfoId) ");
    Query query = session.createQuery(sb.toString());
    return query.setLong("storehouseId", excludeStorehouseId).setLong("shopId", shopId).setParameter("deleted",DeletedType.FALSE).setParameterList("productLocalInfoId",productLocalInfoId);
  }

  public static Query getStoreHouseInventoryByProductIds(Session session,Long shopId, Long... productLocalInfoId) {
    StringBuffer sb = new StringBuffer("select si from StoreHouse s,StoreHouseInventory si where s.id=si.storehouseId and s.shopId=:shopId and s.deleted=:deleted and si.productLocalInfoId in(:productLocalInfoId) ");
    Query query = session.createQuery(sb.toString());
    return query.setLong("shopId", shopId).setParameter("deleted",DeletedType.FALSE).setParameterList("productLocalInfoId",productLocalInfoId);
  }

  public static Query countStoreHouses(Session session, Long shopId) {
    StringBuffer sb = new StringBuffer("select count(*) from StoreHouse s where s.shopId=:shopId and s.deleted=:deleted");
    Query query = session.createQuery(sb.toString());
    query.setLong("shopId", shopId).setParameter("deleted",DeletedType.FALSE);
    return query;
  }

  public static Query countStoreHousesByName(Session session, Long shopId,StoreHouseDTO storeHouseDTO) {
    StringBuffer sb = new StringBuffer("select count(*) from StoreHouse s where s.shopId=:shopId and s.name=:name and s.deleted=:deleted");
    if (storeHouseDTO.getId() != null) {
      sb.append(" and s.id <>:id");
    }
    Query query = session.createQuery(sb.toString());
    query.setLong("shopId", shopId).setString("name", storeHouseDTO.getName()).setParameter("deleted", DeletedType.FALSE);
    if (storeHouseDTO.getId() != null) {
      query.setLong("id", storeHouseDTO.getId());
    }
    return query;
  }

  public static Query countProcessingPurchaseInventoryOrdersUseStoreHouseByStorehouseId(Session session, Long shopId,Long storehouseId) {
    StringBuffer sb = new StringBuffer();
    sb.append("select count(*) from PurchaseInventory pi where pi.shopId=:shopId and pi.storehouseId=:storehouseId and pi.statusEnum in(:status)");
    Query query = session.createQuery(sb.toString());
    query.setLong("shopId", shopId).setLong("storehouseId", storehouseId)
        .setParameterList("status",OrderUtil.purchaseInventoryInProgress);
    return query;
  }

  public static Query countProcessingRelatedPurchaseOrders(Session session, Long customerShopId,Long supplierShopId) {
    StringBuffer sb = new StringBuffer();
    sb.append("select count(p.id) from purchase_order p left join sales_order s on p.id = s.purchase_order_id ");
    sb.append(" where p.shop_id =:customerShopId and s.shop_id =:supplierShopId and s.id is not null and p.status_enum in(:status)");
    Query query = session.createSQLQuery(sb.toString());
    query.setLong("supplierShopId", supplierShopId).setLong("customerShopId", customerShopId)
        .setParameterList("status",OrderUtil.getOrderStatusListToString(OrderUtil.purchaseOrderInProgress));
    return query;
  }

  public static Query getProcessingRelatedPurchaseOrders(Session session, Long customerShopId,Long supplierShopId) {
    StringBuffer sb = new StringBuffer();
    sb.append("select p.* from purchase_order p left join sales_order s on p.id = s.purchase_order_id ");
    sb.append(" where p.shop_id =:customerShopId and s.shop_id =:supplierShopId and s.id is not null and p.status_enum in(:status)");
    Query query = session.createSQLQuery(sb.toString()).addEntity(PurchaseOrder.class);
    query.setLong("supplierShopId", supplierShopId).setLong("customerShopId", customerShopId)
        .setParameterList("status",OrderUtil.getOrderStatusListToString(OrderUtil.purchaseOrderInProgress));
    return query;
  }



  public static Query countProcessingRelatedPurchaseReturnOrders(Session session, Long customerShopId,Long supplierShopId) {
    StringBuffer sb = new StringBuffer();
    sb.append("select count(p.id) from purchase_return p left join sales_return s on p.id = s.purchase_return_order_id ");
    sb.append(" where p.shop_id =:customerShopId and s.shop_id =:supplierShopId and s.id is not null and p.status in(:status)");
    Query query = session.createSQLQuery(sb.toString());
    query.setLong("supplierShopId", supplierShopId).setLong("customerShopId", customerShopId)
        .setParameterList("status",OrderUtil.getOrderStatusListToString(OrderUtil.purchaseReturnInProgress));
    return query;
  }

  public static Query getProcessingRelatedPurchaseReturnOrders(Session session, Long customerShopId,Long supplierShopId) {
    StringBuffer sb = new StringBuffer();
    sb.append("select p.* from purchase_return p left join sales_return s on p.id = s.purchase_return_order_id ");
    sb.append(" where p.shop_id =:customerShopId and s.shop_id =:supplierShopId and s.id is not null and p.status in(:status)");
    Query query = session.createSQLQuery(sb.toString()).addEntity(PurchaseReturn.class);
    query.setLong("supplierShopId", supplierShopId).setLong("customerShopId", customerShopId)
        .setParameterList("status",OrderUtil.getOrderStatusListToString(OrderUtil.purchaseReturnInProgress));
    return query;
  }

  public static Query countProcessingRelatedSalesReturnOrders(Session session, Long customerShopId,Long supplierShopId) {
    StringBuffer sb = new StringBuffer();
    sb.append("select count(p.id) from sales_return s left join purchase_return p on p.id = s.purchase_return_order_id ");
    sb.append(" where p.shop_id =:customerShopId and s.shop_id =:supplierShopId and p.id is not null and s.status in(:status)");
    Query query = session.createSQLQuery(sb.toString());
    query.setLong("supplierShopId", supplierShopId).setLong("customerShopId", customerShopId)
        .setParameterList("status",OrderUtil.getOrderStatusListToString(OrderUtil.salesReturnInProgress));
    return query;
  }

  public static Query countProcessingRelatedSalesOrders(Session session, Long customerShopId,Long supplierShopId) {
    StringBuffer sb = new StringBuffer();
    sb.append("select count(s.id) from sales_order s left join purchase_order p on p.id = s.purchase_order_id ");
    sb.append(" where p.shop_id =:customerShopId and s.shop_id =:supplierShopId and p.id is not null and s.status_enum in(:status)");
    Query query = session.createSQLQuery(sb.toString());
    query.setLong("supplierShopId", supplierShopId).setLong("customerShopId", customerShopId)
        .setParameterList("status",OrderUtil.getOrderStatusListToString(OrderUtil.salesOrderInProgress));
    return query;
  }

  public static Query getUnsettledSalesOrdersByCustomerId(Session session, Long shopId,Long customerId) {
    StringBuffer sb = new StringBuffer();
    sb.append("from SalesOrder s ");
    sb.append(" where s.shopId =:shopId and s.customerId =:customerId and s.statusEnum in(:status)");
    Query query = session.createQuery(sb.toString());
    query.setLong("shopId", shopId).setLong("customerId", customerId)
        .setParameterList("status",OrderUtil.salesOrderInProgress);
    return query;
  }


  public static Query countProcessingPurchaseReturnOrdersUseStoreHouseByStorehouseId(Session session, Long shopId,Long storehouseId) {
    StringBuffer sb = new StringBuffer();
    sb.append("select count(*) from PurchaseReturn pr where pr.shopId=:shopId and pr.storehouseId=:storehouseId and pr.status in(:status)");
    Query query = session.createQuery(sb.toString());
    query.setLong("shopId", shopId).setLong("storehouseId", storehouseId)
        .setParameterList("status", OrderUtil.purchaseReturnInProgress);
    return query;
  }

  public static Query countProcessingRepairOrderOrdersUseStoreHouseByStorehouseId(Session session, Long shopId,Long storehouseId) {
    StringBuffer sb = new StringBuffer();
    sb.append("select count(*) from RepairOrder r where r.shopId=:shopId and r.storehouseId=:storehouseId and r.statusEnum in(:status)");
    Query query = session.createQuery(sb.toString());
    query.setLong("shopId", shopId).setLong("storehouseId", storehouseId)
        .setParameterList("status", OrderUtil.repairOrderInProgress);
    return query;
  }

  public static Query countProcessingRepairOrderUseMaterialByShopId(Session session, Long shopId) {
    StringBuffer sb = new StringBuffer();
    sb.append("select count(r.id) from RepairOrder r,RepairOrderItem ri where r.shopId=:shopId and r.id=ri.repairOrderId and r.statusEnum in(:status)");
    Query query = session.createQuery(sb.toString());
    query.setLong("shopId", shopId)
        .setParameterList("status", OrderUtil.repairOrderInProgress);
    return query;
  }

  public static Query countProcessingSalesReturnOrdersUseStoreHouseByStorehouseId(Session session, Long shopId,Long storehouseId) {
    StringBuffer sb = new StringBuffer();
    sb.append("select count(*) from SalesReturn sr where sr.shopId=:shopId and sr.storehouseId=:storehouseId and sr.status in(:status)");
    Query query = session.createQuery(sb.toString());
    query.setLong("shopId", shopId).setLong("storehouseId", storehouseId)
        .setParameterList("status", OrderUtil.salesReturnInProgress);

    return query;
  }

  public static Query countProcessingRepairPickingsUseStoreHouseByStorehouseId(Session session, Long shopId,Long storehouseId) {
    StringBuffer sb = new StringBuffer();
    sb.append("select count(*) from RepairPicking rp,RepairPickingItem rpi where rp.id = rpi.repairPickingId ");
    sb.append(" and rp.shopId=:shopId and rp.storehouseId=:storehouseId and rpi.status in(:status)");
    Query query = session.createQuery(sb.toString());
    query.setLong("shopId", shopId).setLong("storehouseId", storehouseId)
        .setParameterList("status", OrderUtil.RepairPickingInProgress);

    return query;
  }

  public static Query countProcessingRepairPickingUseByShopId(Session session, Long shopId) {
    StringBuffer sb = new StringBuffer();
    sb.append("select count(*) from RepairPicking rp,RepairPickingItem rpi  where rp.id = rpi.repairPickingId ");
    sb.append(" and rp.shopId=:shopId and rpi.status in(:status)");
    Query query = session.createQuery(sb.toString());
    query.setLong("shopId", shopId)
        .setParameterList("status", OrderUtil.RepairPickingInProgress);
    return query;
  }

  public static Query countProcessingRepairOrderUseRepairPickingByShopId(Session session, Long shopId) {
    StringBuffer sb = new StringBuffer();
    sb.append("select count(ro.id) from RepairPicking rp,RepairOrder ro where ro.shopId=:shopId and ro.id = rp.repairOrderId  and ro.statusEnum in(:status)");
    Query query = session.createQuery(sb.toString());
    query.setLong("shopId", shopId)
        .setParameterList("status", OrderUtil.repairOrderInProgress);
    return query;
  }

  public static Query countProcessingSalesOrderOrdersUseStoreHouseByStorehouseId(Session session, Long shopId,Long storehouseId) {
    StringBuffer sb = new StringBuffer();
    sb.append("select count(*) from SalesOrder so where so.shopId=:shopId and so.storehouseId=:storehouseId and so.statusEnum in(:status)");
    Query query = session.createQuery(sb.toString());
    query.setLong("shopId", shopId).setLong("storehouseId", storehouseId)
        .setParameterList("status", OrderUtil.salesOrderInProgress);

    return query;
  }

  public static Query getStoreHouseById(Session session, Long shopId, Long id) {
    return session.createQuery("from StoreHouse s where s.shopId=:shopId and s.id = :id")
        .setLong("shopId", shopId).setLong("id", id);
  }

  public static Query sumStoreHouseAllInventoryAmountByStoreHouseId(Session session, Long shopId,Long storehouseId) {
    StringBuffer sb = new StringBuffer("select sum(si.amount) from StoreHouse s,StoreHouseInventory si where s.id = si.storehouseId and s.shopId=:shopId and si.storehouseId=:storehouseId");
    Query query = session.createQuery(sb.toString());
    query.setLong("shopId", shopId).setLong("storehouseId",storehouseId);
    return query;
  }

  public static Query sumStoreHouseAllInventoryAmountByProductLocalInfoId(Session session, Long shopId,Long productLocalInfoId) {
    StringBuffer sb = new StringBuffer("select sum(si.amount) from StoreHouse s,StoreHouseInventory si where s.id = si.storehouseId and s.shopId=:shopId and si.productLocalInfoId=:productLocalInfoId  and s.deleted=:deleted");
    Query query = session.createQuery(sb.toString());
    query.setLong("shopId", shopId).setLong("productLocalInfoId",productLocalInfoId).setParameter("deleted", DeletedType.FALSE);
    return query;
  }
  public static Query getInventoryDTOsByShopId(Session session, Long shopId,int start,int rows) {
    return session.createQuery("from Inventory i where i.shopId =:shopId")
        .setLong("shopId", shopId).setFirstResult(start).setMaxResults(rows);
  }

  public static Query searchAllocateRecords(Session session,AllocateRecordSearchConditionDTO allocateRecordSearchConditionDTO) {
    StringBuffer sb = new StringBuffer("from AllocateRecord ar where ar.shopId=:shopId");
    if(StringUtils.isNotBlank(allocateRecordSearchConditionDTO.getReceiptNo())){
      sb.append(" and ar.receiptNo like :receiptNo ");
    }
    if(StringUtils.isNotBlank(allocateRecordSearchConditionDTO.getEditor())){
      sb.append(" and ar.editor like :editor ");
    }
    if(allocateRecordSearchConditionDTO.getOutStorehouseId()!=null){
      sb.append(" and ar.outStorehouseId =:outStorehouseId ");
    }
    if(allocateRecordSearchConditionDTO.getInStorehouseId()!=null){
      sb.append(" and ar.inStorehouseId =:inStorehouseId ");
    }
    if(allocateRecordSearchConditionDTO.getStartDate()!=null){
      sb.append(" and ar.vestDate >=:startDate ");
    }
    if(allocateRecordSearchConditionDTO.getEndDate()!=null){
      sb.append(" and ar.vestDate <:endDate ");
    }
    if(AllocateRecordSearchConditionDTO.GenerateType.SYSTEM.equals(allocateRecordSearchConditionDTO.getGenerateType())){
      sb.append(" and ar.originOrderId is not null ");
    }else if(AllocateRecordSearchConditionDTO.GenerateType.USER.equals(allocateRecordSearchConditionDTO.getGenerateType())){
      sb.append(" and ar.originOrderId is null ");
    }
    if (StringUtils.isNotBlank(allocateRecordSearchConditionDTO.getSortStatus())) {
      sb.append(" order by ar.").append(allocateRecordSearchConditionDTO.getSortStatus());
    }
    Query query = session.createQuery(sb.toString());
    query.setLong("shopId", allocateRecordSearchConditionDTO.getShopId());
    if(StringUtils.isNotBlank(allocateRecordSearchConditionDTO.getReceiptNo())){
      query.setString("receiptNo","%"+allocateRecordSearchConditionDTO.getReceiptNo().trim()+"%");
    }
    if(StringUtils.isNotBlank(allocateRecordSearchConditionDTO.getEditor())){
      query.setString("editor","%"+allocateRecordSearchConditionDTO.getEditor().trim()+"%");
    }
    if(allocateRecordSearchConditionDTO.getOutStorehouseId()!=null){
      query.setLong("outStorehouseId", allocateRecordSearchConditionDTO.getOutStorehouseId());
    }
    if(allocateRecordSearchConditionDTO.getInStorehouseId()!=null){
      query.setLong("inStorehouseId", allocateRecordSearchConditionDTO.getInStorehouseId());
    }
    if(allocateRecordSearchConditionDTO.getStartDate()!=null){
      query.setLong("startDate", allocateRecordSearchConditionDTO.getStartDate());
    }
    if(allocateRecordSearchConditionDTO.getEndDate()!=null){
      query.setLong("endDate", allocateRecordSearchConditionDTO.getEndDate());
    }
    return query.setFirstResult((allocateRecordSearchConditionDTO.getStartPageNo() - 1) * allocateRecordSearchConditionDTO.getMaxRows()).setMaxResults(allocateRecordSearchConditionDTO.getMaxRows());
  }

  public static Query countAllocateRecords(Session session,AllocateRecordSearchConditionDTO allocateRecordSearchConditionDTO) {
    StringBuffer sb = new StringBuffer(" select count(*) from AllocateRecord ar where ar.shopId=:shopId");
    if(StringUtils.isNotBlank(allocateRecordSearchConditionDTO.getReceiptNo())){
      sb.append(" and ar.receiptNo like :receiptNo ");
    }
    if(StringUtils.isNotBlank(allocateRecordSearchConditionDTO.getEditor())){
      sb.append(" and ar.editor like :editor ");
    }
    if(allocateRecordSearchConditionDTO.getOutStorehouseId()!=null){
      sb.append(" and ar.outStorehouseId =:outStorehouseId ");
    }
    if(allocateRecordSearchConditionDTO.getInStorehouseId()!=null){
      sb.append(" and ar.inStorehouseId =:inStorehouseId ");
    }
    if(allocateRecordSearchConditionDTO.getStartDate()!=null){
      sb.append(" and ar.vestDate >=:startDate ");
    }
    if(allocateRecordSearchConditionDTO.getEndDate()!=null){
      sb.append(" and ar.vestDate <:endDate ");
    }
    if(AllocateRecordSearchConditionDTO.GenerateType.SYSTEM.equals(allocateRecordSearchConditionDTO.getGenerateType())){
      sb.append(" and ar.originOrderId is not null ");
    }else if(AllocateRecordSearchConditionDTO.GenerateType.USER.equals(allocateRecordSearchConditionDTO.getGenerateType())){
      sb.append(" and ar.originOrderId is null ");
    }
    Query query = session.createQuery(sb.toString());
    query.setLong("shopId", allocateRecordSearchConditionDTO.getShopId());
    if(StringUtils.isNotBlank(allocateRecordSearchConditionDTO.getReceiptNo())){
      query.setString("receiptNo","%"+allocateRecordSearchConditionDTO.getReceiptNo()+"%");
    }
    if(StringUtils.isNotBlank(allocateRecordSearchConditionDTO.getEditor())){
      query.setString("editor","%"+allocateRecordSearchConditionDTO.getEditor()+"%");
    }
    if(allocateRecordSearchConditionDTO.getOutStorehouseId()!=null){
      query.setLong("outStorehouseId", allocateRecordSearchConditionDTO.getOutStorehouseId());
    }
    if(allocateRecordSearchConditionDTO.getInStorehouseId()!=null){
      query.setLong("inStorehouseId", allocateRecordSearchConditionDTO.getInStorehouseId());
    }
    if(allocateRecordSearchConditionDTO.getStartDate()!=null){
      query.setLong("startDate", allocateRecordSearchConditionDTO.getStartDate());
    }
    if(allocateRecordSearchConditionDTO.getEndDate()!=null){
      query.setLong("endDate", allocateRecordSearchConditionDTO.getEndDate());
    }
    return query;
  }

  public static Query getAllocateRecordById(Session session, Long shopId, Long id) {
    return session.createQuery("from AllocateRecord ar where ar.shopId=:shopId and ar.id = :id")
        .setLong("shopId", shopId).setLong("id", id);
  }

  public static Query getAllocateRecordItemByAllocateRecordId(Session session, Long allocateRecordId) {
    return session.createQuery("from AllocateRecordItem ari where ari.allocateRecordId = :allocateRecordId")
        .setLong("allocateRecordId", allocateRecordId);
  }

  public static Query getProductHistoryByProductHistoryIds(Session session, Set<Long> productHistoryIds) {
    return session.createQuery("from ProductHistory where id in :productHistoryIds").setParameterList("productHistoryIds", productHistoryIds);
  }

  public static Query getProductHistoryByProductIds(Session session, Set<Long> productIds) {
    return session.createQuery("from ProductHistory where productLocalInfoId in :productIds order by created desc").setParameterList("productIds", productIds);
  }

  public static Query getServiceHistoryByServiceHistoryIdSet(Session session, Long shopId, Set<Long> serviceHistoryIds) {
    if (shopId != null) {
      return session.createQuery("from ServiceHistory where id in :serviceHistoryIds and shopId=:shopId")
          .setParameterList("serviceHistoryIds", serviceHistoryIds).setLong("shopId", shopId);
    }else{
      return session.createQuery("from ServiceHistory where id in :serviceHistoryIds")
          .setParameterList("serviceHistoryIds", serviceHistoryIds);
    }
  }

  public static Query updateRepairOrderStorehouse(Session session, Long shopId, Long storehouseId,String storehouseName) {
    return session.createQuery("update RepairOrder o set o.storehouseId = :storehouseId, o.storehouseName =:storehouseName where o.shopId = :shopId")
        .setLong("shopId", shopId).setLong("storehouseId", storehouseId).setString("storehouseName", storehouseName);
  }
  public static Query updatePurchaseInventoryOrderStorehouse(Session session, Long shopId, Long storehouseId,String storehouseName) {
    return session.createQuery("update PurchaseInventory o set o.storehouseId = :storehouseId, o.storehouseName =:storehouseName where o.shopId = :shopId")
        .setLong("shopId", shopId).setLong("storehouseId", storehouseId).setString("storehouseName", storehouseName);
  }
  public static Query updatePurchaseReturnOrderStorehouse(Session session, Long shopId, Long storehouseId,String storehouseName) {
    return session.createQuery("update PurchaseReturn o set o.storehouseId = :storehouseId, o.storehouseName =:storehouseName where o.shopId = :shopId")
        .setLong("shopId", shopId).setLong("storehouseId", storehouseId).setString("storehouseName", storehouseName);
  }
  public static Query updateSalesReturnOrderStorehouse(Session session, Long shopId, Long storehouseId,String storehouseName) {
    return session.createQuery("update SalesReturn o set o.storehouseId = :storehouseId, o.storehouseName =:storehouseName where o.shopId = :shopId")
        .setLong("shopId", shopId).setLong("storehouseId", storehouseId).setString("storehouseName", storehouseName);
  }
  public static Query updateSalesOrderStorehouse(Session session, Long shopId, Long storehouseId,String storehouseName) {
    return session.createQuery("update SalesOrder o set o.storehouseId = :storehouseId, o.storehouseName =:storehouseName where o.shopId = :shopId")
        .setLong("shopId", shopId).setLong("storehouseId", storehouseId).setString("storehouseName", storehouseName);
  }

  public static Query updateInventoryCheckOrderStorehouse(Session session, Long shopId, Long storehouseId,String storehouseName) {
    return session.createQuery("update InventoryCheck o set o.storehouseId = :storehouseId, o.storehouseName =:storehouseName where o.shopId = :shopId")
        .setLong("shopId", shopId).setLong("storehouseId", storehouseId).setString("storehouseName", storehouseName);
  }


  public static Query getInnerPickingById(Session session, Long shopId, Long innerPickingId) {
    return session.createQuery("from InnerPicking where id =:innerPickingId and shopId =:shopId")
        .setLong("shopId", shopId).setLong("innerPickingId", innerPickingId);
  }

  public static Query getInnerReturnById(Session session, Long shopId, Long innerReturnId) {
    return session.createQuery("from InnerReturn where id =:innerReturnId and shopId =:shopId")
        .setLong("shopId", shopId).setLong("innerReturnId", innerReturnId);
  }

  public static Query getInnerPickingItemsByInnerPickingId(Session session, Long... innerPickingId) {
    return session.createQuery("from InnerPickingItem where innerPickingId in(:innerPickingId) ")
        .setParameterList("innerPickingId", innerPickingId);
  }

  public static Query getInnerReturnItemsByInnerReturnId(Session session, Long... innerReturnId) {
    return session.createQuery("from InnerReturnItem where innerReturnId in(:innerReturnId) ")
        .setParameterList("innerReturnId", innerReturnId);
  }

  public static Query getReturnOrderItemsByOrderIds(Session session,Long shopId,Long... orderIds) {
    return session.createQuery("from ReturnOrderItem where orderId in(:orderIds) ").setParameterList("orderIds",orderIds);
  }

  public static Query getInsuranceOrderByPolicyNo(Session session, Long shopId, String policyNo) {
    return session.createQuery("from InsuranceOrder where shopId =:shopId and policyNo =:policyNo")
        .setLong("shopId", shopId).setString("policyNo",policyNo);
  }

  public static Query getInsuranceOrderByReportNo(Session session, Long shopId, String reportNo) {
    return session.createQuery("from InsuranceOrder where shopId =:shopId and reportNo =:reportNo")
        .setLong("shopId", shopId).setString("reportNo",reportNo);
  }

  public static Query getInsuranceOrderById(Session session, Long shopId, Long id) {
    return session.createQuery("from InsuranceOrder where id =:id and shopId =:shopId")
        .setLong("shopId", shopId).setLong("id",id);
  }

  public static Query getInsuranceOrderIdByRepairOrderId(Session session, Long shopId, Long repairOrderId) {
    return session.createQuery("select id from InsuranceOrder where repairOrderId =:repairOrderId and shopId =:shopId")
        .setLong("shopId", shopId).setLong("repairOrderId", repairOrderId);
  }

  public static Query getInsuranceOrderByRepairOrderId(Session session, Long shopId, Long repairOrderId) {
    return session.createQuery("from InsuranceOrder where repairOrderId =:repairOrderId and shopId =:shopId")
        .setLong("shopId", shopId).setLong("repairOrderId",repairOrderId);
  }

  public static Query getInsuranceOrderByRepairDraftOrderId(Session session, Long shopId, Long repairDraftOrderId) {
    return session.createQuery("from InsuranceOrder where repairDraftOrderId =:repairDraftOrderId and shopId =:shopId")
        .setLong("shopId", shopId).setLong("repairDraftOrderId",repairDraftOrderId);
  }

  public static Query getInsuranceOrderServiceByOrderId(Session session,Long shopId, Long orderId) {
    return session.createQuery("from InsuranceOrderService where insuranceOrderId =:orderId and shopId =:shopId")
        .setLong("orderId", orderId).setLong("shopId",shopId);
  }

  public static Query getInsuranceOrderItemByOrderId(Session session,Long shopId, Long orderId) {
    return session.createQuery("select item from InsuranceOrderItem item,InsuranceOrder io where " +
        "item.insuranceOrderId =:orderId and io.shopId =:shopId and io.id =:orderId ")
        .setLong("orderId", orderId).setLong("shopId",shopId);
  }

  public static Query getMemberCardConsumeByMemberId(Session session, Long memberId) {
    String sql = "select r from Receivable r where r.memberId = :memberId and (r.memberBalancePay > 0 and r.orderTypeEnum in ('SALE','REPAIR') or r.orderTypeEnum = 'WASH_BEAUTY') and r.statusEnum ='FINISH'";
    return session.createQuery(sql).setLong("memberId",memberId);
  }
  public static Query getPurchaseInventoryItemByProductIdVestDate(Session session, Long shopId, Long productId,Long vestDate) {
    return session.createQuery("select p.amount,p.price,p.total,i.vestDate,p.productId from PurchaseInventoryItem p,PurchaseInventory i " +
        "where p.amount > 0 and p.productId=:productId and p.purchaseInventoryId = i.id and i.shopId=:shopId  and i.statusEnum=:status and i.vestDate >=:vestDate order by i.vestDate desc")
        .setLong("productId", productId).setLong("shopId", shopId).setLong("vestDate", vestDate).setParameter("status", OrderStatus.PURCHASE_INVENTORY_DONE);
  }

  public static Query countStatDateByNormalProductIds(Session session, Long[] shopIds,Long[] normalProductIds,NormalProductStatType normalProductStatType) {
    StringBuffer sb = new StringBuffer(" select normalProductId from NormalProductInventoryStat where normalProductId in(:normalProductIds) and normalProductStatType=:normalProductStatType and productLocalInfoId is null");
    if (ArrayUtil.isNotEmpty(shopIds)) {
      sb.append(" and shopId in(:shopId) ");
    }
    sb.append(" group by normalProductId ");
    Query query = session.createQuery(sb.toString()).setParameterList("normalProductIds", normalProductIds).setParameter("normalProductStatType", normalProductStatType);
    if (ArrayUtil.isNotEmpty(shopIds)) {
      query.setParameterList("shopId", shopIds);
    }
    return query;
  }

  public static Query getStatDateByNormalProductIds(Session session, Long[] shopIds, Long[] normalProductIds, NormalProductStatType normalProductStatType) {
    StringBuffer sb = new StringBuffer(" from NormalProductInventoryStat where normalProductId in(:normalProductIds) and normalProductStatType=:normalProductStatType  and productLocalInfoId is null ");
    if (ArrayUtil.isNotEmpty(shopIds)) {
      sb.append(" and shopId in(:shopId) ");
    }
    Query query = session.createQuery(sb.toString()).setParameterList("normalProductIds", normalProductIds)
        .setParameter("normalProductStatType", normalProductStatType);
    if (ArrayUtil.isNotEmpty(shopIds)) {
      query.setParameterList("shopId", shopIds);
    }
    return query;
  }
  public static Query deleteAllNormalProductStat(Session session) {
    StringBuffer sb = new StringBuffer(" delete from NormalProductInventoryStat ");
    Query query = session.createQuery(sb.toString());
    return query;
  }



  public static Query countSupplierReturnPayable(Session session)
  {
    return session.createQuery("select count(*) from SupplierReturnPayable srp where srp.moveStatus = :status")
        .setString("status","no");
  }

  public static Query getSupplierReturnPayable(Session session,int size)
  {
    return session.createQuery("select srp from SupplierReturnPayable srp where srp.moveStatus = :status order by srp.id asc")
        .setString("status","no").setMaxResults(size);
  }

  public static Query getSupplierReturnPayableByIds(Session session,List<Long> ids)
  {
    return session.createQuery("select srp from SupplierReturnPayable srp where srp.id in (:ids)")
        .setParameterList("ids",ids);
  }

  public static Query updateMovedSupplierReturnPayable(Session session,List<Long> ids)
  {
    return session.createQuery("update SupplierReturnPayable srp set srp.moveStatus =:status where srp.id in (:ids)")
        .setString("status","yes").setParameterList("ids",ids);
  }

  public static Query getQualifiedCredentialsDTO(Session session,Long shopId,Long orderId)
  {
    return session.createQuery("select q from QualifiedCredentials q where q.shopId=:shopId and q.orderId =:orderId")
        .setLong("shopId",shopId).setLong("orderId",orderId);
  }

  public static Query getLastStatementAccountOrder(Session session, Long shopId, Long customerOrSupplierId, OrderTypes orderType) {
    return session.createQuery("from StatementAccountOrder where shopId=:shopId and customerOrSupplierId=:customerOrSupplierId and orderType=:orderType order by vestDate desc ")
        .setLong("shopId", shopId).setLong("customerOrSupplierId", customerOrSupplierId).setParameter("orderType", orderType).setMaxResults(1);
  }

  public static Query getReceivableListByCustomerId(Session session, Long shopId, Long customerId,Long startDate,Long endDate) {
    return session.createQuery("from Receivable where shopId=:shopId and customerId=:customerId and vestDate >=:startDate and vestDate <:endDate  and statusEnum =:statusEnum and statementAccountOrderId is null order by vestDate desc ")
        .setLong("shopId", shopId).setLong("customerId", customerId).setLong("startDate", startDate).setLong("endDate", endDate).setParameter("statusEnum", ReceivableStatus.FINISH);
  }

  public static Query getTotalDebtByOrderIds(Session session, Long shopId, OrderTypes orderType, Long[] orderIds) {
    if (orderType == OrderTypes.CUSTOMER_STATEMENT_ACCOUNT) {
      return session.createQuery(" select sum(debt),count(*) from Receivable where shopId=:shopId and statementAccountOrderId is null and orderId in(:orderIds) and statusEnum =:statusEnum")
          .setLong("shopId", shopId).setParameterList("orderIds", orderIds).setParameter("statusEnum", ReceivableStatus.FINISH);
    } else if (orderType == OrderTypes.SUPPLIER_STATEMENT_ACCOUNT) {
      return session.createQuery(" select sum(creditAmount),count(*) from Payable where shopId=:shopId and statementAccountOrderId is null and purchaseInventoryId in(:orderIds) and status =:statusEnum")
          .setLong("shopId", shopId).setParameterList("orderIds", orderIds).setParameter("statusEnum", PayStatus.USE);
    }
    return null;
  }

  public static Query getStatementAccountOrderList(Session session,OrderSearchConditionDTO orderSearchConditionDTO, Pager pager) {
    StringBuffer sb = new StringBuffer("from StatementAccountOrder where shopId=:shopId ");
    Long[] customerOrSupplierIds = null;
    if (orderSearchConditionDTO.getCustomerOrSupplierIds() != null) {
      customerOrSupplierIds = new Long[orderSearchConditionDTO.getCustomerOrSupplierIds().length];
      for (int i = 0; i < customerOrSupplierIds.length; i++) {
        customerOrSupplierIds[i] = Long.parseLong(orderSearchConditionDTO.getCustomerOrSupplierIds()[i]);
      }
    }
    if (customerOrSupplierIds != null) {
      sb.append("and customerOrSupplierId in (:customerOrSupplierIds) ");
    }else if(StringUtils.isNotEmpty(orderSearchConditionDTO.getCustomerOrSupplierName())){
      sb.append("and customerOrSupplier like :customerOrSupplier ");
    }
    if (StringUtils.isNotEmpty(orderSearchConditionDTO.getReceiptNo())) {
      sb.append(" and receiptNo=:receiptNo");
    }
    if (StringUtils.isNotEmpty(orderSearchConditionDTO.getOperator())) {
      sb.append(" and salesMan=:salesMan");
    }
    if(StringUtils.isNotEmpty(orderSearchConditionDTO.getMobile())){
      sb.append(" and mobile=:mobile");
    }
    if (orderSearchConditionDTO.getStartTime() != null) {
      sb.append(" and vestDate >=:startTime");
    }
    if (orderSearchConditionDTO.getEndTime() != null) {
      sb.append(" and vestDate <=:endTime");
    }
    if (StringUtils.isNotEmpty(orderSearchConditionDTO.getSort())) {
      sb.append(orderSearchConditionDTO.getSort());
    } else {
      sb.append(" order by vestDate desc ");
    }
    Query query = session.createQuery(sb.toString()).setLong("shopId", orderSearchConditionDTO.getShopId());
    if (customerOrSupplierIds != null) {
      query.setParameterList("customerOrSupplierIds", customerOrSupplierIds);
    }else if(StringUtils.isNotEmpty(orderSearchConditionDTO.getCustomerOrSupplierName())){
      query.setString("customerOrSupplier", "%" + orderSearchConditionDTO.getCustomerOrSupplierName() + "%");
    }
    if (StringUtils.isNotEmpty(orderSearchConditionDTO.getReceiptNo())) {
      query.setString("receiptNo", orderSearchConditionDTO.getReceiptNo());
    }
    if(StringUtils.isNotEmpty(orderSearchConditionDTO.getMobile())){
      query.setString("mobile", orderSearchConditionDTO.getMobile());
    }
    if (StringUtils.isNotEmpty(orderSearchConditionDTO.getOperator())) {
      query.setString("salesMan", orderSearchConditionDTO.getOperator());
    }
    if (orderSearchConditionDTO.getStartTime() != null) {
      query.setLong("startTime", orderSearchConditionDTO.getStartTime());
    }
    if (orderSearchConditionDTO.getEndTime() != null) {
      query.setLong("endTime", orderSearchConditionDTO.getEndTime());
    }
    return  query.setFirstResult(pager.getRowStart()).setMaxResults(pager.getPageSize());
  }

  public static Query countStatementAccountOrderList(Session session,OrderSearchConditionDTO orderSearchConditionDTO) {
    StringBuffer sb = new StringBuffer("select count(*) from StatementAccountOrder where shopId=:shopId ");
    Long[] customerOrSupplierIds = null;
    if (orderSearchConditionDTO.getCustomerOrSupplierIds() != null) {
      customerOrSupplierIds = new Long[orderSearchConditionDTO.getCustomerOrSupplierIds().length];
      for (int i = 0; i < customerOrSupplierIds.length; i++) {
        customerOrSupplierIds[i] = Long.parseLong(orderSearchConditionDTO.getCustomerOrSupplierIds()[i]);
      }
    }
    if (customerOrSupplierIds != null) {
      sb.append("and customerOrSupplierId in (:customerOrSupplierIds) ");
    }else if(StringUtils.isNotEmpty(orderSearchConditionDTO.getCustomerOrSupplierName())){
      sb.append("and customerOrSupplier like :customerOrSupplier ");
    }
    if (StringUtils.isNotEmpty(orderSearchConditionDTO.getReceiptNo())) {
      sb.append(" and receiptNo=:receiptNo");
    }
    if (StringUtils.isNotEmpty(orderSearchConditionDTO.getOperator())) {
      sb.append(" and salesMan=:salesMan");
    }
    if(StringUtils.isNotEmpty(orderSearchConditionDTO.getMobile())){
      sb.append(" and mobile=:mobile");
    }
    if (orderSearchConditionDTO.getStartTime() != null) {
      sb.append(" and vestDate >=:startTime");
    }
    if (orderSearchConditionDTO.getEndTime() != null) {
      sb.append(" and vestDate <=:endTime");
    }
    if (StringUtils.isNotEmpty(orderSearchConditionDTO.getSort())) {
      sb.append(orderSearchConditionDTO.getSort());
    } else {
      sb.append(" order by vestDate desc ");
    }
    Query query = session.createQuery(sb.toString()).setLong("shopId", orderSearchConditionDTO.getShopId());
    if (customerOrSupplierIds != null) {
      query.setParameterList("customerOrSupplierIds", customerOrSupplierIds);
    }else if(StringUtils.isNotEmpty(orderSearchConditionDTO.getCustomerOrSupplierName())){
      query.setString("customerOrSupplier", "%" + orderSearchConditionDTO.getCustomerOrSupplierName() + "%");
    }
    if (StringUtils.isNotEmpty(orderSearchConditionDTO.getReceiptNo())) {
      query.setString("receiptNo", orderSearchConditionDTO.getReceiptNo());
    }
    if (StringUtils.isNotEmpty(orderSearchConditionDTO.getOperator())) {
      query.setString("salesMan", orderSearchConditionDTO.getOperator());
    }
    if(StringUtils.isNotEmpty(orderSearchConditionDTO.getMobile())){
      query.setString("mobile", orderSearchConditionDTO.getMobile());
    }
    if (orderSearchConditionDTO.getStartTime() != null) {
      query.setLong("startTime", orderSearchConditionDTO.getStartTime());
    }
    if (orderSearchConditionDTO.getEndTime() != null) {
      query.setLong("endTime", orderSearchConditionDTO.getEndTime());
    }
    return query;
  }

  public static Query getPayableListBySupplierId(Session session, Long shopId, Long supplierId,Long startDate,Long endDate) {
    return session.createQuery("from Payable where shopId=:shopId and supplierId=:supplierId and payTime >=:startDate and payTime <:endDate and status =:statusEnum and statementAccountOrderId is null order by payTime desc ")
        .setLong("shopId", shopId).setLong("supplierId", supplierId).setLong("startDate", startDate).setLong("endDate", endDate).setParameter("statusEnum", PayStatus.USE);
  }

  public static Query getOperatorByCustomerOrSupplierId(Session session,Long shopId,Long customerOrSupplierId,String salesMan) {
    StringBuffer sb = new StringBuffer();
    sb.append(" from StatementAccountOrder where shopId=:shopId ");
    if(customerOrSupplierId != null){
      sb.append(" and customerOrSupplierId =:customerOrSupplierId ");
    }
    if (StringUtils.isNotEmpty(salesMan)) {
      sb.append(" and salesMan like:salesMan");
    }
    sb.append(" group by salesMan");
    Query query = session.createQuery(sb.toString()).setLong("shopId", shopId);
    if(customerOrSupplierId != null){
      query.setLong("customerOrSupplierId", customerOrSupplierId);
    }
    if (StringUtils.isNotEmpty(salesMan)) {
      query.setString("salesMan", "%" + salesMan + "%");
    }
    return query;
  }

  public static Query getPurchaseReturnByPurchaseReturnId(Session session,Long... id)
  {
    return session.createQuery("select p from PurchaseReturn p where p.id in(:id)").setParameterList("id",id);
  }

  public static Query getUnsettledRepairOrderByServiceId(Session session, Long shopId, Long serviceId) {
    StringBuffer sb = new StringBuffer();
    sb.append("select r from RepairOrder r,RepairOrderService rs where r.id = rs.repairOrderId");
    sb.append(" and r.statusEnum in(:status) and rs.serviceId = :serviceId");
    return session.createQuery(sb.toString()).setParameterList("status", OrderUtil.repairOrderInProgress).setLong("serviceId", serviceId);
  }

  public static Query getInventoryCheckById(Session session, Long shopId, Long inventoryCheckId) {
    String hql="from InventoryCheck where shopId=:shopId and id=:inventoryCheckId";
    return session.createQuery(hql).setLong("shopId", shopId).setLong("inventoryCheckId",inventoryCheckId);
  }

  public static Query getInventoryCheckItem(Session session,Long inventoryCheckId) {
    String hql="from InventoryCheckItem where inventoryCheckId=:inventoryCheckId";
    return session.createQuery(hql).setLong("inventoryCheckId",inventoryCheckId);
  }

  public static Query getInventoryCheckByIds(Session session,Long shopId,Set<Long> orderIds,Pager pager) {
    StringBuffer sb=new StringBuffer();
    sb.append("from InventoryCheck where id in (:orderIds)");
    sb.append(" order by editDate desc");
    Query query= session.createQuery(sb.toString()).setParameterList("orderIds",orderIds);
    return query;
  }

  public static Query getInventoryChecks(Session session,InventoryCheckDTO condition) {
    StringBuffer sb = new StringBuffer();
    sb.append("from InventoryCheck where 1=1");
    if(condition.getShopId()!=null){
      sb.append(" and shopId=:shopId");
    }
    if(StringUtil.isNotEmpty(condition.getEditor())){
      sb.append(" and editor=:editor");
    }
    if(condition.getStartTime()!=null){
      sb.append(" and editDate>=:startTime");
    }
    if(condition.getEndTime()!=null){
      sb.append(" and editDate<=:endTime");
    }
    if(condition.getStorehouseId()!=null){
      sb.append(" and storehouseId=:storehouseId");
    }
    if(condition.getCheckResultFlag()==InventoryCheckDTO.CHECK_LOSE){
      sb.append(" and adjustPriceTotal< 0");
    }else if(condition.getCheckResultFlag()==InventoryCheckDTO.CHECK_WIN){
      sb.append(" and adjustPriceTotal> 0");
    }
    sb.append(" order by editDate desc");
    Query query=session.createQuery(sb.toString());
    if(condition.getPager()!=null){
      query.setFirstResult(condition.getPager().getRowStart()).setMaxResults(condition.getPager().getPageSize());
    }
    if(condition.getShopId()!=null){
      query.setLong("shopId",condition.getShopId());
    }
    if(StringUtil.isNotEmpty(condition.getEditor())){
      query.setString("editor",condition.getEditor());
    }
    if(condition.getStartTime()!=null){
      query.setLong("startTime",condition.getStartTime());
    }
    if(condition.getEndTime()!=null){
      query.setLong("endTime",condition.getEndTime());
    }
    if(condition.getStorehouseId()!=null){
      query.setLong("storehouseId",condition.getStorehouseId());
    }
    return query;
  }

  public static Query getInventoryCheckCount(Session session,InventoryCheckDTO condition) {
    StringBuffer sb = new StringBuffer();
    sb.append("select count(*) from InventoryCheck where shopId=:shopId");
    if(StringUtil.isNotEmpty(condition.getEditor())){
      sb.append(" and editor=:editor");
    }
    if(condition.getStartTime()!=null){
      sb.append(" and editDate>=:startTime");
    }
    if(condition.getEndTime()!=null){
      sb.append(" and editDate<=:endTime");
    }
    if(condition.getStorehouseId()!=null){
      sb.append(" and storehouseId=:storehouseId");
    }
    if(condition.getCheckResultFlag()==InventoryCheckDTO.CHECK_LOSE){
      sb.append(" and adjustPriceTotal< 0");
    }else if(condition.getCheckResultFlag()==InventoryCheckDTO.CHECK_WIN){
      sb.append(" and adjustPriceTotal> 0");
    }
    Query query=session.createQuery(sb.toString()).setLong("shopId", condition.getShopId());
    if(StringUtil.isNotEmpty(condition.getEditor())){
      query.setString("editor",condition.getEditor());
    }
    if(condition.getStartTime()!=null){
      query.setLong("startTime",condition.getStartTime());
    }
    if(condition.getEndTime()!=null){
      query.setLong("endTime",condition.getEndTime());
    }
    if(condition.getStorehouseId()!=null){
      query.setLong("storehouseId",condition.getStorehouseId());
    }
    return query;
  }

  public static Query getStockAdjustPriceTotal(Session session,Long shopId) {
    String hql="select sum(adjustPriceTotal) from InventoryCheck where shopId=:shopId";
    return session.createQuery(hql).setLong("shopId",shopId);
  }



  public static Query getReceivableListByStatementOrderId(Session session, Long shopId,Long statementOrderId) {
    return session.createQuery("from Receivable where shopId=:shopId and statementAccountOrderId =:statementOrderId  and statusEnum =:statusEnum  order by vestDate desc ")
        .setLong("shopId", shopId).setLong("statementOrderId", statementOrderId).setParameter("statusEnum", ReceivableStatus.FINISH);
  }

  public static Query getPayableListByStatementOrderId(Session session, Long shopId,Long statementOrderId) {
    return session.createQuery("from Payable where shopId=:shopId and statementAccountOrderId =:statementOrderId  and status =:statusEnum  order by payTime desc ")
        .setLong("shopId", shopId).setLong("statementOrderId", statementOrderId).setParameter("statusEnum", PayStatus.USE);
  }

  public static Query getReceptionRecordBySopId(Session session, Long shopId, Pager pager,List statusList) {
    return session.createQuery("from ReceptionRecord where shopId=:shopId and orderId is not null and receivableHistoryId is null and (orderStatusEnum not in :statusList or orderStatusEnum is null )  order by receptionDate asc ")
        .setLong("shopId", shopId).setParameterList("statusList", statusList).setFirstResult(pager.getRowStart()).setMaxResults(pager.getPageSize());
  }

  public static Query countReceptionRecordBySopId(Session session, Long shopId,List statusList) {
    return session.createQuery("select count(*) from ReceptionRecord where shopId=:shopId and orderId is not null and receivableHistoryId is null and (orderStatusEnum not in :statusList or orderStatusEnum is null )  order by receptionDate asc ")
        .setLong("shopId", shopId).setParameterList("statusList", statusList);
  }

  public static Query getStatementAccountOrderByShopIdAndOrderIds(Session session, Long shopId, Long... orderIds) {
    StringBuffer sb = new StringBuffer("from StatementAccountOrder where id in (:orderIds)");
    if (shopId != null) {
      sb.append(" and shopId=:shopId");
    }
    Query query = session.createQuery(sb.toString()).setParameterList("orderIds", orderIds);
    if (shopId != null) {
      query.setLong("shopId", shopId);
    }
    return query;
  }

  public static Query getStatementAccountOrderIds(Session session, Long shopId, int start, int pageSize) {
    StringBuffer sb = new StringBuffer("select id from StatementAccountOrder");
    if (shopId != null) {
      sb.append(" where shopId=:shopId");
    }
    Query query = session.createQuery(sb.toString());
    if (shopId != null) {
      query.setLong("shopId", shopId);
    }
    return query.setFirstResult(start).setMaxResults(pageSize);
  }

  public static Query getSupplierTotalDebtByShopId(Session session,Long shopId,OrderDebtType type) {
    StringBuffer hql = new StringBuffer(" select sum(creditAmount) from Payable where shopId =:shopId and  status =:status and orderDebtType= :type");
    Query query = session.createQuery(hql.toString());
    query.setLong("shopId", shopId).setParameter("status",PayStatus.USE).setString("type",type.toString());
    return query;
  }


  public static Query getAllRepairRemindEvent(Session session) {
    String sql = "select r from RepairRemindEvent r";
    return session.createQuery(sql);
  }

  public static Query getAllDebt(Session session) {
    String sql = "select d from Debt d where d.status <> '作废'";
    return session.createQuery(sql);
  }

  public static Query getAllInventoryRemindEvent(Session session) {
    String sql = "select i from InventoryRemindEvent i group by i.purchaseOrderId";
    return session.createQuery(sql);
  }

  public static Query getWXRemindEvent(Session session,Long startTime,Long endTime){
    StringBuilder sb=new StringBuilder();
    sb.append("select  wuv.open_id as open_id,e.*  from remind_event e " +
      "join bcuser.customer_vehicle cv on e.customer_id=cv.customer_id and (cv.status='ENABLED' or cv.status is null) " +
      "join bcuser.vehicle v on cv.vehicle_id=v.id and (v.status is null or v.status ='ENABLED') " +
      "join bcuser.customer cus on cus.id=cv.customer_id and (cus.status is null or cus.status = 'ENABLED') " +
      "join bcuser.wx_user_vehicle wuv on wuv.vehicle_no=v.licence_no and wuv.deleted='FALSE' ");
    sb.append("where e.event_type='CUSTOMER_SERVICE' and (e.wx_remind_status!='YES' or e.wx_remind_status is null) and e.remind_time>=:starTime and e.remind_time<=:endTime");
    Query query=session.createSQLQuery(sb.toString())
      .addScalar("open_id",StandardBasicTypes.STRING)
      .addEntity(RemindEvent.class)
      .setLong("starTime",startTime)
      .setLong("endTime",endTime)
      ;
    return query;
  }

  public static Query queryRepairRemindEvent(Session session, Long shopId, Long flashTime, Integer pageNo, Integer pageSize) {
    StringBuffer sb = new StringBuffer();
    sb.append("(select * from remind_event r where r.event_type = 'REPAIR' and r.shop_id = :shopId ");
    sb.append(" and r.remind_time >= " + flashTime);
    sb.append(" and r.remind_time < " + (flashTime + 2*24*3600*1000));
    sb.append(" and r.remind_status = 'activity'");
    sb.append(" order by r.id desc)");
    sb.append(" union all ");
    sb.append("(select * from remind_event r where r.event_type = 'REPAIR' and r.shop_id = :shopId ");
    sb.append(" and (r.remind_time <" + flashTime);
    sb.append(" or r.remind_time >= " + (flashTime + 2*24*3600*1000) + ")");
    sb.append(" and r.remind_status = 'activity'");
    sb.append(" order by r.id desc)");
    Query query = session.createSQLQuery(sb.toString()).addEntity(RemindEvent.class).setLong("shopId", shopId);
    if (pageNo != null && pageSize!=null) {
      int firstResult = pageNo.intValue() * pageSize.intValue();
      query.setFirstResult(firstResult).setMaxResults(pageSize.intValue());
    }
    return query;
  }
  //根据提醒类型查找来料待修 缺料待修 待交付
  public static Query queryRepairRemindEvent(Session session, Long shopId, Long flashTime, RepairRemindEventTypes repairRemindEventTypes, Integer pageNo, Integer pageSize) {
        StringBuffer sb = new StringBuffer();
        sb.append("(select * from remind_event r where r.event_type = 'REPAIR' and r.shop_id = :shopId ");
        sb.append(" and r.remind_status = 'activity'");
        if(repairRemindEventTypes != null) {
            sb.append(" and r.event_status = :eventStatus");
        }
        sb.append(" order by r.id desc)");
        Query query = session.createSQLQuery(sb.toString()).addEntity(RemindEvent.class).setLong("shopId", shopId);
        if(repairRemindEventTypes != null) {
            query.setString("eventStatus",repairRemindEventTypes.toString());
        }
        if (pageNo != null && pageSize!=null) {
            int firstResult = pageNo.intValue() * pageSize.intValue();
            query.setFirstResult(firstResult).setMaxResults(pageSize.intValue());
        }
        return query;
  }

  public static Query countFlashRepairRemindEvent(Session session, Long shopId, Long flashTime) {
    StringBuffer sb = new StringBuffer();
    sb.append("select count(*) from RemindEvent r where r.eventType = 'REPAIR' and r.shopId = :shopId ");
    sb.append(" and r.remindTime >= " + flashTime);
    sb.append(" and r.remindTime < " + (flashTime + 2*24*3600*1000));
    sb.append(" and r.remindStatus = 'activity'");
    Query query = session.createQuery(sb.toString()).setLong("shopId", shopId);
    return query;
  }

  public static Query countRepairRemindEvent(Session session, Long shopId, RepairRemindEventTypes repairRemindEventTypes) {
      StringBuffer sb = new StringBuffer();
      sb.append("select count(*) from RemindEvent r where r.eventType = 'REPAIR' and r.shopId = :shopId and r.remindStatus = 'activity'");
      if(repairRemindEventTypes != null) {
         sb.append(" and r.eventStatus = :eventStatus");
      }
      Query query = session.createQuery(sb.toString()).setLong("shopId",shopId);
      if(repairRemindEventTypes != null) {
          query.setString("eventStatus",repairRemindEventTypes.toString());
      }
      return query;
  }

  public static Query countUnflashRepairRemindEvent(Session session, Long shopId, Long flashTime) {
    StringBuffer sb = new StringBuffer();
    sb.append("select count(*) from RemindEvent r where r.eventType = 'REPAIR' and r.shopId = :shopId ");
    sb.append(" and (r.remindTime <" + flashTime);
    sb.append(" or r.remindTime >= " + (flashTime + 2*24*3600*1000) + ")");
    sb.append(" and r.remindStatus = 'activity'");
    Query query = session.createQuery(sb.toString()).setLong("shopId", shopId);
    return query;
  }

  public static Query queryDebtRemindEvent(Session session, Long shopId, Boolean isOverdue, Boolean hasRemind, Long flashTime, Integer pageNo, Integer pageSize) {
    StringBuffer sb = new StringBuffer();
    //数据展示顺序，1、今明过期的（时间正序），2、已过期的（时间倒序），3、过期时间在明天以后的（时间正序），4、未设时间的，5、已提醒的（时间倒序）
    if((hasRemind==null && isOverdue==null) || (isOverdue!=null && isOverdue==false)){
      //今明过期的
      sb.append("select x.customer_id, x.remind_status, x.remind_time, y.total_debt, x.old_remind_event_id, x.id, x.supplier_id ");
      sb.append("from remind_event x, (");
      sb.append("select d.customer_id cid, sum(d.debt) total_debt, min(d.remind_time) rtime, d.created ");
      sb.append("from remind_event d ");
      sb.append("where d.shop_id = :shopId and d.debt>0 ");
      sb.append("and d.remind_status = '" + UserConstant.Status.ACTIVITY + "' ");
      sb.append("and (d.deleted_type='FALSE' or d.deleted_type is null) ");
      sb.append("group by d.customer_id ");
      sb.append("order by d.remind_time asc");
      sb.append(") y ");
      sb.append("where x.customer_id = y.cid and x.created = y.created ");
      sb.append("and x.remind_status = '" + UserConstant.Status.ACTIVITY + "' ");
      sb.append("and x.remind_time >= " + flashTime);
      sb.append(" and x.remind_time < " + (flashTime+2*24*3600*1000));
//      sb.append(" order by x.remind_time asc");

      //供应商的记录
      sb.append(" union all ");
      sb.append("select x.customer_id, x.remind_status, x.remind_time, y.total_debt, x.old_remind_event_id, x.id, x.supplier_id ");
      sb.append("from remind_event x, (");
      sb.append("select d.supplier_id sid, sum(d.debt) total_debt, min(d.remind_time) rtime, d.created ");
      sb.append("from remind_event d ");
      sb.append("where d.shop_id = :shopId and d.debt>0 ");
      sb.append("and d.remind_status = '" + UserConstant.Status.ACTIVITY + "' ");
      sb.append("and (d.deleted_type='FALSE' or d.deleted_type is null) ");
      sb.append("group by d.supplier_id ");
      sb.append("order by d.remind_time asc");
      sb.append(") y ");
      sb.append("where x.supplier_id = y.sid and x.created = y.created ");
      sb.append("and x.remind_status = '" + UserConstant.Status.ACTIVITY + "' ");
      sb.append("and x.remind_time >= " + flashTime);
      sb.append(" and x.remind_time < " + (flashTime+2*24*3600*1000));
//      sb.append(" order by x.remind_time asc");
    }

    if((hasRemind==null && isOverdue==null) || (isOverdue!=null && isOverdue==true)){
      //已过期的
      if(hasRemind==null && isOverdue==null){
        sb.append(" union all ");
      }
      sb.append("select x.customer_id, x.remind_status, x.remind_time, y.total_debt, x.old_remind_event_id, x.id, x.supplier_id ");
      sb.append("from remind_event x, (");
      sb.append("select d.customer_id cid, sum(d.debt) total_debt, min(d.remind_time) rtime, d.created ");
      sb.append("from remind_event d ");
      sb.append("where d.shop_id = :shopId and d.debt>0 ");
      sb.append("and d.remind_status = '" + UserConstant.Status.ACTIVITY + "' ");
      sb.append("and (d.deleted_type='FALSE' or d.deleted_type is null) ");
      sb.append("group by d.customer_id ");
      sb.append("order by d.remind_time desc");
      sb.append(") y ");
      sb.append("where x.customer_id = y.cid and x.created = y.created ");
      sb.append("and x.remind_status = '" + UserConstant.Status.ACTIVITY + "' ");
      sb.append("and x.remind_time < " + flashTime);
//      sb.append(" order by x.remind_time asc");
      //供应商的记录
      sb.append(" union all ");
      sb.append("select x.customer_id, x.remind_status, x.remind_time, y.total_debt, x.old_remind_event_id, x.id, x.supplier_id ");
      sb.append("from remind_event x, (");
      sb.append("select d.supplier_id sid, sum(d.debt) total_debt, min(d.remind_time) rtime, d.created ");
      sb.append("from remind_event d ");
      sb.append("where d.shop_id = :shopId and d.debt>0 ");
      sb.append("and d.remind_status = '" + UserConstant.Status.ACTIVITY + "' ");
      sb.append("and (d.deleted_type='FALSE' or d.deleted_type is null) ");
      sb.append("group by d.supplier_id ");
      sb.append("order by d.remind_time desc");
      sb.append(") y ");
      sb.append("where x.supplier_id = y.sid and x.created = y.created ");
      sb.append("and x.remind_status = '" + UserConstant.Status.ACTIVITY + "' ");
      sb.append("and x.remind_time < " + flashTime);
//      sb.append(" order by x.remind_time asc");
    }

    if((hasRemind==null && isOverdue==null) || (isOverdue!=null && isOverdue==false)){
      sb.append(" union all ");
      //过期时间在明天以后的
      sb.append("select x.customer_id, x.remind_status, x.remind_time, y.total_debt, x.old_remind_event_id, x.id, x.supplier_id ");
      sb.append("from remind_event x, (");
      sb.append("select d.customer_id cid, sum(d.debt) total_debt, min(d.remind_time) rtime, d.created ");
      sb.append("from remind_event d ");
      sb.append("where d.shop_id = :shopId and d.debt>0 ");
      sb.append("and d.remind_status = '" + UserConstant.Status.ACTIVITY + "' ");
      sb.append("and (d.deleted_type='FALSE' or d.deleted_type is null) ");
      sb.append("group by d.customer_id ");
      sb.append("order by d.remind_time asc");
      sb.append(") y ");
      sb.append("where x.customer_id = y.cid and x.created = y.created ");
      sb.append("and x.remind_status = '" + UserConstant.Status.ACTIVITY + "' ");
      sb.append("and x.remind_time >= " + (flashTime+2*24*3600*1000));
//      sb.append(" order by x.remind_time asc");
      //供应商的记录
      sb.append(" union all ");
      sb.append("select x.customer_id, x.remind_status, x.remind_time, y.total_debt, x.old_remind_event_id, x.id, x.supplier_id ");
      sb.append("from remind_event x, (");
      sb.append("select d.supplier_id sid, sum(d.debt) total_debt, min(d.remind_time) rtime, d.created ");
      sb.append("from remind_event d ");
      sb.append("where d.shop_id = :shopId and d.debt>0 ");
      sb.append("and d.remind_status = '" + UserConstant.Status.ACTIVITY + "' ");
      sb.append("and (d.deleted_type='FALSE' or d.deleted_type is null) ");
      sb.append("group by d.supplier_id ");
      sb.append("order by d.remind_time asc");
      sb.append(") y ");
      sb.append(" where x.supplier_id = y.sid and x.created = y.created ");
      sb.append("and x.remind_status = '" + UserConstant.Status.ACTIVITY + "' ");
      sb.append("and x.remind_time >= " + (flashTime+2*24*3600*1000));
//      sb.append(" order by x.remind_time asc");
      sb.append(" union all ");

      //未设时间的
      sb.append("select x.customer_id, x.remind_status, x.remind_time, y.total_debt, x.old_remind_event_id, x.id, x.supplier_id ");
      sb.append("from remind_event x, (");
      sb.append("select d.customer_id cid, sum(d.debt) total_debt, min(d.remind_time) rtime, d.created ");
      sb.append("from remind_event d ");
      sb.append("where d.shop_id = :shopId and d.debt>0 ");
      sb.append("and d.remind_status = '" + UserConstant.Status.ACTIVITY + "' ");
      sb.append("and (d.deleted_type='FALSE' or d.deleted_type is null) ");
      sb.append("group by d.customer_id ");
      sb.append(") y ");
      sb.append("where x.customer_id = y.cid and x.created = y.created ");
      sb.append("and x.remind_status = '" + UserConstant.Status.ACTIVITY + "' ");
      sb.append("and x.remind_time is null ");

      //供应商的记录
      sb.append(" union all ");
      sb.append("select x.customer_id, x.remind_status, x.remind_time, y.total_debt, x.old_remind_event_id, x.id, x.supplier_id ");
      sb.append("from remind_event x, (");
      sb.append("select d.supplier_id sid, sum(d.debt) total_debt, min(d.remind_time) rtime, d.created ");
      sb.append("from remind_event d ");
      sb.append("where d.shop_id = :shopId and d.debt>0 ");
      sb.append("and d.remind_status = '" + UserConstant.Status.ACTIVITY + "' ");
      sb.append("and (d.deleted_type='FALSE' or d.deleted_type is null) ");
      sb.append("group by d.supplier_id ");
      sb.append(") y ");
      sb.append("where x.supplier_id = y.sid and x.created = y.created ");
      sb.append("and x.remind_status = '" + UserConstant.Status.ACTIVITY + "' ");
      sb.append("and x.remind_time is null " );
    }



    if((hasRemind==null && isOverdue==null) || (hasRemind!=null && hasRemind==true)){
      //已提醒
      if(hasRemind==null && isOverdue==null){
        sb.append(" union all ");
      }
      sb.append("select x.customer_id, x.remind_status, x.remind_time, y.total_debt, x.old_remind_event_id, x.id, x.supplier_id ");
      sb.append("from remind_event x, (");
      sb.append("select d.customer_id cid, sum(d.debt) total_debt, min(d.remind_time) rtime, d.created ");
      sb.append("from remind_event d ");
      sb.append("where d.shop_id = :shopId and d.debt>0 ");
      sb.append("and d.remind_status = '" + UserConstant.Status.REMINDED + "' ");
      sb.append("and (d.deleted_type='FALSE' or d.deleted_type is null) ");
      sb.append("group by d.customer_id ");
      sb.append("order by d.remind_time desc");
      sb.append(") y ");
      sb.append("where x.customer_id = y.cid and x.created = y.created ");
      sb.append("and x.remind_status = '" + UserConstant.Status.REMINDED + "' ");
//      sb.append(" order by x.remind_time asc");
      //供应商的记录
      sb.append(" union all ");
      sb.append("select x.customer_id, x.remind_status, x.remind_time, y.total_debt, x.old_remind_event_id, x.id, x.supplier_id ");
      sb.append("from remind_event x, (");
      sb.append("select d.supplier_id sid, sum(d.debt) total_debt, min(d.remind_time) rtime, d.created ");
      sb.append("from remind_event d ");
      sb.append("where d.shop_id = :shopId and d.debt>0 ");
      sb.append("and d.remind_status = '" + UserConstant.Status.REMINDED + "' ");
      sb.append("and (d.deleted_type='FALSE' or d.deleted_type is null) ");
      sb.append("group by d.supplier_id ");
      sb.append("order by d.remind_time desc");
      sb.append(") y ");
      sb.append("where x.supplier_id = y.sid and x.created = y.created ");
      sb.append("and x.remind_status = '" + UserConstant.Status.REMINDED + "' ");
//      sb.append(" order by x.remind_time asc");
    }
    return session.createSQLQuery(sb.toString()).setLong("shopId",shopId).setFirstResult(pageNo * pageSize).setMaxResults(pageSize);
  }

  public static Query countDebtRemindEvent(Session session, Long shopId, Boolean isOverdue, Boolean hasRemind, Long flashTime) {
    StringBuffer sb = new StringBuffer();
    sb.append("select count(*) from (");
    //未过期
    sb.append("select x.customer_id, x.remind_status, x.remind_time, y.total_debt, x.object_id ");
    sb.append("from remind_event x, (");
    sb.append("select d.customer_id cid, sum(d.debt) total_debt, min(d.remind_time) rtime, d.created ");
    sb.append("from remind_event d ");
    sb.append("where d.shop_id = :shopId and d.debt>0 ");
    if(hasRemind == null){
      sb.append(" and (d.remind_status = '" + UserConstant.Status.ACTIVITY + "' or d.remind_status = '" + UserConstant.Status.REMINDED + "') ");
    }else if(hasRemind!=null && !hasRemind){
      sb.append(" and d.remind_status = '" + UserConstant.Status.ACTIVITY + "' ");
    }else if(hasRemind!=null && hasRemind){
      sb.append(" and d.remind_status = '" + UserConstant.Status.REMINDED + "' ");
    }
    sb.append("and (d.deleted_type='FALSE' or d.deleted_type is null) ");
    sb.append("group by d.customer_id ");
    sb.append("order by d.remind_time ");
    sb.append(") y ");
    sb.append("where x.customer_id = y.cid and x.created = y.created ");
    if(hasRemind == null){
      sb.append("and (x.remind_status = '" + UserConstant.Status.ACTIVITY + "' or x.remind_status = '" + UserConstant.Status.REMINDED + "') ");
    }else if(hasRemind!=null && !hasRemind){
      sb.append(" and x.remind_status = '" + UserConstant.Status.ACTIVITY + "' ");
    }else if(hasRemind!=null && hasRemind){
      sb.append("and x.remind_status = '" + UserConstant.Status.REMINDED + "' ");
    }

    if(isOverdue!=null && !isOverdue){
      sb.append("and (x.remind_time >= " + flashTime);
      sb.append(" or x.remind_time is null) ");
    }else if(isOverdue!=null && isOverdue){
      sb.append("and x.remind_time < " + flashTime);
    }

    //供应商的记录
    sb.append(" union all ");
    sb.append("select x.supplier_id, x.remind_status, x.remind_time, y.total_debt, x.object_id ");
    sb.append("from remind_event x, (");
    sb.append("select d.supplier_id sid, sum(d.debt) total_debt, min(d.remind_time) rtime, d.created ");
    sb.append("from remind_event d ");
    sb.append("where d.shop_id = :shopId and d.debt>0 ");
    if(hasRemind == null){
      sb.append(" and (d.remind_status = '" + UserConstant.Status.ACTIVITY + "' or d.remind_status = '" + UserConstant.Status.REMINDED + "') ");
    }else if(hasRemind!=null && !hasRemind){
      sb.append(" and d.remind_status = '" + UserConstant.Status.ACTIVITY + "' ");
    }else if(hasRemind!=null && hasRemind){
      sb.append("and d.remind_status = '" + UserConstant.Status.REMINDED + "' ");
    }
    sb.append("and (d.deleted_type='FALSE' or d.deleted_type is null) ");
    sb.append("group by d.supplier_id ");
    sb.append("order by d.remind_time ");
    sb.append(") y, bcuser.supplier s ");
    sb.append("where x.supplier_id = y.sid and y.sid = s.id and s.identity is null and x.created = y.created ");
    if(hasRemind == null){
      sb.append("and (x.remind_status = '" + UserConstant.Status.ACTIVITY + "' or x.remind_status = '" + UserConstant.Status.REMINDED + "') ");
    }else if(hasRemind!=null && !hasRemind){
      sb.append(" and x.remind_status = '" + UserConstant.Status.ACTIVITY + "' ");
    }else if(hasRemind!=null && hasRemind){
      sb.append("and x.remind_status = '" + UserConstant.Status.REMINDED + "' ");
    }
    if(isOverdue!=null && !isOverdue){
      sb.append("and (x.remind_time >= " + flashTime);
      sb.append(" or x.remind_time is null) ");
    }else if(isOverdue!=null && isOverdue){
      sb.append("and x.remind_time < " + flashTime);
    }
    sb.append(") tmp");
    return session.createSQLQuery(sb.toString()).setLong("shopId",shopId);
  }

  public static Query RFCountDebtRemindEvent(Session session, Long shopId, Boolean isOverdue, Boolean hasRemind, Long flashTime) {
    StringBuffer sb = new StringBuffer();
    sb.append("select temp.customer_id as customer_id from (");
    //数据展示顺序，1、今明过期的，2、已过期的，3、过期时间在明天以后的，4、未设时间的，5、已提醒的
    if((hasRemind==null && isOverdue==null) || (isOverdue!=null && isOverdue==false)){
      //未过期
      sb.append("select x.customer_id, x.remind_status, x.remind_time, y.total_debt, x.object_id ");
      sb.append("from remind_event x, (");
      sb.append("select d.customer_id cid, sum(d.debt) total_debt, min(d.remind_time) rtime, d.created time ");
      sb.append("from remind_event d ");
      sb.append("where d.shop_id = :shopId and d.debt>0 ");
      sb.append("and d.remind_status = '" + UserConstant.Status.ACTIVITY + "' ");
      sb.append("group by d.customer_id ");
      sb.append("order by d.remind_time ");
      sb.append(") y ");
      sb.append("where x.customer_id = y.cid and x.created = y.time ");
      sb.append("and x.remind_status = '" + UserConstant.Status.ACTIVITY + "' ");
      sb.append("and (x.remind_time >= " + flashTime);
      sb.append(" or x.remind_time is null) ");
    }

    if((hasRemind==null && isOverdue==null) || (isOverdue!=null && isOverdue==true)){
      //已过期的
      if(hasRemind==null && isOverdue==null){
        sb.append(" union all ");
      }
      sb.append("select x.customer_id, x.remind_status, x.remind_time, y.total_debt, x.object_id ");
      sb.append("from remind_event x, (");
      sb.append("select d.customer_id cid, sum(d.debt) total_debt, min(d.remind_time) rtime, d.created time ");
      sb.append("from remind_event d ");
      sb.append("where d.shop_id = :shopId and d.debt>0 ");
      sb.append("and d.remind_status = '" + UserConstant.Status.ACTIVITY + "' ");
      sb.append("group by d.customer_id ");
      sb.append("order by d.remind_time ");
      sb.append(") y ");
      sb.append("where x.customer_id = y.cid and x.created = y.time ");
      sb.append("and x.remind_status = '" + UserConstant.Status.ACTIVITY + "' ");
      sb.append("and x.remind_time < " + flashTime);
    }

    if((hasRemind==null && isOverdue==null) || (hasRemind!=null && hasRemind==true)){
      //已提醒
      if(hasRemind==null && isOverdue==null){
        sb.append(" union all ");
      }
      sb.append("select x.customer_id, x.remind_status, x.remind_time, y.total_debt, x.object_id ");
      sb.append("from remind_event x, (");
      sb.append("select d.customer_id cid, sum(d.debt) total_debt, min(d.remind_time) rtime, d.created time ");
      sb.append("from remind_event d ");
      sb.append("where d.shop_id = :shopId and d.debt>0 ");
      sb.append("and d.remind_status = '" + UserConstant.Status.REMINDED + "' ");
      sb.append("group by d.customer_id ");
      sb.append("order by d.remind_time ");
      sb.append(") y ");
      sb.append("where x.customer_id = y.cid and x.created = y.time ");
      sb.append("and x.remind_status = '" + UserConstant.Status.REMINDED + "' ");
    }
    sb.append(") temp");
    return session.createSQLQuery(sb.toString()).addScalar("customer_id",StandardBasicTypes.LONG).setLong("shopId",shopId);
  }

  public static Query queryTxnRemindEvent(Session session, Long shopId, Long flashTime, Integer pageNo, Integer pageSize){
    StringBuffer sb = new StringBuffer();
    sb.append("select r.* from remind_event r where r.event_Type = 'TXN' and r.shop_Id = :shopId ");
    sb.append(" and r.remind_Time >= " + flashTime);
    sb.append(" and r.remind_Time < " + (flashTime + 2*24*3600*1000));
    sb.append(" and r.remind_Status = 'activity'");
    sb.append(" and (r.deleted_type = 'FALSE' or r.deleted_type is null) ");
    sb.append(" union all ");
    sb.append("select r.* from remind_event r where r.event_Type = 'TXN' and r.shop_Id = :shopId ");
    sb.append(" and (r.remind_Time < " + flashTime);
    sb.append(" or r.remind_Time >= " + (flashTime + 2*24*3600*1000));
    sb.append(") and r.remind_Status = 'activity'");
    sb.append(" and (r.deleted_type = 'FALSE' or r.deleted_type is null) ");
    Query query = session.createSQLQuery(sb.toString()).addEntity(RemindEvent.class).setLong("shopId", shopId);
    if (pageNo != null && pageSize!=null) {
      int firstResult = pageNo.intValue() * pageSize.intValue();
      query.setFirstResult(firstResult).setMaxResults(pageSize.intValue());
    }
    return query;
  }

  public static Query countFlashTxnRemindEvent(Session session, Long shopId, Long flashTime){
    StringBuffer sb = new StringBuffer();
    sb.append("select count(*) from RemindEvent r where r.eventType = 'TXN' and r.shopId = :shopId ");
    sb.append(" and r.remindTime >= " + flashTime);
    sb.append(" and r.remindTime < " + (flashTime + 2*24*3600*1000));
    sb.append(" and r.remindStatus = 'activity'");
    Query query = session.createQuery(sb.toString()).setLong("shopId", shopId);
    return query;
  }

  public static Query countUnflashTxnRemindEvent(Session session, Long shopId, Long flashTime){
    StringBuffer sb = new StringBuffer();
    sb.append("select count(*) from RemindEvent r where r.eventType = 'TXN' and r.shopId = :shopId ");
    sb.append(" and (r.remindTime < " + flashTime);
    sb.append(" or r.remindTime >= " + (flashTime + 2*24*3600*1000));
    sb.append(") and r.remindStatus = 'activity'");
    Query query = session.createQuery(sb.toString()).setLong("shopId", shopId);
    return query;
  }

  public static Query countTotalTxnRemindEvent(Session session, Long shopId){
    return session.createQuery("select count(*) from RemindEvent r where r.eventType = 'TXN' and r.shopId = :shopId and r.remindStatus = 'activity' and (r.deletedType =:deletedType or r.deletedType is null)").setLong("shopId", shopId).setParameter("deletedType",DeletedType.FALSE);
  }


  public static Query queryCustomerRemindEvent(Session session, Long shopId, Boolean isOverdue, Boolean hasRemind, Long flashTime, Integer pageNo, Integer pageSize){
    StringBuffer sb = new StringBuffer();
    //数据展示顺序，1、今明过期的，2、已过期的，3、过期时间在明天以后的，4、已提醒的
    if((hasRemind==null && isOverdue==null) || (isOverdue!=null && isOverdue==false)){
      //今明过期的
      sb.append("select * from (");
      sb.append("select r.* from remind_event r where r.event_Type = 'CUSTOMER_SERVICE' and r.shop_Id = :shopId ");
      sb.append(" and r.remind_Time is null and r.remind_mileage >0 ");
      sb.append(" and r.remind_Status = 'activity'");
      sb.append(") temp0");
      sb.append(" union all ");
      sb.append("select * from (");
      sb.append("select r.* from remind_event r where (r.event_Type = 'CUSTOMER_SERVICE' or r.event_Type = 'MEMBER_SERVICE') and r.shop_Id = :shopId ");
      sb.append(" and r.remind_Time >= " + flashTime);
      sb.append(" and r.remind_Time <= " + (flashTime + 2*24*3600*1000));
      sb.append(" and r.remind_Status = 'activity'");
      sb.append(" order by remind_Time asc");
      sb.append(") temp1");
    }
    if((hasRemind==null && isOverdue==null) || (isOverdue!=null && isOverdue==true)){
      //已过期的
      if(hasRemind==null && isOverdue==null){
        sb.append(" union all ");
      }
      sb.append("select * from (");
      sb.append("select r.* from remind_event r where (r.event_Type = 'CUSTOMER_SERVICE' or r.event_Type = 'MEMBER_SERVICE') and r.shop_Id = :shopId ");
      sb.append(" and r.remind_Time < " + flashTime);
      sb.append(" and r.remind_Time > 0");
      sb.append(" and r.remind_Status = 'activity'");
      sb.append(" order by remind_Time desc");
      sb.append(") temp2");
    }
    if((hasRemind==null && isOverdue==null) || (isOverdue!=null && isOverdue==false)){
      sb.append(" union all ");
      //过期时间在明天以后的
      sb.append("select * from (");
      sb.append("select r.* from remind_event r where (r.event_Type = 'CUSTOMER_SERVICE' or r.event_Type = 'MEMBER_SERVICE') and r.shop_Id = :shopId ");
      sb.append(" and r.remind_Time > " + (flashTime + 2*24*3600*1000));
      sb.append(" and r.remind_Status = 'activity'");
      sb.append(" order by remind_Time asc");
      sb.append(") temp3");
    }
    if((hasRemind==null && isOverdue==null) || (hasRemind!=null && hasRemind==true)){
      //已提醒
      if(hasRemind==null && isOverdue==null){
        sb.append(" union all ");
      }
      sb.append("select * from (");
      sb.append("select r.* from remind_event r where (r.event_Type = 'CUSTOMER_SERVICE' or r.event_Type = 'MEMBER_SERVICE') and r.shop_Id = :shopId ");
      sb.append(" and r.remind_Status = 'reminded'");
      sb.append(" and (r.remind_Time > 0 or r.remind_time is null and r.remind_mileage >0)");
      sb.append(" order by remind_Time desc");
      sb.append(") temp4");
    }
    Query q = session.createSQLQuery(sb.toString()).addEntity(RemindEvent.class).setLong("shopId",shopId);
    if(pageNo!=null && pageSize!=null){
      q.setFirstResult(pageNo * pageSize).setMaxResults(pageSize);
    }
    return q;
  }

  public static Query countCustomerRemindEvent1(Session session, Long shopId){
    StringBuffer sb = new StringBuffer();
    //默认全部的
    sb.append("select count(*) from RemindEvent r where (r.eventType = 'CUSTOMER_SERVICE' or r.eventType = 'MEMBER_SERVICE') and r.shopId = :shopId ");
    sb.append(" and r.remindStatus <> 'canceled' and (r.remindTime > 0 or r.remindTime is null and r.remindMileage >0)");
    return session.createQuery(sb.toString()).setLong("shopId",shopId);
  }

  public static Query countCustomerRemindEvent2(Session session, Long shopId,  Long flashTime){
    StringBuffer sb = new StringBuffer();
    //未提醒，已经过期的
    sb.append("select count(*) from RemindEvent r where (r.eventType = 'CUSTOMER_SERVICE' or r.eventType = 'MEMBER_SERVICE') and r.shopId = :shopId ");
    sb.append(" and r.remindTime <= " + flashTime);
    sb.append(" and r.remindStatus = 'activity' and r.remindTime > 0");
    return session.createQuery(sb.toString()).setLong("shopId",shopId);
  }

  public static Query countCustomerRemindEvent3(Session session, Long shopId, Long flashTime){
    StringBuffer sb = new StringBuffer();
    //未提醒，未过期的
    sb.append("select count(*) from RemindEvent r where (r.eventType = 'CUSTOMER_SERVICE' or r.eventType = 'MEMBER_SERVICE') and r.shopId = :shopId ");
    sb.append(" and (r.remindTime >:flashTime or r.remindTime is null and r.remindMileage >0)");
    sb.append(" and r.remindStatus = 'activity'");
    return session.createQuery(sb.toString()).setLong("shopId",shopId).setParameter("flashTime",flashTime);
  }

  public static Query countCustomerRemindEvent4(Session session, Long shopId){
    StringBuffer sb = new StringBuffer();
    //已提醒的
    sb.append("select count(*) from RemindEvent r where (r.eventType = 'CUSTOMER_SERVICE' or r.eventType = 'MEMBER_SERVICE') and r.shopId = :shopId ");
    sb.append(" and r.remindStatus = 'reminded' and (r.remindTime > 0 or r.remindTime is null and r.remindMileage >0)");
    return session.createQuery(sb.toString()).setLong("shopId",shopId);
  }

  public static Query cancelRemindEventByOrderId(Session session, RemindEventType type, Long orderId){
    return session.createQuery("update RemindEvent r set r.remindStatus = 'canceled' where r.orderId = :orderId and r.eventType = :type and r.remindStatus <> 'canceled'")
        .setLong("orderId",orderId).setString("type",type.toString());
  }

  public static Query cancelRemindEventByOldRemindEventId(Session session, RemindEventType type, Long oldRemindEventId){
    return session.createQuery("update RemindEvent r set r.remindStatus = 'canceled' where r.oldRemindEventId = :oldRemindEventId and r.eventType = :type and r.remindStatus <> 'canceled'")
        .setLong("oldRemindEventId",oldRemindEventId).setString("type",type.toString());
  }

  public static Query getRemindEventByOrderId(Session session, RemindEventType type, Long shopId, Long orderId) {
    return session.createQuery("select r from RemindEvent r where r.shopId =:shopId and  r.orderId = :orderId and r.eventType = :type and r.remindStatus <> 'canceled'")
        .setLong("orderId", orderId).setString("type", type.toString()).setLong("shopId", shopId);
  }

  public static Query getRemindEventListByCustomerIdAndType(Session session, RemindEventType type, Long customerId){
    return session.createQuery("select r from RemindEvent r where r.eventType = :type and r.customerId = :customerId and r.remindStatus <> 'canceled'")
        .setString("type",type.toString()).setLong("customerId",customerId);
  }

  public static Query getRemindEventListBySupplierIdAndType(Session session, RemindEventType type, Long supplierId){
     return session.createQuery("select r from RemindEvent r where r.eventType = :type and r.supplierId = :supplierId and r.remindStatus <> 'canceled'")
                .setString("type",type.toString()).setLong("supplierId",supplierId);
  }

  public static Query getRemindEventByCustomerId(Session session, Long shopId, Long[] customerIds){
    return session.createQuery("select r from RemindEvent r where r.shopId = :shopId and r.customerId = :customerId")
        .setLong("shopId",shopId).setParameterList("customerId",customerIds);
  }

  public static Query getRemindEventListByOrderIdAndObjectIdAndEventStatus(Session session, RemindEventType type, Long orderId, Long objectId, String eventStatus){
    String hql = "select r from RemindEvent r where r.eventType = :type and r.orderId = :orderId and r.eventStatus = :eventStatus and r.remindStatus <> 'canceled'";
    if(objectId!=null){
      hql = hql + " and r.objectId = :objectId";
    }
    Query q = session.createQuery(hql).setString("type",type.toString()).setLong("orderId",orderId).setString("eventStatus",eventStatus);
    if(objectId!=null){
      q.setLong("objectId",objectId);
    }
    return q;
  }

  public static Query getRemindEventByOldRemindEventId(Session session, RemindEventType type,Long shopId, Long oldRemindEventId){
    return session.createQuery("select r from RemindEvent r where r.eventType = :type and r.shopId =:shopId and r.oldRemindEventId = :oldRemindEventId and r.remindStatus <> 'canceled'")
        .setString("type",type.toString()).setLong("oldRemindEventId",oldRemindEventId).setLong("shopId",shopId);
  }

  public static Query getRemindEventByOldRemindEventIds(Session session,Long shopId,Set<Long> oldRemindEventId){
    return session.createQuery("select r from RemindEvent r where r.shopId = :shopId and r.oldRemindEventId in (:oldRemindEventId) and r.remindStatus <> 'canceled'")
        .setLong("shopId", shopId).setParameterList("oldRemindEventId", oldRemindEventId);
  }

  public static Query mergerCustmerRemindEvent(Session session, Long parentId, Long[] childIds){
    return session.createQuery("update RemindEvent r set r.customerId = :parentId where r.customerId in (:childIds)").setLong("parentId",parentId).setParameterList("childIds",childIds);
  }

  public static Query updateCustomerBirthdayRemindEvent(Session session, Long customerId, Long newBirthday){
    return session.createQuery("update RemindEvent r set r.remindTime = :newBirthday where r.eventType = 'CUSTOMER_SERVICE' and r.eventStatus = '生日' and r.customerId = :customerId")
        .setLong("customerId",customerId).setLong("newBirthday",newBirthday);
  }

  public static Query cancelRemindEventByOrderIdAndStatus(Session session, RemindEventType type, Long orderId, RepairRemindEventTypes status){
    return session.createQuery("update RemindEvent r set r.remindStatus = 'canceled' where r.orderId = :orderId and r.eventType = :type and r.eventStatus = :status")
        .setLong("orderId",orderId).setString("type",type.toString()).setString("status",status.toString());
  }

  public static Query cancelRemindEventByOrderTypeAndOrderId(Session session, RemindEventType type, OrderTypes orderType, Long orderId){
    return session.createQuery("update RemindEvent r set r.remindStatus = 'canceled' where r.orderId = :orderId and r.eventType = :type and r.orderType = :orderType")
        .setLong("orderId",orderId).setString("type",type.toString()).setString("orderType",orderType.toString());
  }

  public static Query cancelRemindEventByOrderIdAndObjectId(Session session, RemindEventType type, String eventStatus, Long orderId, Long objectId){
    String hql = "update RemindEvent r set r.remindStatus = 'canceled' where r.orderId = :orderId and r.eventType = :type and r.objectId = :objectId";
    if(eventStatus!=null){
      hql = hql + " and r.eventStatus = :eventStatus";
    }
    Query q = session.createQuery(hql).setLong("orderId",orderId).setString("type",type.toString()).setLong("objectId",objectId);
    if(eventStatus!=null){
      q.setString("eventStatus",eventStatus);
    }
    return q;
  }

  public static Query getDebtFromReceivableByCustomerId(Session session, Long shopId, Long customerId, OrderDebtType orderDebtType, ReceivableStatus receivableStatus) {
    return session.createQuery(" select sum(debt),count(*) from Receivable where shopId=:shopId and customerId=:customerId  and orderDebtType=:orderDebtType  and statusEnum =:statusEnum ")
        .setLong("shopId", shopId).setLong("customerId", customerId).setParameter("orderDebtType", orderDebtType).setParameter("statusEnum", ReceivableStatus.FINISH);
  }

  public static Query getTotalReturnAmountByCustomerIds(Session session, Long shopId, Long... customerIds) {
    return session.createQuery("select customerId, sum(settledAmount + debt), count(*) from Receivable " +
        "where shopId=:shopId and customerId in (:customerId) and statusEnum=:status and orderTypeEnum=:orderType " +
        "group by customerId")
        .setLong("shopId", shopId).setParameterList("customerId", customerIds).setParameter("status", ReceivableStatus.FINISH).setParameter("orderType", OrderTypes.SALE_RETURN);
  }

  public static Query getLackStorageRemind(Session session, Long shopId, Integer pageNo, Integer pageSize){
    Query query = session.createQuery("select r from RemindEvent r where r.shopId = :shopId and r.eventType = 'REPAIR' and r.eventStatus = 'LACK' and r.remindStatus <> 'canceled'").setLong("shopId",shopId);
    if (pageNo != null && pageSize!=null) {
      int firstResult = pageNo.intValue() * pageSize.intValue();
      query.setFirstResult(firstResult).setMaxResults(pageSize.intValue());
    }
    return query;
  }

  public static Query countLackStorageRemind(Session session, Long shopId){
    return session.createQuery("select count(*) from RemindEvent r where r.shopId = :shopId and r.eventType = 'REPAIR' and r.eventStatus = 'LACK' and r.remindStatus <> 'canceled'").setLong("shopId",shopId);
  }


  public static Query countShoppingCartItemByUserId(Session session, Long shopId,Long userId){
    return session.createQuery("select count(*) from ShoppingCartItem s where s.shopId = :shopId and s.userId=:userId").setLong("shopId",shopId).setLong("userId",userId);
  }

  public static Query getShoppingCartItemByUserId(Session session, Long shopId,Long userId){
    return session.createQuery("from ShoppingCartItem s where s.shopId = :shopId and s.userId=:userId order by s.editDate desc").setLong("shopId",shopId).setLong("userId",userId);
  }

  public static Query getShoppingCartItemById(Session session, Long shopId,Long userId,Long... shoppingCartItemId){
    return session.createQuery("from ShoppingCartItem s where s.shopId = :shopId and s.userId=:userId and s.id in(:shoppingCartItemId)").setLong("shopId",shopId).setLong("userId",userId).setParameterList("shoppingCartItemId",shoppingCartItemId);
  }

  public static Query getShoppingCartItemByUserIdAndProduct(Session session, Long shopId,Long userId,Long productLocalInfoId){
    return session.createQuery("from ShoppingCartItem s where s.shopId = :shopId and s.userId=:userId and s.productLocalInfoId =:productLocalInfoId").setLong("shopId",shopId).setLong("userId",userId).setLong("productLocalInfoId",productLocalInfoId);
  }

  public static Query getCommentRecordByOrderId(Session session, Long commentatorShopId, Long orderId) {

    if (commentatorShopId == null) {
      return session.createQuery(" from CommentRecord sa where sa.orderId=:orderId ")
          .setLong("orderId", orderId);
    }

    return session.createQuery(" from CommentRecord sa where sa.orderId=:orderId and sa.commentatorShopId=:commentatorShopId")
        .setLong("orderId", orderId).setLong("commentatorShopId", commentatorShopId);
  }

  public static Query getCommentRecordByShopId(Session session, Long commentTargetShopId, CommentStatus commentStatus) {

    if(commentStatus == null){
      return session.createQuery(" from CommentRecord sa where sa.commentTargetShopId=:commentTargetShopId ")
        .setLong("commentTargetShopId", commentTargetShopId);
    }

    return session.createQuery(" from CommentRecord sa where sa.commentTargetShopId=:commentTargetShopId and sa.commentStatus=:commentStatus")
        .setLong("commentTargetShopId", commentTargetShopId).setParameter("commentStatus", commentStatus);
  }



  public static Query getCommentStatByShopId(Session session, Long supplierShopId) {

    return session.createQuery(" from CommentStat sa where sa.shopId=:supplierShopId ")
        .setLong("supplierShopId", supplierShopId);
  }

  public static Query countSupplierCommentRecord(Session session, Long commentTargetShopId, CommentStatus commentStatus) {

    return session.createQuery(" select count(*) from CommentRecord sa where sa.commentTargetShopId=:commentTargetShopId and sa.commentStatus=:commentStatus")
        .setLong("commentTargetShopId", commentTargetShopId).setParameter("commentStatus", commentStatus);
  }

  public static Query getSupplierCommentByPager(Session session, Long commentTargetShopId, CommentStatus commentStatus,Pager pager,Sort sort) {

    StringBuffer sb = new StringBuffer();
    sb.append(" from CommentRecord sa where sa.commentTargetShopId=:commentTargetShopId and sa.commentStatus=:commentStatus ");
    if (sort != null) {
      sb.append(sort.toOrderString());
    }
    return session.createQuery(sb.toString()).setLong("commentTargetShopId", commentTargetShopId).setParameter("commentStatus", commentStatus)
        .setFirstResult(pager.getRowStart()).setMaxResults(pager.getPageSize());
  }


  public static Query updateRemindEventByShopIdEventTypeObjectId(Session session, Long shopId, String eventType, String eventStatus, Long productId) {
    return session.createQuery("update RemindEvent set eventStatus=:eventStatus where shopId=:shopId and eventType=:eventType and objectId=:productId and remindStatus=:remindStatus")
        .setString("eventStatus", eventStatus).setLong("shopId", shopId).setString("eventType", eventType).setLong("productId", productId).setString("remindStatus", UserConstant.Status.ACTIVITY);
  }

  public static Query getCommentStatBySupplier(Session session, Collection<Long> supplierShopIds) {

    return session.createQuery(" from CommentStat sa where sa.shopId in(:supplierShopId) ")
        .setParameterList("supplierShopId", supplierShopIds);
  }


  public static Query getPurchaseOrderBySupplierShopId(Session session, Long supplierShopId) {
    StringBuffer sb = new StringBuffer();
    sb.append("from PurchaseOrder r where  r.supplierShopId=:supplierShopId ");

    Query query = session.createQuery(sb.toString()).setLong("supplierShopId", supplierShopId);

    return query;
  }


  public static Query searchBcgogoPaymentResult(Session session, BcgogoReceivableSearchCondition condition) {
    StringBuilder sql = new StringBuilder();
    sql.append("select brorr.id as bcgogoReceivableOrderRecordRelationId,brorr.receivable_method as receivableMethod," +
        "brorr.payment_method as paymentMethod,brr.operator_id as operatorId,brr.operator_time as operatorTime," +
        "brorr.instalment_plan_id as instalmentPlanId,brorr.instalment_plan_item_id as instalmentPlanItemId," +

        "brr.shop_id as shopId,brr.payee_id as payeeId,brr.payment_time as recordPaymentTime," +
        "brr.payment_amount as recordPaymentAmount,brr.paid_amount as recordPaidAmount,brr.submitter_id as submitterId,brr.submit_time as submitTime," +
        "brr.auditor_id as auditorId,brr.audit_time as auditTime,brr.status as status," +

        "bro.start_time as orderStartTime,bro.end_time as orderEndTime,bro.current_instalment_plan_end_time as currentInstalmentPlanEndTime," +
        "bro.received_amount as orderReceivedAmount,bro.receivable_amount as orderReceivableAmount,bro.receivable_content as receivableContent," +
        "bro.total_amount as orderTotalAmount,bro.status as orderPaymentStatus,bro.payment_type as orderPaymentType,bro.receipt_no as orderReceiptNo,bro.id as orderId,brorr.sms_recharge_id as smsRechargeId ");
    SQLQuery query = splittingBcgogoPaymentResultSql(session, condition, sql);
    query.setMaxResults(condition.getLimit()).setFirstResult(condition.getStart());
    query.addScalar("bcgogoReceivableOrderRecordRelationId", StandardBasicTypes.LONG)
        .addScalar("receivableMethod", StandardBasicTypes.STRING)
        .addScalar("paymentMethod", StandardBasicTypes.STRING)
        .addScalar("orderPaymentType", StandardBasicTypes.STRING)
        .addScalar("orderReceiptNo", StandardBasicTypes.STRING)
        .addScalar("orderId", StandardBasicTypes.LONG)
        .addScalar("status", StandardBasicTypes.STRING)
        .addScalar("receivableContent", StandardBasicTypes.STRING)
        .addScalar("recordPaymentAmount", StandardBasicTypes.DOUBLE)
        .addScalar("recordPaidAmount", StandardBasicTypes.DOUBLE)
        .addScalar("orderReceivedAmount", StandardBasicTypes.DOUBLE)
        .addScalar("orderReceivableAmount", StandardBasicTypes.DOUBLE)
        .addScalar("orderTotalAmount", StandardBasicTypes.DOUBLE)
        .addScalar("orderPaymentStatus", StandardBasicTypes.STRING)
        .addScalar("instalmentPlanId", StandardBasicTypes.LONG)
        .addScalar("instalmentPlanItemId", StandardBasicTypes.LONG)
        .addScalar("shopId", StandardBasicTypes.LONG)
        .addScalar("payeeId", StandardBasicTypes.LONG)
        .addScalar("recordPaymentTime", StandardBasicTypes.LONG)
        .addScalar("submitterId", StandardBasicTypes.LONG)
        .addScalar("auditorId", StandardBasicTypes.LONG)
        .addScalar("submitTime", StandardBasicTypes.LONG)
        .addScalar("auditTime", StandardBasicTypes.LONG)
        .addScalar("orderEndTime", StandardBasicTypes.LONG)
        .addScalar("orderStartTime", StandardBasicTypes.LONG)
        .addScalar("operatorId", StandardBasicTypes.LONG)
        .addScalar("operatorTime", StandardBasicTypes.LONG)
        .addScalar("currentInstalmentPlanEndTime", StandardBasicTypes.LONG)
        .addScalar("smsRechargeId",StandardBasicTypes.LONG);
    return query;
  }

  public static Query countBcgogoPayment(Session session, BcgogoReceivableSearchCondition condition) {
    StringBuilder sql = new StringBuilder();
    sql.append("select count(*) ");
    return splittingBcgogoPaymentResultSql(session, condition, sql);
  }
  public static Query statBcgogoReceivableOrderRecordByStatus(Session session, BcgogoReceivableSearchCondition condition) {
    StringBuilder sql = new StringBuilder();
    sql.append("select brr.status,brorr.payment_method,count(brr.id),sum(brr.paid_amount) ");
    condition.setGroupField("brr.status,brorr.payment_method");
    return splittingBcgogoPaymentResultSql(session, condition, sql);
  }
  private static SQLQuery splittingBcgogoPaymentResultSql(Session session, BcgogoReceivableSearchCondition condition, StringBuilder sql) {
    sql.append(" from bcgogo_receivable_order_record_relation brorr ");
    sql.append("left join bcgogo_receivable_record brr on brr.id=brorr.bcgogo_receivable_record_id ");
    sql.append("left join bcgogo_receivable_order bro on bro.id=brorr.bcgogo_receivable_order_id ");

    sql.append("where 1=1 ");
    if(!ArrayUtils.isEmpty(condition.getReceivableStatuses())){
      sql.append("and brr.status in (:status) ");
    }
    if(!ArrayUtils.isEmpty(condition.getPaymentMethods())){
      sql.append("and brorr.payment_method in (:paymentMethod) ");
    }
    if (!ArrayUtils.isEmpty(condition.getPaymentTypes())) {
      sql.append("and ( bro.payment_type in(:paymentType)  )");
    }
    if (condition.getStartTime() != null) {
      if (condition.isSearchByPendingReviewAndPaid()) {
        sql.append("and (").append(" brr.payment_time >= ").append(condition.getStartTime()).append(" ) ");
      } else {
        if (condition.getStatus() == BcgogoReceivableStatus.TO_BE_PAID) {
          sql.append("and (").append(" bro.start_time >= ").append(condition.getStartTime()).append(" or ")
              .append(" bro.current_instalment_plan_end_time >= ").append(condition.getStartTime()).append(" ) ");
        } else if (condition.getStatus() == BcgogoReceivableStatus.PENDING_REVIEW) {
          sql.append("and (").append(" brr.payment_time >= ").append(condition.getStartTime()).append(" ) ");
        } else if (condition.getStatus() == BcgogoReceivableStatus.HAS_BEEN_PAID) {
          sql.append("and (").append(" brr.audit_time >= ").append(condition.getStartTime()).append(" ) ");
        }
      }
    }
    if (condition.getEndTime() != null) {
      if (condition.isSearchByPendingReviewAndPaid()) {
        sql.append("and (").append(" brr.payment_time < ").append(condition.getEndTime()).append(" ) ");
      } else {
        if (condition.getStatus() == BcgogoReceivableStatus.TO_BE_PAID) {
          sql.append("and (").append(" bro.start_time < ").append(condition.getEndTime()).append(" or ")
              .append(" bro.current_instalment_plan_end_time < ").append(condition.getEndTime()).append(" ) ");
        } else if (condition.getStatus() == BcgogoReceivableStatus.PENDING_REVIEW) {
          sql.append("and (").append(" brr.payment_time < ").append(condition.getEndTime()).append(" ) ");
        } else if (condition.getStatus() == BcgogoReceivableStatus.HAS_BEEN_PAID) {
          sql.append("and (").append(" brr.audit_time < ").append(condition.getEndTime()).append(" ) ");
        }
      }
    }

    if (CollectionUtil.isNotEmpty(condition.getShopIds())) sql.append(" and brorr.shop_id in(:shopIds) ");
    if (StringUtils.isNotBlank(condition.getPayeeName())) sql.append(" and brr.payee_name like :payeeName ");
    if (StringUtils.isNotBlank(condition.getReceiptNo())) sql.append(" and bro.receipt_no like :receiptNo ");
    if(condition.getBcgogoReceivableOrderId()!=null) sql.append("and bro.id =:bcgogoReceivableOrderId ");
    if(StringUtils.isNotBlank(condition.getGroupField())){
      sql.append(" group by ").append(condition.getGroupField());
    }

//    sql.append("order by brorr.payment_time desc");
    SQLQuery query = (SQLQuery) session.createSQLQuery(sql.toString());
    if (CollectionUtil.isNotEmpty(condition.getShopIds())){
      query.setParameterList("shopIds",condition.getShopIds());
    }
    if (!ArrayUtils.isEmpty(condition.getPaymentTypes())) {
      query.setParameterList("paymentType", condition.getPaymentTypes());
    }
    if (!ArrayUtils.isEmpty(condition.getReceivableStatuses())) {
      query.setParameterList("status", condition.getReceivableStatuses());
    }
    if(!ArrayUtils.isEmpty(condition.getPaymentMethods())){
      query.setParameterList("paymentMethod", condition.getPaymentMethods());
    }
    if(StringUtils.isNotBlank(condition.getPayeeName())){
      query.setString("payeeName", "%" + condition.getPayeeName() + "%");
    }
    if(StringUtils.isNotBlank(condition.getReceiptNo())){
      query.setString("receiptNo", "%" + condition.getReceiptNo() + "%");
    }
    if(condition.getBcgogoReceivableOrderId()!=null){
      query.setLong("bcgogoReceivableOrderId", condition.getBcgogoReceivableOrderId());
    }
    return query;
  }

  public static Query getBcgogoReceivableOrderItemByOrderId(Session session,Long... orderId) {
    Query query = session.createQuery(" from BcgogoReceivableOrderItem where orderId in(:orderId)");
    query.setParameterList("orderId",orderId);
    return query;
  }

  public static Query getBcgogoReceivableDTOByRelationId(Session session,Long... relationId) {
    StringBuilder sql = new StringBuilder();
    sql.append("select brorr.shop_id as shopId,brorr.id as bcgogoReceivableOrderRecordRelationId,brorr.receivable_method as relationReceivableMethod," +
        "bro.received_amount as orderReceivedAmount,bro.receivable_amount as orderReceivableAmount," +
        "bro.total_amount as orderTotalAmount,bro.payment_type as orderPaymentType ");
    sql.append(" from bcgogo_receivable_order_record_relation brorr ");
    sql.append(" left join bcgogo_receivable_order bro on bro.id=brorr.bcgogo_receivable_order_id ");
    sql.append(" where brorr.id in (:relationId)");
    SQLQuery query = (SQLQuery) session.createSQLQuery(sql.toString());
    query.setParameterList("relationId",relationId);
    query.addScalar("bcgogoReceivableOrderRecordRelationId", StandardBasicTypes.LONG)
        .addScalar("relationReceivableMethod", StandardBasicTypes.STRING)
        .addScalar("orderPaymentType", StandardBasicTypes.STRING)

        .addScalar("orderReceivedAmount", StandardBasicTypes.DOUBLE)
        .addScalar("orderReceivableAmount", StandardBasicTypes.DOUBLE)
        .addScalar("orderTotalAmount", StandardBasicTypes.DOUBLE)
        .addScalar("shopId", StandardBasicTypes.LONG);

    query.setParameterList("relationId",relationId);
    return query;
  }

  public static Query getBcgogoReceivableOrderByRelationId(Session session,Long... relationId) {
    Query query = session.createQuery("select bor from BcgogoReceivableOrder bor,BcgogoReceivableOrderRecordRelation brorr where brorr.id in(:relationId) and brorr.bcgogoReceivableOrderId = bor.id");
    query.setParameterList("relationId",relationId);
    return query;
  }

  public static Query getBcgogoReceivableOrderRecordAndRelationByOrderId(Session session,Long... orderId) {
    Query query = session.createQuery(" select brorr,brr from BcgogoReceivableOrderRecordRelation brorr,BcgogoReceivableRecord brr where brorr.bcgogoReceivableOrderId in(:orderId) and brorr.bcgogoReceivableRecordId = brr.id order by brr.submitTime desc ");
    query.setParameterList("orderId",orderId);
    return query;
  }

  public static Query getBcgogoReceivableOrderToBePaidRecordByOrderId(Session session,Long orderId) {
    Query query = session.createQuery(" select brr from BcgogoReceivableOrderRecordRelation brorr,BcgogoReceivableRecord brr where brorr.bcgogoReceivableOrderId =:orderId and brorr.bcgogoReceivableRecordId = brr.id and brr.status=:bcgogoReceivableStatus");
    query.setLong("orderId", orderId);
    query.setParameter("bcgogoReceivableStatus", BcgogoReceivableStatus.TO_BE_PAID);
    return query;
  }
  public static Query getBcgogoReceivableOrderToBePaidRecordRelationByOrderId(Session session,Long... orderId) {
    Query query = session.createQuery(" select brorr from BcgogoReceivableOrderRecordRelation brorr,BcgogoReceivableRecord brr where brorr.bcgogoReceivableOrderId in(:orderId) and brorr.bcgogoReceivableRecordId = brr.id and brr.status=:bcgogoReceivableStatus");
    query.setParameterList("orderId",orderId);
    query.setParameter("bcgogoReceivableStatus", BcgogoReceivableStatus.TO_BE_PAID);
    return query;
  }
  public static Query searchBcgogoReceivableOrderResult(Session session, BcgogoReceivableSearchCondition condition) {
    StringBuilder sql = new StringBuilder();
    sql.append("select bro ");
    Query query = splittingBcgogoReceivableOrderResultSql(session, condition, sql);
    query.setMaxResults(condition.getLimit()).setFirstResult(condition.getStart());
    return query;
  }
  public static Query searchBcgogoReceivableOrder(Session session, BcgogoReceivableSearchCondition condition) {
    StringBuilder sql = new StringBuilder();
    sql.append("select bro ");
    Query query = splittingBcgogoReceivableOrderResultSql(session, condition, sql);
    query.setMaxResults(condition.getMaxRows()).setFirstResult((condition.getStartPageNo() - 1) * condition.getMaxRows());
    return query;
  }

  public static Query statBcgogoReceivableOrderByStatus(Session session, BcgogoReceivableSearchCondition condition) {
    StringBuilder sql = new StringBuilder();
    sql.append("select bro.status,count(bro.id),sum(bro.totalAmount) ");
    condition.setGroupField("bro.status");
    return splittingBcgogoReceivableOrderResultSql(session, condition, sql);
  }

  public static Query countBcgogoReceivableOrder(Session session, BcgogoReceivableSearchCondition condition) {
    StringBuilder sql = new StringBuilder();
    sql.append("select count(*) ");
    return splittingBcgogoReceivableOrderResultSql(session, condition, sql);
  }

  private static Query splittingBcgogoReceivableOrderResultSql(Session session, BcgogoReceivableSearchCondition condition, StringBuilder sql) {
    sql.append(" from BcgogoReceivableOrder bro where 1=1 ");
    if(!ArrayUtils.isEmpty(condition.getBcgogoProductIds())){
      sql.append("and bro.id in (select distinct broi.orderId from BcgogoReceivableOrderItem broi where broi.productId in (:bcgogoProductIds)) ");
    }

    if(!ArrayUtils.isEmpty(condition.getPaymentStatuses())){
      sql.append("and bro.status in (:status) ");
    }
    if (!ArrayUtils.isEmpty(condition.getPaymentTypes())) {
      sql.append("and bro.paymentType in(:paymentType) ");
    }
    if (!ArrayUtils.isEmpty(condition.getBuyChannels())) {
      sql.append("and bro.buyChannels in(:buyChannels) ");
    }
    if (condition.getStartTime() != null) {
        sql.append("and ").append(" bro.createdTime >= ").append(condition.getStartTime());
    }
    if (condition.getEndTime() != null) {
        sql.append("and ").append(" bro.createdTime < ").append(condition.getEndTime());
    }

    if (CollectionUtil.isNotEmpty(condition.getShopIds())) sql.append(" and bro.shopId in(:shopIds) ");
    if (StringUtils.isNotBlank(condition.getReceiptNo())) sql.append(" and bro.receiptNo like :receiptNo ");

    if(StringUtils.isNotBlank(condition.getGroupField())){
      sql.append(" group by ").append(condition.getGroupField());
    }
    sql.append(" order by bro.createdTime desc");
    Query query = session.createQuery(sql.toString());

    if(!ArrayUtils.isEmpty(condition.getBcgogoProductIds())){
      query.setParameterList("bcgogoProductIds",condition.getBcgogoProductIds());
    }
    if (CollectionUtil.isNotEmpty(condition.getShopIds())){
      query.setParameterList("shopIds",condition.getShopIds());
    }
    if (!ArrayUtils.isEmpty(condition.getPaymentTypes())) {
      query.setParameterList("paymentType", condition.getPaymentTypesEnum());
    }
    if (!ArrayUtils.isEmpty(condition.getBuyChannels())) {
      query.setParameterList("buyChannels", condition.getBuyChannelsEnum());
    }
    if (!ArrayUtils.isEmpty(condition.getPaymentStatuses())) {
      query.setParameterList("status", condition.getPaymentStatusesEnum());
    }
    if(StringUtils.isNotBlank(condition.getReceiptNo())){
      query.setString("receiptNo", "%" + condition.getReceiptNo() + "%");
    }
    return query;
  }

  public static Query getSoftwareReceivable(Session session, long shopId) {
    return session.createQuery("select r from BcgogoReceivableOrderRecordRelation r where r.shopId = :shopId and r.paymentType=:paymentType")
        .setLong("shopId",shopId).setParameter("paymentType", PaymentType.SOFTWARE);
  }

  public static Query getSoftwareReceivableByReceivableMethod(Session session, long shopId, ReceivableMethod receivableMethod) {
    return session.createQuery("select r from BcgogoReceivableOrderRecordRelation r where r.shopId = :shopId and r.paymentType=:paymentType and r.receivableMethod=:receivableMethod")
        .setLong("shopId", shopId).setParameter("paymentType", PaymentType.SOFTWARE).setParameter("receivableMethod", ReceivableMethod.FULL);
  }

  public static Query getInstalmentPlanAlgorithms(Session session) {
    return session.createQuery("from InstalmentPlanAlgorithm");
  }

  public static Query getInstalmentPlanByIds(Session session, Long... instalmentPlanIds) {
    return session.createQuery("select i from InstalmentPlan i where i.id in (:instalmentPlanIds)")
        .setParameterList("instalmentPlanIds", instalmentPlanIds);
  }
  public static Query getInstalmentPlanItemsByIds(Session session, Long... instalmentPlanItemIds) {
    return session.createQuery("select i from InstalmentPlanItem i where i.id in (:instalmentPlanItemIds)")
        .setParameterList("instalmentPlanItemIds", instalmentPlanItemIds);
  }
  public static Query getInstalmentPlanItemsByInstalmentPlanIds(Session session, Long... instalmentPlanIds) {
    return session.createQuery("select i from InstalmentPlanItem i where i.instalmentPlanId in (:instalmentPlanIds) order by periodNumber asc ")
        .setParameterList("instalmentPlanIds", instalmentPlanIds);
  }
  public static Query getHardwareSoftwareAccountByShopId(Session session, long shopId) {
    return session.createQuery("select h from HardwareSoftwareAccount h where h.shopId = :shopId")
        .setLong("shopId", shopId);
  }

  public static Query countHardwareSoftwareAccountResult(Session session, AccountSearchCondition condition) {
    StringBuilder hql = new StringBuilder();
    hql.append("select count(h) ");
    return splittingHardwareSoftwareAccountResultSql(session, condition, hql);
  }

  public static Query searchHardwareSoftwareAccountResult(Session session, AccountSearchCondition condition) {
    StringBuilder hql = new StringBuilder();
    hql.append("select h ");
    Query query = splittingHardwareSoftwareAccountResultSql(session, condition, hql);
    return query.setMaxResults(condition.getLimit()).setFirstResult(condition.getStart());
  }

  private static Query splittingHardwareSoftwareAccountResultSql(Session session, AccountSearchCondition condition, StringBuilder hql){
    hql.append("  from HardwareSoftwareAccount h ");
    boolean start = true;
    if (CollectionUtil.isNotEmpty(condition.getShopIds())) {
      hql.append(" where h.shopId in (:shopIds)");
      start = false;
    }
    if (condition.getHavePayable() != null) {
      if (start) {
        hql.append(" where ");
        start = false;
      }
      if (condition.getHavePayable()) hql.append(" h.totalReceivableAmount > 0");
      if (!condition.getHavePayable()) hql.append(" h.totalReceivableAmount = 0");
    }
    Query query = session.createQuery(hql.toString());
    if (CollectionUtil.isNotEmpty(condition.getShopIds())) {
      query.setParameterList("shopIds", condition.getShopIds());
    }
    return query;
  }

  public static Query getBcgogoReceivableOrderByShopIds(Session session, Long... shopIds) {
    return session.createQuery("select o from BcgogoReceivableOrder o where o.shopId in (:shopIds)")
        .setParameterList("shopIds", shopIds);
  }

  public static Query getHardwareSoftwareAccountRecordByShopIds(Session session, Long... shopIds) {
    SQLQuery query = (SQLQuery) session.createSQLQuery("select brr.id as bcgogoReceivableRecordId,brr.payment_amount as paymentAmount,brr.paid_amount as paidAmount,brr.operator_id as operatorId," +
        " brr.operator_time as operatorTime,brr.payee_id as payeeId,brr.payment_time as paymentTime,brr.submitter_id as submitterId,brr.submit_time as submitTime,brr.auditor_id as auditorId," +
        " brr.audit_time as auditTime,brr.status as status,brr.shop_id as shopId," +
        " brorr.bcgogo_receivable_order_id as bcgogoReceivableOrderId,brorr.receivable_method as receivableMethod,brorr.payment_method as paymentMethod,brorr.payment_type as paymentType" +
        " from bcgogo_receivable_record brr left join bcgogo_receivable_order_record_relation brorr on brr.id=brorr.bcgogo_receivable_record_id  where brr.shop_id in (:shopIds)").setParameterList("shopIds", shopIds);
    query.addScalar("bcgogoReceivableRecordId",StandardBasicTypes.LONG)
        .addScalar("paymentAmount",StandardBasicTypes.DOUBLE)
        .addScalar("paidAmount",StandardBasicTypes.DOUBLE)
        .addScalar("operatorId",StandardBasicTypes.LONG)
        .addScalar("operatorTime",StandardBasicTypes.LONG)
        .addScalar("payeeId",StandardBasicTypes.LONG)
        .addScalar("paymentTime",StandardBasicTypes.LONG)
        .addScalar("submitterId",StandardBasicTypes.LONG)
        .addScalar("submitTime",StandardBasicTypes.LONG)
        .addScalar("auditorId",StandardBasicTypes.LONG)
        .addScalar("auditTime",StandardBasicTypes.LONG)
        .addScalar("receivableMethod",StandardBasicTypes.STRING)
        .addScalar("paymentMethod",StandardBasicTypes.STRING)
        .addScalar("paymentType",StandardBasicTypes.STRING)
        .addScalar("status",StandardBasicTypes.STRING)
        .addScalar("shopId",StandardBasicTypes.LONG)
        .addScalar("bcgogoReceivableOrderId",StandardBasicTypes.LONG);
    return query;
  }

  public static Query countHardwareSoftwareAccount(Session session,AccountSearchCondition condition) {
    String hql = "select sum(totalAmount),sum(totalReceivableAmount),sum(totalReceivedAmount) from HardwareSoftwareAccount";
    if (CollectionUtil.isNotEmpty(condition.getShopIds())) {
      hql += " where shopId in (:shopIds)";
    }
    Query q = session.createQuery(hql);
    if (CollectionUtil.isNotEmpty(condition.getShopIds())) {
      q.setParameterList("shopIds", condition.getShopIds());
    }
    return q;
  }
  public static Query countSoftwareAccount(Session session,AccountSearchCondition condition) {
    String hql = "select sum(softwareTotalAmount),sum(softwareReceivableAmount),sum(softwareReceivedAmount) from HardwareSoftwareAccount ";
    if (CollectionUtil.isNotEmpty(condition.getShopIds())) {
      hql += " where shopId in (:shopIds)";
    }
    Query q = session.createQuery(hql);
    if (CollectionUtil.isNotEmpty(condition.getShopIds())) {
      q.setParameterList("shopIds", condition.getShopIds());
    }
    return q;
  }
  public static Query countHardwareAccount(Session session,AccountSearchCondition condition) {
    String hql = "select sum(hardwareTotalAmount),sum(hardwareReceivableAmount),sum(hardwareReceivedAmount)from HardwareSoftwareAccount ";
    if (CollectionUtil.isNotEmpty(condition.getShopIds())) {
      hql += " where shopId in (:shopIds)";
    }
    Query q = session.createQuery(hql);
    if (CollectionUtil.isNotEmpty(condition.getShopIds())) {
      q.setParameterList("shopIds", condition.getShopIds());
    }
    return q;
  }

  public static Query countHardwareSoftwarePaidAmountAccount(Session session, PaymentMethod paymentMethod, PaymentType paymentType,AccountSearchCondition condition) {
    StringBuilder hql = new StringBuilder();
    hql.append("select sum(brr.paidAmount) from HardwareSoftwareAccount h ,BcgogoReceivableOrderRecordRelation brorr ,BcgogoReceivableRecord brr " +
        " where brr.id=brorr.bcgogoReceivableRecordId and h.shopId=brr.shopId " +
        " and brorr.paymentMethod =:paymentMethod and brr.status!=:status");
    if (CollectionUtil.isNotEmpty(condition.getShopIds())) {
      hql.append(" and h.shopId in (:shopIds)");
    }
    if (paymentType != null) {
      hql.append(" and brorr.paymentType =:paymentType");
    }
    if (condition.getHavePayable() != null) {
      if (condition.getHavePayable()) hql.append(" and h.totalReceivableAmount > 0");
      if (!condition.getHavePayable()) hql.append(" and h.totalReceivableAmount = 0");
    }
    Query q = session.createQuery(hql.toString()).setParameter("paymentMethod", paymentMethod).setParameter("status", BcgogoReceivableStatus.TO_BE_PAID);
    if (paymentType != null) q.setParameter("paymentType", paymentType);
    if (CollectionUtil.isNotEmpty(condition.getShopIds())) q.setParameterList("shopIds", condition.getShopIds());
    return q;
  }

  public static Query countShopSmsAccountResult(Session session, SmsRecordSearchCondition condition) {
    StringBuilder hql = new StringBuilder();
    hql.append("select count(*) ");
    return splittingShopSmsAccountResultSql(session, condition, hql);
  }

  public static Query searchShopSmsAccountResult(Session session, SmsRecordSearchCondition condition) {
    StringBuilder hql = new StringBuilder();
    Query query = splittingShopSmsAccountResultSql(session, condition, hql);
    return query.setMaxResults(condition.getLimit()).setFirstResult(condition.getStart());
  }

  private static Query splittingShopSmsAccountResultSql(Session session, SmsRecordSearchCondition condition, StringBuilder hql) {
    hql.append(" from ShopSmsAccount s ");
    boolean start = true;
    if (CollectionUtil.isNotEmpty(condition.getShopIds())) {
      hql.append(" where s.shopId in (:shopIds)");
      start = false;
    }
    Query query = session.createQuery(hql.toString());
    if (CollectionUtil.isNotEmpty(condition.getShopIds())) {
      query.setParameterList("shopIds", condition.getShopIds());
    }
    return query;
  }

  public static Query countShopSmsRecordResult(Session session, SmsRecordSearchCondition condition) {
    StringBuilder hql = new StringBuilder();
    hql.append("select count(*) ");
    return splittingShopSmsRecordResultSql(session, condition, hql);
  }

  public static Query searchShopSmsRecordResult(Session session, SmsRecordSearchCondition condition) {
    StringBuilder hql = new StringBuilder();
    Query query = splittingShopSmsRecordResultSql(session, condition, hql);
    return query.setMaxResults(condition.getLimit()).setFirstResult(condition.getStart());
  }

  public static Query shopSmsRecordStatistics(Session session, SmsRecordSearchCondition condition) {
    StringBuilder hql = new StringBuilder();
    hql.append("select sum(balance),sum(number) ");
    return splittingShopSmsRecordResultSql(session, condition, hql);
  }

  private static Query splittingShopSmsRecordResultSql(Session session, SmsRecordSearchCondition condition, StringBuilder hql) {
    hql.append(" from ShopSmsRecord ");
    boolean start = true;
    if (CollectionUtil.isNotEmpty(condition.getShopIds())) {
      hql.append(" where shopId in (:shopIds)");
      start = false;
    }
    if (condition.getStartTime() != null) {
      if (start) {
        hql.append(" where ");
        start = false;
      } else {
        hql.append(" and ");
      }
      hql.append(" operateTime>=:startTime ");
    }
    if (condition.getEndTime() != null) {
      if (start) {
        hql.append(" where ");
        start = false;
      } else {
        hql.append(" and ");
      }
      hql.append(" operateTime<=:endTime");
    }
    if (!ArrayUtils.isEmpty(condition.getSmsCategories())) {
      if (start) {
        hql.append(" where ");
        start = false;
      } else {
        hql.append(" and ");
      }
      hql.append(" smsCategory in (:smsCategory) ");
    }
    if(StatType.DAY.equals(condition.getStatType())) {
      if(start) {
        hql.append(" where ");
        start = false;
      } else {
        hql.append(" and ");
      }
      hql.append(" (statType = 'DAY' or statType is null)");
    } else if(StatType.ONE_TIME.equals(condition.getStatType())) {
      if(start) {
        hql.append(" where ");
        start = false;
      } else {
        hql.append(" and ");
      }
      hql.append(" (statType = 'ONE_TIME' or statType is null)");
    }


    hql.append(" order by creationDate desc");
    Query query = session.createQuery(hql.toString());
    if (CollectionUtil.isNotEmpty(condition.getShopIds())) {
      query.setParameterList("shopIds", condition.getShopIds());
    }
    if (condition.getStartTime() != null) {
      query.setLong("startTime", condition.getStartTime());
    }
    if (condition.getEndTime() != null) {
      query.setLong("endTime", condition.getEndTime());
    }
    if (!ArrayUtils.isEmpty(condition.getSmsCategories())) {
      query.setParameterList("smsCategory", condition.getSmsCategories());
    }
    return query;
  }


  public static Query countBcgogoSmsRecordResult(Session session, SmsRecordSearchCondition condition) {
    StringBuilder hql = new StringBuilder();
    hql.append("select count(*) ");
    return splittingBcgogoSmsRecordResultSql(session, condition, hql);
  }

  public static Query searchBcgogoSmsRecordResult(Session session, SmsRecordSearchCondition condition) {
    StringBuilder hql = new StringBuilder();
    Query query = splittingBcgogoSmsRecordResultSql(session, condition, hql);
    return query.setMaxResults(condition.getLimit()).setFirstResult(condition.getStart());
  }

  private static Query splittingBcgogoSmsRecordResultSql(Session session, SmsRecordSearchCondition condition, StringBuilder hql) {
    hql.append(" from BcgogoSmsRecord ");
    boolean start = true;
    if (condition.getStartTime() != null) {
      hql.append(" where ((operateTime>=:startTime and smsCategory not in ('BCGOGO_RECHARGE','SHOP_RECHARGE')) or ( rechargeTime >=:startTime and smsCategory in ('BCGOGO_RECHARGE','SHOP_RECHARGE') ))");
      start = false;
    }
    if (condition.getEndTime() != null) {
      if (start) {
        hql.append(" where ");
        start = false;
      } else {
        hql.append(" and ");
      }
//      hql.append(" operateTime<=:endTime");
      hql.append(" ((operateTime<=:endTime and smsCategory not in ('BCGOGO_RECHARGE','SHOP_RECHARGE')) or ( rechargeTime<=:endTime and smsCategory in ('BCGOGO_RECHARGE','SHOP_RECHARGE') ))");
    }
    if (!ArrayUtils.isEmpty(condition.getSmsCategories())) {
      if (start) {
        hql.append(" where ");
        start = false;
      } else {
        hql.append(" and ");
      }
      hql.append(" smsCategory in (:smsCategory) ");
    }
    hql.append(" order by creationDate desc");
    Query query = session.createQuery(hql.toString());
    if (condition.getStartTime() != null) {
      query.setLong("startTime", condition.getStartTime());
    }
    if (condition.getEndTime() != null) {
      query.setLong("endTime", condition.getEndTime());
    }
    if (!ArrayUtils.isEmpty(condition.getSmsCategories())) {
      query.setParameterList("smsCategory", condition.getSmsCategories());
    }
    return query;
  }

  public static Query countSmsAccountNumberBySmsCategory(Session session,SmsCategory... categories) {
    return session.createQuery("select sum(number) from BcgogoSmsRecord where smsCategory in (:categories)").setParameterList("categories",categories);
  }

  public static Query countSmsAccountBalanceBySmsCategory(Session session, SmsCategory... categories) {
    return session.createQuery("select sum(balance) from BcgogoSmsRecord where smsCategory in (:categories)").setParameterList("categories", categories);
  }

  public static Query countSmsTotalRecharge(Session session,SmsCategory smsCategory) {
    return session.createQuery("select sum(balance),sum(number) from BcgogoSmsRecord where smsCategory =:smsCategory").setParameter("smsCategory", smsCategory);
  }

  public static Query countShopSmsTotalRecharge(Session session) {
    return session.createQuery("select sum(currentBalance),sum(currentNumber) from ShopSmsAccount ");
  }

  public static Query getShopSmsAccountByShopId(Session session, long shopId) {
    return session.createQuery("from ShopSmsAccount where shopId =:shopId").setLong("shopId", shopId);
  }

  public static Query getShopSmsRecordByShopId(Session session, Long shopId, Long operateTime) {
    return session.createQuery("from ShopSmsRecord where shopId =:shopId and operateTime=:operateTime").setLong("shopId", shopId).setLong("operateTime",operateTime);
  }

  public static Query getCurrentDayBcgogoSmsRecord(Session session, Long startTime, Long endTime, SmsCategory smsCategory) {
    return session.createQuery("from BcgogoSmsRecord where operateTime >=:startTime and operateTime <:endTime and smsCategory=:smsCategory")
        .setLong("endTime", endTime).setLong("startTime", startTime).setParameter("smsCategory", smsCategory);
  }

  public static Query getCurrentDayShopSmsRecord(Session session,long shopId, Long startTime, Long endTime, SmsCategory smsCategory,StatType statType) {
    return session.createQuery("from ShopSmsRecord where operateTime >=:startTime and operateTime <:endTime and smsCategory=:smsCategory and shopId =:shopId and statType=:statType ")
        .setLong("endTime", endTime).setLong("startTime", startTime).setParameter("smsCategory", smsCategory).setLong("shopId", shopId).setParameter("statType",statType);
  }

  public static Query shopSmsAccountStatistics(Session session, SmsRecordSearchCondition condition) {
    String hql = "select sum(rechargeBalance),sum(consumptionNumber),sum(currentNumber) from ShopSmsAccount ";
    if (CollectionUtil.isNotEmpty(condition.getShopIds())) {
      hql += " where shopId in (:shopIds)";
    }
    Query q = session.createQuery(hql);
    if (CollectionUtil.isNotEmpty(condition.getShopIds())) {
      q.setParameterList("shopIds", condition.getShopIds());
    }
    return q;
  }

  public static Query getPurchaseInventoryOrderIdByOnline(Session session){
    String sql ="select pi.shop_id,pi.id,po.supplier_shop_id from purchase_inventory pi,purchase_order po where pi.purchase_order_id = po.id and po.supplier_shop_id is not null";
    Query query = session.createSQLQuery(sql.toString());
    return query;
  }

  public static Query getSupplierInventoryList(Session session, Long shopId, Long supplierId, Long storehouseId, Set<Long> productIds) {
    StringBuffer sb = new StringBuffer();
    sb.append("from SupplierInventory where shopId =:shopId and productId in(:productIds) and disabled =:disabled ");
    if(storehouseId == null){
      sb.append(" and storehouseId is null" );
    }else {
      sb.append(" and storehouseId =:storehouseId" );
    }

    if(supplierId != null){
      sb.append(" and supplierId=:supplierId " );
    }

    Query q = session.createQuery(sb.toString());
    q.setLong("shopId",shopId);

    if(supplierId != null){
      q.setLong("supplierId",supplierId);
    }
    q.setParameterList("productIds",productIds);
    if(storehouseId != null){
      q.setLong("storehouseId",storehouseId);
    }
    q.setParameter("disabled",YesNo.NO);
    return q;
  }

  public static Query getHasRemainSupplierInventory(Session session, Long shopId, Long supplierId, Long storehouseId, Set<Long> productIds) {
    StringBuffer sb = new StringBuffer();
    sb.append("from SupplierInventory where shopId =:shopId and productId in(:productIds) and remainAmount > 0 and disabled =:disabled ");
    if(storehouseId == null){
      sb.append(" and storehouseId is null" );
    }else {
      sb.append(" and storehouseId =:storehouseId" );
    }

    if(supplierId != null){
      sb.append(" and supplierId=:supplierId " );
    }

    Query q = session.createQuery(sb.toString());
    q.setLong("shopId",shopId);

    if(supplierId != null){
      q.setLong("supplierId",supplierId);
    }
    q.setParameterList("productIds",productIds);
    if(storehouseId != null){
      q.setLong("storehouseId",storehouseId);
    }
    q.setParameter("disabled",YesNo.NO);
    return q;
  }




  public static Query getSupplierInventoriesByProductIds(Session session, Long shopId, Set<Long> productIds) {
    StringBuffer sb = new StringBuffer();
    sb.append("from SupplierInventory where shopId =:shopId and productId in(:productIds) and disabled =:disabled ");
    Query q = session.createQuery(sb.toString());
    q.setLong("shopId", shopId);
    q.setParameterList("productIds", productIds);
    q.setParameter("disabled",YesNo.NO);
    return q;
  }

  public static Query getNextProductId(Session session, Long shopId, long startProductId, int defaultPageSize) {
    StringBuffer sb = new StringBuffer();
    sb.append("select max(p2.productId) as productId from (select distinct i.product_id as productId from purchase_inventory_item i ");
    sb.append(" left join purchase_inventory p on i.purchase_inventory_id = p.id ");
    sb.append(" where p.status_enum = 'PURCHASE_INVENTORY_DONE' ");
    sb.append("and p.shop_id =:shopId ");
    sb.append("and i.product_id >:startProductId order by i.product_id asc limit :rows) as p2");
    Query q = session.createSQLQuery(sb.toString())
        .addScalar("productId", StandardBasicTypes.LONG)
        .setLong("startProductId", startProductId)
        .setInteger("rows", defaultPageSize);
      q.setLong("shopId", shopId);
    return q;
  }

  public static Query getInitHaveStoreHouseSupplierInventory(Session session, Long shopId, Long startProductId, Long endProductId) {
    StringBuffer sb = new StringBuffer();
    sb.append("select i.product_id as productId,p.supplier_id as supplierId,p.shop_id as shopId ,max(p.vest_date) as vestDate,");
    sb.append("s.name as name,s.contact as contact,s.mobile as mobile,p.storehouse_id as storehouseId,pro.sell_unit as unit,");
    sb.append("sum(if(i.unit = pro.storage_unit && pro.rate is not null,i.amount * pro.rate,i.amount)) as totalAmount, ");
    sb.append("max(if(i.unit = pro.storage_unit && pro.rate is not null,i.price / pro.rate,i.price)) as maxPurchasePrice, ");
    sb.append("min(if(i.unit = pro.storage_unit && pro.rate is not null,i.price / pro.rate,i.price)) as minPurchasePrice ");
    sb.append("from purchase_inventory_item i left join purchase_inventory p on i.purchase_inventory_id = p.id ");
    sb.append("left join bcuser.supplier s on s.id = p.supplier_id ");
    sb.append("left join product.product_local_info pro on pro.id = i.product_id ");
    sb.append("where p.status_enum = 'PURCHASE_INVENTORY_DONE' ");
    sb.append("and p.shop_id =:shopId ");
    sb.append("and p.storehouse_id is not null ");
    if (startProductId != null) {
      sb.append("and i.product_Id >=:startProductId ");
    }
    if (endProductId != null) {
      sb.append("and i.product_Id <:endProductId ");
    }
    sb.append("group by i.product_id,p.supplier_id,p.shop_id,p.storehouse_id ");
    sb.append("order by i.product_id asc,p.vest_date asc");

    Query q = session.createSQLQuery(sb.toString())
        .addScalar("productId", StandardBasicTypes.LONG)
        .addScalar("supplierId", StandardBasicTypes.LONG)
        .addScalar("shopId", StandardBasicTypes.LONG)
        .addScalar("vestDate", StandardBasicTypes.LONG)
        .addScalar("name", StandardBasicTypes.STRING)
        .addScalar("contact", StandardBasicTypes.STRING)
        .addScalar("mobile", StandardBasicTypes.STRING)
        .addScalar("storehouseId", StandardBasicTypes.LONG)
        .addScalar("unit", StandardBasicTypes.STRING)
        .addScalar("totalAmount", StandardBasicTypes.DOUBLE)
        .addScalar("maxPurchasePrice", StandardBasicTypes.DOUBLE)
        .addScalar("minPurchasePrice", StandardBasicTypes.DOUBLE);
    if (shopId != null) {
      q.setLong("shopId", shopId);
    }
    if (startProductId != null) {
      q.setLong("startProductId", startProductId);
    }
    if (endProductId != null) {
      q.setLong("endProductId", endProductId);
    }
    return q;
  }

  public static Query getInitNoStoreHouseSupplierInventory(Session session, Long shopId, Long startProductId, Long endProductId) {
    StringBuffer sb = new StringBuffer();
    sb.append("select i.product_id as productId,p.supplier_id as supplierId,p.shop_id as shopId ,max(p.vest_date) as vestDate,");
    sb.append("s.name as name,s.contact as contact,s.mobile as mobile,pro.sell_unit as unit,");
    sb.append("sum(if(i.unit = pro.storage_unit && pro.rate is not null,i.amount * pro.rate,i.amount)) as totalAmount, ");
    sb.append("max(if(i.unit = pro.storage_unit && pro.rate is not null,i.price / pro.rate,i.price)) as maxPurchasePrice, ");
    sb.append("min(if(i.unit = pro.storage_unit && pro.rate is not null,i.price / pro.rate,i.price)) as minPurchasePrice ");
    sb.append("from purchase_inventory_item i left join purchase_inventory p on i.purchase_inventory_id = p.id ");
    sb.append("left join bcuser.supplier s on s.id = p.supplier_id ");
    sb.append("left join product.product_local_info pro on pro.id = i.product_id ");
    sb.append("where p.status_enum = 'PURCHASE_INVENTORY_DONE' ");
    sb.append("and p.shop_id =:shopId ");
    sb.append("and p.storehouse_id is null ");
    if (startProductId != null) {
      sb.append("and i.product_Id >=:startProductId ");
    }
    if (endProductId != null) {
      sb.append("and i.product_Id <:endProductId ");
    }
    sb.append("group by i.product_id,p.supplier_id,p.shop_id,p.storehouse_id ");
    sb.append("order by i.product_id asc,p.vest_date asc");

    Query q = session.createSQLQuery(sb.toString())
        .addScalar("productId", StandardBasicTypes.LONG)
        .addScalar("supplierId", StandardBasicTypes.LONG)
        .addScalar("shopId", StandardBasicTypes.LONG)
        .addScalar("vestDate", StandardBasicTypes.LONG)
        .addScalar("name", StandardBasicTypes.STRING)
        .addScalar("contact", StandardBasicTypes.STRING)
        .addScalar("mobile", StandardBasicTypes.STRING)
        .addScalar("unit", StandardBasicTypes.STRING)
        .addScalar("totalAmount", StandardBasicTypes.DOUBLE)
        .addScalar("maxPurchasePrice", StandardBasicTypes.DOUBLE)
        .addScalar("minPurchasePrice", StandardBasicTypes.DOUBLE);
    q.setLong("shopId", shopId);
    if (startProductId != null) {
      q.setLong("startProductId", startProductId);
    }
    if (endProductId != null) {
      q.setLong("endProductId", endProductId);
    }
    return q;
  }

  public static Query countProductInventory(Session session, Long shopId) {
    StringBuffer sb = new StringBuffer();
    sb.append("select count(i.id) from Inventory i where i.shopId =:shopId");
    Query q = session.createQuery(sb.toString());
    q.setLong("shopId", shopId);
    return q;
  }

  public static Query getInitNoStoreHouseSupplierInventoryAmount(Session session, Long shopId, Pager pager) {
    StringBuffer sb = new StringBuffer();
    sb.append("select i.id as productId,i.shop_id as shopId,i.unit as unit,i.amount as amount,");
    sb.append("i.latest_inventory_price as lastStoragePrice,");
    sb.append("i.inventory_average_price as averagePrice,");
    sb.append("i.last_storage_time as lastStorageTime ");
    sb.append("from inventory i ");
    sb.append("left join product.product_local_info pli on pli.id = i.product_id ");
    sb.append("left join product.product pro on pro.id = pli.product_id ");
    sb.append("where pro.status is null ");
    sb.append("and i.shop_id =:shopId ");
    sb.append("order by i.id asc");

    Query q = session.createSQLQuery(sb.toString())
        .addScalar("productId", StandardBasicTypes.LONG)
        .addScalar("shopId", StandardBasicTypes.LONG)
        .addScalar("unit", StandardBasicTypes.STRING)
        .addScalar("amount", StandardBasicTypes.DOUBLE)
        .addScalar("lastStoragePrice", StandardBasicTypes.DOUBLE)
        .addScalar("averagePrice", StandardBasicTypes.DOUBLE)
        .addScalar("lastStorageTime", StandardBasicTypes.LONG);
    q.setLong("shopId", shopId);
    q.setFirstResult(pager.getRowStart());
    q.setMaxResults(pager.getPageSize());
    return q;
  }

  public static Query getInitHaveStoreHouseSupplierInventoryAmount(Session session, Long shopId, Set<Long> productIds) {
    StringBuffer sb = new StringBuffer();
       sb.append("select i.id as productId,i.shop_id as shopId,i.unit as unit,si.amount as amount,");
       sb.append("i.latest_inventory_price as lastStoragePrice,");
       sb.append("i.inventory_average_price as averagePrice,");
       sb.append("i.last_storage_time as lastStorageTime,");
       sb.append("si.storehouse_id as storehouseId ");
       sb.append("from storehouse_inventory si ");
       sb.append("left join inventory i on si.product_local_info_id = i.id ");
       sb.append("left join product.product_local_info pli on pli.id = i.product_id ");
       sb.append("left join product.product pro on pro.id = pli.product_id ");
       sb.append("where pro.status is null ");
       sb.append("and i.shop_id =:shopId ");
       sb.append("and i.id in(:productIds) ");

       Query q = session.createSQLQuery(sb.toString())
           .addScalar("productId", StandardBasicTypes.LONG)
           .addScalar("shopId", StandardBasicTypes.LONG)
           .addScalar("unit", StandardBasicTypes.STRING)
           .addScalar("amount", StandardBasicTypes.DOUBLE)
           .addScalar("lastStoragePrice", StandardBasicTypes.DOUBLE)
           .addScalar("averagePrice", StandardBasicTypes.DOUBLE)
           .addScalar("lastStorageTime", StandardBasicTypes.LONG)
           .addScalar("storehouseId", StandardBasicTypes.LONG);
       q.setLong("shopId", shopId);
       q.setParameterList("productIds", productIds);
       return q;
  }


  public static Query getLastProductSupplierByProductIds(Session session, Long shopId, Set<Long> productIds) {
//    StringBuffer sb = new StringBuffer("select si2.* from (select si.* from SupplierInventory si where si.productId is not null " +
//        "and si.supplierId is not null and si.shopId =:shopId group by si.productId,si.supplierId ) si2" +
//        " where si2.productId in(:productIds) group by si2.supplierId having count(si2.productId) =:size order by max(si2.lastStorageTime) desc");
  StringBuffer sb = new StringBuffer("select si2.* from (select si.* from supplier_inventory si where si.product_id in (:productIds) " +
        "and si.supplier_id is not null and si.shop_id =:shopId and si.is_disabled =:disabled group by si.product_id,si.supplier_id ) si2" +
        "  group by si2.supplier_id having count(si2.product_id) =:size order by max(si2.last_storage_time) desc");
//    Query query = session.createQuery(sb.toString());
    Query query = session.createSQLQuery(sb.toString()).addEntity(SupplierInventory.class);
    query.setLong("shopId", shopId);
    query.setParameter("disabled", YesNo.NO.name());
    query.setParameterList("productIds", productIds);
    query.setInteger("size", productIds.size());
    return query;
  }

  public static Query getSupplierAllInventory(Session session, Long shopId, Long supplierId) {
    StringBuffer sb = new StringBuffer();
    sb.append("from SupplierInventory where shopId =:shopId and supplierId=:supplierId  and disabled =:disabled ");
    return session.createQuery(sb.toString()).setLong("shopId",shopId).setLong("supplierId",supplierId).setParameter("disabled",YesNo.NO);
  }

  public static Query getSupplierInventory(Session session,SupplierInventoryDTO condition) {
    StringBuffer sb = new StringBuffer();
    sb.append("from SupplierInventory where shopId =:shopId and disabled =:disabled ");
    if(!ArrayUtil.isEmpty(condition.getProductIds())){
      sb.append(" and productId in (:productIds)");
    }
    if(condition.getStorehouseId()!=null){
      sb.append(" and storehouseId =:storehouseId");
    }
    if(condition.getSupplierId()!=null){
      sb.append(" and supplierId =:supplierId");
    }
    Query query= session.createQuery(sb.toString()).setLong("shopId",condition.getShopId()).setParameter("disabled",YesNo.NO);
    if(!ArrayUtil.isEmpty(condition.getProductIds())){
      query.setParameterList("productIds",condition.getProductIds());
    }
    if(condition.getStorehouseId()!=null){
      query.setLong("storehouseId",condition.getStorehouseId());
    }
    if(condition.getSupplierId()!=null){
      query.setLong("supplierId",condition.getSupplierId());
    }
    return query;
  }

  public static Query getSupplierInventoryByStorehouseIdAndProductIds(Session session, Long shopId, Long storehouseId, Long... productIds) {
    StringBuffer sb = new StringBuffer();
    sb.append("from SupplierInventory where shopId =:shopId and disabled =:disabled ");
    sb.append(" and productId in (:productIds)");
    if(storehouseId != null) {
      sb.append(" and storehouseId =:storehouseId");
    } else{
      sb.append(" and storehouseId is null");
    }

    Query query = session.createQuery(sb.toString()).setLong("shopId", shopId).setParameter("disabled",YesNo.NO);
    query.setParameterList("productIds", productIds);
    if(storehouseId != null) {
      query.setLong("storehouseId", storehouseId);
    }
    return query;
  }

  public static Query getOutStorageRelation(Session session, Long shopId, Long outStorageOrderId, OrderTypes outStorageOrderType, Long outStorageItemId, Long productId) {
    StringBuffer sb = new StringBuffer();
    sb.append("from OutStorageRelation where shopId =:shopId and outStorageOrderId=:outStorageOrderId and outStorageOrderType=:outStorageOrderType and disabled =:disabled ");
    if(outStorageItemId!=null){
      sb.append(" and outStorageItemId=:outStorageItemId");
    }
    if(productId!=null){
      sb.append(" and productId=:productId");
    }
    Query query = session.createQuery(sb.toString())
        .setLong("shopId", shopId)
        .setLong("outStorageOrderId", outStorageOrderId)
        .setParameter("outStorageOrderType", outStorageOrderType).setParameter("disabled",YesNo.NO);
    if(outStorageItemId!=null){
      query.setLong("outStorageItemId", outStorageItemId);
    }
    if(productId!=null){
      query.setLong("productId", productId);
    }
    return query;
  }

  public static Query getOutStorageRelationByOrderIds(Session session, Long shopId, Long... outStorageOrderId) {
    StringBuffer sb = new StringBuffer();
    sb.append("from OutStorageRelation where shopId =:shopId and outStorageOrderId in(:outStorageOrderId) and disabled =:disabled ");

    Query query = session.createQuery(sb.toString())
        .setLong("shopId", shopId)
        .setParameterList("outStorageOrderId", outStorageOrderId).setParameter("disabled",YesNo.NO);
    return query;
  }

  public static Query getOutStorageRelationByOrderAndProductIds(Session session, Long shopId, Long outStorageOrderId,Set<Long> productIds) {
    StringBuffer sb = new StringBuffer();
    sb.append("from OutStorageRelation where shopId =:shopId and productId in(:productIds) and outStorageOrderId =:outStorageOrderId and disabled =:disabled ");

    Query query = session.createQuery(sb.toString())
        .setLong("shopId", shopId)
        .setLong("outStorageOrderId", outStorageOrderId)
        .setParameter("disabled",YesNo.NO)
        .setParameterList("productIds",productIds);
    return query;
  }

  public static Query getOutStorageRelationByRelated(Session session, Long shopId, Long relatedOrderId, OrderTypes relatedOrderType, Long relatedItemId, Long productId) {
    StringBuffer sb = new StringBuffer();
    sb.append("from OutStorageRelation where shopId =:shopId and relatedOrderId=:relatedOrderId " +
        "and relatedOrderType=:relatedOrderType and relatedItemId=:relatedItemId and productId=:productId and disabled =:disabled ");
    return session.createQuery(sb.toString()).setLong("shopId", shopId).setLong("relatedOrderId", relatedOrderId)
        .setLong("relatedItemId", relatedItemId).setLong("productId", productId).setParameter("relatedOrderType", relatedOrderType).setParameter("disabled",YesNo.NO);
  }


  public static Query getInStorageRecordMap(Session session,Long shopId, Long storehouseId,Set<Long> productIdSet, Set<Long> supplierIdSet,Set<OutStorageSupplierType> outStorageSupplierTypes,boolean containSupplierIdEmpty) {
    StringBuffer sb = new StringBuffer();
    sb.append("from InStorageRecord where shopId =:shopId and productId in(:productId) and remainAmount > 0 and disabled =:disabled ");
    if (CollectionUtils.isNotEmpty(supplierIdSet) && containSupplierIdEmpty) {
      sb.append(" and ( supplierId in(:supplierId) or supplierId is null )");
    } else if (CollectionUtils.isNotEmpty(supplierIdSet)) {
      sb.append(" and supplierId in(:supplierId) ");
    } else if (!containSupplierIdEmpty) {
      sb.append(" and supplierId is not null ");
    }


    if (CollectionUtils.isNotEmpty(outStorageSupplierTypes)) {
      sb.append(" and supplierType in(:outStorageSupplierTypes)");
    }

    if (storehouseId != null) {
      sb.append(" and storehouseId =:storehouseId");
    }
    Query query = session.createQuery(sb.toString())
        .setLong("shopId", shopId)
        .setParameterList("productId", productIdSet);

    if (CollectionUtils.isNotEmpty(supplierIdSet)) {
      query.setParameterList("supplierId", supplierIdSet);
    }

    if (CollectionUtils.isNotEmpty(outStorageSupplierTypes)) {
      query.setParameterList("outStorageSupplierTypes", outStorageSupplierTypes);
    }
    if (storehouseId != null) {
      query.setLong("storehouseId", storehouseId);
    }
    query.setParameter("disabled",YesNo.NO);
    return query;
  }



  public static Query getInStorageRecordByOrderIds(Session session, Long shopId, Long... inStorageOrderId) {
    StringBuffer sb = new StringBuffer();
    sb.append("from InStorageRecord where shopId =:shopId and inStorageOrderId in(:inStorageOrderId) and disabled =:disabled ");

    Query query = session.createQuery(sb.toString())
        .setLong("shopId", shopId)
        .setParameterList("inStorageOrderId", inStorageOrderId).setParameter("disabled",YesNo.NO);
    return query;
  }
  public static Query getBorrowOrdersByShopIdAndOrderIds(Session session, Long shopId, Long... orderIds) {
    StringBuffer sb = new StringBuffer("from BorrowOrder where id in (:orderIds)");
    if (shopId != null) {
      sb.append(" and shopId=:shopId");
    }
    Query query = session.createQuery(sb.toString()).setParameterList("orderIds", orderIds);
    if (shopId != null) {
      query.setLong("shopId", shopId);
    }
    return query;
  }
  public static Query getReturnBorrowOrdersByShopIdAndOrderIds(Session session, Long shopId, Long... orderIds) {
    StringBuffer sb = new StringBuffer("from ReturnOrder where id in (:orderIds)");
    if (shopId != null) {
      sb.append(" and shopId=:shopId");
    }
    Query query = session.createQuery(sb.toString()).setParameterList("orderIds", orderIds);
    if (shopId != null) {
      query.setLong("shopId", shopId);
    }
    return query;
  }
  public static Query getInnerReturnsByShopIdAndOrderIds(Session session, Long shopId, Long... orderIds) {
    StringBuffer sb = new StringBuffer("from InnerReturn where id in (:orderIds)");
    if (shopId != null) {
      sb.append(" and shopId=:shopId");
    }
    Query query = session.createQuery(sb.toString()).setParameterList("orderIds", orderIds);
    if (shopId != null) {
      query.setLong("shopId", shopId);
    }
    return query;
  }
  public static Query getInnerPickingsByShopIdAndOrderIds(Session session, Long shopId, Long... orderIds) {
    StringBuffer sb = new StringBuffer("from InnerPicking where id in (:orderIds)");
    if (shopId != null) {
      sb.append(" and shopId=:shopId");
    }
    Query query = session.createQuery(sb.toString()).setParameterList("orderIds", orderIds);
    if (shopId != null) {
      query.setLong("shopId", shopId);
    }
    return query;
  }
  public static Query getInventoryChecksByShopIdAndOrderIds(Session session, Long shopId, Long... orderIds) {
    StringBuffer sb = new StringBuffer("from InventoryCheck where id in (:orderIds)");
    if (shopId != null) {
      sb.append(" and shopId=:shopId");
    }
    Query query = session.createQuery(sb.toString()).setParameterList("orderIds", orderIds);
    if (shopId != null) {
      query.setLong("shopId", shopId);
    }
    return query;
  }

  public static Query getAllocateRecordsByShopIdAndOrderIds(Session session, Long shopId, Long... orderIds) {
    StringBuffer sb = new StringBuffer("from AllocateRecord where id in (:orderIds)");
    if (shopId != null) {
      sb.append(" and shopId=:shopId");
    }
    Query query = session.createQuery(sb.toString()).setParameterList("orderIds", orderIds);
    if (shopId != null) {
      query.setLong("shopId", shopId);
    }
    return query;
  }

  public static Query countSupplierInventory(Session session, Long shopId, Long productId) {
    StringBuffer sb = new StringBuffer("select count(siId.supplier_id) as total from ");
    sb.append(" (select si.supplier_id from supplier_inventory si");
//    sb.append(" LEFT JOIN bcuser.supplier s on si.supplier_id = s.id ");
    sb.append(" where si.is_disabled =:disabled and ");
    sb.append(" si.shop_id =:shopId and si.product_id =:productId and si.supplier_id is not null");
    sb.append(" group by si.supplier_id,si.shop_id,si.product_id) as siId");
    Query query = session.createSQLQuery(sb.toString()).addScalar("total", StandardBasicTypes.LONG);
    query.setLong("shopId", shopId);
    query.setLong("productId", productId);
    query.setParameter("disabled", YesNo.NO.name());
    return query;
  }

  public static Query getSupplierIdsByProductId(Session session, Long shopId, Long productId, Pager pager) {
    StringBuffer sb = new StringBuffer();
    sb.append(" select si.supplierId from SupplierInventory si");
    sb.append(" where si.disabled =:disabled and ");
    sb.append(" si.shopId =:shopId and si.productId =:productId and si.supplierId is not null");
    sb.append(" group by si.supplierId,si.shopId,si.productId order by si.lastStorageTime desc");
    Query query = session.createQuery(sb.toString());
    query.setLong("shopId", shopId);
    query.setLong("productId", productId);
    query.setParameter("disabled", YesNo.NO);
    query.setFirstResult(pager.getRowStart());
    query.setMaxResults(pager.getPageSize());
    return query;
  }

  public static Query getSupplierInventoryByProductAndSupplierIds(Session session, Long shopId, Long productId, Set<Long> supplierIds) {
    StringBuffer sb = new StringBuffer();
    sb.append("from SupplierInventory where shopId =:shopId and productId =:productId and supplierId in(:supplierIds) and disabled =:disabled");
    return session.createQuery(sb.toString()).setLong("shopId",shopId).setLong("productId",productId)
        .setParameterList("supplierIds",supplierIds).setParameter("disabled",YesNo.NO);
  }

  public static Query getSupplierInventoryBySupplierIds(Session session, Long shopId, Set<Long> supplierIds) {
    StringBuffer sb = new StringBuffer();
    sb.append("from SupplierInventory where shopId =:shopId and supplierId in(:supplierIds) and disabled =:disabled");
    return session.createQuery(sb.toString()).setLong("shopId",shopId).setParameterList("supplierIds",supplierIds)
        .setParameter("disabled",YesNo.NO);
  }

  public static Query getSupplierInventoryBySupplierType(Session session, Long shopId, Long productId, Long storehouseId,
                                                         OutStorageSupplierType supplierType) {
    StringBuffer sb = new StringBuffer();
    sb.append("from SupplierInventory where shopId =:shopId and productId =:productId and supplierType =:supplierType and disabled =:disabled ");
    if (storehouseId == null) {
      sb.append(" and storehouseId is null");
    } else {
      sb.append(" and storehouseId =:storehouseId");
    }
    sb.append(" and supplierId is null");
    Query query = session.createQuery(sb.toString());
    query.setLong("shopId", shopId).setLong("productId", productId).setParameter("supplierType", supplierType);
    if (storehouseId != null) {
      query.setLong("storehouseId", storehouseId);
    }
    query.setParameter("disabled",YesNo.NO);
    return query;
  }

  public static Query getSupplierInventoriesByIds(Session session, Set<Long> supplierInventoryIds) {
    StringBuffer sb = new StringBuffer();
    sb.append("from SupplierInventory where id  in (:supplierInventoryIds) and disabled =:disabled ");
    return session.createQuery(sb.toString()).setParameterList("supplierInventoryIds",supplierInventoryIds).setParameter("disabled",YesNo.NO);

  }

  public static Query getSupplierInventoriesByPurchaseInventoryId(Session session, Long shopId,Long purchaseInventoryId) {
    StringBuffer sb = new StringBuffer();
    sb.append("from SupplierInventory where shopId =:shopId and lastPurchaseInventoryOrderId =:purchaseInventoryId and disabled =:disabled ");
    return session.createQuery(sb.toString()).setLong("shopId",shopId).setLong("purchaseInventoryId",purchaseInventoryId).setParameter("disabled",YesNo.NO);

  }


  public static Query getLastPurchaseInventoryItems(Session session, Long shopId, Long supplierId, Long storehouseId, Set<Long> productIds) {
    StringBuffer sb = new StringBuffer();
    sb.append("select pi2.id,pi2.purchase_inventory_id,pi2.product_id,pi2.amount,pi2.price,pi2.unit,pi2.product_history_id,pi2.vest_date ");
    sb.append("from (select pi.*,p.vest_date from purchase_inventory_item pi LEFT JOIN purchase_inventory p ");
    sb.append("ON pi.purchase_inventory_id = p.id ");
    sb.append("where pi.product_id in(:productIds) ");
    sb.append("and p.shop_id =:shopId ");
    sb.append("and p.status_enum =:status ");
    sb.append("and p.supplier_id =:supplierId ");
    if (storehouseId != null) {
      sb.append("and p.storehouse_id =:storehouseId ");
    }else{
      sb.append("and p.storehouse_id is null ");
    }
    sb.append("ORDER BY p.vest_date DESC ");
    sb.append(") as pi2 GROUP BY pi2.product_id,pi2.id");


    Query sqlQuery = session.createSQLQuery(sb.toString())
        .addScalar("id", StandardBasicTypes.LONG)
        .addScalar("purchase_inventory_id", StandardBasicTypes.LONG)
        .addScalar("product_id", StandardBasicTypes.LONG)
        .addScalar("amount", StandardBasicTypes.DOUBLE)
        .addScalar("price", StandardBasicTypes.DOUBLE)
        .addScalar("unit", StandardBasicTypes.STRING)
        .addScalar("product_history_id", StandardBasicTypes.LONG)
        .addScalar("vest_date", StandardBasicTypes.LONG);
    sqlQuery.setParameterList("productIds",productIds);
    sqlQuery.setLong("shopId", shopId);
    sqlQuery.setString("status", OrderStatus.PURCHASE_INVENTORY_DONE.name());
    sqlQuery.setLong("supplierId", supplierId);
    if (storehouseId != null) {
      sqlQuery.setLong("storehouseId", storehouseId);
    }
    return sqlQuery;
  }


  public static Query getAllPurchaseInventoryItems(Session session, Long shopId, Set<Long> purchaseInventoryIds, Set<Long> productIds) {
    StringBuffer sb = new StringBuffer();
    sb.append("from PurchaseInventoryItem where purchaseInventoryId in(:purchaseInventoryIds) and productId in (:productIds) ");
    return session.createQuery(sb.toString())
        .setParameterList("purchaseInventoryIds", purchaseInventoryIds)
        .setParameterList("productIds", productIds);
  }

  public static Query getInStorageRecordBySupplierIds(Session session, Long shopId, Set<Long> supplierIds) {
    StringBuffer sb = new StringBuffer();
    sb.append("from InStorageRecord where shopId =:shopId and supplierId in (:supplierIds) ");
    return session.createQuery(sb.toString())
        .setLong("shopId", shopId)
        .setParameterList("supplierIds", supplierIds);
  }

  public static Query getOutStorageRelationBySupplierIds(Session session, Long shopId, Long... supplierIds) {
    StringBuffer sb = new StringBuffer();
    sb.append("from OutStorageRelation where shopId =:shopId and relatedSupplierId in (:supplierIds) ");
    return session.createQuery(sb.toString())
        .setLong("shopId", shopId)
        .setParameterList("supplierIds", supplierIds);
  }

  public static Query getPrintTemplateInfoById(Session session, Long templateId) {
    StringBuffer sb = new StringBuffer();
    sb.append("select id, name, orderTypeEnum from PrintTemplate where id = :id");
    return session.createQuery(sb.toString()).setLong("id",templateId);
  }

  public static Query getPrintTemplateFullById(Session session, Long templateId) {
    return session.createQuery("from PrintTemplate where id=:templateId").setLong("templateId", templateId);
  }

  //add by zhuj
  public static Query getCustomerDepositByShopIdAndCustomerId(Session session, Long shopId, Long customerId) {
    return session.createQuery("from CustomerDeposit where shop_id =:shopId and customer_id=:customerId").setLong("shopId", shopId).setLong("customerId", customerId);
  }

  public static Query getCustomerDepositsByShopIdAndCustomerIds(Session session, Long shopId, List<Long> customerIds){
    return session.createQuery("from CustomerDeposit where shop_id =:shopId and customer_id in :customerIds").setLong("shopId", shopId).setParameterList("customerIds", customerIds);
  }

  public static Query getDepositOrderByShopIdAndCustomerIdOrSupplier(Session session, Long shopId, Long customerId, Long supplierId, List<Long> inOut,SortObj sortObj) {
    StringBuilder sb = new StringBuilder("from DepositOrder where shopId =:shopId ");
    if (customerId != null) {
      sb.append("and customerId =:customerId ");
    }
    if (supplierId != null) {
      sb.append("and supplierId=:supplierId ");
    }
    if (inOut != null) {
      sb.append("and inOut in :inOut");
    }

    //TODO zhuj 这个地方要干掉。。。
    if (sortObj != null && StringUtils.isNotBlank(sortObj.getSortName()) && StringUtils.isNotBlank(sortObj.getSortFlag())) {
      String sortName = sortObj.getSortName();
      String sortFlag = sortObj.getSortFlag();

      if (StringUtils.equals(sortName, "money")) {
        sb.append(" order by actually_paid ");
      } else if (StringUtils.equals(sortName, "time")) {
        sb.append(" order by created ");
      }

      if (StringUtils.equals(sortFlag, "ascending")) {
        sb.append(" asc ");
      } else {
        sb.append(" desc ");
      }
    }

    Query query = session.createQuery(sb.toString());
    query.setLong("shopId", shopId);
    if (customerId != null) {
      query.setLong("customerId", customerId);
    }
    if (supplierId != null) {
      query.setLong("supplierId", supplierId);
    }
    if (inOut != null) {
      query.setParameterList("inOut", inOut);
    }
    return query;
  }

  public static Query countDepositOrders(Session session, Long shopId, Long customerId, Long supplierId, List<Long> inOut) {
    StringBuilder sb = new StringBuilder("select count(depositOrder.id) from DepositOrder as depositOrder where shopId =:shopId ");
    if (customerId != null) {
      sb.append("and customerId =:customerId ");
    }
    if (supplierId != null) {
      sb.append("and supplierId=:supplierId ");
    }
    if (inOut != null) {
      sb.append("and inOut in :inOut");
    }
    Query query = session.createQuery(sb.toString());
    query.setLong("shopId", shopId);
    if (customerId != null) {
      query.setLong("customerId", customerId);
    }
    if (supplierId != null) {
      query.setLong("supplierId", supplierId);
    }
    if (inOut != null) {
      query.setParameterList("inOut", inOut);
    }
    return query;
  }

  public static Query queryDepositOrderByShopIdCustomerIdOrSupplierId(Session session, Long shopId, Long customerId, Long supplierId, Long relatedOrderId) {
    StringBuilder sb = new StringBuilder(" from DepositOrder where shopId =:shopId and relatedOrderId=:relatedOrderId ");
    if (customerId != null) {
      sb.append("and customerId =:customerId ");
    }
    if (supplierId != null) {
      sb.append("and supplierId=:supplierId ");
    }
    Query query = session.createQuery(sb.toString());
    query.setLong("shopId", shopId).setLong("relatedOrderId",relatedOrderId);
    if (customerId != null) {
      query.setLong("customerId", customerId);
    }
    if (supplierId != null) {
      query.setLong("supplierId", supplierId);
    }

    return query;

  }

  public static Query queryDepositOrderByShopIdAndIdsAndType(Session session, Long shopId, List<Long> ids, String type) {
    StringBuilder stringBuilder = new StringBuilder("from DepositOrder where shopId =:shopId ");
    if (StringUtils.equals("customer", type)) {
      stringBuilder.append(" and customerId in :ids ");
    } else if (StringUtils.equals("supplier", type)) {
      stringBuilder.append(" and supplierId in :ids ");
    }
    stringBuilder.append(" order by created desc ");
    return session.createQuery(stringBuilder.toString()).setLong("shopId", shopId).setParameterList("ids", ids);
  }

  public static Query countDepositOrdersByIdsAndType(Session session, Long shopId, List<Long> ids, String type) {
    StringBuilder stringBuilder = new StringBuilder("select count(depositOrder.id) from DepositOrder as depositOrder where shopId =:shopId ");
    if (StringUtils.equals("customer", type)) {
      stringBuilder.append(" and customerId in :ids ");
    } else if (StringUtils.equals("supplier", type)) {
      stringBuilder.append(" and supplierId in :ids ");
    }
    return session.createQuery(stringBuilder.toString()).setLong("shopId", shopId).setParameterList("ids", ids);
  }

  public static Query queryDepositOrderByConditions(Session session, Long shopId, Long customerId, Long supplierId, List<Long> inOut, SortObj sortObj, Long startTime, Long endTime,String type) {
    StringBuilder sb = new StringBuilder("from DepositOrder where shopId =:shopId ");
    if (customerId != null) {
      sb.append(" and customerId =:customerId ");
    }
    if (supplierId != null) {
      sb.append(" and supplierId=:supplierId ");
    }
    if (inOut != null) {
      sb.append(" and inOut in :inOut ");
    }
    if (startTime != null) {
      sb.append(" and created >= :startTime ");
    }
    if (endTime != null) {
      sb.append(" and created < :endTime ");
    }

    if (StringUtils.equals(type, "customer")) {
      sb.append(" and customerId != null ");
    } else if (StringUtils.equals(type, "supplier")) {
      sb.append(" and supplierId != null ");
    }else{
      //TODO
    }

    //TODO zhuj 这个地方要干掉。。。
    if (sortObj != null && StringUtils.isNotBlank(sortObj.getSortName()) && StringUtils.isNotBlank(sortObj.getSortFlag())) {
      String sortName = sortObj.getSortName();
      String sortFlag = sortObj.getSortFlag();

      if (StringUtils.equals(sortName, "money")) {
        sb.append(" order by actually_paid ");
      } else if (StringUtils.equals(sortName, "time")) {
        sb.append(" order by created ");
      }

      if (StringUtils.equals(sortFlag, "ascending")) {
        sb.append(" asc ");
      } else {
        sb.append(" desc ");
      }
    }

    Query query = session.createQuery(sb.toString());
    query.setLong("shopId", shopId);
    if (customerId != null) {
      query.setLong("customerId", customerId);
    }
    if (supplierId != null) {
      query.setLong("supplierId", supplierId);
    }
    if (inOut != null) {
      query.setParameterList("inOut", inOut);
    }
    if (startTime != null) {
      query.setLong("startTime", startTime);
    }
    if (endTime != null) {
      query.setLong("endTime", endTime+24*3600*1000);
    }
    return query;
  }

  public static Query queryDepositOrderByIdsAndType(Session session, Long shopId,List<Long> ids,List<Long> inOut, SortObj sortObj, Long startTime, Long endTime,String type) {
    StringBuilder sb = new StringBuilder("from DepositOrder where shopId =:shopId ");
    if (ids != null && StringUtils.equals(type,"customer") ) {
      sb.append(" and customerId  in :customerIds ");
    }
    if (ids != null && StringUtils.equals(type,"supplier")){
      sb.append(" and supplierId  in :supplierIds ");
    }
    if (inOut != null) {
      sb.append(" and inOut in :inOut ");
    }
    if (startTime != null) {
      sb.append(" and created >= :startTime ");
    }
    if (endTime != null) {
      sb.append(" and created < :endTime ");
    }

    if (StringUtils.equals(type, "customer")) {
      sb.append(" and customerId != null ");
    } else if (StringUtils.equals(type, "supplier")) {
      sb.append(" and supplierId != null ");
    }else{
      //TODO
    }

    //TODO zhuj 这个地方要干掉。。。
    if (sortObj != null && StringUtils.isNotBlank(sortObj.getSortName()) && StringUtils.isNotBlank(sortObj.getSortFlag())) {
      String sortName = sortObj.getSortName();
      String sortFlag = sortObj.getSortFlag();

      if (StringUtils.equals(sortName, "money")) {
        sb.append(" order by actually_paid ");
      } else if (StringUtils.equals(sortName, "time")) {
        sb.append(" order by created ");
      }

      if (StringUtils.equals(sortFlag, "ascending")) {
        sb.append(" asc ");
      } else {
        sb.append(" desc ");
      }
    }

    Query query = session.createQuery(sb.toString());
    query.setLong("shopId", shopId);
    if (ids != null && StringUtils.equals(type,"customer")) {
      query.setParameterList("customerIds", ids);
    }
    if (ids != null && StringUtils.equals(type,"supplier")) {
      query.setParameterList("supplierIds", ids);
    }
    if (inOut != null) {
      query.setParameterList("inOut", inOut);
    }
    if (startTime != null) {
      query.setLong("startTime", startTime);
    }
    if (endTime != null) {
      query.setLong("endTime", endTime+24*3600*1000);
    }
    return query;
  }

  public static Query countDepositOrdersByConditions(Session session, Long shopId, Long customerId, Long supplierId, List<Long> inOut, Long startTime, Long endTime,String type) {
    StringBuilder sb = new StringBuilder("select count(depositOrder.id) from DepositOrder as depositOrder where shopId =:shopId ");
    if (customerId != null) {
      sb.append(" and customerId =:customerId ");
    }
    if (supplierId != null) {
      sb.append(" and supplierId=:supplierId ");
    }
    if (inOut != null) {
      sb.append(" and inOut in :inOut");
    }
    if (startTime != null) {
      sb.append(" and created >= :startTime ");
    }
    if (endTime != null) {
      sb.append(" and created < :endTime ");
    }

    if (StringUtils.equals(type, "customer")) {
      sb.append(" and customerId != null ");
    } else if (StringUtils.equals(type, "supplier")) {
      sb.append(" and supplierId != null ");
    }else{
      //TODO
    }

    Query query = session.createQuery(sb.toString());
    query.setLong("shopId", shopId);
    if (customerId != null) {
      query.setLong("customerId", customerId);
    }
    if (supplierId != null) {
      query.setLong("supplierId", supplierId);
    }
    if (inOut != null) {
      query.setParameterList("inOut", inOut);
    }
    if (startTime != null) {
      query.setLong("startTime", startTime);
    }
    if (endTime != null) {
      query.setLong("endTime", endTime+24*3600*1000);
    }
    return query;
  }

  public static Query getCustomerDepositForReindex(Session session, Long shopId, List<Long> ids) {
    StringBuffer sb = new StringBuffer("from CustomerDeposit");
    sb.append(" where customerId in :ids");
    if (shopId != null) {
      sb.append(" and shopId=:shopId");
    }
    Query query = session.createQuery(sb.toString()).setParameterList("ids", ids);
    if (shopId != null) {
      query.setLong("shopId", shopId);
    }
    return query;
  }

  /**
   * @param session
   * @param preBuyOrderId
   * @return
   */
  public static Query getPreBuyOrderItemsByPreBuyOrderId(Session session, Long preBuyOrderId) {
    return session.createQuery("from PreBuyOrderItem p where  p.preBuyOrderId=:preBuyOrderId")
        .setLong("preBuyOrderId", preBuyOrderId);
  }

  public static Query getLatestPreBuyOrderItem(Session session,int pageStart, int pageSize,ShopKind shopKind) {
    return session.createQuery("select pi from PreBuyOrderItem pi,PreBuyOrder p where pi.preBuyOrderId=p.id and p.businessChanceType=:businessChanceType and pi.shopKind=:shopKind order by pi.creationDate desc")
      .setParameter("shopKind",shopKind).setParameter("businessChanceType",BusinessChanceType.Normal).setFirstResult(pageStart).setMaxResults(pageSize);
  }

  public static Query getPreBuyOrderItemDetailDTO(Session session,PreBuyOrderSearchCondition condition) throws ParseException {
    StringBuilder sb=new StringBuilder();
    sb.append("select pi,p from PreBuyOrderItem pi,PreBuyOrder p where pi.preBuyOrderId=p.id");
    if(condition.getShopId()!=null){
      sb.append(" and p.shopId=:shopId");
    }
    if(ArrayUtil.isNotEmpty(condition.getPreBuyOrderItemIds())){
      sb.append(" and pi.id in (:preBuyOrderItemIds)");
    }
    if(condition.getStartTime()!=null){
      sb.append(" and p.vestDate >=:startTime");
    }
    if(condition.getEndTime()!=null){
      sb.append(" and p.vestDate <=:endTime");
    }
    if(condition.getBusinessChanceType()!=null){
      sb.append(" and p.businessChanceType=:businessChanceType");
    }
    if(condition.isValid()){
      sb.append(" and p.endDate>=:now");
    }
    if(ArrayUtil.isNotEmpty(condition.getProductIds())){
       sb.append(" and pi.productId in (:productIds)");
    }
    if(condition.getSort()!=null){
      sb.append(" order by").append(" p.").append(condition.getSort().getOrderBy()).append(" ").append(condition.getSort().getOrder());
    }
    Query query= session.createQuery(sb.toString());
    if(condition.getShopId()!=null){
      query.setLong("shopId",condition.getShopId());
    }
    if(condition.getStartTime()!=null){
      query.setLong("startTime",condition.getStartTime());
    }
    if(condition.getEndTime()!=null){
      query.setLong("endTime",condition.getEndTime());
    }
    if(ArrayUtil.isNotEmpty(condition.getPreBuyOrderItemIds())){
      query.setParameterList("preBuyOrderItemIds",condition.getPreBuyOrderItemIds());
    }
    if(condition.getBusinessChanceType()!=null){
      query.setParameter("businessChanceType",condition.getBusinessChanceType());
    }
    if(condition.isValid()){
      query.setLong("now",DateUtil.getStartTimeOfToday());
    }
    if(ArrayUtil.isNotEmpty(condition.getProductIds())){
      query.setParameterList("productIds",condition.getProductIds());
    }
    if(condition.getPageSize()!=null){
      query.setFirstResult(condition.getStart()*condition.getPageSize()).setMaxResults(condition.getPageSize());
    }
    return query;
  }

  /**
   * @param session
   * @param quotedPreBuyOrderId
   * @return
   */
  public static Query getQuotedPreBuyOrderItemsByQuotedPreBuyOrderId(Session session, Long quotedPreBuyOrderId) {
    return session.createQuery("from QuotedPreBuyOrderItem q where q.quotedPreBuyOrderId=:quotedPreBuyOrderId")
        .setLong("quotedPreBuyOrderId", quotedPreBuyOrderId);
  }
  /**
   * @param session
   * @param shopId
   * @param preBuyOrderItemId
   * @return
   */
  public static Query getQuotedPreBuyOrderItemsByPreBuyOrderItemId(Session session,Long shopId, Long preBuyOrderItemId) {
    StringBuffer sb = new StringBuffer("from QuotedPreBuyOrderItem p where  p.preBuyOrderItemId=:preBuyOrderItemId");
    if(shopId!=null){
      sb.append(" and p.shopId=:shopId");
    }
    sb.append(" order by p.creationDate desc");
    Query query =session.createQuery(sb.toString()).setLong("preBuyOrderItemId", preBuyOrderItemId);
    if(shopId!=null){
      query.setLong("shopId",shopId);
    }
    return query;
  }

  public static Query getPreBuyOrderItemDTOByIds(Session session,Long shopId, Long... preBuyOrderItemIds) {
    StringBuffer sb = new StringBuffer("from PreBuyOrderItem p where p.shopId=:shopId and p.id in (:preBuyOrderItemIds)");
    sb.append(" order by p.creationDate desc");
    Query query =session.createQuery(sb.toString()).setLong("shopId",shopId).setParameterList("preBuyOrderItemIds", preBuyOrderItemIds);
    return query;
  }
  /**
   * @param session
   * @param shopId
   * @param preBuyOrderId
   * @return
   */
  public static Query getQuotedPreBuyOrderItemsByPreBuyOrderId(Session session,Long shopId, Long preBuyOrderId) {
    StringBuffer sb = new StringBuffer("from QuotedPreBuyOrderItem p where  p.preBuyOrderId=:preBuyOrderId");
    if(shopId!=null){
      sb.append(" and p.shopId=:shopId");
    }
    Query query =session.createQuery(sb.toString()).setLong("preBuyOrderId", preBuyOrderId);
    if(shopId!=null){
      query.setLong("shopId",shopId);
    }
    return query;
  }
  /**
   * @param session
   * @param shopId
   * @return
   */
  public static Query countPreBuyOrderItems(Session session, Long shopId) {
    return session.createQuery("select count(pi) from PreBuyOrderItem pi,PreBuyOrder p where p.id=pi.preBuyOrderId and pi.shopId=:shopId and p.businessChanceType=:businessChanceType and p.deleted='FALSE'")
        .setLong("shopId", shopId).setParameter("businessChanceType",BusinessChanceType.Normal);
  }
  /**
   * @param session
   * @param shopId
   * @return
   */
  public static Query countValidPreBuyOrderItems(Session session, Long shopId) {
    StringBuffer sb = new StringBuffer("select count(*) from PreBuyOrderItem pi where pi.preBuyOrderId in (select id from PreBuyOrder p where p.shopId=:shopId and p.businessChanceType=:businessChanceType and p.deleted='FALSE'");
    try {
      sb.append(" and p.endDate>=").append(DateUtil.getTheDayTime());
    } catch (ParseException e) {
      e.printStackTrace();
    }
    sb.append(")");
    return session.createQuery(sb.toString()).setLong("shopId", shopId).setParameter("businessChanceType",BusinessChanceType.Normal);
  }
  /**
   * @param session
   * @param shopId
   * @return
   */
  public static Query getValidPreBuyOrderItemByShopId(Session session, Long shopId) {
    StringBuffer sb = new StringBuffer("select pb from PreBuyOrder p,PreBuyOrderItem pb where p.id=pb.preBuyOrderId and p.shopId=:shopId and p.deleted='FALSE'");
    try {
      sb.append(" and p.endDate>=").append(DateUtil.getTheDayTime());
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return session.createQuery(sb.toString()).setLong("shopId", shopId);
  }
  /**
   * @param session
   * @param shopId
   * @return
   */
  public static Query countQuotedPreBuyOrderItems(Session session, Long shopId) {
    StringBuilder sb =new StringBuilder("select count(q.id) from QuotedPreBuyOrderItem q,PreBuyOrderItem p,PreBuyOrder o where o.deleted='FALSE'");
    sb.append(" and o.shopId=:shopId and q.preBuyOrderItemId=p.id and p.preBuyOrderId = o.id");
    return session.createQuery(sb.toString())
        .setLong("shopId", shopId);
  }


  /**
   * @param session
   * @param preBuyOrderId
   * @return
   */
  public static Query getQuotedPreBuyOrdersByPreBuyOrderId(Session session, Long preBuyOrderId) {
    return session.createQuery("from QuotedPreBuyOrder p where  p.preBuyOrderId=:preBuyOrderId AND p.deleted='FALSE'")
        .setLong("preBuyOrderId", preBuyOrderId);
  }

  /**
   * @param session
   * @param shopId
   * @return
   */
  public static Query countQuotedPreBuyOrders(Session session, Long shopId) {
    return session.createQuery("select count(*) from QuotedPreBuyOrder p where p.shopId=:shopId and p.deleted='FALSE'")
        .setLong("shopId", shopId);
  }

  /**
   * @param session
   * @param shopId
   * @return
   */
  public static Query countOrdersFromQuotedPreBuyOrder(Session session, Long shopId) {
    return session.createQuery("select count(p.id) from PurchaseOrderItem p,QuotedPreBuyOrderItem qi where p.quotedPreBuyOrderItemId = qi.id and qi.shopId=:shopId")
        .setLong("shopId", shopId);
  }

  public static Query countQuotedPreBuyOrderSupplier(Session session,QuotedPreBuyOrderSearchConditionDTO conditionDTO) {
    StringBuilder sb=new StringBuilder();
    sb.append("select count (distinct shopId) from QuotedPreBuyOrder q where 1=1");
    if(conditionDTO.getStartTime()!=null){
      sb.append(" and q.vestDate>=:startTime");
    }
    if(conditionDTO.getEndTime()!=null){
      sb.append(" and q.vestDate<=:endTime");
    }
    Query query= session.createQuery(sb.toString());
    if(conditionDTO.getStartTime()!=null){
      query.setLong("startTime",conditionDTO.getStartTime());
    }
    if(conditionDTO.getEndTime()!=null){
      query.setLong("endTime",conditionDTO.getEndTime());
    }
    return query;
  }

  public static Query getSupplierOtherQuotedItems(Session session,Long quoterShopId,Long preBuyerShopId,Long quotedPreBuyOrderItemId,Pager pager) {
    return session.createQuery("select q from QuotedPreBuyOrderItem q,PreBuyOrderItem p where q.shopId=:quoterShopId and q.preBuyOrderItemId = p.id and p.shopId=:preBuyerShopId and q.id<>:quotedPreBuyOrderItemId ")
      .setLong("quoterShopId", quoterShopId)
      .setLong("preBuyerShopId", preBuyerShopId)
      .setLong("quotedPreBuyOrderItemId", quotedPreBuyOrderItemId)
      .setFirstResult(pager.getRowStart()).setMaxResults(pager.getPageSize());
  }

  public static Query countSupplierOtherQuotedItems(Session session,Long quoterShopId,Long preBuyerShopId,Long quotedPreBuyOrderItemId) {
    return session.createQuery("select count(q) from QuotedPreBuyOrderItem q,PreBuyOrderItem p where q.shopId=:quoterShopId and q.preBuyOrderItemId = p.id and p.shopId=:preBuyerShopId and q.id<>:quotedPreBuyOrderItemId ")
      .setLong("quoterShopId", quoterShopId)
      .setLong("preBuyerShopId", preBuyerShopId)
      .setLong("quotedPreBuyOrderItemId", quotedPreBuyOrderItemId);
  }

  /**
   * @param session
   * @param quotedPreBuyOrderItemId
   * @return
   */
  public static Query getQuotedPreBuyOrderItemsByItemId(Session session, Long... quotedPreBuyOrderItemId) {
    return session.createQuery("from QuotedPreBuyOrderItem p where  p.id in(:quotedPreBuyOrderItemId)")
        .setParameterList("quotedPreBuyOrderItemId", quotedPreBuyOrderItemId);
  }
  /**
   * @param session
   * @param quotedPreBuyOrderItemId
   * @return
   */
  public static Query getPreBuyOrderByQuotedPreBuyOrderItemId(Session session, Long quotedPreBuyOrderItemId) {
    return session.createQuery("select p from PreBuyOrder p,QuotedPreBuyOrderItem qi where  p.id=qi.preBuyOrderId and qi.id=:quotedPreBuyOrderItemId")
        .setLong("quotedPreBuyOrderItemId", quotedPreBuyOrderItemId);
  }

  public static Query getQuotedPreBuyOrder(Session session,QuotedPreBuyOrderSearchConditionDTO conditionDTO) {
    StringBuilder sb=new StringBuilder("select q,qi from QuotedPreBuyOrder q,QuotedPreBuyOrderItem qi where  q.id=qi.quotedPreBuyOrderId");
    if(conditionDTO.getShopId()!=null){
      sb.append(" and q.shopId=:shopId");
    }
    if(conditionDTO.getCustomerShopId()!=null){
      sb.append(" and q.customerShopId=:customerShopId");
    }
    if(ArrayUtil.isNotEmpty(conditionDTO.getPreBuyOrderItemIds())){
        sb.append(" and qi.preBuyOrderItemId in(:preBuyOrderItemIds)");
    }
    if(conditionDTO.getSort()!=null){
      sb.append(" order by").append(" q.").append(conditionDTO.getSort().getOrderBy()).append(" ").append(conditionDTO.getSort().getOrder());
    }
    Query query= session.createQuery(sb.toString());
    if(conditionDTO.getShopId()!=null){
      query.setLong("shopId",conditionDTO.getShopId());
    }
    if(conditionDTO.getCustomerShopId()!=null){
      query.setLong("customerShopId",conditionDTO.getCustomerShopId());
    }
      if(ArrayUtil.isNotEmpty(conditionDTO.getPreBuyOrderItemIds())){
        query.setParameterList("preBuyOrderItemIds",conditionDTO.getPreBuyOrderItemIds());
      }
    if(conditionDTO.getLimit()!=null){
      query.setFirstResult(conditionDTO.getStart()).setMaxResults(conditionDTO.getLimit());
    }
    return query;
  }

  /**
   * @param session
   * @param shopId
   * @return
   */
  public static Query countQuotedPreBuyOrdersByPreBuyOrderId(Session session,Long shopId, Long preBuyOrderId) {
    StringBuffer sb = new StringBuffer("select count(*) from QuotedPreBuyOrderItem qi where qi.preBuyOrderId=:preBuyOrderId ");
    if(shopId != null) {
      sb.append(" and qi.shopId=:shopId");
    }
    Query q = session.createQuery(sb.toString()).setLong("preBuyOrderId", preBuyOrderId);
    if(shopId != null) {
      q.setLong("shopId",shopId);
    }
    return q;
  }

  public static Query getQuotedPreBuyOrderItemDTOsByIds(Session session,Long shopId,Long... quotedPreBuyOrderItemIds) {
    return session.createQuery("from QuotedPreBuyOrderItem  where shopId=:shopId and id in(:quotedPreBuyOrderItemIds)")
     .setLong("shopId",shopId).setParameterList("quotedPreBuyOrderItemIds", quotedPreBuyOrderItemIds);
  }

  public static Query getLatestConsumeSupplier(Session session, Long shopId, Long productId) {
    StringBuffer sb = new StringBuffer();
    sb.append(" select si.*  from supplier_inventory si");
    sb.append(" LEFT JOIN bcuser.supplier s on si.supplier_id = s.id ");
    sb.append(" where (s.`status` is null or s.`status` = 'ENABLED') and ");
    sb.append(" si.shop_id =:shopId and si.product_id =:productId and si.supplier_id is not null");
    sb.append(" group by si.supplier_id,si.shop_id,si.product_id ");
    sb.append("  order by si.last_storage_time desc");
    Query query = session.createSQLQuery(sb.toString()).addEntity(SupplierInventory.class);
    query.setLong("shopId", shopId);
    query.setLong("productId", productId);
    query.setFirstResult(0);
    query.setMaxResults(1);
    return query;
  }

  public static Query getBcgogoRecommendSupplierId(Session session, List<Long> productIds, Long shopId, Double comparePrices) {
    StringBuilder sql = new StringBuilder();
    sql.append(" select i.*");
    sql.append(" from inventory i");
    sql.append(" where i.id in (:productIds)  and i.sales_price > 0");
    if (comparePrices != null) {
      sql.append(" and i.sales_price <:comparePrices ");
    }
    sql.append(" order by i.sales_price asc");
    Query q = session.createSQLQuery(sql.toString()).addEntity(Inventory.class);
    if (comparePrices != null) {
      q.setDouble("comparePrices", comparePrices);
    }
    return q.setParameterList("productIds",productIds);
  }


  public static Query getWashBeautyShopIdList(Session session, Set<Long> shopIdList) {
    StringBuffer sb = new StringBuffer();
    sb.append(" select distinct shopId from WashBeautyOrderItem where salesManIds is not null ");

    if (CollectionUtils.isNotEmpty(shopIdList)) {
      sb.append("and shopId in(:shopId) ");
    }

    Query query = session.createQuery(sb.toString());

    if (CollectionUtils.isNotEmpty(shopIdList)) {
      query.setParameterList("shopId", shopIdList);
    }
    return query;
  }

  public static Query getMemberCardOrderShopIdList(Session session, Set<Long> shopIdList) {
    StringBuffer sb = new StringBuffer();
    sb.append(" select distinct shopId from MemberCardOrderItem where salesId is not null ");

    if (CollectionUtils.isNotEmpty(shopIdList)) {
      sb.append("and shopId in(:shopId) ");
    }

    Query query = session.createQuery(sb.toString());

    if (CollectionUtils.isNotEmpty(shopIdList)) {
      query.setParameterList("shopId", shopIdList);
    }
    return query;
  }

  public static Query getMemberCardReturnShopIdList(Session session, Set<Long> shopIdList) {
    StringBuffer sb = new StringBuffer();
    sb.append(" select distinct shopId from MemberCardReturnItem where salesId is not null ");

    if (CollectionUtils.isNotEmpty(shopIdList)) {
      sb.append("and shopId in(:shopId) ");
    }

    Query query = session.createQuery(sb.toString());

    if (CollectionUtils.isNotEmpty(shopIdList)) {
      query.setParameterList("shopId", shopIdList);
    }
    return query;
  }

  public static Query getSalesOrderShopIdList(Session session, Set<Long> shopIdList) {
    StringBuffer sb = new StringBuffer();
    sb.append(" select distinct shopId from SalesOrder where ( goodsSaler is not null or goodsSalerId is not null ) ");

    if (CollectionUtils.isNotEmpty(shopIdList)) {
      sb.append("and shopId in(:shopId) ");
    }

    Query query = session.createQuery(sb.toString());

    if (CollectionUtils.isNotEmpty(shopIdList)) {
      query.setParameterList("shopId", shopIdList);
    }
    return query;
  }

  public static Query getSalesReturnShopIdList(Session session, Set<Long> shopIdList) {
    StringBuffer sb = new StringBuffer();
    sb.append(" select distinct shopId from SalesReturn where ( salesReturner is not null or salesReturnerId is not null ) ");

    if (CollectionUtils.isNotEmpty(shopIdList)) {
      sb.append("and shopId in(:shopId) ");
    }

    Query query = session.createQuery(sb.toString());

    if (CollectionUtils.isNotEmpty(shopIdList)) {
      query.setParameterList("shopId", shopIdList);
    }
    return query;
  }

  public static Query getServiceAchievementHistory(Session session, Long shopId, Long serviceId) {
    if (serviceId != null) {
      StringBuffer sb = new StringBuffer();
      sb.append(" from ServiceAchievementHistory where shopId =:shopId and serviceId =:serviceId order by changeTime asc ");
      Query query = session.createQuery(sb.toString());
      query.setLong("shopId", shopId).setLong("serviceId", serviceId);
      return query;
    }
    StringBuffer sb = new StringBuffer();
    sb.append(" from ServiceAchievementHistory where shopId =:shopId group by serviceId ");
    Query query = session.createQuery(sb.toString());
    query.setLong("shopId", shopId);
    return query;
  }

  public static Query getLastedServiceAchievementHistory(Session session, Long shopId, Long serviceId) {
    StringBuffer sb = new StringBuffer();
    sb.append(" from ServiceAchievementHistory where shopId =:shopId and serviceId =:serviceId order by changeTime desc ");
    Query query = session.createQuery(sb.toString());
    query.setLong("shopId", shopId).setLong("serviceId", serviceId).setFirstResult(0).setMaxResults(1);
    return query;
  }



  public static Query getProductAchievementHistory(Session session, Long shopId, Long productId) {
    if (productId != null) {
      StringBuffer sb = new StringBuffer();
      sb.append(" from ProductAchievementHistory where shopId =:shopId and productId =:productId order by changeTime asc ");
      Query query = session.createQuery(sb.toString());
      query.setLong("shopId", shopId).setLong("productId", productId);
      return query;
    }

    StringBuffer sb = new StringBuffer();
    sb.append(" from ProductAchievementHistory where shopId =:shopId group by productId ");
    Query query = session.createQuery(sb.toString());
    query.setLong("shopId", shopId);
    return query;
  }

  public static Query getMemberAchievementHistory(Session session, Long shopId, Long vestDate,MemberOrderType memberOrderType) {
    StringBuffer sb = new StringBuffer();
    sb.append(" from MemberAchievementHistory where shopId =:shopId ");
    if (vestDate != null) {
      sb.append(" and changeTime <=:vestDate ");
    }

    if (memberOrderType != null) {
      sb.append(" and memberOrderType =:memberOrderType ");
    }

    sb.append(" order by changeTime desc ");
    Query query = session.createQuery(sb.toString());
    query.setLong("shopId", shopId);
    if (vestDate != null) {
      query.setLong("vestDate", vestDate);
    }
    if (memberOrderType != null) {
      query.setParameter("memberOrderType", memberOrderType).setMaxResults(1);
    }
    return query;
  }


  public static Query getShopAchievementConfig(Session session, Long shopId, Long achievementRecordId, AssistantRecordType assistantRecordType,Pager pager) {
    StringBuffer sb = new StringBuffer();
    sb.append(" from ShopAchievementConfig where shopId =:shopId  and assistantRecordType =:assistantRecordType ");

    if (achievementRecordId != null) {
      sb.append(" and achievementRecordId =:achievementRecordId ");
    }

    Query query = session.createQuery(sb.toString()).setLong("shopId", shopId).setParameter("assistantRecordType", assistantRecordType);

    if (achievementRecordId != null) {
      query.setLong("achievementRecordId", achievementRecordId);
    }
    if(pager != null){
      query.setFirstResult(pager.getRowStart()).setMaxResults(pager.getPageSize());
    }

    return query;
  }

  public static Query countShopAchievementConfig(Session session, Long shopId, Long achievementRecordId, AssistantRecordType assistantRecordType) {
    StringBuffer sb = new StringBuffer();
    sb.append(" select count(*) from ShopAchievementConfig where shopId =:shopId  and assistantRecordType =:assistantRecordType ");

    if (achievementRecordId != null) {
      sb.append(" and achievementRecordId =:achievementRecordId ");
    }

    Query query = session.createQuery(sb.toString()).setLong("shopId", shopId).setParameter("assistantRecordType", assistantRecordType);

    if (achievementRecordId != null) {
      query.setLong("achievementRecordId", achievementRecordId);
    }
    return query;
  }

  public static Query deleteShopAchievementConfig(Session session, Long shopId) {
    StringBuffer sb = new StringBuffer(" delete from ShopAchievementConfig where shopId =:shopId ");
    Query query = session.createQuery(sb.toString()).setLong("shopId", shopId);
    return query;
  }

  public static Query deleteAssistantAchievementStat(Session session, Long shopId) {
    StringBuffer sb = new StringBuffer(" delete from AssistantAchievementStat where shopId =:shopId ");
    Query query = session.createQuery(sb.toString()).setLong("shopId", shopId);
    return query;
  }

  public static Query deleteAssistantServiceRecord(Session session, Long shopId, Long statTime) {
    StringBuffer sb = new StringBuffer(" delete from AssistantServiceRecord where shopId =:shopId and ( statTime !=:statTime or statTime is null ) ");
    Query query = session.createQuery(sb.toString()).setLong("shopId", shopId).setLong("statTime", statTime);
    return query;
  }

  public static Query deleteAssistantProductRecord(Session session, Long shopId, Long statTime) {
    StringBuffer sb = new StringBuffer(" delete from AssistantProductRecord where shopId =:shopId and ( statTime !=:statTime or statTime is null ) ");
    Query query = session.createQuery(sb.toString()).setLong("shopId", shopId).setLong("statTime", statTime);
    return query;
  }

  public static Query deleteAssistantMemberRecord(Session session, Long shopId, Long statTime) {
    StringBuffer sb = new StringBuffer(" delete from AssistantMemberRecord where shopId =:shopId and ( statTime !=:statTime or statTime is null ) ");
    Query query = session.createQuery(sb.toString()).setLong("shopId", shopId).setLong("statTime", statTime);
    return query;
  }


  public static Query deleteAssistantBusinessAccountRecord(Session session, Long shopId, Long statTime) {
    StringBuffer sb = new StringBuffer(" delete from AssistantBusinessAccountRecord where shopId =:shopId and ( statTime !=:statTime or statTime is null ) ");
    Query query = session.createQuery(sb.toString()).setLong("shopId", shopId).setLong("statTime", statTime);
    return query;
  }

  public static Query countAssistantStatByCondition(Session session, AssistantStatSearchDTO assistantStatSearchDTO) {
    StringBuffer sb = new StringBuffer();
    if (assistantStatSearchDTO.getAchievementStatType() == AchievementStatType.DEPARTMENT) {
      sb.append(" select distinct departmentId");
    } else if (assistantStatSearchDTO.getAchievementStatType() == AchievementStatType.ASSISTANT) {
      sb.append(" select distinct assistantId");
    }

    sb.append(" from AssistantAchievementStat where shopId =:shopId  and statTime >=:startTime and statTime <:endTime ");
    sb.append(" and achievementStatType =:achievementStatType ");

    if (assistantStatSearchDTO.getServiceId() != null) {
      sb.append(" and serviceId =:serviceId ");
    }else{
      sb.append(" and serviceId is null ");
    }
    if (assistantStatSearchDTO.getAssistantOrDepartmentId() != null) {
      if (assistantStatSearchDTO.getAchievementStatType() == AchievementStatType.DEPARTMENT) {
        sb.append(" and departmentId =:departmentId ");
      } else if (assistantStatSearchDTO.getAchievementStatType() == AchievementStatType.ASSISTANT) {
        sb.append(" and assistantId =:assistantId ");
      }
    }

    Query query = session.createQuery(sb.toString()).setLong("shopId", assistantStatSearchDTO.getShopId())
        .setLong("startTime", assistantStatSearchDTO.getStartTime()).setLong("endTime", assistantStatSearchDTO.getEndTime())
        .setParameter("achievementStatType", assistantStatSearchDTO.getAchievementStatType());
    if (assistantStatSearchDTO.getServiceId() != null) {
      query.setLong("serviceId", assistantStatSearchDTO.getServiceId());
    }

    if (assistantStatSearchDTO.getAssistantOrDepartmentId() != null) {
      if (assistantStatSearchDTO.getAchievementStatType() == AchievementStatType.DEPARTMENT) {
        query.setLong("departmentId", assistantStatSearchDTO.getAssistantOrDepartmentId());
      } else if (assistantStatSearchDTO.getAchievementStatType() == AchievementStatType.ASSISTANT) {
        query.setLong("assistantId", assistantStatSearchDTO.getAssistantOrDepartmentId());
      }
    }

    return query;
  }


  public static Query countAssistantRecordByCondition(Session session,AssistantStatSearchDTO assistantStatSearchDTO,Set<OrderTypes> orderTypes) {
    StringBuffer sb = new StringBuffer();
    sb.append(" select count(*) from");
    if (assistantStatSearchDTO.getAssistantRecordType() == AssistantRecordType.SERVICE) {
      sb.append(" AssistantServiceRecord where 1=1 ");

      if (NumberUtil.isLongNumber(assistantStatSearchDTO.getServiceIdStr())) {
        sb.append(" and serviceId=:serviceId ");
      }
      sb.append(" and orderType in(:orderTypes) ");

    } else if (assistantStatSearchDTO.getAssistantRecordType() == AssistantRecordType.PRODUCT) {
      sb.append(" AssistantProductRecord where 1=1 ");
      sb.append(" and orderType in(:orderTypes) ");

    } else if (assistantStatSearchDTO.getAssistantRecordType() == AssistantRecordType.MEMBER_NEW || assistantStatSearchDTO.getAssistantRecordType() == AssistantRecordType.MEMBER_RENEW) {
      sb.append(" AssistantMemberRecord where 1=1 ");

      if (MemberOrderType.RENEW.name().equals(assistantStatSearchDTO.getServiceIdStr()) || MemberOrderType.NEW.name().equals(assistantStatSearchDTO.getServiceIdStr())) {
        sb.append(" and memberOrderType=:memberOrderType ");
      }
      sb.append(" and orderType in(:orderTypes) ");

    } else if (assistantStatSearchDTO.getAssistantRecordType() == AssistantRecordType.BUSINESS_ACCOUNT) {
      sb.append(" AssistantBusinessAccountRecord where 1=1 ");

    }
    sb.append(" and shopId =:shopId  and vestDate >=:startTime and vestDate <:endTime ");

    if (assistantStatSearchDTO.getAchievementStatType() == AchievementStatType.DEPARTMENT) {
      sb.append(" and departmentId =:departmentId ");
    } else if (assistantStatSearchDTO.getAchievementStatType() == AchievementStatType.ASSISTANT) {
      sb.append(" and assistantId =:assistantId");
    }

    Query query = session.createQuery(sb.toString()).setLong("shopId", assistantStatSearchDTO.getShopId()).setLong("startTime", assistantStatSearchDTO.getStartTime()).setLong("endTime", assistantStatSearchDTO.getEndTime());
    if (assistantStatSearchDTO.getAchievementStatType() == AchievementStatType.DEPARTMENT) {
      query.setLong("departmentId", assistantStatSearchDTO.getAssistantOrDepartmentId());
    } else if (assistantStatSearchDTO.getAchievementStatType() == AchievementStatType.ASSISTANT) {
      query.setLong("assistantId", assistantStatSearchDTO.getAssistantOrDepartmentId());
    }

    if (assistantStatSearchDTO.getAssistantRecordType() == AssistantRecordType.SERVICE) {
      if (NumberUtil.isLongNumber(assistantStatSearchDTO.getServiceIdStr())) {
        query.setLong("serviceId", Long.valueOf(assistantStatSearchDTO.getServiceIdStr()));
      }
      query.setParameterList("orderTypes", orderTypes);

    } else if (assistantStatSearchDTO.getAssistantRecordType() == AssistantRecordType.MEMBER_NEW || assistantStatSearchDTO.getAssistantRecordType() == AssistantRecordType.MEMBER_RENEW) {
      if (MemberOrderType.RENEW.name().equals(assistantStatSearchDTO.getServiceIdStr()) || MemberOrderType.NEW.name().equals(assistantStatSearchDTO.getServiceIdStr())) {
        query.setString("memberOrderType", assistantStatSearchDTO.getServiceIdStr());
      }
      query.setParameterList("orderTypes", orderTypes);

    }else if (assistantStatSearchDTO.getAssistantRecordType() == AssistantRecordType.PRODUCT) {
      query.setParameterList("orderTypes", orderTypes);
    }

    return query;
  }


  public static Query getAssistantRecordByPager(Session session,AssistantStatSearchDTO assistantStatSearchDTO,Set<OrderTypes> orderTypes,Pager pager) {
    StringBuffer sb = new StringBuffer();
    sb.append(" from");
    if (assistantStatSearchDTO.getAssistantRecordType() == AssistantRecordType.SERVICE) {
      sb.append(" AssistantServiceRecord where 1=1 ");
      if (NumberUtil.isLongNumber(assistantStatSearchDTO.getServiceIdStr())) {
        sb.append(" and serviceId=:serviceId ");
      }
      sb.append(" and orderType in(:orderTypes) ");
    } else if (assistantStatSearchDTO.getAssistantRecordType() == AssistantRecordType.PRODUCT) {
      sb.append(" AssistantProductRecord where 1=1 ");
      sb.append(" and orderType in(:orderTypes) ");

    } else if (assistantStatSearchDTO.getAssistantRecordType() == AssistantRecordType.MEMBER_NEW || assistantStatSearchDTO.getAssistantRecordType() == AssistantRecordType.MEMBER_RENEW) {
      sb.append(" AssistantMemberRecord where 1=1  ");
      if (MemberOrderType.RENEW.name().equals(assistantStatSearchDTO.getServiceIdStr()) || MemberOrderType.NEW.name().equals(assistantStatSearchDTO.getServiceIdStr())) {
        sb.append(" and memberOrderType=:memberOrderType ");
      }
      sb.append(" and orderType in(:orderTypes) ");
    } else if (assistantStatSearchDTO.getAssistantRecordType() == AssistantRecordType.BUSINESS_ACCOUNT) {
      sb.append(" AssistantBusinessAccountRecord where 1=1 ");
    }
    sb.append(" and shopId =:shopId  and vestDate >=:startTime and vestDate <:endTime ");
    if (assistantStatSearchDTO.getAchievementStatType() == AchievementStatType.DEPARTMENT) {
      sb.append(" and departmentId =:departmentId ");
    } else if (assistantStatSearchDTO.getAchievementStatType() == AchievementStatType.ASSISTANT) {
      sb.append(" and assistantId =:assistantId");
    }

    if (assistantStatSearchDTO.getAssistantRecordType() == AssistantRecordType.SERVICE) {
      sb.append(" order by receiptNo  desc");
    } else if (assistantStatSearchDTO.getAssistantRecordType() == AssistantRecordType.PRODUCT) {
      sb.append(" order by receiptNo  desc");

    } else if (assistantStatSearchDTO.getAssistantRecordType() == AssistantRecordType.MEMBER_NEW || assistantStatSearchDTO.getAssistantRecordType() == AssistantRecordType.MEMBER_RENEW) {
      sb.append(" order by vestDate  desc");
    }

    Query query = session.createQuery(sb.toString()).setLong("shopId", assistantStatSearchDTO.getShopId()).setLong("startTime", assistantStatSearchDTO.getStartTime()).setLong("endTime", assistantStatSearchDTO.getEndTime());
    if (assistantStatSearchDTO.getAchievementStatType() == AchievementStatType.DEPARTMENT) {
      query.setLong("departmentId", assistantStatSearchDTO.getAssistantOrDepartmentId());
    } else if (assistantStatSearchDTO.getAchievementStatType() == AchievementStatType.ASSISTANT) {
      query.setLong("assistantId", assistantStatSearchDTO.getAssistantOrDepartmentId());
    }

    if (assistantStatSearchDTO.getAssistantRecordType() == AssistantRecordType.SERVICE) {
      if (NumberUtil.isLongNumber(assistantStatSearchDTO.getServiceIdStr())) {
        query.setLong("serviceId", Long.valueOf(assistantStatSearchDTO.getServiceIdStr()));
      }
      query.setParameterList("orderTypes", orderTypes);
    } else if (assistantStatSearchDTO.getAssistantRecordType() == AssistantRecordType.MEMBER_NEW || assistantStatSearchDTO.getAssistantRecordType() == AssistantRecordType.MEMBER_RENEW) {
      if (MemberOrderType.RENEW.name().equals(assistantStatSearchDTO.getServiceIdStr()) || MemberOrderType.NEW.name().equals(assistantStatSearchDTO.getServiceIdStr())) {
        query.setString("memberOrderType", assistantStatSearchDTO.getServiceIdStr());
      }
      query.setParameterList("orderTypes", orderTypes);
    } else if (assistantStatSearchDTO.getAssistantRecordType() == AssistantRecordType.PRODUCT) {
      query.setParameterList("orderTypes", orderTypes);
    }

    query = query.setFirstResult(pager.getRowStart()).setMaxResults(pager.getPageSize());
    return query;
  }

  public static Query getAssistantStatByIds(Session session,AssistantStatSearchDTO assistantStatSearchDTO,Set<Long> ids) {
    StringBuffer sb = new StringBuffer();
    sb.append(" from AssistantAchievementStat where shopId =:shopId  and statTime >=:startTime and statTime <:endTime ");
    sb.append(" and achievementStatType =:achievementStatType ");

    if (assistantStatSearchDTO.getAchievementStatType() == AchievementStatType.DEPARTMENT) {
      sb.append(" and departmentId in(:ids) ");
    } else if (assistantStatSearchDTO.getAchievementStatType() == AchievementStatType.ASSISTANT) {
      sb.append(" and assistantId in(:ids) ");
    }

    if (assistantStatSearchDTO.getServiceId() != null) {
      sb.append(" and serviceId =:serviceId ");
    } else {
      sb.append(" and serviceId is null ");
    }
    if (assistantStatSearchDTO.getAssistantOrDepartmentId() != null) {
      if (assistantStatSearchDTO.getAchievementStatType() == AchievementStatType.DEPARTMENT) {
        sb.append(" and departmentId =:departmentId ");
      } else if (assistantStatSearchDTO.getAchievementStatType() == AchievementStatType.ASSISTANT) {
        sb.append(" and assistantId =:assistantId ");
      }
    }

    Query query = session.createQuery(sb.toString()).setLong("shopId", assistantStatSearchDTO.getShopId())
        .setLong("startTime", assistantStatSearchDTO.getStartTime()).setLong("endTime", assistantStatSearchDTO.getEndTime())
        .setParameter("achievementStatType", assistantStatSearchDTO.getAchievementStatType());
    if (assistantStatSearchDTO.getServiceId() != null) {
      query.setLong("serviceId", assistantStatSearchDTO.getServiceId());
    }

    if (assistantStatSearchDTO.getAssistantOrDepartmentId() != null) {
      if (assistantStatSearchDTO.getAchievementStatType() == AchievementStatType.DEPARTMENT) {
        query.setLong("departmentId", assistantStatSearchDTO.getAssistantOrDepartmentId());
      } else if (assistantStatSearchDTO.getAchievementStatType() == AchievementStatType.ASSISTANT) {
        query.setLong("assistantId", assistantStatSearchDTO.getAssistantOrDepartmentId());
      }
    }
    query = query.setParameterList("ids", ids);
    return query;
  }

  public static Query validateCouponNoUsed(Session session,Long shopId,String couponType,String couponNo) {
    StringBuffer sb = new StringBuffer();
    sb.append("select count(*) as count from wash_beauty_order_item oi join wash_beauty_order wo on oi.wash_beauty_order_id = wo.id ");
    sb.append(" where oi.shop_id =:shopId and oi.coupon_type =:couponType and oi.coupon_no =:couponNo  and wo.status <>:status");
    Query query = session.createSQLQuery(sb.toString()).addScalar("count",StandardBasicTypes.LONG).setLong("shopId",shopId).setString("couponType",couponType).setString("couponNo",couponNo)
                                                       .setParameter("status",OrderStatus.WASH_REPEAL.toString());
    return query;
  }

  public static Query getRandomNProductIdStr(Session session, Long shopId, int n) {
    Query query = session.createQuery("from Inventory where shopId =:shopId and amount >1 order by lastModified").setLong("shopId", shopId);
    query = query.setMaxResults(n);
    return query;
  }

  public static Query updateRemindEvent(Session session, Long shopId, Long customerId, Long supplierId) {
     return   session.createQuery("update RemindEvent  set customerId = null, supplierId =:supplierId where shopId=:shopId and customerId=:customerId and orderType=:orderType")
                                  .setLong("supplierId",supplierId).setLong("shopId",shopId).setLong("customerId",customerId)
                                  .setString("orderType",OrderTypes.RETURN.toString());
  }

    public static Query updateRemindEvent2(Session session, Long shopId, Long customerId, Long supplierId) {
        return   session.createQuery("update RemindEvent  set supplierId = null, customerId =:customerId where shopId=:shopId and supplierId=:supplierId and orderType=:orderType")
                .setLong("supplierId",supplierId).setLong("shopId",shopId).setLong("customerId",customerId)
                .setString("orderType",OrderTypes.RETURN.toString());
    }

  public static Query getPayableDTOBySupplierIdAndOrderType(Session session, Long shopId, Long supplierId, OrderTypes orderType) {
    StringBuffer hql = new StringBuffer("select p from Payable p where p.shopId = :shopId and p.supplierId =:supplierId and p.orderType=:orderType and p.status <>:status");
    Query q = session.createQuery(hql.toString()).setLong("shopId", shopId).setLong("supplierId", supplierId).setParameter("orderType",orderType).setParameter("status",PayStatus.REPEAL);
    return q;
  }

  /**
   *
   * @param session
   * @param receiverId
   * @param types
   * @return
   * @throws ParseException
   */
   public static Query getLatestUnPushPushMessages(Session session, Long receiverId, PushMessageType... types) throws ParseException {
     StringBuffer sb = new StringBuffer("select pm from PushMessage pm,PushMessageReceiver pmr");
     sb.append(" where (pm.endDate>=").append(DateUtil.getTheDayTime()).append(" or pm.endDate=-1)");
     sb.append(" and pm.id=pmr.messageId and pmr.receiverId=:receiverId and pm.type in (:types)");
     sb.append(" and pmr.pushStatus=:pushStatus");
     sb.append(" order by pm.level desc,pm.createTime desc");
     Query query = session.createQuery(sb.toString())
       .setLong("receiverId", receiverId)
       .setParameterList("types", types)
       .setParameter("pushStatus", PushMessagePushStatus.UN_PUSH);
     return query;
   }

  /**
   * @param session
   * @param receiverId
   * @param shopKind
   * @return
   */
  public static Query getLatestPushMessage(Session session,Long shopId,Long receiverId,ShopKind shopKind,PushMessageType... types ) {
    StringBuffer sb = new StringBuffer("from PushMessage pm,PushMessageReceiver pmr where pmr.shopKind=:shopKind");
    try {
      sb.append(" and (pm.endDate>=").append(DateUtil.getTheDayTime()).append(" or pm.endDate=-1)");
    } catch (ParseException e) {
      e.printStackTrace();
    }
    sb.append(" and pm.id=pmr.messageId and pmr.shopId=:shopId and pmr.receiverId=:receiverId and pmr.status=:status and pm.type in (:types)");
    sb.append(" and pmr.pushStatus=:pushStatus");
    sb.append(" order by pm.level desc,pm.createTime desc");
    Query query = session.createQuery(sb.toString())
        .setLong("shopId", shopId)
        .setLong("receiverId", receiverId)
        .setParameter("shopKind", shopKind)
        .setParameter("status", PushMessageReceiverStatus.UNREAD)
        .setParameter("pushStatus", PushMessagePushStatus.UN_PUSH)
        .setParameterList("types", types);
    query.setMaxResults(1);
    return query;
  }

  public static Query getLatestPushMessage(Session session, Long shopId, PushMessageType... types) {
    StringBuilder sb = new StringBuilder("from PushMessage pm,PushMessageReceiver pmr where pm.id=pmr.messageId and pmr.shopId=:shopId ");
    try {
      sb.append(" and ( pm.endDate>=").append(DateUtil.getTheDayTime()).append(" or pm.endDate=-1 )");
    } catch (ParseException e) {
      e.printStackTrace();
    }
    sb.append(" and pmr.status =:status and pm.type in (:types) ");
    sb.append(" order by pm.level desc,pm.createTime desc");
    return session.createQuery(sb.toString())
        .setLong("shopId", shopId)
        .setParameter("status", PushMessageReceiverStatus.UNREAD)
        .setParameter("pushStatus", PushMessagePushStatus.UN_PUSH)
        .setParameterList("types", types);
  }

  public static Query getLatestPushMessage(Session session, Long shopId, int start, int limit, PushMessageType... type) {
    StringBuilder hql = new StringBuilder("select pm,pmr from PushMessage pm,PushMessageReceiver pmr");
    hql.append(" where pm.id=pmr.messageId and pmr.shopId=:shopId ");
    if (!ArrayUtil.isEmpty(type)) hql.append(" and pm.type in(:type)");
    try {
      hql.append(" and ( pm.endDate>=").append(DateUtil.getTheDayTime()).append(" or pm.endDate=-1 )");
    } catch (ParseException e) {
      e.printStackTrace();
    }
    hql.append(" and pmr.status=:status");
    hql.append(" and pmr.pushStatus=:pushStatus");
    hql.append(" order by pm.level desc,pm.createTime desc");
    Query query = session.createQuery(hql.toString())
        .setLong("shopId", shopId)
        .setParameter("status", PushMessageReceiverStatus.UNREAD)
        .setParameter("pushStatus", PushMessagePushStatus.UN_PUSH);
    if (!ArrayUtil.isEmpty(type))  query.setParameterList("type", type);
    query.setMaxResults(limit).setFirstResult(start);
    return query;
  }

  public static Query getTalkMessageList(Session session,TalkMessageCondition condition) {
    StringBuilder hql = new StringBuilder("select pm from PushMessage pm ,PushMessageReceiver pmr " +
      "where pm.id=pmr.messageId and pm.type in(:types)");
    if (condition.getReceiverId() != null) {
      hql.append(" and pmr.receiverId=:receiverId");
    }
    if (condition.getShopId() != null) {
      hql.append(" and pm.shopId=:shopId");
    }
//    hql.append(" group by pmr.receiverId");
    hql.append(" order by pm.level desc,pm.createTime desc");
    Query query = session.createQuery(hql.toString())
      .setParameterList("types", condition.getTypes());
    if (condition.getReceiverId() != null) {
      query.setLong("receiverId", condition.getReceiverId());
    }
    if (condition.getShopId() != null) {
      query.setLong("shopId", condition.getShopId());
    }
    query.setFirstResult(condition.getStart()).setMaxResults(condition.getLimit());
    return query;
  }

  public static Query countShopTalkMessageList(Session session, String appUserNo,String vehicleNo, Long shopId) {
     StringBuilder hql = new StringBuilder("select count(s) from ShopTalkMessage s where 1=1" );
      if (StringUtil.isNotEmpty(appUserNo)) {
       hql.append(" and appUserNo=:appUserNo");
     }
     if (shopId != null) {
       hql.append(" and shopId=:shopId");
     }
    if (StringUtil.isNotEmpty(vehicleNo)) {
      hql.append(" and vehicleNo=:vehicleNo");
    }
     Query query = session.createQuery(hql.toString());
     if (StringUtil.isNotEmpty(appUserNo)) {
       query.setParameter("appUserNo", appUserNo);
     }
     if (shopId != null) {
       query.setLong("shopId", shopId);
     }
    if (StringUtil.isNotEmpty(vehicleNo)) {
      query.setParameter("vehicleNo", vehicleNo);
    }
     return query;
   }


 public static Query getShopTalkMessage(Session session, String appUserNo,String vehicleNo, Long shopId, int start, int limit) {
    StringBuilder hql = new StringBuilder("from ShopTalkMessage where 1=1");
    if (StringUtil.isNotEmpty(appUserNo)) {
      hql.append(" and appUserNo=:appUserNo");
    }
    if (shopId != null) {
      hql.append(" and shopId=:shopId");
    }
   if (StringUtil.isNotEmpty(vehicleNo)) {
     hql.append(" and vehicleNo=:vehicleNo");
   }
    hql.append(" order by sendTime desc");
    Query query = session.createQuery(hql.toString()) ;
   if (StringUtil.isNotEmpty(appUserNo)) {
      query.setParameter("appUserNo", appUserNo);
    }
    if (shopId != null) {
      query.setLong("shopId", shopId);
    }
   if (StringUtil.isNotEmpty(vehicleNo)) {
     query.setParameter("vehicleNo", vehicleNo);
   }
    query.setFirstResult(start).setMaxResults(limit);
    return query;
  }

//  public static Query countTalkMessageList(Session session, Long receiverId, Long shopId, PushMessageType... type) {
//    StringBuilder hql = new StringBuilder("select count(*) from PushMessage pm,PushMessageReceiver pmr" +   //todo SUM
//      " where pm.id=pmr.messageId  and pm.type in(:type)");
//    if (receiverId != null) {
//      hql.append(" and pmr.receiverId=:receiverId");
//    }
//    if (shopId != null) {
//      hql.append(" and pm.shopId=:shopId");
//    }
//    hql.append(" group by pmr.receiverId");
//    Query query = session.createQuery(hql.toString())
//      .setParameterList("type", type);
//    if (receiverId != null) {
//      query.setLong("receiverId", receiverId);
//    }
//    if (shopId != null) {
//      query.setLong("shopId", shopId);
//    }
//    return query;
//  }


  public static Query countLatestPushMessage(Session session, Long shopId,PushMessageType... type) {
    StringBuilder hql = new StringBuilder("select count(pm.id) from PushMessage pm,PushMessageReceiver pmr");
    hql.append(" where pm.id=pmr.messageId and pmr.shopId=:shopId ");
    if (!ArrayUtil.isEmpty(type)) hql.append(" and pm.type in(:type)");
    try {
      hql.append(" and ( pm.endDate>=").append(DateUtil.getTheDayTime()).append(" or pm.endDate=-1 )");
    } catch (ParseException e) {
      e.printStackTrace();
    }
    hql.append(" and pmr.status=:pushMessageStatus");
    hql.append(" order by pm.level desc,pm.createTime desc");
    Query query = session.createQuery(hql.toString())
        .setLong("shopId", shopId).setParameter("pushMessageStatus", PushMessageReceiverStatus.UNREAD);
    if (!ArrayUtil.isEmpty(type))  query.setParameterList("type", type);
    return query;
  }

  /**
   * @param session
   * @param messageId
   * @return
   */
  public static Query updatePushMessageReceiverByStatusMessageId(Session session, PushMessageReceiverStatus status, Long... messageId) {
    StringBuffer sb = new StringBuffer("update PushMessageReceiver pmr set pmr.status=:pushMessageStatus where pmr.messageId in (:messageIds)");
    Query query = session.createQuery(sb.toString())
        .setParameterList("messageIds", messageId)
        .setParameter("pushMessageStatus", status);
    return query;
  }

  public static Query getUnreadPushMessageSourceBySourceIds(Session session, Set<Long> sourceIdSet, PushMessageSourceType... pushMessageSourceType) {
    return session.createQuery("select pms from PushMessageSource pms,PushMessageReceiver pmr where pms.messageId=pmr.messageId and pmr.status =:status and pms.sourceId in(:sourceIdSet) and pms.type in(:pushMessageSourceTypes)")
        .setParameterList("sourceIdSet", sourceIdSet)
        .setParameterList("pushMessageSourceTypes", pushMessageSourceType)
        .setParameter("status", PushMessageReceiverStatus.UNREAD);
  }
  public static Query getPushMessageReceiverBySourceId(Session session, Long shopId, Long sourceId,Long pushMessageReceiverShopId, PushMessageSourceType... pushMessageSourceType) {
    StringBuilder sb = new StringBuilder();
    sb.append("select pmr from PushMessageReceiver pmr,PushMessageSource pms ");
    sb.append("where pms.messageId = pmr.messageId and pmr.status =:status and pms.sourceId=:sourceId");
    if (shopId != null) {
      sb.append(" and pms.shopId=:shopId ");
    }
    if (!ArrayUtil.isEmpty(pushMessageSourceType)) {
      sb.append(" and pms.type in (:pushMessageSourceType) ");
    }
    if(pushMessageReceiverShopId!=null){
      sb.append(" and pmr.shopId =:pushMessageReceiverShopId ");
    }

    Query query = session.createQuery(sb.toString())
        .setLong("sourceId", sourceId)
        .setParameter("status", PushMessageReceiverStatus.UNREAD);
    if (shopId != null) {
      query.setLong("shopId", shopId);
    }
    if (!ArrayUtil.isEmpty(pushMessageSourceType)) {
      query.setParameterList("pushMessageSourceType",pushMessageSourceType);
    }
    if(pushMessageReceiverShopId!=null){
      query.setLong("pushMessageReceiverShopId",pushMessageReceiverShopId);
    }
    return query;
  }
  public static Query getPushMessageAndReceiverBySourceId(Session session, Long shopId, Long sourceId,Long pushMessageReceiverShopId, PushMessageSourceType... pushMessageSourceType) {
    StringBuilder sb = new StringBuilder();
    sb.append("select p,pmr from PushMessage p,PushMessageReceiver pmr ,PushMessageSource pms ");
    sb.append("where p.id = pms.messageId and p.id = pmr.messageId and p.deleted=:deleted and pmr.status =:status and pms.sourceId=:sourceId");
    if (shopId != null) {
      sb.append(" and pms.shopId=:shopId ");
    }
    if (!ArrayUtil.isEmpty(pushMessageSourceType)) {
      sb.append(" and pms.type in (:pushMessageSourceType) ");
    }
    if(pushMessageReceiverShopId!=null){
      sb.append(" and pmr.shopId =:pushMessageReceiverShopId ");
    }

    Query query = session.createQuery(sb.toString())
        .setLong("sourceId", sourceId)
        .setParameter("status", PushMessageReceiverStatus.UNREAD)
        .setParameter("deleted", DeletedType.FALSE);
    if(pushMessageReceiverShopId!=null){
      query.setLong("pushMessageReceiverShopId", pushMessageReceiverShopId);
    }
    if (!ArrayUtil.isEmpty(pushMessageSourceType)) {
      query.setParameterList("pushMessageSourceType",pushMessageSourceType);
    }
    if (shopId != null) {
      query.setLong("shopId", shopId);
    }
    return query;
  }

  public static Query getUnreadPushMessageReceiver(Session session, Long receiverShopId,Long receiverId, PushMessageType... pushMessageTypes) {
    Query q = session.createQuery("select pmr from PushMessage pm,PushMessageReceiver pmr where pm.id=pmr.messageId and pm.type in(:pushMessageTypes) and pmr.receiverId in (:receiverIds) and pmr.status =:status and pmr.shopId=:receiverShopId")
        .setParameter("status", PushMessageReceiverStatus.UNREAD)
        .setParameterList("pushMessageTypes", pushMessageTypes).setLong("receiverShopId", receiverShopId).setParameterList("receiverIds",new Long[]{receiverShopId,receiverId});

    return q;
  }

  public static Query getPushMessageByCreatorId(Session session, Long creatorId, PushMessageType pushMessageType) {
    return session.createQuery("from PushMessage  where type=:pushMessageType and creatorId=:creatorId")
        .setParameter("pushMessageType", pushMessageType).setLong("creatorId", creatorId);
  }

  public static Query getPushMessageByType(Session session, PushMessageType pushMessageType) {
    return session.createQuery("from PushMessage  where type=:pushMessageType ")
        .setParameter("pushMessageType", pushMessageType);
  }

  public static Query getPushMessageReceiverByMessageId(Session session, Long shopId,PushMessageReceiverStatus status, Long... messageIds) {
    String hql = "from PushMessageReceiver where messageId in(:messageIds)";
    if(shopId!=null){
      hql += " and shopId =:shopId ";
    }
    if (status!=null) {
      hql += " and status =:status ";
    }
    Query q = session.createQuery(hql).setParameterList("messageIds", messageIds);
    if (status!=null) {
      q.setParameter("status", status);
    }
    if(shopId!=null){
      q.setParameter("shopId", shopId);
    }
    return q;
  }

  public static Query getPushMessageBySourceId(Session session, Long sourceShopId, Long sourceId,PushMessageType pushMessageType, PushMessageSourceType pushMessageSourceType) {
    String hql = "select pm from PushMessage pm,PushMessageSource pms where pm.id=pms.messageId ";//PushMessageSource
    if (pushMessageType != null) {
      hql += " and pm.type =(:pushMessageType)  ";
    }
    if (pushMessageSourceType != null) {
      hql += " and pms.type =(:pushMessageSourceType)  ";
    }
    if (sourceShopId != null) {
      hql += " and pms.shopId=:sourceShopId ";
    }
    if (sourceId != null) {
      hql += " and pms.sourceId=:sourceId ";
    }
    Query query= session.createQuery(hql);
    if (pushMessageType != null) {
      query.setParameter("pushMessageType", pushMessageType);
    }
    if (pushMessageSourceType != null) {
      query.setParameter("pushMessageSourceType", pushMessageSourceType);
    }
    if (sourceShopId != null) {
      query.setParameter("sourceShopId", sourceShopId);
    }
    if (sourceId != null) {
      query.setLong("sourceId", sourceId);
    }
    return query;
  }

  public static Query getAllPushMessage(Session session) {
    return session.createQuery("from PushMessage");
  }
  public static Query getMovePushMessage(Session session,PushMessageType[] pushMessageTypes,Long keepDate, Long startId, int pageSize) {
    return session.createQuery("from PushMessage where type in(:types) and (createTime >:keepDate or deleted=:deleted) and id >:startId").setParameterList("types",pushMessageTypes).setParameter("deleted",DeletedType.TRUE).setLong("keepDate",keepDate)
        .setLong("startId",startId).setMaxResults(pageSize);
  }

  public static Query getMovePushMessageReceiver(Session session, Long startId, int pageSize) {
    return session.createQuery("select pmr from PushMessageReceiver pmr where (pmr.status =:status or pmr.messageId not in ( select pm.id from PushMessage pm )) and id >:startId")
        .setParameter("status", PushMessageReceiverStatus.DISABLED)
        .setLong("startId",startId).setMaxResults(pageSize);
  }

  public static Query getUnReadPushMessageByReceiverId(Session session, Long receiverId, int limit, PushMessageType... types) {
    return session.createQuery("select pm from PushMessageReceiver pmr,PushMessage pm where pm.id=pmr.messageId and pmr.status =:status and pmr.receiverId =:receiverId and pm.type in (:types)" +
      " order by pm.createTime desc")
        .setParameter("status", PushMessageReceiverStatus.UNREAD)
        .setParameterList("types",types)
        .setLong("receiverId", receiverId).setMaxResults(limit);
  }

  public static Query getAppUserUnReadPushMessage(Session session, int limit, PushMessageType... types) {
    String sql="select pmr.*,pm.* from push_message_receiver pmr " +
           "left join push_message as pm on pm.id=pmr.message_id " +
           "left join bcuser.app_user u on pmr.receiver_id=u.id " ;
//           "where u.user_type=:userType  ";
//    and   pmr.status =:status and pmr.receiver_type =:receiverType and  pm.type in (:types)
     return session.createSQLQuery(sql)
       .addEntity(PushMessageReceiver.class)
       .addEntity(PushMessage.class)
//       .setParameter("status", PushMessageReceiverStatus.UNREAD)
//         .setParameter("userType", AppUserType.MIRROR)
//       .setParameter("receiverType", OperatorType.APP_USER)
//       .setParameterList("types", types)
       .setMaxResults(limit);
   }

  public static Query getMovePushMessageFeedbackRecord(Session session, Long startId, int pageSize) {
    return session.createQuery("select record from PushMessageFeedbackRecord record where record.messageId not in ( select pm.id from PushMessage pm ) and id >:startId")
        .setLong("startId",startId).setMaxResults(pageSize);
  }

  /**
   * @param session
   * @return
   */
  public static Query getLatestPushMessageBuildTask(Session session,PushMessageScene... scene) {
    return session.createQuery("from PushMessageBuildTask where exeStatus =:exeStatus and scene in(:scene)  order by createTime")
        .setParameter("exeStatus", ExeStatus.READY).setParameterList("scene", scene).setMaxResults(1);
  }

  /**
   * @param session
   * @param shopId
   * @param sourceId
   * @param pushMessageSourceType
   * @return
   */
  public static Query getPushMessageIdsBySource(Session session,Long shopId,Long sourceId,PushMessageSourceType pushMessageSourceType) {
    StringBuffer sb = new StringBuffer("select distinct pm.id from PushMessage pm,PushMessageReceiver pmr,PushMessageSource pms where pm.id=pms.messageId and pm.id=pmr.messageId ");
    sb.append(" pmr.status =:status and pms.sourceId=:sourceId and pms.shopId=:shopId and pms.type=:pushMessageSourceType");//PushMessageSource

    Query query = session.createQuery(sb.toString())
        .setLong("shopId", shopId)
        .setLong("sourceId",sourceId)
        .setParameter("status", PushMessageReceiverStatus.UNREAD)
        .setParameter("pushMessageSourceType", pushMessageSourceType);
    return query;
  }

  public static Query getPushMessageSourceBySourceId(Session session, Long sourceId) {
    Query query = session.createQuery("from PushMessageSource where sourceId=:sourceId")
        .setLong("sourceId", sourceId);
    return query;
  }

  public static Query getPushMessageSourcesByMessageIds(Session session, Set<Long> messageIds) {
    Query query = session.createQuery("from PushMessageSource where messageId in (:messageIds)")
        .setParameterList("messageIds", messageIds);
    return query;
  }


  public static Query getMoveProductRecommendList(Session session,int start, int pageSize) {
    Query query = session.createQuery("from ProductRecommend where deleted=:deleted")
        .setParameter("deleted", DeletedType.TRUE)
        .setFirstResult(start)
        .setMaxResults(pageSize);
    return query;
  }

  public static Query getMovePreBuyOrderItemRecommendList(Session session,int start, int pageSize) {
    Query query = session.createQuery("from PreBuyOrderItemRecommend where deleted=:deleted")
        .setParameter("deleted", DeletedType.TRUE)
        .setFirstResult(start)
        .setMaxResults(pageSize);
    return query;
  }

  public static Query getMoveShopRecommendList(Session session,int start, int pageSize) {
    Query query = session.createQuery("from ShopRecommend where deleted=:deleted")
        .setParameter("deleted", DeletedType.TRUE)
        .setFirstResult(start)
        .setMaxResults(pageSize);
    return query;
  }

  public static Query getMovePushMessageReceiverRecordListByPushTime(Session session,Long pushTime,int start, int pageSize) {
    Query query = session.createQuery("from PushMessageReceiverRecord where pushTime<:pushTime")
        .setLong("pushTime", pushTime)
        .setFirstResult(start)
        .setMaxResults(pageSize);
    return query;
  }


  public static Query getLastWeekSalesByShopId(Session session, Long shopId, long startTime,long endTime,Long... productId ) {
    StringBuffer sb = new StringBuffer(" select productId, sum(amount)  from SalesStat where shopId=:shopId and statTime<:endTime and statTime>=:startTime ");
    if(!ArrayUtils.isEmpty(productId)){
      sb.append(" and productId in(:productId) ");
    }
    sb.append(" group by productId ");
    Query query = session.createQuery(sb.toString());
    query.setLong("shopId", shopId).setLong("endTime", endTime).setLong("startTime", startTime);
    if(!ArrayUtils.isEmpty(productId)){
      query.setParameterList("productId", productId);
    }
    return query;
  }

  public static Query getLastWeekSalesChangeByShopId(Session session, Long shopId, long startTime,long endTime,Long... productId ) {
    StringBuffer sb = new StringBuffer("select productId, sum(amount)  from SalesStatChange where shopId=:shopId and statTime<:endTime and statTime>=:startTime ");
    if(!ArrayUtils.isEmpty(productId)){
      sb.append(" and productId in(:productId) ");
    }
    sb.append(" group by productId ");
    Query query = session.createQuery(sb.toString());
    query.setLong("shopId", shopId).setLong("endTime", endTime).setLong("startTime", startTime);
    if(!ArrayUtils.isEmpty(productId)){
      query.setParameterList("productId", productId);
    }
    return query;
  }

  public static Query getLastWeekInventoryByShopId(Session session, Long shopId, long startTime,long endTime ) {
    return session.createQuery(" select productId, sum(amount)  from PurchaseInventoryStat where shopId=:shopId and statTime<:endTime and statTime>=:startTime  group by productId ")
        .setLong("shopId", shopId).setLong("endTime", endTime).setLong("startTime", startTime);
  }

  public static Query getLastWeekInventoryChangeByShopId(Session session, Long shopId, long startTime,long endTime ) {
    return session.createQuery(" select productId, sum(amount)  from PurchaseInventoryStatChange where shopId=:shopId and statTime<:endTime and statTime>=:startTime  group by productId ")
        .setLong("shopId", shopId).setLong("endTime", endTime).setLong("startTime", startTime);
  }

  public static Query getSalesInventoryWeekStatByCondition(Session session, int statYear,int statMonth,int statDay, ShopKind shopKind, String productName, String productBrand) {
    if (StringUtil.isEmpty(productBrand)) {
      return session.createQuery(" from SalesInventoryWeekStat where statMonth=:statMonth and statYear=:statYear and statDay=:statDay " +
          " and shopKind=:shopKind and productName=:productName and productBrand is null order by statTime desc ")
          .setInteger("statMonth", statMonth).setInteger("statYear", statYear).setInteger("statDay", statDay).setParameter("shopKind", shopKind).setString("productName", productName);
    }
    return session.createQuery(" from SalesInventoryWeekStat where statMonth=:statMonth and statYear=:statYear and statDay=:statDay  " +
        " and shopKind=:shopKind and productName=:productName and productBrand=:productBrand order by statTime desc ")
        .setInteger("statMonth", statMonth).setInteger("statYear", statYear).setInteger("statDay", statDay).setParameter("shopKind", shopKind).setString("productName", productName).setString("productBrand", productBrand);
  }


  public static Query deleteOldProductRecommend(Session session, Long shopId,ProductRecommendType productRecommendType) {
    return session.createQuery("update ProductRecommend set deleted='"+DeletedType.TRUE+"' where shopId=:shopId and productRecommendType=:productRecommendType and deleted='"+DeletedType.FALSE+"'")
        .setLong("shopId", shopId).setParameter("productRecommendType",productRecommendType);
  }


  public static Query deleteOldPreBuyOrderItemRecommend(Session session, Long shopId) {
    return session.createQuery("update PreBuyOrderItemRecommend set deleted='"+DeletedType.TRUE+"' where shopId=:shopId and deleted='"+DeletedType.FALSE+"'")
        .setLong("shopId", shopId);
  }

  public static Query deleteOldShopRecommend(Session session, Long shopId) {
    return session.createQuery("update ShopRecommend set deleted='"+DeletedType.TRUE+"' where shopId=:shopId and deleted='"+DeletedType.FALSE+"'")
        .setLong("shopId", shopId);
  }

  /**
   * @param session
   * @param messageId
   * @param pushMessageSourceType
   * @return
   */
  public static Query getPushMessageSourceByMessageId(Session session,Long messageId,PushMessageSourceType pushMessageSourceType) {
    StringBuffer sb = new StringBuffer("from PushMessageSource pms where pms.messageId=:messageId and pms.type=:pushMessageSourceType");

    Query query = session.createQuery(sb.toString())
        .setLong("messageId",messageId)
        .setParameter("pushMessageSourceType", pushMessageSourceType);
    return query;
  }

  public static Query getRecommendShopByShopId(Session session, Long shopId,Pager pager) {
    Query query= session.createQuery("  from ShopRecommend where shopId=:shopId and deleted =:deleted order by customScore desc ")
      .setLong("shopId", shopId).setParameter("deleted", DeletedType.FALSE);
    if(pager != null) {
      query.setFirstResult(pager.getRowStart()).setMaxResults(pager.getPageSize());
    }
    return query;
  }


  public static Query countProductRecommendByShopId(Session session,Long shopId,DeletedType deletedType) {
    Query query = session.createQuery(" select count(*) from ProductRecommend where shopId=:shopId and deleted =:deleted ")
        .setLong("shopId", shopId).setParameter("deleted", deletedType);
    return query;
  }

  public static Query getRecommendProductByShopId(Session session,Long shopId,DeletedType deletedType,int start, int rows) {
    Query query = session.createQuery(" from ProductRecommend where shopId=:shopId and deleted =:deleted ")
        .setLong("shopId", shopId).setParameter("deleted", deletedType)
        .setFirstResult(start)
        .setMaxResults(rows);;
    return query;
  }

  public static Query getRecommendProduct(Session session,ProductSearchCondition condition) {
    StringBuilder sb=new StringBuilder();
    sb.append("from ProductRecommend where shopId=:shopId and deleted =:deleted");
    if(condition.getProductRecommendType()!=null){
      sb.append(" and productRecommendType=:productRecommendType");
    }
    sb.append(" order by customScore desc");
    Query query = session.createQuery(sb.toString())
      .setLong("shopId", condition.getShopId()).setParameter("deleted", DeletedType.FALSE);
    if(condition.getProductRecommendType()!=null){
      query.setString("productRecommendType",condition.getProductRecommendType().toString());
    }
    if (condition.getLimit()!=null) {
      query.setFirstResult(condition.getStart()).setMaxResults(condition.getLimit());
    }
    return query;
  }

  public static Query countWholesalerProductRecommendByShopId(Session session,Long shopId,DeletedType deletedType) {
    Query query = session.createQuery(" select count(*) from PreBuyOrderItemRecommend where shopId=:shopId and deleted =:deleted ")
        .setLong("shopId", shopId).setParameter("deleted", deletedType);
    return query;
  }

  public static Query getWholesalerProductRecommendByPager(Session session,Long shopId,DeletedType deletedType,Pager pager) {
    Query query = session.createQuery(" from PreBuyOrderItemRecommend where shopId=:shopId and deleted =:deleted order by customScore desc  ")
        .setLong("shopId", shopId).setParameter("deleted", deletedType);

    if (pager != null) {
      query.setFirstResult(pager.getRowStart()).setMaxResults(pager.getPageSize());
    }

    return query;
  }

  public static Query getPreBuyOrderItemsByIdSet(Session session,Set<Long> idSet) {
    StringBuffer sb = new StringBuffer();
    sb.append("from PreBuyOrderItem pi where pi.id in (:idSet) ");
    Query q = session.createQuery(sb.toString()).setParameterList("idSet", idSet);
    return q;
  }

  public static Query countLastWeekSalesInventoryStatByShopId(Session session,Long shopId,int weekOfYear) {
    Query query = session.createQuery(" select count(*) from ShopProductMatchResult where shopId=:shopId and weekOfYear =:weekOfYear ")
        .setLong("shopId", shopId).setInteger("weekOfYear", weekOfYear);
    return query;
  }
  public static Query getLastMonthSalesInventoryStatByShopId(Session session,Long shopId,int statYear,int statMonth,int statDay,Pager pager) {
    Query query = session.createQuery(" from ShopProductMatchResult where shopId=:shopId and statYear =:statYear and statMonth =:statMonth and statDay =:statDay order by productName desc ")
        .setLong("shopId", shopId).setInteger("statYear", statYear).setInteger("statMonth", statMonth).setInteger("statDay", statDay);
    if (pager != null) {
      query.setFirstResult(pager.getRowStart()).setMaxResults(pager.getPageSize());
    }

    return query;
  }

  public static Query getSupplierPayable(Session session) {
      String sql = "select * from payable where order_type <>:orderType and credit_amount <> 0 and  id not in (select p.id  from remind_event re join payable p on p.purchase_inventory_id = re.order_id where re.remind_status <> \'canceled\' and p.credit_amount  <> 0)";
      return  session.createSQLQuery(sql).addEntity(Payable.class).setParameter("orderType",OrderTypes.INVENTORY.toString());

  }
  public static Query getActivePreBuyOrderItemList(Session session, Long shopId, Set<String> productNames) throws Exception {
     return session.createQuery("select i from PreBuyOrderItem i, PreBuyOrder o where o.shopId=:shopId and " +
         " o.id=i.preBuyOrderId and i.productName in(:productNames) and o.deleted !=:deleted and o.endDate >=:endDate")
         .setLong("shopId", shopId)
         .setParameterList("productNames", productNames)
         .setParameter("deleted",DeletedType.TRUE)
         .setLong("endDate",DateUtil.getTheDayTime());
  }

    public static Query updateRemindEventStatus(Session session, Long shopId, Long customerOrSupplierId, String identity) {
        StringBuffer sb = new StringBuffer();
        sb.append("update RemindEvent set remindStatus =:remindStatus where shopId =:shopId and eventType =:eventType and remindStatus <>:remindStatus2");
          if("customer".equals(identity)) {
              sb.append(" and customerId =:customerId");
          } else if("supplier".equals(identity)) {
              sb.append(" and supplierId =:supplierId");
          }
       Query query =  session.createQuery(sb.toString()).setString("remindStatus",PlansRemindStatus.activity.toString()).setLong("shopId",shopId)
                             .setString("eventType",RemindEventType.DEBT.toString()).setString("remindStatus2",PlansRemindStatus.canceled.toString());
        if("customer".equals(identity)) {
            query.setLong("customerId",customerOrSupplierId);
        } else if("supplier".equals(identity)) {
            query.setLong("supplierId",customerOrSupplierId);
        }
       return query;
    }

  public static Query getPreBuyOrderListByShopIdByPage(Session session, Long shopId,int pageStart,int pageSize) {
    String sql = " from PreBuyOrder where shopId = :shopId and deleted =:deleteType order by vestDate desc ";
    return session.createQuery(sql).setLong("shopId", shopId).setParameter("deleteType",DeletedType.FALSE).setFirstResult(pageStart).setMaxResults(pageSize);
  }

  public static Query getUnsettledSalesReturnByCustomerId(Session session, Long shopId, Long customerId) {
    StringBuffer sb = new StringBuffer();
    sb.append("from SalesReturn s where s.shopId =:shopId ");
    sb.append(" and s.status in(:status) and s.customerId = :customerId");
    return session.createQuery(sb.toString())
        .setParameterList("status", OrderUtil.salesReturnInProgress)
        .setLong("shopId", shopId)
        .setLong("customerId",customerId);
  }

  public static Query getOrderItemPromotionsByOrderItemId(Session session, Long orderItemId) {
    StringBuilder sb = new StringBuilder(" from OrderItemPromotion where orderItemId =:orderItemId ");
    return session.createQuery(sb.toString()).setLong("orderItemId", orderItemId);
  }

  public static Query getQuotedPreBuyOrderByIds(Session session, Set<Long> ids) {
    StringBuilder sb = new StringBuilder(" from QuotedPreBuyOrder where id in(:ids) ");
    return session.createQuery(sb.toString()).setParameterList("ids", ids);
  }
  public static Query getAppointOrderServiceItems(Session session, Long shopId, Long appointOrderId) {
    StringBuilder sb = new StringBuilder("from AppointOrderServiceItem where shopId =:shopId and appointOrderId =:appointOrderId and  status =:status");
    return session.createQuery(sb.toString()).setLong("shopId",shopId).setLong("appointOrderId",appointOrderId).setParameter("status", ObjectStatus.ENABLED);
  }
  public static Query getRepairOrderByCustomerId(Session session, Long customerId) {
    StringBuilder sb = new StringBuilder(" from RepairOrder where customerId =:customerId ");
    return session.createQuery(sb.toString()).setLong("customerId", customerId);
  }

  public static Query getSalesNewOrderCountBySupplierShopId(Session session, Long supplierShopId, Long startTime, Long endTime, String orderStatus, String timeField) {
    StringBuffer sb = new StringBuffer();
    sb.append("select count(s.id) from purchase_order s where ");
    if(startTime!=null){
      sb.append("s.").append(timeField).append(" >= :startTime and ");
    }
    if(endTime!=null){
      sb.append("s.").append(timeField).append(" <= :endTime and ");
    }

    sb.append("s.status_enum =:orderStatus and ");
    sb.append("s.supplier_shop_id =:supplierShopId");

    Query q = session.createSQLQuery(sb.toString()).setLong("supplierShopId",supplierShopId);
    if(startTime!=null){
      q.setLong("startTime",startTime);
    }
    if(endTime!=null){
      q.setLong("endTime",endTime);
    }

    q.setString("orderStatus",orderStatus);
    return q;
  }

  public static Query getAppointOrderById(Session session, Long id, Long shopId) {
    StringBuilder sb = new StringBuilder("from AppointOrder where shopId =:shopId and id =:id");
    return session.createQuery(sb.toString()).setLong("shopId",shopId).setLong("id",id);
  }

  public static Query getWashBeautyOrderByCustomerId(Session session, Long customerId) {
    StringBuilder sb = new StringBuilder(" from WashBeautyOrder where customerId =:customerId ");
    return session.createQuery(sb.toString()).setLong("customerId", customerId);
  }
  public static Query countAppointOrderByShopId(Session session, Long shopId) {
    return session.createQuery("select status,count(*) from AppointOrder where shopId=:shopId group by status")
        .setLong("shopId", shopId);
  }


  public static Query getRemindedAppointOrder(Session session, Long upTime, Long downTime, int start, int limit) {
    return session.createSQLQuery("select ao.* from appoint_order ao " +
        "where ao.status in (:aostatuses) and ao.appoint_time <=:upTime and ao.appoint_time >=:downTime order by ao.id asc")
        .addEntity(AppointOrder.class)
        .setLong("upTime", upTime)
        .setLong("downTime", downTime)
        .setParameterList("aostatuses", new String[]{AppointOrderStatus.PENDING.name(),AppointOrderStatus.ACCEPTED.name()})
        .setMaxResults(limit).setFirstResult(start);

//    return session.createSQLQuery("select ao.* from appoint_order ao " +
//        "where ao.status in (:aostatuses) and ao.appoint_time <=:upTime and ao.appoint_time >=:downTime " +
//        "and ao.id not in (select distinct(pms.source_id) from push_message_source pms,push_message_receiver pmr where pms.type in (:pmstyps) and pmr.message_id = pms.message_id and pmr.status=:pmrstatus" +
//        "and ) order by ao.id asc")
//        .addEntity(AppointOrder.class)
//        .setLong("upTime", upTime)
//        .setLong("downTime", downTime)
//        .setParameterList("pmrstatus", new String[]{PushMessageStatus.UN_PUSH.name()})
//        .setParameterList("aostatuses", new String[]{"PENDING", "ACCEPTED"})
//        .setParameterList("pmstyps", new String[]{"OVERDUE_APPOINT_TO_APP","SOON_EXPIRE_APPOINT_TO_APP", "OVERDUE_APPOINT_TO_SHOP", "SOON_EXPIRE_APPOINT_TO_SHOP"})
  }

  public static Query countOverdueAndSoonExpireAppointOrderByShopId(Session session, Long upTime, Long downTime,Long shopId) {
    return session.createSQLQuery("select count(ao.id) as number from appoint_order ao where ao.status in (:aostatuses) and ao.appoint_time <=:upTime and ao.appoint_time >=:downTime and ao.shop_id=:shopId")
        .addScalar("number",StandardBasicTypes.LONG)
        .setLong("upTime", upTime)
        .setLong("downTime", downTime)
        .setLong("shopId", shopId)
        .setParameterList("aostatuses", new String[]{AppointOrderStatus.PENDING.name(),AppointOrderStatus.ACCEPTED.name()});
  }

  public static Query countAppointOrderByAppUserNoStatus(Session session, String appUserNo) {

    StringBuilder sb = new StringBuilder();
    sb.append("SELECT sum(st.num) FROM (");
    sb.append(" (SELECT count(DISTINCT ao.id) as num");
    sb.append(" FROM appoint_order ao LEFT JOIN bcuser.app_user_customer auc ON ao.customer_id = auc.customer_id");
    sb.append(" where (auc.app_user_no =:appUserNo OR ao.app_user_no =:appUserNo ) ");
    sb.append(" AND ao.status in(:appointOrderStatus) )");
    sb.append(" UNION ALL ");
    sb.append(" (SELECT count(DISTINCT ro.id) as num ");
    sb.append(" FROM repair_order ro LEFT JOIN bcuser.app_user_customer auc ON ro.customer_id = auc.customer_id AND ro.shop_id = auc.shop_id");
    sb.append(" WHERE auc.app_user_no =:appUserNo AND ro.status_enum in(:repairStatus) )) AS st");
    Set<String> appointOrderStatus = new HashSet<String>();
    appointOrderStatus.add(AppointOrderStatus.PENDING.name());
    appointOrderStatus.add(AppointOrderStatus.ACCEPTED.name());
    appointOrderStatus.add(AppointOrderStatus.TO_DO_REPAIR.name());
    Set<String> repairStatus = new HashSet<String>();
    repairStatus.add(OrderStatus.REPAIR_DISPATCH.name());
    repairStatus.add(OrderStatus.REPAIR_DONE.name());
    return session.createSQLQuery(sb.toString())
        .setString("appUserNo", appUserNo)
        .setParameterList("appointOrderStatus", appointOrderStatus)
        .setParameterList("repairStatus", repairStatus);
  }

  public static Query getAppointRepairByAppUserNoStatus(Session session, String appUserNo, Pager pager) {

    StringBuilder sb = new StringBuilder();
    sb.append(" (SELECT DISTINCT ao.id,ao.created as vest_date ,'APPOINT_ORDER'as type,ao.shop_id,status as status ");
    sb.append(" FROM appoint_order ao LEFT JOIN bcuser.app_user_customer auc ON ao.customer_id = auc.customer_id");
    sb.append(" where (auc.app_user_no =:appUserNo OR ao.app_user_no =:appUserNo ) ");
    sb.append(" AND ao.status in(:appointOrderStatus) )");
    sb.append(" UNION ALL ");
    sb.append(" (SELECT DISTINCT ro.id,ro.created AS vest_date,'REPAIR' AS type,ro.shop_id,ro.status_enum AS status ");
    sb.append(" FROM repair_order ro LEFT JOIN bcuser.app_user_customer auc ON ro.customer_id = auc.customer_id AND ro.shop_id = auc.shop_id");
    sb.append(" WHERE auc.app_user_no =:appUserNo AND ro.status_enum in(:repairStatus) )");
    sb.append(" ORDER BY vest_date DESC ");
    Set<String> appointOrderStatus = new HashSet<String>();
    appointOrderStatus.add(AppointOrderStatus.PENDING.name());
    appointOrderStatus.add(AppointOrderStatus.ACCEPTED.name());
    appointOrderStatus.add(AppointOrderStatus.TO_DO_REPAIR.name());
    Set<String> repairStatus = new HashSet<String>();
    repairStatus.add(OrderStatus.REPAIR_DISPATCH.name());
    repairStatus.add(OrderStatus.REPAIR_DONE.name());
    return session.createSQLQuery(sb.toString())
        .setString("appUserNo", appUserNo)
        .setParameterList("appointOrderStatus", appointOrderStatus)
        .setParameterList("repairStatus", repairStatus)
        .setFirstResult(pager.getRowStart())
        .setMaxResults(pager.getPageSize());
  }


  public static Query getAppointOrderByPager(Session session, String appUserNo,Set<AppointOrderStatus> appointOrderStatuses,Pager pager) {
    StringBuilder sb = new StringBuilder(" from AppointOrder where appUserNo =:appUserNo and status in(:status) order by appointTime desc ");
    return session.createQuery(sb.toString()).setString("appUserNo", appUserNo).setParameterList("status", appointOrderStatuses).setMaxResults(pager.getPageSize()).setFirstResult(pager.getRowStart());
  }

  public static Query countWashRepairByAppUserNoStatus(Session session, String appUserNo, String washSettled,String repairSettled) {

    StringBuilder sb = new StringBuilder();
    sb.append("SELECT sum(st.num),sum(st.total) FROM (");
    sb.append(" (SELECT count(DISTINCT wbo.id) as num,sum(wbo.total) as total ");
    sb.append(" FROM wash_beauty_order wbo LEFT JOIN bcuser.app_user_customer auc ");
    sb.append(" ON wbo.customer_id = auc.customer_id AND wbo.shop_id = auc.shop_id");
    sb.append(" where auc.app_user_no =:appUserNo ");
    sb.append(" AND wbo.status =:washSettled )");
    sb.append(" UNION ALL ");
    sb.append(" (SELECT count(DISTINCT ro.id) as num,sum(ro.total) as total ");
    sb.append(" FROM repair_order ro LEFT JOIN bcuser.app_user_customer auc ON ro.customer_id = auc.customer_id AND ro.shop_id = auc.shop_id");
    sb.append(" WHERE auc.app_user_no =:appUserNo AND ro.status_enum =:repairSettled )) as st");

    return session.createSQLQuery(sb.toString())
        .setString("appUserNo", appUserNo)
        .setParameter("washSettled", washSettled)
        .setParameter("repairSettled", repairSettled);

  }

  public static Query getWashRepairByPagerAppUserNo(Session session, String appUserNo, String washSettled,String repairSettled, Pager pager) {

    StringBuilder sb = new StringBuilder();
    sb.append(" (SELECT DISTINCT wbo.id,wbo.created as vest_date ,'WASH_BEAUTY'as type,wbo.shop_id,wbo.total ");
    sb.append(" FROM wash_beauty_order wbo LEFT JOIN bcuser.app_user_customer auc ");
    sb.append(" ON wbo.customer_id = auc.customer_id AND wbo.shop_id = auc.shop_id");
    sb.append(" where auc.app_user_no =:appUserNo ");
    sb.append(" AND wbo.status =:washSettled )");
    sb.append(" UNION ALL ");
    sb.append(" (SELECT DISTINCT ro.id,ro.vest_date,'REPAIR' as type,ro.shop_id,ro.total ");
    sb.append(" FROM repair_order ro LEFT JOIN bcuser.app_user_customer auc ON ro.customer_id = auc.customer_id AND ro.shop_id = auc.shop_id");
    sb.append(" WHERE auc.app_user_no =:appUserNo AND ro.status_enum =:repairSettled )");
    sb.append(" ORDER BY vest_date DESC");

    return session.createSQLQuery(sb.toString())
        .setString("appUserNo", appUserNo)
        .setParameter("washSettled", washSettled)
        .setParameter("repairSettled", repairSettled)
        .setFirstResult(pager.getRowStart())
        .setMaxResults(pager.getPageSize());
  }

  public static Query getAppointOrderServiceItemsByOrderIds(Session session, Set<Long> orderIds) {
    StringBuilder sb = new StringBuilder("from AppointOrderServiceItem where appointOrderId in(:appointOrderId) and  status =:status");
    return session.createQuery(sb.toString()).setParameterList("appointOrderId", orderIds).setParameter("status", ObjectStatus.ENABLED);
  }


  public static Query searchAppointOrders(Session session, AppointOrderSearchCondition searchCondition) {
    Set<String> appointOrderStatusStr = new HashSet<String>();
    if(!ArrayUtils.isEmpty(searchCondition.getAppointOrderStatus())){
      for(AppointOrderStatus appointOrderStatus : searchCondition.getAppointOrderStatus()){
        if(appointOrderStatus != null){
          appointOrderStatusStr.add(appointOrderStatus.toString());
        }
      }
    }
    StringBuilder sb = new StringBuilder();
    sb.append("select distinct a.* from appoint_order a left join appoint_order_service_item ai ");
    sb.append("on a.id = ai.appoint_order_id ");
    sb.append(" where a.shop_id =:shopId and ai.status =:itemStatus ");
    if (StringUtils.isNotBlank(searchCondition.getCustomerSearchWord())) {
      sb.append(" and (a.customer like :customerName");
      if (searchCondition.getCustomerSearchWord().matches("^[0-9]+$")) {
        sb.append(" or a.customer_mobile like :customerMobile");
      }
      if(!ArrayUtils.isEmpty(searchCondition.getAppUserNos())){
        sb.append(" or a.app_user_no in (:appUserNos) ");
      }
//      if(!ArrayUtils.isEmpty(searchCondition.getCustomers())){
//        sb.append(" or a.customer in (:customerNames) ");
//      }
      sb.append(")");
    }
    if(StringUtils.isNotBlank(searchCondition.getReceiptNo())){
      sb.append(" and a.receipt_no like :receiptNo");
    }
    if(StringUtils.isNotBlank(searchCondition.getVehicleNo())){
      sb.append(" and a.vehicle_no like :vehicleNo");
    }
    if(searchCondition.getAppointWay() != null){
      sb.append(" and a.appoint_way =:appointWay");
    }
    if(CollectionUtils.isNotEmpty(appointOrderStatusStr)){
      sb.append(" and a.status in(:status)");
    }
    if(searchCondition.getCreateTimeStart() != null){
      sb.append(" and a.create_time >=:createTimeStart");
    }
    if(searchCondition.getCreateTimeEnd() != null){
      sb.append(" and a.create_time <=:createTimeEnd");
    }
    if(searchCondition.getAppointTimeStart() != null){
      sb.append(" and a.appoint_time >=:appointTimeStart");
    }
    if(searchCondition.getAppointTimeEnd() != null){
      sb.append(" and a.appoint_time <=:appointTimeEnd");
    }
    if (!ArrayUtils.isEmpty(searchCondition.getCustomerIds())) {
      sb.append(" and (");
      sb.append(" a.customer_id in(:customerIds)");
      if (!ArrayUtils.isEmpty(searchCondition.getAppUserNos())) {
        sb.append(" or a.customer_id is null and a.app_user_no in (:appUserNos)");
      }
      sb.append(" ) ");
    }

    if(!ArrayUtils.isEmpty(searchCondition.getServiceCategoryIds())){
      sb.append(" and ai.service_id in(:serviceCategoryIds)");
    }
    sb.append(" ORDER BY FIELD(a.STATUS,'PENDING','ACCEPTED','TO_DO_REPAIR','HANDLED','CANCELED','REFUSED'),a.appoint_time asc");
    Query query = session.createSQLQuery(sb.toString()).addEntity(AppointOrder.class)
        .setParameter("shopId",searchCondition.getShopId())
        .setParameter("itemStatus",ObjectStatus.ENABLED.toString());
    if (StringUtils.isNotBlank(searchCondition.getCustomerSearchWord())) {
      query.setParameter("customerName", "%" + searchCondition.getCustomerSearchWord() + "%");
      if (searchCondition.getCustomerSearchWord().matches("^[0-9]+$")) {
        query.setParameter("customerMobile", "%" + searchCondition.getCustomerSearchWord() + "%");
      }
      if(!ArrayUtils.isEmpty(searchCondition.getAppUserNos())){
        query.setParameterList("appUserNos", searchCondition.getAppUserNos());
      }
//      if(!ArrayUtils.isEmpty(searchCondition.getCustomers())){
//        query.setParameterList("customerNames", searchCondition.getCustomers());
//      }
    }
      if(StringUtils.isNotBlank(searchCondition.getReceiptNo())){
        query.setParameter("receiptNo", "%" + searchCondition.getReceiptNo() + "%");
      }
      if(StringUtils.isNotBlank(searchCondition.getVehicleNo())){
        query.setParameter("vehicleNo", "%" + searchCondition.getVehicleNo() + "%");
      }
      if(searchCondition.getAppointWay() != null){
        query.setParameter("appointWay", searchCondition.getAppointWay().toString());
      }
      if(CollectionUtils.isNotEmpty(appointOrderStatusStr)){
        query.setParameterList("status", appointOrderStatusStr);
      }
      if(searchCondition.getCreateTimeStart() != null){
        query.setParameter("createTimeStart", searchCondition.getCreateTimeStart());
      }
      if(searchCondition.getCreateTimeEnd() != null){
        query.setParameter("createTimeEnd", searchCondition.getCreateTimeEnd());
      }
      if(searchCondition.getAppointTimeStart() != null){
        query.setParameter("appointTimeStart", searchCondition.getAppointTimeStart());
      }
      if(searchCondition.getAppointTimeEnd() != null){
        query.setParameter("appointTimeEnd", searchCondition.getAppointTimeEnd());
      }
      if(!ArrayUtils.isEmpty(searchCondition.getCustomerIds())){
        query.setParameterList("customerIds", searchCondition.getCustomerIds());
        if (!ArrayUtils.isEmpty(searchCondition.getAppUserNos())) {
          query.setParameterList("appUserNos", searchCondition.getAppUserNos());
        }
      }
      if(!ArrayUtils.isEmpty(searchCondition.getServiceCategoryIds())){
        query.setParameterList("serviceCategoryIds", searchCondition.getServiceCategoryIds());
      }
    query.setMaxResults(searchCondition.getMaxRows()).setFirstResult((searchCondition.getStartPageNo() - 1) * searchCondition.getMaxRows());
    return query;
  }

  public static Query countAppointOrders(Session session, AppointOrderSearchCondition searchCondition) {
    Set<String> appointOrderStatusStr = new HashSet<String>();
    if (!ArrayUtils.isEmpty(searchCondition.getAppointOrderStatus())) {
      for (AppointOrderStatus appointOrderStatus : searchCondition.getAppointOrderStatus()) {
        if (appointOrderStatus != null) {
          appointOrderStatusStr.add(appointOrderStatus.toString());
        }
      }
    }
    StringBuilder sb = new StringBuilder();
    sb.append("select count(distinct a.id) as count from appoint_order a left join appoint_order_service_item ai ");
    sb.append("on a.id = ai.appoint_order_id ");
    sb.append(" where a.shop_id =:shopId and ai.status =:itemStatus ");
    if (StringUtils.isNotBlank(searchCondition.getCustomerSearchWord())) {
      sb.append(" and (a.customer like :customerName ");
      if (searchCondition.getCustomerSearchWord().matches("^[0-9]+$")) {
        sb.append(" or a.customer_mobile like :customerMobile ");
      }
      if(!ArrayUtils.isEmpty(searchCondition.getAppUserNos())){
        sb.append(" or a.app_user_no in (:appUserNos) ");
      }
//      if(!ArrayUtils.isEmpty(searchCondition.getCustomers())){
//        sb.append(" or a.customer in (:customerNames) ");
//      }
      sb.append(")");
    }
    if(StringUtils.isNotBlank(searchCondition.getReceiptNo())){
      sb.append(" and a.receipt_no like :receiptNo");
    }
    if(StringUtils.isNotBlank(searchCondition.getVehicleNo())){
      sb.append(" and a.vehicle_no like :vehicleNo");
    }
    if(searchCondition.getAppointWay() != null){
      sb.append(" and a.appoint_way =:appointWay");
    }
    if(CollectionUtils.isNotEmpty(appointOrderStatusStr)){
      sb.append(" and a.status in(:status) ");
    }
    if(searchCondition.getCreateTimeStart() != null){
      sb.append(" and a.create_time >=:createTimeStart");
    }
    if(searchCondition.getCreateTimeEnd() != null){
      sb.append(" and a.create_time <=:createTimeEnd");
    }
    if(searchCondition.getAppointTimeStart() != null){
      sb.append(" and a.appoint_time >=:appointTimeStart");
    }
    if(searchCondition.getAppointTimeEnd() != null){
      sb.append(" and a.appoint_time <=:appointTimeEnd");
    }
    if(!ArrayUtils.isEmpty(searchCondition.getCustomerIds())){
      sb.append(" and (");
      sb.append(" a.customer_id in (:customerIds)");
      if (!ArrayUtils.isEmpty(searchCondition.getAppUserNos())) {
        sb.append(" or a.customer_id is null and a.app_user_no in (:appUserNos)");
      }
      sb.append(" ) ");
    }
    if(!ArrayUtils.isEmpty(searchCondition.getServiceCategoryIds())){
      sb.append(" and ai.service_id in(:serviceCategoryIds)");
    }
    Query query = session.createSQLQuery(sb.toString()).addScalar("count",StandardBasicTypes.LONG)
        .setParameter("shopId",searchCondition.getShopId())
        .setParameter("itemStatus",ObjectStatus.ENABLED.toString());
    if (StringUtils.isNotBlank(searchCondition.getCustomerSearchWord())) {
      query.setParameter("customerName", "%" + searchCondition.getCustomerSearchWord() + "%");
      if (searchCondition.getCustomerSearchWord().matches("^[0-9]+$")) {
        query.setParameter("customerMobile", "%" + searchCondition.getCustomerSearchWord() + "%");
      }
      if(!ArrayUtils.isEmpty(searchCondition.getAppUserNos())){
        query.setParameterList("appUserNos", searchCondition.getAppUserNos());
      }
//      if(!ArrayUtils.isEmpty(searchCondition.getCustomers())){
//        query.setParameterList("customerNames", searchCondition.getCustomers());
//      }
    }
      if(StringUtils.isNotBlank(searchCondition.getReceiptNo())){
        query.setParameter("receiptNo", "%" + searchCondition.getReceiptNo() + "%");
      }
      if(StringUtils.isNotBlank(searchCondition.getVehicleNo())){
        query.setParameter("vehicleNo", "%" + searchCondition.getVehicleNo() + "%");
      }
      if(searchCondition.getAppointWay() != null){
        query.setParameter("appointWay", searchCondition.getAppointWay().toString());
      }
      if(CollectionUtils.isNotEmpty(appointOrderStatusStr)){
        query.setParameterList("status", appointOrderStatusStr);
      }
      if(searchCondition.getCreateTimeStart() != null){
        query.setParameter("createTimeStart", searchCondition.getCreateTimeStart());
      }
      if(searchCondition.getCreateTimeEnd() != null){
        query.setParameter("createTimeEnd", searchCondition.getCreateTimeEnd());
      }
      if(searchCondition.getAppointTimeStart() != null){
        query.setParameter("appointTimeStart", searchCondition.getAppointTimeStart());
      }
      if(searchCondition.getAppointTimeEnd() != null){
        query.setParameter("appointTimeEnd", searchCondition.getAppointTimeEnd());
      }
      if(!ArrayUtils.isEmpty(searchCondition.getCustomerIds())){
        query.setParameterList("customerIds", searchCondition.getCustomerIds());
        if (!ArrayUtils.isEmpty(searchCondition.getAppUserNos())) {
          query.setParameterList("appUserNos", searchCondition.getAppUserNos());
        }
      }
      if(!ArrayUtils.isEmpty(searchCondition.getServiceCategoryIds())){
        query.setParameterList("serviceCategoryIds", searchCondition.getServiceCategoryIds());
      }
    return query;
  }

  public static Query getAppointOrderServiceItemsByAppointOrderIds(Session session,Set<Long> appointOrderIds) {
    StringBuilder sb = new StringBuilder("from AppointOrderServiceItem where  appointOrderId in(:appointOrderIds) and  status =:status");
      return session.createQuery(sb.toString()).setParameterList("appointOrderIds",appointOrderIds).setParameter("status", ObjectStatus.ENABLED);
  }

  public static Query getAppointmentOrderByCustomerId(Session session, Long customerId) {
    StringBuilder sb = new StringBuilder(" from AppointOrder where customerId =:customerId ");
    return session.createQuery(sb.toString()).setLong("customerId", customerId);
  }
  public static Query getOnlinePurchaseOrderItemsByOrderIdSupplierProductId(Session session, Long purchaseOrderId, Long supplierProductId) {
    return session.createQuery("select p from PurchaseOrderItem p where p.purchaseOrderId =:purchaseOrderId and p.supplierProductId =:productId")
        .setLong("purchaseOrderId", purchaseOrderId).setLong("productId", supplierProductId);
  }



  /**
   * 有 user 级别 有shop 级别
   * @param session
   * @param types
   * @param receiverShopId
   * @param receiverIds
   * @param status
   * @return
   */
  public static Query countPushMessageByStatus(Session session,List<PushMessageType> types, Long receiverShopId,PushMessageReceiverStatus status,Long... receiverIds) {
    StringBuilder sql = new StringBuilder();
    sql.append("select count(distinct p.id) from PushMessage p,PushMessageReceiver r where p.id = r.messageId and p.type in(:types) and r.shopId=:receiverShopId and r.receiverId in(:receiverIds) and r.showStatus=:showStatus and p.deleted =:deleted");
    if (status != null) {
      sql.append(" and r.status=:status ");
    } else {
      sql.append(" and r.status!=:status ");
    }
    Query query = session.createQuery(sql.toString())
        .setLong("receiverShopId", receiverShopId)
        .setParameterList("receiverIds", receiverIds)
        .setParameterList("types", types)
        .setParameter("showStatus", PushMessageShowStatus.ACTIVE)
        .setParameter("deleted", DeletedType.FALSE);
    if (status != null) {
      query.setParameter("status", status);
    } else {
      query.setParameter("status", PushMessageReceiverStatus.DISABLED);
    }
    return query;
  }

  public static Query getPushMessageAndReceivers(Session session,Long receiverShopId,Long... pushMessageReceiverIds) {
    StringBuilder sql = new StringBuilder();
    sql.append("select p,r from PushMessage p,PushMessageReceiver r where p.id = r.messageId and r.id in(:pushMessageReceiverIds) and r.shopId=:receiverShopId");
    return session.createQuery(sql.toString())
        .setLong("receiverShopId", receiverShopId).setParameterList("pushMessageReceiverIds", pushMessageReceiverIds);
  }

  public static Query searchReceiverPushMessageDTO(Session session,List<PushMessageType> pushMessageTypeList,SearchMessageCondition searchMessageCondition) throws ParseException {
    StringBuilder sb = new StringBuilder();
    sb.append("select p,r from PushMessage p,PushMessageReceiver r where p.id = r.messageId and p.type in (:types) and p.deleted=:deleted ");
    sb.append(" and r.showStatus=:showStatus and r.receiverId in(:receiverIds) and r.shopId=:receiverShopId");
    if(searchMessageCondition.getReceiverStatus()!=null){
      sb.append(" and r.status=:receiverStatus");
    }else{
      sb.append(" and r.status<>:receiverStatus");
    }
    if(StringUtils.isNotBlank(searchMessageCondition.getKeyWord())){
      sb.append(" and p.contentText like :keyWord");
    }
    if(searchMessageCondition.getDayRange()!=null){
      sb.append(" and p.createTime >=:createTime");
    }
    if(searchMessageCondition.getRelatedObjectId()!=null){
      sb.append(" and p.relatedObjectId =:relatedObjectId");
    }
    sb.append(" order by p.createTime desc");

    Query query = session.createQuery(sb.toString());
    query.setLong("receiverShopId", searchMessageCondition.getShopId());
    query.setParameterList("types",pushMessageTypeList);
    query.setParameterList("receiverIds", new Long[]{searchMessageCondition.getShopId(),searchMessageCondition.getUserId()});//只能这样 才能 把 user 和shop  2个级别的都搜出来
    query.setParameter("deleted",DeletedType.FALSE);
    query.setParameter("showStatus",PushMessageShowStatus.ACTIVE);
    if(searchMessageCondition.getReceiverStatus()!=null){
      query.setParameter("receiverStatus", searchMessageCondition.getReceiverStatus());
    }else{
      query.setParameter("receiverStatus", PushMessageReceiverStatus.DISABLED);
    }
    if(StringUtils.isNotBlank(searchMessageCondition.getKeyWord())){
      query.setString("keyWord", "%" + searchMessageCondition.getKeyWord() + "%");
    }
    if(searchMessageCondition.getDayRange()!=null){
      query.setLong("createTime",DateUtil.getTheDayTime()-searchMessageCondition.getDayRange().getValue()*DateUtil.DAY_MILLION_SECONDS);
    }
    if(searchMessageCondition.getRelatedObjectId()!=null){
      query.setLong("relatedObjectId",searchMessageCondition.getRelatedObjectId());
    }
    return query.setFirstResult((searchMessageCondition.getStartPageNo() - 1) * searchMessageCondition.getMaxRows()).setMaxResults(searchMessageCondition.getMaxRows());
  }

  public static Query countReceiverPushMessageDTO(Session session,List<PushMessageType> pushMessageTypeList,SearchMessageCondition searchMessageCondition) throws ParseException {
    StringBuilder sb = new StringBuilder();
    sb.append("select count(distinct p.id) from PushMessage p,PushMessageReceiver r where p.id = r.messageId and p.type in (:types) and p.deleted=:deleted ");
    sb.append(" and r.showStatus=:showStatus and r.receiverId in(:receiverIds) and r.shopId=:receiverShopId");
    if(searchMessageCondition.getReceiverStatus()!=null){
      sb.append(" and r.status=:receiverStatus");
    }else{
      sb.append(" and r.status<>:receiverStatus");
    }
    if(StringUtils.isNotBlank(searchMessageCondition.getKeyWord())){
      sb.append(" and p.contentText like :keyWord");
    }
    if(searchMessageCondition.getDayRange()!=null){
      sb.append(" and p.createTime >=:createTime");
    }
    if(searchMessageCondition.getRelatedObjectId()!=null){
      sb.append(" and p.relatedObjectId =:relatedObjectId");
    }
    Query query = session.createQuery(sb.toString());
    query.setLong("receiverShopId", searchMessageCondition.getShopId());
    query.setParameterList("types",pushMessageTypeList);
    query.setParameterList("receiverIds", new Long[]{searchMessageCondition.getShopId(),searchMessageCondition.getUserId()});
    query.setParameter("deleted",DeletedType.FALSE);
    query.setParameter("showStatus",PushMessageShowStatus.ACTIVE);
    if(searchMessageCondition.getReceiverStatus()!=null){
      query.setParameter("receiverStatus", searchMessageCondition.getReceiverStatus());
    }else{
      query.setParameter("receiverStatus", PushMessageReceiverStatus.DISABLED);
    }
    if(StringUtils.isNotBlank(searchMessageCondition.getKeyWord())){
      query.setString("keyWord", "%" + searchMessageCondition.getKeyWord() + "%");
    }
    if(searchMessageCondition.getDayRange()!=null){
      query.setLong("createTime",DateUtil.getTheDayTime()-searchMessageCondition.getDayRange().getValue()*DateUtil.DAY_MILLION_SECONDS);
    }
    if(searchMessageCondition.getRelatedObjectId()!=null){
      query.setLong("relatedObjectId",searchMessageCondition.getRelatedObjectId());
    }
    return query;
  }
  public static Query getAppointOrderByCondition(Session session, Long appointTime, Long shopId,Long serviceCategoryId,String vehicleNo,Set<AppointOrderStatus> statusSet) {
    StringBuilder sb = new StringBuilder(" select a from AppointOrder a, AppointOrderServiceItem b where a.shopId =:shopId and a.appointTime =:appointTime " +
        "and a.vehicleNo =:vehicleNo and a.status in(:statusSet)  and a.id = b.appointOrderId and b.serviceId =:serviceCategoryId ");
    return session.createQuery(sb.toString()).setLong("shopId", shopId).setLong("appointTime", appointTime).setLong("serviceCategoryId", serviceCategoryId).setString("vehicleNo", vehicleNo).setParameterList("statusSet", statusSet);
  }

  public static Query getAppointOrderByVehicleNoStatus(Session session, String vehicleNo,Set<AppointOrderStatus> statusSet) {
    StringBuilder sb = new StringBuilder("from AppointOrder  where vehicleNo =:vehicleNo and status in(:statusSet)");
    return session.createQuery(sb.toString()).setString("vehicleNo", vehicleNo).setParameterList("statusSet", statusSet);
  }

  public static Query countAppointOrderByStatus(Session session,AppointOrderStatus statusSet) {
    StringBuilder sb = new StringBuilder(" select count(*) from AppointOrder  where status =:statusSet ");
    return session.createQuery(sb.toString()).setParameter("statusSet", statusSet);
  }

  public static Query getAppointOrderByStatus(Session session, AppointOrderStatus statusSet, int start ,int size) {
    StringBuilder sb = new StringBuilder("  from AppointOrder where status =:statusSet order by created asc ");
    return session.createQuery(sb.toString()).setParameter("statusSet", statusSet).setFirstResult(start).setMaxResults(size);
  }

  public static Query countRepairOrderByShopIdStatus(Session session, Long shopId,OrderStatus status) {
    StringBuilder sb = new StringBuilder(" select count(*) from RepairOrder where shopId =:shopId and statusEnum=:status and appUserNo is not null ");
    return session.createQuery(sb.toString()).setLong("shopId", shopId).setParameter("status",status);
  }


  public static Query countWashBeautyOrderByShopIdStatus(Session session, Long shopId,OrderStatus status) {
    StringBuilder sb = new StringBuilder(" select count(*)  from WashBeautyOrder where shopId =:shopId and status=:status and appUserNo is not null ");
    return session.createQuery(sb.toString()).setLong("shopId", shopId).setParameter("status", status);
  }

  public static Query getRepairOrderByShopId(Session session, Long shopId, OrderStatus[] orderStatus, int start, int size) {
    return session.createQuery("from RepairOrder where shopId = :shopId and statusEnum in (:orderStatus) ORDER BY startDate ASC").setLong("shopId", shopId).setParameterList("orderStatus", orderStatus).setFirstResult(start).setMaxResults(size);
  }

  public static Query getRepairOrderCountByShopId(Session session, Long shopId, OrderStatus[] orderStatus) {
    return session.createQuery("select count(*) from RepairOrder where shopId = :shopId and statusEnum in (:orderStatus)").setLong("shopId", shopId).setParameterList("orderStatus", orderStatus);
  }

  public static Query getRepairOrderByShopId(Session session, Long shopId, OrderStatus[] orderStatus, RepairRemindEventTypes repairRemindEventTypes, int start, int size) {
    return session.createQuery("from RepairOrder r where r.shopId=:shopId and r.statusEnum in (:orderStatus) and exists(from RepairRemindEvent t where t.eventTypeEnum = :eventTypeEnum and t.repairOrderId = r.id) ORDER BY r.startDate ASC").setLong("shopId", shopId).setParameterList("orderStatus", orderStatus).setParameter("eventTypeEnum", repairRemindEventTypes).setFirstResult(start).setMaxResults(size);
  }

  public static Query getRepairOrderCountByShopId(Session session, Long shopId, OrderStatus[] orderStatus, RepairRemindEventTypes repairRemindEventTypes) {
    return session.createQuery("select count(*) from RepairOrder r where r.shopId=:shopId and r.statusEnum in (:orderStatus) and exists(from RepairRemindEvent t where t.eventTypeEnum = :eventTypeEnum and t.repairOrderId = r.id)").setLong("shopId", shopId).setParameterList("orderStatus", orderStatus).setParameter("eventTypeEnum", repairRemindEventTypes);
  }

  public static Query getRepairOrderService(Session session, Set<Long> repairOrderId) {
    return session.createQuery("from RepairOrderService where repairOrderId in (:repairOrderId)").setParameterList("repairOrderId", repairOrderId);
  }

  public static Query getRepairRemindEventsByOrderId(Session session, Long shopId, Set<Long> repairOrderId) {
    return session.createQuery("from RepairRemindEvent where shopId =:shopId and repairOrderId in (:repairOrderId)").setLong("shopId", shopId).setParameterList("repairOrderId", repairOrderId);
  }

  public static Query getPepairOrderItemStatistics(Session session, Long shopId){
    return session.createQuery("select eventTypeEnum, count(distinct repairOrderId) from RepairRemindEvent where shopId = :shopId group by eventTypeEnum").setLong("shopId", shopId);
  }

  public static Query getPepairOrderStatistics(Session session, Long shopId, OrderStatus[] orderStatus){
    return session.createQuery("select statusEnum, sum(total), count(id) from RepairOrder where shopId = :shopId and statusEnum in (:orderStatus) group by statusEnum").setLong("shopId", shopId).setParameterList("orderStatus", orderStatus);
  }

  public static Query getRepairPickingByOrderId(Session session, Set<Long> repairOrderIdSet){
    return session.createQuery("from RepairPicking where repairOrderId in (:repairOrderId)").setParameterList("repairOrderId", repairOrderIdSet);
  }

  public static Query countRepairOrderByDate(Session session, Long shopId, Long startDate, Long endDate) {
    String sql = "select id from repair_order where shop_Id = :shopId and start_date>= :startDate and start_date <= :endDate and status_enum in ('REPAIR_DISPATCH','REPAIR_CHANGE','REPAIR_DONE','REPAIR_SETTLED')" +
        "union all select id from wash_beauty_order where shop_id = :shopId and  vest_date >= :startDate and vest_date <= :endDate and status = 'WASH_SETTLED' ";
    sql = "select count(*) from (" + sql + ") as view";
    return session.createSQLQuery(sql).setLong("shopId", shopId).setLong("startDate", startDate).setLong("endDate", endDate);
  }

  public static Query getCustomerOfTodayAddVehicle(Session session, Long shopId, Long fromDate, Long endDate, int start, int size) {
    String sql = "select distinct cv.customer_id from bcuser.vehicle v " +
        "join bcuser.customer_vehicle cv on v.id = cv.vehicle_id and (cv.status is null or cv.status = 'ENABLED') " +
        "join txn.repair_order ro on ro.shop_id = :shopId and ro.vechicle_id = v.id and ro.status_enum in ('REPAIR_DISPATCH', 'REPAIR_CHANGE', 'REPAIR_DONE', 'REPAIR_SETTLED') and ro.created >= :fromDate and ro.created <= :endDate " +
        "where v.shop_id = :shopId and (v.status is null or v.status = 'ENABLED') and v.created >= :fromDate and v.created <= :endDate " +
        "union select distinct cv2.customer_id from bcuser.vehicle v2 " +
        "join bcuser.customer_vehicle cv2 on v2.id = cv2.vehicle_id and (cv2.status is null or cv2.status = 'ENABLED') " +
        "join txn.wash_beauty_order wbo on wbo.shop_id = :shopId and wbo.vechicle_id = v2.id and wbo.status in ('WASH_SETTLED') and wbo.created >= :fromDate and wbo.created <= :endDate " +
        "where v2.shop_id = :shopId and (v2.status is null or v2.status = 'ENABLED') and v2.created >= :fromDate and v2.created <= :endDate ";
    return session.createSQLQuery(sql).setLong("shopId", shopId).setLong("fromDate", fromDate).setLong("endDate", endDate).setFirstResult(start).setMaxResults(size);
  }

  public static Query getCustomerCountOfTodayAddVehicle(Session session, Long shopId, Long fromDate, Long endDate) {
    String sql = "select distinct cv.customer_id from bcuser.vehicle v " +
        "join bcuser.customer_vehicle cv on v.id = cv.vehicle_id and (cv.status is null or cv.status = 'ENABLED') " +
        "join txn.repair_order ro on ro.shop_id = :shopId and ro.vechicle_id = v.id and ro.status_enum in ('REPAIR_DISPATCH', 'REPAIR_CHANGE', 'REPAIR_DONE', 'REPAIR_SETTLED') and ro.created >= :fromDate and ro.created <= :endDate " +
        "where v.shop_id = :shopId and (v.status is null or v.status = 'ENABLED') and v.created >= :fromDate and v.created <= :endDate " +
        "union select distinct cv2.customer_id from bcuser.vehicle v2 " +
        "join bcuser.customer_vehicle cv2 on v2.id = cv2.vehicle_id and (cv2.status is null or cv2.status = 'ENABLED') " +
        "join txn.wash_beauty_order wbo on wbo.shop_id = :shopId and wbo.vechicle_id = v2.id and wbo.status in ('WASH_SETTLED') and wbo.created >= :fromDate and wbo.created <= :endDate " +
        "where v2.shop_id = :shopId and (v2.status is null or v2.status = 'ENABLED') and v2.created >= :fromDate and v2.created <= :endDate ";
    sql = "select count(*) from ( " + sql + " ) as view";
    return session.createSQLQuery(sql).setLong("shopId", shopId).setLong("fromDate", fromDate).setLong("endDate", endDate);
  }

  public static Query getTodayServiceVehicleByCustomerId(Session session, Long shopId, Set<Long> customerIdSet,Long fromDate, Long endDate) {
    String sql = "select v.licence_no from bcuser.vehicle v " +
        "join bcuser.customer_vehicle cv on v.id = cv.vehicle_id and (cv.status is null or cv.status = 'ENABLED') " +
        "join txn.repair_order ro on ro.shop_id = :shopId and ro.vechicle_id = v.id and ro.status_enum in ('REPAIR_DISPATCH', 'REPAIR_CHANGE', 'REPAIR_DONE', 'REPAIR_SETTLED') and ro.created >= :fromDate and ro.created <= :endDate " +
        "where v.shop_id = :shopId and (v.status is null or v.status = 'ENABLED') and v.created >= :fromDate and v.created <= :endDate and cv.customer_id in (:customerIdSet) " +
        "union select v2.licence_no from bcuser.vehicle v2 " +
        "join bcuser.customer_vehicle cv2 on v2.id = cv2.vehicle_id and (cv2.status is null or cv2.status = 'ENABLED') " +
        "join txn.wash_beauty_order wbo on wbo.shop_id = :shopId and wbo.vechicle_id = v2.id and wbo.status in ('WASH_SETTLED') and wbo.created >= :fromDate and wbo.created <= :endDate " +
        "where v2.shop_id = :shopId and (v2.status is null or v2.status = 'ENABLED') and v2.created >= :fromDate and v2.created <= :endDate and cv2.customer_id in (:customerIdSet)";
    return session.createSQLQuery(sql).setLong("shopId", shopId).setParameterList("customerIdSet", customerIdSet).setLong("fromDate", fromDate).setLong("endDate", endDate);
  }

  public static Query getPushMessageIdAndCommentedId(Session session, Set<Long> pushMessageIds) {
    return session.createSQLQuery("select pm.id as pushMessageId,cr.order_id as orderId " +
        "from push_message pm " +
        "left join push_message_source pms on pm.id=pms.message_id " +
        "left join appoint_order ao on ao.id=pms.source_id " +
        "left join comment_record cr on cr.order_id=ao.order_id " +
        "where pm.id in (:pushMessageIds) and cr.id is not null")
        .addScalar("pushMessageId",StandardBasicTypes.LONG)
        .addScalar("orderId",StandardBasicTypes.LONG)
        .setParameterList("pushMessageIds", pushMessageIds);
  }



  /**
   * @param session
   * @param shopId
   * @return
   */
  public static Query getPreBuyOrderItemByProductDTO(Session session, Long shopId,ProductDTO productDTO) {
    StringBuffer sb = new StringBuffer("select pb from PreBuyOrder p,PreBuyOrderItem pb where p.id=pb.preBuyOrderId and p.shopId=:shopId and p.deleted='FALSE'");
    try {
      sb.append(" and p.endDate>=").append(DateUtil.getTheDayTime());
    } catch (ParseException e) {
      e.printStackTrace();
    }
    if(StringUtils.isNotBlank(productDTO.getName())){
      sb.append(" and pb.productName =:productName");
    }
    if(StringUtils.isNotBlank(productDTO.getBrand())){
      sb.append(" and pb.productBrand =:productBrand");
    }
    if(StringUtils.isNotBlank(productDTO.getSpec())){
      sb.append(" and pb.productSpec =:productSpec");
    }
    if(StringUtils.isNotBlank(productDTO.getModel())){
      sb.append(" and pb.productModel =:productModel");
    }
    if(StringUtils.isNotBlank(productDTO.getProductVehicleModel())){
      sb.append(" and pb.productVehicleModel =:productVehicleModel");
    }
    if(StringUtils.isNotBlank(productDTO.getProductVehicleBrand())){
      sb.append(" and pb.productVehicleBrand =:productVehicleBrand");
    }
    Query query = session.createQuery(sb.toString()).setLong("shopId", shopId);
    if(StringUtils.isNotBlank(productDTO.getName())){
      query.setString("productName",productDTO.getName().trim());
    }
    if(StringUtils.isNotBlank(productDTO.getBrand())){
      query.setString("productBrand",productDTO.getBrand().trim());
    }
    if(StringUtils.isNotBlank(productDTO.getSpec())){
      query.setString("productSpec",productDTO.getSpec().trim());
    }
    if(StringUtils.isNotBlank(productDTO.getModel())){
      query.setString("productModel",productDTO.getModel().trim());
    }
    if(StringUtils.isNotBlank(productDTO.getProductVehicleModel())){
      query.setString("productVehicleModel",productDTO.getProductVehicleModel().trim());
    }
    if(StringUtils.isNotBlank(productDTO.getProductVehicleBrand())){
      query.setString("productVehicleBrand",productDTO.getProductVehicleBrand().trim());
    }
    return query;
  }


  public static Query countValidPreBuyOrderItemsByType(Session session, Long shopId, BusinessChanceType type) {
    StringBuffer sb = new StringBuffer("select count(*) from PreBuyOrderItem pi where pi.preBuyOrderId in (select id from PreBuyOrder p where p.shopId=:shopId and p.businessChanceType =:businessChanceType and p.deleted='FALSE'");
    try {
      sb.append(" and p.endDate>=").append(DateUtil.getTheDayTime());
    } catch (ParseException e) {
      e.printStackTrace();
    }
    sb.append(")");
    return session.createQuery(sb.toString()).setLong("shopId", shopId).setParameter("businessChanceType",type);
  }

  public static Query getValidPreBuyOrdersWithoutSelf(Session session, Long shopId, Long preBuyOrderId) {
    StringBuffer sb = new StringBuffer("from PreBuyOrder where shopId = :shopId and deleted =:deleteType and id <>:preBuyOrderId");
    try {
      sb.append(" and endDate>=").append(DateUtil.getTheDayTime());
    } catch (ParseException e) {
      e.printStackTrace();
    }
    sb.append(" order by created desc");
    return session.createQuery(sb.toString()).setLong("shopId", shopId).setParameter("deleteType",DeletedType.FALSE).setLong("preBuyOrderId",preBuyOrderId);
  }

  public static Query getOtherShopPreBuyOrders(Session session, PreBuyOrderSearchCondition condition) {
    StringBuffer sb = new StringBuffer("select p from PreBuyOrderItemRecommend pr,PreBuyOrder p where pr.preBuyOrderId = p.id and pr.shopId =:shopId and p.shopId <>:noneShopId and p.deleted='FALSE' and pr.deleted='FALSE'");
    try {
      sb.append(" and p.endDate>=").append(DateUtil.getTheDayTime());
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return session.createQuery(sb.toString()).setLong("shopId",condition.getShopId()).setLong("noneShopId", condition.getNoneShopId()).setFirstResult((condition.getStartPageNo() - 1)*condition.getPageSize()).setMaxResults(condition.getPageSize());
  }


  public static Query getQuotedPreBuyOrderItemsByPager(Session session, Long preBuyOrderItemId, int pageStart, int pageSize) {
    StringBuffer sb = new StringBuffer("from QuotedPreBuyOrderItem p where  p.preBuyOrderItemId=:preBuyOrderItemId");
    sb.append(" order by p.creationDate desc");
    Query query =session.createQuery(sb.toString()).setLong("preBuyOrderItemId", preBuyOrderItemId).setFirstResult(pageStart).setMaxResults(pageSize);
    return query;
  }

  public static Query countOtherShopPreBuyOrders(Session session, Long shopId, Long noneShopId) {
    StringBuffer sb = new StringBuffer("select count(*) from PreBuyOrderItemRecommend pr,PreBuyOrder p where pr.preBuyOrderId = p.id and pr.shopId =:shopId and p.shopId <>:noneShopId and p.deleted='FALSE' and pr.deleted='FALSE'");
    try {
      sb.append(" and p.endDate>=").append(DateUtil.getTheDayTime());
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return session.createQuery(sb.toString()).setLong("shopId",shopId).setLong("noneShopId", noneShopId);
  }

  public static Query getLastedAssistantAchievementHistory(Session session, Long shopId, Long assistantId,Long departmentChangeTime) {
    StringBuffer sb = new StringBuffer();
    sb.append(" from AssistantAchievementHistory where shopId =:shopId and assistantId =:assistantId and departmentChangeTime <=:departmentChangeTime order by departmentChangeTime desc ");
    Query query = session.createQuery(sb.toString());
    query.setLong("shopId", shopId).setLong("assistantId", assistantId).setLong("departmentChangeTime",departmentChangeTime).setFirstResult(0).setMaxResults(1);
    return query;
  }

  public static Query geAssistantAchievementHistory(Session session, Long shopId, Long assistantId) {
    if (assistantId != null) {
      StringBuffer sb = new StringBuffer();
      sb.append(" from AssistantAchievementHistory where shopId =:shopId and assistantId =:assistantId order by departmentChangeTime asc ");
      Query query = session.createQuery(sb.toString());
      query.setLong("shopId", shopId).setLong("assistantId", assistantId);
      return query;
    }

    StringBuffer sb = new StringBuffer();
    sb.append(" from AssistantAchievementHistory where shopId =:shopId group by assistantId ");
    Query query = session.createQuery(sb.toString());
    query.setLong("shopId", shopId);
    return query;

  }

  public static Query getShopAllStatServiceByShopId(Session session, Long shopId) {
    return session.createQuery("from AssistantAchievementStat s where s.shopId = :shopId and serviceId is not null group by serviceId ")
        .setLong("shopId", shopId);
  }

  public static Query getAssistantAchievementStat(Session session, Long shopId,int statYear,int statMonth,AchievementStatType statType,Long assistantOrDepartmentId) {

    if (statType == AchievementStatType.ASSISTANT) {
      return session.createQuery("from AssistantAchievementStat s where s.shopId = :shopId and s.statYear = :statYear and s.statMonth = :statMonth  and s.achievementStatType = :statType " +
          " and s.assistantId = :assistantId and serviceId is  null  ")
          .setLong("shopId", shopId).setLong("assistantId", assistantOrDepartmentId).setInteger("statYear", statYear).setInteger("statMonth", statMonth).setParameter("statType", statType);
    } else {
      return session.createQuery("from AssistantAchievementStat s where s.shopId = :shopId and s.statYear = :statYear and s.statMonth = :statMonth  and s.achievementStatType = :statType " +
          " and s.departmentId = :departmentId and serviceId is  null  ")
          .setLong("shopId", shopId).setLong("departmentId", assistantOrDepartmentId).setInteger("statYear", statYear).setInteger("statMonth", statMonth).setParameter("statType", statType);
    }
  }


  public static Query getAssistantRecord(Session session,Long shopId,Long orderId,Long assistantId,Long itemId,AssistantRecordType recordType) {
    StringBuffer sb = new StringBuffer();
    sb.append(" from ");
    if (recordType == AssistantRecordType.PRODUCT) {
      sb.append(" AssistantProductRecord where itemId=:itemId  and orderId=:orderId");
    } else if (recordType == AssistantRecordType.SERVICE) {
      sb.append(" AssistantServiceRecord where itemId=:itemId  and orderId=:orderId");
    } else if (recordType == AssistantRecordType.MEMBER_NEW || recordType == AssistantRecordType.MEMBER_RENEW) {
      sb.append(" AssistantMemberRecord where 1=1  and orderId=:orderId ");
    } else if (recordType == AssistantRecordType.BUSINESS_ACCOUNT) {
      sb.append(" AssistantBusinessAccountRecord where 1=1 and businessAccountId=:businessAccountId ");
    }
    sb.append(" and shopId =:shopId and assistantId=:assistantId ");

    Query query = session.createQuery(sb.toString()).setLong("shopId", shopId).setLong("assistantId", assistantId);

    if (recordType == AssistantRecordType.SERVICE || recordType == AssistantRecordType.PRODUCT) {
      query.setLong("itemId", itemId).setLong("orderId", orderId);
    } else if (recordType == AssistantRecordType.MEMBER_NEW || recordType == AssistantRecordType.MEMBER_RENEW) {
      query.setLong("orderId", orderId);

    } else if (recordType == AssistantRecordType.BUSINESS_ACCOUNT) {
      query.setLong("businessAccountId", orderId);

    }

    return query;
  }


  public static Query getARecordIdFromAHistory(Session session, Long shopId, AssistantRecordType recordType, List<Long> recordIdList) {
    StringBuffer sb = new StringBuffer();

    if (recordType == AssistantRecordType.PRODUCT) {
      sb.append(" select distinct productId from ProductAchievementHistory where shopId=:shopId and productId  in(:list)  ");
    } else if (recordType == AssistantRecordType.SERVICE) {
      sb.append(" select distinct serviceId from ServiceAchievementHistory where shopId=:shopId and serviceId  in(:list)  ");
    }

    Query query = session.createQuery(sb.toString()).setLong("shopId", shopId).setParameterList("list", recordIdList);
    return query;
  }

  public static Query getARecordIdFromSAConfig(Session session, Long shopId, AssistantRecordType assistantRecordType, List<Long> recordIdList) {
    StringBuffer sb = new StringBuffer();
    sb.append(" select id, achievementRecordId from ShopAchievementConfig where shopId =:shopId and assistantRecordType=:assistantRecordType")
        .append(" and achievementRecordId  in(:list) ");
    Query query = session.createQuery(sb.toString());
    query.setLong("shopId", shopId).setParameterList("list", recordIdList).setParameter("assistantRecordType",assistantRecordType);
    return query;
  }

  public static Query getAssistantAchievementRecord(Session session, Long shopId, Set<Long> itemIdSet, AssistantRecordType recordType) {
    StringBuffer sb = new StringBuffer();
    sb.append(" from ");
    if (recordType == AssistantRecordType.PRODUCT) {
      sb.append(" AssistantProductRecord where itemId in(:list) ");
    } else if (recordType == AssistantRecordType.SERVICE) {
      sb.append(" AssistantServiceRecord where itemId in(:list) ");
    } else if (recordType == AssistantRecordType.MEMBER_NEW || recordType == AssistantRecordType.MEMBER_RENEW) {
      sb.append(" AssistantMemberRecord where 1=1  and orderId in(:list) ");
    } else if (recordType == AssistantRecordType.BUSINESS_ACCOUNT) {
      sb.append(" AssistantBusinessAccountRecord where 1=1 and businessAccountId in(:list) ");
    }
    sb.append(" and shopId =:shopId ");

    Query query = session.createQuery(sb.toString()).setLong("shopId", shopId).setParameterList("list", itemIdSet);
    return query;
  }

  public static Query getRepairAndDraftOrders(Session session, Long shopId, Long userId, Long vehicleId, Pager pager, String[] orderTypes, Long startTime, Long endTime) {
    StringBuffer sb = new StringBuffer("select id,receiptNo as receipt_no,save_time,customer_Supplier_name as customer,vechicle,material,service_content,status from draft_order where status='DRAFT_SAVED' and shop_id =:shopId and user_id=:userId and order_type_enum in (:orderTypes)");
    if(vehicleId != null){
      sb.append(" and vechicle_id = :vehicleId");
    }
    if (startTime != null) {
      sb.append(" and save_time >= :startTime");
    }
    if (endTime != null) {
      sb.append(" and save_time <= :endTime");
    }
    sb.append(" union ");
    sb.append(" select id,receipt_no,last_update as save_time,customer,vechicle,vechicle as material,vechicle as service_content,status_enum as status from repair_order where status_enum in (:orderStatus) and shop_id =:shopId2");
    if(vehicleId != null){
      sb.append(" and vechicle_id = :vehicleId2");
    }
    sb.append(" order by save_time desc");
    Query query = session.createSQLQuery(sb.toString()).setLong("shopId", shopId).setLong("shopId2", shopId).setLong("userId", userId).setParameterList("orderTypes", orderTypes).setParameterList("orderStatus",new String[]{"REPAIR_DISPATCH","REPAIR_DONE"});
    if(vehicleId != null){
      query.setLong("vehicleId", vehicleId);
      query.setLong("vehicleId2", vehicleId);
    }
    if (startTime != null) {
      query.setLong("startTime", startTime);
    }
    if (endTime != null) {
      query.setLong("endTime", endTime);
    }
    return query.setFirstResult((pager.getCurrentPage() - 1) * pager.getPageSize()).setMaxResults(pager.getPageSize());
  }
  public static Query findRepairOrderSecondaryById(Session session, Long shopId, Long repairOrderSecondaryId) {
    return session.createQuery("from RepairOrderSecondary where shopId = :shopId and id = :id").setLong("shopId", shopId).setLong("id", repairOrderSecondaryId);
  }

  public static Query findRepairOrderServiceSecondaryById(Session session, Long shopId, Long repairOrderSecondaryId) {
    return session.createQuery("from RepairOrderServiceSecondary where shopId = :shopId and repairOrderSecondaryId = :repairOrderSecondaryId").setLong("shopId", shopId).setLong("repairOrderSecondaryId", repairOrderSecondaryId);
  }

  public static Query findRepairOrderItemSecondaryById(Session session, Long shopId, Long repairOrderSecondaryId) {
    return session.createQuery("from RepairOrderItemSecondary where shopId = :shopId and repairOrderSecondaryId = :repairOrderSecondaryId").setLong("shopId", shopId).setLong("repairOrderSecondaryId", repairOrderSecondaryId);
  }

  public static Query findRepairOrderOtherIncomeItemSecondaryById(Session session, Long shopId, Long repairOrderSecondaryId) {
    return session.createQuery("from RepairOrderOtherIncomeItemSecondary where shopId = :shopId and repairOrderSecondaryId = :repairOrderSecondaryId").setLong("shopId", shopId).setLong("repairOrderSecondaryId", repairOrderSecondaryId);
  }

  public static Query findRepairOrderSettlementSecondaryByRepairOrderId(Session session, Long shopId, Long repairOrderSecondaryId) {
    return session.createQuery("from RepairOrderSettlementSecondary where shopId = :shopId and repairOrderSecondaryId = :repairOrderSecondaryId order by created asc").setLong("shopId", shopId).setLong("repairOrderSecondaryId", repairOrderSecondaryId);
  }

  public static Query findRepairOrderSettlementSecondaryByRepairOrderIds(Session session, Long shopId, Long[] repairOrderSecondaryIds) {
    return session.createQuery("from RepairOrderSettlementSecondary where shopId = :shopId and repairOrderSecondaryId in (:repairOrderSecondaryId) order by created asc").setLong("shopId", shopId).setParameterList("repairOrderSecondaryId", repairOrderSecondaryIds);
  }

  public static Query updateRepairOrderSecondaryOrderStatus(Session session, Long shopId, Long id, OrderStatus orderStatus) {
    return session.createQuery("update RepairOrderSecondary set status = :orderStatus where shopId = :shopId and id = :id").setLong("shopId", shopId).setLong("id", id).setParameter("orderStatus",orderStatus);
  }

  public static Query deleteRepairOrderSettlementSecondary(Session session, Long repairOrderSecondaryId) {
    return session.createQuery("delete RepairOrderSettlementSecondary where repairOrderSecondaryId = :repairOrderSecondaryId").setLong("repairOrderSecondaryId", repairOrderSecondaryId);
  }

  public static Query findRepairOrderSecondaryByRepairOrderId(Session session, Long shopId, Long repairOrderId) {
    return session.createQuery("from RepairOrderSecondary where shopId = :shopId and repairOrderId = :repairOrderId").setLong("shopId", shopId).setLong("repairOrderId", repairOrderId);
  }

  public static Query statisticsRepairOrderSecondary(Session session, Long shopId, RepairOrderSecondaryCondition repairOrderSecondaryCondition) {
    StringBuffer sql = new StringBuffer("select count(id),sum(total),sum(accountDebtAmount) from RepairOrderSecondary where shopId = :shopId ");
    if (repairOrderSecondaryCondition.getCustomerId() != null) {
      sql.append("and customerId in :customerId ");
    } else if (StringUtils.isNotEmpty(repairOrderSecondaryCondition.getCustomerInfo())) {
      sql.append("and (customerName like :customerInfo or customerContact like :customerInfo or customerMobile like :customerInfo or vehicleLicense like :customerInfo) ");
    }
    if (repairOrderSecondaryCondition.getStatus() != null) {
      sql.append("and status in :status ");
    }
    if (StringUtils.isNotEmpty(repairOrderSecondaryCondition.getReceipt())) {
      sql.append("and receipt = :receipt ");
    }
    if (repairOrderSecondaryCondition.getStartDate() != null) {
      sql.append("and endDate > :startDate ");
    }
    if (repairOrderSecondaryCondition.getEndDate() != null) {
      sql.append("and endDate <= :endDate ");
    }
    Query query = session.createQuery(sql.toString()).setLong("shopId", shopId);
    if (repairOrderSecondaryCondition.getStatus() != null) {
      query.setParameterList("status", repairOrderSecondaryCondition.getStatus());
    }
    if (StringUtils.isNotEmpty(repairOrderSecondaryCondition.getReceipt())) {
      query.setString("receipt", repairOrderSecondaryCondition.getReceipt());
    }
    if (repairOrderSecondaryCondition.getCustomerId() != null) {
      query.setLong("customerId", repairOrderSecondaryCondition.getCustomerId());
    } else if (StringUtils.isNotEmpty(repairOrderSecondaryCondition.getCustomerInfo())) {
      query.setString("customerInfo", repairOrderSecondaryCondition.getCustomerInfo());
    }
    if (repairOrderSecondaryCondition.getStartDate() != null) {
      query.setLong("startDate", repairOrderSecondaryCondition.getStartDate());
    }
    if (repairOrderSecondaryCondition.getEndDate() != null) {
      query.setLong("endDate", repairOrderSecondaryCondition.getEndDate());
    }
    return query;
  }

  public static Query statisticsRepairOrderSettlementSecondary(Session session, Long shopId, RepairOrderSecondaryCondition repairOrderSecondaryCondition) {
    StringBuffer sql = new StringBuffer("select sum(ss.income),sum(ss.discount) from RepairOrderSecondary s, RepairOrderSettlementSecondary ss  where s.shopId = :shopId and ss.shopId = :shopId and s.id = ss.repairOrderSecondaryId ");
    if (repairOrderSecondaryCondition.getCustomerId() != null) {
      sql.append("and s.customerId in :customerId ");
    } else if (StringUtils.isNotEmpty(repairOrderSecondaryCondition.getCustomerInfo())) {
      sql.append("and (s.customerName like :customerInfo or s.customerContact like :customerInfo or s.customerMobile like :customerInfo or s.vehicleLicense like :customerInfo) ");
    }
    if (repairOrderSecondaryCondition.getStatus() != null) {
      sql.append("and s.status in :status ");
    }
    if (StringUtils.isNotEmpty(repairOrderSecondaryCondition.getReceipt())) {
      sql.append("and s.receipt = :receipt ");
    }
    if (repairOrderSecondaryCondition.getStartDate() != null) {
      sql.append("and s.endDate > :startDate ");
    }
    if (repairOrderSecondaryCondition.getEndDate() != null) {
      sql.append("and s.endDate <= :endDate ");
    }
    Query query =  session.createQuery(sql.toString()).setLong("shopId", shopId);
    if (repairOrderSecondaryCondition.getStatus() != null) {
      query.setParameterList("status", repairOrderSecondaryCondition.getStatus());
    }
    if (StringUtils.isNotEmpty(repairOrderSecondaryCondition.getReceipt())) {
      query.setString("receipt", repairOrderSecondaryCondition.getReceipt());
    }
    if (repairOrderSecondaryCondition.getCustomerId() != null) {
      query.setLong("customerId", repairOrderSecondaryCondition.getCustomerId());
    } else if (StringUtils.isNotEmpty(repairOrderSecondaryCondition.getCustomerInfo())) {
      query.setString("customerInfo", repairOrderSecondaryCondition.getCustomerInfo());
    }
    if (repairOrderSecondaryCondition.getStartDate() != null) {
      query.setLong("startDate", repairOrderSecondaryCondition.getStartDate());
    }
    if (repairOrderSecondaryCondition.getEndDate() != null) {
      query.setLong("endDate", repairOrderSecondaryCondition.getEndDate());
    }
    return query;
  }

  public static Query queryRepairOrderSecondary(Session session, Long shopId, RepairOrderSecondaryCondition repairOrderSecondaryCondition) {
    StringBuffer sql = new StringBuffer("from RepairOrderSecondary where shopId = :shopId ");
    if (repairOrderSecondaryCondition.getCustomerId() != null) {
      sql.append("and customerId in :customerId ");
    } else if (StringUtils.isNotEmpty(repairOrderSecondaryCondition.getCustomerInfo())) {
      sql.append("and (customerName like :customerInfo or customerContact like :customerInfo or customerMobile like :customerInfo or vehicleLicense like :customerInfo) ");
    }
    if (repairOrderSecondaryCondition.getStatus() != null) {
      sql.append("and status in :status ");
    }
    if (StringUtils.isNotEmpty(repairOrderSecondaryCondition.getReceipt())) {
      sql.append("and receipt = :receipt ");
    }
    if (repairOrderSecondaryCondition.getStartDate() != null) {
      sql.append("and endDate > :startDate ");
    }
    if (repairOrderSecondaryCondition.getEndDate() != null) {
      sql.append("and endDate <= :endDate ");
    }
    sql.append("order by startDate desc");
    Query query = session.createQuery(sql.toString()).setLong("shopId", shopId);
    if (repairOrderSecondaryCondition.getStatus() != null) {
      query.setParameterList("status", repairOrderSecondaryCondition.getStatus());
    }
    if (StringUtils.isNotEmpty(repairOrderSecondaryCondition.getReceipt())) {
      query.setString("receipt", repairOrderSecondaryCondition.getReceipt());
    }
    if (repairOrderSecondaryCondition.getCustomerId() != null) {
      query.setLong("customerId", repairOrderSecondaryCondition.getCustomerId());
    } else if (StringUtils.isNotEmpty(repairOrderSecondaryCondition.getCustomerInfo())) {
      query.setString("customerInfo", repairOrderSecondaryCondition.getCustomerInfo());
    }
    if (repairOrderSecondaryCondition.getStartDate() != null) {
      query.setLong("startDate", repairOrderSecondaryCondition.getStartDate());
    }
    if (repairOrderSecondaryCondition.getEndDate() != null) {
      query.setLong("endDate", repairOrderSecondaryCondition.getEndDate());
    }
    return query.setFirstResult((repairOrderSecondaryCondition.getStartPageNo() - 1) * repairOrderSecondaryCondition.getMaxRows()).setMaxResults(repairOrderSecondaryCondition.getMaxRows());
  }

  public static Query countQueryRepairOrderSecondary(Session session, Long shopId, RepairOrderSecondaryCondition repairOrderSecondaryCondition) {
    StringBuffer sql = new StringBuffer("select count(id) from RepairOrderSecondary where shopId = :shopId ");
    if (repairOrderSecondaryCondition.getCustomerId() != null) {
      sql.append("and customerId in :customerId ");
    } else if (StringUtils.isNotEmpty(repairOrderSecondaryCondition.getCustomerInfo())) {
      sql.append("and (customerName like :customerInfo or customerContact like :customerInfo or customerMobile like :customerInfo or vehicleLicense like :customerInfo) ");
    }
    if (repairOrderSecondaryCondition.getStatus() != null) {
      sql.append("and status in :status ");
    }
    if (StringUtils.isNotEmpty(repairOrderSecondaryCondition.getReceipt())) {
      sql.append("and receipt = :receipt ");
    }
    if (repairOrderSecondaryCondition.getStartDate() != null) {
      sql.append("and endDate > :startDate ");
    }
    if (repairOrderSecondaryCondition.getEndDate() != null) {
      sql.append("and endDate <= :endDate ");
    }
    Query query = session.createQuery(sql.toString()).setLong("shopId", shopId);
    if (repairOrderSecondaryCondition.getStatus() != null) {
      query.setParameterList("status", repairOrderSecondaryCondition.getStatus());
    }
    if (StringUtils.isNotEmpty(repairOrderSecondaryCondition.getReceipt())) {
      query.setString("receipt", repairOrderSecondaryCondition.getReceipt());
    }
    if (repairOrderSecondaryCondition.getCustomerId() != null) {
      query.setLong("customerId", repairOrderSecondaryCondition.getCustomerId());
    } else if (StringUtils.isNotEmpty(repairOrderSecondaryCondition.getCustomerInfo())) {
      query.setString("customerInfo", repairOrderSecondaryCondition.getCustomerInfo());
    }
    if (repairOrderSecondaryCondition.getStartDate() != null) {
      query.setLong("startDate", repairOrderSecondaryCondition.getStartDate());
    }
    if (repairOrderSecondaryCondition.getEndDate() != null) {
      query.setLong("endDate", repairOrderSecondaryCondition.getEndDate());
    }
    return query;
  }

  public static Query countRepairOrders(Session session,Long shopId, Long vehicleId) {
    StringBuffer sb = new StringBuffer("select count(*) from RepairOrder where shopId=:shopId and vechicleId=:vechicleId and statusEnum in (:orderStatus)");
    return session.createQuery(sb.toString()).setLong("shopId",shopId).setLong("vechicleId",vehicleId).setParameterList("orderStatus",new OrderStatus[]{OrderStatus.REPAIR_DISPATCH,OrderStatus.REPAIR_DONE});
  }

  public static Query getUseTimesMostService(Session session, Long shopId) {
    return session.createQuery("from Service s where s.shopId =:shopId  and (s.status is null or s.status <> :status) order by useTimes desc ")
      .setLong("shopId", shopId).setParameter("status", ServiceStatus.DISABLED).setMaxResults(1);
  }
  public static Query getEnquiryById(Session session, Long id, String appUserNo) {
    StringBuilder sb = new StringBuilder();
    sb.append("from Enquiry where id =:id and appUserNo =:appUserNo");
    return session.createQuery(sb.toString()).setLong("id",id).setParameter("appUserNo",appUserNo);
  }

  public static Query getEnquiryTargetShops(Session session, Long enquiryId, Set<EnquiryTargetShopStatus> enquiryTargetShopStatuses) {
    StringBuilder sb = new StringBuilder();
    sb.append("from EnquiryTargetShop where enquiryId =:enquiryId ");
    if (CollectionUtils.isNotEmpty(enquiryTargetShopStatuses)) {
      sb.append("and status in (:enquiryTargetShopStatuses)");
    }
    Query query = session.createQuery(sb.toString()).setParameter("enquiryId", enquiryId);
    if (CollectionUtils.isNotEmpty(enquiryTargetShopStatuses)) {
      query.setParameterList("enquiryTargetShopStatuses", enquiryTargetShopStatuses);
    }
    return query;
  }

  public static Query getEnquiryByAppUserNoAndStatus(Session session, String appUserNo, Set<EnquiryStatus> enquiryStatuses, Pager pager) {
    StringBuilder sb = new StringBuilder();
    sb.append("from Enquiry where appUserNo=:appUserNo ");
    if(CollectionUtils.isNotEmpty(enquiryStatuses)){
      sb.append(" and status in (:enquiryStatuses) ");
    }
    sb.append(" order by lastUpdateTime desc");
    Query query = session.createQuery(sb.toString())
        .setParameter("appUserNo", appUserNo)
        .setMaxResults(pager.getPageSize())
        .setFirstResult(pager.getRowStart());
     if (CollectionUtils.isNotEmpty(enquiryStatuses)) {
       query.setParameterList("enquiryStatuses", enquiryStatuses);
     }
     return query;
  }

  public static Query countEnquiryListByUserNoAndStatus(Session session, String appUserNo, Set<EnquiryStatus> enquiryStatuses) {
    StringBuilder sb = new StringBuilder();
    sb.append("select count(id) from Enquiry where appUserNo=:appUserNo ");
    if (CollectionUtils.isNotEmpty(enquiryStatuses)) {
      sb.append(" and status in (:enquiryStatuses) ");
    }
    Query query = session.createQuery(sb.toString())
        .setParameter("appUserNo", appUserNo);
    if (CollectionUtils.isNotEmpty(enquiryStatuses)) {
      query.setParameterList("enquiryStatuses", enquiryStatuses);
    }
    return query;
  }

  public static Query getEnquiryShopResponseByEnquiryIds(Session session, Set<Long> enquiryIds) {
    StringBuilder sb = new StringBuilder();
    sb.append("from EnquiryShopResponse where enquiryId in(:enquiryIds) order by responseTime desc");

    Query query = session.createQuery(sb.toString())
        .setParameterList("enquiryIds", enquiryIds);
    return query;
  }

  public static Query getEnquiryTargetShopByEnquiryIdsAndStatus(Session session, Set<Long> enquiryIds, Set<EnquiryTargetShopStatus> statuses) {
    StringBuilder sb = new StringBuilder();
    sb.append("from EnquiryTargetShop where enquiryId in(:enquiryIds) ");
    if (CollectionUtils.isNotEmpty(statuses)) {
      sb.append(" and status in(:statuses)");
    }
    Query query = session.createQuery(sb.toString())
        .setParameterList("enquiryIds", enquiryIds);
    if (CollectionUtils.isNotEmpty(statuses)) {
      query.setParameterList("statuses", statuses);
    }
    return query;
  }

  public static Query searchShopEnquiries(Session session, EnquirySearchConditionDTO searchCondition) {
    StringBuilder sb = new StringBuilder();
    sb.append("select e,ets from Enquiry e,EnquiryTargetShop ets where e.id = ets.enquiryId ");
    sb.append(" and ets.targetShopId =:targetShopId ");
    sb.append(" and e.status =:enquiryStatus ");
    if (!ArrayUtils.isEmpty(searchCondition.getAppUserNos())) {
      sb.append(" and e.appUserNo in(:appUserNos) ");
    } else if (StringUtils.isNotEmpty(searchCondition.getCustomerSearchWord())) {
      sb.append(" and (e.appUserName like:customerSearchWord ");
      sb.append(" or e.vehicleNo like:customerSearchWord ");
      sb.append(" or e.appUserMobile like:customerSearchWord ) ");
    }
    if (StringUtils.isNotEmpty(searchCondition.getReceiptNo())) {
      sb.append(" and ets.receiptNo like:receiptNo ");
    }
    if (searchCondition.getEnquiryTimeStart() != null) {
      sb.append(" and ets.sendTime >=:enquiryTimeStart");
    }
    if (searchCondition.getEnquiryTimeEnd() != null) {
      sb.append(" and ets.sendTime <=:enquiryTimeEnd");
    }
    if (searchCondition.getResponseTimeStart() != null) {
      sb.append(" and ets.lastResponseTime >=:responseTimeStart");
    }
    if (searchCondition.getResponseTimeEnd() != null) {
      sb.append(" and ets.lastResponseTime <=:responseTimeEnd");
    }
    sb.append(" and ets.shopResponseStatus in(:shopResponseStatus)");
    sb.append(" order by FIELD(ets.shopResponseStatus,'UN_RESPONSE','RESPONSE'),ets.sendTime desc");
    Query query = session.createQuery(sb.toString())
        .setParameter("targetShopId", searchCondition.getShopId())
        .setParameter("enquiryStatus", EnquiryStatus.SENT)
        .setFirstResult((searchCondition.getStartPageNo() - 1) * searchCondition.getMaxRows())
        .setMaxResults(searchCondition.getMaxRows());
    if (!ArrayUtils.isEmpty(searchCondition.getAppUserNos())) {
      query.setParameterList("appUserNos", searchCondition.getAppUserNos());
    } else if (StringUtils.isNotEmpty(searchCondition.getCustomerSearchWord())) {
      query.setParameter("customerSearchWord", "%" + searchCondition.getCustomerSearchWord() + "%");

    }
    if (StringUtils.isNotEmpty(searchCondition.getReceiptNo())) {
      query.setParameter("receiptNo", "%" + searchCondition.getReceiptNo() + "%");
    }
    if (searchCondition.getEnquiryTimeStart() != null) {
      query.setParameter("enquiryTimeStart", searchCondition.getEnquiryTimeStart());
    }
    if (searchCondition.getEnquiryTimeEnd() != null) {
      query.setParameter("enquiryTimeEnd", searchCondition.getEnquiryTimeEnd());
    }
    if (searchCondition.getResponseTimeStart() != null) {
      query.setParameter("responseTimeStart", searchCondition.getResponseTimeStart());
    }
    if (searchCondition.getResponseTimeEnd() != null) {
      query.setParameter("responseTimeEnd", searchCondition.getResponseTimeEnd());
    }
    if (ArrayUtils.isEmpty(searchCondition.getResponseStatuses())) {
      Set<EnquiryShopResponseStatus> defaultEnquiryShopResponseStatus = new HashSet<EnquiryShopResponseStatus>();
      defaultEnquiryShopResponseStatus.add(EnquiryShopResponseStatus.RESPONSE);
      defaultEnquiryShopResponseStatus.add(EnquiryShopResponseStatus.UN_RESPONSE);
      query.setParameterList("shopResponseStatus", defaultEnquiryShopResponseStatus);
    } else {
      query.setParameterList("shopResponseStatus", searchCondition.getResponseStatuses());
    }
    return query;
  }

  public static Query countShopEnquiries(Session session, EnquirySearchConditionDTO searchCondition) {
    StringBuilder sb = new StringBuilder();
    sb.append("select count(ets.id) from Enquiry e,EnquiryTargetShop ets where e.id = ets.enquiryId ");
    sb.append(" and ets.targetShopId =:targetShopId ");
    sb.append(" and e.status =:enquiryStatus ");
    if (!ArrayUtils.isEmpty(searchCondition.getAppUserNos())) {
      sb.append(" and e.appUserNo in(:appUserNos) ");
    } else if (StringUtils.isNotEmpty(searchCondition.getCustomerSearchWord())) {
      sb.append(" and (e.appUserName like:customerSearchWord ");
      sb.append(" or e.vehicleNo like:customerSearchWord ");
      sb.append(" or e.appUserMobile like:customerSearchWord ) ");
    }
    if (StringUtils.isNotEmpty(searchCondition.getReceiptNo())) {
      sb.append(" and ets.receiptNo like:receiptNo ");
    }
    if (searchCondition.getEnquiryTimeStart() != null) {
      sb.append(" and ets.sendTime >=:enquiryTimeStart");
    }
    if (searchCondition.getEnquiryTimeEnd() != null) {
      sb.append(" and ets.sendTime <=:enquiryTimeEnd");
    }
    if (searchCondition.getResponseTimeStart() != null) {
      sb.append(" and ets.lastResponseTime >=:responseTimeStart");
    }
    if (searchCondition.getResponseTimeEnd() != null) {
      sb.append(" and ets.lastResponseTime <=:responseTimeEnd");
    }
    sb.append(" and ets.shopResponseStatus in(:shopResponseStatus)");
    Query query = session.createQuery(sb.toString())
        .setParameter("targetShopId", searchCondition.getShopId())
        .setParameter("enquiryStatus", EnquiryStatus.SENT);
    if (!ArrayUtils.isEmpty(searchCondition.getAppUserNos())) {
      query.setParameterList("appUserNos", searchCondition.getAppUserNos());
    } else if (StringUtils.isNotEmpty(searchCondition.getCustomerSearchWord())) {
      query.setParameter("customerSearchWord", "%" + searchCondition.getCustomerSearchWord() + "%");

    }
    if (StringUtils.isNotEmpty(searchCondition.getReceiptNo())) {
      query.setParameter("receiptNo", "%" + searchCondition.getReceiptNo() + "%");
    }
    if (searchCondition.getEnquiryTimeStart() != null) {
      query.setParameter("enquiryTimeStart", searchCondition.getEnquiryTimeStart());
    }
    if (searchCondition.getEnquiryTimeEnd() != null) {
      query.setParameter("enquiryTimeEnd", searchCondition.getEnquiryTimeEnd());
    }
    if (searchCondition.getResponseTimeStart() != null) {
      query.setParameter("responseTimeStart", searchCondition.getResponseTimeStart());
    }
    if (searchCondition.getResponseTimeEnd() != null) {
      query.setParameter("responseTimeEnd", searchCondition.getResponseTimeEnd());
    }
    if (ArrayUtils.isEmpty(searchCondition.getResponseStatuses())) {
      Set<EnquiryShopResponseStatus> defaultEnquiryShopResponseStatus = new HashSet<EnquiryShopResponseStatus>();
      defaultEnquiryShopResponseStatus.add(EnquiryShopResponseStatus.RESPONSE);
      defaultEnquiryShopResponseStatus.add(EnquiryShopResponseStatus.UN_RESPONSE);
      query.setParameterList("shopResponseStatus", defaultEnquiryShopResponseStatus);
    } else {
      query.setParameterList("shopResponseStatus", searchCondition.getResponseStatuses());
    }
    return query;
  }

  public static Query getShopEnquiryByIdAndShopId(Session session, Long enquiryOrderId, Long shopId) {
    StringBuilder sb = new StringBuilder();
    sb.append("select e,ets from Enquiry e,EnquiryTargetShop ets where e.id = ets.enquiryId ");
    sb.append(" and ets.targetShopId =:shopId ");
    sb.append(" and e.status =:enquiryStatus ");
    sb.append(" and e.id =:enquiryOrderId");
    sb.append(" and ets.shopResponseStatus in(:shopResponseStatus)");
    Query query = session.createQuery(sb.toString())
        .setParameter("shopId", shopId)
        .setParameter("enquiryOrderId", enquiryOrderId)
        .setParameter("enquiryStatus", EnquiryStatus.SENT);
    Set<EnquiryShopResponseStatus> defaultEnquiryShopResponseStatus = new HashSet<EnquiryShopResponseStatus>();
    defaultEnquiryShopResponseStatus.add(EnquiryShopResponseStatus.RESPONSE);
    defaultEnquiryShopResponseStatus.add(EnquiryShopResponseStatus.UN_RESPONSE);
    query.setParameterList("shopResponseStatus", defaultEnquiryShopResponseStatus);
    return query;
  }

  public static Query getEnquiryShopResponseByEnquiryIdAndShopId(Session session, Long enquiryOrderId, Long shopId) {
    StringBuilder sb = new StringBuilder();
    sb.append("from EnquiryShopResponse where enquiryId =:enquiryOrderId and  shopId =:shopId");
    Query query = session.createQuery(sb.toString())
        .setParameter("enquiryOrderId", enquiryOrderId)
        .setParameter("shopId", shopId);
    return query;
  }

  public static Query getEnquiryTargetShopByShopIdAndEnquiryId(Session session, Long shopId, Long enquiryId) {
    StringBuilder sb = new StringBuilder();
    sb.append("from EnquiryTargetShop where enquiryId =:enquiryId and targetShopId =:shopId and status =:status");
    Set<EnquiryTargetShopStatus> status = new HashSet<EnquiryTargetShopStatus>();
    status.add(EnquiryTargetShopStatus.SENT);
    Query query = session.createQuery(sb.toString())
        .setParameter("enquiryId", enquiryId)
        .setParameterList("status", status)
        .setParameter("shopId", shopId);
    return query;
  }

  public static Query countAllAppOrderDTOs(Session session, String appUserNo) {
    StringBuilder sb = new StringBuilder();
    sb.append("SELECT sum(st.num) AS total FROM (");
    sb.append(" (SELECT count(DISTINCT ao.id) as num");
    sb.append(" FROM appoint_order ao INNER JOIN bcuser.app_user_customer auc ON ao.customer_id = auc.customer_id");
    sb.append(" AND auc.app_user_no =:appUserNo AND ao.app_user_no is null");
    sb.append(" WHERE ao.status in(:appointOrderStatus) )");
    sb.append(" UNION ALL ");
    sb.append(" (SELECT count(DISTINCT ao.id) as num");
    sb.append(" FROM appoint_order ao ");
    sb.append(" WHERE ao.app_user_no =:appUserNo AND ao.status in(:appointOrderStatus) )");
    sb.append(" UNION ALL ");
    sb.append(" (SELECT count(DISTINCT ro.id) as num ");
    sb.append(" FROM repair_order ro INNER JOIN bcuser.app_user_customer auc ");
    sb.append(" ON ro.customer_id = auc.customer_id AND ro.shop_id = auc.shop_id AND auc.app_user_no =:appUserNo");
    sb.append(" AND ro.app_user_no is null");
    sb.append(" WHERE ro.status_enum in(:repairStatus))");
    sb.append(" UNION ALL ");
    sb.append(" (SELECT count(ro.id) as num ");
    sb.append(" FROM repair_order ro  ");
    sb.append(" WHERE ro.app_user_no =:appUserNo AND ro.status_enum in(:repairStatus))");
    sb.append(" UNION ALL ");
    sb.append(" (SELECT count(DISTINCT wbo.id) as num ");
    sb.append(" FROM wash_beauty_order wbo INNER JOIN bcuser.app_user_customer auc ");
    sb.append(" ON wbo.customer_id = auc.customer_id AND wbo.shop_id = auc.shop_id AND auc.app_user_no =:appUserNo");
    sb.append(" AND wbo.app_user_no is null");
    sb.append(" where wbo.status in(:washBeautyStatus))");
    sb.append(" UNION ALL ");
    sb.append(" (SELECT count(wbo.id) as num ");
    sb.append(" FROM wash_beauty_order wbo ");
    sb.append(" WHERE  wbo.app_user_no =:appUserNo AND wbo.status in(:washBeautyStatus))");
    sb.append(" ) AS st ");
    Set<String> appointOrderStatus = new HashSet<String>();
    appointOrderStatus.add(AppointOrderStatus.PENDING.name());
    appointOrderStatus.add(AppointOrderStatus.ACCEPTED.name());
    appointOrderStatus.add(AppointOrderStatus.TO_DO_REPAIR.name());
    Set<String> repairStatus = new HashSet<String>();
    repairStatus.add(OrderStatus.REPAIR_DISPATCH.name());
    repairStatus.add(OrderStatus.REPAIR_DONE.name());
    repairStatus.add(OrderStatus.REPAIR_SETTLED.name());
    Set<String> washBeautyStatus = new HashSet<String>();
    washBeautyStatus.add(OrderStatus.WASH_SETTLED.name());
    return session.createSQLQuery(sb.toString()).addScalar("total",StandardBasicTypes.LONG)
        .setString("appUserNo", appUserNo)
        .setParameterList("appointOrderStatus", appointOrderStatus)
        .setParameterList("repairStatus", repairStatus)
        .setParameterList("washBeautyStatus", washBeautyStatus);
  }

  //返回这四个字段 ao.id,ao.created as vest_date ,'APPOINT_ORDER'as type,ao.shop_id,status
  public static Query getAllAppointOrders(Session session, String appUserNo, Pager pager) {
    StringBuilder sb = new StringBuilder();
    sb.append(" (SELECT DISTINCT ao.id,ao.created as vest_date ,'APPOINT_ORDER'as type,ao.shop_id,ao.status as status ");
    sb.append(" FROM appoint_order ao INNER JOIN bcuser.app_user_customer auc ON ao.customer_id = auc.customer_id");
    sb.append(" AND auc.app_user_no =:appUserNo AND ao.app_user_no is null");
    sb.append(" WHERE ao.status in(:appointOrderStatus) )");
    sb.append(" UNION ALL ");
    sb.append(" (SELECT ao.id,ao.created as vest_date ,'APPOINT_ORDER'as type,ao.shop_id,ao.status as status ");
    sb.append(" FROM appoint_order ao ");
    sb.append(" WHERE ao.app_user_no =:appUserNo  AND ao.status in(:appointOrderStatus) )");
    sb.append(" UNION ALL ");
    sb.append(" (SELECT DISTINCT ro.id,ro.created AS vest_date,'REPAIR' AS type,ro.shop_id,ro.status_enum AS status ");
    sb.append(" FROM repair_order ro INNER JOIN bcuser.app_user_customer auc ");
    sb.append(" ON ro.customer_id = auc.customer_id AND ro.shop_id = auc.shop_id AND auc.app_user_no =:appUserNo");
    sb.append(" AND ro.app_user_no is null");
    sb.append(" WHERE  ro.status_enum in(:repairStatus) )");
    sb.append(" UNION ALL ");
    sb.append(" (SELECT ro.id,ro.created AS vest_date,'REPAIR' AS type,ro.shop_id,ro.status_enum AS status ");
    sb.append(" FROM repair_order ro ");
    sb.append(" WHERE  ro.app_user_no =:appUserNo AND ro.status_enum in(:repairStatus) )");
    sb.append(" UNION ALL ");
    sb.append(" (SELECT DISTINCT wbo.id,wbo.created as vest_date ,'WASH_BEAUTY'as type,wbo.shop_id,wbo.status as status");
    sb.append(" FROM wash_beauty_order wbo INNER JOIN bcuser.app_user_customer auc ");
    sb.append(" ON wbo.customer_id = auc.customer_id AND wbo.shop_id = auc.shop_id AND auc.app_user_no =:appUserNo");
    sb.append(" AND wbo.app_user_no is null");
    sb.append(" WHERE wbo.status in(:washBeautyStatus))");
    sb.append(" UNION ALL ");
    sb.append(" (SELECT DISTINCT wbo.id,wbo.created as vest_date ,'WASH_BEAUTY'as type,wbo.shop_id,wbo.status as status");
    sb.append(" FROM wash_beauty_order wbo ");
    sb.append(" where  wbo.app_user_no =:appUserNo");
    sb.append(" AND wbo.status in(:washBeautyStatus))");
    sb.append(" ORDER BY vest_date DESC ");
    Set<String> appointOrderStatus = new HashSet<String>();
    appointOrderStatus.add(AppointOrderStatus.PENDING.name());
    appointOrderStatus.add(AppointOrderStatus.ACCEPTED.name());
    appointOrderStatus.add(AppointOrderStatus.TO_DO_REPAIR.name());
    Set<String> repairStatus = new HashSet<String>();
    repairStatus.add(OrderStatus.REPAIR_DISPATCH.name());
    repairStatus.add(OrderStatus.REPAIR_DONE.name());
    repairStatus.add(OrderStatus.REPAIR_SETTLED.name());
    Set<String> washBeautyStatus = new HashSet<String>();
    washBeautyStatus.add(OrderStatus.WASH_SETTLED.name());
    return session.createSQLQuery(sb.toString())
        .setString("appUserNo", appUserNo)
        .setParameterList("appointOrderStatus", appointOrderStatus)
        .setParameterList("repairStatus", repairStatus)
        .setParameterList("washBeautyStatus", washBeautyStatus)
        .setFirstResult(pager.getRowStart())
        .setMaxResults(pager.getPageSize());
  }

  //返回   cr.order_type,cr.order_id,COUNT(cr.id)
  public static Query getOrderCommentCount(Session session, List<Pair<OrderTypes, Long>> orderTypeIdPairList) {
    StringBuilder sb = new StringBuilder();
    sb.append("SELECT cr.order_type,cr.order_id,COUNT(cr.id) FROM comment_record cr");
    boolean isStart = false;
    for (Pair<OrderTypes, Long> pair : orderTypeIdPairList) {
      if (pair != null && pair.getKey() != null && pair.getValue() != null) {
        if (isStart) {
          sb.append(" OR ");
        } else {
          isStart = true;
          sb.append(" WHERE ");
        }
        sb.append(" (cr.order_type = '")
            .append(pair.getKey().name())
            .append("' ")
            .append(" AND cr.order_id = ")
            .append(pair.getValue()).append(" )");
      }
    }
    return session.createSQLQuery(sb.toString())
        .setMaxResults(CollectionUtils.isNotEmpty(orderTypeIdPairList) ? orderTypeIdPairList.size() : 0);
  }


  public static Query getSupplierCommentByPagerAndKeyword(Session session, Long commentTargetShopId,Pager pager,CommentRecordDTO commentRecordDTO) {

    StringBuffer sb = new StringBuffer();
    sb.append(" from CommentRecord sa where sa.commentTargetShopId=:commentTargetShopId ");

    if(commentRecordDTO.getCommentTimeStart()!=null){
      sb.append("and sa.commentTime>=:commentTimeStart ");
    }
    if(commentRecordDTO.getCommentTimeEnd()!=null){
      sb.append("and sa.commentTime<:commentTimeEnd ");
    }
    if(commentRecordDTO.getCommentScores()!=null){
      sb.append("and sa.commentScore in(:commentScore) ");
    }
    if(commentRecordDTO.getOrderTypes()!=null){
      sb.append("and sa.orderType in(:orderType) ");
    }
    if(commentRecordDTO.getCustomerIds()!=null&&commentRecordDTO.getCustomerIds().size()!=0){
      sb.append("and sa.customerId in (:customerId) ");
    }
    if(StringUtils.isNotBlank(commentRecordDTO.getReceiptNo())){
      sb.append("and sa.receiptNo like:receiptNo ");
    }
    if(commentRecordDTO.getAddGoodCommentScores()!=null){
      sb.append("and sa.commentScore in(:addGoodCommentScore) ");
    }
    if(commentRecordDTO.getAddMediumCommentScores()!=null){
      sb.append("and sa.commentScore in(:addMediumCommentScore) ");
    }
    if(commentRecordDTO.getAddBadCommentScores()!=null){
      sb.append("and sa.commentScore in(:addBadCommentScore) ");
    }
    Sort sort = new Sort("commentTime", " desc ");
    sb.append(sort.toOrderString());
    Query q=session.createQuery(sb.toString()).setLong("commentTargetShopId", commentTargetShopId);
    if(commentRecordDTO.getCommentTimeStart()!=null){
      q.setLong("commentTimeStart",commentRecordDTO.getCommentTimeStart());
    }
    if(commentRecordDTO.getCommentTimeEnd()!=null){
      q.setLong("commentTimeEnd",commentRecordDTO.getCommentTimeEnd());
    }
    if(commentRecordDTO.getCommentScores()!=null){
      q.setParameterList("commentScore",commentRecordDTO.getCommentScores());
    }
    if(commentRecordDTO.getOrderTypes()!=null){
      q.setParameterList("orderType",commentRecordDTO.getOrderTypes());
    }
    if(commentRecordDTO.getCustomerIds()!=null&&commentRecordDTO.getCustomerIds().size()!=0){
      q.setParameterList("customerId",commentRecordDTO.getCustomerIds());
    }
    if(StringUtils.isNotBlank(commentRecordDTO.getReceiptNo())){
      q.setParameter("receiptNo","%"+commentRecordDTO.getReceiptNo()+"%");
    }
    if(commentRecordDTO.getAddGoodCommentScores()!=null){
      q.setParameterList("addGoodCommentScore",commentRecordDTO.getAddGoodCommentScores());
    }
    if(commentRecordDTO.getAddMediumCommentScores()!=null){
      q.setParameterList("addMediumCommentScore",commentRecordDTO.getAddMediumCommentScores());
    }
    if(commentRecordDTO.getAddBadCommentScores()!=null){
      q.setParameterList("addBadCommentScore",commentRecordDTO.getAddBadCommentScores());
    }
    q.setFirstResult(pager.getRowStart()).setMaxResults(pager.getPageSize());
    return q;
  }


  public static Query countSupplierCommentRecordByKeyword(Session session, Long commentTargetShopId,CommentRecordDTO commentRecordDTO) {
    StringBuffer sb = new StringBuffer();
    sb.append(" select count(*) from CommentRecord sa where sa.commentTargetShopId=:commentTargetShopId ");
    if(commentRecordDTO.getCommentTimeStart()!=null){
      sb.append("and sa.commentTime>=:commentTimeStart ");
    }
    if(commentRecordDTO.getCommentTimeEnd()!=null){
      sb.append("and sa.commentTime<:commentTimeEnd ");
    }
    if(commentRecordDTO.getCommentScores()!=null){
      sb.append("and sa.commentScore in(:commentScore) ");
    }
    if(commentRecordDTO.getOrderTypes()!=null){
      sb.append("and sa.orderType in(:orderType) ");
    }
    if(commentRecordDTO.getCustomerIds()!=null&&commentRecordDTO.getCustomerIds().size()!=0){
      sb.append("and sa.customerId in (:customerId) ");
    }
    if(StringUtils.isNotBlank(commentRecordDTO.getReceiptNo())){
      sb.append("and sa.receiptNo like:receiptNo ");
    }
    if(commentRecordDTO.getAddGoodCommentScores()!=null){
      sb.append("and sa.commentScore in(:addGoodCommentScore) ");
    }
    if(commentRecordDTO.getAddMediumCommentScores()!=null){
      sb.append("and sa.commentScore in(:addMediumCommentScore) ");
    }
    if(commentRecordDTO.getAddBadCommentScores()!=null){
      sb.append("and sa.commentScore in(:addBadCommentScore) ");
    }
    Query q = session.createQuery(sb.toString()).setLong("commentTargetShopId", commentTargetShopId);
    if(commentRecordDTO.getCommentTimeStart()!=null){
      q.setLong("commentTimeStart",commentRecordDTO.getCommentTimeStart());
    }
    if(commentRecordDTO.getCommentTimeEnd()!=null){
      q.setLong("commentTimeEnd",commentRecordDTO.getCommentTimeEnd());
    }
    if(commentRecordDTO.getCommentScores()!=null){
      q.setParameterList("commentScore",commentRecordDTO.getCommentScores());
    }
    if(commentRecordDTO.getOrderTypes()!=null){
      q.setParameterList("orderType",commentRecordDTO.getOrderTypes());
    }
    if(commentRecordDTO.getCustomerIds()!=null&&commentRecordDTO.getCustomerIds().size()!=0){
      q.setParameterList("customerId",commentRecordDTO.getCustomerIds());
    }
    if(StringUtils.isNotBlank(commentRecordDTO.getReceiptNo())){
      q.setParameter("receiptNo","%"+commentRecordDTO.getReceiptNo()+"%");
    }
    if(commentRecordDTO.getAddGoodCommentScores()!=null){
      q.setParameterList("addGoodCommentScore",commentRecordDTO.getAddGoodCommentScores());
    }
    if(commentRecordDTO.getAddMediumCommentScores()!=null){
      q.setParameterList("addMediumCommentScore",commentRecordDTO.getAddMediumCommentScores());
    }
    if(commentRecordDTO.getAddBadCommentScores()!=null){
      q.setParameterList("addBadCommentScore",commentRecordDTO.getAddBadCommentScores());
    }
    return q;

  }

  public static Query countCommentTypeRecordByKeyword(Session session, Long commentTargetShopId,CommentRecordDTO commentRecordDTO,String commentType) {
    StringBuffer sb = new StringBuffer();
    sb.append(" select count(*) from CommentRecord sa where sa.commentTargetShopId=:commentTargetShopId ");
    if(commentRecordDTO.getCommentTimeStart()!=null){
      sb.append("and sa.commentTime>=:commentTimeStart ");
    }
    if(commentRecordDTO.getCommentTimeEnd()!=null){
      sb.append("and sa.commentTime<:commentTimeEnd ");
    }
    if(commentRecordDTO.getOrderTypes()!=null){
      sb.append("and sa.orderType in(:orderType) ");
    }
    if(commentRecordDTO.getCustomerIds()!=null&&commentRecordDTO.getCustomerIds().size()!=0){
      sb.append("and sa.customerId in (:customerId) ");
    }
    if(commentRecordDTO.getCommentScores()!=null){
      sb.append("and sa.commentScore in(:commentScore) ");
    }
    if(commentType.trim().equals("badComment")){
      sb.append("and sa.commentScore in(1,2) ");
    }else if(commentType.trim().equals("mediumComment")){
      sb.append("and sa.commentScore in(3) ");
    }else if(commentType.trim().equals("goodComment")){
      sb.append("and sa.commentScore in(4,5) ");
    }
    if(StringUtils.isNotBlank(commentRecordDTO.getReceiptNo())){
      sb.append("and sa.receiptNo like:receiptNo ");
    }
    Query q = session.createQuery(sb.toString()).setLong("commentTargetShopId", commentTargetShopId);
    if(commentRecordDTO.getCommentTimeStart()!=null){
      q.setLong("commentTimeStart",commentRecordDTO.getCommentTimeStart());
    }
    if(commentRecordDTO.getCommentTimeEnd()!=null){
      q.setLong("commentTimeEnd",commentRecordDTO.getCommentTimeEnd());
    }
    if(commentRecordDTO.getOrderTypes()!=null){
      q.setParameterList("orderType",commentRecordDTO.getOrderTypes());
    }
    if(commentRecordDTO.getCustomerIds()!=null&&commentRecordDTO.getCustomerIds().size()!=0){
      q.setParameterList("customerId",commentRecordDTO.getCustomerIds());
    }
    if(commentRecordDTO.getCommentScores()!=null){
      q.setParameterList("commentScore",commentRecordDTO.getCommentScores());
    }
    if(StringUtils.isNotBlank(commentRecordDTO.getReceiptNo())){
      q.setParameter("receiptNo","%"+commentRecordDTO.getReceiptNo()+"%");
    }
    return q;

  }

  public static Query countSmsRecharge(Session session, SmsRechargeSearchCondition condition) {
    StringBuilder sb = new StringBuilder();
    sb.append("select count(*) ");
    return splittingSmsRechargeResultSql(session, condition, sb);
  }

  public static Query searchSmsRechargeResult(Session session, SmsRechargeSearchCondition condition) {
    StringBuilder sb = new StringBuilder();
    sb.append("select sr ");
    Query query = splittingSmsRechargeResultSql(session, condition, sb);
    query.setMaxResults(condition.getLimit()).setFirstResult(condition.getStart());
    return query;
  }

  private static Query splittingSmsRechargeResultSql(Session session, SmsRechargeSearchCondition condition, StringBuilder sql) {
    sql.append(" from SmsRecharge sr where sr.state = 2 ");
    if (CollectionUtil.isNotEmpty(condition.getShopIds())) {
      sql.append(" and sr.shopId in(:shopIds) ");
    }
    if (condition.getStartTime() != null) {
      sql.append(" and ").append(" sr.payTime >= ").append(condition.getStartTime());
    }
    if (condition.getEndTime() != null) {
      sql.append(" and ").append(" sr.payTime < ").append(condition.getEndTime());
    }
    if (StringUtils.isNotBlank(condition.getReceiptNo())) {
      sql.append(" and sr.receiptNo like :receiptNo ");
    }
    if (!ArrayUtils.isEmpty(condition.getRechargeMethods())) {
      sql.append(" and sr.rechargeMethod in (:rechargeMethods)");
    }
    if(!ArrayUtils.isEmpty(condition.getStatuses())) {
      sql.append(" and sr.status in (:statuses)");
    }
    if(StringUtils.isNotBlank(condition.getGroupField())){
      sql.append(" group by ").append(condition.getGroupField());
    }
    sql.append(" order by sr.payTime desc");
    Query query = session.createQuery(sql.toString());
    if (CollectionUtil.isNotEmpty(condition.getShopIds())){
      query.setParameterList("shopIds",condition.getShopIds());
    }
    if(StringUtils.isNotBlank(condition.getReceiptNo())){
      query.setString("receiptNo", "%" + condition.getReceiptNo() + "%");
    }
    if (!ArrayUtils.isEmpty(condition.getRechargeMethods())) {
      query.setParameterList("rechargeMethods",condition.getRechargeMethods());
    }
    if(!ArrayUtils.isEmpty(condition.getStatuses())) {
      query.setParameterList("statuses",condition.getStatuses());
    }
    return query;
  }

  public static Query statSmsRechargeByPaymentWay(Session session, SmsRechargeSearchCondition condition) {
    StringBuilder sql = new StringBuilder();
    sql.append("select sr.paymentWay,count(sr.id),sum(sr.rechargeAmount) ");
    condition.setGroupField("sr.paymentWay");
    return splittingSmsRechargeResultSql(session, condition, sql);
  }

  public static Query getSmsPreferentialPolicy(Session session) {
    StringBuilder sql = new StringBuilder();
    sql.append("select pp from PreferentialPolicy pp where pp.deletedType = 'FALSE' order by pp.rechargeAmount");
    return session.createQuery(sql.toString());
  }

  public static Query getShopSmsRecordBySmsId(Session session, Long shopId, Long smsId) {
    StringBuilder sql = new StringBuilder();
    sql.append("select ssr from ShopSmsRecord ssr where ssr.shopId =:shopId and ssr.smsId =:smsId");
    return session.createQuery(sql.toString()).setLong("shopId",shopId).setLong("smsId",smsId);
  }

  public static Query deleteTxnRemind(Session session, Long shopId, Long purchaseOrderId) {
    StringBuilder sql = new StringBuilder();
    sql.append("update RemindEvent set deletedType =:deletedType where shopId=:shopId and eventType='TXN' and orderId=:orderId");
    return session.createQuery(sql.toString()).setLong("shopId",shopId).setLong("orderId",purchaseOrderId).setParameter("deletedType",DeletedType.TRUE);
  }

  public static Query updateDebtRemindDeletedType(Session session, Long shopId, Long customerOrSupplierId, String identity,DeletedType deletedType) {
    StringBuilder sql = new StringBuilder();
    sql.append("update RemindEvent set deletedType =:deletedType where shopId=:shopId and eventType='DEBT' and ");
    if("customer".equals(identity)) {
      sql.append(" customerId=:customerOrSupplierId ");
    } else if("supplier".equals(identity)) {
      sql.append(" supplierId=:customerOrSupplierId ");
    }
    return session.createQuery(sql.toString()).setLong("shopId",shopId).setLong("customerOrSupplierId",customerOrSupplierId).setParameter("deletedType",deletedType);
  }

  public static Query countAppToShopCommentRecord(Session session, Long commentTargetShopId, CommentRecordType commentRecordType) {
    StringBuilder sql = new StringBuilder();
    sql.append("select count(id) from CommentRecord sa where sa.commentTargetShopId=:commentTargetShopId and sa.commentRecordType=:commentRecordType");
    return session.createQuery(sql.toString())
        .setLong("commentTargetShopId", commentTargetShopId).setParameter("commentRecordType", commentRecordType);
  }

  public static Query getCommentRecordByShopIdAndCommentRecordType(Session session, Long commentTargetShopId, Pager pager, CommentRecordType commentRecordType) {
    StringBuilder sql = new StringBuilder();
    sql.append("from CommentRecord sa where sa.commentTargetShopId=:commentTargetShopId and sa.commentRecordType=:commentRecordType");
    sql.append(" order by sa.commentTime desc");
    return session.createQuery(sql.toString())
        .setLong("commentTargetShopId", commentTargetShopId)
        .setParameter("commentRecordType", commentRecordType)
        .setFirstResult(pager.getRowStart())
        .setMaxResults(pager.getPageSize());
  }

  public static Query countStatDetailByNormalProductIds(Session session, Long[] shopIds, Long normalProductId, NormalProductStatType normalProductStatType) {
    StringBuffer sb = new StringBuffer(" select count(*) from NormalProductInventoryStat where normalProductId =:normalProductId  and shopId != -1 and normalProductStatType=:normalProductStatType  and productLocalInfoId is not null");
    if (ArrayUtil.isNotEmpty(shopIds)) {
      sb.append(" and shopId in(:shopId)  ");
    }
    Query query = session.createQuery(sb.toString()).setLong("normalProductId", normalProductId).setParameter("normalProductStatType", normalProductStatType);
    if (ArrayUtil.isNotEmpty(shopIds)) {
      query.setParameterList("shopId", shopIds);
    }
    return query;
  }

  public static Query getStatDetailByNormalProductIds(Session session, Long[] shopIds, Long normalProductId, NormalProductStatType normalProductStatType, Pager pager) {
    StringBuffer sb = new StringBuffer(" from NormalProductInventoryStat where normalProductId =:normalProductId and normalProductStatType=:normalProductStatType  and productLocalInfoId is not null ");
    if (ArrayUtil.isNotEmpty(shopIds)) {
      sb.append(" and shopId in(:shopId) ");
    }
    Query query = session.createQuery(sb.toString()).setLong("normalProductId", normalProductId).setFirstResult(pager.getRowStart()).setMaxResults(pager.getPageSize())
        .setParameter("normalProductStatType", normalProductStatType);
    if (ArrayUtil.isNotEmpty(shopIds)) {
      query.setParameterList("shopId", shopIds);
    }
    return query;
  }

  public static Query getProductTopPriceByProductIdTime(Session session, Long[] productId, Long startTime, Long endTime) {
    return session.createQuery("select pii.productId,max(pii.price),min(pii.price),pii.unit from PurchaseInventory pi, PurchaseInventoryItem pii " +
        "where pi.id = pii.purchaseInventoryId and pii.productId in(:productId) and pi.vestDate>=:startTime and pi.vestDate<:endTime " +
        "and pi.statusEnum=:status group by pii.productId,pii.unit ")
        .setParameterList("productId", productId).setLong("startTime", startTime).setLong("endTime", endTime)
        .setParameter("status", OrderStatus.PURCHASE_INVENTORY_DONE);
  }


 public static Query getAppointOrderMaterials(Session session, Long shopId, Long appointOrderId) {
    StringBuilder sql = new StringBuilder();
    sql.append("from AppointOrderMaterial aom where aom.shopId =:shopId and aom.appointOrderId =:appointOrderId ");
    sql.append(" and aom.status =:status");
    return session.createQuery(sql.toString())
        .setLong("shopId",shopId)
        .setLong("appointOrderId",appointOrderId)
        .setParameter("status",ObjectStatus.ENABLED);
  }

  public static Query getAppointOrderServiceDetails(Session session, Long shopId, Long appointOrderId) {
    StringBuilder sql = new StringBuilder();
    sql.append("from AppointOrderServiceDetail aos where aos.shopId =:shopId and aos.appointOrderId =:appointOrderId");
    sql.append(" and aos.status =:status");
    return session.createQuery(sql.toString())
        .setLong("shopId",shopId)
        .setParameter("status",ObjectStatus.ENABLED)
        .setLong("appointOrderId",appointOrderId);
  }

  public static Query countAdvertByDateStatus(Session session, Long shopId, Long startDate, Long endDate, AdvertStatus[] advertStatuses) {
    StringBuffer hql = new StringBuffer("select count(*) from Advert r where 1=1 ");

    if (null != shopId) {
      hql.append(" and r.shopId =:shopId ");
    }

    if (null != startDate) {
      hql.append(" and r.editDate >:startDate");
    }
    if (null != endDate) {
      hql.append(" and r.editDate <:endDate");
    }
    if (ArrayUtil.isNotEmpty(advertStatuses)) {
      hql.append(" and r.status in(:advertStatuses)");
    }

    Query query = session.createQuery(hql.toString());

    if (null != shopId) {
      query = query.setLong("shopId", shopId);
    }
    if (null != startDate) {
      query = query.setLong("startDate", startDate);
    }
    if (null != endDate) {
      query = query.setLong("endDate", endDate);
    }
    if (ArrayUtil.isNotEmpty(advertStatuses)) {
      query = query.setParameterList("advertStatuses", advertStatuses);
    }
    return query;
  }

  public static Query getAdvertByDateStatus(Session session, Long shopId, Long startDate, Long endDate, AdvertStatus[] advertStatuses, Pager pager) {
    StringBuffer hql = new StringBuffer(" from Advert r where 1=1 ");

    if (null != shopId) {
      hql.append(" and r.shopId =:shopId ");
    }


    if (null != startDate) {
      hql.append(" and r.editDate >:startDate");
    }
    if (null != endDate) {
      hql.append(" and r.editDate <:endDate");
    }
    if (ArrayUtil.isNotEmpty(advertStatuses)) {
      hql.append(" and r.status in(:advertStatuses)");
    }

    hql.append(" order by editDate desc ");

    Query query = session.createQuery(hql.toString()).setFirstResult(pager.getRowStart()).setMaxResults(pager.getPageSize());

    if (null != shopId) {
      query = query.setLong("shopId", shopId);
    }

    if (null != startDate) {
      query = query.setLong("startDate", startDate);
    }
    if (null != endDate) {
      query = query.setLong("endDate", endDate);
    }
    if (ArrayUtil.isNotEmpty(advertStatuses)) {
      query = query.setParameterList("advertStatuses", advertStatuses);
    }
    return query;
  }

  public static Query updateAdvertToOverdue(Session session, Long endDate) {
    StringBuffer hql = new StringBuffer(" update  Advert r set r.status = 'OVERDUE' where  r.endDate <=:endDate  and r.endDate is not null " +
        "and r.status = 'ACTIVE' ");

    Query query = session.createQuery(hql.toString()).setLong("endDate", endDate);

    return query;
  }

  public static Query countRemindMileageCustomerRemind(Session session, Long shopId) {
    StringBuffer sb = new StringBuffer();
    //未提醒，未过期的
    sb.append("select count(*) from RemindEvent r where r.eventType = 'CUSTOMER_SERVICE' and r.shopId = :shopId ");
    sb.append(" and r.remindMileage is not null and  r.remindMileage > 0 ");
    sb.append(" and r.remindStatus = 'activity'");
    return session.createQuery(sb.toString()).setLong("shopId", shopId);
  }

  public static Query getRemindMileageCustomerRemind(Session session, Long shopId, Pager pager) {
    StringBuffer sb = new StringBuffer();
    //未提醒，未过期的
    sb.append(" from RemindEvent r where r.eventType = 'CUSTOMER_SERVICE' and r.shopId = :shopId ");
    sb.append(" and r.remindMileage is not null and  r.remindMileage > 0 ");
    sb.append(" and r.remindStatus = 'activity' order by id desc ");
    return session.createQuery(sb.toString()).setLong("shopId", shopId).setFirstResult(pager.getRowStart()).setMaxResults(pager.getPageSize());
  }

  public static Query getPushMessageReceiverByMsgId(Session session,Long... ids) throws ParseException {
     StringBuffer sb = new StringBuffer("select pmr from PushMessage pm,PushMessageReceiver pmr");
     sb.append(" where pm.id=pmr.messageId and pm.id in(:ids)");
     Query query = session.createQuery(sb.toString())
       .setParameterList("ids", ids)
       ;
     return query;
   }

  public static Query getShopTalkMessageDTOByAppUserNo(Session session, Long shopId, String appUserNo) {
    StringBuilder sql = new StringBuilder();
    sql.append("from ShopTalkMessage m where shopId =:shopId and appUserNo =:appUserNo ");
    return session.createQuery(sql.toString())
      .setLong("shopId", shopId)
      .setParameter("appUserNo", appUserNo)
      ;
  }

  public static Query getShopFaultInfoByFaultCode(Session session, FaultInfoSearchConditionDTO searchCondition) {
    StringBuilder sb = new StringBuilder();
    sb.append("FROM FaultInfoToShop f  ");
    sb.append("WHERE shopId = :shopId  and status = 'ACTIVE' and faultCode = :faultCode and vehicleNo=:vehicleNo "); //and r.handle_status = :handleStatus
    Query query = session.createQuery(sb.toString()).setParameter("shopId", searchCondition.getShopId()).setParameter("faultCode",searchCondition.getCode())
        .setParameter("vehicleNo",searchCondition.getVehicleNo());   //.setParameter("handleStatus",handleStatus)
    return query;
  }

  public static Query updateReceivableCouponConsume(Session session, Long shopId, Long receivableId, Double couponConsume){
    StringBuilder sb=new StringBuilder();
    sb.append("UPDATE txn.receivable SET coupon = :couponConsume ");
    sb.append(" WHERE id = :receivableId AND shop_id = :shopId ");
    Query query = session.createSQLQuery(sb.toString()).setDouble("couponConsume", couponConsume).setLong("receivableId",receivableId).setLong("shopId",shopId);
    return query;
  }

}