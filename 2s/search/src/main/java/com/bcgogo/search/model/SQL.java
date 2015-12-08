package com.bcgogo.search.model;

import com.bcgogo.common.Pager;
import com.bcgogo.common.Sort;
import com.bcgogo.enums.ItemTypes;
import com.bcgogo.enums.OrderStatus;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.search.dto.ItemIndexDTO;
import com.bcgogo.search.dto.SearchConditionDTO;
import com.bcgogo.search.dto.SearchMemoryConditionDTO;
import com.bcgogo.utils.ConfigConstant;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.type.StandardBasicTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: caiweili
 * Date: 12/31/11
 * Time: 12:06 PM
 * To change this template use File | Settings | File Templates.
 */
public class SQL {
   private static final Logger LOG = LoggerFactory.getLogger(SQL.class);

  public static Query countReturn(Session session, Long shopId, Long customerOrSupplierId, OrderTypes orderType, OrderStatus orderStatus) {
   StringBuilder stringBuilder = new StringBuilder();
   stringBuilder.append("select count(*) from OrderIndex as oi where oi.shopId=:shopId and oi.customerOrSupplierId=:customerOrSupplierId and oi.orderTypeEnum='").append(orderType.toString())
                .append("' and oi.orderStatusEnum='").append(orderStatus.toString()).append("'");
   Query query = session.createQuery(stringBuilder.toString()).setLong("shopId",shopId).setLong("customerOrSupplierId",customerOrSupplierId);
   return  query;
  }
  public static Query countCarHistory(Session session, Long shopId, String vehicle,Long startTime,Long endTime) {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("select count(*) from ItemIndex as i where i.shopId = :shopId and i.vehicle = :vehicle ");
    if (null != startTime) {
      stringBuilder.append(" and orderTimeCreated >=:startTime ");
    }
    if (null != endTime) {
      stringBuilder.append(" and orderTimeCreated <=:endTime ");
    }

    Query query = session.createQuery(stringBuilder.toString()).setLong("shopId", shopId).setString("vehicle",vehicle);

    if (null != startTime) {
      query = query.setLong("startTime", startTime);
    }
    if (null != endTime) {
      query = query.setLong("endTime", endTime);
    }

    return query;
  }

  public static Query countGoodsHistory(Session session, Long shopId, String customerOrSupplierName, List<OrderTypes> orderType,
                                        String productName,
                                        String productBrand, String productSpec,
                                        String productModel,Long startTime,Long endTime) {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("select count(*) from ItemIndex as i where i.shopId = :shopId and i.itemType=2");
    if (null != startTime) {
      stringBuilder.append(" and orderTimeCreated >=:startTime ");
    }
    if (null != endTime) {
      stringBuilder.append(" and orderTimeCreated <=:endTime ");
    }
    if (customerOrSupplierName != null && !"".equals(customerOrSupplierName)) {
      stringBuilder.append(" and customerOrSupplierName like :customerOrSupplierName ");
    }
    if (orderType != null && !orderType.isEmpty()) {
      stringBuilder.append(" and (");
      for (OrderTypes ot : orderType) {
        if (ot != null) {
          stringBuilder.append(" orderTypeEnum ='").append(ot.toString()).append("' or ");
        }
      }
      stringBuilder.replace(stringBuilder.length() - 4, stringBuilder.length(), ") ");
    }

    if (productName != null && !"".equals(productName)) {
      stringBuilder.append(" and itemName like :itemName ");
    }
    if (productBrand != null && !"".equals(productBrand)) {
      stringBuilder.append(" and itemBrand like :itemBrand ");
    }
    if (productSpec != null && !"".equals(productSpec)) {
      stringBuilder.append(" and itemSpec like :itemSpec ");
    }
    if (productModel != null && !"".equals(productModel)) {
      stringBuilder.append(" and itemModel like :itemModel ");
    }

    Query query = session.createQuery(stringBuilder.toString()).setLong("shopId", shopId);

    if (null != startTime) {
      query = query.setLong("startTime", startTime);
    }
    if (null != endTime) {
      query = query.setLong("endTime", endTime);
    }
    if (null != customerOrSupplierName && !"".equals(customerOrSupplierName)) {
      query = query.setString("customerOrSupplierName", customerOrSupplierName + "%");
    }

    if (productName != null && !"".equals(productName)) {
      query = query.setString("itemName", productName + "%");
    }
    if (productBrand != null && !"".equals(productBrand)) {
      query = query.setString("itemBrand", productBrand + "%");
    }
    if (productSpec != null && !"".equals(productSpec)) {
      query = query.setString("itemSpec", productSpec + "%");
    }
    if (productModel != null && !"".equals(productModel)) {
      query = query.setString("itemModel", productModel + "%");
    }
    return query;
  }

  public static Query searchItemIndex(Session session,
                                      Long shopId,
                                      Long customerId,
                                      String vehicle,
                                      Long orderId,
                                      List<OrderTypes> orderTypeEnums,
                                      Long itemId,
                                      ItemTypes itemType,
                                      String customerOrSupplierName,
                                      String itemName,
                                      String itemBrand,
                                      String itemSpec,
                                      String itemModel,
                                      OrderStatus orderStatus,
                                      Long fromTime,
                                      Long toTime,
                                      Integer startNo,
                                      Integer maxResult) {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("select o from ItemIndex o where shopId =:shopId ");
    if (fromTime != null) {
      stringBuilder.append(" and orderTimeCreated >=:fromTime ");
    }
    if (toTime != null) {
      stringBuilder.append(" and orderTimeCreated <=:toTime ");
    }
    if (customerId != null) {
      stringBuilder.append(" and customerId =:customerId ");
    }
    if (vehicle != null && !"".equals(vehicle)) {
      stringBuilder.append(" and vehicle =:vehicle ");
    }
    if (orderId != null) {
      stringBuilder.append(" and orderId =:orderId ");
    }
    if(orderTypeEnums!=null) orderTypeEnums.removeAll(Collections.singleton(null));
    if (orderTypeEnums!=null&&!orderTypeEnums.isEmpty()) {
      stringBuilder.append(" and (");
      for (OrderTypes ot : orderTypeEnums) {
        if (ot != null) {
          stringBuilder.append(" orderTypeEnum ='").append(ot.toString()).append("' or ");
        }
      }
      stringBuilder.replace(stringBuilder.length() - 4, stringBuilder.length(), ") ");
    }
    if (itemId != null) {
      stringBuilder.append(" and itemId =:itemId ");
    }
    if (itemType != null) {
      stringBuilder.append(" and itemTypeEnum =:itemType ");
    }
    if (customerOrSupplierName != null && !"".equals(customerOrSupplierName)) {
      stringBuilder.append(" and customerOrSupplierName like :customerOrSupplierName ");
    }
    if (itemName != null && !"".equals(itemName)) {
      stringBuilder.append(" and itemName like :itemName ");
    }
    if (itemBrand != null && !"".equals(itemBrand)) {
      stringBuilder.append(" and itemBrand like :itemBrand ");
    }
    if (itemSpec != null && !"".equals(itemSpec)) {
      stringBuilder.append(" and itemSpec like :itemSpec ");
    }
    if (itemModel != null && !"".equals(itemModel)) {
      stringBuilder.append(" and itemModel like :itemModel ");
    }
    if (orderStatus != null) {
      stringBuilder.append(" and orderStatusEnum =:orderStatus ");
    }
    stringBuilder.append(" order by ascii(orderId),orderTimeCreated desc ");

    Query query = session.createQuery(stringBuilder.toString()).setLong("shopId", shopId);

    if (fromTime != null) {
      query = query.setLong("fromTime", fromTime);
    }
    if (toTime != null) {
      query = query.setLong("toTime", toTime);
    }
    if (customerId != null) {
      query = query.setLong("customerId", customerId);
    }
    if (vehicle != null && !"".equals(vehicle)) {
      query = query.setString("vehicle", vehicle);
    }
    if (orderId != null) {
      query = query.setLong("orderId", orderId);
    }
    if (itemId != null) {
      query = query.setLong("itemId", itemId);
    }
    if (itemType != null) {
      query = query.setParameter("itemType", itemType);
    }
    if (customerOrSupplierName != null && !"".equals(customerOrSupplierName)) {
      query = query.setString("customerOrSupplierName", customerOrSupplierName + "%");
    }
    if (itemName != null && !"".equals(itemName)) {
      query = query.setString("itemName", itemName + "%");
    }
    if (itemBrand != null && !"".equals(itemBrand)) {
      query = query.setString("itemBrand", itemBrand + "%");
    }
    if (itemSpec != null && !"".equals(itemSpec)) {
      query = query.setString("itemSpec", itemSpec + "%");
    }
    if (itemModel != null && !"".equals(itemModel)) {
      query = query.setString("itemModel", itemModel + "%");
    }
    if (orderStatus != null) {
      query = query.setParameter("orderStatus", orderStatus);
    }
    if (startNo != null) {
      query.setFirstResult(startNo).setMaxResults(maxResult);
    }
    return query;
  }
  public static Query countInventoryNumberByShopIdAndProductName(Session session, Long shopId,String productName) {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(" select count(*) from InventorySearchIndex r where shopId=:shopId ");
    if (productName != null && !"".equals(productName)) {
      stringBuilder.append("and productName =:productName ");
    }
    Query query = session.createQuery(stringBuilder.toString()).setLong("shopId", shopId);
    if (productName != null && !"".equals(productName)) {
      query.setString("productName", productName);
    }
    return query;
  }

    public static Query countInventoryNumberByShopId(Session session, Long shopId) {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("select count(*) from inventory_search_index  where shop_id=:shopId ");
    Query query = session.createSQLQuery(stringBuilder.toString()).setLong("shopId", shopId);
    return query;
  }

  public static Query countInventorySumByShopId(Session session,Long shopId){
    StringBuffer sb = new StringBuffer();
    sb.append("select sum(i.amount * i.purchase_price) from inventory_search_index i where " +
        " i.shop_id =:shopId and i.amount >0 and i.purchase_price >0");
    Query query = session.createSQLQuery(sb.toString()).setLong("shopId",shopId);
    return query;
  }

  public static Query searchInventorySearchIndexByProductIds(Session session, Long shopId, Long[] productIds) {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(" select r from InventorySearchIndex r ");
    boolean start = true;
    if (productIds != null && productIds.length > 0) {
      stringBuilder.append(" where r.productId in(:productId)");
      start = false;
    }
      if (start) {
        stringBuilder.append("  where shopId=:shopId ");
      } else {
        stringBuilder.append("  and shopId=:shopId ");
    }

    Query query = session.createQuery(stringBuilder.toString());
      query.setLong("shopId", shopId);
    if (productIds != null && productIds.length > 0) {
      query.setParameterList("productId", productIds);
    }
    return query;
  }

  /**
   * @param session
   * @param shopId
   * @param productName
   * @param productBrand
   * @param productSpec
   * @param productModel
   * @param pvBrand
   * @param pvModel
   * @param pvYear
   * @param pvEngine
   * @param startNo
   * @param maxResult
   * @param inventoryFlag true:库存>0；false库存<=0 ;null:排除库存量查询条件
   * @return
   */
  public static Query searchInventorySearchIndex(Session session, Long shopId, String productName, String productBrand,
                                                 String productSpec, String productModel, String pvBrand, String pvModel,
                                                 String pvYear, String pvEngine, Integer startNo, Integer maxResult,
                                                 Boolean inventoryFlag) {

    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(" select distinct o from InventorySearchIndex o where shopId=:shopId ");
    if (productName != null && !"".equals(productName)) {
      stringBuilder.append(" and productName =:productName ");
    }
    if (productBrand != null && !"".equals(productBrand)) {
      stringBuilder.append(" and productBrand =:productBrand ");
    }
    if (productSpec != null && !"".equals(productSpec)) {
      stringBuilder.append(" and productSpec =:productSpec ");
    }
    if (productModel != null && !"".equals(productModel)) {
      stringBuilder.append(" and productModel =:productModel ");
    }
    if (pvBrand != null && !"".equals(pvBrand)) {
      stringBuilder.append(" and brand =:pvBrand ");
    }
    if (pvModel != null && !"".equals(pvModel)) {
      stringBuilder.append(" and model =:pvModel ");
    }
    if (pvYear != null && !"".equals(pvYear)) {
      stringBuilder.append(" and year =:pvYear ");
    }
    if (pvEngine != null && !"".equals(pvEngine)) {
      stringBuilder.append(" and engine =:pvEngine ");
    }
    if (inventoryFlag != null) {
      if (inventoryFlag) {
        stringBuilder.append(" and amount>0 ");
        stringBuilder.append(" order by editDate desc,amount desc ");
      } else {
        stringBuilder.append(" and amount<=0 ");
      }
    }
	  stringBuilder.append(" and (status is null or status <>'DISABLED')");
    stringBuilder.append(" order by editDate desc,amount desc ");
    Query query = session.createQuery(stringBuilder.toString()).setLong("shopId", shopId);
    if (productName != null && !"".equals(productName)) {
      query.setString("productName", productName);
    }
    if (productBrand != null && !"".equals(productBrand)) {
      query.setString("productBrand", productBrand);
    }
    if (productSpec != null && !"".equals(productSpec)) {
      query.setString("productSpec", productSpec);
    }
    if (productModel != null && !"".equals(productModel)) {
      query.setString("productModel", productModel);
    }
    if (pvBrand != null && !"".equals(pvBrand)) {
      query.setString("pvBrand", pvBrand);
    }
    if (pvModel != null && !"".equals(pvModel)) {
      query.setString("pvModel", pvModel);
    }
    if (pvYear != null && !"".equals(pvYear)) {
      query.setString("pvYear", pvYear);
    }
    if (pvEngine != null && !"".equals(pvEngine)) {
      query.setString("pvEngine", pvEngine);
    }
    if (startNo != null && maxResult != null) {
      query.setFirstResult(startNo).setMaxResults(maxResult);
    }
    return query;
  }

  public static Query searchMutipleInventorySearchIndexId(Session session, Long shopId, String productName, String productBrand,
                                                          String productSpec, String productModel) {

    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(" select o from InventorySearchIndex o where shopId=:shopId and brand='多款' ");
    if (productName != null && !"".equals(productName)) {
      stringBuilder.append(" and productName =:productName ");
    }
    if (productBrand != null && !"".equals(productBrand)) {
      stringBuilder.append(" and productBrand =:productBrand ");
    }
    if (productSpec != null && !"".equals(productSpec)) {
      stringBuilder.append(" and productSpec =:productSpec ");
    }
    if (productModel != null && !"".equals(productModel)) {
      stringBuilder.append(" and productModel =:productModel ");
    }
	  stringBuilder.append(" and (status is null or status <>'DISABLED')");
    Query query = session.createQuery(stringBuilder.toString()).setLong("shopId", shopId);
    if (productName != null && !"".equals(productName)) {
      query.setString("productName", productName);
    }
    if (productBrand != null && !"".equals(productBrand)) {
      query.setString("productBrand", productBrand);
    }
    if (productSpec != null && !"".equals(productSpec)) {
      query.setString("productSpec", productSpec);
    }
    if (productModel != null && !"".equals(productModel)) {
      query.setString("productModel", productModel);
    }
    return query;
  }

  public static Query searchInventorySearchIndexCount(Session session, Long shopId, String productName, String productBrand,
                                                      String productSpec, String productModel, String pvBrand, String pvModel,
                                                      String pvYear, String pvEngine, Boolean inventoryFlag) {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(" select count(*) from InventorySearchIndex o where shopId=:shopId ");
    if (productName != null && !"".equals(productName)) {
      stringBuilder.append(" and productName =:productName ");
    }
    if (productBrand != null && !"".equals(productBrand)) {
      stringBuilder.append(" and productBrand =:productBrand ");
    }
    if (productSpec != null && !"".equals(productSpec)) {
      stringBuilder.append(" and productSpec =:productSpec ");
    }
    if (productModel != null && !"".equals(productModel)) {
      stringBuilder.append(" and productModel =:productModel ");
    }
    if (pvBrand != null && !"".equals(pvBrand)) {
      stringBuilder.append(" and brand =:pvBrand ");
    }
    if (pvModel != null && !"".equals(pvModel)) {
      stringBuilder.append(" and model =:pvModel ");
    }
    if (pvYear != null && !"".equals(pvYear)) {
      stringBuilder.append(" and year =:pvYear ");
    }
    if (pvEngine != null && !"".equals(pvEngine)) {
      stringBuilder.append(" and engine =:pvEngine ");
    }
    if (inventoryFlag != null) {
      if (inventoryFlag) {
        stringBuilder.append(" and amount>0 ");
      } else {
        stringBuilder.append(" and amount<=0 ");
      }
    }
	  stringBuilder.append(" and (status is null or status <>'DISABLED')");
    Query query = session.createQuery(stringBuilder.toString()).setLong("shopId", shopId);
    if (productName != null && !"".equals(productName)) {
      query.setString("productName", productName);
    }
    if (productBrand != null && !"".equals(productBrand)) {
      query.setString("productBrand", productBrand);
    }
    if (productSpec != null && !"".equals(productSpec)) {
      query.setString("productSpec", productSpec);
    }
    if (productModel != null && !"".equals(productModel)) {
      query.setString("productModel", productModel);
    }
    if (pvBrand != null && !"".equals(pvBrand)) {
      query.setString("pvBrand", pvBrand);
    }
    if (pvModel != null && !"".equals(pvModel)) {
      query.setString("pvModel", pvModel);
    }
    if (pvYear != null && !"".equals(pvYear)) {
      query.setString("pvYear", pvYear);
    }
    if (pvEngine != null && !"".equals(pvEngine)) {
      query.setString("pvEngine", pvEngine);
    }
    return query;
  }

  public static Query searchInventorySearchIndexForVehicle(Session session, Long shopId, String productName, String productBrand,
                                                           String productSpec, String productModel, String pvBrand, String pvModel,
                                                           String pvYear, String pvEngine, Integer startNo, Integer maxResult) {

    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(" select o from InventorySearchIndex o where shopId=:shopId ");
    if (productName != null && !"".equals(productName)) {
      stringBuilder.append(" and productName =:productName ");
    }
    if (productBrand != null && !"".equals(productBrand)) {
      stringBuilder.append(" and productBrand =:productBrand ");
    }
    if (productSpec != null && !"".equals(productSpec)) {
      stringBuilder.append(" and productSpec =:productSpec ");
    }
    if (productModel != null && !"".equals(productModel)) {
      stringBuilder.append(" and productModel =:productModel ");
    }
    stringBuilder.append(" and (brand='全部' or brand='多款' ");
    stringBuilder.append(" or ( 1=1 ");
    if (pvBrand != null && !"".equals(pvBrand)) {
      stringBuilder.append(" and brand =:pvBrand ");
    }
    if (pvModel != null && !"".equals(pvModel)) {
      stringBuilder.append(" and model =:pvModel ");
    }
    if (pvYear != null && !"".equals(pvYear)) {
      stringBuilder.append(" and year =:pvYear ");
    }
    if (pvEngine != null && !"".equals(pvEngine)) {
      stringBuilder.append(" and engine =:pvEngine ");
    }
    stringBuilder.append(" )) ");
	  stringBuilder.append(" and (status is null or status <>'DISABLED')");
    Query query = session.createQuery(stringBuilder.toString()).setLong("shopId", shopId);
    if (productName != null && !"".equals(productName)) {
      query.setString("productName", productName);
    }
    if (productBrand != null && !"".equals(productBrand)) {
      query.setString("productBrand", productBrand);
    }
    if (productSpec != null && !"".equals(productSpec)) {
      query.setString("productSpec", productSpec);
    }
    if (productModel != null && !"".equals(productModel)) {
      query.setString("productModel", productModel);
    }
    if (pvBrand != null && !"".equals(pvBrand)) {
      query.setString("pvBrand", pvBrand);
    }
    if (pvModel != null && !"".equals(pvModel)) {
      query.setString("pvModel", pvModel);
    }
    if (pvYear != null && !"".equals(pvYear)) {
      query.setString("pvYear", pvYear);
    }
    if (pvEngine != null && !"".equals(pvEngine)) {
      query.setString("pvEngine", pvEngine);
    }
    if (startNo != null && maxResult != null) {
      query.setFirstResult(startNo).setMaxResults(maxResult);
    }
    return query;
  }

  public static Query searchInventorySearchIndexCountForVehicle(Session session, Long shopId, String productName, String productBrand,
                                                                String productSpec, String productModel, String pvBrand, String pvModel,
                                                                String pvYear, String pvEngine) {

    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(" select count(*) from InventorySearchIndex o where shopId=:shopId ");
    if (productName != null && !"".equals(productName)) {
      stringBuilder.append(" and productName =:productName ");
    }
    if (productBrand != null && !"".equals(productBrand)) {
      stringBuilder.append(" and productBrand =:productBrand ");
    }
    if (productSpec != null && !"".equals(productSpec)) {
      stringBuilder.append(" and productSpec =:productSpec ");
    }
    if (productModel != null && !"".equals(productModel)) {
      stringBuilder.append(" and productModel =:productModel ");
    }
    stringBuilder.append(" and (brand='全部' or brand='多款' ");
    stringBuilder.append(" or ( 1=1 ");
    if (pvBrand != null && !"".equals(pvBrand)) {
      stringBuilder.append(" and brand =:pvBrand ");
    }
    if (pvModel != null && !"".equals(pvModel)) {
      stringBuilder.append(" and model =:pvModel ");
    }
    if (pvYear != null && !"".equals(pvYear)) {
      stringBuilder.append(" and year =:pvYear ");
    }
    if (pvEngine != null && !"".equals(pvEngine)) {
      stringBuilder.append(" and engine =:pvEngine ");
    }
    stringBuilder.append(" )) ");
	  stringBuilder.append(" and (status is null or status <>'DISABLED')");
    Query query = session.createQuery(stringBuilder.toString()).setLong("shopId", shopId);
    if (productName != null && !"".equals(productName)) {
      query.setString("productName", productName);
    }
    if (productBrand != null && !"".equals(productBrand)) {
      query.setString("productBrand", productBrand);
    }
    if (productSpec != null && !"".equals(productSpec)) {
      query.setString("productSpec", productSpec);
    }
    if (productModel != null && !"".equals(productModel)) {
      query.setString("productModel", productModel);
    }
    if (pvBrand != null && !"".equals(pvBrand)) {
      query.setString("pvBrand", pvBrand);
    }
    if (pvModel != null && !"".equals(pvModel)) {
      query.setString("pvModel", pvModel);
    }
    if (pvYear != null && !"".equals(pvYear)) {
      query.setString("pvYear", pvYear);
    }
    if (pvEngine != null && !"".equals(pvEngine)) {
      query.setString("pvEngine", pvEngine);
    }
    return query;
  }

  public static Query searchInventorySearchIndexCountForOneVehicle(Session session, Long shopId, String productName, String productBrand,
                                                                   String productSpec, String productModel, String pvBrand, String pvModel,
                                                                   String pvYear, String pvEngine) {

    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(" select count(*) from InventorySearchIndex o where shopId=:shopId ");
    if (productName != null && !"".equals(productName)) {
      stringBuilder.append(" and productName =:productName ");
    }
    if (productBrand != null && !"".equals(productBrand)) {
      stringBuilder.append(" and productBrand =:productBrand ");
    }
    if (productSpec != null && !"".equals(productSpec)) {
      stringBuilder.append(" and productSpec =:productSpec ");
    }
    if (productModel != null && !"".equals(productModel)) {
      stringBuilder.append(" and productModel =:productModel ");
    }
    stringBuilder.append(" and ((brand = '全部') or (brand = '多款') ");
    if (pvBrand != null && !"".equals(pvBrand)) {
      stringBuilder.append(" or ( brand =:pvBrand ");
    }
    if (pvModel != null && !"".equals(pvModel)) {
      stringBuilder.append(" and model =:pvModel ");
    }
    if (pvYear != null && !"".equals(pvYear)) {
      stringBuilder.append(" and year =:pvYear ");
    }
    if (pvEngine != null && !"".equals(pvEngine)) {
      stringBuilder.append(" and engine =:pvEngine ) ");
    }
    if (pvEngine == null || "".equals(pvEngine)) {
      stringBuilder.append(" ) ");
    }
    stringBuilder.append(" ) ");
	  stringBuilder.append(" and (status is null or status <>'DISABLED')");
    Query query = session.createQuery(stringBuilder.toString()).setLong("shopId", shopId);
    if (productName != null && !"".equals(productName)) {
      query.setString("productName", productName);
    }
    if (productBrand != null && !"".equals(productBrand)) {
      query.setString("productBrand", productBrand);
    }
    if (productSpec != null && !"".equals(productSpec)) {
      query.setString("productSpec", productSpec);
    }
    if (productModel != null && !"".equals(productModel)) {
      query.setString("productModel", productModel);
    }
    if (pvBrand != null && !"".equals(pvBrand)) {
      query.setString("pvBrand", pvBrand);
    }
    if (pvModel != null && !"".equals(pvModel)) {
      query.setString("pvModel", pvModel);
    }
    if (pvYear != null && !"".equals(pvYear)) {
      query.setString("pvYear", pvYear);
    }
    if (pvEngine != null && !"".equals(pvEngine)) {
      query.setString("pvEngine", pvEngine);
    }
    return query;
  }

  public static Query getInventorySearchIndexByProductLocalInfoId(Session session, Long shopId,
                                                                  Long productLocalInfoId) {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(" select o from InventorySearchIndex o where o.shopId=:shopId and o.productId=:productLocalInfoId ");
    Query query = session.createQuery(stringBuilder.toString()).setLong("shopId", shopId).setLong("productLocalInfoId", productLocalInfoId);
    return query;
  }

	  public static Query getInventorySearchIndexByProductLocalInfoIds(Session session, Long shopId,
                                                                  Long ...productLocalInfoId) {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(" select o from InventorySearchIndex o where o.shopId=:shopId and o.productId in(:productLocalInfoId) ");
    Query query = session.createQuery(stringBuilder.toString()).setLong("shopId", shopId).setParameterList("productLocalInfoId", productLocalInfoId);
    return query;
  }

  public static Query getInventorySearchIndexByShopIdAndBarcode(Session session, Long shopId,
                                                                String barcode) {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(" select o from InventorySearchIndex o where o.shopId=:shopId and o.barcode=:barcode ");
    Query query = session.createQuery(stringBuilder.toString()).setLong("shopId", shopId).setString("barcode", barcode);
    return query;
  }

  public static Query getPurchaseInventoryInfoOfSupplier(Session session, Long supplierId, Long shopId, Long dateFrom, Long dateTo) {
    if(dateFrom==null && dateTo==null){
      String sql = "select i from ItemIndex i where shopId = :shopId and customerId = :supplierId and orderTypeEnum = :orderType order by orderTimeCreated desc";
      return session.createQuery(sql).setLong("supplierId", supplierId).setLong("shopId", shopId).setParameter("orderType", OrderTypes.INVENTORY);
    }else if(dateTo==null){
      String sql = "select i from ItemIndex i where shopId = :shopId and customerId = :supplierId and orderTimeCreated >= :dateFrom and orderTypeEnum = :orderType order by orderTimeCreated desc";
      return session.createQuery(sql).setLong("supplierId", supplierId).setLong("shopId", shopId).setLong("dateFrom",dateFrom).setParameter("orderType", OrderTypes.INVENTORY);
    }else if(dateFrom==null){
      String sql = "select i from ItemIndex i where shopId = :shopId and customerId = :supplierId and orderTimeCreated <= :dateTo and orderTypeEnum = :orderType order by orderTimeCreated desc";
      return session.createQuery(sql).setLong("supplierId", supplierId).setLong("shopId", shopId).setLong("dateTo",dateTo).setParameter("orderType", OrderTypes.INVENTORY);
    }else{
      String sql = "select i from ItemIndex i where shopId = :shopId and customerId = :supplierId and orderTimeCreated >= :dateFrom and orderTimeCreated <= :dateTo and orderTypeEnum = :orderType order by orderTimeCreated desc";
      return session.createQuery(sql).setLong("supplierId", supplierId).setLong("shopId", shopId).setLong("dateFrom",dateFrom).setLong("dateTo",dateTo).setParameter("orderType", OrderTypes.INVENTORY);
  }
  }

  public static Query getConsumeHistoryOfCustomer(Session session, Long customerId, Long shopId, Long dateFrom, List<OrderTypes> orderTypeEnums, Sort sort) {
    String sql = "select i from ItemIndex i where shopId = :shopId and customerId = :customerId and orderTypeEnum in (:orderTypes) and orderTimeCreated >= :dateFrom and itemType <> :itemType";
    if (sort != null) {
      sql += sort.toOrderString();
    } else {
      sql += " order by orderTimeCreated desc";
    }
    return session.createQuery(sql).setLong("dateFrom", dateFrom).setLong("customerId", customerId)
        .setLong("shopId", shopId).setParameterList("orderTypes", orderTypeEnums).setParameter("itemTypeEnum", ItemTypes.SALE_MEMBER_CARD_SERVICE);
  }

  public static Query getConsumeHistoryOfCustomer(Session session, Long customerId, Long shopId, Long dateFrom, List<OrderTypes> orderTypeEnums, Sort sort,int currentPage,int pageSize) {
    String sql = "select i from ItemIndex i where shopId = :shopId and customerId = :customerId and orderTypeEnum in (:orderTypes) and orderTimeCreated >= :dateFrom";
    if (sort != null) {
      sql += sort.toOrderString();
    } else {
      sql += " order by orderTimeCreated desc";
    }
    return session.createQuery(sql).setLong("dateFrom", dateFrom).setLong("customerId", customerId)
            .setLong("shopId", shopId).setFirstResult((currentPage-1)*pageSize).setMaxResults(pageSize).setParameterList("orderTypes", orderTypeEnums);
  }

  public static Query getConsumeHistoryOfCustomer(Session session, Long customerId, Long shopId, Long startTime, Long endTime, List<OrderTypes> orderTypeEnums, Pager pager) {
    StringBuffer sb = new StringBuffer();
    sb.append("select i from ItemIndex i where shopId = :shopId and customerId = :customerId and cardTimesLimitType is null ");
    if(CollectionUtils.isNotEmpty(orderTypeEnums)){
      sb.append("and orderTypeEnum in (:orderTypes) ");
    }
    if(startTime!=null){
      sb.append("and orderTimeCreated >= :startTime ");
    }
    if(endTime!=null){
      sb.append("and orderTimeCreated <= :endTime ");
    }
    sb.append("order by orderTimeCreated desc");
    Query q = session.createQuery(sb.toString()).setLong("customerId", customerId).setLong("shopId", shopId);
    if(CollectionUtils.isNotEmpty(orderTypeEnums)){
      q.setParameterList("orderTypes", orderTypeEnums);
    }
    if(startTime!=null){
      q.setLong("startTime",startTime);
    }
    if(endTime!=null){
      q.setLong("endTime",endTime);
    }
    if(pager!=null){
      q.setFirstResult(pager.getRowStart()).setMaxResults(pager.getPageSize());
    }
    return q;
  }

  public static Query countConsumeHistory(Session session, Long customerId, Long shopId,Long dateForm, List<OrderTypes> orderTypeEnums) {
    return session.createQuery("select count(DISTINCT i.orderId) from ItemIndex as i where i.shopId = :shopId and i.customerId = :customerId and orderTypeEnum in (:orderTypes) and orderTimeCreated >= :dateFrom")
    .setLong("shopId", shopId).setLong("customerId",customerId).setLong("dateFrom",dateForm).setParameterList("orderTypes",orderTypeEnums);
  }

  public static Query getPurchaseOrderNotInventoried(Session session, Long shopId, Long supplierId, Long dateFrom) {
	  Set<String> orderStatusEnum = new HashSet<String>();
	  orderStatusEnum.add(OrderStatus.PURCHASE_ORDER_WAITING.toString());
	  orderStatusEnum.add(OrderStatus.STOCKING.toString());
	  orderStatusEnum.add(OrderStatus.DISPATCH.toString());

    String sql = "select * from search.item_index where shop_id = :shopId and customer_id = :supplierId and order_type_enum = :orderType " +
        "and order_status_enum in (:orderStatusEnum) and order_time_created >= :dateFrom order by order_time_created desc ";
    return session.createSQLQuery(sql).addEntity(ItemIndex.class)
        .setLong("shopId", shopId).setLong("supplierId", supplierId).setString("orderType", OrderTypes.PURCHASE.toString())
        .setLong("dateFrom", dateFrom).setParameterList("orderStatusEnum",orderStatusEnum);
  }

  public static Query updateItemIndexByOrderId(Session session, Long orderId, double arrears, Long paymentTime) {
    StringBuffer hql = new StringBuffer();
	  if(paymentTime != null){
    hql.append("update ItemIndex i set i.arrears =:arrears ,i.paymentTime=:paymentTime ")
        .append("where i.orderId =:orderId");
	  }else {
		  hql.append("update ItemIndex i set i.arrears =:arrears ,i.paymentTime = null ")
        .append("where i.orderId =:orderId");
	  }
    Query query = session.createQuery(hql.toString()).setLong("orderId", orderId)
        .setDouble("arrears", arrears);
	  if(paymentTime!=null){
		   query.setLong("paymentTime", paymentTime);
	  }
    return query;
  }

  public static Query getUpdateItemIndexPurchaseOrderStatus(Session session, Long shopId, OrderTypes orderTypeEnum, Long purchaseOrderId, OrderStatus orderStatusEnum) {
    StringBuffer hql = new StringBuffer();
    hql.append("update ItemIndex i set i.orderStatusEnum = :orderStatusEnum " +
        " where i.shopId=:shopId and i.orderId =:purchaseOrderId " +
        "and i.orderTypeEnum = :orderTypeEnum");
    Query query = session.createQuery(hql.toString()).setLong("shopId", shopId).setLong("purchaseOrderId", purchaseOrderId)
        .setParameter("orderStatusEnum", orderStatusEnum)
        .setParameter("orderTypeEnum", orderTypeEnum);
    return query;
  }

  public static Query exactSearchInventorySearchIndex(Session session, SearchConditionDTO searchConditionDTO) {
	  if (searchConditionDTO.getProductId() != null) {
		  StringBuffer sb = new StringBuffer("select i from InventorySearchIndex i where shopId=:shopId ");
		  sb.append(" and productId=:productId ");
		  Query query = session.createQuery(sb.toString()).setLong("shopId", searchConditionDTO.getShopId());
		  query.setLong("productId", searchConditionDTO.getProductId());
		  return query;
	  } else {
		  StringBuffer sb = new StringBuffer("select i from InventorySearchIndex i where shopId=:shopId ");
		  sb.append(" and productName=:productName ");
		  sb.append(" and productBrand=:productBrand ");
		  sb.append(" and productSpec=:productSpec ");
		  sb.append(" and productModel=:productModel ");
		  if (StringUtils.isNotBlank(searchConditionDTO.getCommodityCode())) {
			  sb.append(" and commodityCode=:commodityCode ");
		  } else {
			  sb.append(" and (commodityCode is null or commodityCode='\0' or commodityCode='')");
		  }
		  if (StringUtils.isNotBlank(searchConditionDTO.getProductVehicleBrand())) {
			  sb.append(" and brand=:productVehicleBrand ");
		  } else {
			  sb.append(" and (brand is null or brand='\0' or brand='') ");
		  }
		  if (StringUtils.isNotBlank(searchConditionDTO.getProductVehicleModel())) {
			  sb.append(" and model=:productVehicleModel ");
		  } else {
			  sb.append(" and (model is null or model='\0' or model = '') ");
		  }

		  sb.append(" and (status is null or status <>'DISABLED')");
		  Query query = session.createQuery(sb.toString()).setLong("shopId", searchConditionDTO.getShopId());
		  query.setString("productName", searchConditionDTO.getProductName());
		  query.setString("productBrand", searchConditionDTO.getProductBrand());
		  query.setString("productSpec", searchConditionDTO.getProductSpec());
		  query.setString("productModel", searchConditionDTO.getProductModel());
		  if(StringUtils.isNotBlank(searchConditionDTO.getCommodityCode())){
			  query.setString("commodityCode", searchConditionDTO.getCommodityCode());
		  }
		  if (StringUtils.isNotBlank(searchConditionDTO.getProductVehicleBrand())) {
			  query.setString("productVehicleBrand", searchConditionDTO.getProductVehicleBrand());
		  }
		  if (StringUtils.isNotBlank(searchConditionDTO.getProductVehicleModel())) {
			  query.setString("productVehicleModel", searchConditionDTO.getProductVehicleModel());
		  }
		  return query;
	  }
  }


  //在itemIndex中查找单据，不包括具体的项目内容
  public static SQLQuery getRepairOrderHistory(Session session, long shopId, String licenceNo, String services,
                                               String materialName, Long fromTime, Long toTime,
                                               int startNo, int maxResult) {
    StringBuilder sb = new StringBuilder();
    sb.append("select i.vehicle as vehicle,i.order_id as orderId,i.order_type_enum as orderType,i.item_name as itemName, ")
        .append(" i.order_time_created as orderTimeCreated,i.order_total_amount as orderTotalAmount, ")
        .append(" i.order_status_enum as orderStatus,i.arrears as arrears,i.payment_time as paymentTime ")
        .append(" from item_index i where i.shop_id=:shopId ");
    if (StringUtils.isNotBlank(licenceNo)) {
      sb.append(" and i.vehicle=:licenceNo ");
    }
    if (StringUtils.isNotBlank(services)) {
      sb.append(" and i.item_name like :services and item_type_enum!='")
          .append(ItemTypes.MATERIAL.toString()).append("' ");
    }
    if (StringUtils.isNotBlank(materialName)) {
      sb.append(" and i.item_name like :materialName and item_type_enum='")
          .append(ItemTypes.MATERIAL.toString()).append("' ");
    }
    if (fromTime != null) {
      sb.append(" and i.order_time_created>=:fromTime ");
    }
    if (toTime != null) {
      sb.append(" and i.order_time_created<=:toTime ");
    }
    sb.append(" and i.order_type in ('4','5','6','7','9') ")
        .append(" group by i.order_id order by i.order_time_created desc ");
    SQLQuery query = session.createSQLQuery(sb.toString());
    if (StringUtils.isNotBlank(licenceNo)) {
      query.setString("licenceNo", licenceNo.trim());
    }
    if (StringUtils.isNotBlank(services)) {
      query.setString("services", services.trim() + "%");
    }
    if (StringUtils.isNotBlank(materialName)) {
      query.setString("materialName", materialName.trim() + "%");
    }
    if (fromTime != null) {
      query.setLong("fromTime", fromTime);
    }
    if (toTime != null) {
      query.setLong("toTime", toTime);
    }
    return (SQLQuery) query.setLong("shopId", shopId)
        .setFirstResult(startNo).setMaxResults(maxResult);
  }

    public static Query getRepairOrderHistory(Session session,List orderIds)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("select i from ItemIndex i");
      if (null != orderIds && 0 != orderIds.size()) {
            sb.append(" where i.orderId in :orderIds");
      } else {
            sb.append(" where i.shopId is null");
        }
        Query query = session.createQuery(sb.toString());
      if (null != orderIds && 0 != orderIds.size()) {
        query.setParameterList("orderIds", orderIds);
        }
        return query;
    }

    public static Query getRepairOrderIdFromItemIndex(Session session, long shopId, String licenceNo, String services,
                                               String materialName, Long fromTime, Long toTime,
                                               int startNo, int maxResult) {
    StringBuilder sb = new StringBuilder();
    sb.append("select i.orderId ")
        .append(" from ItemIndex i where i.shopId=:shopId ");
    if (StringUtils.isNotBlank(licenceNo)) {
      sb.append(" and i.vehicle=:licenceNo ");
    }
    if(StringUtils.isNotBlank(services) && StringUtils.isNotBlank(materialName))
    {
        sb.append(" and orderId in (select it.orderId from ItemIndex it where it.itemName like :services and it.itemTypeEnum!='")
                .append(ItemTypes.MATERIAL.toString()).append("') ");
        sb.append(" and (i.itemName like :materialName and itemTypeEnum='")
                .append(ItemTypes.MATERIAL.toString()).append("') ");

    }
    else
    {
        if (StringUtils.isNotBlank(services)) {
            sb.append(" and (i.itemName like :services and itemTypeEnum!='")
                    .append(ItemTypes.MATERIAL.toString()).append("') ");
        }
        if (StringUtils.isNotBlank(materialName)) {
            sb.append(" and (i.itemName like :materialName and itemTypeEnum='")
                    .append(ItemTypes.MATERIAL.toString()).append("') ");

        }
    }

    if (fromTime != null) {
      sb.append(" and i.orderTimeCreated>=:fromTime ");
    }
    if (toTime != null) {
      sb.append(" and i.orderTimeCreated<=:toTime ");
    }
    sb.append(" and i.orderTypeEnum in ('REPAIR','WASH','REPAIR_SALE','RECHARGE','WASH_MEMBER','WASH_BEAUTY')")
        .append(" group by i.orderId");
    Query query = session.createQuery(sb.toString());
    if (StringUtils.isNotBlank(licenceNo)) {
      query.setString("licenceNo", licenceNo.trim());
    }
    if (StringUtils.isNotBlank(services)) {
      query.setString("services", services.trim() + "%");
    }
    if (StringUtils.isNotBlank(materialName)) {
      query.setString("materialName", materialName.trim() + "%");
    }
    if (fromTime != null) {
      query.setLong("fromTime", fromTime);
    }
    if (toTime != null) {
      query.setLong("toTime", toTime);
    }
    return (Query) query.setLong("shopId", shopId)
        .setFirstResult(startNo).setMaxResults(maxResult);
  }


  public static Query countRepairOrderHistory(Session session, long shopId, String licenceNo, String services,
                                               String materialName, Long fromTime, Long toTime) {
    StringBuilder sb = new StringBuilder();
    sb.append("select count(*) from (");
    sb.append(" select i.order_id from item_index i where order_status_enum in ('REPAIR_DISPATCH', 'REPAIR_CHANGE', 'REPAIR_DONE', 'REPAIR_SETTLED', 'WASH_SETTLED') and i.shop_id=:shopId ");
    if (StringUtils.isNotBlank(licenceNo)) {
      sb.append(" and i.vehicle=:licenceNo ");
    }
    if (StringUtils.isNotBlank(services) && StringUtils.isNotBlank(materialName)) {
      sb.append(" and i.order_id in (select it.order_id from item_index it where it.item_name like :services and it.item_type_enum!='")
          .append(ItemTypes.MATERIAL.toString()).append("') ");
      sb.append(" and (i.item_name like :materialName and i.item_type_enum='")
          .append(ItemTypes.MATERIAL.toString()).append("') ");
    } else {
      if (StringUtils.isNotBlank(services)) {
        sb.append(" and (i.item_name like :services and i.item_type_enum!='")
            .append(ItemTypes.MATERIAL.toString()).append("') ");
      }
      if (StringUtils.isNotBlank(materialName)) {
        sb.append(" and (i.item_name like :materialName and i.item_type_enum='")
            .append(ItemTypes.MATERIAL.toString()).append("') ");
      }
    }
    if (fromTime != null) {
      sb.append(" and i.order_time_created>=:fromTime ");
    }
    if (toTime != null) {
      sb.append(" and i.order_time_created<=:toTime ");
    }
    sb.append(" and i.order_type_enum in ('REPAIR','WASH','REPAIR_SALE','RECHARGE','WASH_MEMBER','WASH_BEAUTY') ");
    sb.append(" group by i.order_id");
    sb.append(") as b");
    Query query = session.createSQLQuery(sb.toString());
    if (StringUtils.isNotBlank(licenceNo)) {
      query.setString("licenceNo", licenceNo.trim());
    }
    if (StringUtils.isNotBlank(services)) {
      query.setString("services", services.trim() + "%");
    }
    if (StringUtils.isNotBlank(materialName)) {
      query.setString("materialName", materialName.trim() + "%");
    }
    if (fromTime != null) {
      query.setLong("fromTime", fromTime);
    }
    if (toTime != null) {
      query.setLong("toTime", toTime);
    }
    return (Query) query.setLong("shopId", shopId);
  }



  public static SQLQuery getItemInfo(Session session, Long orderId, ItemTypes itemType) {
    return (SQLQuery) session.createSQLQuery("select item_name as itemName from item_index where order_id=:orderId and item_type_enum=:itemType")
        .setLong("orderId", orderId).setParameter("itemType", itemType.toString());
  }

  public static Query searchOrderIndexByOrder(Session session, Long shopId, Long orderId, OrderTypes orderTypeEnum, OrderStatus orderStatusEnum, Long customerOrSupplierId) {
    StringBuffer hql = new StringBuffer();
    hql.append(" select oi from OrderIndex oi where shopId = :shopId and orderId = :orderId and orderTypeEnum = :orderType ");
    return session.createQuery(hql.toString()).setLong("shopId", shopId).setLong("orderId", orderId)
        .setParameter("orderType", orderTypeEnum);

  }
  public static Query searchOrderIndexByShopIdAndCustomerOrSupplierId(Session session, Long shopId,Long customerOrSupplierId) {
    StringBuffer hql = new StringBuffer();
    hql.append(" select oi from OrderIndex oi where shopId = :shopId and customerOrSupplierId = :customerOrSupplierId");
    return session.createQuery(hql.toString()).setLong("shopId", shopId).setLong("customerOrSupplierId", customerOrSupplierId);
  }
    public static Query searchReturnAbleProducts(Session session, ItemIndexDTO itemIndexDTO , OrderTypes type) {
        StringBuffer hql = new StringBuffer();
        hql.append("select i from ItemIndex i where shopId = :shopId and orderTypeEnum = :orderType");
        if(itemIndexDTO.getCustomerOrSupplierName() != null && !StringUtil.isEmpty(itemIndexDTO.getCustomerOrSupplierName().trim())){
            hql.append(" and customerOrSupplierName = :customerOrSupplierName");
        }
        if(itemIndexDTO.getItemName() != null && !StringUtil.isEmpty(itemIndexDTO.getItemName().trim())){
            hql.append(" and itemName = :itemName");
        }
        if(itemIndexDTO.getItemBrand() != null){
            hql.append(" and itemBrand = :itemBrand");
        }
        if(itemIndexDTO.getItemSpec() != null){
            hql.append(" and itemSpec = :itemSpec");
        }
        if(itemIndexDTO.getItemModel() != null){
            hql.append(" and itemModel = :itemModel");
        }
        if(itemIndexDTO.getVehicleBrand() != null){
            hql.append(" and vehicleBrand = :vehicleBrand");
        }
        if(itemIndexDTO.getVehicleModel() != null){
            hql.append(" and vehicleModel = :vehicleModel");
        }
        if(itemIndexDTO.getVehicleYear() != null){
            hql.append(" and vehicleYear = :vehicleYear");
        }
        if(itemIndexDTO.getVehicleEngine() != null){
            hql.append(" and vehicleEngine = :vehicleEngine");
        }
        hql.append(" order by i.orderTimeCreated desc");
        Query query = session.createQuery(hql.toString()).setLong("shopId", itemIndexDTO.getShopId())
                    .setParameter("orderType", type);
        if(itemIndexDTO.getCustomerOrSupplierName() != null && !StringUtil.isEmpty(itemIndexDTO.getCustomerOrSupplierName().trim())){
            query.setString("customerOrSupplierName",itemIndexDTO.getCustomerOrSupplierName().trim());
        }
        if(itemIndexDTO.getItemName() != null && !StringUtil.isEmpty(itemIndexDTO.getItemName().trim())){
           query.setString("itemName",itemIndexDTO.getItemName().trim());
        }
        if(itemIndexDTO.getItemBrand() != null){
            query.setString("itemBrand", itemIndexDTO.getItemBrand().trim());
        }
        if(itemIndexDTO.getItemSpec() != null){
            query.setString("itemSpec",itemIndexDTO.getItemSpec().trim());
        }
        if(itemIndexDTO.getItemModel() != null){
            query.setString("itemModel",itemIndexDTO.getItemModel().trim());
        }
        if(itemIndexDTO.getVehicleBrand() != null){
            query.setString("vehicleBrand",itemIndexDTO.getVehicleBrand().trim());
        }
        if(itemIndexDTO.getVehicleModel() != null){
            query.setString("vehicleModel",itemIndexDTO.getVehicleModel().trim());
        }
        if(itemIndexDTO.getVehicleYear() != null){
            query.setString("vehicleYear",itemIndexDTO.getVehicleYear().trim());
        }
        if(itemIndexDTO.getVehicleEngine() != null){
            query.setString("vehicleEngine",itemIndexDTO.getVehicleEngine().trim());
        }
        return query;
    }

  public static Query getItemIndex(Session session,Long shopId,String supplierName,String itemName,String itemBrand,
                                   String itemSpec,String itemModel,Integer startNo,Integer maxRows){
    StringBuffer hql = new StringBuffer();
    hql.append("select i from ItemIndex i where i.orderTypeEnum =" + OrderTypes.INVENTORY.toString());
    hql.append(" and i.shopId =:shopId");
    if (StringUtils.isNotBlank(supplierName)) {
      hql.append(" and i.customerOrSupplierName =:supplierName ");
    }
    if (StringUtils.isNotBlank(itemName)) {
      hql.append(" and i.itemName =:itemName ");
    }
    if (StringUtils.isNotBlank(itemBrand)) {
      hql.append(" and i.itemBrand =:itemBrand ");
    }
    if (StringUtils.isNotBlank(itemSpec)) {
      hql.append(" and i.itemSpec =:itemSpec ");
    }
    if (StringUtils.isNotBlank(itemModel)) {
      hql.append(" and i.itemModel =:itemModel ");
    }
    hql.append(" group by i.customerId,i.productId ");
    Query query = session.createQuery(hql.toString()).setLong("shopId", shopId).setFirstResult(startNo).setMaxResults(maxRows);
    if (StringUtils.isNotBlank(supplierName)) {
      query.setString("supplierName", supplierName);
    }
    if (StringUtils.isNotBlank(itemName)) {
      query.setString("itemName", itemName);
    }
    if (StringUtils.isNotBlank(itemBrand)) {
      query.setString("itemBrand", itemBrand);
    }
    if (StringUtils.isNotBlank(itemSpec)) {
      query.setString("itemSpec", itemSpec);
    }
    if (StringUtils.isNotBlank(itemModel)) {
      query.setString("itemModel", itemModel);
    }
    return query;
  }

  public static Query countItemIndexWithItemIndexDTO(Session session,Long shopId,String supplierName,String itemName,String itemBrand,
                                   String itemSpec,String itemModel){
    StringBuffer hql = new StringBuffer();
    hql.append("select count(i) from ItemIndex i where i.orderTypeEnum =" + OrderTypes.INVENTORY.toString());
    hql.append(" and i.shopId =:shopId");
    if (StringUtils.isNotBlank(supplierName)) {
      hql.append(" and i.customerOrSupplierName =:supplierName ");
    }
    if (StringUtils.isNotBlank(itemName)) {
      hql.append(" and i.itemName =:itemName ");
    }
    if (StringUtils.isNotBlank(itemBrand)) {
      hql.append(" and i.itemBrand =:itemBrand ");
    }
    if (StringUtils.isNotBlank(itemSpec)) {
      hql.append(" and i.itemSpec =:itemSpec ");
    }
    if (StringUtils.isNotBlank(itemModel)) {
      hql.append(" and i.itemModel =:itemModel ");
    }
    hql.append(" group by i.customerId,i.productId ");
    Query query = session.createQuery(hql.toString()).setLong("shopId", shopId);
    if (StringUtils.isNotBlank(supplierName)) {
      query.setString("supplierName", supplierName);
    }
    if (StringUtils.isNotBlank(itemName)) {
      query.setString("itemName", itemName);
    }
    if (StringUtils.isNotBlank(itemBrand)) {
      query.setString("itemBrand", itemBrand);
    }
    if (StringUtils.isNotBlank(itemSpec)) {
      query.setString("itemSpec", itemSpec);
    }
    if (StringUtils.isNotBlank(itemModel)) {
      query.setString("itemModel", itemModel);
    }
    return query;
  }

    public static Query searchReturnTotal(Session session, ItemIndexDTO itemIndexDTO, Integer startNo) {
        StringBuffer hql = new StringBuffer();
        hql.append("select i from ItemIndex i where i.orderType = 2 and i.shopId = " + itemIndexDTO.getShopId());
        if(itemIndexDTO.getCustomerOrSupplierName() != null && !StringUtil.isEmpty(itemIndexDTO.getCustomerOrSupplierName().trim())){
            hql.append(" and customerOrSupplierName like :customerOrSupplierName");
        }
        if(itemIndexDTO.getItemName() != null && !StringUtil.isEmpty(itemIndexDTO.getItemName().trim())){
            hql.append(" and itemName like :itemName");
        }
        if(itemIndexDTO.getItemBrand() != null && !StringUtil.isEmpty(itemIndexDTO.getItemBrand().trim())){
            hql.append(" and itemBrand like :itemBrand");
        }
        if(itemIndexDTO.getItemSpec() != null && !StringUtil.isEmpty(itemIndexDTO.getItemSpec().trim())){
            hql.append(" and itemSpec like :itemSpec");
        }
        if(itemIndexDTO.getItemModel() != null && !StringUtil.isEmpty(itemIndexDTO.getItemModel().trim())){
            hql.append(" and itemModel like :itemModel");
        }
        hql.append(" group by i.customerOrSupplierName,i.itemName,i.itemBrand,i.itemSpec,i.itemModel,i.vehicleBrand," +
                "i.vehicleModel,i.vehicleYear,i.vehicleEngine");
      LOG.debug("searchReturnTotal: " + hql.toString());
        Query query = session.createQuery(hql.toString());
        if(itemIndexDTO.getCustomerOrSupplierName() != null && !StringUtil.isEmpty(itemIndexDTO.getCustomerOrSupplierName().trim())){
            query.setString("customerOrSupplierName","%" + itemIndexDTO.getCustomerOrSupplierName().trim() + "%");
        }
        if(itemIndexDTO.getItemName() != null && !StringUtil.isEmpty(itemIndexDTO.getItemName().trim())){
           query.setString("itemName","%" + itemIndexDTO.getItemName().trim() + "%");
        }
        if(itemIndexDTO.getItemBrand() != null && !StringUtil.isEmpty(itemIndexDTO.getItemBrand().trim())){
            query.setString("itemBrand", "%" + itemIndexDTO.getItemBrand().trim() + "%");
        }
        if(itemIndexDTO.getItemSpec() != null && !StringUtil.isEmpty(itemIndexDTO.getItemSpec().trim())){
            query.setString("itemSpec","%" + itemIndexDTO.getItemSpec().trim() + "%");
        }
        if(itemIndexDTO.getItemModel() != null && !StringUtil.isEmpty(itemIndexDTO.getItemModel().trim())){
            query.setString("itemModel","%" + itemIndexDTO.getItemModel().trim() + "%");
        }
        if(startNo != null){
            query.setFirstResult(startNo).setMaxResults(5);
        }
      LOG.debug("searchReturnTotal query list size is  " + query.list().size());
        return query;
    }

    public static Query countInwareHistory(Session session, ItemIndexDTO itemIndexDTO) {
        StringBuffer hql = new StringBuffer();
        hql.append("select count(*) from (select a.* from item_index a where a.order_type = 2 and shop_id = "+itemIndexDTO.getShopId());
        if(itemIndexDTO.getCustomerOrSupplierName() != null && !StringUtil.isEmpty(itemIndexDTO.getCustomerOrSupplierName().trim())){
            hql.append(" and a.customer_or_supplier_name like :customerOrSupplierName");
        }
        if(itemIndexDTO.getItemName() != null && !StringUtil.isEmpty(itemIndexDTO.getItemName().trim())){
            hql.append(" and a.item_name like :itemName");
        }
        if(itemIndexDTO.getItemBrand() != null && !StringUtil.isEmpty(itemIndexDTO.getItemBrand().trim())){
            hql.append(" and a.item_brand like :itemBrand");
        }
        if(itemIndexDTO.getItemSpec() != null && !StringUtil.isEmpty(itemIndexDTO.getItemSpec().trim())){
            hql.append(" and a.item_spec like :itemSpec");
        }
        if(itemIndexDTO.getItemModel() != null && !StringUtil.isEmpty(itemIndexDTO.getItemModel().trim())){
            hql.append(" and a.item_model like :itemModel");
        }
        hql.append(" group by customer_or_supplier_name,item_name,item_brand,item_spec,item_model,vehicle_brand,vehicle_model" +
                ") as b");

        Query query = session.createSQLQuery(hql.toString());
        if(itemIndexDTO.getCustomerOrSupplierName() != null && !StringUtil.isEmpty(itemIndexDTO.getCustomerOrSupplierName().trim())){
            query.setString("customerOrSupplierName","%" + itemIndexDTO.getCustomerOrSupplierName().trim() + "%");
        }
        if(itemIndexDTO.getItemName() != null && !StringUtil.isEmpty(itemIndexDTO.getItemName().trim())){
           query.setString("itemName","%" + itemIndexDTO.getItemName().trim() + "%");
        }
        if(itemIndexDTO.getItemBrand() != null && !StringUtil.isEmpty(itemIndexDTO.getItemBrand().trim())){
            query.setString("itemBrand", "%" + itemIndexDTO.getItemBrand().trim() + "%");
        }
        if(itemIndexDTO.getItemSpec() != null && !StringUtil.isEmpty(itemIndexDTO.getItemSpec().trim())){
            query.setString("itemSpec","%" + itemIndexDTO.getItemSpec().trim() + "%");
        }
        if(itemIndexDTO.getItemModel() != null && !StringUtil.isEmpty(itemIndexDTO.getItemModel().trim())){
            query.setString("itemModel","%" + itemIndexDTO.getItemModel().trim() + "%");
        }

        return query;
    }

    public static Query searchInventorySearchIndexAmount(Session session, Long shopId, String productName, String productBrand,
                                                                String productSpec, String productModel, String pvBrand, String pvModel,
                                                                String pvYear, String pvEngine, String commodityCode) {

    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(" select o from InventorySearchIndex o where shopId=:shopId ");
    if (productName != null && !"".equals(productName)) {
      stringBuilder.append(" and productName =:productName ");
    }else {
	    stringBuilder.append(" and (productName is null or productName = '\0' or productName = '')");
    }
    if (productBrand != null) {
      stringBuilder.append(" and productBrand =:productBrand ");
    }else {
	    stringBuilder.append(" and (productBrand is null or productBrand = '\0' or productBrand = '')");
    }
    if (productSpec != null) {
      stringBuilder.append(" and productSpec =:productSpec ");
    }else {
	    stringBuilder.append(" and (productSpec is null or productSpec = '\0' or productSpec = '')");
    }
    if (productModel != null) {
      stringBuilder.append(" and productModel =:productModel ");
    }else {
	    stringBuilder.append(" and (productModel is null or productModel = '\0' or productModel = '')");
    }
//    stringBuilder.append(" and (brand='全部' or brand='多款' ");
//    stringBuilder.append(" or ( 1=1 ");
    if (pvBrand != null) {
      stringBuilder.append(" and brand =:pvBrand ");
    }else {
	    stringBuilder.append(" and (brand is null or brand = '\0' or brand = '')");
    }
    if (pvModel != null) {
      stringBuilder.append(" and model =:pvModel ");
    } else {
	    stringBuilder.append(" and (model is null or model = '\0' or model = '')");
    }
    if (pvYear != null) {
      stringBuilder.append(" and year =:pvYear ");
    } else {
	    stringBuilder.append(" and (year is null or year = '\0' or year = '')");
    }
    if (pvEngine != null) {
      stringBuilder.append(" and engine =:pvEngine ");
    } else {
	    stringBuilder.append(" and (engine is null or engine = '\0' or engine = '')");
    }
//    stringBuilder.append(" )) ");
	  stringBuilder.append(" and (status is null or status <>'DISABLED')");
    Query query = session.createQuery(stringBuilder.toString()).setLong("shopId", shopId);
    if (productName != null && !"".equals(productName)) {
      query.setString("productName", productName);
    }
    if (productBrand != null) {
      query.setString("productBrand", productBrand);
    }
    if (productSpec != null) {
      query.setString("productSpec", productSpec);
    }
    if (productModel != null) {
      query.setString("productModel", productModel);
    }
    if (pvBrand != null) {
      query.setString("pvBrand", pvBrand);
    }
    if (pvModel != null) {
      query.setString("pvModel", pvModel);
    }
    if (pvYear != null) {
      query.setString("pvYear", pvYear);
    }
    if (pvEngine != null) {
      query.setString("pvEngine", pvEngine);
    }
    return query;
  }

  public static Query getInventorySearchIndex(Session session, Long shopId, Long startId, int num) {
    StringBuffer sb = new StringBuffer();
    sb.append("select i from InventorySearchIndex as i where i.id > :id");
    if (shopId != null)
      sb.append(" and i.shopId=:shopId ");
    Query query = session.createQuery(sb.toString()).setLong("id", startId);
    if (shopId != null)
      query.setLong("shopId", shopId);
    return query.setMaxResults(num);
  }

  public static Query getCurrentUsedProduct(Session session, SearchMemoryConditionDTO searchMemoryConditionDTO) {
    String searchField = searchMemoryConditionDTO.getSearchField();
    if (SearchConditionDTO.PRODUCT_INFO.equals(searchField)) {
      searchField = SearchConditionDTO.PRODUCT_NAME;
    }
    return session.createSQLQuery("select * from current_used_product p where p.shop_id=:shopId and p.type=:searchField order by p.time_order desc ").addEntity(CurrentUsedProduct.class)
        .setLong("shopId", searchMemoryConditionDTO.getShopId()).setString("searchField", searchField);
  }

  public static Query getAllSolrMatchStopWordList(Session session,Long shopId) {
    return session.createQuery("from SolrMatchStopWord  where shopId=:shopId")
        .setLong("shopId",shopId);
  }
  public static Query updateOrderIndexStatus(Session session, Long shopId, OrderTypes orderType, Long orderId, OrderStatus newStatus) {
    return session.createQuery("update OrderIndex oi set oi.orderStatusEnum=:newStatusEnum " +
        "where oi.shopId =:shopId " +
        "and oi.orderTypeEnum = :orderTypeEnum  and oi.orderId =:orderId")
        .setParameter("newStatusEnum", newStatus)
        .setLong("shopId", shopId)
        .setLong("orderId", orderId)
        .setParameter("orderTypeEnum", orderType);
  }

  public static Query getOrderIndexForReindexByRange(Session session,Long shopId,long startId,long endId){
    StringBuffer sb = new StringBuffer("from OrderIndex where id>:startId and id<=:endId");
    if(shopId!=null)
      sb.append(" and shopId=:shopId");
    sb.append(" order by id asc");
    Query query=session.createQuery(sb.toString()).setLong("startId", startId).setLong("endId",endId);
    if(shopId!=null)
      query.setLong("shopId",shopId);
    return query;
  }

  public static Query getOrderIndexForReindex(Session session,Long shopId,long startId,int num){
    StringBuffer sb=new StringBuffer("from OrderIndex where id>:startId");
    if(shopId!=null)
      sb.append(" and shopId=:shopId");
    Query query=session.createQuery(sb.toString()).setLong("startId",startId);
    if(shopId!=null)
      query.setLong("shopId",shopId);
    return query.setMaxResults(num);
  }


  public static Query getCurrentUsedVehicle(Session session, SearchConditionDTO searchConditionDTO) {
    return session.createQuery("select c from CurrentUsedVehicle c where c.shopId=:shopId  order by c.timeOrder desc")
        .setLong("shopId", searchConditionDTO.getShopId());
  }

  public static Query getInventorySearchIndexByProductId(Session session, Long productId) {
    Query query = session.createQuery("select i from InventorySearchIndex as i where i.productId=:productId").setLong("productId", productId);
    return query;
  }

  public static Query getItemIndexByOrderIdAndItemIdAndOrderType(Session session, Long orderId,Long itemId, OrderTypes orderType) {
     return  session.createSQLQuery("select * from item_index where order_id=:orderId and item_id=:itemId and order_type_enum =:orderType").addEntity(ItemIndex.class)
              .setLong("orderId",orderId).setLong("itemId",itemId).setString("orderType", orderType.toString());

  }
  /**
   *
   * @param session
   * @param shopId
   * @param startTime
   * @param endTime
   * @return
   */
   public static Query getWashOrderItemIndexList(Session session, long shopId, long startTime, long endTime,int pageNo,int pageSize,String arrayType) {

     StringBuffer sb = new StringBuffer();
     sb.append(" select * from " +
         " ( ( select * from item_index where shop_id = :washBeautyShopId and order_time_created >= :washBeautyStartTime and order_time_created < :washBeautyEndTime " +
         " and order_type_enum =:washBeautyType and order_status_enum != 'WASH_REPEAL' group by order_id ) " +
         " union all" +
         " ( select * from item_index where shop_id = :shopId and created >= :startTime and created < :endTime " +
         " and ((order_type_enum =:orderType and item_type_enum =:itemType ) or (order_type_enum =:orderType1 and item_type_enum =:itemType1 ) or (order_type_enum =:orderType2 and item_type_enum =:itemType2 ) ) " +
         " and order_status_enum =:orderStatus ) " +
         " )as item ");
     if(StringUtil.isEmpty(arrayType)){
       arrayType = " order by created desc " ;
     }
     sb.append(arrayType);
     Query query = session.createSQLQuery(sb.toString()).addEntity(ItemIndex.class)
         .setLong("washBeautyShopId",shopId)
         .setParameter("washBeautyType",OrderTypes.WASH_BEAUTY.toString())
         .setLong("washBeautyStartTime", startTime)
         .setLong("washBeautyEndTime", endTime)
         .setLong("shopId", shopId)
         .setLong("startTime", startTime)
         .setLong("endTime", endTime)
         .setParameter("orderType", OrderTypes.RECHARGE.toString())
         .setParameter("itemType", ItemTypes.RECHARGE.toString())
         .setParameter("orderType1", OrderTypes.WASH.toString())
         .setParameter("itemType1", ItemTypes.WASH.toString())
         .setParameter("orderType2", OrderTypes.WASH_MEMBER.toString())
         .setParameter("itemType2", ItemTypes.WASH_MEMBER.toString())
         .setParameter("orderStatus", OrderStatus.WASH_SETTLED.toString())
         .setFirstResult((pageNo - 1) * pageSize)
         .setMaxResults(pageSize);
     return query;
   }

  public static Query getWashItemIndexList(Session session, long shopId, long startTime, long endTime) {


     StringBuffer sb = new StringBuffer();
     sb.append(" select * from item_index where shop_id = :shopId and created >= :startTime and created < :endTime " +
         " and ((order_type_enum =:orderType and item_type_enum =:itemType ) or (order_type_enum =:orderType1 and item_type_enum =:itemType1 ) or (order_type_enum =:orderType2 and item_type_enum =:itemType2 ) ) " +
         " and order_status_enum =:orderStatus ");

     Query query = session.createSQLQuery(sb.toString()).addEntity(ItemIndex.class)
         .setLong("shopId", shopId)
         .setLong("startTime", startTime)
         .setLong("endTime", endTime)
         .setParameter("orderType", OrderTypes.RECHARGE.toString())
         .setParameter("itemType", ItemTypes.RECHARGE.toString())
         .setParameter("orderType1", OrderTypes.WASH.toString())
         .setParameter("itemType1", ItemTypes.WASH.toString())
         .setParameter("orderType2", OrderTypes.WASH_MEMBER.toString())
         .setParameter("itemType2", ItemTypes.WASH_MEMBER.toString())
         .setParameter("orderStatus", OrderStatus.REPAIR_SETTLED.toString());

     return query;
   }

  public static Query getSalesOrderItemIndexList(Session session, long shopId,String idString,String arrayType) {
    StringBuffer sb = new StringBuffer();
     sb.append(" select *  from item_index where shop_id = :shopId");
     if(!StringUtil.isEmpty(idString)){
       sb.append(" and order_id in ").append(idString);
     }
     if(StringUtil.isEmpty(arrayType)){
       arrayType = " order by created desc " ;
     }
     sb.append(arrayType);
    return session.createSQLQuery(sb.toString())
        .addEntity(ItemIndex.class)
        .setLong("shopId", shopId);
  }


  public static Query getRepairOrderItemIndexList(Session session, long shopId,String idString,String arrayType) {
    StringBuffer sb = new StringBuffer();
     sb.append(" select *  from item_index where shop_id = :shopId " +
         " and order_type_enum =:orderType and ( item_type_enum =:itemType or item_type_enum =:itemType1 ) ");

    if (!StringUtil.isEmpty(idString)) {
      sb.append(" and order_id in ").append(idString);
    }

     if(StringUtil.isEmpty(arrayType)){
       arrayType = " order by created desc " ;
     }
     sb.append(arrayType);

    return session.createSQLQuery(sb.toString())
        .addEntity(ItemIndex.class)
        .setLong("shopId", shopId)
        .setParameter("orderType", OrderTypes.REPAIR.toString())
        .setParameter("itemType", ItemTypes.SERVICE.toString())
        .setParameter("itemType1", ItemTypes.MATERIAL.toString());
  }


  public static Query getLowerLimitInventorySearchIndex(Session session, Pager pager, String sortStr, Long shopId) {
    StringBuffer sb = new StringBuffer();
    if (StringUtils.isNotBlank(sortStr)) {
      sb.append("select i from  InventorySearchIndex i where i.shopId =:shopId " +
          "and i.amount < (i.lowerLimit - 0.0001) and i.lowerLimit is not null and (i.status <>'DISABLED' or i.status is null)");
      sb.append(" order by i." + sortStr);
    } else {
      sb.append("select i from InventorySearchIndex i where  i.shopId =:shopId " +
          "and i.amount < (i.lowerLimit - 0.0001) and i.lowerLimit is not null and (i.status <>'DISABLED'or i.status is null)");
    }
    Query query = session.createQuery(sb.toString());
    query.setLong("shopId", shopId)
        .setFirstResult(pager.getRowStart())
        .setMaxResults(pager.getPageSize());
    return query;
  }

  public static Query getUpperLimitInventorySearchIndex(Session session, Pager pager, String sortStr, Long shopId) {
        StringBuffer sb = new StringBuffer();

    if (StringUtils.isNotBlank(sortStr)) {
      sb.append("select i from InventorySearchIndex i where i.shopId =:shopId " +
          "and i.amount > (i.upperLimit + 0.0001) and i.upperLimit is not null  and i.upperLimit !=0 and (i.status <>'DISABLED' or i.status is null)");
      sb.append(" order by i." + sortStr);
    } else {
      sb.append("select i from  InventorySearchIndex i where i.shopId =:shopId " +
          "and i.amount > (i.upperLimit + 0.0001) and i.upperLimit is not null and i.upperLimit !=0 and (i.status <>'DISABLED' or i.status is null)");
    }
    Query query = session.createQuery(sb.toString());
    query.setLong("shopId", shopId)
        .setFirstResult(pager.getRowStart())
        .setMaxResults(pager.getPageSize());
    return query;
  }
  public static Query getWashItemTotal(Session session,long shopId, long startTime, long endTime){

   StringBuffer sb = new StringBuffer();
     sb.append(" select count(*),sum(item.order_total_amount),sum(item.after_member_discount_order_total) from" +
         " ( ( select order_total_amount,after_member_discount_order_total from item_index where shop_id =:washBeautyShopId" +
         " and order_type_enum =:washBeautyType and order_time_created >= :washBeautyStartTime and order_time_created < :washBeautyEndTime and order_status_enum != 'WASH_REPEAL' " +
         " group by order_id ) " +
         " union all " +
         " ( select order_total_amount,after_member_discount_order_total from item_index where shop_id = :shopId and created >= :startTime and created < :endTime " +
         " and ((order_type_enum =:orderType and item_type_enum =:itemType ) or (order_type_enum =:orderType1 and item_type_enum =:itemType1 ) or (order_type_enum =:orderType2 and item_type_enum =:itemType2 ) ) " +
         " and order_status_enum =:orderStatus ) " +
         " ) as item ");
     Query query = session.createSQLQuery(sb.toString())
         .setLong("washBeautyShopId",shopId)
         .setParameter("washBeautyType",OrderTypes.WASH_BEAUTY.toString())
         .setLong("washBeautyStartTime", startTime)
         .setLong("washBeautyEndTime", endTime)
         .setLong("shopId", shopId)
         .setLong("startTime", startTime)
         .setLong("endTime", endTime)
         .setParameter("orderType", OrderTypes.RECHARGE.toString())
         .setParameter("itemType", ItemTypes.RECHARGE.toString())
         .setParameter("orderType1", OrderTypes.WASH.toString())
         .setParameter("itemType1", ItemTypes.WASH.toString())
         .setParameter("orderType2", OrderTypes.WASH_MEMBER.toString())
         .setParameter("itemType2", ItemTypes.WASH_MEMBER.toString())
         .setParameter("orderStatus", OrderStatus.WASH_SETTLED.toString());

     return query;
   }

   /**
   * 根据customerId，统计洗车金额
   *
   * @param session
   * @param shopId
   * @param customerId
   * @return
   */
  public static Query getWashTotalByCustomerId(Session session, long shopId, long customerId) {
    return session.createSQLQuery(" select sum(order_total_amount) as total from item_index where shop_id = :shopId and customer_id =:customerId " +
        " and ((order_type_enum =:orderType and item_type_enum =:itemType ) or (order_type_enum =:orderType1 and item_type_enum =:itemType1 ) or" +
        " (order_type_enum =:orderType2 and item_type_enum =:itemType2 ) ) ")
        .addScalar("total", StandardBasicTypes.DOUBLE)
        .setLong("shopId", shopId)
        .setLong("customerId", customerId)
        .setParameter("orderType", OrderTypes.RECHARGE.toString())
        .setParameter("itemType", ItemTypes.RECHARGE.toString())
        .setParameter("orderType1", OrderTypes.WASH.toString())
        .setParameter("itemType1", ItemTypes.WASH.toString())
        .setParameter("orderType2", OrderTypes.WASH_MEMBER.toString())
        .setParameter("itemType2", ItemTypes.WASH_MEMBER.toString());
  }


  /**
   * 根据orderId，shopId 获得itemIndex
   *
   * @param session
   * @param shopId
   * @param orderId
   * @return
   */
  public static Query getItemIndexDTOListByOrderId(Session session, long shopId, long orderId) {
    StringBuffer sb = new StringBuffer();
     sb.append(" select *  from item_index where shop_id = :shopId " +
         " and order_id =:orderId ");
    return session.createSQLQuery(sb.toString())
        .addEntity(ItemIndex.class)
        .setLong("shopId", shopId)
        .setLong("orderId", orderId);
  }

  /**
   * 根据orderId和shopId获得orderIndex
   * @param session
   * @param shopId
   * @param orderId
   * @return
   */
  public static Query getOrderIndexDTOByOrderId(Session session, Long shopId, Long orderId) {
    StringBuffer hql = new StringBuffer();
    hql.append(" select oi from OrderIndex oi where shopId = :shopId and orderId = :orderId ");
    return session.createQuery(hql.toString()).setLong("shopId", shopId).setLong("orderId", orderId);

  }
  public static Query getItemIndexDTOByOrderItemId(Session session, Long shopId,Long orderId, Long orderItemId) {
    StringBuffer hql = new StringBuffer();
    hql.append(" select oi from ItemIndex oi where shopId = :shopId and orderId =:orderId and itemId = :orderItemId ");
    return session.createQuery(hql.toString()).setLong("shopId", shopId).setLong("orderId", orderId).setLong("orderItemId", orderItemId);

  }
  public static Query getOrderIndexDTOByArrayOrderId(Session session, Long shopId, Long startId, Long endId) {
    StringBuffer sb = new StringBuffer("from OrderIndex where id>:startId and c.id<=:endId");
    if (shopId != null) {
      sb.append(" and shopId=:shopId");
    }
    Query query = session.createQuery(sb.toString()).setLong("startId", startId).setLong("endId", endId);
    if (shopId != null) {
      query.setLong("shopId", shopId);
    }
    sb.append(" order by customerId asc");
    return query;
  }

   /**
   * 根据shopId获得itemIndex
   *
   * @param session
   * @param shopId
   * @return
   */
  public static Query countItemIndexByShopId(Session session, Long shopId) {
    return session.createQuery("select count(*) from ItemIndex as c where c.shopId = :shopId")
        .setLong("shopId", shopId);

  }

  /**
   * 根据shopId获得itemIndex
   * @param session
   * @param shopId
   * @return
   */
  public static Query getItemIndexListByShopId(Session session, Long shopId,int pageNum,int pageSize) {
    return session.createQuery("select c from ItemIndex  c where c.shopId = :shopId ")
        .setLong("shopId", shopId).setFirstResult(pageNum * pageSize).setMaxResults(pageSize);
  }

  /**
   * 根据shopId获得orderIndex
   * @param session
   * @param shopId
   * @return
   */
  public static Query countOrderIndexByShopId(Session session, Long shopId) {
    return session.createQuery("select count(*) from OrderIndex as c where c.shopId = :shopId")
        .setLong("shopId", shopId);

  }

   /**
   * 根据shopId获得orderIndex
   *
   * @param session
   * @param shopId
   * @param pageNum
   * @param pageSize
   * @return
   */
  public static Query getOrderIndexListByShopId(Session session, Long shopId, int pageNum, int pageSize) {
    return session.createQuery("select c from OrderIndex  c where c.shopId = :shopId")
        .setLong("shopId", shopId).setFirstResult(pageNum * pageSize).setMaxResults(pageSize);
  }

  /**
   * 查询当日新增客户【车辆】历史记录数
   *
   * @param session
   * @param shopId
   * @param licenceNo
   * @param services
   * @param materialName
   * @param fromTime
   * @param toTime
   * @author zhangchuanlong
   * @return
   */
  public static Query countRepairOrderHistoryByTodayNewVehicle(Session session, long shopId, String licenceNo, String services,
                                               String materialName, Long fromTime, Long toTime,List<String> licenceNoList)  {
    StringBuilder sb = new StringBuilder();
    sb.append("select count(*) from")
        .append(" (select i.order_id,i.vehicle as vehicle from item_index i where i.shop_id=:shopId ");
    if (StringUtils.isNotBlank(licenceNo) && licenceNoList != null && licenceNoList.size() > 0) {
      for (String s : licenceNoList) {
        if (licenceNo.equals(s)) {
          sb.append(" and i.vehicle=:licenceNo ");
          break;
        }
      }
    }
    if (StringUtils.isNotBlank(services) && StringUtils.isNotBlank(materialName)) {
      sb.append(" and i.order_id in (select it.order_id from item_index it where it.item_name like :services and it.item_type_enum!='")
          .append(ItemTypes.MATERIAL.toString()).append("') ");
      sb.append(" and (i.item_name like :materialName and i.item_type_enum='")
          .append(ItemTypes.MATERIAL.toString()).append("') ");

    } else {
      if (StringUtils.isNotBlank(services)) {
        sb.append(" and (i.item_name like :services and i.item_type_enum!='")
            .append(ItemTypes.MATERIAL.toString()).append("') ");
      }
      if (StringUtils.isNotBlank(materialName)) {
        sb.append(" and (i.item_name like :materialName and i.item_type_enum='")
            .append(ItemTypes.MATERIAL.toString()).append("') ");
      }
    }
    if (fromTime != null) {
      sb.append(" and i.order_time_created>=:fromTime ");
    }
    if (toTime != null) {
      sb.append(" and i.order_time_created<=:toTime ");
    }
    if (!StringUtils.isNotBlank(licenceNo) && licenceNoList != null && licenceNoList.size() > 0) {
      sb.append(" and  i.vehicle in:licenceNoList");
    }
    try {
      sb.append(" and i.order_type_enum in ('REPAIR','WASH','REPAIR_SALE','RECHARGE','WASH_MEMBER','WASH_BEAUTY') ")
          .append(" group by i.order_id) as p");
    } catch (Exception e) {
      LOG.error(e.getMessage(),e);  //To change body of catch statement use File | Settings | File Templates.
    }
    Query query = session.createSQLQuery(sb.toString());
  if (StringUtils.isNotBlank(licenceNo) && licenceNoList != null && licenceNoList.size() > 0) {
      for (String s : licenceNoList) {
        if (licenceNo.equals(s)) {
         query.setString("licenceNo", licenceNo.trim());
          break;
        }
      }

    }
    if (StringUtils.isNotBlank(services)) {
      query.setString("services", services.trim() + "%");
    }
    if (StringUtils.isNotBlank(materialName)) {
      query.setString("materialName", materialName.trim() + "%");
    }
    if (fromTime != null) {
      query.setLong("fromTime", fromTime);
    }
    if (toTime != null) {
      query.setLong("toTime", toTime);
    }
    if (!StringUtils.isNotBlank(licenceNo) && licenceNoList != null && licenceNoList.size() > 0) {
      query.setParameterList("licenceNoList", licenceNoList);
    }
    return query.setLong("shopId", shopId);
  }

  /**
   *       查询今天新增客户【车辆】历史记录
   *
   * @param session
   * @param shopId
   * @param licenceNo               车牌号
   * @param services                   施工项目
   * @param materialName        材料品名
   * @param fromTime
   * @param toTime
   * @param pager
   * @param licenceNos              当天新增【车辆】列表
   * @author zhangchuanlong
   * @return
   */
  public static Query getRepairOrderIdFromItemIndexByNewVehicle(Session session, long shopId, String licenceNo, String services,
                                                                 String materialName, Long fromTime, Long toTime, Pager pager,List<String> licenceNos) {
    StringBuilder sb = new StringBuilder();
    sb.append("select i.* from item_index i where i.shop_id=:shopId ");
    if (StringUtils.isNotBlank(licenceNo) && licenceNos != null && licenceNos.size() > 0) {
      for (String s : licenceNos) {
        if (licenceNo.equals(s)) {
          sb.append(" and i.vehicle=:licenceNo ");
          break;
        }
      }
    }
    if (StringUtils.isNotBlank(services) && StringUtils.isNotBlank(materialName)) {
      sb.append(" and i.order_id in (select it.order_id from item_index it where it.item_name like :services and it.item_type_enum!='")
          .append(ItemTypes.MATERIAL.toString()).append("') ");
      sb.append(" and (i.item_name like :materialName and i.item_type_enum='")
          .append(ItemTypes.MATERIAL.toString()).append("') ");
    } else {
      if (StringUtils.isNotBlank(services)) {
        sb.append(" and (i.item_name like :services and i.item_type_enum!='")
            .append(ItemTypes.MATERIAL.toString()).append("') ");
      }
      if (StringUtils.isNotBlank(materialName)) {
        sb.append(" and (i.item_name like :materialName and i.item_type_enum='")
            .append(ItemTypes.MATERIAL.toString()).append("') ");
      }
    }
    if (fromTime != null) {
      sb.append(" and i.order_time_created>=:fromTime ");
    }
    if (toTime != null) {
      sb.append(" and i.order_time_created<=:toTime ");
    }
    if (!StringUtils.isNotBlank(licenceNo) && licenceNos != null && licenceNos.size() > 0) {
      sb.append(" and  i.vehicle in:licenceNos");
    }
    try {
      sb.append(" and i.order_type_enum in ('REPAIR','WASH','REPAIR_SALE','RECHARGE','WASH_MEMBER','WASH_BEAUTY') ")
          .append(" group by i.order_id");
    } catch (Exception e) {
      LOG.error(e.getMessage(),e);
    }
    Query query = session.createSQLQuery(sb.toString()).addEntity(ItemIndex.class);
    if (StringUtils.isNotBlank(licenceNo) && licenceNos != null && licenceNos.size() > 0) {
      for (String s : licenceNos) {
        if (licenceNo.equals(s)) {
          query.setString("licenceNo", licenceNo.trim());
          break;
        }
      }

    }
    if (StringUtils.isNotBlank(services)) {
      query.setString("services", services.trim() + "%");
    }
    if (StringUtils.isNotBlank(materialName)) {
      query.setString("materialName", materialName.trim() + "%");
    }
    if (fromTime != null) {
      query.setLong("fromTime", fromTime);
    }
    if (toTime != null) {
      query.setLong("toTime", toTime);
    }
    if (!StringUtils.isNotBlank(licenceNo) && licenceNos != null && licenceNos.size() > 0) {
      query.setParameterList("licenceNos", licenceNos);
    }
    return query.setLong("shopId", shopId)
        .setFirstResult((pager.getCurrentPage() - 1) * 5).setMaxResults(pager.getPageSize());
  }

/**
 * 根据 shop_id 和 start_time end_time获取洗车条数
 * @param session
 * @param shopId
 * @param startTime
 * @param endTime
 * @return
 */
  public static Query countWashItemIndexByShopId(Session session, long shopId, long startTime, long endTime) {
     StringBuffer sb = new StringBuffer();
     sb.append(" select count(*) from item_index where shop_id = :shopId and created >= :startTime and created <:endTime " +
         " and ((order_type_enum =:orderType and item_type_enum =:itemType ) or (order_type_enum =:orderType1 and item_type_enum =:itemType1 ) or (order_type_enum =:orderType2 and item_type_enum =:itemType2 ) ) " +
         " and order_status_enum =:orderStatus ");

     Query query = session.createSQLQuery(sb.toString())
         .setLong("shopId", shopId)
         .setLong("startTime", startTime)
         .setLong("endTime", endTime)
         .setParameter("orderType", OrderTypes.RECHARGE.toString())
         .setParameter("itemType", ItemTypes.RECHARGE.toString())
         .setParameter("orderType1", OrderTypes.WASH.toString())
         .setParameter("itemType1", ItemTypes.WASH.toString())
         .setParameter("orderType2", OrderTypes.WASH_MEMBER.toString())
         .setParameter("itemType2", ItemTypes.WASH_MEMBER.toString())
         .setParameter("orderStatus", OrderStatus.REPAIR_SETTLED.toString());

     return query;
  }

  public static Query getWashItemIndexListByPager(Session session, long shopId, long startTime, long endTime,
                                                  Pager pager) {
	  StringBuffer sb = new StringBuffer();
	  sb.append(" select * from item_index where shop_id = :shopId and created >= :startTime and created < :endTime " +
			            " and ((order_type_enum =:orderType and item_type_enum =:itemType ) " +
			            "or (order_type_enum =:orderType1 and item_type_enum =:itemType1 )" +
			            " or (order_type_enum =:orderType2 and item_type_enum =:itemType2 ) ) " +
			            " and order_status_enum =:orderStatus ");

     Query query = session.createSQLQuery(sb.toString()).addEntity(ItemIndex.class)
         .setLong("shopId", shopId)
         .setLong("startTime", startTime)
         .setLong("endTime", endTime)
         .setParameter("orderType", OrderTypes.RECHARGE.toString())
         .setParameter("itemType", ItemTypes.RECHARGE.toString())
         .setParameter("orderType1", OrderTypes.WASH.toString())
         .setParameter("itemType1", ItemTypes.WASH.toString())
         .setParameter("orderType2", OrderTypes.WASH_MEMBER.toString())
         .setParameter("itemType2", ItemTypes.WASH_MEMBER.toString())
         .setParameter("orderStatus", OrderStatus.REPAIR_SETTLED.toString())
		     .setFirstResult(pager.getRowStart())
		     .setMaxResults(pager.getPageSize());

     return query;
   }

  /**
   * 获得没有productId的itemindex总数
   * @param session
   * @return
   */
  public static Query countNullProductIDItemIndex(Session session) {
    return session.createQuery("select count(i) from ItemIndex i where i.productId is null and i.orderType <>'5' " +
        "and not (i.orderType ='4' and i.itemType ='1') and i.orderType <>'7' and  i.orderType <>'9'");
  }

  //初始化itemIndex中有product信息的情况，ordertype = 5 表示洗车单，(i.orderType ='4' and i.itemType ='1')")表示施工单 orderType = 7 washorder chongzhi  orderType = 9
  public static Query getNullProductIDItemIndexs(Session session, int rowStart, int pageRows) {
    return session.createQuery("select i from ItemIndex i where i.productId is null and i.orderType <>'5' " +
        "and not (i.orderType ='4' and i.itemType ='1') and  i.orderType <>'7' and  i.orderType <>'9'").setFirstResult(rowStart).setMaxResults(pageRows);
  }

  /**
   * 根据supplierId和ProductId查询订单详情， i.orderType = '2' or i.orderType = '8' 表示入库单和退货单
   * @param session
   * @param shopId
   * @param supplierId
   * @param productId
   * @return
   */
  public static Query getItemIndexBycustomerIdAndProductId(Session session, Long shopId, Long supplierId, Long productId) {
    return session.createQuery("select i from ItemIndex i where (i.orderType = '2' or i.orderType = '8') and i.shopId =:shopId and i.customerId =:supplierId and " +
        " i.productId =:productId order by i.lastModified desc")
        .setLong("shopId",shopId).setLong("supplierId",supplierId).setLong("productId",productId);
  }

	public static Query countInventoryInfo(Session session, Long shopId) {
		StringBuffer sb = new StringBuffer();
		sb.append("select count(id),sum(amount),sum(amount*purchase_price) from inventory_search_index where shop_id =:shopId");
		return session.createSQLQuery(sb.toString()).setLong("shopId",shopId);
	}

  public static Query getItemIndexDTOByOrderId(Session session, long shopId, long orderId) {
    return session.createQuery("select i from ItemIndex i where i.orderId=:orderId and i.shopId = :shopId")
        .setLong("shopId", shopId).setLong("orderId", orderId);
  }

  public static Query getOrderIndexMaxId(Session session, Long shopId, long startId, int pageSize) {
    StringBuffer sb = new StringBuffer("select max(o.id) as max from (select id from order_index where id>:startId ");
    if (shopId != null) {
      sb.append(" and shop_id=:shopId");
    }
    sb.append(" limit :pageSize) as o");
    Query query = session.createSQLQuery(sb.toString()).addScalar("max", StandardBasicTypes.LONG).setLong("startId", startId);
    if (shopId != null) {
      query.setLong("shopId", shopId);
    }
    query.setLong("pageSize", pageSize);
    return query;
  }

  public static Query getItemIndexDTOByArrayOrderId(Session session, Long shopId, Long startId, Long endId) {
    StringBuffer sb = new StringBuffer("from ItemIndex i where i.orderId>:startId and i.orderId<=:endId");
    if (shopId != null) {
      sb.append(" and i.shopId=:shopId");
    }
    Query query = session.createQuery(sb.toString()).setLong("startId", startId).setLong("endId", endId);
    if (shopId != null) {
      query.setLong("shopId", shopId);
    }
    sb.append(" order by i.orderId asc");
    return query;
  }

  public static Query getRepairOrderIndexMaxId(Session session, Long shopId, long startId, int pageSize) {
    StringBuffer sb = new StringBuffer("select max(c.id) as max from (select id  from repair_order where id>:startId  limit :pageSize) as c");
    if (shopId != null) {
      sb.append(" and shop_id=:shopId");
    }
    sb.append(" order by id asc");
    Query query = session.createSQLQuery(sb.toString()).addScalar("max", StandardBasicTypes.LONG).setLong("startId", startId);
    if (shopId != null) {
      query.setLong("shopId", shopId);
    }
    query.setLong("pageSize", pageSize);
    return query;
  }

	public static Query getProductSupplierDTO(Session session, Long shopId, Long startProductId, Long endProductId) {
		StringBuffer sb = new StringBuffer();
		sb.append("select i.product_id as productId,i.customer_id as customerId,i.shop_id as shopId ,max(i.order_time_created) as orderTimeCreated,");
		sb.append("s.name as name,s.contact as contact,s.mobile as mobile ");
		sb.append("from search.item_index i left join bcuser.supplier s on i.customer_id=s.id and i.shop_id = s.shop_id ");
		sb.append("where (i.order_type_enum = 'INVENTORY' or  i.order_type_enum ='PURCHASE') ");
		sb.append("and i.order_time_created is not null and i.customer_id is not null ");
		if(shopId != null){
			sb.append("and i.shop_id =:shopId ");
		}
		if(startProductId !=null){
			sb.append("and i.product_Id >=:startProductId ");
		}
		if(endProductId !=null){
			sb.append("and i.product_Id <:endProductId ");
		}
		sb.append("group by i.product_id,i.customer_id,i.shop_id ");
		sb.append("order by i.product_id asc,i.order_time_created asc");

		Query q= session.createSQLQuery(sb.toString())
				       .addScalar("productId",StandardBasicTypes.LONG)
				       .addScalar("customerId",StandardBasicTypes.LONG)
				       .addScalar("shopId", StandardBasicTypes.LONG)
				       .addScalar("orderTimeCreated", StandardBasicTypes.LONG)
				       .addScalar("name",StandardBasicTypes.STRING)
				       .addScalar("contact",StandardBasicTypes.STRING)
				       .addScalar("mobile",StandardBasicTypes.STRING);
		if (shopId != null) {
			q.setLong("shopId", shopId);
		}
		if (startProductId != null) {
			q.setLong("startProductId", startProductId);
		}
		if (endProductId != null) {
			q.setLong("endProductId", endProductId);
		}
		return q;
	}

	public static Query getItemIndexNextProductIdWithSupplier(Session session, Long shopId, Long startProductId, int rows) {
		StringBuffer sb = new StringBuffer();
		sb.append("select max(p2.productId) as productId from (select distinct i.product_id as productId from search.item_index i ");
		sb.append("where (i.order_type_enum = 'INVENTORY' or  i.order_type_enum ='PURCHASE') ");
		sb.append("and i.order_time_created is not null and i.customer_id is not null ");
		if(shopId !=null){
			sb.append("and i.shop_id =:shopId ");
		}
		sb.append("and i.product_id >:startProductId order by i.product_id asc limit :rows) as p2");
		Query q = session.createSQLQuery(sb.toString())
				          .addScalar("productId",StandardBasicTypes.LONG)
				          .setLong("startProductId",startProductId)
				          .setInteger("rows",rows);
		if(shopId != null){
			q.setLong("shopId",shopId);
		}
		return  q;
	}


  /**
   * 根据shopId获得老洗车单的 非会员洗车和洗车充值 用于流水统计初始化
   *
   * @param session
   * @param shopId
   * @return
   */
  public static Query countWashItemIndexByShopId(Session session, Long shopId) {

    StringBuffer sb = new StringBuffer();
     sb.append(" select count(*) from item_index where shop_id = :shopId " +
         " and  ( order_type_enum =:wash_member or order_type_enum =:recharge ) ");

     Query query = session.createSQLQuery(sb.toString())
         .setLong("shopId", shopId)
         .setParameter("wash_member",OrderTypes.WASH_MEMBER.toString())
         .setParameter("recharge", OrderTypes.RECHARGE.toString());
     return query;

  }


  public static Query getOrderIndex(Session session,Long shopId,Long orderId)
  {
    return session.createQuery("From OrderIndex oi where oi.shopId = :shopId and oi.orderId =:orderId")
        .setLong("shopId",shopId).setLong("orderId",orderId);
  }
	public static Query getInventorySearchIndexByCommodityCode(Session session, long shopId, String commodityCode) {
		StringBuffer sb = new StringBuffer();
		sb.append(" from InventorySearchIndex where shopId =:shopId and commodityCode =:commodityCode");
		sb.append(" and (status is null or status <>'DISABLED')");
		sb.append(" order by editDate desc");
		return session.createQuery(sb.toString()).setLong("shopId",shopId).setString("commodityCode",commodityCode);
	}

	  public static Query countInventoryLowerLimitAmout(Session session, Long shopId) {
    StringBuffer sb = new StringBuffer();
    sb.append("select count(*) from InventorySearchIndex i where i.shopId =:shopId and i.amount< (i.lowerLimit - 0.0001)" +
        " and i.lowerLimit is not null and (i.status is null or i.status <> 'DISABLED')");
    return session.createQuery(sb.toString()).setLong("shopId", shopId);
  }

  public static Query countInventoryUpperLimitAmount(Session session, Long shopId) {
    StringBuffer sb = new StringBuffer();
    sb.append("select count(*) from InventorySearchIndex i where i.shopId =:shopId and i.amount > (i.upperLimit + 0.0001)" +
        " and i.upperLimit is not null and i.upperLimit !=0 and (i.status is null or i.status <> 'DISABLED')");
    return session.createQuery(sb.toString()).setLong("shopId", shopId);
  }

  public static Query updateInventorySearchIndexKindName(Session session, Long shopId, String oldKindName, String newKindName){
    StringBuffer sb = new StringBuffer();
    sb.append("update InventorySearchIndex i set i.kindName =:kindName2 where i.shopId =:shopId and i.kindName =:kindName1");
    return session.createQuery(sb.toString()).setLong("shopId",shopId).setString("kindName1",oldKindName).setString("kindName2",newKindName);
  }

  public static Query getInventorySearchIndexIdListByProductKind(Session session,Long shopId,String kindName){
    return session.createQuery("select i.id from InventorySearchIndex i where i.shopId =:shopId and kindName =:kindName").setLong("shopId",shopId).setString("kindName",kindName);
  }

  public static Query deleteMultipleInventoryKind(Session session,Long shopId, String kindName){
    StringBuffer sb = new StringBuffer();
    sb.append("update InventorySearchIndex i set i.kindName=null where i.shopId =:shopId and i.kindName =:kindName");
    return session.createQuery(sb.toString()).setLong("shopId",shopId).setString("kindName",kindName);
  }

	// long inventoryCount;    //入库种类
//  double inventoryAmount;   //入库总数量
//  double totalPurchasePrice;   //入库总价值

	public static Query getLowerLimitSearchCount(Session session, Long shopId) {
		StringBuffer sb = new StringBuffer();
		sb.append("select count(i.id) ,sum(i.amount),sum(i.amount * i.inventoryAveragePrice) from InventorySearchIndex i ");
		sb.append(" where i.shopId =:shopId and i.amount < (i.lowerLimit - 0.0001) and i.lowerLimit is not null and (i.status <>'DISABLED'or i.status is null)");
		return session.createQuery(sb.toString()).setLong("shopId", shopId);
	}

	public static Query getUpperLimitSearchCount(Session session, Long shopId) {
		StringBuffer sb = new StringBuffer();
		sb.append("select count(i.id) ,sum(i.amount),sum(i.amount * i.inventoryAveragePrice) from InventorySearchIndex i ");
		sb.append(" where i.shopId =:shopId and i.amount > (i.upperLimit + 0.0001) and i.upperLimit is not null and i.upperLimit !=0 and (i.status <>'DISABLED' or i.status is null)");
		return session.createQuery(sb.toString()).setLong("shopId", shopId);
	}

  public static Query getCustomerOrSupplierOrderIndexs(Session session, Long shopId,Long[] customerOrSupplierIds) {
		String sql="from OrderIndex where shopId=:shopId and customerOrSupplierId in (:customerOrSupplierIds)";
		return session.createQuery(sql).setLong("shopId", shopId).setParameterList("customerOrSupplierIds",customerOrSupplierIds);
	}

  public static Query getItemIndexByCustomerIds(Session session, Long shopId,Long[] customerIds) {
		String sql="from ItemIndex where shopId=:shopId and customerId in (:customerIds)";
		return session.createQuery(sql).setLong("shopId", shopId).setParameterList("customerIds",customerIds);
	}

  public static Query countItemIndexByProductIdOrderStatus(Session session, Long productId, Long shopId, Map<OrderTypes, List<OrderStatus>> inProgressStatusMap) {
    StringBuffer sb = new StringBuffer();
    sb.append("select count(*) from ItemIndex where productId=:productId and shopId=:shopId and ( ");
    Set<OrderTypes> allTypes = inProgressStatusMap.keySet();
    int i=0;
    for(OrderTypes orderType : allTypes){
      String type = ":type"+i;
      String status = ":status"+i;
      sb.append("(orderTypeEnum = " + type);
      sb.append(" and orderStatusEnum in ("+ status + "))");
      if(i!=allTypes.size()-1){
        sb.append(" or ");
      }
      i++;
    }
    sb.append(" )");
    Query q = session.createQuery(sb.toString()).setLong("productId", productId).setLong("shopId", shopId);
    int j = 0;
    for(OrderTypes orderType : allTypes){
      String type = "type"+j;
      String status = "status"+j;
      q.setParameter(type, orderType).setParameterList(status, inProgressStatusMap.get(orderType));
      j++;
    }
    return q;
  }

  public static Query countItemIndexByServiceIdOrderStatus(Session session, Long serviceId, Long shopId, Map<OrderTypes, List<OrderStatus>> inProgressStatusMap) {
    StringBuffer sb = new StringBuffer();
    sb.append("select count(*) from ItemIndex where serviceId=:serviceId and shopId=:shopId and ( ");
    Set<OrderTypes> allTypes = inProgressStatusMap.keySet();
    int i=0;
    for(OrderTypes orderType : allTypes){
      String type = ":type"+i;
      String status = ":status"+i;
      sb.append("(orderTypeEnum = " + type);
      sb.append(" and orderStatusEnum in ("+ status + "))");
      if(i!=allTypes.size()-1){
        sb.append(" or ");
      }
      i++;
    }
    sb.append(" )");
    Query q = session.createQuery(sb.toString()).setLong("serviceId", serviceId).setLong("shopId", shopId);
    int j = 0;
    for(OrderTypes orderType : allTypes){
      String type = "type"+j;
      String status = "status"+j;
      q.setParameter(type, orderType).setParameterList(status, inProgressStatusMap.get(orderType));
      j++;
    }
    return q;
  }


public static Query getItemIndexByProductIdOrderStatus(Session session, Set<Long> productIdSet, Long shopId, Map<OrderTypes, List<OrderStatus>> settledStatusMap) {
  StringBuffer sb = new StringBuffer();
  sb.append(" from ItemIndex where productId in(:productId) and shopId=:shopId and ( ");
  Set<OrderTypes> allTypes = settledStatusMap.keySet();
  int i = 0;
  for (OrderTypes orderType : allTypes) {
    String type = ":type" + i;
    String status = ":status" + i;
    sb.append("(orderTypeEnum = " + type);
    sb.append(" and orderStatusEnum in (" + status + "))");
    if (i != allTypes.size() - 1) {
      sb.append(" or ");
    }
    i++;
  }
  sb.append(" )");
  Query q = session.createQuery(sb.toString()).setParameterList("productId", productIdSet).setLong("shopId", shopId);
  int j = 0;
  for (OrderTypes orderType : allTypes) {
    String type = "type" + j;
    String status = "status" + j;
    q.setParameter(type, orderType).setParameterList(status, settledStatusMap.get(orderType));
    j++;
  }
  return q;
}

  public static Query countRepairOrderInOrderIndex(Session session, Long shopId, Long fromTime, Long toTime) {
    StringBuilder sb = new StringBuilder();
    sb.append(" select count(id) from order_index i where i.shop_id=:shopId ");
    if (fromTime != null) {
      sb.append(" and i.created>=:fromTime ");
    }
    if (toTime != null) {
      sb.append(" and i.created<=:toTime ");
    }
    sb.append(" and i.order_type_enum in ('REPAIR','WASH','REPAIR_SALE','RECHARGE','WASH_MEMBER','WASH_BEAUTY') ");
    Query query = session.createSQLQuery(sb.toString());
    if (fromTime != null) {
      query.setLong("fromTime", fromTime);
    }
    if (toTime != null) {
      query.setLong("toTime", toTime);
    }
    return query.setLong("shopId", shopId);
  }
}
