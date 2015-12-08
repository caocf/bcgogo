package com.bcgogo.search.service.product;

import com.bcgogo.common.Pager;
import com.bcgogo.constant.ProductConstants;
import com.bcgogo.enums.Product.ProductRelevanceStatus;
import com.bcgogo.enums.ProductStatus;
import com.bcgogo.exception.BcgogoException;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.search.client.SolrClientHelper;
import com.bcgogo.search.dto.*;
import com.bcgogo.search.service.CurrentUsed.IProductCurrentUsedService;
import com.bcgogo.search.util.SolrQueryUtils;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.PromotionsDTO;
import com.bcgogo.txn.dto.StoreHouseInventoryDTO;
import com.bcgogo.txn.dto.SupplierInventoryDTO;
import com.bcgogo.utils.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.*;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: caiweili
 * Date: 8/1/12
 * Time: 2:18 PM
 * To change this template use File | Settings | File Templates.
 */
@Component
public class SearchProductService implements ISearchProductService {
  private static final Logger LOG = LoggerFactory.getLogger(SearchProductService.class);
  private static final int MAX_QUERY_TIMES = 15;

  //下拉建议(查询memcache 再查询solr）
  @Override
  @Deprecated
  public List<SearchSuggestionDTO> getProductSuggestion(SearchConditionDTO searchConditionDTO) throws Exception {
    if (!searchConditionDTO.gotoMemCacheFunction()) {
      QueryResponse rsp = queryProductSuggestionWithResponse(searchConditionDTO);
      return processQueryResult(rsp.getResults(), searchConditionDTO.getSearchWord(), searchConditionDTO.getSearchField());
    }
    IProductCurrentUsedService productCurrentUsedService = ServiceManager.getService(IProductCurrentUsedService.class);
    return productCurrentUsedService.getProductSuggestionFromMemory(new SearchMemoryConditionDTO(searchConditionDTO));
  }

  //库存查询（不知道Field的情况下6字段全部查询）
  @Override
  public ProductSearchResultListDTO queryProductWithUnknownField(SearchConditionDTO searchConditionDTO) throws Exception {
    StringBuilder qString = new StringBuilder();
    boolean started = false;
    if (!StringUtils.isBlank(searchConditionDTO.getSearchWord())) {
      generateMultifieldSuggestionQuery(qString, searchConditionDTO.getSearchWord());
      started = true;
    }

    String productIds = searchConditionDTO.getProductIds();
    if (!StringUtils.isBlank(productIds)) {
      started = generateStringArrayRelatedQuery("product_id", productIds.split(","), started, qString);
    }
    if (!ArrayUtils.isEmpty(searchConditionDTO.getShopIds())) {
      if (started) {
        qString.append(" AND ");
      }
      qString.append("shop_id:(");
      for (int i = 0, max = searchConditionDTO.getShopIds().length; i < max; i++) {
        qString.append(searchConditionDTO.getShopIds()[i]);
        if (i < (max - 1)) qString.append(" OR ");
      }
      qString.append(")");
      started = true;
    }
    if (!ArrayUtils.isEmpty(searchConditionDTO.getExcludeShopIds())) {
      if (started) {
        qString.append(" AND ");
      }
      qString.append("!shop_id:(");
      for (int i = 0, max = searchConditionDTO.getExcludeShopIds().length; i < max; i++) {
        qString.append(searchConditionDTO.getExcludeShopIds()[i]);
        if (i < (max - 1)) qString.append(" OR ");
      }
      qString.append(")");
      started = true;
    }

    started = generateProductCategoryStringArrayQuery("product_category_id", searchConditionDTO.getProductCategoryIds(), started, qString);

    qString = getQueryOfProductWithStd(started, qString, searchConditionDTO);

    if (qString.length() == 0) {
      qString.append("*:*");
    }
    SolrQuery query = new SolrQuery();
    query.setQuery(qString.toString());
    query.setParam("debugQuerydebugQuery", "true");

    generateFilterQuery(searchConditionDTO, query);

    if(searchConditionDTO.getJoinSearchConditionDTO()!=null){
      generateFilterJoinSupplierOrShopQuery(searchConditionDTO.getJoinSearchConditionDTO(),query);
    }


    query.setParam("fl", "*,score");
    if (searchConditionDTO.getRows() <= 0) {
      searchConditionDTO.setRows(SolrQueryUtils.getSelectOptionNumber());
    }
    query.setStart(searchConditionDTO.getStart());
    query.setRows(searchConditionDTO.getRows());
    if (StringUtils.isNotBlank(searchConditionDTO.getSort())) {
      query.setParam("sort", searchConditionDTO.getSort());
      query.addSortField("product_id", SolrQuery.ORDER.asc);
    }
    if(searchConditionDTO.getSearchStrategy()!=null
      && Arrays.asList(searchConditionDTO.getSearchStrategy()).contains(SearchConditionDTO.SEARCHSTRATEGY_STATS)
      && !ArrayUtils.isEmpty(searchConditionDTO.getStatsFields())){
      query.setParam("stats", "true");
      query.setParam("stats.field",searchConditionDTO.getStatsFields());
    }

    QueryResponse rsp = SolrClientHelper.getProductSolrClient().query(query);
    // mergeCache  move to controller use IsolrMergeService
    ProductSearchResultListDTO productSearchResultListDTO = getProductDTOByQueryResponses(searchConditionDTO,rsp);
    return productSearchResultListDTO;
  }

  @Override
  public ProductSearchResultListDTO queryProductWithStdQuery(SearchConditionDTO searchConditionDTO) throws Exception {
    boolean started = false;
    StringBuilder qString = new StringBuilder();
    qString = getQueryOfProductWithStd(started, qString, searchConditionDTO);
    SolrQuery query = new SolrQuery();
    if (qString.length() == 0) {
      qString.append("*:*");
    }
    query.setQuery(qString.toString());
//    query.setParam("debugQuery", "true");
    generateFilterQuery(searchConditionDTO, query);
    query.setParam("fl", "*,score");
    if (searchConditionDTO.getRows() <= 0) {
      searchConditionDTO.setRows(SolrQueryUtils.getSelectOptionNumber());
    }
    query.setStart(searchConditionDTO.getStart());
    query.setRows(searchConditionDTO.getRows());
    query.setParam("q.op", "AND");
    if (StringUtils.isNotBlank(searchConditionDTO.getSort())) {
      query.setParam("sort", searchConditionDTO.getSort());
      query.addSortField("product_id", SolrQuery.ORDER.asc);
    }

    if(searchConditionDTO.getSearchStrategy()!=null
      && Arrays.asList(searchConditionDTO.getSearchStrategy()).contains(SearchConditionDTO.SEARCHSTRATEGY_STATS)
      && !ArrayUtils.isEmpty(searchConditionDTO.getStatsFields())){
      query.setParam("stats", "true");
      query.setParam("stats.field",searchConditionDTO.getStatsFields());
    }

    QueryResponse rsp = SolrClientHelper.getProductSolrClient().query(query);
    ProductSearchResultListDTO productSearchResultListDTO = getProductDTOByQueryResponses(searchConditionDTO,rsp);

    return productSearchResultListDTO;
  }

  @Deprecated
  @Override
  public List<String> queryProductSuggestionWithSimpleList(SearchConditionDTO searchConditionDTO) throws Exception {

    QueryResponse rsp = queryProductSuggestionWithResponse(searchConditionDTO);

    return processQueryResultWithSimpleList(rsp, searchConditionDTO.getSearchWord(), searchConditionDTO.getSearchField());
  }


  //拼接标准查询query
  private StringBuilder getQueryOfProductWithStd(boolean started, StringBuilder qString, SearchConditionDTO searchConditionDTO) {
    String shopName = SolrQueryUtils.escape(searchConditionDTO.getShopName());
    String productName = SolrQueryUtils.escape(searchConditionDTO.getProductName());
    String productBrand = SolrQueryUtils.escape(searchConditionDTO.getProductBrand());
    String productSpec = SolrQueryUtils.escape(searchConditionDTO.getProductSpec());
    String productModel = SolrQueryUtils.escape(searchConditionDTO.getProductModel());
    String pvBrand = SolrQueryUtils.escape(searchConditionDTO.getProductVehicleBrand());
    String pvModel = SolrQueryUtils.escape(searchConditionDTO.getProductVehicleModel());
    String productKind = SolrQueryUtils.escape(searchConditionDTO.getProductKind());
    String supplierKeyWord = SolrQueryUtils.escape(searchConditionDTO.getSupplierKeyWord());
    String commodityCode = SolrQueryUtils.escape(searchConditionDTO.getCommodityCode());
    if (qString == null) {
      qString = new StringBuilder();
    }
    if (!StringUtils.isBlank(shopName)) {
      if (started) {
        qString.append(" AND ");
      }
      qString.append("(");
      qString.append("shop_name_exact:").append("(\"").append(shopName).append("\")^10 ");
      qString.append(" OR shop_name_exact:").append("(").append(shopName).append("*)^5 ");
      qString.append(" OR shop_name_ngram_continuous").append(":").append("(").append(shopName).append(")");
      qString.append(")");
      started = true;
    }

    started = generatePromotionsQueryString(started, qString, searchConditionDTO);
    if(searchConditionDTO.getProductId()!=null){
      if (started)  qString.append(" AND ");
      qString.append("(product_id:").append(searchConditionDTO.getProductId()).append(")");
    }else if(ArrayUtil.isNotEmpty(searchConditionDTO.getProductIdArr())){
      started = generateStringArrayRelatedQuery("product_id",searchConditionDTO.getProductIdArr(), started, qString);
    }

    if (!StringUtils.isBlank(commodityCode)) {
      if (started) {
        qString.append(" AND ");
      }
      qString.append("(");
      qString.append("commodity_code:").append("(\"").append(commodityCode).append("\")^10 ");
      qString.append(" OR commodity_code:").append("(").append(commodityCode).append("*)^5 ");
      qString.append(" OR commodity_code_ngram_continuous").append(":").append("(").append(commodityCode).append(")");

      qString.append(")");
      started = true;
    }

    if (!StringUtils.isBlank(productName)) {
      if (started) {
        qString.append(" AND ");
      }
      qString.append("(");
      qString.append("product_name_exact:").append("(\"").append(productName).append("\")^10 ");
      qString.append(" OR product_name_exact:").append("(").append(productName).append("*)^5 ");
      qString.append(" OR product_name_ngram_continuous").append(":").append("(").append(productName).append(")");

      qString.append(")");
      started = true;
    }

    if (!StringUtils.isBlank(productBrand)) {
      if (started) {
        qString.append(" AND ");
      }
      qString.append("(");
      qString.append("product_brand_exact:").append("(\"").append(productBrand).append("\")^10 ");
      qString.append(" OR product_brand_exact:").append("(").append(productBrand).append("*)^5 ");
      qString.append(" OR product_brand_ngram_continuous").append(":").append("(").append(productBrand).append(")");
      qString.append(")");
      started = true;
    }

    if (!StringUtils.isBlank(productSpec)) {
      if (started) {
        qString.append(" AND ");
      }
      qString.append("(");
      qString.append("product_spec_exact:").append("(\"").append(productSpec).append("\")^10 ");
      qString.append(" OR product_spec_exact:").append("(").append(productSpec).append("*)^5 ");
      qString.append(" OR product_spec_ngram_continuous").append(":").append("(").append(productSpec).append(")");
      qString.append(")");
      started = true;
    }

    if (!StringUtils.isBlank(productModel)) {
      if (started) {
        qString.append(" AND ");
      }
      qString.append("(");
      qString.append("product_model_exact:").append("(\"").append(productModel).append("\")^10 ");
      qString.append(" OR product_model_exact:").append("(").append(productModel).append("*)^5 ");
      qString.append(" OR product_model_ngram_continuous").append(":").append("(").append(productModel).append(")");
      qString.append(")");
      started = true;
    }

    if (!StringUtils.isBlank(pvBrand)) {
      if (started) {
        qString.append(" AND ");
      }
      qString.append("(");
      qString.append("product_vehicle_brand_exact:").append("(\"").append(pvBrand).append("\")^10 ");
      qString.append(" OR product_vehicle_brand_exact:").append("(").append(pvBrand).append("*)^5 ");
      qString.append(" OR product_vehicle_brand_ngram_continuous").append(":").append("(").append(pvBrand).append(")");
      qString.append(")");
      started = true;
    }

    if (!StringUtils.isBlank(pvModel)) {
      if (started) {
        qString.append(" AND ");
      }
      qString.append("(");
      qString.append("product_vehicle_model_exact:").append("(\"").append(pvModel).append("\")^10 ");
      qString.append(" OR product_vehicle_model_exact:").append("(").append(pvModel).append("*)^5 ");
      qString.append(" OR product_vehicle_model_ngram_continuous").append(":").append("(").append(pvModel).append(")");
      qString.append(")");
      started = true;
    }

    if (!StringUtils.isBlank(productKind)) {
      if (started) {
        qString.append(" AND ");
      }
      qString.append("(");
      qString.append("product_kind_exact:").append("(\"").append(productKind).append("\")^10 ");
      qString.append(" OR product_kind_exact:").append("(").append(productKind).append("*)^5 ");
      qString.append(" OR product_kind_ngram_continuous").append(":").append("(").append(productKind).append(")");
      qString.append(")");
      started = true;
    }

    if (StringUtils.isNotBlank(searchConditionDTO.getSupplierId())) {
      if (started) {
        qString.append(" AND ");
      }
      qString.append("(");
      qString.append("supplier_info").append(":");
      qString.append(searchConditionDTO.getSupplierId());
      qString.append(")");
      started = true;
    } else {
      if (StringUtils.isNotBlank(supplierKeyWord)) {
        if (started) {
          qString.append(" AND ");
        }
        qString.append("(");
        qString.append("supplier_info").append(":").append("(");
        qString.append("*").append(supplierKeyWord).append("*");
        qString.append(")");
        qString.append(")");
        started = true;
      }
    }
    if (searchConditionDTO.getProvinceNo()!=null || searchConditionDTO.getCityNo()!=null || searchConditionDTO.getRegionNo()!=null) {
      if (started) {
        qString.append(" AND ");
      }
      Long shopAreaId = searchConditionDTO.getRegionNo()!=null?searchConditionDTO.getRegionNo():(searchConditionDTO.getCityNo()!=null?searchConditionDTO.getCityNo():searchConditionDTO.getProvinceNo());

      qString.append("(");
      qString.append(" shop_area_ids").append(":").append("(").append(shopAreaId).append(")");
      qString.append(")");
      started = true;
    }

    started = generateRangeQuery("last_in_sales_time", StringUtil.valueOf(searchConditionDTO.getStartLastInSalesTime()),StringUtil.valueOf(searchConditionDTO.getEndLastInSalesTime()), started, qString,true);
    started = generateRangeQuery("trade_price", StringUtil.valueOf(searchConditionDTO.getTradePriceStart()),StringUtil.valueOf(searchConditionDTO.getTradePriceEnd()), started, qString,true);
    started = generateRangeQuery("recommendedprice", StringUtil.valueOf(searchConditionDTO.getRecommendedPriceStart()),StringUtil.valueOf(searchConditionDTO.getRecommendedPriceEnd()), started, qString,true);
    started = generateRangeQuery("in_sales_price", StringUtil.valueOf(searchConditionDTO.getInSalesPriceStart()),StringUtil.valueOf(searchConditionDTO.getInSalesPriceEnd()), started, qString,true);
    started = generateRangeQuery("inventory_average_price", StringUtil.valueOf(searchConditionDTO.getInventoryAveragePriceDown()),StringUtil.valueOf(searchConditionDTO.getInventoryAveragePriceUp()), started, qString,true);
    //是否用库存作为查询条件
    started = generateRangeRightRegionQuery("inventory_amount",StringUtil.valueOf(searchConditionDTO.getInventoryAmountDown()),StringUtil.valueOf(searchConditionDTO.getInventoryAmountUp()), started, qString);
    return qString;
  }

  private boolean generatePromotionsQueryString(boolean started, StringBuilder qString, SearchConditionDTO searchConditionDTO) {
    if (PromotionsUtils.ADD_PROMOTIONS_PRODUCT.equals(searchConditionDTO.getPromotionsFilter())||
      PromotionsUtils.ADD_PROMOTIONS_PRODUCT_CURRENT.equals(searchConditionDTO.getPromotionsFilter())){
      if (CollectionUtil.isNotEmpty(searchConditionDTO.getOverlappingProductIds())) {
        if (started) {
          qString.append(" AND ");
        }
        started = true;
        qString.append(" -product_id:(");
        List<Long> overlappingProductIds = searchConditionDTO.getOverlappingProductIds();
        for (int i = 0; i < overlappingProductIds.size(); i++) {
          Long overlappingProductId = overlappingProductIds.get(i);
          if (overlappingProductId == null) continue;
          if (i == 0) {
            qString.append(overlappingProductId);
          } else {
            qString.append(" OR ").append(overlappingProductId);
          }
        }
        qString.append(")");

      }
    }else if (PromotionsUtils.PRODUCT_IN_PROMOTIONS.equals(searchConditionDTO.getPromotionsFilter())){
      if(searchConditionDTO.getPromotionsId()!=null){
        started=generateStringRelatedQuery("promotions_id",StringUtil.valueOf(searchConditionDTO.getPromotionsId()),started,qString);
      }
    }else if (PromotionsUtils.PROMOTIONS_PRODUCT.equals(searchConditionDTO.getPromotionsFilter())){
      if(ArrayUtil.isNotEmpty(searchConditionDTO.getPromotionsTypeList())){
        started = generateStringArrayRelatedQuery("promotions_type",searchConditionDTO.getPromotionsTypeList(), started, qString);
      }
    }else{
      if(ArrayUtil.isNotEmpty(searchConditionDTO.getPromotionsTypeStatusList())){
        started = generateStringArrayRelatedQuery("promotions_type_status",searchConditionDTO.getPromotionsTypeStatusList(),started,qString);
      }
      if(ArrayUtil.isNotEmpty(searchConditionDTO.getPromotionsTypeList())){
        started = generateStringArrayRelatedQuery("promotions_type",searchConditionDTO.getPromotionsTypeList(), started, qString);
      }
      if (!StringUtils.isBlank(searchConditionDTO.getPromotionsName())) {
        if (started) {
          qString.append(" AND ");
        }
        String pName=SolrQueryUtils.escape(searchConditionDTO.getPromotionsName());
        qString.append("(");
        qString.append("promotions_name_exact:").append("(\"").append(pName).append("\")^10 ");
        qString.append(" OR promotions_name_exact:").append("(").append(pName).append("*)^5 ");
        qString.append(" OR promotions_name_ngram_continuous").append(":").append("(").append(pName).append(")");
        qString.append(")");
        started = true;
      }
    }
    return started;
  }

  //AND (field:A OR field:B OR field:C)
  private boolean generateStringArrayRelatedQuery(String field, String[] values, boolean started, StringBuilder qString) {
    if (ArrayUtils.isEmpty(values)) return started;
    int i = 0;
    boolean flag = false; //排除 values{"",""}
    for (String value : values) {
      if (StringUtils.isBlank(value)) continue;
      flag = true;
      if (i > 0) {
        qString.append(" OR ");
      } else if (i == 0 && started) {
        qString.append(" AND (");
      } else {
        qString.append(" ( ");
      }
      value = SolrQueryUtils.escape(value);
      if (!StringUtils.isBlank(value)) {
        qString.append(field).append(":").append("(").append(value).append(")");
        started = true;
        i++;
      }
    }
    if (flag)
      qString.append(")");
    return started;
  }
  //OR (field:A OR field:B OR field:C)
  private boolean generateProductCategoryStringArrayQuery(String field, String[] values, boolean started, StringBuilder qString) {
    if (ArrayUtils.isEmpty(values)) return started;
    int i = 0;
    boolean flag = false; //排除 values{"",""}
    for (String value : values) {
      if (StringUtils.isBlank(value)) continue;
      flag = true;
      if (i > 0) {
        qString.append(" OR ");
      } else if (i == 0 && started) {
        qString.append(" AND (");
      } else {
        qString.append(" ( ");
      }
      value = SolrQueryUtils.escape(value);
      if (!StringUtils.isBlank(value)) {
        if(value.equals(ShopConstant.BC_ADMIN_SHOP_PRODUCT_CATEGORY_OTHER_QUERY.toString())){
          qString.append(field).append(":").append("[").append(value).append(" TO ").append("*]");
        }else{
          qString.append(field).append(":").append("(").append(value).append(")");
        }
        started = true;
        i++;
      }
    }
    if (flag)
      qString.append(")");
    return started;
  }

//AND (field:A)
  private boolean generateStringRelatedQuery(String field, String value, boolean started, StringBuilder qString) {
    if (StringUtil.isEmpty(value)) return started;
     if(started){
       qString.append(" AND ");
     }
    qString.append("(").append(field).append(":").append(value).append(")");
    return started;
  }

  private QueryResponse queryProductSuggestionWithResponse(SearchConditionDTO searchConditionDTO) throws Exception {
    String field = searchConditionDTO.getSearchField();
    StringBuilder qString = new StringBuilder();
    boolean started = false;
    if (!StringUtils.isBlank(searchConditionDTO.getSearchWord())) {
      if (field == null || field.equals(SearchConditionDTO.PRODUCT_INFO)) {
        generateMultifieldSuggestionQuery(qString, searchConditionDTO.getSearchWord());
      } else if (field != null && field.equals(SearchConditionDTO.COMMODITY_CODE)) {
        generateCodeSuggestionQuery(qString, searchConditionDTO.getSearchWord(), field);
      } else {
        generateSinglefieldSuggestionQuery(qString, searchConditionDTO.getSearchWord(), field);
      }
      started = true;
    }
    if (!StringUtils.isBlank(searchConditionDTO.getCommodityCode())) {
      if (started) qString.append(" AND ");
      qString.append("commodity_code:").append("(\"").append(SolrQueryUtils.escape(searchConditionDTO.getCommodityCode())).append("\") ");
      started = true;
    }
    if (!StringUtils.isBlank(searchConditionDTO.getProductName())) {
      if (started) qString.append(" AND ");
      qString.append("product_name_exact:").append("(\"").append(SolrQueryUtils.escape(searchConditionDTO.getProductName())).append("\") ");
      started = true;
    }
    if (!StringUtils.isBlank(searchConditionDTO.getProductBrand())) {
      if (started) qString.append(" AND ");
      qString.append("product_brand_exact:").append("(\"").append(SolrQueryUtils.escape(searchConditionDTO.getProductBrand())).append("\") ");
      started = true;
    }
    if (!StringUtils.isBlank(searchConditionDTO.getProductModel())) {
      if (started) qString.append(" AND ");
      qString.append("product_model_exact:").append("(\"").append(SolrQueryUtils.escape(searchConditionDTO.getProductModel())).append("\") ");
      started = true;
    }
    if (!StringUtils.isBlank(searchConditionDTO.getProductSpec())) {
      if (started) qString.append(" AND ");
      qString.append("product_spec_exact:").append("(\"").append(SolrQueryUtils.escape(searchConditionDTO.getProductSpec())).append("\") ");
      started = true;
    }

    if (!StringUtils.isBlank(searchConditionDTO.getVehicleBrand())) {
      if (started) qString.append(" AND ");
      qString.append("product_vehicle_brand_exact:").append("(\"").append((searchConditionDTO.getVehicleBrand())).append("\") ");
      started = true;
    }

    if (!StringUtils.isBlank(searchConditionDTO.getVehicleModel())) {
      if (started) qString.append(" AND ");
      qString.append("product_vehicle_model_exact:").append("(\"").append(SolrQueryUtils.escape(searchConditionDTO.getVehicleModel())).append("\") ");
      started = true;
    }

    SolrQuery query = new SolrQuery();
    if (qString.length() == 0) {
      qString.append("*:*");
    }
    query.setQuery(qString.toString());
//    query.setParam("debugQuery", "true");

    StringBuffer fQueryString = new StringBuffer();
    if (searchConditionDTO.getIncludeBasic()) fQueryString.append("(");
    if (searchConditionDTO.getShopId() == null)
      throw new BcgogoException("shopId nullPointException!");
    fQueryString.append("shop_id:").append(searchConditionDTO.getShopId());
    if (searchConditionDTO.getIncludeBasic()) {
      fQueryString.append(" OR shop_id:").append(SolrConstant.BASIC_SHOP_ID).append(")");
    }
    query.setFilterQueries(fQueryString.toString());
    query.addFilterQuery("product_status:ENABLED");
    if (searchConditionDTO.getRows() <= 0)
      throw new BcgogoException("function[queryProductSuggestionWithResponse]rows is illegal!");
    query.setRows(searchConditionDTO.getRows());
    query.setParam("fl", "*,score");
    query.setParam("group", "true");
    if (field == null || field.equals(SearchConditionDTO.PRODUCT_INFO)) {
      query.setParam("group.field", "product_name_exact");
    }else if(field.equals(SearchConditionDTO.COMMODITY_CODE)){
      query.setParam("group.field", field);
    } else {
      query.setParam("group.field", field + "_exact");
    }
    query.setParam("group.main", "true");
    return SolrClientHelper.getProductSolrClient().query(query);
  }

  //if field is not product name,  we only generate suggestion list against this particular field.
  //if field is product name, suggestion may contain other fields, separated by space.
  private List<String> processQueryResultWithSimpleList(QueryResponse rsp, String q, String field) {
    List<String> results = new ArrayList<String>();

    SolrDocumentList documents = rsp.getResults();

    if (field != null && !field.equals("product_name")) {
      for (SolrDocument document : documents) {
        String value = (String) document.getFirstValue(field);
        results.add(value);
      }
      return results;
    }
    for (SolrDocument document : documents) {
      String name = (String) document.getFirstValue("product_name");
      String brand = (String) document.getFirstValue("product_brand");
      String model = (String) document.getFirstValue("product_model");
      String spec = (String) document.getFirstValue("product_spec");
      String vBrand = (String) document.getFirstValue("product_vehicle_brand");
      String vModel = (String) document.getFirstValue("product_vehicle_model");

      String brandFl = (String) document.getFirstValue("product_brand_fl");
      String modelFl = (String) document.getFirstValue("product_model_fl");
      String specFl = (String) document.getFirstValue("product_spec_fl");
      String vBrandFl = (String) document.getFirstValue("product_vehicle_brand_fl");
      String vModelFl = (String) document.getFirstValue("product_vehicle_model_fl");

      String brandPy = (String) document.getFirstValue("product_brand_py");
      String modelPy = (String) document.getFirstValue("product_model_py");
      String specPy = (String) document.getFirstValue("product_spec_py");
      String vBrandPy = (String) document.getFirstValue("product_vehicle_brand_py");
      String vModelPy = (String) document.getFirstValue("product_vehicle_model_py");

      StringBuffer result = new StringBuffer();
      result.append(name);
      if (StringUtil.isOverlap(q, brand)
        || StringUtil.isPrefixOfWord(q, brandFl)
        || StringUtil.isPrefixOfWord(q, brandPy)) {
        result.append(" ").append(brand);
      }
      if (StringUtil.isOverlap(q, model)
        || StringUtil.isPrefixOfWord(q, modelFl)
        || StringUtil.isPrefixOfWord(q, modelPy)) {
        result.append(" ").append(model);
      }
      if (StringUtil.isOverlap(q, spec)
        || StringUtil.isPrefixOfWord(q, specFl)
        || StringUtil.isPrefixOfWord(q, specPy)) {
        result.append(" ").append(spec);
      }
      if (StringUtil.isOverlap(q, vBrand)
        || StringUtil.isPrefixOfWord(q, vBrandFl)
        || StringUtil.isPrefixOfWord(q, vBrandPy)) {
        result.append(" ").append(vBrand);
      }
      if (StringUtil.isOverlap(q, vModel)
        || StringUtil.isPrefixOfWord(q, vModelFl)
        || StringUtil.isPrefixOfWord(q, vModelPy)) {
        result.append(" ").append(vModel);
      }

      results.add(result.toString());
    }
    return results;
  }

  private List<SearchSuggestionDTO> processQueryResult(SolrDocumentList documents, String q, String field) {
    List<SearchSuggestionDTO> results = new ArrayList<SearchSuggestionDTO>();

    //如果除了product_name的其他字段 只拼接该field
    if (field != null && !field.equals(SearchConditionDTO.PRODUCT_INFO)) {
      for (SolrDocument document : documents) {
        String value = (String) document.getFirstValue(field);
        if (StringUtils.isBlank(value)) continue;
        SearchSuggestionDTO result = new SearchSuggestionDTO();
        result.addEntry(field, value);
        results.add(result);
      }
      return results;
    }
    //如果是product_name 则拼接product_name+包含的field
    for (SolrDocument document : documents) {
      String name = (String) document.getFirstValue("product_name");
      String commodityCode = (String) document.getFirstValue("commodity_code");
      String brand = (String) document.getFirstValue("product_brand");
      String model = (String) document.getFirstValue("product_model");
      String spec = (String) document.getFirstValue("product_spec");
      String vBrand = (String) document.getFirstValue("product_vehicle_brand");
      String vModel = (String) document.getFirstValue("product_vehicle_model");

      String brandFl = (String) document.getFirstValue("product_brand_fl");
      String modelFl = (String) document.getFirstValue("product_model_fl");
      String specFl = (String) document.getFirstValue("product_spec_fl");
      String vBrandFl = (String) document.getFirstValue("product_vehicle_brand_fl");
      String vModelFl = (String) document.getFirstValue("product_vehicle_model_fl");

      String brandPy = (String) document.getFirstValue("product_brand_py");
      String modelPy = (String) document.getFirstValue("product_model_py");
      String specPy = (String) document.getFirstValue("product_spec_py");
      String vBrandPy = (String) document.getFirstValue("product_vehicle_brand_py");
      String vModelPy = (String) document.getFirstValue("product_vehicle_model_py");

      SearchSuggestionDTO result = new SearchSuggestionDTO();
      result.addEntry("product_name", name);
      if (StringUtils.isNotBlank(commodityCode)) {
        result.addEntry("commodity_code", commodityCode);
      }
      if (StringUtil.isOverlap(q, brand)
        || StringUtil.isPrefixOfWord(q, brandFl)
        || StringUtil.isPrefixOfWord(q, brandPy)) {
        result.addEntry("product_brand", brand);
      }
      if (StringUtil.isOverlap(q, model)
        || StringUtil.isPrefixOfWord(q, modelFl)
        || StringUtil.isPrefixOfWord(q, modelPy)) {
        result.addEntry("product_model", model);
      }
      if (StringUtil.isOverlap(q, spec)
        || StringUtil.isPrefixOfWord(q, specFl)
        || StringUtil.isPrefixOfWord(q, specPy)) {
        result.addEntry("product_spec", spec);
      }
      if (StringUtil.isOverlap(q, vBrand)
        || StringUtil.isPrefixOfWord(q, vBrandFl)
        || StringUtil.isPrefixOfWord(q, vBrandPy)) {
        result.addEntry("product_vehicle_brand", vBrand);
      }
      if (StringUtil.isOverlap(q, vModel)
        || StringUtil.isPrefixOfWord(q, vModelFl)
        || StringUtil.isPrefixOfWord(q, vModelPy)) {
        result.addEntry("product_vehicle_model", vModel);
      }

      results.add(result);
    }
    return results;
  }

  private void generateMultifieldSuggestionQuery(StringBuilder qString, String q) {
    qString.append("(");
    q = SolrQueryUtils.escape(q);
    qString.append("commodity_code").append(":").append("(").append(q).append("*").append(")^3");
    qString.append(" OR commodity_code_ngram").append(":").append("(").append(q).append(")^0.001");
    qString.append(" OR commodity_code_ngram_continuous").append(":").append("(").append(q).append(")");
    qString.append(" OR ").append("commodity_code").append(":").append("(\"").append(q).append("\")^4");

    qString.append(" OR product_name").append(":").append("(").append(q).append(")^5");
    qString.append(" OR product_name_ngram").append(":").append("(").append(q).append(")^0.001");
    qString.append(" OR product_name_ngram_continuous").append(":").append("(").append(q).append(")");
    qString.append(" OR product_name_exact").append(":").append("(").append(q).append("*").append(")^1000");


    qString.append(" OR product_brand").append(":").append("(").append(q).append(")");
    qString.append(" OR product_brand_ngram").append(":").append("(").append(q).append(")^0.001");
    qString.append(" OR product_brand_ngram_continuous").append(":").append("(").append(q).append(")");
    qString.append(" OR product_brand_exact").append(":").append("(").append(q).append("*").append(")");

    qString.append(" OR product_model").append(":").append("(").append(q).append(")");
    qString.append(" OR product_model_ngram").append(":").append("(").append(q).append(")^0.001");
    qString.append(" OR product_model_ngram_continuous").append(":").append("(").append(q).append(")");
    qString.append(" OR product_model_exact").append(":").append("(").append(q).append("*").append(")");

    qString.append(" OR product_spec").append(":").append("(").append(q).append(")");
    qString.append(" OR product_spec_ngram").append(":").append("(").append(q).append(")^0.001");
    qString.append(" OR product_spec_ngram_continuous").append(":").append("(").append(q).append(")");
    qString.append(" OR product_spec_exact").append(":").append("(").append(q).append("*").append(")");

    qString.append(" OR product_vehicle_brand").append(":").append("(").append(q).append(")");
    qString.append(" OR product_vehicle_brand_ngram").append(":").append("(").append(q).append(")^0.001");
    qString.append(" OR product_vehicle_brand_ngram_continuous").append(":").append("(").append(q).append(")");
    qString.append(" OR product_vehicle_brand_exact").append(":").append("(").append(q).append("*").append(")");

    qString.append(" OR product_vehicle_model").append(":").append("(").append(q).append(")");
    qString.append(" OR product_vehicle_model_ngram").append(":").append("(").append(q).append(")^0.001");
    qString.append(" OR product_vehicle_model_ngram_continuous").append(":").append("(").append(q).append(")");
    qString.append(" OR product_vehicle_model_exact").append(":").append("(").append(q).append("*").append(")");

    qString.append(" OR product_name_fl").append(":").append("(").append(q).append("*").append(")");
    qString.append(" OR product_brand_fl").append(":").append("(").append(q).append("*").append(")");
    qString.append(" OR product_model_fl").append(":").append("(").append(q + "*").append(")");
    qString.append(" OR product_spec_fl").append(":").append("(").append(q + "*").append(")");
    qString.append(" OR product_vehicle_brand_fl").append(":").append("(").append(q).append("*").append(")");
    qString.append(" OR product_vehicle_model_fl").append(":").append("(").append(q).append("*").append(")");

    qString.append(" OR product_name_py").append(":").append("(").append(q).append("*").append(")");
    qString.append(" OR product_brand_py").append(":").append("(").append(q).append("*").append(")");
    qString.append(" OR product_model_py").append(":").append("(").append(q).append("*").append(")");
    qString.append(" OR product_spec_py").append(":").append("(").append(q).append("*").append(")");
    qString.append(" OR product_vehicle_brand_py").append(":").append("(").append(q).append("*").append(")");
    qString.append(" OR product_vehicle_model_py").append(":").append("(").append(q).append("*").append(")");

    qString.append(")");
    return;
  }

  /**
   * 编号  类型的  前后 匹配  不做分词 拆字 拼音
   *
   * @param qString
   * @param q
   * @param field
   */
  private void generateCodeSuggestionQuery(StringBuilder qString, String q, String field) {
    q = SolrQueryUtils.escape(q);
    qString.append("(");
    qString.append(field).append(":").append("(\"").append(q).append("\")^200");
    qString.append(" OR ").append(field).append("(").append(q + "*").append(")^100");
    qString.append(" OR ").append(field).append("_ngram_continuous:").append("(").append(q).append(")");
    qString.append(" OR ").append(field).append("_ngram:").append("(").append(q).append(")^0.001");
    qString.append(")");
  }

  private void generateSinglefieldSuggestionQuery(StringBuilder qString, String q, String field) {
    q = SolrQueryUtils.escape(q);
    qString.append("(");
    qString.append(field).append(":").append("(").append(q).append(")");
    qString.append(" OR ").append(field).append("_ngram:").append("(").append(q).append(")^0.001");
    qString.append(" OR ").append(field).append("_ngram_continuous:").append("(").append(q).append(")");
    qString.append(" OR ").append(field).append("_exact:").append("(").append(q + "*").append(")^100");
    qString.append(" OR ").append(field).append("_fl:").append("(").append(q + "*").append(")");
    qString.append(" OR ").append(field).append("_py:").append("(").append(q + "*").append(")");
    qString.append(")");
    return;
  }

  private ProductSearchResultListDTO getProductDTOByQueryResponses(SearchConditionDTO searchConditionDTO,QueryResponse... responses) throws Exception {
    List<ProductDTO> results = new ArrayList<ProductDTO>();
    ProductSearchResultListDTO productSearchResultListDTO = new ProductSearchResultListDTO();
    for (QueryResponse rsp : responses) {
      if (rsp == null) continue;
      if(rsp.getFieldStatsInfo()!=null){
        if(searchConditionDTO.getStorehouseId()!=null){
          FieldStatsInfo statsInfo = rsp.getFieldStatsInfo().get(searchConditionDTO.getStorehouseId()+"_storehouse_inventory_price");
          if (statsInfo != null) {
            productSearchResultListDTO.setTotalPurchasePrice((Double)statsInfo.getSum());
          }
          statsInfo = rsp.getFieldStatsInfo().get(searchConditionDTO.getStorehouseId()+"_storehouse_inventory_amount");
          if (statsInfo != null) {
            productSearchResultListDTO.setInventoryAmount((Double)statsInfo.getSum());
          }
        }else{
          FieldStatsInfo statsInfo = rsp.getFieldStatsInfo().get("inventory_price");
          if (statsInfo != null) {
            productSearchResultListDTO.setTotalPurchasePrice((Double)statsInfo.getSum());
          }
          statsInfo = rsp.getFieldStatsInfo().get("inventory_amount");
          if (statsInfo != null) {
            productSearchResultListDTO.setInventoryAmount((Double)statsInfo.getSum());
          }
        }
      }

      SolrDocumentList documents = rsp.getResults();
      productSearchResultListDTO.setInventoryCount(documents.getNumFound());
      productSearchResultListDTO.setNumFound(documents.getNumFound());
      results.addAll(getProductDTOBySolrDocuments(documents));
    }
    productSearchResultListDTO.setProducts(results);

    Pager pager = new Pager(((Long)productSearchResultListDTO.getNumFound()).intValue(),searchConditionDTO.getStartPageNo(),searchConditionDTO.getMaxRows());
    productSearchResultListDTO.setPager(pager);
    return productSearchResultListDTO;
  }

  private List<ProductDTO> getProductDTOBySolrDocuments(SolrDocumentList documents) {
    List<ProductDTO> result = new ArrayList<ProductDTO>();
    for (SolrDocument document : documents) {
      Long id = Long.valueOf(document.getFirstValue("id").toString());
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
      String inSalesUnit = (String) document.getFirstValue("in_sales_unit");
      Long pShopId = Long.valueOf(document.getFieldValue("shop_id").toString());
      String pShopName = (String)document.getFieldValue("shop_name");
      String pShopAreaInfo = (String)document.getFieldValue("shop_area_info");
      Object rate = document.getFieldValue("product_rate");
      String storageBin = (String) document.getFirstValue("storage_bin");
      Double tradePrice = (Double) document.getFirstValue("trade_price");
      String commodityCode = (String) document.getFieldValue("commodity_code");
      String productStatusStr = (String) document.getFirstValue("product_status");
      String product_kind = (String) document.getFirstValue("product_kind");
      Object normal_product_id = document.getFirstValue("normal_product_id");
      String relevance_status =  (String)document.getFirstValue("relevance_status");
      List<Object> supplier_details = (ArrayList) document.getFieldValues("supplier_detail");
      List<Object> storehouse_ids = (ArrayList) document.getFieldValues("storehouse_id");
      Object inventoryAveragePrice = document.getFirstValue("inventory_average_price");
      Double in_sales_amount = (Double) document.getFirstValue("in_sales_amount");
      if(NumberUtil.subtraction(in_sales_amount,ProductConstants.IN_SALES_AMOUNT_AVAILABLE)==0){
        in_sales_amount=-1d;
      }
      Double in_sales_price = (Double) document.getFirstValue("in_sales_price");
      String guarantee_period = StringUtil.valueOf(document.getFirstValue("guarantee_period"));
      String sales_status = (String) document.getFirstValue("sales_status");
      Long last_in_sales_time = (Long) document.getFirstValue("last_in_sales_time");
      List<Object> promotions_infos = (ArrayList) document.getFieldValues("promotions_info");
      List<Object> promotions_types = (ArrayList) document.getFieldValues("promotions_type");
      List<Object> promotions_ids = (ArrayList) document.getFieldValues("promotions_id");
      Double lower_limit = (Double) document.getFirstValue("lower_limit");
      Double upper_limit = (Double) document.getFirstValue("upper_limit");
      Long lastStorageTime = (Long) document.getFirstValue("storage_time");

      ProductDTO productDTO = new ProductDTO();
      productDTO.setId(id);
      productDTO.setShopId(pShopId);
      productDTO.setShopName(pShopName);
      productDTO.setShopAreaInfo(pShopAreaInfo);
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
      productDTO.setStorageBin(storageBin);
      productDTO.setInSalesUnit(inSalesUnit);
      productDTO.setCommodityCode(commodityCode);
      productDTO.setInventoryAveragePrice(inventoryAveragePrice == null ? null : Double.parseDouble(inventoryAveragePrice.toString()));
      productDTO.setKindName(product_kind);
      if(StringUtils.isNotBlank(relevance_status)){
        productDTO.setRelevanceStatus(ProductRelevanceStatus.valueOf(relevance_status));
      }
      productDTO.setNormalProductId(normal_product_id==null?null:Long.valueOf(normal_product_id.toString()));
      productDTO.generateProductInfo();
      productDTO.setTradePrice(NumberUtil.numberValue(tradePrice,0d));
      if (StringUtils.isNotBlank(productStatusStr)) {
        productDTO.setStatus(ProductStatus.valueOf(productStatusStr));
      }

      if (CollectionUtils.isNotEmpty(supplier_details)) {
        List<SupplierInventoryDTO> supplierInventoryDTOs = new ArrayList<SupplierInventoryDTO>();
        for (Object supplierDetail : supplier_details) {
          supplierInventoryDTOs.add(new SupplierInventoryDTO(supplierDetail.toString()));
        }
        productDTO.setSupplierInventoryDTOs(supplierInventoryDTOs);
      }
      if(productDTO.getProductLocalInfoId()!=null && CollectionUtils.isNotEmpty(storehouse_ids)){
        Map<Long, StoreHouseInventoryDTO> storeHouseInventoryDTOMap = new TreeMap<Long, StoreHouseInventoryDTO>();
        for (Object storehouse_id : storehouse_ids) {
          Object sia = document.getFirstValue(storehouse_id+"_storehouse_inventory_amount");
          storeHouseInventoryDTOMap.put(Long.parseLong(storehouse_id.toString()),new StoreHouseInventoryDTO(Long.parseLong(storehouse_id.toString()),productDTO.getProductLocalInfoId(),sia==null?0d:Double.parseDouble(sia.toString())));
        }
        productDTO.setStoreHouseInventoryDTOMap(storeHouseInventoryDTOMap);
      }
      productDTO.setInSalesAmount(NumberUtil.numberValue(in_sales_amount,0d));
      productDTO.setInSalesPrice(NumberUtil.round(in_sales_price));
      productDTO.setGuaranteePeriod(guarantee_period);
      if(StringUtils.isNotBlank(sales_status)){
        productDTO.setSalesStatus(ProductStatus.valueOf(sales_status));
      }
      productDTO.setLastInSalesTime(NumberUtil.longValue(last_in_sales_time));
      if(CollectionUtil.isNotEmpty(promotions_infos)){
        productDTO.setPromotionsDTOs(JsonUtil.jsonArrayToList(promotions_infos.toString(), PromotionsDTO.class, null));
      }
      if(CollectionUtil.isNotEmpty(promotions_types)){
        productDTO.setPromotionsTypeList(JsonUtil.jsonArrayToList(promotions_types.toString(), String.class, null));
      }

      productDTO.setLowerLimit(lower_limit);
      productDTO.setUpperLimit(upper_limit);
      if(NumberUtil.longValue(lastStorageTime) > 0){
        productDTO.setLastPurchaseDateStr(DateUtil.convertDateLongToDateString(DateUtil.YEAR_MONTH_DATE,lastStorageTime));
      }
      result.add(productDTO);
    }
    return result;
  }



  @Override
  public ProductSearchSuggestionListDTO queryProductSuggestionWithDetails(SearchConditionDTO searchConditionDTO) throws Exception {
    String field = searchConditionDTO.getSearchField();
    StringBuilder qString = new StringBuilder();
    boolean started = false;
    if (!StringUtils.isBlank(searchConditionDTO.getSearchWord())) {
      if (field == null || field.equals(SearchConditionDTO.PRODUCT_INFO)) {
        generateMultifieldSuggestionQuery(qString, searchConditionDTO.getSearchWord());
      } else if (field != null && field.equals(SearchConditionDTO.COMMODITY_CODE)) {
        generateCodeSuggestionQuery(qString, searchConditionDTO.getSearchWord(), field);
      } else {
        generateSinglefieldSuggestionQuery(qString, searchConditionDTO.getSearchWord(), field);
      }
      started = true;
    }

    //查询促销
    started = generatePromotionsQueryString(started, qString, searchConditionDTO);
    if (!ArrayUtils.isEmpty(searchConditionDTO.getExcludeShopIds())) {
      if (started) {
        qString.append(" AND ");
      }
      qString.append("!shop_id:(");
      for (int i = 0, max = searchConditionDTO.getExcludeShopIds().length; i < max; i++) {
        qString.append(searchConditionDTO.getExcludeShopIds()[i]);
        if (i < (max - 1)) qString.append(" OR ");
      }
      qString.append(")");
      started = true;
    }
    if (!StringUtils.isBlank(searchConditionDTO.getCommodityCode())) {
      if (started) qString.append(" AND ");
      qString.append("commodity_code:").append("(\"").append(SolrQueryUtils.escape(searchConditionDTO.getCommodityCode())).append("\") ");
      started = true;
    }
    if (!StringUtils.isBlank(searchConditionDTO.getProductName())) {
      if (started) qString.append(" AND ");
      qString.append("product_name_exact:").append("(\"").append(SolrQueryUtils.escape(searchConditionDTO.getProductName())).append("\") ");
      started = true;
    }
    if (!StringUtils.isBlank(searchConditionDTO.getProductBrand())) {
      if (started) qString.append(" AND ");
      qString.append("product_brand_exact:").append("(\"").append(SolrQueryUtils.escape(searchConditionDTO.getProductBrand())).append("\") ");
      started = true;
    }
    if (!StringUtils.isBlank(searchConditionDTO.getProductModel())) {
      if (started) qString.append(" AND ");
      qString.append("product_model_exact:").append("(\"").append(SolrQueryUtils.escape(searchConditionDTO.getProductModel())).append("\") ");
      started = true;
    }
    if (!StringUtils.isBlank(searchConditionDTO.getProductSpec())) {
      if (started) qString.append(" AND ");
      qString.append("product_spec_exact:").append("(\"").append(SolrQueryUtils.escape(searchConditionDTO.getProductSpec())).append("\") ");
      started = true;
    }

    if (!StringUtils.isBlank(searchConditionDTO.getVehicleBrand())) {
      if (started) qString.append(" AND ");
      qString.append("product_vehicle_brand_exact:").append("(\"").append((searchConditionDTO.getVehicleBrand())).append("\") ");
      started = true;
    }

    if (!StringUtils.isBlank(searchConditionDTO.getVehicleModel())) {
      if (started) qString.append(" AND ");
      qString.append("product_vehicle_model_exact:").append("(\"").append(SolrQueryUtils.escape(searchConditionDTO.getVehicleModel())).append("\") ");
      started = true;
    }

    if (!StringUtils.isBlank(searchConditionDTO.getProductKind())) {
      if (started) qString.append(" AND ");
      qString.append("product_kind_exact:").append("(\"").append(SolrQueryUtils.escape(searchConditionDTO.getProductKind())).append("\") ");
      started = true;
    }

    SolrQuery query = new SolrQuery();
    if (qString.length() == 0) {
      qString.append("*:*");
    }
    query.setQuery(qString.toString());
    generateFilterQuery(searchConditionDTO, query);

    if (searchConditionDTO.getRows() <= 0) {
      LOG.error("queryProductSuggestionWithDetails rows is illegal!");
      searchConditionDTO.setRows(15);
      LOG.info("queryProductSuggestionWithDetails init rows 15.");
    }
    query.setParam("fl", "*,score");
    if (StringUtils.isBlank(searchConditionDTO.getSearchWord()) && StringUtils.isBlank(searchConditionDTO.getSort())) {
      query.setParam("sort", "inventory_amount desc,lastmodified desc");
    } else {
      if (StringUtils.isNotBlank(searchConditionDTO.getSort())) {
        query.setParam("sort", searchConditionDTO.getSort());
      }
    }

    return collapsingResult(query, searchConditionDTO);

  }

  private ProductSearchSuggestionListDTO collapsingResult(SolrQuery query, SearchConditionDTO searchConditionDTO) throws Exception {
    IProductCurrentUsedService productCurrentUsedService = ServiceManager.getService(IProductCurrentUsedService.class);
    ProductSearchSuggestionListDTO result = new ProductSearchSuggestionListDTO();
    List<SearchSuggestionDTO> suggestionResults = new ArrayList<SearchSuggestionDTO>();
    result.setSuggestionDTOs(suggestionResults);
    //录单页面的小下拉和详细下拉
    if(ArrayUtils.isEmpty(searchConditionDTO.getSearchStrategy())
      || (ArrayUtils.contains(searchConditionDTO.getSearchStrategy(),SearchConditionDTO.SEARCHSTRATEGY_DETAIL)
      && ArrayUtils.contains(searchConditionDTO.getSearchStrategy(),SearchConditionDTO.SEARCHSTRATEGY_SUGGESTION))){
      int rows = searchConditionDTO.getRows();
      int stepSize = rows * 5;
      query.setRows(stepSize);
      QueryResponse rsp = SolrClientHelper.getProductSolrClient().query(query);

      List<ProductDTO> productDetailList = getProductDTOBySolrDocuments(rsp.getResults());
      //分页只取15条
      productDetailList = productDetailList.subList(0, productDetailList.size() > searchConditionDTO.getRows() ? searchConditionDTO.getRows() : productDetailList.size());
      result.setProductDetailResultDTOs(productDetailList);
      result.setProductDetailTotalCount(rsp.getResults().getNumFound());

      if (searchConditionDTO.gotoMemCacheFunction()) {
        //如果是商品（品名和品牌）SearchWord为空
        suggestionResults.addAll(productCurrentUsedService.getProductSuggestionFromMemory(new SearchMemoryConditionDTO(searchConditionDTO)));
      } else {
        int start = 0;
        Map<String, SolrDocument> res = new HashMap<String, SolrDocument>();
        SolrDocumentList documents = rsp.getResults();
        int queryTime = 1;
        while (documents.size() > 0 && suggestionResults.size() < searchConditionDTO.getRows()) {
          for (SolrDocument document : documents) {
            SearchSuggestionDTO r = getResult(document, searchConditionDTO.getSearchWord(), searchConditionDTO.getSearchField());
            r.setUuid(searchConditionDTO.getUuid());
            List<String[]> entries = r.suggestionEntry;
            StringBuffer value = new StringBuffer();
            for (int i = 0; i < entries.size(); i++) {
              if (StringUtils.isNotBlank(entries.get(i)[1]))
                value.append(entries.get(i)[1]).append(" ");
            }
            if (res.containsKey(value.toString())) continue;
            res.put(value.toString(), document);
            if (value.length() > 0) suggestionResults.add(r);
            if (suggestionResults.size() == searchConditionDTO.getRows()) return result;
          }
          if (suggestionResults.size() >= searchConditionDTO.getRows() || documents.size() < stepSize) break;
          start += stepSize;
          query.setStart(start);
          rsp = SolrClientHelper.getProductSolrClient().query(query);
          documents = rsp.getResults();
          queryTime++;
          if (queryTime > MAX_QUERY_TIMES) {
            break;
          }
        }
      }
//录单页面的详细下拉 分页时用
    }else if (ArrayUtils.contains(searchConditionDTO.getSearchStrategy(),SearchConditionDTO.SEARCHSTRATEGY_DETAIL)){
      query.setRows(searchConditionDTO.getRows());
      query.setStart(searchConditionDTO.getStart());
      QueryResponse rsp = SolrClientHelper.getProductSolrClient().query(query);

      List<ProductDTO> productDetailList = getProductDTOBySolrDocuments(rsp.getResults());
      result.setProductDetailResultDTOs(productDetailList);
      result.setProductDetailTotalCount(rsp.getResults().getNumFound());

      //单个小下拉建议
    }else if (ArrayUtils.contains(searchConditionDTO.getSearchStrategy(),SearchConditionDTO.SEARCHSTRATEGY_SUGGESTION)){
      if (searchConditionDTO.gotoMemCacheFunction()&&StringUtils.isBlank(searchConditionDTO.getProductKind())) {
        //如果是商品（品名和品牌）SearchWord为空
        suggestionResults.addAll(productCurrentUsedService.getProductSuggestionFromMemory(new SearchMemoryConditionDTO(searchConditionDTO)));
      } else {
        int rows = searchConditionDTO.getRows();
        int stepSize = rows * 5;
        query.setRows(stepSize);
        QueryResponse rsp = SolrClientHelper.getProductSolrClient().query(query);

        int start = 0;
        Map<String, SolrDocument> res = new HashMap<String, SolrDocument>();
        SolrDocumentList documents = rsp.getResults();
        int queryTime = 1;
        while (documents.size() > 0 && suggestionResults.size() < searchConditionDTO.getRows()) {
          for (SolrDocument document : documents) {
            SearchSuggestionDTO r = getResult(document, searchConditionDTO.getSearchWord(), searchConditionDTO.getSearchField());
            r.setUuid(searchConditionDTO.getUuid());
            List<String[]> entries = r.suggestionEntry;
            StringBuffer value = new StringBuffer();
            for (int i = 0; i < entries.size(); i++) {
              if (StringUtils.isNotBlank(entries.get(i)[1]))
                value.append(entries.get(i)[1]).append(" ");
            }
            if (res.containsKey(value.toString())) continue;
            res.put(value.toString(), document);
            if (value.length() > 0) suggestionResults.add(r);
            if (suggestionResults.size() == searchConditionDTO.getRows()) return result;
          }
          if (suggestionResults.size() >= searchConditionDTO.getRows() || documents.size() < stepSize) break;
          start += stepSize;
          query.setStart(start);
          rsp = SolrClientHelper.getProductSolrClient().query(query);
          documents = rsp.getResults();
          queryTime++;
          if (queryTime > MAX_QUERY_TIMES) {
            break;
          }
        }
      }
    }
    return result;
  }



  private SearchSuggestionDTO getResult(SolrDocument document, String q, String field) {
    SearchSuggestionDTO result = new SearchSuggestionDTO();
    //如果除了product_name的其他字段 只拼接该field
    if (field != null && !field.equals(SearchConditionDTO.PRODUCT_INFO)) {
      String value = (String) document.getFirstValue(field);
      result.addEntry(field, value);
      return result;
    }
    String name = (String) document.getFirstValue("product_name");
    String brand = (String) document.getFirstValue("product_brand");
    String model = (String) document.getFirstValue("product_model");
    String spec = (String) document.getFirstValue("product_spec");

    String vBrand = (String) document.getFirstValue("product_vehicle_brand");
    String vModel = (String) document.getFirstValue("product_vehicle_model");

    String brandFl = (String) document.getFirstValue("product_brand_fl");
    String modelFl = (String) document.getFirstValue("product_model_fl");
    String specFl = (String) document.getFirstValue("product_spec_fl");
    String vBrandFl = (String) document.getFirstValue("product_vehicle_brand_fl");
    String vModelFl = (String) document.getFirstValue("product_vehicle_model_fl");

    String brandPy = (String) document.getFirstValue("product_brand_py");
    String modelPy = (String) document.getFirstValue("product_model_py");
    String specPy = (String) document.getFirstValue("product_spec_py");
    String vBrandPy = (String) document.getFirstValue("product_vehicle_brand_py");
    String vModelPy = (String) document.getFirstValue("product_vehicle_model_py");

    String commodityCode = (String) document.getFirstValue("commodity_code");
    result.addEntry("product_name", name);
    if (StringUtil.isOverlap(q, commodityCode)) {
      result.addEntry("commodity_code", commodityCode);
    }
    if (StringUtil.isOverlap(q, brand)
      || StringUtil.isPrefixOfWord(q, brandFl)
      || StringUtil.isPrefixOfWord(q, brandPy)) {
      result.addEntry("product_brand", brand);
    }
    if (StringUtil.isOverlap(q, model)
      || StringUtil.isPrefixOfWord(q, modelFl)
      || StringUtil.isPrefixOfWord(q, modelPy)) {
      result.addEntry("product_model", model);
    }
    if (StringUtil.isOverlap(q, spec)
      || StringUtil.isPrefixOfWord(q, specFl)
      || StringUtil.isPrefixOfWord(q, specPy)) {
      result.addEntry("product_spec", spec);
    }
    if (StringUtil.isOverlap(q, vBrand)
      || StringUtil.isPrefixOfWord(q, vBrandFl)
      || StringUtil.isPrefixOfWord(q, vBrandPy)) {
      result.addEntry("product_vehicle_brand", vBrand);
    }
    if (StringUtil.isOverlap(q, vModel)
      || StringUtil.isPrefixOfWord(q, vModelFl)
      || StringUtil.isPrefixOfWord(q, vModelPy)) {
      result.addEntry("product_vehicle_model", vModel);
    }

    return result;
  }

  @Override
  public ProductDTO getProductDTOFromSolrById(Long shopId, Long productId) throws Exception {
    //2.设置solr 查询条件sorQuery
    SolrQuery query = new SolrQuery();
    StringBuffer sb = new StringBuffer();
    sb.append("id:").append(productId).append(" AND shop_id:").append(shopId);
    query.setQuery(sb.toString());
    query.setStart(0);
    query.setRows(10);

    //3.执行query  构造productDTO
    QueryResponse rsp = SolrClientHelper.getProductSolrClient().query(query);
    if (rsp == null) {
      return null;
    }
    SolrDocumentList documents = rsp.getResults();
    List<ProductDTO> productDTOs = new ArrayList<ProductDTO>();
    for (SolrDocument doc : documents) {
      ProductDTO productDTO = new ProductDTO();
      productDTO.setId(Long.parseLong((String) doc.getFirstValue("id")));
      productDTO.setShopId(shopId);
      productDTO.setName((String) doc.getFirstValue("product_name"));
      productDTO.setModel((String) doc.getFirstValue("product_model"));
      productDTO.setBrand((String) doc.getFirstValue("product_brand"));
      productDTO.setSpec((String) doc.getFirstValue("product_spec"));
      productDTO.setProductVehicleBrand((String) doc.getFirstValue("product_vehicle_brand"));
      productDTO.setProductVehicleModel((String) doc.getFirstValue("product_vehicle_model"));
      productDTO.setProductVehicleYear((String) doc.getFirstValue("product_vehicle_year"));
      productDTO.setProductVehicleEngine((String) doc.getFirstValue("product_vehicle_engine"));
      productDTO.setProductVehicleStatus((Integer) doc.getFieldValue("product_vehicle_status"));
      if (doc.getFirstValue("inventory_amount") != null) {
        productDTO.setInventoryNum(Double.parseDouble(doc.getFirstValue("inventory_amount").toString()));
      }
      if (doc.getFirstValue("purchase_price") != null) {
        productDTO.setPurchasePrice(Double.parseDouble(doc.getFirstValue("purchase_price").toString()));
      }
      if (doc.getFirstValue("recommendedprice") != null) {
        productDTO.setRecommendedPrice(Double.parseDouble(doc.getFirstValue("recommendedprice").toString()));
      }
      productDTO.setProductLocalInfoId(NumberUtil.longValue(doc.getFirstValue("product_id")));
      productDTO.setRate(NumberUtil.longValue(doc.getFieldValue("product_rate")));
      productDTO.setStorageUnit((String) doc.getFirstValue("product_storage_unit"));
      productDTO.setSellUnit((String) doc.getFirstValue("product_sell_unit"));
      productDTO.setCommodityCode((String) doc.getFirstValue("commodity_code"));
      String productStatusStr = (String) doc.getFirstValue("product_status");
      if (StringUtils.isNotBlank(productStatusStr)) {
        productDTO.setStatus(ProductStatus.valueOf(productStatusStr));
      }
      productDTOs.add(productDTO);
    }
    if (productDTOs != null && productDTOs.size() > 0) {
      return productDTOs.get(0);
    }
    return null;
  }

  @Override
  public List<ProductDTO> queryProductByLocalInfoIds(Long shopId, Long... productLocalInfoId) throws Exception {
    if (productLocalInfoId == null || productLocalInfoId.length < 1) {
      return new ArrayList<ProductDTO>();
    }
    StringBuffer sb = new StringBuffer();
    sb.append("product_id:(");
    for (int i = 0, len = productLocalInfoId.length; i < len; i++) {
      if (productLocalInfoId[i] == null) {
        continue;
      }
      sb.append(productLocalInfoId[i].toString()).append(" ");
    }
    sb.deleteCharAt(sb.length() - 1);
    sb.append(")");
    if (shopId != null) {
      sb.append(" AND shop_id:").append(shopId.toString());
    }
    QueryResponse rsp = SolrClientHelper.getProductSolrClient().query(new SolrQuery(sb.toString()).setParam("q.op", "OR"));
    return getProductDTOBySolrDocuments(rsp.getResults());
  }

  @Override
  public ProductSearchResultGroupListDTO queryCustomerShopInventory(SearchConditionDTO searchConditionDTO) throws Exception {
    ProductSearchResultGroupListDTO productSearchResultGroupListDTO = new ProductSearchResultGroupListDTO();
    StringBuilder qString = new StringBuilder();
    boolean started = false;
    if (!StringUtils.isBlank(searchConditionDTO.getSearchWord())) {
      generateMultifieldSuggestionQuery(qString, searchConditionDTO.getSearchWord());
      started = true;
    }
    if (!ArrayUtils.isEmpty(searchConditionDTO.getRelatedCustomerShopIds())) {
      if (started) {
        qString.append(" AND ");
      }
      qString.append("shop_id:(");
      for (int i = 0, max = searchConditionDTO.getRelatedCustomerShopIds().length; i < max; i++) {
        qString.append(searchConditionDTO.getRelatedCustomerShopIds()[i]);
        if (i < (max - 1)) qString.append(" OR ");
      }
      qString.append(")");
      started = true;
    }

    qString = getQueryOfProductWithStd(started, qString, searchConditionDTO);
    if (qString.length() == 0) {
      qString.append("*:*");
    }
    SolrQuery query = new SolrQuery();
    query.setQuery(qString.toString());
//    query.setParam("debugQuery", "true");
    if (searchConditionDTO.getWholesalerShopId() == null) throw new BcgogoException("getWholesalerShopId nullPointException!");
    query.setFilterQueries("wholesaler_shop_id:" + searchConditionDTO.getWholesalerShopId());
    query.addFilterQuery("product_status:ENABLED");
    query.setParam("fl", "*,score");
    if (searchConditionDTO.getRows() <= 0) {
      searchConditionDTO.setRows(SolrQueryUtils.getSelectOptionNumber());
    }
    query.setStart(searchConditionDTO.getStart());
    query.setRows(searchConditionDTO.getRows());
    query.setParam("group", "true");
    query.setParam("group.ngroups", "true");
    query.setParam("group.limit", "10");
    query.setParam("group.field", "shop_id");
    if (StringUtils.isNotBlank(searchConditionDTO.getSort())) {
      query.setParam("group.sort",searchConditionDTO.getSort());
    }

    ProductSearchResultListDTO productSearchResultListDTO;
    QueryResponse rsp = SolrClientHelper.getProductSolrClient().query(query);
    GroupResponse groupResponse = rsp.getGroupResponse();
    List<GroupCommand> groupCommandList = groupResponse.getValues();
    if (CollectionUtils.isNotEmpty(groupCommandList)) {
      GroupCommand groupCommand = groupCommandList.get(0);
      for (Group group : groupCommand.getValues()) {
        productSearchResultListDTO = new ProductSearchResultListDTO();
        productSearchResultListDTO.setRelatedCustomerShopId(Long.valueOf(group.getGroupValue()));
        productSearchResultListDTO.setProducts(getProductDTOBySolrDocuments(group.getResult()));
        productSearchResultListDTO.setNumFound(group.getResult().getNumFound());
        productSearchResultGroupListDTO.getProductSearchResultList().add(productSearchResultListDTO);
      }
      productSearchResultGroupListDTO.setNumberGroups(groupCommand.getNGroups());
      productSearchResultGroupListDTO.setTotalNumberFound(groupCommand.getMatches());
    }
    return productSearchResultGroupListDTO;
  }

  @Override
  public ProductSearchResultListDTO queryCustomerInventoryByCustomerShopId(SearchConditionDTO searchConditionDTO) throws Exception {
    ProductSearchResultListDTO productSearchResultListDTO = new ProductSearchResultListDTO();
    if (searchConditionDTO == null) return productSearchResultListDTO;
    StringBuilder qString = new StringBuilder();
    boolean started = false;
    if (!StringUtils.isBlank(searchConditionDTO.getSearchWord())) {
      generateMultifieldSuggestionQuery(qString, searchConditionDTO.getSearchWord());
      started = true;
    }

    qString = getQueryOfProductWithStd(started, qString, searchConditionDTO);
    if (qString.length() == 0) {
      qString.append("*:*");
    }
    SolrQuery query = new SolrQuery();
    query.setQuery(qString.toString());
//    query.setParam("debugQuery", "true");
    if (searchConditionDTO.getWholesalerShopId() == null) throw new BcgogoException("getWholesalerId nullPointException!");
    query.setFilterQueries("wholesaler_shop_id:" + searchConditionDTO.getWholesalerShopId());
    if (ArrayUtils.isEmpty(searchConditionDTO.getRelatedCustomerShopIds()) || searchConditionDTO.getRelatedCustomerShopIds().length != 1) {
      throw new Exception("relatedCustomerIds should be unique in the method of queryCustomerInventoryByCustomerShopId!");
    }
    query.addFilterQuery("shop_id:" + searchConditionDTO.getRelatedCustomerShopIds()[0]);
    query.addFilterQuery("product_status:ENABLED");
    query.setParam("fl", "*,score");
    if (searchConditionDTO.getRows() <= 0) {
      searchConditionDTO.setRows(SolrQueryUtils.getSelectOptionNumber());
    }
    query.setStart(searchConditionDTO.getStart());
    query.setRows(searchConditionDTO.getRows());
    if (StringUtils.isNotBlank(searchConditionDTO.getSort())) {
      query.setParam("sort", searchConditionDTO.getSort());
      query.addSortField("product_id", SolrQuery.ORDER.asc);//解决   有多条数据的排序条件一样时候  solr 会按照solr文件位置（每次更新都会有变化）取数据，像数据库排序一样  遇到排序条件一样的按照数据库（不变所以感觉不到）存在顺序
    }
    QueryResponse rsp = SolrClientHelper.getProductSolrClient().query(query);
    SolrDocumentList documents = rsp.getResults();
    productSearchResultListDTO.setRelatedCustomerShopId(NumberUtil.longValue(searchConditionDTO.getRelatedCustomerShopIds()[0]));
    productSearchResultListDTO.setNumFound(documents.getNumFound());
    productSearchResultListDTO.setProducts(getProductDTOBySolrDocuments(documents));
    return productSearchResultListDTO;
  }

  private void generateFilterQuery(SearchConditionDTO searchConditionDTO, SolrQuery query) throws BcgogoException {
    if (searchConditionDTO.getSearchStrategy()!=null &&
      Arrays.asList(searchConditionDTO.getSearchStrategy()).contains(SearchConditionDTO.SEARCHSTRATEGY_NORMAL_PRODUCT)) {
      if (!ArrayUtils.isEmpty(searchConditionDTO.getRelevanceStatuses())) {
        StringBuilder fQueryString = new StringBuilder();
        fQueryString.append("relevance_status:(");
        for (int i = 0, max = searchConditionDTO.getRelevanceStatuses().length; i < max; i++) {
          fQueryString.append(searchConditionDTO.getRelevanceStatuses()[i]);
          if (i < (max - 1)) fQueryString.append(" OR ");
        }
        fQueryString.append(")");
        query.addFilterQuery(fQueryString.toString());
      }
    } else {
      if(!(!ArrayUtils.isEmpty(searchConditionDTO.getSearchStrategy()) && Arrays.asList(searchConditionDTO.getSearchStrategy()).contains(SearchConditionDTO.SEARCHSTRATEGY_NO_SHOP_RESTRICT))){
        if (searchConditionDTO.getShopId() == null && ArrayUtils.isEmpty(searchConditionDTO.getShopIds())) throw new BcgogoException("shopId nullPointException!");
      }else{
        if (searchConditionDTO.getShopKind() ==null) throw new BcgogoException("shopKind nullPointException!");
      }
    }


    StringBuffer fQueryString = new StringBuffer();
    if(searchConditionDTO.getProductStatus()!=null){
      fQueryString.append(" product_status:"+searchConditionDTO.getProductStatus());
    }else{
      fQueryString.append(" product_status:ENABLED");
    }

    if (searchConditionDTO.getShopId()!=null) {
      if (searchConditionDTO.getIncludeBasic()){
        fQueryString.append(" AND (");
        fQueryString.append("shop_id:").append(searchConditionDTO.getShopId());
        fQueryString.append(" OR shop_id:").append(SolrConstant.BASIC_SHOP_ID).append(")");
      }else{
        fQueryString.append(" AND shop_id:").append(searchConditionDTO.getShopId());
      }

    }else{
      if (searchConditionDTO.getIncludeBasic()) {
        fQueryString.append(" AND shop_id:").append(SolrConstant.BASIC_SHOP_ID);
      }else{
        fQueryString.append(" AND -shop_id:").append(SolrConstant.BASIC_SHOP_ID);
      }
    }

    if(searchConditionDTO.getShopKind()!=null){
      fQueryString.append(" AND shop_kind:"+searchConditionDTO.getShopKind());
    }


    if (searchConditionDTO.getWholesalerShopId() != null) {
      fQueryString.append(" AND wholesaler_shop_id"+ searchConditionDTO.getWholesalerShopId());
    }
    if (searchConditionDTO.getHasInventoryFlag() != null && searchConditionDTO.getHasInventoryFlag()) {
      fQueryString.append(" AND inventory_amount:{0 TO *}");
    }
    if (searchConditionDTO.getSalesStatus()!=null) {
      fQueryString.append(" AND sales_status:"+searchConditionDTO.getSalesStatus());
    }
     if (searchConditionDTO.getAdStatus()!=null) {
      fQueryString.append(" AND ad_status:"+searchConditionDTO.getAdStatus());
    }
    query.addFilterQuery(fQueryString.toString());

    if(searchConditionDTO.getStorehouseId()!=null && !searchConditionDTO.getShowAllStorehouseProducts()){
      query.addFilterQuery("storehouse_id:"+searchConditionDTO.getStorehouseId()+" AND "+searchConditionDTO.getStorehouseId()+"_storehouse_inventory_amount:[0 TO *] ");
    }
    //function query
    if(SearchConditionDTO.InventoryAlarm.LOWER_LIMIT.equals(searchConditionDTO.getInventoryAlarm())){
      query.addFilterQuery("{!frange u=0 incu=false}sub(inventory_amount,lower_limit)");
    }
    if(SearchConditionDTO.InventoryAlarm.UPPER_LIMIT.equals(searchConditionDTO.getInventoryAlarm())){
      query.addFilterQuery("{!frange l=0 incl=false}sub(inventory_amount,upper_limit)");
      query.addFilterQuery("upper_limit:{0 TO *}");
    }
  }

  //左闭右开  [a,b)
  private boolean generateRangeRightRegionQuery(String field, String start, String end, boolean started, StringBuilder qString) {
    if (StringUtils.isBlank(start) && StringUtils.isBlank(end)) return started;
    if (started) {
      qString.append(" AND ");
    }
    if (StringUtils.isNotBlank(start) && StringUtils.isNotBlank(end)) {
      qString.append("(").append(field).append(":{").append(start).append(" TO ").append(end).append("}")
        .append(" OR ").append(field).append(":").append(start).append(")");
    } else if (StringUtils.isNotBlank(end)) {
      qString.append(field).append(":{* TO ").append(end).append("}");
    } else {
      qString.append("(").append(field).append(":{").append(start).append(" TO *}").
        append(" OR ").append(field).append(":").append(start).append(")");
    }
    started = true;
    return started;
  }

  private boolean generateRangeQuery(String field, String start, String end, boolean started,StringBuilder qString,boolean isClose) {
    if (StringUtil.isEmpty(start)&&StringUtil.isEmpty(end)) return started;
    if (started) {
      qString.append(" AND ");
    }
    if (StringUtil.isNotEmpty(start) &&StringUtil.isNotEmpty(end)){
      qString.append(field).append(":").append(isClose?"[":"{").append(start).append(" TO ").append(end).append(isClose?"]":"}");
    } else if (StringUtil.isNotEmpty(start)) {
      qString.append(field).append(":").append(isClose ? "[" : "{").append(start).append(" TO *").append(isClose?"]":"}");
    } else {
      qString.append(field).append(":").append(isClose?"[":"{").append("* TO ").append(end).append(isClose?"]":"}");
    }
    started = true;
    return started;
  }

  //join
  private void generateFilterJoinSupplierOrShopQuery(JoinSearchConditionDTO joinSearchConditionDTO,SolrQuery query) {
    if(joinSearchConditionDTO==null) return;
    StringBuilder joinQueryString = new StringBuilder();
    boolean started = false;
    if(SolrClientHelper.BcgogoSolrCore.CUSTOMER_SUPPLIER_CORE.getValue().equals(joinSearchConditionDTO.getFromIndex())){
      joinQueryString.append("doc_type:").append(SolrClientHelper.BcgogoSolrDocumentType.CUSTOMER_SUPPLIER.getValue());
      started = true;
      if(StringUtils.isNotBlank(joinSearchConditionDTO.getCustomerOrSupplierName())){
        if (started) {
          joinQueryString.append(" AND ");
        }
        joinQueryString.append("name:*").append(joinSearchConditionDTO.getCustomerOrSupplierName()).append("*");
        started = true;
      }
      if(StringUtils.isNotBlank(joinQueryString.toString())){
        StringBuffer fQueryString = new StringBuffer();
        if(joinSearchConditionDTO.getShopId()!=null){
          if (joinSearchConditionDTO.getIncludeBasic()) fQueryString.append("(");
          fQueryString.append("shop_id:").append(joinSearchConditionDTO.getShopId());
          if (joinSearchConditionDTO.getIncludeBasic()) fQueryString.append(" OR shop_id:").append(SolrConstant.BASIC_SHOP_ID).append(")");
        }else{
          fQueryString.append("shop_id:").append(SolrConstant.BASIC_SHOP_ID);
        }
        if (started) {
          joinQueryString.append(" AND ");
        }
        joinQueryString.append(fQueryString);

        if (started) {
          joinQueryString.append(" AND ");
        }
        joinQueryString.append("!status:DISABLED");
      }
    }
    StringBuilder qString = new StringBuilder();
    if(StringUtils.isNotBlank(joinQueryString.toString())){
      qString.append("{!join from=").append(joinSearchConditionDTO.getFromColumn()).append(" to=").append(joinSearchConditionDTO.getToColumn()).append(" fromIndex=").append(joinSearchConditionDTO.getFromIndex()).append("}");
      qString.append("(").append(joinQueryString).append(")");
      query.addFilterQuery(qString.toString());
    }
  }



  @Override
  public ProductSearchResultListDTO queryAccessoryRecommend(boolean isMatchNull,SearchConditionDTO searchConditionDTO) throws Exception {
    boolean started = false;
    StringBuilder qString = new StringBuilder();

    String productName = SolrQueryUtils.escape(searchConditionDTO.getProductName());
    String productBrand = SolrQueryUtils.escape(searchConditionDTO.getProductBrand());
    String productSpec = SolrQueryUtils.escape(searchConditionDTO.getProductSpec());
    String productModel = SolrQueryUtils.escape(searchConditionDTO.getProductModel());
    String pvBrand = SolrQueryUtils.escape(searchConditionDTO.getProductVehicleBrand());
    String pvModel = SolrQueryUtils.escape(searchConditionDTO.getProductVehicleModel());

    String customMatchPContent = SolrQueryUtils.escape(searchConditionDTO.getCustomMatchPContent());
    String customMatchPVContent = SolrQueryUtils.escape(searchConditionDTO.getCustomMatchPVContent());

    if (!StringUtils.isBlank(productName)) {
      if (started) {
        qString.append(" AND ");
      }
      qString.append("(");
      qString.append("product_name_exact:").append("(\"").append(productName).append("\")^10 ");
      qString.append(" OR product_name_simple").append(":").append("(").append(productName).append(")");
      qString.append(")");
      started = true;
    }
    if (!StringUtils.isBlank(productBrand)) {
      if (started) {
        qString.append(" AND ");
      }
      qString.append("(");
      qString.append("product_brand_exact:").append("(\"").append(productBrand).append("\")^10 ");
      qString.append(" OR product_brand_simple").append(":").append("(").append(productBrand).append(")");
      if(isMatchNull) qString.append(" OR (-product_brand_simple:[* TO *]^0.001)");
      qString.append(")");
      started = true;
    }

    if (!StringUtils.isBlank(productSpec)) {
      if (started) {
        qString.append(" AND ");
      }
      qString.append("(");
      qString.append("product_spec_exact:").append("(\"").append(productSpec).append("\")^10 ");
      qString.append(" OR product_spec_simple").append(":").append("(").append(productSpec).append(")");
      if(isMatchNull) qString.append(" OR (-product_spec_simple:[* TO *]^0.001)");
      qString.append(")");
      started = true;
    }

    if (!StringUtils.isBlank(productModel)) {
      if (started) {
        qString.append(" AND ");
      }
      qString.append("(");
      qString.append("product_model_exact:").append("(\"").append(productModel).append("\")^10 ");
      qString.append(" OR product_model_simple").append(":").append("(").append(productModel).append(")");
      if(isMatchNull) qString.append(" OR (-product_model_simple:[* TO *]^0.001)");
      qString.append(")");
      started = true;
    }

    if (!StringUtils.isBlank(pvBrand)) {
      if (started) {
        qString.append(" AND ");
      }
      qString.append("(");
      qString.append("product_vehicle_brand_exact:").append("(\"").append(pvBrand).append("\")^10 ");
      qString.append(" OR product_vehicle_brand_simple").append(":").append("(").append(pvBrand).append(")");
      if(isMatchNull) qString.append(" OR (-product_vehicle_brand_simple:[* TO *]^0.001)");
      qString.append(")");
      started = true;
    }

    if (!StringUtils.isBlank(pvModel)) {
      if (started) {
        qString.append(" AND ");
      }
      qString.append("(");
      qString.append("product_vehicle_model_exact:").append("(\"").append(pvModel).append("\")^10 ");
      qString.append(" OR product_vehicle_model_simple").append(":").append("(").append(pvModel).append(")");
      if(isMatchNull) qString.append(" OR (-product_vehicle_model_simple:[* TO *]^0.001)");
      qString.append(")");
      started = true;
    }

    if (!StringUtils.isBlank(customMatchPContent)) {
      if (started) {
        qString.append(" AND ");
      }
      qString.append("custom_match_p_content").append(":").append("(").append(customMatchPContent).append(")");
      started = true;
    }
    if (!StringUtils.isBlank(customMatchPVContent)) {
      if (started) {
        qString.append(" AND ");
      }
      qString.append("(");
      qString.append("custom_match_pv_content").append(":").append("(").append(customMatchPVContent).append(")");
      if(isMatchNull) qString.append(" OR (-custom_match_pv_content:[* TO *]^0.001)");
      qString.append(")");
      started = true;
    }

    if (!ArrayUtils.isEmpty(searchConditionDTO.getExcludeShopIds())) {
      if (started) {
        qString.append(" AND ");
      }
      qString.append("!shop_id:(");
      for (int i = 0, max = searchConditionDTO.getExcludeShopIds().length; i < max; i++) {
        qString.append(searchConditionDTO.getExcludeShopIds()[i]);
        if (i < (max - 1)) qString.append(" OR ");
      }
      qString.append(")");
      started = true;
    }
    //推荐供应商用
//    started = generateRangeQuery("trade_price", searchConditionDTO.getTradePriceStart() == null ? null : searchConditionDTO.getTradePriceStart().toString(),
    started = generateRangeQuery("in_sales_price", searchConditionDTO.getInSalesPriceStart() == null ? null : searchConditionDTO.getInSalesPriceEnd().toString(),
      searchConditionDTO.getTradePriceEnd() == null ? null : searchConditionDTO.getTradePriceEnd().toString(), started, qString,false);

    SolrQuery query = new SolrQuery();
    if (qString.length() == 0) {
      qString.append("*:*");
    }
    query.setQuery(qString.toString());
    StringBuffer fQueryString = new StringBuffer();
    fQueryString.append(" product_status:ENABLED AND !shop_id:").append(SolrConstant.BASIC_SHOP_ID);
    if(searchConditionDTO.getShopKind()!=null){
      fQueryString.append(" AND shop_kind:"+searchConditionDTO.getShopKind());
    }
    if(searchConditionDTO.getShopId()!=null){
      fQueryString.append(" AND shop_id:"+searchConditionDTO.getShopId());
    }

    if (searchConditionDTO.getSalesStatus()!=null) {
      fQueryString.append(" AND sales_status:"+searchConditionDTO.getSalesStatus());
    }

    query.setFilterQueries(fQueryString.toString());

    query.setParam("fl", "*,score");
    query.setStart(searchConditionDTO.getStart());
    query.setRows(searchConditionDTO.getRows());
    query.setParam("q.op", "OR");
    QueryResponse rsp = SolrClientHelper.getProductSolrClient().query(query);

    ProductSearchResultListDTO productSearchResultListDTO = new ProductSearchResultListDTO();
    productSearchResultListDTO.setProducts(getProductDTOBySolrDocuments(rsp.getResults()));
    productSearchResultListDTO.setNumFound(rsp.getResults().getNumFound());
    return productSearchResultListDTO;
  }

}
