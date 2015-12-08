package com.bcgogo.txn.service.web;

import com.bcgogo.common.Result;
import com.bcgogo.enums.*;
import com.bcgogo.product.dto.KindDTO;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.product.dto.ProductLocalInfoDTO;
import com.bcgogo.product.service.IProductService;
import com.bcgogo.search.model.InventorySearchIndex;
import com.bcgogo.search.service.ISearchService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.*;
import com.bcgogo.txn.model.*;
import com.bcgogo.txn.service.*;
import com.bcgogo.txn.service.productThrough.IProductInStorageService;
import com.bcgogo.txn.service.productThrough.IProductThroughService;
import com.bcgogo.user.dto.CustomerDTO;
import com.bcgogo.user.dto.CustomerRecordDTO;
import com.bcgogo.user.dto.SupplierDTO;
import com.bcgogo.user.service.IContactService;
import com.bcgogo.user.service.ISupplierService;
import com.bcgogo.user.service.IUserService;
import com.bcgogo.user.service.utils.BcgogoShopLogicResourceUtils;
import com.bcgogo.utils.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Jimuchen
 * Date: 12-8-12
 * Time: 下午2:01
 * To change this template use File | Settings | File Templates.
 */
@Service
public class GoodsStorageService implements IGoodsStorageService {
  private static final Logger LOG = LoggerFactory.getLogger(GoodsStorageService.class);
  //  @Autowired
//  private IGoodsBuyService goodsBuyService;
  @Autowired
  private ITxnService txnService;
//  @Autowired
//  private ISearchService searchService;
//  @Autowired
//  private IProductService productService;

  @Autowired
  private TxnDaoManager txnDaoManager;
	private IGoodSaleService goodSaleService;
  private ISupplierService supplierService;
  private IUserService userService;
  private ISupplierRecordService supplierRecordService;
  private IProductInStorageService productInStorageService;

  public IProductInStorageService getProductInStorageService() {
    return productInStorageService == null ? ServiceManager.getService(IProductInStorageService.class) : productInStorageService;
  }

  public IGoodSaleService getGoodSaleService() {
		return goodSaleService == null ? ServiceManager.getService(IGoodSaleService.class): goodSaleService;
	}

  public ISupplierService getSupplierService() {
    return supplierService == null ? ServiceManager.getService(ISupplierService.class) : supplierService;
  }

  public IUserService getUserService() {
    return userService == null ? ServiceManager.getService(IUserService.class) : userService;
  }

  public ISupplierRecordService getSupplierRecordService() {
    return supplierRecordService == null ? ServiceManager.getService(ISupplierRecordService.class) : supplierRecordService;
  }

  @Override
  public PurchaseInventoryDTO getPurchaseInventory(Long purchaseInventoryId, Long shopId) throws Exception {
    IProductService productService = ServiceManager.getService(IProductService.class);
    IInventoryService inventoryService = ServiceManager.getService(IInventoryService.class);
    IProductHistoryService productHistoryService = ServiceManager.getService(IProductHistoryService.class);
    TxnWriter writer = txnDaoManager.getWriter();
    PurchaseInventory purchaseInventory = new PurchaseInventory();
    List<PurchaseInventory> purchaseInventories = writer.getPurchaseInventoryById(purchaseInventoryId, shopId);
    if (purchaseInventories != null && purchaseInventories.size() > 0) {
      purchaseInventory = purchaseInventories.get(0);
    } else {
      return null;
    }
    if (purchaseInventory != null) {
      PurchaseInventoryDTO purchaseInventoryDTO = purchaseInventory.toDTO();
      List<PurchaseInventoryItem> items = writer.getPurchaseInventoryItemsByInventoryId(purchaseInventoryId);
      PurchaseInventoryItemDTO[] itemDTOs = new PurchaseInventoryItemDTO[items.size()];
      purchaseInventoryDTO.setItemDTOs(itemDTOs);
      Set<Long> productIds = new HashSet<Long>();
      Set<Long> productHistoryIds = new HashSet<Long>();
      if(CollectionUtils.isNotEmpty(items)){
        for(PurchaseInventoryItem purchaseInventoryItem : items){
          if(purchaseInventoryItem.getProductId() !=null){
            productIds.add(purchaseInventoryItem.getProductId());
          }
          if(purchaseInventoryItem.getProductHistoryId() != null){
            productHistoryIds.add(purchaseInventoryItem.getProductHistoryId());
          }
        }
      }
      Map<Long,ProductDTO> productDTOMap =  productService.getProductDTOMapByProductLocalInfoIds(shopId,productIds);
      Map<Long,InventoryDTO> inventoryDTOMap = inventoryService.getInventoryDTOMap(shopId,productIds);
      Map<Long,ProductHistoryDTO> productHistoryDTOMap = productHistoryService.getProductHistoryDTOMapByProductHistoryIds(productHistoryIds);
      for (int i = 0; i < items.size(); i++) {
        PurchaseInventoryItem item = items.get(i);
        itemDTOs[i] = item.toDTO();
        itemDTOs[i].setPurchasePrice(item.getPrice());
        ProductDTO productDTO = productDTOMap.get(item.getProductId());
        InventoryDTO inventory = inventoryDTOMap.get(itemDTOs[i].getProductId());
        ProductHistoryDTO productHistoryDTO =productHistoryDTOMap.get(itemDTOs[i].getProductHistoryId());
        if(productHistoryDTO!=null){
          itemDTOs[i].setProductHistoryDTO(productHistoryDTO);
        }else{
          itemDTOs[i].setProductDTOWithOutUnit(productDTO);
        }
        if(productDTO!=null){
          if(productDTO.getSalesStatus()!=ProductStatus.InSales){
            purchaseInventoryDTO.getNotInSaleProductIds().add(productDTO.getProductLocalInfoId());
          }
          itemDTOs[i].setSellUnit(productDTO.getSellUnit());
          itemDTOs[i].setStorageUnit(productDTO.getStorageUnit());
          itemDTOs[i].setRate(productDTO.getRate());
          //如果采购单位是库存大单位
          if (UnitUtil.isStorageUnit(itemDTOs[i].getUnit(), productDTO)){
            itemDTOs[i].setInventoryAmount(inventory.getAmount() / productDTO.getRate());
            itemDTOs[i].setRecommendedPrice(NumberUtil.doubleVal(inventory.getSalesPrice())  * productDTO.getRate());
            itemDTOs[i].setLowerLimit(inventory.getLowerLimit() == null ? null : inventory.getLowerLimit() / productDTO.getRate());
            itemDTOs[i].setUpperLimit(inventory.getUpperLimit() == null ? null : inventory.getUpperLimit() / productDTO.getRate());
            itemDTOs[i].setTradePrice(productDTO.getTradePrice() == null ? null : productDTO.getTradePrice() * productDTO.getRate());
          }else {
            itemDTOs[i].setInventoryAmount(inventory.getAmount());
            itemDTOs[i].setRecommendedPrice(inventory.getSalesPrice());
            itemDTOs[i].setTradePrice(productDTO.getTradePrice());
            itemDTOs[i].setLowerLimit(inventory.getLowerLimit());
            itemDTOs[i].setUpperLimit(inventory.getUpperLimit());
          }
        }

      }
      return purchaseInventoryDTO;
    }
    return null;
  }

  public void initProductList(String[] productIds, PurchaseInventoryItemDTO[] itemDTOs, PurchaseInventoryDTO purchaseInventoryDTO, Long shopId, Map<Long,Double> productAmountMap) throws Exception {
    IProductService productService = ServiceManager.getService(IProductService.class);
    IInventoryService inventoryService = ServiceManager.getService(IInventoryService.class);
    Set<Long> productIdSet = new HashSet<Long>();
    if (productIds != null && productIds.length > 0) {
      StringBuilder stringBuilder = new StringBuilder();
      for (int arrayLength = 0; arrayLength < productIds.length; arrayLength++) {
        if (NumberUtil.isNumber(productIds[arrayLength])) {
          stringBuilder.append(productIds[arrayLength]).append(",");
        }
      }
      productIds = stringBuilder.toString().split(",");
    }

    if (null == itemDTOs) {
      itemDTOs = new PurchaseInventoryItemDTO[productIds.length];
      purchaseInventoryDTO.setItemDTOs(itemDTOs);
    }
    double orderTotal = 0.0;
    for (int i = 0; i < productIds.length; i++) {
      if (null != shopId && null != productIds[i] && !"".equals(productIds[i]) && !"null".equals(productIds[i])) {
        productIdSet.add(new Long(productIds[i]));
      }
    }
    Map<Long,InventoryDTO> inventoryDTOMap = inventoryService.getInventoryDTOMap(shopId,productIdSet);
    Map<Long,ProductDTO> productDTOMap = productService.getProductDTOMapByProductLocalInfoIds(shopId,productIdSet);
    Set<Long> productKindIds = new HashSet<Long>();
    if(!MapUtils.isEmpty(productDTOMap)){
      for(ProductDTO productDTO : productDTOMap.values()){
        if(productDTO.getKindId() != null){
          productKindIds.add(productDTO.getKindId());
        }
      }
    }

    Map<Long,KindDTO> kindDTOMap = productService.getProductKindById(productKindIds.toArray(new Long[productKindIds.size()]));
    for (int i = 0; i < productIds.length; i++) {
      if (null != shopId && null != productIds[i] && !"".equals(productIds[i]) && !"null".equals(productIds[i])) {
        ProductDTO productDTO = productDTOMap.get(new Long(productIds[i])); //ServiceManager.getService(IProductService.class).getProductByProductLocalInfoId(new Long(productIds[i]), shopId);
        Long[] productLocalInfoId = new Long[1];
        productLocalInfoId[0] = new Long(productIds[i]);
//        List<InventorySearchIndex> inventorySearchIndexList = ServiceManager.getService(ISearchService.class).searchInventorySearchIndexByProductIds(shopId, productLocalInfoId);         //search Product RecommendedPrice
        InventoryDTO inventoryDTO = inventoryDTOMap.get(Long.parseLong(productIds[i]));
        if (itemDTOs[i] == null) {
          itemDTOs[i] = new PurchaseInventoryItemDTO();
        }
        if (null != productDTO) {
	        itemDTOs[i].setProductDTOWithOutUnit(productDTO);
          itemDTOs[i].setProductVehicleStatus(productDTO.getProductVehicleStatus());
          if (StringUtils.isBlank(itemDTOs[i].getUnit())) {
            itemDTOs[i].setUnit(productDTO.getSellUnit());
          }
	        if(itemDTOs[i].getAmount() == null || itemDTOs[i].getAmount()<0.0001){
            if(productAmountMap!=null){
              itemDTOs[i].setAmount(productAmountMap.get(productLocalInfoId[0]));
            }else{
		        itemDTOs[i].setAmount(TxnConstant.ORDER_DEFAULT_AMOUNT);
	        }
	        }

          String kindName = null;

          if(null != productDTO.getKindId()) {
            KindDTO kindDTO = kindDTOMap.get(productDTO.getKindId());
            if (null != kindDTO) {
              kindName = kindDTO.getName();
            }
          }
          itemDTOs[i].setProductKind(kindName);
          double inventoryAmount = 0, recommendedPrice = 0, purchasePrice = 0, tradePrice = 0, inventoryAveragePrice = 0;
          Double lowerLimit = null,upperLimit = null;
          if(inventoryDTO != null){
            inventoryAmount =  NumberUtil.doubleVal(inventoryDTO.getAmount());
            recommendedPrice = NumberUtil.doubleVal(inventoryDTO.getSalesPrice());
            purchasePrice = NumberUtil.doubleVal(inventoryDTO.getLatestInventoryPrice());
            inventoryAveragePrice = NumberUtil.doubleVal(inventoryDTO.getInventoryAveragePrice());
            lowerLimit = inventoryDTO.getLowerLimit();
            upperLimit = inventoryDTO.getUpperLimit();
          }
          if(productDTO != null){
            tradePrice = NumberUtil.doubleVal(productDTO.getTradePrice());
          }
          if(UnitUtil.isStorageUnit(itemDTOs[i].getUnit(), productDTO)){
            inventoryAmount = NumberUtil.round(inventoryAmount / productDTO.getRate(), 2);
            recommendedPrice = NumberUtil.round(recommendedPrice * productDTO.getRate(), 2);
            purchasePrice = NumberUtil.round(purchasePrice * productDTO.getRate(),2);
            tradePrice = NumberUtil.round(tradePrice * productDTO.getRate(),2);
            inventoryAveragePrice = NumberUtil.round(inventoryAveragePrice * productDTO.getRate(),2);
            if(lowerLimit != null){
              lowerLimit = NumberUtil.round(lowerLimit / productDTO.getRate(),2);
            }
            if(upperLimit != null) {
              upperLimit = NumberUtil.round(upperLimit / productDTO.getRate(), 2);
            }

          }
          itemDTOs[i].setInventoryAmount(inventoryAmount);
          itemDTOs[i].setRecommendedPrice(recommendedPrice);
          itemDTOs[i].setTradePrice(tradePrice);
          itemDTOs[i].setInventoryAveragePrice(inventoryAveragePrice);
          itemDTOs[i].setLowerLimit(lowerLimit);
          itemDTOs[i].setUpperLimit(upperLimit);
          if (itemDTOs[i].getPurchasePrice() == null) {    //为啥要加这个判断，我估计前面传入非null的值后不要从新拿了吧
            itemDTOs[i].setPurchasePrice(purchasePrice);
          }
          itemDTOs[i].setTotal(NumberUtil.round(NumberUtil.doubleVal(itemDTOs[i].getAmount()) * NumberUtil.doubleVal(itemDTOs[i].getPurchasePrice()), NumberUtil.MONEY_PRECISION));
          orderTotal += itemDTOs[i].getTotal();
        }
      }
    }
    orderTotal = NumberUtil.round(orderTotal, NumberUtil.MONEY_PRECISION); //保留两位小数
    purchaseInventoryDTO.setTotal(orderTotal);
    purchaseInventoryDTO.setStroageCreditAmount(orderTotal);
  }

  @Override
  public void cancelPurchaseInventoryInTxnDB(Long shopId, PurchaseInventoryDTO purchaseInventoryDTO,
                                             List<InventorySearchIndex> inventorySearchIndexes,
                                             PurchaseOrderDTO purchaseOrderDTO) throws Exception {
    RFITxnService rfiTxnService = ServiceManager.getService(RFITxnService.class);
    IProductService productService = ServiceManager.getService(IProductService.class);
    TxnWriter txnWriter = txnDaoManager.getWriter();
    Object status = txnWriter.begin();
    try {
      //update PurchaseInventory  status
      purchaseInventoryDTO.setStatus(OrderStatus.PURCHASE_INVENTORY_REPEAL);
      txnWriter.updatePurchaseInventoryStatus(purchaseInventoryDTO);
      //update inventory amount   && update repair_remind_event Status
      for (InventorySearchIndex inventorySearchIndex : inventorySearchIndexes) {
        txnWriter.updateInventoryAmount(shopId, inventorySearchIndex.getProductId(), inventorySearchIndex.getAmount());
        Double inventoryAmount = inventorySearchIndex.getAmount();
        List<RepairRemindEvent> repairRemindEvents = txnWriter.getRepairRemindEventsByProductId(shopId, RepairRemindEventTypes.INCOMING, inventorySearchIndex.getProductId());
        if (repairRemindEvents != null && repairRemindEvents.size() > 0) {
          for (RepairRemindEvent repairRemindEvent : repairRemindEvents) {
            double orderProductAmount = txnWriter.getRepairOrderItemsByRepairOrderIdAndProductId(repairRemindEvent.getRepairOrderId(), repairRemindEvent.getProductId())
                .get(0).getAmount();
            ProductDTO productDTO = productService.getProductByProductLocalInfoId(inventorySearchIndex.getProductId(), shopId);
            if (UnitUtil.isStorageUnit(repairRemindEvent.getUnit(), productDTO)) {
              orderProductAmount = orderProductAmount * productDTO.getRate();
            }
            if (inventoryAmount < orderProductAmount) {
              repairRemindEvent.setEventTypeEnum(RepairRemindEventTypes.LACK);
              txnWriter.update(repairRemindEvent);
              //add by WLF 保存提醒总表
              List<RemindEvent> remindEventList = txnWriter.getRemindEventByOldRemindEventId(RemindEventType.REPAIR,shopId, repairRemindEvent.getId());
              if(CollectionUtil.isNotEmpty(remindEventList)){
                remindEventList.get(0).setEventStatus(RepairRemindEventTypes.LACK.toString());
                txnWriter.update(remindEventList.get(0));
              }
            } else {
              inventoryAmount = inventoryAmount - orderProductAmount;
            }
          }
        }
      }
      //update purchaseOrder Status and InventoryRemindEvent
      if (purchaseInventoryDTO.getPurchaseOrderId() != null && purchaseOrderDTO != null) {
          txnWriter.updatePurchaseOrderStatus(shopId, purchaseOrderDTO.getId(), purchaseOrderDTO.getStatus());
          //add InventoryRemindEvent
          PurchaseOrderItemDTO[] purchaseOrderItemDTOs = purchaseOrderDTO.getItemDTOs();
          for (int i = 0; i < purchaseOrderItemDTOs.length; i++) {
            rfiTxnService.saveInventoryRemindEvent(txnWriter, purchaseOrderDTO, purchaseOrderDTO.getItemDTOs()[i]);
          }
        }
      txnWriter.commit(status);
    } finally {
      txnWriter.rollback(status);
    }
  }

  @Override
  public void cancelPurchaseInventoryInTxnDBByStoreHouse(Long shopId, PurchaseInventoryDTO purchaseInventoryDTO,
                                                         List<InventorySearchIndex> inventorySearchIndexes,List<StoreHouseInventoryDTO> storeHouseInventoryDTOList,
                                                         PurchaseOrderDTO purchaseOrderDTO) throws Exception {
    RFITxnService rfiTxnService = ServiceManager.getService(RFITxnService.class);
    IProductService productService = ServiceManager.getService(IProductService.class);
    IStoreHouseService storeHouseService = ServiceManager.getService(IStoreHouseService.class);
    TxnWriter txnWriter = txnDaoManager.getWriter();
    Object status = txnWriter.begin();
    try {
      //update PurchaseInventory  status
      purchaseInventoryDTO.setStatus(OrderStatus.PURCHASE_INVENTORY_REPEAL);
      txnWriter.updatePurchaseInventoryStatus(purchaseInventoryDTO);
      //update inventory amount   && update repair_remind_event Status
      for (InventorySearchIndex inventorySearchIndex : inventorySearchIndexes) {
        txnWriter.updateInventoryAmount(shopId, inventorySearchIndex.getProductId(), inventorySearchIndex.getAmount());
      }
      List<RepairRemindEvent> repairRemindEvents = null;
      List<RepairOrderItem> repairOrderItems = null;
      for (StoreHouseInventoryDTO storeHouseInventoryDTO : storeHouseInventoryDTOList) {
        storeHouseInventoryDTO = storeHouseService.saveOrUpdateStoreHouseInventoryDTO(txnWriter,storeHouseInventoryDTO);

        Double storehouseInventoryAmount = storeHouseInventoryDTO.getAmount();
        repairRemindEvents = txnWriter.getRepairRemindEventsByProductId(shopId, RepairRemindEventTypes.INCOMING, storeHouseInventoryDTO.getProductLocalInfoId());
        if (CollectionUtils.isNotEmpty(repairRemindEvents)) {
          for (RepairRemindEvent repairRemindEvent : repairRemindEvents) {
            repairOrderItems = txnWriter.getRepairOrderItemsByRepairOrderIdAndProductIdAndStoreHouse(repairRemindEvent.getRepairOrderId(), repairRemindEvent.getProductId(),storeHouseInventoryDTO.getStorehouseId());
            if(CollectionUtils.isNotEmpty(repairOrderItems)){
              double orderProductAmount = repairOrderItems.get(0).getAmount();
              ProductDTO productDTO = productService.getProductByProductLocalInfoId(storeHouseInventoryDTO.getProductLocalInfoId(), shopId);
              if (UnitUtil.isStorageUnit(repairRemindEvent.getUnit(), productDTO)) {
                orderProductAmount = orderProductAmount * productDTO.getRate();
              }
              if (storehouseInventoryAmount < orderProductAmount) {
                repairRemindEvent.setEventTypeEnum(RepairRemindEventTypes.LACK);
                txnWriter.update(repairRemindEvent);
                //add by WLF 保存到提醒总表
                List<RemindEvent> remindEventList = txnWriter.getRemindEventByOldRemindEventId(RemindEventType.REPAIR,shopId, repairRemindEvent.getId());
                if(CollectionUtil.isNotEmpty(remindEventList)){
                  remindEventList.get(0).setEventStatus(RepairRemindEventTypes.LACK.toString());
                  txnWriter.update(remindEventList.get(0));
                }
              } else {
                storehouseInventoryAmount = storehouseInventoryAmount - orderProductAmount;
              }
            }
          }
        }
      }
      //update purchaseOrder Status and InventoryRemindEvent
      if (purchaseInventoryDTO.getPurchaseOrderId() != null && purchaseOrderDTO != null) {
        txnWriter.updatePurchaseOrderStatus(shopId, purchaseOrderDTO.getId(), purchaseOrderDTO.getStatus());
        //add InventoryRemindEvent
        PurchaseOrderItemDTO[] purchaseOrderItemDTOs = purchaseOrderDTO.getItemDTOs();
        for (int i = 0; i < purchaseOrderItemDTOs.length; i++) {
          rfiTxnService.saveInventoryRemindEvent(txnWriter, purchaseOrderDTO, purchaseOrderDTO.getItemDTOs()[i]);
        }
      }
      txnWriter.commit(status);
    } finally {
      txnWriter.rollback(status);
    }
  }

  @Override
  public PurchaseInventoryDTO createOrUpdatePurchaseInventory(Long shopId,Long shopVersionId,PurchaseInventoryDTO purchaseInventoryDTO) throws Exception {
    if (purchaseInventoryDTO.getItemDTOs() == null) return purchaseInventoryDTO;
    TxnWriter writer = txnDaoManager.getWriter();
    IProductService productService = ServiceManager.getService(IProductService.class);
    IInventoryService inventoryService = ServiceManager.getService(IInventoryService.class);
    IStoreHouseService storeHouseService = ServiceManager.getService(IStoreHouseService.class);
    IProductThroughService productThroughService = ServiceManager.getService(IProductThroughService.class);
    IProductHistoryService productHistoryService = ServiceManager.getService(IProductHistoryService.class);

    List<SupplierInventoryDTO> supplierInventoryDTOs = new ArrayList<SupplierInventoryDTO>();
    Set<Long> productIds = purchaseInventoryDTO.getProductIdSet();
    Map<Long,ProductDTO> productDTOMap = productService.getProductDTOMapByProductLocalInfoIds(shopId, productIds);
    Map<Long,ProductLocalInfoDTO> productLocalInfoDTOMap = productService.getProductLocalInfoMap(shopId, productIds.toArray(new Long[productIds.size()]));
    List<InventorySearchIndex> inventorySearchIndexList = new ArrayList<InventorySearchIndex>();
    InventoryLimitDTO inventoryLimitDTO = purchaseInventoryDTO.getInventoryLimitDTO();
    Object status = writer.begin();
    try {
      Map<Long,Inventory> inventoryMap = inventoryService.getInventoryMap(shopId,productIds);
      Map<Long,ProductHistory> productHistoryMap = new HashMap<Long, ProductHistory>();
      PurchaseInventory purchaseInventory = null;
      if (null == purchaseInventoryDTO.getId() || purchaseInventoryDTO.getId() == 0) {
        purchaseInventory = new PurchaseInventory();
        purchaseInventory.fromDTO(purchaseInventoryDTO, true);
        writer.save(purchaseInventory);
      } else {
        purchaseInventory = writer.getById(PurchaseInventory.class, purchaseInventoryDTO.getId());
        purchaseInventory.fromDTO(purchaseInventoryDTO, false);
        writer.update(purchaseInventory);
      }
      purchaseInventoryDTO.setId(purchaseInventory.getId());
      //没有inventory的先new出Inventory，生成productHistory
      for (PurchaseInventoryItemDTO purchaseInventoryItemDTO : purchaseInventoryDTO.getItemDTOs()) {
        if(StringUtils.isBlank(purchaseInventoryItemDTO.getProductName()) || purchaseInventoryItemDTO.getProductId() == null){
           continue;
        }
        Inventory inventory = inventoryMap.get(purchaseInventoryItemDTO.getProductId());
        if (inventory == null) {
          inventory = new Inventory();
          inventory.setId(purchaseInventoryItemDTO.getProductId());
          inventory.setShopId(shopId);
          inventory.setAmount(0d);
          inventory.setUnit(purchaseInventoryItemDTO.getUnit());
          writer.save(inventory);
          inventoryMap.put(purchaseInventoryItemDTO.getProductId(),inventory);
        }
        ProductLocalInfoDTO productLocalInfoDTO = productLocalInfoDTOMap.get(purchaseInventoryItemDTO.getProductId());
        ProductDTO productDTO = productDTOMap.get(purchaseInventoryItemDTO.getProductId());
        ProductHistory productHistory = new ProductHistory();
        productHistory.setProductDTO(productDTO);
        productHistory.setProductLocalInfoDTO(productLocalInfoDTO);
        productHistory.setInventoryDTO(inventory.toDTO());
        productHistoryMap.put(purchaseInventoryItemDTO.getProductId(),productHistory);
      }
      productHistoryService.batchSaveProductHistory(productHistoryMap,writer);

      for (PurchaseInventoryItemDTO itemDTO : purchaseInventoryDTO.getItemDTOs()) {
        if (itemDTO.getProductId() == null && StringUtils.isBlank(itemDTO.getProductName())) {
          continue;
        }
        itemDTO.setPurchaseInventoryId(purchaseInventory.getId());
        Long productLocalInfoId = itemDTO.getProductId();
        ProductLocalInfoDTO productLocalInfoDTO = productLocalInfoDTOMap.get(productLocalInfoId);
        ProductDTO productDTO = productDTOMap.get(productLocalInfoId);
        ProductHistory productHistory = productHistoryMap.get(productLocalInfoId);
        if(productHistory != null){
          itemDTO.setProductHistoryId(productHistory.getId());
        }
        if (itemDTO.getId()==null) {
          PurchaseInventoryItem purchaseInventoryItem = new PurchaseInventoryItem();
          purchaseInventoryItem.fromDTO(itemDTO);
          writer.save(purchaseInventoryItem);
          itemDTO.setId(purchaseInventoryItem.getId());
        } else {
          PurchaseInventoryItem purchaseInventoryItem = writer.getById(PurchaseInventoryItem.class, itemDTO.getId());
          purchaseInventoryItem.fromDTO(itemDTO);
          writer.update(purchaseInventoryItem);
        }


        Inventory inventory = inventoryMap.get(productLocalInfoId);
        if(inventory != null){
          inventoryService.caculateBeforeLimit(inventory.toDTO(),inventoryLimitDTO);
        }

        Double supplierInventoryChangeAmount, supplierInventoryPurchasePrice;
        //最新入库价
        double newPurchasePriceWithSellUnit = NumberUtil.doubleVal(itemDTO.getPurchasePrice());
        //最新销售价
        double newSalePriceWithSellUnit = NumberUtil.doubleVal(itemDTO.getRecommendedPrice());
        //最新批发价
//        double newTradePriceWithSellUnit = NumberUtil.doubleVal(itemDTO.getTradePrice());
        //原先系统库存量
        double formerInventoryAmount = inventory.getAmount();
        //原先系统库存平均价
        double formerInventoryAveragePrice = NumberUtil.doubleVal(inventory.getInventoryAveragePrice());
        //当前入库价
        double currentInventoryPrice = NumberUtil.doubleVal(itemDTO.getPurchasePrice());
        //当前入库量
        double currentInventoryAmount = NumberUtil.doubleVal(itemDTO.getAmount());
        //库存下限
        double lowerLimit = NumberUtil.doubleVal(itemDTO.getLowerLimit());
        //库存上限
        double upperLimit = NumberUtil.doubleVal(itemDTO.getUpperLimit());

        if (UnitUtil.isStorageUnit(itemDTO.getUnit(), productLocalInfoDTO)) {
          currentInventoryPrice = currentInventoryPrice / productLocalInfoDTO.getRate();
          currentInventoryAmount = currentInventoryAmount * productLocalInfoDTO.getRate();
          newPurchasePriceWithSellUnit = newPurchasePriceWithSellUnit / productLocalInfoDTO.getRate();
          newSalePriceWithSellUnit = newSalePriceWithSellUnit / productLocalInfoDTO.getRate();
          lowerLimit = lowerLimit * productLocalInfoDTO.getRate();
          upperLimit = upperLimit * productLocalInfoDTO.getRate();
        }
        double currentInventoryAveragePrice = inventoryService.calculateInventoryAveragePrice(
            formerInventoryAveragePrice, formerInventoryAmount, currentInventoryPrice, currentInventoryAmount);
        inventory.setInventoryAveragePrice(currentInventoryAveragePrice);
        inventory.setAmount(currentInventoryAmount + inventory.getAmount());
        inventory.setLowerLimit(lowerLimit);
        inventory.setUpperLimit(upperLimit);
        supplierInventoryChangeAmount = currentInventoryAmount;
        supplierInventoryPurchasePrice = currentInventoryPrice;
        if (newPurchasePriceWithSellUnit > 0.001) {
          inventory.setLatestInventoryPrice(newPurchasePriceWithSellUnit);
        }
          inventory.setLastStorageTime(purchaseInventoryDTO.getVestDate());
          inventory.setSalesPrice(newSalePriceWithSellUnit);
          //更新库存
          if(BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(shopVersionId)){
            StoreHouseInventoryDTO storeHouseInventoryDTO = new StoreHouseInventoryDTO(purchaseInventoryDTO.getStorehouseId(),itemDTO.getProductId(),null,currentInventoryAmount,itemDTO.getStorageBin());
            storeHouseService.saveOrUpdateStoreHouseInventoryDTO(writer,storeHouseInventoryDTO);
          }
          writer.update(inventory);
        inventoryService.caculateAfterLimit(inventory.toDTO(),inventoryLimitDTO);

        SupplierInventoryDTO  supplierInventoryDTO = new SupplierInventoryDTO(purchaseInventoryDTO, itemDTO);
        supplierInventoryDTO.addStorageInventoryChange(productLocalInfoDTO.getSellUnit(), supplierInventoryChangeAmount, supplierInventoryPurchasePrice);
        supplierInventoryDTOs.add(supplierInventoryDTO);

        InventorySearchIndex inventorySearchIndex = new InventorySearchIndex();
        inventorySearchIndex.createInventorySearchIndex(inventory != null ? inventory.toDTO() : null, productDTO);
        inventorySearchIndexList.add(inventorySearchIndex);
      }
      //更新施工单缺料状态，缺料待修-->来料待修
      handleRemindEventForRepairOrder(writer, shopId, shopVersionId, purchaseInventoryDTO);
      //出入库打通逻辑
      getProductInStorageService().productThroughByOrder(purchaseInventoryDTO, OrderTypes.INVENTORY,purchaseInventoryDTO.getStatus(),writer);
      productThroughService.saveOrUpdateSupplierInventory(writer, supplierInventoryDTOs);
      writer.commit(status);

      ServiceManager.getService(ISearchService.class).batchAddOrUpdateInventorySearchIndexWithList(shopId,inventorySearchIndexList);
      //更新库存上下限
      inventoryService.updateMemocacheLimitByInventoryLimitDTO(shopId,inventoryLimitDTO);
      return purchaseInventoryDTO;
    } finally {
      writer.rollback(status);
    }
  }

	@Override
	public PurchaseInventoryDTO getSimplePurchaseInventory(Long purchaseInventoryId, Long shopId) throws Exception {
		TxnWriter writer = txnDaoManager.getWriter();
    PurchaseInventory purchaseInventory = new PurchaseInventory();
    List<PurchaseInventory> purchaseInventories = writer.getPurchaseInventoryById(purchaseInventoryId, shopId);
    if (purchaseInventories != null && purchaseInventories.size() > 0) {
      purchaseInventory = purchaseInventories.get(0);
    } else {
      return null;
    }
    if (purchaseInventory != null) {
      PurchaseInventoryDTO purchaseInventoryDTO = purchaseInventory.toDTO();
      List<PurchaseInventoryItem> items = writer.getPurchaseInventoryItemsByInventoryId(purchaseInventoryId);
      PurchaseInventoryItemDTO[] itemDTOs = new PurchaseInventoryItemDTO[items.size()];
	    purchaseInventoryDTO.setItemDTOs(itemDTOs);
	    for (int i = 0; i < items.size(); i++) {
		    PurchaseInventoryItem item = items.get(i);
		    itemDTOs[i] = item.toDTO();
	    }
	    return purchaseInventoryDTO;
    }
		return null;
	}

  private void handleRemindEventForRepairOrder(TxnWriter writer, Long shopId, Long shopVersionId, PurchaseInventoryDTO purchaseInventoryDTO) throws Exception {
    IProductService productService = ServiceManager.getService(IProductService.class);
    IStoreHouseService storeHouseService = ServiceManager.getService(IStoreHouseService.class);
    IInventoryService inventoryService = ServiceManager.getService(IInventoryService.class);
    if (StringUtils.isBlank(purchaseInventoryDTO.getRepairOrderId())) {
      return;
    }
    Long repairOrderId = Long.valueOf(purchaseInventoryDTO.getRepairOrderId());
    if (BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(shopVersionId)) {
      RepairOrder repairOrder = writer.getRepairOrderById(repairOrderId, shopId);
      if (repairOrder == null || purchaseInventoryDTO.getStorehouseId() != null
          && !purchaseInventoryDTO.getStorehouseId().equals(repairOrder.getStorehouseId())) {
        return;
      }
    }
    List<RepairRemindEvent> repairRemindEvents = writer.getRepairRemindEventByShopIdAndOrderIdAndType(shopId, repairOrderId, RepairRemindEventTypes.LACK);
    if (repairRemindEvents.size() == 0) {
      purchaseInventoryDTO.setReturnType("");
      return;
    }
    Set<Long> productIds = purchaseInventoryDTO.getProductIdSet();
    Map<Long, ProductLocalInfoDTO> productLocalInfoDTOMap = productService.getProductLocalInfoMap(shopId, productIds.toArray(new Long[productIds.size()]));
    Map<Long, InventoryDTO> inventoryDTOMap = new HashMap<Long, InventoryDTO>();
    Map<Long, StoreHouseInventoryDTO> storeHouseInventoryDTOMap = new HashMap<Long, StoreHouseInventoryDTO>();
    if (BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(shopVersionId)) {
      storeHouseInventoryDTOMap = storeHouseService.getStoreHouseInventoryDTOMapByStorehouseAndProductIds(shopId,
          purchaseInventoryDTO.getStorehouseId(), productIds.toArray(new Long[productIds.size()]));
    } else {
      inventoryDTOMap = inventoryService.getInventoryDTOMap(shopId, productIds);
    }
    //new 库修改维修单缺料状态     todo check to delete
    for (Long productId : productIds) {
      ProductLocalInfoDTO productLocalInfoDTO = productLocalInfoDTOMap.get(productId);
      double inventoryAmount = 0d;
      if (BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(shopVersionId)) {
        StoreHouseInventoryDTO storeHouseInventoryDTO = storeHouseInventoryDTOMap.get(productId);
        inventoryAmount = storeHouseInventoryDTO == null ? 0d : storeHouseInventoryDTO.getAmount();
      } else {
        InventoryDTO inventoryDTO = inventoryDTOMap.get(productId);
        inventoryAmount = inventoryDTO == null ? 0d : inventoryDTO.getAmount();
      }
      for (RepairRemindEvent repairRemindEvent : repairRemindEvents) {
        if (productId.equals(repairRemindEvent.getProductId())) {
          double lackAmountWithSellUnit = NumberUtil.doubleVal(repairRemindEvent.getAmount());
          if (UnitUtil.isStorageUnit(repairRemindEvent.getUnit(), productLocalInfoDTO)) {
            lackAmountWithSellUnit = lackAmountWithSellUnit * productLocalInfoDTO.getRate();
          }
          if (inventoryAmount < lackAmountWithSellUnit - 0.0001) {
            purchaseInventoryDTO.setReturnType("");
            break;
          } else {
            inventoryAmount = inventoryAmount - lackAmountWithSellUnit;
            repairRemindEvent.setEventTypeEnum(RepairRemindEventTypes.INCOMING);
            writer.saveOrUpdate(repairRemindEvent);
            //add by WLF 提醒总表同步更新
            List<RemindEvent> remindEventList = writer.getRemindEventByOldRemindEventId(RemindEventType.REPAIR, shopId, repairRemindEvent.getId());
            if (CollectionUtil.isNotEmpty(remindEventList)) {
              remindEventList.get(0).setEventStatus(RepairRemindEventTypes.INCOMING.toString());
              writer.update(remindEventList.get(0));
            }
          }
        }
      }
    }
    purchaseInventoryDTO.setReturnType("1");
  }

  @Override
  public Result validateCopy(Long purchaseInventoryId, Long shopId) {
    ISupplierService supplierService = ServiceManager.getService(ISupplierService.class);
    IProductHistoryService productHistoryService = ServiceManager.getService(IProductHistoryService.class);
    TxnWriter writer = txnDaoManager.getWriter();
    PurchaseInventory purchaseInventory = new PurchaseInventory();
    List<PurchaseInventory> purchaseInventories = writer.getPurchaseInventoryById(purchaseInventoryId, shopId);
    if (purchaseInventories != null && purchaseInventories.size() > 0) {
      purchaseInventory = purchaseInventories.get(0);
    } else {
      return new Result("无法复制", "单据不存在，无法复制！", false, Result.Operation.ALERT);
    }
    PurchaseInventoryDTO purchaseInventoryDTO = purchaseInventory.toDTO();
    SupplierDTO supplierDTO = purchaseInventoryDTO.generateSupplierDTO();
    supplierDTO.setId(purchaseInventoryDTO.getSupplierId());
    boolean supplierSame = supplierService.compareSupplierSameWithHistory(supplierDTO, shopId);

    List<PurchaseInventoryItem> items = writer.getPurchaseInventoryItemsByInventoryId(purchaseInventoryId);
    Map<Long, Long> localInfoIdAndHistoryIdMap = new HashMap<Long, Long>();
    if(CollectionUtils.isNotEmpty(items)){
      for(PurchaseInventoryItem item: items){
        localInfoIdAndHistoryIdMap.put(item.getProductId(), item.getProductHistoryId());
      }
    }
    boolean productSame = productHistoryService.compareProductSameWithHistory(localInfoIdAndHistoryIdMap, shopId);
    if(supplierSame && productSame){
      return new Result("通过校验", true);
    }else if(supplierSame){
      return new Result("提示", "此单据中的商品信息已被修改，请确认是否继续复制。<br/><br/>如果继续，已被修改过的商品将不会被复制。", false, Result.Operation.CONFIRM);
    }else if(productSame){
      return new Result("提示", "此单据中的供应商信息已被修改，请确认是否继续复制。<br/><br/>如果继续，供应商信息将不会被复制。", false, Result.Operation.CONFIRM);
    }else{
      return new Result("提示", "此单据中的供应商信息与商品信息已被修改，请确认是否继续复制。<br/><br/>如果继续，供应商信息与已被修改过的商品将不会被复制。", false, Result.Operation.CONFIRM);
    }
  }

  @Override
  public SupplierDTO handleSupplierForGoodsStorage(PurchaseInventoryDTO purchaseInventoryDTO) throws Exception {
    SupplierDTO supplierDTO = new SupplierDTO();
    if (purchaseInventoryDTO.getSupplierId() != null) {
      supplierDTO = ServiceManager.getService(IUserService.class).getSupplierById(purchaseInventoryDTO.getSupplierId());
    }
    if ((purchaseInventoryDTO.getSupplierId() == null && StringUtils.isNotBlank(purchaseInventoryDTO.getSupplier())) ||
        (supplierDTO != null && CustomerStatus.DISABLED.equals(supplierDTO.getStatus()))) {
      supplierDTO = getSupplierService().getSupplierDTOByPreciseName(purchaseInventoryDTO.getShopId(), purchaseInventoryDTO.getSupplier());
    }
    if (supplierDTO == null) {
      supplierDTO = new SupplierDTO();
    }
    supplierDTO.set(purchaseInventoryDTO);

    if (supplierDTO.getId() == null || supplierDTO != null && CustomerStatus.DISABLED.equals(supplierDTO.getStatus())) {
      getUserService().createSupplier(supplierDTO);
      // 新增设置contactId
      supplierDTO = getUserService().getSupplierById(supplierDTO.getId());
      purchaseInventoryDTO.setContactId(supplierDTO.getContactId());
      getSupplierRecordService().createSupplierRecordUsingSupplierDTO(supplierDTO);
    }else {
      if(supplierDTO.getCustomerId() == null && supplierDTO.isAddContacts()){
        getUserService().updateSupplier(supplierDTO);
        if(!ArrayUtils.isEmpty(supplierDTO.getContacts())
            && supplierDTO.getContacts()[0] != null
            && supplierDTO.getContacts()[0].getId() != null )
        purchaseInventoryDTO.setContactId(supplierDTO.getContacts()[0].getId());
      }
    }
    purchaseInventoryDTO.setSupplierId(supplierDTO.getId());

    // add by zhuj 既是客户又是供应商
    if(supplierDTO.getCustomerId() != null) {
      //同时更新客户的信息
      CustomerDTO customerDTO =  ServiceManager.getService(IUserService.class).getCustomerById(supplierDTO.getCustomerId());
      customerDTO.fromSupplierDTO(supplierDTO);
      ServiceManager.getService(IUserService.class).updateCustomer(customerDTO);
      if(supplierDTO.isAddContacts()) {
        ServiceManager.getService(IContactService.class).addContactsBelongCustomerAndSupplier(supplierDTO.getCustomerId(),
            supplierDTO.getId(), purchaseInventoryDTO.getShopId(), supplierDTO.getContacts()); // add by zhuj 既是客户又是供应商的联系人新增
      }
      CustomerRecordDTO customerRecordDTO = ServiceManager.getService(IUserService.class).getCustomerRecordByCustomerId(supplierDTO.getCustomerId()).get(0);
      customerRecordDTO.fromCustomerDTO(customerDTO);
      ServiceManager.getService(IUserService.class).updateCustomerRecord(customerRecordDTO);
    }

    return supplierDTO;
  }

  @Override
  public Map<Long, PurchaseInventoryDTO> getSimplePurchaseInventoryByPurchaseOrderIds(Long shopId, Long... purchaseOrderId) throws Exception {
    Map<Long,PurchaseInventoryDTO> purchaseInventoryDTOMap = new HashMap<Long, PurchaseInventoryDTO>();
    if(shopId == null || ArrayUtil.isEmpty(purchaseOrderId)){
      return purchaseInventoryDTOMap;
    }
    List<PurchaseInventory> purchaseInventories = txnDaoManager.getWriter().getPurchaseInventoryIdByPurchaseOrderIds(shopId,purchaseOrderId);
    if(CollectionUtil.isNotEmpty(purchaseInventories)){
      for(PurchaseInventory purchaseInventory : purchaseInventories){
        if(purchaseInventory.getPurchaseOrderId() == null){
          continue;
        }
        PurchaseInventoryDTO purchaseInventoryDTO =  purchaseInventoryDTOMap.get(purchaseInventory.getPurchaseOrderId());
        if(purchaseInventoryDTO != null){
          if(OrderStatus.PURCHASE_INVENTORY_DONE.equals(purchaseInventory.getStatusEnum())){
            purchaseInventoryDTOMap.put(purchaseInventory.getPurchaseOrderId(),purchaseInventory.toDTO());
          }
        }else {
          purchaseInventoryDTOMap.put(purchaseInventory.getPurchaseOrderId(),purchaseInventory.toDTO());
        }
      }
    }
    return purchaseInventoryDTOMap;
  }

  public Result checkPurchaseOrderInventory(Long shopId, String purchaseOrderId) {
    Result result = new Result(true);
    if (NumberUtil.isNumber(purchaseOrderId) && StringUtils.isNotBlank(purchaseOrderId) && shopId != null) {
      PurchaseOrderDTO purchaseOrderDTO = ServiceManager.getService(IGoodBuyService.class).getSimplePurchaseOrderDTO(shopId, NumberUtil.longValue(purchaseOrderId));
      SalesOrderDTO salesOrderDTO = getGoodSaleService().getSimpleSalesOrderByPurchaseOrderId(NumberUtil.longValue(purchaseOrderId));
      if (purchaseOrderDTO == null) {
        result.setSuccess(false);
        return result;
      }
      if (salesOrderDTO == null) {
        if (!OrderStatus.PURCHASE_ORDER_WAITING.equals(purchaseOrderDTO.getStatus())) {
          StringBuffer stringBuffer = new StringBuffer();
          stringBuffer.append("当前采购单状态:");
          if (purchaseOrderDTO.getStatus() == null) {
            stringBuffer.append("无");
          } else {
            stringBuffer.append(purchaseOrderDTO.getStatus().getName());
          }
          stringBuffer.append(",无法入库！");
          result.setSuccess(false);
          result.setMsg(stringBuffer.toString());
          result.setOperation(Result.Operation.ALERT.getValue());
          return result;
        }
      } else {
        if (OrderStatus.SELLER_STOCK.equals(purchaseOrderDTO.getStatus())
            && OrderStatus.STOCKING.equals(salesOrderDTO.getStatus())
            && salesOrderDTO.isShortage()) {
          result.setSuccess(false);
          result.setMsg("当前采购单对应销售单为缺料，无法入库！");
          result.setOperation(Result.Operation.ALERT.getValue());
          return result;
        }
        if (!(OrderStatus.SELLER_STOCK.equals(purchaseOrderDTO.getStatus())
            || OrderStatus.SELLER_DISPATCH.equals(purchaseOrderDTO.getStatus())
            || OrderStatus.PURCHASE_ORDER_WAITING.equals(purchaseOrderDTO.getStatus()))) {
          StringBuffer stringBuffer = new StringBuffer();
          stringBuffer.append("当前采购单状态:");
          if (purchaseOrderDTO.getStatus() == null) {
            stringBuffer.append("无");
          } else {
            stringBuffer.append(purchaseOrderDTO.getStatus().getName());
          }
          stringBuffer.append(",无法入库！");
          result.setSuccess(false);
          result.setMsg(stringBuffer.toString());
          result.setOperation(Result.Operation.ALERT.getValue());
          return result;
        }
      }
    } else {
      result.setSuccess(false);
    }
    return result;
  }

  @Override
  public Long getNextProductId(Long shopId, long startProductId, int defaultPageSize) {
    return txnDaoManager.getWriter().getNextProductId(shopId,startProductId,defaultPageSize);
  }

  @Override
  public List<SupplierInventoryDTO> getInitSupplierInventory(Long shopId, Long shopVersionId, Long startProductId, Long endProductId) {
    if(BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(shopVersionId)){
      return txnDaoManager.getWriter().getInitHaveStoreHouseSupplierInventoryInfo(shopId, startProductId, endProductId);
    } else {
      return txnDaoManager.getWriter().getInitNoStoreHouseSupplierInventoryInfo(shopId, startProductId, endProductId);
    }
  }

  @Override
  public void cancelPurchaseInventoryToUpdateAveragePriceAndLastInfo(PurchaseInventoryDTO purchaseInventoryDTO, TxnWriter writer)throws Exception{
     if(purchaseInventoryDTO == null || purchaseInventoryDTO.getShopId() == null
         || !OrderStatus.PURCHASE_INVENTORY_REPEAL.equals(purchaseInventoryDTO.getStatus())
         || purchaseInventoryDTO.getSupplierId() == null){
       return;
     }
    IProductService productService = ServiceManager.getService(IProductService.class);
    IProductThroughService productThroughService = ServiceManager.getService(IProductThroughService.class);

    Set<Long> productIds= new HashSet<Long>();
    Long shopId = purchaseInventoryDTO.getShopId();
    Long supplierId = purchaseInventoryDTO.getSupplierId();
    Long storehouseId = purchaseInventoryDTO.getStorehouseId();
    if(!ArrayUtils.isEmpty(purchaseInventoryDTO.getItemDTOs())){
      for(PurchaseInventoryItemDTO itemDTO : purchaseInventoryDTO.getItemDTOs()){
        if(itemDTO.getProductId() != null){
          productIds.add(itemDTO.getProductId());
        }
      }
    }
    Map<Long,SupplierInventory> supplierInventoryMap = productThroughService.getSupplierInventoryMapByPurchaseInventoryId(shopId,purchaseInventoryDTO.getId());
    if(supplierInventoryMap == null || supplierInventoryMap.isEmpty()){
      return;
    }
    Map<Long,ProductLocalInfoDTO> productLocalInfoDTOMap =  productService.getProductLocalInfoMap(shopId,productIds.toArray(new Long[productIds.size()]));
    Map<Long,PurchaseInventoryItemDTO> purchaseInventoryItemDTOMap = getLastPurchaseInventoryItemDTOMap(shopId, supplierId,
        storehouseId, productIds,productLocalInfoDTOMap);
    List<SupplierInventoryDTO> toUpdateSupplierInventoryDTO = new ArrayList<SupplierInventoryDTO>();
    for(Long productId : productIds){
      SupplierInventory  supplierInventory = supplierInventoryMap.get(productId);
      if(supplierInventory == null){
        continue;
      }
      SupplierInventoryDTO supplierInventoryDTO = supplierInventory.toDTO();
      PurchaseInventoryItemDTO purchaseInventoryItemDTO =  purchaseInventoryItemDTOMap.get(productId);
      if(purchaseInventoryItemDTO != null){
        supplierInventoryDTO.setLastPurchaseInventoryOrderId(purchaseInventoryItemDTO.getPurchaseInventoryId());
        supplierInventoryDTO.setLastStorageAmount(purchaseInventoryItemDTO.getAmount());
        supplierInventoryDTO.setLastStorageTime(purchaseInventoryItemDTO.getVestDate());
        supplierInventoryDTO.setLastStoragePrice(purchaseInventoryItemDTO.getPrice());
      }else{
        supplierInventoryDTO.setLastPurchaseInventoryOrderId(null);
        supplierInventoryDTO.setLastStorageAmount(null);
        supplierInventoryDTO.setLastStorageTime(null);
        supplierInventoryDTO.setLastStoragePrice(null);
      }
      toUpdateSupplierInventoryDTO.add(supplierInventoryDTO);
    }
    productThroughService.saveOrUpdateSupplierInventoryByModify(writer,toUpdateSupplierInventoryDTO);

  }

  /**
   * 根据查询条件找到最后入库记录，一张入库单上有多相同条商品的，取第一条入库价不为空的。
   * @param shopId
   * @param supplierId
   * @param storehouseId
   * @param productIds
   * @return
   */
  private Map<Long, PurchaseInventoryItemDTO> getLastPurchaseInventoryItemDTOMap(Long shopId, Long supplierId, Long storehouseId,
                                                                                 Set<Long> productIds, Map<Long, ProductLocalInfoDTO> productLocalInfoDTOMap) {
    Map<Long, PurchaseInventoryItemDTO> purchaseInventoryItemDTOMap = new HashMap<Long, PurchaseInventoryItemDTO>();
    if (shopId == null || supplierId == null || CollectionUtils.isEmpty(productIds)) {
      return purchaseInventoryItemDTOMap;
    }
    TxnWriter writer = txnDaoManager.getWriter();
    List<PurchaseInventoryItemDTO> purchaseInventoryItemDTOs = writer.getLastPurchaseInventoryItemDTOs(shopId, supplierId, storehouseId, productIds);

    Set<Long> purchaseInventoryIds = new HashSet<Long>();
    if (CollectionUtils.isNotEmpty(purchaseInventoryItemDTOs)) {
       for(PurchaseInventoryItemDTO purchaseInventoryItemDTO :purchaseInventoryItemDTOs ){
          if(purchaseInventoryItemDTO.getPurchaseInventoryId() != null){
            purchaseInventoryIds.add(purchaseInventoryItemDTO.getPurchaseInventoryId());
            if(purchaseInventoryItemDTO.getProductId() != null){
              ProductLocalInfoDTO productLocalInfoDTO = productLocalInfoDTOMap.get(purchaseInventoryItemDTO.getProductId());
              double itemAmount =   NumberUtil.doubleVal(purchaseInventoryItemDTO.getAmount());
              double purchasePrice = NumberUtil.doubleVal(purchaseInventoryItemDTO.getPrice());
              String sellUnit =   purchaseInventoryItemDTO.getUnit();
              if(UnitUtil.isStorageUnit(purchaseInventoryItemDTO.getUnit(),productLocalInfoDTO)){
                itemAmount = NumberUtil.round(itemAmount * productLocalInfoDTO.getRate(),2) ;
                purchasePrice = NumberUtil.round(purchasePrice/productLocalInfoDTO.getRate(),2);
              }
              if(productLocalInfoDTO != null){
                sellUnit = productLocalInfoDTO.getSellUnit();
              }
              purchaseInventoryItemDTO.setUnit(sellUnit);
              purchaseInventoryItemDTO.setAmount(itemAmount);
              purchaseInventoryItemDTO.setPrice(purchasePrice);
              purchaseInventoryItemDTOMap.put(purchaseInventoryItemDTO.getProductId(),purchaseInventoryItemDTO);
            }
          }
       }
    }

    List<PurchaseInventoryItem> allPurchaseInventoryItems = writer.getAllPurchaseInventoryItems(shopId,purchaseInventoryIds,productIds);
    if(CollectionUtils.isNotEmpty(allPurchaseInventoryItems)){
      for(PurchaseInventoryItem purchaseInventoryItem : allPurchaseInventoryItems){
        PurchaseInventoryItemDTO purchaseInventoryItemDTO = purchaseInventoryItemDTOMap.get(purchaseInventoryItem.getProductId());
        if(purchaseInventoryItemDTO != null
            && purchaseInventoryItem.getId() != null
            && purchaseInventoryItem.getPurchaseInventoryId() != null
            && purchaseInventoryItemDTO.getId() != null
            && !purchaseInventoryItem.getId().equals(purchaseInventoryItemDTO.getId())
            && purchaseInventoryItem.getPurchaseInventoryId().equals(purchaseInventoryItemDTO.getPurchaseInventoryId())){
          ProductLocalInfoDTO productLocalInfoDTO = productLocalInfoDTOMap.get(purchaseInventoryItemDTO.getProductId());
          double lastAmount =   NumberUtil.doubleVal(purchaseInventoryItemDTO.getAmount());
          double lastPurchasePrice = NumberUtil.doubleVal(purchaseInventoryItemDTO.getPrice());

          double secondItemAmount = NumberUtil.doubleVal(purchaseInventoryItem.getAmount());
          double secondPrice = NumberUtil.doubleVal(purchaseInventoryItem.getPrice());

          String secondUnit = purchaseInventoryItem.getUnit();
          if (UnitUtil.isStorageUnit(secondUnit, productLocalInfoDTO)) {
            secondItemAmount = NumberUtil.round(secondItemAmount * productLocalInfoDTO.getRate(), 2);
            secondPrice = NumberUtil.round(secondPrice / productLocalInfoDTO.getRate(), 2);
          }
          lastAmount = NumberUtil.round(lastAmount + secondItemAmount, 2);
          if(lastPurchasePrice<0.0001 && secondPrice>0.0001){
            lastPurchasePrice = secondPrice;
          }
          purchaseInventoryItemDTO.setPrice(lastPurchasePrice);
          purchaseInventoryItemDTO.setAmount(lastAmount);
        }
      }
    }
    return purchaseInventoryItemDTOMap;

  }

  @Override
  public void cancelPurchaseInventoryToUpdateAveragePriceAndLastInfo( PurchaseInventoryDTO purchaseInventoryDTO)throws Exception{
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try{
      cancelPurchaseInventoryToUpdateAveragePriceAndLastInfo(purchaseInventoryDTO,writer);
      writer.commit(status);
    }finally {
      writer.rollback(status);
    }
  }

  @Override
  public void handleProductForSavePurchaseInventory(PurchaseInventoryDTO purchaseInventoryDTO) throws Exception{
    IProductService productService = ServiceManager.getService(IProductService.class);
    IInventoryService inventoryService = ServiceManager.getService(IInventoryService.class);
    Long shopId = purchaseInventoryDTO.getShopId();
    //新增或者更新产品信息
    List<ProductDTO> toUpdateProductDTOs = new ArrayList<ProductDTO>();
    Set<Long> productIds = new HashSet<Long>();  //product表的id
    Set<Long> productLocalInfoIds = new HashSet<Long>();//productLocalInfo id
    List<PurchaseInventoryItemDTO> toAddUnitItemDTOs = new ArrayList<PurchaseInventoryItemDTO>();
    Set<Long> deletedProductIds = new HashSet<Long>();
    //处理商品分类
    Set<String> productKindNames = purchaseInventoryDTO.getProductKindNames();
    Map<String,KindDTO> kindDTOMap = productService.batchSaveAndGetProductKind(shopId,productKindNames);
    purchaseInventoryDTO.setKindIds(kindDTOMap);

    //1.没有productId的item去根据7字段匹配取出product表的id
    for (PurchaseInventoryItemDTO purchaseInventoryItemDTO : purchaseInventoryDTO.getItemDTOs()) {
      if (StringUtils.isBlank(purchaseInventoryItemDTO.getProductName())) {
        continue;
      }
      if (purchaseInventoryDTO.getShopId() != null && purchaseInventoryItemDTO.getProductId() == null) {
        ProductDTO productSearchCondition = new ProductDTO(purchaseInventoryDTO.getShopId(), purchaseInventoryItemDTO);
        List<ProductDTO> productDTOs = productService.getProductDTOsBy7P(purchaseInventoryDTO.getShopId(), productSearchCondition);
        ProductDTO productDTO = CollectionUtil.getFirst(productDTOs);
        if (productDTO != null) {
          purchaseInventoryItemDTO.setProductOriginId(productDTO.getId());
          productIds.add(productDTO.getId());
        }
      }
    }
    //根据productId取出productLocalInfoId
    Map<Long, ProductLocalInfoDTO> productIdProductLocalInfoDTOMap = productService.getProductLocalInfoMapByProductIds(shopId, productIds);
    for (PurchaseInventoryItemDTO purchaseInventoryItemDTO : purchaseInventoryDTO.getItemDTOs()) {
      if (StringUtils.isBlank(purchaseInventoryItemDTO.getProductName())) {
        continue;
      }
      if (purchaseInventoryItemDTO.getProductOriginId() != null) {
        ProductLocalInfoDTO productLocalInfoDTO = productIdProductLocalInfoDTOMap.get(purchaseInventoryItemDTO.getProductOriginId());
        if (productLocalInfoDTO != null) {
          purchaseInventoryItemDTO.setProductId(productLocalInfoDTO.getId());
        }
      }
    }
    productLocalInfoIds = purchaseInventoryDTO.getProductIdSet();
    Map<Long, ProductLocalInfoDTO> productLocalInfoDTOMap = productService.getProductLocalInfoMap(shopId,
        productLocalInfoIds.toArray(new Long[productLocalInfoIds.size()]));
    //新增产品
    for (PurchaseInventoryItemDTO purchaseInventoryItemDTO : purchaseInventoryDTO.getItemDTOs()) {
      if (StringUtils.isBlank(purchaseInventoryItemDTO.getProductName())) {
        continue;
      }
      boolean isNewProductFlag = false;
      ProductDTO productDTO = new ProductDTO(purchaseInventoryDTO.getShopId(), purchaseInventoryItemDTO);
      productDTO.setProductLocalInfoId(purchaseInventoryItemDTO.getProductId());
      if (purchaseInventoryItemDTO.getProductId() == null) {
        isNewProductFlag = productService.addProduct(productDTO);
        if(isNewProductFlag){
          if(productDTO.getProductVehicleBrandId() != null || productDTO.getProductVehicleModelId() != null){
            purchaseInventoryItemDTO.setVehicleBrandId(productDTO.getVehicleBrandId());
            purchaseInventoryItemDTO.setVehicleModelId(productDTO.getProductVehicleModelId());
            purchaseInventoryItemDTO.setAddVehicleInfoToSolr(true);
          }
        }
        purchaseInventoryItemDTO.setProductOriginId(productDTO.getId());
        purchaseInventoryItemDTO.setProductId(productDTO.getProductLocalInfoId());
      }
      if (!isNewProductFlag) {
        toUpdateProductDTOs.add(productDTO);
        ProductLocalInfoDTO productLocalInfoDTO = productLocalInfoDTOMap.get(purchaseInventoryItemDTO.getProductId());
        //需要添加单位的item
        if (StringUtils.isNotBlank(purchaseInventoryItemDTO.getUnit()) && productLocalInfoDTO != null && StringUtils.isBlank(productLocalInfoDTO.getSellUnit())) {
          toAddUnitItemDTOs.add(purchaseInventoryItemDTO);
        }
      }
    }

        //更新维修单中原先库存存在且不带单位的商品的单位
    if(CollectionUtils.isNotEmpty(toAddUnitItemDTOs)) {
      txnService.updateProductUnit(shopId, toAddUnitItemDTOs.toArray(new PurchaseInventoryItemDTO[toAddUnitItemDTOs.size()]));
    }

    //更新产品信息
    if (CollectionUtils.isNotEmpty(toUpdateProductDTOs)) {
      productService.updateProductForPurchaseInventory(shopId, deletedProductIds, toUpdateProductDTOs.toArray(new ProductDTO[toUpdateProductDTOs.size()]));
    }
    //删除商品恢复重新计算上下限
    if (CollectionUtils.isNotEmpty(deletedProductIds)) {
      Map<Long, InventoryDTO> deleteInventoryDTOs = inventoryService.getInventoryDTOMap(shopId, deletedProductIds);
      if (MapUtils.isNotEmpty(deleteInventoryDTOs)) {
        for (InventoryDTO inventoryDTO : deleteInventoryDTOs.values()) {
          inventoryService.caculateAfterLimit(inventoryDTO, purchaseInventoryDTO.getInventoryLimitDTO());
        }
      }
    }

  }


}
