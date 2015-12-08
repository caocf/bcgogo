package com.bcgogo.txn.service;

import com.bcgogo.common.Result;
import com.bcgogo.search.model.InventorySearchIndex;
import com.bcgogo.txn.dto.*;
import com.bcgogo.txn.model.SalesOrderItem;
import com.bcgogo.txn.model.TxnWriter;

import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-8-25
 * Time: 下午1:47
 * To change this template use File | Settings | File Templates.
 */
public interface IGoodSaleService {

  void acceptSaleOrder(SalesOrderDTO salesOrderDTO,PurchaseOrderDTO purchaseOrderDTO, Map<Long,OutStorageRelationDTO[]> relationDTOMap) throws Exception;

  void refuseSaleOrder(SalesOrderDTO salesOrderDTO,PurchaseOrderDTO purchaseOrderDTO) throws Exception;

  void refusePendingPurchaseOrder(SalesOrderDTO salesOrderDTO,PurchaseOrderDTO purchaseOrderDTO) throws Exception;

  void dispatchSaleOrder(SalesOrderDTO salesOrderDTO,PurchaseOrderDTO purchaseOrderDTO, Map<Long,OutStorageRelationDTO[]> relationDTOMap) throws Exception;

  String checkSalesOrderBeforeSettle(Long shopId, SalesOrderDTO salesOrderDTO, String status);

  void saveReceivableDebtReceptionRecord(SalesOrderDTO salesOrderDTO,AccountInfoDTO accountInfoDTO) throws Exception;

  void stopSaleOrder(Long toStorehouseId,SalesOrderDTO salesOrderDTO, PurchaseOrderDTO purchaseOrderDTO) throws Exception;

  SalesOrderDTO getSimpleSalesOrderByPurchaseOrderId(Long purchaseOrderId);

  void reindexCustomerProductSolr(SalesOrderDTO salesOrderDTO) throws Exception;

  Map<Long,List<SalesOrderItem>> getLackSalesOrderItemByProductIds(Long shopId,Long firstSalesOrderId,Long ...productIds);
  Map<Long,List<SalesOrderItem>> getLackSalesOrderItemByProductIdsAndStorehouse(Long shopId,Long firstSalesOrderId,Long storehouseId,Long ...productIds);
  Map<Long,List<SalesOrderItem>> getSalesOrderItemsByOrderId(Long saleOrderId);

  Result validateCopy(Long salesOrderId, Long shopId);

  public List<InventorySearchIndex> updateInventoryAndInventorySearchIndex(Long shopId, SalesOrderDTO salesOrderDTO, TxnWriter writer) throws Exception;

  public List<InventorySearchIndex> updateInventoryAndInventorySearchIndexByStoreHouse(Long shopId, Long toStorehouseId, SalesOrderDTO salesOrderDTO, TxnWriter writer) throws Exception;

  //保存销售单
  public SalesOrderDTO createOrUpdateSalesOrder(SalesOrderDTO salesOrderDTO, String huankuanTime) throws Exception;

  //批量保存营业分类
  void batchSaveCategory(SalesOrderDTO salesOrderDTO);

  //保存销售单item 上的新车型信息,solr在线程里做
  public void populateSalesOrderDTO(SalesOrderDTO salesOrderDTO) throws Exception;

  //更新销售单上的产品信息
  public void saveOrUpdateProductForSaleOrder(SalesOrderDTO salesOrderDTO) throws Exception;

  SalesOrderDTO getSimpleSalesOrderById(Long salesOrderId);

  /**
   * 通过itemId 和 shopId 查询SalesOrderItem
   *
   * @param itemId
   * @return
   */
  public SalesOrderItemDTO getSalesOrderItemById(Long itemId);


  List<SalesOrderDTO> getUnSettledSalesOrdersByCustomerId(Long shopId, Long customerId);

  /**
   * 通过待处理的采购单生成一张销售单（不保存数据库）
   * @param purchaseOrderDTO
   * @return
   */
  SalesOrderDTO generateSaleOrderDTOFromPurchase(PurchaseOrderDTO purchaseOrderDTO);

  /**
   * 通过采购单
   * @param purchaseOrderDTO
   * @return
   */
  SalesOrderDTO createOnlineSalesOrderDTO(PurchaseOrderDTO purchaseOrderDTO) throws Exception;

  void repealSalesOrder(Long shopId, Long toStorehouseId, SalesOrderDTO salesOrderDTO) throws Exception;
}
