package com.bcgogo.txn.service;

import com.bcgogo.common.Result;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.search.dto.InventorySearchIndexDTO;
import com.bcgogo.search.model.InventorySearchIndex;
import com.bcgogo.txn.dto.*;
import com.bcgogo.txn.model.Inventory;
import com.bcgogo.txn.model.TxnWriter;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: QiuXinyu
 * Date: 12-6-18
 * Time: 上午10:08
 * To change this template use File | Settings | File Templates.
 */
public interface IInventoryService {

  //进销存首页,商品首页，查询库存总量，金额总量
  public MemcacheInventorySumDTO getInventorySum(Long shopId) throws Exception;

  //根据商品名查询，查询库存总量，金额总量
  public MemcacheInventorySumDTO getSearchProductNameInventoryCount(Long shopId, String productName, MemcacheInventorySumDTO memcacheInventorySumDTO) throws Exception;

  //更新入库单中设定上下限
  public void saveOrUpdateInventoryLimit(PurchaseInventoryDTO purchaseInventoryDTO) throws Exception;

  //给productInfoDTOs添加库存上下限信息，
  public void getLimitAndAchievementForProductDTOs(List<ProductDTO> productDTOs ,Long shopId) throws Exception;

  //批量更新productInfo的库存告急上下限
  public void updateInventoryLimit(InventoryLimitDTO inventoryLimitDTO) throws Exception;

  //单个更新product的库存告急上下限
  public void updateSingelInventoryLimit(Long productId, Double lowerLimitVal, Double upperLimitVal, Long shopId,
                                         InventoryLimitDTO inventoryLimitDTO) throws Exception;


  public InventoryDTO updateInventoryInfo(Long shopId,InventoryDTO inventoryDTO,InventoryLimitDTO inventoryLimitDTO) throws Exception;
  //优先从memcache中获取memcacheLimit数据
  public MemcacheLimitDTO getMemcacheLimitDTO(Long shopId) throws Exception;

  //从db中count库存告急的数量并保持到memcache中去
  public MemcacheLimitDTO saveMemcacheLimitDTOFromDB(Long shopId) throws Exception;

  //用invenoryLimitDTO更新memcacheLimit，如果inventoryLimitDTO中数据异常，则从数据库中count
  public MemcacheLimitDTO updateMemocacheLimitByInventoryLimitDTO(Long shopId, InventoryLimitDTO inventoryLimitDTO) throws Exception;

  //计算一件商品库存处理前是否超出预警
  public void caculateBeforeLimit(InventoryDTO inventoryDTO, InventoryLimitDTO inventoryLimitDTO);

  //计算一件商品库存处理后是否超出预警
  public void caculateAfterLimit(InventoryDTO inventoryDTO, InventoryLimitDTO inventoryLimitDTO);

  //计算一件商品库存处理前是否超出预警
  public void caculateBeforeLimit(InventorySearchIndexDTO inventoryDTO, InventoryLimitDTO inventoryLimitDTO);

  //计算一件商品库存处理后是否超出预警
  public void caculateAfterLimit(InventorySearchIndexDTO inventoryDTO, InventoryLimitDTO inventoryLimitDTO);

  /**
   * 入库后保存一张应付款表
   *
   * @param purchaseInventoryDTO
   * @return
   * @author zhangchuanlong
   */
  public  PayableDTO savePayableFromPurchaseInventoryDTO(PurchaseInventoryDTO purchaseInventoryDTO);

  /**
   * 查询退货款
   * @param shopId
   * @param purchaseInventoryId
   */
  public Map<Long, PayableDTO> getPayableDTOByPurchaseInventoryId(Long shopId,Long... purchaseInventoryId);

  /**
   * 计算库存平均价
   * @param formerInventoryAveragePrice 系统中的库存平均价
   * @param formerInventoryAmount  系统中的库存量
   * @param currentInventoryPrice  当前入库价
   * @param currentInventoryAmount  当前入库量
   * @return
   */
  public double calculateInventoryAveragePrice(double formerInventoryAveragePrice,double formerInventoryAmount,double currentInventoryPrice,double currentInventoryAmount);

  /**
   * 判断一件商品库存是否为0   ，是则返回true。   小于0.001，或者为负均认为true；
   * @param shopId
   * @param productId
   * @return
   * @throws Exception
   */
  public boolean isInventoryEmpty(Long shopId,Long productId)throws Exception;

  public Map<Long,InventoryDTO> getInventoryDTOMap(Long shopId,Set<Long> productIds);

  public Map<Long,Inventory> getInventoryMap(Long shopId,Set<Long> productIds);

  public InventoryDTO getInventoryDTOByProductId(Long productId);
  @Deprecated
  public boolean updateSaleOrderLack(Long shopId,Long firstSalesOrderId,Long... productIds)throws Exception;
  @Deprecated
  public boolean updateSaleOrderLackByStoreHouse(Long shopId,Long storehouseId,Long firstSalesOrderId,Long... productIds)throws Exception;
  //销售单扣库存处理
  boolean updateSaleOrderLackBySaleOrderId(TxnWriter writer,SalesOrderDTO salesOrderDTO, List<InventorySearchIndex> inventorySearchIndexes) throws Exception;

  //销售单扣库存处理
  boolean updateSaleOrderLackBySaleOrderIdAndStorehouse(TxnWriter writer, SalesOrderDTO salesOrderDTO, List<InventorySearchIndex> inventorySearchIndexes) throws Exception;


  /**
   *
   * @param shopId
   * @param start
   * @param rows
   * @return
   */
  public List<InventoryDTO> getInventoryDTOsByShopId(Long shopId,int start,int rows);

  public boolean checkBatchProductInventory(Long shopId,BcgogoOrderDto bcgogoOrderDto,Map<String, String> data, List<Long> productIdList) throws Exception;

  public boolean checkBatchProductInventoryInOtherStorehouse(Long shopId,BcgogoOrderDto bcgogoOrderDto, List<Long> productIdList) throws Exception;

  public boolean checkBatchProductInventoryByStoreHouse(Long shopId,Long storehouseId,BcgogoOrderItemDto[] bcgogoOrderItemDtos,Map<String, String> data, List<Long> productIdList) throws Exception;

  public boolean RFCheckBatchProductInventoryByStoreHouse(Long shopId,Long storehouseId,Long originalStorehouseId,BcgogoOrderItemDto[] bcgogoOrderItemDtos,Map<String, String> data, List<Long> productIdList) throws Exception;

  public void synInventoryWithStoreHouse(Long shopId,Long productLocalInfoId) throws Exception;

  List<InventoryDTO> getInventoryDTOById(Long shopId,Long ...productLocalInfoIds);

  /**
   * 根据仓库重新初始化  每个item的 库存和仓位信息
   * @param shopId
   * @param storehouseId
   * @param bcgogoOrderDto
   * @throws Exception
   */
  void updateItemDTOInventoryAmountByStorehouse(Long shopId,Long storehouseId, BcgogoOrderDto bcgogoOrderDto) throws Exception;

  public void updateInventorySearchIndexByProductId(Long shopId, Long productLocalInfoId, Double recommendedPrice) throws Exception;

  Result updateInventory(Result result,InventoryDTO inventoryDTO);

  public void addOrUpdateInventorySearchIndexWithList(Long shopId, List<InventorySearchIndex> itemList) throws Exception;

  public void updateInventorySearchIndexAmountWithList(Long shopId, List<InventorySearchIndex> itemList) throws Exception;

  boolean checkProductTradePriceAndInventoryAveragePriceByProductLocalInfoId(Long shopId,Long... productLocalInfoIds)throws Exception;

  //本店产品数量
  int countProductInventory(Long id);

  boolean checkBatchPurchaseInventoryInventory(Long shopId, BcgogoOrderDto bcgogoOrderDto, Map<String, String> data, List<Long> productIdList) throws Exception;

  //草稿单show的时候，更新仓库信息
  void updateDraftItemDTOInventoryAmountByStorehouse(Long shopId, Long storehouseId, BcgogoOrderDto bcgogoOrderDto) throws Exception;

  Map<Long, InventoryDTO> getInventoryDTOMapByProductIds(Long... productIds);
}