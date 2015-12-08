package com.bcgogo.txn.service;

import com.bcgogo.exception.BcgogoException;
import com.bcgogo.txn.dto.StoreHouseDTO;
import com.bcgogo.txn.dto.StoreHouseInventoryDTO;
import com.bcgogo.txn.model.TxnWriter;

import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 12-9-8
 * Time: 上午5:15
 * To change this template use File | Settings | File Templates.
 */
public interface IStoreHouseService {

  public List<StoreHouseDTO> searchStoreHouses(Long shopId,int start, int pageSize) throws Exception;

  public int countStoreHouses(Long shopId) throws Exception;

  public boolean checkStoreHouseExist(Long shopId,StoreHouseDTO storeHouseDTO) throws Exception;

  public boolean checkStoreHouseUsedInProcessingOrder(Long shopId,Long storehouseId) throws Exception;

  public void saveOrUpdateStoreHouse(StoreHouseDTO storeHouseDTO) throws Exception;

  public StoreHouseDTO getStoreHouseDTOById(Long shopId,Long id) throws Exception;

  public void deleteStoreHouseById(Long shopId,Long id) throws Exception;

  public Double sumStoreHouseAllInventoryAmountByStoreHouseId(Long shopId,Long storeHouseId) throws Exception;

  public Double sumStoreHouseAllInventoryAmountByProductLocalInfoId(Long shopId,Long productLocalInfoId) throws Exception;

  public List<StoreHouseDTO> getAllStoreHousesByShopId(Long shopId) throws Exception;

  public Map<Long,StoreHouseDTO> getAllStoreHousesMapByShopId(Long shopId) throws Exception;

  /**
   * 仓库名称为key
   * @param shopId
   * @return
   * @throws Exception
   */
  public Map<String,StoreHouseDTO> getAllStoreHousesNameKeyMapByShopId(Long shopId) throws BcgogoException;

  public StoreHouseInventoryDTO getStoreHouseInventoryDTO(Long storehouseId,Long productLocalInfoId) throws Exception;

  public List<StoreHouseInventoryDTO> getStoreHouseInventoryDTOByStorehouseAndProductIds(Long shopId,Long storehouseId, Long... productLocalInfoId) throws Exception;

  public List<StoreHouseInventoryDTO> getStoreHouseInventoryDTOByProductIds(Long shopId, Long... productLocalInfoId) throws Exception;

  /**
   * key storehouseId
   * @param shopId
   * @param productLocalInfoId
   * @return
   * @throws Exception
   */
  public Map<Long,StoreHouseInventoryDTO> getStoreHouseInventoryDTOMapByProductId(Long shopId, Long productLocalInfoId) throws Exception;

  /**
   * key 为productLocalInfoId
   * @param shopId
   * @param storehouseId
   * @param productLocalInfoId
   * @return
   * @throws Exception
   */
  public Map<Long,StoreHouseInventoryDTO> getStoreHouseInventoryDTOMapByStorehouseAndProductIds(Long shopId, Long storehouseId, Long... productLocalInfoId) throws Exception;

  /**
   * key 为productLocalInfoId   value Map<Long,StoreHouseInventoryDTO> : key storehouseId,
   * @param shopId
   * @param productLocalInfoId
   * @return
   * @throws Exception
   */
  public Map<Long, Map<Long,StoreHouseInventoryDTO>> getStoreHouseInventoryDTOMapMapByProductIds(Long shopId, Long... productLocalInfoId) throws Exception;

  /**
   * @param storeHouseInventoryDTO
   * @return
   * @throws Exception
   */
  public StoreHouseInventoryDTO saveOrUpdateStoreHouseInventoryDTO(StoreHouseInventoryDTO storeHouseInventoryDTO) throws Exception;

  /**
   * 外布调用控制事务
   * @param writer
   * @param storeHouseInventoryDTO
   * @return
   * @throws Exception
   */
  public StoreHouseInventoryDTO saveOrUpdateStoreHouseInventoryDTO(TxnWriter writer,StoreHouseInventoryDTO storeHouseInventoryDTO) throws Exception;

  /**
   * @param storeHouseInventoryDTO
   * @return
   * @throws Exception
   */
  public StoreHouseInventoryDTO saveOrUpdateStoreHouseStorageBin(StoreHouseInventoryDTO storeHouseInventoryDTO) throws Exception;

  /**
   * 供数据初始化 初始化单据 和以后 升级 店铺使用
   * @param shopId
   * @throws Exception
   */
  public void initDefaultStoreHouse(Long shopId) throws Exception;
}
