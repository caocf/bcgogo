package com.bcgogo.search.service;

import com.bcgogo.common.Pager;
import com.bcgogo.common.Sort;
import com.bcgogo.enums.OrderStatus;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.exception.BcgogoException;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.product.dto.ProductSupplierDTO;
import com.bcgogo.search.dto.*;
import com.bcgogo.search.model.InventorySearchIndex;
import com.bcgogo.search.model.ItemIndex;
import com.bcgogo.search.model.OrderIndex;
import com.bcgogo.txn.dto.*;
import com.bcgogo.user.merge.MergeCustomerSnap;
import com.bcgogo.user.merge.MergeResult;
import com.bcgogo.user.merge.MergeSupplierSnap;
import com.bcgogo.user.dto.CustomerDTO;
import com.bcgogo.user.dto.PurchaseInventoryHistoryDTO;
import com.bcgogo.user.dto.PurchaseOrderNotInventoriedInfoDTO;
import com.bcgogo.user.dto.SupplierDTO;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.springframework.ui.ModelMap;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Caiwei
 * Date: 9/25/11
 * Time: 5:48 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ISearchService {

  public List<InventorySearchIndex> searchInventorySearchIndexByProductIds(Long shopId, Long[] productIds);

  public void saveOrUpdateInventorySearchIndexByUpdateInfo(Long shopId, Long productLocalInfoId, Integer productVehicleStatus,
                                                           String productName, String productBrand, String productSpec,
                                                           String productModel);

  public void updateInventorySearchIndexByUpdateInfo(Long productLocalInfoId, Double recommendedPrice);

  public void deleteByQuery(String q, String core);

  public List<ProductDTO> queryProduct(String q, Long shopId, int start, int rows) throws Exception;

  public List<ProductDTO> queryProducts(String q, String field, String productName, String productBrand,
                                        String productSpec, String productModel,
                                        String pvBrand, String pvModel, String pvYear, String pvEngine,
                                        Long pvBrandId, Long pvModelId, Long pvYearId, Long pvEngineId,
                                        Long shopId, boolean includeBasic, String sortStatus,
                                        Integer start, Integer rows) throws Exception;

  public void countSolrSearchService(ProductDTO productDTO, MemcacheInventorySumDTO memcacheInventorySumDTO) throws Exception;

  public List<String> queryProductSuggestionList(String q, String field, String productName, String productBrand,
                                                 String productSpec, String productModel,
                                                 String pvBrand, String pvModel, String pvYear, String pvEngine,
                                                 Long pvBrandId, Long pvModelId, Long pvYearId, Long pvEngineId,
                                                 Long shopId, boolean includeBasic,
                                                 Integer start, Integer rows) throws Exception;

  public List<String> getProductSuggestionList(SearchConditionDTO searchConditionDTO) throws Exception;

  public QueryResponse queryProductByKeywords(Long shopId, String q, int start, int rows) throws Exception;

  public List<String> queryTermsForProduct(String q, String field) throws Exception;

  public QueryResponse queryProductByQueryString(String q, int rows) throws Exception;


  /**
   * 查询产品属性
   */

  public Map<Boolean, Object> getVehicleIdsByKeywords(String brand, String model, String year, String engine, Boolean isOnlyOne) throws Exception;

  public void addItemIndex(ItemIndex item);

  public void addItemIndexList(List<ItemIndex> itemIndexList);

  public void addOrUpdateItemIndex(ItemIndex item);

  public void addOrUpdateItemIndexWithList(List<ItemIndex> itemList, List<ItemIndex> itemIndexToDeleteList);

  public void updateItemIndexSupplier(SupplierDTO supplierDTO);

  public void addOrUpdateInventorySearchIndexWithList(List<InventorySearchIndex> itemList);

  public void batchAddOrUpdateInventorySearchIndexWithList(Long shopId, List<InventorySearchIndex> inventorySearchIndexes)throws Exception;

  public void updateInventorySearchIndexAmountWithList(List<InventorySearchIndex> itemList);

  //统计店铺库存信息，double[0] 种类，double[1]数量，double[2] 总金额
  public MemcacheInventorySumDTO countInventoryInfoByShopId(Long shopId) throws Exception;

  public List<ItemIndex> searchItemIndex(ItemIndexDTO dto, Long fromTime, Long toTime, Integer startNo, Integer maxResult);

  public List<InventorySearchIndexDTO> searchInventorySearchIndex(Long shopId, String productName,
                                                                  String productBrand, String productSpec, String productModel,
                                                                  String pvBrand, String pvModel, String pvYear, String pvEngine,
                                                                  Integer startNo, Integer maxResult, Boolean inventoryFlag);

  public Long searchInventorySearchIndexCount(Long shopId, String productName, String productBrand,
                                              String productSpec, String productModel, String pvBrand, String pvModel,
                                              String pvYear, String pvEngine, Boolean inventoryFlag);

  public Long searchInventorySearchIndexCountForVehicle(Long shopId, String productName, String productBrand,
                                                        String productSpec, String productModel, String pvBrand, String pvModel,
                                                        String pvYear, String pvEngine);

  public Long searchInventorySearchIndexCountForOneVehicle(Long shopId, String productName, String productBrand,
                                                           String productSpec, String productModel, String pvBrand, String pvModel,
                                                           String pvYear, String pvEngine);

  public PurchaseOrderNotInventoriedInfoDTO getPurchaseOrderNotInventoried(Long shopId, long supplierId, int rowStart, int rowEnd) throws BcgogoException;

  public PurchaseInventoryHistoryDTO getPurchaseInventoryHistory(Long shopId, long supplierId, String starttimeStr, String endtimeStr, int rowStart, int rowEnd) throws BcgogoException, ParseException;

  public int getPurchaseInventoryHistoryItemIndexSize(Long shopId, long supplierId, String starttimeStr, String endtimeStr) throws BcgogoException, ParseException;
  /**
   * 查询客户消费记录
   *
   * @param customerId
   * @param shopId
   * @return
   * @throws BcgogoException
   */
  public CustomerConsumeDTO findConsumeHistory(Long customerId, Long shopId, Sort sort) throws BcgogoException;

  public CustomerConsumeDTO findConsumeHistory(Long customerId, Long shopId, Sort sort, int currentPage, int pageSize) throws BcgogoException;

  public List<OrderDTO> getConsumeOrderHistory(Long customerId, Long shopId, Long startTime, Long endTime, List<OrderTypes> orderTypes, Pager pager) throws BcgogoException;

  public int countReturn(Long shopId, Long customerOrSupplierId, OrderTypes orderType, OrderStatus orderStatus) throws BcgogoException;

  public int countConsumeHistory(Long customerId, Long shopId, Long startTime, Long endTime, List<OrderTypes> orderTypes) throws BcgogoException;

  public int countConsumeHistory(Long customerId, Long shopId) throws BcgogoException;

  public int countCarHistory(Long shopId, String vehicle, Long startTiem, Long endTime) throws BcgogoException;

  @Deprecated
  public int countRepairOrderHistory(long shopId, String licenceNo, String services, String materialName,
                                     Long fromTime, Long toTime);

  public int countGoodsHistory(ItemIndexDTO itemIndexDTO, Long startTime, Long endTime) throws BcgogoException;

  public InventorySearchIndex exactSearchInventorySearchIndex(SearchConditionDTO searchConditionDTO);

  String getOrderNamesByOrderId(Long shopId, Long orderId);

  public List getInventorySearchIndexByShopId(Long shopId, Long start, int num) throws Exception;

  public InventorySearchIndex getInventorySearchIndexByProductId(Long productId) throws Exception;

  public void saveOrUpdateOrderIndex(OrderIndexDTO orderIndexDTO);

  public void updateOrderIndex(OrderIndexDTO OrderIndexDTO);

  public void updateOrderIndexSupplier(SupplierDTO supplierDTO);

  public List<OrderIndex> searchOrderIndexByShopIdAndCustomerOrSupplierId(Long shopId, Long supplierId);

  public List<ItemIndex> searchReturnAbleProducts(ItemIndexDTO itemIndexDTO, OrderTypes type) throws Exception;

  public List<ItemIndex> searchReturnTotal(ItemIndexDTO itemIndexDTO, Integer startNo) throws Exception;

  public InventorySearchIndex searchInventorySearchIndexAmount(Long shopId, String productName, String productBrand,
                                                               String productSpec, String productModel, String pvBrand, String pvModel,
                                                               String pvYear, String pvEngine,String commodityCode);

  public List<OrderIndex> getOrderIndexByOrderId(Long shopId, Long orderId, OrderTypes orderType, OrderStatus orderStatus, Long customerOrSupplierId);

  public List<ItemIndexDTO> getWashOrderItemIndexList(long shopId, long startTime, long endTime, int pageNo, int PageSize, String arrayType);

  public List<ItemIndexDTO> getSalesOrderItemIndexList(long shopId, String idString, String arrayType);

  public List<ItemIndexDTO> getRepairOrderItemIndexList(long shopId, String orderIdString, String arrayType);

  public List<ItemIndexDTO> getWashItemIndexList(long shopId, long startTime, long endTime);

  public void cancelPurchaseInventoryInSearchDB(Long shopId, PurchaseInventoryDTO purchaseInventoryDTO,
                                                List<InventorySearchIndex> inventorySearchIndexList,
                                                PurchaseOrderDTO purchaseOrderDTO) throws Exception;

  public void updateOrderIndex(Long shopId, Long orderId, OrderTypes orderType, OrderStatus orderStatus) throws Exception;


  public ItemIndex getItemIndexByOrderIdAndItemIdAndOrderType(Long orderId, Long orderItemId, OrderTypes orderType);


  public InventorySearchIndexDTO getInventorySearchIndexById(Long shopId, Long productLocalInfoId) throws Exception;

  //批量更新productInfo的库存告急上下限
  public void updateInventorySearchIndexLimit(InventoryLimitDTO inventoryLimitDTO) throws Exception;

  //单个更新product的库存告急上下限
  public void updateSingelInventoryInventorySearchIndexLimit(Long productId, Double lowerLimitVal, Double upperLimitVal,
                                                             Long shopId) throws Exception;

  //进销存首页分页，带条件排列查询库存告急信息
  public List<InventorySearchIndexDTO> getInventorySearchIndexDTOLimit(Long shopId, Pager pager, String searchConditionStr, String sortStr) throws Exception;

  //营业统计获取查询时间范围内的总条数和总金额
  public  List<String> getWashItemTotal(long shopId, long startTime, long endTime);

  public double getWashTotalByCustomerId(long shopId, long customerId);

  public List<ItemIndexDTO> getItemIndexDTOListByOrderId(long shopId, long orderId);

  public List<ItemIndex> getItemIndexesByOrderId(Long shopId, Long orderId);

  public List<OrderIndexDTO> getOrderIndexDTOByOrderId(Long shopId, Long orderId);

  public void updatePurchaseOrderIndexStatus(long shopId, long purchaseOrderId, OrderStatus orderStatus);

  public void updateItemIndexName(ItemIndexDTO itemIndexDTO);


  public long countNullProductIDItemIndex();

  public List<ItemIndex> getNullProductIDItemIndexs(Pager pager);

  //更新itemindex中的productId，将更新结果，异常保存在model，list中
  public void updateItemIndex(ItemIndex itemIndex, ModelMap model, List<Long> itemDTOItemIndexFailUpdate);

  //itemindexDTO 封装了查询条件,根据供应商,商品的四个属性来查找
  public List<ItemIndexDTO> getItemIndexDTO(Long shopId, ItemIndexDTO itemIndexDTO, Pager pager) throws Exception;

  //统计itemindexDTO 封装了查询条件,根据供应商,商品的四个属性来查找的符合条件的总数
  public Long countItemIndexWithItemIndexDTO(Long shopId, ItemIndexDTO itemIndexDTO) throws Exception;

  //根据supplierId,productId 查询入库单,退货单明细,如果orderIndexDTOs中有数据,则和orderIndexDTOs匹配,
  // 获得该订单状态,如果没有,则查询orderIndex表,并将查询结果放入orderIndexDTOs中
  public List<ItemIndexDTO> getPurchaseReturnItemIndexDTOs(Long shopId, Long supplierId, Long productId,
                                                           List<OrderIndexDTO> orderIndexDTOs) throws Exception;

  public void updateInventorySearchIndex(InventorySearchIndexDTO inventorySearchIndexDTO) throws Exception;

  public void saveOrderIndexAndItemIndexOfMemberCardOrder(MemberCardOrderDTO memberCardOrderDTO) throws Exception;


  //todo zhangchuanlong
  public  int countRepairOrderHistoryByToDayNewVehicle(Long shopId, String vehicle, String services, String itemName, Long endDateLong, Long endDateLong2, List<String> licenceNoList);
  //todo zhangchuanlong
  public List<ItemIndexDTO> getRepairOrderHistoryByTodayNewCustomer(Long shopId, String vehicle, String services, String itemName, Long startDateTIme, Long endDateTime, Pager pager, List<String> licenceNoList) throws BcgogoException, InvocationTargetException, IllegalAccessException;

  //从itemindex中获取productSupplierDTO用于初始化
  @Deprecated
  public List<ProductSupplierDTO> getProductSupplierDTO(Long shopId,Long startProductId,Long endProductId);

  //从itemindex中获取rows之后的一个productId
  @Deprecated
  public Long getItemIndexNextProductIdWithSupplier(Long shopId,Long startProductId,int rows);

  public void updateSaleOrderIndexReceiptNo(List<InitReceiptNoOrderIndexDTO> initReceiptNoOrderIndexDTOs);


  /**
   * 根据shopId获得老洗车单的 非会员洗车和洗车充值 用于流水统计初始化
   * @param shopId
   * @return
   */
  public long countWashItemIndexByShopId(long shopId);

  Integer countInventoryLowerLimitAmount(Long shopId);

  Integer countInventoryUpperLimitAmount(Long shopId);

  List<InventorySearchIndexDTO> getInventorySearchIndexDTOsByProductIds(Long shopId,Long ...productIds);

  Map<Long,InventorySearchIndex> getInventorySearchIndexMapByProductIds(Long shopId,Long ...productIds);

  void updateOrderIndexAfterRepealWashOrder(WashBeautyOrderDTO washBeautyOrderDTO);

  public void updateInventorySearchIndexKindInfo(Long shopId,String oldKindName, String kindName);

  public void updateMultipleInventoryKind(Long shopId, Long[] inventorySearchIndexIdList, String newKindName);

  public Long[] getInventorySearchIndexIdListByProductKind(Long shopId, String kindName);

  public void deleteMultipleInventoryKind(Long shopId, String kindName);

  public void updateMultipleInventorySearchIndexRecommendedPrice(ProductDTO[] productDTOs,Long shopId ) throws Exception;

  void getLimitSearchCount(Long shopId, String searchConditionStr, ProductSearchResultListDTO productSearchResultListDTO);

  String mergeCustomerOrderIndex(MergeResult<CustomerDTO,MergeCustomerSnap> result,Long[] childIds) throws BcgogoException;

  String mergeSupplierOrderIndex(MergeResult<SupplierDTO,MergeSupplierSnap> result,Long[] childIds) throws BcgogoException;

  void updateSearchOrderStatus(SalesOrderDTO salesOrderDTO, PurchaseOrderDTO purchaseOrderDTO) throws Exception;

  boolean isProductInUse(Long productId, Long shopId);

  boolean isServiceInUse(Long serviceId, Long shopId);

  void deleteItemIndex(Long shopId, Long orderId);

  int countRepairOrderInOrderIndex(Long shopId, Long fromTime, Long toTime);
}


