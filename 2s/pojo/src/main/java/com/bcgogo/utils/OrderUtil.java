package com.bcgogo.utils;

import com.bcgogo.enums.OrderStatus;
import com.bcgogo.enums.OrderTypes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-5-26
 * Time: 下午1:42
 * To change this template use File | Settings | File Templates.
 */

public class OrderUtil {
  //新增状态
  public static final List<OrderStatus> purchaseOrderNew = new ArrayList<OrderStatus>();
  public static final List<OrderStatus> purchaseReturnOrderNew = new ArrayList<OrderStatus>();
  public static final List<OrderStatus> salesOrderNew = new ArrayList<OrderStatus>();
  public static final List<OrderStatus> salesReturnOrderNew = new ArrayList<OrderStatus>();
  //待处理状态
  public static final List<OrderStatus> purchaseOrderTodo = new ArrayList<OrderStatus>();
  public static final List<OrderStatus> purchaseReturnOrderTodo = new ArrayList<OrderStatus>();
  public static final List<OrderStatus> salesOrderTodo = new ArrayList<OrderStatus>();
  public static final List<OrderStatus> salesReturnOrderTodo = new ArrayList<OrderStatus>();
  //处理中状态
  public static final List<OrderStatus> purchaseOrderInProgress = new ArrayList<OrderStatus>();
  public static final List<OrderStatus> purchaseInventoryInProgress = new ArrayList<OrderStatus>();
  public static final List<OrderStatus> salesOrderInProgress = new ArrayList<OrderStatus>();
  public static final List<OrderStatus> repairOrderInProgress = new ArrayList<OrderStatus>();
  public static final List<OrderStatus> washOrderInProgress = new ArrayList<OrderStatus>();
  public static final List<OrderStatus> purchaseReturnInProgress = new ArrayList<OrderStatus>();
  public static final List<OrderStatus> salesReturnInProgress = new ArrayList<OrderStatus>();
  public static final List<OrderStatus> RepairPickingInProgress = new ArrayList<OrderStatus>();
  //结算状态
  public static final List<OrderStatus> purchaseOrderSettled = new ArrayList<OrderStatus>();
  public static final List<OrderStatus> purchaseInventorySettled = new ArrayList<OrderStatus>();
  public static final List<OrderStatus> salesOrderSettled = new ArrayList<OrderStatus>();
  public static final List<OrderStatus> repairOrderSettled = new ArrayList<OrderStatus>();
  public static final List<OrderStatus> washOrderSettled = new ArrayList<OrderStatus>();
  public static final List<OrderStatus> purchaseReturnSettled = new ArrayList<OrderStatus>();
  public static final List<OrderStatus> salesReturnSettled = new ArrayList<OrderStatus>();
  //作废状态
  public static final List<OrderStatus> purchaseOrderRepealed = new ArrayList<OrderStatus>();
  public static final List<OrderStatus> purchaseInventoryRepealed = new ArrayList<OrderStatus>();
  public static final List<OrderStatus> salesOrderRepealed = new ArrayList<OrderStatus>();
  public static final List<OrderStatus> repairOrderRepealed = new ArrayList<OrderStatus>();
  public static final List<OrderStatus> washOrderRepealed = new ArrayList<OrderStatus>();
  public static final List<OrderStatus> purchaseReturnRepealed = new ArrayList<OrderStatus>();
  public static final List<OrderStatus> salesReturnRepealed = new ArrayList<OrderStatus>();

  public static final Map<OrderTypes, List<OrderStatus>> newStatusMap = new HashMap<OrderTypes, List<OrderStatus>>();
  public static final Map<OrderTypes, List<OrderStatus>> todoStatusMap = new HashMap<OrderTypes, List<OrderStatus>>();
  public static final Map<OrderTypes, List<OrderStatus>> inProgressStatusMap = new HashMap<OrderTypes, List<OrderStatus>>();
  public static final Map<OrderTypes, List<OrderStatus>> finishedStatusMap = new HashMap<OrderTypes, List<OrderStatus>>();
  public static final Map<OrderTypes, List<OrderStatus>> settledStatusMap = new HashMap<OrderTypes, List<OrderStatus>>();
  public static final Map<OrderTypes, List<OrderStatus>> repealedStatusMap = new HashMap<OrderTypes, List<OrderStatus>>();

  public static final List<OrderTypes> inStorageOrders=new ArrayList<OrderTypes>();

  static{

    //新增状态初始化
    purchaseOrderNew.add(OrderStatus.SELLER_PENDING);
    purchaseReturnOrderNew.add(OrderStatus.SELLER_PENDING);
    salesOrderNew.add(OrderStatus.PENDING);
    salesReturnOrderNew.add(OrderStatus.PENDING);
    newStatusMap.put(OrderTypes.PURCHASE, purchaseOrderNew);
    newStatusMap.put(OrderTypes.RETURN, purchaseReturnOrderNew);
    newStatusMap.put(OrderTypes.SALE, salesOrderNew);
    newStatusMap.put(OrderTypes.SALE_RETURN, salesReturnOrderNew);

    //代办状态初始化（欠款结算暂不包含）
    purchaseOrderTodo.add(OrderStatus.SELLER_PENDING);
    purchaseOrderTodo.add(OrderStatus.SELLER_STOCK);
    purchaseOrderTodo.add(OrderStatus.SELLER_DISPATCH);
    purchaseOrderTodo.add(OrderStatus.SELLER_REFUSED);
    todoStatusMap.put(OrderTypes.PURCHASE, purchaseOrderTodo);

    purchaseReturnOrderTodo.add(OrderStatus.SELLER_PENDING);
    purchaseReturnOrderTodo.add(OrderStatus.SELLER_ACCEPTED);
    purchaseReturnOrderTodo.add(OrderStatus.SELLER_REFUSED);
    todoStatusMap.put(OrderTypes.RETURN, purchaseReturnOrderTodo);

    salesOrderTodo.add(OrderStatus.PENDING);
    salesOrderTodo.add(OrderStatus.STOCKING);
    salesOrderTodo.add(OrderStatus.DISPATCH);
    todoStatusMap.put(OrderTypes.SALE, salesOrderTodo);

    salesReturnInProgress.add(OrderStatus.PENDING);
    salesReturnInProgress.add(OrderStatus.WAITING_STORAGE);
    todoStatusMap.put(OrderTypes.SALE_RETURN, salesReturnInProgress);

    //处理中状态初始化
    purchaseOrderInProgress.add(OrderStatus.PURCHASE_ORDER_WAITING);
    purchaseOrderInProgress.add(OrderStatus.SELLER_PENDING);
    purchaseOrderInProgress.add(OrderStatus.SELLER_STOCK);
    purchaseOrderInProgress.add(OrderStatus.SELLER_DISPATCH);
    inProgressStatusMap.put(OrderTypes.PURCHASE, purchaseOrderInProgress);

    salesOrderInProgress.add(OrderStatus.PENDING);
    salesOrderInProgress.add(OrderStatus.STOCKING);
    salesOrderInProgress.add(OrderStatus.DISPATCH);
    inProgressStatusMap.put(OrderTypes.SALE, salesOrderInProgress);

    repairOrderInProgress.add(OrderStatus.REPAIR_DISPATCH);
    repairOrderInProgress.add(OrderStatus.REPAIR_CHANGE);
    repairOrderInProgress.add(OrderStatus.REPAIR_DONE);
    inProgressStatusMap.put(OrderTypes.REPAIR, repairOrderInProgress);

    purchaseReturnInProgress.add(OrderStatus.SELLER_PENDING);
    purchaseReturnInProgress.add(OrderStatus.SELLER_ACCEPTED);
    inProgressStatusMap.put(OrderTypes.RETURN, purchaseReturnInProgress);


    salesReturnInProgress.add(OrderStatus.PENDING);
    salesReturnInProgress.add(OrderStatus.WAITING_STORAGE);
    inProgressStatusMap.put(OrderTypes.SALE_RETURN, salesReturnInProgress);

    RepairPickingInProgress.add(OrderStatus.WAIT_RETURN_STORAGE);
    RepairPickingInProgress.add(OrderStatus.WAIT_OUT_STORAGE);
    inProgressStatusMap.put(OrderTypes.REPAIR_PICKING,RepairPickingInProgress);

    //结算状态初始化
    purchaseOrderSettled.add(OrderStatus.PURCHASE_ORDER_DONE);
    purchaseInventorySettled.add(OrderStatus.PURCHASE_INVENTORY_DONE);
    salesOrderSettled.add(OrderStatus.SALE_DONE);
    repairOrderSettled.add(OrderStatus.REPAIR_SETTLED);
    washOrderSettled.add(OrderStatus.WASH_SETTLED);
    purchaseReturnSettled.add(OrderStatus.SETTLED);
    salesReturnSettled.add(OrderStatus.SETTLED);

    settledStatusMap.put(OrderTypes.PURCHASE, purchaseOrderSettled);
    settledStatusMap.put(OrderTypes.INVENTORY, purchaseInventorySettled);
    settledStatusMap.put(OrderTypes.SALE, salesOrderSettled);
    settledStatusMap.put(OrderTypes.REPAIR, repairOrderSettled);
    settledStatusMap.put(OrderTypes.WASH_BEAUTY, washOrderSettled);
    settledStatusMap.put(OrderTypes.RETURN, purchaseReturnSettled);
    settledStatusMap.put(OrderTypes.SALE_RETURN, salesReturnSettled);

    //作废状态初始化
    purchaseOrderRepealed.add(OrderStatus.PURCHASE_ORDER_REPEAL);
    purchaseInventoryRepealed.add(OrderStatus.PURCHASE_INVENTORY_REPEAL);
    salesOrderRepealed.add(OrderStatus.SALE_REPEAL);
    repairOrderRepealed.add(OrderStatus.REPAIR_REPEAL);
    washOrderRepealed.add(OrderStatus.WASH_REPEAL);
    purchaseReturnRepealed.add(OrderStatus.REPEAL);
    salesReturnRepealed.add(OrderStatus.SALE_REPEAL);

    repealedStatusMap.put(OrderTypes.PURCHASE, purchaseOrderRepealed);
    repealedStatusMap.put(OrderTypes.INVENTORY, purchaseInventoryRepealed);
    repealedStatusMap.put(OrderTypes.SALE, salesOrderRepealed);
    repealedStatusMap.put(OrderTypes.REPAIR, repairOrderRepealed);
    repealedStatusMap.put(OrderTypes.WASH_BEAUTY, washOrderRepealed);
    repealedStatusMap.put(OrderTypes.RETURN, purchaseReturnRepealed);
    repealedStatusMap.put(OrderTypes.SALE_RETURN, salesReturnRepealed);

    //出入库打通库存增加的单据
    inStorageOrders.add(OrderTypes.INNER_RETURN);
    inStorageOrders.add(OrderTypes.INVENTORY_CHECK);
    inStorageOrders.add(OrderTypes.SALE_RETURN);
    inStorageOrders.add(OrderTypes.RETURN_ORDER);

  }

  public static List<String> getOrderStatusListToString(List<OrderStatus> orderStatuses) {
    List<String> status = new ArrayList<String>();
    if (CollectionUtil.isNotEmpty(orderStatuses)) {
      for (OrderStatus orderStatus : orderStatuses) {
        status.add(orderStatus.name());
      }
    }
    return status;
  }

}
