package com.bcgogo.txn.model;

import com.bcgogo.api.AppOrderDTO;
import com.bcgogo.common.Pager;
import com.bcgogo.common.Pair;
import com.bcgogo.common.Sort;
import com.bcgogo.common.TwoTuple;
import com.bcgogo.enums.*;
import com.bcgogo.enums.Product.NormalProductStatType;
import com.bcgogo.enums.Product.ProductRelevanceStatus;
import com.bcgogo.enums.app.AppointOrderStatus;
import com.bcgogo.enums.app.EnquiryStatus;
import com.bcgogo.enums.app.EnquiryTargetShopStatus;
import com.bcgogo.enums.assistantStat.AchievementStatType;
import com.bcgogo.enums.assistantStat.AssistantRecordType;
import com.bcgogo.enums.shop.ShopKind;
import com.bcgogo.enums.sms.StatType;
import com.bcgogo.enums.supplierComment.CommentRecordType;
import com.bcgogo.enums.supplierComment.CommentStatus;
import com.bcgogo.enums.txn.AdvertStatus;
import com.bcgogo.enums.txn.Status;
import com.bcgogo.enums.txn.finance.PaymentMethod;
import com.bcgogo.enums.txn.finance.PaymentType;
import com.bcgogo.enums.txn.finance.ReceivableMethod;
import com.bcgogo.enums.txn.finance.SmsCategory;
import com.bcgogo.enums.txn.preBuyOrder.BusinessChanceType;
import com.bcgogo.enums.txn.pushMessage.PushMessageReceiverStatus;
import com.bcgogo.enums.txn.pushMessage.PushMessageScene;
import com.bcgogo.enums.txn.pushMessage.PushMessageSourceType;
import com.bcgogo.enums.txn.pushMessage.PushMessageType;
import com.bcgogo.payment.dto.RechargeSearchDTO;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.product.productManage.ProductSearchCondition;
import com.bcgogo.remind.dto.RemindEventDTO;
import com.bcgogo.remind.dto.message.SearchMessageCondition;
import com.bcgogo.search.dto.*;
import com.bcgogo.service.GenericWriterDao;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.stat.dto.*;
import com.bcgogo.txn.dto.*;
import com.bcgogo.txn.dto.StatementAccount.OrderDebtType;
import com.bcgogo.txn.dto.assistantStat.AssistantStatSearchDTO;
import com.bcgogo.txn.dto.enquiry.EnquirySearchConditionDTO;
import com.bcgogo.txn.dto.finance.*;
import com.bcgogo.txn.dto.pushMessage.PushMessageDTO;
import com.bcgogo.txn.dto.pushMessage.TalkMessageCondition;
import com.bcgogo.txn.dto.secondary.RepairOrderSecondaryCondition;
import com.bcgogo.txn.dto.supplierComment.CommentRecordDTO;
import com.bcgogo.txn.model.app.*;
import com.bcgogo.txn.model.assistantStat.*;
import com.bcgogo.txn.model.finance.*;
import com.bcgogo.txn.model.message.*;
import com.bcgogo.txn.model.pushMessage.*;
import com.bcgogo.txn.model.pushMessage.faultCode.FaultInfoToShop;
import com.bcgogo.txn.model.recommend.*;
import com.bcgogo.txn.model.secondary.*;
import com.bcgogo.txn.model.sql.MessageSQL;
import com.bcgogo.txn.model.supplierComment.CommentRecord;
import com.bcgogo.txn.model.supplierComment.CommentStat;
import com.bcgogo.user.dto.VehicleDTO;
import com.bcgogo.user.service.ICustomerService;
import com.bcgogo.user.service.IVehicleService;
import com.bcgogo.utils.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.transaction.support.ResourceTransactionManager;

import java.math.BigInteger;
import java.text.ParseException;
import java.util.*;

import static com.bcgogo.txn.model.SQL.*;

public class TxnWriter extends GenericWriterDao {

  public TxnWriter(ResourceTransactionManager transactionManager) {
    super(transactionManager);
  }

  /**
   * 获得对账单号
   */
  public String getStatementAccountOrderNo(Long shopId, Long statementAccountOrderId) {
    Session session = getSession();
    try {
      Query query = SQL.getStatementAccountOrderNo(session, shopId, statementAccountOrderId);
      if (query.list().size() > 0) {
        return (String) (query.list().get(0));
      } else {
        return "";
      }
    } finally {
      release(session);
    }
  }

  /**
   * 根据订单类型和订单ID查询该单据历史结算记录
   *
   * @param shopId
   * @param orderTypeEnum
   * @param orderId
   * @return
   */

  public List getSettledRecord(Long shopId, OrderTypes orderTypeEnum, Long orderId) {
    Session session = getSession();
    try {
      Query query = SQL.getSettledRecord(session, shopId, orderTypeEnum, orderId);
      if (OrderTypes.RETURN.equals(orderTypeEnum) || OrderTypes.INVENTORY.equals(orderTypeEnum)) {
        return (List<PayableHistoryRecord>) query.list();
      } else {
        return (List<ReceptionRecord>) query.list();
      }

    } finally {
      release(session);
    }
  }

  /**
   * 根据采购单ID查询采购单货品表
   *
   * @param purchaseOrderId
   * @return
   */
  public List<PurchaseOrderItem> getPurchaseOrderItemsByOrderId(Long purchaseOrderId) {

    Session session = getSession();
    try {
      Query q = SQL.getPurchaseOrderItemsByOrderId(session, purchaseOrderId);
      return (List<PurchaseOrderItem>) q.list();
    } finally {
      release(session);
    }
  }

  /**
   * 根据销售单ID查询销售单货品表
   *
   * @param salesOrderId
   * @return
   */
  public List<SalesOrderItem> getSalesOrderItemsByOrderId(Long salesOrderId) {
    Session session = getSession();
    try {
      Query q = SQL.getSalesOrderItemsByOrderId(session, salesOrderId);
      return (List<SalesOrderItem>) q.list();
    } finally {
      release(session);
    }
  }

  /**
   * 根据入度库单ID查询入库单货品表
   *
   * @param purchaseInventoryId
   * @return
   */
  public List<PurchaseInventoryItem> getPurchaseInventoryItemsByInventoryId(Long purchaseInventoryId) {

    Session session = getSession();
    try {
      Query q = SQL.getPurchaseInventoryItemsByInventoryId(session, purchaseInventoryId);
      return (List<PurchaseInventoryItem>) q.list();
    } finally {
      release(session);
    }
  }


  public List<PurchaseReturnItem> getPurchaseReturnItemsByReturnId(Long purchaseReturnId) {
    Session session = getSession();
    try {
      Query q = SQL.getPurchaseReturnItemsByReturnId(session, purchaseReturnId);
      return (List<PurchaseReturnItem>) q.list();
    } finally {
      release(session);
    }
  }

  /**
   * 根据入度库单ID，ShopID查询入库单货品表
   *
   * @param shopId
   * @param purchaseInventoryId
   * @return
   */
  public List<PurchaseInventory> getPurchaseInventoryByInventoryId(Long shopId, Long purchaseInventoryId) {

    Session session = getSession();
    try {
      Query q = getpurchaseInventoryByShopIdAndPurchasseInventoryId(session, shopId, purchaseInventoryId);
      return (List<PurchaseInventory>) q.list();
    } finally {
      release(session);
    }
  }

  /**
   * 根据汽修车饰单ID查询汽修车饰单货品表
   *
   * @param repairOrderId
   * @return
   */
  public List<RepairOrderItem> getRepairOrderItemsByRepairOrderId(Long repairOrderId) {

    Session session = getSession();
    try {
      Query q = SQL.getRepairOrderItemsByRepairOrderId(session, repairOrderId);
      return (List<RepairOrderItem>) q.list();
    } finally {
      release(session);
    }
  }

  /**
   * 根据汽修车饰单ID,商品id，查询汽修车饰单货品表
   *
   * @param repairOrderId
   * @param productId
   * @return
   */
  public List<RepairOrderItem> getRepairOrderItemsByRepairOrderIdAndProductId(Long repairOrderId, Long productId) {

    Session session = getSession();
    try {
      Query q = SQL.getRepairOrderItemsByRepairOrderIdAndProductId(session, repairOrderId, productId);
      return (List<RepairOrderItem>) q.list();
    } finally {
      release(session);
    }
  }

  /**
   * 根据汽修车饰单ID,商品id，查询汽修车饰单货品表
   *
   * @param repairOrderId
   * @param productId
   * @return
   */
  public List<RepairOrderItem> getRepairOrderItemsByRepairOrderIdAndProductIdAndStoreHouse(Long repairOrderId, Long productId, Long storehouseId) {

    Session session = getSession();
    try {
      Query q = SQL.getRepairOrderItemsByRepairOrderIdAndProductIdAndStoreHouse(session, repairOrderId, productId, storehouseId);
      return (List<RepairOrderItem>) q.list();
    } finally {
      release(session);
    }
  }

  /**
   * 根据汽修车饰单ID查询汽修车饰单服务项目表
   *
   * @param repairOrderId
   * @return
   */
  public List<RepairOrderService> getRepairOrderServicesByRepairOrderId(Long repairOrderId) {

    Session session = getSession();
    try {
      Query q = SQL.getRepairOrderServicesByRepairOrderId(session, repairOrderId);
      return (List<RepairOrderService>) q.list();
    } finally {
      release(session);
    }
  }

  public RepairOrder getUnbalancedAccountRepairOrderByVehicleNumber(Long shopId, Long vehicleId, Long orderId) {
    Session session = getSession();
    try {
      Query q = SQL.getUnbalancedAccountRepairOrderByVehicleNumber(session, shopId, vehicleId, orderId);
      return (RepairOrder) q.uniqueResult();
    } finally {
      release(session);
    }
  }

  /**
   * 根据施工单ID查询施工单单货品表
   *
   * @param repairOrderId
   * @return
   */
  public List<RepairOrderItem> getRepairOrderItemsByOrderId(Long repairOrderId) {

    Session session = getSession();
    try {
      Query q = SQL.getRepairOrderItemsByOrderId(session, repairOrderId);
      return (List<RepairOrderItem>) q.list();
    } finally {
      release(session);
    }
  }

  /**
   * 根据施工单ID查询施工单服务表
   *
   * @param repairOrderId
   * @return
   */

  public List<RepairOrderService> getRepairOrderServicesByOrderId(Long repairOrderId) {

    Session session = getSession();
    try {
      Query q = SQL.getRepairOrderServicesByOrderId(session, repairOrderId);
      return (List<RepairOrderService>) q.list();
    } finally {
      release(session);
    }
  }

  /**
   * 根据店面ID单据类型和单据Id查询收款单
   *
   * @param shopId
   * @param orderType
   * @param orderId
   * @return
   */
  public Receivable getReceivableByShopIdAndOrderTypeAndOrderId(Long shopId, OrderTypes orderType, Long orderId) {

    Session session = getSession();
    try {
      Query q = SQL.getReceivableByShopIdAndOrderTypeAndOrderId(session, shopId, orderType, orderId);
      List list = q.list();
      if (list.size() > 0) {
        return (Receivable) list.get(0);
      } else {
        return null;
      }
    } finally {
      release(session);
    }
  }

  /**
   * 根据收款单ID查询收款单记录
   *
   * @param receivableId
   * @return
   */
  public List<ReceptionRecord> getReceptionRecordsByReceivalbeId(Long receivableId) {

    Session session = getSession();
    try {
      Query q = SQL.getReceptionRecordsByReceivalbeId(session, receivableId);
      return (List<ReceptionRecord>) q.list();
    } finally {
      release(session);
    }
  }

  /**
   * 根据店面Id,维修单Id,事件类型查询维修事件提醒表
   *
   * @param shopId
   * @param repairOrderId
   * @param eventType
   * @return
   */
  public List<RepairRemindEvent> getRepairRemindEventByShopIdAndOrderIdAndType(Long shopId, Long repairOrderId, RepairRemindEventTypes eventType) {

    Session session = getSession();
    try {
      Query q = SQL.getRepairRemindEventByShopIdAndOrderIdAndType(session, shopId, repairOrderId, eventType);
      return (List<RepairRemindEvent>) q.list();
    } finally {
      release(session);
    }
  }

  public List<RepairRemindEvent> getRepairRemindEvents(Long shopId, RepairRemindEventTypes eventType, Long pagNo, Long pageSize) {
    Session session = getSession();
    try {
      Query q = SQL.getRepairRemindEvents(session, shopId, eventType, pagNo, pageSize);
      return (List<RepairRemindEvent>) q.list();
    } finally {
      release(session);
    }
  }

  public List<RepairRemindEvent> getAllRepairRemindEventsByCustomerIds(Long shopId, Long[] customerIds) {
    Session session = getSession();
    try {
      Query q = SQL.getAllRepairRemindEventsByCustomerIds(session, shopId, customerIds);
      return (List<RepairRemindEvent>) q.list();
    } finally {
      release(session);
    }
  }

  public int countRepairRemindEvents(Long shopId, RepairRemindEventTypes eventType) {
    Session session = getSession();
    try {
      Query q = SQL.countRepairRemindEvents(session, shopId, eventType);
      return Integer.parseInt(q.uniqueResult().toString());
    } finally {
      release(session);
    }
  }

  public List<RepairRemindEvent> getRepairRemindEventsByProductId(Long shopId, RepairRemindEventTypes eventType, Long productId) {
    Session session = getSession();
    try {
      Query q = SQL.getRepairRemindEventsByProductId(session, shopId, eventType, productId);
      return (List<RepairRemindEvent>) q.list();
    } finally {
      release(session);
    }
  }

  /**
   * 根据店面Id,客户Id,车辆id查询车辆预约服务
   *
   * @param shopId
   * @param customerId
   * @param vehicleId
   * @return
   */
  public List<ScheduleServiceEvent> getScheduleServiceEventByShopIdAndCustomerIdAndVehicleId(Long shopId, Long customerId, Long vehicleId) {

    Session session = getSession();
    try {
      Query q = SQL.getScheduleServiceEventByShopIdAndCustomerIdAndVehicleId(session, shopId, customerId, vehicleId);
      return (List<ScheduleServiceEvent>) q.list();
    } finally {
      release(session);
    }
  }

  /**
   * 根据店面Id,事件类型,产品id更新维修事件提醒表
   *
   * @param shopId
   * @param eventType
   * @param productId
   * @return
   */
  public boolean updateRepairRemindEventByShopIdAndTypeAndProductId(Long shopId, RepairRemindEventTypes eventType, Long productId, RepairRemindEventTypes targetEventType, Long repairOrderId) {
    Session session = getSession();
    try {
      Query q = SQL.updateRepairRemindEventByShopIdAndTypeAndProductId(session, shopId, eventType, productId, targetEventType, repairOrderId);
      int count = q.executeUpdate();
      if (count > 0)
        return true;
      else
        return false;
    } finally {
      release(session);
    }
  }

  /**
   * 根据店面Id,事件类型,维修单id删除维修事件提醒表
   *
   * @param shopId
   * @param repairOrderId
   * @param eventType
   * @return
   */
  public boolean deleteRepairRemindEventByShopIdAndRepairOrderIdAndType(Long shopId, Long repairOrderId, RepairRemindEventTypes eventType) {
    Session session = getSession();
    try {
      Query q = SQL.deleteRepairRemindEventByShopIdAndRepairOrderIdAndType(session, shopId, repairOrderId, eventType);
      int count = q.executeUpdate();
      if (count > 0)
        return true;
      else
        return false;
    } finally {
      release(session);
    }
  }

  /**
   * 根据店面Id,事件类型,维修单id删除维修事件提醒表
   *
   * @param shopId
   * @param repairOrderId
   * @return
   */
  public boolean deleteRepairRemindEventByShopIdAndRepairOrderIdAndType(Long shopId, Long repairOrderId) {
    Session session = getSession();
    try {
      Query q = SQL.deleteRepairRemindEventByShopIdAndRepairOrderIdAndType(session, shopId, repairOrderId);
      int count = q.executeUpdate();
      if (count > 0)
        return true;
      else
        return false;
    } finally {
      release(session);
    }
  }

  /**
   * 根据店面Id,事件类型,事件内容,维修单id删除维修事件提醒表
   *
   * @param shopId
   * @param repairOrderId
   * @param eventType
   * @return
   */
  public boolean deleteRepairRemindEventByShopIdAndRepairOrderIdAndTypeAndContent(Long shopId, Long repairOrderId, RepairRemindEventTypes eventType, Long eventContent) {
    Session session = getSession();
    try {
      Query q = SQL.deleteRepairRemindEventByShopIdAndRepairOrderIdAndTypeAndContent(session, shopId, repairOrderId, eventType, eventContent);
      int count = q.executeUpdate();
      if (count > 0)
        return true;
      else
        return false;
    } finally {
      release(session);
    }
  }

  /**
   * 根据店面ID,维修单ID,商品ID删除记录
   *
   * @param shopId
   * @param productId
   * @param repairOrderId
   * @return
   */
  public boolean deleteRepairRemindEventByShopIdAndAndRepairOrderIdAndProductId(Long shopId, Long repairOrderId, Long productId) {
    Session session = getSession();
    try {
      Query q = SQL.deleteRepairRemindEventByShopIdAndAndRepairOrderIdAndProductId(session, shopId, repairOrderId, productId);
      int count = q.executeUpdate();
      if (count > 0)
        return true;
      else
        return false;
    } finally {
      release(session);
    }
  }

  /**
   * 根据shopId,productId查询货品采购价记录表
   *
   * @param shopId
   * @param productId
   * @return
   */
  public PurchasePrice getLatestPurchasePriceByShopIdAndProductId(Long shopId, Long productId) {
    Session session = getSession();
    try {
      Query q = SQL.getLatestPurchasePriceByShopIdAndProductId(session, shopId, productId);
      if (q != null) {
        List result = q.list();
        if (CollectionUtils.isNotEmpty(result)) {
          return (PurchasePrice) result.get(0);
        }
      }
      return null;
    } finally {
      release(session);
    }
  }

  /**
   * 根据店面Id,采购单id删除采购入库事件提醒表
   *
   * @param shopId
   * @param purchaseOrderId
   * @return
   */
  public boolean deleteInventoryRemindEventByShopIdAndPurchaseOrderId(Long shopId, Long purchaseOrderId) {
    Session session = getSession();
    try {
      Query q = SQL.deleteInventoryRemindEventByShopIdAndPurchaseOrderId(session, shopId, purchaseOrderId);
      int count = q.executeUpdate();
      if (count > 0)
        return true;
      else
        return false;
    } finally {
      release(session);
    }
  }

  public void updateRepairOrderStatus(Long shopId, Long orderId, OrderStatus status) {
    Session session = getSession();
    try {
      Query q = SQL.updateRepairOrderStatus(session, shopId, orderId, status);
      q.executeUpdate();
    } finally {
      release(session);
    }
  }

  public int countInventoryRemindEventByShopIdAndPageNoAndPageSize(Long shopId) {
    Session session = getSession();
    try {
      Query q = SQL.countInventoryRemindEventByShopIdAndPageNoAndPageSize(session, shopId);
      return Integer.valueOf(q.uniqueResult().toString()).intValue();
    } finally {
      release(session);
    }
  }

  public List<InventoryRemindEvent> getInventoryRemindEventByShopIdAndPageNoAndPageSize(Long shopId, Integer pageNo, Integer pageSize) {
    Session session = getSession();
    try {
      Query q = SQL.getInventoryRemindEventByShopIdAndPageNoAndPageSize(session, shopId, pageNo, pageSize);
      return (List<InventoryRemindEvent>) q.list();
    } finally {
      release(session);
    }
  }


  public List<RepairRemindEvent> getRepairRemindEventByShopId(Long shopId, RepairRemindEventTypes eventType, Long[] productId, int pageNo, int pageSize) {
    Session session = getSession();
    try {
      Query query = SQL.getRepairRemindEventByShopId(session, shopId, eventType, productId, pageNo, pageSize);
      List repairRemindEventList = query.list();
      return repairRemindEventList;
    } finally {
      release(session);
    }
  }

  public int countRepairRemindEventByShopId(Long shopId, RepairRemindEventTypes eventType, Long[] productId) {
    Session session = getSession();
    try {
      Query query = SQL.countRepairRemindEventByShopId(session, shopId, eventType, productId);
      int count = Integer.valueOf(query.uniqueResult().toString());
      return count;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return 0;
    } finally {
      release(session);
    }
  }

  /*
 根据客户ID获取客户洗车记录
  */
  public List<WashOrder> getCustomerWashOrders(long customerId) {
    Session session = getSession();
    try {
      Query q = null;
      q = getWashOrderByCustomer(session, customerId);

      return (List<WashOrder>) q.list();
    } finally {
      release(session);
    }
  }

  /*
 查询当天洗车次数
  */
  public int getTodayWashTimes(long customerId) {
    Session session = getSession();
    try {
      Query q = null;
      q = SQL.getTodayWashTimes(session, customerId);
      List<WashOrder> washOrderList = q.list();
      if (washOrderList != null && washOrderList.size() > 0)
        return washOrderList.size();
      else
        return 0;
    } finally {
      release(session);
    }
  }

  /**
   * 根据shop_id order_id查询欠款
   *
   * @param shopId
   * @param orderId
   * @return
   */
  public List<Debt> getDebtByShopIdAndOrderId(Long shopId, Long orderId) {
    Session session = getSession();
    try {
      Query q = SQL.getDebtByShopIdAndOrderId(session, shopId, orderId);
      if (q != null) {
        List result = q.list();
        if (CollectionUtils.isNotEmpty(result)) {
          return (List<Debt>) result;
        }
      }
      return null;

    } finally {
      release(session);
    }
  }


  public List<Payable> getAllPayables() {
    Session session = getSession();
    try {
      Query q = SQL.getAllPayables(session);
      return (List<Payable>) q.list();
    } finally {
      release(session);
    }
  }

  public Double getTotalPayable(RecOrPayIndexDTO recOrPayIndexDTO) {
    Session session = getSession();
    try {
      Query q = SQL.getTotalPayable(session, recOrPayIndexDTO);
      return (Double) q.uniqueResult();
    } finally {
      release(session);
    }
  }


  public List<Payable> getPayables(RecOrPayIndexDTO recOrPayIndexDTO) {
    Session session = getSession();
    try {
      Query q = SQL.getPayables(session, recOrPayIndexDTO);
      return (List<Payable>) q.list();
    } finally {
      release(session);
    }
  }

  public List<Debt> getAllDebtsByCustomerIds(Long shopId, Long[] customerIds) {
    Session session = getSession();
    try {
      Query q = SQL.getAllDebtsByCustomerIds(session, shopId, customerIds);
      return (List<Debt>) q.list();
    } finally {
      release(session);
    }
  }

  public List<Receivable> getAllReceivablesByCustomerIds(Long shopId, Long[] customerIds) {
    Session session = getSession();
    try {
      Query q = SQL.getAllReceivablesByCustomerIds(session, shopId, customerIds);
      return (List<Receivable>) q.list();
    } finally {
      release(session);
    }
  }

  public List<Payable> getAllPayablesBySupplierIds(Long shopId, Long[] supplierIds) {

    Session session = getSession();
    try {
      Query q = SQL.getAllPayablesBySupplierIds(session, shopId, supplierIds);
      return (List<Payable>) q.list();
    } finally {
      release(session);
    }
  }

  /**
   * 根据shop_id customer_id order_id查询欠款
   *
   * @param shopId
   * @param customerId
   * @param orderId
   * @return
   */
  public Debt getDebtByShopIdAndCustomerIdAndOrderId(Long shopId, Long customerId, Long orderId) {
    Session session = this.getSession();
    try {
      Query q = null;
      q = SQL.getDebtByShopIdAndCustomerIdAndOrderId(session, shopId, customerId, orderId);
      if (q != null) {
        List result = q.list();
        if (CollectionUtils.isNotEmpty(result)) {
          return (Debt) q.uniqueResult();
        }
      }
      return null;
    } finally {
      release(session);
    }
  }


  public int countNoSettlementRepairOrder(Long shopId) {
    Session session = getSession();
    try {
      Query q = SQL.countNoSettlementRepairOrder(session, shopId);
      Long count = (Long) q.uniqueResult();
      if (count == null) {
        return 0;
      }
      return count.intValue();
    } finally {
      release(session);
    }
  }

  /*
 * 短信充值——开始
 * */
  //根据店面ID获取短信充值的列表
  public List<SmsRecharge> getSmsRechargeByShopId(long shopId) {
    Session session = getSession();
    try {
      Query q = SQL.getSmsRechargeByShopId(session, shopId);

      return (List<SmsRecharge>) q.list();
    } finally {
      release(session);
    }
  }

  //根据店面ID获取短信充值记录数
  public int countShopSmsRecharge(long shopId) {
    Session session = getSession();
    try {
      Query q = SQL.countShopSmsRecharge(session, shopId);

      Object o = q.uniqueResult();
      if (o == null) return 0;
      return Integer.parseInt(o.toString());
    } finally {
      release(session);
    }
  }

  //根据店面ID、页码与每页条数获取短信充值的列表
  public List<SmsRecharge> getShopSmsRechargeList(long shopId, int pageNo, int pageSize) {
    Session session = getSession();
    try {
      Query q = SQL.getShopSmsRechargeList(session, shopId, pageNo, pageSize);

      return (List<SmsRecharge>) q.list();
    } finally {
      release(session);
    }
  }

  //根据店面ID获取短信充值的列表
  public List<SmsRecharge> getSmsRechargeByRechargeNumber(String rechargeNumber) {
    Session session = getSession();
    try {
      Query q = SQL.getSmsRechargeByRechargeNumber(session, rechargeNumber);

      return (List<SmsRecharge>) q.list();
    } finally {
      release(session);
    }
  }

  //根据充值序号更新payTime
  public void updateSmsRechargePayTime(Long payTime, String rechargeNumber) {
    Session session = getSession();
    try {
      session.beginTransaction();
      Query q = SQL.updateSmsRechargePayTime(session, payTime, rechargeNumber);
      q.executeUpdate();
      session.getTransaction().commit();

    } finally {
      release(session);
    }
  }

  public int countInventoryRemindEventNumber(Long shopId) throws Exception {
    Session session = getSession();
    try {
      Query q = SQL.countInventoryRemindEventNumber(session, shopId);
      return ((Long) q.uniqueResult()).intValue();
    } finally {
      release(session);
    }
  }

  public int countInventoryNumber(Long shopId) throws Exception {
    Session session = getSession();
    try {
      Query q = SQL.countInventoryNumber(session, shopId);
      return ((Long) q.uniqueResult()).intValue();
    } finally {
      release(session);
    }
  }

  public List<Service> getServiceByShopId(Long shopId) {
    Session session = getSession();
    try {
      Query q = SQL.getServiceByShopId(session, shopId);
      return (List<Service>) q.list();
    } finally {
      release(session);
    }
  }

  public List<Service> getServiceListById(Long shopId, Long... serviceIds) {
    Session session = getSession();
    try {
      Query q = SQL.getServiceListById(session, shopId, serviceIds);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<Service> getServiceByShopIdAndSearchKey(Long shopId, String searchKey) {
    Session session = getSession();
    try {
      Query q = SQL.getServiceByShopIdAndSearchKey(session, shopId, searchKey);
      return (List<Service>) q.list();
    } finally {
      release(session);
    }
  }

  public List<Service> getServiceByShopIdAndName(Long shopId, String name) {
    Session session = getSession();
    try {
      Query q = SQL.getServiceByShopIdAndName(session, shopId, name);
      return (List<Service>) q.list();
    } finally {
      release(session);
    }
  }

  //邵磊
  public List<TemBlance> getSmsByShopId(int pageNo, int pageSize) {
    Session session = this.getSession();
    try {
      Query sql = SQL.getSmsByShopId(session, pageNo, pageSize);
      return (List<TemBlance>) sql.list();
    } finally {
      release(session);
    }
  }

  public int coutSms() {
    Session session = this.getSession();
    try {
      Query sql = SQL.coutSms(session);
      return Integer.parseInt(sql.uniqueResult().toString());
    } finally {
      release(session);
    }
  }

  public List<RepairRemindEvent> getLackProductIdsByRepairOderId(Long repairOrderId, Long shopId, RepairRemindEventTypes eventType) {
    Session session = this.getSession();
    try {
      Query hql = SQL.getLackProductIdsByRepairOderId(session, repairOrderId, shopId, eventType);
      return (List<RepairRemindEvent>) hql.list();
    } finally {
      release(session);
    }
  }

  public List<BusinessStat> getBusinessStatByYearMonthDay(long shopId, long year, long month, long day) {
    Session session = this.getSession();
    try {
      Query hql = SQL.getBusinessStatByYearMonthDay(session, shopId, year, month, day);
      return (List<BusinessStat>) hql.list();
    } finally {
      release(session);
    }
  }

  public List<ExpendDetail> getExpendDetailByYearMonthDay(long shopId, long year, long month, long day) {
    Session session = this.getSession();
    try {
      Query hql = SQL.getExpendDetailByYearMonthDay(session, shopId, year, month, day);
      return (List<ExpendDetail>) hql.list();
    } finally {
      release(session);
    }
  }

  public List<ExpendDetail> getExpendDetailByYearMonth(long shopId, long year, long month, long day) {
    Session session = this.getSession();
    try {
      Query hql = SQL.getExpendDetailByYearMonth(session, shopId, year, month, day);
      return (List<ExpendDetail>) hql.list();
    } finally {
      release(session);
    }
  }

  public List<ExpendDetail> getExpendDetailByYearFromStartMonthToEndMonth(long shopId, long year, long startMonth, long endMonth) {
    Session session = this.getSession();
    try {
      Query hql = SQL.getExpendDetailByYearFromStartMonthToEndMonth(session, shopId, year, startMonth, endMonth);
      return (List<ExpendDetail>) hql.list();
    } finally {
      release(session);
    }
  }

  public List<BusinessStat> getLatestBusinessStat(long shopId, Long year, int size) {
    Session session = this.getSession();
    try {
      Query hql = SQL.getLatestBusinessStat(session, shopId, year, size);
      return (List<BusinessStat>) hql.list();
    } finally {
      release(session);
    }
  }

//   public void deleteBusinessStatByYearMonthDay(long shopId, long year, long month,long day) {
//    Session session = this.getSession();
//    try {
//      Query q = SQL.deleteBusinessStatByYearMonthDay(session, shopId, year, month, day);
//      q.executeUpdate();
//    } finally {
//      release(session);
//    }
//  }

  public List<BusinessStat> getEarliestBusinessStat(long shopId, long year, int size) {
    Session session = this.getSession();
    try {
      Query hql = SQL.getEarliestBusinessStat(session, shopId, year, size);
      return (List<BusinessStat>) hql.list();
    } finally {
      release(session);
    }
  }

  public List<BusinessStat> getLatestBusinessStatMonth(long shopId, long year, long month, int size) {
    Session session = this.getSession();
    try {
      Query hql = SQL.getLatestBusinessStatMonth(session, shopId, year, month, size);
      return (List<BusinessStat>) hql.list();
    } finally {
      release(session);
    }
  }

  public List<BusinessStat> getBusinessStatMonth(long shopId, long year, String queryString) {
    Session session = this.getSession();
    try {
      Query hql = SQL.getBusinessStatMonth(session, shopId, year, queryString);
      return (List<BusinessStat>) hql.list();
    } finally {
      release(session);
    }
  }

  public List<BusinessStat> getBusinessStatMonthEveryDay(long shopId, long year, long month, long day) {
    Session session = this.getSession();
    try {
      Query hql = SQL.getBusinessStatMonthEveryDay(session, shopId, year, month, day);
      return (List<BusinessStat>) hql.list();
    } finally {
      release(session);
    }
  }


  public void deleteExpendDetailByYearMonth(long shopId, long year, long month, Long expendDetailId) {
    Session session = this.getSession();
    try {
      Query q = SQL.deleteExpendDetailByYearMonth(session, shopId, year, month, expendDetailId);
      q.executeUpdate();
    } finally {
      release(session);
    }
  }

  public void deleteBusinessStatByYearMonthDay(long shopId, long year, long month, long day) {
    Session session = this.getSession();
    try {
      Query q = SQL.deleteBusinessStatByYearMonthDay(session, shopId, year, month, day);
      q.executeUpdate();
    } finally {
      release(session);
    }
  }

  public List<RepairOrderItem> countShopRepairOrderSalesIncome(long shopId, long startTime, long endTime) {
    Session session = this.getSession();

    try {
      Query hql = SQL.countShopRepairOrderSalesIncome(session, shopId, startTime, endTime);
      return (List<RepairOrderItem>) hql.list();
    } finally {
      release(session);
    }

  }

  public List<RepairOrderService> countShopRepairOrderServiceIncome(long shopId, long startTime, long endTime) {
    Session session = this.getSession();

    try {
      Query hql = SQL.countShopRepairOrderServiceIncome(session, shopId, startTime, endTime);
      return (List<RepairOrderService>) hql.list();
    } finally {
      release(session);
    }

  }

  public List<SalesOrderItem> countShopSalesIncome(long shopId, long startTime, long endTime) {
    Session session = this.getSession();

    try {
      Query hql = SQL.countShopSalesIncome(session, shopId, startTime, endTime);
      return (List<SalesOrderItem>) hql.list();
    } finally {
      release(session);
    }

  }


  public List<SalesOrderItem> countShopSalesIncomeByShopId(long shopId, long startTime, long endTime) {
    Session session = this.getSession();

    try {
      Query hql = SQL.countShopSalesIncomeByShopId(session, shopId, startTime, endTime);
      return (List<SalesOrderItem>) hql.list();
    } finally {
      release(session);
    }

  }

  public List<PurchaseOrder> getPurchaseOrderById(Long id, Long shopId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getPurchaseOrderById(session, id, shopId);
      return (List<PurchaseOrder>) q.list();
    } finally {
      release(session);
    }
  }

  public List<PurchaseOrder> getPurchaseOrderBySupplierShopIdAndIds(Long supplierShopId, Long... ids) {
    if (supplierShopId == null || ArrayUtils.isEmpty(ids)) {
      return new ArrayList<PurchaseOrder>();
    }
    Session session = this.getSession();
    try {
      Query q = SQL.getPurchaseOrderBySupplierShopIdAndIds(session, supplierShopId, ids);
      return (List<PurchaseOrder>) q.list();
    } finally {
      release(session);
    }
  }

  public List<PurchaseInventory> getPurchaseInventoryById(Long purchaseInventoryId, Long shopId) {
    Session session = this.getSession();
    try {
      Query q = getpurchaseInventoryById(session, purchaseInventoryId, shopId);
      return (List<PurchaseInventory>) q.list();
    } finally {
      release(session);
    }
  }

  public List<PurchaseInventory> getPurchaseInventoryByShopIdAndSupplierId(Long shopId, Long SupplierId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getPurchaseInventoryByShopIdAndSupplierId(session, shopId, SupplierId);
      return (List<PurchaseInventory>) q.list();
    } finally {
      release(session);
    }
  }


  public List<PurchaseOrder> getPurchaseOrderByShopIdAndSupplierId(Long shopId, Long SupplierId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getPurchaseOrderByShopIdAndSupplierId(session, shopId, SupplierId);
      return (List<PurchaseOrder>) q.list();
    } finally {
      release(session);
    }
  }

  public List<SalesOrder> getSalesOrderById(Long salesOrderId, Long shopId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getSalesOrderById(session, salesOrderId, shopId);
      return (List<SalesOrder>) q.list();
    } finally {
      release(session);
    }
  }

  public List<SalesOrder> getSalesOrderByPurchaseOrderId(Long purchaseOrderId, Long supplierShopId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getSalesOrderByPurchaseOrderId(session, purchaseOrderId, supplierShopId);
      return (List<SalesOrder>) q.list();
    } finally {
      release(session);
    }
  }

  public List<SalesOrder> getSalesOrderByPurchaseOrderId(Long purchaseOrderId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getSalesOrderByPurchaseOrderId(session, purchaseOrderId);
      return (List<SalesOrder>) q.list();
    } finally {
      release(session);
    }
  }

  public List<RepairRemindEvent> getLackMaterialByProductId(Long shopId, RepairRemindEventTypes eventType, Long productId) {
    Session session = this.getSession();
    try {
      Query q = getRepairRemindEventByProductIdAndEnentType(session, shopId, eventType, productId);
      return (List<RepairRemindEvent>) q.list();
    } finally {
      release(session);
    }
  }

  public List<RepairRemindEvent> getLackMaterialByProductIds(Long shopId, RepairRemindEventTypes eventType, Set<Long> productIds) {
    Session session = this.getSession();
    try {
      Query q = getRepairRemindEventByProductIdsAndEnentType(session, shopId, eventType, productIds);
      return (List<RepairRemindEvent>) q.list();
    } finally {
      release(session);
    }
  }

  public List<RepairRemindEvent> getLackMaterialByProductIdAndStorehouse(Long shopId, RepairRemindEventTypes eventType, Long productId, Long storehouseId) {
    Session session = this.getSession();
    try {
      Query q = getRepairRemindEventByProductIdAndStorehouse(session, shopId, eventType, productId, storehouseId);
      return (List<RepairRemindEvent>) q.list();
    } finally {
      release(session);
    }
  }

  public List<RepairRemindEvent> getLackMaterialByProductIdsAndStorehouse(Long shopId, RepairRemindEventTypes eventType, Set<Long> productIds, Long storehouseId) {
    Session session = this.getSession();
    try {
      Query q = getRepairRemindEventByProductIdsAndStorehouse(session, shopId, eventType, productIds, storehouseId);
      return (List<RepairRemindEvent>) q.list();
    } finally {
      release(session);
    }
  }

  public List<InventoryRemindEvent> getInventoryRemindEventByProductId(Long shopId, Long productId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getInventoryRemindEventByProductId(session, shopId, productId);
      return (List<InventoryRemindEvent>) q.list();
    } finally {
      release(session);
    }
  }

  public List<InventoryRemindEvent> getInventoryRemindEventByPurchaseOrderId(Long shopId, Long purchaseOrderId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getInventoryRemindEventByPurchaseOrderId(session, shopId, purchaseOrderId);
      return (List<InventoryRemindEvent>) q.list();
    } finally {
      release(session);
    }
  }

  public List<PurchaseOrderItem> getPurchaseOrderItemsByOrderIdAndProductId(Long purchaseOrderId, Long productId) {
    Session session = this.getSession();
    try {
      Query q = getPurchaseOrderItemByOrderIdAndProductId(session, purchaseOrderId, productId);
      return (List<PurchaseOrderItem>) q.list();
    } finally {
      release(session);
    }
  }

  public List<PurchaseOrderItem> getOnlinePurchaseOrderItemsByOrderIdSupplierProductId(Long purchaseOrderId, Long supplierProductId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getOnlinePurchaseOrderItemsByOrderIdSupplierProductId(session, purchaseOrderId, supplierProductId);
      return (List<PurchaseOrderItem>) q.list();
    } finally {
      release(session);
    }
  }

  public List<WashOrder> countWashOrderList(long shopId, long startTime, long endTime) {
    Session session = getSession();

    try {
      Query q = SQL.countWashOrderList(session, shopId, startTime, endTime);
      return (List<WashOrder>) q.list();
    } finally {
      release(session);
    }
  }

  public List<OrderSearchResultDTO> getSalesOrderDTOList(long shopId, long startTime, long endTime, int pageNo, int pageSize, String arrayType) {
    Session session = this.getSession();
    try {
      Query hql = SQL.getSalesOrderDTOList(session, shopId, startTime, endTime, pageNo, pageSize, arrayType);
      List<?> list = hql.list();
      int index = 0;
      List<OrderSearchResultDTO> orderSearchResultDTOList = new ArrayList<OrderSearchResultDTO>();
      ICustomerService customerService = ServiceManager.getService(ICustomerService.class);
      if (CollectionUtils.isNotEmpty(list)) {

        for (int i = 0; i < list.size(); i++) {
          index = 0;
          Object[] array = (Object[]) list.get(i);
          OrderSearchResultDTO orderSearchResultDTO = new OrderSearchResultDTO();
          orderSearchResultDTO.setOrderId(Long.valueOf((array[index++]).toString()));
          orderSearchResultDTO.setReceiptNo((array[index++].toString()));
          orderSearchResultDTO.setOtherIncomeTotal(Double.valueOf((array[index++]).toString()));
          orderSearchResultDTO.setOtherTotalCostPrice(Double.valueOf((array[index++]).toString()));
          orderSearchResultDTO.setOrderIdStr(orderSearchResultDTO.getOrderId().toString());
          orderSearchResultDTO.setCreatedTime(Long.valueOf((array[index++]).toString()));
          orderSearchResultDTO.setShopId(Long.valueOf((array[index++]).toString()));

          orderSearchResultDTO.setCustomerOrSupplierName(array[index] == null ? "**客户**" : array[index].toString());
          index++;

          orderSearchResultDTO.setAmount(Double.valueOf((array[index++]).toString()));

          if (array[index] == null) {
            orderSearchResultDTO.setTotalCostPrice(0d);
            index++;
          } else {
            orderSearchResultDTO.setTotalCostPrice(Double.valueOf((array[index++]).toString()));
          }

          if (array[index] == null) {
            orderSearchResultDTO.setVestDate(0L);
            orderSearchResultDTO.setVestDateStr(DateUtil.dateLongToStr(orderSearchResultDTO.getVestDate(), DateUtil.DATE_STRING_FORMAT_DAY));
            index++;
          } else {
            orderSearchResultDTO.setVestDate(Long.valueOf((array[index++]).toString()));
            orderSearchResultDTO.setVestDateStr(DateUtil.dateLongToStr(orderSearchResultDTO.getVestDate(), DateUtil.DATE_STRING_FORMAT_DAY));
          }
          if (array[index] == null) {
            continue;
          }
          if ("S".equals(String.valueOf(array[index]))) {
            orderSearchResultDTO.setOrderTypeValue(OrderTypes.SALE.getName());
          } else if ("R".equals(String.valueOf(array[index]))) {
            orderSearchResultDTO.setOrderTypeValue(OrderTypes.SALE_RETURN.getName());
          }

          orderSearchResultDTOList.add(orderSearchResultDTO);
        }
      }
      return orderSearchResultDTOList;
    } finally {
      release(session);
    }
  }

  public List<SalesOrder> getSalesOrderDTOListByVestDate(long shopId, long startTime, long endTime) {
    Session session = this.getSession();
    try {
      Query hql = SQL.getSalesOrderDTOListByVestDate(session, shopId, startTime, endTime);
      if (hql != null && hql.list() != null && hql.list().size() > 0) {
        return (List<SalesOrder>) hql.list();
      } else {
        return null;
      }

    } finally {
      release(session);
    }
  }

  public List<SalesOrder> getHundredCostPriceNUllSalesOrderDTOList() {
    Session session = this.getSession();
    try {
      Query hql = SQL.getHundredCostPriceNUllSalesOrderDTOList(session);
      return (List<SalesOrder>) hql.list();
    } finally {
      release(session);
    }
  }

  public int countSalesOrder(long shopId, long startTime, long endTime) {
    Session session = getSession();

    try {
      Query q = SQL.countSalesOrder(session, shopId, startTime, endTime);
      return Integer.valueOf(q.uniqueResult().toString()).intValue();
    } finally {
      release(session);
    }
  }

  public int countSalesOrder(Long shopId) {
    Session session = getSession();

    try {
      Query q = SQL.countSalesOrder(session, shopId);
      return Integer.valueOf(q.uniqueResult().toString()).intValue();
    } finally {
      release(session);
    }
  }

  public int countRepairOrder(Long shopId) {
    Session session = getSession();

    try {
      Query q = SQL.countRepairOrder(session, shopId);
      return Integer.valueOf(q.uniqueResult().toString()).intValue();
    } finally {
      release(session);
    }
  }


  public List<RepairOrderDTO> getRepairOrderDTOList(long shopId, long startTime, long endTime, int pageNo, int pageSize, String arrayType, OrderStatus orderStatus) throws Exception {
    Session session = this.getSession();
    try {
      Query hql = SQL.getRepairOrderDTOList(session, shopId, startTime, endTime, pageNo, pageSize, arrayType, orderStatus);

      List<Object> list = hql.list();
      int index = 0;
      List<RepairOrderDTO> repairOrderDTOList = new ArrayList<RepairOrderDTO>();
      if (CollectionUtils.isNotEmpty(list)) {
        for (int i = 0; i < list.size(); i++) {
          index = 0;
          Object[] array = (Object[]) list.get(i);

          RepairOrderDTO repairOrderDTO = new RepairOrderDTO();
          repairOrderDTO.setId(Long.valueOf((array[index++]).toString()));
          repairOrderDTO.setDate(Long.valueOf((array[index++]).toString()));
          repairOrderDTO.setShopId(Long.valueOf((array[index++]).toString()));
          repairOrderDTO.setReceiptNo(array[index].toString());
          index++;
          repairOrderDTO.setOtherIncomeTotal(Double.valueOf((array[index++]).toString()));
          repairOrderDTO.setOtherTotalCostPrice(Double.valueOf((array[index++]).toString()));
          repairOrderDTO.setTotal(Double.valueOf((array[index++]).toString()));
          if (array[index] == null) {
            repairOrderDTO.setTotalCostPrice(0d);
            index++;
          } else {
            repairOrderDTO.setTotalCostPrice(Double.valueOf((array[index++]).toString()));
          }

          if (array[index] == null) {
            repairOrderDTO.setVechicle("");
            index++;
          } else {
            VehicleDTO vehicleDTO = ServiceManager.getService(IVehicleService.class).findVehicleById(Long.valueOf((array[index++]).toString()));
            if (vehicleDTO != null) {
              repairOrderDTO.setVechicle(vehicleDTO.getLicenceNo());
            }
          }
          if (array[index] == null) {
            repairOrderDTO.setVestDate(0L);
            index++;
          } else {
            repairOrderDTO.setVestDate(Long.valueOf((array[index++]).toString()));
          }

          if (NumberUtil.longValue(repairOrderDTO.getVestDate()) > 0) {
            repairOrderDTO.setVestDateStr(DateUtil.dateLongToStr(repairOrderDTO.getVestDate(), DateUtil.DATE_STRING_FORMAT_DAY));
          }
          repairOrderDTOList.add(repairOrderDTO);
        }
      }
      return repairOrderDTOList;
    } finally {
      release(session);
    }
  }

  public List<RepairOrder> getHundredCostPriceNUllRepairOrderDTOList() {
    Session session = this.getSession();
    try {
      Query hql = SQL.getHundredCostPriceNUllRepairOrderDTOList(session);
      return (List<RepairOrder>) hql.list();
    } finally {
      release(session);
    }
  }

  public List<String> getRepairOrderDTOListByVestDate(long shopId, long startTime, long endTime, OrderStatus orderStatus) {

    Session session = this.getSession();
    List<String> stringList = new ArrayList<String>();
    try {
      Query q = SQL.getRepairOrderDTOListByVestDate(session, shopId, startTime, endTime, orderStatus);
      if (q == null) {
        return stringList;
      }
      List<Object> list = q.list();
      if (CollectionUtils.isNotEmpty(list)) {
        Object[] array = (Object[]) list.get(0);
        if (array[0] != null && array[1] != null) {
          stringList.add(array[0].toString());
          stringList.add(array[1].toString());
          stringList.add(array[2].toString());
        }
      }
      return stringList;
    } finally {
      release(session);
    }
  }

  //充值记录分页查询
  public List getShopSmsRechargeList(String startTime, String endTime, String money, Long shopId, String other, int pageNo, int pageSize) {
    Session session = this.getSession();
    try {
      SQLQuery query = null;
      try {
        query = (SQLQuery) SQL.getShopSmsRechargeList(session, DateUtil.convertDateStringToDateLong("yyyy-MM-dd", startTime), DateUtil.convertDateStringToDateLong("yyyy-MM-dd", endTime), money, shopId, other, pageNo, pageSize)
          .setResultTransformer(Transformers.aliasToBean(SmsRechargeDTO.class));
      } catch (ParseException e) {
        LOG.error(e.getMessage(), e);  //To change body of catch statement use File | Settings | File Templates.
      }
      query.addScalar("shopId", StandardBasicTypes.LONG);
      query.addScalar("userName", StandardBasicTypes.STRING);
      query.addScalar("smsBalance", StandardBasicTypes.DOUBLE);
      query.addScalar("rechargeAmount", StandardBasicTypes.DOUBLE);
      query.addScalar("rechargeTime", StandardBasicTypes.LONG);
      query.addScalar("state", StandardBasicTypes.LONG);

      return query.list();
    } finally {
      release(session);
    }
  }

  //查询充值记录数量
  public int countShopSmsRecharge(String startTime, String endTime, String money, String other, Long shopId) {
    Session session = this.getSession();
    try {
      SQLQuery query = null;
      try {
        query = (SQLQuery) SQL.countShopSmsRecharge(session, DateUtil.convertDateStringToDateLong("yyyy-MM-dd", startTime), DateUtil.convertDateStringToDateLong("yyyy-MM-dd", endTime), money, other, shopId)
          .setResultTransformer(Transformers.aliasToBean(SmsRechargeDTO.class));
      } catch (ParseException e) {
        LOG.error(e.getMessage(), e);  //To change body of catch statement use File | Settings | File Templates.
      }
      query.addScalar("shopId", StandardBasicTypes.LONG);
      query.addScalar("userName", StandardBasicTypes.STRING);
      query.addScalar("smsBalance", StandardBasicTypes.DOUBLE);
      query.addScalar("rechargeAmount", StandardBasicTypes.DOUBLE);
      query.addScalar("rechargeTime", StandardBasicTypes.LONG);
      query.addScalar("state", StandardBasicTypes.LONG);

      return query.list().size();
    } finally {
      release(session);
    }
  }

  public List<WashOrder> countWashAgentAchievements(long shopId, long startTime, long endTime) {
    Session session = getSession();
    try {
      Query q = null;
      q = SQL.countWashAgentAchievements(session, shopId, startTime, endTime);
      List<WashOrder> washOrderList = q.list();
      return washOrderList;
    } finally {
      release(session);
    }
  }

  public List<SalesOrder> countSalesAgentAchievements(long shopId, long startTime, long endTime) {
    Session session = getSession();
    try {
      Query q = null;
      q = SQL.countSalesAgentAchievements(session, shopId, startTime, endTime);
      List<SalesOrder> salesOrderList = q.list();
      return salesOrderList;
    } finally {
      release(session);
    }
  }

  public List<WashOrder> getWashOrderListByAssistantName(String assistantName, long startTime, long endTime) {
    Session session = this.getSession();

    try {
      Query q = SQL.getWashOrderListByAssistantName(session, assistantName, startTime, endTime);

      return (List<WashOrder>) q.list();
    } finally {
      release(session);
    }

  }

  public List<RepairOrder> getRepairOrderListByAssistantName(String assistantName, long startTime, long endTime) {
    Session session = this.getSession();

    try {
      Query q = SQL.getRepairOrderListByAssistantName(session, assistantName, startTime, endTime);

      return (List<RepairOrder>) q.list();
    } finally {
      release(session);
    }

  }

  public List<SalesOrder> getSalesOrderListByAssistantName(String assistantName, long startTime, long endTime) {
    Session session = this.getSession();

    try {
      Query q = SQL.getSalesOrderListByAssistantName(session, assistantName, startTime, endTime);

      return (List<SalesOrder>) q.list();
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

  public void updatePurchaseInventoryStatus(PurchaseInventoryDTO purchaseInventoryDTO) {
    Session session = this.getSession();
    try {
      Query q = SQL.updatePurchaseInventoryStatus(session, purchaseInventoryDTO.getId(), purchaseInventoryDTO.getStatus());
      q.executeUpdate();
    } finally {
      release(session);
    }
  }

  public void updateInventoryAmount(Long shopId, Long productLocalInfoId, Double amount) {
    Session session = this.getSession();
    try {
      Query q = SQL.updateInventoryAmount(session, shopId, productLocalInfoId, amount);
      q.executeUpdate();
    } finally {
      release(session);
    }
  }

  public void updatePurchaseOrderStatus(Long shopId, Long id, OrderStatus status) {
    Session session = this.getSession();
    try {
      Query q = SQL.updatePurchaseOrderStatus(session, shopId, id, status);
      q.executeUpdate();
    } finally {
      release(session);
    }
  }

  public void updateSaleOrderStatus(Long shopId, Long saleOrderId, OrderStatus saleOrderStatus) {
    Session session = this.getSession();
    try {
      Query q = SQL.updateSaleOrderStatus(session, shopId, saleOrderId, saleOrderStatus);
      q.executeUpdate();
    } finally {
      release(session);
    }
  }

  public Inventory getInventoryByIdAndshopId(Long productId, Long shopId) throws Exception {
    Session session = this.getSession();
    try {
      Query query = getInventoryByIdAndShopId(session, productId, shopId);
      return (Inventory) query.uniqueResult();
    } finally {
      release(session);
    }
  }

  public List<Inventory> getInventoryByshopIdAndProductIds(Long shopId, Long... productId) throws Exception {
    Session session = this.getSession();
    try {
      Query query = SQL.getInventoryByshopIdAndProductIds(session, shopId, productId);
      return query.list();
    } finally {
      release(session);
    }
  }

  public List<Inventory> getInventoryByProductIds(Long... productId) {
    Session session = this.getSession();
    try {
      Query query = SQL.getInventoryByProductIds(session, productId);
      return query.list();
    } finally {
      release(session);
    }
  }

  public List<SmsRecharge> getSmsRechargesByConditions(RechargeSearchDTO rechargeSearchDTO) {
    Session session = this.getSession();
    try {
      Query hql = getSmsRechargesByState(session, rechargeSearchDTO);
      return (List<SmsRecharge>) hql.list();
    } finally {
      release(session);
    }
  }

  public List<SmsRecharge> getSmsRechargesByIds(Long... ids) {
    Session session = this.getSession();
    try {
      Query hql = SQL.getSmsRechargesByIds(session, ids);
      return (List<SmsRecharge>) hql.list();
    } finally {
      release(session);
    }
  }

  public List<Long> getSmsRechargesByStatus(Long shopId, int start, int pageSize, Long loanTransferTime) {
    Session session = this.getSession();
    try {
      Query hql = SQL.getSmsRechargesByStatus(session, shopId, start, pageSize, loanTransferTime);
      return (List<Long>) hql.list();
    } finally {
      release(session);
    }
  }

  public int countSmsRechargesByConditions(RechargeSearchDTO rechargeSearchDTO) {
    Session session = this.getSession();
    try {
      Query hql = SQL.countSmsRechargesByConditions(session, rechargeSearchDTO);
      return Integer.valueOf(hql.uniqueResult().toString());
    } finally {
      release(session);
    }
  }


  public List<ShopPrintTemplate> getShopPrintTemplateDTOByShopIdAndType(Long shopId, OrderTypes type) {
    Session session = this.getSession();
    try {
      Query hql = SQL.getShopPrintTemplateDTOByShopIdAndType(session, shopId, type);
      return (List<ShopPrintTemplate>) hql.list();
    } finally {
      release(session);
    }
  }

  public int countPrintTemplateDTOByName(Long shopId, String name) {
    Session session = this.getSession();
    try {
      Query hql = SQL.countPrintTemplateDTOByName(session, shopId, name);
      return Integer.parseInt(hql.uniqueResult().toString());
    } finally {
      release(session);
    }
  }

  public List<PrintTemplate> getPrintTemplateDTOByType(OrderTypes type) {
    Session session = this.getSession();
    try {
      Query hql = SQL.getPrintTemplateDTOByType(session, type);

      List<PrintTemplate> printTemplateList = (List<PrintTemplate>) hql.list();

      if (null != printTemplateList && 0 != printTemplateList.size()) {
        return printTemplateList;
      }

      return null;
    } finally {
      release(session);
    }
  }

  public List<String> getSalesOrderCountAndSum(long shopId, long startTime, long endTime) {
    Session session = this.getSession();
    try {
      Query q = SQL.getSalesOrderCountAndSum(session, shopId, startTime, endTime);
      List<String> stringList = new ArrayList<String>();
      if (q == null) {
        return null;
      }
      List<Object> list = q.list();
      if (CollectionUtils.isNotEmpty(list)) {
        Object[] array = (Object[]) list.get(0);
        if (array[0] != null && array[1] != null && array[2] != null) {
          stringList.add(array[0].toString());
          stringList.add(array[1].toString());
          stringList.add(array[2].toString());
        } else {
          stringList.add("0");
          stringList.add("0");
          stringList.add("0");
        }
        array = (Object[]) list.get(1);
        if (array[0] != null && array[1] != null && array[2] != null) {
          stringList.add(array[0].toString());
          stringList.add(array[1].toString());
          stringList.add(array[2].toString());
        } else {
          stringList.add("0");
          stringList.add("0");
          stringList.add("0");
        }
        return stringList;
      } else {
        return null;
      }
    } finally {
      release(session);
    }
  }


  public List<SmsBalance> getSmsBalance(Pager pager) {
    Session session = this.getSession();
    try {
      Query hql = SQL.getSmsBalance(session, pager);
      return (List<SmsBalance>) hql.list();
    } finally {
      release(session);
    }
  }

  public Integer countSmsBalance() {
    Session session = this.getSession();
    try {
      Query hql = SQL.countSmsBalance(session);
      return Integer.valueOf(hql.uniqueResult().toString());
    } finally {
      release(session);
    }
  }


  public List<SalesOrder> getSalesOrderDTOListByCustomerId(long shopId, long customerId) {
    Session session = this.getSession();
    try {
      Query hql = SQL.getSalesOrderDTOListByCustomerId(session, shopId, customerId);
      if (hql != null && hql.list() != null && hql.list().size() > 0) {
        return (List<SalesOrder>) hql.list();
      } else {
        return null;
      }

    } finally {
      release(session);
    }
  }

  public List<RepairOrder> getRepairOrderDTOListByCustomerId(long shopId, long customerId) {
    Session session = this.getSession();
    try {
      Query hql = SQL.getRepairOrderDTOListByCustomerId(session, shopId, customerId);
      if (hql != null && hql.list() != null && hql.list().size() > 0) {
        return (List<RepairOrder>) hql.list();
      } else {
        return null;
      }

    } finally {
      release(session);
    }
  }


  public long countReceivableDTOByShopId(Long shopId) {
    Session session = getSession();
    try {
      Query q = SQL.countReceivableDTOByShopId(session, shopId);
      return Long.parseLong(q.iterate().next().toString());
    } finally {
      release(session);
    }
  }

  public List<Receivable> getReceivableDTOList(Long shopId, int pageNo, int pageSize) {
    Session session = this.getSession();
    try {
      Query hql = SQL.getReceivableDTOList(session, shopId, pageNo, pageSize);
      List list = hql.list();
      if (CollectionUtils.isEmpty(list)) {
        return null;
      }
      return (List<Receivable>) list;
    } finally {
      release(session);
    }
  }

  public List<Receivable> getAllReceivables(Long shopId, Long customerId) {
    Session session = this.getSession();
    try {
      Query hql = SQL.getAllReceivables(session, shopId, customerId);
      List list = hql.list();
      if (CollectionUtils.isEmpty(list)) {
        return null;
      }
      return (List<Receivable>) list;
    } finally {
      release(session);
    }
  }

  public List<PurchaseOrder> getPurchaseOrderDTOListByShopId(long shopId, long startTime, long endTime) {
    Session session = this.getSession();
    try {
      Query hql = SQL.getPurchaseOrderDTOListByShopId(session, shopId, startTime, endTime);

      List list = hql.list();
      if (CollectionUtils.isEmpty(list)) {
        return null;
      }
      return (List<PurchaseOrder>) list;

    } finally {
      release(session);
    }
  }

  public List<PurchaseInventory> getPurchaseInventoryByShopId(long shopId, OrderStatus status) {
    Session session = this.getSession();
    try {
      Query hql = SQL.getPurchaseInventoryByShopId(session, shopId, status);
      return (List<PurchaseInventory>) hql.list();
    } finally {
      release(session);
    }
  }


  public List<RepairOrder> getRepairOrderDTOListByCreated(long shopId, long startTime, long endTime) {
    Session session = this.getSession();
    try {
      Query hql = SQL.getRepairOrderDTOListByCreated(session, shopId, startTime, endTime);
      return (List<RepairOrder>) hql.list();
    } finally {
      release(session);
    }
  }

  public long countPurchaseInventoryByShopId(long shopId, long startTime, long endTime) {
    Session session = getSession();
    try {
      Query q = SQL.countPurchaseInventoryByShopId(session, shopId, startTime, endTime);
      return Long.parseLong(q.uniqueResult().toString());
    } finally {
      release(session);
    }
  }

  public List<PurchaseInventory> getPurchaseInventoryDTOList(long shopId, int pageNo, int pageSize, long startTime, long endTime) {
    Session session = this.getSession();
    try {
      Query hql = SQL.getPurchaseInventoryDTOList(session, shopId, pageNo, pageSize, startTime, endTime);
      List<PurchaseInventory> purchaseInventories = (List<PurchaseInventory>) hql.list();
      if (CollectionUtils.isNotEmpty(purchaseInventories)) {
        return purchaseInventories;
      } else {
        return null;
      }
    } finally {
      release(session);
    }
  }

  /**
   * @param shopId
   * @param productId
   * @return
   */
  public List<RepairOrderItem> getRepairOrderItemsByProductId(long shopId, long productId) {
    Session session = getSession();
    try {
      Query q = SQL.getRepairOrderItemsByProductId(session, shopId, productId);
      return (List<RepairOrderItem>) q.list();
    } finally {
      release(session);
    }
  }

  public List<SalesOrderItem> getSalesOrderItemsByProductId(long shopId, long productId) {
    Session session = getSession();
    try {
      Query q = SQL.getSalesOrderItemsByProductId(session, shopId, productId);
      return (List<SalesOrderItem>) q.list();
    } finally {
      release(session);
    }
  }

  public List<PurchaseInventoryItem> getPurchaseInventoryItemsByProductId(long orderId, long productId) {
    Session session = getSession();
    try {
      Query q = SQL.getPurchaseInventoryItemsByProductId(session, orderId, productId);
      return (List<PurchaseInventoryItem>) q.list();
    } finally {
      release(session);
    }
  }

  public List<PurchaseOrderItem> getPurchaseOrderItemsByProductId(long orderId, long productId) {
    Session session = getSession();
    try {
      Query q = SQL.getPurchaseOrderItemsByProductId(session, orderId, productId);
      return (List<PurchaseOrderItem>) q.list();
    } finally {
      release(session);
    }
  }

  public List<PurchaseReturnItem> getPurchaseReturnItemsByProdctId(long orderId, long productId) {
    Session session = getSession();
    try {
      Query q = SQL.getPurchaseReturnItemsByProdctId(session, orderId, productId);
      return (List<PurchaseReturnItem>) q.list();
    } finally {
      release(session);
    }
  }

  public List<PurchaseOrder> getPurchaseOrderByShopId(long shopId, Long status) {
    Session session = this.getSession();
    try {
      Query hql = SQL.getPurchaseOrderByShopId(session, shopId, status);
      List<PurchaseOrder> purchaseOrders = (List<PurchaseOrder>) hql.list();
      if (CollectionUtils.isNotEmpty(purchaseOrders)) {
        return purchaseOrders;
      } else {
        return null;
      }
    } finally {
      release(session);
    }
  }

  public List<PurchaseReturn> getPurchaseReturnByShopId(long shopId, Long status) {
    Session session = this.getSession();
    try {
      Query hql = SQL.getPurchaseReturnByShopId(session, shopId, status);
      List<PurchaseReturn> purchaseReturns = (List<PurchaseReturn>) hql.list();
      if (CollectionUtils.isNotEmpty(purchaseReturns)) {
        return purchaseReturns;
      } else {
        return null;
      }
    } finally {
      release(session);
    }
  }


  public List<RepairOrder> getRepairOrdersByShopId(long shopId, OrderStatus status) {
    Session session = getSession();
    try {
      Query q = getrepairOrdersByShopId(session, shopId, status);
      return (List<RepairOrder>) q.list();
    } finally {
      release(session);
    }
  }

  public RepairOrder getRepairOrderById(long id, long shopId) {
    Session session = getSession();
    try {
      Query q = SQL.getRepairOrderById(session, id, shopId);
      return (RepairOrder) q.uniqueResult();
    } finally {
      release(session);
    }
  }

  public List<SalesOrder> getSalesOrderByShopId(long shopId, OrderStatus status) {
    Session session = getSession();
    try {
      Query q = SQL.getSalesOrderByShopId(session, shopId, status);
      return (List<SalesOrder>) q.list();
    } finally {
      release(session);
    }
  }

  public int countSalesOrderByVestDate(long shopId, long startTime, long endTime) {
    Session session = this.getSession();
    try {
      Query hql = SQL.countSalesOrderByVestDate(session, shopId, startTime, endTime);
      return Integer.parseInt(hql.uniqueResult().toString());

    } finally {
      release(session);
    }
  }

  public List<SalesOrder> getSalesOrderListByPager(long shopId, long startTime, long endTime, Pager pager) {
    Session session = getSession();
    try {
      Query q = SQL.getSalesOrderListByPager(session, shopId, startTime, endTime, pager);
      List result = q.list();
      if (CollectionUtils.isNotEmpty(result)) {
        return (List<SalesOrder>) result;
      }
      return null;
    } finally {
      release(session);
    }
  }

  public int countRepairOrderByVestDate(long shopId, long startTime, long endTime) {
    Session session = this.getSession();
    try {
      Query hql = SQL.countRepairOrderByVestDate(session, shopId, startTime, endTime);
      return Integer.parseInt(hql.uniqueResult().toString());

    } finally {
      release(session);
    }
  }

  public List<RepairOrder> getRepairOrderListByPager(long shopId, long startTime, long endTime, Pager pager) {
    Session session = getSession();
    try {
      Query q = SQL.getRepairOrderListByPager(session, shopId, startTime, endTime, pager);
      if (q == null) {
        return null;
      }
      List result = q.list();
      if (CollectionUtils.isNotEmpty(result)) {
        return (List<RepairOrder>) result;
      }
      return null;
    } finally {
      release(session);
    }
  }

  public List<RepealOrder> getRepealOrderByShopIdAndOrderId(long shopId, long orderId) {
    Session session = getSession();
    try {
      Query q = SQL.getRepealOrderByShopIdAndOrderId(session, shopId, orderId);
      if (q == null) {
        return null;
      }
      List result = q.list();
      if (CollectionUtils.isNotEmpty(result)) {
        return (List<RepealOrder>) result;
      }
      return null;
    } finally {
      release(session);
    }
  }

  public WashOrder getLastWashOrderDTO(Long shopId, Long customerId) {
    Session session = getSession();
    try {
      Query q = SQL.getLastWashOrderDTO(session, shopId, customerId);

      List<WashOrder> washOrders = (List<WashOrder>) q.list();
      if (CollectionUtils.isNotEmpty(washOrders) && washOrders.size() > 1) {
        return washOrders.get(1);
      }

      return null;

    } finally {
      release(session);
    }
  }

  public int countServices(Long shopId) {
    Session session = getSession();

    try {
      Query q = SQL.countServices(session, shopId);
      return Integer.valueOf(q.uniqueResult().toString()).intValue();
    } finally {
      release(session);
    }
  }

  public List<Service> getServiceByShopId(Long shopId, int pageNo, int maxPageSize) {
    Session session = getSession();

    try {
      Query q = SQL.getServiceByShopId(session, shopId, pageNo, maxPageSize);

      return (List<Service>) q.list();
    } finally {
      release(session);
    }
  }

  public List<Long> getAllServiceIdsByShopId(Long shopId, int start, int rows) {
    Session session = getSession();
    try {
      Query q = SQL.getAllServiceIdsByShopId(session, shopId, start, rows);
      return (List<Long>) q.list();
    } finally {
      release(session);
    }
  }

  public MemberCardOrder getLastMemberCardOrderByCustomerId(Long shopId, Long customerId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getLastMemberCardOrderByCustomerId(session, shopId, customerId);
      List<MemberCardOrder> memberCardOrders = (List<MemberCardOrder>) q.list();
      if (CollectionUtils.isNotEmpty(memberCardOrders)) {
        return memberCardOrders.get(0);
      }
      return null;
    } finally {
      release(session);
    }
  }

  public MemberCardOrderItem getLastMemberCardOrderItemByOrderId(Long shopId, Long orderId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getLastMemberCardOrderItemByOrderId(session, shopId, orderId);
      List<MemberCardOrderItem> memberCardOrderItems = (List<MemberCardOrderItem>) q.list();
      if (CollectionUtils.isNotEmpty(memberCardOrderItems)) {
        return memberCardOrderItems.get(0);
      }
      return null;
    } finally {
      release(session);
    }
  }

  public List<Service> getAllServiceByShopId(Long shopId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getAllServiceByShopId(session, shopId);
      return (List<Service>) q.list();
    } finally {
      release(session);
    }
  }

  public List<CategoryItemRelation> getCategoryIdByServiceId(Long serviceId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getCategoryIdByServiceId(session, serviceId);
      return (List<CategoryItemRelation>) q.list();
    } finally {
      release(session);
    }
  }

  public List<Service> getServiceByServiceName(Long shopId, String serviceName, Long pageNo, Long pageSize) {
    Session session = this.getSession();
    try {
      Query q = SQL.getServiceByServiceName(session, shopId, serviceName, pageNo, pageSize);
      return (List<Service>) q.list();
    } finally {
      release(session);
    }
  }

  public List<Service> getServiceByCategoryName(Long shopId, String categoryName, CategoryType categoryType, String serviceName, Long pageNo, Long pageSize) {
    Session session = this.getSession();
    try {
      Query q = SQL.getServiceByCategoryName(session, shopId, categoryName, categoryType, serviceName, pageNo, pageSize);
      return (List<Service>) q.list();
    } finally {
      release(session);
    }
  }

  public Category getCategoryByIdAndCategoryName(Long id, String categoryName, String categoryType) {
    Session session = this.getSession();
    try {
      Query q = SQL.getCategoryByIdAndCategoryName(session, id, categoryName, categoryType);
      List<Category> categories = (List<Category>) q.list();
      if (CollectionUtils.isNotEmpty(categories)) {
        return categories.get(0);
      } else {
        return null;
      }
    } finally {
      release(session);
    }
  }

  public List<Category> getCategoryByShopId(Long shopId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getCategoryByShopId(session, shopId);
      return (List<Category>) q.list();
    } finally {
      release(session);
    }
  }

  public List<Service> getServiceNoPercentage(Long shopId, Long pageNo, Long pageSize) {
    Session session = this.getSession();
    try {
      Query q = SQL.getServiceNoPercentage(session, shopId, pageNo, pageSize);
      return (List<Service>) q.list();
    } finally {
      release(session);
    }
  }

  public Category getCategoryByName(Long shopId, String name, CategoryType type) {
    Session session = this.getSession();
    try {
      Query q = SQL.getCategoryByName(session, shopId, name, type);
      List<Category> categories = (List<Category>) q.list();
      if (CollectionUtils.isNotEmpty(categories)) {
        return categories.get(0);
      } else {
        return null;
      }
    } finally {
      release(session);
    }
  }

  public List<Category> getCategoryByNames(Long shopId, CategoryType type, String... name) {
    Session session = this.getSession();
    try {
      Query q = SQL.getCategoryByNames(session, shopId, type, name);
      return q.list();
    } finally {
      release(session);
    }
  }

  public CategoryItemRelation getCategoryItemRelationByCAndSId(Long categoryId, Long serviceId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getCategoryItemRelationByCAndSId(session, categoryId, serviceId);

      List<CategoryItemRelation> categoryItemRelations = (List<CategoryItemRelation>) q.list();
      if (CollectionUtils.isNotEmpty(categoryItemRelations)) {
        return categoryItemRelations.get(0);
      } else {
        return null;
      }
    } finally {
      release(session);
    }
  }

  public List<Service> getServiceNoCategory(Long shopId, String serviceName, Long pageNo, Long pageSize) {
    Session session = this.getSession();
    try {
      Query q = SQL.getServiceNoCategory(session, shopId, serviceName, pageNo, pageSize);
      return (List<Service>) q.list();
    } finally {
      release(session);
    }
  }

  public int countServiceByCategory(Long shopId, String categoryName, CategoryType categoryType, String serviceName) {
    Session session = this.getSession();
    try {
      Query q = SQL.countServiceByCategory(session, shopId, categoryName, categoryType, serviceName);
      return Integer.parseInt(q.uniqueResult().toString());
    } finally {
      release(session);
    }
  }

  public int countServiceByServiceName(Long shopId, String serviceName) {
    Session session = this.getSession();
    try {
      Query q = SQL.countServiceByServiceName(session, shopId, serviceName);
      return Integer.parseInt(q.uniqueResult().toString());
    } finally {
      release(session);
    }
  }

  public int countServiceNoCategory(Long shopId, String serviceName) {
    Session session = this.getSession();
    try {
      Query q = SQL.countServiceNoCategory(session, shopId, serviceName);
      return ((Long) q.uniqueResult()).intValue();
    } finally {
      release(session);
    }
  }

  public int countServiceNoPercentage(Long shopId) {
    Session session = this.getSession();
    try {
      Query q = SQL.countServiceNoPercentage(session, shopId);
      return Integer.parseInt(q.uniqueResult().toString());
    } finally {
      release(session);
    }
  }

  public List<Service> getServiceByWashBeauty(Long shopId, CategoryType categoryType) {
    Session session = this.getSession();
    try {
      Query q = SQL.getServiceByWashBeauty(session, shopId, categoryType);
      return (List<Service>) q.list();
    } finally {
      release(session);
    }
  }

  public Service getWashService(Long shopId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getWashService(session, shopId);
      List<Service> services = (List<Service>) q.list();
      if (CollectionUtils.isNotEmpty(services)) {
        return services.get(0);
      } else {
        return null;
      }
    } finally {
      release(session);
    }
  }


  public MemberCardOrder getMemberCardOrderDTOById(Long shopId, Long orderId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getMemberCardOrderDTOById(session, shopId, orderId);

      List<MemberCardOrder> memberCardOrders = (List<MemberCardOrder>) q.list();
      if (CollectionUtils.isNotEmpty(memberCardOrders)) {
        return memberCardOrders.get(0);
      }
      return null;
    } finally {
      release(session);
    }
  }

  public List<MemberCardOrderItem> getMemberCardOrderItemDTOByOrderId(Long shopId, Long orderId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getMemberCardOrderItemDTOByOrderId(session, shopId, orderId);
      return (List<MemberCardOrderItem>) q.list();
    } finally {
      release(session);
    }
  }

  public List<MemberCardOrderService> getMemberCardOrderServiceDTOByOrderId(Long shopId, Long orderId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getMemberCardOrderServiceDTOByOrderId(session, shopId, orderId);

      return (List<MemberCardOrderService>) q.list();

    } finally {
        release(session);
    }
  }

  public List<Service> getServiceByServiceNameAndShopId(Long shopId, String serviceName) {
    Session session = this.getSession();
    try {
      Query q = SQL.getServiceByServiceNameAndShopId(session, shopId, serviceName);
      if (q != null) {
        List result = q.list();
        return CollectionUtils.isEmpty(result) ? null : (List<Service>) result;
      }
      return null;
    } finally {
      release(session);
    }
  }

  public List<Service> getServiceByNames(Long shopId, boolean isIncludeDisabled, String... serviceName) {
    Session session = this.getSession();
    try {
      Query q = SQL.getServiceByNames(session, shopId, isIncludeDisabled, serviceName);
      return q.list();
    } finally {
      release(session);
    }
  }

  public WashBeautyOrder getWashBeautyOrderDTOById(Long shopId, Long orderId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getWashBeautyOrderDTOById(session, shopId, orderId);

      List<WashBeautyOrder> washBeautyOrders = (List<WashBeautyOrder>) q.list();

      if (CollectionUtils.isNotEmpty(washBeautyOrders)) {
        return washBeautyOrders.get(0);
      }

      return null;

    } finally {
      release(session);
    }
  }


  public List<WashBeautyOrderItem> getWashBeautyOrderItemDTOByOrderId(Long shopId, Long orderId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getWashBeautyOrderItemDTOByOrderId(session, shopId, orderId);

      return (List<WashBeautyOrderItem>) q.list();
    } finally {
      release(session);
    }
  }

  public List<Service> getAllServiceDTOByShopId(Long shopId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getAllServiceDTOByShopId(session, shopId);
      return (List<Service>) q.list();
    } finally {
      release(session);
    }
  }

  public List<Service> getObscureServiceByName(Long shopId, String serviceName) {
    Session session = this.getSession();

    try {
      Query q = SQL.getObscureServiceByName(session, shopId, serviceName);
      return (List<Service>) q.list();
    } finally {
      release(session);
    }
  }

  public List<Category> getObscureCategoryByName(Long shopId, String categoryName) {
    Session session = this.getSession();

    try {
      Query q = SQL.getObscureCategoryByName(session, shopId, categoryName);
      return (List<Category>) q.list();
    } finally {
      release(session);
    }
  }


  public List<String> getMemberOrderCountAndSum(long shopId, long startTime, long endTime, OrderSearchConditionDTO orderSearchConditionDTO) {
    Session session = this.getSession();
    try {
      Query q = SQL.getMemberOrderCountAndSum(session, shopId, startTime, endTime, orderSearchConditionDTO);
      List<String> stringList = new ArrayList<String>();
      if (q == null) {
        return stringList;
      }
      List<Object> list = q.list();
      if (CollectionUtils.isNotEmpty(list)) {
        Object[] array = (Object[]) list.get(0);
        if (array[0] != null && array[1] != null && array[2] != null) {
          stringList.add(array[0].toString());
          stringList.add(array[1].toString());
          stringList.add(array[2].toString());
        }
      }
      return stringList;
    } finally {
      release(session);
    }
  }

  public List<MemberCardOrder> getMemberOrderListByPagerTimeArrayType(long shopId, long startTime, long endTime, Pager pager, String arrayType, OrderSearchConditionDTO orderSearchConditionDTO) {
    Session session = getSession();

    try {
      Query q = SQL.getMemberOrderListByPagerTimeArrayType(session, shopId, startTime, endTime, pager, arrayType, orderSearchConditionDTO);
      return (List<MemberCardOrder>) q.list();
    } finally {
      release(session);
    }
  }


  public List<MemberCardOrder> countMemberAgentAchievements(long shopId, long startTime, long endTime) {
    Session session = getSession();
    try {
      Query q = null;
      q = SQL.countMemberAgentAchievements(session, shopId, startTime, endTime);
      List<MemberCardOrder> memberCardOrderList = q.list();
      return memberCardOrderList;
    } finally {
      release(session);
    }
  }


  public List<WashBeautyOrder> countWashBeautyAgentAchievements(long shopId, long startTime, long endTime) {
    Session session = getSession();
    try {
      Query q = SQL.countWashBeautyAgentAchievements(session, shopId, startTime, endTime);
      List<WashBeautyOrder> washBeautyOrderList = q.list();
      return washBeautyOrderList;
    } finally {
      release(session);
    }
  }

  public List<Service> getServiceByShopIdAndNameRemovalTrimAndUpper(Long shopId, String serviceName, Long serviceId) {
    Session session = this.getSession();

    try {
      Query q = SQL.getServiceByShopIdAndNameRemovalTrimAndUpper(session, shopId, serviceName, serviceId);
      return (List<Service>) q.list();
    } finally {
      release(session);
    }
  }

  public Receivable getReceivableByShopIdAndOrderId(long shopId, long orderId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getReceivableByShopIdAndOrderId(session, shopId, orderId);
      return (Receivable) q.uniqueResult();
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return null;
    } finally {
      release(session);
    }
  }

  public Service getServiceById(Long shopId, Long serviceId) {
    Session session = this.getSession();

    try {
      Query q = SQL.getServiceById(session, shopId, serviceId);

      List<Service> serviceList = (List<Service>) q.list();

      if (CollectionUtils.isNotEmpty(serviceList)) {
        return serviceList.get(0);
      }

      return null;
    } finally {
      release(session);
    }
  }

  public Service getRFServiceByServiceNameAndShopId(Long shopId, String serviceName) {
    Session session = this.getSession();
    try {
      Query q = SQL.getRFServiceByServiceNameAndShopId(session, shopId, serviceName);
      List<Service> serviceList = (List<Service>) q.list();

      if (CollectionUtils.isEmpty(serviceList)) {
        return null;
      } else if (serviceList.size() > 1) {
        LOG.debug("getRFServiceByServiceNameAndShopId");
        LOG.debug("shopId", shopId);
        LOG.debug("serviceName", serviceName);
        LOG.debug("serviceList", serviceList);

        return serviceList.get(0);
      } else if (serviceList.size() == 1) {
        return serviceList.get(0);
      }

      return null;
    } finally {
      release(session);
    }
  }

  public List<Service> searchSuggestionForServices(Long shopId, String searchKey) {
    Session session = this.getSession();
    try {
      Query q = SQL.searchSuggestionForServices(session, shopId, searchKey);
      return (List<Service>) q.list();
    } finally {
      release(session);
    }
  }

  public Map<Long, ReceivableDTO> getReceivableDTOByShopIdAndArrayOrderId(Long shopId, Long... orderId) {
    Session session = this.getSession();
    try {
      Query query = SQL.getReceivableDTOByShopIdAndArrayOrderId(session, shopId, orderId);
      List<Receivable> receivableList = query.list();
      if (CollectionUtils.isEmpty(receivableList)) return new HashMap<Long, ReceivableDTO>();
      Map<Long, ReceivableDTO> map = new HashMap<Long, ReceivableDTO>();
      for (Receivable receivable : receivableList) {
        map.put(receivable.getOrderId(), receivable.toDTO());
      }
      return map;
    } finally {
      release(session);
    }
  }

  public List<Long> getReturnBorrowOrderIds(Long shopId, int start, int pageSize) {
    Session session = this.getSession();
    try {
      Query query = SQL.getReturnBorrowOrderIds(session, shopId, start, pageSize);
      return query.list();
    } finally {
      release(session);
    }
  }

  public List<Long> getBorrowOrderIds(Long shopId, int start, int pageSize) {
    Session session = this.getSession();
    try {
      Query query = SQL.getBorrowOrderIds(session, shopId, start, pageSize);
      return query.list();
    } finally {
      release(session);
    }
  }

  public List<Long> getInnerReturnOrderIds(Long shopId, int start, int pageSize) {
    Session session = this.getSession();
    try {
      Query query = SQL.getInnerReturnOrderIds(session, shopId, start, pageSize);
      return query.list();
    } finally {
      release(session);
    }
  }

  public List<Long> getInnerPickingOrderIds(Long shopId, int start, int pageSize) {
    Session session = this.getSession();
    try {
      Query query = SQL.getInnerPickingOrderIds(session, shopId, start, pageSize);
      return query.list();
    } finally {
      release(session);
    }
  }

  public List<Long> getAllocateRecordOrderIds(Long shopId, int start, int pageSize) {
    Session session = this.getSession();
    try {
      Query query = SQL.getAllocateRecordOrderIds(session, shopId, start, pageSize);
      return query.list();
    } finally {
      release(session);
    }
  }

  public List<Long> getInventoryCheckOrderIds(Long shopId, int start, int pageSize) {
    Session session = this.getSession();
    try {
      Query query = SQL.getInventoryCheckOrderIds(session, shopId, start, pageSize);
      return query.list();
    } finally {
      release(session);
    }
  }

  public List<Long> getRepairOrderIds(Long shopId, int start, int pageSize) {
    Session session = this.getSession();
    try {
      Query query = SQL.getRepairOrderIds(session, shopId, start, pageSize);
      return query.list();
    } finally {
      release(session);
    }
  }

  public List<RepairOrderItem> getRepairOrderItemByShopIdAndArrayOrderId(Long shopId, Long... orderId) {
    Session session = this.getSession();
    try {
      Query query = SQL.getRepairOrderItemByShopIdAndArrayOrderId(session, shopId, orderId);
      return query.list();
    } finally {
      release(session);
    }
  }

  public List<RepairOrderService> getRepairOrderServicesByShopIdAndArrayOrderId(Long shopId, Long... orderId) {

    Session session = getSession();
    try {
      Query query = SQL.getRepairOrderServicesByShopIdAndArrayOrderId(session, shopId, orderId);
      return query.list();
    } finally {
      release(session);
    }
  }


  public List<PurchaseInventoryDTO> getPurchaseInventorysByShopIdAndOrderIds(Long shopId, Long... orderIds) {
    Session session = this.getSession();
    try {
      Query query = SQL.getPurchaseInventoryByShopIdAndArrayOrderId(session, shopId, orderIds);
      List<PurchaseInventory> purchaseInventoryList = query.list();
      if (CollectionUtils.isEmpty(purchaseInventoryList)) return new ArrayList<PurchaseInventoryDTO>();
      List<PurchaseInventoryDTO> purchaseInventoryDTOList = new ArrayList<PurchaseInventoryDTO>();
      for (PurchaseInventory purchaseInventory : purchaseInventoryList) {
        if (purchaseInventory == null) continue;
        purchaseInventoryDTOList.add(purchaseInventory.toDTO());
      }
      return purchaseInventoryDTOList;
    } finally {
      release(session);
    }
  }

  public Map<Long, PurchaseOrderDTO> getPurchaseOrderByShopIdAndArrayOrderId(Long shopId, Long... orderIds) {
    Session session = this.getSession();
    try {
      Query query = SQL.getPurchaseOrdersByShopIdAndOrderIds(session, shopId, orderIds);
      List<PurchaseOrder> purchaseInventoryList = query.list();
      if (CollectionUtils.isEmpty(purchaseInventoryList)) return new HashMap<Long, PurchaseOrderDTO>();
      Map<Long, PurchaseOrderDTO> map = new HashMap<Long, PurchaseOrderDTO>();
      for (PurchaseOrder purchaseOrder : purchaseInventoryList) {
        map.put(purchaseOrder.getId(), purchaseOrder.toDTO());
      }
      return map;
    } finally {
      release(session);
    }
  }

  public Map<Long, PurchaseReturnDTO> getPurchaseReturnByShopIdAndArrayOrderId(Long shopId, Long... orderIds) {
    Session session = this.getSession();
    try {
      Query query = SQL.getPurchaseOrdersByShopIdAndOrderIds(session, shopId, orderIds);
      List<PurchaseReturn> purchaseInventoryList = query.list();
      if (CollectionUtils.isEmpty(purchaseInventoryList)) return new HashMap<Long, PurchaseReturnDTO>();
      Map<Long, PurchaseReturnDTO> map = new HashMap<Long, PurchaseReturnDTO>();
      for (PurchaseReturn purchaseReturn : purchaseInventoryList) {
        map.put(purchaseReturn.getId(), purchaseReturn.toDTO());
      }
      return map;
    } finally {
      release(session);
    }
  }

  public List<PurchaseReturnDTO> getPurchaseReturnByShopIdAndOrderIds(Long shopId, Long... orderIds) {
    Session session = this.getSession();
    try {
      Query query = SQL.getPurchaseReturnsByShopIdAndOrderIds(session, shopId, orderIds);
      List<PurchaseReturn> purchaseInventoryList = query.list();
      if (CollectionUtils.isEmpty(purchaseInventoryList)) return new ArrayList<PurchaseReturnDTO>();
      List<PurchaseReturnDTO> purchaseReturnDTOList = new ArrayList<PurchaseReturnDTO>();
      for (PurchaseReturn purchaseReturn : purchaseInventoryList) {
        if (purchaseReturn == null) continue;
        purchaseReturnDTOList.add(purchaseReturn.toDTO());
      }
      return purchaseReturnDTOList;
    } finally {
      release(session);
    }
  }

  public List<SalesReturnDTO> getSalesReturnByShopIdAndOrderIds(Long shopId, Long... orderIds) {
    Session session = this.getSession();
    try {
      Query query = SQL.getSalesReturnsByShopIdAndOrderIds(session, shopId, orderIds);
      List<SalesReturn> salesReturnList = query.list();
      if (CollectionUtils.isEmpty(salesReturnList)) return new ArrayList<SalesReturnDTO>();
      List<SalesReturnDTO> salesReturnDTOList = new ArrayList<SalesReturnDTO>();
      for (SalesReturn salesReturn : salesReturnList) {
        if (salesReturn == null) continue;
        salesReturnDTOList.add(salesReturn.toDTO());
      }
      return salesReturnDTOList;
    } finally {
      release(session);
    }
  }

  public Map<Long, MemberCardOrderDTO> getMemberCardOrderByShopIdAndArrayOrderId(Long shopId, Long... orderIds) {
    Session session = this.getSession();
    try {
      Query query = SQL.getMemberCardOrderByShopIdAndArrayOrderId(session, shopId, orderIds);
      List<MemberCardOrder> memberCardOrderList = query.list();
      if (CollectionUtils.isEmpty(memberCardOrderList)) return new HashMap<Long, MemberCardOrderDTO>();
      Map<Long, MemberCardOrderDTO> map = new HashMap<Long, MemberCardOrderDTO>();
      for (MemberCardOrder memberCardOrder : memberCardOrderList) {
        map.put(memberCardOrder.getId(), memberCardOrder.toDTO());
      }
      return map;
    } finally {
      release(session);
    }
  }

  public List<MemberCardOrderDTO> getMemberCardOrderDetailByShopIdAndArrayOrderId(Long shopId, Long... orderIds) {
    Session session = this.getSession();
    try {
      List<MemberCardOrderDTO> list = new ArrayList<MemberCardOrderDTO>();
      Map<Long, List<MemberCardOrderItemDTO>> itemMap = new HashMap<Long, List<MemberCardOrderItemDTO>>();
      Query query = SQL.getMemberCardOrderItemsByShopIdAndArrayOrderId(session, shopId, orderIds);
      List<MemberCardOrderItem> memberCardOrderItemList = query.list();
      if (CollectionUtils.isNotEmpty(memberCardOrderItemList)) {
        List<MemberCardOrderItemDTO> memberCardOrderItemDTOList;
        for (MemberCardOrderItem item : memberCardOrderItemList) {
          if (itemMap.get(item.getMemberCardOrderId()) == null) {
            memberCardOrderItemDTOList = new ArrayList<MemberCardOrderItemDTO>();
            memberCardOrderItemDTOList.add(item.toDTO());
            itemMap.put(item.getMemberCardOrderId(), memberCardOrderItemDTOList);
          } else {
            itemMap.get(item.getMemberCardOrderId()).add(item.toDTO());
          }
        }
      }
      Map<Long, List<MemberCardOrderServiceDTO>> orderServiceMap = new HashMap<Long, List<MemberCardOrderServiceDTO>>();
      query = getMemberCardOrderServicesByShopIdAndArrayOrderId(session, shopId, orderIds);
      List<MemberCardOrderService> memberCardOrderServiceList = query.list();
      if (CollectionUtils.isNotEmpty(memberCardOrderServiceList)) {
        List<MemberCardOrderServiceDTO> memberCardOrderServiceDTOList;
        for (MemberCardOrderService service : memberCardOrderServiceList) {
          if (orderServiceMap.get(service.getMemberCardOrderId()) == null) {
            memberCardOrderServiceDTOList = new ArrayList<MemberCardOrderServiceDTO>();
            memberCardOrderServiceDTOList.add(service.toDTO());
            orderServiceMap.put(service.getMemberCardOrderId(), memberCardOrderServiceDTOList);
          } else {
            orderServiceMap.get(service.getMemberCardOrderId()).add(service.toDTO());
          }
        }
      }

      query = SQL.getMemberCardOrderByShopIdAndArrayOrderId(session, shopId, orderIds);
      List<MemberCardOrder> memberCardOrderList = query.list();
      if (CollectionUtils.isEmpty(memberCardOrderList)) return list;

      for (MemberCardOrder memberCardOrder : memberCardOrderList) {
        MemberCardOrderDTO memberCardOrderDTO = memberCardOrder.toDTO();
        memberCardOrderDTO.setMemberCardOrderItemDTOs(itemMap.get(memberCardOrderDTO.getId()));
        memberCardOrderDTO.setNewMemberCardOrderServiceDTOs(orderServiceMap.get(memberCardOrderDTO.getId()));
        list.add(memberCardOrderDTO);
      }
      return list;
    } finally {
      release(session);
    }
  }

  public Map<Long, SalesOrderDTO> getSalesOrderByShopIdAndArrayOrderId(Long shopId, Long... orderIds) {
    Session session = this.getSession();
    try {
      Query query = SQL.getSalesOrdersByShopIdAndOrderIds(session, shopId, orderIds);
      List<SalesOrder> salesOrderList = query.list();
      if (CollectionUtils.isEmpty(salesOrderList)) return new HashMap<Long, SalesOrderDTO>();
      Map<Long, SalesOrderDTO> map = new HashMap<Long, SalesOrderDTO>();
      for (SalesOrder salesOrder : salesOrderList) {
        map.put(salesOrder.getId(), salesOrder.toDTO());
      }
      return map;
    } finally {
      release(session);
    }
  }

  public List<SalesOrderDTO> getSalesOrdersByShopIdAndOrderIds(Long shopId, Long... orderIds) {
    Session session = this.getSession();
    try {
      Query query = SQL.getSalesOrdersByShopIdAndOrderIds(session, shopId, orderIds);
      List<SalesOrder> salesOrderList = query.list();
      if (CollectionUtils.isEmpty(salesOrderList)) return new ArrayList<SalesOrderDTO>();
      List<SalesOrderDTO> salesOrderDTOList = new ArrayList<SalesOrderDTO>();
      for (SalesOrder salesOrder : salesOrderList) {
        if (salesOrder == null) continue;
        salesOrderDTOList.add(salesOrder.toDTO());
      }
      return salesOrderDTOList;
    } finally {
      release(session);
    }
  }

  public List<PreBuyOrder> getPreBuyOrdersByShopIdAndOrderIds(Long shopId, Long... orderIds) {
    Session session = this.getSession();
    try {
      Query query = SQL.getPreBuyOrdersByShopIdAndOrderIds(session, shopId, orderIds);

      return (List<PreBuyOrder>) query.list();
    } finally {
      release(session);
    }
  }

  public List<QuotedPreBuyOrder> getQuotedPreBuyOrdersByShopIdAndOrderIds(Long shopId, Long... orderIds) {
    Session session = this.getSession();
    try {
      Query query = SQL.getQuotedPreBuyOrdersByShopIdAndOrderIds(session, shopId, orderIds);

      return (List<QuotedPreBuyOrder>) query.list();
    } finally {
      release(session);
    }
  }

  public List<AllocateRecord> getAllocateRecordsByShopIdAndOrderIds(Long shopId, Long... orderIds) {
    Session session = this.getSession();
    try {
      Query query = SQL.getAllocateRecordsByShopIdAndOrderIds(session, shopId, orderIds);
      return (List<AllocateRecord>) query.list();
    } finally {
      release(session);
    }
  }

  public List<InnerPicking> getInnerPickingsByShopIdAndOrderIds(Long shopId, Long... orderIds) {
    Session session = this.getSession();
    try {
      Query query = SQL.getInnerPickingsByShopIdAndOrderIds(session, shopId, orderIds);
      return (List<InnerPicking>) query.list();
    } finally {
      release(session);
    }
  }

  public List<BorrowOrder> getBorrowOrdersByShopIdAndOrderIds(Long shopId, Long... orderIds) {
    Session session = this.getSession();
    try {
      Query query = SQL.getBorrowOrdersByShopIdAndOrderIds(session, shopId, orderIds);
      return (List<BorrowOrder>) query.list();
    } finally {
      release(session);
    }
  }

  public List<ReturnOrder> getReturnBorrowOrdersByShopIdAndOrderIds(Long shopId, Long... orderIds) {
    Session session = this.getSession();
    try {
      Query query = SQL.getReturnBorrowOrdersByShopIdAndOrderIds(session, shopId, orderIds);
      return (List<ReturnOrder>) query.list();
    } finally {
      release(session);
    }
  }


  public List<InnerReturn> getInnerReturnsByShopIdAndOrderIds(Long shopId, Long... orderIds) {
    Session session = this.getSession();
    try {
      Query query = SQL.getInnerReturnsByShopIdAndOrderIds(session, shopId, orderIds);
      return (List<InnerReturn>) query.list();
    } finally {
      release(session);
    }
  }

  public List<InventoryCheck> getInventoryChecksByShopIdAndOrderIds(Long shopId, Long... orderIds) {
    Session session = this.getSession();
    try {
      Query query = SQL.getInventoryChecksByShopIdAndOrderIds(session, shopId, orderIds);
      return (List<InventoryCheck>) query.list();
    } finally {
      release(session);
    }
  }

  public Map<Long, WashBeautyOrderDTO> getWashBeautyOrderByShopIdAndArrayOrderId(Long shopId, Long... orderIds) {
    Session session = this.getSession();
    try {
      Query query = SQL.getWashBeautyOrderByShopIdAndArrayOrderId(session, shopId, orderIds);
      List<WashBeautyOrder> washBeautyOrderList = query.list();
      if (CollectionUtils.isEmpty(washBeautyOrderList)) return new HashMap<Long, WashBeautyOrderDTO>();
      Map<Long, WashBeautyOrderDTO> map = new HashMap<Long, WashBeautyOrderDTO>();
      for (WashBeautyOrder washBeautyOrder : washBeautyOrderList) {
        map.put(washBeautyOrder.getId(), washBeautyOrder.toDTO());
      }
      return map;
    } finally {
      release(session);
    }
  }

  public Map<Long, Map<Long, WashBeautyOrderItemDTO>> getWashBeautyOrderItemByShopIdAndArrayOrderId(Long shopId, Long... orderIds) {
    Session session = this.getSession();
    try {
      Query query = SQL.getWashBeautyOrderItemByShopIdAndArrayOrderId(session, shopId, orderIds);
      List<WashBeautyOrderItem> washBeautyOrderItemList = query.list();
      if (CollectionUtils.isEmpty(washBeautyOrderItemList))
        return new HashMap<Long, Map<Long, WashBeautyOrderItemDTO>>();
      Map<Long, Map<Long, WashBeautyOrderItemDTO>> itemMap = new HashMap<Long, Map<Long, WashBeautyOrderItemDTO>>();
      Map<Long, WashBeautyOrderItemDTO> washBeautyOrderItemDTOMap;
      for (WashBeautyOrderItem washBeautyOrderItem : washBeautyOrderItemList) {
        if (itemMap.get(washBeautyOrderItem.getWashBeautyOrderId()) == null) {
          washBeautyOrderItemDTOMap = new HashMap<Long, WashBeautyOrderItemDTO>();
          washBeautyOrderItemDTOMap.put(washBeautyOrderItem.getId(), washBeautyOrderItem.toDTO());
          itemMap.put(washBeautyOrderItem.getWashBeautyOrderId(), washBeautyOrderItemDTOMap);
        } else {
          itemMap.get(washBeautyOrderItem.getWashBeautyOrderId()).put(washBeautyOrderItem.getId(), washBeautyOrderItem.toDTO());
        }
      }
      return itemMap;
    } finally {
      release(session);
    }
  }


  public List<PurchaseInventoryItem> getPurchaseInventoryItemByOrderIds(Long... ids) {
    Session session = this.getSession();
    try {
      Query q = getPurchaseInventoryItemDTOsByOrderIds(session, ids);
      return (List<PurchaseInventoryItem>) q.list();
    } finally {
      release(session);
    }
  }

  public List<Long> getPurchaseInventoryIds(Long shopId, int start, int size) {
    Session session = this.getSession();
    try {
      Query q = SQL.getPurchaseInventoryIds(session, shopId, start, size);
      return (List<Long>) q.list();
    } finally {
      release(session);
    }
  }

  public List<Long> getWashBeautyOrderIds(Long shopId, int start, int pageSize) {
    Session session = this.getSession();
    try {
      Query query = SQL.getWashBeautyOrderIds(session, shopId, start, pageSize);
      return query.list();
    } finally {
      release(session);
    }
  }

  public List<Long> getMemberCardOrderIds(Long shopId, int start, int pageSize) {
    Session session = this.getSession();
    try {
      Query query = SQL.getMemberCardOrderIds(session, shopId, start, pageSize);
      return query.list();
    } finally {
      release(session);
    }
  }

  public List<Long> getMemberReturnCardOrderIds(Long shopId, int start, int pageSize) {
    Session session = this.getSession();
    try {
      Query query = SQL.getMemberReturnCardOrderIds(session, shopId, start, pageSize);
      return query.list();
    } finally {
      release(session);
    }
  }

  public List<PurchaseOrderDTO> getPurchaseOrdersByShopIdAndOrderIds(Long shopId, Long... orderIds) {
    Session session = this.getSession();
    try {
      Query query = SQL.getPurchaseOrdersByShopIdAndOrderIds(session, shopId, orderIds);
      List<PurchaseOrder> purchaseInventoryList = query.list();
      if (CollectionUtils.isEmpty(purchaseInventoryList)) return new ArrayList<PurchaseOrderDTO>();
      List<PurchaseOrderDTO> purchaseOrderDTOList = new ArrayList<PurchaseOrderDTO>();
      for (PurchaseOrder purchaseOrder : purchaseInventoryList) {
        if (purchaseOrder == null) continue;
        purchaseOrderDTOList.add(purchaseOrder.toDTO());
      }
      return purchaseOrderDTOList;
    } finally {
      release(session);
    }
  }

  public List<RepairOrderDTO> getRepairOrdersByShopIdAndOrderIds(Long shopId, Long... orderIds) {
    Session session = this.getSession();
    try {
      Query query = SQL.getRepairOrdersByShopIdAndOrderIds(session, shopId, orderIds);
      List<RepairOrder> repairOrderList = query.list();
      if (CollectionUtils.isEmpty(repairOrderList)) return new ArrayList<RepairOrderDTO>();
      List<RepairOrderDTO> repairOrderDTOList = new ArrayList<RepairOrderDTO>();
      for (RepairOrder repairOrder : repairOrderList) {
        if (repairOrder == null) continue;
        repairOrderDTOList.add(repairOrder.toDTO());
      }
      return repairOrderDTOList;
    } finally {
      release(session);
    }
  }

  public List<WashBeautyOrderDTO> getWashBeautyOrderDetailByShopIdAndArrayOrderId(Long shopId, Long... orderIds) {
    Session session = this.getSession();
    try {
      List<WashBeautyOrderDTO> washBeautyOrderDTOList = new ArrayList<WashBeautyOrderDTO>();
      Query query = SQL.getWashBeautyOrderItemByShopIdAndArrayOrderId(session, shopId, orderIds);
      List<WashBeautyOrderItem> washBeautyOrderItemList = query.list();
      if (CollectionUtils.isEmpty(washBeautyOrderItemList)) return washBeautyOrderDTOList;
      Map<Long, List<WashBeautyOrderItemDTO>> itemMap = new HashMap<Long, List<WashBeautyOrderItemDTO>>();
      List<WashBeautyOrderItemDTO> washBeautyOrderItemDTOList;

      for (WashBeautyOrderItem washBeautyOrderItem : washBeautyOrderItemList) {
        if (itemMap.get(washBeautyOrderItem.getWashBeautyOrderId()) == null) {
          washBeautyOrderItemDTOList = new ArrayList<WashBeautyOrderItemDTO>();
          washBeautyOrderItemDTOList.add(washBeautyOrderItem.toDTO());
          itemMap.put(washBeautyOrderItem.getWashBeautyOrderId(), washBeautyOrderItemDTOList);
        } else {
          itemMap.get(washBeautyOrderItem.getWashBeautyOrderId()).add(washBeautyOrderItem.toDTO());
        }
      }

      query = SQL.getWashBeautyOrderByShopIdAndArrayOrderId(session, shopId, orderIds);
      List<WashBeautyOrder> washBeautyOrderList = query.list();
      if (CollectionUtils.isEmpty(washBeautyOrderList)) return washBeautyOrderDTOList;

      for (WashBeautyOrder washBeautyOrder : washBeautyOrderList) {
        WashBeautyOrderDTO washBeautyOrderDTO = washBeautyOrder.toDTO();
        washBeautyOrderDTO.setWashBeautyOrderItemDTOList(itemMap.get(washBeautyOrderDTO.getId()));
        washBeautyOrderDTOList.add(washBeautyOrderDTO);
      }
      return washBeautyOrderDTOList;
    } finally {
      release(session);
    }
  }

  public Map<Long, ServiceDTO> getServiceByServiceIdSet(Long shopId, Set<Long> serviceIds) {
    Session session = this.getSession();
    try {
      Query query = SQL.getServiceByServiceIdSet(session, shopId, serviceIds);
      List<Service> serviceList = query.list();
      if (CollectionUtils.isEmpty(serviceList)) return new HashMap<Long, ServiceDTO>();
      Map<Long, ServiceDTO> map = new HashMap<Long, ServiceDTO>();
      Map<Long, CategoryDTO> scMap = this.getServiceCategoryMapByServiceId(shopId, serviceIds.toArray(new Long[serviceIds.size()]));
      ServiceDTO serviceDTO = null;
      for (Service service : serviceList) {
        serviceDTO = service.toDTO();
        serviceDTO.setCategoryDTO(scMap.get(service.getId()));
        map.put(service.getId(), serviceDTO);
      }
      return map;
    } finally {
      release(session);
    }
  }

  public List<ServiceHistory> getLastServiceHistories(Long shopId, Set<Long> serviceIds) {
    if (shopId == null || CollectionUtils.isEmpty(serviceIds)) {
      return new ArrayList<ServiceHistory>();
    }
    Session session = this.getSession();
    try {
      Query query = SQL.getLastServiceHistories(session, shopId, serviceIds);
      return query.list();
    } finally {
      release(session);
    }
  }

  public List<PurchaseReturnItem> getPurchaseReturnItemByOrderIds(Long... ids) {
    Session session = this.getSession();
    try {
      Query q = getPurchaseReturnItemDTOsByOrderIds(session, ids);
      return (List<PurchaseReturnItem>) q.list();
    } finally {
      release(session);
    }
  }

  public List<SalesReturnItem> getSalesReturnItemByOrderIds(Long... ids) {
    Session session = this.getSession();
    try {
      Query q = getSalesReturnItemDTOsByOrderIds(session, ids);
      return (List<SalesReturnItem>) q.list();
    } finally {
      release(session);
    }
  }

  public List<Long> getPurchaseReturnIds(Long shopId, int start, int size) {
    Session session = this.getSession();
    try {
      Query q = SQL.getPurchaseReturnIds(session, shopId, start, size);
      return (List<Long>) q.list();
    } finally {
      release(session);
    }
  }

  public List<Long> getSalesReturnIds(Long shopId, int start, int size) {
    Session session = this.getSession();
    try {
      Query q = SQL.getSalesReturnIds(session, shopId, start, size);
      return (List<Long>) q.list();
    } finally {
      release(session);
    }
  }

  public List<PurchaseOrderItem> getPurchaseOrderItems(Long... ids) {
    Session session = this.getSession();
    try {
      Query q = SQL.getPurchaseOrderItems(session, ids);
      return (List<PurchaseOrderItem>) q.list();
    } finally {
      release(session);
    }
  }

  public List<Long> getPurchaseOrders(Long shopId, int start, int size) {
    Session session = this.getSession();
    try {
      Query q = SQL.getPurchaseOrderIds(session, shopId, start, size);
      return (List<Long>) q.list();
    } finally {
      release(session);
    }
  }

  public List<SalesOrderItem> getSalesOrderItems(Long... ids) {
    Session session = this.getSession();
    try {
      Query q = SQL.getSalesOrderItems(session, ids);
      return (List<SalesOrderItem>) q.list();
    } finally {
      release(session);
    }
  }

  public List<PreBuyOrderItem> getPreBuyOrderItems(Long... orderIds) {
    Session session = this.getSession();
    try {
      Query q = SQL.getPreBuyOrderItems(session, orderIds);
      return (List<PreBuyOrderItem>) q.list();
    } finally {
      release(session);
    }
  }

  public List<PreBuyOrder> getPreBuyOrder(Long... orderIds) {
    Session session = this.getSession();
    try {
      Query q = SQL.getPreBuyOrder(session, orderIds);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<QuotedPreBuyOrderItem> getQuotedPreBuyOrderItems(Long... orderIds) {
    Session session = this.getSession();
    try {
      Query q = SQL.getQuotedPreBuyOrderItems(session, orderIds);
      return (List<QuotedPreBuyOrderItem>) q.list();
    } finally {
      release(session);
    }
  }

  public List<InventoryCheckItem> getInventoryCheckItems(Long... ids) {
    Session session = this.getSession();
    try {
      Query q = SQL.getInventoryCheckItems(session, ids);
      return (List<InventoryCheckItem>) q.list();
    } finally {
      release(session);
    }
  }

  public List<InventoryCheckItem> getInventoryCheckItemByProductIds(Long shopId, Pager pager, Long... productIds) {
    Session session = this.getSession();
    try {
      Query q = SQL.getInventoryCheckItemByProductIds(session, shopId, pager, productIds);
      return (List<InventoryCheckItem>) q.list();
    } finally {
      release(session);
    }
  }

  public int getInventoryCheckItemCountByProductIds(Long shopId, Long... productIds) {
    Session session = this.getSession();
    try {
      Query q = SQL.getInventoryCheckItemCountByProductIds(session, shopId, productIds);
      return Integer.valueOf(q.uniqueResult().toString());
    } finally {
      release(session);
    }
  }

  public List<AllocateRecordItem> getAllocateRecordItems(Long... ids) {
    Session session = this.getSession();
    try {
      Query q = SQL.getAllocateRecordItems(session, ids);
      return (List<AllocateRecordItem>) q.list();
    } finally {
      release(session);
    }
  }

  public List<Long> getSalesOrderIds(Long shopId, int start, int size) {
    Session session = this.getSession();
    try {
      Query q = SQL.getSalesOrderIds(session, shopId, start, size);
      return (List<Long>) q.list();
    } finally {
      release(session);
    }
  }

  public List<Long> getPreBuyOrderIds(Long shopId, int start, int size) {
    Session session = this.getSession();
    try {
      Query q = SQL.getPreBuyOrderIds(session, shopId, start, size);
      return (List<Long>) q.list();
    } finally {
      release(session);
    }
  }

  public List<Long> getQuotedPreBuyOrderIds(Long shopId, int start, int size) {
    Session session = this.getSession();
    try {
      Query q = SQL.getQuotedPreBuyOrderIds(session, shopId, start, size);
      return (List<Long>) q.list();
    } finally {
      release(session);
    }
  }

  public List<QuotedPreBuyOrder> getQuotedPreBuyOrders(Long shopId, int start, int size) {
    Session session = this.getSession();
    try {
      Query q = SQL.getQuotedPreBuyOrders(session, shopId, start, size);
      return (List<QuotedPreBuyOrder>) q.list();
    } finally {
      release(session);
    }
  }


  public List<QuotedPreBuyOrder> getQuotedPreBuyOrdersByItemId(Long shopId, Long... quotedBuyOrderItemId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getQuotedPreBuyOrdersByItemId(session, shopId, quotedBuyOrderItemId);
      return (List<QuotedPreBuyOrder>) q.list();
    } finally {
      release(session);
    }
  }

  /**
   * 根据店面Id 和退货单ID 查找退货单
   *
   * @param shopId
   * @param id
   * @return
   */
  public PurchaseReturnDTO getPurchaseReturnById(Long shopId, Long id) {
    Session session = this.getSession();
    try {
      Query q = SQL.getPurchaseReturnById(session, shopId, id);
      List<PurchaseReturn> purchaseReturns = (List<PurchaseReturn>) q.list();
      if (CollectionUtils.isNotEmpty(purchaseReturns)) {
        return purchaseReturns.get(0).toDTO();
      }
      return null;
    } finally {
      release(session);
    }
  }

  /**
   * 根据店面Id 和退货单编号 查找退货单
   *
   * @param shopId
   * @param no     退货单编号
   * @return
   */
  public PurchaseReturn getPurchaseReturnByNo(Long shopId, String no) {
    Session session = this.getSession();
    try {
      Query q = SQL.getPurchaseReturnByNo(session, shopId, no);
      List<PurchaseReturn> purchaseReturns = (List<PurchaseReturn>) q.list();
      if (CollectionUtils.isNotEmpty(purchaseReturns)) {
        return purchaseReturns.get(0);
      }
      return null;
    } finally {
      release(session);
    }
  }


  /**
   * 查询应付款总数
   *
   * @param supplierId
   * @param fromTime   开始时间
   * @param toTime     结束时间
   * @param
   * @return
   */
  public int countSearchPayable(Long shopId, Long supplierId, Long fromTime, Long toTime) {
    Session session = this.getSession();
    try {
      Query q = SQL.countSearchPayable(session, shopId, supplierId, fromTime, toTime);
      return Integer.parseInt(q.uniqueResult().toString());
    } finally {
      release(session);
    }
  }

  /**
   * 分页查询应付款记录
   *
   * @param shopId
   * @param supplierId 供应商ID
   * @param fromTime   开始时间
   * @param toTime     结束时间
   * @param sort       排序
   * @param pager      分页
   * @return
   */
  public List<Payable> searchPayable(Long shopId, Long supplierId, Long fromTime, Long toTime, Sort sort, Pager pager) {
    Session session = this.getSession();
    try {
      Query q = SQL.searchPayable(session, shopId, supplierId, fromTime, toTime, sort, pager);
      return q.list();
    } finally {
      release(session);
    }
  }

  /**
   * 单个单据付款历史记录
   *
   * @param purchaseInventoryId
   * @param payableHistoryDTOid
   * @return
   */
  public PayableHistoryRecord getPayHistoryRecord(Long purchaseInventoryId, Long payableHistoryDTOid, Long shopId) {
    Session session = this.getSession();
    try {
      Query q = searchPayableHistoryRecord(session, purchaseInventoryId, payableHistoryDTOid, shopId);
      List<PayableHistoryRecord> payableHistoryRecords = q.list();
      if (CollectionUtils.isEmpty(payableHistoryRecords)) {
        return null;
      }
      return payableHistoryRecords.get(0);
    } finally {
      release(session);
    }
  }

  /**
   * 根据ShopId查询应付款总额
   *
   * @param shopId
   * @return
   */
  public double getTotalPayableByShopId(Long shopId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getTotalPayableByShopId(session, shopId);
      Object o = q.uniqueResult();
      if (o == null) return 0.0d;
      return Double.parseDouble(o.toString());
    } finally {
      release(session);
    }

  }

  /**
   * 根据shopId获得店面供应商总定金
   *
   * @param shopId
   * @return
   */
  public float getTotaDepositByShopId(Long shopId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getTotaDepositByShopId(session, shopId);
      Object o = q.uniqueResult();
      if (o == null) return 0.0f;
      return Float.parseFloat(o.toString());
    } finally {
      release(session);
    }
  }

  /**
   * 根据供应商ID获取每个供应商的总付款
   *
   * @param supplierId
   * @param shopId
   * @return
   */
  public List<Double> getSumPayableBySupplierId(Long supplierId, Long shopId, OrderDebtType debtType) {
    Session session = this.getSession();
    try {
      Query q = SQL.getSumPayableBySupplierId(session, supplierId, shopId, debtType);
      List<Object> list = q.list();
      List<Double> doubleList = new ArrayList<Double>();
      if (CollectionUtils.isNotEmpty(list)) {
        Object[] array = (Object[]) list.get(0);
        if (!ArrayUtils.isEmpty(array) && array[0] != null && array[1] != null && array[2] != null) {
          doubleList.add((Double) (array[0]));
          doubleList.add((Double) (array[1]));
          doubleList.add(Double.valueOf(array[2].toString()));
          return doubleList;
        }
      }
      doubleList.add(0D);
      doubleList.add(0D);
      doubleList.add(0D);
      return doubleList;

    } finally {
      release(session);
    }
  }

  public Double getSumReceivableByCustomerId(Long customerId, Long shopId, OrderDebtType debtType) {
    Session session = this.getSession();
    try {
      Query q = SQL.getSumReceivableByCustomerId(session, customerId, shopId, debtType);
      Double sum = (Double) q.list().get(0);
      return sum;

    } finally {
      release(session);
    }
  }

  /**
   * 根据供应商ID获取每个供应商的总付款
   *
   * @param supplierId
   * @param shopId
   * @return
   */
  public List<Double> getStatementOrderSumPayable(Long supplierId, Long shopId, OrderDebtType debtType) {
    Session session = this.getSession();
    try {
      Query q = SQL.getStatementOrderSumPayable(session, supplierId, shopId, debtType);
      List<Object> list = q.list();
      List<Double> doubleList = new ArrayList<Double>();
      if (CollectionUtils.isNotEmpty(list)) {
        Object[] array = (Object[]) list.get(0);
        if (!ArrayUtils.isEmpty(array) && array[0] != null && array[1] != null && array[2] != null) {
          doubleList.add((Double) (array[0]));
          doubleList.add((Double) (array[1]));
          doubleList.add(Double.valueOf(array[2].toString()));
          return doubleList;
        }
      }
      doubleList.add(0D);
      doubleList.add(0D);
      doubleList.add(0D);
      return doubleList;

    } finally {
      release(session);
    }
  }


  /**
   * 根据供应商ID获取每个供应商的总付款
   *
   * @param supplierIdList
   * @param shopId
   * @return
   */
  public List<Object> getSumPayableBySupplierIdList(List<Long> supplierIdList, Long shopId, OrderDebtType debtType) {
    Session session = this.getSession();
    try {
      Query q = SQL.getSumPayableBySupplierIdList(session, supplierIdList, shopId, debtType);
      return q.list();
    } finally {
      release(session);
    }
  }

  /**
   * 根据供应商ID获取每个供应商的总付款
   *
   * @param supplierIdList
   * @param shopId
   * @return
   */
  public List<Object> getStatementOrderSumPayable(List<Long> supplierIdList, Long shopId, OrderDebtType debtType) {
    Session session = this.getSession();
    try {
      Query q = SQL.getStatementOrderSumPayable(session, supplierIdList, shopId, debtType);
      return q.list();
    } finally {
      release(session);
    }
  }


  /**
   * 根据供应商ID获取每个供应商的总定金
   *
   * @param supplierId
   * @param shopId
   * @return
   */
  public Double getSumDepositBySupplierId(Long supplierId, Long shopId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getSumDepositBySupplierId(session, supplierId, shopId);
      Object o = q.uniqueResult();
      if (o == null) return 0.0d;
      return Double.parseDouble(o.toString());
    } finally {
      release(session);
    }
  }

  /**
   * 根据供应商ID查询应付款总数
   *
   * @param supplierId
   * @param shopId
   * @return
   */
  public int getTotalCountOfPayable(Long supplierId, Long shopId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getTotalCountOfPayable(session, shopId, supplierId);
      return Integer.parseInt(q.uniqueResult().toString());
    } finally {
      release(session);
    }
  }

  /**
   * 根据供应商ID查询付款历史记录 总数
   *
   * @param shopId
   * @param supplierId
   * @param startTime
   * @param endTime
   * @return
   */
  public int getTotalCountOfPayableHistoryRecord(Long shopId, Long supplierId, Long startTime, Long endTime) {
    Session session = this.getSession();
    try {
      Query q = SQL.getTotalCountOfPayableHistoryRecord(session, shopId, supplierId, startTime, endTime);
      return Integer.parseInt(q.uniqueResult().toString());
    } finally {
      release(session);
    }
  }

  /**
   * @param shopId
   * @param supplierId
   * @param fromTimeLong
   * @param toTimeLong
   * @param sort
   * @param pager
   * @return
   */
  public List<PayableHistoryRecord> getPayableHistoryRecord(Long shopId, Long supplierId, Long fromTimeLong, Long toTimeLong, Sort sort, Pager pager) {
    Session session = this.getSession();
    try {
      Query q = SQL.getPayableHistoryRecord(session, shopId, supplierId, fromTimeLong, toTimeLong, sort, pager);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<PayableHistoryRecord> getAllPayableHistoryRecordBySupplierIds(Long shopId, Long[] supplierIds) {
    Session session = this.getSession();
    try {
      Query q = SQL.getAllPayableHistoryRecordBySupplierIds(session, shopId, supplierIds);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<PayableHistory> getAllPayableHistoryBySupplierIds(Long shopId, Long[] supplierIds) {
    Session session = this.getSession();
    try {
      Query q = SQL.getAllPayableHistoryBySupplierIds(session, shopId, supplierIds);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<PayableHistoryRecord> getPayableHistoryRecord(Long shopId, Long supplierId, Long inventoryId, PaymentTypes paymentTypes) {
    Session session = this.getSession();
    try {
      Query q = SQL.getPayableHistoryRecord(session, shopId, supplierId, inventoryId, paymentTypes);
      return q.list();
    } finally {
      release(session);
    }
  }

  /**
   * 根据suppliI找到对应的预付款
   *
   * @param shopId
   * @param supplierId
   * @return
   * @author zhangchuanlong
   */
  public Deposit getDepositBySupplierId(Long shopId, Long supplierId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getDepositBySupplierId(session, shopId, supplierId);
      if (CollectionUtils.isEmpty(q.list())) return null;
      return (Deposit) q.list().get(0);
    } finally {
      release(session);
    }
  }

  /**
   * @param shopId
   * @param supplierIds
   * @return
   */
  public List<Deposit> getDepositsBySupplierIds(Long shopId, Long[] supplierIds) {
    Session session = this.getSession();
    try {
      Query q = SQL.getDepositsBySupplierIds(session, shopId, supplierIds);
      return q.list();
    } finally {
      release(session);
    }
  }

  /**
   * 根据供应商shopID,入库单ID，供应商ID查找对应的应付款
   *
   * @param shopId
   * @param purchaserInventoryId 入库单ID
   * @param supplierId           供应商ID
   * @return
   */
  public Payable getInventoryPayable(Long shopId, Long purchaserInventoryId, Long supplierId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getInventoryPayable(session, shopId, purchaserInventoryId, supplierId);
      List list = q.list();
      if (CollectionUtils.isEmpty(list)) return null;
      return (Payable) list.get(0);
    } finally {
      release(session);
    }
  }

  /**
   * 根据shopiD,供应商ID，入库单ID 查找对应的付款历史记录
   *
   * @param shopId
   * @param supplierId
   * @param purchaseInventoryId 入库单ID
   * @return
   */
  public PayableHistoryRecord getPayHistoryRecordForRepeal(Long shopId, Long supplierId, Long purchaseInventoryId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getPayHistoryRecordForRepeal(session, shopId, supplierId, purchaseInventoryId);
      List list = q.list();
      if (CollectionUtils.isEmpty(list)) return null;
      return (PayableHistoryRecord) list.get(0);
    } finally {
      release(session);
    }
  }

  /**
   * 获取所有入库单，用于初始化应付款表
   *
   * @return
   * @author zhangchuanlong
   */
  public List<PurchaseInventory> getPurchaseInventory() {
    Session session = this.getSession();
    try {
      Query q = SQL.getPurchaseInventory(session);
      return q.list();
    } finally {
      release(session);
    }
  }

  public DraftOrder getDraftOrderById(Long shopId, Long draftOrderId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getDraftOrderById(session, shopId, draftOrderId);
      List<DraftOrder> draftOrders = (List<DraftOrder>) q.list();
      if (CollectionUtils.isNotEmpty(draftOrders)) {
        return draftOrders.get(0);
      }
      return null;
    } finally {
      release(session);
    }
  }

  public List<DraftOrder> getDraftOrdersByCustomerOrSupplierId(Long shopId, Long customerId) {
    Session session = this.getSession();
    try {
      return SQL.getDraftOrdersByCustomerOrSupplierId(session, shopId, customerId).list();
    } finally {
      release(session);
    }
  }

  public DraftOrder getDraftOrderByVechicle(Long shopId, String vechicle) {
    Session session = this.getSession();
    try {
      Query q = getDraftOrderByVehicle(session, shopId, vechicle);
      List<DraftOrder> draftOrders = (List<DraftOrder>) q.list();
      if (CollectionUtils.isNotEmpty(draftOrders)) {
        return draftOrders.get(0);
      }
      return null;
    } finally {
      release(session);
    }
  }

  public DraftOrder getDraftOrderByTxnOrderId(Long shopId, Long txnOrderId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getDraftOrderByTxnOrderId(session, shopId, txnOrderId);
      List<DraftOrder> draftOrders = (List<DraftOrder>) q.list();
      if (CollectionUtils.isNotEmpty(draftOrders)) {
        return draftOrders.get(0);
      }
      return null;
    } finally {
      release(session);
    }
  }

  public List<DraftOrderItem> getItemsByDraftOrderId(Long draftOrderId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getItemsByDraftOrderId(session, draftOrderId);
      return (List<DraftOrderItem>) q.list();
    } finally {
      release(session);
    }
  }

  public int countDraftOrders(Long shopId, Long userId, Long vehicleId, String[] orderTypes, Long startTime, Long endTime) {
    Session session = getSession();
    try {
      Query q = SQL.countDraftOrders(session, shopId, userId, vehicleId, orderTypes, startTime, endTime);
      return Integer.parseInt(q.uniqueResult().toString());
    } finally {
      release(session);
    }
  }

  public List countDraftOrderByOrderType(Long shopId, Long userId, String[] orderTypes, Long startTime, Long endTime) {
    Session session = this.getSession();
    try {
      Query q = SQL.countDraftOrderByOrderType(session, shopId, userId, orderTypes, startTime, endTime);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<DraftOrder> getDraftOrders(Long shopId, Long userId, Long vehicleId, Pager pager, String[] orderTypes, Long startTime, Long endTime) {
    Session session = this.getSession();
    try {
      Query q = SQL.getDraftOrders(session, shopId, userId, vehicleId, pager, orderTypes, startTime, endTime);
      return (List<DraftOrder>) q.list();
    } finally {
      release(session);
    }
  }


  public SupplierRecord getSupplierRecord(Long shopId, Long supplierId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getSupplierRecord(session, shopId, supplierId);
      List<SupplierRecord> list = q.list();
      if (CollectionUtils.isNotEmpty(list) && list.size() > 1) {
        LOG.error("SupplierRecord 表数据出错。shopId:{}, supplierId:{} 的数据有不止一条", shopId, supplierId);
      }
      return CollectionUtil.getFirst(list);
    } finally {
      release(session);
    }
  }

  public List<DepositDTO> getDepositForReindex(Long shopId, List<Long> ids) {
    Session session = this.getSession();
    try {
      Query query = SQL.getDepositForReindex(session, shopId, ids);
      List<Deposit> depositList = query.list();
      if (CollectionUtils.isEmpty(depositList)) return null;
      List<DepositDTO> depositDTOList = new ArrayList<DepositDTO>();
      for (Deposit deposit : depositList) {
        depositDTOList.add(deposit.toDTO());
      }
      return depositDTOList;
    } finally {
      release(session);
    }
  }

  public List<SupplierRecordDTO> getSupplierRecordForReindex(Long shopId, List<Long> supplierIds) {
    Session session = this.getSession();
    try {
      Query query = SQL.getSupplierRecordForReindex(session, shopId, supplierIds);
      List<SupplierRecord> supplierRecordList = query.list();
      if (CollectionUtils.isEmpty(supplierRecordList)) return null;
      List<SupplierRecordDTO> supplierRecordDTOList = new ArrayList<SupplierRecordDTO>();
      for (SupplierRecord supplierRecord : supplierRecordList) {
        supplierRecordDTOList.add(supplierRecord.toDTO());
      }
      return supplierRecordDTOList;
    } finally {
      release(session);
    }
  }

  public boolean deleteDraftOrderItemsByDraftOrderId(Long shopId, Long draftOrderId) {
    Session session = getSession();
    try {
      Query query = SQL.deleteDraftOrderItemsByDraftOrderId(session, shopId, draftOrderId);
      int count = query.executeUpdate();
      if (count > 0)
        return true;
      else
        return false;
    } finally {
      release(session);
    }
  }

  public List<Inventory> getInventoryByIds(Long shopId, Long... productIds) {
    long begin = System.currentTimeMillis();
    LOG.debug("AOP_SQL start:ProductWriter:getInventoryByIds");
    Session session = this.getSession();
    try {
      Query q = SQL.getInventoryByIds(session, shopId, productIds);
      return (List<Inventory>) q.list();
    } finally {
      LOG.debug("AOP_SQL end:ProductWriter:getInventoryByIds 用时：{}ms", System.currentTimeMillis() - begin);
      release(session);
    }
  }

  public List<BusinessStatChange> getBusinessStatChangeOfDay(Long shopId, long year, long month, long day) {
    Session session = this.getSession();
    try {
      Query hql = SQL.getBusinessStatChangeOfDay(session, shopId, year, month, day);
      return (List<BusinessStatChange>) hql.list();
    } finally {
      release(session);
    }
  }

  public BusinessStatDTO sumBusinessStatChangeOfStatSumForMonth(Long shopId, long year, long month) {
    BusinessStatDTO businessStatDTO = new BusinessStatDTO();
    Session session = this.getSession();
    try {
      Query q = sumBusinessStatChangeForMonth(session, shopId, year, month);
      Object[] o = (Object[]) q.uniqueResult();
      if (ArrayUtils.isEmpty(o) || o.length != 10) {
        return businessStatDTO;
      }
      businessStatDTO.setStatSum(NumberUtil.doubleVal((Double) o[0]));
      businessStatDTO.setSales(NumberUtil.doubleVal((Double) o[1]));
      businessStatDTO.setService(NumberUtil.doubleVal((Double) o[2]));
      businessStatDTO.setProductCost(NumberUtil.doubleVal((Double) o[3]));
      businessStatDTO.setOtherIncome(NumberUtil.doubleVal((Double) o[4]));
      businessStatDTO.setRentExpenditure(NumberUtil.doubleVal((Double) o[5]));
      businessStatDTO.setUtilitiesExpenditure(NumberUtil.doubleVal((Double) o[6]));
      businessStatDTO.setSalaryExpenditure(NumberUtil.doubleVal((Double) o[7]));
      businessStatDTO.setOtherExpenditure(NumberUtil.doubleVal((Double) o[8]));
      businessStatDTO.setWash(NumberUtil.doubleVal((Double) o[9]));
    } catch (Exception e) {
      LOG.error("TxnWriter.java method=sumBusinessStatChangeOfStatSumForMonth");
      LOG.error(e.getMessage(), e);
    } finally {
      release(session);
    }
    return businessStatDTO;
  }

  public BusinessStatDTO sumBusinessStatChangeOfStatSumForYear(Long shopId, long year) {
    BusinessStatDTO businessStatDTO = new BusinessStatDTO();
    Session session = this.getSession();
    try {
      Query q = sumBusinessStatChangeForYear(session, shopId, year);
      Object[] o = (Object[]) q.uniqueResult();
      if (ArrayUtils.isEmpty(o) || o.length != 10) {
        return businessStatDTO;
      }
      businessStatDTO.setStatSum(NumberUtil.doubleVal((Double) o[0]));
      businessStatDTO.setSales(NumberUtil.doubleVal((Double) o[1]));
      businessStatDTO.setService(NumberUtil.doubleVal((Double) o[2]));
      businessStatDTO.setProductCost(NumberUtil.doubleVal((Double) o[3]));
      businessStatDTO.setOtherIncome(NumberUtil.doubleVal((Double) o[4]));
      businessStatDTO.setRentExpenditure(NumberUtil.doubleVal((Double) o[5]));
      businessStatDTO.setUtilitiesExpenditure(NumberUtil.doubleVal((Double) o[6]));
      businessStatDTO.setSalaryExpenditure(NumberUtil.doubleVal((Double) o[7]));
      businessStatDTO.setOtherExpenditure(NumberUtil.doubleVal((Double) o[8]));
      businessStatDTO.setWash(NumberUtil.doubleVal((Double) o[9]));
    } catch (Exception e) {
      LOG.error("TxnWriter.java method=sumBusinessStatChangeOfStatSumForYear");
      LOG.error(e.getMessage(), e);
    } finally {
      release(session);
    }
    return businessStatDTO;
  }

  public List<BusinessStatChange> getDayBusinessStatChange(Long shopId, long year, long month) {
    Session session = this.getSession();
    try {
      Query hql = SQL.getDayBusinessStatChange(session, shopId, year, month);
      return (List<BusinessStatChange>) hql.list();
    } finally {
      release(session);
    }
  }

  public Map<Long, BusinessStatDTO> getMonthBusinessStatChangeMap(Long shopId, long year) {
    Map<Long, BusinessStatDTO> map = new HashMap<Long, BusinessStatDTO>();
    Session session = this.getSession();
    try {
      Query hql = getMonthBusinessStatChange(session, shopId, year);
      List<Object[]> list = (List<Object[]>) hql.list();
      BusinessStatDTO businessStat = null;
      if (CollectionUtils.isEmpty(list)) return map;
      for (Object[] o : list) {
        businessStat = new BusinessStatDTO();
        businessStat.setStatMonth((Long) o[0]);
        businessStat.setStatSum((Double) o[1]);
        businessStat.setShopId(shopId);
        map.put(businessStat.getStatMonth(), businessStat);
      }
      return map;
    } finally {
      release(session);
    }
  }

  public Map<String, BusinessStatDTO> getBusinessStatChangeMapByYearMonth(Long shopId, Long[] year, Long[] month) {
    Map<String, BusinessStatDTO> map = new HashMap<String, BusinessStatDTO>();
    Session session = this.getSession();
    try {
      Query hql = SQL.getBusinessStatChangeByYearMonth(session, shopId, year, month);
      List<Object[]> list = (List<Object[]>) hql.list();
      BusinessStatDTO businessStat = null;
      if (CollectionUtils.isEmpty(list)) return map;
      for (Object[] o : list) {
        businessStat = new BusinessStatDTO();
        businessStat.setStatYear((Long) o[0]);
        businessStat.setStatMonth((Long) o[1]);
        businessStat.setStatSum((Double) o[2]);
        businessStat.setShopId(shopId);
        map.put(String.valueOf(businessStat.getStatYear()) + String.valueOf(businessStat.getStatMonth()), businessStat);
      }
      return map;
    } finally {
      release(session);
    }
  }

  public Map<String, BusinessStatDTO> getBusinessStatChangeMapByYearMonthDay(Long shopId, Long[] year, Long[] month, Long[] day) {
    Map<String, BusinessStatDTO> map = new HashMap<String, BusinessStatDTO>();
    Session session = this.getSession();
    try {
      Query hql = SQL.getBusinessStatChangeByYearMonthDay(session, shopId, year, month, day);
      List<Object[]> list = (List<Object[]>) hql.list();
      BusinessStatDTO businessStat = null;
      if (CollectionUtils.isEmpty(list)) return map;
      for (Object[] o : list) {
        businessStat = new BusinessStatDTO();
        businessStat.setStatYear((Long) o[0]);
        businessStat.setStatMonth((Long) o[1]);
        businessStat.setStatDay((Long) o[2]);
        businessStat.setStatSum((Double) o[3]);
        businessStat.setShopId(shopId);
        map.put(String.valueOf(businessStat.getStatYear()) + String.valueOf(businessStat.getStatMonth()) + String.valueOf(businessStat.getStatDay()), businessStat);
      }
      return map;
    } finally {
      release(session);
    }
  }

  public Map<String, BusinessStatDTO> getBusinessStatMapByYearMonth(Long shopId, String... yearMonth) {
    Map<String, BusinessStatDTO> map = new HashMap<String, BusinessStatDTO>();
    Session session = this.getSession();
    try {
      Query hql = SQL.getBusinessStatMapByYearMonth(session, shopId, yearMonth);
      List<Object[]> list = (List<Object[]>) hql.list();
      BusinessStatDTO businessStatDTO = null;
      if (CollectionUtils.isEmpty(list)) return map;
      for (Object[] o : list) {
        businessStatDTO = new BusinessStatDTO();
        businessStatDTO.setStatYear((Long) o[0]);
        businessStatDTO.setStatMonth((Long) o[1]);
        businessStatDTO.setStatSum((Double) o[2]);
        businessStatDTO.setShopId(shopId);
        map.put(String.valueOf(businessStatDTO.getStatYear()) + String.valueOf(businessStatDTO.getStatMonth() < 10 ? ("0" + businessStatDTO.getStatMonth()) : businessStatDTO.getStatMonth()), businessStatDTO);
      }
      return map;
    } finally {
      release(session);
    }
  }

  public Map<String, BusinessStatDTO> getBusinessStatMapByYearMonthDay(Long shopId, String yearMonthDayStart, String yearMonthDayEnd) {
    Map<String, BusinessStatDTO> map = new HashMap<String, BusinessStatDTO>();
    Session session = this.getSession();
    try {
      Query hql = SQL.getBusinessStatByYearMonthDay(session, shopId, yearMonthDayStart, yearMonthDayEnd);
      List<Object[]> list = (List<Object[]>) hql.list();
      BusinessStatDTO businessStatDTO = null;
      if (CollectionUtils.isEmpty(list)) return map;
      for (Object[] o : list) {
        businessStatDTO = new BusinessStatDTO();
        businessStatDTO.setStatYear((Long) o[0]);
        businessStatDTO.setStatMonth((Long) o[1]);
        businessStatDTO.setStatDay((Long) o[2]);
        businessStatDTO.setStatSum((Double) o[3]);
        businessStatDTO.setShopId(shopId);
        map.put(String.valueOf(businessStatDTO.getStatYear()) + String.valueOf(businessStatDTO.getStatMonth() < 10 ? ("0" + businessStatDTO.getStatMonth()) : businessStatDTO.getStatMonth()) + String.valueOf(businessStatDTO.getStatDay() < 10 ? ("0" + businessStatDTO.getStatDay()) : businessStatDTO.getStatDay()), businessStatDTO);
      }
      return map;
    } finally {
      release(session);
    }
  }

  public List<ReceptionRecord> getReceptionByShopIdAndPager(long shopId, Pager pager) {
    Session session = this.getSession();
    try {
      Query hql = SQL.getReceptionByShopIdAndPager(session, shopId, pager);
      List list = hql.list();
      if (CollectionUtils.isEmpty(list)) {
        return null;
      }
      return (List<ReceptionRecord>) list;
    } finally {
      release(session);
    }
  }

  public List<RunningStat> getRunningStatDTOByShopIdYearMonthDay(long shopId, long statYear, long statMonth, long statDay) {
    Session session = this.getSession();

    try {
      Query q = SQL.getRunningStatDTOByShopIdYearMonthDay(session, shopId, statYear, statMonth, statDay);
      List list = q.list();
      if (CollectionUtils.isEmpty(list)) {
        return null;
      }

      return (List<RunningStat>) list;
    } finally {
      release(session);
    }
  }

  public RunningStat getLastRunningStatDTOByShopId(long shopId, Long statYear) {
    Session session = this.getSession();

    try {
      Query q = SQL.getLastRunningStatDTOByShopId(session, shopId, statYear);
      List list = q.list();
      if (CollectionUtils.isEmpty(list)) {
        return null;
      }

      return (RunningStat) list.get(0);
    } finally {
      release(session);
    }

  }

  public int countRepealOrderByRepealDate(long shopId, long startTime, long endTime) throws Exception {
    Session session = this.getSession();
    try {
      Query q = SQL.countRepealOrderByRepealDate(session, shopId, startTime, endTime);
      return Integer.parseInt(q.uniqueResult().toString());
    } finally {
      release(session);
    }
  }


  public List<RepealOrder> getRepealOrderListByRepealDate(long shopId, long startTime, long endTime, Pager pager) {
    Session session = this.getSession();

    try {
      Query q = getRepealOrderListByPager(session, shopId, startTime, endTime, pager);
      List list = q.list();
      if (CollectionUtils.isEmpty(list)) {
        return null;
      }

      return (List<RepealOrder>) list;
    } finally {
      release(session);
    }
  }

  public List<RunningStat> getRunningStatByYearMonthDay(long shopId, Integer year, Integer month, Integer day, Integer resultSize, Sort sort) {
    Session session = this.getSession();
    try {
      Query q = SQL.getRunningStatByYearMonthDay(session, shopId, year, month, day, resultSize, sort);
      List list = q.list();
      if (CollectionUtils.isEmpty(list)) {
        return null;
      }
      return (List<RunningStat>) list;
    } finally {
      release(session);
    }
  }


  public int countPurchaseInventoryOrderByCreated(long shopId, long startTime, long endTime) {
    Session session = this.getSession();
    try {
      Query hql = SQL.countPurchaseInventoryOrderByCreated(session, shopId, startTime, endTime);
      return Integer.parseInt(hql.uniqueResult().toString());

    } finally {
      release(session);
    }
  }

  public List<PurchaseInventory> getInventoryOrderListByPager(long shopId, long startTime, long endTime, Pager pager) {
    Session session = getSession();
    try {
      Query q = SQL.getInventoryOrderListByPager(session, shopId, startTime, endTime, pager);
      List result = q.list();
      if (CollectionUtils.isNotEmpty(result)) {
        return (List<PurchaseInventory>) result;
      }
      return null;
    } finally {
      release(session);
    }
  }

  public int countPurchaseReturnOrderByCreated(long shopId, long startTime, long endTime) {
    Session session = this.getSession();
    try {
      Query hql = SQL.countPurchaseReturnOrderByCreated(session, shopId, startTime, endTime);
      return Integer.parseInt(hql.uniqueResult().toString());

    } finally {
      release(session);
    }
  }

  public List<PurchaseReturn> getPurchaseReturnOrderListByPager(long shopId, long startTime, long endTime, Pager pager) {
    Session session = getSession();
    try {
      Query q = SQL.getPurchaseReturnOrderListByPager(session, shopId, startTime, endTime, pager);
      List result = q.list();
      if (CollectionUtils.isNotEmpty(result)) {
        return (List<PurchaseReturn>) result;
      }
      return null;
    } finally {
      release(session);
    }
  }

  public List<RunningStat> getRunningStatMonth(long shopId, long year, String queryString) {
    Session session = this.getSession();
    try {
      Query hql = SQL.getRunningStatMonth(session, shopId, year, queryString);
      return (List<RunningStat>) hql.list();
    } finally {
      release(session);
    }
  }

  public List<ReceptionRecord> getReceptionRecordByReceptionDate(long shopId, long startTime, long endTime, Pager pager) {
    Session session = this.getSession();
    try {
      Query q = SQL.getReceptionRecordByReceptionDate(session, shopId, startTime, endTime, pager);
      return (List<ReceptionRecord>) q.list();
    } finally {
      release(session);
    }
  }


  public List<String> countReceptionRecordByReceptionDate(long shopId, long startTime, long endTime) {

    Session session = this.getSession();
    List<String> stringList = new ArrayList<String>();
    try {
      Query q = SQL.countReceptionRecordByReceptionDate(session, shopId, startTime, endTime);
      if (q == null) {
        return null;
      }
      List<Object> list = q.list();
      if (CollectionUtils.isEmpty(list)) {
        return null;
      }
      Object[] array = (Object[]) list.get(0);
      if (array[0] != null && array[1] != null) {
        stringList.add(array[0].toString());
        stringList.add(array[1].toString());
      }
      return stringList;
    } finally {
      release(session);
    }
  }


  public List<ReceptionRecord> getReceptionRecordByOrderId(long shopId, long orderId, OrderTypes orderTypes) {
    Session session = this.getSession();
    try {
      Query q = SQL.getReceptionRecordByOrderId(session, shopId, orderId, orderTypes);
      return (List<ReceptionRecord>) q.list();
    } finally {
      release(session);
    }
  }

  /**
   * 根据shopiD,供应商ID，入库单ID 查找对应的付款历史记录
   *
   * @param shopId
   * @param supplierId
   * @param purchaseInventoryId 入库单ID
   * @return
   */
  public List<PayableHistoryRecord> getPayHistoryRecordListByIds(Long shopId, Long supplierId, Long purchaseInventoryId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getPayHistoryRecordForRepeal(session, shopId, supplierId, purchaseInventoryId);
      List list = q.list();
      if (CollectionUtils.isEmpty(list)) {
        return null;
      }
      return (List<PayableHistoryRecord>) list;
    } finally {
      release(session);
    }
  }


  public int countPayHistoryRecordByPayTime(long shopId, long startTime, long endTime) {
    Session session = this.getSession();
    try {
      Query hql = SQL.countPayHistoryRecordByPayTime(session, shopId, startTime, endTime);
      return Integer.parseInt(hql.uniqueResult().toString());

    } finally {
      release(session);
    }
  }

  public List<PayableHistoryRecord> getPayHistoryRecordByPayTime(long shopId, long startTime, long endTime, Pager pager) {
    Session session = this.getSession();
    try {
      Query q = SQL.getPayHistoryRecordByPayTime(session, shopId, startTime, endTime, pager);
      return (List<PayableHistoryRecord>) q.list();
    } finally {
      release(session);
    }
  }

  public List<Deposit> getDepositDTOListBySHopId(long shopId, long startTime, long endTime) {
    Session session = this.getSession();
    try {
      Query hql = SQL.getDepositDTOListBySHopId(session, shopId, startTime, endTime);
      return (List<Deposit>) hql.list();

    } finally {
      release(session);
    }
  }

  public double sumDebtByIds(List<Long> ids, Long shopId) {
    Session session = this.getSession();
    try {
      Query q = SQL.sumDebtByIds(session, shopId, ids);
      Object o = q.uniqueResult();
      if (o == null) {
        return 0d;
      }
      return (Double) q.uniqueResult();
    } finally {
      release(session);
    }
  }

  public List<SupplierReturnPayable> getSupplierReturnPayableByPurchaseReturnId(Long shopId, Long... purchaseReturnId) {
    Session session = this.getSession();
    try {
      Query query = SQL.getSupplierReturnPayableByPurchaseReturnId(session, shopId, purchaseReturnId);
      return query.list();
    } finally {
      release(session);
    }
  }

  public List<SupplierReturnPayable> getAllSupplierReturnPayableBySupplierIds(Long shopId, Long[] supplierIds) {
    Session session = this.getSession();
    try {
      Query query = SQL.getAllSupplierReturnPayableBySupplierIds(session, shopId, supplierIds);
      return query.list();
    } finally {
      release(session);
    }
  }

  public List<Payable> getPayableByPurchaseInventoryId(Long shopId, Long... purchaseReturnId) {
    Session session = this.getSession();
    try {
      Query query = SQL.getPayableByPurchaseInventoryId(session, shopId, purchaseReturnId);
      return query.list();
    } finally {
      release(session);
    }
  }

  public String getLastOrderReceiptNo(Long shopId, OrderTypes types, String receiptNoNotNo) {
    Session session = getSession();

    try {
      Query q = SQL.getLastOrderReceiptNo(session, shopId, types, receiptNoNotNo);

      List<String> receiptNolList = (List<String>) q.list();

      if (CollectionUtils.isNotEmpty(receiptNolList)) {
        return receiptNolList.get(0);
      }

      return null;

    } finally {
      release(session);
    }
  }

  public List getOrderDTONoReceiptNo(OrderTypes types, int num, int pageNo) {
    Session session = getSession();

    try {
      Query q = SQL.getOrderDTONoReceiptNo(session, types, num, pageNo);

      return q.list();
    } finally {
      release(session);
    }
  }

  public Long getTxnOrderIdByReceiptNo(ReceiptNoDTO receiptNoDTO) {
    Session session = getSession();
    try {
      Query q = SQL.getTxnOrderIdByReceiptNo(session, receiptNoDTO);
      if (null == q) {
        return null;
      }
      return NumberUtil.longValue(q.uniqueResult().toString());
    } finally {
      release(session);
    }
  }

  public int countOrderNoReceiptNo(Long shopId, OrderTypes types) {
    Session session = getSession();

    try {
      Query q = SQL.countOrderNoReceiptNo(session, shopId, types);
      return Integer.parseInt(q.uniqueResult().toString());
    } finally {
      release(session);
    }
  }

  public ReceiptNo getReceiptNOByShopIdAndType(Long shopId, OrderTypes types) {
    Session session = getSession();

    try {
      Query q = SQL.getReceiptNOByShopIdAndType(session, shopId, types);

      List<ReceiptNo> receiptNoList = (List<ReceiptNo>) q.list();

      if (CollectionUtils.isEmpty(receiptNoList)) {
        return null;
      }
      return receiptNoList.get(0);
    } finally {
      release(session);
    }
  }


  public List<RunningStatChange> getRunningStatChangeDTOByShopIdYearMonthDay(Long shopId, long year, long month, long day) {
    Session session = this.getSession();
    try {
      Query hql = SQL.getRunningStatChangeDTOByShopIdYearMonthDay(session, shopId, year, month, day);
      return (List<RunningStatChange>) hql.list();
    } finally {
      release(session);
    }
  }

  public List<RunningStatChange> getRunningStatChangeByYearMonth(Long shopId, Long year, Long month) {
    Session session = getSession();
    try {
      Query query = SQL.getRunningStatChangeByYearMonth(session, shopId, year, month);
      return query.list();
    } finally {
      release(session);
    }
  }


  public List<RunningStatChange> sumRunningStatChangeForYearMonth(Long shopId, Long year, Long month) {
    RunningStatDTO runningStatDTO = new RunningStatDTO();
    Session session = this.getSession();
    try {
      Query q = SQL.sumRunningStatChangeForYearMonth(session, shopId, year, month);
      return q.list();
    } finally {
      release(session);
    }
  }


  public Map<Long, RunningStatDTO> getMonthRunningStatChangeMap(Long shopId, long year) {
    Map<Long, RunningStatDTO> map = new HashMap<Long, RunningStatDTO>();
    Session session = this.getSession();
    try {
      Query hql = SQL.getMonthRunningStatChangeMap(session, shopId, year);
      List<Object[]> list = (List<Object[]>) hql.list();
      RunningStatDTO runningStatDTO = null;
      if (CollectionUtils.isEmpty(list)) return map;
      for (Object[] o : list) {
        runningStatDTO = new RunningStatDTO();
        runningStatDTO.setStatMonth((Long) o[0]);
        runningStatDTO.setCashIncome((Double) o[1]);
        runningStatDTO.setChequeIncome((Double) o[2]);
        runningStatDTO.setUnionPayIncome((Double) o[3]);
        runningStatDTO.setCashExpenditure((Double) o[4]);
        runningStatDTO.setChequeExpenditure((Double) o[5]);
        runningStatDTO.setUnionPayExpenditure((Double) o[6]);

        runningStatDTO.setMemberPayIncome((Double) o[7]);
        runningStatDTO.setDebtNewIncome((Double) o[8]);
        runningStatDTO.setDebtWithdrawalIncome((Double) o[9]);
        runningStatDTO.setDepositPayIncome((Double) o[10]);
        runningStatDTO.setCustomerDepositExpenditure((Double) o[11]);
        runningStatDTO.setDebtNewExpenditure((Double) o[12]);
        runningStatDTO.setDebtWithdrawalExpenditure((Double) o[13]);
        runningStatDTO.setDepositPayExpenditure((Double) o[14]);

        runningStatDTO.setCouponIncome((Double) o[15]);
        runningStatDTO.setCouponExpenditure((Double) o[16]);

        runningStatDTO.setShopId(shopId);
        map.put(runningStatDTO.getStatMonth(), runningStatDTO);
      }
      return map;
    } finally {
      release(session);
    }
  }

  public RepairOrderTemplate getRepairOrderTemplateByTemplateNameAndStatus(Long shopId, String templateName, RepairOrderTemplateStatus status) {
    Session session = this.getSession();
    RepairOrderTemplate repairOrderTemplate = null;
    try {
      Query query = getRepairOrderTemplateByShopIdAndTemplateNameAndStatus(session, shopId, templateName, status);
      List repairOrderTemplateList = query.list();

      if (!repairOrderTemplateList.isEmpty()) {
        repairOrderTemplate = (RepairOrderTemplate) repairOrderTemplateList.get(0);

      }
    } finally {
      release(session);
    }
    return repairOrderTemplate;
  }


  /**
   * 根据施工单模板ID获取施工单模板施工内容
   *
   * @param repairOrderTemplateId
   * @return
   */
  public List<RepairOrderTemplateService> getRepairOrderTemplateServicesByRepairOrderTemplateId(Long repairOrderTemplateId) {

    Session session = getSession();
    try {
      Query q = SQL.getRepairOrderTemplateServicesByRepairOrderTemplateId(session, repairOrderTemplateId);
      return (List<RepairOrderTemplateService>) q.list();
    } finally {
      release(session);
    }
  }

  /**
   * 根据施工单模板ID获取施工单模板施工材料
   *
   * @param repairOrderTemplateId
   * @return
   */
  public List<RepairOrderTemplateItem> getRepairOrderTemplateItemsByRepairOrderTemplateId(Long repairOrderTemplateId) {

    Session session = getSession();
    try {
      Query q = SQL.getRepairOrderTemplateItemsByRepairOrderTemplateId(session, repairOrderTemplateId);
      return (List<RepairOrderTemplateItem>) q.list();
    } finally {
      release(session);
    }
  }


  public List<RepairOrderTemplate> getRepairOrderTemplateByShopId(Long shopId, RepairOrderTemplateStatus status) {
    Session session = this.getSession();
    List<RepairOrderTemplate> repairOrderTemplateList = null;
    try {
      Query query = getRepairOrderTemplateByShopIdAndStatus(session, shopId, status);
      repairOrderTemplateList = query.list();
    } finally {
      release(session);
    }
    return repairOrderTemplateList;
  }


  public List<RepairOrderTemplate> getTop5RepairOrderTemplateByShopId(Long shopId) {
    Session session = this.getSession();
    List<RepairOrderTemplate> repairOrderTemplateList = null;
    try {
      Query query = SQL.getTop5RepairOrderTemplateByShopId(session, shopId);
      repairOrderTemplateList = query.list();
    } finally {
      release(session);
    }
    return repairOrderTemplateList;
  }


  public BizStatPrintDTO getBusinessChangeInfoToPrint(Long shopId, Long startTime, Long endTime) {
    Session session = this.getSession();

    try {
      BizStatPrintDTO bizStatPrintDTO = new BizStatPrintDTO();

      Query q = SQL.getBusinessChangeInfoToPrint(session, shopId, startTime, endTime);

      List<?> list = q.list();

      int index = 0;

      if (CollectionUtils.isNotEmpty(list)) {
        Object[] array = (Object[]) list.get(0);

        double otherIncome = (null == (Double) array[0] ? 0d : (Double) array[0]);
        double rent = (null == (Double) array[1] ? 0d : (Double) array[1]);
        double labor = (null == (Double) array[2] ? 0d : (Double) array[2]);
        double other = (null == (Double) array[3] ? 0d : (Double) array[3]);
        double otherFee = (null == (Double) array[4] ? 0d : (Double) array[4]);
        double sales = (null == (Double) array[5] ? 0d : (Double) array[5]);
        double wash = (null == (Double) array[6] ? 0d : (Double) array[6]);
        double service = (null == (Double) array[7] ? 0d : (Double) array[7]);
        double productCost = (null == (Double) array[8] ? 0d : (Double) array[8]);
        double statSum = (null == (Double) array[9] ? 0d : (Double) array[9]);
        bizStatPrintDTO.setOtherIncome(otherIncome);
        bizStatPrintDTO.setRent(rent);
        bizStatPrintDTO.setLabor(labor);
        bizStatPrintDTO.setOther(other);
        bizStatPrintDTO.setOtherFee(otherFee);
        bizStatPrintDTO.setSales(sales);
        bizStatPrintDTO.setWash(wash);
        bizStatPrintDTO.setService(service);
        bizStatPrintDTO.setProductCost(productCost);
        bizStatPrintDTO.setStatSum(statSum);
      } else {
        bizStatPrintDTO.setOtherIncome(0);
        bizStatPrintDTO.setRent(0);
        bizStatPrintDTO.setLabor(0);
        bizStatPrintDTO.setOther(0);
        bizStatPrintDTO.setOtherFee(0);
        bizStatPrintDTO.setSales(0);
        bizStatPrintDTO.setWash(0);
        bizStatPrintDTO.setService(0);
        bizStatPrintDTO.setProductCost(0);
        bizStatPrintDTO.setStatSum(0);
      }
      return bizStatPrintDTO;
    } finally {
      release(session);
    }
  }


  public int countRepairOrderOfNotSettled(Long shopId, Long customerId) {
    Session session = this.getSession();

    try {
      Query query = SQL.countRepairOrderOfNotSettled(session, shopId, customerId);

      if (null == query) {
        return 0;
      }

      return Integer.parseInt(query.uniqueResult().toString());

    } finally {
      release(session);
    }
  }

  public List<DraftOrder> getDraftOrder(Long shopId, List<OrderTypes> orderTypesList, Long customerId) {
    Session session = this.getSession();

    try {
      Query q = SQL.getDraftOrder(session, shopId, orderTypesList, customerId);

      return q.list();
    } finally {
      release(session);
    }
  }

  public List<RepairOrder> getRepairOrderReceiptNoOfNotSettled(Long shopId, Long customerId) {
    Session session = this.getSession();

    try {
      Query q = SQL.getRepairOrderReceiptNoOfNotSettled(session, shopId, customerId);

      return q.list();

    } finally {
      release(session);
    }
  }

  public Map<Long, CategoryDTO> getServiceCategoryMapByServiceId(Long shopId, Long... serviceId) {
    Session session = this.getSession();
    Map<Long, CategoryDTO> map = new HashMap<Long, CategoryDTO>();

    try {
      Query q = SQL.getCategoryByServiceId(session, shopId, serviceId);
      List<Object[]> list = (List<Object[]>) q.list();
      if (CollectionUtils.isEmpty(list)) return map;
      Long sId = null;
      Category category = null;
      for (Object[] o : list) {
        sId = (Long) o[0];
        category = (Category) o[1];
        if (category != null) {
          map.put(sId, category.toDTO());
        }
      }
      return map;
    } finally {
      release(session);
    }
  }

  public List<Service> getServiceCategory(Long shopId, CategoryType categoryType, Long pageNo, Long pageSize) {
    Session session = this.getSession();
    try {
      Query q = SQL.getServiceCategory(session, shopId, categoryType, pageNo, pageSize);
      return (List<Service>) q.list();
    } finally {
      release(session);
    }
  }

  public int countServiceCategory(Long shopId, CategoryType categoryType) {
    Session session = this.getSession();
    try {
      Query q = SQL.countServiceCategory(session, shopId, categoryType);
      return Integer.parseInt(q.uniqueResult().toString());
    } finally {
      release(session);
    }
  }

  public List<Service> getServiceHasCategory(Long shopId, String serviceName, String categoryName, Long pageNo, Long pageSize) {
    Session session = this.getSession();
    try {
      Query q = SQL.getServiceHasCategory(session, shopId, serviceName, categoryName, pageNo, pageSize);
      return (List<Service>) q.list();
    } finally {
      release(session);
    }
  }

  public int countServiceHasCategory(Long shopId, String serviceName, String categoryName) {
    Session session = this.getSession();
    try {
      Query q = SQL.countServiceHasCategory(session, shopId, serviceName, categoryName);
      return ((Long) q.uniqueResult()).intValue();
    } finally {
      release(session);
    }
  }


  public int countUndoneRepairOrderByVehicleId(Long shopId, Long vehicleId) {
    Session session = this.getSession();
    try {
      Query q = SQL.countUndoneRepairOrderByVehicleId(session, shopId, vehicleId);
      return Integer.parseInt(q.uniqueResult().toString());
    } finally {
      release(session);
    }
  }

  public MemberCardOrder getLatestMemberCardOrder(Long shopId, Long customerId) {
    Session session = getSession();
    try {
      Query q = SQL.getLatestMemberCardOrder(session, shopId, customerId);
      List<MemberCardOrder> memberCardOrders = q.list();
      if (CollectionUtils.isNotEmpty(memberCardOrders)) {
        return memberCardOrders.get(0);
      }
      return null;
    } finally {
      release(session);
    }
  }

  public MemberCardReturn getMemberCardReturnById(Long shopId, Long id) {
    Session session = getSession();
    try {
      Query q = SQL.getMemberCardReturnById(session, shopId, id);
      List<MemberCardReturn> memberCardReturns = q.list();
      if (CollectionUtils.isNotEmpty(memberCardReturns)) {
        return memberCardReturns.get(0);
      }
      return null;
    } finally {
      release(session);
    }
  }

  public List<MemberCardReturnItem> getMemberCardReturnItemByOrderId(Long shopId, Long orderId) {
    Session session = getSession();
    try {
      Query q = SQL.getMemberCardReturnItemByOrderId(session, shopId, orderId);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<MemberCardReturnService> getMemberCardReturnServiceByOrderId(Long shopId, Long orderId) {
    Session session = getSession();
    try {
      Query q = SQL.getMemberCardReturnServiceByOrderId(session, shopId, orderId);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<RepairOrderDTO> getUnsettledRepairOrderDTOsByProductId(Long shopId, Long productId) {
    Session session = getSession();
    try {
      Query query = SQL.getUnsettledRepairOrderDTOsByProductId(session, shopId, productId);
      List<RepairOrder> repairOrders = query.list();
      List<RepairOrderDTO> repairOrderDTOs = new ArrayList<RepairOrderDTO>();
      if (CollectionUtils.isNotEmpty(repairOrders)) {
        for (RepairOrder repairOrder : repairOrders) {
          repairOrderDTOs.add(repairOrder.toDTO());
        }
      }
      return repairOrderDTOs;
    } finally {
      release(session);
    }
  }

  public List<PurchaseOrderDTO> getUnsettledPurchaseOrderDTOsByProductId(Long shopId, Long productId) {
    Session session = getSession();
    try {
      Query query = SQL.getUnsettledPurchaseOrderDTOsByProductId(session, shopId, productId);
      List<PurchaseOrder> purchaseOrders = query.list();
      List<PurchaseOrderDTO> purchaseOrderDTOs = new ArrayList<PurchaseOrderDTO>();
      if (CollectionUtils.isNotEmpty(purchaseOrders)) {
        for (PurchaseOrder purchaseOrder : purchaseOrders) {
          purchaseOrderDTOs.add(purchaseOrder.toDTO());
        }
      }
      return purchaseOrderDTOs;
    } finally {
      release(session);
    }
  }

  public List<SalesOrderDTO> getUnsettledSalesOrderDTOsByProductId(Long shopId, Long productId) {
    Session session = getSession();
    try {
      Query query = SQL.getUnsettledSalesOrderDTOsByProductId(session, shopId, productId);
      List<SalesOrder> salesOrders = query.list();
      List<SalesOrderDTO> salesOrderDTOs = new ArrayList<SalesOrderDTO>();
      if (CollectionUtils.isNotEmpty(salesOrders)) {
        for (SalesOrder salesOrder : salesOrders) {
          salesOrderDTOs.add(salesOrder.toDTO());
        }
      }
      return salesOrderDTOs;
    } finally {
      release(session);
    }
  }

  public List<SalesOrder> getUnsettledSalesOrdersByCustomerId(Long shopId, Long customerId) {
    List<SalesOrder> salesOrders = new ArrayList<SalesOrder>();
    if (shopId == null || customerId == null) {
      return salesOrders;
    }
    Session session = getSession();
    try {
      Query query = SQL.getUnsettledSalesOrdersByCustomerId(session, shopId, customerId);
      return query.list();
    } finally {
      release(session);
    }
  }

  public List<PurchaseReturnDTO> getUnsettledReturnOrderByProductId(Long shopId, Long productId) {
    Session session = getSession();
    try {
      Query query = SQL.getUnsettledReturnOrderByProductId(session, shopId, productId);
      List<PurchaseReturn> purchaseReturns = query.list();
      List<PurchaseReturnDTO> purchaseReturnDTOs = new ArrayList<PurchaseReturnDTO>();
      if (CollectionUtils.isNotEmpty(purchaseReturns)) {
        for (PurchaseReturn purchaseReturn : purchaseReturns) {
          purchaseReturnDTOs.add(purchaseReturn.toDTO());
        }
      }
      return purchaseReturnDTOs;
    } finally {
      release(session);
    }
  }

  public List<SalesReturnDTO> getUnsettledSalesReturnByProductId(Long shopId, Long productId) {
    Session session = getSession();
    try {
      Query query = SQL.getUnsettledSalesReturnByProductId(session, shopId, productId);
      List<SalesReturn> salesReturns = query.list();
      List<SalesReturnDTO> salesReturnDTOs = new ArrayList<SalesReturnDTO>();
      if (CollectionUtils.isNotEmpty(salesReturns)) {
        for (SalesReturn salesReturn : salesReturns) {
          salesReturnDTOs.add(salesReturn.toDTO());
        }
      }
      return salesReturnDTOs;
    } finally {
      release(session);
    }
  }

  public List<SalesReturn> getUnsettledSalesReturnByCustomerId(Long shopId, Long customerId) {
    if (shopId == null || customerId == null) {
      return new ArrayList<SalesReturn>();
    }
    Session session = getSession();
    try {
      Query query = SQL.getUnsettledSalesReturnByCustomerId(session, shopId, customerId);
      return query.list();
    } finally {
      release(session);
    }
  }

  public List<Service> getServiceByIds(List<Long> idList) {
    Session session = this.getSession();
    try {
      Query q = SQL.getServiceByIds(session, idList);
      return (List<Service>) q.list();
    } finally {
      release(session);
    }
  }

  /**
   * 查询应付款记录
   *
   * @param shopId
   * @param supplierId 供应商ID
   * @return
   */
  public List<Payable> searchPayable(Long shopId, Long supplierId) {
    Session session = this.getSession();
    try {
      Query q = SQL.searchPayable(session, shopId, supplierId);
      return q.list();
    } finally {
      release(session);
    }
  }

  public Double getMemberCardConsumeTotal(Long shopId, Long memberId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getMemberCardConsumeTotal(session, shopId, memberId);
      return (Double) q.uniqueResult();
    } finally {
      release(session);
    }
  }

  public List<PayableHistoryRecord> getPayableHistoryRecordByPaymentType(PaymentTypes paymentTypes, Long shopId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getPayableHistoryRecordByPaymentType(session, paymentTypes, shopId);
      return (List<PayableHistoryRecord>) q.list();
    } finally {
      release(session);
    }
  }

  public List<PayableHistoryRecord> getPayHistoryRecordByPayTime(long shopId, long startTime, long endTime) {
    Session session = this.getSession();
    try {
      Query hql = SQL.getPayHistoryRecordByPayTime(session, shopId, startTime, endTime);
      return (List<PayableHistoryRecord>) hql.list();

    } finally {
      release(session);
    }
  }

  public List<ReceptionRecord> getReceptionRecordByReceptionDate(long shopId, long startTime, long endTime) {
    Session session = this.getSession();
    try {
      Query hql = SQL.getReceptionRecordByReceptionDate(session, shopId, startTime, endTime);
      return (List<ReceptionRecord>) hql.list();

    } finally {
      release(session);
    }
  }


  public List<PayableHistoryRecord> getPayableHistoryRecordListByPurchaseReturnId(Long shopId, Long purchaseReturnId, PaymentTypes paymentType) {
    Session session = this.getSession();
    try {
      Query hql = SQL.getPayableHistoryRecordListByPurchaseReturnId(session, shopId, purchaseReturnId, paymentType);
      return (List<PayableHistoryRecord>) hql.list();

    } finally {
      release(session);
    }
  }

  public List<MemberCardReturn> getMemberReturnListByReturnDate(long shopId, long startTime, long endTime) {
    Session session = getSession();
    try {
      Query q = SQL.getMemberReturnListByReturnDate(session, shopId, startTime, endTime);
      List<MemberCardReturn> memberCardReturnList = q.list();
      return memberCardReturnList;
    } finally {
      release(session);
    }
  }

  public LoanTransfersDTO getLoanTransfersByTransfersNumber(String transfersNumber) {
    Session session = this.getSession();
    try {
      Query hql = SQL.getLoanTransfersByTransfersNumber(session, transfersNumber);
      LoanTransfers loanTransfers = (LoanTransfers) hql.uniqueResult();
      if (loanTransfers == null) return null;
      return loanTransfers.toDTO();
    } finally {
      release(session);
    }
  }

  public List<LoanTransfers> getLoanTransfersByShopId(Long shopId, Pager pager) {
    Session session = this.getSession();
    try {
      Query hql = SQL.getLoanTransfersByShopId(session, shopId, pager);
      return hql.list();
    } finally {
      release(session);
    }
  }

  public int countLoanTransfersByShopId(Long shopId) {
    Session session = this.getSession();
    try {
      Query hql = SQL.countLoanTransfersByShopId(session, shopId);
      Object o = hql.uniqueResult();
      if (o == null) return 0;
      return Integer.valueOf(o.toString());
    } finally {
      release(session);
    }
  }

  public Double sumLoanTransfersTotalAmountByShopId(Long shopId) {
    Session session = this.getSession();
    try {
      Query hql = SQL.sumLoanTransfersTotalAmountByShopId(session, shopId);
      Object o = hql.uniqueResult();
      if (o == null) return 0d;
      return NumberUtils.toDouble(o.toString(), 0d);
    } finally {
      release(session);
    }
  }

  public List<Long> getLoanTransfersIdsByStatus(Long shopId, int start, int pageSize, Long loanTransferTime) {
    Session session = this.getSession();
    try {
      Query hql = SQL.getLoanTransfersIdsByStatus(session, shopId, start, pageSize, loanTransferTime);
      return hql.list();
    } finally {
      release(session);
    }
  }

  public List<LoanTransfers> getLoanTransfersIdsById(Long... ids) {
    Session session = this.getSession();
    try {
      return SQL.getLoanTransfersIdsById(session, ids).list();
    } finally {
      release(session);
    }
  }

  public List<MemberCardReturn> getMemberCardReturnOrdersByIds(Long... orderIds) {
    Session session = this.getSession();
    try {
      return SQL.getMemberCardReturnOrdersByIds(session, orderIds).list();
    } finally {
      release(session);
    }
  }

  public List<MemberCardReturnItem> getMemberCardReturnItemsByIds(Long... orderIds) {
    Session session = this.getSession();
    try {
      return SQL.getMemberCardReturnItemsByIds(session, orderIds).list();
    } finally {
      release(session);
    }
  }

  public List<ReceptionRecord> getReceptionRecordsByIds(Long... orderIds) {
    Session session = this.getSession();
    try {
      return SQL.getReceptionRecordsByIds(session, orderIds).list();
    } finally {
      release(session);
    }
  }

  public List<MemberCardReturnService> getMemberCardReturnServicesByIds(Long... orderIds) {
    Session session = this.getSession();
    try {
      return SQL.getMemberCardReturnServicesByIds(session, orderIds).list();
    } finally {
      release(session);
    }
  }

  public List<PurchaseReturn> getPurchaseReturn(Long shopId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getPurchaseReturn(session, shopId);
      return (List<PurchaseReturn>) q.list();
    } finally {
      release(session);
    }
  }

  public void deletePayHistoryRecord(Long shopId) {
    Session session = this.getSession();
    try {
      Query q = SQL.deletePayHistoryRecord(session, shopId);
      q.executeUpdate();
    } finally {
      release(session);
    }
  }

  public double getTotalDebtByShopId(long shopId, OrderDebtType type) {
    Session session = this.getSession();
    try {
      Query q = SQL.getTotalDebtByShopId(session, shopId, type);
      Double sum = (Double) q.uniqueResult();
      if (sum == null) {
        return 0.0;
      }
      return sum.doubleValue();
    } finally {
      release(session);
    }
  }


  public List<Long> getDebtOrReceivableErrorRepairOrder() {
    Session session = this.getSession();
    try {
      Query q = SQL.getDebtOrReceivableErrorRepairOrder(session);
      return (List<Long>) q.list();
    } finally {
      release(session);
    }
  }

  public List<Long> getRepairOrderItemIds(Long shopId, int start, int pageSize) {
    Session session = this.getSession();
    try {
      Query query = SQL.getRepairOrderItemIds(session, shopId, start, pageSize);
      return query.list();
    } finally {
      release(session);
    }
  }

  /**
   * 根据施工单ID查询销售单货品表
   *
   * @param repairOrderItemId
   * @return
   */
  public List<RepairOrderItem> getRepairOrderItemsById(Long shopId, Long... repairOrderItemId) {

    Session session = getSession();
    try {
      Query q = SQL.getRepairOrderItemsById(session, shopId, repairOrderItemId);
      return (List<RepairOrderItem>) q.list();
    } finally {
      release(session);
    }
  }

  public List<Long> getRepairOrderServiceItemIds(Long shopId, int start, int pageSize) {
    Session session = this.getSession();
    try {
      Query query = SQL.getRepairOrderServiceItemIds(session, shopId, start, pageSize);
      return query.list();
    } finally {
      release(session);
    }
  }

  /**
   * 根据施工单ID查询 施工项目
   *
   * @param repairOrderServiceItemId
   * @return
   */
  public List<RepairOrderService> getRepairOrderServiceItemsById(Long shopId, Long... repairOrderServiceItemId) {

    Session session = getSession();
    try {
      Query q = SQL.getRepairOrderServiceItemsById(session, shopId, repairOrderServiceItemId);
      return (List<RepairOrderService>) q.list();
    } finally {
      release(session);
    }
  }

  public List<Long> getPurchaseInventoryOrderItemIds(Long shopId, int start, int pageSize) {
    Session session = this.getSession();
    try {
      Query query = SQL.getPurchaseInventoryOrderItemIds(session, shopId, start, pageSize);
      return query.list();
    } finally {
      release(session);
    }
  }

  /**
   * @param inventoryItemId
   * @return
   */
  public List<PurchaseInventoryItem> getPurchaseInventoryOrderItemById(Long shopId, Long... inventoryItemId) {

    Session session = getSession();
    try {
      Query q = SQL.getPurchaseInventoryOrderItemById(session, shopId, inventoryItemId);
      return (List<PurchaseInventoryItem>) q.list();
    } finally {
      release(session);
    }
  }

  public List<Long> getSaleOrderItemIds(Long shopId, int start, int pageSize) {
    Session session = this.getSession();
    try {
      Query query = SQL.getSaleOrderItemIds(session, shopId, start, pageSize);
      return query.list();
    } finally {
      release(session);
    }
  }

  /**
   * @param repairOrderItemId
   * @return
   */
  public List<SalesOrderItem> getSaleOrderItemsById(Long shopId, Long... repairOrderItemId) {

    Session session = getSession();
    try {
      Query q = SQL.getSaleOrderItemsById(session, shopId, repairOrderItemId);
      return (List<SalesOrderItem>) q.list();
    } finally {
      release(session);
    }
  }

  public List<Long> getWashBeautyOrderItemIds(Long shopId, int start, int pageSize) {
    Session session = this.getSession();
    try {
      Query query = SQL.getWashBeautyOrderItemIds(session, shopId, start, pageSize);
      return query.list();
    } finally {
      release(session);
    }
  }

  /**
   * 根据施工单ID查询 施工项目
   *
   * @param washItemId
   * @return
   */
  public List<WashBeautyOrderItem> getWashBeautyOrderItemsById(Long shopId, Long... washItemId) {

    Session session = getSession();
    try {
      Query q = SQL.getWashBeautyOrderItemsById(session, shopId, washItemId);
      return (List<WashBeautyOrderItem>) q.list();
    } finally {
      release(session);
    }
  }

  public Category getCategoryById(Long shopId, Long categoryId) {
    Session session = getSession();

    try {
      Query q = SQL.getCategoryById(session, shopId, categoryId);

      List<Category> categoryList = (List<Category>) q.list();

      if (CollectionUtils.isEmpty(categoryList)) {
        return null;
      }
      return categoryList.get(0);
    } finally {
      release(session);
    }
  }

  public List<Category> getCategoriesByIds(Long shopId, Set<Long> categoryIds) {
    Session session = getSession();
    try {
      Query q = SQL.getCategoriesByIds(session, shopId, categoryIds);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<Category> vagueGetCategoryByShopIdAndName(Long shopId, String keyWord) {
    Session session = getSession();

    try {
      Query q = SQL.vagueGetCategoryByShopIdAndName(session, shopId, keyWord);

      return (List<Category>) q.list();

    } finally {
      release(session);
    }
  }

  public List<Category> getCategoryByShopIdAndName(Long shopId, String keyWord) {
    Session session = getSession();

    try {
      Query q = SQL.getCategoryByShopIdAndName(session, shopId, keyWord);

      return (List<Category>) q.list();

    } finally {
      release(session);
    }
  }

  public CategoryItemRelation getCategoryItemRelationByServiceId(Long serviceId) {
    Session session = getSession();

    try {
      Query q = SQL.getCategoryItemRelationByServiceId(session, serviceId);

      List<CategoryItemRelation> categoryItemRelationList = (List<CategoryItemRelation>) q.list();

      if (CollectionUtils.isEmpty(categoryItemRelationList)) {
        return null;
      }
      return categoryItemRelationList.get(0);
    } finally {
      release(session);
    }
  }

  public List<CategoryItemRelation> getCategoryItemRelationByServiceIds(Long... serviceId) {
    if (ArrayUtils.isEmpty(serviceId)) {
      return new ArrayList<CategoryItemRelation>();
    }
    Session session = getSession();

    try {
      Query q = SQL.getCategoryItemRelationByServiceIds(session, serviceId);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<CategoryItemRelation> getCategoryItemRelation() {
    Session session = getSession();

    try {
      Query q = SQL.getCategoryItemRelation(session);
      return (List<CategoryItemRelation>) q.list();
    } finally {
      release(session);
    }
  }

  public List<WashBeautyOrderItem> getWashBeautyOrderItem() {
    Session session = getSession();

    try {
      Query q = SQL.getWashBeautyOrderItem(session);
      return (List<WashBeautyOrderItem>) q.list();
    } finally {
      release(session);
    }
  }

  public List<RepairOrderService> getRepairOrderService() {
    Session session = getSession();

    try {
      Query q = SQL.getRepairOrderService(session);
      return (List<RepairOrderService>) q.list();
    } finally {
      release(session);
    }
  }


  public PurchaseInventoryStatChange getPurchaseInventoryStatChange(Long shopId, Long productId, int year, int month, int day) {
    Session session = getSession();
    try {
      Query q = SQL.getPurchaseInventoryStatChange(session, shopId, productId, year, month, day);
      return (PurchaseInventoryStatChange) CollectionUtil.getFirst(q.list());
    } finally {
      release(session);
    }
  }

  public PurchaseInventoryStat getPurchaseInventoryStat(Long shopId, Long productId, int year, int month, int day) {
    Session session = getSession();
    try {
      Query q = SQL.getPurchaseInventoryStat(session, shopId, productId, year, month, day);
      return (PurchaseInventoryStat) CollectionUtil.getFirst(q.list());
    } finally {
      release(session);
    }
  }

  public PurchaseInventoryStat getLatestPurchaseInventoryStatBeforeTime(Long shopId, Long productId, long startTimeOfTimeDay) {
    Session session = getSession();
    try {
      Query q = SQL.getLatestPurchaseInventoryStatBeforeTime(session, shopId, productId, startTimeOfTimeDay);
      return (PurchaseInventoryStat) q.uniqueResult();
    } finally {
      release(session);
    }
  }

  public SupplierTranStat getSupplierTranStat(Long shopId, Long supplierId, int year, int month, int day) {
    Session session = getSession();
    try {
      Query q = SQL.getSupplierTranStat(session, shopId, supplierId, year, month, day);
      return (SupplierTranStat) CollectionUtil.getFirst(q.list());
    } finally {
      release(session);
    }
  }

  public SupplierTranStat getLatestSupplierTranStatBeforeTime(Long shopId, Long supplierId, long startTimeOfTimeDay) {
    Session session = getSession();
    try {
      Query q = SQL.getLatestSupplierTranStatBeforeTime(session, shopId, supplierId, startTimeOfTimeDay);
      return (SupplierTranStat) q.uniqueResult();
    } finally {
      release(session);
    }
  }

  public SupplierTranStatChange getSupplierTranStatChange(Long shopId, Long supplierId, int year, int month, int day) {
    Session session = getSession();
    try {
      Query q = SQL.getSupplierTranStatChange(session, shopId, supplierId, year, month, day);
      return (SupplierTranStatChange) CollectionUtil.getFirst(q.list());
    } finally {
      release(session);
    }
  }

  public PurchaseReturnMonthStat getPurchaseReturnMonthStat(Long shopId, Long supplierId, Long productId, int year, int month) {
    Session session = getSession();
    try {
      Query q = SQL.getPurchaseReturnMonthStat(session, shopId, supplierId, productId, year, month);
      return (PurchaseReturnMonthStat) q.uniqueResult();
    } finally {
      release(session);
    }
  }

  public PurchaseReturnStat getLatestPurchaseReturnStatBeforeTime(Long shopId, Long supplierId, Long productId, long startTimeOfTimeDay) {
    Session session = getSession();
    try {
      Query q = SQL.getLatestPurchaseReturnStatBeforeTime(session, shopId, supplierId, productId, startTimeOfTimeDay);
      return (PurchaseReturnStat) q.uniqueResult();
    } finally {
      release(session);
    }
  }

  public PurchaseReturnStatChange getPurchaseReturnStatChange(Long shopId, Long supplierId, Long productId, int year, int month, int day) {
    Session session = getSession();
    try {
      Query q = SQL.getPurchaseReturnStatChange(session, shopId, supplierId, productId, year, month, day);
      return (PurchaseReturnStatChange) CollectionUtil.getFirst(q.list());
    } finally {
      release(session);
    }
  }

  public SalesStat getSalesStat(Long shopId, Long productId, int year, int month, int day) {
    Session session = getSession();
    try {
      Query q = SQL.getSalesStat(session, shopId, productId, year, month, day);
      return (SalesStat) CollectionUtil.getFirst(q.list());
    } finally {
      release(session);
    }
  }

  public SalesStat getLatestSalesStatBeforeTime(Long shopId, Long productId, long startTimeOfTimeDay) {
    Session session = getSession();
    try {
      Query q = SQL.getLatestSalesStatBeforeTime(session, shopId, productId, startTimeOfTimeDay);
      return (SalesStat) q.uniqueResult();
    } finally {
      release(session);
    }
  }

  public SalesStatChange getSalesStatChange(Long shopId, Long productId, int year, int month, int day) {
    Session session = getSession();
    try {
      Query q = SQL.getSalesStatChange(session, shopId, productId, year, month, day);
      return (SalesStatChange) CollectionUtil.getFirst(q.list());
    } finally {
      release(session);
    }
  }

  public PurchaseInventory getFirstPurchaseInventoryByVestDate(Long shopId) {
    Session session = getSession();
    try {
      Query q = SQL.getFirstPurchaseInventoryByVestDate(session, shopId);
      return (PurchaseInventory) q.uniqueResult();
    } finally {
      release(session);
    }
  }

  public List<PurchaseInventory> getPurchaseInventoryDTOByVestDate(Long shopId, long start, long end) {
    Session session = getSession();
    try {
      Query q = SQL.getPurchaseInventoryByVestDate(session, shopId, start, end);
      return q.list();
    } finally {
      release(session);
    }
  }

  public PurchaseInventoryStat getLatestPurchaseInventoryStatInRange(Long shopId, Long productId, long fromTime, long endTime) {
    Session session = getSession();
    try {
      Query q = SQL.getLatestPurchaseInventoryStatInRange(session, shopId, productId, fromTime, endTime);
      return (PurchaseInventoryStat) q.uniqueResult();
    } finally {
      release(session);
    }
  }

  public PurchaseInventoryStat getLatestInventoryStatBeforeTime(Long shopId, Long productId, long fromTime) {
    Session session = getSession();
    try {
      Query q = SQL.getLatestInventoryStatBeforeTime(session, shopId, productId, fromTime);
      return (PurchaseInventoryStat) q.uniqueResult();
    } finally {
      release(session);
    }
  }

  public PurchaseInventoryStatChange getPurchaseInventoryStatChangeInRange(Long shopId, Long productId, long fromTime, long endTime) {
    Session session = getSession();
    try {
      Query q = SQL.getPurchaseInventoryStatChangeInRange(session, shopId, productId, fromTime, endTime);
      Object[] obj = (Object[]) q.uniqueResult();
      if (obj == null) {
        PurchaseInventoryStatChange stat = new PurchaseInventoryStatChange();
        stat.setAmount(0);
        stat.setTimes(0);
        stat.setTotal(0);
        return stat;
      }
      PurchaseInventoryStatChange change = new PurchaseInventoryStatChange();
      change.setShopId(obj[0] == null ? -1 : Long.parseLong(obj[0].toString()));
      change.setProductId(obj[1] == null ? -1 : Long.parseLong(obj[1].toString()));
      change.setTimes(obj[2] == null ? 0 : Integer.parseInt(obj[2].toString()));
      change.setAmount(obj[3] == null ? 0 : Double.parseDouble(obj[3].toString()));
      change.setTotal(obj[4] == null ? 0 : Double.parseDouble(obj[4].toString()));
      return change;
    } finally {
      release(session);
    }
  }

  public SupplierTranStat getLatestSupplierTranStatInRange(Long shopId, Long supplierId, long fromTime, long endTime) {
    Session session = getSession();
    try {
      Query q = SQL.getLatestSupplierTranStatInRange(session, shopId, supplierId, fromTime, endTime);
      return (SupplierTranStat) q.uniqueResult();
    } finally {
      release(session);
    }
  }

  public SupplierTranStatChange getSupplierTranStatChangeInRange(Long shopId, Long supplierId, long fromTime, long endTime) {
    Session session = getSession();
    try {
      Query q = SQL.getSupplierTranStatChangeInRange(session, shopId, supplierId, fromTime, endTime);
      Object[] obj = (Object[]) q.uniqueResult();
      if (obj == null) {
        return null;
      }
      SupplierTranStatChange supplierTranStatChange = new SupplierTranStatChange();
      supplierTranStatChange.setShopId(obj[0] == null ? -1 : Long.parseLong(obj[0].toString()));
      supplierTranStatChange.setSupplierId(obj[1] == null ? -1 : Long.parseLong(obj[1].toString()));
      supplierTranStatChange.setTimes(obj[2] == null ? 0 : Integer.parseInt(obj[2].toString()));
      supplierTranStatChange.setTotal(obj[3] == null ? 0 : Double.parseDouble(obj[3].toString()));
      return supplierTranStatChange;
    } finally {
      release(session);
    }
  }

  public SupplierTranMonthStat getSupplierTranMonthStat(Long shopId, Long supplierId, int year, int month) {
    Session session = getSession();
    try {
      Query q = SQL.getSupplierTranMonthStat(session, shopId, supplierId, year, month);
      return (SupplierTranMonthStat) CollectionUtil.getFirst(q.list());
    } finally {
      release(session);
    }
  }

  public List<SupplierTranMonthStat> queryTopSupplierTranMonthStat(Long shopId, int year, int month, boolean allYear, int topLimit) {
    Session session = getSession();
    try {
      if (!allYear) {
        Query q = SQL.queryTopSupplierTranMonthStat(session, shopId, year, month, allYear, topLimit);
        return q.list();
      }
      Query q = SQL.queryTopSupplierTranYearStat(session, shopId, year, topLimit);
      List<Object[]> list = q.list();
      List<SupplierTranMonthStat> result = new ArrayList<SupplierTranMonthStat>();
      if (CollectionUtils.isEmpty(list)) {
        return result;
      }
      for (Object[] obj : list) {
        SupplierTranMonthStat stat = new SupplierTranMonthStat();
        stat.setShopId(obj[0] == null ? -1 : Long.parseLong(obj[0].toString()));
        stat.setSupplierId(obj[1] == null ? -1 : Long.parseLong(obj[1].toString()));
        stat.setStatYear(obj[2] == null ? 0 : Integer.parseInt(obj[2].toString()));
        stat.setTimes(obj[3] == null ? 0 : Integer.parseInt(obj[3].toString()));
        stat.setTotal(obj[4] == null ? 0 : Double.parseDouble(obj[4].toString()));
        result.add(stat);
      }
      return result;
    } finally {
      release(session);
    }
  }

  public double querySupplierTranTotal(Long shopId, int year, int month, boolean allYear) {
    Session session = getSession();
    try {
      Query q = SQL.querySupplierTranTotal(session, shopId, year, month, allYear);
      Object o = q.uniqueResult();
      return o == null ? 0 : Double.parseDouble(o.toString());
    } finally {
      release(session);
    }
  }

  public List<PurchaseInventoryMonthStat> queryTopPurchaseInventoryMonthStat(Long shopId, int year, int month, boolean allYear, String[] queryFields, int topLimit) {
    Session session = getSession();
    try {
      Query q = SQL.queryTopPurchaseInventoryMonthStat(session, shopId, year, month, allYear, queryFields, topLimit);
      List<Object[]> list = q.list();
      List<PurchaseInventoryMonthStat> result = new ArrayList<PurchaseInventoryMonthStat>();
      if (CollectionUtils.isEmpty(list)) {
        return result;
      }
      for (Object[] obj : list) {
        PurchaseInventoryMonthStat stat = new PurchaseInventoryMonthStat();
        stat.setProductName(obj[0] == null ? "" : obj[0].toString());
        if (ArrayUtils.contains(queryFields, CostStatConditionDTO.FIELD_BRAND)) {
          stat.setProductBrand(obj[1] == null || obj[1].equals("\u0000") ? "" : obj[1].toString());
        } else {
          stat.setProductBrand("-");
        }
        if (ArrayUtils.contains(queryFields, CostStatConditionDTO.FIELD_VEHICLE_MODEL)) {
          stat.setVehicleBrand(obj[2] == null || obj[2].equals("\u0000") ? "" : obj[2].toString());
          stat.setVehicleModel(obj[3] == null || obj[3].equals("\u0000") ? "" : obj[3].toString());
        } else {
          stat.setVehicleBrand("-");
          stat.setVehicleModel("-");
        }
        stat.setTimes(obj[4] == null ? 0 : Integer.parseInt(obj[4].toString()));
        stat.setAmount(obj[5] == null ? 0 : Double.parseDouble(obj[5].toString()));
        stat.setTotal(obj[6] == null ? 0 : Double.parseDouble(obj[6].toString()));
        stat.setShopId(shopId);
        stat.setStatYear(year);
        stat.setStatMonth(month);
        result.add(stat);
      }
      return result;
    } finally {
      release(session);
    }
  }

  public double queryPurchaseInventoryTotal(Long shopId, int year, int month, boolean allYear) {
    Session session = getSession();
    try {
      Query q = SQL.queryPurchaseInventoryTotal(session, shopId, year, month, allYear);
      Object o = q.uniqueResult();
      return o == null ? 0 : Double.parseDouble(o.toString());
    } finally {
      release(session);
    }
  }

  /**
   * 得到采购月统计表的指定数据
   *
   * @param shopId
   * @param itemDTO 只用来作为存储商品四属性的容器（品名，品牌，车型，车品牌）
   * @param year
   * @param month
   * @return
   */
  public PurchaseInventoryMonthStat getPurchaseInventoryMonthStat(Long shopId, BcgogoOrderItemDto itemDTO, int year, int month) {
    Session session = getSession();
    try {
      Query q = SQL.getPurchaseInventoryMonthStat(session, shopId, itemDTO, year, month);
      return (PurchaseInventoryMonthStat) CollectionUtil.getFirst(q.list());
    } finally {
      release(session);
    }
  }

  public List<ProductModifyLog> getProductModifyLogByStatus(ProductModifyOperations productModifyOperation, StatProcessStatus[] status) {
    Session session = getSession();
    try {
      Query q = SQL.getProductModifyLogByStatus(session, productModifyOperation, status);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<ProductModifyLog> getProductModifyLogDTOByRelevanceStatus(ProductRelevanceStatus relevanceStatus, Long... productId) {
    Session session = getSession();
    try {
      Query q = SQL.getProductModifyLogDTOByRelevanceStatus(session, relevanceStatus, productId);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<PurchaseInventoryMonthStat> getPurchaseInventoryMonthStatByProperties(Long shopId, String name, String brand, String vehicleBrand, String vehicleModel) {
    Session session = getSession();
    try {
      Query q = SQL.getPurchaseInventoryMonthStatByProperties(session, shopId, name, brand, vehicleBrand, vehicleModel);
      return q.list();
    } finally {
      release(session);
    }
  }

  public void batchUpdateProductModifyLogStatus(List<Long> ids, StatProcessStatus doneStatus) {
    Session session = getSession();
    try {
      Query q = SQL.batchUpdateProductModifyLogStatus(session, ids, doneStatus);
      q.executeUpdate();
    } finally {
      release(session);
    }
  }

  public PurchaseReturn getFirstPurchaseReturnByVestDate(Long shopId) {
    Session session = getSession();
    try {
      Query q = SQL.getFirstPurchaseReturnByVestDate(session, shopId);
      return (PurchaseReturn) q.uniqueResult();
    } finally {
      release(session);
    }
  }

  public List<PurchaseReturn> getPurchaseReturnByVestDate(Long shopId, long startTime, long endTime) {
    Session session = getSession();
    try {
      Query q = SQL.getPurchaseReturnByVestDate(session, shopId, startTime, endTime);
      return q.list();
    } finally {
      release(session);
    }
  }

  public SalesOrder getFirstSalesOrderByVestDate(Long shopId) {
    Session session = getSession();
    try {
      Query q = SQL.getFirstSalesOrderByVestDate(session, shopId);
      return (SalesOrder) q.uniqueResult();
    } finally {
      release(session);
    }
  }

  public RepairOrder getFirstRepairOrderByVestDate(Long shopId) {
    Session session = getSession();
    try {
      Query q = SQL.getFirstRepairOrderByVestDate(session, shopId);
      return (RepairOrder) q.uniqueResult();
    } finally {
      release(session);
    }
  }

  public List<RepairOrder> getRepairOrderListByVestDate(Long shopId, long startTime, long endTime) {
    Session session = getSession();
    try {
      Query q = SQL.getRepairOrderListByVestDate(session, shopId, startTime, endTime);
      return (List<RepairOrder>) q.list();
    } finally {
      release(session);
    }
  }

  public List<SalesMonthStat> getSalesMonthStatByYearMonth(Long shopId, Long productId, int year, int month) {
    Session session = getSession();
    try {
      Query q = SQL.getSalesMonthStatByYearMonth(session, shopId, productId, year, month);
      List list = q.list();
      if (CollectionUtils.isEmpty(list)) {
        return null;
      }
      return (List<SalesMonthStat>) list;
    } finally {
      release(session);
    }
  }

  public List<ProductDTO> querySalesStatByCondition(Long shopId, SalesStatCondition salesStatCondition) {
    Session session = getSession();
    try {
      Query q = SQL.querySalesStatByCondition(session, shopId, salesStatCondition);
      List<Object> list = q.list();
      if (CollectionUtils.isEmpty(list)) {
        return null;
      }
      List<ProductDTO> productDTOList = new ArrayList<ProductDTO>();
      for (Object object : list) {
        Object[] array = (Object[]) object;
        if (array[0] != null && array[1] != null && array[2] != null) {
          ProductDTO productDTO = new ProductDTO();
          productDTO.setId((Long) array[0]);
          productDTO.setSalesAmount((Double) array[1]);
          productDTO.setSalesTotal((Double) array[2]);
          productDTOList.add(productDTO);
        }
      }
      return productDTOList;
    } finally {
      release(session);
    }
  }

  public List<String> countSalesStatByCondition(Long shopId, SalesStatCondition salesStatCondition) {
    Session session = getSession();
    try {
      Query q = SQL.countSalesStatByCondition(session, shopId, salesStatCondition);
      List<Object> list = q.list();
      if (CollectionUtils.isEmpty(list)) {
        return null;
      }
      List<String> stringList = new ArrayList<String>();
      Object[] array = (Object[]) list.get(0);
      if (array[0] != null && array[1] != null && array[2] != null) {
        stringList.add(array[0].toString());
        stringList.add(array[1].toString());
        stringList.add(array[2].toString());
      }
      return stringList;
    } finally {
      release(session);
    }
  }


  public List<Inventory> getInventoryByShopId(Long shopId, Pager pager) {
    Session session = getSession();
    try {
      Query q = SQL.getInventoryByShopId(session, shopId, pager);
      List list = q.list();
      if (CollectionUtils.isEmpty(list)) {
        return null;
      }
      return (List<Inventory>) list;
    } finally {
      release(session);
    }
  }

  public Long getSalesVestDateByShopId(Long shopId, Long productId, Sort sort) {
    Session session = getSession();
    try {
      Query q = SQL.getSalesVestDateByShopId(session, shopId, productId, sort);
      return (Long) q.uniqueResult();
    } finally {
      release(session);
    }
  }

  public Long getRepairVestDateByShopId(Long shopId, Long productId, Sort sort) {
    Session session = getSession();
    try {
      Query q = SQL.getRepairVestDateByShopId(session, shopId, productId, sort);
      return (Long) q.uniqueResult();
    } finally {
      release(session);
    }
  }

  public List<String> countBadSalesStatByCondition(Long shopId, SalesStatCondition salesStatCondition) {
    Session session = getSession();
    try {
      Query q = SQL.countBadSalesStatByCondition(session, shopId, salesStatCondition);
      List<Object> list = q.list();
      if (CollectionUtils.isEmpty(list)) {
        return null;
      }
      List<String> stringList = new ArrayList<String>();
      Object[] array = (Object[]) list.get(0);
      if (array[0] != null && array[1] != null && array[2] != null) {
        stringList.add(array[0].toString());
        stringList.add(array[1].toString());
        stringList.add(array[2].toString());
      }
      return stringList;
    } finally {
      release(session);
    }
  }


  public List<ProductDTO> queryBadSalesStatByCondition(Long shopId, SalesStatCondition salesStatCondition) {
    Session session = getSession();
    try {
      Query q = SQL.queryBadSalesStatByCondition(session, shopId, salesStatCondition);
      List<Object> list = q.list();
      if (CollectionUtils.isEmpty(list)) {
        return null;
      }
      List<ProductDTO> productDTOList = new ArrayList<ProductDTO>();
      for (Object object : list) {
        Object[] array = (Object[]) object;
        if (array[0] != null && array[1] != null && array[2] != null) {
          ProductDTO productDTO = new ProductDTO();
          productDTO.setId((Long) array[0]);
          productDTO.setSalesAmount(NumberUtil.toReserve((Double) array[1], 1));
          productDTO.setInventoryAveragePrice(NumberUtil.toReserve((Double) array[2], NumberUtil.MONEY_PRECISION));
          productDTO.setSalesTotal(NumberUtil.toReserve(productDTO.getSalesAmount() * productDTO.getInventoryAveragePrice(), NumberUtil.MONEY_PRECISION));
          productDTOList.add(productDTO);
        }
      }
      return productDTOList;
    } finally {
      release(session);
    }
  }

  public List<PriceFluctuationStatDTO> queryTopPurchaseInventoryLastTwelveMonthStat(Long shopId, int limit) {
    Session session = getSession();
    List<PriceFluctuationStatDTO> dtoList = new ArrayList<PriceFluctuationStatDTO>();
    try {
      Query q = SQL.queryTopPurchaseInventoryLastTwelveMonthStat(session, shopId, limit);
      List<PriceFluctuationStat> list = q.list();
      for (int i = 0; i < list.size(); i++) {
        dtoList.add(list.get(i).toDTO());
      }
      return dtoList;
    } finally {
      release(session);
    }
  }


  public List<Object[]> queryAllProductPriceFluctuation(Long startTime, Long endTime) {
    Session session = getSession();
    List<Object[]> objList = new ArrayList<Object[]>();
    try {
      Query q = SQL.queryAllProductPriceFluctuation(session, startTime, endTime);
      objList = q.list();
      return objList;
    } finally {
      release(session);
    }
  }

  public void emptyPriceFluctuationStat() {
    LOG.info("AOP_DEBUG:emptyPriceFluctuationStat");
    Session session = getSession();
    Transaction tx = session.beginTransaction();
    try {
      session.createSQLQuery("truncate TABLE price_fluctuation_stat").executeUpdate();
      tx.commit();
    } catch (Exception e) {
      tx.rollback();
      e.printStackTrace();
    } finally {
      session.close();
    }
  }

//  public Double getPriceFluctuationLineChartData(Long shopId, Long productId, Long startTime, Long endTime){
//    Session session = getSession();
//    try{
//      Query q = SQL.getPriceFluctuationLineChartData(session,shopId,productId,startTime,endTime);
//      if(q!=null && q.uniqueResult()!=null){
//        Double avgPrice = Double.parseDouble(q.uniqueResult().toString());
//        return avgPrice;
//      }else{
//        return 0D;
//      }
//    }finally {
//      release(session);
//    }
//  }

  public Map<String, Object> getPriceFluctuationLineChartData(Long shopId, Long productId, Long startTime, Long endTime) {
    Session session = getSession();
    try {
      Query q = SQL.getPurchaseInventoryStatByProductId(session, shopId, productId, startTime, endTime);
      List<Object[]> statList = q.list();
      q = SQL.getPurchaseInventoryStatChangeByProductId(session, shopId, productId, startTime, endTime);
      List<Object[]> changeList = q.list();
      Map<String, Object> map = new HashMap<String, Object>();
      map.put("statList", statList);
      map.put("changeList", changeList);
      return map;
    } finally {
      release(session);
    }
  }

  public List<String> countTotalReturnByCondition(Long shopId, SalesStatCondition salesStatCondition) {
    Session session = getSession();
    try {
      Query q = SQL.countTotalReturnByCondition(session, shopId, salesStatCondition);
      List<Object> list = q.list();
      if (CollectionUtils.isEmpty(list)) {
        return null;
      }
      List<String> stringList = new ArrayList<String>();
      Object[] array = (Object[]) list.get(0);
      if (array[0] != null && array[1] != null && array[2] != null && array[3] != null) {
        stringList.add(array[0].toString());
        stringList.add(array[1].toString());
        stringList.add(array[2].toString());
        stringList.add(array[3].toString());
      }
      return stringList;
    } finally {
      release(session);
    }
  }

  public List<PurchaseReturnMonthStatDTO> queryPurchaseReturnByCondition(Long shopId, SalesStatCondition salesStatCondition) {
    Session session = getSession();
    try {
      Query q = SQL.queryPurchaseReturnByCondition(session, shopId, salesStatCondition);
      List<Object> list = q.list();
      if (CollectionUtils.isEmpty(list)) {
        return null;
      }
      List<PurchaseReturnMonthStatDTO> purchaseReturnMonthStatList = new ArrayList<PurchaseReturnMonthStatDTO>();
      for (Object object : list) {
        Object[] array = (Object[]) object;
        if (!ArrayUtils.isEmpty(array) && array.length == 5) {
          PurchaseReturnMonthStatDTO purchaseReturnMonthStat = new PurchaseReturnMonthStatDTO();
          purchaseReturnMonthStat.setSupplierIdOrdProductId((Long) array[0]);
          purchaseReturnMonthStat.setTotal(NumberUtil.toReserve((Double) array[1], NumberUtil.MONEY_PRECISION));
          purchaseReturnMonthStat.setAmount(NumberUtil.toReserve((Double) array[2], 1));
          purchaseReturnMonthStat.setTimes(((Long) array[3]).intValue());
          purchaseReturnMonthStat.setReturnProductCategories(((Long) array[4]).intValue());
          purchaseReturnMonthStat.setShopId(shopId);
          purchaseReturnMonthStatList.add(purchaseReturnMonthStat);
        }
      }
      return purchaseReturnMonthStatList;
    } finally {
      release(session);
    }
  }


  public PurchaseReturnStat getPurchaseReturnStat(Long shopId, Long supplierId, Long productId, int year, int month, int day) {
    Session session = getSession();
    try {
      Query q = SQL.getPurchaseReturnStat(session, shopId, supplierId, productId, year, month, day);
      return (PurchaseReturnStat) q.uniqueResult();
    } finally {
      release(session);
    }
  }

  public List<PurchaseReturnMonthStat> getAllPurchaseReturnMonthStatBySupplierIds(Long shopId, Long... supplierIds) {
    Session session = getSession();
    try {
      Query q = SQL.getAllPurchaseReturnMonthStatBySupplierIds(session, shopId, supplierIds);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<SupplierTranMonthStat> getAllSupplierTranMonthStatBySupplierIds(Long shopId, Long... supplierIds) {
    Session session = getSession();
    try {
      Query q = SQL.getAllSupplierTranMonthStatBySupplierIds(session, shopId, supplierIds);
      return q.list();
    } finally {
      release(session);
    }
  }

  public Long getFirstPurchaseInventoryCreationDateByProductIdShopId(Long shopId, Long productId) {
    Session session = getSession();
    try {
      Query q = SQL.getFirstPurchaseInventoryCreationDateByProductIdShopId(session, shopId, productId);
      return (Long) q.uniqueResult();
    } finally {
      release(session);
    }
  }

  public List<PurchaseInventory> getPurchaseInventoryByProductIdCreationDate(Long shopId, Long productId, Long startTime, Long endTime) {
    Session session = getSession();
    try {
      Query q = SQL.getPurchaseInventoryByProductIdCreationDate(session, shopId, productId, startTime, endTime);
      return q.list();
    } finally {
      release(session);
    }
  }

  public Long getFirstPurchaseReturnCreationDateByProductIdShopId(Long shopId, Long productId) {
    Session session = getSession();
    try {
      Query q = SQL.getFirstPurchaseReturnCreationDateByProductIdShopId(session, shopId, productId);
      return (Long) q.uniqueResult();
    } finally {
      release(session);
    }
  }

  public List<PurchaseReturn> getPurchaseReturnByProductIdCreationDate(Long shopId, Long productId, Long startTime, Long endTime) {
    Session session = getSession();
    try {
      Query q = SQL.getPurchaseReturnByProductIdCreationDate(session, shopId, productId, startTime, endTime);
      return q.list();
    } finally {
      release(session);
    }
  }

  public int getImportedOrderCountByConditions(ImportedOrderDTO importedOrderIndex) {
    Session session = this.getSession();
    try {
      Query q = SQL.getImportedOrderCountByConditions(session, importedOrderIndex);
      return Integer.valueOf(q.uniqueResult().toString());
    } finally {
      release(session);
    }

  }

  public List<ImportedOrderTemp> getAllImportedOrderTemp(Long shopId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getAllImportedOrderTemp(session, shopId);
      return (List<ImportedOrderTemp>) q.list();
    } finally {
      release(session);
    }
  }

  public List<ImportedOrderTemp> getImportedOrderTempByReceipt(Long shopId, String receipt) {
    Session session = this.getSession();
    try {
      Query q = SQL.getImportedOrderTempByReceipt(session, shopId, receipt);
      return (List<ImportedOrderTemp>) q.list();
    } finally {
      release(session);
    }
  }

  public List<ItemIndexDTO> getImportedOrderItemDTOByOrderId(Long shopId, Long orderId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getImportedOrderItemByOrderId(session, shopId, orderId);
      List<ImportedOrderItem> orderItems = (List<ImportedOrderItem>) q.list();
      if (CollectionUtils.isEmpty(orderItems)) {
        return null;
      }
      List<ItemIndexDTO> orderItemDTOs = new ArrayList<ItemIndexDTO>();
      for (ImportedOrderItem orderItem : orderItems) {
        orderItemDTOs.add(orderItem.generateItemDTO());
      }
      return orderItemDTOs;
    } finally {
      release(session);
    }
  }

  public boolean deleteImportedOrderTempByShopId(Long shopId) {
    Session session = this.getSession();
    try {
      Query query = SQL.deleteImportedOrderTempByShopId(session, shopId);
      int count = query.executeUpdate();
      if (count > 0)
        return true;
      else
        return false;
    } finally {
      release(session);
    }
  }

  public List<ImportedOrder> getImportedOrderByConditions(ImportedOrderDTO importedOrderIndex) {
    Session session = this.getSession();
    try {
      Query q = SQL.getImportedOrderByConditions(session, importedOrderIndex);
      return (List<ImportedOrder>) q.list();
    } finally {
      release(session);
    }
  }

  public List getImportedOrderStatByOrderType(ImportedOrderDTO importedOrderIndex) {
    Session session = this.getSession();
    try {
      Query q = SQL.getImportedOrderStatByOrderType(session, importedOrderIndex);
      return q.list();
    } finally {
      release(session);
    }
  }


  public List<SalesOrder> getSalesOrderByProductIdCreationDate(Long shopId, Long productId, Long startTime, Long endTime) {
    Session session = getSession();
    try {
      Query q = SQL.getSalesOrderByProductIdCreationDate(session, shopId, productId, startTime, endTime);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<RepairOrder> getRepairOrderByProductIdCreationDate(Long shopId, Long productId, Long startTime, Long endTime) {
    Session session = getSession();
    try {
      Query q = SQL.getRepairOrderByProductIdCreationDate(session, shopId, productId, startTime, endTime);
      return q.list();
    } finally {
      release(session);
    }
  }

  public Category getCategoryByShopIdAndNameForInit(Long shopId, String name) {
    Session session = getSession();
    try {
      Query q = SQL.getCategoryByShopIdAndNameForInit(session, shopId, name);

      List<Category> categoryList = (List<Category>) q.list();

      if (CollectionUtils.isEmpty(categoryList)) {
        return null;
      }

      return categoryList.get(0);
    } finally {
      release(session);
    }
  }

  public List<Category> getCategoryByNameNotDefault(String name) {
    Session session = getSession();

    try {
      Query q = SQL.getCategoryByNameNotDefault(session, name);

      return (List<Category>) q.list();
    } finally {
      release(session);
    }
  }

  public List<CategoryItemRelation> getCategoryItemRelationByCategoryId(Long categoryId) {
    Session session = getSession();

    try {
      Query q = SQL.getCategoryItemRelationByCategoryId(session, categoryId);

      return (List<CategoryItemRelation>) q.list();
    } finally {
      release(session);
    }
  }

  public List<Long> getPurchaseInventoryIdFromPayableHistory(Long shopId, Long purchaseReturnId, PaymentTypes paymentType) {
    Session session = getSession();
    try {
      Query q = SQL.getPurchaseInventoryIdFromPayableHistory(session, shopId, purchaseReturnId, paymentType);
      return q.list();
    } finally {
      release(session);
    }
  }

  public VehicleServeMonthStat getVehicleServeMonthStat(Long shopId, String brand, String model, int year, int month) {
    Session session = getSession();
    try {
      Query q = SQL.getVehicleServeMonthStat(session, shopId, brand, model, year, month);
      return (VehicleServeMonthStat) CollectionUtil.getFirst(q.list());
    } finally {
      release(session);
    }
  }

  public WashBeautyOrder getFirstWashBeautyOrderByVestDate(Long shopId) {
    Session session = getSession();
    try {
      Query q = SQL.getFirstWashBeautyOrderByVestDate(session, shopId);
      return (WashBeautyOrder) CollectionUtil.getFirst(q.list());
    } finally {
      release(session);
    }
  }

  public List<WashBeautyOrder> getWashBeautyOrderByVestDate(Long shopId, long startTime, long endTime) {
    Session session = getSession();
    try {
      Query q = SQL.getWashBeautyOrderByVestDate(session, shopId, startTime, endTime);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<VehicleServeMonthStat> queryTopVehicleServeMonthStat(Long shopId, int year, int month, boolean allYear, int topLimit) {
    Session session = getSession();
    try {
      if (!allYear) {
        Query q = SQL.queryTopVehicleServeMonthStat(session, shopId, year, month, topLimit);
        return q.list();
      }
      Query q = SQL.queryTopVehicleServeYearStat(session, shopId, year, topLimit);
      List<Object[]> list = q.list();
      List<VehicleServeMonthStat> result = new ArrayList<VehicleServeMonthStat>();
      if (CollectionUtils.isEmpty(list)) {
        return result;
      }
      for (Object[] obj : list) {
        VehicleServeMonthStat stat = new VehicleServeMonthStat();
        stat.setShopId(obj[0] == null ? -1 : Long.parseLong(obj[0].toString()));
        stat.setBrand((obj[1] == null || obj[1].toString().equals("\u0000")) ? "" : obj[1].toString());
        stat.setModel((obj[2] == null || obj[2].toString().equals("\u0000")) ? "" : obj[2].toString());
        stat.setStatYear(obj[3] == null ? 0 : Integer.parseInt(obj[3].toString()));
        stat.setWashTimes(obj[4] == null ? 0 : Integer.parseInt(obj[4].toString()));
        stat.setRepairTimes(obj[5] == null ? 0 : Integer.parseInt(obj[5].toString()));
        stat.setTotalTimes(obj[6] == null ? 0 : Integer.parseInt(obj[6].toString()));
        result.add(stat);
      }
      return result;
    } finally {
      release(session);
    }
  }

  public int queryVehicleServeTotal(Long shopId, int year, int month, boolean allYear) {
    Session session = getSession();
    try {
      Query q = SQL.queryVehicleServeTotal(session, shopId, year, month, allYear);
      Object o = q.uniqueResult();
      return o == null ? 0 : Integer.parseInt(o.toString());
    } finally {
      release(session);
    }
  }

  public List<VehicleServeMonthStat> getVehicleServeMonthStatByBrandModel(Long shopId, String brand, String model) {
    Session session = getSession();
    try {
      Query q = SQL.getVehicleServeMonthStatByBrandModel(session, shopId, brand, model);
      return (List<VehicleServeMonthStat>) q.list();
    } finally {
      release(session);
    }
  }

  public Long getFirstRepairOrderCreationTimeByVehicleId(Long shopId, Long vehicleId) {
    Session session = getSession();
    try {
      Query q = SQL.getFirstRepairOrderCreationTimeByVehicleId(session, shopId, vehicleId);
      return (Long) q.uniqueResult();
    } finally {
      release(session);
    }
  }

  public Long getFirstWashBeautyOrderCreationTimeByVehicleId(Long shopId, Long vehicleId) {
    Session session = getSession();
    try {
      Query q = SQL.getFirstWashBeautyOrderCreationTimeByVehicleId(session, shopId, vehicleId);
      return (Long) q.uniqueResult();
    } finally {
      release(session);
    }
  }

  public VehicleServeMonthStat getVehicleServeMonthStatByBrandModelYearMonth(Long shopId, String brand, String model, int year, int month) {
    Session session = getSession();
    try {
      Query q = SQL.getVehicleServeMonthStatByBrandModelYearMonth(session, shopId, brand, model, year, month);
      return (VehicleServeMonthStat) CollectionUtil.getFirst(q.list());
    } finally {
      release(session);
    }
  }

  public List<RepairOrder> getRepairOrderListByCreationDate(Long shopId, long begin, long end) {
    Session session = getSession();
    try {
      Query q = SQL.getRepairOrderListByCreationDate(session, shopId, begin, end);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<WashBeautyOrder> getWashBeautyOrderByCreationDate(Long shopId, long begin, long end) {
    Session session = getSession();
    try {
      Query q = SQL.getWashBeautyOrderByCreationDate(session, shopId, begin, end);
      return q.list();
    } finally {
      release(session);
    }
  }

  public void deleteVehicleServeMonthStat() {
    Session session = getSession();
    try {
      Query q = SQL.deleteVehicleServeMonthStat(session);
      q.executeUpdate();
    } finally {
      release(session);
    }
  }

  public void deletePurchaseInventoryMonthStat() {
    Session session = getSession();
    try {
      Query q = SQL.deletePurchaseInventoryMonthStat(session);
      q.executeUpdate();
    } finally {
      release(session);
    }
  }

  public List<PurchaseInventory> getPurchaseInventoryDTOByCreationDate(Long shopId, long begin, long end) {
    Session session = getSession();
    try {
      Query q = SQL.getPurchaseInventoryByCreationDate(session, shopId, begin, end);
      return q.list();
    } finally {
      release(session);
    }
  }

  public PurchaseInventoryMonthStat getPurchaseInventoryMonthStatByPropertiesYearMonth(Long shopId, String name, String brand, String vehicleBrand, String vehicleModel, int statYear, int statMonth) {
    Session session = getSession();
    try {
      Query q = SQL.getPurchaseInventoryMonthStatByPropertiesYearMonth(session, shopId, name, brand, vehicleBrand, vehicleModel, statYear, statMonth);
      return (PurchaseInventoryMonthStat) CollectionUtil.getFirst(q.list());
    } finally {
      release(session);
    }
  }

  public List<String> getMemberReturnOrderCountAndSum(long shopId, long startTime, long endTime, OrderSearchConditionDTO orderSearchConditionDTO) {
    Session session = this.getSession();
    try {
      Query q = SQL.getMemberReturnOrderCountAndSum(session, shopId, startTime, endTime, orderSearchConditionDTO);
      List<String> stringList = new ArrayList<String>();
      if (q == null) {
        return stringList;
      }
      List list = q.list();
      if (CollectionUtils.isNotEmpty(list)) {
        Object[] array = (Object[]) list.get(0);
        if (array[0] != null && array[1] != null) {
          stringList.add(array[0].toString());
          stringList.add(array[1].toString());
        }
      }
      return stringList;
    } finally {
      release(session);
    }
  }

  public List<MemberCardReturn> getMemberReturnListByPagerTimeArrayType(long shopId, long startTime, long endTime, Pager pager, String arrayType, OrderSearchConditionDTO orderSearchConditionDTO) {
    Session session = getSession();

    try {
      Query q = SQL.getMemberReturnListByPagerTimeArrayType(session, shopId, startTime, endTime, pager, arrayType, orderSearchConditionDTO);
      return (List<MemberCardReturn>) q.list();
    } finally {
      release(session);
    }
  }


  public SupplierRecord getSupplierRecordDTOBySupplierId(Long shopId, Long supplierId) {
    Session session = getSession();

    try {
      Query q = SQL.getSupplierRecordDTOBySupplierId(session, shopId, supplierId);

      List<SupplierRecord> supplierRecordList = (List<SupplierRecord>) q.list();

      if (CollectionUtils.isEmpty(supplierRecordList)) {
        return null;
      }

      return supplierRecordList.get(0);

    } finally {
      release(session);
    }
  }

  public List<RepairOrder> getRepairOrders(RepairOrderDTO repairOrderIndex) {
    Session session = getSession();
    try {
      Query q = SQL.getRepairOrders(session, repairOrderIndex);
      return (List<RepairOrder>) q.list();
    } finally {
      release(session);
    }
  }

  public List getCustomerOrSupplierOrders(Long shopId, OrderTypes orderType, Long[] customerOrSupplierIds) {
    Session session = getSession();
    try {
      Query query = SQL.getCustomerOrSupplierOrders(session, shopId, orderType, customerOrSupplierIds);
      return query.list();
    } finally {
      release(session);
    }
  }

  public List<RepairRemindEvent> getRepairRemindEventByRepairOrderId(Long shopId, Long repairOrderId) {
    Session session = getSession();
    try {
      Query query = SQL.getRepairRemindEventByRepairOrderId(session, shopId, repairOrderId);
      return (List<RepairRemindEvent>) query.list();
    } finally {
      release(session);
    }
  }

  public Category getEnabledCategoryById(Long shopId, Long categoryId) {
    Session session = getSession();

    try {
      Query q = SQL.getEnabledCategoryById(session, shopId, categoryId);

      List<Category> categoryList = (List<Category>) q.list();

      if (CollectionUtils.isEmpty(categoryList)) {
        return null;
      }

      return categoryList.get(0);
    } finally {
      release(session);
    }
  }

  public List<Receivable> getMemberConsumeReceivable(Long shopId, Long memberId) {
    Session session = getSession();
    try {
      Query query = SQL.getMemberReceivableByOrderId(session, shopId, memberId);
      return (List<Receivable>) query.list();
    } finally {
      release(session);
    }
  }


  public List<SalesReturnItem> getSalesReturnItemsBySalesReturnId(Long salesReturnId) {
    Session session = getSession();
    try {
      Query q = SQL.getSalesReturnItemsBySalesReturnId(session, salesReturnId);
      return (List<SalesReturnItem>) q.list();
    } finally {
      release(session);
    }
  }

  public List<SalesReturn> getSalesReturnDTOByPurchaseReturnOrderId(Long purchaseReturnOrderId) {
    Session session = getSession();
    try {
      Query q = SQL.getSalesReturnDTOByPurchaseReturnOrderId(session, purchaseReturnOrderId);
      return (List<SalesReturn>) q.list();
    } finally {
      release(session);
    }
  }

  public List<SalesReturn> getSalesReturnDTOById(Long shopId, Long id) {
    Session session = getSession();
    try {
      Query q = SQL.getSalesReturnDTOById(session, shopId, id);
      return (List<SalesReturn>) q.list();
    } finally {
      release(session);
    }
  }

  public List<SalesReturn> getSalesReturnByPurchaseReturnOrderIdAndShopId(Long shopId, Long purchaseOrderId) {
    Session session = getSession();
    try {
      Query q = SQL.getSalesReturnByPurchaseReturnOrderIdAndShopId(session, shopId, purchaseOrderId);
      return (List<SalesReturn>) q.list();
    } finally {
      release(session);
    }
  }

  public BigInteger getTodoSalesOrderCount(Long shopId, Long startTime, Long endTime, List<Long> customerIdList, String receiptNo, String orderStatus) {
    Session session = getSession();
    try {
      Query q = SQL.getTodoSalesOrderCount(session, shopId, startTime, endTime, customerIdList, receiptNo, orderStatus);
      return (BigInteger) q.uniqueResult();
    } finally {
      release(session);
    }
  }

  public List<SalesOrder> getTodoSalesOrderDTOListByCondition(Long shopId, Long startTime, Long endTime, List<Long> customerIdList, String receiptNo, String orderStatus, Pager pager) {
    Session session = getSession();
    try {
      Query q = SQL.getTodoSalesOrderDTOListByCondition(session, shopId, startTime, endTime, customerIdList, receiptNo, orderStatus, pager);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<SalesOrder> getAllTodoSalesOrderDTOList(Long shopId, List<Long> customerIdList) {
    Session session = getSession();
    try {
      Query q = SQL.getAllTodoSalesOrderDTOList(session, shopId, customerIdList);
      return q.list();
    } finally {
      release(session);
    }
  }

  public BigInteger getTodoSalesReturnOrderCount(Long shopId, Long startTime, Long endTime, List<Long> customerIdList, String receiptNo, String orderStatus) {
    Session session = getSession();
    try {
      Query q = SQL.getTodoSalesReturnOrderCount(session, shopId, startTime, endTime, customerIdList, receiptNo, orderStatus);
      return (BigInteger) q.uniqueResult();
    } finally {
      release(session);
    }
  }

  public List<SalesReturn> getTodoSalesReturnOrderDTOListByCondition(Long shopId, Long startTime, Long endTime, List<Long> customerIdList, String receiptNo, String orderStatus, Pager pager) {
    Session session = getSession();
    try {
      Query q = SQL.getTodoSalesReturnOrderDTOListByCondition(session, shopId, startTime, endTime, customerIdList, receiptNo, orderStatus, pager);
      return q.list();
    } finally {
      release(session);
    }
  }

  public BigInteger getTodoPurchaseOrderCount(Long shopId, Long startTime, Long endTime, List<Long> supplierIdList, String receiptNo, String orderStatus, String timeField) {
    Session session = getSession();
    try {
      Query q = SQL.getTodoPurchaseOrderCount(session, shopId, startTime, endTime, supplierIdList, receiptNo, orderStatus, timeField);
      return (BigInteger) q.uniqueResult();
    } finally {
      release(session);
    }
  }

  public List<PurchaseOrder> getTodoPurchaseOrderDTOListByCondition(Long shopId, Long startTime, Long endTime, List<Long> supplierIdList, String receiptNo, String orderStatus, Pager pager, String timeField) {
    Session session = getSession();
    try {
      Query q = SQL.getTodoPurchaseOrderDTOListByCondition(session, shopId, startTime, endTime, supplierIdList, receiptNo, orderStatus, pager, timeField);
      return q.list();
    } finally {
      release(session);
    }
  }

  public BigInteger getTodoPurchaseReturnOrderCount(Long shopId, Long startTime, Long endTime, List<Long> supplierIdList, String receiptNo, String orderStatus) {
    Session session = getSession();
    try {
      Query q = SQL.getTodoPurchaseReturnOrderCount(session, shopId, startTime, endTime, supplierIdList, receiptNo, orderStatus);
      return (BigInteger) q.uniqueResult();
    } finally {
      release(session);
    }
  }

  public List<PurchaseReturn> getTodoPurchaseReturnOrderDTOListByCondition(Long shopId, Long startTime, Long endTime, List<Long> supplierIdList, String receiptNo, String orderStatus, Pager pager) {
    Session session = getSession();
    try {
      Query q = SQL.getTodoPurchaseReturnOrderDTOListByCondition(session, shopId, startTime, endTime, supplierIdList, receiptNo, orderStatus, pager);
      return q.list();
    } finally {
      release(session);
    }
  }

  public Integer countPushMessageByStatus(List<PushMessageType> types, Long receiverShopId, PushMessageReceiverStatus status, Long... receiverIds) {
    Session session = getSession();
    try {
      Query query = SQL.countPushMessageByStatus(session, types, receiverShopId, status, receiverIds);
      return Integer.valueOf(query.uniqueResult().toString());
    } finally {
      release(session);
    }
  }

  public List<PushMessage> searchSenderPushMessages(SearchMessageCondition searchMessageCondition) throws Exception {
    Session session = this.getSession();
    try {
      Query query = SQL.searchSenderPushMessages(session, searchMessageCondition);

      return (List<PushMessage>) query.list();
    } finally {
      release(session);
    }
  }

  public int countSenderPushMessages(SearchMessageCondition condition) throws Exception {
    Session session = getSession();
    try {
      Query query = SQL.countSenderPushMessages(session, condition);
      Object o = query.uniqueResult();
      return (o == null) ? 0 : Integer.valueOf(o.toString());
    } finally {
      release(session);
    }
  }

  public List<PushMessageReceiver> getPushMessageReceiverByMsgId(Long... ids) throws Exception {
    Session session = getSession();
    try {
      Query query = SQL.getPushMessageReceiverByMsgId(session, ids);
      return query.list();
    } finally {
      release(session);
    }
  }

  public List<MessageReceiver> getMessageReceive() {
    Session session = getSession();
    try {
      Query query = SQL.getMessageReceiver(session);
      return query.list();
    } finally {
      release(session);
    }
  }

  public PurchaseInventory getPurchaseInventoryIdByPurchaseOrderId(Long shopId, Long purchaseOrderId) {
    Session session = getSession();
    try {
      Query q = SQL.getPurchaseInventoryIdByPurchaseOrderId(session, shopId, purchaseOrderId);
      List<PurchaseInventory> purchaseInventoryList = q.list();
      if (CollectionUtils.isEmpty(purchaseInventoryList)) {
        return null;
      }
      for (PurchaseInventory purchaseInventory : purchaseInventoryList) {
        if (OrderStatus.PURCHASE_INVENTORY_DONE.equals(purchaseInventory.getStatusEnum())) {
          return purchaseInventory;
        }
      }

      return purchaseInventoryList.get(0);
    } finally {
      release(session);
    }
  }

  public List<PurchaseInventory> getPurchaseInventoryIdByPurchaseOrderIds(Long shopId, Long... purchaseOrderId) {
    Session session = getSession();
    try {
      Query q = SQL.getPurchaseInventoryIdByPurchaseOrderIds(session, shopId, purchaseOrderId);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<SalesOrderItem> getLackSalesOrderItemByProductIds(Long shopId, Long... productIds) {

    Session session = getSession();
    if (shopId == null || ArrayUtils.isEmpty(productIds)) {
      return new ArrayList<SalesOrderItem>();
    }
    try {
      Query q = SQL.getLackSalesOrderItemByProductIds(session, shopId, productIds);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<SalesOrderItem> getLackSalesOrderItemByProductIdsAndStorehouse(Long shopId, Long storehouseId, Long... productIds) {

    Session session = getSession();
    if (shopId == null || ArrayUtils.isEmpty(productIds)) {
      return new ArrayList<SalesOrderItem>();
    }
    try {
      Query q = SQL.getLackSalesOrderItemByProductIdsAndStorehouse(session, shopId, storehouseId, productIds);
      return q.list();
    } finally {
      release(session);
    }
  }

  /**
   * @param shopId
   * @param orderId
   * @param containRepeal 是否返回status为REPEAL的Payable
   * @return
   */
  public Payable getPayableDTOByOrderId(Long shopId, Long orderId, boolean containRepeal) {
    Session session = getSession();

    try {
      Query q = SQL.getPayableDTOByOrderId(session, shopId, orderId, containRepeal);
      List<Payable> payableList = (List<Payable>) q.list();
      if (CollectionUtils.isEmpty(payableList)) {
        return null;
      }

      return payableList.get(0);

    } finally {
      release(session);
    }
  }

  public List<OtherIncomeKind> vagueGetOtherIncomeKind(Long shopId, String keyWord) {
    Session session = getSession();

    try {
      Query q = SQL.vagueGetOtherIncomeKind(session, shopId, keyWord);

      return (List<OtherIncomeKind>) q.list();
    } finally {
      release(session);
    }
  }

  public OtherIncomeKind getOtherIncomeKindById(Long shopId, Long id) {
    Session session = getSession();
    try {
      Query q = SQL.getOtherIncomeKindById(session, shopId, id);

      List<OtherIncomeKind> otherIncomeKindList = (List<OtherIncomeKind>) q.list();

      if (CollectionUtils.isEmpty(otherIncomeKindList)) {
        return null;
      }

      return otherIncomeKindList.get(0);

    } finally {
      release(session);
    }
  }

  public List<OtherIncomeKind> getOtherIncomeKindByName(Long shopId, String name) {
    Session session = getSession();

    try {
      Query q = SQL.getOtherIncomeKindByName(session, shopId, name);
      return (List<OtherIncomeKind>) q.list();
    } finally {
      release(session);
    }
  }

  public List<OtherIncomeKind> getOtherIncomeKindByNames(Long shopId, Set<String> names) {
    Session session = getSession();

    try {
      Query q = SQL.getOtherIncomeKindByNames(session, shopId, names);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<SalesOrderOtherIncomeItem> getSaleOtherIncomeItemByOrderId(Long shopId, Long orderId) {
    Session session = getSession();

    try {
      Query q = SQL.getSaleOtherIncomeItemByOrderId(session, shopId, orderId);

      return (List<SalesOrderOtherIncomeItem>) q.list();
    } finally {
      release(session);
    }
  }

  public boolean deleteDraftOrderOtherIncomeItemsByDraftOrderId(Long shopId, Long draftOrderId) {
    Session session = getSession();
    try {
      Query query = SQL.deleteDraftOrderOtherIncomeItemsByDraftOrderId(session, shopId, draftOrderId);
      int count = query.executeUpdate();
      if (count > 0)
        return true;
      else
        return false;
    } finally {
      release(session);
    }
  }

  public List<DraftOrderOtherIncomeItem> getOtherIncomeItemsByDraftOrderId(Long draftOrderId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getOtherIncomeItemsByDraftOrderId(session, draftOrderId);
      return (List<DraftOrderOtherIncomeItem>) q.list();
    } finally {
      release(session);
    }
  }

  public List<RepairOrderOtherIncomeItem> getRepairOtherIncomeItemByOrderId(Long shopId, Long orderId) {
    Session session = getSession();

    try {
      Query q = SQL.getRepairOtherIncomeItemByOrderId(session, shopId, orderId);

      return (List<RepairOrderOtherIncomeItem>) q.list();
    } finally {
      release(session);
    }
  }

  public List<RepairOrderTemplateOtherIncomeItem> getRepairOrderTemplateOtherIncomeItem(Long shopId, Long templateId) {
    Session session = getSession();

    try {
      Query q = SQL.getRepairOrderTemplateOtherIncomeItem(session, shopId, templateId);

      return (List<RepairOrderTemplateOtherIncomeItem>) q.list();
    } finally {
      release(session);
    }
  }

  public List<SalesOrderOtherIncomeItem> getSalesOrderOtherIncomeItems(Long... ids) {
    Session session = this.getSession();
    try {
      Query q = SQL.getSalesOrderOtherIncomeItems(session, ids);
      return (List<SalesOrderOtherIncomeItem>) q.list();
    } finally {
      release(session);
    }
  }

  public List<RepairOrderOtherIncomeItem> getRepairOrderOtherIncomeItemDTOByShopIdAndArrayOrderId(Long shopId, Long... orderId) {
    Session session = getSession();
    try {
      Query query = SQL.getRepairOrderOtherIncomeItemDTOByShopIdAndArrayOrderId(session, shopId, orderId);
      return query.list();
    } finally {
      release(session);
    }
  }

  public List<Object[]> getSupplierHistoryOrderList(Long supplierId, Long shopId, Long startTime, Long endTime, List<String> orderTypeList, Pager pager) {
    Session session = this.getSession();
    try {
      Query q = SQL.getSupplierHistoryOrderList(session, supplierId, shopId, startTime, endTime, orderTypeList, pager);
      return q.list();
    } finally {
      release(session);
    }
  }

  public Double getSupplierTotalMoneyByTimeRangeAndOrderType(Long shopId, Long supplierId, Long startTime, Long endTime, String orderType) {
    Session session = this.getSession();
    try {
      Query q = SQL.getSupplierTotalMoneyByTimeRangeAndOrderType(session, shopId, supplierId, startTime, endTime, orderType);
      return (Double) q.uniqueResult();
    } finally {
      release(session);
    }
  }

  public ProductHistory getProductHistoryByProductLocalInfoIdAndVersions(Long productLocalInfoId, Long shopId, Long productVersion, Long productLocalInfoVersion, Long inventoryVersion) {
    Session session = getSession();
    try {
      Query q = SQL.getProductHistoryByProductLocalInfoIdAndVersions(session, productLocalInfoId, shopId, productVersion, productLocalInfoVersion, inventoryVersion);
      return (ProductHistory) CollectionUtil.getFirst(q.list());
    } finally {
      release(session);
    }
  }

  public ProductHistory getProductHistoryById(Long productHistoryId, Long shopId) {
    if (productHistoryId == null || shopId == null) {
      return null;
    }
    Session session = getSession();
    try {
      Query q = SQL.getProductHistoryById(session, productHistoryId, shopId);
      return (ProductHistory) CollectionUtil.getFirst(q.list());
    } finally {
      release(session);
    }
  }

  public ServiceHistory getServiceHistoryByIdAndVersion(Long serviceId, Long shopId, Long version) {
    Session session = getSession();
    try {
      Query q = SQL.getServiceHistoryByIdAndVersion(session, serviceId, shopId, version);
      return (ServiceHistory) CollectionUtil.getFirst(q.list());
    } finally {
      release(session);
    }
  }

  public ServiceHistory getServiceHistoryById(Long id, Long shopId) {
    Session session = getSession();
    try {
      Query q = SQL.getServiceHistoryById(session, id, shopId);
      return (ServiceHistory) CollectionUtil.getFirst(q.list());
    } finally {
      release(session);
    }
  }

  public List<RepairPicking> getRepairPicks(RepairPickingDTO searchCondition) {
    Session session = getSession();
    try {
      Query q = SQL.getRepairPicks(session, searchCondition);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<InnerPicking> getInnerPickings(InnerPickingDTO searchCondition) {
    Session session = getSession();
    try {
      Query q = SQL.getInnerPickings(session, searchCondition);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<BorrowOrder> getBorrowOrders(BorrowOrderDTO searchCondition) {
    Session session = getSession();
    try {
      Query q = SQL.getBorrowOrders(session, searchCondition);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List getBorrowOrderStat(BorrowOrderDTO searchCondition) {
    Session session = getSession();
    try {
      Query q = SQL.getBorrowOrderStat(session, searchCondition);
      return q.list();
    } finally {
      release(session);
    }
  }

  public BorrowOrder getBorrowOrderById(Long shopId, Long borrowOrderId) {
    Session session = getSession();
    try {
      Query q = SQL.getBorrowOrderById(session, shopId, borrowOrderId);
      return (BorrowOrder) q.uniqueResult();
    } finally {
      release(session);
    }
  }

  public List<BorrowOrderItem> getBorrowOrderItemByOrderId(Long shopId, Long... borrowOrderId) {
    Session session = getSession();
    try {
      Query q = SQL.getBorrowOrderItemByOrderId(session, shopId, borrowOrderId);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<BorrowOrderItem> getBorrowOrderItemByIds(Long shopId, List<Long> itemIdList) {
    Session session = getSession();
    try {
      Query q = SQL.getBorrowOrderItemByIds(session, shopId, itemIdList);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<ReturnOrder> getReturnOrderByBorrowOrderId(Long shopId, Long borrowOrderId) {
    Session session = getSession();
    try {
      Query q = SQL.getReturnOrderByBorrowOrderId(session, shopId, borrowOrderId);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<ReturnOrder> getReturnOrderByBorrowOrderIds(Long shopId, Long... borrowOrderId) {
    Session session = getSession();
    try {
      Query q = SQL.getReturnOrderByBorrowOrderIds(session, shopId, borrowOrderId);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<InnerReturn> getInnerReturns(InnerReturnDTO searchCondition) {
    Session session = getSession();
    try {
      Query q = SQL.getInnerReturns(session, searchCondition);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<InsuranceOrder> getInsuranceOrderDTOs(InsuranceOrderDTO searchCondition) {
    Session session = getSession();
    try {
      Query q = SQL.getInsuranceOrderDTOs(session, searchCondition);
      return q.list();
    } finally {
      release(session);
    }
  }

  public int countRepairPicks(RepairPickingDTO searchCondition) {
    Session session = getSession();
    try {
      Query q = SQL.countRepairPicks(session, searchCondition);
      Long count = (Long) q.uniqueResult();
      if (count == null) {
        return 0;
      } else {
        return count.intValue();
      }
    } finally {
      release(session);
    }
  }

  public int countInnerPickings(InnerPickingDTO searchCondition) {
    Session session = getSession();
    try {
      Query q = SQL.countInnerPickings(session, searchCondition);
      Long count = (Long) q.uniqueResult();
      if (count == null) {
        return 0;
      } else {
        return count.intValue();
      }
    } finally {
      release(session);
    }
  }


  public int countInnerReturns(InnerReturnDTO searchCondition) {
    Session session = getSession();
    try {
      Query q = SQL.countInnerReturns(session, searchCondition);
      Long count = (Long) q.uniqueResult();
      if (count == null) {
        return 0;
      } else {
        return count.intValue();
      }
    } finally {
      release(session);
    }
  }

  public int countBorrowOrders(BorrowOrderDTO searchCondition) {
    Session session = getSession();
    try {
      Query q = SQL.countBorrowOrders(session, searchCondition);
      Long count = (Long) q.uniqueResult();
      if (count == null) {
        return 0;
      } else {
        return count.intValue();
      }
    } finally {
      release(session);
    }
  }

  public List<BorrowOrder> getBorrowOrderByBorrower(Long shopId, String borrower) {
    Session session = getSession();
    try {
      Query q = SQL.getBorrowOrderByBorrower(session, shopId, borrower);
      return q.list();
    } finally {
      release(session);
    }
  }

  public int countInsuranceOrderDTOs(InsuranceOrderDTO searchCondition) {
    Session session = getSession();
    try {
      Query q = SQL.countInsuranceOrderDTOs(session, searchCondition);
      Long count = (Long) q.uniqueResult();
      if (count == null) {
        return 0;
      } else {
        return count.intValue();
      }
    } finally {
      release(session);
    }
  }

  public Double sumInsuranceOrderClaims(InsuranceOrderDTO searchCondition) {
    Session session = getSession();
    try {
      Query q = SQL.sumInsuranceOrderClaims(session, searchCondition);
      Object count = q.uniqueResult();
      if (count == null) {
        return 0d;
      } else {
        return Double.valueOf(String.valueOf(count));
      }
    } finally {
      release(session);
    }
  }

  public int sumInnerPickings(Long shopId) {
    Session session = getSession();
    try {
      Query q = SQL.sumInnerPickings(session, shopId);
      Long count = (Long) q.uniqueResult();
      if (count == null) {
        return 0;
      } else {
        return count.intValue();
      }
    } finally {
      release(session);
    }
  }

  public int sumInnerReturns(Long shopId) {
    Session session = getSession();
    try {
      Query q = SQL.sumInnerReturns(session, shopId);
      Long count = (Long) q.uniqueResult();
      if (count == null) {
        return 0;
      } else {
        return count.intValue();
      }
    } finally {
      release(session);
    }
  }

  public int sumInsuranceOrderDTOs(Long shopId) {
    Session session = getSession();
    try {
      Query q = SQL.sumInsuranceOrderDTOs(session, shopId);
      Long count = (Long) q.uniqueResult();
      if (count == null) {
        return 0;
      } else {
        return count.intValue();
      }
    } finally {
      release(session);
    }
  }

  public List<RepairPickingItem> getRepairPickingItemsByOrderIds(Long... orderIds) {
    Session session = getSession();
    try {
      Query q = SQL.getRepairPickingItemsByOrderIds(session, orderIds);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<RepairPicking> getRepairPicksByIds(Long shopId, Long... ids) {
    Session session = getSession();
    try {
      Query q = SQL.getRepairPicksByIds(session, shopId, ids);
      return q.list();
    } finally {
      release(session);
    }
  }

  public RepairPicking getRepairPicksById(Long shopId, Long id) {
    return CollectionUtil.uniqueResult(getRepairPicksByIds(shopId, id));
  }

  public List<Inventory> getInventoryDTOsByShopId(Long shopId, int start, int rows) {
    Session session = this.getSession();
    try {
      Query query = SQL.getInventoryDTOsByShopId(session, shopId, start, rows);
      return (List<Inventory>) query.list();
    } finally {
      release(session);
    }
  }

  public List<StoreHouse> searchStoreHouses(Long shopId, int start, int pageSize) {
    Session session = this.getSession();
    try {
      Query query = SQL.searchStoreHouses(session, shopId, start, pageSize);
      return (List<StoreHouse>) query.list();
    } finally {
      release(session);
    }
  }


  public int countStoreHouses(Long shopId) {
    Session session = this.getSession();
    try {
      Query hql = SQL.countStoreHouses(session, shopId);
      Object o = hql.uniqueResult();
      if (o == null) return 0;
      return Integer.valueOf(o.toString());
    } finally {
      release(session);
    }
  }

  public int countStoreHousesByName(Long shopId, StoreHouseDTO storeHouseDTO) {
    Session session = this.getSession();
    try {
      Query hql = SQL.countStoreHousesByName(session, shopId, storeHouseDTO);
      Object o = hql.uniqueResult();
      if (o == null) return 0;
      return Integer.valueOf(o.toString());
    } finally {
      release(session);
    }
  }

  public int countProcessingPurchaseInventoryOrdersUseStoreHouseByStorehouseId(Long shopId, Long storehouseId) {
    Session session = this.getSession();
    try {
      Query hql = SQL.countProcessingPurchaseInventoryOrdersUseStoreHouseByStorehouseId(session, shopId, storehouseId);
      Object o = hql.uniqueResult();
      if (o == null) return 0;
      return Integer.valueOf(o.toString());
    } finally {
      release(session);
    }
  }

  public int countProcessingRelatedPurchaseOrders(Long customerShopId, Long supplierShopId) {
    Session session = this.getSession();
    try {
      Query hql = SQL.countProcessingRelatedPurchaseOrders(session, customerShopId, supplierShopId);
      Object o = hql.uniqueResult();
      if (o == null) return 0;
      return Integer.valueOf(o.toString());
    } finally {
      release(session);
    }
  }

  public List<PurchaseOrder> getProcessingRelatedPurchaseOrders(Long customerShopId, Long supplierShopId) {
    Session session = this.getSession();
    if (customerShopId == null || supplierShopId == null) {
      return new ArrayList<PurchaseOrder>();
    }
    try {
      Query query = SQL.getProcessingRelatedPurchaseOrders(session, customerShopId, supplierShopId);
      return query.list();
    } finally {
      release(session);
    }
  }

  public int countProcessingRelatedPurchaseReturnOrders(Long customerShopId, Long supplierShopId) {
    Session session = this.getSession();
    try {
      Query hql = SQL.countProcessingRelatedPurchaseReturnOrders(session, customerShopId, supplierShopId);
      Object o = hql.uniqueResult();
      if (o == null) return 0;
      return Integer.valueOf(o.toString());
    } finally {
      release(session);
    }
  }

  public List<PurchaseReturn> getProcessingRelatedPurchaseReturnOrders(Long customerShopId, Long supplierShopId) {
    if (customerShopId == null || supplierShopId == null) {
      return new ArrayList<PurchaseReturn>();
    }
    Session session = this.getSession();
    try {
      Query q = SQL.getProcessingRelatedPurchaseReturnOrders(session, customerShopId, supplierShopId);
      return q.list();
    } finally {
      release(session);
    }
  }

  public int countProcessingRelatedSalesOrders(Long customerShopId, Long supplierShopId) {
    Session session = this.getSession();
    try {
      Query hql = SQL.countProcessingRelatedSalesOrders(session, customerShopId, supplierShopId);
      Object o = hql.uniqueResult();
      if (o == null) return 0;
      return Integer.valueOf(o.toString());
    } finally {
      release(session);
    }
  }

  public int countProcessingRelatedSalesReturnOrders(Long customerShopId, Long supplierShopId) {
    Session session = this.getSession();
    try {
      Query hql = SQL.countProcessingRelatedSalesReturnOrders(session, customerShopId, supplierShopId);
      Object o = hql.uniqueResult();
      if (o == null) return 0;
      return Integer.valueOf(o.toString());
    } finally {
      release(session);
    }
  }

  public int countProcessingPurchaseReturnOrdersUseStoreHouseByStorehouseId(Long shopId, Long storehouseId) {
    Session session = this.getSession();
    try {
      Query hql = SQL.countProcessingPurchaseReturnOrdersUseStoreHouseByStorehouseId(session, shopId, storehouseId);
      Object o = hql.uniqueResult();
      if (o == null) return 0;
      return Integer.valueOf(o.toString());
    } finally {
      release(session);
    }
  }

  public int countProcessingRepairOrderOrdersUseStoreHouseByStorehouseId(Long shopId, Long storehouseId) {
    Session session = this.getSession();
    try {
      Query hql = SQL.countProcessingRepairOrderOrdersUseStoreHouseByStorehouseId(session, shopId, storehouseId);
      Object o = hql.uniqueResult();
      if (o == null) return 0;
      return Integer.valueOf(o.toString());
    } finally {
      release(session);
    }
  }

  public int countProcessingSalesOrderOrdersUseStoreHouseByStorehouseId(Long shopId, Long storehouseId) {
    Session session = this.getSession();
    try {
      Query hql = SQL.countProcessingSalesOrderOrdersUseStoreHouseByStorehouseId(session, shopId, storehouseId);
      Object o = hql.uniqueResult();
      if (o == null) return 0;
      return Integer.valueOf(o.toString());
    } finally {
      release(session);
    }
  }

  public int countProcessingSalesReturnOrdersUseStoreHouseByStorehouseId(Long shopId, Long storehouseId) {
    Session session = this.getSession();
    try {
      Query hql = SQL.countProcessingSalesReturnOrdersUseStoreHouseByStorehouseId(session, shopId, storehouseId);
      Object o = hql.uniqueResult();
      if (o == null) return 0;
      return Integer.valueOf(o.toString());
    } finally {
      release(session);
    }
  }

  public int countProcessingRepairPickingUseStoreHouseByStorehouseId(Long shopId, Long storehouseId) {
    Session session = this.getSession();
    try {
      Query hql = SQL.countProcessingRepairPickingsUseStoreHouseByStorehouseId(session, shopId, storehouseId);
      Object o = hql.uniqueResult();
      if (o == null) return 0;
      return Integer.valueOf(o.toString());
    } finally {
      release(session);
    }
  }

  public int countProcessingRepairPickingUseByShopId(Long shopId) {
    Session session = this.getSession();
    try {
      Query hql = SQL.countProcessingRepairPickingUseByShopId(session, shopId);
      Object o = hql.uniqueResult();
      if (o == null) return 0;
      return Integer.valueOf(o.toString());
    } finally {
      release(session);
    }
  }

  public int countProcessingRepairOrderUseRepairPickingByShopId(Long shopId) {
    Session session = this.getSession();
    try {
      Query hql = SQL.countProcessingRepairOrderUseRepairPickingByShopId(session, shopId);
      Object o = hql.uniqueResult();
      if (o == null) return 0;
      return Integer.valueOf(o.toString());
    } finally {
      release(session);
    }
  }

  public int countProcessingRepairOrderUseMaterialByShopId(Long shopId) {
    Session session = this.getSession();
    try {
      Query hql = SQL.countProcessingRepairOrderUseMaterialByShopId(session, shopId);
      Object o = hql.uniqueResult();
      if (o == null) return 0;
      return Integer.valueOf(o.toString());
    } finally {
      release(session);
    }
  }

  public StoreHouseDTO getStoreHouseDTOById(Long shopId, Long id) {
    Session session = getSession();
    try {
      Query query = SQL.getStoreHouseById(session, shopId, id);
      List<StoreHouse> storeHouseList = (List<StoreHouse>) query.list();
      if (CollectionUtils.isNotEmpty(storeHouseList)) {
        return storeHouseList.get(0).toDTO();
      }
      return null;
    } finally {
      release(session);
    }
  }

  public List<StoreHouse> getAllStoreHousesByShopId(Long shopId) {
    Session session = this.getSession();
    try {
      Query query = SQL.getAllStoreHousesByShopId(session, shopId);
      return (List<StoreHouse>) query.list();
    } finally {
      release(session);
    }
  }

  public Double sumStoreHouseAllInventoryAmountByStoreHouseId(Long shopId, Long storeHouseId) {
    Session session = this.getSession();
    try {
      Query hql = SQL.sumStoreHouseAllInventoryAmountByStoreHouseId(session, shopId, storeHouseId);
      Object o = hql.uniqueResult();
      if (o == null) return 0D;
      return Double.valueOf(o.toString());
    } finally {
      release(session);
    }
  }

  public Double sumStoreHouseAllInventoryAmountByProductLocalInfoId(Long shopId, Long productLocalInfoId) {
    Session session = this.getSession();
    try {
      Query hql = SQL.sumStoreHouseAllInventoryAmountByProductLocalInfoId(session, shopId, productLocalInfoId);
      Object o = hql.uniqueResult();
      if (o == null) return 0D;
      return Double.valueOf(o.toString());
    } finally {
      release(session);
    }
  }

  public List<AllocateRecord> searchAllocateRecords(AllocateRecordSearchConditionDTO allocateRecordSearchConditionDTO) {
    Session session = this.getSession();
    try {
      Query query = SQL.searchAllocateRecords(session, allocateRecordSearchConditionDTO);
      return (List<AllocateRecord>) query.list();
    } finally {
      release(session);
    }
  }

  public int countAllocateRecords(AllocateRecordSearchConditionDTO allocateRecordSearchConditionDTO) {
    Session session = this.getSession();
    try {
      Query hql = SQL.countAllocateRecords(session, allocateRecordSearchConditionDTO);
      Object o = hql.uniqueResult();
      if (o == null) return 0;
      return Integer.valueOf(o.toString());
    } finally {
      release(session);
    }
  }

  public AllocateRecordDTO getAllocateRecordDTOById(Long shopId, Long id) {
    Session session = getSession();
    try {
      Query query = SQL.getAllocateRecordById(session, shopId, id);
      List<AllocateRecord> allocateRecordList = (List<AllocateRecord>) query.list();
      if (CollectionUtils.isNotEmpty(allocateRecordList)) {
        return allocateRecordList.get(0).toDTO();
      }
      return null;
    } finally {
      release(session);
    }
  }

  public List<StoreHouseInventory> getStoreHouseInventory(Long storehouseId, Long productLocalInfoId) {
    Session session = this.getSession();
    try {
      Query query = SQL.getStoreHouseInventoryDTO(session, storehouseId, productLocalInfoId);
      return (List<StoreHouseInventory>) query.list();
    } finally {
      release(session);
    }
  }

  public List<StoreHouseInventory> getStoreHouseInventoryByStorehouseAndProductIds(Long shopId, Long storehouseId, Long... productLocalInfoId) {
    Session session = this.getSession();
    try {
      Query query = SQL.getStoreHouseInventoryByStorehouseAndProductIds(session, shopId, storehouseId, productLocalInfoId);
      return (List<StoreHouseInventory>) query.list();
    } finally {
      release(session);
    }
  }

  public double sumStoreHouseInventoryInOtherStorehouseByProductIds(Long shopId, Long excludeStorehouseId, Long... productLocalInfoId) {
    Session session = this.getSession();
    try {
      Query query = SQL.sumStoreHouseInventoryInOtherStorehouseByProductIds(session, shopId, excludeStorehouseId, productLocalInfoId);
      Object o = query.uniqueResult();
      if (o == null) return 0;
      return Double.valueOf(o.toString());
    } finally {
      release(session);
    }
  }

  public List<StoreHouseInventory> getStoreHouseInventoryByProductIds(Long shopId, Long... productLocalInfoId) {
    Session session = this.getSession();
    try {
      Query query = SQL.getStoreHouseInventoryByProductIds(session, shopId, productLocalInfoId);
      return (List<StoreHouseInventory>) query.list();
    } finally {
      release(session);
    }
  }

  public List<AllocateRecordItemDTO> getAllocateRecordItemDTOByAllocateRecordId(Long allocateRecordId) {
    Session session = getSession();
    try {
      Query query = SQL.getAllocateRecordItemByAllocateRecordId(session, allocateRecordId);
      List<AllocateRecordItem> allocateRecordItemList = (List<AllocateRecordItem>) query.list();
      if (CollectionUtils.isNotEmpty(allocateRecordItemList)) {
        List<AllocateRecordItemDTO> allocateRecordItemDTOList = new ArrayList<AllocateRecordItemDTO>();
        for (AllocateRecordItem allocateRecordItem : allocateRecordItemList) {
          allocateRecordItemDTOList.add(allocateRecordItem.toDTO());
        }
        return allocateRecordItemDTOList;
      }
      return null;
    } finally {
      release(session);
    }
  }

  public List<ProductHistory> getProductHistoryByProductHistoryIds(Set<Long> productHistoryIds) {
    Session session = getSession();
    try {
      Query q = SQL.getProductHistoryByProductHistoryIds(session, productHistoryIds);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<ProductHistory> getProductHistoryByProductIds(Set<Long> productIds) {
    Session session = getSession();
    try {
      Query q = SQL.getProductHistoryByProductIds(session, productIds);
      return q.list();
    } finally {
      release(session);
    }
  }


  public List<ProductHistory> getProductHistoryByProductHistoryDTOs(Collection<ProductHistory> productHistories) throws Exception {
    long begin = System.currentTimeMillis();
    LOG.debug("AOP_SQL start:TxnWriter:getProductHistoryByProductHistoryDTOs");
    Session session = getSession();
    try {
      List<ProductHistory> productHistoryArray = new ArrayList<ProductHistory>(productHistories);
      List<ProductHistory> returnProductHistory = new ArrayList<ProductHistory>();
      int pageSize = 1;
      int pageNo = 1;
      int totalRows = productHistories.size();
      Pager pager = new Pager(totalRows, pageNo, pageSize);
      do {
        List<ProductHistory> pageSearchProductHistory = new ArrayList<ProductHistory>();
        for (int i = pager.getRowStart(); i < pager.getRowStart() + pageSize && i < totalRows; i++) {
          pageSearchProductHistory.add(productHistoryArray.get(i));
        }
        if (CollectionUtils.isNotEmpty(pageSearchProductHistory)) {
          Query query = SQL.getProductHistoriesByProductLocalInfoIdAndVersions(session, pageSearchProductHistory);
          returnProductHistory = (ArrayList<ProductHistory>) query.list();
          if (CollectionUtils.isNotEmpty(returnProductHistory)) {
            returnProductHistory.addAll(returnProductHistory);
          }
        } else {
          break;
        }
        if (pager.getIsLastPage()) {
          break;
        } else {
          pageNo++;
          pager = new Pager(totalRows, pageNo, pageSize);
        }
      } while (true);
      return returnProductHistory;
    } finally {
      LOG.debug("AOP_SQL end:TxnWriter:getProductHistoryByProductHistoryDTOs 用时：{}ms", System.currentTimeMillis() - begin);
      release(session);
    }
  }

  public List<ServiceHistory> getServiceHistoryByServiceHistoryIdSet(Long shopId, Set<Long> serviceHistoryIds) {
    Session session = this.getSession();
    try {
      Query query = SQL.getServiceHistoryByServiceHistoryIdSet(session, shopId, serviceHistoryIds);
      return query.list();
    } finally {
      release(session);
    }
  }

  public RepairPicking getRepairPickingByRepairOrderId(Long shopId, Long repairOrderId) {
    Session session = getSession();
    try {
      Query q = SQL.getRepairPickingByRepairOrderId(session, shopId, repairOrderId);
      List<RepairPicking> repairPickings = q.list();

      if (CollectionUtils.isNotEmpty(repairPickings)) {
        return repairPickings.get(0);
      }
      return null;
    } finally {
      release(session);
    }
  }

  public List<RepairPicking> getRepairPickingsByRepairOrderIds(Long shopId, Long... repairOrderId) {
    Session session = getSession();
    try {
      Query q = SQL.getRepairPickingsByRepairOrderIds(session, shopId, repairOrderId);
      return (List<RepairPicking>) q.list();
    } finally {
      release(session);
    }
  }

  /**
   * 施工单 销售单  入库单   入库退货单   销售退货单 库存盘点单
   *
   * @param shopId
   */
  public void updateAllOrderStoreHouseByStorehouse(Long shopId, StoreHouse storeHouse) {
    Session session = getSession();
    try {
      Query query = SQL.updateRepairOrderStorehouse(session, shopId, storeHouse.getId(), storeHouse.getName());
      query.executeUpdate();

      query = SQL.updatePurchaseInventoryOrderStorehouse(session, shopId, storeHouse.getId(), storeHouse.getName());
      query.executeUpdate();

      query = SQL.updateSalesReturnOrderStorehouse(session, shopId, storeHouse.getId(), storeHouse.getName());
      query.executeUpdate();

      query = SQL.updatePurchaseReturnOrderStorehouse(session, shopId, storeHouse.getId(), storeHouse.getName());
      query.executeUpdate();

      query = SQL.updateSalesOrderStorehouse(session, shopId, storeHouse.getId(), storeHouse.getName());
      query.executeUpdate();

      query = SQL.updateInventoryCheckOrderStorehouse(session, shopId, storeHouse.getId(), storeHouse.getName());
      query.executeUpdate();
    } finally {
      release(session);
    }
  }

  public InnerPicking getInnerPickingById(Long shopId, Long innerPickingId) {
    Session session = getSession();
    try {
      Query q = SQL.getInnerPickingById(session, shopId, innerPickingId);
      List<InnerPicking> innerPickings = q.list();
      if (CollectionUtils.isNotEmpty(innerPickings)) {
        return innerPickings.get(0);
      }
      return null;
    } finally {
      release(session);
    }
  }

  public InnerReturn getInnerReturnById(Long shopId, Long innerReturnId) {
    Session session = getSession();
    try {
      Query q = SQL.getInnerReturnById(session, shopId, innerReturnId);
      List<InnerReturn> innerReturns = q.list();
      if (CollectionUtils.isNotEmpty(innerReturns)) {
        return innerReturns.get(0);
      }
      return null;
    } finally {
      release(session);
    }
  }

  public List<InnerPickingItem> getInnerPickingItemsByInnerPickingId(Long... innerPickingId) {
    Session session = getSession();
    try {
      Query q = SQL.getInnerPickingItemsByInnerPickingId(session, innerPickingId);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<InnerReturnItem> getInnerReturnItemsByInnerReturnId(Long... innerReturnId) {
    Session session = getSession();
    try {
      Query q = SQL.getInnerReturnItemsByInnerReturnId(session, innerReturnId);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<ReturnOrderItem> getReturnOrderItemsByOrderIds(Long shopId, Long... orderIds) {
    Session session = getSession();
    try {
      Query q = SQL.getReturnOrderItemsByOrderIds(session, shopId, orderIds);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<InsuranceOrder> getInsuranceOrderByPolicyNo(Long shopId, String policyNo) {
    if (shopId == null || StringUtils.isBlank(policyNo)) {
      return new ArrayList<InsuranceOrder>();
    }
    Session session = getSession();
    try {
      Query q = SQL.getInsuranceOrderByPolicyNo(session, shopId, policyNo);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<InsuranceOrder> getInsuranceOrderByReportNo(Long shopId, String reportNo) {
    if (shopId == null || StringUtils.isBlank(reportNo)) {
      return new ArrayList<InsuranceOrder>();
    }
    Session session = getSession();
    try {
      Query q = SQL.getInsuranceOrderByReportNo(session, shopId, reportNo);
      return q.list();
    } finally {
      release(session);
    }
  }

  public InsuranceOrder getInsuranceOrderById(Long shopId, Long id) {
    if (shopId == null || id == null) {
      return null;
    }
    Session session = getSession();
    try {
      Query q = SQL.getInsuranceOrderById(session, shopId, id);
      List<InsuranceOrder> insuranceOrders = q.list();
      return CollectionUtil.uniqueResult(insuranceOrders);
    } finally {
      release(session);
    }
  }

  public Long getInsuranceOrderIdByRepairOrderId(Long shopId, Long repairOrderId) {
    if (shopId == null || repairOrderId == null) {
      return null;
    }
    Session session = getSession();
    try {
      Query q = SQL.getInsuranceOrderIdByRepairOrderId(session, shopId, repairOrderId);
      return CollectionUtil.<Long>uniqueResult(q.list());
    } finally {
      release(session);
    }
  }

  public List<InsuranceOrder> getInsuranceOrderByRepairOrderId(Long shopId, Long repairOrderId) {
    List<InsuranceOrder> insuranceOrders = new ArrayList<InsuranceOrder>();
    if (shopId == null || repairOrderId == null) {
      return insuranceOrders;
    }
    Session session = getSession();
    try {
      Query q = SQL.getInsuranceOrderByRepairOrderId(session, shopId, repairOrderId);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<InsuranceOrder> getInsuranceOrderByRepairDraftOrderId(Long shopId, Long repairDraftOrderId) {
    List<InsuranceOrder> insuranceOrders = new ArrayList<InsuranceOrder>();
    if (shopId == null || repairDraftOrderId == null) {
      return insuranceOrders;
    }
    Session session = getSession();
    try {
      Query q = SQL.getInsuranceOrderByRepairDraftOrderId(session, shopId, repairDraftOrderId);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<InsuranceOrderService> getInsuranceOrderServiceByOrderId(Long shopId, Long id) {
    List<InsuranceOrderService> insuranceOrderServices = new ArrayList<InsuranceOrderService>();
    if (shopId == null || id == null) {
      return insuranceOrderServices;
    }
    Session session = getSession();
    try {
      Query q = SQL.getInsuranceOrderServiceByOrderId(session, shopId, id);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<InsuranceOrderItem> getInsuranceOrderItemByOrderId(Long shopId, Long id) {
    List<InsuranceOrderItem> insuranceOrderItems = new ArrayList<InsuranceOrderItem>();
    if (shopId == null || id == null) {
      return insuranceOrderItems;
    }
    Session session = getSession();
    try {
      Query q = SQL.getInsuranceOrderItemByOrderId(session, shopId, id);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<Receivable> getMemberCardConsumeByMemberId(Long memberId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getMemberCardConsumeByMemberId(session, memberId);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<PurchaseInventoryItemDTO> getPurchaseInventoryItemByProductIdVestDate(Long shopId, Long productId, Long vestDate) {
    Session session = this.getSession();
    try {
      Query q = SQL.getPurchaseInventoryItemByProductIdVestDate(session, shopId, productId, vestDate);
      if (q == null) {
        return null;
      }
      List<PurchaseInventoryItemDTO> purchaseInventoryItemDTOList = new ArrayList<PurchaseInventoryItemDTO>();
      List<Object[]> list = q.list();
      if (CollectionUtils.isEmpty(list)) {
        return null;
      }
      for (Object[] array : list) {
        if (!ArrayUtils.isEmpty(array) && array.length == 5) {
          PurchaseInventoryItemDTO purchaseInventoryItemDTO = new PurchaseInventoryItemDTO();
          purchaseInventoryItemDTO.setAmount(array[0] == null ? 0D : (Double) array[0]);
          purchaseInventoryItemDTO.setPrice(array[1] == null ? 0D : (Double) array[1]);
          purchaseInventoryItemDTO.setTotal(array[2] == null ? 0D : (Double) array[2]);
          purchaseInventoryItemDTO.setVestDate(array[3] == null ? null : (Long) array[3]);
          purchaseInventoryItemDTO.setProductId(array[4] == null ? null : (Long) array[4]);
          purchaseInventoryItemDTOList.add(purchaseInventoryItemDTO);
        }
      }
      return purchaseInventoryItemDTOList;
    } finally {
      release(session);
    }
  }

  public List<Long> countStatDateByNormalProductIds(Long[] shopIds, Long[] normalProductIds, NormalProductStatType normalProductStatType) {

    Session session = getSession();
    try {
      Query query = SQL.countStatDateByNormalProductIds(session, shopIds, normalProductIds, normalProductStatType);
      return query.list();
    } finally {
      release(session);
    }
  }

  public List<NormalProductInventoryStat> getStatDateByNormalProductIds(Long[] shopIds, Long[] normalProductIds, NormalProductStatType normalProductStatType) {
    Session session = getSession();
    try {
      Query query = SQL.getStatDateByNormalProductIds(session, shopIds, normalProductIds, normalProductStatType);
      return (List<NormalProductInventoryStat>) query.list();
    } finally {
      release(session);
    }
  }

  public void deleteAllNormalProductStat() {
    Session session = getSession();
    try {
      Query query = SQL.deleteAllNormalProductStat(session);
      query.executeUpdate();
    } finally {
      release(session);
    }
  }


  public int countSupplierReturnPayable() {
    Session session = this.getSession();

    try {
      Query q = SQL.countSupplierReturnPayable(session);

      Object o = q.uniqueResult();
      if (o == null) return 0;
      return Integer.parseInt(o.toString());
    } finally {
      release(session);
    }
  }

  public List<SupplierReturnPayable> getSupplierReturnPayable(int size) {
    Session session = this.getSession();

    try {
      Query q = SQL.getSupplierReturnPayable(session, size);

      return (List<SupplierReturnPayable>) q.list();
    } finally {
      release(session);
    }
  }

  public List<SupplierReturnPayable> getSupplierReturnPayableByIds(List<Long> ids) {
    Session session = this.getSession();

    try {
      Query q = SQL.getSupplierReturnPayableByIds(session, ids);

      return (List<SupplierReturnPayable>) q.list();
    } finally {
      release(session);
    }
  }

  public void updateMovedSupplierReturnPayable(List<Long> ids) {
    Session session = this.getSession();

    try {
      Query q = SQL.updateMovedSupplierReturnPayable(session, ids);

      q.executeUpdate();
    } finally {
      release(session);
    }
  }

  public QualifiedCredentials getQualifiedCredentialsDTO(Long shopId, Long orderId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getQualifiedCredentialsDTO(session, shopId, orderId);
      List<QualifiedCredentials> qualifiedCredentialsList = (List<QualifiedCredentials>) q.list();
      if (CollectionUtils.isEmpty(qualifiedCredentialsList)) {
        return null;
      }
      return qualifiedCredentialsList.get(0);
    } finally {
      release(session);
    }
  }

  public StatementAccountOrderDTO getLastStatementAccountOrder(Long shopId, Long customerOrSupplierId, OrderTypes orderType) {
    Session session = getSession();
    try {
      Query q = SQL.getLastStatementAccountOrder(session, shopId, customerOrSupplierId, orderType);
      List<StatementAccountOrder> list = q.list();
      if (CollectionUtils.isEmpty(list)) {
        return null;
      }
      return list.get(0).toDTO();
    } finally {
      release(session);
    }
  }

  public List<Receivable> getReceivableListByCustomerId(Long shopId, Long customerId, Long startDate, Long endDate) {
    Session session = getSession();
    try {
      Query q = SQL.getReceivableListByCustomerId(session, shopId, customerId, startDate, endDate);
      List<Receivable> list = q.list();
      return list;
    } finally {
      release(session);
    }
  }

  public List<Payable> getPayableListBySupplierId(Long shopId, Long supplierId, Long startDate, Long endDate) {
    Session session = getSession();
    try {
      Query q = SQL.getPayableListBySupplierId(session, shopId, supplierId, startDate, endDate);
      List<Payable> list = q.list();
      return list;
    } finally {
      release(session);
    }
  }


  public List<String> getTotalDebtByOrderIds(Long shopId, OrderTypes orderType, Long[] orderIds) {
    Session session = getSession();
    try {
      Query q = SQL.getTotalDebtByOrderIds(session, shopId, orderType, orderIds);
      if (q == null) {
        return null;
      }
      List<Object> list = q.list();
      List<String> stringList = new ArrayList<String>();
      if (CollectionUtils.isNotEmpty(list)) {
        Object[] array = (Object[]) list.get(0);
        if (array[0] != null && array[1] != null) {
          stringList.add(array[0].toString());
          stringList.add(array[1].toString());
        }
      }
      if (CollectionUtils.isEmpty(stringList)) {
        stringList.add("0");
        stringList.add("0");
      }
      return stringList;
    } finally {
      release(session);
    }
  }

  public List<StatementAccountOrder> getStatementAccountOrderList(OrderSearchConditionDTO orderSearchConditionDTO, Pager pager) {
    Session session = getSession();
    try {
      Query q = SQL.getStatementAccountOrderList(session, orderSearchConditionDTO, pager);
      return (List<StatementAccountOrder>) q.list();
    } finally {
      release(session);
    }
  }

  public int countStatementAccountOrderList(OrderSearchConditionDTO orderSearchConditionDTO) {
    Session session = getSession();
    try {
      Query q = SQL.countStatementAccountOrderList(session, orderSearchConditionDTO);
      Object o = q.uniqueResult();
      if (o == null) return 0;
      return Integer.valueOf(o.toString());
    } finally {
      release(session);
    }
  }


  public List<StatementAccountOrder> getOperatorByCustomerOrSupplierId(Long shopId, Long customerOrSupplierId, String salesMan) {
    Session session = getSession();
    try {
      Query q = SQL.getOperatorByCustomerOrSupplierId(session, shopId, customerOrSupplierId, salesMan);
      return (List<StatementAccountOrder>) q.list();
    } finally {
      release(session);
    }
  }

  public Map<Long, PurchaseReturnDTO> getPurchaseReturnByPurchaseReturnId(Long... id) {
    Session session = this.getSession();
    try {
      Query query = SQL.getPurchaseReturnByPurchaseReturnId(session, id);
      List<PurchaseReturn> purchaseReturnList = query.list();
      if (CollectionUtils.isEmpty(purchaseReturnList)) return new HashMap<Long, PurchaseReturnDTO>();
      Map<Long, PurchaseReturnDTO> map = new HashMap<Long, PurchaseReturnDTO>();
      for (PurchaseReturn purchaseReturn : purchaseReturnList) {
        map.put(purchaseReturn.getId(), purchaseReturn.toDTO());
      }
      return map;
    } finally {
      release(session);
    }
  }

  public List<RepairOrderDTO> getUnsettledRepairOrderByServiceId(Long shopId, Long serviceId) {
    Session session = getSession();
    try {
      Query query = SQL.getUnsettledRepairOrderByServiceId(session, shopId, serviceId);
      List<RepairOrder> repairOrderList = query.list();
      List<RepairOrderDTO> repairOrderDTOs = new ArrayList<RepairOrderDTO>();
      if (CollectionUtils.isNotEmpty(repairOrderList)) {
        for (RepairOrder repairOrder : repairOrderList) {
          repairOrderDTOs.add(repairOrder.toDTO());
        }
      }
      return repairOrderDTOs;
    } finally {
      release(session);
    }
  }

  public List<Receivable> getReceivableListByStatementOrderId(Long shopId, Long statementOrderId) {
    Session session = getSession();
    try {
      Query q = SQL.getReceivableListByStatementOrderId(session, shopId, statementOrderId);
      return (List<Receivable>) q.list();
    } finally {
      release(session);
    }
  }

  public List<Payable> getPayableListByStatementOrderId(Long shopId, Long statementOrderId) {
    Session session = getSession();
    try {
      Query q = SQL.getPayableListByStatementOrderId(session, shopId, statementOrderId);
      return (List<Payable>) q.list();
    } finally {
      release(session);
    }
  }

  public List<ReceptionRecord> getReceptionRecordBySopId(Long shopId, Pager pager, List statusList) {
    Session session = getSession();
    try {
      Query q = SQL.getReceptionRecordBySopId(session, shopId, pager, statusList);
      return (List<ReceptionRecord>) q.list();
    } finally {
      release(session);
    }
  }


  public int countReceptionRecordBySopId(Long shopId, List statusList) {
    Session session = getSession();
    try {
      Query q = SQL.countReceptionRecordBySopId(session, shopId, statusList);
      Object o = q.uniqueResult();
      if (o == null) return 0;
      return Integer.valueOf(o.toString());
    } finally {
      release(session);
    }
  }

  public List<StatementAccountOrderDTO> getStatementAccountOrderByShopIdAndOrderIds(Long shopId, Long... orderIds) {
    Session session = this.getSession();
    try {
      Query query = SQL.getStatementAccountOrderByShopIdAndOrderIds(session, shopId, orderIds);
      List<StatementAccountOrder> statementAccountOrderList = query.list();
      if (CollectionUtils.isEmpty(statementAccountOrderList)) return new ArrayList<StatementAccountOrderDTO>();
      List<StatementAccountOrderDTO> statementAccountOrderDTOList = new ArrayList<StatementAccountOrderDTO>();
      for (StatementAccountOrder statementAccountOrder : statementAccountOrderList) {
        if (statementAccountOrder == null) continue;
        statementAccountOrderDTOList.add(statementAccountOrder.toDTO());
      }
      return statementAccountOrderDTOList;
    } finally {
      release(session);
    }
  }

  public List<Long> getStatementAccountOrderIds(Long shopId, int start, int pageSize) {
    Session session = this.getSession();
    try {
      Query query = SQL.getStatementAccountOrderIds(session, shopId, start, pageSize);
      return query.list();
    } finally {
      release(session);
    }
  }


  public double getSupplierTotalDebtByShopId(long shopId, OrderDebtType type) {
    Session session = this.getSession();
    try {
      Query q = SQL.getSupplierTotalDebtByShopId(session, shopId, type);
      Double sum = (Double) q.uniqueResult();
      if (sum == null) {
        return 0.0;
      }
      return sum.doubleValue();
    } finally {
      release(session);
    }
  }


  public List<InventoryCheck> getInventoryChecks(InventoryCheckDTO inventoryCheckIndex) {
    Session session = this.getSession();
    try {
      Query query = SQL.getInventoryChecks(session, inventoryCheckIndex);
      return query.list();
    } finally {
      release(session);
    }
  }

  public List<InventoryCheck> getInventoryCheckByIds(Long shopId, Set<Long> orderIds, Pager pager) {
    Session session = this.getSession();
    try {
      Query query = SQL.getInventoryCheckByIds(session, shopId, orderIds, pager);
      return query.list();
    } finally {
      release(session);
    }
  }

  public InventoryCheck getInventoryCheckById(Long shopId, Long inventoryCheckId) {
    Session session = this.getSession();
    try {
      Query query = SQL.getInventoryCheckById(session, shopId, inventoryCheckId);
      return (InventoryCheck) query.uniqueResult();
    } finally {
      release(session);
    }
  }

  public List<InventoryCheckItem> getInventoryCheckItem(Long inventoryCheckId) {
    Session session = this.getSession();
    try {
      Query query = SQL.getInventoryCheckItem(session, inventoryCheckId);
      return query.list();
    } finally {
      release(session);
    }
  }


  public Double getStockAdjustPriceTotal(Long shopId) {
    Session session = this.getSession();
    try {
      Query query = SQL.getStockAdjustPriceTotal(session, shopId);
      Object total = query.uniqueResult();
      if (total == null) total = 0;
      return Double.valueOf(total.toString());
    } finally {
      release(session);
    }
  }

  public int getInventoryCheckCount(InventoryCheckDTO inventoryCheckIndex) {
    Session session = this.getSession();
    try {
      Query query = SQL.getInventoryCheckCount(session, inventoryCheckIndex);
      return Integer.valueOf(query.uniqueResult().toString());
    } finally {
      release(session);
    }
  }

  public List<RepairRemindEvent> getAllRepairRemindEvent() {
    Session session = this.getSession();
    try {
      Query q = SQL.getAllRepairRemindEvent(session);
      return (List<RepairRemindEvent>) q.list();
    } finally {
      release(session);
    }
  }

  public List<Debt> getAllDebt() {
    Session session = this.getSession();
    try {
      Query q = SQL.getAllDebt(session);
      return (List<Debt>) q.list();
    } finally {
      release(session);
    }
  }

  public List<InventoryRemindEvent> getAllInventoryRemindEvent() {
    Session session = this.getSession();
    try {
      Query q = SQL.getAllInventoryRemindEvent(session);
      return (List<InventoryRemindEvent>) q.list();
    } finally {
      release(session);
    }
  }

  public List<RemindEventDTO> getWXRemindEvent(Long startTime, Long endTime) {
    Session session = this.getSession();
    try {
      Query q = SQL.getWXRemindEvent(session, startTime, endTime);
      List<Object[]> objectsList = (List<Object[]>) q.list();
      if (CollectionUtil.isEmpty(objectsList)) return null;
      List<RemindEventDTO> remindEventDTOs = new ArrayList<RemindEventDTO>();
      for (Object[] objects : objectsList) {
        RemindEvent remindEvent = (RemindEvent) objects[1];
        RemindEventDTO remindEventDTO = remindEvent.toDTO();
        remindEventDTO.setOpenId(StringUtil.valueOf(objects[0]));
        remindEventDTOs.add(remindEventDTO);
      }
      return remindEventDTOs;
    } finally {
      release(session);
    }
  }

  public List<RemindEvent> getWXRemindEvent(Long shopId, Long flashTime, Integer pageNo, Integer pageSize) {
    Session session = this.getSession();
    try {
      Query q = SQL.queryRepairRemindEvent(session, shopId, flashTime, pageNo, pageSize);
      return (List<RemindEvent>) q.list();
    } finally {
      release(session);
    }
  }

  public List<RemindEvent> queryRepairRemindEvent(Long shopId, Long flashTime, Integer pageNo, Integer pageSize) {
    Session session = this.getSession();
    try {
      Query q = SQL.queryRepairRemindEvent(session, shopId, flashTime, pageNo, pageSize);
      return (List<RemindEvent>) q.list();
    } finally {
      release(session);
    }
  }

  //根据提醒类型查找来料待修 缺料待修 待交付
  public List<RemindEvent> queryRepairRemindEvent(Long shopId, Long flashTime, RepairRemindEventTypes repairRemindEventTypes, Integer pageNo, Integer pageSize) {
    Session session = this.getSession();
    try {
      Query q = SQL.queryRepairRemindEvent(session, shopId, flashTime, repairRemindEventTypes, pageNo, pageSize);
      return (List<RemindEvent>) q.list();
    } finally {
      release(session);
    }
  }

  //计算来料待修 缺料待修 待交付的数量
  public int countRepairRemindEvent(Long shopId, RepairRemindEventTypes repairRemindEventTypes) {
    StopWatchUtil sw = new StopWatchUtil("countRepairRemindEvent");
    Session session = this.getSession();
    try {
      Query query = SQL.countRepairRemindEvent(session, shopId, repairRemindEventTypes);
      int count = ((Long) query.uniqueResult()).intValue();
//        sw.stopAndPrintLog();
      return count;
    } finally {
      release(session);
    }
  }

  public List<Object[]> queryDebtRemindEvent(Long shopId, Boolean isOverdue, Boolean hasRemind, Long flashTime, Integer pageNo, Integer pageSize) {
    Session session = this.getSession();
    try {
      Query q = SQL.queryDebtRemindEvent(session, shopId, isOverdue, hasRemind, flashTime, pageNo, pageSize);
      return (List<Object[]>) q.list();
    } finally {
      release(session);
    }
  }

  public int countDebtRemindEvent(Long shopId, Boolean isOverdue, Boolean hasRemind, Long flashTime) {
    Session session = this.getSession();
    try {
      Query q = SQL.countDebtRemindEvent(session, shopId, isOverdue, hasRemind, flashTime);
      return ((BigInteger) q.uniqueResult()).intValue();
    } finally {
      release(session);
    }
  }

  public List<Long> RFCountDebtRemindEvent(Long shopId, Boolean isOverdue, Boolean hasRemind, Long flashTime) {
    Session session = this.getSession();
    try {
      Query q = SQL.RFCountDebtRemindEvent(session, shopId, isOverdue, hasRemind, flashTime);
      if (q.list() != null && q.list().size() > 0) {
        return (List<Long>) q.list();
      } else {
        return null;
      }
    } finally {
      release(session);
    }
  }

  public List<RemindEvent> queryTxnRemindEvent(Long shopId, Long flashTime, Integer pageNo, Integer pageSize) {
    Session session = this.getSession();
    try {
      Query q = SQL.queryTxnRemindEvent(session, shopId, flashTime, pageNo, pageSize);
      return (List<RemindEvent>) q.list();
    } finally {
      release(session);
    }
  }

  public int countTxnRemindEvent(Long shopId, Long flashTime) {
    Session session = this.getSession();
    try {
      Query q = SQL.countTotalTxnRemindEvent(session, shopId);
      return ((Long) q.uniqueResult()).intValue();
    } finally {
      release(session);
    }
  }

  public List<RemindEvent> queryCustomerRemindEvent(Long shopId, Boolean isOverdue, Boolean hasRemind, Long flashTime, Integer pageNo, Integer pageSize) {
    Session session = this.getSession();
    try {
      Query q = SQL.queryCustomerRemindEvent(session, shopId, isOverdue, hasRemind, flashTime, pageNo, pageSize);
      return (List<RemindEvent>) q.list();
    } finally {
      release(session);
    }
  }

  public int countCustomerRemindEvent(Long shopId, Boolean isOverdue, Boolean hasRemind, Long flashTime) {
    long begin = System.currentTimeMillis();
    LOG.debug("AOP_SQL start:TxnWriter:countCustomerRemindEvent");
    Session session = this.getSession();
    try {
      Query q;
      int count = 0;
      //默认
      if (hasRemind == null && isOverdue == null) {
        q = SQL.countCustomerRemindEvent1(session, shopId);
        count = ((Long) q.uniqueResult()).intValue();
      }
      //未提醒，已过期
      else if (isOverdue != null && isOverdue == true) {
        q = SQL.countCustomerRemindEvent2(session, shopId, flashTime);
        count = ((Long) q.uniqueResult()).intValue();
      }
      //未提醒，未过期
      else if (isOverdue != null && isOverdue == false) {
        q = SQL.countCustomerRemindEvent3(session, shopId, flashTime);
        count = ((Long) q.uniqueResult()).intValue();
      }
      //已提醒的
      else if (hasRemind != null && hasRemind == true) {
        q = SQL.countCustomerRemindEvent4(session, shopId);
        count = ((Long) q.uniqueResult()).intValue();
      }
      return count;
    } finally {
      LOG.debug("AOP_SQL end:TxnWriter:countCustomerRemindEvent 用时：{}ms", System.currentTimeMillis() - begin);
      release(session);
    }
  }

  public boolean cancelRemindEventByOrderId(RemindEventType type, Long orderId) {
    Session session = this.getSession();
    try {
      Query q = SQL.cancelRemindEventByOrderId(session, type, orderId);
      int result = q.executeUpdate();
      if (result > 0) {
        return true;
      } else {
        return false;
      }
    } finally {
      release(session);
    }
  }

  public boolean cancelRemindEventByOldRemindEventId(RemindEventType type, Long oldRemindEventId) {
    Session session = this.getSession();
    try {
      Query q = SQL.cancelRemindEventByOldRemindEventId(session, type, oldRemindEventId);
      int result = q.executeUpdate();
      if (result > 0) {
        return true;
      } else {
        return false;
      }
    } finally {
      release(session);
    }
  }

  public boolean cancelRemindEventByOrderIdAndStatus(RemindEventType type, Long orderId, RepairRemindEventTypes status) {
    Session session = this.getSession();
    try {
      Query q = SQL.cancelRemindEventByOrderIdAndStatus(session, type, orderId, status);
      int result = q.executeUpdate();
      if (result > 0) {
        return true;
      } else {
        return false;
      }
    } finally {
      release(session);
    }
  }

  public boolean cancelRemindEventByOrderTypeAndOrderId(RemindEventType type, OrderTypes orderType, Long orderId) {
    Session session = this.getSession();
    try {
      Query q = SQL.cancelRemindEventByOrderTypeAndOrderId(session, type, orderType, orderId);
      int result = q.executeUpdate();
      if (result > 0) {
        return true;
      } else {
        return false;
      }
    } finally {
      release(session);
    }
  }

  public boolean cancelRemindEventByOrderIdAndObjectId(RemindEventType type, String eventStatus, Long orderId, Long objectId) {
    Session session = this.getSession();
    try {
      Query q = SQL.cancelRemindEventByOrderIdAndObjectId(session, type, eventStatus, orderId, objectId);
      int result = q.executeUpdate();
      if (result > 0) {
        return true;
      } else {
        return false;
      }
    } finally {
      release(session);
    }
  }

  public List<RemindEvent> getRemindEventByOrderId(RemindEventType type, Long shopId, Long orderId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getRemindEventByOrderId(session, type, shopId, orderId);
      return (List<RemindEvent>) q.list();
    } finally {
      release(session);
    }
  }

  public List<RemindEvent> getRemindEventListByCustomerIdAndType(RemindEventType type, Long customerId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getRemindEventListByCustomerIdAndType(session, type, customerId);
      return (List<RemindEvent>) q.list();
    } finally {
      release(session);
    }
  }

  public List<RemindEvent> getRemindEventListBySupplierIdAndType(RemindEventType type, Long supplierId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getRemindEventListBySupplierIdAndType(session, type, supplierId);
      return (List<RemindEvent>) q.list();
    } finally {
      release(session);
    }
  }

  public List<RemindEvent> getRemindEventByCustomerId(Long shopId, Long[] customerIds) {
    Session session = this.getSession();
    try {
      Query q = SQL.getRemindEventByCustomerId(session, shopId, customerIds);
      return (List<RemindEvent>) q.list();
    } finally {
      release(session);
    }
  }

  public List<RemindEvent> getRemindEventListByOrderIdAndObjectIdAndEventStatus(RemindEventType type, Long orderId, Long objectId, String eventStatus) {
    Session session = this.getSession();
    try {
      Query q = SQL.getRemindEventListByOrderIdAndObjectIdAndEventStatus(session, type, orderId, objectId, eventStatus);
      return (List<RemindEvent>) q.list();
    } finally {
      release(session);
    }
  }

  public List<RemindEvent> getRemindEventByOldRemindEventId(RemindEventType type, Long shopId, Long oldRemindEventId) {
    long begin = System.currentTimeMillis();
    LOG.debug("AOP_SQL start:TxnWriter:getRemindEventByOldRemindEventId");
    Session session = this.getSession();
    try {
      Query q = SQL.getRemindEventByOldRemindEventId(session, type, shopId, oldRemindEventId);
      return (List<RemindEvent>) q.list();
    } finally {
      LOG.debug("AOP_SQL end:TxnWriter:getRemindEventByOldRemindEventId 用时：{}ms", System.currentTimeMillis() - begin);
      release(session);
    }
  }

  /**
   * 查出  oldRemindEventId 有效的 RemindEvent，返回的key是 oldRemindEventId，如果有多条只取最后一条
   *
   * @param shopId
   * @param oldRemindEventIds
   * @return
   */
  public Map<Long, RemindEvent> getRemindEventMapByOldRemindEventIds(Long shopId, Set<Long> oldRemindEventIds) {
    Map<Long, RemindEvent> remindEventMap = new HashMap<Long, RemindEvent>();
    if (shopId == null || CollectionUtils.isEmpty(oldRemindEventIds)) {
      return remindEventMap;
    }
    long begin = System.currentTimeMillis();
    LOG.debug("AOP_SQL start:TxnWriter:getRemindEventMapByOldRemindEventIds");
    Session session = this.getSession();
    try {
      Query q = SQL.getRemindEventByOldRemindEventIds(session, shopId, oldRemindEventIds);
      List<RemindEvent> remindEvents = q.list();
      if (CollectionUtils.isNotEmpty(remindEvents)) {
        for (RemindEvent remindEvent : remindEvents) {
          if (remindEvent.getOldRemindEventId() != null) {
            remindEventMap.put(remindEvent.getOldRemindEventId(), remindEvent);
          }
        }
      }
      return remindEventMap;
    } finally {
      LOG.debug("AOP_SQL end:TxnWriter:getRemindEventMapByOldRemindEventIds 用时：{}ms", System.currentTimeMillis() - begin);
      release(session);
    }
  }

  public boolean mergerCustmerRemindEvent(Long parentId, Long[] childIds) {
    Session session = this.getSession();
    try {
      Query q = SQL.mergerCustmerRemindEvent(session, parentId, childIds);
      int result = q.executeUpdate();
      if (result > 0) {
        return true;
      } else {
        return false;
      }
    } finally {
      release(session);
    }
  }

  public boolean updateCustomerBirthdayRemindEvent(Long customerId, Long newBirthday) {
    Session session = this.getSession();
    try {
      Query q = SQL.updateCustomerBirthdayRemindEvent(session, customerId, newBirthday);
      int result = q.executeUpdate();
      if (result > 0) {
        return true;
      } else {
        return false;
      }
    } finally {
      release(session);
    }
  }

  public List<String> getDebtFromReceivableByCustomerId(Long shopId, Long customerId, OrderDebtType orderDebtType, ReceivableStatus receivableStatus) {
    Session session = getSession();
    try {
      Query q = SQL.getDebtFromReceivableByCustomerId(session, shopId, customerId, orderDebtType, receivableStatus);
      List<Object> list = q.list();
      List<String> stringList = new ArrayList<String>();
      if (CollectionUtils.isNotEmpty(list)) {
        Object[] array = (Object[]) list.get(0);
        if (array[0] != null && array[1] != null) {
          stringList.add(array[0].toString());
          stringList.add(array[1].toString());
        }
      }
      if (CollectionUtils.isEmpty(stringList)) {
        stringList.add("0");
        stringList.add("0");
      }
      return stringList;
    } finally {
      release(session);
    }


  }

  public Map<Long, Object[]> getTotalReturnAmountByCustomerIds(Long shopId, Long... customerIds) {
    Session session = getSession();
    try {
      Query q = SQL.getTotalReturnAmountByCustomerIds(session, shopId, customerIds);
      List<Object[]> list = q.list();
      Map<Long, Object[]> result = new HashMap<Long, Object[]>();
      for (Object[] o : list) {
        Object[] value = new Object[2];
        value[0] = o[1] == null ? 0 : o[1];
        value[1] = o[2] == null ? 0 : o[2];
        result.put(Long.parseLong(o[0].toString()), value);
      }
      return result;
    } finally {
      release(session);
    }
  }

  public List<RemindEvent> getLackStorageRemind(Long shopId, Integer pageNo, Integer pageSize) {
    Session session = this.getSession();
    try {
      Query q = SQL.getLackStorageRemind(session, shopId, pageNo, pageSize);
      return (List<RemindEvent>) q.list();
    } finally {
      release(session);
    }
  }

  public int countLackStorageRemind(Long shopId) {
    Session session = this.getSession();
    try {
      Query q = SQL.countLackStorageRemind(session, shopId);
      return Integer.parseInt(q.uniqueResult().toString());
    } finally {
      release(session);
    }
  }

  public int countShoppingCartItemByUserId(Long shopId, Long userId) {
    Session session = this.getSession();
    try {
      Query query = SQL.countShoppingCartItemByUserId(session, shopId, userId);
      return Integer.parseInt(query.uniqueResult().toString());
    } finally {
      release(session);
    }
  }

  public List<ShoppingCartItem> getShoppingCartItemByUserId(Long shopId, Long userId) {
    Session session = this.getSession();
    try {
      Query query = SQL.getShoppingCartItemByUserId(session, shopId, userId);
      return (List<ShoppingCartItem>) query.list();
    } finally {
      release(session);
    }
  }

  public List<ShoppingCartItem> getShoppingCartItemById(Long shopId, Long userId, Long... shoppingCartItemId) {
    Session session = this.getSession();
    try {
      Query query = SQL.getShoppingCartItemById(session, shopId, userId, shoppingCartItemId);
      return (List<ShoppingCartItem>) query.list();
    } finally {
      release(session);
    }
  }

  public ShoppingCartItem getShoppingCartItemByUserIdAndProduct(Long shopId, Long userId, Long productLocalInfoId) {
    Session session = this.getSession();
    try {
      Query query = SQL.getShoppingCartItemByUserIdAndProduct(session, shopId, userId, productLocalInfoId);
      return (ShoppingCartItem) query.uniqueResult();
    } finally {
      release(session);
    }
  }

  public List<BusinessStatChange> getBusinessStatChangeByYear(long shopId, long year) {
    Session session = this.getSession();
    try {
      Query q = SQL.getBusinessStatChangeByYear(session, shopId, year);
      return (List<BusinessStatChange>) q.list();
    } finally {
      release(session);
    }
  }

  public List<CommentRecord> getCommentRecordByOrderId(Long commentatorShopId, Long orderId) {
    Session session = this.getSession();

    try {
      Query q = SQL.getCommentRecordByOrderId(session, commentatorShopId, orderId);

      return (List<CommentRecord>) q.list();
    } finally {
      release(session);
    }
  }


  public List<CommentRecord> getCommentRecordByShopId(Long supplierShopId, CommentStatus commentStatus) {
    Session session = this.getSession();

    try {
      Query q = SQL.getCommentRecordByShopId(session, supplierShopId, commentStatus);

      return (List<CommentRecord>) q.list();
    } finally {
      release(session);
    }
  }


  public List<CommentStat> getCommentStatByShopId(Long supplierShopId) {
    Session session = this.getSession();

    try {
      Query q = SQL.getCommentStatByShopId(session, supplierShopId);
      return (List<CommentStat>) q.list();
    } finally {
      release(session);
    }
  }

  public int countSupplierCommentRecord(Long supplierShopId, CommentStatus commentStatus) {
    Session session = this.getSession();

    try {
      Query q = SQL.countSupplierCommentRecord(session, supplierShopId, commentStatus);

      return Integer.parseInt(q.uniqueResult().toString());
    } finally {
      release(session);
    }
  }

  public List<CommentRecord> getSupplierCommentByPager(Long supplierShopId, CommentStatus commentStatus, Pager pager, Sort sort) {
    Session session = this.getSession();

    try {
      Query q = SQL.getSupplierCommentByPager(session, supplierShopId, commentStatus, pager, sort);

      return (List<CommentRecord>) q.list();
    } finally {
      release(session);
    }
  }


  public void updateRemindEventByShopIdEventTypeObjectId(Long shopId, String eventType, String eventStatus, Long productId) {
    Session session = getSession();
    try {
      Query q = SQL.updateRemindEventByShopIdEventTypeObjectId(session, shopId, eventType, eventStatus, productId);
      q.executeUpdate();
    } finally {
      release(session);
    }
  }

  public List<CommentStat> getCommentStatBySupplier(Collection<Long> supplierShopIds) {
    Session session = this.getSession();

    try {
      Query q = SQL.getCommentStatBySupplier(session, supplierShopIds);
      return (List<CommentStat>) q.list();
    } finally {
      release(session);
    }
  }

  public List<PurchaseOrder> getPurchaseOrderBySupplierShopId(Long supplierShopId) {
    Session session = getSession();

    try {
      Query q = SQL.getPurchaseOrderBySupplierShopId(session, supplierShopId);

      return (List<PurchaseOrder>) q.list();
    } finally {
      release(session);
    }
  }

  /**
   * 永远只有一条待支付记录
   * 获取待支付记录和单据的 关系
   *
   * @param orderId
   * @return
   */
  public List<BcgogoReceivableOrderRecordRelation> getBcgogoReceivableOrderToBePaidRecordRelationByOrderId(Long... orderId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getBcgogoReceivableOrderToBePaidRecordRelationByOrderId(session, orderId);
      return q.list();
    } finally {
      release(session);
    }
  }

  /**
   * 永远只有一条待支付记录
   * 获取待支付记录和单据的 关系
   *
   * @param orderId
   * @return
   */
  public List<BcgogoReceivableRecord> getBcgogoReceivableOrderToBePaidRecordByOrderId(Long orderId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getBcgogoReceivableOrderToBePaidRecordByOrderId(session, orderId);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<Object[]> getBcgogoReceivableOrderRecordAndRelationByOrderId(Long... orderId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getBcgogoReceivableOrderRecordAndRelationByOrderId(session, orderId);
      return (List<Object[]>) q.list();
    } finally {
      release(session);
    }
  }

  public List<BcgogoReceivableOrderItem> getBcgogoReceivableOrderItemByOrderId(Long... orderId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getBcgogoReceivableOrderItemByOrderId(session, orderId);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<BcgogoReceivableOrder> getBcgogoReceivableOrderByRelationId(Long... relationId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getBcgogoReceivableOrderByRelationId(session, relationId);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<BcgogoReceivableOrder> searchBcgogoReceivableOrderResult(BcgogoReceivableSearchCondition condition) {
    Session session = this.getSession();
    try {
      Query q = SQL.searchBcgogoReceivableOrderResult(session, condition);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<BcgogoReceivableOrder> searchBcgogoReceivableOrder(BcgogoReceivableSearchCondition condition) {
    Session session = this.getSession();
    try {
      Query q = SQL.searchBcgogoReceivableOrder(session, condition);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<Object[]> statBcgogoReceivableOrderByStatus(BcgogoReceivableSearchCondition condition) {
    Session session = this.getSession();
    try {
      Query q = SQL.statBcgogoReceivableOrderByStatus(session, condition);
      return (List<Object[]>) q.list();
    } finally {
      release(session);
    }
  }

  public List<Object[]> statBcgogoReceivableOrderRecordByStatus(BcgogoReceivableSearchCondition condition) {
    Session session = this.getSession();
    try {
      Query q = SQL.statBcgogoReceivableOrderRecordByStatus(session, condition);
      return (List<Object[]>) q.list();
    } finally {
      release(session);
    }
  }

  public Integer countBcgogoReceivableOrder(BcgogoReceivableSearchCondition condition) {
    Session session = this.getSession();
    try {
      Query q = SQL.countBcgogoReceivableOrder(session, condition);
      Object o = q.uniqueResult();
      return o == null ? 0 : Integer.valueOf(o.toString());
    } finally {
      release(session);
    }
  }

  public List<BcgogoReceivableRecordDTO> searchBcgogoReceivableResult(BcgogoReceivableSearchCondition condition) {
    Session session = this.getSession();
    try {
      Query q = SQL.searchBcgogoPaymentResult(session, condition)
        .setResultTransformer(Transformers.aliasToBean(BcgogoReceivableRecordDTO.class));
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<BcgogoReceivableDTO> getBcgogoReceivableDTOByRelationId(Long... relationId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getBcgogoReceivableDTOByRelationId(session, relationId)
        .setResultTransformer(Transformers.aliasToBean(BcgogoReceivableDTO.class));
      return q.list();
    } finally {
      release(session);
    }
  }

  public Integer countBcgogoPayment(BcgogoReceivableSearchCondition condition) {
    Session session = this.getSession();
    try {
      Query q = SQL.countBcgogoPayment(session, condition);
      Object o = q.uniqueResult();
      return o == null ? 0 : Integer.valueOf(o.toString());
    } finally {
      release(session);
    }
  }

  public List<BcgogoReceivableOrderRecordRelation> getSoftwareReceivable(long shopId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getSoftwareReceivable(session, shopId);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<BcgogoReceivableOrderRecordRelation> getSoftwareReceivableByReceivableMethod(long shopId, ReceivableMethod receivableMethod) {
    Session session = this.getSession();
    try {
      Query q = SQL.getSoftwareReceivableByReceivableMethod(session, shopId, receivableMethod);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<InstalmentPlanAlgorithm> getInstalmentPlanAlgorithms() {
    Session session = this.getSession();
    try {
      Query q = SQL.getInstalmentPlanAlgorithms(session);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<InstalmentPlan> getInstalmentPlanByIds(Long... instalmentPlanIds) {
    Session session = this.getSession();
    try {
      Query q = SQL.getInstalmentPlanByIds(session, instalmentPlanIds);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<InstalmentPlanItem> getInstalmentPlanItemsByIds(Long... instalmentPlanItemIds) {
    Session session = this.getSession();
    try {
      Query q = SQL.getInstalmentPlanItemsByIds(session, instalmentPlanItemIds);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<InstalmentPlanItem> getInstalmentPlanItemsByInstalmentPlanIds(Long... instalmentPlanIds) {
    Session session = this.getSession();
    try {
      Query q = SQL.getInstalmentPlanItemsByInstalmentPlanIds(session, instalmentPlanIds);
      return q.list();
    } finally {
      release(session);
    }
  }

  public Integer countHardwareSoftwareAccountResult(AccountSearchCondition condition) {
    Session session = this.getSession();
    try {
      Query q = SQL.countHardwareSoftwareAccountResult(session, condition);
      Object o = q.uniqueResult();
      return o == null ? 0 : Integer.valueOf(o.toString());
    } finally {
      release(session);
    }
  }

  public List<HardwareSoftwareAccount> searchHardwareSoftwareAccountResult(AccountSearchCondition condition) {
    Session session = this.getSession();
    try {
      Query q = SQL.searchHardwareSoftwareAccountResult(session, condition);
      return q.list();
    } finally {
      release(session);
    }
  }

  public HardwareSoftwareAccount getHardwareSoftwareAccountByShopId(long shopId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getHardwareSoftwareAccountByShopId(session, shopId);
      return (HardwareSoftwareAccount) q.uniqueResult();
    } finally {
      release(session);
    }
  }

  public List<BcgogoReceivableOrder> getBcgogoReceivableOrderByShopIds(Long... shopIds) {
    Session session = this.getSession();
    try {
      Query q = SQL.getBcgogoReceivableOrderByShopIds(session, shopIds);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<HardwareSoftwareAccountRecordDTO> getHardwareSoftwareAccountRecordByShopIds(Long... shopIds) {
    Session session = this.getSession();
    try {
      Query q = SQL.getHardwareSoftwareAccountRecordByShopIds(session, shopIds)
        .setResultTransformer(Transformers.aliasToBean(HardwareSoftwareAccountRecordDTO.class));
      return q.list();
    } finally {
      release(session);
    }
  }

  public Object[] countHardwareSoftwareAccount(AccountSearchCondition condition) {
    Session session = this.getSession();
    try {
      Query q = SQL.countHardwareSoftwareAccount(session, condition);
      return (Object[]) q.uniqueResult();
    } finally {
      release(session);
    }
  }

  public Object[] countSoftwareAccount(AccountSearchCondition condition) {
    Session session = this.getSession();
    try {
      Query q = SQL.countSoftwareAccount(session, condition);
      return (Object[]) q.uniqueResult();
    } finally {
      release(session);
    }
  }

  public Object[] countHardwareAccount(AccountSearchCondition condition) {
    Session session = this.getSession();
    try {
      Query q = SQL.countHardwareAccount(session, condition);
      return (Object[]) q.uniqueResult();
    } finally {
      release(session);
    }
  }

  public Double countHardwareSoftwarePaidAmountAccount(PaymentMethod paymentMethod, PaymentType paymentType, AccountSearchCondition condition) {
    Session session = this.getSession();
    try {
      Query q = SQL.countHardwareSoftwarePaidAmountAccount(session, paymentMethod, paymentType, condition);
      Double amount = (Double) q.uniqueResult();
      return amount == null ? 0.0d : amount;
    } finally {
      release(session);
    }
  }

  public Integer countShopSmsAccountResult(SmsRecordSearchCondition condition) {
    Session session = this.getSession();
    try {
      Query q = SQL.countShopSmsAccountResult(session, condition);
      Object o = q.uniqueResult();
      return o == null ? 0 : Integer.valueOf(o.toString());
    } finally {
      release(session);
    }
  }

  public List<ShopSmsAccount> searchShopSmsAccountResult(SmsRecordSearchCondition condition) {
    Session session = this.getSession();
    try {
      Query q = SQL.searchShopSmsAccountResult(session, condition);
      return q.list();
    } finally {
      release(session);
    }
  }

  public Integer countShopSmsRecordResult(SmsRecordSearchCondition condition) {
    Session session = this.getSession();
    try {
      Query q = SQL.countShopSmsRecordResult(session, condition);
      Object o = q.uniqueResult();
      return o == null ? 0 : Integer.valueOf(o.toString());
    } finally {
      release(session);
    }
  }

  public List<ShopSmsRecord> searchShopSmsRecordResult(SmsRecordSearchCondition condition) {
    Session session = this.getSession();
    try {
      Query q = SQL.searchShopSmsRecordResult(session, condition);
      return q.list();
    } finally {
      release(session);
    }
  }

  public Integer countBcgogoSmsRecordResult(SmsRecordSearchCondition condition) {
    Session session = this.getSession();
    try {
      Query q = SQL.countBcgogoSmsRecordResult(session, condition);
      Object o = q.uniqueResult();
      return o == null ? 0 : Integer.valueOf(o.toString());
    } finally {
      release(session);
    }
  }

  public List<BcgogoSmsRecord> searchBcgogoSmsRecordResult(SmsRecordSearchCondition condition) {
    Session session = this.getSession();
    try {
      Query q = SQL.searchBcgogoSmsRecordResult(session, condition);
      return q.list();
    } finally {
      release(session);
    }
  }

  public Long countSmsAccountNumberBySmsCategory(SmsCategory... categories) {
    Session session = this.getSession();
    try {
      Query q = SQL.countSmsAccountNumberBySmsCategory(session, categories);
      Object o = q.uniqueResult();
      return o == null ? 0 : Long.valueOf(o.toString());
    } finally {
      release(session);
    }
  }

  public Double countSmsAccountBalanceBySmsCategory(SmsCategory... categories) {
    Session session = this.getSession();
    try {
      Query q = SQL.countSmsAccountBalanceBySmsCategory(session, categories);
      Object o = q.uniqueResult();
      return o == null ? 0 : Double.valueOf(o.toString());
    } finally {
      release(session);
    }
  }

  public Object[] countSmsTotalRecharge(SmsCategory smsCategory) {
    Session session = this.getSession();
    try {
      Query q = SQL.countSmsTotalRecharge(session, smsCategory);
      return (Object[]) q.uniqueResult();
    } finally {
      release(session);
    }
  }

  public Object[] countShopSmsTotalRecharge() {
    Session session = this.getSession();
    try {
      Query q = SQL.countShopSmsTotalRecharge(session);
      return (Object[]) q.uniqueResult();
    } finally {
      release(session);
    }
  }

  public ShopSmsAccount getShopSmsAccountByShopId(long shopId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getShopSmsAccountByShopId(session, shopId);
      return (ShopSmsAccount) q.uniqueResult();
    } finally {
      release(session);
    }
  }


  public List<ShopSmsRecord> getShopSmsRecordByShopId(Long shopId, Long operateTime) {
    Session session = this.getSession();
    try {
      Query q = SQL.getShopSmsRecordByShopId(session, shopId, operateTime);
      return q.list();
    } finally {
      release(session);
    }
  }

  public BcgogoSmsRecord getCurrentDayBcgogoSmsRecord(Long startTime, Long endTime, SmsCategory smsCategory) {
    Session session = this.getSession();
    try {
      Query q = SQL.getCurrentDayBcgogoSmsRecord(session, startTime, endTime, smsCategory);
      List<BcgogoSmsRecord> smsRecordList = (List<BcgogoSmsRecord>) q.list();
      if (CollectionUtil.isNotEmpty(smsRecordList)) {
        return smsRecordList.get(0);
      }
      return null;
    } finally {
      release(session);
    }
  }

  public ShopSmsRecord getCurrentDayShopSmsRecord(long shopId, Long startTime, Long endTime, SmsCategory smsCategory, StatType statType) {
    Session session = this.getSession();
    try {
      Query q = SQL.getCurrentDayShopSmsRecord(session, shopId, startTime, endTime, smsCategory, statType);
      List<ShopSmsRecord> shopSmsRecordList = (List<ShopSmsRecord>) q.list();
      if (CollectionUtil.isNotEmpty(shopSmsRecordList)) {
        return shopSmsRecordList.get(0);
      }
      return null;
    } finally {
      release(session);
    }
  }

  public Object[] shopSmsAccountStatistics(SmsRecordSearchCondition condition) {
    Session session = this.getSession();
    try {
      Query q = SQL.shopSmsAccountStatistics(session, condition);
      return (Object[]) q.uniqueResult();
    } finally {
      release(session);
    }
  }

  public Object[] shopSmsRecordStatistics(SmsRecordSearchCondition condition, SmsCategory... smsCategories) {
    Session session = this.getSession();
    try {
      condition.setSmsCategories(smsCategories);
      Query q = SQL.shopSmsRecordStatistics(session, condition);
      return (Object[]) q.uniqueResult();
    } finally {
      release(session);
    }
  }

  public List<Object[]> getPurchaseInventoryOrderIdByOnline() {
    Session session = this.getSession();
    try {
      Query q = SQL.getPurchaseInventoryOrderIdByOnline(session);
      return (List<Object[]>) q.list();
    } finally {
      release(session);
    }

  }

  public List<SupplierInventory> getSupplierInventoryList(Long shopId, Long supplierId, Long storehouseId, Set<Long> productIds) {
    Session session = this.getSession();
    try {
      Query q = SQL.getSupplierInventoryList(session, shopId, supplierId, storehouseId, productIds);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<SupplierInventory> getHasRemainSupplierInventory(Long shopId, Long supplierId, Long storehouseId, Set<Long> productIds) {
    Session session = this.getSession();
    try {
      Query q = SQL.getHasRemainSupplierInventory(session, shopId, supplierId, storehouseId, productIds);
      return q.list();
    } finally {
      release(session);
    }
  }


  public List<SupplierInventory> getSupplierInventoriesByProductIds(Long shopId, Set<Long> productIds) {
    Session session = this.getSession();
    try {
      Query q = SQL.getSupplierInventoriesByProductIds(session, shopId, productIds);
      return q.list();
    } finally {
      release(session);
    }
  }


  public List<SupplierInventory> getSupplierAllInventory(Long shopId, Long supplierId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getSupplierAllInventory(session, shopId, supplierId);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<SupplierInventory> getSupplierInventory(SupplierInventoryDTO condition) {
    Session session = this.getSession();
    try {
      Query q = SQL.getSupplierInventory(session, condition);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<SupplierInventory> getSupplierInventoryByStorehouseIdAndProductIds(Long shopId, Long storehouseId, Long... productIds) {
    Session session = this.getSession();
    try {
      Query q = SQL.getSupplierInventoryByStorehouseIdAndProductIds(session, shopId, storehouseId, productIds);
      return q.list();
    } finally {
      release(session);
    }
  }

  public Long getNextProductId(Long shopId, long startProductId, int defaultPageSize) {
    Session session = this.getSession();
    try {
      Query query = SQL.getNextProductId(session, shopId, startProductId, defaultPageSize);
      return (Long) query.uniqueResult();
    } finally {
      release(session);
    }
  }

  public List<SupplierInventoryDTO> getInitHaveStoreHouseSupplierInventoryInfo(Long shopId, Long startProductId, Long endProductId) {
    Session session = this.getSession();
    List<SupplierInventoryDTO> supplierInventoryDTOs = new ArrayList<SupplierInventoryDTO>();
    try {
      Query q = SQL.getInitHaveStoreHouseSupplierInventory(session, shopId, startProductId, endProductId);
      List<Object[]> list = q.list();
      if (CollectionUtils.isNotEmpty(list)) {
        for (Object[] o : list) {
          if (o != null && o.length > 0) {
            SupplierInventoryDTO supplierInventoryDTO = new SupplierInventoryDTO();
            supplierInventoryDTO.setSupplierType(OutStorageSupplierType.NATIVE_SUPPLIER);
            supplierInventoryDTO.setDisabled(YesNo.NO);
            supplierInventoryDTO.setRemainAmount(0D);
            supplierInventoryDTO.setProductId((Long) o[0]);
            supplierInventoryDTO.setSupplierId((Long) o[1]);
            supplierInventoryDTO.setShopId((Long) o[2]);
            supplierInventoryDTO.setLastStorageTime((Long) o[3]);
            supplierInventoryDTO.setSupplierName((String) o[4]);
            supplierInventoryDTO.setSupplierContact((String) o[5]);
            supplierInventoryDTO.setSupplierMobile((String) o[6]);
            supplierInventoryDTO.setStorehouseId((Long) o[7]);
            supplierInventoryDTO.setUnit((String) o[8]);
            supplierInventoryDTO.setTotalInStorageAmount((Double) o[9]);
            supplierInventoryDTO.setMaxStoragePrice((Double) o[10]);
            supplierInventoryDTO.setMinStoragePrice((Double) o[11]);
            supplierInventoryDTOs.add(supplierInventoryDTO);
          }
        }
      }
    } finally {
      release(session);
    }
    return supplierInventoryDTOs;
  }

  public List<SupplierInventoryDTO> getInitNoStoreHouseSupplierInventoryInfo(Long shopId, Long startProductId, Long endProductId) {
    Session session = this.getSession();
    List<SupplierInventoryDTO> supplierInventoryDTOs = new ArrayList<SupplierInventoryDTO>();
    try {
      Query q = SQL.getInitNoStoreHouseSupplierInventory(session, shopId, startProductId, endProductId);
      List<Object[]> list = q.list();
      if (CollectionUtils.isNotEmpty(list)) {
        for (Object[] o : list) {
          if (o != null && o.length > 0) {
            SupplierInventoryDTO supplierInventoryDTO = new SupplierInventoryDTO();
            supplierInventoryDTO.setSupplierType(OutStorageSupplierType.NATIVE_SUPPLIER);
            supplierInventoryDTO.setDisabled(YesNo.NO);
            supplierInventoryDTO.setRemainAmount(0D);
            supplierInventoryDTO.setProductId((Long) o[0]);
            supplierInventoryDTO.setSupplierId((Long) o[1]);
            supplierInventoryDTO.setShopId((Long) o[2]);
            supplierInventoryDTO.setLastStorageTime((Long) o[3]);
            supplierInventoryDTO.setSupplierName((String) o[4]);
            supplierInventoryDTO.setSupplierContact((String) o[5]);
            supplierInventoryDTO.setSupplierMobile((String) o[6]);
            supplierInventoryDTO.setUnit((String) o[7]);
            supplierInventoryDTO.setTotalInStorageAmount((Double) o[8]);
            supplierInventoryDTO.setMaxStoragePrice((Double) o[9]);
            supplierInventoryDTO.setMinStoragePrice((Double) o[10]);
            supplierInventoryDTOs.add(supplierInventoryDTO);
          }
        }
      }
    } finally {
      release(session);
    }
    return supplierInventoryDTOs;
  }


  public int countProductInventory(Long shopId) {
    if (shopId == null) {
      return 0;
    }
    Session session = this.getSession();
    try {
      Query q = SQL.countProductInventory(session, shopId);
      Long result = (Long) q.uniqueResult();
      if (result == null) {
        return 0;
      }
      return result.intValue();
    } finally {
      release(session);
    }
  }

  public List<SupplierInventoryDTO> getInitHaveStoreHouseSupplierInventoryAmount(Long shopId, Pager pager) {
    Session session = this.getSession();
    List<SupplierInventoryDTO> supplierInventoryDTOs = new ArrayList<SupplierInventoryDTO>();
    try {
      Query q = SQL.getInitNoStoreHouseSupplierInventoryAmount(session, shopId, pager);
      List<Object[]> inventoryInfo = q.list();
      Set<Long> productIds = new HashSet<Long>();
      if (CollectionUtils.isNotEmpty(inventoryInfo)) {
        for (Object[] o : inventoryInfo) {
          if (o != null && o.length > 0) {
            productIds.add((Long) o[0]);
          }
        }
      }
      if (CollectionUtil.isNotEmpty(productIds)) {
        q = SQL.getInitHaveStoreHouseSupplierInventoryAmount(session, shopId, productIds);
        List<Object[]> list = q.list();
        if (CollectionUtils.isNotEmpty(list)) {
          for (Object[] o : list) {
            if (o != null && o.length > 0) {
              SupplierInventoryDTO supplierInventoryDTO = new SupplierInventoryDTO();
              supplierInventoryDTO.setSupplierType(OutStorageSupplierType.UNDEFINED_SUPPLIER);
              supplierInventoryDTO.setSupplierName(OutStorageSupplierType.UNDEFINED_SUPPLIER.getName());
              supplierInventoryDTO.setProductId((Long) o[0]);
              supplierInventoryDTO.setShopId((Long) o[1]);
              supplierInventoryDTO.setUnit((String) o[2]);
              supplierInventoryDTO.setTotalInStorageAmount((Double) o[3]);
              supplierInventoryDTO.setRemainAmount((Double) o[3]);
              supplierInventoryDTO.setLastStoragePrice((Double) o[4]);
              supplierInventoryDTO.setAverageStoragePrice((Double) o[5]);
              supplierInventoryDTO.setLastStorageTime((Long) o[6]);
              supplierInventoryDTO.setStorehouseId((Long) o[7]);
              supplierInventoryDTO.setDisabled(YesNo.NO);
              supplierInventoryDTOs.add(supplierInventoryDTO);
            }
          }
        }
      }
    } finally {
      release(session);
    }
    return supplierInventoryDTOs;
  }

  public List<SupplierInventoryDTO> getInitNoStoreHouseSupplierInventoryAmount(Long shopId, Pager pager) {
    Session session = this.getSession();
    List<SupplierInventoryDTO> supplierInventoryDTOs = new ArrayList<SupplierInventoryDTO>();
    try {
      Query q = SQL.getInitNoStoreHouseSupplierInventoryAmount(session, shopId, pager);
      List<Object[]> list = q.list();
      if (CollectionUtils.isNotEmpty(list)) {
        for (Object[] o : list) {
          if (o != null && o.length > 0) {
            SupplierInventoryDTO supplierInventoryDTO = new SupplierInventoryDTO();
            supplierInventoryDTO.setSupplierType(OutStorageSupplierType.UNDEFINED_SUPPLIER);
            supplierInventoryDTO.setSupplierName(OutStorageSupplierType.UNDEFINED_SUPPLIER.getName());
            supplierInventoryDTO.setProductId((Long) o[0]);
            supplierInventoryDTO.setShopId((Long) o[1]);
            supplierInventoryDTO.setUnit((String) o[2]);
            supplierInventoryDTO.setTotalInStorageAmount((Double) o[3]);
            supplierInventoryDTO.setRemainAmount((Double) o[3]);
            supplierInventoryDTO.setLastStoragePrice((Double) o[4]);
            supplierInventoryDTO.setAverageStoragePrice((Double) o[5]);
            supplierInventoryDTO.setLastStorageTime((Long) o[6]);
            supplierInventoryDTO.setDisabled(YesNo.NO);
            supplierInventoryDTOs.add(supplierInventoryDTO);
          }
        }
      }
    } finally {
      release(session);
    }
    return supplierInventoryDTOs;
  }

  public List<SupplierInventoryDTO> getLastProductSupplierByProductIds(Long shopId, Set<Long> productIds) {
    Session session = this.getSession();
    try {
      Query query = SQL.getLastProductSupplierByProductIds(session, shopId, productIds);
      List<SupplierInventory> list = (List<SupplierInventory>) query.list();
      if (CollectionUtils.isEmpty(list)) {
        return null;
      }
      List<SupplierInventoryDTO> supplierInventoryDTOs = new ArrayList<SupplierInventoryDTO>();
      for (SupplierInventory supplierInventory : list) {
        supplierInventoryDTOs.add(supplierInventory.toDTO());
      }
      return supplierInventoryDTOs;
    } finally {
      release(session);
    }
  }

  public List<OutStorageRelation> getOutStorageRelation(Long shopId, Long outStorageOrderId, OrderTypes outStorageOrderType, Long outStorageItemId, Long productId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getOutStorageRelation(session, shopId, outStorageOrderId, outStorageOrderType, outStorageItemId, productId);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<OutStorageRelation> getOutStorageRelationByOrderIds(Long shopId, Long... outStorageOrderId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getOutStorageRelationByOrderIds(session, shopId, outStorageOrderId);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<OutStorageRelation> getOutStorageRelationByOrderAndProductIds(Long shopId, Long outStorageOrderId, Set<Long> productIds) {
    Session session = this.getSession();
    try {
      Query q = SQL.getOutStorageRelationByOrderAndProductIds(session, shopId, outStorageOrderId, productIds);
      return q.list();
    } finally {
      release(session);
    }
  }


  public List<OutStorageRelation> getOutStorageRelationByRelated(Long shopId, Long relatedOrderId, OrderTypes relatedOrderType, Long relatedItemId, Long productId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getOutStorageRelationByRelated(session, shopId, relatedOrderId, relatedOrderType, relatedItemId, productId);
      return q.list();
    } finally {
      release(session);
    }
  }


  public List<InStorageRecord> getInStorageRecordMap(Long shopId, Long storehouseId, Set<Long> productIdSet, Set<Long> supplierIdSet, Set<OutStorageSupplierType> outStorageSupplierTypes, boolean containSupplierIdEmpty) {
    Session session = this.getSession();
    try {
      Query q = SQL.getInStorageRecordMap(session, shopId, storehouseId, productIdSet, supplierIdSet, outStorageSupplierTypes, containSupplierIdEmpty);
      return q.list();
    } finally {
      release(session);
    }
  }


  public List<InStorageRecord> getInStorageRecordByOrderIds(Long shopId, Long... inStorageOrderId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getInStorageRecordByOrderIds(session, shopId, inStorageOrderId);
      return q.list();
    } finally {
      release(session);
    }
  }

  public int countSupplierInventory(Long shopId, Long productId) {
    Session session = this.getSession();
    try {
      Query q = SQL.countSupplierInventory(session, shopId, productId);
      Long result = (Long) q.uniqueResult();
      return result == null ? 0 : result.intValue();
    } finally {
      release(session);
    }
  }

  public List<Long> getSupplierIdsByProductId(Long shopId, Long productId, Pager pager) {
    Session session = this.getSession();
    try {
      Query q = SQL.getSupplierIdsByProductId(session, shopId, productId, pager);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<SupplierInventory> getSupplierInventoryByProductAndSupplierIds(Long shopId, Long productId, Set<Long> supplierIds) {
    Session session = this.getSession();
    try {
      Query q = SQL.getSupplierInventoryByProductAndSupplierIds(session, shopId, productId, supplierIds);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<SupplierInventoryDTO> getSupplierInventoryDTOBySupplierIds(Long shopId, Set<Long> supplierIds) {
    Session session = this.getSession();
    try {
      List<SupplierInventoryDTO> supplierInventoryDTOs = new ArrayList<SupplierInventoryDTO>();
      Query q = SQL.getSupplierInventoryBySupplierIds(session, shopId, supplierIds);
      List<SupplierInventory> supplierInventorys = q.list();
      if (CollectionUtils.isNotEmpty(supplierInventorys)) {
        for (SupplierInventory supplierInventory : supplierInventorys) {
          supplierInventoryDTOs.add(supplierInventory.toDTO());
        }
      }
      return supplierInventoryDTOs;
    } finally {
      release(session);
    }
  }


  public List<SupplierInventory> getSupplierInventoryBySupplierIds(Long shopId, Set<Long> supplierIds) {
    Session session = this.getSession();
    try {
      Query q = SQL.getSupplierInventoryBySupplierIds(session, shopId, supplierIds);
      return q.list();
    } finally {
      release(session);
    }
  }

  public SupplierInventory getSupplierInventoryBySupplierType(Long shopId, Long productId, Long storehouseId, OutStorageSupplierType supplierType) {
    Session session = this.getSession();
    try {
      Query q = SQL.getSupplierInventoryBySupplierType(session, shopId, productId, storehouseId, supplierType);
      List<SupplierInventory> supplierInventories = q.list();
      return CollectionUtil.getFirst(supplierInventories);
    } finally {
      release(session);
    }
  }

  public List<SupplierInventory> getSupplierInventoriesByIds(Set<Long> supplierInventoryIds) {
    Session session = this.getSession();
    try {
      Query q = SQL.getSupplierInventoriesByIds(session, supplierInventoryIds);
      return q.list();
    } finally {
      release(session);
    }
  }

  /**
   * 根据查询条件找到一条最后入库商品记录 一张单据上只能查出一条商品，如果需要查出一张单据上相同商品的所有item需要二次查询
   * 这个方法只查出了部分数据，外部调用的时候请注意
   *
   * @param shopId
   * @param supplierId
   * @param storehouseId
   * @param productIds
   * @return
   */
  public List<PurchaseInventoryItemDTO> getLastPurchaseInventoryItemDTOs(Long shopId, Long supplierId, Long storehouseId, Set<Long> productIds) {
    List<PurchaseInventoryItemDTO> purchaseInventoryItemDTOs = new ArrayList<PurchaseInventoryItemDTO>();
    if (shopId == null || supplierId == null || CollectionUtils.isEmpty(productIds)) {
      return purchaseInventoryItemDTOs;
    }
    Session session = this.getSession();
    try {
      Query q = SQL.getLastPurchaseInventoryItems(session, shopId, supplierId, storehouseId, productIds);
      List<Object[]> objectList = q.list();
      if (CollectionUtils.isNotEmpty(objectList)) {
        for (Object[] objects : objectList) {
          //pi2.id,pi2.purchase_inventory_id,pi2.product_id,pi2.amount,pi2.price,pi2.unit,pi2.product_history_id,pi2.vest_date
          PurchaseInventoryItemDTO purchaseInventoryItemDTO = new PurchaseInventoryItemDTO();
          purchaseInventoryItemDTO.setId((Long) objects[0]);
          purchaseInventoryItemDTO.setPurchaseInventoryId((Long) objects[1]);
          purchaseInventoryItemDTO.setProductId((Long) objects[2]);
          purchaseInventoryItemDTO.setAmount((Double) objects[3]);
          purchaseInventoryItemDTO.setPrice((Double) objects[4]);
          purchaseInventoryItemDTO.setUnit(objects[5] == null ? "" : objects[5].toString());
          purchaseInventoryItemDTO.setProductHistoryId((Long) objects[6]);
          purchaseInventoryItemDTO.setVestDate((Long) objects[7]);
          purchaseInventoryItemDTOs.add(purchaseInventoryItemDTO);
        }
      }
      return purchaseInventoryItemDTOs;
    } finally {
      release(session);
    }
  }

  /**
   * 查出根据产品id，入库单的id交集的items
   *
   * @param shopId
   * @param purchaseInventoryIds
   * @param productIds
   * @return
   */
  public List<PurchaseInventoryItem> getAllPurchaseInventoryItems(Long shopId, Set<Long> purchaseInventoryIds, Set<Long> productIds) {
    List<PurchaseInventoryItem> purchaseInventoryItems = new ArrayList<PurchaseInventoryItem>();
    if (shopId == null || CollectionUtils.isEmpty(purchaseInventoryIds) || CollectionUtils.isEmpty(productIds)) {
      return purchaseInventoryItems;
    }
    Session session = this.getSession();
    try {
      Query q = SQL.getAllPurchaseInventoryItems(session, shopId, purchaseInventoryIds, productIds);
      return q.list();
    } finally {
      release(session);
    }
  }

  //找到某张单据作为最后入库单记录的supplierInventory记录
  public List<SupplierInventory> getSupplierInventoriesByPurchaseInventoryId(Long shopId, Long purchaseInventoryId) {
    List<SupplierInventory> supplierInventories = new ArrayList<SupplierInventory>();
    if (shopId == null || purchaseInventoryId == null) {
      return supplierInventories;
    }
    Session session = this.getSession();
    try {
      Query q = SQL.getSupplierInventoriesByPurchaseInventoryId(session, shopId, purchaseInventoryId);
      return q.list();
    } finally {
      release(session);
    }
  }


  public List<InStorageRecord> getInStorageRecordBySupplierIds(Long shopId, Set<Long> supplierIds) {
    if (shopId == null || CollectionUtils.isEmpty(supplierIds)) {
      return new ArrayList<InStorageRecord>();
    }
    Session session = this.getSession();
    try {
      Query q = SQL.getInStorageRecordBySupplierIds(session, shopId, supplierIds);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<OutStorageRelation> getOutStorageRelationBySupplierIds(Long shopId, Long... supplierIds) {
    if (shopId == null || ArrayUtils.isEmpty(supplierIds)) {
      return new ArrayList<OutStorageRelation>();
    }
    Session session = this.getSession();
    try {
      Query q = SQL.getOutStorageRelationBySupplierIds(session, shopId, supplierIds);
      return q.list();
    } finally {
      release(session);
    }
  }

  public PrintTemplateDTO getPrintTemplateInfoById(Long templateId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getPrintTemplateInfoById(session, templateId);
      Object[] obj = (Object[]) q.uniqueResult();
      if (obj != null) {
        PrintTemplateDTO templateDTO = new PrintTemplateDTO();
        templateDTO.setId(Long.parseLong(obj[0].toString()));
        templateDTO.setName(obj[1].toString());
        templateDTO.setOrderType(OrderTypes.valueOf(obj[2].toString()));
        return templateDTO;
      }
      return null;
    } finally {
      release(session);
    }
  }

  public PrintTemplate getPrintTemplateFullById(Long templateId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getPrintTemplateFullById(session, templateId);
      return (PrintTemplate) q.uniqueResult();
    } finally {
      release(session);
    }
  }

  /**
   * 通过shopId和customerId获取预约金
   * add by zhuj
   *
   * @param shopId
   * @param customerId
   * @return
   */
  public CustomerDeposit queryCustomerDepositByShopIdAndCustomerId(Long shopId, Long customerId) {
    Session session = getSession();
    try {
      return CollectionUtil.getFirst((List<CustomerDeposit>) (SQL.getCustomerDepositByShopIdAndCustomerId(session, shopId, customerId).list()));
    } finally {
      release(session);
    }
  }

  public List<CustomerDeposit> queryCustomerDepositsByShopIdAndCustomerIds(Long shopId, List<Long> customerIds) {
    Session session = getSession();
    try {
      return SQL.getCustomerDepositsByShopIdAndCustomerIds(session, shopId, customerIds).list();
    } finally {
      release(session);
    }
  }

  /**
   * 通过shopId、customerId、supplier查询预约金订单
   *
   * @param shopId
   * @param customerId
   * @param supplierID
   * @return
   */
  public List<DepositOrder> queryDepositOrderByShopIdAndCustomerIdOrSupplierId(Long shopId, Long customerId, Long supplierID, List<Long> inOut, SortObj sortObj, Pager pager) {
    Session session = getSession();
    try {
      Query query = SQL.getDepositOrderByShopIdAndCustomerIdOrSupplier(session, shopId, customerId, supplierID, inOut, sortObj);
      query.setFirstResult(pager.getRowStart());
      query.setMaxResults(pager.getPageSize());
      return (List<DepositOrder>) query.list();
    } finally {
      release(session);
    }
  }

  public int countDepositOrderByShopIdAndCustomerIdOrSupplierId(Long shopId, Long customerId, Long supplierID, List<Long> inOut) {
    Session session = getSession();
    try {
      Query query = SQL.countDepositOrders(session, shopId, customerId, supplierID, inOut);
      return ((Long) query.uniqueResult()).intValue();
    } finally {
      release(session);
    }
  }

  public DepositOrder queryDepositOrderByShopIdAndCustomerIdOrSupplierIdAndRelatedOrderId(Long shopId, Long customerId, Long supplierId, Long relatedOrderId) {
    Session session = getSession();
    try {
      Query query = SQL.queryDepositOrderByShopIdCustomerIdOrSupplierId(session, shopId, customerId, supplierId, relatedOrderId);
      return (DepositOrder) CollectionUtil.getFirst(query.list());
    } finally {
      release(session);
    }
  }

  public List<DepositOrder> queryDepositOrderByConditions(Long shopId, Long customerId, Long supplierId, List<Long> inOut, SortObj sortObj, Long startTime, Long endTime, String type, Pager pager) {
    Session session = getSession();
    try {
      Query query = SQL.queryDepositOrderByConditions(session, shopId, customerId, supplierId, inOut, sortObj, startTime, endTime, type);
      query.setFirstResult(pager.getRowStart());
      query.setMaxResults(pager.getPageSize());
      return (List<DepositOrder>) query.list();
    } finally {
      release(session);
    }
  }

  public List<DepositOrder> queryDepositOrderByIdsAndType(Long shopId, List<Long> ids, List<Long> inOut, SortObj sortObj, Long startTime, Long endTime, String type, Pager pager) {
    Session session = getSession();
    try {
      Query query = SQL.queryDepositOrderByIdsAndType(session, shopId, ids, inOut, sortObj, startTime, endTime, type);
      query.setFirstResult(pager.getRowStart());
      query.setMaxResults(pager.getPageSize());
      return (List<DepositOrder>) query.list();
    } finally {
      release(session);
    }
  }

  public int countDepositOrderByConditions(Long shopId, Long customerId, Long supplierId, List<Long> inOut, Long startTime, Long endTime, String type) {
    Session session = getSession();
    try {
      Query query = SQL.countDepositOrdersByConditions(session, shopId, customerId, supplierId, inOut, startTime, endTime, type);
      return ((Long) query.uniqueResult()).intValue();
    } finally {
      release(session);
    }
  }

  public List<DepositOrder> queryDepositOrderByShopIdAndIdsAndType(Long shopId, List<Long> ids, String type) {
    Session session = getSession();
    try {
      Query query = SQL.queryDepositOrderByShopIdAndIdsAndType(session, shopId, ids, type);
      return query.list();
    } finally {
      release(session);
    }
  }

  public int countDepositOrdersByShopIdAndIdsAndType(Long shopId, List<Long> ids, String type) {
    Session session = getSession();
    try {
      Query query = SQL.countDepositOrdersByIdsAndType(session, shopId, ids, type);
      return ((Long) query.uniqueResult()).intValue();
    } finally {
      release(session);
    }
  }

  public List<CustomerDepositDTO> getCustomerDepositForReindex(Long shopId, List<Long> ids) {
    Session session = getSession();
    try {
      Query query = SQL.getCustomerDepositForReindex(session, shopId, ids);
      List<CustomerDeposit> depositList = query.list();
      if (CollectionUtils.isEmpty(depositList)) return null;
      List<CustomerDepositDTO> depositDTOList = new ArrayList<CustomerDepositDTO>();
      for (CustomerDeposit deposit : depositList) {
        depositDTOList.add(deposit.toDTO());
      }
      return depositDTOList;
    } finally {
      release(session);
    }
  }

  /**
   * @param preBuyOrderId
   * @return
   */
  public List<PreBuyOrderItem> getPreBuyOrderItemsByPreBuyOrderId(Long preBuyOrderId) {

    Session session = getSession();
    try {
      Query q = SQL.getPreBuyOrderItemsByPreBuyOrderId(session, preBuyOrderId);
      return (List<PreBuyOrderItem>) q.list();
    } finally {
      release(session);
    }
  }

  public List<PreBuyOrderItem> getLatestPreBuyOrderItem(int pageStart, int pageSize, ShopKind shopKind) {
    Session session = getSession();
    try {
      Query q = SQL.getLatestPreBuyOrderItem(session, pageStart, pageSize, shopKind);
      return (List<PreBuyOrderItem>) q.list();
    } finally {
      release(session);
    }
  }

  public List<Object[]> getPreBuyOrderItemDetailDTO(PreBuyOrderSearchCondition condition) throws ParseException {
    Session session = getSession();
    try {
      Query q = SQL.getPreBuyOrderItemDetailDTO(session, condition);
      return q.list();
    } finally {
      release(session);
    }
  }

  /**
   * @param quotedPreBuyOrderId
   * @return
   */
  public List<QuotedPreBuyOrderItem> getQuotedPreBuyOrderItemsByQuotedPreBuyOrderId(Long quotedPreBuyOrderId) {

    Session session = getSession();
    try {
      Query q = SQL.getQuotedPreBuyOrderItemsByQuotedPreBuyOrderId(session, quotedPreBuyOrderId);
      return (List<QuotedPreBuyOrderItem>) q.list();
    } finally {
      release(session);
    }
  }

  /**
   * @param preBuyOrderItemId
   * @return
   */
  public List<QuotedPreBuyOrderItem> getQuotedPreBuyOrderItemsByPreBuyOrderItemId(Long shopId, Long preBuyOrderItemId) {

    Session session = getSession();
    try {
      Query q = SQL.getQuotedPreBuyOrderItemsByPreBuyOrderItemId(session, shopId, preBuyOrderItemId);
      return (List<QuotedPreBuyOrderItem>) q.list();
    } finally {
      release(session);
    }
  }

  public List<PreBuyOrderItem> getPreBuyOrderItemDTOByIds(Long shopId, Long... preBuyOrderItemIds) {
    Session session = getSession();
    try {
      Query q = SQL.getPreBuyOrderItemDTOByIds(session, shopId, preBuyOrderItemIds);
      return q.list();
    } finally {
      release(session);
    }
  }

  /**
   * @param preBuyOrderId
   * @return
   */
  public List<QuotedPreBuyOrderItem> getQuotedPreBuyOrderItemsByPreBuyOrderId(Long shopId, Long preBuyOrderId) {

    Session session = getSession();
    try {
      Query q = SQL.getQuotedPreBuyOrderItemsByPreBuyOrderId(session, shopId, preBuyOrderId);
      return (List<QuotedPreBuyOrderItem>) q.list();
    } finally {
      release(session);
    }
  }

  /**
   * @param shopId
   * @return
   */
  public Long countPreBuyOrderItems(Long shopId) {
    Session session = getSession();
    try {
      Query q = SQL.countPreBuyOrderItems(session, shopId);
      return (Long) q.uniqueResult();
    } finally {
      release(session);
    }
  }

  /**
   * @param shopId
   * @return
   */
  public Long countValidPreBuyOrderItems(Long shopId) {

    Session session = getSession();
    try {
      Query q = SQL.countValidPreBuyOrderItems(session, shopId);
      return (Long) q.uniqueResult();
    } finally {
      release(session);
    }
  }

  /**
   * @param shopId
   * @return
   */
  public Long countQuotedPreBuyOrderItems(Long shopId) {

    Session session = getSession();
    try {
      Query q = SQL.countQuotedPreBuyOrderItems(session, shopId);
      return (Long) q.uniqueResult();
    } finally {
      release(session);
    }
  }

  /**
   * @param preBuyOrderId
   * @return
   */
  public List<QuotedPreBuyOrder> getQuotedPreBuyOrdersByPreBuyOrderId(Long preBuyOrderId) {

    Session session = getSession();
    try {
      Query q = SQL.getQuotedPreBuyOrdersByPreBuyOrderId(session, preBuyOrderId);
      return (List<QuotedPreBuyOrder>) q.list();
    } finally {
      release(session);
    }
  }


  /**
   * @param shopId
   * @return
   */
  public Long countQuotedPreBuyOrders(Long shopId) {

    Session session = getSession();
    try {
      Query q = SQL.countQuotedPreBuyOrders(session, shopId);
      return (Long) q.uniqueResult();
    } finally {
      release(session);
    }
  }

  public Long countOrdersFromQuotedPreBuyOrder(Long shopId) {
    Session session = getSession();
    try {
      Query q = SQL.countOrdersFromQuotedPreBuyOrder(session, shopId);
      return (Long) q.uniqueResult();
    } finally {
      release(session);
    }
  }

  public int countQuotedPreBuyOrderSupplier(QuotedPreBuyOrderSearchConditionDTO conditionDTO) {
    Session session = getSession();
    try {
      Query q = SQL.countQuotedPreBuyOrderSupplier(session, conditionDTO);
      return NumberUtil.intValue(q.uniqueResult());
    } finally {
      release(session);
    }
  }

  /**
   * @param quotedPreBuyOrderItemId
   * @return
   */
  public List<QuotedPreBuyOrderItem> getQuotedPreBuyOrderItemsByItemId(Long... quotedPreBuyOrderItemId) {

    Session session = getSession();
    try {
      Query q = SQL.getQuotedPreBuyOrderItemsByItemId(session, quotedPreBuyOrderItemId);
      return (List<QuotedPreBuyOrderItem>) q.list();
    } finally {
      release(session);
    }
  }

  /**
   * @param quotedPreBuyOrderItemId
   * @return
   */
  public PreBuyOrder getPreBuyOrderByQuotedPreBuyOrderItemId(Long quotedPreBuyOrderItemId) {

    Session session = getSession();
    try {
      Query q = SQL.getPreBuyOrderByQuotedPreBuyOrderItemId(session, quotedPreBuyOrderItemId);
      List<PreBuyOrder> preBuyOrderList = (List<PreBuyOrder>) q.list();
      if (CollectionUtils.isNotEmpty(preBuyOrderList)) {
        return preBuyOrderList.get(0);
      }
      return null;
    } finally {
      release(session);
    }
  }

  public List<Object[]> getQuotedPreBuyOrder(QuotedPreBuyOrderSearchConditionDTO conditionDTO) {
    Session session = getSession();
    try {
      Query q = SQL.getQuotedPreBuyOrder(session, conditionDTO);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<QuotedPreBuyOrderItem> getQuotedPreBuyOrderItemDTOsByIds(Long shopId, Long... quotedPreBuyOrderItemIds) {
    Session session = getSession();
    try {
      Query q = SQL.getQuotedPreBuyOrderItemDTOsByIds(session, shopId, quotedPreBuyOrderItemIds);
      return q.list();
    } finally {
      release(session);
    }
  }

  public Long countQuotedPreBuyOrdersByPreBuyOrderId(Long shopId, Long preBuyOrderId) {
    Session session = getSession();
    try {
      Query q = SQL.countQuotedPreBuyOrdersByPreBuyOrderId(session, shopId, preBuyOrderId);
      return (Long) q.uniqueResult();
    } finally {
      release(session);
    }
  }

  public int countSupplierOtherQuotedItems(Long quoterShopId, Long preBuyerShopId, Long quotedPreBuyOrderItemId) {
    Session session = getSession();
    try {
      Query q = SQL.countSupplierOtherQuotedItems(session, quoterShopId, preBuyerShopId, quotedPreBuyOrderItemId);
      return NumberUtil.intValue(q.uniqueResult());
    } finally {
      release(session);
    }
  }

  public List<QuotedPreBuyOrderItem> getSupplierOtherQuotedItems(Long quoterShopId, Long preBuyerShopId, Long quotedPreBuyOrderItemId, Pager pager) {
    Session session = getSession();
    try {
      Query q = SQL.getSupplierOtherQuotedItems(session, quoterShopId, preBuyerShopId, quotedPreBuyOrderItemId, pager);
      return q.list();
    } finally {
      release(session);
    }
  }

  public Inventory getBcgogoRecommendSupplierId(List<Long> productIds, Long shopId, Double comparePrices) {
    Session session = getSession();
    try {
      Query q = SQL.getBcgogoRecommendSupplierId(session, productIds, shopId, comparePrices);
      return (Inventory) CollectionUtil.getFirst(q.list());
    } finally {
      release(session);
    }
  }

  public SupplierInventory getLatestConsumeSupplier(Long productId, Long shopId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getLatestConsumeSupplier(session, shopId, productId);
      List<SupplierInventory> supplierInventories = q.list();
      return CollectionUtil.getFirst(supplierInventories);
    } finally {
      release(session);
    }
  }


  public List<Long> getWashBeautyShopIdList(Set<Long> shopIdSet) {
    Session session = this.getSession();
    try {
      Query q = SQL.getWashBeautyShopIdList(session, shopIdSet);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<Long> getMemberCardOrderShopIdList(Set<Long> shopIdSet) {
    Session session = this.getSession();
    try {
      Query q = SQL.getMemberCardOrderShopIdList(session, shopIdSet);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<Long> getMemberCardReturnShopIdList(Set<Long> shopIdSet) {
    Session session = this.getSession();
    try {
      Query q = SQL.getMemberCardReturnShopIdList(session, shopIdSet);
      return q.list();
    } finally {
      release(session);
    }
  }


  public List<Long> getSalesOrderShopIdList(Set<Long> shopIdSet) {
    Session session = this.getSession();
    try {
      Query q = SQL.getSalesOrderShopIdList(session, shopIdSet);
      return q.list();
    } finally {
      release(session);
    }
  }


  public List<Long> getSalesReturnShopIdList(Set<Long> shopIdSet) {
    Session session = this.getSession();
    try {
      Query q = SQL.getSalesReturnShopIdList(session, shopIdSet);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<ServiceAchievementHistory> getServiceAchievementHistory(Long shopId, Long serviceId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getServiceAchievementHistory(session, shopId, serviceId);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<ProductAchievementHistory> getProductAchievementHistory(Long shopId, Long productId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getProductAchievementHistory(session, shopId, productId);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<MemberAchievementHistory> getMemberAchievementHistory(Long shopId, Long vestDate, MemberOrderType memberOrderType) {
    Session session = this.getSession();
    try {
      Query q = SQL.getMemberAchievementHistory(session, shopId, vestDate, memberOrderType);
      return q.list();
    } finally {
      release(session);
    }
  }

  public void deleteShopAchievementConfig(Long shopId) {
    Session session = getSession();
    try {
      Query query = SQL.deleteShopAchievementConfig(session, shopId);
      query.executeUpdate();
    } finally {
      release(session);
    }
  }

  public void deleteAssistantAchievementStat(Long shopId) {
    Session session = getSession();
    try {
      Query query = SQL.deleteAssistantAchievementStat(session, shopId);
      query.executeUpdate();
    } finally {
      release(session);
    }
  }


  public void deleteAssistantServiceRecord(Long shopId, Long statTime) {
    Session session = getSession();
    try {
      Query query = SQL.deleteAssistantServiceRecord(session, shopId, statTime);
      query.executeUpdate();
    } finally {
      release(session);
    }
  }

  public void deleteAssistantBusinessAccountRecord(Long shopId, Long statTime) {
    Session session = getSession();
    try {
      Query query = SQL.deleteAssistantBusinessAccountRecord(session, shopId, statTime);
      query.executeUpdate();
    } finally {
      release(session);
    }
  }

  public void deleteAssistantProductRecord(Long shopId, Long statTime) {
    Session session = getSession();
    try {
      Query query = SQL.deleteAssistantProductRecord(session, shopId, statTime);
      query.executeUpdate();
    } finally {
      release(session);
    }
  }

  public void deleteAssistantMemberRecord(Long shopId, Long statTime) {
    Session session = getSession();
    try {
      Query query = SQL.deleteAssistantMemberRecord(session, shopId, statTime);
      query.executeUpdate();
    } finally {
      release(session);
    }
  }

  public List<ShopAchievementConfig> getShopAchievementConfig(Long shopId, Long achievementRecordId, AssistantRecordType assistantRecordType, Pager pager) {
    Session session = this.getSession();
    try {
      Query q = SQL.getShopAchievementConfig(session, shopId, achievementRecordId, assistantRecordType, pager);
      return q.list();
    } finally {
      release(session);
    }
  }

  public int countShopAchievementConfig(Long shopId, Long achievementRecordId, AssistantRecordType assistantRecordType) {
    Session session = this.getSession();
    try {
      Query q = SQL.countShopAchievementConfig(session, shopId, achievementRecordId, assistantRecordType);
      Long result = (Long) q.uniqueResult();
      return result == null ? 0 : result.intValue();
    } finally {
      release(session);
    }
  }


  public List<Long> countAssistantStatByCondition(AssistantStatSearchDTO assistantStatSearchDTO) {
    Session session = this.getSession();
    try {
      Query q = SQL.countAssistantStatByCondition(session, assistantStatSearchDTO);
      List<Long> list = q.list();
      return list;
    } finally {
      release(session);
    }
  }

  public List<AssistantAchievementStat> getAssistantStatByIds(AssistantStatSearchDTO assistantStatSearchDTO, Set<Long> ids) {
    Session session = this.getSession();
    try {
      Query q = SQL.getAssistantStatByIds(session, assistantStatSearchDTO, ids);
      return q.list();
    } finally {
      release(session);
    }
  }


  public int countAssistantRecordByCondition(AssistantStatSearchDTO assistantStatSearchDTO, Set<OrderTypes> orderTypes) {
    Session session = this.getSession();
    try {
      Query q = SQL.countAssistantRecordByCondition(session, assistantStatSearchDTO, orderTypes);
      Long result = (Long) q.uniqueResult();
      return result == null ? 0 : result.intValue();
    } finally {
      release(session);
    }
  }

  public List getAssistantRecordByPager(AssistantStatSearchDTO assistantStatSearchDTO, Set<OrderTypes> orderTypes, Pager pager) {
    Session session = this.getSession();
    try {
      Query q = SQL.getAssistantRecordByPager(session, assistantStatSearchDTO, orderTypes, pager);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<SalesReturn> countSalesReturnAchievements(long shopId, long startTime, long endTime) {
    Session session = getSession();
    try {
      Query q = null;
      q = SQL.countSalesReturnAchievements(session, shopId, startTime, endTime);
      List<SalesReturn> salesReturnList = q.list();
      return salesReturnList;
    } finally {
      release(session);
    }
  }

  public List<ServiceAchievementHistory> getLastedServiceAchievementHistory(Long shopId, Long serviceId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getLastedServiceAchievementHistory(session, shopId, serviceId);
      return q.list();
    } finally {
      release(session);
    }
  }

  public Long validateCouponNoUsed(Long shopId, String couponType, String couponNo) {
    Session session = this.getSession();
    try {
      Query q = SQL.validateCouponNoUsed(session, shopId, couponType, couponNo);
      return (Long) q.uniqueResult();
    } finally {
      release(session);
    }
  }

  public String getRandomNProductIdStr(Long shopId, int n) {
    Session session = this.getSession();
    try {
      Query q = SQL.getRandomNProductIdStr(session, shopId, n);
      List<Inventory> inventories = q.list();
      StringBuffer sb = new StringBuffer();
      if (CollectionUtils.isNotEmpty(inventories)) {
        for (Inventory inventory : inventories) {
          sb.append(inventory.getId().toString());
          sb.append(",");
        }
      }
      return sb.toString();
    } finally {
      release(session);
    }
  }

  public Object[] getLatestPushMessage(Long shopId, Long receiverId, ShopKind shopKind, PushMessageType... types) {

    Session session = getSession();
    try {
      Query q = SQL.getLatestPushMessage(session, shopId, receiverId, shopKind, types);
      return (Object[]) q.uniqueResult();
    } finally {
      release(session);
    }
  }

  public List<PushMessage> getLatestUnPushPushMessages(Long receiverId, PushMessageType... types) throws ParseException {
    Session session = getSession();
    try {
      Query q = SQL.getLatestUnPushPushMessages(session, receiverId, types);
      return q.list();
    } finally {
      release(session);
    }
  }

  public void updateRemindEvent(Long shopId, Long customerId, Long supplierId) {
    Session session = this.getSession();
    try {
      Query query = SQL.updateRemindEvent(session, shopId, customerId, supplierId);
      query.executeUpdate();
    } finally {
      release(session);
    }
  }

  public void updateRemindEvent2(Long shopId, Long customerId, Long supplierId) {
    Session session = this.getSession();
    try {
      Query query = SQL.updateRemindEvent2(session, shopId, customerId, supplierId);
      query.executeUpdate();
    } finally {
      release(session);
    }
  }

  public List<Payable> getPayableDTOBySupplierIdAndOrderType(Long shopId, Long supplierId, OrderTypes orderType) {
    Session session = getSession();
    try {
      Query q = SQL.getPayableDTOBySupplierIdAndOrderType(session, shopId, supplierId, orderType);
      List<Payable> payableList = (List<Payable>) q.list();
      if (CollectionUtils.isEmpty(payableList)) {
        return null;
      }
      return payableList;

    } finally {
      release(session);
    }
  }

  public List<Object[]> getLatestPushMessage(Long shopId, PushMessageType... types) {

    Session session = getSession();
    try {
      Query q = SQL.getLatestPushMessage(session, shopId, types);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<Object[]> getLatestPushMessage(Long shopId, int start, int limit, PushMessageType... type) {
    Session session = getSession();
    try {
      Query q = SQL.getLatestPushMessage(session, shopId, start, limit, type);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<PushMessage> getTalkMessageList(TalkMessageCondition condition) {
    Session session = getSession();
    try {
      Query q = SQL.getTalkMessageList(session, condition);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<ShopTalkMessage> getShopTalkMessage(String appUserNo, String vehicleNo, Long shopId, int start, int limit) {
    Session session = getSession();
    try {
      Query q = SQL.getShopTalkMessage(session, appUserNo, vehicleNo, shopId, start, limit);
      return q.list();
    } finally {
      release(session);
    }
  }

//  public int countTalkMessageList(Long receiverId, Long shopId, PushMessageType... type) {
//    Session session = getSession();
//    try {
//      Query q = SQL.countTalkMessageList(session, receiverId, shopId, type);
//      return q.list().size();
//    } finally {
//      release(session);
//    }
//  }

  public int countShopTalkMessageList(String appUserNo, String vehicleNo, Long shopId) {
    Session session = getSession();
    try {
      Query q = SQL.countShopTalkMessageList(session, appUserNo, vehicleNo, shopId);
      return NumberUtil.intValue(q.uniqueResult());
    } finally {
      release(session);
    }
  }

  public Integer countLatestPushMessage(Long shopId, PushMessageType... type) {
    Session session = getSession();
    try {
      Query q = SQL.countLatestPushMessage(session, shopId, type);
      return Integer.valueOf(q.uniqueResult().toString());
    } finally {
      release(session);
    }
  }

  public void updatePushMessageReceiverStatusByMessageId(PushMessageReceiverStatus pushMessageReceiverStatus, Long... messageId) {
    Session session = getSession();
    try {
      Query q = SQL.updatePushMessageReceiverByStatusMessageId(session, pushMessageReceiverStatus, messageId);
      q.executeUpdate();
    } finally {
      release(session);
    }
  }

  public List<PushMessageSource> getUnreadPushMessageSourceBySourceIds(Set<Long> sourceIdSet, PushMessageSourceType... pushMessageSourceType) {
    Session session = getSession();
    try {
      Query q = SQL.getUnreadPushMessageSourceBySourceIds(session, sourceIdSet, pushMessageSourceType);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<PushMessageReceiver> getPushMessageReceiverBySourceId(Long shopId, Long sourceId, Long pushMessageReceiverShopId, PushMessageSourceType... pushMessageSourceType) {
    Session session = getSession();
    try {
      Query q = SQL.getPushMessageReceiverBySourceId(session, shopId, sourceId, pushMessageReceiverShopId, pushMessageSourceType);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<Object[]> getPushMessageAndReceiverBySourceId(Long shopId, Long sourceId, Long pushMessageReceiverShopId, PushMessageSourceType... pushMessageSourceType) {
    Session session = getSession();
    try {
      Query q = SQL.getPushMessageAndReceiverBySourceId(session, shopId, sourceId, pushMessageReceiverShopId, pushMessageSourceType);
      return q.list();
    } finally {
      release(session);
    }
  }


  public List<PushMessageReceiver> getUnreadPushMessageReceiver(Long shopId, Long receiverId, PushMessageType... pushMessageType) {
    Session session = getSession();
    try {
      Query q = SQL.getUnreadPushMessageReceiver(session, shopId, receiverId, pushMessageType);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<PushMessage> getPushMessageByCreatorId(Long creatorId, PushMessageType pushMessageType) {
    Session session = getSession();
    try {
      Query q = SQL.getPushMessageByCreatorId(session, creatorId, pushMessageType);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<PushMessage> getPushMessageByType(PushMessageType pushMessageType) {
    Session session = getSession();
    try {
      Query q = SQL.getPushMessageByType(session, pushMessageType);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<PushMessageReceiver> getPushMessageReceiverByMessageId(Long shopId, PushMessageReceiverStatus status, Long... messageIds) {
    Session session = getSession();
    try {
      Query q = SQL.getPushMessageReceiverByMessageId(session, shopId, status, messageIds);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<PushMessage> getPushMessageBySourceId(Long sourceShopId, Long sourceId, PushMessageType pushMessageType, PushMessageSourceType pushMessageSourceType) {
    Session session = getSession();
    try {
      Query q = SQL.getPushMessageBySourceId(session, sourceShopId, sourceId, pushMessageType, pushMessageSourceType);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<PushMessage> getAllPushMessage() {
    Session session = getSession();
    try {
      Query q = SQL.getAllPushMessage(session);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<PushMessage> getMovePushMessage(PushMessageType[] pushMessageTypes, Long keepDate, Long startId, int pageSize) {
    Session session = getSession();
    try {
      Query q = SQL.getMovePushMessage(session, pushMessageTypes, keepDate, startId, pageSize);
      return q.list();
    } finally {
      release(session);
    }
  }

  public PushMessageBuildTask getLatestPushMessageBuildTask(PushMessageScene... scene) {
    Session session = getSession();
    try {
      Query q = SQL.getLatestPushMessageBuildTask(session, scene);
      return (PushMessageBuildTask) q.uniqueResult();
    } finally {
      release(session);
    }
  }

  public List<PushMessageReceiver> getMovePushMessageReceiver(Long startId, int pageSize) {
    Session session = getSession();
    try {
      Query q = SQL.getMovePushMessageReceiver(session, startId, pageSize);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<PushMessage> getUnReadPushMessageByReceiverId(Long receiverId, int limit, PushMessageType... types) {
    Session session = getSession();
    try {
      Query q = SQL.getUnReadPushMessageByReceiverId(session, receiverId, limit, types);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<Object[]> getAppUserUnReadPushMessage(int limit, PushMessageType... types) {
    Session session = getSession();
    try {
      Query q = SQL.getAppUserUnReadPushMessage(session, limit, types);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<PushMessageFeedbackRecord> getMovePushMessageFeedbackRecord(Long startId, int pageSize) {
    Session session = getSession();
    try {
      Query q = SQL.getMovePushMessageFeedbackRecord(session, startId, pageSize);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<Long> getPushMessageIdsBySource(Long shopId, Long sourceId, PushMessageSourceType pushMessageSourceType) {
    Session session = getSession();
    try {
      Query q = SQL.getPushMessageIdsBySource(session, shopId, sourceId, pushMessageSourceType);
      return (List<Long>) q.list();
    } finally {
      release(session);
    }
  }

  public List<PushMessageSource> getPushMessageSourceBySourceId(Long sourceId) {
    Session session = getSession();
    try {
      Query q = SQL.getPushMessageSourceBySourceId(session, sourceId);
      return (List<PushMessageSource>) q.list();
    } finally {
      release(session);
    }
  }

  public List<PushMessageSource> getPushMessageSourcesByMessageIds(Set<Long> messageIds) {
    Session session = getSession();
    try {
      Query q = SQL.getPushMessageSourcesByMessageIds(session, messageIds);
      return (List<PushMessageSource>) q.list();
    } finally {
      release(session);
    }
  }

  public List<ProductRecommend> getMoveProductRecommendList(int start, int pageSize) {

    Session session = getSession();
    try {
      Query q = SQL.getMoveProductRecommendList(session, start, pageSize);
      return (List<ProductRecommend>) q.list();
    } finally {
      release(session);
    }
  }

  public List<PreBuyOrderItemRecommend> getMovePreBuyOrderItemRecommendList(int start, int pageSize) {

    Session session = getSession();
    try {
      Query q = SQL.getMovePreBuyOrderItemRecommendList(session, start, pageSize);
      return (List<PreBuyOrderItemRecommend>) q.list();
    } finally {
      release(session);
    }
  }

  public List<ShopRecommend> getMoveShopRecommendList(int start, int pageSize) {

    Session session = getSession();
    try {
      Query q = SQL.getMoveShopRecommendList(session, start, pageSize);
      return (List<ShopRecommend>) q.list();
    } finally {
      release(session);
    }
  }

  public List<PushMessageReceiverRecord> getMovePushMessageReceiverRecordListByPushTime(Long pushTime, int start, int pageSize) {

    Session session = getSession();
    try {
      Query q = SQL.getMovePushMessageReceiverRecordListByPushTime(session, pushTime, start, pageSize);
      return (List<PushMessageReceiverRecord>) q.list();
    } finally {
      release(session);
    }
  }

  public List<SalesStatDTO> getLastWeekSalesByShopId(Long shopId, Long startTime, Long endTime, Long... productId) {
    Session session = getSession();
    try {
      Query q = SQL.getLastWeekSalesByShopId(session, shopId, startTime, endTime, productId);
      List list = q.list();
      List<SalesStatDTO> salesStatDTOList = new ArrayList<SalesStatDTO>();

      if (CollectionUtils.isNotEmpty(list)) {
        for (int i = 0; i < list.size(); i++) {
          Object[] array = (Object[]) list.get(i);
          SalesStatDTO salesStatDTO = new SalesStatDTO();
          salesStatDTO.setProductId((Long) array[0]);
          salesStatDTO.setAmount((Double) array[1]);
          salesStatDTOList.add(salesStatDTO);
        }
      }
      return salesStatDTOList;
    } finally {
      release(session);
    }
  }

  public List<SalesStatDTO> getLastWeekSalesChangeByShopId(Long shopId, Long startTime, Long endTime, Long... productId) {
    Session session = getSession();
    try {
      Query q = SQL.getLastWeekSalesChangeByShopId(session, shopId, startTime, endTime, productId);
      List list = q.list();
      List<SalesStatDTO> salesStatDTOList = new ArrayList<SalesStatDTO>();

      if (CollectionUtils.isNotEmpty(list)) {
        for (int i = 0; i < list.size(); i++) {
          Object[] array = (Object[]) list.get(i);
          SalesStatDTO salesStatDTO = new SalesStatDTO();
          salesStatDTO.setProductId((Long) array[0]);
          salesStatDTO.setAmount((Double) array[1]);
          salesStatDTOList.add(salesStatDTO);
        }
      }
      return salesStatDTOList;
    } finally {
      release(session);
    }
  }

  public List<PurchaseInventoryStatDTO> getLastWeekInventoryByShopId(Long shopId, Long startTime, Long endTime) {
    Session session = getSession();
    try {
      Query q = SQL.getLastWeekInventoryByShopId(session, shopId, startTime, endTime);
      List list = q.list();
      List<PurchaseInventoryStatDTO> purchaseInventoryStatDTOList = new ArrayList<PurchaseInventoryStatDTO>();

      if (CollectionUtils.isNotEmpty(list)) {
        for (int i = 0; i < list.size(); i++) {
          Object[] array = (Object[]) list.get(i);
          PurchaseInventoryStatDTO purchaseInventoryStatDTO = new PurchaseInventoryStatDTO();
          purchaseInventoryStatDTO.setProductId((Long) array[0]);
          purchaseInventoryStatDTO.setAmount((Double) array[1]);
          purchaseInventoryStatDTOList.add(purchaseInventoryStatDTO);
        }
      }
      return purchaseInventoryStatDTOList;
    } finally {
      release(session);
    }
  }

  public List<PurchaseInventoryStatDTO> getLastWeekInventoryChangeByShopId(Long shopId, Long startTime, Long endTime) {
    Session session = getSession();
    try {
      Query q = SQL.getLastWeekInventoryChangeByShopId(session, shopId, startTime, endTime);
      List list = q.list();
      List<PurchaseInventoryStatDTO> purchaseInventoryStatDTOList = new ArrayList<PurchaseInventoryStatDTO>();

      if (CollectionUtils.isNotEmpty(list)) {
        for (int i = 0; i < list.size(); i++) {
          Object[] array = (Object[]) list.get(i);
          PurchaseInventoryStatDTO purchaseInventoryStatDTO = new PurchaseInventoryStatDTO();
          purchaseInventoryStatDTO.setProductId((Long) array[0]);
          purchaseInventoryStatDTO.setAmount((Double) array[1]);
          purchaseInventoryStatDTOList.add(purchaseInventoryStatDTO);
        }
      }
      return purchaseInventoryStatDTOList;
    } finally {
      release(session);
    }
  }

  public List<SalesInventoryWeekStat> getSalesInventoryWeekStatByCondition(int statYear, int statMonth, int statDay, ShopKind shopKind, String productName, String productBrand) {

    Session session = getSession();
    try {
      Query q = SQL.getSalesInventoryWeekStatByCondition(session, statYear, statMonth, statDay, shopKind, productName, productBrand);
      return (List<SalesInventoryWeekStat>) q.list();
    } finally {
      release(session);
    }
  }

  public void deleteOldProductRecommend(Long shopId, ProductRecommendType productRecommendType) {

    Session session = getSession();
    try {
      Query q = SQL.deleteOldProductRecommend(session, shopId, productRecommendType);
      q.executeUpdate();
    } finally {
      release(session);
    }
  }

  public void deleteOldPreBuyOrderItemRecommend(Long shopId) {

    Session session = getSession();
    try {
      Query q = SQL.deleteOldPreBuyOrderItemRecommend(session, shopId);
      q.executeUpdate();
    } finally {
      release(session);
    }
  }

  public void deleteOldShopRecommend(Long shopId) {

    Session session = getSession();
    try {
      Query q = SQL.deleteOldShopRecommend(session, shopId);
      q.executeUpdate();
    } finally {
      release(session);
    }
  }


  public List<PushMessageSource> getPushMessageSourceByMessageId(Long messageId, PushMessageSourceType pushMessageSourceType) {
    Session session = getSession();
    try {
      Query q = SQL.getPushMessageSourceByMessageId(session, messageId, pushMessageSourceType);
      return (List<PushMessageSource>) q.list();
    } finally {
      release(session);
    }
  }

  public List<ShopRecommend> getRecommendShopByShopId(Long shopId, Pager pager) {
    Session session = getSession();
    try {
      Query q = SQL.getRecommendShopByShopId(session, shopId, pager);
      return (List<ShopRecommend>) q.list();
    } finally {
      release(session);
    }
  }

  public int countProductRecommendByShopId(Long shopId, DeletedType deletedType) {
    Session session = getSession();
    try {
      Query q = SQL.countProductRecommendByShopId(session, shopId, deletedType);
      Long result = (Long) q.uniqueResult();
      return result == null ? 0 : result.intValue();
    } finally {
      release(session);
    }
  }

  public List<ProductRecommend> getRecommendProductByShopId(Long shopId, DeletedType deletedType, int start, int rows) {
    Session session = getSession();
    try {
      Query q = SQL.getRecommendProductByShopId(session, shopId, deletedType, start, rows);
      return (List<ProductRecommend>) q.list();
    } finally {
      release(session);
    }
  }


  public int countWholesalerProductRecommendByShopId(Long shopId, DeletedType deletedType) {
    Session session = getSession();
    try {
      Query q = SQL.countWholesalerProductRecommendByShopId(session, shopId, deletedType);
      Long result = (Long) q.uniqueResult();
      return result == null ? 0 : result.intValue();
    } finally {
      release(session);
    }
  }

  public List<ProductRecommend> getRecommendProduct(ProductSearchCondition condition) {
    Session session = getSession();
    try {
      Query q = SQL.getRecommendProduct(session, condition);
      return (List<ProductRecommend>) q.list();
    } finally {
      release(session);
    }
  }

  public List<PreBuyOrderItemRecommend> getWholesalerProductRecommendByPager(Long shopId, DeletedType deletedType, Pager pager) {
    Session session = getSession();
    try {
      Query q = SQL.getWholesalerProductRecommendByPager(session, shopId, deletedType, pager);
      return (List<PreBuyOrderItemRecommend>) q.list();
    } finally {
      release(session);
    }
  }

  public List<PreBuyOrderItem> getPreBuyOrderItemsByIdSet(Set<Long> idSet) {
    Session session = this.getSession();
    try {
      Query q = SQL.getPreBuyOrderItemsByIdSet(session, idSet);
      return (List<PreBuyOrderItem>) q.list();
    } finally {
      release(session);
    }
  }

  public int countLastWeekSalesInventoryStatByShopId(Long shopId, int weekOfYear) {
    Session session = getSession();
    try {
      Query q = SQL.countLastWeekSalesInventoryStatByShopId(session, shopId, weekOfYear);
      Long result = (Long) q.uniqueResult();
      return result == null ? 0 : result.intValue();
    } finally {
      release(session);
    }
  }

  public List<ShopProductMatchResult> getLastMonthSalesInventoryStatByShopId(Long shopId, int statYear, int statMonth, int statDay, Pager pager) {
    Session session = this.getSession();
    try {
      Query q = SQL.getLastMonthSalesInventoryStatByShopId(session, shopId, statYear, statMonth, statDay, pager);
      return (List<ShopProductMatchResult>) q.list();
    } finally {
      release(session);
    }
  }

  public List<Payable> getSupplierPayable() {
    Session session = this.getSession();
    try {
      Query q = SQL.getSupplierPayable(session);
      return (List<Payable>) q.list();
    } finally {
      release(session);
    }
  }

  public List<PreBuyOrderItem> getActivePreBuyOrderItemList(Long shopId, Set<String> productNames) throws Exception {
    Session session = this.getSession();
    try {
      Query q = SQL.getActivePreBuyOrderItemList(session, shopId, productNames);
      return (List<PreBuyOrderItem>) q.list();
    } finally {
      release(session);
    }
  }

  public void updateRemindEventStatus(Long shopId, Long customerOrSupplierId, String identity) {
    Session session = this.getSession();
    try {
      Query query = SQL.updateRemindEventStatus(session, shopId, customerOrSupplierId, identity);
      query.executeUpdate();
    } finally {
      release(session);
    }
  }

  public List<PreBuyOrder> getPreBuyOrdersByShopId(Long shopId, int pageStart, int pageSize) {
    Session session = getSession();
    try {
      Query query = SQL.getPreBuyOrderListByShopIdByPage(session, shopId, pageStart, pageSize);
      return (List<PreBuyOrder>) query.list();
    } finally {
      release(session);
    }
  }

  public List<OrderItemPromotion> getOrderItemPromotionsByOrderItemId(Long orderItemId) {
    Session session = getSession();
    try {
      Query query = SQL.getOrderItemPromotionsByOrderItemId(session, orderItemId);
      return (List<OrderItemPromotion>) query.list();
    } finally {
      release(session);
    }
  }

  public List<QuotedPreBuyOrder> getQuotedPreBuyOrdersByIds(Set<Long> ids) {
    Session session = getSession();
    try {
      Query query = SQL.getQuotedPreBuyOrderByIds(session, ids);
      return (List<QuotedPreBuyOrder>) query.list();
    } finally {
      release(session);
    }
  }

  public BigInteger getSalesNewOrderCountBySupplierShopId(Long supplierShopId, Long startTime, Long endTime, String orderStatus, String timeField) {
    Session session = getSession();
    try {
      Query q = SQL.getSalesNewOrderCountBySupplierShopId(session, supplierShopId, startTime, endTime, orderStatus, timeField);
      return (BigInteger) q.uniqueResult();
    } finally {
      release(session);
    }
  }

  public List<AppointOrderServiceItem> getAppointOrderServiceItems(Long shopId, Long appointOrderId) {
    Session session = getSession();
    try {
      Query query = SQL.getAppointOrderServiceItems(session, shopId, appointOrderId);
      return query.list();
    } finally {
      release(session);
    }
  }

  public AppointOrder getAppointOrderById(Long id, Long shopId) {
    Session session = getSession();
    try {
      Query query = SQL.getAppointOrderById(session, id, shopId);
      List<AppointOrder> appointOrders = (ArrayList<AppointOrder>) query.list();
      return CollectionUtil.getFirst(appointOrders);
    } finally {
      release(session);
    }
  }

  public List<WashBeautyOrder> getWashBeautyOrderByCustomerId(Long customerId) {
    Session session = getSession();
    try {
      Query query = SQL.getWashBeautyOrderByCustomerId(session, customerId);
      return (List<WashBeautyOrder>) query.list();
    } finally {
      release(session);
    }
  }

  public List<RepairOrder> getRepairOrderByCustomerId(Long customerId) {
    Session session = getSession();
    try {
      Query query = SQL.getRepairOrderByCustomerId(session, customerId);
      return (List<RepairOrder>) query.list();
    } finally {
      release(session);
    }
  }

  public int countAppointOrders(AppointOrderSearchCondition searchCondition) {
    Session session = getSession();
    try {
      Query query = SQL.countAppointOrders(session, searchCondition);
      Long result = (Long) query.uniqueResult();
      return result == null ? 0 : result.intValue();
    } finally {
      release(session);
    }
  }

  public List<AppointOrder> searchAppointOrders(AppointOrderSearchCondition searchCondition) {
    Session session = getSession();
    try {
      Query query = SQL.searchAppointOrders(session, searchCondition);
      return query.list();
    } finally {
      release(session);
    }
  }

  public List<AppointOrder> getRemindedAppointOrder(Long upTime, Long downTime, int start, int limit) {
    Session session = getSession();
    try {
      Query query = SQL.getRemindedAppointOrder(session, upTime, downTime, start, limit);
      return query.list();
    } finally {
      release(session);
    }
  }

  public Integer countOverdueAndSoonExpireAppointOrderByShopId(Long upTime, Long downTime, Long shopId) {
    Session session = getSession();
    try {
      Query query = SQL.countOverdueAndSoonExpireAppointOrderByShopId(session, upTime, downTime, shopId);
      return Integer.valueOf(query.uniqueResult().toString());
    } finally {
      release(session);
    }
  }

  public List<AppointOrderServiceItem> getAppointOrderServiceItemsByAppointOrderIds(Set<Long> ids) {
    Session session = getSession();
    try {
      Query query = SQL.getAppointOrderServiceItemsByAppointOrderIds(session, ids);
      return query.list();
    } finally {
      release(session);
    }
  }

  public int countAppointOrderByAppUserNoStatus(String appUserNo) {
    Session session = getSession();
    try {
      Query query = SQL.countAppointOrderByAppUserNoStatus(session, appUserNo);
      return Integer.valueOf(query.uniqueResult().toString());
    } finally {
      release(session);
    }
  }

  public Map<AppointOrderStatus, Integer> countAppointOrderByShopId(Long shopId) {
    Session session = getSession();
    try {
      Query query = SQL.countAppointOrderByShopId(session, shopId);
      List<Object[]> objects = query.list();
      Map<AppointOrderStatus, Integer> map = new HashMap<AppointOrderStatus, Integer>();
      for (Object[] o : objects) {
        map.put(AppointOrderStatus.valueOf(o[0].toString()), Integer.valueOf(o[1].toString()));
      }
      return map;
    } finally {
      release(session);
    }
  }

  public List<AppointOrder> getAppointOrderByPager(String appUserNo, Set<AppointOrderStatus> appointOrderStatuses, Pager pager) {
    Session session = getSession();
    try {
      Query query = SQL.getAppointOrderByPager(session, appUserNo, appointOrderStatuses, pager);
      return query.list();
    } finally {
      release(session);
    }
  }


  public List getWashRepairByPagerAppUserNo(String appUserNo, String washSettled, String repairSettled, Pager pager) {
    Session session = getSession();
    try {
      Query query = SQL.getWashRepairByPagerAppUserNo(session, appUserNo, washSettled, repairSettled, pager);
      return query.list();
    } finally {
      release(session);
    }
  }

  public List<String> countWashRepairByAppUserNoStatus(String appUserNo, String washSettled, String repairSettled) {
    Session session = getSession();
    try {
      Query query = SQL.countWashRepairByAppUserNoStatus(session, appUserNo, washSettled, repairSettled);
      List<String> result = new ArrayList<String>();
      List list = query.list();
      Object[] array = (Object[]) list.get(0);
      result.add(array[0].toString());
      result.add(array[1] == null ? "0" : array[1].toString());
      return result;
    } finally {
      release(session);
    }
  }

  public List<AppointOrderServiceItem> getAppointOrderServiceItemsByOrderIds(Set<Long> orderIds) {
    Session session = getSession();
    try {
      Query query = SQL.getAppointOrderServiceItemsByOrderIds(session, orderIds);
      return query.list();
    } finally {
      release(session);
    }
  }

  public List getAppointRepairByAppUserNoStatus(String appUserNo, Pager pager) {
    Session session = getSession();
    try {
      Query query = SQL.getAppointRepairByAppUserNoStatus(session, appUserNo, pager);
      return query.list();
    } finally {
      release(session);
    }
  }

  public List<WashBeautyOrderItem> getWashBeautyOrderItemByShopIdAndOrderIds(Long shopId, Long... orderId) {

    Session session = getSession();
    try {
      Query query = SQL.getWashBeautyOrderItemByShopIdAndArrayOrderId(session, shopId, orderId);
      return query.list();
    } finally {
      release(session);
    }
  }

  public List<AppointOrder> getAppointmentOrderByCustomerId(Long customerId) {
    Session session = getSession();
    try {
      Query query = SQL.getAppointmentOrderByCustomerId(session, customerId);
      return (List<AppointOrder>) query.list();
    } finally {
      release(session);
    }
  }

  public List<AppointOrder> getAppointOrderByCondition(Long appointTime, Long shopId, Long serviceCategoryId, String vehilceNo, Set<AppointOrderStatus> statusSet) {
    Session session = getSession();
    try {
      Query q = SQL.getAppointOrderByCondition(session, appointTime, shopId, serviceCategoryId, vehilceNo, statusSet);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<AppointOrder> getAppointOrderByVehicleNoStatus(String vehilceNo, Set<AppointOrderStatus> statusSet) {
    Session session = getSession();
    try {
      Query q = SQL.getAppointOrderByVehicleNoStatus(session, vehilceNo, statusSet);
      return q.list();
    } finally {
      release(session);
    }
  }

  public int countAppointOrderByStatus(AppointOrderStatus statusSet) {
    Session session = getSession();
    try {
      Query query = SQL.countAppointOrderByStatus(session, statusSet);
      return Integer.valueOf(query.uniqueResult().toString());
    } finally {
      release(session);
    }
  }

  public List<AppointOrder> getAppointOrderByStatus(AppointOrderStatus statusSet, int start, int size) {
    Session session = getSession();
    try {
      Query q = SQL.getAppointOrderByStatus(session, statusSet, start, size);
      return q.list();
    } finally {
      release(session);
    }
  }

  public int countRepairOrderByShopIdStatus(Long shopId, OrderStatus status) {
    Session session = getSession();
    try {
      Query query = SQL.countRepairOrderByShopIdStatus(session, shopId, status);
      return Integer.valueOf(query.uniqueResult().toString());
    } finally {
      release(session);
    }
  }

  public int countWashBeautyOrderByShopIdStatus(Long shopId, OrderStatus status) {
    Session session = getSession();
    try {
      Query query = SQL.countWashBeautyOrderByShopIdStatus(session, shopId, status);
      return Integer.valueOf(query.uniqueResult().toString());
    } finally {
      release(session);
    }
  }

  public Map<Long, Long> getPushMessageIdCommentedIdMap(Set<Long> pushMessageIds) {
    Map<Long, Long> map = new HashMap<Long, Long>();
    if (CollectionUtil.isEmpty(pushMessageIds)) return map;
    Session session = getSession();
    try {
      Query query = SQL.getPushMessageIdAndCommentedId(session, pushMessageIds);
      List<Object[]> list = query.list();
      for (Object[] objects : list) {
        map.put((Long) objects[0], (Long) objects[1]);
      }
      return map;
    } finally {
      release(session);
    }
  }

  public List<Message> getAllMessages() {
    Session session = this.getSession();
    try {
      Query query = SQL.getAllMessages(session);
      return (List<Message>) query.list();
    } finally {
      release(session);
    }
  }

  public List<MessageReceiver> getAllMessageReceiverByMessageId(Long messageId) {
    Session session = this.getSession();
    try {
      Query query = SQL.getAllMessageReceiverByMessageId(session, messageId);
      return (List<MessageReceiver>) query.list();
    } finally {
      release(session);
    }
  }

  public List<MessageUserReceiver> getAllMessageUserReceiverByMessageReceiverId(Long messageReceiverId) {
    Session session = this.getSession();
    try {
      Query query = SQL.getAllMessageUserReceiverByMessageReceiverId(session, messageReceiverId);
      return (List<MessageUserReceiver>) query.list();
    } finally {
      release(session);
    }
  }

  public List<Notice> getAllNotices() {
    Session session = this.getSession();
    try {
      Query query = SQL.getAllNotices(session);
      return (List<Notice>) query.list();
    } finally {
      release(session);
    }
  }

  public List<NoticeReceiver> getAllNoticeReceiverByNoticeId(Long noticeId) {
    Session session = this.getSession();
    try {
      Query query = SQL.getAllNoticeReceiverByNoticeId(session, noticeId);
      return (List<NoticeReceiver>) query.list();
    } finally {
      release(session);
    }
  }

  public List<Object[]> getPushMessageAndReceivers(Long receiverShopId, Long... pushMessageReceiverIds) {
    Session session = this.getSession();
    try {
      return SQL.getPushMessageAndReceivers(session, receiverShopId, pushMessageReceiverIds).list();
    } finally {
      release(session);
    }
  }

  public List<Object[]> searchReceiverPushMessageDTO(List<PushMessageType> pushMessageTypeList, SearchMessageCondition searchMessageCondition) throws ParseException {
    Session session = this.getSession();
    try {
      if (CollectionUtils.isEmpty(pushMessageTypeList)) return null;
      return SQL.searchReceiverPushMessageDTO(session, pushMessageTypeList, searchMessageCondition).list();
    } finally {
      release(session);
    }
  }

  public List<RepairOrder> getRepairOrderByShopId(Long shopId, OrderStatus[] orderStatus, RepairRemindEventTypes repairRemindEventTypes, int start, int size) {
    Session session = getSession();
    try {
      Query query = repairRemindEventTypes != null ? SQL.getRepairOrderByShopId(session, shopId, orderStatus, repairRemindEventTypes, start, size) : SQL.getRepairOrderByShopId(session, shopId, orderStatus, start, size);
      return query.list();
    } finally {
      release(session);
    }
  }

  public int getRepairOrderCountByShopId(Long shopId, OrderStatus[] orderStatus, RepairRemindEventTypes repairRemindEventTypes) {
    int result = 0;
    Session session = getSession();
    try {
      Query query = repairRemindEventTypes != null ? SQL.getRepairOrderCountByShopId(session, shopId, orderStatus, repairRemindEventTypes) : SQL.getRepairOrderCountByShopId(session, shopId, orderStatus);
      Object temp = query.uniqueResult();
      if (temp != null) {
        result = ((Long) temp).intValue();
      }
    } finally {
      release(session);
    }
    return result;
  }

  public List<RepairOrderService> getRepairOrderService(Set<Long> repairOrderId) {
    List<RepairOrderService> result = null;
    Session session = getSession();
    try {
      Query query = SQL.getRepairOrderService(session, repairOrderId);
      result = query.list();
    } finally {
      release(session);
    }
    return result;
  }

  public List<RepairRemindEvent> getRepairRemindEventsByOrderId(Long shopId, Set<Long> repairOrderId) {
    Session session = getSession();
    try {
      Query q = SQL.getRepairRemindEventsByOrderId(session, shopId, repairOrderId);
      return (List<RepairRemindEvent>) q.list();
    } finally {
      release(session);
    }
  }

  public Map<String, Double> getPepairOrderStatistics(Long shopId, OrderStatus[] orderStatus) {
    Session session = getSession();
    Map<String, Double> result = new HashMap<String, Double>();
    try {
      Query q = SQL.getPepairOrderStatistics(session, shopId, orderStatus);
      List list = q.list();
      for (Object item : list) {
        Object[] arr = (Object[]) item;
        String key = arr[0].toString();
        result.put(key + "_SUM", new Double(Double.parseDouble(arr[1].toString())));
        result.put(key + "_COUNT", new Double(Double.parseDouble(arr[2].toString())));
      }
    } finally {
      release(session);
    }
    return result;
  }

  public Map<String, Integer> getPepairOrderItemStatistics(Long shopId) {
    Session session = getSession();
    Map<String, Integer> result = new HashMap<String, Integer>();
    try {
      Query q = SQL.getPepairOrderItemStatistics(session, shopId);
      List list = q.list();
      for (Object item : list) {
        Object[] arr = (Object[]) item;
        String key = arr[0].toString();
        Integer val = new Integer(Integer.parseInt(arr[1].toString()));
        result.put(key, val);
      }
    } finally {
      release(session);
    }
    return result;
  }

  public List<RepairPicking> getRepairPickingByOrderId(Set<Long> repairOrderIdSet) {
    List<RepairPicking> result = null;
    Session session = getSession();
    try {
      Query q = SQL.getRepairPickingByOrderId(session, repairOrderIdSet);
      result = q.list();
    } finally {
      release(session);
    }
    return result;
  }

  public int countRepairOrderByDate(Long shopId, Long startDate, Long endDate) {
    int result = 0;
    Session session = getSession();
    try {
      Query q = SQL.countRepairOrderByDate(session, shopId, startDate, endDate);
      Object temp = q.uniqueResult();
      if (temp != null) {
        result = Integer.parseInt(temp.toString());
      }
    } finally {
      release(session);
    }
    return result;
  }

  public TwoTuple<Integer, Set<Long>> getCustomerOfTodayAddVehicle(Long shopId, Long fromDate, Long endDate, int start, int size) {
    Integer count = 0;
    Set<Long> set = new HashSet<Long>();
    Session session = getSession();
    try {
      Query q = SQL.getCustomerOfTodayAddVehicle(session, shopId, fromDate, endDate, start, size);
      List list = q.list();
      for (Object id : list) {
        set.add(Long.parseLong(id.toString()));
      }
    } finally {
      release(session);
    }
    session = getSession();
    try {
      Query qq = SQL.getCustomerCountOfTodayAddVehicle(session, shopId, fromDate, endDate);
      Object object = qq.uniqueResult();
      if (object != null) {
        count = Integer.parseInt(object.toString());
      }
    } finally {
      release(session);
    }
    return new TwoTuple<Integer, Set<Long>>(count, set);
  }

  public List<String> getTodayServiceVehicleByCustomerId(Long shopId, Set<Long> customerIdSet, Long fromDate, Long endDate) {
    Session session = getSession();
    try {
      Query q = SQL.getTodayServiceVehicleByCustomerId(session, shopId, customerIdSet, fromDate, endDate);
      return q.list();
    } finally {
      release(session);
    }
  }

  public Integer countReceiverPushMessageDTO(List<PushMessageType> pushMessageTypeList, SearchMessageCondition searchMessageCondition) throws ParseException {
    Session session = this.getSession();
    try {
      if (CollectionUtils.isEmpty(pushMessageTypeList)) return 0;
      return Integer.parseInt(SQL.countReceiverPushMessageDTO(session, pushMessageTypeList, searchMessageCondition).uniqueResult().toString());
    } finally {
      release(session);
    }
  }

  public Enquiry getEnquiryById(Long id, String appUserNo) {
    if (id == null || StringUtils.isEmpty(appUserNo)) {
      return null;
    }
    Session session = this.getSession();
    try {
      Query query = SQL.getEnquiryById(session, id, appUserNo);
      List<Enquiry> enquiries = query.list();
      if (CollectionUtils.isNotEmpty(enquiries)) {
        return enquiries.get(0);
      } else {
        return null;
      }
    } finally {
      release(session);
    }
  }

  public List<PreBuyOrderItem> getPreBuyOrderItemByProductDTO(Long shopId, ProductDTO productDTO) {
    Session session = getSession();
    try {
      Query query = SQL.getPreBuyOrderItemByProductDTO(session, shopId, productDTO);
      return (List<PreBuyOrderItem>) query.list();
    } finally {
      release(session);
    }
  }

  public Long countValidPreBuyOrderItemsByType(Long shopId, BusinessChanceType type) {
    Session session = getSession();
    try {
      Query query = SQL.countValidPreBuyOrderItemsByType(session, shopId, type);
      return (Long) query.uniqueResult();
    } finally {
      release(session);
    }
  }

  public List<PreBuyOrder> getValidPreBuyOrdersWithoutSelf(Long shopId, Long preBuyOrderId) {
    Session session = getSession();
    try {
      Query query = SQL.getValidPreBuyOrdersWithoutSelf(session, shopId, preBuyOrderId);
      return query.list();
    } finally {
      release(session);
    }
  }

  public List<PreBuyOrder> getOtherShopPreBuyOrders(PreBuyOrderSearchCondition condition) {
    Session session = getSession();
    try {
      Query query = SQL.getOtherShopPreBuyOrders(session, condition);
      return query.list();
    } finally {
      release(session);
    }
  }

  public List<QuotedPreBuyOrderItem> getQuotedPreBuyOrderItemsByPager(Long preBuyOrderItemId, int pageStart, int pageSize) {
    Session session = getSession();
    try {
      Query q = SQL.getQuotedPreBuyOrderItemsByPager(session, preBuyOrderItemId, pageStart, pageSize);
      return (List<QuotedPreBuyOrderItem>) q.list();
    } finally {
      release(session);
    }
  }

  public Long countOtherShopPreBuyOrders(Long shopId, Long noneShopId) {
    Session session = getSession();
    try {
      Query q = SQL.countOtherShopPreBuyOrders(session, shopId, noneShopId);
      return (Long) q.uniqueResult();
    } finally {
      release(session);
    }
  }

  public List getRepairAndDraftOrders(Long shopId, Long userId, Long vehicleId, Pager pager, String[] orderTypes, Long startTime, Long endTime) {
    Session session = getSession();
    try {
      Query q = SQL.getRepairAndDraftOrders(session, shopId, userId, vehicleId, pager, orderTypes, startTime, endTime);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<EnquiryTargetShop> getEnquiryTargetShopByEnquiryIdAndStatus(Long enquiryId, Set<EnquiryTargetShopStatus> enquiryTargetShopStatuses) {
    List<EnquiryTargetShop> enquiryTargetShops = new ArrayList<EnquiryTargetShop>();
    if (enquiryId == null) {
      return enquiryTargetShops;
    }
    Session session = this.getSession();
    try {
      Query query = SQL.getEnquiryTargetShops(session, enquiryId, enquiryTargetShopStatuses);
      return query.list();
    } finally {
      release(session);
    }
  }

  public int countRepairOrders(Long shopId, Long vechicleId) {
    Session session = getSession();
    try {
      Query q = SQL.countRepairOrders(session, shopId, vechicleId);
      return NumberUtil.toInteger(q.uniqueResult().toString());
    } finally {
      release(session);
    }
  }


  public List<Enquiry> getEnquiryByAppUserNoAndStatus(String appUserNo, Set<EnquiryStatus> enquiryStatuses, Pager pager) {
    List<Enquiry> enquiries = new ArrayList<Enquiry>();
    if (StringUtils.isEmpty(appUserNo)) {
      return enquiries;
    }
    Session session = this.getSession();
    try {
      Query query = SQL.getEnquiryByAppUserNoAndStatus(session, appUserNo, enquiryStatuses, pager);
      return query.list();
    } finally {
      release(session);
    }
  }

  public int countEnquiryListByUserNoAndStatus(String appUserNo, Set<EnquiryStatus> enquiryStatuses) {
    if (StringUtils.isEmpty(appUserNo)) {
      return 0;
    }
    Session session = this.getSession();
    try {
      Query query = SQL.countEnquiryListByUserNoAndStatus(session, appUserNo, enquiryStatuses);
      Long result = (Long) query.uniqueResult();
      return NumberUtil.intValue(result);
    } finally {
      release(session);
    }
  }

  public List<EnquiryShopResponse> getEnquiryShopResponseByEnquiryIds(Set<Long> ids) {
    List<EnquiryShopResponse> enquiryShopResponses = new ArrayList<EnquiryShopResponse>();
    if (CollectionUtils.isEmpty(ids)) {
      return enquiryShopResponses;
    }
    Session session = this.getSession();
    try {
      Query query = SQL.getEnquiryShopResponseByEnquiryIds(session, ids);
      return query.list();
    } finally {
      release(session);
    }
  }

  public List<EnquiryTargetShop> getEnquiryTargetShopByEnquiryIdsAndStatus(Set<Long> enquiryIds, Set<EnquiryTargetShopStatus> statuses) {
    List<EnquiryTargetShop> enquiryTargetShops = new ArrayList<EnquiryTargetShop>();
    if (CollectionUtils.isEmpty(enquiryIds)) {
      return enquiryTargetShops;
    }
    Session session = this.getSession();
    try {
      Query query = SQL.getEnquiryTargetShopByEnquiryIdsAndStatus(session, enquiryIds, statuses);
      return query.list();
    } finally {
      release(session);
    }
  }

  public List<Pair<Enquiry, EnquiryTargetShop>> searchShopEnquiries(EnquirySearchConditionDTO searchCondition) {
    List<Pair<Enquiry, EnquiryTargetShop>> pairs = new ArrayList<Pair<Enquiry, EnquiryTargetShop>>();
    Session session = this.getSession();
    try {
      Query query = SQL.searchShopEnquiries(session, searchCondition);
      List<Object[]> result = query.list();
      if (CollectionUtils.isNotEmpty(result)) {
        for (Object[] objects : result) {
          if (objects != null && objects.length == 2 && objects[0] != null && objects[1] != null) {
            Enquiry enquiry = (Enquiry) objects[0];
            EnquiryTargetShop enquiryTargetShop = (EnquiryTargetShop) objects[1];
            Pair<Enquiry, EnquiryTargetShop> pair = new Pair<Enquiry, EnquiryTargetShop>(enquiry, enquiryTargetShop);
            pairs.add(pair);
          }
        }
      }
    } finally {
      release(session);
    }
    return pairs;
  }

  public int countShopEnquiryDTOs(EnquirySearchConditionDTO searchCondition) {
    Session session = this.getSession();
    try {
      Query query = SQL.countShopEnquiries(session, searchCondition);
      Long result = (Long) query.uniqueResult();
      return NumberUtil.intValue(result);
    } finally {
      release(session);
    }
  }

  public Pair<Enquiry, EnquiryTargetShop> getShopEnquiryByIdAndShopId(Long enquiryOrderId, Long shopId) {
    Pair<Enquiry, EnquiryTargetShop> pair = null;
    if (enquiryOrderId == null || shopId == null) {
      return pair;
    }
    Session session = this.getSession();
    try {
      Query query = SQL.getShopEnquiryByIdAndShopId(session, enquiryOrderId, shopId);
      List<Object[]> result = query.list();
      if (CollectionUtils.isNotEmpty(result)) {
        Object[] objects = result.get(0);
        if (objects != null && objects.length == 2 && objects[0] != null && objects[1] != null) {
          Enquiry enquiry = (Enquiry) objects[0];
          EnquiryTargetShop enquiryTargetShop = (EnquiryTargetShop) objects[1];
          pair = new Pair<Enquiry, EnquiryTargetShop>(enquiry, enquiryTargetShop);
        }
      }
    } finally {
      release(session);
    }
    return pair;
  }

  public List<EnquiryShopResponse> getEnquiryShopResponseByEnquiryIdAndShopId(Long enquiryOrderId, Long shopId) {
    List<EnquiryShopResponse> enquiryShopResponses = new ArrayList<EnquiryShopResponse>();
    if (enquiryOrderId == null || shopId == null) {
      return enquiryShopResponses;
    }
    Session session = this.getSession();
    try {
      Query query = SQL.getEnquiryShopResponseByEnquiryIdAndShopId(session, enquiryOrderId, shopId);
      return query.list();
    } finally {
      release(session);
    }
  }

  public List<EnquiryTargetShop> getEnquiryTargetShopByShopIdAndEnquiryId(Long shopId, Long enquiryId) {
    List<EnquiryTargetShop> enquiryTargetShops = new ArrayList<EnquiryTargetShop>();
    if (enquiryId == null || shopId == null) {
      return enquiryTargetShops;
    }
    Session session = this.getSession();
    try {
      Query query = SQL.getEnquiryTargetShopByShopIdAndEnquiryId(session, shopId, enquiryId);
      return query.list();
    } finally {
      release(session);
    }
  }

  public int countAllAppOrderDTOs(String userNo) {
    if (StringUtils.isNotBlank(userNo)) {
      Session session = this.getSession();
      try {
        Query query = SQL.countAllAppOrderDTOs(session, userNo);
        Long result = (Long) query.uniqueResult();
        return NumberUtil.intValue(result);
      } finally {
        release(session);
      }
    }
    return 0;
  }

  public List<AppOrderDTO> getAllAppointOrderDTOs(String userNo, Pager pager) {
    List<AppOrderDTO> appOrderDTOs = new ArrayList<AppOrderDTO>();
    if (StringUtils.isNotBlank(userNo) && pager != null) {
      Session session = this.getSession();
      try {
        Query query = SQL.getAllAppointOrders(session, userNo, pager);
        List<Object[]> result = query.list();
        if (CollectionUtils.isNotEmpty(result)) {
          for (Object[] objects : result) {
            Long id = NumberUtil.longValue(objects[0]);
            Long orderTime = NumberUtil.longValue(objects[1]);
            OrderTypes orderType = OrderTypes.parseEnum(StringUtil.StringValueOf(objects[2]));
            Long shopId = NumberUtil.longValue(objects[3]);
            String statusEnumStr = StringUtil.StringValueOf(objects[4]);
            String statusValueStr = "";
            if (orderType != null && id != null) {
              if (orderType.equals(OrderTypes.APPOINT_ORDER)) {
                AppointOrderStatus appointOrderStatus = AppointOrderStatus.parseEnum(statusEnumStr);
                if (appointOrderStatus != null) {
                  statusValueStr = appointOrderStatus.getName();
                }
              } else {
                OrderStatus orderStatus = OrderStatus.parseEnum(statusEnumStr);
                if (orderStatus != null) {
                  statusValueStr = orderStatus.getName();
                }
              }
              AppOrderDTO appOrderDTO = new AppOrderDTO(id, orderTime, orderType, shopId, statusValueStr);
              appOrderDTOs.add(appOrderDTO);
            }
          }
        }
      } finally {
        release(session);
      }
    }
    return appOrderDTOs;
  }

  public Map<OrderTypes, Map<Long, Integer>> getOrderCommentCountMap(List<Pair<OrderTypes, Long>> orderTypeIdPairList) {
    Map<OrderTypes, Map<Long, Integer>> resultMap = new HashMap<OrderTypes, Map<Long, Integer>>();
    if (CollectionUtils.isNotEmpty(orderTypeIdPairList)) {
      Session session = this.getSession();
      try {
        Query query = SQL.getOrderCommentCount(session, orderTypeIdPairList);
        List<Object[]> result = query.list();
        if (CollectionUtils.isNotEmpty(result)) {
          for (Object[] objects : result) {
            OrderTypes orderType = OrderTypes.parseEnum(StringUtil.StringValueOf(objects[0]));
            Long orderId = NumberUtil.longValue(objects[1]);
            int count = NumberUtil.intValue(objects[2]);
            if (orderType != null && orderId != null) {
              Map<Long, Integer> orderCommentCountMap = resultMap.get(orderType);
              if (orderCommentCountMap == null) {
                orderCommentCountMap = new HashMap<Long, Integer>();
              }
              orderCommentCountMap.put(orderId, count);
              resultMap.put(orderType, orderCommentCountMap);
            }
          }
        }
      } finally {
        release(session);
      }
    }
    return resultMap;
  }

  public AssistantAchievementHistory getLastedAssistantAchievementHistory(Long shopId, Long assistantId, Long changeTime) {
    Session session = this.getSession();
    try {
      Query q = SQL.getLastedAssistantAchievementHistory(session, shopId, assistantId, changeTime);
      return (AssistantAchievementHistory) q.uniqueResult();
    } finally {
      release(session);
    }
  }

  public List<AssistantAchievementHistory> geAssistantAchievementHistory(Long shopId, Long assistantId) {
    Session session = this.getSession();
    try {
      Query q = SQL.geAssistantAchievementHistory(session, shopId, assistantId);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<AssistantAchievementStat> getShopAllStatServiceByShopId(Long shopId) {
    Session session = getSession();
    try {
      Query q = SQL.getShopAllStatServiceByShopId(session, shopId);
      return (List<AssistantAchievementStat>) q.list();
    } finally {
      release(session);
    }
  }

  public List<AssistantAchievementStat> getAssistantAchievementStat(Long shopId, int statYear, int statMonth, AchievementStatType statType, Long assistantOrDepartmentId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getAssistantAchievementStat(session, shopId, statYear, statMonth, statType, assistantOrDepartmentId);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<Service> getUseTimesMostService(Long shopId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getUseTimesMostService(session, shopId);
      return (List<Service>) q.list();
    } finally {
      release(session);
    }
  }


  public List getAssistantRecord(Long shopId, Long orderId, Long assistantId, Long itemId, AssistantRecordType recordType) {
    Session session = this.getSession();
    try {
      Query q = SQL.getAssistantRecord(session, shopId, orderId, assistantId, itemId, recordType);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<Long> getARecordIdFromAHistory(Long shopId, AssistantRecordType recordType, List<Long> recordIdList) {
    Session session = getSession();
    try {
      Query q = SQL.getARecordIdFromAHistory(session, shopId, recordType, recordIdList);
      return q.list();
    } finally {
      release(session);
    }
  }

  public Map<Long, Long> getARecordIdFromSAConfig(Long shopId, AssistantRecordType recordType, List<Long> recordIdList) {
    Session session = getSession();
    try {
      Query q = SQL.getARecordIdFromSAConfig(session, shopId, recordType, recordIdList);
      Map<Long, Long> recordMap = new HashMap<Long, Long>();
      List list = q.list();

      if (CollectionUtil.isEmpty(list)) {
        return recordMap;
      }
      for (int i = 0; i < list.size(); i++) {
        Object[] array = (Object[]) list.get(i);

        if (ArrayUtil.isEmpty(array) || array.length != 2) {
          continue;
        }
        recordMap.put(Long.valueOf(array[1].toString()), Long.valueOf(array[0].toString()));
      }
      return recordMap;

    } finally {
      release(session);
    }
  }

  public List getAssistantAchievementRecord(Long shopId, Set<Long> itemIdSet, AssistantRecordType recordType) {
    Session session = this.getSession();
    try {
      Query q = SQL.getAssistantAchievementRecord(session, shopId, itemIdSet, recordType);
      return q.list();
    } finally {
      release(session);
    }
  }

  public RepairOrderSecondary findRepairOrderSecondaryById(Long shopId, Long repairOrderSecondaryId) {
    Session session = this.getSession();
    try {
      Query q = SQL.findRepairOrderSecondaryById(session, shopId, repairOrderSecondaryId);
      return (RepairOrderSecondary) q.uniqueResult();
    } finally {
      release(session);
    }
  }

  public List<RepairOrderServiceSecondary> findRepairOrderServiceSecondaryById(Long shopId, Long repairOrderSecondaryId) {
    Session session = this.getSession();
    try {
      Query q = SQL.findRepairOrderServiceSecondaryById(session, shopId, repairOrderSecondaryId);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<RepairOrderItemSecondary> findRepairOrderItemSecondaryById(Long shopId, Long repairOrderSecondaryId) {
    Session session = this.getSession();
    try {
      Query q = SQL.findRepairOrderItemSecondaryById(session, shopId, repairOrderSecondaryId);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<RepairOrderOtherIncomeItemSecondary> findRepairOrderOtherIncomeItemSecondaryById(Long shopId, Long repairOrderSecondaryId) {
    Session session = this.getSession();
    try {
      Query q = SQL.findRepairOrderOtherIncomeItemSecondaryById(session, shopId, repairOrderSecondaryId);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<RepairOrderSettlementSecondary> findRepairOrderSettlementSecondaryByRepairOrderId(Long shopId, Long repairOrderSecondaryId) {
    Session session = this.getSession();
    try {
      Query q = SQL.findRepairOrderSettlementSecondaryByRepairOrderId(session, shopId, repairOrderSecondaryId);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<RepairOrderSettlementSecondary> findRepairOrderSettlementSecondaryByRepairOrderIds(Long shopId, Long[] repairOrderSecondaryIds) {
    Session session = this.getSession();
    try {
      Query q = SQL.findRepairOrderSettlementSecondaryByRepairOrderIds(session, shopId, repairOrderSecondaryIds);
      return q.list();
    } finally {
      release(session);
    }
  }

  public int updateRepairOrderSecondaryOrderStatus(Long shopId, Long id, OrderStatus orderStatus) {
    Session session = this.getSession();
    try {
      Query q = SQL.updateRepairOrderSecondaryOrderStatus(session, shopId, id, orderStatus);
      return q.executeUpdate();
    } finally {
      release(session);
    }
  }

  public int deleteRepairOrderSettlementSecondary(Long repairOrderSecondaryId) {
    Session session = this.getSession();
    try {
      Query q = SQL.deleteRepairOrderSettlementSecondary(session, repairOrderSecondaryId);
      return q.executeUpdate();
    } finally {
      release(session);
    }
  }

  public RepairOrderSecondary findRepairOrderSecondaryByRepairOrderId(Long shopId, Long repairOrderId) {
    Session session = this.getSession();
    try {
      Query q = SQL.findRepairOrderSecondaryByRepairOrderId(session, shopId, repairOrderId);
      return (RepairOrderSecondary) q.uniqueResult();
    } finally {
      release(session);
    }
  }

  public List<RepairOrderSecondary> queryRepairOrderSecondary(Long shopId, RepairOrderSecondaryCondition repairOrderSecondaryCondition) {
    Session session = this.getSession();
    try {
      Query q = SQL.queryRepairOrderSecondary(session, shopId, repairOrderSecondaryCondition);
      return q.list();
    } finally {
      release(session);
    }
  }

  public int queryRepairOrderSecondarySize(Long shopId, RepairOrderSecondaryCondition repairOrderSecondaryCondition) {
    Session session = this.getSession();
    int result = 0;
    try {
      Query q = SQL.countQueryRepairOrderSecondary(session, shopId, repairOrderSecondaryCondition);
      Object temp = q.uniqueResult();
      if (temp != null) {
        result = Integer.parseInt(temp.toString());
      }
    } finally {
      release(session);
    }
    return result;
  }

  public Object[] statisticsRepairOrderSecondary(Long shopId, RepairOrderSecondaryCondition repairOrderSecondaryCondition) {
    Session session = this.getSession();
    try {
      Query q = SQL.statisticsRepairOrderSecondary(session, shopId, repairOrderSecondaryCondition);
      return (Object[]) q.uniqueResult();
    } finally {
      release(session);
    }
  }

  public Object[] statisticsRepairOrderSettlementSecondary(Long shopId, RepairOrderSecondaryCondition repairOrderSecondaryCondition) {
    Session session = this.getSession();
    try {
      Query q = SQL.statisticsRepairOrderSettlementSecondary(session, shopId, repairOrderSecondaryCondition);
      return (Object[]) q.uniqueResult();
    } finally {
      release(session);
    }
  }

  public int countSmsRecharge(SmsRechargeSearchCondition condition) {
    Session session = this.getSession();
    try {
      Query q = SQL.countSmsRecharge(session, condition);
      return Integer.valueOf(q.uniqueResult().toString());
    } finally {
      release(session);
    }
  }

  public List<SmsRecharge> searchSmsRechargeResult(SmsRechargeSearchCondition condition) {
    Session session = this.getSession();
    try {
      Query q = SQL.searchSmsRechargeResult(session, condition);
      return (List<SmsRecharge>) q.list();
    } finally {
      release(session);
    }
  }

  public List<Object[]> statSmsRechargeByPaymentWay(SmsRechargeSearchCondition condition) {
    Session session = this.getSession();
    try {
      Query q = SQL.statSmsRechargeByPaymentWay(session, condition);
      return (List<Object[]>) q.list();
    } finally {
      release(session);
    }
  }

  public List<PreferentialPolicy> getSmsPreferentialPolicy() {
    Session session = this.getSession();
    try {
      Query q = SQL.getSmsPreferentialPolicy(session);
      return (List<PreferentialPolicy>) q.list();
    } finally {
      release(session);
    }
  }

  public ShopSmsRecord getShopSmsRecordBySmsId(Long shopId, Long smsId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getShopSmsRecordBySmsId(session, shopId, smsId);
      List<ShopSmsRecord> shopSmsRecordList = (List<ShopSmsRecord>) q.list();
      if (CollectionUtil.isNotEmpty(shopSmsRecordList)) {
        return shopSmsRecordList.get(0);
      }
      return null;
    } finally {
      release(session);
    }
  }

  public List<CommentRecord> getSupplierCommentByPagerAndKeyword(Long supplierShopId, Pager pager, CommentRecordDTO commentRecordDTO) {
    Session session = this.getSession();

    try {
      Query q = SQL.getSupplierCommentByPagerAndKeyword(session, supplierShopId, pager, commentRecordDTO);
      return (List<CommentRecord>) q.list();
    } finally {
      release(session);
    }
  }

  public int countSupplierCommentRecordByKeyword(Long supplierShopId, CommentRecordDTO commentRecordDTO) {
    Session session = this.getSession();

    try {
      Query q = SQL.countSupplierCommentRecordByKeyword(session, supplierShopId, commentRecordDTO);

      return Integer.parseInt(q.uniqueResult().toString());
    } finally {
      release(session);
    }
  }

  public int countCommentTypeRecordByKeyword(Long supplierShopId, CommentRecordDTO commentRecordDTO, String commentType) {
    Session session = this.getSession();

    try {
      Query q = SQL.countCommentTypeRecordByKeyword(session, supplierShopId, commentRecordDTO, commentType);
      return Integer.parseInt(q.uniqueResult().toString());
    } finally {
      release(session);
    }
  }


  public void deleteTxnRemind(Long shopId, Long purchaseOrderId) {
    Session session = this.getSession();
    try {
      Query q = SQL.deleteTxnRemind(session, shopId, purchaseOrderId);
      q.executeUpdate();
    } finally {
      release(session);
    }
  }

  public void updateDebtRemindDeletedType(Long shopId, Long customerOrSupplierId, String identity, DeletedType deletedType) {
    Session session = this.getSession();
    try {
      Query q = SQL.updateDebtRemindDeletedType(session, shopId, customerOrSupplierId, identity, deletedType);
      q.executeUpdate();
    } finally {
      release(session);
    }
  }

  public List<DepositOrder> getTotalDepositOrderByPurchaseInventoryInfo(Long shopId, Long customerId, Long supplierId, Long relatedOrderId) {
    Session session = getSession();
    try {
      Query query = SQL.queryDepositOrderByShopIdCustomerIdOrSupplierId(session, shopId, customerId, supplierId, relatedOrderId);
      return query.list();
    } finally {
      release(session);
    }
  }


  public int countAppToShopCommentRecord(Long shopId, CommentRecordType commentRecordType) {
    if (shopId == null || commentRecordType == null) {
      return 0;
    }
    Session session = this.getSession();

    try {
      Query q = SQL.countAppToShopCommentRecord(session, shopId, commentRecordType);
      return Integer.parseInt(q.uniqueResult().toString());
    } finally {
      release(session);
    }
  }

  public List<CommentRecord> getCommentRecordByShopIdAndCommentRecordType(Long shopId, Pager pager, CommentRecordType commentRecordType) {
    if (shopId == null || pager == null || commentRecordType == null) {
      return new ArrayList<CommentRecord>();
    }
    Session session = this.getSession();

    try {
      Query q = SQL.getCommentRecordByShopIdAndCommentRecordType(session, shopId, pager, commentRecordType);
      return q.list();
    } finally {
      release(session);
    }
  }

  public int countStatDetailByNormalProductIds(Long[] shopIds, Long normalProductId, NormalProductStatType normalProductStatType) {

    Session session = getSession();
    try {
      Query query = SQL.countStatDetailByNormalProductIds(session, shopIds, normalProductId, normalProductStatType);
      return Integer.parseInt(query.uniqueResult().toString());
    } finally {
      release(session);
    }
  }

  public List<NormalProductInventoryStat> getStatDetailByNormalProductIds(Long[] shopIds, Long normalProductId, NormalProductStatType normalProductStatType, Pager pager) {
    Session session = getSession();
    try {
      Query query = SQL.getStatDetailByNormalProductIds(session, shopIds, normalProductId, normalProductStatType, pager);
      return (List<NormalProductInventoryStat>) query.list();
    } finally {
      release(session);
    }
  }

  public Map<Long, List<NormalProductInventoryStatDTO>> getProductTopPriceByProductIdTime(Long[] productIds, Long startTime, Long endTime) {
    Session session = getSession();
    try {
      Query query = SQL.getProductTopPriceByProductIdTime(session, productIds, startTime, endTime);
      List list = query.list();

      Map<Long, List<NormalProductInventoryStatDTO>> itemMap = new HashMap<Long, List<NormalProductInventoryStatDTO>>();

      if (CollectionUtil.isEmpty(list)) {
        return itemMap;
      }

      for (Object object : list) {
        Object[] array = (Object[]) object;
        Long productId = Long.valueOf(array[0].toString());

        NormalProductInventoryStatDTO statDTO = new NormalProductInventoryStatDTO();
        statDTO.setTopPrice(Double.valueOf(array[1].toString()));
        statDTO.setBottomPrice(Double.valueOf(array[2].toString()));
        statDTO.setUnit(array[3].toString());

        List<NormalProductInventoryStatDTO> items = itemMap.get(productId);
        if (CollectionUtil.isEmpty(items)) {
          items = new ArrayList<NormalProductInventoryStatDTO>();
        }
        items.add(statDTO);
        itemMap.put(productId, items);
      }
      return itemMap;

    } finally {
      release(session);
    }
  }

  public List<AppointOrderMaterial> getAppointOrderMaterials(Long shopId, Long appointOrderId) {
    if (shopId == null || appointOrderId == null) {
      return new ArrayList<AppointOrderMaterial>();
    }
    Session session = this.getSession();
    try {
      Query q = SQL.getAppointOrderMaterials(session, shopId, appointOrderId);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<AppointOrderServiceDetail> getAppointOrderServiceDetails(Long shopId, Long appointOrderId) {
    if (shopId == null || appointOrderId == null) {
      return new ArrayList<AppointOrderServiceDetail>();
    }
    Session session = this.getSession();
    try {
      Query q = SQL.getAppointOrderServiceDetails(session, shopId, appointOrderId);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<FaultInfoToShop> getFaultInfoToShopByIds(Long shopId, Long... ids) {
    if (shopId == null || ArrayUtils.isEmpty(ids)) {
      return new ArrayList<FaultInfoToShop>();
    }
    Session session = getSession();
    try {
      Query query = MessageSQL.getFaultInfoToShopByIds(session, shopId, ids);
      return query.list();
    } finally {
      release(session);
    }
  }

  public List<FaultInfoToShop> getUnHandledFaultInfoToShopsByVehicleNo(Long shopId, String vehicleNo) {
    if (shopId == null || StringUtils.isEmpty(vehicleNo)) {
      return new ArrayList<FaultInfoToShop>();
    }
    Session session = getSession();
    try {
      Query query = MessageSQL.getUnHandledFaultInfoToShopByVehicleNo(session, shopId, vehicleNo);
      return query.list();
    } finally {
      release(session);
    }
  }

  public List<FaultInfoToShop> getUnHandledFaultInfoToShopsByVehicleNoFaultCode(String vehicleNo, Long shopId, Set<String> faultCodes) {
    if (shopId == null || StringUtils.isEmpty(vehicleNo)) {
      return new ArrayList<FaultInfoToShop>();
    }
    Session session = getSession();
    try {
      Query query = MessageSQL.getUnHandledFaultInfoToShopsByVehicleNoFaultCode(session, vehicleNo, shopId, faultCodes);
      return query.list();
    } finally {
      release(session);
    }
  }

  public int countAdvertByDateStatus(Long shopId, Long startDate, Long endDate, AdvertStatus[] advertStatuses) {
    Session session = getSession();
    try {
      Query q = SQL.countAdvertByDateStatus(session, shopId, startDate, endDate, advertStatuses);
      return Integer.parseInt(q.uniqueResult().toString());
    } finally {
      release(session);
    }
  }

  public List<Advert> getAdvertByDateStatus(Long shopId, Long startDate, Long endDate, AdvertStatus[] advertStatuses, Pager pager) {
    Session session = getSession();
    try {
      Query q = SQL.getAdvertByDateStatus(session, shopId, startDate, endDate, advertStatuses, pager);
      return (List<Advert>) q.list();
    } finally {
      release(session);
    }
  }

  public void updateAdvertToOverdue(Long endDate) {
    Session session = this.getSession();
    try {
      Query q = SQL.updateAdvertToOverdue(session, endDate);
      q.executeUpdate();
    } finally {
      release(session);
    }
  }

  public int countShopFaultInfoByVehicleNo(Long shopId, String vehicleNo) {
    if (shopId == null || StringUtils.isEmpty(vehicleNo)) {
      return 0;
    }
    Session session = getSession();
    try {
      Query query = MessageSQL.countShopFaultInfoByVehicleNo(session, shopId, vehicleNo);
      return NumberUtil.intValue(query.uniqueResult());
    } finally {
      release(session);
    }
  }

  public List<ShopTalkMessage> getShopTalkMessageDTOByAppUserNo(Long shopId, String appUserNo) {
    Session session = getSession();
    try {
      Query q = SQL.getShopTalkMessageDTOByAppUserNo(session, shopId, appUserNo);
      return q.list();
    } finally {
      release(session);
    }
  }

  /**
   * 更新receivable的couponConsume字段
   * @param shopId
   * @param receivableId
   * @param couponConsume
   */
  public void updateReceivableCouponConsume(Long shopId, Long receivableId, Double couponConsume){
    Session session = getSession();
    try {
      Query q = SQL.updateReceivableCouponConsume(session, shopId, receivableId, couponConsume);
      q.executeUpdate();
    } finally {
      release(session);
    }
  }
}
