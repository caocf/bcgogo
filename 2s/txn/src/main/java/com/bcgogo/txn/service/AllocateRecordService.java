package com.bcgogo.txn.service;

import com.bcgogo.config.dto.OperationLogDTO;
import com.bcgogo.config.service.IOperationLogService;
import com.bcgogo.enums.*;
import com.bcgogo.exception.BcgogoException;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.product.dto.ProductLocalInfoDTO;
import com.bcgogo.product.service.IProductService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.bcgogoListener.orderEvent.BcgogoOrderReindexEvent;
import com.bcgogo.txn.bcgogoListener.publisher.BcgogoEventPublisher;
import com.bcgogo.txn.dto.*;
import com.bcgogo.txn.model.*;
import com.bcgogo.txn.service.productThrough.IProductInStorageService;
import com.bcgogo.txn.service.solr.IProductSolrWriterService;
import com.bcgogo.user.service.utils.BcgogoShopLogicResourceUtils;
import com.bcgogo.utils.CollectionUtil;
import com.bcgogo.utils.UnitUtil;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: xzhu
 * Date: 12-12-18
 * Time: 上午8:59
 * To change this template use File | Settings | File Templates.
 */
@Service
public class AllocateRecordService implements IAllocateRecordService {
  private static final Logger LOG = LoggerFactory.getLogger(AllocateRecordService.class);
  @Autowired
  private TxnDaoManager txnDaoManager;

  @Override
  public List<AllocateRecordDTO> searchAllocateRecords(AllocateRecordSearchConditionDTO allocateRecordSearchConditionDTO) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    List<AllocateRecord> allocateRecordList = writer.searchAllocateRecords(allocateRecordSearchConditionDTO);
    if(CollectionUtils.isNotEmpty(allocateRecordList)){
      List<AllocateRecordDTO> allocateRecordDTOList = new ArrayList<AllocateRecordDTO>();
      for(AllocateRecord allocateRecord : allocateRecordList){
        allocateRecordDTOList.add(allocateRecord.toDTO());
      }
      return allocateRecordDTOList;
    }
    return null;
  }

  @Override
  public int countAllocateRecords(AllocateRecordSearchConditionDTO allocateRecordSearchConditionDTO) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.countAllocateRecords(allocateRecordSearchConditionDTO);
  }

  @Override
  public void saveOrUpdateAllocateRecord(Long shopId,AllocateRecordDTO allocateRecordDTO) throws Exception {
    if (allocateRecordDTO == null)
      throw new BcgogoException("allocateRecordDTO is null");

    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      OperationTypes operationType = saveOrUpdateAllocateRecordToDB(shopId, allocateRecordDTO, writer);
      //保存出入库记录
      ServiceManager.getService(IProductInStorageService.class).productThroughByAllocateRecord(allocateRecordDTO,writer);

      if(OrderTypes.REPAIR.equals(allocateRecordDTO.getUpdateLackOrderType()) && allocateRecordDTO.getUpdateLackOrderId()!=null){
        handleRemindEventForRepairOrder(writer,shopId,allocateRecordDTO.getShopVersionId(),allocateRecordDTO);
      }
      writer.commit(status);
      IOperationLogService operationLogService = ServiceManager.getService(IOperationLogService.class);
      operationLogService.saveOperationLog(new OperationLogDTO(shopId, allocateRecordDTO.getUserId(), allocateRecordDTO.getId(), ObjectTypes.ALLOCATE_RECORD, operationType));
      ServiceManager.getService(IProductSolrWriterService.class).createProductSolrIndex(shopId, allocateRecordDTO.getProductIdList().toArray(new Long[allocateRecordDTO.getProductIdList().size()]));

    } finally {
      writer.rollback(status);
    }
  }
  private void handleRemindEventForRepairOrder(TxnWriter writer, Long shopId,Long shopVersionId,AllocateRecordDTO allocateRecordDTO) throws Exception {
    IProductService productService = ServiceManager.getService(IProductService.class);
    IStoreHouseService storeHouseService = ServiceManager.getService(IStoreHouseService.class);
    if (allocateRecordDTO.getUpdateLackOrderId()==null) {
      return;
    }
    Long repairOrderId = allocateRecordDTO.getUpdateLackOrderId();
    if(BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(shopVersionId)){
      RepairOrder repairOrder = writer.getRepairOrderById(repairOrderId ,shopId);
      if (allocateRecordDTO.getInStorehouseId() != null && !allocateRecordDTO.getInStorehouseId().equals(repairOrder.getStorehouseId())) {
        return;
      }
    }

    List<RepairRemindEvent> repairRemindEvents = writer.getRepairRemindEventByShopIdAndOrderIdAndType(shopId,repairOrderId, RepairRemindEventTypes.LACK);
    if (repairRemindEvents.size() == 0) {
      return;
    }
    AllocateRecordItemDTO itemDTOs[] = allocateRecordDTO.getItemDTOs();

    //new 库修改维修单缺料状态     todo check to delete
    for (AllocateRecordItemDTO itemDTO : itemDTOs) {

      ProductLocalInfoDTO productLocalInfoDTO = productService.getProductLocalInfoById(itemDTO.getProductId(), shopId);
      double inventoryAmount = 0d;
      if(BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(shopVersionId)){
        StoreHouseInventoryDTO storeHouseInventoryDTO = storeHouseService.getStoreHouseInventoryDTO(allocateRecordDTO.getInStorehouseId(),itemDTO.getProductId());
        inventoryAmount = storeHouseInventoryDTO==null?0d:storeHouseInventoryDTO.getAmount();
      }else{
        Inventory inventory = writer.getById(Inventory.class, itemDTO.getProductId());
        inventoryAmount =inventory.getAmount();
      }
      double lackAmountWithSellUnit = 0d;
      for (RepairRemindEvent repairRemindEvent : repairRemindEvents) {
        if (itemDTO.getProductId().equals(repairRemindEvent.getProductId())) {
          if (UnitUtil.isStorageUnit(repairRemindEvent.getUnit(), productLocalInfoDTO)) {
            lackAmountWithSellUnit = repairRemindEvent.getAmount() == null ? 0d : repairRemindEvent.getAmount();
            lackAmountWithSellUnit = lackAmountWithSellUnit * productLocalInfoDTO.getRate();
          } else {
            lackAmountWithSellUnit = repairRemindEvent.getAmount() == null ? 0d : repairRemindEvent.getAmount();
          }
          if (inventoryAmount < lackAmountWithSellUnit - 0.0001) {
            break;
          } else {
            inventoryAmount = inventoryAmount - repairRemindEvent.getAmount();
            repairRemindEvent.setEventTypeEnum(RepairRemindEventTypes.INCOMING);
            writer.saveOrUpdate(repairRemindEvent);
            //add by WLF 提醒总表同步更新
            List<RemindEvent> remindEventList = writer.getRemindEventByOldRemindEventId(RemindEventType.REPAIR,shopId, repairRemindEvent.getId());
            if(CollectionUtil.isNotEmpty(remindEventList)){
              remindEventList.get(0).setEventStatus(RepairRemindEventTypes.INCOMING.toString());
              writer.update(remindEventList.get(0));
            }
          }
        }
      }
    }
  }
  @Override
  public void saveOrUpdateAllocateRecord(TxnWriter writer, Long shopId, AllocateRecordDTO allocateRecordDTO) throws Exception {
    if (allocateRecordDTO == null)
      throw new BcgogoException("allocateRecordDTO is null");
    OperationTypes operationType = saveOrUpdateAllocateRecordToDB(shopId, allocateRecordDTO, writer);
          //保存出入库记录
     ServiceManager.getService(IProductInStorageService.class).productThroughByAllocateRecord(allocateRecordDTO,writer);
    IOperationLogService operationLogService = ServiceManager.getService(IOperationLogService.class);
    operationLogService.saveOperationLog(new OperationLogDTO(shopId, allocateRecordDTO.getUserId(), allocateRecordDTO.getId(), ObjectTypes.ALLOCATE_RECORD, operationType));

    ServiceManager.getService(IProductSolrWriterService.class).createProductSolrIndex(shopId, allocateRecordDTO.getProductIdList().toArray(new Long[allocateRecordDTO.getProductIdList().size()]));
  }

  private OperationTypes saveOrUpdateAllocateRecordToDB(Long shopId, AllocateRecordDTO allocateRecordDTO, TxnWriter writer) throws Exception {
    OperationTypes operationType = null;
    StoreHouse inStorehouse = writer.getById(StoreHouse.class, allocateRecordDTO.getInStorehouseId());
    allocateRecordDTO.setInStorehouseName(inStorehouse.getName());
    StoreHouse outStorehouse = writer.getById(StoreHouse.class, allocateRecordDTO.getOutStorehouseId());
    allocateRecordDTO.setOutStorehouseName(outStorehouse.getName());

    if (allocateRecordDTO.getId() != null) {
      AllocateRecord allocateRecord = writer.getById(AllocateRecord.class, allocateRecordDTO.getId());
      allocateRecord = allocateRecord.fromDTO(allocateRecordDTO);
      writer.update(allocateRecord);
      operationType = OperationTypes.UPDATE;
    } else {
      AllocateRecord allocateRecord = new AllocateRecord();
      writer.save(allocateRecord.fromDTO(allocateRecordDTO));
      allocateRecordDTO.setId(allocateRecord.getId());
      operationType = OperationTypes.CREATE;
    }
    //保存或者更新item
    IStoreHouseService storeHouseService = ServiceManager.getService(IStoreHouseService.class);
    IProductService productService = ServiceManager.getService(IProductService.class);
    IProductHistoryService productHistoryService = ServiceManager.getService(IProductHistoryService.class);
    StoreHouseInventoryDTO outStoreHouseInventoryDTO = null, inStoreHouseInventoryDTO = null;
    ProductHistoryDTO productHistoryDTO = null;
    Double itemAmount = 0d;
    Set<Long> productIds = new HashSet<Long>();
    for (AllocateRecordItemDTO itemDTO : allocateRecordDTO.getItemDTOs()) {
      productIds.add(itemDTO.getProductId());
    }
    Map<Long, ProductHistoryDTO> productHistoryDTOMap = productHistoryService.getOrSaveProductHistoryByLocalInfoId(shopId, productIds.toArray(new Long[productIds.size()]));
    for (AllocateRecordItemDTO itemDTO : allocateRecordDTO.getItemDTOs()) {
      if (itemDTO.getProductHistoryId() == null) {
        productHistoryDTO = productHistoryDTOMap.get(itemDTO.getProductId());
        itemDTO.setProductHistoryId(productHistoryDTO == null ? null : productHistoryDTO.getId());
      }
      itemDTO.setAllocateRecordId(allocateRecordDTO.getId());

      AllocateRecordItem allocateRecordItem = new AllocateRecordItem();
      allocateRecordItem = allocateRecordItem.fromDTO(itemDTO);
      writer.save(allocateRecordItem);
      itemDTO.setId(allocateRecordItem.getId());
      //处理库存信息
      ProductDTO productDTO = productService.getProductByProductLocalInfoId(itemDTO.getProductId(), shopId);
      if (UnitUtil.isStorageUnit(itemDTO.getUnit(), productDTO)) {
        itemAmount = itemDTO.getAmount() * productDTO.getRate();
      }else{
        itemAmount = itemDTO.getAmount();
      }
      inStoreHouseInventoryDTO = new StoreHouseInventoryDTO();
      inStoreHouseInventoryDTO.setStorehouseId(allocateRecordDTO.getInStorehouseId());
      inStoreHouseInventoryDTO.setProductLocalInfoId(itemDTO.getProductId());
      inStoreHouseInventoryDTO.setChangeAmount(itemAmount);
//      inStoreHouseInventoryDTO.setStorageBin(itemDTO.getInStorageBin());
      storeHouseService.saveOrUpdateStoreHouseInventoryDTO(writer, inStoreHouseInventoryDTO);

      outStoreHouseInventoryDTO = new StoreHouseInventoryDTO();
      outStoreHouseInventoryDTO.setStorehouseId(allocateRecordDTO.getOutStorehouseId());
      outStoreHouseInventoryDTO.setProductLocalInfoId(itemDTO.getProductId());
      outStoreHouseInventoryDTO.setChangeAmount(itemAmount * -1);
      storeHouseService.saveOrUpdateStoreHouseInventoryDTO(writer, outStoreHouseInventoryDTO);
    }
    return operationType;
  }

  @Override
  public AllocateRecordDTO getAllocateRecordDTOById(Long shopId, Long id) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    AllocateRecordDTO allocateRecordDTO = writer.getAllocateRecordDTOById(shopId,id);
    if(allocateRecordDTO!=null){
      IProductHistoryService productHistoryService = ServiceManager.getService(IProductHistoryService.class);
      List<AllocateRecordItemDTO> allocateRecordItemDTOList = writer.getAllocateRecordItemDTOByAllocateRecordId(id);
      for(AllocateRecordItemDTO allocateRecordItemDTO : allocateRecordItemDTOList){
        allocateRecordItemDTO.setProductHistoryDTO(productHistoryService.getProductHistoryById(allocateRecordItemDTO.getProductHistoryId(),shopId));
      }
      allocateRecordDTO.setItemDTOs(allocateRecordItemDTOList.toArray(new AllocateRecordItemDTO[allocateRecordItemDTOList.size()]));
    }
    return allocateRecordDTO;
  }
}
