package com.bcgogo.txn.service;

import com.bcgogo.common.Result;
import com.bcgogo.config.dto.OperationLogDTO;
import com.bcgogo.config.service.IOperationLogService;
import com.bcgogo.enums.*;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.product.dto.ProductLocalInfoDTO;
import com.bcgogo.product.service.IProductService;
import com.bcgogo.search.model.InventorySearchIndex;
import com.bcgogo.search.service.ISearchService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.bcgogoListener.orderEvent.BcgogoOrderReindexEvent;
import com.bcgogo.txn.bcgogoListener.publisher.BcgogoEventPublisher;
import com.bcgogo.txn.dto.*;
import com.bcgogo.txn.model.*;
import com.bcgogo.txn.service.productThrough.IProductInStorageService;
import com.bcgogo.txn.service.productThrough.IProductOutStorageService;
import com.bcgogo.user.service.utils.BcgogoShopLogicResourceUtils;
import com.bcgogo.utils.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 12-12-17
 * Time: 下午1:47
 * To change this template use File | Settings | File Templates.
 */
@Component
public class PickingService implements IPickingService {
  @Autowired
  private TxnDaoManager txnDaoManager;

  private IProductService productService;
  private IInventoryService inventoryService;
  private ITxnService txnService;
  private RFITxnService rfTxnService;
  private IStoreHouseService storeHouseService;
  private ISearchService searchService;
  private IProductHistoryService productHistoryService;
  private IOperationLogService operationLogService;
  private IRepairService repairService;
  private IProductOutStorageService productOutStorageService;

  public IProductService getProductService() {
    return productService == null ? ServiceManager.getService(IProductService.class) :productService;
  }

  public IInventoryService getInventoryService() {
    return inventoryService == null ?ServiceManager.getService(IInventoryService.class) :inventoryService;
  }

  public ITxnService getTxnService() {
    return txnService == null ?ServiceManager.getService(ITxnService.class) : txnService;
  }

  public RFITxnService getRfTxnService() {
    return rfTxnService == null ?ServiceManager.getService(RFITxnService.class) : rfTxnService;
  }

  public IStoreHouseService getStoreHouseService() {
    return storeHouseService == null ?ServiceManager.getService(IStoreHouseService.class) : storeHouseService;
  }

  public ISearchService getSearchService() {
    return searchService == null ?ServiceManager.getService(ISearchService.class) : searchService;
  }

  public IProductHistoryService getProductHistoryService() {
    return productHistoryService == null ? ServiceManager.getService(IProductHistoryService.class) : productHistoryService;
  }

  public IOperationLogService getOperationLogService() {
    return operationLogService == null ? ServiceManager.getService(IOperationLogService.class) : operationLogService;
  }

  public IRepairService getRepairService() {
    return repairService == null ? ServiceManager.getService(IRepairService.class) : repairService;
  }

  public IProductOutStorageService getProductOutStorageService() {
    return productOutStorageService == null ? ServiceManager.getService(IProductOutStorageService.class) : productOutStorageService;
  }

  @Override
  public List<RepairPickingDTO> getRepairPickingDTODetails(RepairPickingDTO searchCondition)throws Exception{
    List<RepairPickingDTO> repairPickingDTOs = new ArrayList<RepairPickingDTO>();
    if(searchCondition == null || searchCondition.getShopId() == null){
      return   repairPickingDTOs;
    }
    searchCondition.initSearchTime();
    TxnWriter writer = txnDaoManager.getWriter();
    List<RepairPicking> repairPickings =writer.getRepairPicks(searchCondition);
    Set<Long> repairPickingIds = new HashSet<Long>();
    if(CollectionUtils.isNotEmpty(repairPickings)){
      for(RepairPicking repairPicking :repairPickings){
        repairPickingIds.add(repairPicking.getId());
      }
    }
    repairPickingDTOs = getRepairPickingDTODetails(searchCondition.getShopId(), repairPickingIds.toArray(new Long[repairPickingIds.size()]));
    List<RepairPickingDTO> sortRepairPickingDTO = new ArrayList<RepairPickingDTO>();
    if(CollectionUtils.isNotEmpty(repairPickings) && CollectionUtils.isNotEmpty(repairPickingDTOs)){
       for(RepairPicking repairPicking :repairPickings){
         for(RepairPickingDTO repairPickingDTO :repairPickingDTOs ){
           if(repairPicking.getId().equals(repairPickingDTO.getId())){
             sortRepairPickingDTO.add(repairPickingDTO);
             break;
           }
         }
       }
    }
    return sortRepairPickingDTO;
  }

  @Override
  public List<RepairPickingDTO> getRepairPickingDTODetails(Long shopId, Long... ids) throws Exception{
    List<RepairPickingDTO> repairPickingDTOs = new ArrayList<RepairPickingDTO>();
    if(shopId == null || ids == null || ArrayUtils.isEmpty(ids)){
      return repairPickingDTOs;
    }
    TxnWriter writer = txnDaoManager.getWriter();
    List<RepairPicking> repairPickings = writer.getRepairPicksByIds(shopId, ids);
    Set<Long> repairPickingIds = new HashSet<Long>();
    Set<Long> repairOrderIds = new HashSet<Long>();
    Map<Long,RepairOrderDTO> repairOrderDTOMap = new HashMap<Long, RepairOrderDTO>();
    List<RepairPickingItem> repairPickingItemList = new ArrayList<RepairPickingItem>();
    Set<Long> productIds = new HashSet<Long>();
    Map<Long, ProductDTO> productDTOMap = new HashMap<Long, ProductDTO>();
    Map<Long, InventoryDTO> inventoryDTOMap = new HashMap<Long, InventoryDTO>();
    if(CollectionUtils.isNotEmpty(repairPickings)){
      for(RepairPicking repairPicking :repairPickings){
        repairPickingIds.add(repairPicking.getId());
        repairOrderIds.add(repairPicking.getRepairOrderId());
      }
    }
    if (CollectionUtils.isNotEmpty(repairPickingIds)) {
      repairPickingItemList = writer.getRepairPickingItemsByOrderIds(repairPickingIds.toArray(new Long[repairPickings.size()]));
    }
    if (CollectionUtils.isNotEmpty(repairOrderIds)) {
      repairOrderDTOMap = getTxnService().getRepairOrderMapByShopIdAndOrderIds(shopId,repairOrderIds.toArray(new Long[repairOrderIds.size()]));
    }

    if (CollectionUtils.isNotEmpty(repairPickingItemList)) {
      for (RepairPickingItem repairPickingItem : repairPickingItemList) {
        productIds.add(repairPickingItem.getProductId());
      }
    }
    if (CollectionUtils.isNotEmpty(productIds)) {
      productDTOMap = getProductService().getProductDTOMapByProductLocalInfoIds(shopId, productIds);
      inventoryDTOMap = getInventoryService().getInventoryDTOMap(shopId,productIds);
    }
    if (CollectionUtils.isNotEmpty(repairPickings)) {
      for (RepairPicking repairPicking : repairPickings) {
        RepairPickingDTO repairPickingDTO = repairPicking.toDTO();
        RepairOrderDTO repairOrderDTO = repairOrderDTOMap.get(repairPicking.getRepairOrderId());
        if (repairOrderDTO != null) {
          repairPickingDTO.setVehicle(repairOrderDTO.getVechicle());
          repairPickingDTO.setCustomer(repairOrderDTO.getCustomerName());
        }
        List<RepairPickingItemDTO> repairPickingItemDTOs = new ArrayList<RepairPickingItemDTO>();
        if (CollectionUtils.isNotEmpty(repairPickingItemList)) {
          for (RepairPickingItem repairPickingItem : repairPickingItemList) {
            if(repairPickingItem.getProductId() == null || !repairPickingDTO.getId().equals(repairPickingItem.getRepairPickingId())){
              continue;
            }
            RepairPickingItemDTO repairPickingItemDTO = repairPickingItem.toDTO();
            ProductDTO productDTO = productDTOMap.get(repairPickingItem.getProductId());
            InventoryDTO inventoryDTO = inventoryDTOMap.get(repairPickingItem.getProductId());
            if(repairPickingItemDTO!=null && productDTO!=null){
              repairPickingItemDTO.setProductDTO(productDTO);
              repairPickingItemDTO.changeItemAmountUnit();
            }
            if(repairPickingItemDTO!=null && inventoryDTO!=null){
              repairPickingItemDTO.setInventoryDTO(inventoryDTO);
            }
            repairPickingItemDTOs.add(repairPickingItemDTO);
          }
          repairPickingDTO.setTotalItemDTOs(repairPickingItemDTOs);
        }
        repairPickingDTOs.add(repairPickingDTO);
      }
    }
    //初始化维修领料Item上有仓库的inventoryAmount
    if (CollectionUtils.isNotEmpty(repairPickingDTOs)) {
      for (RepairPickingDTO repairPickingDTO : repairPickingDTOs) {
        if (CollectionUtils.isNotEmpty(repairPickingDTO.getTotalItemDTOs()) && repairPickingDTO.getStorehouseId() != null) {
          Map<Long, StoreHouseInventoryDTO> storeHouseInventoryDTOMap = new HashMap<Long, StoreHouseInventoryDTO>();
          Set<Long> itemProductIds = new HashSet<Long>();
          for (RepairPickingItemDTO repairPickingItemDTO : repairPickingDTO.getTotalItemDTOs()) {
            if (repairPickingItemDTO.getProductId() != null) {
              itemProductIds.add(repairPickingItemDTO.getProductId());
            }
          }
          storeHouseInventoryDTOMap = getStoreHouseService().getStoreHouseInventoryDTOMapByStorehouseAndProductIds(repairPickingDTO.getShopId(),
              repairPickingDTO.getStorehouseId(), itemProductIds.toArray(new Long[itemProductIds.size()]));
          for (RepairPickingItemDTO repairPickingItemDTO : repairPickingDTO.getTotalItemDTOs()) {
            if (repairPickingItemDTO.getProductId() != null) {
              StoreHouseInventoryDTO storeHouseInventoryDTO = storeHouseInventoryDTOMap.get(repairPickingItemDTO.getProductId());
              if (storeHouseInventoryDTO != null) {
                repairPickingItemDTO.setInventoryAmount(storeHouseInventoryDTO.getAmount());
              }else {
                repairPickingItemDTO.setInventoryAmount(0d);
              }
            }

          }
        }
      }
    }
    if(CollectionUtils.isNotEmpty(repairPickingDTOs)){
      for(RepairPickingDTO repairPickingDTO :repairPickingDTOs){
        repairPickingDTO.initRepairPickingDTO();
      }
    }
    return repairPickingDTOs;
  }

  @Override
  public RepairPickingDTO getRepairPickDTODById(Long shopId, Long id) throws Exception{
    return CollectionUtil.uniqueResult(getRepairPickingDTODetails(shopId,id));
  }

  @Override
  public int countRepairPickDTOs(RepairPickingDTO searchCondition) {
    if (searchCondition == null || searchCondition.getShopId() == null) {
      return 0;
    }
    searchCondition.initSearchTime();
    return txnDaoManager.getWriter().countRepairPicks(searchCondition);
  }



  @Override
  public Result verifyOutStorage(RepairPickingDTO repairPickingDTO) {
    Result result = new Result();
    if(repairPickingDTO == null){
      result.setSuccess(false);
      result.setMsg(ValidatorConstant.NO_REPAIR_PICKING_TO_OUT_STORAGE);
      result.setOperation(Result.Operation.ALERT.getValue());
      return result;
    }
    if (OrderStatus.REPEAL.equals(repairPickingDTO.getStatus())) {
      result.setSuccess(false);
      result.setMsg("当前单据对应的施工单已经作废，无法出库");
      result.setOperation(Result.Operation.ALERT.getValue());
      return result;
    }
    List<RepairPickingItemDTO> repairPickingItemDTOs = repairPickingDTO.getPendingItemDTOs();
    boolean hasWaitOutItem = false;
    boolean isLack = false;
    StringBuffer lackMsg = new StringBuffer();
    if(CollectionUtils.isNotEmpty(repairPickingItemDTOs)){
      for(RepairPickingItemDTO repairPickingItemDTO :repairPickingItemDTOs){
        if(OrderStatus.WAIT_OUT_STORAGE.equals(repairPickingItemDTO.getStatus())){
          hasWaitOutItem = true;
          if(repairPickingItemDTO.getIsLack()){
            isLack = true;
            lackMsg.append(repairPickingItemDTO.getProductName()).append(",");
          }
        }
      }
    }
    if (!hasWaitOutItem) {
      result.setSuccess(false);
      result.setMsg("当前领料单没有待出库产品，无法出库");
      result.setOperation(Result.Operation.ALERT.getValue());
      return result;
    }
    if (isLack) {
      result.setSuccess(false);
      if (lackMsg.length()>0){
        lackMsg.setLength(lackMsg.length() - 1);
        lackMsg.append("库存不足，无法出库，是否需要入库？");
      }else {
        lackMsg = new StringBuffer("当前领料单有商品库存不足，无法出库，是否需要入库？");
      }
      result.setMsg(lackMsg.toString());
      result.setOperation(Result.Operation.CONFIRM.getValue());
      result.setData("storage.do?method=getProducts&repairPickingId=" + repairPickingDTO.getId());
      return result;
    }
    return result;
  }

  @Override
  public Result verifyRepairPicking(RepairPickingDTO repairPickingDTO, RepairPickingDTO dbRepairPickingDTO) throws Exception{
    Result result = new Result();
    if (repairPickingDTO == null) {
      result.setSuccess(false);
      result.setMsg(ValidatorConstant.NO_REPAIR_PICKING_TO_OUT_STORAGE);
      result.setOperation(Result.Operation.ALERT.getValue());
      return result;
    }
    if (OrderStatus.REPEAL.equals(repairPickingDTO.getStatus())) {
      result.setSuccess(false);
      result.setMsg("当前单据对应的施工单已经作废，无法出库");
      result.setOperation(Result.Operation.ALERT.getValue());
      return result;
    }
    Map<Long, RepairPickingItemDTO> repairPickingItemDTOMap = new HashMap<Long, RepairPickingItemDTO>();
    boolean hasWaitOutItem = false;
    boolean hasReturnItem = false;
    boolean isLack = false;
    StringBuffer lackMsg = new StringBuffer();
    if (repairPickingDTO != null && CollectionUtils.isNotEmpty(repairPickingDTO.getPendingItemDTOs())) {
      for (RepairPickingItemDTO repairPickingItemDTO : repairPickingDTO.getPendingItemDTOs()) {
        if (repairPickingItemDTO.getId() == null) {
          continue;
        }
        repairPickingItemDTOMap.put(repairPickingItemDTO.getId(), repairPickingItemDTO);
      }
    }
    if (dbRepairPickingDTO != null && CollectionUtils.isNotEmpty(dbRepairPickingDTO.getTotalItemDTOs())) {
      for (RepairPickingItemDTO dbRepairPickingItemDTO : dbRepairPickingDTO.getTotalItemDTOs()) {
        RepairPickingItemDTO repairPickingItemDTO = repairPickingItemDTOMap.get(dbRepairPickingItemDTO.getId());
        if (repairPickingItemDTO == null) {
          continue;
        }
        if(OrderStatus.WAIT_OUT_STORAGE.equals(dbRepairPickingItemDTO.getStatus())){
          hasWaitOutItem = true;
        }else if(OrderStatus.WAIT_RETURN_STORAGE.equals(dbRepairPickingItemDTO.getStatus())){
          hasReturnItem = true;
        }else if(dbRepairPickingItemDTO.getStatus() != null){
          result.setSuccess(false);
          result.setMsg(dbRepairPickingItemDTO.getProductName()+dbRepairPickingItemDTO.getStatus().getName()+",请刷新页面，重新选择");
          result.setOperation(Result.Operation.ALERT.getValue());
          return result;
        }
        if (dbRepairPickingItemDTO.getIsLack()) {
          isLack = true;
          lackMsg.append(dbRepairPickingItemDTO.getProductName()).append(",");
        }
      }
      if (isLack) {
        result.setSuccess(false);
        if (lackMsg.length() > 0) {
          lackMsg.setLength(lackMsg.length() - 1);
          lackMsg.append("库存不足，无法出库，如需补料请点击【缺料】");
        } else {
          lackMsg = new StringBuffer("当前领料单有商品库存不足，如需补料请点击【缺料】");
        }
        result.setMsg(lackMsg.toString());
        result.setOperation(Result.Operation.ALERT.getValue());
        result.setData("storage.do?method=getProducts&repairPickingId=" + repairPickingDTO.getId());
        return result;
      }
      if(!(hasWaitOutItem || hasReturnItem)){
        result.setSuccess(false);
        result.setMsg("当前领料单无领/退料内容，无法领料或出库");
        result.setOperation(Result.Operation.ALERT.getValue());
        return result;
      }
    }
    if(BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(repairPickingDTO.getShopVersionId())){
       StoreHouseDTO storeHouseDTO = getStoreHouseService().getStoreHouseDTOById(repairPickingDTO.getShopId(),repairPickingDTO.getStorehouseId());
      if(storeHouseDTO==null || DeletedType.TRUE.equals(storeHouseDTO.getDeleted())){
        if(repairPickingDTO.getToStorehouseId() != null){
          storeHouseDTO = getStoreHouseService().getStoreHouseDTOById(repairPickingDTO.getShopId(),repairPickingDTO.getToStorehouseId());
          if(storeHouseDTO==null || DeletedType.TRUE.equals(storeHouseDTO.getDeleted())){
            return new Result("当前仓库不存，请选择退料调拨仓库",false,Result.Operation.TO_CHOOSE_STOREHOUSE.toString(),null);
          }
        }else {
          return new Result("当前仓库不存，请选择退料调拨仓库",false,Result.Operation.TO_CHOOSE_STOREHOUSE.toString(),null);
        }
      }
    }
    return result;
  }

  @Override
  public void handleRepairPicking(RepairPickingDTO repairPickingDTO)throws Exception{
    //Key是repairPickingItemId
    Map<Long ,RepairPickingItemDTO> toHandleRepairPickingItemDTOMap = new HashMap<Long, RepairPickingItemDTO>();
     //Key都是productLocalInfoId
    Set<Long> toHandleProductIds = new HashSet<Long>();
    Map<Long ,Inventory> inventoryMap = new HashMap<Long, Inventory>();
    Map<Long ,InventorySearchIndex> inventorySearchIndexMap = new HashMap<Long, InventorySearchIndex>();
    InventoryLimitDTO inventoryLimitDTO = new InventoryLimitDTO();
    Map<Long ,RepairOrderItem> repairOrderItemMap = new HashMap<Long, RepairOrderItem>();
    Map<Long ,ProductLocalInfoDTO> productLocalInfoDTOMap = new HashMap<Long, ProductLocalInfoDTO>();
    List<RepairPickingItemDTO> returnItems = new ArrayList<RepairPickingItemDTO>();
    Long operationTime = System.currentTimeMillis();  //操作时间

    //出库的item
    List<RepairPickingItemDTO> outStorageItemDTOs = new ArrayList<RepairPickingItemDTO>();
    //入库的item
    List<RepairPickingItemDTO> inStorageItemDTOs = new ArrayList<RepairPickingItemDTO>();


    if(repairPickingDTO != null && CollectionUtils.isNotEmpty(repairPickingDTO.getPendingItemDTOs())){
      for(RepairPickingItemDTO repairPickingItemDTO :repairPickingDTO.getPendingItemDTOs()){
        if(repairPickingItemDTO.getId()!=null ) {
          toHandleRepairPickingItemDTOMap.put(repairPickingItemDTO.getId(), repairPickingItemDTO);
        }
      }
    }
    TxnWriter writer = txnDaoManager.getWriter();
    List<RepairPickingItem> repairPickingItems = writer.getRepairPickingItemsByOrderIds(repairPickingDTO.getId());
    if (CollectionUtils.isNotEmpty(repairPickingItems)) {
      for (RepairPickingItem repairPickingItem : repairPickingItems) {
        RepairPickingItemDTO repairPickingItemDTO = toHandleRepairPickingItemDTOMap.get(repairPickingItem.getId());
        if (repairPickingItemDTO != null && repairPickingItem.getProductId() != null) {
          toHandleProductIds.add(repairPickingItem.getProductId());
          repairPickingItemDTO.setProductId(repairPickingItem.getProductId());
        }
      }
    }

    if (CollectionUtils.isNotEmpty(toHandleProductIds)) {
      inventoryMap = getInventoryService().getInventoryMap(repairPickingDTO.getShopId(), toHandleProductIds);
      productLocalInfoDTOMap = getProductService().getProductLocalInfoMap(repairPickingDTO.getShopId(), toHandleProductIds.toArray(new Long[toHandleProductIds.size()]));
      inventorySearchIndexMap = getSearchService().getInventorySearchIndexMapByProductIds(repairPickingDTO.getShopId(),
          toHandleProductIds.toArray(new Long[toHandleProductIds.size()]));
    }
    Object status = writer.begin();
    try{
      if(repairPickingDTO.getRepairOrderId() != null){
        List<RepairOrderItem> repairOrderItems = writer.getRepairOrderItemsByOrderId(repairPickingDTO.getRepairOrderId());
        if(CollectionUtils.isNotEmpty(repairOrderItems)){
          for(RepairOrderItem repairOrderItem : repairOrderItems){
            repairOrderItemMap.put(repairOrderItem.getProductId(),repairOrderItem);
          }
        }
      }
      if(CollectionUtils.isNotEmpty(repairPickingItems)){
        for(RepairPickingItem repairPickingItem : repairPickingItems){
          RepairPickingItemDTO repairPickingItemDTO = toHandleRepairPickingItemDTOMap.get(repairPickingItem.getId());
          if (repairPickingItemDTO == null) {
            continue;
          }
          repairPickingItem.setOperationDate(operationTime);
          repairPickingItem.setOperationMan(repairPickingItemDTO.getOperationMan());
          repairPickingItem.setOperationManId(repairPickingItemDTO.getOperationManId());
          repairPickingItem.setPickingMan(repairPickingItemDTO.getPickingMan());
          repairPickingItem.setPickingManId(repairPickingItemDTO.getPickingManId());
          ProductLocalInfoDTO productLocalInfoDTO = productLocalInfoDTOMap.get(repairPickingItem.getProductId());
          //出库逻辑
          if(OrderStatus.WAIT_OUT_STORAGE.equals(repairPickingItem.getStatus()) && repairPickingItem.getProductId() != null) {
            outStorageItemDTOs.add(repairPickingItem.toDTO());
            double toOutAmountWithSellUnit = NumberUtil.doubleVal(repairPickingItem.getAmount());   //出库的数量
            if(UnitUtil.isStorageUnit(repairPickingItem.getUnit(),productLocalInfoDTO)){
              toOutAmountWithSellUnit = toOutAmountWithSellUnit *  productLocalInfoDTO.getRate();
            }
            //1，inventory 中剪掉库存
            Inventory inventory = inventoryMap.get(repairPickingItem.getProductId());
            if(inventory!=null){
              getInventoryService().caculateBeforeLimit(inventory.toDTO(),inventoryLimitDTO);
              if(BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(repairPickingDTO.getShopVersionId())){
                StoreHouseInventoryDTO storeHouseInventoryDTO = new StoreHouseInventoryDTO(repairPickingDTO.getStorehouseId(),repairPickingItem.getProductId(),null);
                storeHouseInventoryDTO.setChangeAmount(0d - toOutAmountWithSellUnit);
                getStoreHouseService().saveOrUpdateStoreHouseInventoryDTO(writer,storeHouseInventoryDTO);
              }
              inventory.setAmount(inventory.getAmount() - toOutAmountWithSellUnit);
              writer.update(inventory);
              InventorySearchIndex inventorySearchIndex = inventorySearchIndexMap.get(repairPickingItem.getProductId());
              if(inventorySearchIndex!=null){
                inventorySearchIndex.setAmount(inventory.getAmount());
              }
              getInventoryService().caculateAfterLimit(inventory.toDTO(),inventoryLimitDTO);
              //2,repairPickingItem更新状态，领料人
              repairPickingItem.setStatus(OrderStatus.OUT_STORAGE);
              writer.update(repairPickingItem);
              RepairOrderItem repairOrderItem = repairOrderItemMap.get(repairPickingItem.getProductId());
              double toReturnAmountWithSellUnit = repairPickingItem.getAmount();  //出库之后可能还剩余的数量
              //3,更新 repairOrderItem，中的预留
              if (repairOrderItem != null) {
                double itemNeedReservedWithItemUnit = NumberUtil.doubleVal(repairOrderItem.getAmount()) - NumberUtil.doubleVal(repairOrderItem.getReserved());
                double itemNeedReservedWithSellUnit = NumberUtil.doubleVal(repairOrderItem.getAmount()) - NumberUtil.doubleVal(repairOrderItem.getReserved());
                double toOutAmountWithRepairItemUnit = toOutAmountWithSellUnit;   //要出库数量
                if(UnitUtil.isStorageUnit(repairOrderItem.getUnit(),productLocalInfoDTO)){
                  toOutAmountWithRepairItemUnit = toOutAmountWithRepairItemUnit / productLocalInfoDTO.getRate();
                  itemNeedReservedWithSellUnit = itemNeedReservedWithSellUnit * productLocalInfoDTO.getRate();
                }
                toReturnAmountWithSellUnit =  toOutAmountWithSellUnit - itemNeedReservedWithSellUnit;
                if(NumberUtil.isEqualOrGreater(toOutAmountWithRepairItemUnit,itemNeedReservedWithItemUnit)){    //出库数量大于等于施工单需要用量
                  repairOrderItem.setReserved(repairOrderItem.getAmount());
                }else {
                  repairOrderItem.setReserved(repairOrderItem.getReserved()+toOutAmountWithRepairItemUnit);
                }
                writer.update(repairOrderItem);
              }
              if (Math.abs(toReturnAmountWithSellUnit) > 0.0001) {     //出库量大于或者小于需要用量时
                RepairPickingItem toReturnItem = new RepairPickingItem(repairPickingDTO.getId(), repairOrderItem.getProductId()
                    , Math.abs(toReturnAmountWithSellUnit), inventory.getUnit(), null);
                toReturnItem.setStatus(toReturnAmountWithSellUnit > 0.0001 ? OrderStatus.WAIT_RETURN_STORAGE : OrderStatus.WAIT_OUT_STORAGE);
                writer.save(toReturnItem);
              }
            }
          } else if (OrderStatus.WAIT_RETURN_STORAGE.equals(repairPickingItem.getStatus()) && repairPickingItem.getProductId() != null) {
            inStorageItemDTOs.add(repairPickingItem.toDTO());
            //退料逻辑
            //1,更新item状态
            repairPickingItem.setStatus(OrderStatus.RETURN_STORAGE);
            writer.update(repairPickingItem);
            Inventory inventory = inventoryMap.get(repairPickingItem.getProductId());
            RepairPickingItemDTO returnItem = repairPickingItem.toDTO();
            double toReturnAmountWithSellUnit = NumberUtil.doubleVal(repairPickingItem.getAmount());
            double costPriceWithItemUnit = inventory == null ? 0d: NumberUtil.doubleVal(inventory.getInventoryAveragePrice());
            if(UnitUtil.isStorageUnit(repairPickingItem.getUnit(),productLocalInfoDTO)){
              toReturnAmountWithSellUnit = toReturnAmountWithSellUnit * productLocalInfoDTO.getRate();
              costPriceWithItemUnit = costPriceWithItemUnit * productLocalInfoDTO.getRate();
            }
            returnItem.setCostPrice(costPriceWithItemUnit);
            //2,更新库存信息
            if (inventory != null) {
              getInventoryService().caculateBeforeLimit(inventory.toDTO(), inventoryLimitDTO);
              if (BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(repairPickingDTO.getShopVersionId())) {
                StoreHouseInventoryDTO storeHouseInventoryDTO = new StoreHouseInventoryDTO(repairPickingDTO.getStorehouseId(), repairPickingItem.getProductId(), null);
                storeHouseInventoryDTO.setChangeAmount(0d + toReturnAmountWithSellUnit);
                getStoreHouseService().saveOrUpdateStoreHouseInventoryDTO(writer, storeHouseInventoryDTO);
              }
              inventory.setAmount(inventory.getAmount() + toReturnAmountWithSellUnit);
              writer.update(inventory);

              getInventoryService().caculateAfterLimit(inventory.toDTO(), inventoryLimitDTO);
              InventorySearchIndex inventorySearchIndex = inventorySearchIndexMap.get(repairPickingItem.getProductId());
              if (inventorySearchIndex != null) {
                inventorySearchIndex.setAmount(inventory.getAmount());
              }
            }
            returnItems.add(returnItem);
          }
        }
      }
      RepairPickingDTO cloneRepairPickingDTO =  repairPickingDTO.clone();
      //保存入库记录
      if (CollectionUtils.isNotEmpty(inStorageItemDTOs)) {
        cloneRepairPickingDTO.setItemDTOs(inStorageItemDTOs.toArray(new RepairPickingItemDTO[inStorageItemDTOs.size()]));
        ServiceManager.getService(IProductInStorageService.class).productThroughByOrder(cloneRepairPickingDTO, OrderTypes.REPAIR_PICKING, repairPickingDTO.getStatus(), writer);

      }
      //保存出库记录
      if (CollectionUtils.isNotEmpty(outStorageItemDTOs)) {
        cloneRepairPickingDTO.setItemDTOs(outStorageItemDTOs.toArray(new RepairPickingItemDTO[inStorageItemDTOs.size()]));
        ServiceManager.getService(IProductOutStorageService.class).productThroughByOrder(cloneRepairPickingDTO, OrderTypes.REPAIR_PICKING, repairPickingDTO.getStatus(), writer,null);
      }



      writer.commit(status);
      //下面操作有需要用到上面commit的数据

            //自动生成调拨单
      if (BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(repairPickingDTO.getShopVersionId()) && repairPickingDTO.getToStorehouseId() != null) {
        status = writer.begin();
        AllocateRecordDTO allocateRecordDTO = createAllocateRecordByRepairPickingDTO(writer, repairPickingDTO, returnItems);
        writer.commit(status);
        if (allocateRecordDTO != null) {
          BcgogoEventPublisher bcgogoEventPublisher = new BcgogoEventPublisher();
          BcgogoOrderReindexEvent bcgogoOrderReindexEvent = new BcgogoOrderReindexEvent(allocateRecordDTO, OrderTypes.ALLOCATE_RECORD);
          bcgogoEventPublisher.bcgogoOrderReindex(bcgogoOrderReindexEvent);
        }
      }

      updateRepairPickingStatus(repairPickingDTO);
      if (repairPickingDTO.getRepairOrderId() != null && repairPickingDTO.getShopId() != null) {
        RepairOrderDTO repairOrderDTO = getRfTxnService().getRepairOrderDTODetailById(repairPickingDTO.getRepairOrderId(), repairPickingDTO.getShopId());
        repairOrderDTO.setShopVersionId(repairPickingDTO.getShopVersionId());
        getRepairService().getProductInfo(repairOrderDTO);
        if (repairOrderDTO != null) {
          getRepairService().saveOrUpdateRepairRemindEventWithRepairPicking(repairOrderDTO);
        }
      }

      getInventoryService().updateMemocacheLimitByInventoryLimitDTO(repairPickingDTO.getShopId(), inventoryLimitDTO);

   			if (inventorySearchIndexMap!=null && MapUtils.isNotEmpty(inventorySearchIndexMap)) {
           getInventoryService().addOrUpdateInventorySearchIndexWithList(repairPickingDTO.getShopId(), new ArrayList<InventorySearchIndex>(inventorySearchIndexMap.values()));
   			}
    } finally {
      writer.rollback(status);
    }
  }

  private AllocateRecordDTO createAllocateRecordByRepairPickingDTO(TxnWriter writer,RepairPickingDTO repairPickingDTO,
                                                      List<RepairPickingItemDTO> repairPickingItemDTOs) throws Exception {
    if(repairPickingDTO == null || repairPickingDTO.getStorehouseId() == null
        || repairPickingDTO.getToStorehouseId() == null || CollectionUtils.isEmpty(repairPickingItemDTOs)){
      return null;
    }
    if(!BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(repairPickingDTO.getShopVersionId())){
       return null;
    }
    StoreHouseDTO storeHouseDTO = getStoreHouseService().getStoreHouseDTOById(repairPickingDTO.getShopId(),repairPickingDTO.getStorehouseId());
    if(storeHouseDTO != null && DeletedType.FALSE.equals(storeHouseDTO.getDeleted())){
      return null;
    }
    AllocateRecordDTO allocateRecordDTO = new AllocateRecordDTO();
    allocateRecordDTO.fromRepairPickingDTO(repairPickingDTO,repairPickingItemDTOs);
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    allocateRecordDTO.setReceiptNo(txnService.getReceiptNo(allocateRecordDTO.getShopId(), OrderTypes.ALLOCATE_RECORD, null));
    IAllocateRecordService allocateRecordService = ServiceManager.getService(IAllocateRecordService.class);
    allocateRecordService.saveOrUpdateAllocateRecord(writer, repairPickingDTO.getShopId(), allocateRecordDTO);
    return allocateRecordDTO;
  }
  @Override
  public void updateRepairPickingStatus(RepairPickingDTO repairPickingDTO) throws Exception {
    if (repairPickingDTO.getShopId() == null || repairPickingDTO.getId() == null || repairPickingDTO.getRepairOrderId() == null) {
      return;
    }
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      RepairPicking repairPicking = writer.getRepairPicksById(repairPickingDTO.getShopId(), repairPickingDTO.getId());
      if (repairPicking == null) {
        return;
      }
      List<RepairPickingItem> repairPickingItems = writer.getRepairPickingItemsByOrderIds(repairPickingDTO.getId());
      if(repairPicking!=null && CollectionUtils.isNotEmpty(repairPickingItems)){
        writer.delete(repairPicking);
        return;
      }

      boolean hasWaitReturn = false;
      boolean hasWaitOut = false;
      if (CollectionUtils.isNotEmpty(repairPickingItems)) {
        for (RepairPickingItem repairPickingItem : repairPickingItems) {
          if (OrderStatus.WAIT_RETURN_STORAGE.equals(repairPickingItem.getStatus())) {
            hasWaitReturn = true;
            break;
          }
        }
        for (RepairPickingItem repairPickingItem : repairPickingItems) {
          if (OrderStatus.WAIT_OUT_STORAGE.equals(repairPickingItem.getStatus())) {
            hasWaitOut = true;
            break;
          }
        }
      }
      if (hasWaitReturn || hasWaitOut) {
        repairPicking.setStatus(OrderStatus.PENDING);
        writer.update(repairPicking);
        repairPickingDTO.setStatus(repairPicking.getStatus());
      }else {
        repairPicking.setStatus(OrderStatus.SETTLED);
        writer.update(repairPicking);
        repairPickingDTO.setStatus(repairPicking.getStatus());
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public Map<Long, RepairPickingItem> getPendingRepairPickingItemMap(Long id) {
    Map<Long, RepairPickingItem> repairPickingItemMap = new HashMap<Long, RepairPickingItem>();
    if(id == null){
     return repairPickingItemMap;
    }
    List<RepairPickingItem> repairPickingItems = txnDaoManager.getWriter().getRepairPickingItemsByOrderIds(id);
    if(CollectionUtils.isNotEmpty(repairPickingItems)){
      for(RepairPickingItem repairPickingItem :repairPickingItems){
        if(OrderStatus.WAIT_OUT_STORAGE.equals(repairPickingItem.getStatus())
            || OrderStatus.WAIT_RETURN_STORAGE.equals(repairPickingItem.getStatus())){
          repairPickingItemMap.put(repairPickingItem.getProductId(),repairPickingItem);
        }
      }
    }
    return repairPickingItemMap;
  }

  @Override
  public void createInnerPickingDTO(InnerPickingDTO innerPickingDTO, Set<Long> productIds) throws Exception {
    if (innerPickingDTO == null || innerPickingDTO.getShopId() == null) {
      return;
    }
    List<InnerPickingItemDTO> innerPickingItemDTOs = new ArrayList<InnerPickingItemDTO>();
    if (CollectionUtils.isNotEmpty(productIds)) {
      Map<Long, ProductDTO> productDTOMap = getProductService().getProductDTOMapByProductLocalInfoIds(innerPickingDTO.getShopId(), productIds);
      Map<Long, InventoryDTO> inventoryDTOMap = getInventoryService().getInventoryDTOMap(innerPickingDTO.getShopId(), productIds);
      Map<Long, StoreHouseInventoryDTO> storeHouseInventoryDTOMap = new HashMap<Long, StoreHouseInventoryDTO>();
      if (innerPickingDTO.getIsHaveStoreHouse() && innerPickingDTO.getStorehouseId() != null) {
        storeHouseInventoryDTOMap = getStoreHouseService().getStoreHouseInventoryDTOMapByStorehouseAndProductIds(innerPickingDTO.getShopId()
            , innerPickingDTO.getStorehouseId(), productIds.toArray(new Long[productIds.size()]));
      }
      double total = 0d;
      for (Long productId : productIds) {
        ProductDTO productDTO = productDTOMap.get(productId);
        InventoryDTO inventoryDTO = inventoryDTOMap.get(productId);
        StoreHouseInventoryDTO storeHouseInventoryDTO = storeHouseInventoryDTOMap.get(productId);
        if (productDTO != null && inventoryDTO != null) {
          InnerPickingItemDTO innerPickingItemDTO = new InnerPickingItemDTO();
          innerPickingItemDTO.setProductDTO(productDTO);
          innerPickingItemDTO.setAmount(1D);
          innerPickingItemDTO.setPrice(NumberUtil.doubleVal(inventoryDTO.getInventoryAveragePrice()));
          double itemTotal = innerPickingItemDTO.getAmount() * innerPickingItemDTO.getPrice();
          total += itemTotal;
          innerPickingItemDTO.setTotal(itemTotal);
          innerPickingItemDTO.setUnit(inventoryDTO.getUnit());
          if (storeHouseInventoryDTO != null) {
            innerPickingItemDTO.setInventoryAmount(storeHouseInventoryDTO.getAmount());
          }else {
            innerPickingItemDTO.setInventoryAmount(inventoryDTO.getAmount());
          }
          innerPickingItemDTOs.add(innerPickingItemDTO);
        }
      }
      if(CollectionUtils.isNotEmpty(innerPickingItemDTOs)){
        innerPickingDTO.setItemDTOs(innerPickingItemDTOs.toArray(new InnerPickingItemDTO[innerPickingItemDTOs.size()]));
      }
      innerPickingDTO.setTotal(total);
    }
  }

  @Override
  public void createInnerReturnDTO(InnerReturnDTO innerReturnDTO, Set<Long> productIds) throws Exception {
    if (innerReturnDTO == null || innerReturnDTO.getShopId() == null) {
      return;
    }
    List<InnerReturnItemDTO> innerReturnItemDTOs = new ArrayList<InnerReturnItemDTO>();
    if (CollectionUtils.isNotEmpty(productIds)) {
      Map<Long, ProductDTO> productDTOMap = getProductService().getProductDTOMapByProductLocalInfoIds(innerReturnDTO.getShopId(), productIds);
      Map<Long, InventoryDTO> inventoryDTOMap = getInventoryService().getInventoryDTOMap(innerReturnDTO.getShopId(), productIds);
      Map<Long, StoreHouseInventoryDTO> storeHouseInventoryDTOMap = new HashMap<Long, StoreHouseInventoryDTO>();
      if (innerReturnDTO.getIsHaveStoreHouse() && innerReturnDTO.getStorehouseId() != null) {
        storeHouseInventoryDTOMap = getStoreHouseService().getStoreHouseInventoryDTOMapByStorehouseAndProductIds(innerReturnDTO.getShopId()
            , innerReturnDTO.getStorehouseId(), productIds.toArray(new Long[productIds.size()]));
      }
      double total = 0d;
      for (Long productId : productIds) {
        ProductDTO productDTO = productDTOMap.get(productId);
        InventoryDTO inventoryDTO = inventoryDTOMap.get(productId);
        StoreHouseInventoryDTO storeHouseInventoryDTO = storeHouseInventoryDTOMap.get(productId);
        if (productDTO != null && inventoryDTO != null) {
          InnerReturnItemDTO innerReturnItemDTO = new InnerReturnItemDTO();
          innerReturnItemDTO.setProductDTO(productDTO);
          innerReturnItemDTO.setAmount(1D);
          innerReturnItemDTO.setPrice(NumberUtil.doubleVal(inventoryDTO.getInventoryAveragePrice()));
          double itemTotal = innerReturnItemDTO.getAmount() * innerReturnItemDTO.getPrice();
          total += itemTotal;
          innerReturnItemDTO.setTotal(itemTotal);
          innerReturnItemDTO.setUnit(inventoryDTO.getUnit());
          if (storeHouseInventoryDTO != null) {
            innerReturnItemDTO.setInventoryAmount(storeHouseInventoryDTO.getAmount());
          }else {
            innerReturnItemDTO.setInventoryAmount(inventoryDTO.getAmount());
          }
          innerReturnItemDTOs.add(innerReturnItemDTO);
        }
      }
      if(CollectionUtils.isNotEmpty(innerReturnItemDTOs)){
        innerReturnDTO.setItemDTOs(innerReturnItemDTOs.toArray(new InnerReturnItemDTO[innerReturnItemDTOs.size()]));
      }
      innerReturnDTO.setTotal(total);
    }
  }

  @Override
  public void saveInnerPicking(InnerPickingDTO innerPickingDTO) throws Exception {
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    txnService.updateProductUnit(innerPickingDTO);

    if (BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(innerPickingDTO.getShopVersionId())) {
      StoreHouseDTO storeHouseDTO = getStoreHouseService().getStoreHouseDTOById(innerPickingDTO.getShopId(), innerPickingDTO.getStorehouseId());
      innerPickingDTO.setStorehouseName(storeHouseDTO == null ? null : storeHouseDTO.getName());
    }

    Map<Long, InventorySearchIndex> inventorySearchIndexMap = new HashMap<Long, InventorySearchIndex>();
    Map<Long,ProductLocalInfoDTO> productLocalInfoDTOMap = null;
    InventoryLimitDTO inventoryLimitDTO = new InventoryLimitDTO();
    Set<Long> productIds = new HashSet<Long>();
    for (InnerPickingItemDTO itemDTO : innerPickingDTO.getItemDTOs()) {
      if (itemDTO.getProductId() != null) {
        productIds.add(itemDTO.getProductId());
      }
    }
    if (!productIds.isEmpty()) {
      inventorySearchIndexMap = getSearchService().getInventorySearchIndexMapByProductIds(innerPickingDTO.getShopId(), productIds.toArray(new Long[productIds.size()]));
       productLocalInfoDTOMap = getProductService().getProductLocalInfoMap(innerPickingDTO.getShopId(), productIds.toArray(new Long[productIds.size()]));
    }
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      Map<Long, ProductHistoryDTO> productHistoryDTOMap = getProductHistoryService().getOrSaveProductHistoryByLocalInfoId(innerPickingDTO.getShopId(), productIds.toArray(new Long[productIds.size()]));
      InnerPicking innerPicking = new InnerPicking();
      innerPickingDTO.setId(null);
      innerPicking.fromDTO(innerPickingDTO);
      writer.save(innerPicking);
      innerPickingDTO.setId(innerPicking.getId());
      //保存item
      ProductHistoryDTO productHistoryDTO = null;
      for (InnerPickingItemDTO itemDTO : innerPickingDTO.getItemDTOs()) {
        if(itemDTO.getProductId() == null || StringUtils.isBlank(itemDTO.getProductName())){
          continue;
        }
        itemDTO.setId(null);
        if (itemDTO.getProductHistoryId() == null) {
          productHistoryDTO = productHistoryDTOMap.get(itemDTO.getProductId());
          itemDTO.setProductHistoryId(productHistoryDTO == null ? null : productHistoryDTO.getId());
        }
        itemDTO.setInnerPickingId(innerPickingDTO.getId());
        InnerPickingItem innerPickingItem = new InnerPickingItem();
        innerPickingItem.fromDTO(itemDTO);
        writer.save(innerPickingItem);
        itemDTO.setId(innerPickingItem.getId());
        //处理库存信息
        if (BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(innerPickingDTO.getShopVersionId())) {
          StoreHouseInventoryDTO storeHouseInventoryDTO = new StoreHouseInventoryDTO(innerPickingDTO.getStorehouseId(), itemDTO.getProductId(), null);
          storeHouseInventoryDTO.setChangeAmount(NumberUtil.doubleVal(itemDTO.getAmount()) * -1);
          getStoreHouseService().saveOrUpdateStoreHouseInventoryDTO(writer, storeHouseInventoryDTO);
        }

        Inventory inventory = writer.getInventoryByIdAndshopId(itemDTO.getProductId(),innerPickingDTO.getShopId());
        ProductLocalInfoDTO productLocalInfoDTO = productLocalInfoDTOMap.get(itemDTO.getProductId());
        if (inventory != null) {
          getInventoryService().caculateBeforeLimit(inventory.toDTO(), inventoryLimitDTO);

          double amount = NumberUtil.doubleVal(itemDTO.getAmount());
          if (UnitUtil.isStorageUnit(itemDTO.getUnit(), productLocalInfoDTO)) {
            amount = amount * productLocalInfoDTO.getRate();
          }
          inventory.setAmount(inventory.getAmount() - amount);

          writer.update(inventory);
          getInventoryService().caculateAfterLimit(inventory.toDTO(), inventoryLimitDTO);
          InventorySearchIndex inventorySearchIndex = inventorySearchIndexMap.get(itemDTO.getProductId());
          if (inventorySearchIndex != null) {
            inventorySearchIndex.setAmount(inventory.getAmount());
          }
        }
      }

      getProductOutStorageService().productThroughByOrder(innerPickingDTO,OrderTypes.INNER_PICKING,innerPickingDTO.getStatus(),writer,null);

      writer.commit(status);
       //操作记录
      getOperationLogService().saveOperationLog(new OperationLogDTO(innerPickingDTO.getShopId(), innerPickingDTO.getUserId(),
          innerPickingDTO.getId(), ObjectTypes.INNER_PICKING, OperationTypes.OUT_STORAGE));
      //更新库存上下限
      getInventoryService().updateInventoryLimit(inventoryLimitDTO);
      //更新searchIndex
      if (inventorySearchIndexMap != null && !inventorySearchIndexMap.isEmpty()) {
        getInventoryService().addOrUpdateInventorySearchIndexWithList(innerPickingDTO.getShopId(),
            new ArrayList<InventorySearchIndex>(inventorySearchIndexMap.values()));
      }
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public void saveInnerReturn(InnerReturnDTO innerReturnDTO) throws Exception {

    ITxnService txnService =ServiceManager.getService(ITxnService.class);
    txnService.updateProductUnit(innerReturnDTO);

    if (BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(innerReturnDTO.getShopVersionId())) {
      StoreHouseDTO storeHouseDTO = getStoreHouseService().getStoreHouseDTOById(innerReturnDTO.getShopId(), innerReturnDTO.getStorehouseId());
      innerReturnDTO.setStorehouseName(storeHouseDTO == null ? null : storeHouseDTO.getName());
    }

    Map<Long, InventorySearchIndex> inventorySearchIndexMap = new HashMap<Long, InventorySearchIndex>();
    Map<Long,ProductLocalInfoDTO> productLocalInfoDTOMap = null;
    InventoryLimitDTO inventoryLimitDTO = new InventoryLimitDTO();
    Set<Long> productIds = new HashSet<Long>();
    for (InnerReturnItemDTO itemDTO : innerReturnDTO.getItemDTOs()) {
      if (itemDTO.getProductId() != null) {
        productIds.add(itemDTO.getProductId());
      }
    }
    if (!productIds.isEmpty()) {
      inventorySearchIndexMap = getSearchService().getInventorySearchIndexMapByProductIds(innerReturnDTO.getShopId(), productIds.toArray(new Long[productIds.size()]));
      productLocalInfoDTOMap = getProductService().getProductLocalInfoMap(innerReturnDTO.getShopId(), productIds.toArray(new Long[productIds.size()]));
    }
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      Map<Long, ProductHistoryDTO> productHistoryDTOMap = getProductHistoryService().getOrSaveProductHistoryByLocalInfoId(innerReturnDTO.getShopId(), productIds.toArray(new Long[productIds.size()]));
      InnerReturn innerReturn = new InnerReturn();
      innerReturnDTO.setId(null);
      innerReturn.fromDTO(innerReturnDTO);
      writer.save(innerReturn);
      innerReturnDTO.setId(innerReturn.getId());
      //保存item
      ProductHistoryDTO productHistoryDTO = null;
      for (InnerReturnItemDTO itemDTO : innerReturnDTO.getItemDTOs()) {
        if(itemDTO.getProductId() == null || StringUtils.isBlank(itemDTO.getProductName())){
          continue;
        }
        itemDTO.setId(null);
        if (itemDTO.getProductHistoryId() == null) {
          productHistoryDTO = productHistoryDTOMap.get(itemDTO.getProductId());
          itemDTO.setProductHistoryId(productHistoryDTO == null ? null : productHistoryDTO.getId());
        }
        itemDTO.setInnerReturnId(innerReturnDTO.getId());
        InnerReturnItem innerReturnItem = new InnerReturnItem();
        innerReturnItem.fromDTO(itemDTO);
        writer.save(innerReturnItem);
        itemDTO.setId(innerReturnItem.getId());
        //处理库存信息
        if (BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(innerReturnDTO.getShopVersionId())) {
          StoreHouseInventoryDTO storeHouseInventoryDTO = new StoreHouseInventoryDTO(innerReturnDTO.getStorehouseId(), itemDTO.getProductId(), null);
          storeHouseInventoryDTO.setChangeAmount(NumberUtil.doubleVal(itemDTO.getAmount()));
          getStoreHouseService().saveOrUpdateStoreHouseInventoryDTO(writer, storeHouseInventoryDTO);
        }

        Inventory inventory = writer.getInventoryByIdAndshopId(itemDTO.getProductId(),innerReturnDTO.getShopId());
        ProductLocalInfoDTO productLocalInfoDTO = productLocalInfoDTOMap.get(itemDTO.getProductId());
        if (inventory != null) {
          getInventoryService().caculateBeforeLimit(inventory.toDTO(), inventoryLimitDTO);
          double amount = NumberUtil.doubleVal(itemDTO.getAmount());
          if (UnitUtil.isStorageUnit(itemDTO.getUnit(), productLocalInfoDTO)) {
            amount = amount * productLocalInfoDTO.getRate();
          }
          inventory.setAmount(inventory.getAmount() + amount);
          writer.update(inventory);
          getInventoryService().caculateAfterLimit(inventory.toDTO(), inventoryLimitDTO);
          InventorySearchIndex inventorySearchIndex = inventorySearchIndexMap.get(itemDTO.getProductId());
          if (inventorySearchIndex != null) {
            inventorySearchIndex.setAmount(inventory.getAmount());
          }
        }
      }

      ServiceManager.getService(IProductInStorageService.class).productThroughByOrder(innerReturnDTO,OrderTypes.INNER_RETURN,innerReturnDTO.getStatus(),writer);

      writer.commit(status);
       //操作记录
      getOperationLogService().saveOperationLog(new OperationLogDTO(innerReturnDTO.getShopId(), innerReturnDTO.getUserId(),
          innerReturnDTO.getId(), ObjectTypes.INNER_RETURN, OperationTypes.RETURN_STORAGE));
      //更新库存上下限
      getInventoryService().updateInventoryLimit(inventoryLimitDTO);
      //更新searchIndex
      if (inventorySearchIndexMap != null && !inventorySearchIndexMap.isEmpty()) {
        getInventoryService().addOrUpdateInventorySearchIndexWithList(innerReturnDTO.getShopId(),
            new ArrayList<InventorySearchIndex>(inventorySearchIndexMap.values()));
      }
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public Result verifySaveInnerPicking(InnerPickingDTO innerPickingDTO) throws Exception {
    Result result = new Result();
    Long shopId = innerPickingDTO.getShopId();
    //去掉空行
    List<Long> productIdList = removeNullProductRow(innerPickingDTO);
    if (ArrayUtil.isEmpty(innerPickingDTO.getItemDTOs())) {
      return new Result(ValidatorConstant.ORDER_NULL_MSG, false);
    } else if (!ArrayUtil.isEmpty(innerPickingDTO.getItemDTOs()) && innerPickingDTO.getItemDTOs().length > productIdList.size()) {
      return new Result(ValidatorConstant.ORDER_NEW_PRODUCT_ERROR, false);
    } else {
      //校验产品库存
      if (!BcgogoShopLogicResourceUtils.isIgnoreVerifierInventoryResource(innerPickingDTO.getShopVersionId())) {
        Map<String, String> data = new HashMap<String, String>();
        if (BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(innerPickingDTO.getShopVersionId())) {
          //通过仓库校验库存
          if (innerPickingDTO.getStorehouseId() != null) {
            if (!getInventoryService().checkBatchProductInventoryByStoreHouse(shopId, innerPickingDTO.getStorehouseId(), innerPickingDTO.getItemDTOs(), data, productIdList)) {
              return new Result(ValidatorConstant.PRODUCT_INVENTORY_LACK, false, Result.Operation.UPDATE_PRODUCT_INVENTORY.getValue(), data);
            }
          } else {
            return new Result(ValidatorConstant.STOREHOUSE_NULL_MSG, false);
          }
        } else {
          if (!getInventoryService().checkBatchProductInventory(shopId, innerPickingDTO, data, productIdList)) {
            return new Result(ValidatorConstant.PRODUCT_INVENTORY_LACK, false, Result.Operation.UPDATE_PRODUCT_INVENTORY.getValue(), data);
          }
        }
      }
    }
    return result;
  }

@Override
  public Result verifySaveInnerReturn(InnerReturnDTO innerReturnDTO) throws Exception {
  Result result = new Result();
  Long shopId = innerReturnDTO.getShopId();
  //校验仓库是否存在
  if (BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(innerReturnDTO.getShopVersionId())) {
    //通过仓库校验库存
    if (innerReturnDTO.getStorehouseId() != null) {
      StoreHouseDTO storeHouseDTO = getStoreHouseService().getStoreHouseDTOById(shopId, innerReturnDTO.getStorehouseId());
      if (storeHouseDTO == null) {
        return new Result(ValidatorConstant.STOREHOUSE_NULL_MSG, false);
      } else {
        if (DeletedType.TRUE.equals(storeHouseDTO.getDeleted())) {
          return new Result(ValidatorConstant.STOREHOUSE_DELETED_MSG, false);
        }
      }
    } else {
      return new Result(ValidatorConstant.STOREHOUSE_NULL_MSG, false);
    }
  }
  return result;
}

  @Override
  public InnerPickingDTO getInnerPickingById(Long shopId, Long innerPickingId) throws Exception {
    if (shopId == null || innerPickingId == null) {
      return null;
    }
    TxnWriter writer = txnDaoManager.getWriter();
    InnerPicking innerPicking = writer.getInnerPickingById(shopId, innerPickingId);
    if (innerPicking == null) {
      return null;
    }
    InnerPickingDTO innerPickingDTO = innerPicking.toDTO();
    if (innerPicking.getStorehouseId() != null) {
      innerPickingDTO.setIsHaveStoreHouse(true);
    }
    List<InnerPickingItem> innerPickingItems = writer.getInnerPickingItemsByInnerPickingId(innerPickingId);
    Set<Long> productIds = new HashSet<Long>();
    Set<Long> productHistoryIds = new HashSet<Long>();
    Map<Long, ProductHistoryDTO> productHistoryDTOMap = new HashMap<Long, ProductHistoryDTO>();
    Map<Long, ProductDTO> productDTOMap = new HashMap<Long, ProductDTO>();
    Map<Long, StoreHouseInventoryDTO> storeHouseInventoryDTOMap = new HashMap<Long, StoreHouseInventoryDTO>();
    Map<Long, InventoryDTO> inventoryDTOMap = new HashMap<Long, InventoryDTO>();

    if (CollectionUtils.isNotEmpty(innerPickingItems)) {
      for (InnerPickingItem innerPickingItem : innerPickingItems) {
        if (innerPickingItem.getProductHistoryId() != null) {
          productHistoryIds.add(innerPickingItem.getProductHistoryId());
        }
        if (innerPickingItem.getProductId() != null) {
          productIds.add(innerPickingItem.getProductId());
        }
      }
    }

    if (!productHistoryIds.isEmpty()) {
      productHistoryDTOMap = getProductHistoryService().getProductHistoryDTOMapByProductHistoryIds(productHistoryIds);
    }
    if (!productIds.isEmpty()) {
      productDTOMap = getProductService().getProductDTOMapByProductLocalInfoIds(shopId, productIds);
      if (innerPickingDTO.getStorehouseId() != null) {
        storeHouseInventoryDTOMap = getStoreHouseService().getStoreHouseInventoryDTOMapByStorehouseAndProductIds(shopId,
            innerPickingDTO.getStorehouseId(), productIds.toArray(new Long[productIds.size()]));
      }
      inventoryDTOMap = getInventoryService().getInventoryDTOMap(shopId, productIds);
    }


    List<InnerPickingItemDTO> innerPickingItemDTOs = new ArrayList<InnerPickingItemDTO>();
    double totalAmount = 0d,totalInventoryAmount = 0d;
    if (CollectionUtils.isNotEmpty(innerPickingItems)) {
      for (InnerPickingItem innerPickingItem : innerPickingItems) {
        InnerPickingItemDTO innerPickingItemDTO = innerPickingItem.toDTO();

        ProductHistoryDTO productHistoryDTO = null;
        if (innerPickingItem.getProductHistoryId() != null) {
          productHistoryDTO = productHistoryDTOMap.get(innerPickingItem.getProductHistoryId());
        }
        if (productHistoryDTO != null) {
          innerPickingItemDTO.setProductHistoryDTO(productHistoryDTO);
        } else {
          ProductDTO productDTO = productDTOMap.get(innerPickingItem.getProductId());
          if (productDTO != null) {
            innerPickingItemDTO.setProductDTO(productDTO);
          }
        }
        totalAmount += NumberUtil.doubleVal(innerPickingItem.getAmount());
        if (innerPickingDTO.getStorehouseId() != null) {
          StoreHouseInventoryDTO storeHouseInventoryDTO = storeHouseInventoryDTOMap.get(innerPickingItem.getProductId());
          if (storeHouseInventoryDTO != null) {
            innerPickingItemDTO.setInventoryAmount(NumberUtil.round(storeHouseInventoryDTO.getAmount(),2));
            totalInventoryAmount += NumberUtil.doubleVal(storeHouseInventoryDTO.getAmount());
          }
        } else {
          InventoryDTO inventoryDTO = inventoryDTOMap.get(innerPickingItem.getProductId());
          if (inventoryDTO != null) {
            innerPickingItemDTO.setInventoryAmount(NumberUtil.round(inventoryDTO.getAmount(),2));
            totalInventoryAmount += NumberUtil.doubleVal(inventoryDTO.getAmount());
          }
        }
        innerPickingItemDTOs.add(innerPickingItemDTO);
      }
    }
    innerPickingDTO.setTotalAmount(totalAmount);
    innerPickingDTO.setTotalInventoryAmount(NumberUtil.round(totalInventoryAmount,2));
    if (CollectionUtils.isNotEmpty(innerPickingItemDTOs)) {
      innerPickingDTO.setItemDTOs(innerPickingItemDTOs.toArray(new InnerPickingItemDTO[innerPickingItemDTOs.size()]));
    }
    innerPickingDTO.setVestDateStr(DateUtil.dateLongToStr(innerPickingDTO.getVestDate(),DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN));
    return innerPickingDTO;
  }

  @Override
  public InnerReturnDTO getInnerReturnById(Long shopId, Long innerReturnId) throws Exception {
    if (shopId == null || innerReturnId == null) {
      return null;
    }
    TxnWriter writer = txnDaoManager.getWriter();
    InnerReturn innerReturn = writer.getInnerReturnById(shopId, innerReturnId);
    if (innerReturn == null) {
      return null;
    }
    InnerReturnDTO innerReturnDTO = innerReturn.toDTO();
    if (innerReturn.getStorehouseId() != null) {
      innerReturnDTO.setIsHaveStoreHouse(true);
    }
    List<InnerReturnItem> innerReturnItems = writer.getInnerReturnItemsByInnerReturnId(innerReturnId);
    Set<Long> productIds = new HashSet<Long>();
    Set<Long> productHistoryIds = new HashSet<Long>();
    Map<Long, ProductHistoryDTO> productHistoryDTOMap = new HashMap<Long, ProductHistoryDTO>();
    Map<Long, ProductDTO> productDTOMap = new HashMap<Long, ProductDTO>();
    Map<Long, StoreHouseInventoryDTO> storeHouseInventoryDTOMap = new HashMap<Long, StoreHouseInventoryDTO>();
    Map<Long, InventoryDTO> inventoryDTOMap = new HashMap<Long, InventoryDTO>();

    if (CollectionUtils.isNotEmpty(innerReturnItems)) {
      for (InnerReturnItem innerReturnItem : innerReturnItems) {
        if (innerReturnItem.getProductHistoryId() != null) {
          productHistoryIds.add(innerReturnItem.getProductHistoryId());
        }
        if (innerReturnItem.getProductId() != null) {
          productIds.add(innerReturnItem.getProductId());
        }
      }
    }

    if (!productHistoryIds.isEmpty()) {
      productHistoryDTOMap = getProductHistoryService().getProductHistoryDTOMapByProductHistoryIds(productHistoryIds);
    }
    if (!productIds.isEmpty()) {
      productDTOMap = getProductService().getProductDTOMapByProductLocalInfoIds(shopId, productIds);
      if (innerReturnDTO.getStorehouseId() != null) {
        storeHouseInventoryDTOMap = getStoreHouseService().getStoreHouseInventoryDTOMapByStorehouseAndProductIds(shopId,
            innerReturnDTO.getStorehouseId(), productIds.toArray(new Long[productIds.size()]));
      }
      inventoryDTOMap = getInventoryService().getInventoryDTOMap(shopId, productIds);
    }


    List<InnerReturnItemDTO> innerReturnItemDTOs = new ArrayList<InnerReturnItemDTO>();
    double totalAmount = 0d,totalInventoryAmount = 0d;
    if (CollectionUtils.isNotEmpty(innerReturnItems)) {
      for (InnerReturnItem innerReturnItem : innerReturnItems) {
        InnerReturnItemDTO innerReturnItemDTO = innerReturnItem.toDTO();

        ProductHistoryDTO productHistoryDTO = null;
        if (innerReturnItem.getProductHistoryId() != null) {
          productHistoryDTO = productHistoryDTOMap.get(innerReturnItem.getProductHistoryId());
        }
        if (productHistoryDTO != null) {
          innerReturnItemDTO.setProductHistoryDTO(productHistoryDTO);
        } else {
          ProductDTO productDTO = productDTOMap.get(innerReturnItem.getProductId());
          if (productDTO != null) {
            innerReturnItemDTO.setProductDTO(productDTO);
          }
        }
        totalAmount += NumberUtil.doubleVal(innerReturnItem.getAmount());
        if (innerReturnDTO.getStorehouseId() != null) {
          StoreHouseInventoryDTO storeHouseInventoryDTO = storeHouseInventoryDTOMap.get(innerReturnItem.getProductId());
          if (storeHouseInventoryDTO != null) {
            innerReturnItemDTO.setInventoryAmount(NumberUtil.toReserve(NumberUtil.doubleVal(storeHouseInventoryDTO.getAmount()),2));
            totalInventoryAmount += NumberUtil.doubleVal(storeHouseInventoryDTO.getAmount());
          }
        } else {
          InventoryDTO inventoryDTO = inventoryDTOMap.get(innerReturnItem.getProductId());
          if (inventoryDTO != null) {
            innerReturnItemDTO.setInventoryAmount(NumberUtil.toReserve(inventoryDTO.getAmount(),2));
            totalInventoryAmount += NumberUtil.doubleVal(inventoryDTO.getAmount());
          }
        }
        innerReturnItemDTOs.add(innerReturnItemDTO);
      }
    }
    innerReturnDTO.setTotalAmount(totalAmount);
    innerReturnDTO.setTotalInventoryAmount(totalInventoryAmount);
    if (CollectionUtils.isNotEmpty(innerReturnItemDTOs)) {
      innerReturnDTO.setItemDTOs(innerReturnItemDTOs.toArray(new InnerReturnItemDTO[innerReturnItemDTOs.size()]));
    }
    innerReturnDTO.setVestDateStr(DateUtil.dateLongToStr(innerReturnDTO.getVestDate(),DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN));
    return innerReturnDTO;
  }

  private List<Long> removeNullProductRow(InnerPickingDTO innerPickingDTO) {
    List<Long> productIdList = new ArrayList<Long>();
    List<InnerPickingItemDTO> innerPickingItemDTOList = new ArrayList<InnerPickingItemDTO>();
    InnerPickingItemDTO[] innerPickingItemDTOs = innerPickingDTO.getItemDTOs();
    if (innerPickingItemDTOs != null && !ArrayUtil.isEmpty(innerPickingItemDTOs)) {
      for (int i = 0; i < innerPickingItemDTOs.length; i++) {
        if (innerPickingItemDTOs[i].getProductId() != null && StringUtils.isNotBlank(innerPickingItemDTOs[i].getProductName())) {
          innerPickingItemDTOList.add(innerPickingItemDTOs[i]);
          productIdList.add(innerPickingItemDTOs[i].getProductId());
        }
      }
    }
    if (CollectionUtils.isNotEmpty(innerPickingItemDTOList)) {
      innerPickingDTO.setItemDTOs(innerPickingItemDTOList.toArray(new InnerPickingItemDTO[innerPickingItemDTOList.size()]));
    } else {
      innerPickingDTO.setItemDTOs(new InnerPickingItemDTO[0]);
    }
    return productIdList;
  }

  @Override
  public List<InnerPickingDTO> getInnerPickingDTOs(InnerPickingDTO searchCondition) {
    List<InnerPickingDTO> innerPickingDTOs = new ArrayList<InnerPickingDTO>();
    if (searchCondition == null || searchCondition.getShopId() == null) {
      return innerPickingDTOs;
    }
    searchCondition.initSearchTime();
    TxnWriter writer = txnDaoManager.getWriter();
    List<InnerPicking> innerPickings = writer.getInnerPickings(searchCondition);
    Set<Long> innerPickingIds = new HashSet<Long>();
    if (CollectionUtils.isNotEmpty(innerPickings)) {
      for (InnerPicking innerPicking : innerPickings) {
        innerPickingIds.add(innerPicking.getId());
      }
    }
    Map<Long, List<InnerPickingItemDTO>> innerPickingItemDTOMap = getInnerPickingItemDTOs(innerPickingIds.toArray(new Long[innerPickingIds.size()]));
    if (CollectionUtils.isNotEmpty(innerPickings)) {
      for (InnerPicking innerPicking : innerPickings) {
        InnerPickingDTO innerPickingDTO = innerPicking.toDTO();
        List<InnerPickingItemDTO> innerPickingItemDTOs = innerPickingItemDTOMap.get(innerPicking.getId());
        double totalAmount = 0d;
        if (CollectionUtils.isNotEmpty(innerPickingItemDTOs)) {
          for (InnerPickingItemDTO innerPickingItemDTO : innerPickingItemDTOs) {
            totalAmount += NumberUtil.doubleVal(innerPickingItemDTO.getAmount());
          }
        }
        innerPickingDTO.setTotalAmount(totalAmount);
        innerPickingDTOs.add(innerPickingDTO);
      }
    }

    return innerPickingDTOs;
  }

  @Override
  public List<InnerReturnDTO> getInnerReturnDTOs(InnerReturnDTO searchCondition) {
    List<InnerReturnDTO> innerReturnDTOs = new ArrayList<InnerReturnDTO>();
      if (searchCondition == null || searchCondition.getShopId() == null) {
        return innerReturnDTOs;
      }
      searchCondition.initSearchTime();
      TxnWriter writer = txnDaoManager.getWriter();
      List<InnerReturn> innerReturns = writer.getInnerReturns(searchCondition);
      Set<Long> innerReturnIds = new HashSet<Long>();
      if (CollectionUtils.isNotEmpty(innerReturns)) {
        for (InnerReturn innerReturn : innerReturns) {
          innerReturnIds.add(innerReturn.getId());
        }
      }
      Map<Long, List<InnerReturnItemDTO>> innerReturnItemDTOMap = getInnerReturnItemDTOs(innerReturnIds.toArray(new Long[innerReturnIds.size()]));
      if (CollectionUtils.isNotEmpty(innerReturns)) {
        for (InnerReturn innerReturn : innerReturns) {
          InnerReturnDTO innerReturnDTO = innerReturn.toDTO();
          List<InnerReturnItemDTO> innerReturnItemDTOs = innerReturnItemDTOMap.get(innerReturn.getId());
          double totalAmount = 0d;
          if (CollectionUtils.isNotEmpty(innerReturnItemDTOs)) {
            for (InnerReturnItemDTO innerReturnItemDTO : innerReturnItemDTOs) {
              totalAmount += NumberUtil.doubleVal(innerReturnItemDTO.getAmount());
            }
          }
          innerReturnDTO.setTotalAmount(totalAmount);
          innerReturnDTOs.add(innerReturnDTO);
        }
      }
      return innerReturnDTOs;
  }

  @Override
  public int countInnerPickingDTOs(InnerPickingDTO searchCondition) {
    if (searchCondition == null || searchCondition.getShopId() == null) {
      return 0;
    }
    searchCondition.initSearchTime();
    return txnDaoManager.getWriter().countInnerPickings(searchCondition);
  }

  @Override
  public int countInnerReturnDTOs(InnerReturnDTO searchCondition) {
    if (searchCondition == null || searchCondition.getShopId() == null) {
      return 0;
    }
    searchCondition.initSearchTime();
    return txnDaoManager.getWriter().countInnerReturns(searchCondition);
  }

  @Override
  public int sumInnerPickingDTOs(Long shopId) {
    return txnDaoManager.getWriter().sumInnerPickings(shopId);
  }

  @Override
  public int sumInnerReturnDTOs(Long shopId) {
    return txnDaoManager.getWriter().sumInnerReturns(shopId);
  }

  @Override
  public Map<Long, List<InnerPickingItemDTO>> getInnerPickingItemDTOs(Long... ids) {
    Map<Long, List<InnerPickingItemDTO>> innerPickingItemDTOMap = new HashMap<Long, List<InnerPickingItemDTO>>();
    if (ArrayUtils.isEmpty(ids)) {
      return innerPickingItemDTOMap;
    }
    TxnWriter writer = txnDaoManager.getWriter();

    List<InnerPickingItem> innerPickingItems = writer.getInnerPickingItemsByInnerPickingId(ids);
    if (CollectionUtils.isNotEmpty(innerPickingItems)) {
      Set<Long> productHistoryIds = new HashSet<Long>();
      for (InnerPickingItem innerPickingItem : innerPickingItems) {
        productHistoryIds.add(innerPickingItem.getProductHistoryId());
      }
      Map<Long, ProductHistoryDTO> productHistoryDTOMap = getProductHistoryService().getProductHistoryDTOMapByProductHistoryIds(productHistoryIds);
      InnerPickingItemDTO innerPickingItemDTO = null;
      for (InnerPickingItem innerPickingItem : innerPickingItems) {
        innerPickingItemDTO = innerPickingItem.toDTO();
        innerPickingItemDTO.setProductHistoryDTO(productHistoryDTOMap.get(innerPickingItem.getProductHistoryId()));

        List<InnerPickingItemDTO> innerPickingItemDTOs = innerPickingItemDTOMap.get(innerPickingItem.getInnerPickingId());
        if (CollectionUtil.isEmpty(innerPickingItemDTOs)) {
          innerPickingItemDTOs = new ArrayList<InnerPickingItemDTO>();
        }
        innerPickingItemDTOs.add(innerPickingItemDTO);
        innerPickingItemDTOMap.put(innerPickingItem.getInnerPickingId(), innerPickingItemDTOs);
      }
    }
    return innerPickingItemDTOMap;
  }

  @Override
  public Map<Long, List<InnerReturnItemDTO>> getInnerReturnItemDTOs(Long... ids) {
    Map<Long, List<InnerReturnItemDTO>> innerReturnItemDTOMap = new HashMap<Long, List<InnerReturnItemDTO>>();
    if (ArrayUtils.isEmpty(ids)) {
      return innerReturnItemDTOMap;
    }
    TxnWriter writer = txnDaoManager.getWriter();

    List<InnerReturnItem> innerReturnItems = writer.getInnerReturnItemsByInnerReturnId(ids);
    if (CollectionUtils.isNotEmpty(innerReturnItems)) {
      Set<Long> productHistoryIds = new HashSet<Long>();
      for (InnerReturnItem innerReturnItem : innerReturnItems) {
        productHistoryIds.add(innerReturnItem.getProductHistoryId());
      }
      Map<Long, ProductHistoryDTO> productHistoryDTOMap = getProductHistoryService().getProductHistoryDTOMapByProductHistoryIds(productHistoryIds);
      InnerReturnItemDTO innerReturnItemDTO = null;

      for (InnerReturnItem innerReturnItem : innerReturnItems) {
        innerReturnItemDTO = innerReturnItem.toDTO();
        innerReturnItemDTO.setProductHistoryDTO(productHistoryDTOMap.get(innerReturnItem.getProductHistoryId()));

        List<InnerReturnItemDTO> innerReturnItemDTOs = innerReturnItemDTOMap.get(innerReturnItem.getInnerReturnId());
        if (CollectionUtil.isEmpty(innerReturnItemDTOs)) {
          innerReturnItemDTOs = new ArrayList<InnerReturnItemDTO>();
        }
        innerReturnItemDTOs.add(innerReturnItemDTO);
        innerReturnItemDTOMap.put(innerReturnItem.getInnerReturnId(), innerReturnItemDTOs);
      }
    }
    return innerReturnItemDTOMap;
  }

  @Override
  public boolean checkRepairPickingUsedInProcessingOrder(Long shopId) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    if(writer.countProcessingRepairPickingUseByShopId(shopId)>0){
      return true;
    }
    if(writer.countProcessingRepairOrderUseRepairPickingByShopId(shopId)>0){
      return true;
    }
    return false;
  }

  @Override
  public boolean checkProcessingRepairOrderUseMaterialByShopId(Long shopId) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    if (writer.countProcessingRepairOrderUseMaterialByShopId(shopId) > 0) {
      return true;
    }
    return false;
  }

  @Override
  public RepairPickingDTO getRepairPickingDTOSimpleByRepairId(Long shopId, Long repairOrderId) {
    if(shopId == null || repairOrderId == null){
      return null;
    }
    RepairPicking repairPicking = txnDaoManager.getWriter().getRepairPickingByRepairOrderId(shopId,repairOrderId);
    if(repairPicking!=null){
      return repairPicking.toDTO();
    }
    return null;
  }

  @Override
  public Map<Long,RepairPickingDTO> getSimpleRepairPickingDTOsByRepairOrderIds(Long shopId, Long... repairOrderId) {
    Map<Long,RepairPickingDTO> repairPickingDTOMap = new HashMap<Long, RepairPickingDTO>();
    List<RepairPicking> repairPickingList = txnDaoManager.getWriter().getRepairPickingsByRepairOrderIds(shopId,repairOrderId);
    if(CollectionUtils.isNotEmpty(repairPickingList)){
      for(RepairPicking repairPicking:repairPickingList){
        repairPickingDTOMap.put(repairPicking.getRepairOrderId(),repairPicking.toDTO());
      }
    }
    return repairPickingDTOMap;
  }

  @Override
  public Result validatorLackProductTodo(Long shopId, Long orderId)throws Exception{
    if(shopId == null || orderId == null){
      return new Result();
    }
    RepairPickingDTO repairPickingDTO = getRepairPickDTODById(shopId,orderId);
    Set<Long> lackProductIds = new HashSet<Long>();
    if (repairPickingDTO != null && CollectionUtils.isNotEmpty(repairPickingDTO.getPendingItemDTOs())) {
       for(RepairPickingItemDTO repairPickingItemDTO : repairPickingDTO.getPendingItemDTOs()){
         if(repairPickingItemDTO.getProductId()!=null && repairPickingItemDTO.getIsLack()){
           lackProductIds.add(repairPickingItemDTO.getProductId());
         }
       }
    }

    if(CollectionUtils.isNotEmpty(lackProductIds) && repairPickingDTO.getStorehouseId() != null){
      if(getInventoryService().checkBatchProductInventoryInOtherStorehouse(shopId,repairPickingDTO,new ArrayList<Long>(lackProductIds))){
        return new Result(ValidatorConstant.ALLOCATE_OR_PURCHASE, false,Result.Operation.ALLOCATE_OR_PURCHASE.getValue(), null);
      }
    }
    return new Result();
  }

  @Override
  public boolean isNeedToCreateNewRepairPicking(RepairOrderDTO repairOrderDTO) {
    if(repairOrderDTO == null ){
      return  false;
    }
    if (repairOrderDTO.getId() == null) {
      if (repairOrderDTO.isHaveItem()) {
        return true;
      } else {
        return false;
      }
    }
    RepairPickingDTO repairPickingDTO = getRepairPickingDTOSimpleByRepairId(repairOrderDTO.getShopId(),repairOrderDTO.getId());
    if(repairPickingDTO == null ){
      if(OrderStatus.REPAIR_REPEAL.equals(repairOrderDTO.getStatus()) && repairOrderDTO.isHaveItem()){      //已作废
        for(RepairOrderItemDTO repairOrderItemDTO : repairOrderDTO.getItemDTOs()){
          if(StringUtils.isBlank(repairOrderItemDTO.getProductName())){
            continue;
          }
          if(NumberUtil.doubleVal(repairOrderItemDTO.getReserved())>0.0001){
            return true;
          }
        }
      } else if ((OrderStatus.REPAIR_DISPATCH.equals(repairOrderDTO.getStatus()) ||
          OrderStatus.REPAIR_CHANGE.equals(repairOrderDTO.getStatus()) ||
          OrderStatus.REPAIR_DONE.equals(repairOrderDTO.getStatus()))
          && repairOrderDTO.isHaveItem()) {
        return  true;
      }
    }else {
      return false;
    }
    return false;
  }

  @Override
  public boolean isNeedToUpdateRepairPicking(RepairOrderDTO repairOrderDTO) {
    return getRepairPickingDTOSimpleByRepairId(repairOrderDTO.getShopId(),repairOrderDTO.getId()) != null;
  }
}
