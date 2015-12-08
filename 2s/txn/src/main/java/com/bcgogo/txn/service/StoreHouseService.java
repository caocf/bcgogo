package com.bcgogo.txn.service;

import com.bcgogo.config.dto.OperationLogDTO;
import com.bcgogo.config.service.IOperationLogService;
import com.bcgogo.enums.DeletedType;
import com.bcgogo.enums.ObjectTypes;
import com.bcgogo.enums.OperationTypes;
import com.bcgogo.exception.BcgogoException;
import com.bcgogo.product.model.ProductLocalInfo;
import com.bcgogo.product.service.IProductService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.InventoryDTO;
import com.bcgogo.txn.dto.StoreHouseDTO;
import com.bcgogo.txn.dto.StoreHouseInventoryDTO;
import com.bcgogo.txn.model.StoreHouse;
import com.bcgogo.txn.model.StoreHouseInventory;
import com.bcgogo.txn.model.TxnDaoManager;
import com.bcgogo.txn.model.TxnWriter;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: xzhu
 * Date: 12-12-14
 * Time: 下午1:11
 * To change this template use File | Settings | File Templates.
 */
@Service
public class StoreHouseService implements IStoreHouseService {
  private static final Logger LOG = LoggerFactory.getLogger(StoreHouseService.class);
  @Autowired
  private TxnDaoManager txnDaoManager;
  @Override
  public List<StoreHouseDTO> searchStoreHouses(Long shopId, int start, int pageSize) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    List<StoreHouse> storeHouseList = writer.searchStoreHouses(shopId, start, pageSize);
    if(CollectionUtils.isNotEmpty(storeHouseList)){
      List<StoreHouseDTO> storeHouseDTOList = new ArrayList<StoreHouseDTO>();
      for(StoreHouse storeHouse : storeHouseList){
        storeHouseDTOList.add(storeHouse.toDTO());
      }
      return storeHouseDTOList;
    }
    return null;
  }

  @Override
  public int countStoreHouses(Long shopId) throws Exception{
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.countStoreHouses(shopId);
  }

  @Override
  public boolean checkStoreHouseExist(Long shopId,StoreHouseDTO storeHouseDTO) throws Exception{
    TxnWriter writer = txnDaoManager.getWriter();
    int count = writer.countStoreHousesByName(shopId,storeHouseDTO);
    if(count>0){
      return true;
    }
    return false;
  }

  @Override
  public boolean checkStoreHouseUsedInProcessingOrder(Long shopId,Long storehouseId) throws Exception{
    TxnWriter writer = txnDaoManager.getWriter();
    //入库单没有中间状态
//    if(writer.countProcessingPurchaseInventoryOrdersUseStoreHouseByStorehouseId(shopId,storehouseId)>0){
//      return true;
//    }
    if(writer.countProcessingPurchaseReturnOrdersUseStoreHouseByStorehouseId(shopId,storehouseId)>0){
      return true;
    }
    if(writer.countProcessingRepairOrderOrdersUseStoreHouseByStorehouseId(shopId,storehouseId)>0){
      return true;
    }
    if(writer.countProcessingSalesOrderOrdersUseStoreHouseByStorehouseId(shopId,storehouseId)>0){
      return true;
    }
    if(writer.countProcessingSalesReturnOrdersUseStoreHouseByStorehouseId(shopId,storehouseId)>0){
      return true;
    }
    if(writer.countProcessingRepairPickingUseStoreHouseByStorehouseId(shopId,storehouseId)>0){
      return true;
    }

    return false;
  }

  @Override
  public void saveOrUpdateStoreHouse(StoreHouseDTO storeHouseDTO) throws Exception {
    if (storeHouseDTO == null)
      return;
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      IOperationLogService operationLogService = ServiceManager.getService(IOperationLogService.class);
      if(storeHouseDTO.getId()!=null){
        StoreHouse storeHouse = writer.getById(StoreHouse.class,storeHouseDTO.getId());
        storeHouse.setAddress(StringUtil.toTrim(storeHouseDTO.getAddress()));
        storeHouse.setMemo(StringUtil.toTrim(storeHouseDTO.getMemo()));
        storeHouse.setName(StringUtil.toTrim(storeHouseDTO.getName()));
        storeHouse.setShopId(storeHouseDTO.getShopId());
        storeHouse.setUserId(storeHouseDTO.getUserId());
        writer.update(storeHouse);
        ServiceManager.getService(ITxnService.class).saveOperationLogTxnService(new OperationLogDTO(storeHouse.getShopId(),storeHouse.getUserId(),storeHouse.getId(), ObjectTypes.STOREHOUSE, OperationTypes.UPDATE));
      }else{
        StoreHouse storeHouse = new StoreHouse();
        storeHouse.setAddress(StringUtil.toTrim(storeHouseDTO.getAddress()));
        storeHouse.setMemo(StringUtil.toTrim(storeHouseDTO.getMemo()));
        storeHouse.setName(StringUtil.toTrim(storeHouseDTO.getName()));
        storeHouse.setShopId(storeHouseDTO.getShopId());
        storeHouse.setUserId(storeHouseDTO.getUserId());
        writer.save(storeHouse);
        storeHouseDTO.setId(storeHouse.getId());
        ServiceManager.getService(ITxnService.class).saveOperationLogTxnService(new OperationLogDTO(storeHouse.getShopId(),storeHouse.getUserId(),storeHouse.getId(), ObjectTypes.STOREHOUSE, OperationTypes.CREATE));
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public StoreHouseDTO getStoreHouseDTOById(Long shopId, Long id) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.getStoreHouseDTOById(shopId,id);
  }

  @Override
  public void deleteStoreHouseById(Long shopId, Long id) throws Exception {
    if (id == null)
      return;
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      StoreHouseDTO storeHouseDTO = writer.getStoreHouseDTOById(shopId,id);
      if (storeHouseDTO == null)
        return;
      StoreHouse storeHouse = writer.getById(StoreHouse.class, storeHouseDTO.getId());
      storeHouse.setDeleted(DeletedType.TRUE);
      writer.update(storeHouse);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public Double sumStoreHouseAllInventoryAmountByStoreHouseId(Long shopId, Long storeHouseId) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.sumStoreHouseAllInventoryAmountByStoreHouseId(shopId, storeHouseId);
  }

  @Override
  public Double sumStoreHouseAllInventoryAmountByProductLocalInfoId(Long shopId, Long productLocalInfoId) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.sumStoreHouseAllInventoryAmountByProductLocalInfoId(shopId, productLocalInfoId);
  }

  @Override
  public List<StoreHouseDTO> getAllStoreHousesByShopId(Long shopId) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    List<StoreHouse> storeHouseList = writer.getAllStoreHousesByShopId(shopId);
    if(CollectionUtils.isNotEmpty(storeHouseList)){
      List<StoreHouseDTO> storeHouseDTOList = new ArrayList<StoreHouseDTO>();
      for(StoreHouse storeHouse : storeHouseList){
        storeHouseDTOList.add(storeHouse.toDTO());
      }
      return storeHouseDTOList;
    }
    return null;
  }

  @Override
  public Map<Long,StoreHouseDTO> getAllStoreHousesMapByShopId(Long shopId) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    Map<Long,StoreHouseDTO> storeHouseDTOMap = new HashMap<Long, StoreHouseDTO>();
    List<StoreHouse> storeHouseList = writer.getAllStoreHousesByShopId(shopId);
    if(CollectionUtils.isNotEmpty(storeHouseList)){
      for(StoreHouse storeHouse : storeHouseList){
        storeHouseDTOMap.put(storeHouse.getId(),storeHouse.toDTO());
      }
      return storeHouseDTOMap;
    }
    return storeHouseDTOMap;
  }

  @Override
  public Map<String,StoreHouseDTO> getAllStoreHousesNameKeyMapByShopId(Long shopId) throws BcgogoException {
    TxnWriter writer = txnDaoManager.getWriter();
    Map<String,StoreHouseDTO> storeHouseDTOMap = new HashMap<String, StoreHouseDTO>();
    List<StoreHouse> storeHouseList = writer.getAllStoreHousesByShopId(shopId);
    if(CollectionUtils.isNotEmpty(storeHouseList)){
      for(StoreHouse storeHouse : storeHouseList){
        storeHouseDTOMap.put(storeHouse.getName(),storeHouse.toDTO());
      }
      return storeHouseDTOMap;
    }
    return storeHouseDTOMap;
  }

  @Override
  public StoreHouseInventoryDTO getStoreHouseInventoryDTO(Long storehouseId, Long productLocalInfoId) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    if(storehouseId==null || productLocalInfoId==null){
      return null;
    }
    List<StoreHouseInventory> storeHouseInventoryList = writer.getStoreHouseInventory(storehouseId, productLocalInfoId);
    if(CollectionUtils.isNotEmpty(storeHouseInventoryList)){
      return storeHouseInventoryList.get(0).toDTO();
    }
    return null;
  }

  @Override
  public List<StoreHouseInventoryDTO> getStoreHouseInventoryDTOByStorehouseAndProductIds(Long shopId,Long storehouseId, Long... productLocalInfoId) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    if(storehouseId==null) return null;
    List<StoreHouseInventory> storeHouseInventoryList =  writer.getStoreHouseInventoryByStorehouseAndProductIds(shopId, storehouseId, productLocalInfoId);
    if(CollectionUtils.isNotEmpty(storeHouseInventoryList)){
      List<StoreHouseInventoryDTO> storeHouseInventoryDTOList = new ArrayList<StoreHouseInventoryDTO>();
      for(StoreHouseInventory storeHouseInventory : storeHouseInventoryList){
        storeHouseInventoryDTOList.add(storeHouseInventory.toDTO());
      }
      return storeHouseInventoryDTOList;
    }
    return null;
  }

  @Override
  public List<StoreHouseInventoryDTO> getStoreHouseInventoryDTOByProductIds(Long shopId, Long... productLocalInfoId) throws Exception {
    if(shopId ==null || productLocalInfoId ==null || productLocalInfoId.length==0){
      return new ArrayList<StoreHouseInventoryDTO>();
    }
    TxnWriter writer = txnDaoManager.getWriter();
    List<StoreHouseInventory> storeHouseInventoryList = writer.getStoreHouseInventoryByProductIds(shopId, productLocalInfoId);
    if(CollectionUtils.isNotEmpty(storeHouseInventoryList)){
      List<StoreHouseInventoryDTO> storeHouseInventoryDTOList = new ArrayList<StoreHouseInventoryDTO>();
      for(StoreHouseInventory storeHouseInventory : storeHouseInventoryList){
        storeHouseInventoryDTOList.add(storeHouseInventory.toDTO());
      }
      return storeHouseInventoryDTOList;
    }
    return null;
  }

  @Override
  public Map<Long, StoreHouseInventoryDTO> getStoreHouseInventoryDTOMapByProductId(Long shopId, Long productLocalInfoId) throws Exception {
    if(shopId ==null || productLocalInfoId ==null){
      return new HashMap<Long, StoreHouseInventoryDTO>();
    }
    TxnWriter writer = txnDaoManager.getWriter();
    List<StoreHouseInventory> storeHouseInventoryList = writer.getStoreHouseInventoryByProductIds(shopId, productLocalInfoId);
    Map<Long, StoreHouseInventoryDTO> storehouseInventoryDTOMap = new HashMap<Long, StoreHouseInventoryDTO>();
    if (CollectionUtils.isNotEmpty(storeHouseInventoryList)) {
      for (StoreHouseInventory storeHouseInventory : storeHouseInventoryList) {
        storehouseInventoryDTOMap.put(storeHouseInventory.getStorehouseId(), storeHouseInventory.toDTO());
      }
    }
    return storehouseInventoryDTOMap;
  }

  @Override
  public Map<Long, StoreHouseInventoryDTO> getStoreHouseInventoryDTOMapByStorehouseAndProductIds(Long shopId, Long storehouseId, Long... productLocalInfoId) throws Exception{
    if(shopId ==null || storehouseId ==null || productLocalInfoId ==null || productLocalInfoId.length==0){
      return new HashMap<Long, StoreHouseInventoryDTO>();
    }
    List<StoreHouseInventoryDTO> storeHouseInventoryDTOList = this.getStoreHouseInventoryDTOByStorehouseAndProductIds(shopId, storehouseId, productLocalInfoId);
    Map<Long, StoreHouseInventoryDTO> storehouseInventoryDTOMap = new HashMap<Long, StoreHouseInventoryDTO>();
    if (CollectionUtils.isNotEmpty(storeHouseInventoryDTOList)) {
      for (StoreHouseInventoryDTO storeHouseInventoryDTO : storeHouseInventoryDTOList) {
        storehouseInventoryDTOMap.put(storeHouseInventoryDTO.getProductLocalInfoId(), storeHouseInventoryDTO);
      }
    }
    return storehouseInventoryDTOMap;
  }
  @Override
  public Map<Long, Map<Long,StoreHouseInventoryDTO>> getStoreHouseInventoryDTOMapMapByProductIds(Long shopId, Long... productLocalInfoId) throws Exception{
    if(shopId ==null || productLocalInfoId ==null || productLocalInfoId.length==0){
      return new HashMap<Long, Map<Long,StoreHouseInventoryDTO>>();
    }
    TxnWriter writer = txnDaoManager.getWriter();
    List<StoreHouseInventory> storeHouseInventoryList = writer.getStoreHouseInventoryByProductIds(shopId, productLocalInfoId);
    Map<Long, Map<Long,StoreHouseInventoryDTO>> storehouseInventoryDTOMapMap = new HashMap<Long, Map<Long,StoreHouseInventoryDTO>>();
    if (CollectionUtils.isNotEmpty(storeHouseInventoryList)) {
      Map<Long,StoreHouseInventoryDTO> storeHouseInventoryDTOMap = null;
      for (StoreHouseInventory storeHouseInventory : storeHouseInventoryList) {
        storeHouseInventoryDTOMap  = storehouseInventoryDTOMapMap.get(storeHouseInventory.getProductLocalInfoId());
        if(storeHouseInventoryDTOMap==null){
          storeHouseInventoryDTOMap = new HashMap<Long, StoreHouseInventoryDTO>();
        }
        storeHouseInventoryDTOMap.put(storeHouseInventory.getStorehouseId(), storeHouseInventory.toDTO());
        storehouseInventoryDTOMapMap.put(storeHouseInventory.getProductLocalInfoId(),storeHouseInventoryDTOMap);
      }
    }
    return storehouseInventoryDTOMapMap;
  }
  @Override
  public StoreHouseInventoryDTO saveOrUpdateStoreHouseInventoryDTO(TxnWriter writer,StoreHouseInventoryDTO storeHouseInventoryDTO) throws Exception {
    List<StoreHouseInventory> storeHouseInventoryList = writer.getStoreHouseInventory(storeHouseInventoryDTO.getStorehouseId(), storeHouseInventoryDTO.getProductLocalInfoId());
    StoreHouseInventory storeHouseInventory = null;
    if(CollectionUtils.isNotEmpty(storeHouseInventoryList)){
      storeHouseInventory = storeHouseInventoryList.get(0);
    }else{
      storeHouseInventory = new StoreHouseInventory();
      storeHouseInventory.setProductLocalInfoId(storeHouseInventoryDTO.getProductLocalInfoId());
      storeHouseInventory.setStorehouseId(storeHouseInventoryDTO.getStorehouseId());
      storeHouseInventory.setAmount(0d);
    }
    if(storeHouseInventoryDTO.getChangeAmount()!=null){
      if(storeHouseInventory.getAmount() + storeHouseInventoryDTO.getChangeAmount()<0){
        LOG.error("产品[{}]仓库[{}]库存数量[{}]+[{}]不能小于0",new Object[]{storeHouseInventoryDTO.getProductLocalInfoId(),storeHouseInventoryDTO.getStorehouseId(),storeHouseInventory.getAmount(),storeHouseInventoryDTO.getChangeAmount()});
        throw new BcgogoException("产品{["+storeHouseInventoryDTO.getProductLocalInfoId()+"]}仓库{["+storeHouseInventoryDTO.getStorehouseId()+"]}库存数量不能小于0");
      }
      storeHouseInventory.setAmount(storeHouseInventory.getAmount() + storeHouseInventoryDTO.getChangeAmount());
    }else{
      storeHouseInventory.setAmount(storeHouseInventoryDTO.getAmount());
    }

    if(storeHouseInventoryDTO.getStorageBin()!=null){
      storeHouseInventory.setStorageBin(storeHouseInventoryDTO.getStorageBin());
    }
    writer.saveOrUpdate(storeHouseInventory);

    return storeHouseInventory.toDTO();
  }

  @Override
  public StoreHouseInventoryDTO saveOrUpdateStoreHouseInventoryDTO(StoreHouseInventoryDTO storeHouseInventoryDTO) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      StoreHouseInventoryDTO newStoreHouseInventoryDTO = saveOrUpdateStoreHouseInventoryDTO(writer,storeHouseInventoryDTO);
      writer.commit(status);
      return newStoreHouseInventoryDTO;
    } finally {
      writer.rollback(status);
    }
  }
  @Override
  public StoreHouseInventoryDTO saveOrUpdateStoreHouseStorageBin(StoreHouseInventoryDTO storeHouseInventoryDTO) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      List<StoreHouseInventory> storeHouseInventoryList = writer.getStoreHouseInventory(storeHouseInventoryDTO.getStorehouseId(), storeHouseInventoryDTO.getProductLocalInfoId());
      StoreHouseInventory storeHouseInventory = null;
      if(CollectionUtils.isNotEmpty(storeHouseInventoryList)){
        storeHouseInventory = storeHouseInventoryList.get(0);
      }else{
        storeHouseInventory = new StoreHouseInventory();
        storeHouseInventory.setProductLocalInfoId(storeHouseInventoryDTO.getProductLocalInfoId());
        storeHouseInventory.setStorehouseId(storeHouseInventoryDTO.getStorehouseId());
        storeHouseInventory.setAmount(0d);
      }
      if(storeHouseInventoryDTO.getStorageBin()!=null){
        storeHouseInventory.setStorageBin(storeHouseInventoryDTO.getStorageBin());
      }
      writer.saveOrUpdate(storeHouseInventory);
      writer.commit(status);
      return storeHouseInventory.toDTO();
    } finally {
      writer.rollback(status);
    }
  }
  @Override
  public void initDefaultStoreHouse(Long shopId) throws Exception {
    int count = this.countStoreHouses(shopId);
    if (count > 0) {
      LOG.debug("Shop[{}]已经有仓库，仓库数量为[{}],不需要再进行初始化生成默认仓库!", new Object[]{shopId, count});
      return;
    }
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {

      StoreHouse storeHouse = new StoreHouse();
      storeHouse.setShopId(shopId);
      storeHouse.setName("默认仓库");
      writer.save(storeHouse);

      IInventoryService inventoryService = ServiceManager.getService(IInventoryService.class);
      IProductService productService = ServiceManager.getService(IProductService.class);

      int start = 0;
      int rows = 2000;
      while (true) {
        List<Long> productLocalInfoIdList = productService.getProductLocalInfoIdList(shopId, start, rows);
        if (CollectionUtils.isEmpty(productLocalInfoIdList)) break;
        start += rows;

        List<Object[]> productDataList = productService.getProductDataByProductLocalInfoId(shopId, productLocalInfoIdList.toArray(new Long[productLocalInfoIdList.size()]));
        Set<Long> productIdSet = new HashSet<Long>();
        productIdSet.addAll(productLocalInfoIdList);
        Map<Long, InventoryDTO> inventoryDTOMap = inventoryService.getInventoryDTOMap(shopId, productIdSet);
        if (CollectionUtils.isNotEmpty(productDataList)) {
          ProductLocalInfo productLocalInfo = null;
          InventoryDTO inventoryDTO = null;
          for (Object[] obj : productDataList) {
            if (obj != null && obj.length == 2) {
              productLocalInfo = (ProductLocalInfo) obj[1];
              if (productLocalInfo != null) {
                inventoryDTO = inventoryDTOMap.get(productLocalInfo.getId());
                StoreHouseInventory storeHouseInventory = new StoreHouseInventory();
                storeHouseInventory.setStorehouseId(storeHouse.getId());
                storeHouseInventory.setProductLocalInfoId(productLocalInfo.getId());
                storeHouseInventory.setAmount(inventoryDTO == null ? 0d : inventoryDTO.getAmount());
                storeHouseInventory.setStorageBin(productLocalInfo.getStorageBin());
                writer.save(storeHouseInventory);
              }
            }
          }
        }
      }
      //初始化单据
      writer.updateAllOrderStoreHouseByStorehouse(shopId,storeHouse);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }
}
