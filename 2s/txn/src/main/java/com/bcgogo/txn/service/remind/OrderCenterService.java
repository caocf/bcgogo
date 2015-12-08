package com.bcgogo.txn.service.remind;

import com.bcgogo.enums.OrderStatus;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.remind.OrderCenterDTO;
import com.bcgogo.txn.model.TxnDaoManager;
import com.bcgogo.txn.service.ITxnService;
import com.bcgogo.user.service.ICustomerService;
import com.bcgogo.user.service.IUserService;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.TxnConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * User: ZhangJuntao
 * Date: 13-6-18
 * Time: 下午1:51
 */
@Component
public class OrderCenterService implements IOrderCenterService {
  private static final Logger LOG = LoggerFactory.getLogger(OrderCenterService.class);
  @Autowired
  private TxnDaoManager txnDaoManager;


  @Override
  public OrderCenterDTO getOrderCenterSaleAndSaleReturnNewStatistics(Long shopId) {
    OrderCenterDTO orderCenterDTO = new OrderCenterDTO();
    ICustomerService customerService = ServiceManager.getService(ICustomerService.class);
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    Long timePoint = null;
    try {
      timePoint = DateUtil.getToday(DateUtil.YEAR_MONTH_DATE, new Date());
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }
    //本店全部关联客户的idList
    List<Long> relatedCustomerIdList = customerService.getRelatedCustomerIdListByShopId(shopId);
    /********* 销售单 ************/
    //今日新增
    Long sale_today_new = txnService.getTodoSalesOrderCount(shopId, timePoint, null, relatedCustomerIdList, null, OrderStatus.PENDING.toString());
    orderCenterDTO.setSaleTodayNew(sale_today_new);
    //往日新增
    Long sale_early_new = txnService.getTodoSalesOrderCount(shopId, null, timePoint, relatedCustomerIdList, null, OrderStatus.PENDING.toString());
    orderCenterDTO.setSaleEarlyNew(sale_today_new);
    //新增总数
    Long sale_new = sale_today_new + sale_early_new;
    orderCenterDTO.setSaleNew(sale_new);

    /********* 销售退货单 ************/
    //今日新增
    Long sale_return_today_new = txnService.getTodoSalesReturnOrderCount(shopId, timePoint, null, relatedCustomerIdList, null, OrderStatus.PENDING.toString());
    orderCenterDTO.setSaleReturnTodayNew(sale_return_today_new);
    //往日新增
    Long sale_return_early_new = txnService.getTodoSalesReturnOrderCount(shopId, null, timePoint, relatedCustomerIdList, null, OrderStatus.PENDING.toString());
    orderCenterDTO.setSaleReturnEarlyNew(sale_return_early_new);
    //新增总数
    Long sale_return_new = sale_return_today_new + sale_return_early_new;
    orderCenterDTO.setSaleReturnNew(sale_return_new);
    return orderCenterDTO;
  }

  @Override
  public OrderCenterDTO getOrderCenterPurchaseSellerStatistics(Long shopId) {
    OrderCenterDTO orderCenterDTO = new OrderCenterDTO();
    IUserService userService = ServiceManager.getService(IUserService.class);
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    //本店全部关联客户的idList
    List<Long> relatedSupplierIdList = userService.getRelatedSupplierIdListByShopId(shopId);
    /********* 采购单 ************/
    //卖家备货中
    Long purchase_seller_stock = txnService.getTodoPurchaseOrderCount(shopId, null, null, relatedSupplierIdList, null, OrderStatus.SELLER_STOCK.toString(), TxnConstant.PURCHASE_ORDER_FIELD_CREATED);
    orderCenterDTO.setPurchaseSellerStock(purchase_seller_stock);
    //卖家发货中
    Long purchase_seller_dispatch = txnService.getTodoPurchaseOrderCount(shopId, null, null, relatedSupplierIdList, null, OrderStatus.SELLER_DISPATCH.toString(), TxnConstant.PURCHASE_ORDER_FIELD_CREATED);
    orderCenterDTO.setPurchaseSellerDispatch(purchase_seller_dispatch);
    //卖家已拒绝
    Long purchase_seller_refused = txnService.getTodoPurchaseOrderCount(shopId, null, null, relatedSupplierIdList, null, OrderStatus.SELLER_REFUSED.toString(), TxnConstant.PURCHASE_ORDER_FIELD_CREATED);
    orderCenterDTO.setPurchaseSellerRefused(purchase_seller_refused);
    return orderCenterDTO;
  }

  public OrderCenterDTO getOrderCenterStatistics(Long shopId) {
    OrderCenterDTO orderCenterDTO = new OrderCenterDTO();
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    IUserService userService = ServiceManager.getService(IUserService.class);
    ICustomerService customerService = ServiceManager.getService(ICustomerService.class);
    //本店全部关联客户的idList
    //List<Long> relatedCustomerIdList = customerService.getRelatedCustomerIdListByShopId(shopId);
    List<Long> relatedCustomerIdList = null;   //为了与solr查询结果一致
    //本店全部关联供应商的idList
    //List<Long> relatedSupplierIdList = userService.getRelatedSupplierIdListByShopId(shopId);
    List<Long> relatedSupplierIdList = null;  //为了与solr查询结果一致
        //今天的时间段
    Long timePoint = null;
    try {
      timePoint = DateUtil.getToday(DateUtil.YEAR_MONTH_DATE, new Date());
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }

    /********* 销售单 ************/
    //今日新增
    Long sale_today_new = txnService.getSalesNewOrderCountBySupplierShopId(shopId, timePoint, null, OrderStatus.SELLER_PENDING.toString(), TxnConstant.PURCHASE_ORDER_FIELD_CREATED);
    orderCenterDTO.setSaleTodayNew(sale_today_new);
    //往日新增
    Long sale_early_new = txnService.getSalesNewOrderCountBySupplierShopId(shopId, null, timePoint, OrderStatus.SELLER_PENDING.toString(), TxnConstant.PURCHASE_ORDER_FIELD_CREATED);
    orderCenterDTO.setSaleEarlyNew(sale_early_new);
    //新增总数
    Long sale_new = sale_today_new + sale_early_new;
    orderCenterDTO.setSaleNew(sale_new);
    //备货中
    Long sale_stocking = txnService.getTodoSalesOrderCount(shopId, null, null, relatedCustomerIdList, null, OrderStatus.STOCKING.toString());
    orderCenterDTO.setSaleStocking(sale_stocking);
    //已发货
    Long sale_dispatch = txnService.getTodoSalesOrderCount(shopId, null, null, relatedCustomerIdList, null, OrderStatus.DISPATCH.toString());
    orderCenterDTO.setSaleDispatch(sale_dispatch);
    //欠款结算
    Long sale_sale_debt_done = txnService.getTodoSalesOrderCount(shopId, null, null, relatedCustomerIdList, null, OrderStatus.SALE_DEBT_DONE.toString());
    orderCenterDTO.setSaleSaleDebtDone(sale_sale_debt_done);
    //处理中（各个待处理状态的总和）
    Long sale_in_progress = sale_stocking + sale_dispatch + sale_sale_debt_done;
    orderCenterDTO.setSaleInProgress(sale_in_progress);

    /********* 销售退货单 ************/
    //今日新增
    Long sale_return_today_new = txnService.getTodoSalesReturnOrderCount(shopId, timePoint, null, relatedCustomerIdList, null, OrderStatus.PENDING.toString());
    orderCenterDTO.setSaleReturnTodayNew(sale_return_today_new);
    //往日新增
    Long sale_return_early_new = txnService.getTodoSalesReturnOrderCount(shopId, null, timePoint, relatedCustomerIdList, null, OrderStatus.PENDING.toString());
    orderCenterDTO.setSaleReturnEarlyNew(sale_return_early_new);
    //新增总数
    Long sale_return_new = sale_return_today_new + sale_return_early_new;
    orderCenterDTO.setSaleReturnNew(sale_return_new);
    //处理中（待入库）
    Long sale_return_in_progress = txnService.getTodoSalesReturnOrderCount(shopId, null, null, relatedCustomerIdList, null, OrderStatus.WAITING_STORAGE.toString());
    orderCenterDTO.setSaleReturnInProgress(sale_return_in_progress);


    /********* 采购单 ************/
    //今日新增
    Long purchase_today_new = txnService.getTodoPurchaseOrderCount(shopId, timePoint, null, relatedSupplierIdList, null, OrderStatus.SELLER_PENDING.toString(), TxnConstant.PURCHASE_ORDER_FIELD_CREATED);
    orderCenterDTO.setPurchaseTodayNew(purchase_today_new);
    //往日新增
    Long purchase_early_new = txnService.getTodoPurchaseOrderCount(shopId, null, timePoint, relatedSupplierIdList, null, OrderStatus.SELLER_PENDING.toString(), TxnConstant.PURCHASE_ORDER_FIELD_CREATED);
    orderCenterDTO.setPurchaseEarlyNew(purchase_early_new);
    //新增总数
    Long purchase_new = purchase_today_new + purchase_early_new;
    orderCenterDTO.setPurchaseNew(purchase_new);
    //卖家备货中
    Long purchase_seller_stock = txnService.getTodoPurchaseOrderCount(shopId, null, null, relatedSupplierIdList, null, OrderStatus.SELLER_STOCK.toString(), TxnConstant.PURCHASE_ORDER_FIELD_CREATED);
    orderCenterDTO.setPurchaseSellerStock(purchase_seller_stock);
    //卖家发货中
    Long purchase_seller_dispatch = txnService.getTodoPurchaseOrderCount(shopId, null, null, relatedSupplierIdList, null, OrderStatus.SELLER_DISPATCH.toString(), TxnConstant.PURCHASE_ORDER_FIELD_CREATED);
    orderCenterDTO.setPurchaseSellerDispatch(purchase_seller_dispatch);
    //卖家已拒绝
    Long purchase_seller_refused = txnService.getTodoPurchaseOrderCount(shopId, null, null, relatedSupplierIdList, null, OrderStatus.SELLER_REFUSED.toString(), TxnConstant.PURCHASE_ORDER_FIELD_CREATED);
    orderCenterDTO.setPurchaseSellerRefused(purchase_seller_refused);
    //卖家终止交易
    Long purchase_seller_stop = txnService.getTodoPurchaseOrderCount(shopId, null, null, relatedSupplierIdList, null, OrderStatus.PURCHASE_SELLER_STOP.toString(), TxnConstant.PURCHASE_ORDER_FIELD_CREATED);
    orderCenterDTO.setPurchaseSellerStop(purchase_seller_stop);
    //处理中（各个待处理状态的总和）
    Long purchase_in_progress = purchase_seller_stock + purchase_seller_dispatch + purchase_seller_refused + purchase_seller_stop;
    orderCenterDTO.setPurchaseInProgress(purchase_in_progress);
    //今日入库
    Long purchase_today_done = txnService.getTodoPurchaseOrderCount(shopId, timePoint, null, relatedSupplierIdList, null, OrderStatus.PURCHASE_ORDER_DONE.toString(), TxnConstant.PURCHASE_ORDER_FIELD_INVENTORY_VEST_DATE);
    orderCenterDTO.setPurchaseTodayDone(purchase_today_done);
    //往日入库
    Long purchase_early_done = txnService.getTodoPurchaseOrderCount(shopId, null, timePoint, relatedSupplierIdList, null, OrderStatus.PURCHASE_ORDER_DONE.toString(), TxnConstant.PURCHASE_ORDER_FIELD_INVENTORY_VEST_DATE);
    orderCenterDTO.setPurchaseEarlyDone(purchase_early_done);
    //入库总数
    Long purchase_done = purchase_today_done + purchase_early_done;
    orderCenterDTO.setPurchaseDone(purchase_done);

    /********* 入库退货单 ************/
    //今日新增
    Long purchase_return_today_new = txnService.getTodoPurchaseReturnOrderCount(shopId, timePoint, null, relatedSupplierIdList, null, OrderStatus.SELLER_PENDING.toString());
    orderCenterDTO.setPurchaseReturnTodayNew(purchase_return_today_new);
    //往日新增
    Long purchase_return_early_new = txnService.getTodoPurchaseReturnOrderCount(shopId, null, timePoint, relatedSupplierIdList, null, OrderStatus.SELLER_PENDING.toString());
    orderCenterDTO.setPurchaseReturnEarlyNew(purchase_return_early_new);
    //新增总数
    Long purchase_return_new = purchase_return_today_new + purchase_return_early_new;
    orderCenterDTO.setPurchaseReturnNew(purchase_return_new);
    //卖家已接受
    Long purchase_return_seller_accept = txnService.getTodoPurchaseReturnOrderCount(shopId, null, null, relatedSupplierIdList, null, OrderStatus.SELLER_ACCEPTED.toString());
    orderCenterDTO.setPurchaseReturnSellerAccept(purchase_return_seller_accept);
    //卖家已拒绝
    Long purchase_return_seller_refused = txnService.getTodoPurchaseReturnOrderCount(shopId, null, null, relatedSupplierIdList, null, OrderStatus.SELLER_REFUSED.toString());
    orderCenterDTO.setPurchaseReturnSellerRefused(purchase_return_seller_refused);
    //处理中（各个待处理状态的总和）
    Long purchase_return_in_progress = purchase_return_seller_accept + purchase_return_seller_refused;
    orderCenterDTO.setPurchaseReturnInProgress(purchase_return_in_progress);
    return orderCenterDTO;
  }

}
