package com.bcgogo.txn.service.web;

import com.bcgogo.common.Result;
import com.bcgogo.search.model.InventorySearchIndex;
import com.bcgogo.txn.dto.*;
import com.bcgogo.txn.model.TxnWriter;
import com.bcgogo.user.dto.SupplierDTO;

import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Jimuchen
 * Date: 12-8-12
 * Time: 下午2:00
 * To change this template use File | Settings | File Templates.
 */
public interface IGoodsStorageService {

  PurchaseInventoryDTO getPurchaseInventory(Long purchaseInventoryId, Long shopId) throws Exception;

  void initProductList(String[] productIds, PurchaseInventoryItemDTO[] itemDTOs, PurchaseInventoryDTO purchaseInventoryDTO, Long shopId, Map<Long,Double> productAmountMap) throws Exception ;

	void cancelPurchaseInventoryInTxnDB(Long shopId, PurchaseInventoryDTO purchaseInventoryDTO,
	                                    List<InventorySearchIndex> inventorySearchIndexes,
	                                    PurchaseOrderDTO purchaseOrderDTO) throws Exception;

  void cancelPurchaseInventoryInTxnDBByStoreHouse(Long shopId, PurchaseInventoryDTO purchaseInventoryDTO,
                                      List<InventorySearchIndex> inventorySearchIndexes,List<StoreHouseInventoryDTO> storeHouseInventoryDTOList,
                                      PurchaseOrderDTO purchaseOrderDTO) throws Exception;

  PurchaseInventoryDTO createOrUpdatePurchaseInventory(Long shopId,Long shopVersionId,PurchaseInventoryDTO purchaseInventoryDTO) throws Exception;

	PurchaseInventoryDTO getSimplePurchaseInventory(Long purchaseInventoryId, Long shopId) throws Exception;

	Map<Long,PurchaseInventoryDTO> getSimplePurchaseInventoryByPurchaseOrderIds(Long shopId,Long ... purchaseOrderId) throws Exception;

  Result validateCopy(Long purchaseInventoryId, Long shopId);

  SupplierDTO handleSupplierForGoodsStorage(PurchaseInventoryDTO purchaseInventoryDTO)throws Exception;

  //校验入库单关联的采购单状态
  Result checkPurchaseOrderInventory(Long shopId, String purchaseOrderId);

  Long getNextProductId(Long id, long startProductId, int defaultPageSize);

  //根据产品id段用入库单数据组装SupplierInventoryDTO 用于初始化
  List<SupplierInventoryDTO> getInitSupplierInventory(Long shopId, Long shopVersionId, Long startProductId, Long endProductId);

  //入库单作废更新Inventory库存均价，更新SupplierInventory最后入库信息
  void cancelPurchaseInventoryToUpdateAveragePriceAndLastInfo(PurchaseInventoryDTO purchaseInventoryDTO,TxnWriter writer) throws Exception;

  void cancelPurchaseInventoryToUpdateAveragePriceAndLastInfo(PurchaseInventoryDTO purchaseInventoryDTO) throws Exception;


  //入库单处理商品逻辑
  void handleProductForSavePurchaseInventory(PurchaseInventoryDTO purchaseInventoryDTO) throws Exception;


}
