package com.bcgogo.txn.service.productThrough;

import com.bcgogo.common.Pager;
import com.bcgogo.enums.OrderStatus;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.enums.OutStorageSupplierType;
import com.bcgogo.enums.YesNo;
import com.bcgogo.product.dto.ProductLocalInfoDTO;
import com.bcgogo.search.dto.ItemIndexDTO;
import com.bcgogo.txn.dto.*;
import com.bcgogo.txn.model.OutStorageRelation;
import com.bcgogo.txn.model.SupplierInventory;
import com.bcgogo.txn.model.TxnWriter;
import com.bcgogo.user.dto.SupplierDTO;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 商品出入库打通专用接口
 * Created by IntelliJ IDEA.
 * User: lw
 * Date: 13-4-12
 * Time: 上午9:33
 * To change this template use File | Settings | File Templates.
 */
public interface IProductThroughService {

  //产品供应商打通数据初始化
  void initSupplierInventory() throws Exception;

  //产品供应商打通数据初始化
  void initProductThrough(Long shopId) throws Exception;

  /**
   * 适用于 saveOrUpdateSupplierInventory，saveOrUpdateSupplierInventoryByModify
   * supplierInventoryDTO，必须字段：supplierId，storehouseId，productId，shopId，supplierType,unit
   * 需要更新剩余库存量的在  changeAmount 传值+为增加，-为减少
   * 需要更新累计入库总量的 在totalInStorageChangeAmount 传值
   * 需要更新平均价的   changeAmount 传值，LastStorageAmount传值（只能为正数），
   * 需要更新最后入库信息的，传入 LastStorageTime,LastPurchaseInventoryOrderId,LastStorageAmount,LastPurchasePrice
   * 需要更新最好入库价，最低入库价的 传入LastPurchasePrice
   * @param writer
   * @param supplierInventoryDTOs
   * @throws Exception
   */
  //批量更新供应商库存 外部事务，通过单据组装的SupplierInventoryDTO ，封装changeAmount等字段
  void saveOrUpdateSupplierInventory(TxnWriter writer, Collection<SupplierInventoryDTO> supplierInventoryDTOs) throws Exception;

  //批量更新供应商库存 内部事务  通过单据组装的SupplierInventoryDTO 封装changeAmount等字段
  void saveOrUpdateSupplierInventory(Collection<SupplierInventoryDTO> supplierInventoryDTOs)throws Exception;

  //将要更新的字段直接组装好，进行更新，不要更新的字段也要赋原值,有Id的就更新，没有的新增
  void saveOrUpdateSupplierInventoryByModify(TxnWriter writer,Collection<SupplierInventoryDTO> supplierInventoryDTOs)throws Exception;

    //将要更新的字段直接组装好，进行更新，不要更新的字段也要赋原值,有Id的就更新，没有的新增
  void saveOrUpdateSupplierInventoryByModify(Collection<SupplierInventoryDTO> supplierInventoryDTOs)throws Exception;

  //批量获得供应商库存用于更新   key 是productId
  Map<Long, SupplierInventory> getSupplierInventoryMap(Long shopId, Long supplierId, Long storehouseId, Set<Long> productIds);
    //批量获得供应商库存   key 是productId
  Map<Long, SupplierInventoryDTO> getSupplierInventoryDTOMap(Long shopId, Long supplierId, Long storehouseId, Set<Long> productIds);

   //批量获得供应商库存
  List<SupplierInventory> getSupplierInventoryList(Long shopId, Long supplierId, Long storehouseId, Set<Long> productIds);

  //找到一组产品公共的供应商
  List<SupplierDTO> getCommonSupplierByProductIds(Long shopId, Long[] productIds);

    //获得supplierInventory 多个仓库的合并成一条记录 不包含仓库信息
  Map<Long,List<SupplierInventoryDTO>> getSupplierInventoryMap(Long shopId, Set<Long> productIdSet);

  Map<Long,List<SupplierInventoryDTO>> getSimpleSupplierInventoryMap(Long shopId, Set<Long> productIdSet);

  /**
   * 获取供应商所有库存
   *
   * @param shopId
   * @param supplierId
   * @return
   */
  List<SupplierInventory> getSupplierAllInventory(Long shopId, Long supplierId);




  /**
   * 根据商品获取供应商列表
   * @param condition
   * @return
   */
   List<SupplierInventory> getSupplierInventory(SupplierInventoryDTO condition);

  /**
   * 库存增加时选择供应商逻辑，该商品所有供应商列出来，本仓库的赋值帐面库存，如果一个供应商都查不到自动组装一个系统默认供应商上去
   * @param shopId
   * @param productId
   * @param storehouseId
   * @return
   */
  List<SupplierInventoryDTO> getSupplierInventoryDTOsWithOtherStorehouse(Long shopId, Long productId, Long storehouseId) throws Exception;

  Map<Long,List<SupplierInventoryDTO>> getSupplierInventoryDTOsWithOtherStorehouseMap(Long shopId,Set<Long> productIds,Long storehouseId );
  /**
   * 更改仓库，批量查询供应商库存
   * key productId
   * @param condition
   * @return
   */
  Map<Long,List<SupplierInventoryDTO>> getSupplierInventoryDTOByStorehouse(SupplierInventoryDTO condition);

  /**
   * 根据productId,unit amount 更新item的剩余库存
   *
   * @param txnWriter
   * @param itemIndexDTOList
   */
  public void handleProductInventoryAddRecord(OutStorageRelationDTO outStorageRelationDTO, ProductLocalInfoDTO productLocalInfoDTO,Double amount, List<ItemIndexDTO> itemIndexDTOList, TxnWriter txnWriter);


  /**
   * 销售退货单库存单打通
   * @param bcgogoOrderDto
   */
  public Map<Long, BcgogoOrderItemDto> salesReturnProductThrough(BcgogoOrderDto bcgogoOrderDto,OrderTypes orderType) throws Exception;


  /**
   * 根据shopId,单据id和单据类型 itemId productId获得商品出入库关系表
   * @param shopId
   * @param outStorageOrderId
   * @param outStorageOrderType
   * @param outStorageItemId
   * @param productId
   * @return
   */
  public List<OutStorageRelationDTO>  getOutStorageRelation(Long shopId,Long outStorageOrderId ,OrderTypes outStorageOrderType,Long outStorageItemId ,Long productId);


  public List<SupplierInventoryDTO> getSupplierInventoryList(BcgogoOrderItemDto bcgogoOrderItemDto, OutStorageRelationDTO[] outStorageRelationDTOs, SupplierInventoryDTO supplierInventoryDTO);

  public void handleSelectSupplier(OutStorageRelationDTO outStorageRelationDTO, ProductLocalInfoDTO productLocalInfoDTO, BcgogoOrderItemDto bcgogoOrderItemDto, List<ItemIndexDTO> itemIndexDTOList, TxnWriter txnWriter);


  /**
   * 根据shopId,关联的单据id和关联的单据类型 关联的itemId productId获得商品出入库关系表
   * @param shopId
   * @param relatedOrderId
   * @param relatedOrderType
   * @param relatedItemId
   * @param productId
   * @return
   */
  public List<OutStorageRelationDTO>  getOutStorageRelationByRelated(Long shopId,Long relatedOrderId ,OrderTypes relatedOrderType,Long relatedItemId ,Long productId);

  /**
   * 根据shopId,单据id 获得商品出库关系表
   * 根据suplier 去重复
   * @param shopId
   * @param outStorageOrderId
   * @return key:itemId  value:OutList
   */
  Map<Long,List<OutStorageRelationDTO>> getOutStorageRelationMap(Long shopId, Long... outStorageOrderId);


  /**
   * 根据shopId,单据id 获得商品出入库关系表   key为productId
   *
   * @param shopId
   * @param outStorageOrderId
   * @return itemId OutList
   */

  Map<Long,List<OutStorageRelation>> getOutStorageRelationProductMap(Long shopId, Long outStorageOrderId);
  /**
   *根据shopId,单据id 获得商品入库记录表
   * @param shopId
   * @param orderId
   * @return Map  key:itemId  value:InStorageRecordDTOList
   */
  Map<Long,List<InStorageRecordDTO>> getInStorageRecordDTOMapByOrderIds(Long shopId, Long... orderId);

  //根据产品Id查询供应商数量，多仓库合并成一条记录统计
  int countSupplierInventory(Long shopId,Long productId);

 //根据产品Id查询供应商产品信息，多仓库合并成一条记录统计
  List<SupplierInventoryDTO> getSupplierInventoryByPaging(Long shopId,Long productId,Pager pager);

  /**
   *
   * @param shopId
   * @param productId
   * @param supplierIds
   * @return   key 是供应商Id,没有supplierId的用supplierType代替，value 是组装过后的supplierInventoryDTO
   */
  Map<String, SupplierInventoryDTO> getSupplierInventoryMapBySupplierIds(Long shopId, Long productId, Set<Long> supplierIds);

  //根据产品Id,仓库Id，查找该产品所以供应商库存，若该产品没有供应商，则在查询结果中添加。
  List<SupplierInventoryDTO> getSupplierInventoryDTOsByProductIdAndStorehouseId(Long shopId,Long storehouseId,Long... productIds);

  //根据产品id，查找所有的供应商库存，结果不做合并
  List<SupplierInventoryDTO> getSupplierInventoryDTOsByProductId(Long shopId,Long productId);
  /**
   * 只查询 有剩余量 和不被作废的
   * @param shopId
   * @param storehouseId 仓库列表
   * @param productIdSet 产品id列表
   * @param supplierIdSet  供应商列表
   * @return
   */
  public Map<Long, List<InStorageRecordDTO>> getInStorageRecordMap(Long shopId,Long storehouseId, Set<Long> productIdSet, Set<Long> supplierIdSet,Set<OutStorageSupplierType> outStorageSupplierTypes,boolean containSupplierIdEmpty) ;

  void sortSupplierInventoryDTOsByLastPurchaseTime(List<SupplierInventoryDTO> supplierInventoryDTOs);

  Map<Long, SupplierInventory> getSupplierInventoryMapByIds(Set<Long> supplierInventoryIds);

  /**
   *
   * @param shopId
   * @param purchaseInventoryDTOId
   * @return   key 是productLocalInfoId
   */
  Map<Long,SupplierInventory> getSupplierInventoryMapByPurchaseInventoryId(Long shopId, Long purchaseInventoryDTOId);

  /**
   * 更新supplierInventory状态
   * @param shopId
   * @param supplierId
   * @param isDisabled
   */
  void updateSupplierInventoryStatusBySupplierId(Long shopId, Long supplierId, YesNo isDisabled)throws Exception;

  /**
   * 更新supplierInventory状态
   * @param shopId
   * @param productId
   * @param isDisabled
   */

  void updateSupplierInventoryStatusByProductId(Long shopId, Long productId, YesNo isDisabled) throws Exception;

  /**
   * 生成入库退货的出库关系
   * @param orderDto
   * @param supplierId
   */
  void  generatePurchaseReturnOutStorageRelation(BcgogoOrderDto orderDto,Long supplierId);

  /**
   *  在线退货，先作废，在入库
   * @param purchaseReturnDTO
   * @param originPurchaseReturnDTO
   * @param writer
   */
  void productThroughByOrderForUpdateOnlineReturn(PurchaseReturnDTO purchaseReturnDTO,PurchaseReturnDTO originPurchaseReturnDTO,TxnWriter writer);




}