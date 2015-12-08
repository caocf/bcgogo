package com.bcgogo.search.model;

import com.bcgogo.common.Pager;
import com.bcgogo.common.Sort;
import com.bcgogo.enums.ItemTypes;
import com.bcgogo.enums.OrderStatus;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.exception.BcgogoException;
import com.bcgogo.product.dto.ProductSupplierDTO;
import com.bcgogo.search.dto.ItemIndexDTO;
import com.bcgogo.search.dto.OrderIndexDTO;
import com.bcgogo.search.dto.SearchConditionDTO;
import com.bcgogo.search.dto.SearchMemoryConditionDTO;
import com.bcgogo.service.GenericWriterDao;
import com.bcgogo.utils.ServiceUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.transaction.support.ResourceTransactionManager;

import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: caiweili
 * Date: 12/31/11
 * Time: 12:06 PM
 * To change this template use File | Settings | File Templates.
 */
public class SearchWriter extends GenericWriterDao {

  public SearchWriter(ResourceTransactionManager transactionManager) {
    super(transactionManager);
  }

  public int countReturn(Long shopId, Long customerOrSupplierId, OrderTypes orderType, OrderStatus orderStatus) {
    Session session = this.getSession();
    try {
      Query query = SQL.countReturn(session, shopId, customerOrSupplierId, orderType, orderStatus);
      if (query.uniqueResult() == null) {
        return 0;
      }
      return ((Long) query.uniqueResult()).intValue();
    } finally {
      release(session);
    }
  }

  public int countCarHistory(Long shopId, String vehicle, Long startTime, Long endTime) {
    Session session = this.getSession();

    try {
      Query q = SQL.countCarHistory(session, shopId, vehicle, startTime, endTime);
      if (q.uniqueResult() == null) {
        return 0;
      }
      return ((Long) q.uniqueResult()).intValue();
    } finally {
      release(session);
    }
  }

  public int  countGoodsHistory(ItemIndexDTO dto,Long startTime,Long endTime)
  {
    Session session = this.getSession();

    try {
      Query q = SQL.countGoodsHistory(session,
          dto.getShopId(), dto.getCustomerOrSupplierName(), dto.getSelectedOrderTypes(), dto.getItemName(), dto.getItemBrand(), dto.getItemSpec(),
          dto.getItemModel(), startTime, endTime);
      if (q.uniqueResult() == null) {
        return 0;
      }
      return ((Long) q.uniqueResult()).intValue();
    } finally {
      release(session);
    }
  }

  public List<ItemIndex> searchItemIndex(ItemIndexDTO dto,
                                         Long fromTime,
                                         Long toTime,
                                         Integer startNo,
                                         Integer maxResult) {

    Session session = this.getSession();

    try {
      Query q = SQL.searchItemIndex(session,
          dto.getShopId(),
          dto.getCustomerId(),
          dto.getVehicle(),
          dto.getOrderId(),
          dto.getSelectedOrderTypes(),
          dto.getItemId(),
          dto.getItemType(),
          dto.getCustomerOrSupplierName(),
          dto.getItemName(),
          dto.getItemBrand(),
          dto.getItemSpec(),
          dto.getItemModel(),
          dto.getOrderStatus(),
          fromTime,
          toTime,
          startNo,
          maxResult);

      return (List<ItemIndex>) q.list();
    } finally {
      release(session);
    }
  }

  // 欠款金额 时间更新
  public void updateItemIndexByOrderId(long orderId, Double arrears, Long paymentTime) {
    Session session = this.getSession();
    try {
      Query query = SQL.updateItemIndexByOrderId(session, orderId, arrears, paymentTime);
      query.executeUpdate();
    } finally {
      release(session);
    }
  }

  public int countInventoryNumberByShopIdAndProductName(Long shopId, String productName) throws Exception {
    Session session = getSession();
    try {
      Query q = SQL.countInventoryNumberByShopIdAndProductName(session, shopId, productName);
      return ((Long) q.uniqueResult()).intValue();
    } finally {
      release(session);
    }
  }

  public Integer countInventoryNumberByShopId(Long shopId) throws Exception {
    Session session = getSession();
    try {
      Query q = SQL.countInventoryNumberByShopId(session, shopId);
      if (q.uniqueResult() != null) {
        return Integer.parseInt(q.uniqueResult().toString());
      } else {
        return 0;
      }
    } finally {
      release(session);
    }
  }

  public Double countInventorySumByShopId(Long shopId) throws Exception {
    Session session = this.getSession();
    try {
      Query query = SQL.countInventorySumByShopId(session, shopId);
      if (query.uniqueResult() != null) {
        return Double.parseDouble(query.uniqueResult().toString());
      } else {
        return 0D;
      }
    } finally {
      release(session);
    }
  }

  public List<InventorySearchIndex> searchInventorySearchIndexByProductIds(Long shopId, Long[] productIds) {
    Session session = this.getSession();
    try {
      Query query = SQL.searchInventorySearchIndexByProductIds(session, shopId, productIds);
      return (List<InventorySearchIndex>) query.list();
    } finally {
      release(session);
    }
  }

  public List<InventorySearchIndex> searchInventorySearchIndex(Long shopId, String productName,
                                                               String productBrand, String productSpec, String productModel,
                                                               String pvBrand, String pvModel, String pvYear, String pvEngine,
                                                               Integer startNo, Integer maxResult, Boolean inventoryFlag) {
    Session session = this.getSession();
    try {
      Query query = SQL.searchInventorySearchIndex(session, shopId, productName, productBrand, productSpec, productModel, pvBrand,
          pvModel, pvYear, pvEngine, startNo, maxResult, inventoryFlag);
      return (List<InventorySearchIndex>) query.list();
    } finally {
      release(session);
    }
  }

  public Long searchInventorySearchIndexCount(Long shopId, String productName, String productBrand,
                                              String productSpec, String productModel, String pvBrand, String pvModel,
                                              String pvYear, String pvEngine, Boolean inventoryFlag) {
    Session session = this.getSession();
    try {
      Query query = SQL.searchInventorySearchIndexCount(session, shopId, productName, productBrand, productSpec, productModel
          , pvBrand, pvModel, pvYear, pvEngine, inventoryFlag);
      return (Long) query.uniqueResult();
    } finally {
      release(session);
    }
  }

  public Long searchInventorySearchIndexCountForVehicle(Long shopId, String productName, String productBrand,
                                                        String productSpec, String productModel, String pvBrand, String pvModel,
                                                        String pvYear, String pvEngine) {
    Session session = this.getSession();
    try {
      Query query = SQL.searchInventorySearchIndexCountForVehicle(session, shopId, productName, productBrand, productSpec, productModel
          , pvBrand, pvModel, pvYear, pvEngine);
      return (Long) query.uniqueResult();
    } finally {
      release(session);
    }
  }

  public Long searchInventorySearchIndexCountForOneVehicle(Long shopId, String productName, String productBrand,
                                                           String productSpec, String productModel, String pvBrand, String pvModel,
                                                           String pvYear, String pvEngine) {
    Session session = this.getSession();
    try {
      Query query = SQL.searchInventorySearchIndexCountForOneVehicle(session, shopId, productName, productBrand, productSpec, productModel
          , pvBrand, pvModel, pvYear, pvEngine);
      return (Long) query.uniqueResult();
    } finally {
      release(session);
    }
  }

  public List<InventorySearchIndex> searchInventorySearchIndexForVehicle(Long shopId, String productName,
                                                                         String productBrand, String productSpec, String productModel,
                                                                         String pvBrand, String pvModel, String pvYear, String pvEngine,
                                                                         Integer startNo, Integer maxResult) {
    Session session = this.getSession();
    try {
      Query query = SQL.searchInventorySearchIndexForVehicle(session, shopId, productName, productBrand, productSpec, productModel, pvBrand,
          pvModel, pvYear, pvEngine, startNo, maxResult);
      return (List<InventorySearchIndex>) query.list();
    } finally {
      release(session);
    }
  }

  public InventorySearchIndex getInventorySearchIndexByProductLocalInfoId(Long shopId, Long productLocalInfoId) {
    Session session = this.getSession();
    try {
      Query query = SQL.getInventorySearchIndexByProductLocalInfoId(session, shopId, productLocalInfoId);
      return (InventorySearchIndex) query.uniqueResult();
    } finally {
      release(session);
    }
  }

  public List<InventorySearchIndex> getInvenSearchIndexByShopIdAndBarcode(Long shopId, String barcode) {
    Session session = this.getSession();
    try {
      return (List<InventorySearchIndex>) SQL.getInventorySearchIndexByShopIdAndBarcode(session, shopId, barcode).list();
    } finally {
      release(session);
    }
  }

  /**
   * 根据供应商ID获取采购货品信息列表
   *
   * @param supplierId
   * @param shopId
   * @return
   * @throws com.bcgogo.exception.BcgogoException
   *
   */
  public List<ItemIndexDTO> getPurchaseInventoryInfoOfSupplier(Long supplierId, Long shopId, Long dateFrom, Long dateTo) throws BcgogoException {
    Session session = getSession();
    try {
      Query q = null;
      q = SQL.getPurchaseInventoryInfoOfSupplier(session, supplierId, shopId, dateFrom, dateTo);
      List<ItemIndex> itemIndexList = (List<ItemIndex>) q.list();
      List<ItemIndexDTO> itemIndexDTOList = new ArrayList<ItemIndexDTO>();
      if (itemIndexList == null || itemIndexList.isEmpty()) {
        return itemIndexDTOList;
      }
      ItemIndexDTO itemIndexDTO = null;
      for (ItemIndex itemIndex : itemIndexList) {
        itemIndexDTO = new ItemIndexDTO();
        try {
          itemIndexDTO = itemIndex.toDTO();
        } catch (Exception e) {
          throw new BcgogoException(e);
        }
        itemIndexDTOList.add(itemIndexDTO);
      }
      return itemIndexDTOList;
    } finally {
      release(session);
    }
  }

  /**
   * 根据供应商ID获取采购货品信息列表长度
   *
   * @param supplierId
   * @param shopId
   * @return
   * @throws com.bcgogo.exception.BcgogoException
   *
   */
  public int getPurchaseInventoryHistoryItemIndexSize(Long supplierId, Long shopId, Long dateFrom, Long dateTo) throws BcgogoException {
    Session session = getSession();
    try {
      Query q = null;
      q = SQL.getPurchaseInventoryInfoOfSupplier(session, supplierId, shopId, dateFrom, dateTo);
      List<ItemIndex> itemIndexList = (List<ItemIndex>) q.list();
      if (itemIndexList == null || itemIndexList.isEmpty()) {
        return 0;
      }
      return itemIndexList.size();
    } finally {
      release(session);
    }
  }

  /**
   * 获取某个客户的所有消费记录列表
   *
   *
   * @param customerId
   * @param shopId
   * @return
   * @throws com.bcgogo.exception.BcgogoException
   *
   */
  public List<ItemIndexDTO> getConsumeHistoryOfCustomer(Long customerId, Long shopId, Long dateFrom, List<OrderTypes> orderTypes, Sort sort) throws BcgogoException {
    Session session = getSession();
    try {
      Query q = null;
      q = SQL.getConsumeHistoryOfCustomer(session, customerId, shopId, dateFrom, orderTypes, sort);
      List<ItemIndex> itemIndexList = (List<ItemIndex>) q.list();
      List<ItemIndexDTO> itemIndexDTOList = new ArrayList<ItemIndexDTO>();
      if (itemIndexList == null || itemIndexList.isEmpty()) {
        return itemIndexDTOList;
      }
      ItemIndexDTO itemIndexDTO = null;
      for (ItemIndex itemIndex : itemIndexList) {
        itemIndexDTO = new ItemIndexDTO();
        itemIndexDTO.setLastUpdate(itemIndex.getLastModified());
        try {
          itemIndexDTO = itemIndex.toDTO();
        } catch (Exception e) {
          throw new BcgogoException(e);
        }
        itemIndexDTOList.add(itemIndexDTO);
      }
      return itemIndexDTOList;
    } finally {
      release(session);
    }
  }

  public List<ItemIndexDTO> getConsumeHistoryOfCustomer(Long customerId, Long shopId, Long dateFrom, List<OrderTypes> orderTypes, Sort sort,int currentPage,int pageSize) throws BcgogoException {
    Session session = getSession();
    try {
      Query q = null;
      q = SQL.getConsumeHistoryOfCustomer(session, customerId, shopId, dateFrom, orderTypes, sort, currentPage, pageSize);
      List<ItemIndex> itemIndexList = (List<ItemIndex>) q.list();
      List<ItemIndexDTO> itemIndexDTOList = new ArrayList<ItemIndexDTO>();
      if (itemIndexList == null || itemIndexList.isEmpty()) {
        return itemIndexDTOList;
      }
      ItemIndexDTO itemIndexDTO = null;
      for (ItemIndex itemIndex : itemIndexList) {
        itemIndexDTO = new ItemIndexDTO();
        itemIndexDTO.setLastUpdate(itemIndex.getLastModified());
        try {
          itemIndexDTO = itemIndex.toDTO();
        } catch (Exception e) {
          throw new BcgogoException(e);
        }
        itemIndexDTOList.add(itemIndexDTO);
      }
      return itemIndexDTOList;
    } finally {
      release(session);
    }
  }

  public List<ItemIndexDTO> getConsumeHistoryOfCustomer(Long customerId, Long shopId, Long startTime, Long endTime, List<OrderTypes> orderTypes, Pager pager) throws BcgogoException {
    Session session = getSession();
    try {
      Query q = null;
      q = SQL.getConsumeHistoryOfCustomer(session, customerId, shopId, startTime, endTime, orderTypes, pager);
      List<ItemIndex> itemIndexList = (List<ItemIndex>) q.list();
      List<ItemIndexDTO> itemIndexDTOList = new ArrayList<ItemIndexDTO>();
      if (itemIndexList == null || itemIndexList.isEmpty()) {
        return itemIndexDTOList;
      }
      ItemIndexDTO itemIndexDTO = null;
      for (ItemIndex itemIndex : itemIndexList) {
        itemIndexDTO = new ItemIndexDTO();
        itemIndexDTO.setLastUpdate(itemIndex.getLastModified());
        try {
          itemIndexDTO = itemIndex.toDTO();
        } catch (Exception e) {
          throw new BcgogoException(e);
        }
        itemIndexDTOList.add(itemIndexDTO);
      }
      return itemIndexDTOList;
    } finally {
      release(session);
    }
  }

  public int countConsumeHistory(long customerId,long shopId,Long dateForm, List<OrderTypes> orderTypes) throws BcgogoException {
    Session session = this.getSession();

    try {
      Query q = SQL.countConsumeHistory(session, customerId, shopId, dateForm, orderTypes);
      if (q.uniqueResult() == null) {
        return 0;
      }
      return ((Long) q.uniqueResult()).intValue();
    } finally {
      release(session);
    }
  }

  public List<ItemIndexDTO> getPurchaseOrderNotInventoried(Long shopId, Long supplierId, Long dateFrom) throws Exception {
    Session session = getSession();
    try {
      Query q = SQL.getPurchaseOrderNotInventoried(session, shopId, supplierId, dateFrom);
      List<ItemIndex> purchaseOrderNotInventoriedList = (List<ItemIndex>) q.list();
      List<ItemIndexDTO> purchaseOrderNotInventoriedDTOList = new ArrayList<ItemIndexDTO>();
      if (purchaseOrderNotInventoriedList == null || purchaseOrderNotInventoriedList.isEmpty()) {
        return purchaseOrderNotInventoriedDTOList;
      }
      ItemIndexDTO itemIndexDTO = null;
      for (ItemIndex itemIndex : purchaseOrderNotInventoriedList) {
        itemIndexDTO = new ItemIndexDTO();
        itemIndexDTO = itemIndex.toDTO();
        purchaseOrderNotInventoriedDTOList.add(itemIndexDTO);
      }
      return purchaseOrderNotInventoriedDTOList;
    } finally {
      release(session);
    }
  }

  /**
   * 采购单入库之后更新itemIndex 中orderstatus
   *
   *
   * @param shopId
   * @param purchaseOrderId
   * @return
   * @throws com.bcgogo.exception.BcgogoException
   *
   */
  public void updateItemIndexPurchaseOrderStatus(Long shopId, OrderTypes orderType,Long purchaseOrderId, OrderStatus orderStatus) {
    Session session = getSession();
    try {
      Query q = SQL.getUpdateItemIndexPurchaseOrderStatus(session, shopId, orderType, purchaseOrderId, orderStatus);
      q.executeUpdate();
    } finally {
      release(session);
    }
  }

  public InventorySearchIndex exactSearchInventorySearchIndex(SearchConditionDTO searchConditionDTO) {
    Session session = getSession();
    try {
      List list = SQL.exactSearchInventorySearchIndex(session, searchConditionDTO).list();
      if (list!=null && list.size()>0) {
        return (InventorySearchIndex) list.get(0);
      } else {
        return null;
      }
    } finally {
      release(session);
    }
  }

  public List<ItemIndexDTO> getRepairOrderHistory(long shopId, String licenceNo, String services, String materialName,
                                                  Long fromTime, Long toTime, int startNo, int maxResult) throws Exception {
    Session session = getSession();
    List<ItemIndex> list = null;
    List<ItemIndexDTO> itemIndexDTOs = null;
    try {

      List<Long> orderIdList = this.getRepairOrderIdFromItemIndex(shopId, licenceNo, services, materialName,
          fromTime, toTime, startNo, maxResult);
      if (CollectionUtils.isNotEmpty(orderIdList)) {
        Query query = SQL.getRepairOrderHistory(session, orderIdList);
        list = (List<ItemIndex>) query.list();
        if (null != list && 0 != list.size()) {
          itemIndexDTOs = new ArrayList<ItemIndexDTO>();
          for (ItemIndex itemIndex : list) {
            ItemIndexDTO itemIndexDTO = itemIndex.toDTO();

            itemIndexDTOs.add(itemIndexDTO);
          }
        }
      }
    } finally {
      release(session);
    }
    return itemIndexDTOs;
  }

  public int countRepairOrderHistory(long shopId, String licenceNo, String services, String materialName,
                                     Long fromTime, Long toTime) {
    Session session = this.getSession();
    try {
      Query query = SQL.countRepairOrderHistory(session, shopId, licenceNo, services, materialName,
          fromTime, toTime);
	     BigInteger count = (BigInteger)query.uniqueResult();
      if (count == null) {
        return 0;
      }
      return count.intValue();
    } finally {
      release(session);
    }
  }
//   public List<ItemIndex>

  public List<Long> getRepairOrderIdFromItemIndex(long shopId, String licenceNo, String services, String materialName,
                                                  Long fromTime, Long toTime, int startNo, int maxResult) {
    Session session = getSession();
    List<Long> list = null;
    List<ItemIndexDTO> returnList = null;

    try {
      Query query = (Query) SQL.getRepairOrderIdFromItemIndex(
          session, shopId, licenceNo, services, materialName, fromTime, toTime, startNo, maxResult);

      list = (List<Long>) query.list();
    } finally {
      release(session);
    }
    return list;
  }

  /**
   *       查询当日新增【车辆】历史记录
   *
   * @param shopId
   * @param licenceNo      车牌号
   * @param services          施工项目
   * @param materialName             材料品名
   * @param fromTime
   * @param toTime
   * @param pager
   * @author zhangchuanlong
   * @return
   */
  public List<ItemIndex> getRepairOrderIdFromItemIndexByTodayNewVehicle(long shopId, String licenceNo, String services, String materialName,
                                                                        Long fromTime, Long toTime, Pager pager, List<String> licenceNos) {
    Session session = getSession();
    List<ItemIndex> list = null;
    try {
      Query query = SQL.getRepairOrderIdFromItemIndexByNewVehicle(
          session, shopId, licenceNo, services, materialName, fromTime, toTime, pager, licenceNos);
      list = (List<ItemIndex>) query.list();
    } finally {
      release(session);
    }
    return list;
  }



  public Set<String> getItemInfo(Long orderId, ItemTypes itemType) {
    if (itemType==null) return null;
    Session session = getSession();
    List<String> list = null;

    try {
      SQLQuery query = SQL.getItemInfo(session, orderId, itemType);
      query.addScalar("itemName", StandardBasicTypes.STRING);
      list = query.list();
    } finally {
      release(session);
    }
    return new HashSet<String>(list);
  }

  public List<OrderIndex> searchOrderIndexByOrder(Long shopId, Long orderId, OrderTypes orderType, OrderStatus orderStatus, Long customerOrSupplierId) {
    Session session = getSession();
    try {
      Query q = SQL.searchOrderIndexByOrder(session, shopId, orderId, orderType, orderStatus, customerOrSupplierId);
      if (q != null) {
        List result = q.list();
        if (CollectionUtils.isNotEmpty(result)) {
          return (List<OrderIndex>) result;
        }
      }
        return new ArrayList<OrderIndex>();


    } finally {
      release(session);
    }
  }

  public List<OrderIndex> searchOrderIndexByShopIdAndCustomerOrSupplierId(Long shopId, Long customerOrSupplierId) {
    Session session = getSession();
    try {
      Query q = SQL.searchOrderIndexByShopIdAndCustomerOrSupplierId(session, shopId, customerOrSupplierId);
      if (q != null) {
        List result = q.list();
        if (CollectionUtils.isNotEmpty(result)) {
          return (List<OrderIndex>) result;
        }
      }
        return new ArrayList<OrderIndex>();


    } finally {
      release(session);
    }
  }

  public List getInventorySearchIndexByShopId(Long shopId, Long start, int num) {
    Session session = getSession();
    try {
      Query query = SQL.getInventorySearchIndex(session, shopId, start, num);
      return query.list();
    } finally {
      release(session);
    }
  }

  public List<CurrentUsedProduct> getCurrentUsedProduct(SearchMemoryConditionDTO searchMemoryConditionDTO) {
    Session session = getSession();
    try {
      Query query = SQL.getCurrentUsedProduct(session, searchMemoryConditionDTO);
      return query.list();
    } finally {
      release(session);
    }
  }

  public List<SolrMatchStopWord> getAllSolrMatchStopWordList(Long shopId) {
    Session session = getSession();
    try {
      Query query = SQL.getAllSolrMatchStopWordList(session,shopId);
      return (List<SolrMatchStopWord>)query.list();
    } finally {
      release(session);
    }
  }

  public List<ItemIndex> searchReturnAbleProducts(ItemIndexDTO itemIndexDTO, OrderTypes type) {
    Session session = getSession();
    try {
      List<ItemIndex> list = SQL.searchReturnAbleProducts(session, itemIndexDTO, type).list();
      if (list != null && list.size() > 0) {
        return list;
      }
      return null;
    } finally {
      release(session);
    }
  }

  public List<ItemIndex> searchReturnTotal(ItemIndexDTO itemIndexDTO, Integer startNo) {
    Session session = getSession();
    try {
      Query query = SQL.searchReturnTotal(session, itemIndexDTO, startNo);
      List<ItemIndex> list = query.list();
      if (list != null) {
        return list;
      }
      return null;
    } finally {
      release(session);
    }
  }

  public int countInwareHistory(ItemIndexDTO itemIndexDTO) {
    Session session = this.getSession();

    try {
      Query q = SQL.countInwareHistory(session, itemIndexDTO);
      if (q.uniqueResult() == null) {
        return 0;
      }
      return ((BigInteger) q.uniqueResult()).intValue();
    } finally {
      release(session);
    }
  }


  public InventorySearchIndex searchInventorySearchIndexAmount(Long shopId, String productName, String productBrand,
                                                               String productSpec, String productModel, String pvBrand, String pvModel,
                                                               String pvYear, String pvEngine,String commodityCode) {
    Session session = this.getSession();
    try {
      Query query = SQL.searchInventorySearchIndexAmount(session, shopId, productName, productBrand, productSpec, productModel
          , pvBrand, pvModel, pvYear, pvEngine, commodityCode);
      if (query.list().size() > 0) {
        return (InventorySearchIndex) query.list().get(0);
      }
      return null;
    } finally {
      release(session);
    }
  }

  public void updateOrderIndexStatus(Long shopId, OrderTypes orderType, Long orderId, OrderStatus newStatus) {
    Session session = this.getSession();
    try {
      Query query = SQL.updateOrderIndexStatus(session, shopId, orderType, orderId, newStatus);
      query.executeUpdate();
    } finally {
      release(session);
    }
  }

  public Long getOrderIndexMaxId(Long shopId, long startId, int pageSize) {
    Session session = this.getSession();
    try {
      Query query = SQL.getOrderIndexMaxId(session, shopId, startId, pageSize);
      Long endId = Long.valueOf(query.uniqueResult().toString());
      return endId;
    } finally {
      release(session);
    }
  }
  public List<OrderIndexDTO> getOrderIndexForReindexByRange(Long shopId, long startId, long endId) {
    Session session = this.getSession();
    try {
      Query query = SQL.getOrderIndexForReindexByRange(session, shopId, startId, endId);
      List<OrderIndex> orderIndexList = query.list();
      if (CollectionUtils.isEmpty(orderIndexList)) return null;
      List<OrderIndexDTO> orderIndexDTOList = new ArrayList<OrderIndexDTO>();
      OrderIndexDTO orderIndexDTO = null;
      for (OrderIndex orderIndex : orderIndexList) {
        orderIndexDTO = orderIndex.toDTO();
        orderIndexDTOList.add(orderIndexDTO);
      }
      return orderIndexDTOList;
    } finally {
      release(session);
    }
  }

  public List<OrderIndex> getOrderIndexForReindex(Long shopId, long startId, int pageSize) {
    Session session = this.getSession();
    try {
      Query query = SQL.getOrderIndexForReindex(session, shopId, startId, pageSize);
      return query.list();
    } finally {
      release(session);
    }
  }

  public List<ItemIndex> getWashOrderItemIndexList(long shopId, long startTime, long endTime, int pageNo, int pageSize, String arrayType) {
    Session session = this.getSession();

    try {
      Query hql = SQL.getWashOrderItemIndexList(session, shopId, startTime, endTime, pageNo, pageSize, arrayType);
      return (List<ItemIndex>) hql.list();
    } finally {
      release(session);
    }
  }

  public List<ItemIndex> getWashItemIndexList(long shopId, long startTime, long endTime) {
    Session session = this.getSession();

    try {
      Query hql = SQL.getWashItemIndexList(session, shopId, startTime, endTime);
      return (List<ItemIndex>) hql.list();
    } finally {
      release(session);
    }
  }

  public List<ItemIndex> getSalesOrderItemIndexList(long shopId, String idString, String arrayType) {
    Session session = this.getSession();

    try {
      Query hql = SQL.getSalesOrderItemIndexList(session, shopId, idString, arrayType);
      return (List<ItemIndex>) hql.list();
    } finally {
      release(session);
    }
  }

  public List<ItemIndex> getRepairOrderItemIndexList(long shopId, String idString, String arrayType) {
    Session session = this.getSession();

    try {
      Query hql = SQL.getRepairOrderItemIndexList(session, shopId, idString, arrayType);
      return (List<ItemIndex>) hql.list();
    } finally {
      release(session);
    }
  }

  public InventorySearchIndex getInventorySearchIndexByProductId(Long productId) {
    Session session = getSession();
    try {
      Query query = SQL.getInventorySearchIndexByProductId(session, productId);
      List<InventorySearchIndex> inventorySearchIndexList = query.list();
      if (inventorySearchIndexList == null || inventorySearchIndexList.isEmpty()) {
        return null;
      }
      return inventorySearchIndexList.get(0);
    } finally {
      release(session);
    }
  }

  public ItemIndex getItemIndexByOrderIdAndItemIdAndOrderType(Long orderId, Long itemId, OrderTypes orderTpe) {
    Session session = getSession();
    try {
      Query query = SQL.getItemIndexByOrderIdAndItemIdAndOrderType(session, orderId, itemId, orderTpe);
      List<ItemIndex> itemIndexList = query.list();
      if (itemIndexList == null || itemIndexList.isEmpty()) {
        return null;
      }
      return itemIndexList.get(0);
    } finally {
      release(session);
    }
  }

  public List<CurrentUsedVehicle> getCurrentUsedVehicle(SearchConditionDTO searchConditionDTO) {
    Session session = getSession();
    try {
      Query query = SQL.getCurrentUsedVehicle(session, searchConditionDTO);
      return query.list();
    } finally {
      release(session);
    }
  }

  public List<InventorySearchIndex> getLowerLimitInventorySearchIndexs(Pager pager, String sortStr, Long shopId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getLowerLimitInventorySearchIndex(session, pager, sortStr, shopId);
      return (List<InventorySearchIndex>) q.list();
    } finally {
      release(session);
    }
  }

  public List<InventorySearchIndex> getUpperLimitInventorySearchIndexs(Pager pager, String sortStr, Long shopId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getUpperLimitInventorySearchIndex(session, pager, sortStr, shopId);
      return (List<InventorySearchIndex>) q.list();
    } finally {
      release(session);
    }
  }

  public List<String> getWashItemTotal(long shopId, long startTime, long endTime) {
    Session session = this.getSession();
    try {
      Query q = SQL.getWashItemTotal(session, shopId, startTime, endTime);
      List<String> stringList = new ArrayList<String>();
      if (q == null) {
        return stringList;
      }

        List<Object> list = q.list();
      if (CollectionUtils.isEmpty(list)) {
        return stringList;
      }

          Object[] array = (Object[]) list.get(0);
          if (array[0] != null && array[1] != null) {
            stringList.add(array[0].toString());
            stringList.add(array[1].toString());
            if(null==array[2])
            {
              stringList.add(array[1].toString());
          }
            else
            {
              stringList.add(array[2].toString());
            }

          }
      return stringList;
    } finally {
      release(session);
    }
  }

  public double getWashTotalByCustomerId(long shopId, long customerId) {
    Session session = this.getSession();

    try {
      Query q = SQL.getWashTotalByCustomerId(session, shopId, customerId);

      Object o = q.uniqueResult();
      if (o == null) return 0.0d;
      return Double.parseDouble(o.toString());
    } finally {
      release(session);
    }

  }

  public List<ItemIndexDTO> getItemIndexDTOListByOrderId(long shopId, long orderId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getItemIndexDTOListByOrderId(session, shopId, orderId);
      List<ItemIndex> itemIndexList = (List<ItemIndex>) q.list();
      List<ItemIndexDTO> itemIndexDTOList = null;
      if (CollectionUtils.isNotEmpty(itemIndexList)) {
        itemIndexDTOList = new ArrayList<ItemIndexDTO>();
        for (ItemIndex itemIndex : itemIndexList) {
          ItemIndexDTO itemIndexDTO = itemIndex.toDTO();
          itemIndexDTOList.add(itemIndexDTO);
        }
      }
      return itemIndexDTOList;
    } finally {
      release(session);
    }
  }

  public List<ItemIndex> getItemIndexesByOrderId(Long shopId, Long orderId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getItemIndexDTOListByOrderId(session, shopId, orderId);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<OrderIndex> getOrderIndexDTOByOrderId(Long shopId, Long orderId) {
    Session session = getSession();
    try {
      Query q = SQL.getOrderIndexDTOByOrderId(session, shopId, orderId);
      return (List<OrderIndex>) q.list();

    } finally {
      release(session);
    }
  }

  public List<ItemIndex> getItemIndexDTOByOrderItemId(Long shopId,Long orderId, Long orderItemId) {
    Session session = getSession();
    try {
      Query q = SQL.getItemIndexDTOByOrderItemId(session, shopId,orderId, orderItemId);
      return (List<ItemIndex>) q.list();
    } finally {
      release(session);
    }
  }

  public List<OrderIndex> getOrderIndexDTOByArrayOrderId(Long shopId, Long start, Long endId) {
    Session session = getSession();
    try {
      Query q = SQL.getOrderIndexDTOByArrayOrderId(session, shopId, start, endId);
      return (List<OrderIndex>) q.list();

    } finally {
      release(session);
    }
  }

  public long countItemIndexByShopId(long shopId) {
    Session session = getSession();
    try {
      Query q = SQL.countItemIndexByShopId(session, shopId);
      return Long.parseLong(q.iterate().next().toString());
    } finally {
      release(session);
    }
  }

  public List<ItemIndex> getItemIndexListByShopId(long shopId,int pageNum,int pageSize) {
   Session session = this.getSession();
    try {
      Query q = SQL.getItemIndexListByShopId(session, shopId, pageNum, pageSize);
      if(q != null) {
        List result = q.list();
        if (CollectionUtils.isNotEmpty(result)) {
          return (List<ItemIndex>) result;
      }
      }
      return null;
    } finally {
      release(session);
    }
  }

  public long countOrderIndexByShopId(long shopId) {
    Session session = getSession();
    try {
      Query q = SQL.countOrderIndexByShopId(session, shopId);
      return Long.parseLong(q.iterate().next().toString());
    } finally {
      release(session);
    }
  }

  public List<OrderIndex> getOrderIndexListByShopId(long shopId,int pageNum,int pageSize) {
   Session session = this.getSession();
    try {
      Query q = SQL.getOrderIndexListByShopId(session, shopId, pageNum, pageSize);
      if (q != null) {
        List result = q.list();
        if (CollectionUtils.isNotEmpty(result)) {
          return (List<OrderIndex>) result;
      }
      }
      return null;
    } finally {
      release(session);
    }
  }

  /**
   *       查询当日新增客户【车辆】历史记录
   *
   * @param shopId
   * @param vehicle              车牌号
   * @param services            施工项目
   * @param itemName        材料品名
   * @param fromTime
   * @param toTime
   * @param pager
   * @author zhangchuanlong
   * @return
   */
  public List<ItemIndexDTO> getRepairOrderHistoryByTodayNewCustomer(Long shopId, String vehicle, String services, String itemName, Long fromTime, Long toTime, Pager pager,List<String> licenceNos) throws BcgogoException, InvocationTargetException, IllegalAccessException {
           Session session = getSession();
    List<ItemIndex> list = null;
    List<ItemIndexDTO> itemIndexDTOs = null;
    List<Long> orderIdList = new ArrayList<Long>();
    try {
      List<ItemIndex> itemIndexList = this.getRepairOrderIdFromItemIndexByTodayNewVehicle(shopId, vehicle, services, itemName,
          fromTime, toTime, pager, licenceNos);
      if (itemIndexList != null && itemIndexList.size() > 0) {
        for (ItemIndex itemIndex : itemIndexList) {
          orderIdList.add(itemIndex.getOrderId());
        }
      }
      Query query = SQL.getRepairOrderHistory(session, orderIdList);
      list = (List<ItemIndex>) query.list();
      if (null != list && 0 != list.size()) {
        itemIndexDTOs = new ArrayList<ItemIndexDTO>();
        for (ItemIndex itemIndex : list) {
          ItemIndexDTO itemIndexDTO = itemIndex.toDTO();
          itemIndexDTOs.add(itemIndexDTO);
        }
      }
    } finally {
      release(session);
    }
    return itemIndexDTOs;
  }

  /**
   * 查询当日新增客户【车辆】历史记录总数
   *
   * @param shopId
   * @param vehicle
   * @param services
   * @param itemName
   * @param endDateLong
   * @param endDateLong2
   * @author zhangchuanlong
   * @return
   */
  public int countRepairOrderHistoryByToDayNewVehicle(Long shopId, String vehicle, String services, String itemName, Long endDateLong, Long endDateLong2,List<String> licenceNoList)  {
    Session session = this.getSession();
    Query query = null;

      query = SQL.countRepairOrderHistoryByTodayNewVehicle(session, shopId, vehicle, services, itemName, endDateLong, endDateLong2, licenceNoList);
      BigInteger count =  (BigInteger) query.uniqueResult();
	  if (count == null) {
        return 0;
      }
      return count.intValue();

  }

  public Long countNullProductIDItemIndex() {
    Session session = getSession();
    try {
      Query q = SQL.countNullProductIDItemIndex(session);
      return Long.parseLong(q.iterate().next().toString());
    } finally {
      release(session);
    }
  }

  public List<ItemIndex> getNullProductIDItemIndexs(Pager pager) {
    Session session = getSession();
    try{
      Query query = SQL.getNullProductIDItemIndexs(session,pager.getRowStart(),pager.getPageRows());
      return (List<ItemIndex>) query.list();
    }finally {
      release(session);
    }
  }

  public List<ItemIndex> getItemIndex(Long shopId, ItemIndexDTO itemIndexDTO, Pager pager)throws Exception{
    if(itemIndexDTO == null){
      itemIndexDTO = new ItemIndexDTO();
    }
    Session session = getSession();
    try{
      if(pager == null){
      pager = new Pager(1);
    }
      Query query = SQL.getItemIndex(session,shopId,itemIndexDTO.getCustomerOrSupplierName(),itemIndexDTO.getItemName(),
          itemIndexDTO.getItemBrand(),itemIndexDTO.getItemSpec(),itemIndexDTO.getItemModel(),pager.getRowStart(),pager.getPageSize());
      return  (List<ItemIndex>)query.list();
    }finally {
      release(session);
    }
  }

  public Long countItemIndexWithItemIndexDTO(Long shopId, ItemIndexDTO itemIndexDTO)throws Exception{
    if(itemIndexDTO == null){
      itemIndexDTO = new ItemIndexDTO();
    }
    Session session = getSession();
    try{
      Query query = SQL.countItemIndexWithItemIndexDTO(session, shopId, itemIndexDTO.getCustomerOrSupplierName(), itemIndexDTO.getItemName(),
          itemIndexDTO.getItemBrand(), itemIndexDTO.getItemSpec(), itemIndexDTO.getItemModel());
      List<Object> count = query.list();
      if(CollectionUtils.isNotEmpty(count)){
        return new Long(count.size());
      }else {
        return 0L;
      }
    }finally {
      release(session);
    }
  }

  public List<ItemIndex> getPurchaseReturnItemIndex(Long shopId, Long supplierId, Long productId) {
    Session session = getSession();
    try{
     Query query = SQL.getItemIndexBycustomerIdAndProductId(session, shopId, supplierId, productId);
      return (List<ItemIndex>)query.list();
    } finally {
      release(session);
    }
  }

  public int countWashItemIndexByShopId(long shopId, long startTime, long endTime) {
    Session session = getSession();
    try {
      Query q = SQL.countWashItemIndexByShopId(session, shopId, startTime, endTime);
      return Integer.parseInt(q.uniqueResult().toString());
    } finally {
      release(session);
    }
  }

  public List<ItemIndex> getWashItemIndexListByPager(long shopId, long startTime, long endTime,Pager pager) {
    Session session = this.getSession();

    try {
      Query hql = SQL.getWashItemIndexListByPager(session, shopId, startTime, endTime,pager);
      return (List<ItemIndex>) hql.list();
    } finally {
      release(session);
    }
  }

//统计店铺库存信息，double[0] 种类，double[1]数量，double[2] 总金额
	public Double[] countInventoryInfoByShopId(Long shopId) {
		Session session = this.getSession();
		try{
			Query hql = SQL.countInventoryInfo(session,shopId);
			Object[] countInfo= (Object[]) hql.uniqueResult() ;
			if(countInfo!=null){
				Double[] counts = new Double[countInfo.length];
				counts[0] = countInfo[0]==null?0d:((BigInteger)countInfo[0]).doubleValue();
				counts[1] = countInfo[1]==null?0d:(Double)countInfo[1];
				counts[2] = countInfo[2]==null?0d:(Double)countInfo[2];
				return counts;
			}
			return null;
		}finally {
			 release(session);
		}
	}


  public long countWashItemIndexByShopId(long shopId) {
    Session session = getSession();
    try {
      Query q = SQL.countWashItemIndexByShopId(session, shopId);
      return Long.parseLong(q.iterate().next().toString());
    } finally {
      release(session);
    }
  }

  public List<ItemIndex> getItemIndexDTOByOrderId(long shopId, long orderId) {
    Session session = this.getSession();
    try {
      Query query = SQL.getItemIndexDTOByOrderId(session, shopId, orderId);
      return query.list();
    } finally {
      release(session);
    }
  }

	/**
	 * 仅用于数据初始化
	 * @param shopId
	 * @param startProductId
	 * @param endProductId
	 * @param limit   eg:limit=3 表示限制3个，limit=null表示无限制
	 * @return
	 */

	//o[0] productId,o[1] supplierId,o[2] shopId ,o[3] orderTimeCreated  o[4] s.name,o[5]s.contact,o[6]s.moblie "
  @Deprecated
	public List<ProductSupplierDTO> getProductSupplierDTO(Long shopId, Long startProductId, Long endProductId, Integer limit) {
		Session session = this.getSession();
		List<ProductSupplierDTO> productSupplierDTOs = new LinkedList<ProductSupplierDTO>();
		try{
		 Query q = SQL.getProductSupplierDTO(session,shopId,startProductId,endProductId);
			List<Object[]> list = q.list();
			if (CollectionUtils.isNotEmpty(list) && limit!=null && limit>0) {
				List<ProductSupplierDTO> temp = new LinkedList<ProductSupplierDTO>();
				for (Object[] o : list) {
					if (o != null && o.length > 0) {
						ProductSupplierDTO productSupplierDTO = new ProductSupplierDTO();
						productSupplierDTO.setProductId((Long) o[0]);
						productSupplierDTO.setSupplierId((Long) o[1]);
						productSupplierDTO.setShopId((Long) o[2]);
						productSupplierDTO.setLastUsedTime((Long) o[3]);
						productSupplierDTO.setName((String) o[4]);
						productSupplierDTO.setContact((String) o[5]);
						productSupplierDTO.setMobile((String) o[6]);
						if(CollectionUtils.isNotEmpty(temp)){
							if(temp.get(0).getProductId() != null && temp.get(0).getProductId().equals((Long) o[0])) {
								temp.add(productSupplierDTO);
							}else {
								productSupplierDTOs.addAll(ServiceUtil.getTopNProductSupplierDTO(temp,limit));
								temp.clear();
								temp.add(productSupplierDTO);
							}
						}else{
							temp.add(productSupplierDTO);
						}
					}
				}
			}

			 if(CollectionUtils.isNotEmpty(list) &&(limit==null || limit<1) ){
				 for(Object[] o :list){
					 if (o != null && o.length > 0) {
						 ProductSupplierDTO productSupplierDTO = new ProductSupplierDTO();
						 productSupplierDTO.setProductId((Long)o[0]);
						 productSupplierDTO.setSupplierId((Long) o[1]);
						 productSupplierDTO.setShopId((Long) o[2]);
						 productSupplierDTO.setLastUsedTime((Long) o[3]);
						 productSupplierDTO.setName((String) o[4]);
						 productSupplierDTO.setContact((String) o[5]);
						 productSupplierDTO.setMobile((String) o[6]);
						 productSupplierDTOs.add(productSupplierDTO);
					 }
				 }
			 }
		} finally {
			release(session);
		}
		return productSupplierDTOs;
	}



	public Long getItemIndexNextProductIdWithSupplier(Long shopId, Long startProductId, int rows){
		Session session = this.getSession();
		try{
			Query query = SQL.getItemIndexNextProductIdWithSupplier(session, shopId, startProductId,rows);
      return (Long)query.uniqueResult();
		} finally {
			release(session);
		}
	}
public Map<Long, List<ItemIndexDTO>> getItemIndexDTOByArrayOrderId(long shopId, Long startId, Long endId) {
     Session session = this.getSession();
    try {
      Query query = SQL.getItemIndexDTOByArrayOrderId(session, shopId, startId, endId);
      List<ItemIndex> itemIndexes = query.list();
      if (CollectionUtils.isEmpty(itemIndexes)) return null;
      Map<Long, List<ItemIndexDTO>> map = new HashMap<Long, List<ItemIndexDTO>>();
      List<ItemIndexDTO>  itemIndexDTOs;
      for (ItemIndex itemIndex : itemIndexes) {
        if (map.get(itemIndex.getOrderId()) == null) {
          itemIndexDTOs = new ArrayList<ItemIndexDTO>();
          itemIndexDTOs.add(itemIndex.toDTO());
          map.put(itemIndex.getOrderId(), itemIndexDTOs);
        } else {
          map.get(itemIndex.getOrderId()).add(itemIndex.toDTO());
        }
      }
      return map;
    } finally {
      release(session);
    }
  }


  public Long getRepairOrderIndexMaxId(Long shopId, long startId, int pageSize) {
    Session session = this.getSession();
    try {
      Query query = SQL.getRepairOrderIndexMaxId(session, shopId, startId, pageSize);
      Long endId = Long.valueOf(query.uniqueResult().toString());
      return endId;
    } finally {
      release(session);
    }
  }

  public OrderIndex getOrderIndex(Long shopId,Long orderId)
  {
    Session session = getSession();

    try{
      Query q = SQL.getOrderIndex(session,shopId,orderId);

      List<OrderIndex> orderIndexList = (List<OrderIndex>)q.list();

      if(CollectionUtils.isEmpty(orderIndexList))
      {
        return null;
      }

      return orderIndexList.get(0);

    }finally {
      release(session);
    }
  }

	public InventorySearchIndex getInventorySearchIndexByCommodityCode(long shopId, String commodityCode) {
		Session session = this.getSession();
		try{
			if(StringUtils.isBlank(commodityCode)){
				return null;
			}
			Query query = SQL.getInventorySearchIndexByCommodityCode(session,shopId,commodityCode);
			List<InventorySearchIndex> inventorySearchIndexes = (List<InventorySearchIndex>)query.list();
			if(CollectionUtils.isNotEmpty(inventorySearchIndexes)){
				return inventorySearchIndexes.get(0);
			}else {
				return null;
			}
		} finally {
			release(session);
		}
	}

	public Integer countInventoryLowerLimitAmount(Long shopId) {
    Session session = this.getSession();
    try {
      Query query = SQL.countInventoryLowerLimitAmout(session, shopId);
      Long amount = (Long) query.uniqueResult();
      Integer amountIntVal = amount == null ? null : amount.intValue();
      return amountIntVal;
    } finally {
      release(session);
    }
  }

  public Integer countInventoryUpperLimitAmount(Long shopId) {
    Session session = this.getSession();
    try {
      Query query = SQL.countInventoryUpperLimitAmount(session, shopId);
      Long amount = (Long) query.uniqueResult();
      Integer amountIntVal = amount == null ? null : amount.intValue();
      return amountIntVal;
    } finally {
      release(session);
    }
  }

	public List<InventorySearchIndex> getInventorySearchIndexByProductLocalInfoIds(Long shopId, Long... productLocalInfoIds) {
		Session session = this.getSession();
		try {
			Query query = SQL.getInventorySearchIndexByProductLocalInfoIds(session, shopId, productLocalInfoIds);
			return (List<InventorySearchIndex>) query.list();
		} finally {
			release(session);
		}
	}

  public Map<Long,InventorySearchIndex> getInventorySearchIndexMapByIds(Long shopId, Set<Long> ids) {
    Map<Long,InventorySearchIndex> inventorySearchIndexMap = new HashMap<Long, InventorySearchIndex>();
    if(shopId == null || CollectionUtils.isEmpty(ids)){
      return inventorySearchIndexMap;
    }
		Session session = this.getSession();
		try {
			Query query = SQL.getInventorySearchIndexByProductLocalInfoIds(session, shopId, ids.toArray(new Long[ids.size()]));
      List<InventorySearchIndex> inventorySearchIndexes = query.list();
      if(CollectionUtils.isNotEmpty(inventorySearchIndexes)){
        for(InventorySearchIndex inventorySearchIndex :inventorySearchIndexes){
          inventorySearchIndexMap.put(inventorySearchIndex.getProductId(),inventorySearchIndex);
        }
      }
			return inventorySearchIndexMap;
		} finally {
			release(session);
		}
	}

  public void updateInventorySearchIndexKindName(Long shopId, String oldKindName,String newKindName){
    Session session = this.getSession();
    try{
      Query query = SQL.updateInventorySearchIndexKindName(session,shopId,oldKindName,newKindName);
      query.executeUpdate();
    }finally {
      release(session);
    }
  }

  public List<Long> getInventorySearchIndexIdListByProductKind(Long shopId,String kindName){
    Session session = this.getSession();
    try{
      Query q = SQL.getInventorySearchIndexIdListByProductKind(session,shopId,kindName);
      return (List<Long>)q.list();
    }finally {
      release(session);
    }
  }

  public void deleteMultipleInventoryKind(Long shopId, String kindName){
    Session session = this.getSession();
    try{
      Query q = SQL.deleteMultipleInventoryKind(session,shopId,kindName);
      q.executeUpdate();
    }finally {
      release(session);
    }
  }

	public Object[] getLowerLimitSearchCount(Long shopId) {
	    Session session = this.getSession();
    try{
      Query q = SQL.getLowerLimitSearchCount(session,shopId);
      return  (Object[])q.uniqueResult();
    }finally {
      release(session);
    }
	}

	public Object[] getUpperLimitSearchCount(Long shopId) {
	    Session session = this.getSession();
    try{
      Query q = SQL.getUpperLimitSearchCount(session,shopId);
      return  (Object[])q.uniqueResult();
    }finally {
      release(session);
    }
	}

  public List<OrderIndex> getCustomerOrSupplierOrderIndexs(Long shopId,Long[] customerOrSupplierIds){
    Session session = this.getSession();
    try{
      Query q = SQL.getCustomerOrSupplierOrderIndexs(session,shopId,customerOrSupplierIds);
      return  (List<OrderIndex>)q.list();
    }finally {
      release(session);
    }
  }

  public List<ItemIndex> getItemIndexByCustomerIds(Long shopId,Long[] customerIds){
    Session session = this.getSession();
    try{
      Query q = SQL.getItemIndexByCustomerIds(session,shopId,customerIds);
      return  (List<ItemIndex>)q.list();
    }finally {
      release(session);
    }
  }


  public int countItemIndexByProductIdOrderStatus(Long productId, Long shopId, Map<OrderTypes, List<OrderStatus>> inProgressStatusMap) {
    Session session = getSession();
    try{
      Query q = SQL.countItemIndexByProductIdOrderStatus(session, productId, shopId, inProgressStatusMap);
      return Integer.parseInt(q.uniqueResult().toString());
    }finally{
      release(session);
    }
  }

  public int countItemIndexByServiceIdOrderStatus(Long serviceId, Long shopId, Map<OrderTypes, List<OrderStatus>> inProgressStatusMap){
    Session session = getSession();
    try{
      Query q = SQL.countItemIndexByServiceIdOrderStatus(session, serviceId, shopId, inProgressStatusMap);
      return Integer.parseInt(q.uniqueResult().toString());
    }finally{
      release(session);
    }
  }

  public List<ItemIndex> getItemIndexByProductIdOrderStatus(Set<Long> productIdSet, Long shopId, Map<OrderTypes, List<OrderStatus>> settledStatusMap) {
    Session session = getSession();
    try {
      Query q = SQL.getItemIndexByProductIdOrderStatus(session, productIdSet, shopId, settledStatusMap);
      return (List<ItemIndex>) q.list();
    } finally {
      release(session);
    }
  }

  public int countRepairOrderInOrderIndex(Long shopId, Long fromTime, Long toTime) {
    Session session = this.getSession();
    try {
      Query query = SQL.countRepairOrderInOrderIndex(session, shopId, fromTime, toTime);
       BigInteger count = (BigInteger)query.uniqueResult();
      if (count == null) {
        return 0;
      }
      return count.intValue();
    } finally {
      release(session);
    }
  }
}