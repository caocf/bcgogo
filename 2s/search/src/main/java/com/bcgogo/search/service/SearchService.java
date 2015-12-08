package com.bcgogo.search.service;

import com.bcgogo.common.Pager;
import com.bcgogo.common.Sort;
import com.bcgogo.enums.ItemTypes;
import com.bcgogo.enums.OrderStatus;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.exception.BcgogoException;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.product.dto.ProductSupplierDTO;
import com.bcgogo.search.client.SolrClientHelper;
import com.bcgogo.search.dto.*;
import com.bcgogo.search.model.*;
import com.bcgogo.search.service.CurrentUsed.IProductCurrentUsedService;
import com.bcgogo.search.service.IndexItemToOrder.IndexItemToOrderStragy;
import com.bcgogo.search.service.IndexItemToOrder.IndexItemToOrderStragySelector;
import com.bcgogo.search.util.SolrQueryUtils;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.*;
import com.bcgogo.user.dto.CustomerDTO;
import com.bcgogo.user.dto.PurchaseInventoryHistoryDTO;
import com.bcgogo.user.dto.PurchaseOrderNotInventoriedInfoDTO;
import com.bcgogo.user.dto.SupplierDTO;
import com.bcgogo.user.merge.*;
import com.bcgogo.utils.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.TermsResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.CommonParams;
import org.apache.solr.common.params.TermsParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.*;

@Component
public class SearchService implements ISearchService {
  private static final Logger LOG = LoggerFactory.getLogger(SearchService.class);

  @Override
  public MemcacheInventorySumDTO countInventoryInfoByShopId(Long shopId) throws Exception {
    SearchWriter writer = searchDaoManager.getWriter();
    Double[] counts = writer.countInventoryInfoByShopId(shopId);
    MemcacheInventorySumDTO memcacheInventorySumDTO = new MemcacheInventorySumDTO();
    memcacheInventorySumDTO.setInventoryInfoCounts(counts);
    return memcacheInventorySumDTO;
  }

  /**
   * 保存购卡单的同时把信息保存到orderIndex和itemIndex (在memberCardOrder保存成功后才保存，所以要判断id)
   * 不更新solr
   * @param memberCardOrderDTO
   * @throws Exception
   */
  @Override
  public void saveOrderIndexAndItemIndexOfMemberCardOrder(MemberCardOrderDTO memberCardOrderDTO) throws Exception {
//    IOrderIndexService orderIndexService = ServiceManager.getService(IOrderIndexService.class);
    SearchWriter writer = searchDaoManager.getWriter();
    Object status = writer.begin();
    OrderIndex orderIndex = null;
    try {
      if (null == memberCardOrderDTO || null == memberCardOrderDTO.getId()) {
        return;
    }

      orderIndex = new OrderIndex(memberCardOrderDTO);
      orderIndex.setOrderTypeEnum(OrderTypes.MEMBER_BUY_CARD);
      orderIndex.setOrderStatusEnum(OrderStatus.MEMBERCARD_ORDER_STATUS);
      writer.save(orderIndex);

//      List<OrderIndexDTO> orderIndexDTOs = new ArrayList<OrderIndexDTO>();
//      orderIndexDTOs.add(orderIndex.toDTO());
//      orderIndexService.addOrderIndexToSolr(orderIndexDTOs);

      if (CollectionUtils.isEmpty(memberCardOrderDTO.getMemberCardOrderItemDTOs())) {
        return;
  }

      for (MemberCardOrderItemDTO memberCardOrderItemDTO : memberCardOrderDTO.getMemberCardOrderItemDTOs()) {
        if (null == memberCardOrderItemDTO.getId()) {
          continue;
  }

        ItemIndex itemIndex = new ItemIndex();
        itemIndex.setOrderId(memberCardOrderDTO.getId());
        itemIndex.setOrderStatus(TxnConstant.OrderStatusInIntemIndex.ITEMINDEX_ORDERSTATUS_FINISH);
        itemIndex.setItemId(memberCardOrderItemDTO.getId());
        itemIndex.setShopId(memberCardOrderDTO.getShopId());
        itemIndex.setCustomerId(memberCardOrderDTO.getCustomerId());
        itemIndex.setCustomerOrSupplierName(memberCardOrderDTO.getCustomerName());
        itemIndex.setOrderTimeCreated(memberCardOrderDTO.getVestDate());
        itemIndex.setOrderType(TxnConstant.OrderType.ORDER_TYPE_SALE_MEMBER_CARD);
        itemIndex.setItemType(TxnConstant.OrderType.ORDER_TYPE_SALE_MEMBER_CARD);
        itemIndex.setItemName(memberCardOrderDTO.getMemberCardName());
        itemIndex.setItemCount(Double.valueOf(1));
        itemIndex.setItemPrice(memberCardOrderDTO.getReceivableDTO().getTotal());
        itemIndex.setOrderTotalAmount(itemIndex.getItemPrice());
        itemIndex.setArrears(memberCardOrderDTO.getReceivableDTO().getDebt());
        itemIndex.setOrderTypeEnum(OrderTypes.MEMBER_BUY_CARD);
        itemIndex.setItemTypeEnum(ItemTypes.SALE_MEMBER_CARD);
        itemIndex.setOrderStatusEnum(OrderStatus.MEMBERCARD_ORDER_STATUS);
        if (null != itemIndex.getArrears() && itemIndex.getArrears() > 0) {
          itemIndex.setPaymentTime(DateUtil.convertDateStringToDateLong(DateUtil.YEAR_MONTH_DATE, memberCardOrderDTO.getRepayTime()));
  }
        itemIndex.setMemberCardId(memberCardOrderDTO.getId());
        writer.save(itemIndex);
      }

      if (CollectionUtils.isEmpty(memberCardOrderDTO.getNewMemberCardOrderServiceDTOs())) {
        return;
  }

      for (MemberCardOrderServiceDTO memberCardOrderServiceDTO : memberCardOrderDTO.getNewMemberCardOrderServiceDTOs()) {
        if (null != memberCardOrderServiceDTO.getId() && null != memberCardOrderServiceDTO.getServiceId()) {
          ItemIndex itemIndex = new ItemIndex();
          itemIndex.setOrderId(memberCardOrderDTO.getId());
          itemIndex.setItemId(memberCardOrderServiceDTO.getId());
          itemIndex.setShopId(memberCardOrderDTO.getShopId());
          itemIndex.setCustomerId(memberCardOrderDTO.getCustomerId());
          itemIndex.setCustomerOrSupplierName(memberCardOrderDTO.getCustomerName());
          itemIndex.setOrderTimeCreated(memberCardOrderDTO.getVestDate());
          itemIndex.setOrderType(TxnConstant.OrderType.ORDER_TYPE_SALE_MEMBER_CARD);
          itemIndex.setItemType(TxnConstant.ITEM_TYPE_MEMBER_CARD_ORDER_SERVICE);
          itemIndex.setItemName(memberCardOrderServiceDTO.getServiceName());
          itemIndex.setServiceId(memberCardOrderServiceDTO.getServiceId());
          itemIndex.setIncreasedTimes(memberCardOrderServiceDTO.getIncreasedTimes());
          itemIndex.setIncreasedTimesLimitType(memberCardOrderServiceDTO.getIncreasedTimesLimitType());
          itemIndex.setVehicles(memberCardOrderServiceDTO.getVehicles());
          itemIndex.setDeadline(memberCardOrderServiceDTO.getDeadline());
          itemIndex.setCardTimes(memberCardOrderServiceDTO.getCardTimes());
          itemIndex.setCardTimesLimitType(memberCardOrderServiceDTO.getCardTimesLimitType());
          itemIndex.setOldTimes(memberCardOrderServiceDTO.getOldTimes());
          itemIndex.setOldTimesLimitType(memberCardOrderServiceDTO.getOldTimesLimitType());
          itemIndex.setBalanceTimes(memberCardOrderServiceDTO.getBalanceTimes());
          itemIndex.setBalanceTimesLimitType(memberCardOrderServiceDTO.getBalanceTimesLimitType());
          itemIndex.setOrderTypeEnum(OrderTypes.MEMBER_BUY_CARD);
          itemIndex.setItemTypeEnum(ItemTypes.SALE_MEMBER_CARD_SERVICE);
          itemIndex.setOrderStatusEnum(OrderStatus.MEMBERCARD_ORDER_STATUS);
          writer.save(itemIndex);
	}
      }

      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public List<InventorySearchIndex> searchInventorySearchIndexByProductIds(Long shopId, Long[] productIds) {
    SearchWriter writer = searchDaoManager.getWriter();
    return writer.searchInventorySearchIndexByProductIds(shopId, productIds);
  }

  @Override
  public void deleteByQuery(String q, String core){
    LOG.debug("清理时间:" + DateUtil.convertDateLongToDateString("yyyy-MM-dd hh:mm:ss", System.currentTimeMillis()));
    LOG.debug("清理模块:" + core);
    LOG.debug("清理语句:" + q);
    try {
      if (core.equals(SolrClientHelper.BcgogoSolrCore.PRODUCT_CORE.getValue())) {
        SolrClientHelper.getProductSolrClient().deleteByQuery(q);
      } else if (core.equals(SolrClientHelper.BcgogoSolrCore.ORDER_CORE.getValue())) {
        SolrClientHelper.getOrderSolrClient().deleteByQuery(q);
      } else if (core.equals(SolrClientHelper.BcgogoSolrCore.VEHICLE_CORE.getValue())) {
        SolrClientHelper.getVehicleSolrClient().deleteByQuery(q);
      } else if (core.equals(SolrClientHelper.BcgogoSolrCore.CUSTOMER_SUPPLIER_CORE.getValue())) {
        SolrClientHelper.getCustomerSupplierSolrClient().deleteByQuery(q);
      }else if (core.equals(SolrClientHelper.BcgogoSolrCore.SHOP.getValue())) {
        SolrClientHelper.geShopSolrClient().deleteByQuery(q);
      }
    } catch (Exception e) {
      LOG.debug(e.getMessage(), e);
    }
  }

  @Override
  public List<ProductDTO> queryProduct(String q, Long shopId, int start, int rows) throws Exception {
    q = SolrQueryUtils.escape(q);

    SolrQuery query = new SolrQuery();
    if (StringUtils.isBlank(q)) query.setQuery("*");
    else query.setQuery(q);
    query.setParam("defType", "dismax");
    query.setParam("qf", "product_name^4 product_model^4 product_brand^4 product_spec^4 product_name_ngram product_model_ngram product_brand_ngram product_spec_ngram");
    query.setParam("mm", "1");
//    query.setParam("debugQuery", "true");
    query.setFilterQueries("shop_id:" + shopId.toString());
    query.setParam("fl", "*,score");
    query.setStart(start);
    query.setRows(rows);
	  query.addFilterQuery("!product_status:DISABLED");
    QueryResponse rsp = SolrClientHelper.getProductSolrClient().query(query);
    return getProductDTOByQueryResponses(shopId, rsp);
  }

  @Override
  public QueryResponse queryProductByQueryString(String q, int rows) throws Exception {
    SolrQuery query = new SolrQuery();
    query.setQuery(q);
    query.setRows(rows);
    QueryResponse rsp = SolrClientHelper.getProductSolrClient().query(query);
    return rsp;
  }

  @Override
  public QueryResponse queryProductByKeywords(Long shopId, String q, int start, int rows) throws Exception {
    SolrQuery query = new SolrQuery();
    q = SolrQueryUtils.escape(q);

    if (StringUtils.isNotBlank(q)) {
      List<String> terms = queryTermsForProduct(q, "product_name");
      if (terms.size() == 0) {
        query.setQuery("product_name:" + q + " AND isBasicData:false");
      } else {
        StringBuilder stringBuilder = new StringBuilder();
        for (String term : terms)
          stringBuilder.append(term).append(" ");
        query.setQuery("product_name:(" + stringBuilder.toString().trim() + ") AND isBasicData:false");
      }
    } else {
      query.setQuery("product_name:* AND isBasicData:false");
    }
//    query.setParam("q.op", "AND");
    StringBuffer fQueryString = new StringBuffer();
    fQueryString.append("(shop_id:").append(shopId.toString()).append(" OR ")
        .append("shop_id:1 )");
    String fqString = fQueryString.toString();
    query.setFilterQueries(fqString);
	  query.addFilterQuery("!product_status:DISABLED");
    query.setStart(start);
    query.setRows(rows);

    QueryResponse rsp = SolrClientHelper.getProductSolrClient().query(query);
    return rsp;
  }

  @Deprecated
  private List<String> getFieldOfCurrentUsedProductFromMemoryAndSolr(SearchConditionDTO searchConditionDTO) throws Exception {
    IProductCurrentUsedService serviceCurrentUsed = ServiceManager.getService(IProductCurrentUsedService.class);
    //得到 下拉框前5个常用product  memcache
    List<String> currentUsedProductFieldList = new ArrayList<String>();
    //先从内存中找
    List<CurrentUsedProduct> currentUsedProductList = serviceCurrentUsed.getCurrentUsedProductsFromMemory(new SearchMemoryConditionDTO(searchConditionDTO));
    if (CollectionUtils.isNotEmpty(currentUsedProductList)) {
      //currentUsedProductBrandList.add("<最近使用>");
      for (CurrentUsedProduct currentUsedProduct : currentUsedProductList) {
        //如果是品牌 加上 product_name field 过滤
        if (searchConditionDTO.searchFieldEquals(SearchConstant.PRODUCT_BRAND) && (StringUtils.isBlank(searchConditionDTO.getProductName()) || searchConditionDTO.getProductName().equals(currentUsedProduct.getProductName()))) {
          currentUsedProductFieldList.add(currentUsedProduct.getBrand());
        } else if (searchConditionDTO.searchFieldEquals(SearchConstant.PRODUCT_NAME)) {
          currentUsedProductFieldList.add(currentUsedProduct.getProductName());
        }
      }
    }
    //得到 下拉框后面的product 按照 solr 使用率查找
    List<String> currentUsedProductBrandListFromSolr = this.getProductsOrderByFirstLetter(searchConditionDTO);   //todo
//    if (LOG.isDebugEnabled()) {
//      for (String cup : currentUsedProductBrandListFromSolr) {
//        LOG.debug("Solr classify CurrentUsedProduct is " + cup + ".");
//      }
//    }
    if (CollectionUtils.isNotEmpty(currentUsedProductBrandListFromSolr)) {
      currentUsedProductFieldList.addAll(currentUsedProductBrandListFromSolr);
    }
    return currentUsedProductFieldList;
  }

  @Override
  public List<String> getProductSuggestionList(SearchConditionDTO searchConditionDTO) throws Exception {
    /*
    * 如果是品牌下拉列表先从 memcache中获取常用的品牌，然后从solr获取其他品牌
    * 然后其他情况直接从solr获取。
    * modify by zhangjuntao
    */
    if (searchConditionDTO.searchFieldEquals(SearchConstant.PRODUCT_BRAND, SearchConstant.PRODUCT_NAME)) {
      return getFieldOfCurrentUsedProductFromMemoryAndSolr(searchConditionDTO);
    } else {
        return this.queryProductSuggestionList(searchConditionDTO.getSearchWord(),
          searchConditionDTO.getSearchField(), searchConditionDTO.getProductName(),
          searchConditionDTO.getVehicleBrand(), searchConditionDTO.getProductSpec(), searchConditionDTO.getVehicleModel(),
            searchConditionDTO.getVehicleBrand(), searchConditionDTO.getVehicleModel(), searchConditionDTO.getVehicleYear(),
          searchConditionDTO.getVehicleBrand(), null, null, null, null, searchConditionDTO.getShopId(), searchConditionDTO.getIncludeBasic(), 0, 10);
      }

  }


  private List<String> getProductsOrderByFirstLetter(SearchConditionDTO searchConditionDTO) throws Exception {
    int rows = 10;
    if (searchConditionDTO.searchFieldEquals(SearchConstant.PRODUCT_BRAND, SearchConstant.PRODUCT_NAME)) {
      rows = SolrQueryUtils.getSelectOptionNumber();
    }
    List<String> results = null;
      results = this.queryProductSuggestionList(searchConditionDTO.getSearchWord(),
        searchConditionDTO.getSearchField(), searchConditionDTO.getProductName(),
        searchConditionDTO.getVehicleBrand(), searchConditionDTO.getProductSpec(), searchConditionDTO.getVehicleModel(),
          searchConditionDTO.getVehicleBrand(), searchConditionDTO.getVehicleModel(), searchConditionDTO.getVehicleYear(),
        searchConditionDTO.getVehicleBrand(), null, null, null, null, searchConditionDTO.getShopId(), searchConditionDTO.getIncludeBasic(), 0, 10);
//    if (LOG.isDebugEnabled()) {
//      for (String cup : results) {
//        LOG.debug("Solr CurrentUsedProduct is " + cup + ".");
//      }
//    }
    return ServiceUtil.classifyList(results);
  }

  public List<String> queryTermsForProduct(String q, String field) throws Exception {

    SolrQuery query = new SolrQuery();
    query.setParam(CommonParams.QT, "/terms");
    query.setParam(TermsParams.TERMS, true);
    query.setParam(TermsParams.TERMS_LIMIT, "10");
    query.setParam(TermsParams.TERMS_FIELD, field);
    query.setParam(TermsParams.TERMS_PREFIX_STR, q);
    QueryResponse rsp = SolrClientHelper.getProductSolrClient().query(query);
    TermsResponse termsResponse = rsp.getTermsResponse();
    List<TermsResponse.Term> terms = termsResponse.getTerms(field);
    List<String> results = new ArrayList<String>();
    for (TermsResponse.Term term : terms) {
      String result = term.getTerm();
      results.add(result);
    }
    return results;
  }


  @Override
  public Map<Boolean, Object> getVehicleIdsByKeywords(String brand, String model, String year, String engine,
                                                      Boolean isOnlyOne) throws Exception {
    SolrQuery query = new SolrQuery();
    query.setQuery("*:*");
    StringBuffer fQueryString = new StringBuffer();

    boolean filter = false;
    if (brand != null && brand.trim().length() > 0) {
      fQueryString.append("brand_exact:(\"").append(brand).append("\")");
      filter = true;
    }
    if (model != null && model.trim().length() > 0) {
      if (filter) {
        fQueryString.append(" AND ");
      }
      fQueryString.append("model_exact:(\"").append(model).append("\")");
      filter = true;
    }

    if (engine != null && engine.trim().length() > 0) {
      if (filter) {
        fQueryString.append(" AND ");
      }
      fQueryString.append("engine_exact:(\"").append(engine).append("\")");
      filter = true;
    }

    if (year != null && year.trim().length() > 0) {
      if (filter) {
        fQueryString.append(" AND ");
      }
      fQueryString.append("year_exact:(\"").append(year).append("\")");
      filter = true;
    }

    if (filter) {
      String fqString = fQueryString.toString();
      query.addFilterQuery(fqString);
    }
    query.addFilterQuery("doc_type:"+SolrClientHelper.BcgogoSolrDocumentType.VEHICLE_PROPERTY_SUGGESTION_DOC_TYPE.getValue());
    query.setParam("q.op", "AND");
    QueryResponse rsp = SolrClientHelper.getVehicleSolrClient().query(query);
    SolrDocumentList documents = rsp.getResults();
    List<Long> results = new ArrayList<Long>();
    LOG.debug("documents.size():" + documents.size());
    Map<Boolean, Object> resultMap = new HashMap<Boolean, Object>();
    if (isOnlyOne) {
      if (documents.size() == 1) {
        SolrDocument document = documents.get(0);
        Long brandId = NumberUtil.longValue(document.getFirstValue("pv_brand_id"));
        Long modelId = NumberUtil.longValue(document.getFirstValue("pv_model_id"));
        Long yearId = NumberUtil.longValue(document.getFirstValue("pv_year_id"));
        Long engineId = NumberUtil.longValue(document.getFirstValue("pv_engine_id"));
        results.add(brandId);
        results.add(modelId);
        results.add(yearId);
        results.add(engineId);
      }
      resultMap.put(false, results);
    } else {
      resultMap.put(true, rsp);
    }

    return resultMap;
  }

  private List<ProductDTO> getProductDTOByQueryResponses(Long shopId, QueryResponse... responses) throws Exception {
    List<ProductDTO> results = new ArrayList<ProductDTO>();
    for (QueryResponse rsp : responses) {
      if (rsp == null) continue;
      SolrDocumentList documents = rsp.getResults();
      for (SolrDocument document : documents) {
        Long id = Long.parseLong((String) document.getFirstValue("id"));
        String name = (String) document.getFirstValue("product_name");
        String brand = (String) document.getFirstValue("product_brand");
        String model = (String) document.getFirstValue("product_model");
        String spec = (String) document.getFirstValue("product_spec");
        String dpvBrand = (String) document.getFirstValue("product_vehicle_brand");
        String dpvModel = (String) document.getFirstValue("product_vehicle_model");
        String dpvYear = (String) document.getFirstValue("product_vehicle_year");
        String dpvEngine = (String) document.getFirstValue("product_vehicle_engine");
        Integer pvStatus = (Integer) document.getFieldValue("product_vehicle_status");
        Object inventoryAmount = document.getFirstValue("inventory_amount");
        Object purchasePrice = document.getFirstValue("purchase_price");
        Object recommendedPrice = document.getFirstValue("recommendedprice");
        Object productLocalInfoId = document.getFirstValue("product_id");
        String storageUnit = (String) document.getFirstValue("product_storage_unit");
        String sellUnit = (String) document.getFirstValue("product_sell_unit");
        Object rate = document.getFieldValue("product_rate");
        String product_kind  = (String) document.getFirstValue("product_kind");
        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(id);
        productDTO.setShopId(shopId);
        productDTO.setName(name);
        productDTO.setModel(model);
        productDTO.setBrand(brand);
        productDTO.setSpec(spec);
        productDTO.setProductVehicleBrand(dpvBrand);
        productDTO.setProductVehicleModel(dpvModel);
        productDTO.setProductVehicleYear(dpvYear);
        productDTO.setProductVehicleEngine(dpvEngine);
        productDTO.setProductVehicleStatus(pvStatus);
        productDTO.setInventoryNum(inventoryAmount == null ? null : Double.parseDouble(inventoryAmount.toString()));
        productDTO.setPurchasePrice(purchasePrice == null ? null : Double.parseDouble(purchasePrice.toString()));
        productDTO.setRecommendedPrice(recommendedPrice == null ? null : Double.parseDouble(recommendedPrice.toString()));
        productDTO.setProductLocalInfoId(productLocalInfoId == null ? null : Long.parseLong(productLocalInfoId.toString()));
        productDTO.setRate(rate == null ? null : Long.parseLong(rate.toString()));
        productDTO.setStorageUnit(storageUnit);
        productDTO.setSellUnit(sellUnit);
        productDTO.setKindName(product_kind);
        results.add(productDTO);
      }
    }
    return results;
  }

  private List<ProductDTO> queryForProductSort(String q, String field, String productName, String productBrand,
                                               String productSpec, String productModel,
                                               String pvBrand, String pvModel, String pvYear, String pvEngine,
                                               Long pvBrandId, Long pvModelId, Long pvYearId, Long pvEngineId,
                                               Long shopId, boolean includeBasic,
                                               Integer start, Integer rows) throws Exception {
    String sortStr = "storage_time desc,inventory_amount desc";
    QueryResponse response = null;
    QueryResponse response2 = null;
    SolrQuery query = null;

    query = generateQueryForSearchProduct(q, field, productName, productBrand,
        productSpec, productModel, pvBrand, pvModel, pvYear, pvEngine,
        pvBrandId, pvModelId, pvYearId, pvEngineId,
        shopId, includeBasic, true, false, null, null, null);
    response = SolrClientHelper.getProductSolrClient().query(query);
//    int totalSize = response.getResults().size();       //库存大于0的产品的总数量
    int totalSize = ((Long) response.getResults().getNumFound()).intValue();


    query = generateQueryForSearchProduct(q, field, productName, productBrand,
        productSpec, productModel, pvBrand, pvModel, pvYear, pvEngine,
        pvBrandId, pvModelId, pvYearId, pvEngineId,
        shopId, includeBasic, true, false, sortStr, start, rows);
    response = SolrClientHelper.getProductSolrClient().query(query);
    int x = response.getResults().size();   //查询到的库存量大于0的数量
    int y = rows - x;                      //需要补全的库存量等于0的数量

    if (x == 0) {
      if (start == 0) y = 0;
      else y = start - totalSize;
      query = generateQueryForSearchProduct(q, field, productName, productBrand,
          productSpec, productModel, pvBrand, pvModel, pvYear, pvEngine,
          pvBrandId, pvModelId, pvYearId, pvEngineId,
          shopId, includeBasic, false, false, sortStr, y, rows);
      response = SolrClientHelper.getProductSolrClient().query(query);
    } else if (x > 0 && x < rows) {
      query = generateQueryForSearchProduct(q, field, productName, productBrand,
          productSpec, productModel, pvBrand, pvModel, pvYear, pvEngine,
          pvBrandId, pvModelId, pvYearId, pvEngineId,
          shopId, includeBasic, false, false, sortStr, 0, y);
      response2 = SolrClientHelper.getProductSolrClient().query(query);
    }
    return getProductDTOByQueryResponses(shopId, response, response2);
  }

  private List<ProductDTO> queryProductsDirect(String q, String field, String productName, String productBrand,
                                               String productSpec, String productModel,
                                               String pvBrand, String pvModel, String pvYear, String pvEngine,
                                               Long pvBrandId, Long pvModelId, Long pvYearId, Long pvEngineId,
                                               Long shopId, boolean includeBasic, String sortStatus,
                                               Integer start, Integer rows) throws Exception {

    if (StringUtils.isNotBlank(sortStatus) || !StringUtil.strArrayIsBlank(q, productBrand, productSpec, productModel, pvBrand, pvModel)) {
      String sortStr = RfTxnConstant.sortCommandMap.get(sortStatus);
      SolrQuery query = generateQueryForSearchProduct(q, field, productName, productBrand, productSpec, productModel,
          pvBrand, pvModel, pvYear, pvEngine, pvBrandId, pvModelId, pvYearId, pvEngineId,
          shopId, includeBasic, null, false, sortStr, start, rows);
      QueryResponse response = SolrClientHelper.getProductSolrClient().query(query);
      return getProductDTOByQueryResponses(shopId, response);

    } else {
      /*排序查询商品的方法*/
      return queryForProductSort(q, field, productName, productBrand, productSpec, productModel,
          pvBrand, pvModel, pvYear, pvEngine, pvBrandId, pvModelId, pvYearId, pvEngineId, shopId, includeBasic, start, rows);
    }
  }

  /**
   * @param includeInventory true->库存量大于0,false->库存量等于0,为NULL,排除库存量查询条件;
   * @param sortStr          排序语句
   * @param excludeNull      是否排除该field为NULL的状况，防止下拉建议排序查询时，每次分页都找出NULL的结果，无意义
   */
  @Deprecated
  private SolrQuery generateQueryForSearchProduct(String q, String field, String productName, String productBrand,
                                                  String productSpec, String productModel,
                                                  String pvBrand, String pvModel, String pvYear, String pvEngine,
                                                  Long pvBrandId, Long pvModelId, Long pvYearId, Long pvEngineId,
                                                  Long shopId, boolean includeBasic, Boolean includeInventory,
                                                  boolean excludeNull, String sortStr, Integer start, Integer rows) {
    q = SolrQueryUtils.escape(q);
    productName = SolrQueryUtils.escape(productName);
    productBrand = SolrQueryUtils.escape(productBrand);
    productSpec = SolrQueryUtils.escape(productSpec);
    productModel = SolrQueryUtils.escape(productModel);
    SolrQuery query = new SolrQuery();
    StringBuilder qString = new StringBuilder();
    boolean started = false;
    if (!StringUtils.isBlank(q)) {
      qString.append("(");
      qString.append(field).append(":").append("(").append(q).append(") OR first_letter_combination:(")
          .append(q).append(")) ");
      started = true;
    }
    if (!StringUtils.isBlank(productName)) {
      if (started) qString.append("AND ");
      qString.append("product_name_exact:").append("(\"").append(productName).append("\") ");
      started = true;
    }
    if (!StringUtils.isBlank(productBrand)) {
      if (started) qString.append("AND ");
      qString.append("product_brand_exact:").append("(\"").append(productBrand).append("\") ");
      started = true;
    }
    if (!StringUtils.isBlank(productModel)) {
      if (started) qString.append("AND ");
      qString.append("product_model_exact:").append("(\"").append(productModel).append("\") ");
      started = true;
    }
    if (!StringUtils.isBlank(productSpec)) {
      if (started) qString.append("AND ");
      qString.append("product_spec_exact:").append("(\"").append(productSpec).append("\") ");
      started = true;
    }
    //车辆信息
    if (!StringUtils.isBlank(pvBrand)) {
      if (started) qString.append("AND ");
      qString.append("product_vehicle_brand_exact:").append("(\"").append(pvBrand).append("\") ");
      started = true;
    }
    //车型
    if (!StringUtils.isBlank(pvModel)) {
      if (started) qString.append("AND ");
      qString.append("product_vehicle_model_exact:").append("(\"").append(pvModel).append("\") ");
      started = true;
    }
    if (qString.length() == 0) {
      qString.append("*:*");
      started = true;
    }

    if (excludeNull) qString.append(" AND ").append(field).append(":[* TO *]");
    if (includeInventory != null) {
      if (includeInventory) {
        qString.append(" AND inventory_amount:{0 TO *}");
      } else {
        qString.append(" AND -(inventory_amount:{0 TO *})");
      }
    }
    if (sortStr != null) query.setParam("sort", sortStr);

    query.setQuery(qString.toString());
    StringBuffer fQueryString = new StringBuffer();
    if (includeBasic) fQueryString.append("(");
    fQueryString.append("shop_id:").append(shopId);
    if (includeBasic) {
      fQueryString.append(" OR shop_id:1)");
    } else {
      fQueryString.append(" AND isBasicData:false ");
    }
    String fqString = fQueryString.toString();
    query.setFilterQueries(fqString);
	  query.addFilterQuery("!product_status:DISABLED");
    if (start != null && rows != null) {
      query.setParam("start", start.toString());
      query.setParam("rows", rows.toString());
    }
    return query;
  }

  @Override
  public List<String> queryProductSuggestionList(String q, String field, String productName, String productBrand,
                                                 String productSpec, String productModel,
                                                 String pvBrand, String pvModel, String pvYear, String pvEngine,
                                                 Long pvBrandId, Long pvModelId, Long pvYearId, Long pvEngineId,
                                                 Long shopId, boolean includeBasic,
                                                 Integer start, Integer rows) throws Exception {
    String fieldExact = field + "_exact";
    String sortStr = "lastmodified desc";
    SolrQuery query = null;
    if (StringUtils.isBlank(q)) {
      return queryProductSortForSuggestionList(new ArrayList<String>(), q, field, productName, productBrand,
          productSpec, productModel, pvBrand, pvModel, pvYear, pvEngine,
          pvBrandId, pvModelId, pvYearId, pvEngineId,
          shopId, includeBasic, sortStr, start, rows);
    } else {
      List<String> terms = queryTermsForProduct(q.toLowerCase(), field);
      if (terms == null || terms.size() <= 0) {
        terms = queryTermsForProduct(q.toLowerCase(), "first_letter_combination");
      }
      if (terms.size() == 0) {
        query = generateQueryForSearchProduct(q, field, productName, productBrand,
            productSpec, productModel, pvBrand, pvModel, pvYear, pvEngine,
            pvBrandId, pvModelId, pvYearId, pvEngineId,
            shopId, includeBasic, null, false, null, start, rows);
      } else {
        StringBuilder stringBuilder = new StringBuilder();
        for (String term : terms) {
          stringBuilder.append(term).append(" ");
        }

        query = generateQueryForSearchProduct(stringBuilder.toString().trim(), field, productName, productBrand,
            productSpec, productModel, pvBrand, pvModel, pvYear, pvEngine,
            pvBrandId, pvModelId, pvYearId, pvEngineId,
            shopId, includeBasic, null, false, null, start, rows);
      }
    }
    query.addFacetField(fieldExact).setFacetMinCount(1);
    query.setFacetLimit(10);
    QueryResponse rsp = SolrClientHelper.getProductSolrClient().query(query);
    FacetField facetField = rsp.getFacetField(fieldExact);
    List<FacetField.Count> counts = facetField.getValues();
    List<String> results = new ArrayList<String>();
    if (counts != null) {
      for (FacetField.Count count : counts) {
        String name = count.getName();
        results.add(name);
      }
    }
    return results;
  }

  //查询条件为空时，为下拉建议排序
  private List<String> queryProductSortForSuggestionList(List<String> results, String q,
                                                         String field, String productName, String productBrand,
                                                         String productSpec, String productModel,
                                                         String pvBrand, String pvModel, String pvYear, String pvEngine,
                                                         Long pvBrandId, Long pvModelId, Long pvYearId, Long pvEngineId,
                                                         Long shopId, boolean includeBasic, String sortStr,
                                                         Integer start, Integer rows) throws Exception {
    int i = 0;
    boolean isComplete = false;
    while (!isComplete) {
      isComplete = completeProductList(results, q, field, productName, productBrand,
          productSpec, productModel, pvBrand, pvModel, pvYear, pvEngine,
          pvBrandId, pvModelId, pvYearId, pvEngineId,
          shopId, includeBasic, sortStr, start, rows, i++);
    }
    return results;
  }

  //判断下拉建议是否查全，或者已找不到符合条件的结果
  private boolean completeProductList(List<String> results, String q, String field, String productName,
                                      String productBrand, String productSpec, String productModel,
                                      String pvBrand, String pvModel, String pvYear, String pvEngine,
                                      Long pvBrandId, Long pvModelId, Long pvYearId, Long pvEngineId,
                                      Long shopId, boolean includeBasic, String sortStr,
                                      Integer start, Integer rows, int searchNum) throws Exception {

    SolrQuery query = generateQueryForSearchProduct(q, field, productName, productBrand,
        productSpec, productModel, pvBrand, pvModel, pvYear, pvEngine,
        pvBrandId, pvModelId, pvYearId, pvEngineId,
        shopId, includeBasic, null, true, sortStr, start + rows * searchNum, rows);

    QueryResponse response = SolrClientHelper.getProductSolrClient().query(query);
    excludeDuplicateProduct(results, response, field, rows);
    return response.getResults().size() <= 0 || results.size() == rows;
  }

  /*排除产品下拉建议中重复的值*/
  private void excludeDuplicateProduct(List<String> results, QueryResponse response, String field, int rows) throws Exception {
    for (SolrDocument document : response.getResults()) {
      Object obj = document.getFirstValue(field);
      if (obj != null && !results.contains((String) obj) && results.size() < rows) {
        results.add((String) obj);
      }
    }
  }


  @Override
  public List<ProductDTO> queryProducts(String q, String field, String productName, String productBrand,
                                        String productSpec, String productModel,
                                        String pvBrand, String pvModel, String pvYear, String pvEngine,
                                        Long pvBrandId, Long pvModelId, Long pvYearId, Long pvEngineId,
                                        Long shopId, boolean includeBasic, String sortStatus,
                                        Integer start, Integer rows)
      throws Exception {
    if (StringUtils.isBlank(q)) {
      return queryProductsDirect(q, field, productName, productBrand, productSpec, productModel,
          pvBrand, pvModel, pvYear, pvEngine,
          pvBrandId, pvModelId, pvYearId, pvEngineId,
          shopId, includeBasic, sortStatus, start, rows);
    }
    List<String> terms = queryTermsForProduct(q.toLowerCase(), field);
    if (terms == null || terms.size() <= 0) {
      terms = queryTermsForProduct(q.toLowerCase(), "first_letter_combination");
    }
    if (terms.size() == 0) {
      return queryProductsDirect(q, field, productName, productBrand, productSpec, productModel,
          pvBrand, pvModel, pvYear, pvEngine,
          pvBrandId, pvModelId, pvYearId, pvEngineId,
          shopId, includeBasic, sortStatus, start, rows);
    }
    StringBuilder stringBuilder = new StringBuilder();
    for (String term : terms) {
      stringBuilder.append(term).append(" ");
    }
    return queryProductsDirect(stringBuilder.toString().trim(), field, productName, productBrand, productSpec, productModel,
        pvBrand, pvModel, pvYear, pvEngine,
        pvBrandId, pvModelId, pvYearId, pvEngineId,
        shopId, includeBasic, sortStatus, start, rows);
  }

  @Override
  public void countSolrSearchService(ProductDTO productDTO, MemcacheInventorySumDTO memcacheInventorySumDTO) throws Exception {

    List<ProductDTO> productDTOs = new ArrayList<ProductDTO>();
    Integer start = 0;
    Integer rows = RfTxnConstant.SOLR_INVENTORY_SUM_MAXROWS;
    int i = 0;
    if (memcacheInventorySumDTO == null) {
      memcacheInventorySumDTO = new MemcacheInventorySumDTO();
      memcacheInventorySumDTO.setInventoryCount(0);
      memcacheInventorySumDTO.setInventorySum(0d);
      memcacheInventorySumDTO.setShopId(productDTO.getShopId());
    }
    if (StringUtils.isBlank(productDTO.getName())) {
      do {
        productDTOs = queryProductsDirect(productDTO.getName(), "product_name", "", productDTO.getBrand(), productDTO.getSpec(),
            productDTO.getModel(), productDTO.getProductVehicleBrand(), productDTO.getProductVehicleModel(),
            productDTO.getProductVehicleYear(), productDTO.getProductVehicleEngine(), productDTO.getProductVehicleBrandId(),
            productDTO.getProductVehicleModelId(), productDTO.getProductVehicleYearId(), productDTO.getProductVehicleEngineId(),
            productDTO.getShopId(), false, "", start, rows);
        i++;
        start = rows * i;
        caculateSolrInventorySum(productDTOs, memcacheInventorySumDTO);
      } while (rows.equals(productDTOs.size()));
      return;
    }

    List<String> terms = queryTermsForProduct(productDTO.getName().toLowerCase(), "product_name");
    if (terms == null || terms.size() <= 0) {
      terms = queryTermsForProduct(productDTO.getName().toLowerCase(), "first_letter_combination");
    }
    if (terms.size() == 0) {
      do {
        productDTOs = queryProductsDirect(productDTO.getName(), "product_name", "", productDTO.getBrand(), productDTO.getSpec(),
            productDTO.getModel(), productDTO.getProductVehicleBrand(), productDTO.getProductVehicleModel(),
            productDTO.getProductVehicleYear(), productDTO.getProductVehicleEngine(), productDTO.getProductVehicleBrandId(),
            productDTO.getProductVehicleModelId(), productDTO.getProductVehicleYearId(), productDTO.getProductVehicleEngineId(),
            productDTO.getShopId(), false, "", start, rows);
        i++;
        start = rows * i;
        caculateSolrInventorySum(productDTOs, memcacheInventorySumDTO);
      } while (rows.equals(productDTOs.size()));
      return;
    }
    StringBuilder stringBuilder = new StringBuilder();
    for (String term : terms) {
      stringBuilder.append(term).append(" ");
    }
    do {
      productDTOs = queryProductsDirect(stringBuilder.toString().trim(), "product_name", "", productDTO.getBrand(), productDTO.getSpec(),
          productDTO.getModel(), productDTO.getProductVehicleBrand(), productDTO.getProductVehicleModel(),
          productDTO.getProductVehicleYear(), productDTO.getProductVehicleEngine(), productDTO.getProductVehicleBrandId(),
          productDTO.getProductVehicleModelId(), productDTO.getProductVehicleYearId(), productDTO.getProductVehicleEngineId(),
          productDTO.getShopId(), false, "", start, rows);
      i++;
      start = rows * i;
      caculateSolrInventorySum(productDTOs, memcacheInventorySumDTO);
    } while (rows.equals(productDTOs.size()));
    return;
  }

  private void caculateSolrInventorySum(List<ProductDTO> productDTOs, MemcacheInventorySumDTO memcacheInventorySumDTO) {
    if (memcacheInventorySumDTO == null) {
      memcacheInventorySumDTO = new MemcacheInventorySumDTO();
      memcacheInventorySumDTO.setInventoryCount(0);
      memcacheInventorySumDTO.setInventorySum(0d);
	    memcacheInventorySumDTO.setInventoryProductAmount(0d);
    } else {
      if (memcacheInventorySumDTO.getInventoryCount() == null) {
        memcacheInventorySumDTO.setInventoryCount(0);
      }
      if (memcacheInventorySumDTO.getInventorySum() == null) {
        memcacheInventorySumDTO.setInventorySum(0d);
      }
      if (memcacheInventorySumDTO.getInventoryProductAmount() == null) {
		    memcacheInventorySumDTO.setInventoryProductAmount(0d);
    }
    }
    if (CollectionUtils.isNotEmpty(productDTOs)) {
      memcacheInventorySumDTO.setInventoryCount(memcacheInventorySumDTO.getInventoryCount() + productDTOs.size());
      double totalPrice = 0d;
	    double totalAmount = 0d;
      for (ProductDTO productDTOTemp : productDTOs) {
        if (productDTOTemp == null) {
          continue;
        }
        double pruchasePrice = productDTOTemp.getPurchasePrice() != null ? productDTOTemp.getPurchasePrice() : 0d;
        double amount = productDTOTemp.getInventoryNum() != null ? productDTOTemp.getInventoryNum() : 0d;
        totalAmount += amount;
        totalPrice += pruchasePrice * amount;
      }
      memcacheInventorySumDTO.setInventorySum(memcacheInventorySumDTO.getInventorySum() + totalPrice);
      memcacheInventorySumDTO.setInventoryProductAmount(memcacheInventorySumDTO.getInventoryProductAmount() + totalAmount);
    }
  }

  public int countReturn(Long shopId, Long customerOrSupplierId, OrderTypes orderType, OrderStatus orderStatus) throws BcgogoException{
      SearchWriter writer = searchDaoManager.getWriter();
      return writer.countReturn(shopId,customerOrSupplierId,orderType,orderStatus);
  }

  public int countCarHistory(Long shopId, String vehicle, Long startTime, Long endTime) throws BcgogoException {
    SearchWriter writer = searchDaoManager.getWriter();
    return writer.countCarHistory(shopId, vehicle, startTime, endTime);
  }

  @Deprecated
  public int countRepairOrderHistory(long shopId, String licenceNo, String services, String materialName,
                                     Long fromTime, Long toTime) {
    SearchWriter writer = searchDaoManager.getWriter();
    return writer.countRepairOrderHistory(shopId, licenceNo, services, materialName,
        fromTime, toTime);
  }


  public int countGoodsHistory(ItemIndexDTO itemIndexDTO, Long startTime, Long endTime) throws BcgogoException {
    SearchWriter writer = searchDaoManager.getWriter();
    return writer.countGoodsHistory(itemIndexDTO, startTime, endTime);
  }

  @Override
  public List<ItemIndex> searchItemIndex(ItemIndexDTO dto, Long fromTime, Long toTime, Integer startNo, Integer maxResult) {
    SearchWriter writer = searchDaoManager.getWriter();
    return writer.searchItemIndex(dto, fromTime, toTime, startNo, maxResult);
  }

//  @Override
//  public List<ItemIndex> searchItemIndex
//      (Long shopId, Long customerId, String vehicle, Long orderId,
//       List<OrderTypes> orderType, Long itemId, ItemTypes itemType,
//       String customerOrSupplierName, String itemName,
//       String itemBrand, String itemSpec, String itemModel,
//       String orderStatus, Long fromTime, Long toTime,
//       Integer startNo, Integer maxResult) {
//    SearchWriter writer = searchDaoManager.getWriter();
//    return writer.searchItemIndex(shopId,
//        customerId,
//        vehicle,
//        orderId,
//        orderType,
//        itemId,
//        itemType,
//        customerOrSupplierName,
//        itemName,
//        itemBrand,
//        itemSpec,
//        itemModel,
//        orderStatus,
//        fromTime,
//        toTime,
//        startNo,
//        maxResult);
//  }

  @Override
  public List<InventorySearchIndexDTO> searchInventorySearchIndex
      (Long shopId, String productName, String productBrand,
       String productSpec, String productModel, String pvBrand,
       String pvModel, String pvYear, String pvEngine,
       Integer startNo, Integer maxResult, Boolean inventoryFlag) {
    SearchWriter writer = searchDaoManager.getWriter();
    List<InventorySearchIndex> inventorySearchIndexList = null;
    if (inventoryFlag != null && startNo != null && maxResult != null) {
      List<InventorySearchIndex> indexList1 = writer.searchInventorySearchIndex(shopId, productName,
          productBrand, productSpec, productModel, pvBrand, pvModel, pvYear, pvEngine, startNo, maxResult, true);
      int listSize = indexList1 == null || indexList1.size() <= 0 ? 0 : indexList1.size();
      int p = listSize % maxResult;
      if (p == 0) {
        if (listSize == 0) {
          Long indexListSize = writer.searchInventorySearchIndexCount(shopId, productName,
              productBrand, productSpec, productModel, pvBrand, pvModel, pvYear, pvEngine, true);
          inventorySearchIndexList = writer.searchInventorySearchIndex(shopId, productName,
              productBrand, productSpec, productModel, pvBrand, pvModel, pvYear, pvEngine,
              startNo - indexListSize.intValue(), maxResult, false);
        } else {
          inventorySearchIndexList = indexList1;
        }
      } else {
        List<InventorySearchIndex> indexList2 = null;
        if (indexList1.size() == 0) {
          Long indexListSize = writer.searchInventorySearchIndexCount(shopId, productName,
              productBrand, productSpec, productModel, pvBrand, pvModel, pvYear, pvEngine, true);
          indexList2 = writer.searchInventorySearchIndex(shopId, productName,
              productBrand, productSpec, productModel, pvBrand, pvModel, pvYear, pvEngine,
              startNo - indexListSize.intValue(), maxResult, false);
        } else {
          indexList2 = writer.searchInventorySearchIndex(shopId, productName,
              productBrand, productSpec, productModel, pvBrand, pvModel, pvYear, pvEngine, 0, (maxResult - p), false);
        }
        inventorySearchIndexList = new ArrayList<InventorySearchIndex>();
        for (InventorySearchIndex isi1 : indexList1) {
          inventorySearchIndexList.add(isi1);
        }
        for (InventorySearchIndex isi2 : indexList2) {
          if (isi2 != null && isi2.getShopId() != 1L) {
            inventorySearchIndexList.add(isi2);
          }
        }
      }
    } else {
      inventorySearchIndexList = writer.searchInventorySearchIndex(shopId, productName,
          productBrand, productSpec, productModel, pvBrand, pvModel, pvYear, pvEngine, startNo, maxResult, inventoryFlag);
    }

    if (inventorySearchIndexList != null && inventorySearchIndexList.size() > 0) {
      List<InventorySearchIndexDTO> inventorySearchIndexDTOList = new ArrayList<InventorySearchIndexDTO>();
      for (InventorySearchIndex inventorySearchIndex : inventorySearchIndexList) {
        InventorySearchIndexDTO inventorySearchIndexDTO = inventorySearchIndex.toDTO();
        inventorySearchIndexDTO.setBarcode(inventorySearchIndex.getBarcode());
        inventorySearchIndexDTOList.add(inventorySearchIndexDTO);
      }
      return inventorySearchIndexDTOList;
    }
    return null;
  }

  @Override
  public Long searchInventorySearchIndexCount
      (Long shopId, String productName, String productBrand,
       String productSpec, String productModel, String pvBrand, String pvModel,
       String pvYear, String pvEngine, Boolean inventoryFlag) {
    SearchWriter writer = searchDaoManager.getWriter();
    return writer.searchInventorySearchIndexCount(shopId, productName,
        productBrand, productSpec, productModel, pvBrand, pvModel, pvYear, pvEngine, inventoryFlag);
  }

  @Override
  public Long searchInventorySearchIndexCountForVehicle
      (Long shopId, String productName, String productBrand,
       String productSpec, String productModel, String pvBrand, String pvModel,
       String pvYear, String pvEngine) {
    SearchWriter writer = searchDaoManager.getWriter();
    return writer.searchInventorySearchIndexCountForVehicle(shopId, productName,
        productBrand, productSpec, productModel, pvBrand, pvModel, pvYear, pvEngine);
  }

  @Override
  public Long searchInventorySearchIndexCountForOneVehicle
      (Long shopId, String productName, String productBrand,
       String productSpec, String productModel, String pvBrand, String pvModel,
       String pvYear, String pvEngine) {
    SearchWriter writer = searchDaoManager.getWriter();
    return writer.searchInventorySearchIndexCountForOneVehicle(shopId, productName,
        productBrand, productSpec, productModel, pvBrand, pvModel, pvYear, pvEngine);
  }

  @Override
  public void addItemIndex(ItemIndex item) {
    SearchWriter writer = searchDaoManager.getWriter();
    Object status = writer.begin();
    try {
      writer.save(item);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public void addItemIndexList(List<ItemIndex> itemIndexList) {
    SearchWriter writer = searchDaoManager.getWriter();
    if (itemIndexList != null && !itemIndexList.isEmpty()) {
      Object status = writer.begin();
      try {
        for (ItemIndex itemIndex : itemIndexList) {
          writer.save(itemIndex);
        }
        writer.commit(status);
      } finally {
        writer.rollback(status);
      }
    }
  }

  @Override
  public void addOrUpdateItemIndex(ItemIndex item) {
    SearchWriter writer = searchDaoManager.getWriter();
    Object status = writer.begin();
    List<OrderTypes> itemOrderType = new ArrayList<OrderTypes>();
    itemOrderType.add(item.getOrderTypeEnum());

    ItemIndexDTO dto = new ItemIndexDTO();
    dto.setShopId(item.getShopId());
    dto.setOrderId(item.getOrderId());
    dto.setItemId(item.getItemId());
    dto.setSelectedOrderTypes(itemOrderType);
    try {
      List<ItemIndex> its = writer.searchItemIndex(dto, null, null, null, null);
      if (its == null || its.size() == 0) {
        writer.save(item);
      } else {
        for (ItemIndex itemIndex : its) {
          itemIndex.setCustomerId(item.getCustomerId());
          itemIndex.setVehicle(item.getVehicle());
          itemIndex.setCustomerOrSupplierName(item.getCustomerOrSupplierName());
          itemIndex.setItemName(item.getItemName());
          itemIndex.setItemBrand(item.getItemBrand());
          itemIndex.setItemSpec(item.getItemSpec());
          itemIndex.setItemModel(item.getItemModel());
          itemIndex.setItemMemo(item.getItemMemo());
          itemIndex.setVehicleBrand(item.getVehicleBrand());
          itemIndex.setVehicleModel(item.getVehicleModel());
          itemIndex.setVehicleYear(item.getVehicleYear());
          itemIndex.setVehicleEngine(item.getVehicleEngine());
          itemIndex.setOrderStatus(item.getOrderStatus());
          itemIndex.setItemCount(item.getItemCount());
          itemIndex.setItemPrice(item.getItemPrice());
          itemIndex.setArrears(item.getArrears());
          itemIndex.setServices(item.getServices());
          itemIndex.setOrderTotalAmount(item.getOrderTotalAmount());
          itemIndex.setPaymentTime(item.getPaymentTime());
          writer.update(itemIndex);
        }
      }
      writer.commit(status);

    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public void addOrUpdateInventorySearchIndexWithList(List<InventorySearchIndex> itemList) {
    SearchWriter writer = searchDaoManager.getWriter();
    Object status = writer.begin();
    try {
      for (InventorySearchIndex item : itemList) {
        InventorySearchIndex itemIndex = writer.getById(InventorySearchIndex.class, item.getProductId());
        if (itemIndex == null) {
          item.setId(item.getProductId());
          writer.save(item);
        } else {
          itemIndex.setProductName(item.getProductName());
          itemIndex.setProductBrand(item.getProductBrand());
          itemIndex.setProductSpec(item.getProductSpec());
          itemIndex.setProductModel(item.getProductModel());
          itemIndex.setBrand(item.getBrand());
          itemIndex.setModel(item.getModel());
          itemIndex.setYear(item.getYear());
          itemIndex.setEngine(item.getEngine());
          itemIndex.setEditDate(item.getEditDate());
          itemIndex.setAmount(item.getAmount());
          itemIndex.setParentProductId(item.getParentProductId());
          itemIndex.setProductId(item.getProductId());
          itemIndex.setProductVehicleStatus(item.getProductVehicleStatus());
          itemIndex.setLowerLimit(item.getLowerLimit());
          itemIndex.setUpperLimit(item.getUpperLimit());
          Double price = item.getPrice();
          if (price != null) {
            itemIndex.setPrice(price);
          }
          Double purchasePrice = item.getPurchasePrice();
          if (purchasePrice != null) {
            itemIndex.setPurchasePrice(purchasePrice);
          }
          Double recommendedPrice = item.getRecommendedPrice();
          if (recommendedPrice != null) {
            itemIndex.setRecommendedPrice(recommendedPrice);
          }
          itemIndex.setBarcode(item.getBarcode());
          itemIndex.setUnit(item.getUnit());
	        if (StringUtils.isNotBlank(item.getCommodityCode())) {
		        itemIndex.setCommodityCode(item.getCommodityCode());
	        } else {
		        itemIndex.setCommodityCode(null);
	        }
          Double inventoryAveragePrice = item.getInventoryAveragePrice();
          if(inventoryAveragePrice != null)
          {
             itemIndex.setInventoryAveragePrice(inventoryAveragePrice);
          }
          itemIndex.setKindName(item.getkindName());
          writer.update(itemIndex);
        }
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public void batchAddOrUpdateInventorySearchIndexWithList(Long shopId, List<InventorySearchIndex> inventorySearchIndexes)throws Exception{
    if (shopId == null || CollectionUtil.isEmpty(inventorySearchIndexes)) {
      return;
    }
    SearchWriter writer = searchDaoManager.getWriter();
    Object status = writer.begin();
    try {
      Set<Long> productIds = new HashSet<Long>();
      for (InventorySearchIndex inventorySearchIndex : inventorySearchIndexes) {
        if (inventorySearchIndex.getProductId() != null) {
          productIds.add(inventorySearchIndex.getProductId());
        }
      }
      Map<Long, InventorySearchIndex> inventorySearchIndexMap = writer.getInventorySearchIndexMapByIds(shopId, productIds);

      for (InventorySearchIndex item : inventorySearchIndexes) {
        InventorySearchIndex itemIndex = inventorySearchIndexMap.get(item.getProductId());
        if (itemIndex == null) {
          item.setId(item.getProductId());
          writer.save(item);
          inventorySearchIndexMap.put(item.getProductId(), item);
        } else {
          itemIndex.setProductName(item.getProductName());
          itemIndex.setProductBrand(item.getProductBrand());
          itemIndex.setProductSpec(item.getProductSpec());
          itemIndex.setProductModel(item.getProductModel());
          itemIndex.setBrand(item.getBrand());
          itemIndex.setModel(item.getModel());
          itemIndex.setYear(item.getYear());
          itemIndex.setEngine(item.getEngine());
          itemIndex.setEditDate(item.getEditDate());
          itemIndex.setAmount(item.getAmount());
          itemIndex.setParentProductId(item.getParentProductId());
          itemIndex.setProductId(item.getProductId());
          itemIndex.setProductVehicleStatus(item.getProductVehicleStatus());
          itemIndex.setLowerLimit(item.getLowerLimit());
          itemIndex.setUpperLimit(item.getUpperLimit());
          itemIndex.setPrice(item.getPrice());
          itemIndex.setPurchasePrice(item.getPurchasePrice());
          itemIndex.setRecommendedPrice(item.getRecommendedPrice());
          itemIndex.setBarcode(item.getBarcode());
          itemIndex.setUnit(item.getUnit());
          itemIndex.setCommodityCode(item.getCommodityCode());
          itemIndex.setInventoryAveragePrice(item.getInventoryAveragePrice());
          itemIndex.setKindName(item.getkindName());
          writer.update(itemIndex);
        }
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public void updateInventorySearchIndexAmountWithList
      (List<InventorySearchIndex> itemList) {
    SearchWriter writer = searchDaoManager.getWriter();
    Object status = writer.begin();
    try {
      if (itemList != null && itemList.size() > 0) {
        for (InventorySearchIndex item : itemList) {
          InventorySearchIndex it = writer.getById(InventorySearchIndex.class, item.getProductId());
          if (it != null) {
            it.setEditDate(item.getEditDate());
            it.setAmount(item.getAmount());
            it.setUnit(item.getUnit());
            it.setPurchasePrice(item.getPurchasePrice());
            it.setRecommendedPrice(item.getRecommendedPrice());
            it.setLowerLimit(item.getLowerLimit());
            it.setUpperLimit(item.getUpperLimit());
	          it.setCommodityCode(item.getCommodityCode());
            it.setInventoryAveragePrice(item.getInventoryAveragePrice());
            writer.update(it);
          }
        }
        writer.commit(status);
      }
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public void saveOrUpdateOrderIndex(OrderIndexDTO orderIndexDTO) {
    SearchWriter writer = searchDaoManager.getWriter();
    if (orderIndexDTO == null || orderIndexDTO.getShopId() == null || orderIndexDTO.getOrderId() == null) {
      return;
    }

    List<OrderIndexDTO> orderIndexDTOList = this.getOrderIndexDTOByOrderId(orderIndexDTO.getShopId(), orderIndexDTO.getOrderId());
    if (CollectionUtils.isEmpty(orderIndexDTOList)) {
    OrderIndex orderIndex = new OrderIndex();
      orderIndex = orderIndex.fromDTO(orderIndexDTO, true);
    Object status = writer.begin();
    try {
      writer.save(orderIndex);
      orderIndexDTO.setCreationDate(orderIndex.getCreationDate());
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
    } else {
      OrderIndexDTO indexDTO = orderIndexDTOList.get(0);
      OrderIndex orderIndex = writer.getById(OrderIndex.class, indexDTO.getId());
      orderIndex = orderIndex.fromDTO(orderIndexDTO, false);

      Object status = writer.begin();
      try {
        writer.saveOrUpdate(orderIndex);
        orderIndexDTO.setCreationDate(orderIndex.getCreationDate());
        writer.commit(status);
      } finally {
        writer.rollback(status);
  }
    }
  }

  @Override
  public void updateOrderIndex(OrderIndexDTO orderIndexDTO) {
    SearchWriter searchWriter = searchDaoManager.getWriter();
    if (orderIndexDTO == null) {
      return;
    }
    OrderIndex orderIndex = new OrderIndex();
    orderIndex.fromDTO(orderIndexDTO, true);
    Object status = searchWriter.begin();
    try {
      List<OrderIndex> orderIndexList = searchWriter.searchOrderIndexByOrder(orderIndex.getShopId(),
          orderIndex.getOrderId(), orderIndex.getOrderTypeEnum(), orderIndex.getOrderStatusEnum(), orderIndex.getCustomerOrSupplierId());
      if (orderIndexList != null && orderIndexList.size() > 0) {
        OrderIndex orderIndex1 = orderIndexList.get(0);
        orderIndex1.fromDTO(orderIndexDTO, false);
        searchWriter.update(orderIndex1);
        orderIndexDTO.setCreationDate(orderIndex1.getCreationDate());
//        searchWriter.update(orderIndex);
//        orderIndexDTO.setCreationDate(orderIndex.getCreationDate());

//        OrderIndex orderIndex1 = orderIndexList.get(0);
//        orderIndexDTO.setId(orderIndex1.getId());
//        orderIndexDTO.setCreationDate(orderIndex1.getCreationDate());
//        orderIndex = new OrderIndex(orderIndexDTO);
//        searchWriter.update(orderIndex);
////        searchWriter.delete(OrderIndex.class, orderIndex1.getId());
////        searchWriter.save(orderIndex);


      } else {
        searchWriter.save(orderIndex);
        orderIndexDTO.setCreationDate(orderIndex.getCreationDate());
      }

      searchWriter.commit(status);
    } finally {
      searchWriter.rollback(status);
    }
  }

  @Override
  public void addOrUpdateItemIndexWithList(List<ItemIndex> itemList, List<ItemIndex> itemIndexToDeleteList) {
    SearchWriter writer = searchDaoManager.getWriter();
    Object status = writer.begin();
    try {
      if (itemIndexToDeleteList != null) {
        for (ItemIndex item : itemIndexToDeleteList) {
          List<OrderTypes> itemOrderType = new ArrayList<OrderTypes>();
          itemOrderType.add(item.getOrderTypeEnum());

          ItemIndexDTO dto = new ItemIndexDTO();
          dto.setShopId(item.getShopId());
          dto.setOrderId(item.getOrderId());
          dto.setItemId(item.getItemId());
          dto.setSelectedOrderTypes(itemOrderType);

          List<ItemIndex> its = writer.searchItemIndex(dto, null, null, null, null);
          if (its != null && its.size() > 0) {
            ItemIndex itemIndex = its.get(0);
            writer.delete(ItemIndex.class, itemIndex.getId());
          }
        }
      }

      for (ItemIndex item : itemList) {
        List<OrderTypes> itemOrderType = new ArrayList<OrderTypes>();
        itemOrderType.add(item.getOrderTypeEnum());

        ItemIndexDTO dto = new ItemIndexDTO();
        dto.setShopId(item.getShopId());
        dto.setOrderId(item.getOrderId());
        dto.setItemId(item.getItemId());
        dto.setSelectedOrderTypes(itemOrderType);

        List<ItemIndex> its = writer.searchItemIndex(dto, null, null, null, null);
        if (its == null || its.size() == 0) {
          writer.save(item);
        } else {
          ItemIndex itemIndex = its.get(0);
          itemIndex.setCustomerId(item.getCustomerId());
          itemIndex.setVehicle(item.getVehicle());
          itemIndex.setCustomerOrSupplierName(item.getCustomerOrSupplierName());
          itemIndex.setItemName(item.getItemName());
          itemIndex.setItemBrand(item.getItemBrand());
          itemIndex.setItemSpec(item.getItemSpec());
          itemIndex.setItemModel(item.getItemModel());
          itemIndex.setItemMemo(item.getItemMemo());
          itemIndex.setVehicleBrand(item.getVehicleBrand());
          itemIndex.setVehicleModel(item.getVehicleModel());
          itemIndex.setVehicleYear(item.getVehicleYear());
          itemIndex.setVehicleEngine(item.getVehicleEngine());
          itemIndex.setOrderStatus(item.getOrderStatus());
          itemIndex.setOrderStatusEnum(item.getOrderStatusEnum());
          itemIndex.setItemCount(item.getItemCount());
          itemIndex.setItemPrice(item.getItemPrice());
          itemIndex.setArrears(item.getArrears());
          itemIndex.setServices(item.getServices());
          itemIndex.setOrderTotalAmount(item.getOrderTotalAmount());
          itemIndex.setPaymentTime(item.getPaymentTime());
          itemIndex.setItemCostPrice(item.getItemCostPrice());
          itemIndex.setTotalCostPrice(item.getTotalCostPrice());
          itemIndex.setUnit(item.getUnit());
          itemIndex.setProductId(item.getProductId());
          itemIndex.setServiceId(item.getServiceId());
	        itemIndex.setCommodityCode(item.getCommodityCode());
          itemIndex.setBusinessCategoryName(item.getBusinessCategoryName());
          itemIndex.setBusinessCategoryId(item.getBusinessCategoryId());
          itemIndex.setProductKind(item.getProductKind());
          writer.update(itemIndex);
        }
      }
      writer.commit(status);

    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public void updateItemIndexSupplier(SupplierDTO supplierDTO) {
    SearchWriter writer = searchDaoManager.getWriter();
    Object status = writer.begin();
    try {
      ItemIndexDTO itemIndexDTO = new ItemIndexDTO();
      itemIndexDTO.setShopId(supplierDTO.getShopId());
      itemIndexDTO.setCustomerId(supplierDTO.getId());

      List<ItemIndex> itemIndexes = this.searchItemIndex(itemIndexDTO, null, null, null, null);
      if (null != itemIndexes && itemIndexes.size() > 0) {
        for (ItemIndex itemIndex : itemIndexes) {
          itemIndex.setCustomerOrSupplierName(supplierDTO.getName());
          writer.update(itemIndex);
        }
      }

      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }


  public List<OrderIndex> searchOrderIndexByShopIdAndCustomerOrSupplierId(Long shopId, Long supplierId) {
    SearchWriter writer = searchDaoManager.getWriter();
    return writer.searchOrderIndexByShopIdAndCustomerOrSupplierId(shopId, supplierId);
  }

  @Override
  public void updateOrderIndexSupplier(SupplierDTO supplierDTO) {
    SearchWriter writer = searchDaoManager.getWriter();
    Object status = writer.begin();
    try {
      List<OrderIndex> orderIndexes = searchOrderIndexByShopIdAndCustomerOrSupplierId(supplierDTO.getShopId(), supplierDTO.getId());
      if (null != orderIndexes && orderIndexes.size() > 0) {
        for (OrderIndex orderIndex : orderIndexes) {
          orderIndex.setCustomerOrSupplierName(supplierDTO.getName());
        }
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public void saveOrUpdateInventorySearchIndexByUpdateInfo
      (Long shopId, Long productLocalInfoId, Integer productVehicleStatus,
       String brand, String model, String year, String engine) {
    SearchWriter writer = searchDaoManager.getWriter();
    Object status = writer.begin();
    try {
      InventorySearchIndex inventorySearchIndex = writer.getById(InventorySearchIndex.class, productLocalInfoId);
      inventorySearchIndex.setProductVehicleStatus(productVehicleStatus);
      inventorySearchIndex.setBrand(brand);
      inventorySearchIndex.setModel(model);
      inventorySearchIndex.setYear(year);
      inventorySearchIndex.setEngine(engine);
      writer.saveOrUpdate(inventorySearchIndex);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public void updateInventorySearchIndexByUpdateInfo(Long productLocalInfoId, Double recommendedPrice) {
    SearchWriter writer = searchDaoManager.getWriter();
    Object status = writer.begin();
    try {
      InventorySearchIndex inventorySearchIndex = writer.getById(InventorySearchIndex.class, productLocalInfoId);
      inventorySearchIndex.setRecommendedPrice(recommendedPrice);
      writer.update(inventorySearchIndex);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }


  public int caculateTotalCount(List<ItemIndexDTO> itemIndexDTOList) {
    int totalCount = 0;
    if (itemIndexDTOList == null || itemIndexDTOList.isEmpty()) {
      return totalCount;
    }
    for (ItemIndexDTO itemIndexDTO : itemIndexDTOList) {
      if (itemIndexDTO == null || itemIndexDTO.getItemCount() == null) {
        continue;
      }
      totalCount += itemIndexDTO.getItemCount();
    }
    return totalCount;
  }

  /**
   * 根据单据信息列表计算出总额，并为每一个订单项计算金额
   *
   * @param itemIndexDTOList
   * @return
   */
  private double caculateTotalMoney(List<ItemIndexDTO> itemIndexDTOList) {
    double totalMoney = 0;
    if (itemIndexDTOList == null || itemIndexDTOList.isEmpty()) {
      return totalMoney;
    }
    for (ItemIndexDTO itemIndexDTO : itemIndexDTOList) {
      //计算除作废单以外单据的总金额
      if (itemIndexDTO == null || itemIndexDTO.getItemCount() == null || itemIndexDTO.getItemPrice() == null) {
        continue;
      }
      if (itemIndexDTO.getOrderType() != null && itemIndexDTO.getOrderStatus() != null) {
        if ((itemIndexDTO.getOrderType() == OrderTypes.INVENTORY && itemIndexDTO.getOrderStatus() == OrderStatus.PURCHASE_INVENTORY_REPEAL)) {
          continue;
        }
      }
      itemIndexDTO.setItemTotalAmount(NumberUtil.round(itemIndexDTO.getItemPrice() * itemIndexDTO.getItemCount(), NumberUtil.MONEY_PRECISION));
      totalMoney += itemIndexDTO.getItemPrice() * itemIndexDTO.getItemCount();
    }
    return totalMoney;
  }


  /**
   * 获取某个某个店铺的某个供应商的待入库采购货品信息列表
   *
   * @param shopId
   * @param supplierId
   * @param dateFrom
   * @return
   */
  public List<ItemIndexDTO> getPurchaseOrderListNotInventoried(Long shopId, Long supplierId, Long dateFrom) throws BcgogoException {
    SearchWriter writer = searchDaoManager.getWriter();
    try {
      return writer.getPurchaseOrderNotInventoried(shopId, supplierId, dateFrom);
    } catch (Exception e) {
      throw new BcgogoException(e);
    }
  }

  /**
   * 获取某个客户的所有消费记录列表
   * 包含信息：消费时间，车牌，内容，施工，材料，金额，实际(预计)出厂，状态，欠款，预计还款
   *
   * @param customerId
   * @param shopId
   * @return
   * @throws Exception
   */
  public List<ItemIndexDTO> getConsumeHistoryOfCustomer(Long customerId, Long shopId, Long dateFrom, List<OrderTypes> orderTypes, Sort sort) throws BcgogoException {
    SearchWriter writer = searchDaoManager.getWriter();
    List<ItemIndexDTO> itemIndexDTOList = writer.getConsumeHistoryOfCustomer(customerId, shopId, dateFrom, orderTypes, sort);
    return itemIndexDTOList;
  }

  public List<ItemIndexDTO> getConsumeHistoryOfCustomer(Long customerId, Long shopId, Long dateFrom, List<OrderTypes> orderTypes, Sort sort, int currentPage, int pageSize) throws BcgogoException {
    SearchWriter writer = searchDaoManager.getWriter();
    List<ItemIndexDTO> itemIndexDTOList = writer.getConsumeHistoryOfCustomer(customerId, shopId, dateFrom, orderTypes, sort, currentPage, pageSize);
    return itemIndexDTOList;
  }

  public List<ItemIndexDTO> getConsumeHistoryOfCustomer(Long customerId, Long shopId, Long startTime, Long endTime, List<OrderTypes> orderTypes, Pager pager) throws BcgogoException {
    SearchWriter writer = searchDaoManager.getWriter();
    List<ItemIndexDTO> itemIndexDTOList = writer.getConsumeHistoryOfCustomer(customerId, shopId, startTime, endTime, orderTypes, pager);
    return itemIndexDTOList;
  }

  public int countConsumeHistory(Long customerId, Long shopId) throws BcgogoException {
    Calendar cal = Calendar.getInstance();
    cal.add(Calendar.YEAR, -1);
    List<OrderTypes> orderTypes = new ArrayList<OrderTypes>();
    orderTypes.add(OrderTypes.SALE);
    orderTypes.add(OrderTypes.REPAIR);
    orderTypes.add(OrderTypes.WASH);
    orderTypes.add(OrderTypes.REPAIR_SALE);
    orderTypes.add(OrderTypes.RECHARGE);
    orderTypes.add(OrderTypes.WASH_MEMBER);
    orderTypes.add(OrderTypes.MEMBER_BUY_CARD);
    orderTypes.add(OrderTypes.WASH_BEAUTY);
    orderTypes.add(OrderTypes.SALE_RETURN);
    SearchWriter writer = searchDaoManager.getWriter();
    return writer.countConsumeHistory(customerId, shopId, cal.getTime().getTime(), orderTypes);
  }

  /**
   * 获取某个供应商的所有入库货品详情信息列表
   * 包含信息：时间，品名，品牌，规格，型号，车辆品牌，车型，年代，排量，单价，采购量，金额，备注，操作
   *
   * @param supplierId
   * @param shopId
   * @return
   * @throws Exception
   */
  public List<ItemIndexDTO> getPurchaseInventoryInfoOfSupplier(Long supplierId, Long shopId, Long dateFrom, Long dateTo) throws BcgogoException {
    SearchWriter writer = searchDaoManager.getWriter();
    List<ItemIndexDTO> itemIndexDTOList = writer.getPurchaseInventoryInfoOfSupplier(supplierId, shopId, dateFrom, dateTo);
    return itemIndexDTOList;
  }

  /**
   * 获取当前店铺的某个供应商的入库历史信息
   * 包括：入库单历史记录、入库金额、日期范围
   *
   * @param shopId
   * @param supplierId
   * @param starttimeStr
   * @param endtimeStr
   * @param rowStart
   * @param rowEnd
   * @return
   * @throws BcgogoException,ParseException
   */
  public PurchaseInventoryHistoryDTO getPurchaseInventoryHistory(Long shopId, long supplierId, String starttimeStr, String endtimeStr, int rowStart, int rowEnd) throws BcgogoException, ParseException {
    PurchaseInventoryHistoryDTO purchaseInventoryHistoryDTO = new PurchaseInventoryHistoryDTO();
    Long startDate = null;
    Long endDate = null;
    Calendar cal = Calendar.getInstance();
    //不填日期，默认所有范围内的数据,如果只填一个时间，则按闭区间处理
    if(null!=starttimeStr && !"".equals(starttimeStr)){
      startDate = DateUtil.convertDateStringToDateLong(DateUtil.DATE_STRING_FORMAT_DEFAULT, starttimeStr);
    }
    if(null!=endtimeStr && !"".equals(endtimeStr)){
      endDate = DateUtil.convertDateStringToDateLong(DateUtil.DATE_STRING_FORMAT_DEFAULT,endtimeStr);
    }
    //获取所有数据
    List<ItemIndexDTO> allItemIndexDTOList = getPurchaseInventoryInfoOfSupplier(supplierId, shopId, startDate, endDate);
    if (allItemIndexDTOList == null || allItemIndexDTOList.isEmpty()) {
      return purchaseInventoryHistoryDTO;
    }
    //入库历史总数
    purchaseInventoryHistoryDTO.setInventoryHistorySize(allItemIndexDTOList.size());
    //入库历史总额
    purchaseInventoryHistoryDTO.setInventoryTotalMoney(caculateTotalMoney(allItemIndexDTOList));
    //获取相应入库单列表信息（包含货品单列表）
    purchaseInventoryHistoryDTO.setItemIndexDTOList(allItemIndexDTOList.subList(rowStart, rowEnd < allItemIndexDTOList.size() ? rowEnd : allItemIndexDTOList.size()));
    return purchaseInventoryHistoryDTO;
  }

  /**
   * 获取当前店铺的某个供应商的入库历史信息条数
   *
   * @param shopId
   * @param supplierId
   * @param starttimeStr
   * @param endtimeStr
   * @return
   * @throws BcgogoException,ParseException
   */
  public int getPurchaseInventoryHistoryItemIndexSize(Long shopId, long supplierId, String starttimeStr, String endtimeStr) throws BcgogoException, ParseException{
    Long startDate = null;
    Long endDate = null;
    Calendar cal = Calendar.getInstance();
    //不填日期，默认所有范围内的数据,如果只填一个时间，则按闭区间处理
    if(null!=starttimeStr && !"".equals(starttimeStr)){
      startDate = DateUtil.convertDateStringToDateLong(DateUtil.DATE_STRING_FORMAT_DEFAULT, starttimeStr);
    }
    if(null!=endtimeStr && !"".equals(endtimeStr)){
      endDate = DateUtil.convertDateStringToDateLong(DateUtil.DATE_STRING_FORMAT_DEFAULT,endtimeStr);
    }
    SearchWriter writer = searchDaoManager.getWriter();
    return writer.getPurchaseInventoryHistoryItemIndexSize(supplierId, shopId, startDate, endDate);
  }

  /**
   * 查询某个店铺的某个供应商的待入库采购货品详细信息
   * 包括：待入库历史记录，待入库历史记录总数，商品总数，金额总数
   *
   * @param shopId
   * @param supplierId
   * @param rowStart
   * @param rowEnd
   * @return
   * @throws BcgogoException
   */
  public PurchaseOrderNotInventoriedInfoDTO getPurchaseOrderNotInventoried(Long shopId, long supplierId, int rowStart, int rowEnd) throws BcgogoException {
    PurchaseOrderNotInventoriedInfoDTO purchaseOrderNotInventoriedInfoDTO = new PurchaseOrderNotInventoriedInfoDTO();
    //默认只查1年范围内的数据
    Calendar cal = Calendar.getInstance();
    cal.add(Calendar.YEAR, -1);
    List<ItemIndexDTO> allItemIndexDTOList = getPurchaseOrderListNotInventoried(shopId, supplierId, cal.getTime().getTime());
    if (allItemIndexDTOList == null || allItemIndexDTOList.isEmpty()) {
      return purchaseOrderNotInventoriedInfoDTO;
    }
    //查总量
    purchaseOrderNotInventoriedInfoDTO.setPurchaseOrderNotInventoriedSize(allItemIndexDTOList.size());
    //计算商品总数
    purchaseOrderNotInventoriedInfoDTO.setProductAmount(caculateTotalCount(allItemIndexDTOList));
    //计算金额总数
    purchaseOrderNotInventoriedInfoDTO.setPurchaseOrderNotInventoriedTotalMoney(caculateTotalMoney(allItemIndexDTOList));
    //查详细列表
    purchaseOrderNotInventoriedInfoDTO.setPurchaseOrderNotInventoriedList(allItemIndexDTOList.subList(rowStart, rowEnd < allItemIndexDTOList.size() ? rowEnd : allItemIndexDTOList.size()));
    return purchaseOrderNotInventoriedInfoDTO;
  }

  /**
   * 查询客户消费记录
   *
   * @param customerId
   * @param shopId
   * @return
   * @throws Exception
   */
  public CustomerConsumeDTO findConsumeHistory(Long customerId, Long shopId, Sort sort) throws BcgogoException {
    CustomerConsumeDTO customerConsumeDTO = new CustomerConsumeDTO();
    //默认只查1年范围内的数据
    Calendar cal = Calendar.getInstance();
    cal.add(Calendar.YEAR, -1);
    List<OrderTypes> orderTypes = new ArrayList<OrderTypes>();
    orderTypes.add(OrderTypes.SALE);
    orderTypes.add(OrderTypes.REPAIR);
    orderTypes.add(OrderTypes.WASH);
    orderTypes.add(OrderTypes.REPAIR_SALE);
    orderTypes.add(OrderTypes.RECHARGE);
    orderTypes.add(OrderTypes.WASH_MEMBER);
    orderTypes.add(OrderTypes.MEMBER_BUY_CARD);
    orderTypes.add(OrderTypes.WASH_BEAUTY);
    orderTypes.add(OrderTypes.MEMBER_RETURN_CARD);
    orderTypes.add(OrderTypes.SALE_RETURN);
    List<ItemIndexDTO> itemIndexDTOList = getConsumeHistoryOfCustomer(customerId, shopId, cal.getTime().getTime(), orderTypes, sort);
    customerConsumeDTO.setItemIndexDTOList(itemIndexDTOList);
    if (itemIndexDTOList == null || itemIndexDTOList.isEmpty()) {
      return customerConsumeDTO;
    }
    customerConsumeDTO.setItemCount(itemIndexDTOList.size());
    List<OrderDTO> orderDTOs = new ArrayList<OrderDTO>();
    OrderDTO orderDTO = null;
    boolean orderExist = false;
    //将item合并为order ,遍历所有的item，根据item对应的order类型，选择合适的策略进行处理
    for (ItemIndexDTO itemIndexDTO : itemIndexDTOList) {
      orderExist = true;
      if (itemIndexDTO == null) {
        continue;
      }
      orderDTO = findExistOrderDTO(itemIndexDTO, orderDTOs);
      if (orderDTO == null || orderDTO.getOrderId() == 0) {
        orderExist = false;
        orderDTO = new OrderDTO();
      }
      //根据消费类型选择合适的处理策略
      IndexItemToOrderStragy indexItemToOrderStragy = this.indexItemToOrderStragySelector.selectStragy(itemIndexDTO.getOrderType());
      if (indexItemToOrderStragy == null) {
        continue;
      }
      indexItemToOrderStragy.indexItemToOrder(itemIndexDTO, orderDTO);
      if (!orderExist) {
        orderDTOs.add(orderDTO);
      }
    }
    customerConsumeDTO.setOrderCount(orderDTOs.size());
    customerConsumeDTO.setOrderDTOList(orderDTOs);
    return customerConsumeDTO;
  }

  /**
   * 查询客户消费记录
   *
   * @param customerId
   * @param shopId
   * @return
   * @throws Exception
   */
  public CustomerConsumeDTO findConsumeHistory(Long customerId, Long shopId, Sort sort, int currentPage, int pageSize) throws BcgogoException {
    CustomerConsumeDTO customerConsumeDTO = new CustomerConsumeDTO();
    //默认只查1年范围内的数据
    Calendar cal = Calendar.getInstance();
    cal.add(Calendar.YEAR, -1);
    List<OrderTypes> orderTypes = new ArrayList<OrderTypes>();
    orderTypes.add(OrderTypes.SALE);
    orderTypes.add(OrderTypes.REPAIR);
    orderTypes.add(OrderTypes.WASH);
    orderTypes.add(OrderTypes.REPAIR_SALE);
    orderTypes.add(OrderTypes.RECHARGE);
    orderTypes.add(OrderTypes.WASH_BEAUTY);
    List<ItemIndexDTO> itemIndexDTOList = getConsumeHistoryOfCustomer(customerId, shopId, cal.getTime().getTime(), orderTypes, sort, currentPage, pageSize);
    customerConsumeDTO.setItemIndexDTOList(itemIndexDTOList);
    if (itemIndexDTOList == null || itemIndexDTOList.isEmpty()) {
      return customerConsumeDTO;
    }
    customerConsumeDTO.setItemCount(itemIndexDTOList.size());
    List<OrderDTO> orderDTOs = new ArrayList<OrderDTO>();
    OrderDTO orderDTO = null;
    boolean orderExist = false;
    //将item合并为order ,遍历所有的item，根据item对应的order类型，选择合适的策略进行处理
    for (ItemIndexDTO itemIndexDTO : itemIndexDTOList) {
      orderExist = true;
      if (itemIndexDTO == null) {
        continue;
      }
      orderDTO = findExistOrderDTO(itemIndexDTO, orderDTOs);
      if (orderDTO == null || orderDTO.getOrderId() == 0) {
        orderExist = false;
        orderDTO = new OrderDTO();
      }
      //根据消费类型选择合适的处理策略
      IndexItemToOrderStragy indexItemToOrderStragy = this.indexItemToOrderStragySelector.selectStragy(itemIndexDTO.getOrderType());
      if (indexItemToOrderStragy == null) {
        continue;
      }
      indexItemToOrderStragy.indexItemToOrder(itemIndexDTO, orderDTO);
      if (!orderExist) {
        orderDTOs.add(orderDTO);
      }
    }
    customerConsumeDTO.setOrderCount(orderDTOs.size());
    customerConsumeDTO.setOrderDTOList(orderDTOs);
    return customerConsumeDTO;

  }

  @Override
  public List<OrderDTO> getConsumeOrderHistory(Long customerId, Long shopId, Long startTime, Long endTime, List<OrderTypes> orderTypes, Pager pager) throws BcgogoException {
    List<OrderDTO> orderDTOList = new ArrayList<OrderDTO>();
    List<ItemIndexDTO> itemIndexDTOList = getConsumeHistoryOfCustomer(customerId, shopId, startTime, endTime, orderTypes, pager);
    //将item合并为order ,遍历所有的item，根据item对应的order类型，选择合适的策略进行处理
    for (ItemIndexDTO itemIndexDTO : itemIndexDTOList) {
      boolean orderExist = true;
      if (itemIndexDTO == null) {
        continue;
      }
      OrderDTO orderDTO = findExistOrderDTO(itemIndexDTO, orderDTOList);
      if (orderDTO == null || orderDTO.getOrderId() == 0) {
        orderExist = false;
        orderDTO = new OrderDTO();
      }
      //根据消费类型选择合适的处理策略
      IndexItemToOrderStragy indexItemToOrderStragy = this.indexItemToOrderStragySelector.selectStragy(itemIndexDTO.getOrderType());
      if (indexItemToOrderStragy == null) {
        continue;
      }
      indexItemToOrderStragy.indexItemToOrder(itemIndexDTO, orderDTO);
      if (!orderExist) {
        orderDTOList.add(orderDTO);
      }
    }
    return orderDTOList;
  }


  public int countConsumeHistory(Long customerId, Long shopId, Long startTime, Long endTime, List<OrderTypes> orderTypes) throws BcgogoException{
    List<OrderDTO> orderDTOList = new ArrayList<OrderDTO>();
    List<ItemIndexDTO> itemIndexDTOList = getConsumeHistoryOfCustomer(customerId, shopId, startTime, endTime, orderTypes, null);
    OrderDTO orderDTO = null;
    boolean orderExist = false;
    //将item合并为order ,遍历所有的item，根据item对应的order类型，选择合适的策略进行处理
    for (ItemIndexDTO itemIndexDTO : itemIndexDTOList) {
      orderExist = true;
      if (itemIndexDTO == null) {
        continue;
      }
      orderDTO = findExistOrderDTO(itemIndexDTO, orderDTOList);
      if (orderDTO == null || orderDTO.getOrderId() == 0) {
        orderExist = false;
        orderDTO = new OrderDTO();
      }
      //根据消费类型选择合适的处理策略
      IndexItemToOrderStragy indexItemToOrderStragy = this.indexItemToOrderStragySelector.selectStragy(itemIndexDTO.getOrderType());
      if (indexItemToOrderStragy == null) {
        continue;
      }
      indexItemToOrderStragy.indexItemToOrder(itemIndexDTO, orderDTO);
      if (!orderExist) {
        orderDTOList.add(orderDTO);
      }
    }
    return orderDTOList.size();
  }

  /**
   * 判断orderDTO对象是否已经生成(返回值为空表示尚未生成）
   *
   * @param itemIndexDTO
   * @param orderDTOs
   * @return
   */
  private OrderDTO findExistOrderDTO(ItemIndexDTO itemIndexDTO, List<OrderDTO> orderDTOs) {
    if (itemIndexDTO == null || itemIndexDTO.getOrderId() == null || orderDTOs == null || orderDTOs.isEmpty()) {
      return null;
    }
    for (OrderDTO orderDTO : orderDTOs) {
      if (orderDTO == null) {
        continue;
      }
      if (itemIndexDTO.getOrderId().equals(orderDTO.getOrderId())) {
        return orderDTO;
      }
    }
    return null;
  }

  @Override
  public InventorySearchIndex exactSearchInventorySearchIndex(SearchConditionDTO searchConditionDTO) {
    return searchDaoManager.getWriter().exactSearchInventorySearchIndex(searchConditionDTO);
  }

  @Override
  public String getOrderNamesByOrderId(Long shopId, Long orderId) {
    ItemIndexDTO dto = new ItemIndexDTO();
    dto.setShopId(shopId);
    dto.setOrderId(orderId);
    List<ItemIndex> itemIndexs = searchDaoManager.getWriter().searchItemIndex(dto, null, null, null, null);
    StringBuffer orderProductsName = new StringBuffer();
    if (itemIndexs != null && !itemIndexs.isEmpty()) {
      for (ItemIndex itemIndex : itemIndexs) {
        if (itemIndex.getItemName() != null && !"".equals(itemIndex.getItemName())) {
          orderProductsName.append(itemIndex.getItemName()).append("等、");
        }
      }
    }
    if(orderProductsName.length()>0){
      orderProductsName.setLength(orderProductsName.length()-1);
    }
    return orderProductsName.toString();
  }


  @Override
  public void cancelPurchaseInventoryInSearchDB(Long shopId, PurchaseInventoryDTO purchaseInventoryDTO,
                                                List<InventorySearchIndex> inventorySearchIndexList,
                                                PurchaseOrderDTO purchaseOrderDTO) throws Exception {
    SearchWriter writer = searchDaoManager.getWriter();
    Object status = writer.begin();
    try {
      //update inventorySearchIndex
      for (InventorySearchIndex inventorySearchIndex : inventorySearchIndexList) {
        writer.update(inventorySearchIndex);
      }
      //update Purchase_Inventory order status
      writer.updateOrderIndexStatus(shopId, OrderTypes.INVENTORY, purchaseInventoryDTO.getId(), purchaseInventoryDTO.getStatus());
      writer.updateItemIndexPurchaseOrderStatus(shopId, OrderTypes.INVENTORY, purchaseInventoryDTO.getId(), OrderStatus.PURCHASE_INVENTORY_REPEAL);
      //if exist purchaseOrder update purchase order status
      if (purchaseInventoryDTO.getPurchaseOrderId() != null && !(new Long(0l)).equals(purchaseInventoryDTO.getPurchaseOrderId())) {
        List<OrderIndex> orderIndexs = writer.searchOrderIndexByOrder(shopId, purchaseInventoryDTO.getPurchaseOrderId(),
            OrderTypes.PURCHASE, null, null);

        if (orderIndexs != null && orderIndexs.size() > 0) {
            OrderIndex orderIndex = orderIndexs.get(0);
            orderIndex.setOrderStatusEnum(purchaseOrderDTO.getStatus());
            writer.update(orderIndex);
            writer.updateItemIndexPurchaseOrderStatus(shopId, OrderTypes.PURCHASE,
                purchaseInventoryDTO.getPurchaseOrderId(),purchaseOrderDTO.getStatus());
          }
        }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public List getInventorySearchIndexByShopId(Long shopId, Long start, int num) throws Exception {
    return searchDaoManager.getWriter().getInventorySearchIndexByShopId(shopId, start, num);
  }

  @Override
  public List<ItemIndexDTO> getWashOrderItemIndexList(long shopId, long startTime, long endTime, int pageNo, int pageSize, String arrayType) {
    SearchWriter writer = searchDaoManager.getWriter();

    List<ItemIndex> itemIndexList = writer.getWashOrderItemIndexList(shopId, startTime, endTime, pageNo, pageSize, arrayType);
    List<ItemIndexDTO> itemIndexDTOList = new ArrayList<ItemIndexDTO>();
    if (itemIndexList != null) {
      for (ItemIndex itemIndex : itemIndexList) {
        ItemIndexDTO itemIndexDTO = itemIndex.toDTO();
        itemIndexDTOList.add(itemIndexDTO);
      }
      return itemIndexDTOList;
    }
    return null;
  }


  @Override
  public List<ItemIndexDTO> getWashItemIndexList(long shopId, long startTime, long endTime) {
    SearchWriter writer = searchDaoManager.getWriter();

    List<ItemIndex> itemIndexList = writer.getWashItemIndexList(shopId, startTime, endTime);
    List<ItemIndexDTO> itemIndexDTOList = new ArrayList<ItemIndexDTO>();
    if (itemIndexList != null) {
      for (ItemIndex itemIndex : itemIndexList) {
        ItemIndexDTO itemIndexDTO = itemIndex.toDTO();
        itemIndexDTOList.add(itemIndexDTO);
      }
      return itemIndexDTOList;
    }
    return null;
  }


  @Override
  public List<ItemIndexDTO> getSalesOrderItemIndexList(long shopId, String idString, String arrayType) {
    SearchWriter writer = searchDaoManager.getWriter();

    List<ItemIndex> itemIndexList = writer.getSalesOrderItemIndexList(shopId, idString, arrayType);
    List<ItemIndexDTO> itemIndexDTOList = new ArrayList<ItemIndexDTO>();
    if (itemIndexList != null) {
      for (ItemIndex itemIndex : itemIndexList) {
        ItemIndexDTO itemIndexDTO = itemIndex.toDTO();
        itemIndexDTOList.add(itemIndexDTO);
      }
      return itemIndexDTOList;
    }
    return null;
  }

  @Override
  public List<ItemIndexDTO> getRepairOrderItemIndexList(long shopId, String idString, String arrayType) {
    SearchWriter writer = searchDaoManager.getWriter();

    List<ItemIndex> itemIndexList = writer.getRepairOrderItemIndexList(shopId, idString, arrayType);
    List<ItemIndexDTO> itemIndexDTOList = new ArrayList<ItemIndexDTO>();
    if (itemIndexList != null) {
      for (ItemIndex itemIndex : itemIndexList) {
        ItemIndexDTO itemIndexDTO = itemIndex.toDTO();
        itemIndexDTOList.add(itemIndexDTO);
      }
      return itemIndexDTOList;
    }
    return null;
  }


  //  @Autowired
  @Override
  public List<ItemIndex> searchReturnAbleProducts(ItemIndexDTO itemIndexDTO, OrderTypes type) throws Exception {
    return searchDaoManager.getWriter().searchReturnAbleProducts(itemIndexDTO, type);  //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public List<ItemIndex> searchReturnTotal(ItemIndexDTO itemIndexDTO, Integer startNo) throws Exception {
    return searchDaoManager.getWriter().searchReturnTotal(itemIndexDTO, startNo);  //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public InventorySearchIndex searchInventorySearchIndexAmount(Long shopId, String productName, String productBrand, String productSpec, String productModel, String pvBrand, String pvModel, String pvYear, String pvEngine, String commodityCode) {
    return searchDaoManager.getWriter().searchInventorySearchIndexAmount(shopId, productName, productBrand, productSpec, productModel, pvBrand, pvModel, pvYear, pvEngine, commodityCode);  //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public List<OrderIndex> getOrderIndexByOrderId(Long shopId, Long orderId, OrderTypes orderType, OrderStatus orderStatus, Long customerOrSupplierId) {
    return searchDaoManager.getWriter().searchOrderIndexByOrder(shopId, orderId, orderType, orderStatus, customerOrSupplierId);  //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public void updateOrderIndex(Long shopId, Long orderId, OrderTypes orderType, OrderStatus orderStatus) throws Exception {
//    SearchWriter writer = searchDaoManager.getWriter();
//    writer.updateOrderIndexStatus(shopId, orderType, orderId, orderStatus);

    SearchWriter writer = searchDaoManager.getWriter();
    Object s = writer.begin();
    try {
      writer.updateOrderIndexStatus(shopId, orderType, orderId, orderStatus);
      writer.commit(s);
    } finally {
      writer.rollback(s);
    }
  }

  @Autowired
  private SearchDaoManager searchDaoManager;

  @Autowired
  private IndexItemToOrderStragySelector indexItemToOrderStragySelector;

  @Override
  public InventorySearchIndex getInventorySearchIndexByProductId(Long productId) throws Exception {
    return searchDaoManager.getWriter().getInventorySearchIndexByProductId(productId);
  }

  public ItemIndex getItemIndexByOrderIdAndItemIdAndOrderType(Long orderId, Long orderItemId, OrderTypes orderType) {
    return searchDaoManager.getWriter().getItemIndexByOrderIdAndItemIdAndOrderType(orderId, orderItemId, orderType);
  }

  @Override
  public InventorySearchIndexDTO getInventorySearchIndexById(Long shopId, Long productLocalInfoId) throws Exception {
    SearchWriter writer = searchDaoManager.getWriter();
    InventorySearchIndex inventorySearchIndex = writer.getInventorySearchIndexByProductLocalInfoId(shopId, productLocalInfoId);
    if (inventorySearchIndex != null) {
      return inventorySearchIndex.toDTO();
    } else {
      return null;
    }
  }

  @Override
  public void updateInventorySearchIndexLimit(InventoryLimitDTO inventoryLimitDTO) throws Exception {
    SearchWriter writer = searchDaoManager.getWriter();
    Object status = writer.begin();
    try {
      if (inventoryLimitDTO == null || inventoryLimitDTO.getProductDTOs() == null || inventoryLimitDTO.getShopId() == null) {
        return;
      }
      Set<Long> productLocalInfoIdSet = new HashSet<Long>();
      Map<Long, ProductDTO> productDTOMap = new HashMap<Long, ProductDTO>();
      for (ProductDTO productDTO : inventoryLimitDTO.getProductDTOs()) {
        if (productDTO == null || productDTO.getProductLocalInfoId() == null) {
          continue;
        }
        productLocalInfoIdSet.add(productDTO.getProductLocalInfoId());
        productDTOMap.put(productDTO.getProductLocalInfoId(), productDTO);
      }
      if (productLocalInfoIdSet != null && !productLocalInfoIdSet.isEmpty() && productDTOMap != null) {
        List<InventorySearchIndex> inventorySearchIndexes = writer.getInventorySearchIndexByProductLocalInfoIds(inventoryLimitDTO.getShopId(), productLocalInfoIdSet.toArray(new Long[productLocalInfoIdSet.size()]));
        if (CollectionUtils.isNotEmpty(inventorySearchIndexes)) {
          for (InventorySearchIndex inventorySearchIndex : inventorySearchIndexes) {
            if (inventorySearchIndex == null) {
              continue;
            }
            ProductDTO productDTO = productDTOMap.get(inventorySearchIndex.getId());
            inventorySearchIndex.setLowerLimit(productDTO.getLowerLimit());
            inventorySearchIndex.setUpperLimit(productDTO.getUpperLimit());
            writer.update(inventorySearchIndex);
          }
        }
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public void updateSingelInventoryInventorySearchIndexLimit(Long productId, Double lowerLimitVal, Double upperLimitVal, Long shopId) throws Exception {
    if (shopId == null || productId == null) {
      return;
    }
    SearchWriter writer = searchDaoManager.getWriter();
    Object status = writer.begin();
    try {
      InventorySearchIndex inventorySearchIndex = writer.getInventorySearchIndexByProductLocalInfoId(shopId, productId);
      if (inventorySearchIndex != null && lowerLimitVal != null && upperLimitVal != null) {
        if (lowerLimitVal.equals(inventorySearchIndex.getLowerLimit()) && upperLimitVal.equals(inventorySearchIndex.getUpperLimit())) {
		      return;
	      }
        inventorySearchIndex.setLowerLimit(lowerLimitVal);
        inventorySearchIndex.setUpperLimit(upperLimitVal);
        writer.update(inventorySearchIndex);
        writer.commit(status);
      }
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public List<InventorySearchIndexDTO> getInventorySearchIndexDTOLimit(Long shopId, Pager pager, String searchConditionStr, String sortStr) throws Exception {
    SearchWriter writer = searchDaoManager.getWriter();
    List<InventorySearchIndex> inventorySearchIndexs = null;
    if (RfTxnConstant.INVENTORY_LOWER_LIMIT.equals(searchConditionStr)) {
      inventorySearchIndexs = writer.getLowerLimitInventorySearchIndexs(pager, sortStr, shopId);
    } else if (RfTxnConstant.INVENTORY_UPPER_LIMIT.equals(searchConditionStr)) {
      inventorySearchIndexs = writer.getUpperLimitInventorySearchIndexs(pager, sortStr, shopId);
    }

    List<InventorySearchIndexDTO> inventorySearchIndexDTOs = new ArrayList<InventorySearchIndexDTO>();
    if (CollectionUtils.isNotEmpty(inventorySearchIndexs)) {
      for (InventorySearchIndex inventorySearchIndex : inventorySearchIndexs) {
        if (inventorySearchIndex == null) {
          continue;
        }
        inventorySearchIndexDTOs.add(inventorySearchIndex.toDTO());
      }
    }
    return inventorySearchIndexDTOs;
  }

  @Override
  public List<String> getWashItemTotal(long shopId, long startTime, long endTime) {
    SearchWriter writer = searchDaoManager.getWriter();

    List<String> stringList = writer.getWashItemTotal(shopId, startTime, endTime);
    return stringList;
  }

  @Override
  public double getWashTotalByCustomerId(long shopId, long customerId) {
    SearchWriter writer = searchDaoManager.getWriter();
    return writer.getWashTotalByCustomerId(shopId, customerId);
  }

  @Override
  public List<ItemIndexDTO> getItemIndexDTOListByOrderId(long shopId, long orderId) {
    SearchWriter writer = searchDaoManager.getWriter();
    return writer.getItemIndexDTOListByOrderId(shopId, orderId);
      }

  @Override
  public List<ItemIndex> getItemIndexesByOrderId(Long shopId, Long orderId) {
    SearchWriter writer = searchDaoManager.getWriter();
    return writer.getItemIndexesByOrderId(shopId, orderId);
  }

  @Override
  public List<OrderIndexDTO> getOrderIndexDTOByOrderId(Long shopId, Long orderId) {
    SearchWriter writer = searchDaoManager.getWriter();

    List<OrderIndex> orderIndexList = writer.getOrderIndexDTOByOrderId(shopId, orderId);

    List<OrderIndexDTO> orderIndexDTOList = new ArrayList<OrderIndexDTO>();
    if (orderIndexList != null && orderIndexList.size() > 0) {
      for (OrderIndex orderIndex : orderIndexList) {
        if (orderIndex == null) {
          continue;
        }
        OrderIndexDTO orderIndexDTO = orderIndex.toDTO();
        orderIndexDTOList.add(orderIndexDTO);
      }
      return orderIndexDTOList;
    }
    return null;
  }

	@Override
  public void updatePurchaseOrderIndexStatus(long shopId, long purchaseOrderId, OrderStatus orderStatus) {
    SearchWriter writer = searchDaoManager.getWriter();
    List<OrderIndex> orderIndexList = writer.getOrderIndexDTOByOrderId(shopId, purchaseOrderId);
    if (orderIndexList != null && orderIndexList.size() > 0) {
      OrderIndex orderIndex = orderIndexList.get(0);
      orderIndex.setOrderStatusEnum(orderStatus);
      Object status = writer.begin();
      try {
        writer.update(orderIndex);
        writer.commit(status);
      } finally {
        writer.rollback(status);
      }
    }
  }


  @Override
  public void updateItemIndexName(ItemIndexDTO itemIndexDTO) {
    SearchWriter writer = searchDaoManager.getWriter();

    if (itemIndexDTO == null) {
      return;
    }
    ItemIndex itemIndex = null;
    if (itemIndexDTO.getId() != null) {
      itemIndex = writer.getById(ItemIndex.class, itemIndexDTO.getId());
      itemIndex.setCustomerOrSupplierName(itemIndexDTO.getCustomerOrSupplierName());
    }
    Object status = writer.begin();
    try {
      writer.update(itemIndex);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }


  @Override
  public long countNullProductIDItemIndex() {
    Long count = searchDaoManager.getWriter().countNullProductIDItemIndex();
    return (count == null ? 0 : count.longValue());
  }

  @Override
  public List<ItemIndex> getNullProductIDItemIndexs(Pager pager) {
   return searchDaoManager.getWriter().getNullProductIDItemIndexs(pager);
  }

  @Override
  public void updateItemIndex(ItemIndex itemIndex, ModelMap model, List<Long> itemDTOItemIndexFailUpdate) {
    if (itemIndex == null) {
      return;
    }
    Long successUpdateCount = (Long) model.get("successUpdateCount") == null ? 0L : (Long) model.get("successUpdateCount");
    Long failUpdateCount = (Long) model.get("failUpdateCount") == null ? 0L : (Long) model.get("failUpdateCount");
    SearchWriter writer = searchDaoManager.getWriter();
    Object status = writer.begin();
    try {
      writer.update(itemIndex);
      writer.commit(status);
      if (successUpdateCount != null) {
        successUpdateCount++;
        model.addAttribute("successUpdateCount", successUpdateCount);
       }
    } catch (Exception e) {
      writer.rollback(status);
      if (itemDTOItemIndexFailUpdate != null) {
       itemDTOItemIndexFailUpdate.add(itemIndex.getId());
      }
      if (failUpdateCount != null) {
        failUpdateCount++;
        model.addAttribute("failUpdateCount", failUpdateCount);
      }
      LOG.error("itemIndex 初始化productId出错" + e.getMessage(), e);
    } finally{
      writer.rollback(status);
    }
  }

  @Override
  public List<ItemIndexDTO> getItemIndexDTO(Long shopId, ItemIndexDTO itemIndexDTO, Pager pager) throws Exception {
    SearchWriter writer = searchDaoManager.getWriter();
    List<ItemIndex> itemIndexes = writer.getItemIndex(shopId, itemIndexDTO, pager);
    List<ItemIndexDTO> itemIndexDTOs = new ArrayList<ItemIndexDTO>();
    if (CollectionUtils.isNotEmpty(itemIndexes)) {
      for (ItemIndex itemIndex : itemIndexes) {
         itemIndexDTOs.add(itemIndex.toDTO());
       }
    }
    return itemIndexDTOs;
  }

  @Override
  public Long countItemIndexWithItemIndexDTO(Long shopId, ItemIndexDTO itemIndexDTO) throws Exception {
    SearchWriter writer = searchDaoManager.getWriter();
    return  writer.countItemIndexWithItemIndexDTO(shopId, itemIndexDTO);
  }

  @Override
  public List<ItemIndexDTO> getPurchaseReturnItemIndexDTOs(Long shopId, Long supplierId, Long productId,
                                                           List<OrderIndexDTO> orderIndexDTOs) throws Exception {
    SearchWriter writer = searchDaoManager.getWriter();
    List<ItemIndex> itemIndexes = writer.getPurchaseReturnItemIndex(shopId, supplierId, productId);
    List<ItemIndexDTO> itemIndexDTOs = new ArrayList<ItemIndexDTO>();
    if (orderIndexDTOs == null) {
      orderIndexDTOs = new ArrayList<OrderIndexDTO>();
    }
    boolean isInOrderList = false;
    if (CollectionUtils.isNotEmpty(itemIndexes)) {
      for (ItemIndex itemIndex : itemIndexes) {
        isInOrderList = false;
        if (CollectionUtils.isNotEmpty(orderIndexDTOs)) {
          for (OrderIndexDTO orderIndexDTO : orderIndexDTOs) {
            if (itemIndex.getOrderId().equals(orderIndexDTO.getOrderId())) {
              itemIndex.setOrderStatusEnum(orderIndexDTO.getOrderStatus());

              itemIndexDTOs.add((itemIndex.toDTO()));
              isInOrderList = true;
              break;
            }
          }
        }
        if (isInOrderList) {
          continue;
        }
        List<OrderIndex> orderIndexs = writer.getOrderIndexDTOByOrderId(shopId, itemIndex.getOrderId());
        OrderIndex orderIndex = new OrderIndex();
        if (CollectionUtils.isNotEmpty(orderIndexs)) {
          orderIndex = orderIndexs.get(0);
          orderIndexDTOs.add(orderIndex.toDTO());
        }
        itemIndex.setOrderStatus(orderIndex.getOrderStatus());
	      itemIndex.setOrderStatusEnum(orderIndex.getOrderStatusEnum());
        itemIndexDTOs.add(itemIndex.toDTO());
      }
    }
    return itemIndexDTOs;
  }

  @Override
  public void updateInventorySearchIndex(InventorySearchIndexDTO inventorySearchIndexDTO) throws Exception {
    SearchWriter writer = searchDaoManager.getWriter();
    Object status = writer.begin();
    try {
      InventorySearchIndex inventorySearchIndex = writer.getById(InventorySearchIndex.class, inventorySearchIndexDTO.getId());
      if(inventorySearchIndex==null){
        inventorySearchIndex = new InventorySearchIndex();
        inventorySearchIndex.setId(inventorySearchIndexDTO.getId());
      }
      inventorySearchIndex.setProductName(inventorySearchIndexDTO.getProductName());
      inventorySearchIndex.setProductBrand(inventorySearchIndexDTO.getProductBrand());
      inventorySearchIndex.setProductSpec(inventorySearchIndexDTO.getProductSpec());
      inventorySearchIndex.setProductModel(inventorySearchIndexDTO.getProductModel());
      inventorySearchIndex.setBrand(inventorySearchIndexDTO.getBrand());
      inventorySearchIndex.setModel(inventorySearchIndexDTO.getModel());
      inventorySearchIndex.setRecommendedPrice(inventorySearchIndexDTO.getRecommendedPrice());
      inventorySearchIndex.setLowerLimit(inventorySearchIndexDTO.getLowerLimit());
      inventorySearchIndex.setUpperLimit(inventorySearchIndexDTO.getUpperLimit());
	    inventorySearchIndex.setAmount(inventorySearchIndexDTO.getAmount());
      inventorySearchIndex.setInventoryAveragePrice(inventorySearchIndexDTO.getInventoryAveragePrice());
	    if(StringUtils.isNotBlank(inventorySearchIndexDTO.getUnit())){
		    inventorySearchIndex.setUnit(inventorySearchIndexDTO.getUnit());
	    }
	    inventorySearchIndex.setCommodityCode(StringUtils.isNotBlank(inventorySearchIndexDTO.getCommodityCode()) ? inventorySearchIndexDTO.getCommodityCode() : null);
      inventorySearchIndex.setKindName(inventorySearchIndexDTO.getKindName());
      writer.saveOrUpdate(inventorySearchIndex);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }


  @Override
  public int countRepairOrderHistoryByToDayNewVehicle(Long shopId, String vehicle, String services, String itemName, Long endDateLong, Long endDateLong2, List<String> licenceNoList) {
    SearchWriter writer = searchDaoManager.getWriter();
    return writer.countRepairOrderHistoryByToDayNewVehicle(shopId, vehicle, services, itemName, endDateLong, endDateLong2, licenceNoList);
  }

  @Override
  public List<ItemIndexDTO> getRepairOrderHistoryByTodayNewCustomer(Long shopId, String vehicle, String services, String itemName, Long startDateTIme, Long endDateTime, Pager pager, List<String> licenceNoList) throws BcgogoException, InvocationTargetException, IllegalAccessException {
    SearchWriter writer = searchDaoManager.getWriter();
    return writer.getRepairOrderHistoryByTodayNewCustomer(shopId, vehicle, services, itemName, startDateTIme, endDateTime, pager, licenceNoList);
  }

	@Override
	public List<ProductSupplierDTO> getProductSupplierDTO(Long shopId, Long startProductId, Long endProductId) {
		SearchWriter writer = searchDaoManager.getWriter();
		return writer.getProductSupplierDTO(shopId, startProductId, endProductId, null);
	}

	@Override
	public Long getItemIndexNextProductIdWithSupplier(Long shopId, Long startProductId, int rows) {
		SearchWriter writer = searchDaoManager.getWriter();
		return writer.getItemIndexNextProductIdWithSupplier(shopId,startProductId,rows);
	}

  /**
   * 根据shopId获得老洗车单的 非会员洗车和洗车充值 用于流水统计初始化
   * @param shopId
   * @return
   */
  @Override
  public long countWashItemIndexByShopId(long shopId) {
    SearchWriter writer = searchDaoManager.getWriter();
    return writer.countWashItemIndexByShopId(shopId);
  }

  @Override
  public void updateSaleOrderIndexReceiptNo(List<InitReceiptNoOrderIndexDTO> initReceiptNoOrderIndexDTOs)
  {
    if(CollectionUtils.isEmpty(initReceiptNoOrderIndexDTOs))
    {
      return;
    }
    SearchWriter writer = searchDaoManager.getWriter();

    Object status = writer.begin();

    OrderIndex orderIndex = null;

    try{
      for(InitReceiptNoOrderIndexDTO initReceiptNoOrderIndexDTO : initReceiptNoOrderIndexDTOs)
      {
        orderIndex = writer.getOrderIndex(initReceiptNoOrderIndexDTO.getShopId(),initReceiptNoOrderIndexDTO.getOrderId());
        if(null == orderIndex)
        {
          continue;
        }
        orderIndex.setReceiptNo(initReceiptNoOrderIndexDTO.getReceiptNo());
        writer.update(orderIndex);
      }
      writer.commit(status);
    }finally {
      writer.rollback(status);
    }
  }


		@Override
	public Integer countInventoryLowerLimitAmount(Long shopId) {
		return searchDaoManager.getWriter().countInventoryLowerLimitAmount(shopId);
	}

	@Override
	public Integer countInventoryUpperLimitAmount(Long shopId) {
		return searchDaoManager.getWriter().countInventoryUpperLimitAmount(shopId);
	}

	@Override
	public List<InventorySearchIndexDTO> getInventorySearchIndexDTOsByProductIds(Long shopId, Long... productIds) {
		SearchWriter writer = searchDaoManager.getWriter();
		List<InventorySearchIndexDTO> inventorySearchIndexDTOs = new ArrayList<InventorySearchIndexDTO>();
		if (shopId != null && productIds != null && productIds.length > 0) {
			List<InventorySearchIndex> inventorySearchIndexes = writer.getInventorySearchIndexByProductLocalInfoIds(shopId, productIds);
			if (CollectionUtils.isNotEmpty(inventorySearchIndexes)) {
				for (InventorySearchIndex inventorySearchIndex : inventorySearchIndexes) {
					inventorySearchIndexDTOs.add(inventorySearchIndex.toDTO());
				}
			}
		}
		return inventorySearchIndexDTOs;
	}

	@Override
	public Map<Long, InventorySearchIndex> getInventorySearchIndexMapByProductIds(Long shopId, Long... productIds) {
		if (shopId == null || ArrayUtils.isEmpty(productIds)) {
			return new HashMap<Long, InventorySearchIndex>();
		}
		SearchWriter writer = searchDaoManager.getWriter();
		Map<Long, InventorySearchIndex> inventorySearchIndexMap = new HashMap<Long, InventorySearchIndex>();
			List<InventorySearchIndex> inventorySearchIndexes = writer.getInventorySearchIndexByProductLocalInfoIds(shopId, productIds);
			if (CollectionUtils.isNotEmpty(inventorySearchIndexes)) {
				for (InventorySearchIndex inventorySearchIndex : inventorySearchIndexes) {
					inventorySearchIndexMap.put(inventorySearchIndex.getId(), inventorySearchIndex);
				}
			}
		return inventorySearchIndexMap;
	}

	@Override
	public void updateOrderIndexAfterRepealWashOrder(WashBeautyOrderDTO washBeautyOrderDTO) {
		if (washBeautyOrderDTO == null || washBeautyOrderDTO.getShopId() == null || washBeautyOrderDTO.getId() == null) {
			return;
		}
		SearchWriter writer = searchDaoManager.getWriter();
		Object status = writer.begin();
		try {
			OrderIndex orderIndex = writer.getOrderIndex(washBeautyOrderDTO.getShopId(), washBeautyOrderDTO.getId());
			if (orderIndex != null) {
				orderIndex.setOrderStatusEnum(OrderStatus.WASH_REPEAL);
				writer.update(orderIndex);
			}
			List<ItemIndex> itemIndexes = writer.getItemIndexDTOByOrderId(washBeautyOrderDTO.getShopId(), washBeautyOrderDTO.getId());
			if (CollectionUtils.isNotEmpty(itemIndexes)) {
				for (ItemIndex itemIndex : itemIndexes) {
					if (itemIndex == null) {
						continue;
					}
					itemIndex.setOrderStatusEnum(OrderStatus.WASH_REPEAL);
					writer.update(itemIndex);
				}
			}
			writer.commit(status);

		} finally {
			writer.rollback(status);
		}
	}

  @Override
  public void updateInventorySearchIndexKindInfo(Long shopId, String oldKindName, String newKindName){
    //将本店分类名为oldKindName的商品的分类名更新为newKindName
    SearchWriter writer = searchDaoManager.getWriter();
    Object status = writer.begin();
    try{
      writer.updateInventorySearchIndexKindName(shopId,oldKindName,newKindName);
      writer.commit(status);
    }finally {
      writer.rollback(status);
    }
  }

  @Override
  public void updateMultipleInventoryKind(Long shopId, Long[] inventorySearchIndexIdList, String newKindName){
    SearchWriter writer = searchDaoManager.getWriter();
    Object status = writer.begin();
    try{
      for(int i=0;i<inventorySearchIndexIdList.length;i++){
        InventorySearchIndex idi = writer.getById(InventorySearchIndex.class,inventorySearchIndexIdList[i]);
        idi.setKindName(newKindName);
        writer.update(idi);
      }
      writer.commit(status);
    }finally {
      writer.rollback(status);
    }
  }

  @Override
  public Long[] getInventorySearchIndexIdListByProductKind(Long shopId, String kindName){
    SearchWriter writer = searchDaoManager.getWriter();
    List<Long> idList =  writer.getInventorySearchIndexIdListByProductKind(shopId,kindName);
    Long[] ids = new Long[idList.size()];
    for(int i=0;i<ids.length;i++){
      ids[i] = idList.get(i);
    }
    return ids;
  }

  @Override
  public void deleteMultipleInventoryKind(Long shopId, String kindName){
    SearchWriter writer = searchDaoManager.getWriter();
    Object status = writer.begin();
    try{
      writer.deleteMultipleInventoryKind(shopId,kindName);
      writer.commit(status);
    }finally {
      writer.rollback(status);
    }
  }

	@Override
	public void updateMultipleInventorySearchIndexRecommendedPrice(ProductDTO[] productDTOs, Long shopId) throws Exception {
		if (productDTOs == null && productDTOs.length == 0) {
			return;
		}
		Long[] productIds = new Long[productDTOs.length];
		Map<Long, ProductDTO> productDTOMap = new HashMap<Long, ProductDTO>(productDTOs.length * 2, 0.75f);
		for (int i = 0, len = productDTOs.length; i < len; i++) {
			productIds[i] = productDTOs[i].getProductLocalInfoId();
			productDTOMap.put(productDTOs[i].getProductLocalInfoId(), productDTOs[i]);
		}
		SearchWriter writer = searchDaoManager.getWriter();
		Object status = writer.begin();
		try {
			List<InventorySearchIndex> inventorySearchIndexes = writer.getInventorySearchIndexByProductLocalInfoIds(shopId, productIds);
			if (CollectionUtils.isNotEmpty(inventorySearchIndexes)) {
				for (InventorySearchIndex inventorySearchIndex : inventorySearchIndexes) {
					if (productDTOMap != null && inventorySearchIndex != null && productDTOMap.get(inventorySearchIndex.getId()) != null) {
						inventorySearchIndex.setRecommendedPrice(productDTOMap.get(inventorySearchIndex.getId()).getRecommendedPrice());
						writer.update(inventorySearchIndex);
					}
				}
			}
			writer.commit(status);
		} finally {
			writer.rollback(status);
		}
	}

	@Override
	public void getLimitSearchCount(Long shopId, String searchConditionStr, ProductSearchResultListDTO productSearchResultListDTO) {
		SearchWriter writer = searchDaoManager.getWriter();
		Object[] results = null;
		if (RfTxnConstant.INVENTORY_LOWER_LIMIT.equals(searchConditionStr)) {
			results = writer.getLowerLimitSearchCount(shopId);
		} else if (RfTxnConstant.INVENTORY_UPPER_LIMIT.equals(searchConditionStr)) {
			results = writer.getUpperLimitSearchCount(shopId);
		}
		if (results != null && results.length == 3) {
			productSearchResultListDTO.setInventoryCount(results[0] == null ? 0 : (Long) results[0]);
			productSearchResultListDTO.setInventoryAmount(results[1] == null ? 0d : (Double) results[1]);
			productSearchResultListDTO.setTotalPurchasePrice(results[2] == null ? 0d : (Double) results[2]);
		}
	}

  /**
   * 合并客户的orderindex
   * @param result
   * @param childIds
   * @return
   * @throws BcgogoException
   */
  public String mergeCustomerOrderIndex(MergeResult<CustomerDTO,MergeCustomerSnap> result,Long[] childIds) throws BcgogoException {
    SearchWriter searchWriter=searchDaoManager.getWriter();
    CustomerDTO parent=result.getCustomerOrSupplierDTO();
    List<OrderIndex> orderIndexes= searchWriter.getCustomerOrSupplierOrderIndexs(parent.getShopId(),childIds);
    List<ItemIndex> itemIndexes= searchWriter.getItemIndexByCustomerIds(parent.getShopId(),childIds);
    Object status=searchWriter.begin();
    try{
      MergeChangeLogDTO mergeChangeLog=null;
      if(CollectionUtils.isNotEmpty(orderIndexes)){
        for(OrderIndex orderIndex:orderIndexes){
          if(orderIndex==null){
            continue;
          }
          orderIndex.setCustomerOrSupplierId(parent.getId());
          result.getMergeChangeLogs().add(MergeLogUtil.log_orderIndex_customerId(parent.getShopId(), parent.getUserId(), orderIndex.getId(),
              orderIndex.getCustomerOrSupplierId(), parent.getId()));
          searchWriter.update(orderIndex);
        }
      }
      if(CollectionUtils.isNotEmpty(itemIndexes)){
        for(ItemIndex itemIndex:itemIndexes){
          if(itemIndex==null){
            continue;
          }
          itemIndex.setCustomerId(parent.getId());
          searchWriter.update(itemIndex);
          result.getMergeChangeLogs().add(MergeLogUtil.log_orderItem_customerId(parent.getShopId(), parent.getUserId(), itemIndex.getId(),
              itemIndex.getCustomerId(), parent.getId()));
        }
      }
      searchWriter.commit(status);
      return "succ";
    }catch (Exception e){
      LOG.error("合并orderIndex异常！！！");
      searchWriter.rollback(status);
      throw new BcgogoException(e.getMessage());
    }finally{
      searchWriter.rollback(status);
    }
  }

  /**
   * 合并供应商的orderindex
   * @param result
   * @param childIds
   * @return
   * @throws BcgogoException
   */
  public String mergeSupplierOrderIndex(MergeResult<SupplierDTO,MergeSupplierSnap> result,Long[] childIds) throws BcgogoException {
     SearchWriter searchWriter=searchDaoManager.getWriter();
     SupplierDTO parent=result.getCustomerOrSupplierDTO();
     List<OrderIndex> orderIndexes= searchWriter.getCustomerOrSupplierOrderIndexs(parent.getShopId(),childIds);
     Object status=searchWriter.begin();
     try{
       MergeChangeLogDTO mergeChangeLog=null;
       if(CollectionUtils.isNotEmpty(orderIndexes)){
         for(OrderIndex orderIndex:orderIndexes){
           if(orderIndex==null){
             continue;
           }
           orderIndex.setCustomerOrSupplierId(parent.getId());
           result.getMergeChangeLogs().add(MergeLogUtil.log_orderIndex_customerId(parent.getShopId(), parent.getUserId(), orderIndex.getId(),
               orderIndex.getCustomerOrSupplierId(), parent.getId()));
           searchWriter.update(orderIndex);
         }
       }
       searchWriter.commit(status);
       return "succ";
     }catch (Exception e){
       LOG.error("合并orderIndex异常！！！");
       searchWriter.rollback(status);
       throw new BcgogoException(e.getMessage());
     }
   }




	@Override
	public void updateSearchOrderStatus(SalesOrderDTO salesOrderDTO, PurchaseOrderDTO purchaseOrderDTO) {
		SearchWriter writer = searchDaoManager.getWriter();
		Object status = writer.begin();
		try{
			if (salesOrderDTO != null && salesOrderDTO.getShopId() != null
					    && salesOrderDTO.getId() != null && salesOrderDTO.getStatus() != null) {
				OrderIndex saleOrderIndex = writer.getOrderIndex(salesOrderDTO.getShopId(), salesOrderDTO.getId());
				if (saleOrderIndex != null) {
					saleOrderIndex.setOrderStatusEnum(salesOrderDTO.getStatus());
					writer.update(saleOrderIndex);
				}
				List<ItemIndex> saleOrderItemIndexes = writer.getItemIndexDTOByOrderId(salesOrderDTO.getShopId(), salesOrderDTO.getId());
				if (CollectionUtils.isNotEmpty(saleOrderItemIndexes)) {
					for (ItemIndex itemIndex : saleOrderItemIndexes) {
						itemIndex.setOrderStatusEnum(salesOrderDTO.getStatus());
						writer.update(itemIndex);
					}
				}
			}
			if (purchaseOrderDTO != null && purchaseOrderDTO.getShopId() != null
					    && purchaseOrderDTO.getId() != null && purchaseOrderDTO.getStatus() != null) {
				OrderIndex purchaseOrderIndex = writer.getOrderIndex(purchaseOrderDTO.getShopId(), purchaseOrderDTO.getId());
				if (purchaseOrderIndex != null) {
					purchaseOrderIndex.setOrderStatusEnum(purchaseOrderDTO.getStatus());
					writer.update(purchaseOrderIndex);
				}
				List<ItemIndex> purchaseOrderItemIndexes = writer.getItemIndexDTOByOrderId(purchaseOrderDTO.getShopId(), purchaseOrderDTO.getId());
				if (CollectionUtils.isNotEmpty(purchaseOrderItemIndexes)) {
					for (ItemIndex itemIndex : purchaseOrderItemIndexes) {
						itemIndex.setOrderStatusEnum(purchaseOrderDTO.getStatus());
						writer.update(itemIndex);
					}
				}
			}
			writer.commit(status);
		} finally {
			writer.rollback(status);
		}
	}

  /**
   * product是否存在于进行中的单据
   * @param productId productLocalInfoId
   * @param shopId
   * @return
   */
  @Override
  public boolean isProductInUse(Long productId, Long shopId) {
    SearchWriter writer = searchDaoManager.getWriter();
    int inProgressNum = writer.countItemIndexByProductIdOrderStatus(productId, shopId, OrderUtil.inProgressStatusMap);
    if(inProgressNum>0){
      return true;
    }
    return false;
  }

  /**
   * service是否存在于进行中的单据
   * @param serviceId
   * @param shopId
   * @return
   */
  @Override
  public boolean isServiceInUse(Long serviceId, Long shopId){
    SearchWriter writer = searchDaoManager.getWriter();
    int inProgressNum = writer.countItemIndexByServiceIdOrderStatus(serviceId, shopId, OrderUtil.inProgressStatusMap);
    if(inProgressNum>0){
      return true;
    }
    return false;
  }

  /**
   * 删除一个order的itemIndex
   * @param shopId
   * @param orderId
   */
  @Override
  public void deleteItemIndex(Long shopId, Long orderId) {
    if (shopId == null || orderId == null ) {
      return;
    }
      SearchWriter writer = searchDaoManager.getWriter();
      Object status = writer.begin();
      try {
        List<ItemIndex> indexes = writer.getItemIndexesByOrderId(shopId, orderId);
        if (CollectionUtils.isNotEmpty(indexes)) {
          for (ItemIndex itemIndex : indexes) {
            writer.delete(itemIndex);
          }
        }
        writer.commit(status);
      } finally {
        writer.rollback(status);
      }
    }

  @Override
  public int countRepairOrderInOrderIndex(Long shopId, Long fromTime, Long toTime) {
    SearchWriter writer = searchDaoManager.getWriter();
    return writer.countRepairOrderInOrderIndex(shopId, fromTime, toTime);
  }
}
